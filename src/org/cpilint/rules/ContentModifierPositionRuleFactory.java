package org.cpilint.rules;

import org.dom4j.Element;

/**
 * Factory for creating ContentModifierPositionRule instances from XML configuration.
 * This rule checks if a Content Modifier step exists within the first 1-5 steps of an iFlow
 * and if it sets the required ILCD Framework properties.
 */
public final class ContentModifierPositionRuleFactory implements RuleFactory {
    
    private static final String ELEMENT_NAME = "content-modifier-position";
    
    @Override
    public boolean canCreateFrom(Element e) {
        return e != null && ELEMENT_NAME.equals(e.getName());
    }
    
    @Override
    public Rule createFrom(Element e) {
        if (!canCreateFrom(e)) {
            throw new RuleFactoryError("Cannot create ContentModifierPositionRule from element: " + e.getName());
        }
        
        return new ContentModifierPositionRule();
    }
}
