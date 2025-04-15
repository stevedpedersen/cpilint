package org.cpilint.rules;

import org.cpilint.IflowXml;
import org.cpilint.artifacts.ArtifactResource;
import org.cpilint.artifacts.ArtifactResourceType;
import org.cpilint.artifacts.IflowArtifact;
import org.cpilint.artifacts.IflowArtifactTag;
import org.cpilint.issues.IflowDescriptionRequiredIssue;
import org.cpilint.model.XmlModel;
import org.cpilint.model.XmlModelFactory;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import java.io.IOException;
import java.util.Properties;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class IflowDescriptionRequiredRule extends RuleBase {
    private static final Logger logger = LoggerFactory.getLogger(IflowDescriptionRequiredRule.class);
    
    @Override
    public void inspect(IflowArtifact iflow) {
        IflowXml iflowXml = iflow.getIflowXml();
        XmlModel model = XmlModelFactory.getModelFor(iflowXml);
        IflowArtifactTag tag = iflow.getTag();
        
        // Check for description in iflow XML
        XdmValue descriptionNodes = iflowXml.evaluateXpath(model.xpathForIflowDescription());
        boolean hasDescriptionInXml = descriptionNodes.size() > 0;
        
        // Check metainfo.prop
        Map<String, String> metainfo = iflow.getMetainfo();
        String description = metainfo.get("description");
        boolean hasDescriptionInMeta = description != null && !description.trim().isEmpty();
        
        // If neither XML nor metainfo.properties has a description, create an issue
        if (!hasDescriptionInXml && !hasDescriptionInMeta) {
            consumer.consume(new IflowDescriptionRequiredIssue(ruleId, tag));
        }
    }
}
