package org.rundeck.gradle.changelog

import pl.allegro.tech.build.axion.release.domain.VersionConfig
import pl.allegro.tech.build.axion.release.infrastructure.di.GradleAwareContext
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction;
import java.io.File;


class ChangelogUtil{
	Project project
	VersionConfig versionConfig

	def getAxionVersion() {
        VersionConfig config = GradleAwareContext.configOrCreateFromProject(project, versionConfig)
		def ctx = GradleAwareContext.create(project, config)
	    def rules = ctx.rules
	    // println "scmPosition: $config.scmPosition"
	    def curVersion=config.versionService.currentVersion(
	            rules.version,
	            rules.tag,
	            rules.nextVersion
	    )
	    // println "curVersion: $curVersion"
	    
	    String tagName = rules.tag.serialize(rules.tag, curVersion.previousVersion.toString())
	    String newTagName = rules.tag.serialize(rules.tag, curVersion.version.toString())
	    // println "prevTag: $tagName"
	    return [curVersion,newTagName,tagName]
	}

	/**
	 * Unreleased changes list from the changelog file
	 * @param prevVersion
	 * @param changelog
	 * @return
	 */
	def unreleasedLog(prevVersion, changelog) {
	    def m = (changelog&&changelog.isFile())? changelog?.text =~ ~/(?si)^(## unreleased(.*))(## ${prevVersion}.*)?$/:null
	    if (m?.find()) {
	        return m.group(2)?.split(/\n/).findAll { it }.collect { it.replaceAll(/^[\*-]\s*/, '') }
	    }
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
	def genChangelog(prevTag, baseUrl, curVersion, curTag, prevVersion, File changelog, full = false) {
	    def diff = "${prevTag}..."
	    def serr = new StringBuilder(), sout = new StringBuilder()
	    def proc = ['git', 'log', '--no-merges','--pretty=format:%s', diff].execute()
	    proc.consumeProcessOutput(sout, serr)
	    proc.waitForOrKill(1000)
	    def logs = sout.toString().readLines()
	    // println "logs: $logs"
	    def include = [~/.*[fF]ix(e[sd])? #\d+.*/, ~/^[fF]ix(e[sd])?(:|\s).*/, ~/^[lL]og:?.*/]

	    def unrel = changelog ? unreleasedLog(prevVersion, changelog) ?: [] : []

	    unrel.addAll(logs.findAll { t -> t && include.any { t ==~ it } })
	    logs = unrel.collect { it.replaceAll(/^[lL]og:\s+/,'') }
	    if(baseUrl){
	    	logs = logs.collect{it.replaceAll(/(#(\d+))/, "[Issue \$1]($baseUrl/issues/\$2)")}
	    }

        logs = logs.collect{"* $it"}.join('\n')
	    logs = logs ? logs + '\n' : ''
	    if (full && changelog) {
	        def text = """## ${curVersion}

$logs
"""
			if(baseUrl){
				text+="""
[Changes]($baseUrl/compare/${diff}v${curVersion})
"""
			}
			if(changelog.exists()){
		        return changelog.text.replaceAll(
		                ~/(?s)^(## unreleased(.*))?(## ${prevVersion}.*)$/,
		                Matcher.quoteReplacement(text) + '$3'
		        )
	        }else{
	        	return text
	        }
	    }
	    // println "logs: ${logs?.size()}"
	    return logs
	}
}