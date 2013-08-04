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
package com.rimerosolutions.gradle.plugins.classworlds

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification

/**
 * Test for the <code>ClassWorlds</code> plugin.
 *
 * @author Yves Zoundi
 */
class ClassWorldsPluginSpec extends Specification {
        private Project project

        def setup() {
                project = ProjectBuilder.builder().build()

                project.with {
                        apply plugin: 'classworlds'
                        apply plugin: 'java'
                        version = "test.version"
                }
        }

        def 'The ClassWorlds extension was created successfully'() {
                expect: 'The classworlds extension is registered with the expected type'
                project.extensions.getByName(ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME) instanceof ClassWorldsPluginExtension
        }

        def 'All ClassWorlds tasks were registered'() {
                when: 'The project is evaluated'
                project.evaluate()

                then: 'We should have the main task and its sub-tasks registered'
                def tasks = project.tasks.findAll { it.name.toLowerCase().startsWith(ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME) }
                tasks.size() == ClassWorldsPlugin.CLASSWORLDS_SUB_TASKS_NAMES.size() + 1
        }
}