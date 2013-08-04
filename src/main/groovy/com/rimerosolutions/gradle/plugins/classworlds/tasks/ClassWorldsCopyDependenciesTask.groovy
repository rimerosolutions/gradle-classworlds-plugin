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

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Main task provided by the ClassWorlds plugin.
 *
 * @author Yves Zoundi
 */
class ClassWorldsCopyDependenciesTask extends DefaultTask {

        static final String TASK_NAME = 'classworldsCopyDependencies'
        static final String TASK_DESCRIPTION = 'Copy application dependencies.'

        @OutputDirectory
        File libDir

        @Input
        FileCollection allArtifacts

        @TaskAction
        void copyApplicationDependenciesToLibDir() {
                logger.info 'Copying all artifacts to lib folder.'

                project.copy  {
                        from allArtifacts
                        into libDir
                        include ClassWorldsPluginConstants.Archives.WILDCARD_JAR
                        include ClassWorldsPluginConstants.Archives.WILDCARD_ZIP
                }
        }
}
