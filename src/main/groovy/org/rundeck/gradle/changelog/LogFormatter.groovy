package org.rundeck.gradle.changelog

import com.github.zafarkhaja.semver.Version

interface LogFormatter {
    String formatLogs(List<String> logs)
}
