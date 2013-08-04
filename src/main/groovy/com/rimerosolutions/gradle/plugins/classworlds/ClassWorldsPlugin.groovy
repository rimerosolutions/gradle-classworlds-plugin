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
import org.gradle.api.tasks.GradleBuild

import com.rimerosolutions.gradle.plugins.classworlds.tasks.*

/**
 * ClassWorlds Plugin implementation that registers the <code>classworlds</code> task.
 *
 * @author Yves Zoundi
 */
class ClassWorldsPlugin implements Plugin<Project> {

        static final List<String> CLASSWORLDS_SUB_TASKS_NAMES = [ClassWorldsInitStagingDirTask.TASK_NAME,                                                                 
                                                                 ClassWorldsCopyBootstrapJarTask.TASK_NAME,                                                                 
                                                                 ClassWorldsLauncherScriptsTask.TASK_NAME,                                                                 
                                                                 ClassWorldsLauncherCfgTask.TASK_NAME,                                                                 
                                                                 ClassWorldsCopyDependenciesTask.TASK_NAME,
                                                                 ClassWorldsAssemblyTask.TASK_NAME,
                                                                 ClassWorldsCleanupTask.TASK_NAME]

        @Override
        void apply(Project project) {
                project.logger.info 'Applying ClassWorlds plugin'

                ClassWorldsPluginExtension ext = project.extensions.create(ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME, ClassWorldsPluginExtension)
                ext.configureDefaults(project)

                project.afterEvaluate {
                        registerProjectTasks(project, ext)
                }
        }

        private void registerProjectTasks(Project project, ClassWorldsPluginExtension ext) {
                project.with {
                        task(type: GradleBuild, ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_TASK_NAME) {
                                description = ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_TASK_DESCRIPTION
                                group = ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_GROUP
                                tasks = CLASSWORLDS_SUB_TASKS_NAMES
                        }

                        task(type: ClassWorldsInitStagingDirTask, ClassWorldsInitStagingDirTask.TASK_NAME, dependsOn: ClassWorldsPluginConstants.TaskSettings.BUILD_TASK_NAME) {
                                description = ClassWorldsInitStagingDirTask.TASK_DESCRIPTION
                                group = ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_GROUP
                                
                                stagingDir = ext.stagingDir
                        }

                        task(type: ClassWorldsCopyBootstrapJarTask, ClassWorldsCopyBootstrapJarTask.TASK_NAME, dependsOn: ClassWorldsInitStagingDirTask.TASK_NAME) {
                                description = ClassWorldsCopyBootstrapJarTask.TASK_DESCRIPTION
                                group = ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_GROUP
                                
                                bootDir = new File("${ext.stagingDir}/${ClassWorldsPluginConstants.AssemblyDirNames.BOOT}")
                        }

                        task(type: ClassWorldsLauncherScriptsTask, ClassWorldsLauncherScriptsTask.TASK_NAME, dependsOn: ClassWorldsCopyBootstrapJarTask.TASK_NAME) {
                                description = ClassWorldsLauncherScriptsTask.TASK_DESCRIPTION
                                group = ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_GROUP

                                appMainClassName = ext.appMainClassName
                                appHome = ext.appHome
                                scriptsDir = new File("${ext.stagingDir}/${ClassWorldsPluginConstants.AssemblyDirNames.BIN}")
                                jvmOptions = ext.jvmOptions

                        }

                        task(type: ClassWorldsLauncherCfgTask, ClassWorldsLauncherCfgTask.TASK_NAME, dependsOn: ClassWorldsLauncherScriptsTask.TASK_NAME) {
                                description = ClassWorldsLauncherCfgTask.TASK_DESCRIPTION
                                group = ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_GROUP

                                appMainClassName = ext.appMainClassName
                                cfgDir = new File("${ext.stagingDir}/${ClassWorldsPluginConstants.AssemblyDirNames.ETC}")
                                allArtifacts = project.configurations.compile + project.configurations.runtime + project.configurations.archives.allArtifacts.files
                        }

                        task(type: ClassWorldsCopyDependenciesTask, ClassWorldsCopyDependenciesTask.TASK_NAME, dependsOn:ClassWorldsLauncherScriptsTask.TASK_NAME) {
                                description = ClassWorldsCopyDependenciesTask.TASK_DESCRIPTION
                                group = ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_GROUP

                                libDir = new File("${ext.stagingDir}/${ClassWorldsPluginConstants.AssemblyDirNames.LIB}")
                                allArtifacts = project.configurations.compile + project.configurations.runtime + project.configurations.archives.allArtifacts.files
                        }

                        task(type: ClassWorldsAssemblyTask, ClassWorldsAssemblyTask.TASK_NAME, dependsOn:ClassWorldsCopyDependenciesTask.TASK_NAME) {
                                description = ClassWorldsAssemblyTask.TASK_DESCRIPTION
                                group = ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_GROUP

                                assemblyFormats = ext.assemblyFormats
                                assemblyFileName = ext.assemblyFileName
                                stagingDir = ext.stagingDir
                        }

                        task(type: ClassWorldsCleanupTask, ClassWorldsCleanupTask.TASK_NAME, dependsOn: ClassWorldsAssemblyTask.TASK_NAME) {
                                description = ClassWorldsCleanupTask.TASK_DESCRIPTION
                                group = ClassWorldsPluginConstants.TaskSettings.CLASSWORLDS_GROUP
                                
                                stagingDir = ext.stagingDir
                        }
                }

        }
}