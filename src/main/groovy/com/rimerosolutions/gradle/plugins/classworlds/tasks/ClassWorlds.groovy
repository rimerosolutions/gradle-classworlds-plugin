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

import com.rimerosolutions.gradle.plugins.classworlds.ClassWorldsPluginExtension
import com.rimerosolutions.gradle.plugins.classworlds.ClassWorldsPluginConstants

import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.TaskAction

/**
 * Main task provided by the ClassWorlds plugin.
 *
 * @author Yves Zoundi
 */
class ClassWorlds extends DefaultTask {

        @TaskAction assemble(){
                // Get the file list of dependencies for relevant configurations
                def compileDeps = project.configurations.compile.files
                def runtimeDeps = project.configurations.runtime.files
                def archiveDeps = project.configurations.archives.allArtifacts.files.files

                // Combine all the libraries dependencies into one list
                def allDeps = compileDeps + runtimeDeps + archiveDeps

                def stagingDir = new File(project.buildDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.STAGING)
                def bootDir = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.BOOT)
                def libDir  = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.LIB)
                def etcDir  = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.ETC)
                def binDir  = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.BIN)

                // Create assembly directory layout
                ant.mkdir(dir:stagingDir)
                ant.mkdir(dir:etcDir)
                ant.mkdir(dir:binDir)
                ant.mkdir(dir:bootDir)
                ant.mkdir(dir:libDir)

                // Locate the classworlds dependency jar file from the buildscript classpath
                ResolvedArtifact classworldsJarArtifact = project.buildscript.configurations.classpath.resolvedConfiguration.resolvedArtifacts.find { it ->
                        ModuleVersionIdentifier mvi = it.moduleVersion.id

                        (mvi.group == ClassWorldsPluginConstants.CLASSWORLDS_GROUP_ID &&
                         mvi.name == ClassWorldsPluginConstants.CLASSWORLDS_ARTIFACT_ID &&
                         it.type == ClassWorldsPluginConstants.CLASSWORLDS_ARTIFACT_TYPE)
                }

                // Validate the plugin configuration
                def classworldsClosure = project.extensions.getByName(ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME)
                validatePluginConfiguration(classworldsClosure)

                String appMainClassName = classworldsClosure.appMainClassName
                String appHomeEnvName = classworldsClosure.appLocationEnvVariableName

                copyApplicationDependenciesToLibDir(allDeps, libDir)
                copyClassWorldsJarToBootDir(classworldsJarArtifact.file, bootDir)
                generateLauncherConfigurationFile(etcDir, appMainClassName, allDeps)
                generateLauncherScripts(binDir, classworldsJarArtifact.file.name, appHomeEnvName)
                generateAssemblyZip(stagingDir)
                cleanupStagingArea(stagingDir)                
        }

        private def validatePluginConfiguration(ClassWorldsPluginExtension classworldsClosure) {
                if (!classworldsClosure.appLocationEnvVariableName) {
                        StringBuilder sb = new StringBuilder(256)
                        sb.append("The property \"appLocationEnvVariableName\" is missing or empty from the \"classworlds\" block in the Gradle build file.\n")
                        sb.append("\"appLocationEnvVariableName\" should be assigned to the expected environment variable used to locate your app folder.\n")

                        throw new InvalidUserDataException(sb.append(getConfigurationBlockExample()).toString())
                }

                if (!classworldsClosure.appMainClassName) {
                        StringBuilder sb = new StringBuilder(256)
                        sb.append("The property \"appMainClassName\" is missing or empty from the \"classworlds\" block in the Gradle build file.\n")
                        sb.append("\"appMainClassName\" should be assigned to the main class of the generated launcher.\n")

                        throw new InvalidUserDataException(sb.append(getConfigurationBlockExample()).toString())
                }
        }

        private def copyApplicationDependenciesToLibDir(Collection libFiles, File libDir) {
                project.copy  {
                        from libFiles
                        into libDir
                }
        }

        private def copyClassWorldsJarToBootDir(File bootJarArtifact, File bootDir) {
                project.copy {
                        from bootJarArtifact
                        into bootDir
                }
        }

        private def generateAssemblyZip(File stagingDir) {
                String assemblyName = "${project.name}-${project.version}"
                
                ant.zip(destfile: new File(project.buildDir.absolutePath, "${assemblyName}.zip")) {
                        zipfileset(dir:stagingDir, prefix:assemblyName)
                }
        }

        private def cleanupStagingArea(File stagingDir) {
                ant.delete(dir:stagingDir, includeEmptyDirs:true, verbose: true, failonerror: false, quiet:true)
        }
        
        private static String getConfigurationBlockExample() {
                return "\nSample configuration:\n\nclassworlds {\n\tappMainClassName=\"com.Main\"\n\tappLocationEnvVariableName=\"application_home\"\n}"
        }

        private def generateLauncherScripts(File binDir, String bootJarFileName, String appHome) {
                def shellHeaders = ["#!/usr/bin/env bash", ""]
                def shellComments = ["#Generated by Gradle ClassWorlds Plugin", "@REM Generated by Gradle ClassWorlds Plugin"]
                def launcherFileNames = ["launcher.sh", "launcher.bat"]

                launcherFileNames.eachWithIndex { launcherName, listIndex ->
                        File launcherFile = new File(binDir.absolutePath, launcherName)
                        def shellComment = shellComments[listIndex]
                        def shellHeader = shellHeaders[listIndex]
                        def tplModel = [appHome:appHome, bootJarFileName:bootJarFileName, shellHeader:shellHeader, shellComment:shellComment]

                        launcherFile.withWriter { w ->
                                def engine = new groovy.text.GStringTemplateEngine()
                                def tplLocation = "${ClassWorldsPluginConstants.TEMPLATES_LOCATION}/${launcherName}"
                                def templateUrl = Thread.currentThread().getContextClassLoader().getResource(tplLocation)
                                def template = engine.createTemplate(templateUrl)

                                template.make(tplModel).writeTo(w)
                        }

                        launcherFile.setExecutable(true)
                }
        }

        private def generateLauncherConfigurationFile(File cfgDir, String mainClassName, Collection libs) {
                def tplLocation = "${ClassWorldsPluginConstants.TEMPLATES_LOCATION}/classworlds-template.groovy"
                File launcherCfgFile = new File(cfgDir.absolutePath, "classworlds.conf")
                def tplModel = [libs:libs, mainClassName:mainClassName]

                launcherCfgFile.withWriter { w ->
                        def engine = new groovy.text.GStringTemplateEngine()
                        def templateUrl = Thread.currentThread().getContextClassLoader().getResource(tplLocation)
                        def template = engine.createTemplate(templateUrl)

                        template.make(tplModel).writeTo(w)
                }
        }

}
