package org.cpilint.rules;

import org.cpilint.IflowXml;
import org.cpilint.artifacts.ArtifactResource;
import org.cpilint.artifacts.ArtifactResourceType;
import org.cpilint.artifacts.IflowArtifact;
import org.cpilint.artifacts.IflowArtifactTag;
import org.cpilint.issues.NamingConventionsRuleIssue;
import org.cpilint.model.XmlModel;
import org.cpilint.model.XmlModelFactory;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.cpilint.model.ChannelDirection;
import org.cpilint.model.DataStoreOperation;
import org.cpilint.model.MappingType;
import org.cpilint.model.Nameable;
import org.cpilint.model.ScriptingLanguage;
import org.cpilint.model.XmlModel;
import org.cpilint.model.XmlModelFactory;
import org.cpilint.model.SenderAdapter;
import org.cpilint.model.ReceiverAdapter;
import org.cpilint.rules.naming.NamingScheme;
import org.cpilint.rules.naming.NamingSchemeFactory;

final class NamingConventionsRule extends RuleBase {
	
	private static final Map<Nameable, Function<XmlModel, String>> nameableToXpathFunctionMap;
	private static final Map<Nameable, BiFunction<XdmNode, XmlModel, String>> nameableToNameFunctionMap;
	private static final Map<Nameable, BiFunction<XdmNode, XmlModel, String>> nameableToIdentFunctionMap;
	private static final Logger logger = LoggerFactory.getLogger(NamingConventionsRule.class);
	
