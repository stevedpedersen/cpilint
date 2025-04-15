package org.cpilint.issues;

import java.util.Optional;

import org.cpilint.artifacts.IflowArtifactTag;

public final class UndeclaredContentModifierDatatypeIssue extends ArtifactIssueBase {
	
	public UndeclaredContentModifierDatatypeIssue(IflowArtifactTag tag, String descriptionIssue) {
		super(tag, descriptionIssue);  
	}

    @Override
    public String getRationale() {
        return "Content Modifier steps must declare the datatype of the data they are processing.";
    }
    
    @Override
    public String getExample() {
        return "java.lang.String";
    }
    
    @Override
    public String getCounterExample() {
        return "<undefined>";
    }
    
    @Override
    public String getRecommendation() {
        return "Declare the datatype of the data being processed in the Content Modifier step.";
    }
}
