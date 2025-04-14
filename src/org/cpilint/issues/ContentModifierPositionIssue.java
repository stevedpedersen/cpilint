package org.cpilint.issues;

import java.util.Optional;
import java.util.Set;
import org.cpilint.artifacts.IflowArtifactTag;
import org.cpilint.issues.Severity;

/**
 * Issue raised when an iFlow does not have a properly configured Content Modifier step 
 * within the first 1-5 steps of the integration flow.
 */
public final class ContentModifierPositionIssue extends IssueBase {
    
    /**
     * Types of issues related to Content Modifier position and configuration
     */
    public enum IssueType {
        MISSING,            // No Content Modifier found in the iFlow
        WRONG_POSITION,     // Content Modifier not within first 1-5 steps
        MISSING_PROPERTIES  // Content Modifier exists but missing required properties
    }
    
    private final IssueType issueType;
    private final String stepName;
    private final String stepId;
    private final int position;
    private final boolean missingProjectName;
    private final boolean missingIntegrationID;
    protected final IflowArtifactTag tag;
    public static final String RULE_ID = "content-modifier-position-rule";
    
    /**
     * Constructor for MISSING issue type
     * 
     * @param ruleId The rule ID that generated this issue
     * @param tag The iFlow artifact tag
     * @param severity The severity level of the issue
     * @param issueType Must be MISSING
     */
    public ContentModifierPositionIssue(Optional<String> ruleId, IflowArtifactTag tag, Severity severity, IssueType issueType) {
        super(
            Optional.ofNullable(ruleId.isPresent() ? ruleId.get() : RULE_ID), 
            Set.of(tag), 
            String.format("iFlow %s does not have a Content Modifier step, which is required by the ILCD Framework", 
            tag.getName()), 
            severity
        );
        if (issueType != IssueType.MISSING) {
            throw new IllegalArgumentException("This constructor should only be used for MISSING issue type");
        }
        this.issueType = issueType;
        this.stepName = "";
        this.stepId = "";
        this.position = -1;
        this.missingProjectName = false;
        this.missingIntegrationID = false;
        this.tag = tag;
    }
    
    /**
     * Constructor for WRONG_POSITION issue type
     * 
     * @param ruleId The rule ID that generated this issue
     * @param tag The iFlow artifact tag
     * @param severity The severity level of the issue
     * @param issueType Must be WRONG_POSITION
     * @param stepName The name of the Content Modifier step
     * @param stepId The ID of the Content Modifier step
     * @param position The actual position of the Content Modifier step
     */
    public ContentModifierPositionIssue(Optional<String> ruleId, IflowArtifactTag tag, Severity severity, IssueType issueType, 
            String stepName, String stepId, int position) {
        super(Optional.ofNullable(ruleId.isPresent() ? ruleId.get() : RULE_ID), Set.of(tag), String.format("Content Modifier step '%s' is at position %d in the flow. It should be within the first 5 steps according to ILCD Framework guidelines", 
                stepName, position), severity);
        if (issueType != IssueType.WRONG_POSITION) {
            throw new IllegalArgumentException("This constructor should only be used for WRONG_POSITION issue type");
        }
        this.issueType = issueType;
        this.stepName = stepName;
        this.stepId = stepId;
        this.position = position;
        this.missingProjectName = false;
        this.missingIntegrationID = false;
        this.tag = tag;
    }
    
    /**
     * Constructor for MISSING_PROPERTIES issue type
     * 
     * @param ruleId The rule ID that generated this issue
     * @param tag The iFlow artifact tag
     * @param severity The severity level of the issue
     * @param issueType Must be MISSING_PROPERTIES
     * @param stepName The name of the Content Modifier step
     * @param stepId The ID of the Content Modifier step
     * @param missingProjectName Whether the projectName property is missing
     * @param missingIntegrationID Whether the integrationID property is missing
     */
    public ContentModifierPositionIssue(Optional<String> ruleId, IflowArtifactTag tag, Severity severity, IssueType issueType, 
            String stepName, String stepId, boolean missingProjectName, boolean missingIntegrationID) {
        super(Optional.ofNullable(ruleId.isPresent() ? ruleId.get() : RULE_ID), Set.of(tag), generateMissingPropertiesMessage(stepName, missingProjectName, missingIntegrationID), severity);
        if (issueType != IssueType.MISSING_PROPERTIES) {
            throw new IllegalArgumentException("This constructor should only be used for MISSING_PROPERTIES issue type");
        }
        this.issueType = issueType;
        this.stepName = stepName;
        this.stepId = stepId;
        this.position = -1;
        this.missingProjectName = missingProjectName;
        this.missingIntegrationID = missingIntegrationID;
        this.tag = tag;
    }
    
