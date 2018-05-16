package org.rundeck.gradle.changelog;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

class ChangelogPluginExtension{
	def changelogFile
    String githubUrl
    ChangelogPluginExtension(Project project){
    	
    	changelogFile=project.file("CHANGELOG.md")
    }
}