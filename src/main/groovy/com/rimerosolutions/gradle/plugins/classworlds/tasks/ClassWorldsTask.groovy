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
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.TaskAction

/**
 * Main task provided by the ClassWorlds plugin.
 *
 * @author Yves Zoundi
 */
class ClassWorldsTask extends DefaultTask {

        @Input
        String appMainClassName

        @Input
        @Optional
        String assemblyFileName

        @OutputDirectory
        @Optional
        File assemblyDirectory

        @Optional
        @Input
        String appLocationEnvVariableName

        @Optional
        @Input
        String jvmOptions

        @Input
        @Optional
        List<String> assemblyFormats

        @TaskAction assemble(){
                // Get the file list of dependencies for relevant configurations
                def compileDeps = project.configurations.compile.files
                def runtimeDeps = project.configurations.runtime.files
                def archiveDeps = project.configurations.archives.allArtifacts.files.files

                // Combine all the libraries dependencies into one list
                def allDeps = compileDeps + runtimeDeps + archiveDeps

                def stagingDir = new File(project.buildDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.STAGING)
                def bootDir    = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.BOOT)
                def libDir     = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.LIB)
                def etcDir     = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.ETC)
                def binDir     = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.BIN)

                // Create assembly directory layout
                logger.info 'Creating standard folder structure'

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
                def classWorldsClosure = project.extensions.getByName(ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME)
                validatePluginConfiguration(classWorldsClosure)

                appMainClassName = classWorldsClosure.appMainClassName
                assemblyFileName = classWorldsClosure.assemblyFileName
                jvmOptions = classWorldsClosure.jvmOptions
                assemblyFormats = classWorldsClosure.assemblyFormats
                appLocationEnvVariableName = classWorldsClosure.appLocationEnvVariableName

                copyApplicationDependenciesToLibDir(allDeps, libDir)
                copyClassWorldsJarToBootDir(classworldsJarArtifact.file, bootDir)
                generateLauncherConfigurationFile(etcDir, appMainClassName, allDeps)
                generateLauncherScripts(binDir, classworldsJarArtifact.file.name, appLocationEnvVariableName)

                if (classWorldsClosure.doWithStagingDir) {
                        classWorldsClosure.doWithStagingDir(stagingDir)
                }

                generateAssemblyZip(stagingDir)
                cleanupStagingArea(stagingDir)
        }

        private validatePluginConfiguration(ClassWorldsPluginExtension classworldsClosure) {
                logger.info 'Validating ClassWorlds configuration'

                if (!classworldsClosure.appLocationEnvVariableName) {
                        StringBuilder sb = new StringBuilder(256)

                        sb.append('The property \"appLocationEnvVariableName\" is missing or empty from the \"classworlds\" block in the Gradle build file.\n')
                        sb.append('\"appLocationEnvVariableName\" should be assigned to the expected environment variable used to locate your app folder.\n')

                        throw new InvalidUserDataException(sb.append(configurationBlockExample()).toString())
                }

                if (!classworldsClosure.appMainClassName) {
                        StringBuilder sb = new StringBuilder(256)

                        sb.append('The property \"appMainClassName\" is missing or empty from the \"classworlds\" block in the Gradle build file.\n')
                        sb.append('\"appMainClassName\" should be assigned to the main class of the generated launcher.\n')

                        throw new InvalidUserDataException(sb.append(configurationBlockExample()).toString())
                }
        }

        private copyApplicationDependenciesToLibDir(Collection libFiles, File libDir) {
                project.copy  {
                        from libFiles
                        into libDir
                }
        }

        private copyClassWorldsJarToBootDir(File bootJarArtifact, File bootDir) {
                logger.info 'Copying dependencies to project folder'

                project.copy {
                        from bootJarArtifact
                        into bootDir
                }
        }

        private generateAssemblyZip(File stagingDir) {
                logger.info 'Creating zip assembly'

                if(!assemblyFileName) {
                        assemblyFileName = "${project.name}-${project.version}"
                }

                if (!assemblyDirectory) {
                        assemblyDirectory = new File(project.buildDir.absolutePath)
                }

                if (!assemblyFormats) {
                        assemblyFormats = [
                                ClassWorldsPluginConstants.AssemblyFormats.ZIP.name()
                        ]
                }

                for (String fileFormat in assemblyFormats) {
                        ClassWorldsPluginConstants.AssemblyFormats f = null

                        try {
                                f = ClassWorldsPluginConstants.AssemblyFormats.valueOf(fileFormat.toUpperCase())
                        }
                        catch (IllegalArgumentException err) {
                                throw new InvalidUserDataException("Invalid format. Valid formats are ${ClassWorldsPluginConstants.AssemblyFormats.values()}")
                        }

                        def assemblyFile = new File(assemblyDirectory.absolutePath, "${assemblyFileName}.${f.name().toLowerCase()}")

                        if (f == ClassWorldsPluginConstants.AssemblyFormats.ZIP) {
                                ant.zip(destfile: assemblyFile) {
                                        zipfileset(dir:stagingDir, prefix:assemblyFileName)
                                }
                        }
                        else if (f == ClassWorldsPluginConstants.AssemblyFormats.TAR) {
                                ant.tar(destfile: assemblyFile, compression: 'none' ) {
                                        tarfileset(dir:stagingDir, prefix:assemblyFileName)
                                }
                        }
                }
        }

        private cleanupStagingArea(File stagingDir) {
                ant.delete(dir:stagingDir, includeEmptyDirs:true, verbose: true, failonerror: false, quiet:true)
        }

        private static String configurationBlockExample() {
                '\nSample configuration:\n\nclassworlds {\n\tappMainClassName=\"com.Main\"\n\tappLocationEnvVariableName=\"application_home\"\n}'
        }

        private generateLauncherScripts(File binDir, String bootJarFileName, String appHome) {
                logger.info 'Generating launchers scripts'

                if (!jvmOptions) {
                        jvmOptions = ClassWorldsPluginConstants.DEFAULT_JVM_OPTIONS 
                }

                def shellHeaders = ['#!/usr/bin/env bash', '']
                def shellComments = ['#Generated by Gradle ClassWorlds Plugin', '@REM Generated by Gradle ClassWorlds Plugin']
                def launcherFileNames = ['launcher_unix.txt', 'launcher_windows.txt']
                def launcherFileNamesMapping = ['run.sh', 'run.bat']

                launcherFileNames.eachWithIndex { launcherName, listIndex ->
                        def launcherFile = new File(binDir.absolutePath, launcherFileNamesMapping[listIndex])
                        def shellComment = shellComments[listIndex]
                        def shellHeader = shellHeaders[listIndex]
                        def tplModel = [appHome:appHome, bootJarFileName:bootJarFileName, shellHeader:shellHeader, shellComment:shellComment, jvmOptions:jvmOptions]

                        launcherFile.withWriter { w ->
                                def engine = new groovy.text.GStringTemplateEngine()
                                def tplLocation = "${ClassWorldsPluginConstants.TEMPLATES_LOCATION}/${launcherName}"
                                def templateUrl = Thread.currentThread().contextClassLoader.getResource(tplLocation)
                                def template = engine.createTemplate(templateUrl)

                                template.make(tplModel).writeTo(w)
                        }

                        launcherFile.setExecutable(true)
                }
        }

        private generateLauncherConfigurationFile(File cfgDir, String mainClassName, Collection libs) {
                def tplLocation = "${ClassWorldsPluginConstants.TEMPLATES_LOCATION}/classworlds-template.txt"
                def launcherCfgFile = new File(cfgDir.absolutePath, 'classworlds.conf')
                def tplModel = [libs:libs, mainClassName:mainClassName]

                launcherCfgFile.withWriter { w ->
                        def engine = new groovy.text.GStringTemplateEngine()
                        def templateUrl = Thread.currentThread().contextClassLoader.getResource(tplLocation)
                        def template = engine.createTemplate(templateUrl)

                        template.make(tplModel).writeTo(w)
                }
        }

}
