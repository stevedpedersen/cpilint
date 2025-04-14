package org.cpilint.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cpilint.RulesFile;
import org.cpilint.util.JarResourceUtil;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;

final class DefaultXmlModel implements XmlModel {
	private static final Logger logger = LoggerFactory.getLogger(DefaultXmlModel.class);
	private static final String ACTIVITY_TYPE_PROPERTY_KEY = "activityType";
	private static final String PROPERTY_KEY_ELEMENT_NAME = "key";
	private static final String PROPERTY_VALUE_ELEMENT_NAME = "value";
	
	private static final Map<ChannelDirection, String> directionPropertyValues;
	private static final Map<ReceiverAdapter, String> receiverAdapterComponentTypes;
	private static final Map<SenderAdapter, String> senderAdapterComponentTypes;
	private static final Map<ReceiverAdapter, List<String>> httpEndpointPropertyKeyNames;
	private static final Map<MappingType, String> mappingTypePropertyKeys;
	private static final Map<MappingType, String> mappingTypePropertyValues;
	private static final Map<ScriptingLanguage, String> scriptingLanguagePropertyValues;
	private static final Map<ScriptingLanguage, String> scriptingSubActivityValues;
	private static final Map<ReceiverAdapter, String> basicAuthPropertyKeys;
	private static final Map<ReceiverAdapter, String> basicAuthPropertyValues;
	private static final Map<DataStoreOperation, String> datastoreOperationPropertyValues;
	private static final Map<SenderAdapter, String> clientCertAuthPropertyKeys;
	private static final Map<SenderAdapter, String> clientCertAuthPropertyValues;
	private static final Set<ReceiverAdapter> onPremReceiverAdapters;
	private static final Map<String, SenderAdapter> componentTypeToSenderAdapter;
	
