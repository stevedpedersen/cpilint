package org.cpilint.issues;

import java.util.Optional;

import org.cpilint.artifacts.IflowArtifactTag;

public final class NameNotDefaultRequiredIssue extends ArtifactIssueBase {

	public static final String RULE_ID = "default-names-not-allowed";
	
	public NameNotDefaultRequiredIssue(IflowArtifactTag tag, String descriptionIssue) {
		// super(tag, descriptionIssue);
		super(Optional.ofNullable(RULE_ID), tag, descriptionIssue);
	}

	public NameNotDefaultRequiredIssue(Optional<String> ruleId, IflowArtifactTag tag, String descriptionIssue, Severity severity) {
		super(Optional.ofNullable(ruleId.isPresent() ? ruleId.get() : RULE_ID), tag, descriptionIssue, severity);
	}

	public void setSeverity(Severity severity) {
		super.setSeverity(severity);
	}

	@Override
	public String getRationale() {
		return "Names should be unique and meaningful for optimal maintainability.";
	}

	@Override
	public String getExample() {
		return "LIP_PartnerSystem_PayloadValidation";
	}

	@Override
	public String getCounterExample() {
		return "Content Modifier 2 or Groovy Script 7";
	}

	@Override
	public String getRecommendation() {
		return "Use meaningful names for iFlow components that describe their purpose.";
	}
}