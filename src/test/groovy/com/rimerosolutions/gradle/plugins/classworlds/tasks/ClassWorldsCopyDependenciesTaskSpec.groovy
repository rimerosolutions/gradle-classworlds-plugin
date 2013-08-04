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

import com.rimerosolutions.gradle.plugins.classworlds.ClassWorldsPluginConstants

/**
 * Spock test for the <code>classWorldsAssemblies</code> task.
 * 
 * @author Yves Zoundi
 */
class ClassWorldsCopyDependenciesTaskSpec extends ClassWorldsTaskSpec {
        
        @Override
        Class getTaskClass() {
                ClassWorldsCopyDependenciesTask
        }

        @Override
        String getTaskName() {
                ClassWorldsCopyDependenciesTask.TASK_NAME
        }

        def 'With a stagingDir configured, the task should run fine'() {
                setup: 'The lib dir is set'                
                def stagingDirectory = project.extensions[ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME].stagingDir
                File libDirectory = new File("$stagingDirectory/${ClassWorldsPluginConstants.AssemblyDirNames.LIB}")
                libDirectory.mkdirs()
                
                when: 'A dummy dependency is created'                
                File libJar = new File("$stagingDirectory", 'lib.jar')
                libJar.createNewFile()  

                and: 'The task is registered'
                project.tasks.create(name:ClassWorldsCopyDependenciesTask.TASK_NAME, type:ClassWorldsCopyDependenciesTask)

                and: 'The task is configured'
                project.tasks[ClassWorldsCopyDependenciesTask.TASK_NAME].with {
                        libDir = libDirectory
                        allArtifacts = project.files(libJar)
                }

                then: 'When the task is executed'
                project.tasks[ClassWorldsCopyDependenciesTask.TASK_NAME].execute() 
                
                expect: 'The classworlds bootstrap dependency should end up inside the lib directory'
                new File("$libDirectory/${libJar.name}").exists()
        }
}