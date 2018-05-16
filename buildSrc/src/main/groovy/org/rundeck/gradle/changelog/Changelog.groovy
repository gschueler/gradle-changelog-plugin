package org.rundeck.gradle.changelog;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import java.io.File;

public class Changelog extends DefaultTask {
    def changelogFile;
    String githubUrl;

    @TaskAction
    void printChangelog() {
        System.out.printf("%s, %s!\n", changelogFile, githubUrl); 
    }
}