package org.cpilint.rules;

import org.cpilint.rules.MaxProcessStepsRule;
import org.cpilint.rules.Rule;
import org.cpilint.rules.RuleFactory;
import org.cpilint.rules.RuleFactoryError;
import org.dom4j.Element;

/**
 * Factory for creating MaxProcessStepsRule instances from XML configuration.
 * This rule checks if integration processes have too many steps.
 */
public final class MaxProcessStepsRuleFactory implements RuleFactory {
    
    private static final String ELEMENT_NAME = "max-process-steps";
    
    @Override
    public boolean canCreateFrom(Element e) {
        return e != null && ELEMENT_NAME.equals(e.getName());
    }
    
    @Override
    public Rule createFrom(Element e) {
        if (!canCreateFrom(e)) {
            throw new RuleFactoryError("Cannot create MaxProcessStepsRule from element: " + e.getName());
        }
        
        MaxProcessStepsRule rule = new MaxProcessStepsRule();
        
        // Get the rule ID if specified
        String id = e.attributeValue("id");
        if (id != null && !id.isEmpty()) {
            rule.setId(id);
        }
        
        // Parse warning threshold
        Element warningElement = e.element("warning-threshold");
        if (warningElement != null) {
            try {
                int threshold = Integer.parseInt(warningElement.getTextTrim());
                rule.setWarningThreshold(threshold);
            } catch (NumberFormatException ex) {
                // Use default if invalid
            }
        }
        
        // Parse error threshold
        Element errorElement = e.element("error-threshold");
        if (errorElement != null) {
            try {
                int threshold = Integer.parseInt(errorElement.getTextTrim());
                rule.setErrorThreshold(threshold);
            } catch (NumberFormatException ex) {
                // Use default if invalid
            }
        }
        
        // Parse critical threshold
        Element criticalElement = e.element("critical-threshold");
        if (criticalElement != null) {
            try {
                int threshold = Integer.parseInt(criticalElement.getTextTrim());
                rule.setCriticalThreshold(threshold);
            } catch (NumberFormatException ex) {
                // Use default if invalid
            }
        }
        
        // Extract documentation fields (severity, rationale, example, etc.)
        extractDocumentationFields(e, rule);
        
        return rule;
    }
}
