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
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

import com.rimerosolutions.gradle.plugins.classworlds.ClassWorldsPluginConstants

/**
 * Main task provided by the ClassWorlds plugin.
 *
 * @author Yves Zoundi
 */
class ClassWorldsLauncherCfgTask extends DefaultTask {

        static final String CONFIGURATION_FILENAME = 'classworlds.conf'
        static final String TEMPLATE_LOCATION = "${ClassWorldsPluginConstants.TEMPLATES_LOCATION}/classworlds-template.txt"
        static final String TASK_NAME = 'classWorldsLaunchersCfgTask'
        static final String TASK_DESCRIPTION = 'Generate ClassWorlds launcher configuration.'
        
        @Input
        String appMainClassName

        @InputDirectory
        File cfgDir
        
        @Input
        FileCollection allArtifacts

        @TaskAction writeConfigurationFile() {
                project.logger.info 'Generating ClassWorlds launcher configuration.'
                
                File configurationFile = project.file("$cfgDir/$CONFIGURATION_FILENAME")
                
                def filteredLibs = allArtifacts.files.findAll {
                        it.name.toLowerCase().endsWith(ClassWorldsPluginConstants.Archives.EXTENSION_ZIP) ||
                        it.name.toLowerCase().endsWith(ClassWorldsPluginConstants.Archives.EXTENSION_JAR)
                }
                
                def launcherConfigurationBinding = [libs:filteredLibs, mainClassName:appMainClassName]

                configurationFile.withWriter { w ->
                        def templateEngine = new GStringTemplateEngine()
                        def templateUrl = Thread.currentThread().contextClassLoader.getResource(TEMPLATE_LOCATION)
                        def template = templateEngine.createTemplate(templateUrl)

                        template.make(launcherConfigurationBinding).writeTo(w)
                }
        }

}