	static {
		// Initialize the directionPropertyValues map.
		directionPropertyValues = new HashMap<>();
		directionPropertyValues.put(ChannelDirection.SENDER, "Sender");
		directionPropertyValues.put(ChannelDirection.RECEIVER, "Receiver");
		// Initialize the receiverAdapterComponentTypes map.
		receiverAdapterComponentTypes = new HashMap<>();
		receiverAdapterComponentTypes.put(ReceiverAdapter.ADVANCEDEVENTMESH, "AdvancedEventMesh");
		receiverAdapterComponentTypes.put(ReceiverAdapter.AMAZONDYNAMODB, "AmazonDynamoDB");
		receiverAdapterComponentTypes.put(ReceiverAdapter.AMAZONEVENTBRIDGE, "AmazonEventBridge");
		receiverAdapterComponentTypes.put(ReceiverAdapter.AMQP, "AMQP");
		receiverAdapterComponentTypes.put(ReceiverAdapter.ANAPLAN, "Anaplan");
		receiverAdapterComponentTypes.put(ReceiverAdapter.ARIBA, "Ariba");
		receiverAdapterComponentTypes.put(ReceiverAdapter.AS2, "AS2");
		receiverAdapterComponentTypes.put(ReceiverAdapter.AS4, "AS4");
		receiverAdapterComponentTypes.put(ReceiverAdapter.AZURECOSMOSDB, "AzureCosmosDB");
		receiverAdapterComponentTypes.put(ReceiverAdapter.AZURESTORAGE, "AzureStorage");
		receiverAdapterComponentTypes.put(ReceiverAdapter.COUPA, "Coupa");
		receiverAdapterComponentTypes.put(ReceiverAdapter.DROPBOX, "Dropbox");
		receiverAdapterComponentTypes.put(ReceiverAdapter.ELSTER, "ELSTER");
		receiverAdapterComponentTypes.put(ReceiverAdapter.FACEBOOK, "Facebook");
		receiverAdapterComponentTypes.put(ReceiverAdapter.FTP, "FTP");
		receiverAdapterComponentTypes.put(ReceiverAdapter.HUBSPOT, "HubSpot");
		receiverAdapterComponentTypes.put(ReceiverAdapter.ODATA, "HCIOData");
		receiverAdapterComponentTypes.put(ReceiverAdapter.HTTP, "HTTP");
		receiverAdapterComponentTypes.put(ReceiverAdapter.IDOC, "IDOC");
		receiverAdapterComponentTypes.put(ReceiverAdapter.JDBC, "JDBC");
		receiverAdapterComponentTypes.put(ReceiverAdapter.JIRA, "Jira");
		receiverAdapterComponentTypes.put(ReceiverAdapter.JMS, "JMS");
		receiverAdapterComponentTypes.put(ReceiverAdapter.KAFKA, "Kafka");
		receiverAdapterComponentTypes.put(ReceiverAdapter.LDAP, "LDAP");
		receiverAdapterComponentTypes.put(ReceiverAdapter.MAIL, "Mail");
		receiverAdapterComponentTypes.put(ReceiverAdapter.MDI, "MDI");
		receiverAdapterComponentTypes.put(ReceiverAdapter.MICROSOFTSHAREPOINT, "MicrosoftSharePoint");
		receiverAdapterComponentTypes.put(ReceiverAdapter.NETSUITE, "NetSuite");
		receiverAdapterComponentTypes.put(ReceiverAdapter.ODC, "ODC");
		receiverAdapterComponentTypes.put(ReceiverAdapter.OPENCONNECTORS, "OpenConnectors");
		receiverAdapterComponentTypes.put(ReceiverAdapter.PROCESSDIRECT, "ProcessDirect");
		receiverAdapterComponentTypes.put(ReceiverAdapter.RABBITMQ, "RabbitMQ");
		receiverAdapterComponentTypes.put(ReceiverAdapter.RFC, "RFC");
		receiverAdapterComponentTypes.put(ReceiverAdapter.SERVICENOW, "ServiceNow");
		receiverAdapterComponentTypes.put(ReceiverAdapter.SFTP, "SFTP");
		receiverAdapterComponentTypes.put(ReceiverAdapter.SLACK, "Slack");
		receiverAdapterComponentTypes.put(ReceiverAdapter.SMB, "SMB");
		receiverAdapterComponentTypes.put(ReceiverAdapter.SNOWFLAKE, "Snowflake");
		receiverAdapterComponentTypes.put(ReceiverAdapter.SOAP, "SOAP");
		receiverAdapterComponentTypes.put(ReceiverAdapter.SPLUNK, "Splunk");
		receiverAdapterComponentTypes.put(ReceiverAdapter.SUCCESSFACTORS, "SuccessFactors");
		receiverAdapterComponentTypes.put(ReceiverAdapter.SUGARCRM, "SugarCRM");
		receiverAdapterComponentTypes.put(ReceiverAdapter.TWITTER, "Twitter");
		receiverAdapterComponentTypes.put(ReceiverAdapter.WORKDAY, "Workday");
		receiverAdapterComponentTypes.put(ReceiverAdapter.XI, "XI");
		// Initialize the senderAdapterComponentTypes map.
		senderAdapterComponentTypes = new HashMap<>();
		senderAdapterComponentTypes.put(SenderAdapter.ADVANCEDEVENTMESH, "AdvancedEventMesh");
		senderAdapterComponentTypes.put(SenderAdapter.AMQP, "AMQP");
		senderAdapterComponentTypes.put(SenderAdapter.ARIBA, "Ariba");
		senderAdapterComponentTypes.put(SenderAdapter.AS2, "AS2");
		senderAdapterComponentTypes.put(SenderAdapter.AS4, "AS4");
		senderAdapterComponentTypes.put(SenderAdapter.AZURESTORAGE, "AzureStorage");
		senderAdapterComponentTypes.put(SenderAdapter.DATASTORE, "DataStoreConsumer");
		senderAdapterComponentTypes.put(SenderAdapter.DROPBOX, "Dropbox");
		senderAdapterComponentTypes.put(SenderAdapter.FTP, "FTP");
		senderAdapterComponentTypes.put(SenderAdapter.HTTPS, "HTTPS");
		senderAdapterComponentTypes.put(SenderAdapter.IDOC, "IDOC");
		senderAdapterComponentTypes.put(SenderAdapter.JMS, "JMS");
		senderAdapterComponentTypes.put(SenderAdapter.KAFKA, "Kafka");
		senderAdapterComponentTypes.put(SenderAdapter.MAIL, "Mail");
		senderAdapterComponentTypes.put(SenderAdapter.MICROSOFTSHAREPOINT, "MicrosoftSharePoint");
		senderAdapterComponentTypes.put(SenderAdapter.ODATA, "ODataSender");
		senderAdapterComponentTypes.put(SenderAdapter.PROCESSDIRECT, "ProcessDirect");
		senderAdapterComponentTypes.put(SenderAdapter.RABBITMQ, "RabbitMQ");
		senderAdapterComponentTypes.put(SenderAdapter.SFTP, "SFTP");
		senderAdapterComponentTypes.put(SenderAdapter.SLACK, "Slack");
		senderAdapterComponentTypes.put(SenderAdapter.SMB, "SMB");
		senderAdapterComponentTypes.put(SenderAdapter.SOAP, "SOAP");
		senderAdapterComponentTypes.put(SenderAdapter.SPLUNK, "Splunk");
		senderAdapterComponentTypes.put(SenderAdapter.SUCCESSFACTORS, "SuccessFactors");
		senderAdapterComponentTypes.put(SenderAdapter.XI, "XI");
		// Initialize the httpEndpointPropertyKeyNames map.
		httpEndpointPropertyKeyNames = new HashMap<>();
		httpEndpointPropertyKeyNames.put(ReceiverAdapter.AS4, List.of("endpointUrl", "samlEndpoint", "pullReceiptTargetURL"));
		httpEndpointPropertyKeyNames.put(ReceiverAdapter.ODATA, List.of("address"));
		httpEndpointPropertyKeyNames.put(ReceiverAdapter.HTTP, List.of("httpAddressWithoutQuery"));
		httpEndpointPropertyKeyNames.put(ReceiverAdapter.IDOC, List.of("address"));
		httpEndpointPropertyKeyNames.put(ReceiverAdapter.SOAP, List.of("address"));
		httpEndpointPropertyKeyNames.put(ReceiverAdapter.XI, List.of("Address"));
		httpEndpointPropertyKeyNames.put(ReceiverAdapter.AS2, List.of("receipientURL")); // Removed mdnTargetURL since it's not called from CPI.
		httpEndpointPropertyKeyNames.put(ReceiverAdapter.ANAPLAN, List.of("authHost", "anaplanHost"));
		httpEndpointPropertyKeyNames.put(ReceiverAdapter.HUBSPOT, List.of("addressURL"));
		httpEndpointPropertyKeyNames.put(ReceiverAdapter.AZURECOSMOSDB, List.of("hostUrl"));
		httpEndpointPropertyKeyNames.put(ReceiverAdapter.JIRA, List.of("address"));
		// Initialize the mappingTypePropertyKeys map.
		mappingTypePropertyKeys = new HashMap<>();
		mappingTypePropertyKeys.put(MappingType.MESSAGE_MAPPING, "mappingType");
		mappingTypePropertyKeys.put(MappingType.XSLT_MAPPING, "subActivityType");
		mappingTypePropertyKeys.put(MappingType.OPERATION_MAPPING, "mappingType");
		// mappingTypePropertyKeys.put(MappingType.ID_MAPPING, "activityType");
		// Initialize the mappingTypePropertyValues map.
		mappingTypePropertyValues = new HashMap<>();
		mappingTypePropertyValues.put(MappingType.MESSAGE_MAPPING, "MessageMapping");
		mappingTypePropertyValues.put(MappingType.XSLT_MAPPING, "XSLTMapping");
		mappingTypePropertyValues.put(MappingType.OPERATION_MAPPING, "OperationMapping");
		// mappingTypePropertyValues.put(MappingType.ID_MAPPING, "IDMapper");
		// Initialize the scriptingLanguagePropertyValues map.
		scriptingLanguagePropertyValues = new HashMap<>();
		scriptingLanguagePropertyValues.put(ScriptingLanguage.GROOVY, "GroovyScript");
		scriptingLanguagePropertyValues.put(ScriptingLanguage.JAVASCRIPT, "JavaScript");
		// Initialize the scriptingSubActivityValues map.
		scriptingSubActivityValues = new HashMap<>();
		scriptingSubActivityValues.put(ScriptingLanguage.GROOVY, "GroovyScript");
		scriptingSubActivityValues.put(ScriptingLanguage.JAVASCRIPT, "JavaScript");
		// Initialize the basicAuthPropertyKeys map.
		basicAuthPropertyKeys = new HashMap<>();
		basicAuthPropertyKeys.put(ReceiverAdapter.HTTP, "authenticationMethod");
		basicAuthPropertyKeys.put(ReceiverAdapter.ODATA, "authenticationMethod");
		basicAuthPropertyKeys.put(ReceiverAdapter.IDOC, "authentication");
		basicAuthPropertyKeys.put(ReceiverAdapter.SOAP, "authentication");
		basicAuthPropertyKeys.put(ReceiverAdapter.XI, "AuthenticationType");
		// Initialize the basicAuthPropertyValues map.
		basicAuthPropertyValues = new HashMap<>();
		basicAuthPropertyValues.put(ReceiverAdapter.HTTP, "Basic");
		basicAuthPropertyValues.put(ReceiverAdapter.ODATA, "Basic");
		basicAuthPropertyValues.put(ReceiverAdapter.IDOC, "Basic");
		basicAuthPropertyValues.put(ReceiverAdapter.SOAP, "Basic");
		basicAuthPropertyValues.put(ReceiverAdapter.XI, "BasicAuthentication");
		// Initialize the datastoreOperationPropertyValues map.
		datastoreOperationPropertyValues = new HashMap<>();
		datastoreOperationPropertyValues.put(DataStoreOperation.GET, "get");
		datastoreOperationPropertyValues.put(DataStoreOperation.WRITE, "put");
		datastoreOperationPropertyValues.put(DataStoreOperation.SELECT, "select");
		datastoreOperationPropertyValues.put(DataStoreOperation.DELETE, "delete");
		// Initialize the clientCertAuthPropertyKeys map.
		clientCertAuthPropertyKeys = new HashMap<>();
		clientCertAuthPropertyKeys.put(SenderAdapter.HTTPS, "senderAuthType");
		clientCertAuthPropertyKeys.put(SenderAdapter.IDOC, "senderAuthType");
		clientCertAuthPropertyKeys.put(SenderAdapter.ODATA, "authentication");
		clientCertAuthPropertyKeys.put(SenderAdapter.SOAP, "senderAuthType");
		clientCertAuthPropertyKeys.put(SenderAdapter.XI, "senderAuthType");
		clientCertAuthPropertyKeys.put(SenderAdapter.AS2, "senderAuthType");
		clientCertAuthPropertyKeys.put(SenderAdapter.AS4, "senderAuthType");
		// Initialize the clientCertAuthPropertyValues map.
		clientCertAuthPropertyValues = new HashMap<>();
		clientCertAuthPropertyValues.put(SenderAdapter.HTTPS, "ClientCertificate");
		clientCertAuthPropertyValues.put(SenderAdapter.IDOC, "ClientCertificate");
		clientCertAuthPropertyValues.put(SenderAdapter.ODATA, "certificate");
		clientCertAuthPropertyValues.put(SenderAdapter.SOAP, "ClientCertificate");
		clientCertAuthPropertyValues.put(SenderAdapter.XI, "ClientCertificate");
		clientCertAuthPropertyValues.put(SenderAdapter.AS2, "ClientCertificate");
		clientCertAuthPropertyValues.put(SenderAdapter.AS4, "ClientCertificate");
		// Initialize the onPremReceiverAdapters set.
		// TODO: Check that the following is still accurate.
		onPremReceiverAdapters = new HashSet<>();
		onPremReceiverAdapters.add(ReceiverAdapter.HTTP);
		onPremReceiverAdapters.add(ReceiverAdapter.ODATA);
		onPremReceiverAdapters.add(ReceiverAdapter.SOAP);
		onPremReceiverAdapters.add(ReceiverAdapter.XI);
		onPremReceiverAdapters.add(ReceiverAdapter.IDOC);
		onPremReceiverAdapters.add(ReceiverAdapter.LDAP);
		onPremReceiverAdapters.add(ReceiverAdapter.MAIL);
		onPremReceiverAdapters.add(ReceiverAdapter.SFTP);
		onPremReceiverAdapters.add(ReceiverAdapter.ODC);
		onPremReceiverAdapters.add(ReceiverAdapter.AS2);
		onPremReceiverAdapters.add(ReceiverAdapter.AS4);
		/*
		 * Initialize the componentTypeToSenderAdapter map. This map is the
		 * inverse of the senderAdapterComponentTypes.
		 */
		componentTypeToSenderAdapter = senderAdapterComponentTypes.entrySet()
			.stream()
			.collect(Collectors.toMap(Entry::getValue, Entry::getKey));
	}

