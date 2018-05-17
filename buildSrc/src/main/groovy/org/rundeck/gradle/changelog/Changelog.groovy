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

    void xprintChangelog() {
        System.out.printf("%s, %s!\n", extension.changelogFile, extension.githubUrl); 
    }

	def getAxionVersion() {
        VersionConfig config = GradleAwareContext.configOrCreateFromProject(project, versionConfig)
		def ctx = GradleAwareContext.create(project, config)
	    def rules = ctx.rules
	    println "scmPosition: $config.scmPosition"
	    def curVersion=config.versionService.currentVersion(
	            rules.version,
	            rules.tag,
	            rules.nextVersion
	    )
	    println "curVersion: $curVersion"
	    return curVersion
	}

    /**
	 * Generate partial or full changelog
	 * @param prevTag
	 * @param baseUrl
	 * @param curVersion
	 * @param prevVersion
	 * @param changelog
	 * @param full
	 * @return
	 */
	def genChangelog(prevTag, baseUrl, curVersion, prevVersion, File changelog, full = false) {
	    def diff = "${prevTag}..."
	    def serr = new StringBuilder(), sout = new StringBuilder()
	    def proc = ['git', 'log', '--no-merges','--pretty=format:%s', diff].execute()
	    proc.consumeProcessOutput(sout, serr)
	    proc.waitForOrKill(1000)
	    def logs = sout.toString().readLines()
	    def include = [~/.*[fF]ix(e[sd])? #\d+.*/, ~/^[fF]ix(e[sd])?(:|\s).*/, ~/^[lL]og:?.*/]

	    def unrel = changelog ? unreleasedLog(prevVersion, changelog) ?: [] : []

	    unrel.addAll(logs.findAll { t -> t && include.any { t ==~ it } })
	    logs = (baseUrl?
	    		unrel.collect { '* ' + it.replaceAll(/^[lL]og:\s+/,'').replaceAll(/(#(\d+))/, "[Issue \$1]($baseUrl/issues/\$2)") }
	            :unrel).join('\n')
	    logs = logs ? logs + '\n' : ''
	    if (full && changelog) {
	        def text = """## ${curVersion}

	$logs
	[Changes]($baseUrl/compare/${diff}v${curVersion})

	"""
	        return changelog.text.replaceAll(
	                ~/(?s)^(## unreleased(.*))?(## ${prevVersion}.*)$/,
	                Matcher.quoteReplacement(text) + '$3'
	        )
	    }
	    return logs
	}

    @TaskAction
    def printChangelog(){
    	 def version = getAxionVersion()
        println genChangelog(
                version.position.revision,
                extension.githubUrl,
                version.version,
                version.previousVersion,
                extension.changelogFile,
                project.hasProperty("changelogFull")
        )

    }
}