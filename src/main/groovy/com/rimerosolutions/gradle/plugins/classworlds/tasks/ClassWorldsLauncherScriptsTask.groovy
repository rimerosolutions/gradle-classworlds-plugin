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

import groovy.text.GStringTemplateEngine

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import com.rimerosolutions.gradle.plugins.classworlds.ClassWorldsPluginConstants

/**
 * Main task provided by the ClassWorlds plugin.
 *
 * @author Yves Zoundi
 */
class ClassWorldsLauncherScriptsTask extends DefaultTask {

        /**
         * The task name.
         */
        static final String TASK_NAME = 'classWorldsLauncherScripts'
        
        /**
         * The task description.
         */
        static final String TASK_DESCRIPTION = 'Generate ClassWorlds launcher scripts.'

        @Input
        /**
         * The main class name.
         */
        String appMainClassName

        @Input
        /**
         * The application home JVM property.
         */
        String appHome

        @OutputDirectory
        /**
         * The launcher scripts folder.
         */
        File scriptsDir

        @Input
        /**
         * The launcher scripts JVM options.
         */
        String jvmOptions

        @TaskAction writeLaunchers() {
                generateLauncherScripts(scriptsDir, project.ext[ClassWorldsCopyBootstrapJarTask.BOOTSTRAP_JAR_FILENAME_PROPERTY])
        }

        /**
         * Generates the Unix and Windows launcher scripts.
         * 
         * @param binDir The scripts directory
         * @param bootJarFileName The <code>plexus-classworlds</code> bootstrap jar
         */
        void generateLauncherScripts(File binDir, String bootJarFileName) {
                logger.info 'Generating launchers scripts.'

                ClassWorldsPluginConstants.ScriptConstants.LAUNCHER_TEMPLATES_FILENAMES.eachWithIndex { launcherName, listIndex ->
                        String launcherFilename = ClassWorldsPluginConstants.ScriptConstants.LAUNCHER_TEMPLATES_FILENAMES_MAPPING[listIndex]
                        File launcherFile = new File(binDir.absolutePath, launcherFilename)
                        String shellComment = ClassWorldsPluginConstants.ScriptConstants.BRANDING_COMMENTS[listIndex]
                        String shellHeader = ClassWorldsPluginConstants.ScriptConstants.SHELL_HEADERS[listIndex]

                        def scriptBinding = [
                                appHome:appHome,
                                bootJarFileName: bootJarFileName,
                                shellHeader:shellHeader,
                                shellComment:shellComment,
                                jvmOptions:jvmOptions
                        ]

                        launcherFile.withWriter { w ->
                                def templateEngine = new GStringTemplateEngine()
                                def templateLocation = "${ClassWorldsPluginConstants.TEMPLATES_LOCATION}/${launcherName}"
                                def templateUrl = Thread.currentThread().contextClassLoader.getResource(templateLocation)
                                def template = templateEngine.createTemplate(templateUrl)

                                template.make(scriptBinding).writeTo(w)
                        }
                }
        }

}