	static {
		// Initialize the nameableToXpathFunctionMap map.
		nameableToXpathFunctionMap = new HashMap<>();
		nameableToXpathFunctionMap.put(Nameable.IFLOW_NAME, m -> "//bpmn2:definitions");
		nameableToXpathFunctionMap.put(Nameable.IFLOW_ID, m -> "//bpmn2:definitions");
		nameableToXpathFunctionMap.put(Nameable.CHANNEL_NAME, m -> m.xpathForChannels());
		nameableToXpathFunctionMap.put(Nameable.SENDER_CHANNEL_NAME, m -> m.xpathForChannels(m.channelPredicateForDirection(ChannelDirection.SENDER)));
		nameableToXpathFunctionMap.put(Nameable.ADVANCEDEVENTMESH_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.ADVANCEDEVENTMESH));
		nameableToXpathFunctionMap.put(Nameable.AMQP_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.AMQP));
		nameableToXpathFunctionMap.put(Nameable.ARIBA_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.ARIBA));
		nameableToXpathFunctionMap.put(Nameable.AS2_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.AS2));
		nameableToXpathFunctionMap.put(Nameable.AS4_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.AS4));
		nameableToXpathFunctionMap.put(Nameable.AZURESTORAGE_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.AZURESTORAGE));
		nameableToXpathFunctionMap.put(Nameable.DATA_STORE_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.DATASTORE));
		nameableToXpathFunctionMap.put(Nameable.DROPBOX_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.DROPBOX));
		nameableToXpathFunctionMap.put(Nameable.FTP_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.FTP));
		nameableToXpathFunctionMap.put(Nameable.HTTPS_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.HTTPS));
		nameableToXpathFunctionMap.put(Nameable.IDOC_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.IDOC));
		nameableToXpathFunctionMap.put(Nameable.JMS_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.JMS));
		nameableToXpathFunctionMap.put(Nameable.KAFKA_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.KAFKA));
		nameableToXpathFunctionMap.put(Nameable.MAIL_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.MAIL));
		nameableToXpathFunctionMap.put(Nameable.MICROSOFT_SHAREPOINT_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.MICROSOFTSHAREPOINT));
		nameableToXpathFunctionMap.put(Nameable.ODATA_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.ODATA));
		nameableToXpathFunctionMap.put(Nameable.PROCESSDIRECT_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.PROCESSDIRECT));
		nameableToXpathFunctionMap.put(Nameable.RABBITMQ_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.RABBITMQ));
		nameableToXpathFunctionMap.put(Nameable.SFTP_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.SFTP));
		nameableToXpathFunctionMap.put(Nameable.SLACK_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.SLACK));
		nameableToXpathFunctionMap.put(Nameable.SMB_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.SMB));
		nameableToXpathFunctionMap.put(Nameable.SOAP_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.SOAP));
		nameableToXpathFunctionMap.put(Nameable.SPLUNK_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.SPLUNK));
		nameableToXpathFunctionMap.put(Nameable.SUCCESSFACTORS_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.SUCCESSFACTORS));
		nameableToXpathFunctionMap.put(Nameable.XI_SENDER_CHANNEL_NAME, m -> m.xpathForSenderChannels(SenderAdapter.XI));
		nameableToXpathFunctionMap.put(Nameable.RECEIVER_CHANNEL_NAME, m -> m.xpathForChannels(m.channelPredicateForDirection(ChannelDirection.RECEIVER)));
		nameableToXpathFunctionMap.put(Nameable.ADVANCEDEVENTMESH_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.ADVANCEDEVENTMESH));
		nameableToXpathFunctionMap.put(Nameable.AMAZONDYNAMODB_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.AMAZONDYNAMODB));
		nameableToXpathFunctionMap.put(Nameable.AMAZONEVENTBRIDGE_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.AMAZONEVENTBRIDGE));
		nameableToXpathFunctionMap.put(Nameable.AMQP_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.AMQP));
		nameableToXpathFunctionMap.put(Nameable.ANAPLAN_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.ANAPLAN));
		nameableToXpathFunctionMap.put(Nameable.ARIBA_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.ARIBA));
		nameableToXpathFunctionMap.put(Nameable.AS2_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.AS2));
		nameableToXpathFunctionMap.put(Nameable.AS4_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.AS4));
		nameableToXpathFunctionMap.put(Nameable.AZURESTORAGE_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.AZURESTORAGE));
		nameableToXpathFunctionMap.put(Nameable.COUPA_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.COUPA));
		nameableToXpathFunctionMap.put(Nameable.DROPBOX_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.DROPBOX));
		nameableToXpathFunctionMap.put(Nameable.ELSTER_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.ELSTER));
		nameableToXpathFunctionMap.put(Nameable.FACEBOOK_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.FACEBOOK));
		nameableToXpathFunctionMap.put(Nameable.FTP_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.FTP));
		nameableToXpathFunctionMap.put(Nameable.ODATA_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.ODATA));
		nameableToXpathFunctionMap.put(Nameable.HTTP_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.HTTP));
		nameableToXpathFunctionMap.put(Nameable.HUBSPOT_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.HUBSPOT));
		nameableToXpathFunctionMap.put(Nameable.IDOC_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.IDOC));
		nameableToXpathFunctionMap.put(Nameable.JDBC_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.JDBC));
		nameableToXpathFunctionMap.put(Nameable.JIRA_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.JIRA));
		nameableToXpathFunctionMap.put(Nameable.JMS_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.JMS));
		nameableToXpathFunctionMap.put(Nameable.KAFKA_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.KAFKA));
		nameableToXpathFunctionMap.put(Nameable.LDAP_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.LDAP));
		nameableToXpathFunctionMap.put(Nameable.MAIL_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.MAIL));
		nameableToXpathFunctionMap.put(Nameable.MDI_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.MDI));
		nameableToXpathFunctionMap.put(Nameable.MICROSOFT_SHAREPOINT_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.MICROSOFTSHAREPOINT));
		nameableToXpathFunctionMap.put(Nameable.NETSUITE_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.NETSUITE));
		nameableToXpathFunctionMap.put(Nameable.ODC_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.ODC));
		nameableToXpathFunctionMap.put(Nameable.OPENCONNECTORS_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.OPENCONNECTORS));
		nameableToXpathFunctionMap.put(Nameable.PROCESSDIRECT_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.PROCESSDIRECT));
		nameableToXpathFunctionMap.put(Nameable.RABBITMQ_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.RABBITMQ));
		nameableToXpathFunctionMap.put(Nameable.RFC_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.RFC));
		nameableToXpathFunctionMap.put(Nameable.SERVICENOW_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.SERVICENOW));
		nameableToXpathFunctionMap.put(Nameable.SFTP_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.SFTP));
		nameableToXpathFunctionMap.put(Nameable.SLACK_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.SLACK));
		nameableToXpathFunctionMap.put(Nameable.SMB_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.SMB));
		nameableToXpathFunctionMap.put(Nameable.SNOWFLAKE_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.SNOWFLAKE));
		nameableToXpathFunctionMap.put(Nameable.SOAP_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.SOAP));
		nameableToXpathFunctionMap.put(Nameable.SPLUNK_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.SPLUNK));
		nameableToXpathFunctionMap.put(Nameable.SUCCESSFACTORS_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.SUCCESSFACTORS));
		nameableToXpathFunctionMap.put(Nameable.SUGARCRM_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.SUGARCRM));
		nameableToXpathFunctionMap.put(Nameable.TWITTER_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.TWITTER));
		nameableToXpathFunctionMap.put(Nameable.WORKDAY_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.WORKDAY));
		nameableToXpathFunctionMap.put(Nameable.XI_RECEIVER_CHANNEL_NAME, m -> m.xpathForReceiverChannels(ReceiverAdapter.XI));
		nameableToXpathFunctionMap.put(Nameable.MAPPING_STEP_NAME, m -> m.xpathForFlowSteps(m.stepPredicateForMappingSteps()));
		nameableToXpathFunctionMap.put(Nameable.MESSAGE_MAPPING_STEP_NAME, m -> m.xpathForMappingSteps(MappingType.MESSAGE_MAPPING));
		nameableToXpathFunctionMap.put(Nameable.XSLT_MAPPING_STEP_NAME, m -> m.xpathForMappingSteps(MappingType.XSLT_MAPPING));
		nameableToXpathFunctionMap.put(Nameable.OPERATION_MAPPING_STEP_NAME, m -> m.xpathForMappingSteps(MappingType.OPERATION_MAPPING));
		nameableToXpathFunctionMap.put(Nameable.SCRIPT_STEP_NAME, m -> m.xpathForFlowSteps(m.stepPredicateForScriptSteps()));
		nameableToXpathFunctionMap.put(Nameable.GROOVY_SCRIPT_STEP_NAME, m -> m.xpathForScriptSteps(ScriptingLanguage.GROOVY));
		nameableToXpathFunctionMap.put(Nameable.JS_SCRIPT_STEP_NAME, m -> m.xpathForScriptSteps(ScriptingLanguage.JAVASCRIPT));
		nameableToXpathFunctionMap.put(Nameable.SENDER_NAME, m -> m.xpathForSenderParticipants());
		nameableToXpathFunctionMap.put(Nameable.RECEIVER_NAME, m -> m.xpathForReceiverParticipants());
		nameableToXpathFunctionMap.put(Nameable.CONTENT_MODIFIER_STEP_NAME, m -> m.xpathForFlowSteps(m.stepPredicateForContentModifierSteps()));
		nameableToXpathFunctionMap.put(Nameable.FILTER_STEP_NAME, m -> m.xpathForFlowSteps(m.stepPredicateForFilterSteps()));
		nameableToXpathFunctionMap.put(Nameable.XML_VALIDATOR_STEP_NAME, m -> m.xpathForFlowSteps(m.stepPredicateForXmlValidatorSteps()));
		nameableToXpathFunctionMap.put(Nameable.EDI_VALIDATOR_STEP_NAME, m -> m.xpathForFlowSteps(m.stepPredicateForEdiValidatorSteps()));
		nameableToXpathFunctionMap.put(Nameable.DATA_STORE_OPERATIONS_STEP_NAME, m -> m.xpathForFlowSteps(m.stepPredicateForDataStoreSteps()));
		nameableToXpathFunctionMap.put(Nameable.GET_DATA_STORE_OPERATIONS_STEP_NAME, m -> m.xpathForDataStoreOperationsSteps(DataStoreOperation.GET));
		nameableToXpathFunctionMap.put(Nameable.SELECT_DATA_STORE_OPERATIONS_STEP_NAME, m -> m.xpathForDataStoreOperationsSteps(DataStoreOperation.SELECT));
		nameableToXpathFunctionMap.put(Nameable.DELETE_DATA_STORE_OPERATIONS_STEP_NAME, m -> m.xpathForDataStoreOperationsSteps(DataStoreOperation.DELETE));
		nameableToXpathFunctionMap.put(Nameable.WRITE_DATA_STORE_OPERATIONS_STEP_NAME, m -> m.xpathForDataStoreOperationsSteps(DataStoreOperation.WRITE));
		nameableToXpathFunctionMap.put(Nameable.PROCESS_NAME, m -> m.xpathForProcesses());
		nameableToXpathFunctionMap.put(Nameable.LOCAL_PROCESS_NAME, m -> m.xpathForLocalProcesses());
		nameableToXpathFunctionMap.put(Nameable.EXCEPTION_SUBPROCESS_NAME, m -> m.xpathForExceptionSubprocesses());
		nameableToXpathFunctionMap.put(Nameable.ROUTER_STEP_NAME, m -> m.xpathForRouters());
		nameableToXpathFunctionMap.put(Nameable.ROUTER_ROUTE_STEP_NAME, m -> m.xpathForRouterRoutes());
		nameableToXpathFunctionMap.put(Nameable.SPLITTER_STEP_NAME, m -> m.xpathForSplitters());
		nameableToXpathFunctionMap.put(Nameable.MULTICAST_STEP_NAME, m -> m.xpathForMulticasts());
		nameableToXpathFunctionMap.put(Nameable.JOIN_STEP_NAME, m -> m.xpathForJoins());
		nameableToXpathFunctionMap.put(Nameable.GATHER_STEP_NAME, m -> m.xpathForGathers());
		nameableToXpathFunctionMap.put(Nameable.AGGREGATOR_STEP_NAME, m -> m.xpathForAggregators());
		nameableToXpathFunctionMap.put(Nameable.ID_MAPPING_STEP_NAME, m -> m.xpathForIdMappings());
		nameableToXpathFunctionMap.put(Nameable.VALUE_MAPPING_STEP_NAME, m -> m.xpathForValueMappings());
		nameableToXpathFunctionMap.put(Nameable.CONVERTER_STEP_NAME, m -> m.xpathForConverters());
		nameableToXpathFunctionMap.put(Nameable.ENCODER_STEP_NAME, m -> m.xpathForEncoders());
		nameableToXpathFunctionMap.put(Nameable.DECODER_STEP_NAME, m -> m.xpathForDecoders());
		nameableToXpathFunctionMap.put(Nameable.REQUEST_REPLY_STEP_NAME, m -> m.xpathForRequestReplies());
		nameableToXpathFunctionMap.put(Nameable.SEND_STEP_NAME, m -> m.xpathForSends());
		nameableToXpathFunctionMap.put(Nameable.ENCRYPTOR_STEP_NAME, m -> m.xpathForEncryptors());
		nameableToXpathFunctionMap.put(Nameable.DECRYPTOR_STEP_NAME, m -> m.xpathForDecryptors());
		nameableToXpathFunctionMap.put(Nameable.SIGNER_STEP_NAME, m -> m.xpathForSigners());
		nameableToXpathFunctionMap.put(Nameable.VERIFIER_STEP_NAME, m -> m.xpathForVerifiers());
		nameableToXpathFunctionMap.put(Nameable.VALIDATOR_STEP_NAME, m -> m.xpathForValidators());
		nameableToXpathFunctionMap.put(Nameable.COMMUNICATION_CHANNEL_NAME, m -> m.xpathForCommunicationChannels());
		nameableToXpathFunctionMap.put(Nameable.MESSAGE_QUEUE_NAME, m -> m.xpathForMessageQueues());
		// Initialize the nameableToNameFunctionMap map.
		nameableToNameFunctionMap = new HashMap<>();
		nameableToNameFunctionMap.put(Nameable.IFLOW_NAME, (n, m) -> {
			// Get the name from the attribute only - we'll handle the special case in inspect()
			return n.attribute("name");
		});
		nameableToNameFunctionMap.put(Nameable.IFLOW_ID, (n, m) -> n.attribute("id"));
		nameableToNameFunctionMap.put(Nameable.CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.ADVANCEDEVENTMESH_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.AMQP_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.ARIBA_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.AS2_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.AS4_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.AZURESTORAGE_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.DATA_STORE_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.DROPBOX_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.FTP_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.HTTPS_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.IDOC_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.JMS_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.KAFKA_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.MAIL_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.MICROSOFT_SHAREPOINT_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.ODATA_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.PROCESSDIRECT_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.RABBITMQ_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SFTP_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SLACK_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SMB_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SOAP_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SPLUNK_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SUCCESSFACTORS_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.XI_SENDER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.ADVANCEDEVENTMESH_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.AMAZONDYNAMODB_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.AMAZONEVENTBRIDGE_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.AMQP_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.ANAPLAN_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.ARIBA_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.AS2_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.AS4_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.AZURESTORAGE_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.COUPA_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.DROPBOX_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.ELSTER_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.FACEBOOK_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.FTP_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.ODATA_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.HTTP_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.HUBSPOT_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.IDOC_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.JDBC_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.JIRA_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.JMS_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.KAFKA_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.LDAP_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.MAIL_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.MDI_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.MICROSOFT_SHAREPOINT_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.NETSUITE_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.ODC_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.OPENCONNECTORS_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.PROCESSDIRECT_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.RABBITMQ_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.RFC_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SERVICENOW_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SFTP_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SLACK_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SMB_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SNOWFLAKE_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SOAP_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SPLUNK_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SUCCESSFACTORS_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SUGARCRM_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.TWITTER_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.WORKDAY_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.XI_RECEIVER_CHANNEL_NAME, (n, m) -> m.getChannelNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.MAPPING_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.MESSAGE_MAPPING_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.XSLT_MAPPING_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.OPERATION_MAPPING_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SCRIPT_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.GROOVY_SCRIPT_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.JS_SCRIPT_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SENDER_NAME, (n, m) -> m.getParticipantNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.RECEIVER_NAME, (n, m) -> m.getParticipantNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.CONTENT_MODIFIER_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.FILTER_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.XML_VALIDATOR_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.EDI_VALIDATOR_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.DATA_STORE_OPERATIONS_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.GET_DATA_STORE_OPERATIONS_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SELECT_DATA_STORE_OPERATIONS_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.DELETE_DATA_STORE_OPERATIONS_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.WRITE_DATA_STORE_OPERATIONS_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.PROCESS_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.LOCAL_PROCESS_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.EXCEPTION_SUBPROCESS_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.ROUTER_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.ROUTER_ROUTE_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SPLITTER_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.MULTICAST_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.JOIN_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.GATHER_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.AGGREGATOR_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.ID_MAPPING_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.VALUE_MAPPING_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.CONVERTER_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.ENCODER_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.DECODER_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.REQUEST_REPLY_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SEND_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.ENCRYPTOR_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.DECRYPTOR_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.SIGNER_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.VERIFIER_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.VALIDATOR_STEP_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.COMMUNICATION_CHANNEL_NAME, (n, m) -> m.getStepNameFromElement(n));
		nameableToNameFunctionMap.put(Nameable.MESSAGE_QUEUE_NAME, (n, m) -> m.getStepNameFromElement(n));
		// Initialize the nameableToIdentFunctionMap map.
		nameableToIdentFunctionMap = new HashMap<>();
		nameableToIdentFunctionMap.put(Nameable.IFLOW_NAME, (n, m) -> String.format("iFlow with name '%s'", n.attribute("name")));
		nameableToIdentFunctionMap.put(Nameable.IFLOW_ID, (n, m) -> String.format("iFlow with ID '%s'", n.attribute("id")));
		nameableToIdentFunctionMap.put(Nameable.CHANNEL_NAME, (n, m) -> String.format("channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SENDER_CHANNEL_NAME, (n, m) -> String.format("sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.ADVANCEDEVENTMESH_SENDER_CHANNEL_NAME, (n, m) -> String.format("AdvancedEventMesh sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.AMQP_SENDER_CHANNEL_NAME, (n, m) -> String.format("AMQP sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.ARIBA_SENDER_CHANNEL_NAME, (n, m) -> String.format("Ariba sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.AS2_SENDER_CHANNEL_NAME, (n, m) -> String.format("AS2 sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.AS4_SENDER_CHANNEL_NAME, (n, m) -> String.format("AS4 sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.AZURESTORAGE_SENDER_CHANNEL_NAME, (n, m) -> String.format("AzureStorage sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.DATA_STORE_SENDER_CHANNEL_NAME, (n, m) -> String.format("Data Store sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.DROPBOX_SENDER_CHANNEL_NAME, (n, m) -> String.format("Dropbox sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.FTP_SENDER_CHANNEL_NAME, (n, m) -> String.format("FTP sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.HTTPS_SENDER_CHANNEL_NAME, (n, m) -> String.format("HTTPS sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.IDOC_SENDER_CHANNEL_NAME, (n, m) -> String.format("IDoc sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.JMS_SENDER_CHANNEL_NAME, (n, m) -> String.format("JMS sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.KAFKA_SENDER_CHANNEL_NAME, (n, m) -> String.format("Kafka sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.MAIL_SENDER_CHANNEL_NAME, (n, m) -> String.format("Mail sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.MICROSOFT_SHAREPOINT_SENDER_CHANNEL_NAME, (n, m) -> String.format("Microsoft SharePoint sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.ODATA_SENDER_CHANNEL_NAME, (n, m) -> String.format("OData sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.PROCESSDIRECT_SENDER_CHANNEL_NAME, (n, m) -> String.format("ProcessDirect sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.RABBITMQ_SENDER_CHANNEL_NAME, (n, m) -> String.format("RabbitMQ sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SFTP_SENDER_CHANNEL_NAME, (n, m) -> String.format("SFTP sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SLACK_SENDER_CHANNEL_NAME, (n, m) -> String.format("Slack sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SMB_SENDER_CHANNEL_NAME, (n, m) -> String.format("SMB sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SOAP_SENDER_CHANNEL_NAME, (n, m) -> String.format("SOAP sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SPLUNK_SENDER_CHANNEL_NAME, (n, m) -> String.format("Splunk sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SUCCESSFACTORS_SENDER_CHANNEL_NAME, (n, m) -> String.format("SuccessFactors sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.XI_SENDER_CHANNEL_NAME, (n, m) -> String.format("XI sender channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.RECEIVER_CHANNEL_NAME, (n, m) -> String.format("receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.ADVANCEDEVENTMESH_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("AdvancedEventMesh receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.AMAZONDYNAMODB_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("AmazonDynamoDB receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.AMAZONEVENTBRIDGE_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("AmazonEventBridge receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.AMQP_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("AMQP receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.ANAPLAN_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Anaplan receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.ARIBA_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Ariba receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.AS2_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("AS2 receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.AS4_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("AS4 receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.AZURESTORAGE_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("AzureStorage receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.COUPA_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Coupa receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.DROPBOX_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Dropbox receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.ELSTER_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Elster receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.FACEBOOK_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Facebook receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.FTP_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("FTP receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.ODATA_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("OData receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.HTTP_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("HTTP receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.HUBSPOT_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("HubSpot receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.IDOC_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("IDoc receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.JDBC_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("JDBC receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.JIRA_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Jira receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.JMS_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("JMS receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.KAFKA_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Kafka receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.LDAP_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("LDAP receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.MAIL_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Mail receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.MDI_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("MDI receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.MICROSOFT_SHAREPOINT_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Microsoft SharePoint receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.NETSUITE_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("NetSuite receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.ODC_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("ODC receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.OPENCONNECTORS_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("OpenConnectors receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.PROCESSDIRECT_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("ProcessDirect receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.RABBITMQ_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("RabbitMQ receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.RFC_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("RFC receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SERVICENOW_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("ServiceNow receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SFTP_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("SFTP receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SLACK_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Slack receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SMB_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("SMB receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SNOWFLAKE_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Snowflake receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SOAP_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("SOAP receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SPLUNK_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Splunk receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SUCCESSFACTORS_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("SuccessFactors receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SUGARCRM_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("SugarCRM receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.TWITTER_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Twitter receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.WORKDAY_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("Workday receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.XI_RECEIVER_CHANNEL_NAME, (n, m) -> String.format("XI receiver channel '%s' (ID '%s')", m.getChannelNameFromElement(n), m.getChannelIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.MAPPING_STEP_NAME, (n, m) -> String.format("mapping step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.MESSAGE_MAPPING_STEP_NAME, (n, m) -> String.format("message mapping step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.XSLT_MAPPING_STEP_NAME, (n, m) -> String.format("XSLT mapping step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.OPERATION_MAPPING_STEP_NAME, (n, m) -> String.format("operation mapping step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SCRIPT_STEP_NAME, (n, m) -> String.format("script step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.GROOVY_SCRIPT_STEP_NAME, (n, m) -> String.format("Groovy script step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.JS_SCRIPT_STEP_NAME, (n, m) -> String.format("JavaScript script step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SENDER_NAME, (n, m) -> String.format("Sender participant '%s' (ID '%s')", m.getParticipantNameFromElement(n), m.getParticipantIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.RECEIVER_NAME, (n, m) -> String.format("Receiver participant '%s' (ID '%s')", m.getParticipantNameFromElement(n), m.getParticipantIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.CONTENT_MODIFIER_STEP_NAME, (n, m) -> String.format("content modifier step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.FILTER_STEP_NAME, (n, m) -> String.format("filter step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.XML_VALIDATOR_STEP_NAME, (n, m) -> String.format("XML Validator step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.EDI_VALIDATOR_STEP_NAME, (n, m) -> String.format("EDI Validator step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.DATA_STORE_OPERATIONS_STEP_NAME, (n, m) -> String.format("Data Store Operations step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.GET_DATA_STORE_OPERATIONS_STEP_NAME, (n, m) -> String.format("Get Data Store Operations step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SELECT_DATA_STORE_OPERATIONS_STEP_NAME, (n, m) -> String.format("Select Data Store Operations step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.DELETE_DATA_STORE_OPERATIONS_STEP_NAME, (n, m) -> String.format("Delete Data Store Operations step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.WRITE_DATA_STORE_OPERATIONS_STEP_NAME, (n, m) -> String.format("Write Data Store Operations step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.PROCESS_NAME, (n, m) -> String.format("Process '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.LOCAL_PROCESS_NAME, (n, m) -> String.format("Local Process '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.EXCEPTION_SUBPROCESS_NAME, (n, m) -> String.format("Exception Subprocess '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.ROUTER_STEP_NAME, (n, m) -> String.format("Router step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.ROUTER_ROUTE_STEP_NAME, (n, m) -> String.format("Router Route step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SPLITTER_STEP_NAME, (n, m) -> String.format("Splitter step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.MULTICAST_STEP_NAME, (n, m) -> String.format("Multicast step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.JOIN_STEP_NAME, (n, m) -> String.format("Join step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.GATHER_STEP_NAME, (n, m) -> String.format("Gather step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.AGGREGATOR_STEP_NAME, (n, m) -> String.format("Aggregator step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.ID_MAPPING_STEP_NAME, (n, m) -> String.format("ID Mapping step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.VALUE_MAPPING_STEP_NAME, (n, m) -> String.format("Value Mapping step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.CONVERTER_STEP_NAME, (n, m) -> String.format("Converter step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.ENCODER_STEP_NAME, (n, m) -> String.format("Encoder step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.DECODER_STEP_NAME, (n, m) -> String.format("Decoder step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.REQUEST_REPLY_STEP_NAME, (n, m) -> String.format("Request-Reply step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SEND_STEP_NAME, (n, m) -> String.format("Send step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.ENCRYPTOR_STEP_NAME, (n, m) -> String.format("Encryptor step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.DECRYPTOR_STEP_NAME, (n, m) -> String.format("Decryptor step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.SIGNER_STEP_NAME, (n, m) -> String.format("Signer step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.VERIFIER_STEP_NAME, (n, m) -> String.format("Verifier step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.VALIDATOR_STEP_NAME, (n, m) -> String.format("Validator step '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.COMMUNICATION_CHANNEL_NAME, (n, m) -> String.format("Communication Channel '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		nameableToIdentFunctionMap.put(Nameable.MESSAGE_QUEUE_NAME, (n, m) -> String.format("Message Queue '%s' (ID '%s')", m.getStepNameFromElement(n), m.getStepIdFromElement(n)));
		// The keys of the above maps should be identical.
		assert nameableToXpathFunctionMap.keySet().equals(nameableToNameFunctionMap.keySet());
		assert nameableToNameFunctionMap.keySet().equals(nameableToIdentFunctionMap.keySet());
	}
	
	private final NamingScheme scheme;
	private final String message;
	private final Set<Nameable> applyTo;
	
	NamingConventionsRule(NamingScheme scheme, String message, Set<Nameable> applyTo) {
		this.scheme = Objects.requireNonNull(scheme, "scheme must not be null");
		Objects.requireNonNull(message, "message must not be null");
		if (message.isBlank()) {
			throw new IllegalArgumentException("message must not be blank");
		}
		this.message = message;
		/*
		 *  The applyTo set can neither be null nor empty, and it cannot contain
		 *  a null element.
		 */
		Objects.requireNonNull(applyTo, "applyTo must not be null");
		if (applyTo.isEmpty()) {
			throw new IllegalArgumentException("applyTo must not be empty");
		}
		if (applyTo.contains(null)) {
			throw new IllegalArgumentException("applyTo must not contain null");
		}
		this.applyTo = new HashSet<>(applyTo);
	}

	@Override
	public void inspect(IflowArtifact iflow) {
		IflowXml iflowXml = iflow.getIflowXml();
		XmlModel model = XmlModelFactory.getModelFor(iflowXml);
		IflowArtifactTag tag = iflow.getTag();
		
		// Create a copy of applyTo to use locally
		Set<Nameable> localApplyTo = new HashSet<>(applyTo);
		
		// Special handling for IFLOW_NAME
		if (localApplyTo.contains(Nameable.IFLOW_NAME)) {
			// Get the name directly from the tag which we know has it
			String name = tag.getName();
			if (name != null && !scheme.test(name)) {
				String ruleIdStr = getId().isPresent() ? getId().get() : "";
				consumer.consume(new NamingConventionsRuleIssue(
					ruleIdStr, 
					tag, 
					String.format("The iFlow with name '%s' does not follow the naming scheme: %s", name, message),
					name,
					getSeverity()
				));
			}
			// Remove from localApplyTo so we don't process it again below
			localApplyTo.remove(Nameable.IFLOW_NAME);
			
			// If that was the only nameable we were checking, exit early
			if (localApplyTo.isEmpty()) {
				return;
			}
		}
		
		// Get source and target from metainfo.properties
		// These properties aren't used for naming conventions but might be used by other rules
		Properties props = new Properties();
		for (ArtifactResource resource : iflow.getResourcesByType(ArtifactResourceType.METAINFO)) {
			try {
				props.load(resource.getContents());
			} catch (IOException e) {
				logger.warn("Error reading metainfo.properties: {}", e.getMessage());
			}
			break;
		}
		
		// Check each nameable type that's specified in the apply-to elements
		for (Nameable n : localApplyTo) {
			// Skip if the map doesn't contain this nameable type
			if (!nameableToXpathFunctionMap.containsKey(n) || 
				!nameableToNameFunctionMap.containsKey(n) || 
				!nameableToIdentFunctionMap.containsKey(n)) {
				logger.debug("Skipping nameable type {} as it's not configured in the maps", n);
				continue;
			}
			
			// Get component names for the specified nameable type
			XdmValue names = iflowXml.evaluateXpath(nameableToXpathFunctionMap.get(n).apply(model));
			for (XdmItem item : names) {
				XdmNode node = (XdmNode)item;
				String name = nameableToNameFunctionMap.get(n).apply(node, model);
				String ident = nameableToIdentFunctionMap.get(n).apply(node, model);
				
				// Skip null names - can happen if the component exists but has no name attribute
				if (name == null) {
					logger.debug("Skipping nameable type {} as its name is null, identifier: {}", n, ident);
					continue;
				}
				
				// Test the name against the scheme pattern defined in the XML
				if (!scheme.test(name)) {
					String ruleIdStr = getId().isPresent() ? getId().get() : "";
					consumer.consume(new NamingConventionsRuleIssue(
						ruleIdStr, 
						tag, 
						errorMessage(ident),
						name,
						getSeverity()
					));
				}
			}
		}
	}
	
	private String errorMessage(String ident) {
		return String.format("The %s does not follow the naming scheme: %s", ident, message);
	}

}
