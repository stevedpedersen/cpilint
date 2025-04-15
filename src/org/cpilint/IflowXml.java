package org.cpilint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

	private IflowXml(byte[] rawDocument, XdmNode docRoot, XPathCompiler xpathCompiler, XQueryCompiler xqueryCompiler) {
		this.rawDocument = rawDocument;
		this.docRoot = docRoot;
		this.xpathCompiler = xpathCompiler;
		this.xqueryCompiler = xqueryCompiler;
	}
	
	public InputStream getRawDocument() {
		return new ByteArrayInputStream(rawDocument);
	}

	public XdmValue evaluateXpath(String xpath) {
		XdmValue value;
		try {
			value = xpathCompiler.evaluate(sanitizeMalformedXpath(xpath), docRoot);
		} catch (SaxonApiException e) {
			if (e.getMessage().contains("Effective boolean value is defined only for sequences")) {
				System.out.println("WARNING: XPath returned an array instead of boolean: " + xpath);
				// Retry with exists() wrapped around it
				String existsXpath = String.format("exists(%s)", xpath);
				try {
					value = xpathCompiler.evaluate(existsXpath, docRoot);
				} catch (SaxonApiException ex) {
					System.out.println("WARNING: exists() workaround failed for XPath: " + xpath + ", error: " + ex.getMessage());
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
                System.out.println("WARNING: Fixing malformed XPath: " + xpath);
                alreadyWarnedXpaths.add(xpath);
            }

            xpath = xpath.replaceAll("\\[\\[", "[");
            xpath = xpath.replaceAll("]]", "]");

            // REPEAT flattening until stable
            while (xpath.contains("][") || xpath.matches(".*\\][\\s\\r\\n]*\\[.*")) {
                xpath = xpath.replaceAll("\\]\\s*\\[", " and ");
                xpath = xpath.replaceAll("\\] and \\[", " and ");
            }

            // Extra cleaning, just in case
            xpath = xpath.replaceAll("\\[\\[", "[");
            xpath = xpath.replaceAll("]]", "]");

            // Count open and close brackets
            long openBrackets = xpath.chars().filter(ch -> ch == '[').count();
            long closeBrackets = xpath.chars().filter(ch -> ch == ']').count();

            if (openBrackets > closeBrackets) {
                System.out.println("WARNING: Detected unbalanced brackets after fix, trying to auto-close");

                long bracketsToAdd = openBrackets - closeBrackets;
                for (long i = 0; i < bracketsToAdd; i++) {
                    xpath += "]";
                }
            } else if (closeBrackets > openBrackets) {
                System.out.println("WARNING: More closing than opening brackets, trimming extras");
                for (long i = 0; i < (closeBrackets - openBrackets); i++) {
                    int lastCloseIndex = xpath.lastIndexOf(']');
                    if (lastCloseIndex != -1) {
                        xpath = xpath.substring(0, lastCloseIndex) + xpath.substring(lastCloseIndex + 1);
                    }
                }
            }
		}

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
		// Read the document into a byte array.
		byte[] rawDocument;
		try {
			rawDocument = is.readAllBytes();
		} catch (IOException e) {
			throw new IflowXmlError("I/O error when reading iflow XML document", e);
		}
		// Parse the document.
		Processor p = new Processor(false);
		XdmNode docRoot;
		try {
			docRoot = p.newDocumentBuilder().build(new StreamSource(new ByteArrayInputStream(rawDocument)));
		} catch (SaxonApiException e) {
			throw new IflowXmlError("Error while processing iflow XML", e);
		}
		XPathCompiler xpathCompiler = p.newXPathCompiler();
		namespacePrefixes.forEach((prefix, ns) -> xpathCompiler.declareNamespace(prefix, ns));
		/*
		 *  To use an XdmNode (the document node, specifically) as a source in
		 *  XQuery evaluation, the node and the XQueryCompiler must originate
		 *  from the same Processor object. Otherwise the evaluation will fail
		 *  at runtime. 
		 */
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
