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
class ClassWorldsInitStagingDirTaskSpec extends ClassWorldsTaskSpec {
        
        @Override
        Class getTaskClass() {
                ClassWorldsInitStagingDirTask
        }

        @Override
        String getTaskName() {
                ClassWorldsInitStagingDirTask.TASK_NAME
        }

        def 'With a stagingDir configured, the task should run fine'() {
                given: 'The staging dir is set'
                def stagingDir = project.extensions[ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME].stagingDir

                when: 'The task is registered'
                project.tasks.create(name:ClassWorldsInitStagingDirTask.TASK_NAME, type:ClassWorldsInitStagingDirTask)

                and: 'The task is configured'
                project.tasks[ClassWorldsInitStagingDirTask.TASK_NAME].stagingDir = stagingDir

                then: 'When the task is executed'
                project.tasks[ClassWorldsInitStagingDirTask.TASK_NAME].execute()

                expect: 'All the default staging directories should be created'
                new File("$stagingDir/${ClassWorldsPluginConstants.AssemblyDirNames.BOOT}").exists()
                new File("$stagingDir/${ClassWorldsPluginConstants.AssemblyDirNames.LIB}").exists()
                new File("$stagingDir/${ClassWorldsPluginConstants.AssemblyDirNames.BIN}").exists()
                new File("$stagingDir/${ClassWorldsPluginConstants.AssemblyDirNames.ETC}").exists()
        }
}
