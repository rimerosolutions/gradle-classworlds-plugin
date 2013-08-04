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
package com.rimerosolutions.gradle.plugins.classworlds.tasks;

import org.gradle.api.Project
import org.gradle.api.tasks.TaskValidationException
import org.gradle.testfixtures.ProjectBuilder

import com.rimerosolutions.gradle.plugins.classworlds.ClassWorldsPluginConstants

import spock.lang.Specification

/**
 * Spock test for the <code>classworldsInitStaginDir</code> task.
 * 
 * @author Yves Zoundi
 */
public class ClassWorldsCleanupTaskSpec extends ClassWorldsTaskSpec {

        def 'When inputs are missing an exception should be thrown'() {
                given: 'The task is configured with no inputs'
                def task = project.task(ClassWorldsCleanupTask.TASK_NAME, type: ClassWorldsCleanupTask)

                when: 'The task is executed'
                task.execute()

                then: 'An TaskValidationException error should be thrown'
                thrown(TaskValidationException)
        }

        def 'With a stagingDir configured, the task should run fine'() {
                given: 'The staging dir is set'
                File stagingDir = project.extensions[ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME].stagingDir
                stagingDir.mkdirs()

                when: 'The task is registered'
                project.tasks.create(name:ClassWorldsCleanupTask.TASK_NAME, type:ClassWorldsCleanupTask)

                and: 'The task is configured'
                project.tasks[ClassWorldsCleanupTask.TASK_NAME].stagingDir = stagingDir

                then: 'When the task is executed'
                project.tasks[ClassWorldsCleanupTask.TASK_NAME].execute()

                expect: 'The staging folder should be deleted'
                !stagingDir.exists()
        }
}
