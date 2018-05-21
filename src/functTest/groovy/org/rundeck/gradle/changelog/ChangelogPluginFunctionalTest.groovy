package org.rundeck.gradle.changelog

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ChangelogPluginFunctionalTest extends Specification {
    @Rule TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    File changelogFile

    def setup() {
        buildFile = testProjectDir.newFile("build.gradle")
        buildFile << """
    plugins {
        id 'pl.allegro.tech.build.axion-release' version '1.9.0'
        id 'org.rundeck.gradle.changelog'
    }
"""
        changelogFile = testProjectDir.newFile("CHANGELOG.md")
        changelogFile << """## unreleased

* one entry
* another entry
"""
        testProjectDir.root
        "git init ${testProjectDir.root}".execute()
    }

    def "can successfully get unreleased logs"() {
        buildFile << """
            changelog {
            }
        """

        when:
        def result = GradleRunner.create()
                                 .withProjectDir(testProjectDir.root)
                                 .withDebug(true)
                                 .withArguments('changelog')
                                 .withPluginClasspath()
                                 .build()

        then:
        result.output.contains("* one entry")
        result.output.contains("* another entry")
        result.task(":changelog").outcome == TaskOutcome.SUCCESS
    }
}
