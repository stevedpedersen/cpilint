package org.cpilint.issues;

import java.util.Optional;
import java.util.Set;

import org.cpilint.artifacts.IflowArtifactTag;

public final class IflowDescriptionRequiredIssue extends IssueBase {
	
	public IflowDescriptionRequiredIssue(Optional<String> ruleId, IflowArtifactTag tag) {
		super(ruleId, Set.of(tag), "Iflow does not have the required description.", Severity.CRITICAL);
	}
	
    @Override
    public String getRationale() {
        return "iFlows should have a clear description to improve maintainability and documentation.";
    }
    
    @Override
    public String getExample() {
        return "An iFlow with a description that explains its purpose, interfaces, and key behaviors.";
    }
    
    @Override
    public String getCounterExample() {
        return "An iFlow with no description or only a default description.";
    }
    
    @Override
    public String getRecommendation() {
        return "Add a thorough description that explains what the iFlow does, which systems it connects, and any special considerations.";
    }
}
