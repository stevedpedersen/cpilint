package org.cpilint.issues;

import java.util.Optional;
import java.util.Set;

import org.cpilint.artifacts.IflowArtifactTag;

public final class NamingConventionsRuleIssue extends IssueBase {
	
	private String actualName;
	
	public NamingConventionsRuleIssue(String ruleId, IflowArtifactTag tag, String message, String actualName, Severity severity) {
		super(Optional.ofNullable(ruleId), Set.of(tag), message, severity);
		this.actualName = actualName;
	}
	
	// Keep backward compatibility constructor that uses INFO severity
	public NamingConventionsRuleIssue(String ruleId, IflowArtifactTag tag, String message, String actualName) {
		super(Optional.ofNullable(ruleId), Set.of(tag), message);
		this.actualName = actualName;
	}
	
	public String getActualName() {
		return actualName;
	}

}
