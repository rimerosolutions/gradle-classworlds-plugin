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

import com.rimerosolutions.gradle.plugins.classworlds.ClassWorldsPlugin
import com.rimerosolutions.gradle.plugins.classworlds.ClassWorldsPluginExtension
import com.rimerosolutions.gradle.plugins.classworlds.ClassWorldsPluginConstants

import spock.lang.Specification

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

/**
 * Basic spock test for the <code>ClassWorlds</code> task, work left.
 *
 * @author Yves Zoundi
 */
class ClassWorldsTaskTest extends Specification {
        private static final List<String> KNOWN_INPUT_NAMES = ['appLocationEnvVariableName', 'appMainClassName', 'assemblyFileName', 'assemblyFormats']        
        private final Project project = ProjectBuilder.builder().build()

        def setup() {
                project.apply(plugin: ClassWorldsPlugin)
        }
        
        def 'extensionsAreInstalled'() {
                expect:
                        project.extensions.getByName(ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME) instanceof ClassWorldsPluginExtension
        }

        void 'classworldsTaskRegisteredCorrectly'() {
                when:
                        def classworldsTasks = project.getTasksByName(ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_TASK_NAME, false)

                then: 
                        classworldsTasks.size() == 1
                        classworldsTasks.iterator().next() instanceof ClassWorldsTask
        }

        void 'classworldsTaskInputsRegisteredCorrectly'() {
                when:
                        def classworldsTasks = project.getTasksByName(ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_TASK_NAME, false)
                        ClassWorldsTask task = classworldsTasks.iterator().next()
                        

                then:                         
                        task.inputs.properties.keySet().containsAll(KNOWN_INPUT_NAMES)
        }


}