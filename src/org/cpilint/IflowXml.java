package org.cpilint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

public final class IflowXml {

	private static final Map<String, String> namespacePrefixes = Map.of(
		"bpmn2", "http://www.omg.org/spec/BPMN/20100524/MODEL",
		"ifl", "http:///com.sap.ifl.model/Ifl.xsd"
	); 
	
	private byte[] rawDocument;
	private XdmNode docRoot;
	private XPathCompiler xpathCompiler;
	private XQueryCompiler xqueryCompiler;
	private static final Set<String> alreadyWarnedXpaths = new HashSet<>();
	private static final Map<String, Integer> bracketFixWarningCounts = new HashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(IflowXml.class);

	private IflowXml(byte[] rawDocument, XdmNode docRoot, XPathCompiler xpathCompiler, XQueryCompiler xqueryCompiler) {
		this.rawDocument = rawDocument;
		this.docRoot = docRoot;
		this.xpathCompiler = xpathCompiler;
		this.xqueryCompiler = xqueryCompiler;
	}
	
	public InputStream getRawDocument() {
		return new ByteArrayInputStream(rawDocument);
	}

	private static void debugLog(String message, Object... args) {
		if (logger.isDebugEnabled()) {
			logger.debug(message, args);
		}
	}

    public XdmValue evaluateXpath(String xpath) {
        XdmValue value;
        try {
            if (!xpath.contains("[[")) {
                return xpathCompiler.evaluate(xpath, docRoot);
            }
            String sanitized = sanitizeMalformedXpath(xpath);
            value = xpathCompiler.evaluate(sanitized, docRoot);
            debugLog("Final sanitized XPath: {}", sanitized);
        } catch (SaxonApiException e) {
            if (e.getMessage().contains("Effective boolean value is defined only for sequences")) {
                logger.warn("XPath returned an array instead of boolean: {}", xpath);
                String countWrapped = "count(" + xpath + ") > 0";
                try {
                    value = xpathCompiler.evaluate(countWrapped, docRoot);
                } catch (SaxonApiException ex) {
                    logger.warn("count() workaround also failed for XPath: {}, error: {}", xpath, ex.getMessage());
                    return XdmValue.makeSequence(new ArrayList<XdmItem>());
                }
            } else {
                throw new IflowXmlError("Error while processing iflow XML: " + e.getMessage(), e);
            }
        }
        return value;
    }

    public static String sanitizeMalformedXpath(String originalXpath) {
        String xpath = originalXpath;
        if (xpath.contains("[[")) {
            if (!alreadyWarnedXpaths.contains(xpath)) {
                logger.warn("Fixing malformed XPath: {}", xpath);
                alreadyWarnedXpaths.add(xpath);
            }

            xpath = xpath.replaceAll("\\[\\[", "[");
            xpath = xpath.replaceAll("]]", "]");

            while (xpath.contains("][") || xpath.matches(".*\\][\\s\\r\\n]*\\[.*")) {
                xpath = xpath.replaceAll("\\]\\s*\\[", " and ");
                xpath = xpath.replaceAll("\\] and \\[", " and ");
            }

            xpath = xpath.replaceAll("\\[\\[", "[");
            xpath = xpath.replaceAll("]]", "]");

            long openBrackets = xpath.chars().filter(ch -> ch == '[').count();
            long closeBrackets = xpath.chars().filter(ch -> ch == ']').count();

            if (openBrackets > closeBrackets) {
                int count = bracketFixWarningCounts.getOrDefault(xpath, 0);
                if (count < 1) {
                    logger.warn("Detected unbalanced brackets after fix, trying to auto-close");
                    bracketFixWarningCounts.put(xpath, count + 1);
                }
                long bracketsToAdd = openBrackets - closeBrackets;
                for (long i = 0; i < bracketsToAdd; i++) {
                    xpath += "]";
                }
            } else if (closeBrackets > openBrackets) {
                int count = bracketFixWarningCounts.getOrDefault(xpath, 0);
                if (count < 1) {
                    logger.warn("More closing than opening brackets, trimming extras");
                    bracketFixWarningCounts.put(xpath, count + 1);
                }
                for (long i = 0; i < (closeBrackets - openBrackets); i++) {
                    int lastCloseIndex = xpath.lastIndexOf(']');
                    if (lastCloseIndex != -1) {
                        xpath = xpath.substring(0, lastCloseIndex) + xpath.substring(lastCloseIndex + 1);
                    }
                }
            }
        }
        xpath = xpath.trim();
        debugLog("DEBUG: Final sanitized XPath: {}", xpath);
        return xpath;
    }

