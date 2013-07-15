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

        String appLocationEnvVariableName
        String appMainClassName
        String assemblyFileName
        File assemblyDirectory
        List<ClassWorldsPluginConstants.AssemblyFormats> assemblyFormats = []

        ClassWorldsPluginExtension(Project project) {
                assemblyDirectory = new File(project.buildDir.absolutePath)
                assemblyFileName = "${project.name}-${project.version}"
                appLocationEnvVariableName = 'APP_HOME'
                assemblyFormats << ClassWorldsPluginConstants.AssemblyFormats.ZIP
        }

        def doWithStagingDir(File stagingFolder, Closure closure) {
                closure.delegate = this
                closure(stagingFolder)
        }
}