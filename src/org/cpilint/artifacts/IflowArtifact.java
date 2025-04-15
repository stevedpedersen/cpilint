package org.cpilint.artifacts;

import java.util.Collection;
import java.util.Map;

import org.cpilint.IflowXml;

public interface IflowArtifact {
	
	public Collection<ArtifactResource> getResourcesByType(ArtifactResourceType type);
	
	public IflowXml getIflowXml();
	
	public IflowArtifactTag getTag();
	
	public Map<String, String> getMetainfo();
}
