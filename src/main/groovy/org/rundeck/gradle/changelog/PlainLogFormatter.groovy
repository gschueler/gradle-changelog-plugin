package org.rundeck.gradle.changelog


class PlainLogFormatter implements LogFormatter {
    @Override
    String formatLogs(final List<String> logs) {
        logs.join("\n")
    }
}
