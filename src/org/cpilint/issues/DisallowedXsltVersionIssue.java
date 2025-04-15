package org.cpilint.issues;

import java.util.Optional;
import java.util.Set;

import org.cpilint.artifacts.IflowArtifactTag;
import org.cpilint.model.XsltVersion;

public final class DisallowedXsltVersionIssue extends IssueBase {
	
	private final String stylesheetName;
	private final XsltVersion version;
	
	public DisallowedXsltVersionIssue(Optional<String> ruleId, IflowArtifactTag tag, String stylesheetName, XsltVersion version, Severity severity) {
		super(ruleId, Set.of(tag), String.format(
			"The XSLT stylesheet '%s' is written in XSLT version %s, which is not allowed.",
			stylesheetName,
			version.getVersionString()), severity);
		this.stylesheetName = stylesheetName;
		this.version = version;
	}
	
	public DisallowedXsltVersionIssue(Optional<String> ruleId, IflowArtifactTag tag, String stylesheetName, XsltVersion version) {
		this(ruleId, tag, stylesheetName, version, Severity.CRITICAL);
	}

	public String getStylesheetName() {
		return stylesheetName;
	}
	
	public XsltVersion getVersion() {
		return version;
	}

	@Override
	public String getRationale() {
		return "Supported XSLT versions are selected based on stability, security, and compatibility.";
	}
	
	@Override
	public String getExample() {
		return "XSLT 1.0, 2.0, or 3.0.";
	}
	
	@Override
	public String getCounterExample() {
		return "XSLT 4.0.";
	}
	
	@Override
	public String getRecommendation() {
		return "Use only the XSLT versions supported by the tenant.";
	}
}
