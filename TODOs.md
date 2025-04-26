# TODOs for CPILint Enhancements
## Naming
https://help.sap.com/docs/cloud-integration/sap-cloud-integration/naming-conventions
# iFlow Memory
https://help.sap.com/docs/cloud-integration/sap-cloud-integration/consider-basic-layout-principles
An integration flow can handle:
1 Main Integration Process pool

5 Local Integration Process pool

25 flow steps per pool

## JnJ/SAP Avoiding Common Pitfalls:
If you use * as a value for Allowed Headers, all the HTTP headers from the sender are passed to the receiver. This way you will not have any control over what was sent and sometimes it may even confuse the receiver.

Use of Byte instead of String reduces the memory consumption: mappingByteVSString

Do not access or set a datastore (local variable, global variable or aggregator) from within a parallel multicast. The parallel multicast spawns multiple threads, but a database transaction cannot be shared over multiple threads. Check if a sequential multicast can be used instead.

Global variables make use of headers to perform db persist. Memory to these headers remain allocated even after the flow ends. In case the header is holding a large amount of data, it may fail the integration flow processing. It is important to release the memory allocated to the header (having the same name as the global variable) before the integration flow exits. This however cannot be performed from a content modifier - it will need to be done via a script.

Local variables are alive even after the integration flow execution is over. Even though the local variable is visible only to the integration flow, it is important to note that the memory allocated to the variable is not variable is not released when the integration flow execution is over. It is important to reset the local variable in the beginning of the flow to avoid any values getting used from the previous flow. It is specifically relevant when the previous flow ended abruptly and the variable may be holding invalid data. For better memory management, it also makes sense to reset the local variables before exiting the integration flow.

Never use an Aggregator step in a sub-process. The moment the sub-process exits the handle to the Aggregator is lost but the Aggregator stays allocated. It appears in Message Monitoring. Undeploying the corresponding integration flow does not release the Aggregator. Since Aggregator stores its data in the database, it poses stability and performance threats to the usage of database.

## Core CPILint Improvements

### JPM Integration Priority Items
- [ ] **Enhance artifact handling for JPM integration**
  - Create `MultiDirectoryArtifactSupplier` to process sibling directories with different artifact types
  - Support unzipped iFlows, Value Maps, and Script Collections as sibling directories
  - Implement directory structure detection to identify artifact types
  - Update CliClient to accept a parent directory containing multiple artifact directories

- [ ] **Adapt CliClient for Jenkins Pipeline Manager**
  - Add command-line option for JPM integration mode
  - Support passing working directory containing multiple artifact directories
  - Create JSON output format compatible with JPM reporting
  - Add option to specify which artifact types to scan

### Rule System Enhancements
- [ ] Add support for rule examples in XML rulesets
  - Create a new `<example>` tag in rule definitions to provide positive examples
  - Add a `<counterexample>` tag to show what should be avoided
  - Update rule factories to parse and store these examples
  
- [ ] Add severity levels to rules
  - Implement `ERROR`, `WARNING`, and `INFO` severity levels
  - Allow ruleset configuration to override default severities
  - Update issue reporting to display severity appropriately

- [ ] Implement rule explanation system
  - Add `<rationale>` tag to explain why a rule exists
  - Create `<recommendation>` tag for remediation steps
  - Ensure rule factories can parse and include these in issue reports

- [ ] Create rule categories for better organization
  - Security rules
  - Naming convention rules
  - Performance rules
  - ILCD Framework compliance rules

- [ ] Add rule configuration parameters
  - Allow rules to have customizable parameters
  - Support parameter overrides in ruleset XML files

- [ ] 

### User Experience Improvements
- [ ] Implement HTML report generation
  - Create a visually appealing HTML report template
  - Include rule examples, rationales, and recommendations in reports
  - Group issues by category and severity

- [ ] Add support for inline suppression comments
  - Define a comment format to suppress specific rules
  - Implement parser to detect and honor suppressions

