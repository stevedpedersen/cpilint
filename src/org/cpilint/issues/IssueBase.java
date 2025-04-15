package org.cpilint.issues;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.cpilint.artifacts.IflowArtifactTag;
import org.cpilint.artifacts.PackageInfo;

abstract class IssueBase implements Issue {
	
	private final Optional<String> ruleId;
	private final Set<IflowArtifactTag> tags;
	private final String message;
	private Severity severity;
	private final String rationale;
	private final String example;
	private final String counterExample;
	private final String recommendation;
	
	protected IssueBase(String message) {
		// this(Optional.empty(), new IflowArtifactTag(null, null), message);
		this(Optional.empty(), Set.of(new IflowArtifactTag(null, null)), message, Severity.INFO, "", "", "", "");
	}

	protected IssueBase(Optional<String> ruleId, IflowArtifactTag tag, String message) {
		this(ruleId, Set.of(Objects.requireNonNull(tag, "tag must not be null")), message);
	}

	protected IssueBase(Optional<String> ruleId, Set<IflowArtifactTag> tags, String message) {
		this(
			Objects.requireNonNull(ruleId, "ruleId must not be null"),
			new HashSet<>(tags),
			Objects.requireNonNull(message, "message must not be null"),
			Severity.INFO
		);
	}

	protected IssueBase(Optional<String> ruleId, Set<IflowArtifactTag> tags, String message, Severity severity) {
		// The set of tags must not be null and must not be empty.
		Objects.requireNonNull(tags, "tags must not be null");
		if (tags.isEmpty()) {
			throw new IllegalArgumentException("No iflow artifacts tags provided");
		}
		this.ruleId = Objects.requireNonNull(ruleId, "ruleId must not be null");
		this.tags = new HashSet<>(tags);
		this.message = Objects.requireNonNull(message, "message must not be null");
		this.severity = Objects.requireNonNull(severity, "severity must not be null");
		
		// Initialize the additional fields with default values
		this.rationale = "";
		this.example = "";
		this.counterExample = "";
		this.recommendation = "";
	}
	
	/**
	 * Extended constructor with additional documentation fields
	 */
	protected IssueBase(
			Optional<String> ruleId, 
			Set<IflowArtifactTag> tags, 
			String message, 
			Severity severity,
			String rationale,
			String example,
			String counterExample,
			String recommendation) {
		// The set of tags must not be null and must not be empty.
		Objects.requireNonNull(tags, "tags must not be null");
		if (tags.isEmpty()) {
			throw new IllegalArgumentException("No iflow artifacts tags provided");
		}
		this.ruleId = Objects.requireNonNull(ruleId, "ruleId must not be null");
		this.tags = new HashSet<>(tags);
		this.message = Objects.requireNonNull(message, "message must not be null");
		this.severity = Objects.requireNonNull(severity, "severity must not be null");
		
		// Initialize the additional fields
		this.rationale = rationale != null ? rationale : "";
		this.example = example != null ? example : "";
		this.counterExample = counterExample != null ? counterExample : "";
		this.recommendation = recommendation != null ? recommendation : "";
	}

	@Override
	public Optional<String> getRuleId() {
		return ruleId;
	}

	@Override
	public Set<IflowArtifactTag> getTags() {
		return tags;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
	@Override
	public Severity getSeverity() {
		return this.severity;
	}

	@Override
	public String getIflowId() {
		return tags.stream()
			.findFirst()
			.map(IflowArtifactTag::getId)
			.orElse("");
	}
	
	@Override
	public String getIflowName() {
		return tags.stream()
			.findFirst()
			.map(IflowArtifactTag::getName)
			.orElse("");
	}
	
	@Override
	public String getRationale() {
		return this.rationale;
	}
	
	@Override
	public String getExample() {
		return this.example;
	}
	
	@Override
	public String getCounterExample() {
		return this.counterExample;
	}
	
	@Override
	public String getRecommendation() {
		return this.recommendation;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		/*
			* If this issue has a single source iflow, include that iflow's name,
			* ID and package information (if available). If not, only include the
			* message.
			*/
		if (tags.size() == 1) {
			IflowArtifactTag tag = tags.iterator().next();
			Optional<PackageInfo> pinfo = tag.getPackageInfo();
			sb.append("In iflow '%s' (ID '%s')".formatted(tag.getName(), tag.getId()));
			if (pinfo.isPresent()) {
				sb.append(" of package '%s' (ID '%s')".formatted(pinfo.get().name(), pinfo.get().id()));
			}
			sb.append(": ");
		}
		sb.append(message);
		return sb.toString();
	}

	protected void setSeverity(Severity severity) {
		this.severity = severity;
	}
}
