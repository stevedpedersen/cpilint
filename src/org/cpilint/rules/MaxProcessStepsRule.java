package org.cpilint.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.cpilint.IflowXml;
import org.cpilint.artifacts.IflowArtifact;
import org.cpilint.artifacts.IflowArtifactTag;
import org.cpilint.issues.Issue;
import org.cpilint.issues.MaxProcessStepsIssue;
import org.cpilint.issues.Severity;
import org.cpilint.model.XmlModel;
import org.cpilint.model.XmlModelFactory;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

/**
 * Rule that checks if integration processes have too many steps.
 * This rule analyzes each integration process (main and local) and counts
 * the number of steps, generating issues when thresholds are exceeded.
 */
public final class MaxProcessStepsRule extends RuleBase {
    
    private int warningThreshold = 7;
    private int errorThreshold = 12;
    private int criticalThreshold = 15;
    
    // Cache to store count of steps for each process
    private final Map<String, Integer> processStepCounts = new HashMap<>();
    
    /**
     * Set the threshold for generating warnings.
     * @param warningThreshold Number of steps that will trigger a warning
     */
    public void setWarningThreshold(int warningThreshold) {
        this.warningThreshold = warningThreshold;
    }
    
    /**
     * Set the threshold for generating errors.
     * @param errorThreshold Number of steps that will trigger an error
     */
    public void setErrorThreshold(int errorThreshold) {
        this.errorThreshold = errorThreshold;
    }
    
    /**
     * Set the threshold for generating critical issues.
     * @param criticalThreshold Number of steps that will trigger a critical issue
     */
    public void setCriticalThreshold(int criticalThreshold) {
        this.criticalThreshold = criticalThreshold;
    }
    
    @Override
    public void inspect(IflowArtifact iflow) {
        try {
            IflowXml iflowXml = iflow.getIflowXml();
            XmlModel model = XmlModelFactory.getModelFor(iflowXml);
            IflowArtifactTag tag = iflow.getTag();
            
            // Clear previous counts for this iflow
            processStepCounts.clear();
            
            // Find all processes (main and local integration processes)
            String xpath = "//bpmn2:process";
            
            // For each process, count steps and check thresholds
            iflowXml.evaluateXpath(xpath)
                .stream()
                .forEach(item -> {
                    try {
                        // Cast to XdmNode to access getAttributeValue
                        XdmNode node = (XdmNode) item;
                        
                        // Get process ID and name
                        String processId = node.getAttributeValue(new QName("id"));
                        String processName = node.getAttributeValue(new QName("name"));
                        if (processName == null || processName.isEmpty()) {
                            processName = processId;
                        }
                        
                        // Count steps - exclude start and end events
                        String stepsXpath = "//bpmn2:process[@id='" + processId + 
                                "']/bpmn2:*[not(self::bpmn2:startEvent) and not(self::bpmn2:endEvent)]";
                        int stepCount = iflowXml.evaluateXpath(stepsXpath).size();
                        processStepCounts.put(processId, stepCount);
                        
                        // Check thresholds and create issues
                        checkThresholds(tag, processId, processName, stepCount);
                    } catch (Exception e) {
                        // Continue with next process
                    }
                });
                
        } catch (Exception e) {
            // Continue with other rules if this one encounters an exception
        }
    }
    
    /**
     * Check step count against thresholds and report issues
     */
    private void checkThresholds(IflowArtifactTag tag, String processId, String processName, int stepCount) {
        Severity severity = null;
        String thresholdDesc = null;
        
        // Determine severity based on thresholds
        if (stepCount >= criticalThreshold) {
            severity = Severity.CRITICAL;
            thresholdDesc = "critical threshold of " + criticalThreshold;
        } else if (stepCount >= errorThreshold) {
            severity = Severity.ERROR;
            thresholdDesc = "error threshold of " + errorThreshold;
        } else if (stepCount >= warningThreshold) {
            severity = Severity.WARNING;
            thresholdDesc = "warning threshold of " + warningThreshold;
        }
        
        // If a threshold was exceeded, create an issue
        if (severity != null) {
            int limit = 0;
            if (severity == Severity.WARNING) {
                limit = warningThreshold;
            } else if (severity == Severity.ERROR) {
                limit = errorThreshold;
            } else if (severity == Severity.CRITICAL) {
                limit = criticalThreshold;
            }
                
            MaxProcessStepsIssue issue = new MaxProcessStepsIssue(
                ruleId, 
                tag, 
                processName,
                stepCount,
                limit,
                severity
            );
            
            consumer.consume(issue);
        }
    }
}
