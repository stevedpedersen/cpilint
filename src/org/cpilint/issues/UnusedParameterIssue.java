package org.cpilint.issues;

import java.util.Optional;

import org.cpilint.artifacts.IflowArtifactTag;

public final class UnusedParameterIssue extends ArtifactIssueBase {
	
	public UnusedParameterIssue(IflowArtifactTag tag, String descriptionIssue) {
		super(tag, descriptionIssue);
	}

	@Override
	public String getRationale() {
		return "Unused parameters create clutter and increase the risk of confusion and errors.";
	}
	
	@Override
	public String getExample() {
		return "Leftover external parameters or unused variables.";
	}
	
	@Override
	public String getCounterExample() {
		return "N/A.";
	}
	
	@Override
	public String getRecommendation() {
		return "Use the 'Remove unused parameters' feature in the iFlow configuration.";
	}
}