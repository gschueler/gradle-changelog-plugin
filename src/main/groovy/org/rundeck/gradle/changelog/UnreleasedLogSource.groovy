package org.rundeck.gradle.changelog

import com.github.zafarkhaja.semver.Version
import org.gradle.api.Project

class UnreleasedLogSource implements LogSource {
    Project project
    ChangelogPluginExtension extension

    UnreleasedLogSource(final Project project, final ChangelogPluginExtension extension) {
        this.project = project
        this.extension = extension
    }

    /**
     * Unreleased changes list from the changelog file
     * @param prevVersion
     * @param changelog
     * @return
     */
    List<String> unreleasedLogs(Version prevVersion, File changelog) {
        def m = (changelog&&changelog.isFile())? changelog?.text =~ ~/(?si)^(## unreleased(.*))(## ${prevVersion}.*)?$/:null
        if (m?.find()) {
            return m.group(2)?.split(/\n/).findAll { it }.collect { it.replaceAll(/^[\*-]\s*/, '') }
        }
        []
    }
    @Override
    List<String> getLogs() {
        def util = extension.getUtil(project, null)
        VersionInfo info = util.getAxionVersion()

        unreleasedLogs(info.version.previousVersion, project.file(extension.changelogFile))
    }
}
