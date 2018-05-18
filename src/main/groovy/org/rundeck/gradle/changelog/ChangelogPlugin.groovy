package org.rundeck.gradle.changelog;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ChangelogPlugin implements Plugin<Project> {
    public void apply(Project project) {
    	// Add the 'greeting' extension object
        def extension = project.extensions.create('changelog', ChangelogPluginExtension, project)

        project.tasks.create("changelog", ChangelogTask) { task ->
        	task.extension=extension
        	
        }
        project.tasks.create("updateChangelog", UpdateChangelogTask){task->
        	task.extension=extension
        	
        }
    }
    


}
