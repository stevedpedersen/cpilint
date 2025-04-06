package org.cpilint.rules;

import org.cpilint.artifacts.IflowArtifact;
import org.cpilint.artifacts.IflowArtifactTag;
import org.cpilint.issues.IflowNameMatchesIssue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class IflowNameMatchesRule extends RuleBase {

	private Pattern namePattern;

	IflowNameMatchesRule(Pattern namePattern) {
		this.namePattern = namePattern;
	}

	@Override
	public void inspect(IflowArtifact iflow) {
		IflowArtifactTag tag = iflow.getTag();
		Matcher matcher = this.namePattern.matcher(tag.getName());
		if (!matcher.find()){
			consumer.consume(new IflowNameMatchesIssue(tag, namePattern.toString()));
		}
	}
}