package org.cpilint.model;

import net.sf.saxon.s9api.XdmNode;

public interface XmlModel {
	
	// Channel related.
	
	public String xpathForChannels(String... predicates);
	
	public String channelPredicateForDirection(ChannelDirection direction);
	
	public String channelPredicateForAdapter(ReceiverAdapter receiverAdapter);
	
	public String channelPredicateForAdapter(SenderAdapter senderAdapter);
	
	public String channelPredicateForHttpEndpoints(ReceiverAdapter receiverAdapter);
	
	public String channelPredicateForNoCsrfProtection();
	
	public String channelPredicateForBasicAuthentication(ReceiverAdapter receiverAdapter);
	
	public String channelPredicateForClientCertAuth(SenderAdapter senderAdapter);
	
	public String channelPredicateForProxyTypeOnPremise(ReceiverAdapter receiverAdapter);
	
	public String getChannelNameFromElement(XdmNode node);
	
	public String getChannelIdFromElement(XdmNode node);
	
	public default String xpathForSenderChannels(SenderAdapter senderAdapter, String... predicates) {
		/*
		 * Create a new array containing the predicates for direction and
		 * adapter type. Note that the order of the predicates doesn't
		 * matter.
		 */
		String[] newPredicates = new String[predicates.length + 2];
		newPredicates[0] = channelPredicateForDirection(ChannelDirection.SENDER);
		newPredicates[1] = channelPredicateForAdapter(senderAdapter);
		System.arraycopy(predicates, 0, newPredicates, 2, predicates.length);
		return xpathForChannels(newPredicates);
	}

	public default String xpathForReceiverChannels(ReceiverAdapter receiverAdapter, String... predicates) {
		/*
		 * Create a new array containing the predicates for direction and
		 * adapter type. Note that the order of the predicates doesn't
		 * matter.
		 */
		String[] newPredicates = new String[predicates.length + 2];
		newPredicates[0] = channelPredicateForDirection(ChannelDirection.RECEIVER);
		newPredicates[1] = channelPredicateForAdapter(receiverAdapter);
		System.arraycopy(predicates, 0, newPredicates, 2, predicates.length);
		return xpathForChannels(newPredicates);
	}

	public SenderAdapter senderChannelComponentTypeToSenderAdapter(String componentType);
	
	public String xqueryForProcessDirectReceiverChannels();
	
	public String xqueryForProcessDirectSenderChannelAddresses();
	
	public String xqueryForCleartextBasicAuthReceiverChannels();

	public String xqueryForSenderChannelUserRoles();
	
	// Flow step related.
	
	public String xpathForFlowSteps(String... predicates);
	
	public String stepPredicateForMappingSteps();
	
	public String stepPredicateForMappingType(MappingType mappingType);
	
	public String getStepNameFromElement(XdmNode node);
	
	public String getStepIdFromElement(XdmNode node);
	
	public default String xpathForMappingSteps(MappingType mappingType) {
		return xpathForFlowSteps(
			stepPredicateForMappingSteps(),
			stepPredicateForMappingType(mappingType)
		);
	}
	
	public String stepPredicateForScriptSteps();

	public String stepPredicateForFilterSteps();

	public String stepPredicateForXmlValidatorSteps();

	public String stepPredicateForEdiValidatorSteps();

	public String stepPredicateForContentModifierSteps();
	
	public String stepPredicateForScriptingLanguage(ScriptingLanguage scriptingLanguage);
	
	public default String xpathForScriptSteps(ScriptingLanguage scriptingLanguage) {
		return xpathForFlowSteps(
			stepPredicateForScriptSteps(),
			stepPredicateForScriptingLanguage(scriptingLanguage)
		);
	}
	
	public String stepPredicateForDataStoreSteps();
	
	public String stepPredicateForDataStoreOperation(DataStoreOperation dataStoreOperation);
	
	public String stepPredicateForUnencryptedWrite();
	
	public String xqueryForMultiConditionTypeRouters();
	
	/**
	 * Gets XPath expression to return all steps in process order.
	 * @return XPath expression string
	 */
	public String xpathForAllStepsInProcessOrder();
	
	/**
	 * Gets XPath expression to return all steps regardless of order.
	 * @return XPath expression string
	 */
	public String xpathForAllSteps();
	
	/**
	 * Gets XPath expression to find steps of a specific type.
	 * @param stepType The type of the step to find
	 * @return XPath expression string
	 */
	public String xpathForStepsByType(String stepType);
	
	/**
	 * Gets XPath expression to find properties in a specific step.
	 * @param stepId The ID of the step to search properties in
	 * @return XPath expression string
	 */
	public String xpathForPropertiesInStep(String stepId);
	
	/**
	 * Gets the property name from a property element node.
	 * @param propertyNode The property node
	 * @return The property name
	 */
	public String getPropertyNameFromElement(XdmNode propertyNode);
	
	/**
	 * Gets the property value from a property element node.
	 * @param propertyNode The property node
	 * @return The property value
	 */
	public String getPropertyValueFromElement(XdmNode propertyNode);
	
	// ContentModifierPositionRule related methods
	
	/**
	 * Gets XPath expression to find Content Modifier steps with position.
	 * @return XPath expression string
	 */
	public String xpathForContentModifierStepsWithPositionPredicate();
	
	/**
	 * Gets XPath expression to find Content Modifier steps in a specific position.
	 * @param position The position of the Content Modifier step
	 * @return XPath expression string
	 */
	public String xpathForContentModifierStepsWithPositionPredicate(String position);
	
	// Participant related.

	public String xpathForSenderParticipants();

	public String xpathForReceiverParticipants();

	public String getParticipantNameFromElement(XdmNode node);

	public String getParticipantIdFromElement(XdmNode node);

	// Other iflow content.
	
	public String xpathForIflowDescription();

	public String xpathForProcesses();

	public String xpathForLocalProcesses();

	public String xpathForExceptionSubprocesses();
	
	public String xpathForRouters();
	
	public String xpathForRouterRoutes();
	
	public String xpathForSplitters();
	
	public String xpathForMulticasts();
	
	public String xpathForJoins();
	
	public String xpathForGathers();
	
	public String xpathForAggregators();
	
	public String xpathForIdMappings();
	
	/* Placeholder for when adding VMs */
	public String xpathForValueMappings();
	
	public String xpathForConverters();
	
	public String xpathForEncoders();
	
	public String xpathForDecoders();
	
	public String xpathForRequestReplies();
	
	public String xpathForSends();
	
	public String xpathForEncryptors();
	
	public String xpathForDecryptors();
	
	public String xpathForSigners();

	public String xpathForVerifiers();

	public String xpathForValidators();

	/* Probably redundant */
	public String xpathForCommunicationChannels();

	/* Placeholder to check for references to queues */
	public String xpathForMessageQueues();
	
	/**
	 * Gets XPath expression to find Data Store Operations elements by operation type.
	 * @param operation The type of data store operation (GET, SELECT, DELETE, WRITE)
	 * @return XPath expression string
	 */
	public String xpathForDataStoreOperationsSteps(DataStoreOperation operation);

}
