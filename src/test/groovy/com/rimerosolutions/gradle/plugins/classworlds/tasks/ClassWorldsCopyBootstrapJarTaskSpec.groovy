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
class ClassWorldsCopyBootstrapJarTaskSpec extends ClassWorldsTaskSpec {

        @Override
        Class getTaskClass() {
                MockableClassWorldsCopyBootstrapJarTask
        }

        @Override
        String getTaskName() {
                BOOTSTRAP_TASK_NAME
        }
        
        static final String BOOTSTRAP_TASK_NAME = 'bootJarTask'
        
        static class MockableClassWorldsCopyBootstrapJarTask extends ClassWorldsCopyBootstrapJarTask {
                File bootJar
                
                protected File findBootstrapJarArtifact() {
                         bootJar
                }
        }        

        def 'With a stagingDir configured, the task should run fine'() {
                setup: 'With basic settings'
                def stagingDirectory = project.extensions[ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME].stagingDir
                File bootDirectory = new File("$stagingDirectory/${ClassWorldsPluginConstants.AssemblyDirNames.BOOT}")
                
                and: 'The boot dir is set along with the boot jar'
                bootDirectory.mkdirs()
                File bootJarFile = new File("$bootDirectory", 'boot.jar')
                bootJarFile.createNewFile()
                                
                when: 'The task is registered'
                project.tasks.create(name:BOOTSTRAP_TASK_NAME, type:MockableClassWorldsCopyBootstrapJarTask)

                and: 'The task is configured'
                project.tasks[BOOTSTRAP_TASK_NAME].with {
                        bootDir = bootDirectory
                        bootJar = bootJarFile
                } 

                then: 'When the task is executed'
                project.tasks[BOOTSTRAP_TASK_NAME].execute() 
                
                expect: 'The classworlds jar file should be in the boot folder'
                new File("$bootDirectory/${project.ext[ClassWorldsCopyBootstrapJarTask.BOOTSTRAP_JAR_FILENAME_PROPERTY]}").exists()
        }
}