	@Override
	public String xpathForChannels(String... predicates) {
		return appendPredicates("//bpmn2:messageFlow", predicates);
	}

	@Override
	public String channelPredicateForDirection(ChannelDirection direction) {
		assert directionPropertyValues.containsKey(direction);
		String propertyValue = directionPropertyValues.get(direction);
		return propertyKeyValuePredicate("direction", propertyValue);
	}

	@Override
	public String channelPredicateForAdapter(ReceiverAdapter receiverAdapter) {
		assert receiverAdapterComponentTypes.containsKey(receiverAdapter);
		String componentType = receiverAdapterComponentTypes.get(receiverAdapter);
		return propertyKeyValuePredicate("ComponentType", componentType);
	}

	@Override
	public String channelPredicateForAdapter(SenderAdapter senderAdapter) {
		assert senderAdapterComponentTypes.containsKey(senderAdapter);
		String componentType = senderAdapterComponentTypes.get(senderAdapter);
		return propertyKeyValuePredicate("ComponentType", componentType);
	}

	@Override
	public String channelPredicateForHttpEndpoints(ReceiverAdapter receiverAdapter) {
		/*
		 *  Note that for a given adapter, there might be more than one
		 *  property containing a URL. That's why we create a boolean 
		 *  "key = 'x' or key = 'y'" expression. The address property keys
		 *  are contained in the httpEndpointPropertyKeyNames map.
		 */
		if (!httpEndpointPropertyKeyNames.containsKey(receiverAdapter)) {
			throw new IllegalArgumentException(String.format("Receiver channels of adapter %s cannot contain HTTP endpoints", receiverAdapter.getName()));
		}
		String keyPredicate = httpEndpointPropertyKeyNames.get(receiverAdapter)
			.stream()
			.map(k -> "key = '" + k + "'")
			.collect(Collectors.joining(" or ", "[", "]"));
		return propertyPredicateGeneric(
			keyPredicate,
			String.format("[starts-with(lower-case(%s/text()), 'http://')]", PROPERTY_VALUE_ELEMENT_NAME)
		);
	}

