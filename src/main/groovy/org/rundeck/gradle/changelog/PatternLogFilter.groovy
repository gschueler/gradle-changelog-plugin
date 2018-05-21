package org.rundeck.gradle.changelog

import java.util.regex.Pattern

class PatternLogFilter implements LogFilter {
    Pattern pattern
    Closure converter

    PatternLogFilter(final Pattern pattern, final Closure converter) {
        this.pattern = pattern
        this.converter = converter
    }

    PatternLogFilter(final Pattern pattern) {
        this.pattern = pattern
    }

    @Override
    boolean include(final String entry) {
        entry ==~ pattern
    }

    @Override
    String transform(final String entry) {
        return converter == null ? entry : converter.call(entry)
    }
}
