package org.rundeck.gradle.changelog

interface LogFilter {
    boolean include(String entry)

    String transform(String entry)
}
