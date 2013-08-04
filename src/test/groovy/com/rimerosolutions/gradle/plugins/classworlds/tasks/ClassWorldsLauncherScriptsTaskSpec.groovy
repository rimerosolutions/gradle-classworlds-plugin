/*
 * Copyright 2013 Rimero Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rimerosolutions.gradle.plugins.classworlds.tasks

import java.io.File;

import org.gradle.api.Project
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskValidationException
import org.gradle.testfixtures.ProjectBuilder
import com.rimerosolutions.gradle.plugins.classworlds.ClassWorldsPluginConstants
import spock.lang.Specification

/**
 * Spock test for the <code>classWorldsAssemblies</code> task.
 * 
 * @author Yves Zoundi
 */
public class ClassWorldsLauncherScriptsTaskSpec extends ClassWorldsTaskSpec {
        
        def 'When inputs are missing an exception should be thrown'() {
                given: 'The task is configured with no inputs'
                def task = project.task(ClassWorldsLauncherScriptsTask.TASK_NAME, type: ClassWorldsLauncherScriptsTask)

                when: 'The task is executed'
                task.execute()

                then: 'An TaskValidationException error should be thrown'
                thrown(TaskValidationException)
        }


        def 'With inputs configured, the task should run fine'() {
                given: 'The configuration dir is set'
                def stagingDirectory = project.extensions[ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME].stagingDir
                project.ext[ClassWorldsCopyBootstrapJarTask.BOOTSTRAP_JAR_FILENAME_PROPERTY] = 'boot.jar'
                
                File binDirectory = new File("$stagingDirectory/${ClassWorldsPluginConstants.AssemblyDirNames.BIN}")
                binDirectory.mkdirs()

                when: 'The task is registered'
                project.tasks.create(name:ClassWorldsLauncherScriptsTask.TASK_NAME, type:ClassWorldsLauncherScriptsTask)

                and: 'The task is configured'
                project.tasks[ClassWorldsLauncherScriptsTask.TASK_NAME].with {
                        scriptsDir = binDirectory
                        appMainClassName = project.extensions[ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME].appMainClassName
                        appHome  = project.extensions[ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME].appHome
                        jvmOptions = '-Xms32m -Xmx32m'
                }

                then: 'When the task is executed'
                project.tasks[ClassWorldsLauncherScriptsTask.TASK_NAME].execute() 
                
                expect: 'The classworlds configuration file should be present'                
                new File("$binDirectory/${ClassWorldsPluginConstants.ScriptConstants.LAUNCHER_TEMPLATES_FILENAMES_MAPPING[0]}").exists()
                new File("$binDirectory/${ClassWorldsPluginConstants.ScriptConstants.LAUNCHER_TEMPLATES_FILENAMES_MAPPING[1]}").exists()
        }
}