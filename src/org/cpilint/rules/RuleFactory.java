package org.cpilint.rules;

import org.dom4j.Element;
import org.cpilint.issues.Severity;

public interface RuleFactory {
	
	public boolean canCreateFrom(Element e);
	
	public Rule createFrom(Element e);
	
	/**
	 * Helper method to extract documentation fields from a rule element
	 * This makes it easier for RuleFactory implementations to parse and use these fields
	 */
	default void extractDocumentationFields(Element e, Rule rule) {
		if (e == null || rule == null) {
			return;
		}
		
		// Set the rule ID based on the element name
		// First check for an explicit ID attribute
		String id = e.attributeValue("id");
		if (id == null || id.isEmpty()) {
			// If no explicit ID, use the element name as the rule ID
			id = e.getName();
			
			// For naming rules, we need a more sophisticated ID generation approach
			if ("naming".equals(id)) {
				StringBuilder idBuilder = new StringBuilder(id);
				
				// Add apply-to information
				Element applyToElement = e.element("apply-to");
				if (applyToElement != null) {
					String applyTo = applyToElement.getTextTrim();
					if (applyTo != null && !applyTo.isEmpty()) {
						idBuilder.append("-").append(applyTo.replace('.', '-'));
					}
				}
				
				// Add scheme type information for more uniqueness
				Element schemeElement = e.element("scheme");
				if (schemeElement != null) {
					// Check which type of naming scheme is being used
					Element schemeTypeElement = null;
					if (schemeElement.element("regex") != null) {
						schemeTypeElement = schemeElement.element("regex");
						idBuilder.append("-regex");
					} else if (schemeElement.element("starts-with") != null) {
						schemeTypeElement = schemeElement.element("starts-with");
						idBuilder.append("-startswith");
					} else if (schemeElement.element("not") != null) {
						schemeTypeElement = schemeElement.element("not");
						idBuilder.append("-not");
					}
					
					// Add some content from the scheme for uniqueness
					if (schemeTypeElement != null) {
						String schemeContent = schemeTypeElement.getTextTrim();
						if (schemeContent != null && !schemeContent.isEmpty()) {
							// Use a hash of the scheme content to avoid very long IDs
							idBuilder.append("-").append(Math.abs(schemeContent.hashCode() % 10000));
						}
					}
				}
				
				// If we still don't have enough uniqueness, add message content
				Element messageElement = e.element("message");
				if (messageElement != null) {
					String message = messageElement.getTextTrim();
					if (message != null && !message.isEmpty()) {
						// Just add a hash of the first few characters to keep ID reasonable length
						String prefix = message.length() > 20 ? message.substring(0, 20) : message;
						idBuilder.append("-").append(Math.abs(prefix.hashCode() % 1000));
					}
				}
				
				id = idBuilder.toString();
			}
		}
		
		try {
			rule.setId(id);
		} catch (IllegalStateException ex) {
			// Rule ID may already be set, that's okay
		}
		
		// Parse severity if present
		Element severityElement = e.element("severity");
		if (severityElement != null && !severityElement.getTextTrim().isEmpty()) {
			try {
				rule.setSeverity(Severity.valueOf(severityElement.getTextTrim()));
			} catch (IllegalArgumentException ex) {
				// If severity is invalid, use default (INFO)
			}
		}
		
		// Parse rationale if present
		Element rationaleElement = e.element("rationale");
		if (rationaleElement != null && !rationaleElement.getTextTrim().isEmpty()) {
			rule.setRationale(rationaleElement.getTextTrim());
		}
		
		// Parse example if present
		Element exampleElement = e.element("example");
		if (exampleElement != null && !exampleElement.getTextTrim().isEmpty()) {
			rule.setExample(exampleElement.getTextTrim());
		}
		
		// Parse counterExample if present
		Element counterExampleElement = e.element("counterExample");
		if (counterExampleElement != null && !counterExampleElement.getTextTrim().isEmpty()) {
			rule.setCounterExample(counterExampleElement.getTextTrim());
		}
		
		// Parse recommendation if present
		Element recommendationElement = e.element("recommendation");
		if (recommendationElement != null && !recommendationElement.getTextTrim().isEmpty()) {
			rule.setRecommendation(recommendationElement.getTextTrim());
		}
	}
}
