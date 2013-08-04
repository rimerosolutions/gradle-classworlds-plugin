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

/**
 * ClassWorlds Plugin extension.
 *
 * @author Yves Zoundi
 */
class ClassWorldsPluginExtension {

        /**
         * The application home location.
         */
        String appHome
        
        /**
         * The application main class name.
         */
        String appMainClassName
        
        /**
         * 
         */
        String assemblyFileName
        
        /**
         * The launcher JVM options.
         */
        String jvmOptions
        
        /**
         * The staging directory to use to prepare assemblies.
         */
        File stagingDir

        /**
         * The assembly formats.
         * 
         * @see ClassWorldsPluginConstants.AssemblyFormats
         */
        List<String> assemblyFormats

        /**
         * Apply the default ClassWorlds plugin settings to a project.
         * 
         * @param project The Gradle project
         */
        void configureDefaults(Project project) {
                project.logger.info 'Configuring default ClassWorlds settings'

                assemblyFileName = assemblyFileName ?: "${project.name}-${project.version}"
                stagingDir = stagingDir ?: new File(project.buildDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.STAGING)
                assemblyFormats = assemblyFormats ?: [ClassWorldsPluginConstants.AssemblyFormats.ZIP.name()]
                jvmOptions = jvmOptions ?: ClassWorldsPluginConstants.DEFAULT_JVM_OPTIONS
                appHome = appHome ?: 'APP_HOME'
        }

        /**
         * Perform arbitrary actions with a given staging folder for flexibility.
         * 
         * @param stagingFolder The assembly staging folder
         * @param closure The closure block to run
         */
        void doWithStagingDir(File stagingFolder, Closure closure) {
                closure(stagingFolder)
        }
}