    public XdmValue executeXquery(String query) {
        XdmValue result;
        try {
            XQueryExecutable exe = xqueryCompiler.compile(query);
            XQueryEvaluator eval = exe.load();
            eval.setSource(docRoot.asSource());
            result = eval.evaluate();
        } catch (SaxonApiException e) {
            throw new IflowXmlError("Error executing XQuery query", e);
        }
        return result;
    }

    public static IflowXml fromInputStream(InputStream is) {
        byte[] rawDocument;
        try {
            rawDocument = is.readAllBytes();
        } catch (IOException e) {
            throw new IflowXmlError("I/O error when reading iflow XML document", e);
        }
        Processor p = new Processor(false);
        XdmNode docRoot;
        try {
            docRoot = p.newDocumentBuilder().build(new StreamSource(new ByteArrayInputStream(rawDocument)));
        } catch (SaxonApiException e) {
            throw new IflowXmlError("Error while processing iflow XML", e);
        }
        XPathCompiler xpathCompiler = p.newXPathCompiler();
        namespacePrefixes.forEach((prefix, ns) -> xpathCompiler.declareNamespace(prefix, ns));
        return new IflowXml(rawDocument, docRoot, xpathCompiler, p.newXQueryCompiler());
    }
	

	// public XdmValue evaluateXpath(String xpath) {
	// 	// TARGETED WORKAROUND: Only skip the specific problematic XPath queries related to IDMapper
	// 	// This is much more selective than our previous overly aggressive approach
	// 	if (xpath.contains("IDMapper") && !xpath.contains("count(")) {
	// 		// For IDMapper-specific queries that don't use count(), try to convert them
	// 		// to use count() which often resolves array vs boolean issues
	// 		String modifiedXPath = xpath;
	// 		if (xpath.contains("property[key='activityType' and value='IDMapper']")) {
	// 			modifiedXPath = xpath.replace(
	// 				"property[key='activityType' and value='IDMapper']", 
	// 				"count(property[key='activityType' and value='IDMapper']) > 0"
	// 			);
	// 			try {
	// 				return xpathCompiler.evaluate(modifiedXPath, docRoot);
	// 			} catch (SaxonApiException e) {
	// 				// Still failed with modified query, fall back to empty sequence
	// 				System.out.println("WARNING: Modified IDMapper XPath still failed: " + e.getMessage());
	// 				return XdmValue.makeSequence(new ArrayList<XdmItem>());
	// 			}
	// 		}
			
	// 		// If we can't modify it effectively, just return an empty sequence
	// 		System.out.println("WARNING: Skipping problematic IDMapper XPath: " + xpath);
	// 		return XdmValue.makeSequence(new ArrayList<XdmItem>());
	// 	}
		
	// 	XdmValue value;
	// 	try {
	// 		value = xpathCompiler.evaluate(xpath, docRoot);
	// 	} catch (SaxonApiException e) {
	// 		// Only handle specific errors related to IDMapper or boolean/array type issues
	// 		if (e.getMessage().contains("IDMapper") || 
	// 			(e.getMessage().contains("boolean") && e.getMessage().contains("array"))) {
	// 			System.out.println("WARNING: IDMapper-related XPath error caught: " + e.getMessage());
	// 			return XdmValue.makeSequence(new ArrayList<XdmItem>());
	// 		} else {
	// 			// Let other errors be reported normally
	// 			throw new IflowXmlError("Error while processing iflow XML: " + e.getMessage(), e);
	// 		}
	// 	}
	// 	return value;		
	// }
}