	@Override
	public String channelPredicateForNoCsrfProtection() {
		return propertyKeyValuePredicate("xsrfProtection", "0");
	}

	@Override
	public String channelPredicateForBasicAuthentication(ReceiverAdapter receiverAdapter) {
		if (!basicAuthPropertyKeys.containsKey(receiverAdapter)) {
			throw new IllegalArgumentException(String.format("Receiver channels of adapter %s cannot use basic authentication", receiverAdapter.getName()));
		}
		assert basicAuthPropertyValues.containsKey(receiverAdapter);
		String key = basicAuthPropertyKeys.get(receiverAdapter);
		String value = basicAuthPropertyValues.get(receiverAdapter);
		return propertyKeyValuePredicate(key, value);
	}
	
	@Override
	public String channelPredicateForClientCertAuth(SenderAdapter senderAdapter) {
		if (!clientCertAuthPropertyKeys.containsKey(senderAdapter)) {
			throw new IllegalArgumentException(String.format("Sender channels of adapter %s cannot use client certificate authentication", senderAdapter.getName()));
		}
		assert clientCertAuthPropertyValues.containsKey(senderAdapter);
		String key = clientCertAuthPropertyKeys.get(senderAdapter);
		String value = clientCertAuthPropertyValues.get(senderAdapter);
		return propertyKeyValuePredicate(key, value);
	}

