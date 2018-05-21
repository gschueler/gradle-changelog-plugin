package org.rundeck.gradle.changelog

import org.gradle.api.Project

class GitLogSource extends LogSourceContainer implements LogSource {
    ChangelogPluginExtension extension
    Project project

    String getPrevTag() {
        def util = extension.getUtil(project, null)
        VersionInfo info = util.getAxionVersion()
        info.lastTag
    }

    static List<LogFilter> defaultLogFilters() {
        [
                new PatternLogFilter(~/.*[fF]ix(e[sd])? #\d+.*/),
                new PatternLogFilter(~/^[fF]ix(e[sd])?(:|\s).*/),
                new PatternLogFilter(~/^[lL]og:?.*/, { it.replaceAll(/^[lL]og:\s+/, '') }),
        ]
    }

    @Override
    List<String> getLogs() {
        filterLogs(readGitLogs())
    }

    List<String> readGitLogs() {
        def diff = "${prevTag}..."
        def serr = new StringBuilder(), sout = new StringBuilder()
        def proc = ['git', 'log', '--no-merges', '--pretty=format:%s', diff].execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitForOrKill(1000)
        sout.toString().readLines()
    }
}
