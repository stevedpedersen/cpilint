package org.cpilint.issues;

import java.util.Optional;
import java.util.Set;

import org.cpilint.artifacts.IflowArtifactTag;

/**
 * Issue representing a process with too many steps.
 */
public final class MaxProcessStepsIssue extends IssueBase {
    
    private final String processName;
    private final int stepCount;
    private final int limit;
    
    /**
     * Create a new MaxProcessStepsIssue.
     * 
     * @param ruleId Rule ID that detected the issue
     * @param tag Iflow artifact tag
     * @param processName Name of the process with too many steps
     * @param stepCount Number of steps found in the process
     * @param limit Maximum allowed steps
     * @param severity Severity of the issue
     */
    public MaxProcessStepsIssue(
            Optional<String> ruleId, 
            IflowArtifactTag tag, 
            String processName,
            int stepCount,
            int limit,
            Severity severity) {
        super(ruleId, Set.of(tag), String.format(
                "The process '%s' has %d steps, which is more than the limit of %d steps.",
                processName,
                stepCount,
                limit), severity);
        this.processName = processName;
        this.stepCount = stepCount;
        this.limit = limit;
    }
    
    /**
     * Create a new MaxProcessStepsIssue with default severity.
     * 
     * @param ruleId Rule ID that detected the issue
     * @param tag Iflow artifact tag
     * @param processName Name of the process with too many steps
     * @param stepCount Number of steps found in the process
     * @param limit Maximum allowed steps
     */
    public MaxProcessStepsIssue(
            Optional<String> ruleId, 
            IflowArtifactTag tag, 
            String processName,
            int stepCount,
            int limit) {
        this(ruleId, tag, processName, stepCount, limit, Severity.CRITICAL);
    }
    
    /**
     * Get the name of the process with too many steps.
     * 
     * @return Process name
     */
    public String getProcessName() {
        return processName;
    }
    
    /**
     * Get the number of steps found in the process.
     * 
     * @return Step count
     */
    public int getStepCount() {
        return stepCount;
    }
    
    /**
     * Get the maximum allowed steps.
     * 
     * @return Limit
     */
    public int getLimit() {
        return limit;
    }
    
    @Override
    public String getRationale() {
        return "Processes with too many steps become difficult to debug and maintain.";
    }
    
    @Override
    public String getExample() {
        return "Integration Process with < 10 steps.";
    }
    
    @Override
    public String getCounterExample() {
        return "Integration Process with 20+ steps without subdivision into Local Integration Processes";
    }
    
    @Override
    public String getRecommendation() {
        return "Break large processes into multiple Local Integration Processes with specific responsibilities.";
    }
}
