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

import org.gradle.api.Project
import org.gradle.api.tasks.TaskValidationException
import org.gradle.testfixtures.ProjectBuilder

import com.rimerosolutions.gradle.plugins.classworlds.ClassWorldsPluginConstants

import spock.lang.Specification

/**
 * Spock test for the <code>classWorldsAssemblies</code> task.
 * 
 * @author Yves Zoundi
 */
abstract class ClassWorldsTaskSpec extends Specification {

        Project project
        final String tmpFolder = System.properties['java.io.tmpdir']
        final String projectPath = tmpFolder + File.separator + ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME

        def setup() {
                project = ProjectBuilder.builder().withProjectDir(new File(projectPath)).build()

                project.with {
                        apply plugin: 'classworlds'
                        apply plugin: 'java'
                        version = 'test.version'
                }

                project.extensions[ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME].appMainClassName = 'com.test.Main'
        }

        def 'When inputs are missing an exception should be thrown'() {
                given: 'The task is configured with no inputs'
                def task = project.task(taskName, type: taskClass)

                when: 'The task is executed'
                task.execute()

                then: 'An TaskValidationException error should be thrown'
                thrown(TaskValidationException)
        }

        def cleanup() {
                boolean deleted = new File(projectPath).deleteDir()
                assert deleted
        }
        
        abstract Class getTaskClass()
        
        abstract String getTaskName()
}
