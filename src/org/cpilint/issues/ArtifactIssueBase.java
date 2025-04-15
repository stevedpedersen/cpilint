package org.cpilint.issues;

import java.util.Optional;
import java.util.Set;

import org.cpilint.artifacts.IflowArtifactTag;

public abstract class ArtifactIssueBase extends IssueBase {
	
	private final IflowArtifactTag tag;

	
	protected ArtifactIssueBase(IflowArtifactTag tag, String message) {
		super(String.format(
      "In iflow '%s' (ID '%s'): %s",
      tag.getName(),
      tag.getId(),
      message
    ));
		this.tag = tag;
	}
	protected ArtifactIssueBase(Optional<String> ruleId, IflowArtifactTag tag, String message) {
		super(ruleId, Set.of(tag), message);
		this.tag = tag;
	}
	protected ArtifactIssueBase(Optional<String> ruleId, IflowArtifactTag tag, String message, Severity severity) {
		super(ruleId, Set.of(tag), message, severity);
		this.tag = tag;
	}
	public IflowArtifactTag getTag() {
		return tag;
	}
	protected void setSeverity(Severity severity) {
		super.setSeverity(severity);
	}
}