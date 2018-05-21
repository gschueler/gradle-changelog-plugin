package org.rundeck.gradle.changelog

import com.github.zafarkhaja.semver.Version

class MdChangelogFormatter implements ChangelogFormatter {
    LogFormatter logFormatter = new MdLogFormatter()

    @Override
    String formatChangelog(final Version version, final Date date, final List<String> logs) {
        """## $version

Date: $date

""" + logFormatter.formatLogs(logs)
    }
}
