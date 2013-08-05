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
 * Spock test for the <code>classWorldsLaunchersCfgTask</code> task.
 * 
 * @author Yves Zoundi
 */
class ClassWorldsLauncherCfgTaskSpec extends ClassWorldsTaskSpec {
        
        @Override
        Class getTaskClass() {
                ClassWorldsLauncherCfgTask
        }

        @Override
        String getTaskName() {
                ClassWorldsLauncherCfgTask.TASK_NAME
        }

        def 'With all inputs configured, the task should run fine'() {
                given: 'The configuration dir is set'
                def stagingDirectory = project.extensions[ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME].stagingDir
                
                File cfgDirectory = new File("$stagingDirectory/${ClassWorldsPluginConstants.AssemblyDirNames.ETC}")
                cfgDirectory.mkdirs()

                when: 'The task is registered'
                project.tasks.create(name:ClassWorldsLauncherCfgTask.TASK_NAME, type:ClassWorldsLauncherCfgTask)

                and: 'The task is configured'
                project.tasks[ClassWorldsLauncherCfgTask.TASK_NAME].with {
                        cfgDir = cfgDirectory
                        appMainClassName = project.extensions[ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME].appMainClassName
                        allArtifacts = project.files(cfgDirectory)
                }

                then: 'When the task is executed'
                project.tasks[ClassWorldsLauncherCfgTask.TASK_NAME].execute() 
                
                expect: 'The classworlds configuration file should be present in the etc folder'
                new File("$cfgDirectory/${ClassWorldsLauncherCfgTask.CONFIGURATION_FILENAME}").exists()
        }
}