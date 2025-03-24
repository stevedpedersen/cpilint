package org.cpilint.consumers;

import org.cpilint.issues.Issue;
import org.cpilint.suppliers.IflowArtifactSupplierError;
import java.util.ArrayList;
import java.util.List;

public abstract class FileIssueConsumer implements IssueConsumer {

    protected final String outputFilePath;
    protected final List<Issue> allIssues = new ArrayList<>();

    public FileIssueConsumer(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    @Override
    public void consume(Issue issue) {
        allIssues.add(issue);
    }

    @Override
    public int issuesConsumed() {
        return allIssues.size();
    }

    public void writeIssuesToFile() throws IflowArtifactSupplierError {
        writeIssues(allIssues, outputFilePath);
    }

    protected abstract void writeIssues(List<Issue> issues, String filePath) throws IflowArtifactSupplierError;
}
