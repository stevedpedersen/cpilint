package org.cpilint.issues;

import java.util.Optional;

import org.cpilint.artifacts.IflowArtifactTag;
import org.cpilint.model.ScriptingLanguage;

public final class DisallowedScriptingLanguageIssue extends StepIssueBase {
	
	private final ScriptingLanguage language;
	
	public DisallowedScriptingLanguageIssue(Optional<String> ruleId, IflowArtifactTag tag, String stepName, String stepId, ScriptingLanguage language, Severity severity) {
		super(ruleId, tag, stepName, stepId, String.format(
			"Script step '%s' (ID '%s') executes a %s script, which is not allowed.", 
			stepName, 
			stepId,
			language.toString()), severity);
		this.language = language;
	}
	
	// Backward compatibility constructor (uses CRITICAL severity)
	public DisallowedScriptingLanguageIssue(Optional<String> ruleId, IflowArtifactTag tag, String stepName, String stepId, ScriptingLanguage language) {
		this(ruleId, tag, stepName, stepId, language, Severity.CRITICAL);
	}
	
	public ScriptingLanguage getScriptingLanguage() {
		return language;
	}

	@Override
	public String getRationale() {
		return "Allowed scripting languages are chosen to ensure maintainability for current and future developers.";
	}
	
	@Override
	public String getExample() {
		return "Groovy.";
	}
	
	@Override
	public String getCounterExample() {
		return "JavaScript.";
	}
	
	@Override
	public String getRecommendation() {
		return "Use Groovy for all custom scripts.";
	}
}
