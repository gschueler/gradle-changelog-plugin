package org.rundeck.gradle.changelog

import org.gradle.api.Project
import pl.allegro.tech.build.axion.release.domain.VersionConfig

class ChangelogPluginExtension{
	def changelogFile
    String githubUrl
    boolean create=true
    
    ChangelogPluginExtension(Project project){
        changelogFile = "CHANGELOG.md"
    }
    ChangelogUtil getUtil(Project project, VersionConfig versionConfig){
    	new ChangelogUtil(project:project,versionConfig:versionConfig)
    }
}
