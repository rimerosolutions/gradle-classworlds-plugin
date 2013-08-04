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

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

import com.rimerosolutions.gradle.plugins.classworlds.ClassWorldsPluginConstants

/**
 * Main task provided by the ClassWorlds plugin.
 *
 * @author Yves Zoundi
 */
class ClassWorldsCopyBootstrapJarTask extends DefaultTask {

        /**
         * The task name.
         */
        static final String TASK_NAME = 'classworldsCopyBootstrapJar'
        
        /**
         * The task description.
         */
        static final String TASK_DESCRIPTION = 'Copy ClassWorlds bootstrap jar.'
        
        /**
         * Property holding the file name of the <code>plexus-classworlds</code> dependency.
         */
        static final String BOOTSTRAP_JAR_FILENAME_PROPERTY = 'bootJarArtifactName'

        @InputDirectory File bootDir

        /**
         * Locates the <code>plexus-classworlds</code> dependency in the classpath.
         * 
         * @return The <code>plexus-classworlds</code> dependency jar file
         */
        protected File findBootstrapJarArtifact() {
                def configs = project.buildscript.configurations
                def resolvedArtifacts = configs.classpath.resolvedConfiguration.resolvedArtifacts

                resolvedArtifacts.find { resolvedDependency ->
                        ModuleVersionIdentifier moduleVersionIdentifier = resolvedDependency.moduleVersion.id

                        (moduleVersionIdentifier.group == ClassWorldsPluginConstants.CLASSWORLDS_GROUP_ID &&
                         moduleVersionIdentifier.name == ClassWorldsPluginConstants.CLASSWORLDS_ARTIFACT_ID &&
                         resolvedDependency.type == ClassWorldsPluginConstants.CLASSWORLDS_ARTIFACT_TYPE)
                }.file
        }

        /**
         * Copies the <code>plexus-classworlds</code> jar file to the boot directory.
         * 
         * @param bootJarArtifact The <code>plexus-classworlds</code> jar file
         * @param bootDirectory The boot directory
         */
        protected void copyClassWorldsJarToBootDir(File bootJarArtifact, File bootDirectory) {
                project.copy {
                        from bootJarArtifact
                        into bootDirectory
                }

                project.ext[BOOTSTRAP_JAR_FILENAME_PROPERTY] = bootJarArtifact.name
        }

        @TaskAction copyClassWorldsJar() {
                logger.info 'Copying classworlds bootstrap jar to boot folder.'
                
                copyClassWorldsJarToBootDir(findBootstrapJarArtifact(), bootDir)
        }

}
