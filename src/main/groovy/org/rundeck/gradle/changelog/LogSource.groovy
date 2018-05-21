package org.rundeck.gradle.changelog

/**
 * Return a list of changelog entries
 */
interface LogSource {
    List<String> getLogs()
}
