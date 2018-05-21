package org.rundeck.gradle.changelog

/**
 * Default includes all entries and doesn't modify
 */
class DefaultLogFilter implements LogFilter {
    @Override
    boolean include(final String entry) {
        true
    }

    @Override
    String transform(final String entry) {
        entry
    }
}
