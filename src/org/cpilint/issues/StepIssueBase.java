package org.cpilint.issues;

import java.util.Optional;
import java.util.Set;

import org.cpilint.artifacts.IflowArtifactTag;

abstract class StepIssueBase extends IssueBase {
	
	private final String stepName;
	private final String stepId;
	
	protected StepIssueBase(Optional<String> ruleId, IflowArtifactTag tag, String stepName, String stepId, String message, Severity severity) {
		super(ruleId, Set.of(tag), message, severity);
		this.stepName = stepName;
		this.stepId = stepId;
	}
	
	// Backward compatibility constructor (uses INFO severity)
	protected StepIssueBase(Optional<String> ruleId, IflowArtifactTag tag, String stepName, String stepId, String message) {
		this(ruleId, tag, stepName, stepId, message, Severity.INFO);
	}

	public String getStepName() {
		return stepName;
	}
	
	public String getStepId() {
		return stepId;
	}

}
