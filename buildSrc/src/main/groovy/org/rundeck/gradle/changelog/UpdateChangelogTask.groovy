package org.rundeck.gradle.changelog;

import pl.allegro.tech.build.axion.release.domain.VersionConfig
import pl.allegro.tech.build.axion.release.infrastructure.di.GradleAwareContext
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction;
import java.io.File;

public class UpdateChangelogTask extends DefaultTask {
    @Optional
    ChangelogPluginExtension extension

    @Optional
    VersionConfig versionConfig

    @TaskAction
    def updateChangelog(){
    	def util = extension.getUtil(project,versionConfig)
		def (version,curTag,prevTag) = util.getAxionVersion()
        def newtext= util.genChangelog(
                prevTag,
                extension.githubUrl,
                version.version,
                curTag,
                version.previousVersion,
                extension.changelogFile,
                true
        )
        def changelogFile=extension.changelogFile
        if(!changelogFile){
        	// println "File is not set: changelogFile"
        	throw new Exception("File is not set: changelogFile")
        }
        if(!changelogFile.exists() && !extension.create){
        	throw new Exception("File does not exist, and create is false: $changelogFile")	
        }
        boolean dryRun = project.hasProperty 'release.dryRun'

        if(dryRun){
        	println "DRYRUN: update changlog file: $changelogFile"
        	println "--------------------------------------------"
        	println text
        	return
        }
        changelogFile.text = newtext
    }
}