- [ ] Create a configuration file validator
  - Tool to validate ruleset XML files before using them
  - Provide helpful error messages for malformed definitions

## ILCD Framework Validation

### Content Modifier Property Rules
- [ ] **Implement ContentModifierPositionRule** (HIGH PRIORITY)
  - Check for Content Modifier within first 1-5 steps
  - Verify required properties (projectName and integrationID) are set
  - Create corresponding rule factory and issue classes

- [ ] **Implement PropertyOrderingRule**
  - Ensure ILCD properties are defined at the top of property lists
  - Check ordering of critical properties

### ILCD Script Collection Validation
- [ ] **Implement ILCDScriptImportRule**
  - Verify correct import of ILCD script collection
  - Check for references to standard ILCD libraries
  - Create Issue class with appropriate remediation instructions

- [ ] **Implement ILCDStepSequenceRule**
  - Validate that ILCD steps are called in the correct sequence
  - Check for mandatory steps in ILCD workflows
  - Add configuration options for step sequences

### Error Handling Validation
- [ ] **Implement ILCDErrorHandlingRule**
  - Check for proper implementation of error handling patterns
  - Verify integration with ILCD error logging framework
  - Validate error propagation according to ILCD standards

### ILCD Ruleset Creation
- [ ] Create a comprehensive ILCD ruleset XML
  - Include all ILCD-specific rules
  - Set appropriate severities and categories
  - Add detailed examples and rationales

## Value Maps and Script Collections Support
- [ ] **Implement Value Map Validation** (HIGH PRIORITY)
  - Create ValueMapArtifact class extending from base artifact
  - Support linting of Value Map naming conventions
  - Verify Value Map structure and content format

- [ ] **Implement Script Collection Validation** (HIGH PRIORITY)
  - Create ScriptCollectionArtifact class for Script Collection handling
  - Support linting of script naming conventions
  - Verify script imports and dependencies
  - Check for ILCD script patterns

## Integration with CI/CD
- [ ] Create Jenkins integration guide
  - Document how to include CPILint in Jenkins pipelines
  - Provide example configuration for fail-on-error settings

- [ ] Implement GitHub Actions workflow
  - Create reusable GitHub Actions for CPILint validation
  - Support PR comments with linting results

## Features from Other Linters

### Auto-fixing Capability
- [ ] Implement basic auto-fix functionality for simple issues
  - Focus on naming conventions first
  - Add XML structure corrections
  - Support for applying ILCD templates

### Rule Configuration Profiles
- [ ] Create profile system for different teams/projects
  - Allow teams to define their own rule priorities
  - Support inheritance between profiles
  - Create migration tools between profiles

### Documentation Improvements
- [ ] Generate rule documentation from code
  - Create Markdown documentation for each rule
  - Include examples, counterexamples, and rationales
  - Link to SAP best practices where applicable

## Testing
- [ ] Create comprehensive test suite for new rules
  - Unit tests for rule logic
  - Integration tests with sample iFlows
  - Regression tests for existing functionality

- [ ] Implement test coverage reporting
  - Add JaCoCo or similar tool for code coverage
  - Set minimum coverage thresholds

## Implementation Roadmap

### Phase 1: JPM Integration and ILCD Framework (Priority)
1. ContentModifierPositionRule implementation
2. MultiDirectoryArtifactSupplier for sibling directories
3. Value Maps and Script Collections support
4. CLI client updates for JPM integration

### Phase 2: Core Enhancements
1. Rule examples and explanations
2. Severity levels
3. HTML reporting

### Phase 3: Additional ILCD Framework Rules
1. Script collection validation
2. Error handling validation
3. ILCD ruleset creation

### Phase 4: Advanced Features
1. Auto-fixing capability
2. Rule configuration profiles
3. CI/CD integration

## Project Tracking
- Create JIRA (or preferred tool) tickets for each TODO item
- Assign priorities based on developer needs
- Schedule regular review of implementation progress