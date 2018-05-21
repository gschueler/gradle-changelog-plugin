package org.rundeck.gradle.changelog

import org.gradle.api.Project
import pl.allegro.tech.build.axion.release.domain.VersionConfig

import javax.inject.Inject
import java.util.regex.Pattern

class ChangelogPluginExtension {
    def changelogFile
    String githubUrl
    boolean create = true
    boolean gitSource = true
    boolean unreleasedSource = true
    Map<String, LogSourceContainer> logSources = [:]
    Project project

    ChangelogFormatter changelogFormatter

    @Inject
    ChangelogPluginExtension(Project project) {
        this.project = project
        changelogFile = "CHANGELOG.md"
        changelogFormatter = new MdChangelogFormatter()

    }

    ChangelogUtil getUtil(Project project, VersionConfig versionConfig) {
        new ChangelogUtil(project: project, versionConfig: versionConfig)
    }

    def logFilter(String name, String pattern, Closure converter = null) {
        if (!logSources[name]) {
            throw new IllegalArgumentException("Source is not defined with name $name")
        }
        logSources[name].filters << new PatternLogFilter(Pattern.compile(pattern), converter)
    }

    def logSource(String name, Closure source) {
        logSources[name] = new LogSourceContainer(source: source as LogSource)
    }

    def logSource(String name) {
        if (name == 'git' && this.gitSource) {
            def gitsource = new GitLogSource(
                    project: project,
                    extension: this,
                    filters: GitLogSource.defaultLogFilters()
            )
            logSources.git = gitsource
            this.gitSource = false
        }
        if (name == 'unreleased' && this.unreleasedSource) {
            logSources.unreleased = new LogSourceContainer(
                    source: new UnreleasedLogSource(project, this)
            )
            this.unreleasedSource = false
        }
    }

    def formatter(ChangelogFormatter changelogFormatter1) {
        this.changelogFormatter = changelogFormatter1
    }

    def formatter(Closure closure) {
        changelogFormatter.with(closure)
    }

    def formatter(ChangelogFormatter changelogFormatter1, Closure closure) {
        formatter(changelogFormatter1)
        formatter(closure)
    }


    List<LogSource> buildSources(Project project) {
        logSource('git')
        logSource('unreleased')
        return new ArrayList<LogSource>(logSources.values())
    }
}
