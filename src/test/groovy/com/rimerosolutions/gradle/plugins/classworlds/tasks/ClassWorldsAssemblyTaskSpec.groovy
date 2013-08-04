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
 * Spock test for the <code>classworldsInitStaginDir</code> task.
 * 
 * @author Yves Zoundi
 */
class ClassWorldsAssemblyTaskSpec extends ClassWorldsTaskSpec {

        def 'With a stagingDir configured, the task should run fine'() {
                given: 'The staging dir is set'
                File stagingDirectory = project.extensions[ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME].stagingDir
                stagingDirectory.mkdirs()

                when: 'The task is registered'
                project.tasks.create(name:ClassWorldsAssemblyTask.TASK_NAME, type:ClassWorldsAssemblyTask)

                and: 'The task is configured'
                String assemblyName = 'assembly'
                project.tasks[ClassWorldsAssemblyTask.TASK_NAME].with {
                        stagingDir = stagingDirectory
                        assemblyFormats = [ClassWorldsPluginConstants.AssemblyFormats.ZIP]
                        assemblyFileName = assemblyName
                }

                then: 'When the task is executed'
                project.tasks[ClassWorldsAssemblyTask.TASK_NAME].execute()

                expect: 'The archive zip should be there'
                new File("${project.buildDir}/${assemblyName}${ClassWorldsPluginConstants.Archives.EXTENSION_ZIP}").exists()
        }

        @Override
        Class getTaskClass() {
                ClassWorldsAssemblyTask
        }

        @Override
        String getTaskName() {
                ClassWorldsAssemblyTask.TASK_NAME
        }
}
