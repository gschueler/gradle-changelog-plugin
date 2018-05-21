package org.rundeck.gradle.changelog

class LogSourceContainer implements LogSource {
    LogSource source = null
    List<LogFilter> filters = []

    @Override
    List<String> getLogs() {
        filterLogs(source?.logs ?: [])
    }

    List<String> filterLogs(final List<String> logs) {
        logs.collect { String log ->

            for (LogFilter filter : filters) {
                if (log == null) {
                    break
                }
                log = filter.include(log) ? filter.transform(log) : null
            }
            log
        }.findAll { it != null }
    }
}