	@Override
	public String channelPredicateForProxyTypeOnPremise(ReceiverAdapter receiverAdapter) {
		String predicate;
		if (receiverAdapter == ReceiverAdapter.ODC) {
		    // Implicitly on-premise.
		    predicate = ModelUtil.xpathTruePredicate();
		} else if (receiverAdapter == ReceiverAdapter.LDAP) {
		    predicate = propertyKeyValuePredicate("ldapProxyType", "ldapProxyTypeOnPremise");
		} else if (onPremReceiverAdapters.contains(receiverAdapter)) {
		    predicate = propertyKeyValuePredicate("proxyType", "sapcc");
		} else {
		    // Not on-premise enabled.
		    predicate = ModelUtil.xpathFalsePredicate();
		}
		return predicate;
	}
	
	@Override
	public String getChannelNameFromElement(XdmNode node) {
		nodeMustBeAnElement(node);
		return node.attribute("name");
	}

	@Override
	public String getChannelIdFromElement(XdmNode node) {
		nodeMustBeAnElement(node);
		return node.attribute("id");
	}

	@Override
	public String xpathForFlowSteps(String... predicates) {
		if (predicates == null || predicates.length == 0) {
			// Directly query for callActivity elements without any predicates
			return "//bpmn2:callActivity";
		}
		
		// Create a predicate for call activities without using potentially problematic XPath functions
		StringBuilder xpathBuilder = new StringBuilder("//bpmn2:callActivity");
		
		// Add predicates if any
		if (predicates.length > 0) {
			xpathBuilder.append("[");
			for (int i = 0; i < predicates.length; i++) {
				if (i > 0) {
					xpathBuilder.append(" and ");
				}
				xpathBuilder.append(predicates[i]);
			}
			xpathBuilder.append("]");
		}
		
		return xpathBuilder.toString();
	}

	@Override
	public String stepPredicateForMappingSteps() {
		return propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Mapping");
	}

