package org.rundeck.gradle.changelog

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import pl.allegro.tech.build.axion.release.domain.VersionConfig

public class ChangelogTask extends DefaultTask {

    @Internal
    @Optional
    ChangelogPluginExtension extension

    @Internal
    @Optional
    VersionConfig versionConfig


//    @TaskAction
    def printChangelog() {
        def util = extension.getUtil(project, versionConfig)
        VersionInfo info = util.getAxionVersion()
        println util.genChangelog(
                info.lastTag,
                extension.githubUrl,
                info.version.version,
                info.nextTag,
                info.version.previousVersion,
                extension.changelogFile,
                project.hasProperty("changelogFull")
        )

    }

    @TaskAction
    def printChangelog2() {
        def util = extension.getUtil(project, versionConfig)
        VersionInfo info = util.getAxionVersion()
        List<LogSource> sources = extension.buildSources(project)
        println extension.changelogFormatter.formatChangelog(
                info.version.version,
                new Date(),
                sources.collect { it.logs }.flatten()
        )
    }
}
