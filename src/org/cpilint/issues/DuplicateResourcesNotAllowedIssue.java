package org.cpilint.issues;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.cpilint.artifacts.ArtifactResource;
import org.cpilint.artifacts.ArtifactResourceType;

public final class DuplicateResourcesNotAllowedIssue extends IssueBase {
	
	public static final String RULE_ID = "duplicate-resources-not-allowed";
	private final ArtifactResourceType type;
	private final Set<ArtifactResource> duplicateResources;
	
	public DuplicateResourcesNotAllowedIssue(Optional<String> ruleId, ArtifactResourceType type, Set<ArtifactResource> duplicateResources, Severity severity) {
		super(Optional.ofNullable(ruleId.isPresent() ? ruleId.get() : RULE_ID),
			duplicateResources.stream().map(r -> r.getTag()).collect(Collectors.toSet()),
			buildMessage(type, duplicateResources), severity);

		if (ruleId.isPresent() && !ruleId.get().equals(RULE_ID)) {
			System.out.println("Unexpected Rule Identifier being used: " + ruleId.get());
		} 
		this.type = type;
		this.duplicateResources = duplicateResources;
	}
	
	public ArtifactResourceType getType() {
		return type;
	}
	
	public Set<ArtifactResource> getDuplicateResources() {
		return Collections.unmodifiableSet(duplicateResources);
	}
	
	private static String buildMessage(ArtifactResourceType type, Set<ArtifactResource> duplicateResources) {
		assert !duplicateResources.isEmpty();
		StringBuilder sb = new StringBuilder();
		sb.append("The following ");
		sb.append(type.getName());
		sb.append(" resources are duplicates, which is not allowed: ");
		sb.append(IssuesUtil.capitalizeFirst(duplicateResources
			.stream()
			.map(r -> String.format("resource '%s' in iflow '%s' (ID '%s')", r.getName(), r.getTag().getName(), r.getTag().getId()))
			.collect(Collectors.joining(", "))));		
		return sb.toString();
	}
	
	@Override
	public String getRationale() {
		return "Duplicate resources create confusion and increase the risk of avoidable errors.";
	}
	
	@Override
	public String getExample() {
		return "Reuse a logging script at both the start and end of an iFlow.";
	}
	
	@Override
	public String getCounterExample() {
		return "Create separate, identical Groovy scripts for XML namespace removal.";
	}
	
	@Override
	public String getRecommendation() {
		return "Reuse existing resources instead of creating new copies.";
	}
}