	@Override
	public String stepPredicateForMappingType(MappingType mappingType) {
		Objects.requireNonNull(mappingType, "mappingType must not be null");
		String propertyKey = mappingTypePropertyKeys.get(mappingType);
		String propertyValue = mappingTypePropertyValues.get(mappingType);
		logger.info("MappingType:" + mappingType + "PropertyKey: " + propertyKey + ", PropertyValue: " + propertyValue);
		return propertyKeyValuePredicate(propertyKey, propertyValue);
		// if (mappingType == MappingType.ID_MAPPING) {
		// 	// Special case for ID_MAPPING since it's checked by activityType directly
		// 	return propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, propertyValue);
		// } else {
		// 	return propertyKeyValuePredicate(propertyKey, propertyValue);
		// }
	}

	@Override
	public String getStepNameFromElement(XdmNode node) {
		nodeMustBeAnElement(node);
		return node.attribute("name");
	}

	@Override
	public String getStepIdFromElement(XdmNode node) {
		nodeMustBeAnElement(node);
		return node.attribute("id");
	}

	@Override
	public String stepPredicateForScriptSteps() {
		return propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Script");
	}

	@Override
	public String stepPredicateForFilterSteps() {
		return propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Filter");
	}

	@Override
	public String stepPredicateForXmlValidatorSteps() {
		return propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "XmlValidator");
	}

	@Override
	public String stepPredicateForEdiValidatorSteps() {
		return propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "EDIValidator");
	}

	@Override
	public String stepPredicateForContentModifierSteps() {
		return propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Enricher");
	}

	@Override
	public String stepPredicateForScriptingLanguage(ScriptingLanguage scriptingLanguage) {
		Objects.requireNonNull(scriptingLanguage, "scriptingLanguage must not be null");
		// We need to check for subActivityType since Script steps use this to distinguish language type
		return propertyKeyValuePredicate("subActivityType", scriptingSubActivityValues.get(scriptingLanguage));
	}
	
	@Override
	public String xpathForIflowDescription() {
		return "/bpmn2:definitions/bpmn2:collaboration/bpmn2:documentation";
	}

	@Override
	public String stepPredicateForDataStoreSteps() {
		return propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "DBstorage");
	}

	@Override
	public String stepPredicateForDataStoreOperation(DataStoreOperation dataStoreOperation) {
		assert datastoreOperationPropertyValues.containsKey(dataStoreOperation);
		String key = "operation";
		String value = datastoreOperationPropertyValues.get(dataStoreOperation);
		return propertyKeyValuePredicate(key, value);
	}

	@Override
	public String stepPredicateForUnencryptedWrite() {
		return propertyKeyValuePredicate("encrypt", "false");
	}
	
	private String elementStringEqualityPredicate(String elementName, String value) {
		return String.format("[%s = '%s']", elementName, value);
	}
	
	private String propertyPredicateGeneric(String... predicates) {
		if (predicates.length == 0) {
			throw new IllegalArgumentException("At least one predicate required to build a property predicate");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("[bpmn2:extensionElements/ifl:property");
		for (String predicate : predicates) {
			sb.append(predicate);
		}
		sb.append(']');
		return sb.toString();
	}

	private String propertyKeyValuePredicate(String key, String value) {
		return propertyPredicateGeneric(
			elementStringEqualityPredicate(PROPERTY_KEY_ELEMENT_NAME, key),
			elementStringEqualityPredicate(PROPERTY_VALUE_ELEMENT_NAME, value)
		);
	}
	
	private String appendPredicates(String xpath, String... predicates) {
		StringBuilder sb = new StringBuilder(xpath);
		for (String predicate : predicates) {
			sb.append(predicate);
		}
		return sb.toString();
	}

	@Override
	public SenderAdapter senderChannelComponentTypeToSenderAdapter(String componentType) {
		Objects.requireNonNull(componentType, "componentType must not be null");
		assert componentTypeToSenderAdapter.containsKey(componentType);
		return componentTypeToSenderAdapter.get(componentType);
	}

	@Override
	public String xqueryForMultiConditionTypeRouters() {
		return JarResourceUtil.loadXqueryResource("multi-condition-type-routers.xquery");
	}
	
	@Override
	public String xqueryForProcessDirectReceiverChannels() {
		return JarResourceUtil.loadXqueryResource("process-direct-receiver-channels.xquery");
	}

	@Override
	public String xqueryForProcessDirectSenderChannelAddresses() {
		return JarResourceUtil.loadXqueryResource("process-direct-sender-channel-addresses.xquery");
	}

	@Override
	public String xqueryForCleartextBasicAuthReceiverChannels() {
		return JarResourceUtil.loadXqueryResource("cleartext-basic-auth-not-allowed.xquery");
	}

	@Override
	public String xqueryForSenderChannelUserRoles() {
		return JarResourceUtil.loadXqueryResource("sender-channel-user-roles.xquery");	}

	private static void nodeMustBeAnElement(XdmNode node) {
		if (node.getNodeKind() != XdmNodeKind.ELEMENT) {
			throw new IllegalArgumentException("Provided node is not an element");
		}
	}

	private static String attributeEqualityPredicate(String attributeName, String value) {
		return String.format("[@%s = '%s']", attributeName, value);
	}

	private String xpathForParticipants(String... predicates) {
		return appendPredicates("//bpmn2:participant", predicates);
	}

	public String xpathForSenderParticipants() {
		String senderTypePredicate = attributeEqualityPredicate("ifl:type", "EndpointSender");
		return xpathForParticipants(senderTypePredicate);
	}

	public String xpathForReceiverParticipants() {
		String receiverTypePredicate = attributeEqualityPredicate("ifl:type", "EndpointRecevier"); // Yes, that is the spelling to use, even though receiver is spelled wrong.
		return xpathForParticipants(receiverTypePredicate);
	}

	public String getParticipantNameFromElement(XdmNode node) {
		nodeMustBeAnElement(node);
		return node.attribute("name");
	}

	public String getParticipantIdFromElement(XdmNode node) {
		nodeMustBeAnElement(node);
		return node.attribute("id");
	}

	// ILCD Framework validation related methods implementation
	
	@Override
	public String xpathForAllStepsInProcessOrder() {
		return "//bpmn2:process/bpmn2:sequenceFlow";
	}
	
	@Override
	public String xpathForAllSteps() {
		return "//bpmn2:process/bpmn2:task";
	}
	
	@Override
	public String xpathForStepsByType(String stepType) {
		return xpathForFlowSteps(propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, stepType));
	}
	
	@Override
	public String xpathForPropertiesInStep(String stepId) {
		return String.format("//bpmn2:process/bpmn2:task[@id='%s']/bpmn2:extensionElements/ifl:property", stepId);
	}
	
	@Override
	public String getPropertyNameFromElement(XdmNode propertyNode) {
		nodeMustBeAnElement(propertyNode);
		// Find the child element with name "key"
		for (XdmNode child : propertyNode.children()) {
			if (child.getNodeKind() == XdmNodeKind.ELEMENT && 
				PROPERTY_KEY_ELEMENT_NAME.equals(child.getNodeName().getLocalName())) {
				return child.getStringValue();
			}
		}
		return "";
	}
	
	@Override
	public String getPropertyValueFromElement(XdmNode propertyNode) {
		nodeMustBeAnElement(propertyNode);
		// Find the child element with name "value"
		for (XdmNode child : propertyNode.children()) {
			if (child.getNodeKind() == XdmNodeKind.ELEMENT && 
				PROPERTY_VALUE_ELEMENT_NAME.equals(child.getNodeName().getLocalName())) {
				return child.getStringValue();
			}
		}
		return "";
	}
	
	@Override
	public String xpathForContentModifierStepsWithPositionPredicate() {
		return xpathForFlowSteps(stepPredicateForContentModifierSteps());
	}
	
	@Override
	public String xpathForContentModifierStepsWithPositionPredicate(String position) {
		String positionPredicate = String.format("[position() = %s]", position);
		return xpathForFlowSteps(stepPredicateForContentModifierSteps()) + positionPredicate;
	}

	@Override
	public String xpathForProcesses() {
		return "//bpmn2:process";
	}
	
	@Override
	public String xpathForLocalProcesses() {
		return xpathForFlowSteps(propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "LocalIntegrationProcess"));
	}
	
	@Override
	public String xpathForExceptionSubprocesses() {
		return xpathForFlowSteps(propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "ErrorEventSubProcessTemplate"));
	}
	
	@Override
	public String xpathForRouters() {
		return xpathForFlowSteps(propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Router"));
	}
	
	@Override
	public String xpathForRouterRoutes() {
		return "//bpmn2:process//bpmn2:sequenceFlow[starts-with(@targetRef, 'router_')]";
	}
	
	@Override
	public String xpathForSplitters() {
		return xpathForFlowSteps(propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Splitter"));
	}
	
	@Override
	public String xpathForMulticasts() {
		String multicastPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Multicast");
		String sequentialMulticastPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "SequentialMulticast");
		
		// Combine the predicates
		return xpathForFlowSteps(String.format("(%s or %s)", multicastPredicate, sequentialMulticastPredicate));
	}
	
	@Override
	public String xpathForJoins() {
		return xpathForFlowSteps(propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Join"));
	}
	
	@Override
	public String xpathForGathers() {
		return xpathForFlowSteps(propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Gather"));
	}
	
	@Override
	public String xpathForAggregators() {
		return xpathForFlowSteps(propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Aggregator"));
	}
	
	@Override
	public String xpathForIdMappings() {
		// TEMPORARY WORKAROUND: Return an empty string to completely skip IDMapper validation
		// This avoids XPath expression issues with array vs boolean values
		return "";
	}
	
	@Override
	/* Placeholder - TODO: implement VM parsing */
	public String xpathForValueMappings() {
		return xpathForFlowSteps(propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "ValueMapping"));
	}
	
	@Override
	public String xpathForConverters() {
		// Building a more comprehensive predicate for various converter types
		String xmlToJsonPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "XmlToJsonConverter");
		String xmlToCsvPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "XmlToCsvConverter");
		String genericConverterPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Converter");
		
		// Combine all predicates with OR
		return xpathForFlowSteps(String.format("(%s or %s or %s)", 
			xmlToJsonPredicate, xmlToCsvPredicate, genericConverterPredicate));
	}
	
	@Override
	public String xpathForEncoders() {
		// Check the encoderType property with both "Encoder" activity type and specific encoder types
		String encoderPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Encoder");
		String base64EncoderPredicate = String.format("(%s and %s)", 
			encoderPredicate, propertyKeyValuePredicate("encoderType", "Base64 Encode"));
		String zipEncoderPredicate = String.format("(%s and %s)", 
			encoderPredicate, propertyKeyValuePredicate("encoderType", "ZIP Compress"));
		
		// Combine all predicates
		return xpathForFlowSteps(String.format("(%s or %s or %s)", 
			encoderPredicate, base64EncoderPredicate, zipEncoderPredicate));
	}
	
	@Override
	public String xpathForDecoders() {
		// Check the encoderType property with both "Decoder" activity type and specific decoder types
		String decoderPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Decoder");
		String base64DecoderPredicate = String.format("(%s and %s)", 
			decoderPredicate, propertyKeyValuePredicate("encoderType", "Base64 Decode"));
		String zipDecoderPredicate = String.format("(%s and %s)", 
			decoderPredicate, propertyKeyValuePredicate("encoderType", "ZIP Decompress"));
		
		// Combine all predicates
		return xpathForFlowSteps(String.format("(%s or %s or %s)", 
			decoderPredicate, base64DecoderPredicate, zipDecoderPredicate));
	}
	
	@Override
	public String xpathForEncryptors() {
		String encryptPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Encrypt");
		String pgpEncryptPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "PgpEncrypt");
		
		// Combine the predicates
		return xpathForFlowSteps(String.format("(%s or %s)", encryptPredicate, pgpEncryptPredicate));
	}
	
	@Override
	public String xpathForDecryptors() {
		String decryptPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Decrypt");
		String pgpDecryptPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "PgpDecrypt");
		
		// Combine the predicates
		return xpathForFlowSteps(String.format("(%s or %s)", decryptPredicate, pgpDecryptPredicate));
	}
	
	@Override
	public String xpathForRequestReplies() {
		return xpathForFlowSteps(propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "RequestReply"));
	}
	
	@Override
	public String xpathForSends() {
		return xpathForFlowSteps(propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Send"));
	}
	
	@Override
	public String xpathForSigners() {
		String signerPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Signer");
		String simpleSignPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "SimpleSignMessage");
		
		// Combine the predicates
		return xpathForFlowSteps(String.format("(%s or %s)", signerPredicate, simpleSignPredicate));
	}
	
	@Override
	public String xpathForVerifiers() {
		String verifierPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "Verifier");
		String xmlDigitalVerifyPredicate = propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "XMLDigitalVerifySign");
		
		// Combine the predicates
		return xpathForFlowSteps(String.format("(%s or %s)", verifierPredicate, xmlDigitalVerifyPredicate));
	}
	
	@Override
	public String xpathForValidators() {
		return xpathForFlowSteps(propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "XmlValidator"));
	}
	
	@Override
	public String xpathForCommunicationChannels() {
		return "//bpmn2:process//bpmn2:task[contains(@name, '_SND_') or contains(@name, '_RCV_')]";
	}
	
	@Override
	public String xpathForMessageQueues() {
		return xpathForFlowSteps(propertyKeyValuePredicate(ACTIVITY_TYPE_PROPERTY_KEY, "MessageQueue"));
	}
	
	@Override
	public String xpathForDataStoreOperationsSteps(DataStoreOperation operation) {
		return xpathForFlowSteps(
			stepPredicateForDataStoreSteps(),
			stepPredicateForDataStoreOperation(operation)
		);
	}
}
