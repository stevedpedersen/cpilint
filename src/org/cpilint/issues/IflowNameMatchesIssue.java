package org.cpilint.issues;

import org.cpilint.artifacts.IflowArtifactTag;

public final class IflowNameMatchesIssue extends ArtifactIssueBase {

	public IflowNameMatchesIssue(IflowArtifactTag tag, String patternString) {
		super(tag, String.format("Iflow name does not match the required pattern (%s) for Iflow names.", patternString));
	}

}