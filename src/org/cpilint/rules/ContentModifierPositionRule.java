package org.cpilint.rules;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Optional;

import org.cpilint.IflowXml;
import org.cpilint.artifacts.IflowArtifact;
import org.cpilint.artifacts.IflowArtifactTag;
import org.cpilint.issues.ContentModifierPositionIssue;
import org.cpilint.model.XmlModel;
import org.cpilint.model.XmlModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

/**
 * Rule to check if an iFlow has a Content Modifier step within the first 1-5 steps
 * and if it sets the required ILCD framework properties (projectName and integrationID).
 * These properties are nothing but Value Map Src/Trgt Identifiers for metadata VMs.
 */
final class ContentModifierPositionRule extends RuleBase {
    private static final Logger logger = LoggerFactory.getLogger(ContentModifierPositionRule.class);
    private static final int MAX_ALLOWED_POSITION = 5;
    private static final String CONTENT_MODIFIER_TYPE = "ContentModifier";
    private static final String PROPERTY_PROJECT_NAME = "projectName";
    private static final String PROPERTY_INTEGRATION_ID = "integrationID";

    @Override
    public void inspect(IflowArtifact iflow) {
        IflowXml iflowXml = iflow.getIflowXml();
        XmlModel model = XmlModelFactory.getModelFor(iflowXml);
        IflowArtifactTag tag = iflow.getTag();

        // Find all steps in the iFlow
        String stepsQuery = model.xpathForAllSteps();
        XdmValue allSteps = iflowXml.evaluateXpath(stepsQuery);
        
        // Check if a content modifier exists and what position it's in
        boolean foundContentModifier = false;
        boolean isWithinAllowedPosition = false;
        boolean hasProjectNameProperty = false;
        boolean hasIntegrationIDProperty = false;
        String contentModifierName = "";
        String contentModifierId = "";
        int contentModifierPosition = -1;
        
        Map<String, Integer> stepPositions = getStepPositions(iflowXml, model);
        
        // Get content modifier steps
        String contentModifierQuery = model.xpathForStepsByType(CONTENT_MODIFIER_TYPE);
        XdmValue contentModifierSteps = iflowXml.evaluateXpath(contentModifierQuery);
        
        if (contentModifierSteps.size() > 0) {
            foundContentModifier = true;
            
            // We'll check the first Content Modifier step we find
            XdmNode firstContentModifier = (XdmNode) contentModifierSteps.itemAt(0);
            contentModifierName = model.getStepNameFromElement(firstContentModifier);
            contentModifierId = model.getStepIdFromElement(firstContentModifier);
            
            // Check position
            if (stepPositions.containsKey(contentModifierId)) {
                contentModifierPosition = stepPositions.get(contentModifierId);
                isWithinAllowedPosition = contentModifierPosition <= MAX_ALLOWED_POSITION;
            }
            
            // Check if the required properties are set
            String propertiesQuery = model.xpathForPropertiesInStep(contentModifierId);
            XdmValue properties = iflowXml.evaluateXpath(propertiesQuery);
            
            for (int i = 0; i < properties.size(); i++) {
                XdmNode property = (XdmNode) properties.itemAt(i);
                String propertyName = model.getPropertyNameFromElement(property);
                
                if (PROPERTY_PROJECT_NAME.equals(propertyName)) {
                    hasProjectNameProperty = true;
                } else if (PROPERTY_INTEGRATION_ID.equals(propertyName)) {
                    hasIntegrationIDProperty = true;
                }
            }
        }
        
        // Create issues as needed
        if (!foundContentModifier) {
            consumer.consume(new ContentModifierPositionIssue(
                ruleId, tag, getSeverity(), ContentModifierPositionIssue.IssueType.MISSING));
        } else if (!isWithinAllowedPosition) {
            consumer.consume(new ContentModifierPositionIssue(
                ruleId, tag, getSeverity(), ContentModifierPositionIssue.IssueType.WRONG_POSITION, 
                contentModifierName, contentModifierId, contentModifierPosition));
        } else if (!hasProjectNameProperty || !hasIntegrationIDProperty) {
            consumer.consume(new ContentModifierPositionIssue(
                ruleId, tag, getSeverity(), ContentModifierPositionIssue.IssueType.MISSING_PROPERTIES,
                contentModifierName, contentModifierId, 
                !hasProjectNameProperty, !hasIntegrationIDProperty));
        }
    }
    
    /**
     * Gets the positions of all steps in the flow based on execution order.
     * 
     * @param iflowXml The iFlow XML document
     * @param model The XML model for the iFlow
     * @return A map of step IDs to their positions (1-indexed)
     */
    private Map<String, Integer> getStepPositions(IflowXml iflowXml, XmlModel model) {
        Map<String, Integer> positions = new HashMap<>();
        
        // This query gets all steps in order of execution
        String stepsInOrderQuery = model.xpathForAllStepsInProcessOrder();
        XdmValue stepsInOrder = iflowXml.evaluateXpath(stepsInOrderQuery);
        
        for (int i = 0; i < stepsInOrder.size(); i++) {
            XdmNode step = (XdmNode) stepsInOrder.itemAt(i);
            String stepId = model.getStepIdFromElement(step);
            // We use 1-indexed positions for better human readability in error messages
            positions.put(stepId, i + 1); 
        }
        
        return positions;
    }

    public Optional<String> getRuleId() {
        return Optional.of("content-modifier-position");
    }
}
