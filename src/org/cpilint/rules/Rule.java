package org.cpilint.rules;

import java.util.Optional;

import org.cpilint.artifacts.IflowArtifact;
import org.cpilint.consumers.IssueConsumer;
import org.cpilint.issues.Severity;

public interface Rule {
	
	public void startTesting(IssueConsumer consumer);
	
	public void inspect(IflowArtifact iflow);
	
	public void endTesting();

	public void setId(String id);

	public Optional<String> getId();
	
	// New documentation field methods
	public Severity getSeverity();
	
	public void setSeverity(Severity severity);
	
	public String getRationale();
	
	public void setRationale(String rationale);
	
	public String getExample();
	
	public void setExample(String example);
	
	public String getCounterExample();
	
	public void setCounterExample(String counterExample);
	
	public String getRecommendation();
	
	public void setRecommendation(String recommendation);

}
