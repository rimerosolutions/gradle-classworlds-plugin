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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.Action
import org.gradle.api.artifacts.Dependency
import com.rimerosolutions.gradle.plugins.classworlds.tasks.ClassWorlds

class ClassWorldsPlugin implements Plugin<Project> {
        static final String CLASSWORLDS_TASK_NAME = "classworlds"
        static final String CLASSWORLDS_TASK_DESCRIPTION = "Generates a runnable application assembly using ClassWorlds as application launcher."
        static final String CLASSWORLDS_GROUP = "ClassWorlds"
        static final String CLASSWORDS_STAGE_DIR = "classworlds"

        @Override
        public void apply(Project project) {
                project.extensions.classworlds = new ClassWorldsPluginExtension()
                
                Task classworlds = project.tasks.create(CLASSWORLDS_TASK_NAME, ClassWorlds)
                classworlds.description = CLASSWORLDS_TASK_DESCRIPTION
                classworlds.group = CLASSWORLDS_GROUP
                classworlds.dependsOn "build"
        }
}