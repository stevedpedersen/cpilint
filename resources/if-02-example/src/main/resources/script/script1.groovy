import com.sap.gateway.ip.core.customdev.util.Message
import src.main.resources.script.Logger

Message processData(Message message) {
    Logger logger = Logger.newLogger(message, messageLogFactory)

    logger.debug("----- Getting message headers -----")
    message.getHeaders().each { String key, Object value ->
        if (value instanceof InputStream) {
            def reader = message.getHeader(key, Reader)
            logger.debug("${key}|${value.getClass().getName()}|${reader.getText()}")
        } else {
            logger.debug("${key}|${value.getClass().getName()}|${value}")
        }
    }

    logger.debug("----- Getting message properties -----")
    message.getProperties().each { String key, Object value ->
        if (value instanceof InputStream) {
            logger.debug("${key}|${value.getClass().getName()}|{{Value for InputStream skipped}}")
        } else {
            logger.debug("${key}|${value.getClass().getName()}|${value}")
        }
    }

    logger.debug("----- Getting message body -----")
    logger.debug("Body object type = ${message.getBody().getClass().getName()}")
    def reader = message.getBody(Reader)
    if (reader) {
        logger.debug("Body value = ${reader.getText()}")
    } else {
        logger.debug("Body value = ${message.getBody()}")
    }
    
    def ex = message.getProperty("CamelExceptionCaught")
    if (ex != null) {
        try {
            logger.error("----- Getting Error body -----")
            logger.error("Exception type = ${ex.getClass().getCanonicalName()}")
            logger.error("Exception message = ${ex.getMessage()}")
            logger.error("Status code = ${ex.getStatusCode()}")
            def exBody = ex.getResponseBody()
            if (exBody) {
                logger.error("Err Body value = ${exBody}")
            } else {
                logger.error("Body value = ${message.getBody()}")
            }     
        } catch (Exception e) {
            logger.error("Error parsing exception - ${e.message}")
        }
    }

    logger.saveEntriesInAttachment()
    return additional(message)
}

def Message additional(Message message) {
    
    try {
        def additionalTrackingObjects = ["traceid", "traceparent", "requestid" ]
    
        def messageLog = messageLogFactory.getMessageLog(message);
    
        def iterationElementsLists = [message.getProperties(), message.getHeaders()]
                                     
    
        if(messageLog != null) {
            iterationElementsLists.each { elements ->
                                          elements.each { key, value ->
    	        if (key.toLowerCase().startsWith("ch_")) 
    					{
    		        println("$key - $value")
    	          messageLog.addCustomHeaderProperty(key.substring(3), value);
    	        }
    	
    	        if(additionalTrackingObjects.contains(key.toLowerCase().replaceAll("_","").replaceAll("-",""))) 
    					{
    		        println("$key - $value")
    	          messageLog.addCustomHeaderProperty(key, value);
    	        }
    	      }
          }
        }
    } catch (Exception e) {
        def messageLog = messageLogFactory.getMessageLog(message);
        messageLog.addAttachmentAsString("Additional Exception", "${e.message}\n${e.stackTrace}", "text/plain")
    } finally {
        return message
    }
    return message
}