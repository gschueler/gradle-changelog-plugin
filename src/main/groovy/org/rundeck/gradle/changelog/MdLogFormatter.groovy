package org.rundeck.gradle.changelog


class MdLogFormatter implements LogFormatter {
    @Override
    String formatLogs(final List<String> logs) {
        logs.collect { "* $it" }.join('\n')
    }
}
