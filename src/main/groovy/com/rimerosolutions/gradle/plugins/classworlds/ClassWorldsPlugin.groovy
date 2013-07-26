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
import com.rimerosolutions.gradle.plugins.classworlds.tasks.ClassWorldsTask

/**
 * ClassWorlds Plugin implementation that registers the <code>classworlds</code> task.
 *
 * @author Yves Zoundi
 */
class ClassWorldsPlugin implements Plugin<Project> {

        @Override
        void apply(Project project) {
                project.extensions.create('classworlds', ClassWorldsPluginExtension)

                project.task(type: ClassWorldsTask, ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_TASK_NAME) {
                        description = ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_TASK_DESCRIPTION
                        group = ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_GROUP
                        dependsOn ClassWorldsPluginConstants.TaskSettings.BUILD_TASK_NAME

                        conventionMapping.appMainClassName = {
                                project.extensions.getByName(ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME).appMainClassName
                        }

                        if (project.extensions.getByName(ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME).appLocationEnvVariableName) {
                                conventionMapping.appLocationEnvVariableName = {
                                        project.extensions.getByName(ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME).appLocationEnvVariableName
                                }
                        }

                        if (project.extensions.getByName(ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME).assemblyFileName) {
                                conventionMapping.assemblyFileName = {
                                        project.extensions.getByName(ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME).assemblyFileName
                                }
                        }
                }
        }
}