package org.rundeck.gradle.changelog

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import pl.allegro.tech.build.axion.release.domain.VersionConfig

public class UpdateChangelogTask extends DefaultTask {
    @Internal
    @Optional
    ChangelogPluginExtension extension

    @Internal
    @Optional
    VersionConfig versionConfig

    @TaskAction
    def updateChangelog(){
    	def util = extension.getUtil(project,versionConfig)
        VersionInfo info = util.getAxionVersion()
        String newtext = util.genChangelog(
                info.lastTag,
                extension.githubUrl,
                info.version.version,
                info.nextTag,
                info.version.previousVersion,
                extension.changelogFile,
                true
        )
        def changelogFile = extension.changelogFile ? project.file(extension.changelogFile) : null
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
        	println newtext
        	return
        }
        changelogFile.text = newtext
    }
}
