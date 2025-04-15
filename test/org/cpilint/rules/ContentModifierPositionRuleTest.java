package org.cpilint.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.cpilint.IflowTag;
import org.cpilint.IflowXml;
import org.cpilint.artifacts.ZipArchiveIflowArtifact;
import org.cpilint.issues.ContentModifierPositionIssue;
import org.cpilint.issues.Issue;
import org.cpilint.model.XmlModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

/**
 * Test for ContentModifierPositionRule to verify its functionality.
 */
public class ContentModifierPositionRuleTest {
    
    @Mock
    private IssueConsumer issueConsumer;
    
    @Mock
    private ZipArchiveIflowArtifact artifact;
    
    @Mock
    private IflowTag tag;
    
    @Mock
    private IflowXml iflowXml;
    
    @Mock
    private XmlModel xmlModel;
    
    @Mock
    private XdmValue allStepsResult;
    
    @Mock
    private XdmValue contentModifierStepsResult;
    
    @Mock
    private XdmNode contentModifierStepNode;
    
    @Mock
    private XdmValue propertiesResult;
    
    @Mock
    private XdmNode propertyNode;
    
    private ContentModifierPositionRule rule;
    
    @Before
    public void setUp() throws IOException, SaxonApiException {
        MockitoAnnotations.initMocks(this);
        
        rule = new ContentModifierPositionRule();
        
        // Setup common test data
        when(tag.getName()).thenReturn("TestIflow");
        when(artifact.getTag()).thenReturn(tag);
        
        // Setup XML model returns
        when(xmlModel.xpathForAllSteps()).thenReturn("//steps");
        when(xmlModel.xpathForStepsByType("ContentModifier")).thenReturn("//steps[type='ContentModifier']");
        when(xmlModel.xpathForPropertiesInStep("step1")).thenReturn("//steps[@id='step1']/properties");
        when(xmlModel.getStepIdFromElement(contentModifierStepNode)).thenReturn("step1");
        when(xmlModel.getStepNameFromElement(contentModifierStepNode)).thenReturn("Content Modifier");
        
        // Setup iflowXml
        when(iflowXml.getXmlModel()).thenReturn(xmlModel);
    }
    
    @Test
    public void testNoContentModifier() throws SaxonApiException {
        // Setup no content modifier steps found
        when(iflowXml.evaluateXpath(xmlModel.xpathForAllSteps())).thenReturn(allStepsResult);
        when(allStepsResult.size()).thenReturn(5);
        when(iflowXml.evaluateXpath(xmlModel.xpathForStepsByType("ContentModifier"))).thenReturn(contentModifierStepsResult);
        when(contentModifierStepsResult.size()).thenReturn(0);
        
        // Execute
        rule.inspect(artifact, iflowXml, issueConsumer);
        
        // Verify
        ArgumentCaptor<Issue> issueCaptor = forClass(Issue.class);
        verify(issueConsumer).consume(issueCaptor.capture());
        
        Issue issue = issueCaptor.getValue();
        assertTrue(issue instanceof ContentModifierPositionIssue);
        ContentModifierPositionIssue cmIssue = (ContentModifierPositionIssue) issue;
        assertEquals(ContentModifierPositionIssue.IssueType.MISSING, cmIssue.getIssueType());
    }
    
    @Test
    public void testContentModifierWrongPosition() throws SaxonApiException {
        // Setup content modifier at wrong position (position 6)
        when(iflowXml.evaluateXpath(xmlModel.xpathForAllSteps())).thenReturn(allStepsResult);
        when(allStepsResult.size()).thenReturn(10);
        when(iflowXml.evaluateXpath(xmlModel.xpathForStepsByType("ContentModifier"))).thenReturn(contentModifierStepsResult);
        when(contentModifierStepsResult.size()).thenReturn(1);
        when(contentModifierStepsResult.itemAt(0)).thenReturn(contentModifierStepNode);
        
        // Mock actual position by returning 6 for the content modifier step position
        List<XdmItem> allSteps = Arrays.asList(
            mockXdmItem("step0"), mockXdmItem("step2"), mockXdmItem("step3"),
            mockXdmItem("step4"), mockXdmItem("step5"), contentModifierStepNode
        );
        setupAllStepsIterator(allStepsResult, allSteps);
        
        // Execute
        rule.inspect(artifact, iflowXml, issueConsumer);
        
        // Verify
        ArgumentCaptor<Issue> issueCaptor = forClass(Issue.class);
        verify(issueConsumer).consume(issueCaptor.capture());
        
        Issue issue = issueCaptor.getValue();
        assertTrue(issue instanceof ContentModifierPositionIssue);
        ContentModifierPositionIssue cmIssue = (ContentModifierPositionIssue) issue;
        assertEquals(ContentModifierPositionIssue.IssueType.WRONG_POSITION, cmIssue.getIssueType());
    }
    
