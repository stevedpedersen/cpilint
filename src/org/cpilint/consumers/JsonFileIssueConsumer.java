package org.cpilint.consumers;

import org.cpilint.issues.Issue;
// import org.cpilint.artifacts.IflowArtifactTag;
import org.cpilint.suppliers.IflowArtifactSupplierError;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public final class JsonFileIssueConsumer extends FileIssueConsumer {

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

            jsonArray.put(jsonIssue);
        }

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4));
        } catch(IOException e) {
            throw new IflowArtifactSupplierError("Error writing issues to JSON file.", e);
        }
    }
}