    /**
     * Backward compatibility constructor for MISSING issue type (uses ERROR severity)
     */
    public ContentModifierPositionIssue(Optional<String> ruleId, IflowArtifactTag tag, IssueType issueType) {
        this(Optional.ofNullable(ruleId.isPresent() ? ruleId.get() : RULE_ID), tag, Severity.ERROR, issueType);
    }
    
    /**
     * Backward compatibility constructor for WRONG_POSITION issue type (uses ERROR severity)
     */
    public ContentModifierPositionIssue(Optional<String> ruleId, IflowArtifactTag tag, IssueType issueType, 
            String stepName, String stepId, int position) {
        this(Optional.ofNullable(ruleId.isPresent() ? ruleId.get() : RULE_ID), tag, Severity.ERROR, issueType, stepName, stepId, position);
    }
    
    /**
     * Backward compatibility constructor for MISSING_PROPERTIES issue type (uses ERROR severity)
     */
    public ContentModifierPositionIssue(Optional<String> ruleId, IflowArtifactTag tag, IssueType issueType, 
            String stepName, String stepId, boolean missingProjectName, boolean missingIntegrationID) {
        this(Optional.ofNullable(ruleId.isPresent() ? ruleId.get() : RULE_ID), tag, Severity.ERROR, issueType, stepName, stepId, missingProjectName, missingIntegrationID);
    }
    
    private static String generateMissingPropertiesMessage(String stepName, boolean missingProjectName, boolean missingIntegrationID) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Content Modifier step '%s' is missing required ILCD Framework properties: ", stepName));
        
        if (missingProjectName && missingIntegrationID) {
            sb.append("projectName and integrationID");
        } else if (missingProjectName) {
            sb.append("projectName");
        } else if (missingIntegrationID) {
            sb.append("integrationID");
        }
        
        return sb.toString();
    }
    
    @Override
    public String getMessage() {
        switch (issueType) {
            case MISSING:
                return String.format("Content Modifier with properties required to link Value Map data to iFlow logs.", tag.getName());
                
            case WRONG_POSITION:
                return String.format("The Content Modifier which sets Source/Target Value Map Identifiers should be within the first 5 steps.", 
                        stepName, position);
                
            case MISSING_PROPERTIES:
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("Content Modifier step '%s' is missing required ILCD Framework properties: ", stepName));
                
                if (missingProjectName && missingIntegrationID) {
                    sb.append("projectName and integrationID");
                } else if (missingProjectName) {
                    sb.append("projectName");
                } else if (missingIntegrationID) {
                    sb.append("integrationID");
                }
                
                return sb.toString();
                
            default:
                return "Unexpected Content Modifier issue";
        }
    }
    
    public IssueType getIssueType() {
        return issueType;
    }
    
    public String getStepName() {
        return stepName;
    }
    
    public String getStepId() {
        return stepId;
    }
    
    public int getPosition() {
        return position;
    }
    
    public boolean isMissingProjectName() {
        return missingProjectName;
    }
    
    public boolean isMissingIntegrationID() {
        return missingIntegrationID;
    }

    @Override
    public String getRationale() {
        return "The ILCD Framework requires integrationID and projectName properties early to initialize error handling and value map lookups. Delaying them risks missing critical features if errors occur beforehand.";
    }
    
    @Override
    public String getExample() {
        return "Content Modifier as step 1, setting integrationID and projectName.";
    }
    
    @Override
    public String getCounterExample() {
        return "Content Modifier setting integrationID and projectName after several steps.";
    }
    
    @Override
    public String getRecommendation() {
        return "Place the initializing Content Modifier within the first 1–5 steps of the iFlow.";
    }
}
