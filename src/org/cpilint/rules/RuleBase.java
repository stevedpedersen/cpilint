package org.cpilint.rules;

import java.util.Objects;
import java.util.Optional;

import org.cpilint.consumers.IssueConsumer;
import org.cpilint.issues.Severity;

abstract class RuleBase implements Rule {
	
	protected IssueConsumer consumer;
	protected Optional<String> ruleId = Optional.empty();
	
	// New documentation fields
	protected Severity severity = Severity.INFO;
	protected String rationale = "";
	protected String example = "";
	protected String counterExample = "";
	protected String recommendation = "";

	@Override
	public void startTesting(IssueConsumer consumer) {
		this.consumer = Objects.requireNonNull(consumer, "consumer must not be null");
	}

	@Override
	public void endTesting() {
		// Any required action is taken in subclasses.
	}

	@Override
	public void setId(String id) {
		if (ruleId.isPresent()) {
			throw new IllegalStateException("Rule ID already set");
		}
		ruleId = Optional.of(Objects.requireNonNull(id, "id must not be null"));
	}

	@Override
	public Optional<String> getId() {
		return ruleId;
	}
	
	@Override
	public Severity getSeverity() {
		return severity;
	}
	
	@Override
	public void setSeverity(Severity severity) {
		this.severity = Objects.requireNonNull(severity, "severity must not be null");
	}
	
	@Override
	public String getRationale() {
		return rationale;
	}
	
	@Override
	public void setRationale(String rationale) {
		this.rationale = rationale != null ? rationale : "";
	}
	
	@Override
	public String getExample() {
		return example;
	}
	
	@Override
	public void setExample(String example) {
		this.example = example != null ? example : "";
	}
	
	@Override
	public String getCounterExample() {
		return counterExample;
	}
	
	@Override
	public void setCounterExample(String counterExample) {
		this.counterExample = counterExample != null ? counterExample : "";
	}
	
	@Override
	public String getRecommendation() {
		return recommendation;
	}
	
	@Override
	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation != null ? recommendation : "";
	}
}