    @Test
    public void testContentModifierMissingProperties() throws SaxonApiException {
        // Setup content modifier at correct position but missing properties
        when(iflowXml.evaluateXpath(xmlModel.xpathForAllSteps())).thenReturn(allStepsResult);
        when(allStepsResult.size()).thenReturn(10);
        when(iflowXml.evaluateXpath(xmlModel.xpathForStepsByType("ContentModifier"))).thenReturn(contentModifierStepsResult);
        when(contentModifierStepsResult.size()).thenReturn(1);
        when(contentModifierStepsResult.itemAt(0)).thenReturn(contentModifierStepNode);
        
        // Mock position 3 for content modifier (which is valid)
        List<XdmItem> allSteps = Arrays.asList(
            mockXdmItem("step0"), mockXdmItem("step2"), contentModifierStepNode,
            mockXdmItem("step4"), mockXdmItem("step5"), mockXdmItem("step6")
        );
        setupAllStepsIterator(allStepsResult, allSteps);
        
        // Setup properties missing required ones
        when(iflowXml.evaluateXpath(xmlModel.xpathForPropertiesInStep("step1"))).thenReturn(propertiesResult);
        when(propertiesResult.size()).thenReturn(2);
        when(propertiesResult.iterator()).thenReturn(Arrays.asList(
            mockPropertyNode("someKey", "someValue"),
            mockPropertyNode("anotherKey", "anotherValue")
        ).iterator());
        
        // Execute
        rule.inspect(artifact, iflowXml, issueConsumer);
        
        // Verify
        ArgumentCaptor<Issue> issueCaptor = forClass(Issue.class);
        verify(issueConsumer).consume(issueCaptor.capture());
        
        Issue issue = issueCaptor.getValue();
        assertTrue(issue instanceof ContentModifierPositionIssue);
        ContentModifierPositionIssue cmIssue = (ContentModifierPositionIssue) issue;
        assertEquals(ContentModifierPositionIssue.IssueType.MISSING_PROPERTIES, cmIssue.getIssueType());
    }
    
    @Test
    public void testContentModifierCorrect() throws SaxonApiException {
        // Setup content modifier at correct position with correct properties
        when(iflowXml.evaluateXpath(xmlModel.xpathForAllSteps())).thenReturn(allStepsResult);
        when(allStepsResult.size()).thenReturn(10);
        when(iflowXml.evaluateXpath(xmlModel.xpathForStepsByType("ContentModifier"))).thenReturn(contentModifierStepsResult);
        when(contentModifierStepsResult.size()).thenReturn(1);
        when(contentModifierStepsResult.itemAt(0)).thenReturn(contentModifierStepNode);
        
        // Mock position 2 for content modifier (which is valid)
        List<XdmItem> allSteps = Arrays.asList(
            mockXdmItem("step0"), contentModifierStepNode, mockXdmItem("step2"),
            mockXdmItem("step4"), mockXdmItem("step5"), mockXdmItem("step6")
        );
        setupAllStepsIterator(allStepsResult, allSteps);
        
        // Setup properties with required ones
        when(iflowXml.evaluateXpath(xmlModel.xpathForPropertiesInStep("step1"))).thenReturn(propertiesResult);
        when(propertiesResult.size()).thenReturn(3);
        when(propertiesResult.iterator()).thenReturn(Arrays.asList(
            mockPropertyNode("projectName", "TestProject"),
            mockPropertyNode("integrationID", "INT123"),
            mockPropertyNode("anotherKey", "anotherValue")
        ).iterator());
        
        // Execute
        rule.inspect(artifact, iflowXml, issueConsumer);
        
        // Verify that no issues were created
        ArgumentCaptor<Issue> issueCaptor = forClass(Issue.class);
        verify(issueConsumer, javax.mock.Mockito.never()).consume(issueCaptor.capture());
    }
    
    // Helper methods to setup mocks
    
    private XdmItem mockXdmItem(String id) {
        XdmNode node = org.mockito.Mockito.mock(XdmNode.class);
        when(xmlModel.getStepIdFromElement(node)).thenReturn(id);
        return node;
    }
    
    private void setupAllStepsIterator(XdmValue value, List<XdmItem> items) {
        when(value.iterator()).thenReturn(items.iterator());
    }
    
    private XdmNode mockPropertyNode(String key, String value) {
        XdmNode node = org.mockito.Mockito.mock(XdmNode.class);
        when(xmlModel.getPropertyNameFromElement(node)).thenReturn(key);
        when(xmlModel.getPropertyValueFromElement(node)).thenReturn(value);
        return node;
    }
}
