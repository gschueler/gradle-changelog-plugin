package org.rundeck.gradle.changelog

import com.github.zafarkhaja.semver.Version

interface ChangelogFormatter {
    String formatChangelog(Version version, Date date, List<String> logs)
}
