package src.main.resources.script

import com.sap.gateway.ip.core.customdev.util.Message

class Logger {
    final List entries
    final String logLevel
    final Object messageLog
    final Object mpl

    static Logger newLogger(Message message, Object messageLogFactory) {
        return new Logger(message, messageLogFactory)
    }

    private Logger() {}

    private Logger(Message message, Object messageLogFactory) {
        this.entries = []
        def mplConfig = message.getProperty('SAP_MessageProcessingLogConfiguration')
        this.logLevel = (mplConfig?.getLogLevel() ?: 'INFO') as String
        this.messageLog = messageLogFactory?.getMessageLog(message)
        this.mpl = message.getProperty('SAP_MessageProcessingLog')
    }

    void info(String entry) {
        this.entries.add("[INFO] " + entry)
    }

    void warn(String entry) {
        this.entries.add("[WARNING] " + entry)
    }

    void error(String entry) {
        this.entries.add("[ERROR] " + entry)
    }

    void debug(String entry) {
        if (this.logLevel == 'DEBUG' || this.logLevel == 'TRACE') {
            this.entries.add("[DEBUG] " + entry)
        }
    }

    void saveEntriesInAttachment() {
        Set mplKeys = this.mpl.getContainedKeys()
        def stepId = this.mpl.get(mplKeys.find { it.getName() == 'StepId' })
        saveEntriesInAttachment(stepId)
    }

    void saveEntriesInAttachment(String attachmentName) {
        if (this.messageLog) {
            if (this.entries.size()) {
                StringBuilder sb = new StringBuilder()
                this.entries.each { sb << it + "\r\n" }
                this.messageLog.addAttachmentAsString(attachmentName, sb.toString(), 'text/plain')
            }
        }
    }
}