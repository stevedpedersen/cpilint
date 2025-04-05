package org.cpilint.consumers;

import org.cpilint.issues.Issue;
import org.cpilint.issues.IflowDescriptionRequiredIssue;
import org.cpilint.issues.NamingConventionsRuleIssue;
import org.cpilint.issues.DisallowedMappingTypeIssue;
import org.cpilint.artifacts.IflowArtifactTag;
import org.cpilint.suppliers.IflowArtifactSupplierError;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonFileIssueConsumer extends FileIssueConsumer {

    public JsonFileIssueConsumer(String outputFilePath) {
        super(outputFilePath);
    }

    @Override
    protected void writeIssues(List<Issue> issues, String filePath) throws IflowArtifactSupplierError {
        JSONArray jsonArray = new JSONArray();

        for (Issue issue : issues) {
            JSONObject jsonIssue = new JSONObject();
            jsonIssue.put("message", issue.getMessage());

            // Extracting clearly from tags:
            issue.getTags().stream().findFirst().ifPresent(tag -> {
                jsonIssue.put("iflowId", tag.getId());
                jsonIssue.put("iflowName", tag.getName());
            });

            // Add rule information
            jsonIssue.put("ruleId", issue.getRuleId().orElse(""));
            jsonIssue.put("ruleClass", issue.getClass().getName());
            
            // Add criteria (if available)
            if (issue instanceof IflowDescriptionRequiredIssue) {
                jsonIssue.put("criteria", "Integration flow must have a description in either the iflow XML or metainfo.properties");
            } else if (issue instanceof NamingConventionsRuleIssue) {
                jsonIssue.put("criteria", "Name must match the required naming convention");
                jsonIssue.put("actualName", ((NamingConventionsRuleIssue) issue).getActualName());
            } else if (issue instanceof DisallowedMappingTypeIssue) {
                DisallowedMappingTypeIssue mappingIssue = (DisallowedMappingTypeIssue) issue;
                jsonIssue.put("criteria", String.format(
                    "Mapping step must not use %s mapping", mappingIssue.getMappingType().name()));
            }

            jsonArray.put(jsonIssue);
        }

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4));
        } catch (IOException e) {
            throw new IflowArtifactSupplierError("Error writing issues to JSON file.", e);
        }
    }
}
