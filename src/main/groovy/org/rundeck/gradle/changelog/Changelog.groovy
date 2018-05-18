package org.rundeck.gradle.changelog;

import pl.allegro.tech.build.axion.release.domain.VersionConfig
import pl.allegro.tech.build.axion.release.infrastructure.di.GradleAwareContext
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction;
import java.io.File;

public class Changelog extends DefaultTask {

    @Optional
    ChangelogPluginExtension extension

    @Optional
    VersionConfig versionConfig

	
    @TaskAction
    def printChangelog(){
    	def util = extension.getUtil(project,versionConfig)
    	 def (version,curTag,prevTag) = util.getAxionVersion()
        println util.genChangelog(
                prevTag,
                extension.githubUrl,
                version.version,
                curTag,
                version.previousVersion,
                extension.changelogFile,
                project.hasProperty("changelogFull")
        )

    }
}