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

                // Set staging dir folders
                def stagingDir = new File(project.buildDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.STAGING)
                def bootDir    = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.BOOT)
                def libDir     = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.LIB)
                def etcDir     = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.ETC)
                def binDir     = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.BIN)

                // Create assembly directory layout
                makeStagingDirectories(stagingDir, etcDir, binDir, bootDir, libDir)

                // Locate the classworlds dependency jar file from the buildscript classpath
                ResolvedArtifact classworldsJarArtifact = findBootstrapJarArtifact()

                // Set input/output values
                def classWorldsClosure = project.extensions.getByName(ClassWorldsPluginConstants.CLASSWORLDS_EXTENSION_NAME)
                appMainClassName = classWorldsClosure.appMainClassName
                assemblyFileName = classWorldsClosure.assemblyFileName
                jvmOptions = classWorldsClosure.jvmOptions
                assemblyFormats = classWorldsClosure.assemblyFormats
                appLocationEnvVariableName = classWorldsClosure.appLocationEnvVariableName
                
                fallBackToDefaultInputValuesAsNeeded()

                // Default actions
                copyApplicationDependenciesToLibDir(allDeps, libDir)
                copyClassWorldsJarToBootDir(classworldsJarArtifact.file, bootDir)
                generateLauncherConfigurationFile(etcDir, appMainClassName, allDeps)
                generateLauncherScripts(binDir, classworldsJarArtifact.file.name, appLocationEnvVariableName)

                // Staging dir closure for general tasks prior to assembly generation
                if (classWorldsClosure.doWithStagingDir) {
                        classWorldsClosure.doWithStagingDir(stagingDir)
                }

                generateArchiveAssemblies(stagingDir)
                cleanupStagingArea(stagingDir)
        }

        protected void fallBackToDefaultInputValuesAsNeeded() {
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

                if (!jvmOptions) {
                        jvmOptions = ClassWorldsPluginConstants.DEFAULT_JVM_OPTIONS
                }
        }

        protected ResolvedArtifact findBootstrapJarArtifact() {
                def resolvedArtifacts = project.buildscript.configurations.classpath.resolvedConfiguration.resolvedArtifacts

                resolvedArtifacts.find { resolvedDependency ->
                        ModuleVersionIdentifier moduleVersionIdentifier = resolvedDependency.moduleVersion.id

                        (moduleVersionIdentifier.group == ClassWorldsPluginConstants.CLASSWORLDS_GROUP_ID &&
                         moduleVersionIdentifier.name == ClassWorldsPluginConstants.CLASSWORLDS_ARTIFACT_ID &&
                         resolvedDependency.type == ClassWorldsPluginConstants.CLASSWORLDS_ARTIFACT_TYPE)
                }
        }

        protected void makeStagingDirectories(File[] dirs) {
                logger.info 'Creating staging directory layout'
                dirs.each { File dir ->
                        ant.mkdir(dir: dir)
                }
        }

        protected void copyApplicationDependenciesToLibDir(Collection allArtifacts, File libDir) {
                logger.info 'Copying all artifacts to lib folder.'

                project.copy  {
                        from allArtifacts
                        into libDir
                        include ClassWorldsPluginConstants.Archives.WILDCARD_JAR
                        include ClassWorldsPluginConstants.Archives.WILDCARD_ZIP
                }
        }

        protected void copyClassWorldsJarToBootDir(File bootJarArtifact, File bootDir) {
                logger.info 'Copying classworlds bootstrap jar to boot folder.'

                project.copy {
                        from bootJarArtifact
                        into bootDir
                }
        }

        protected void generateArchiveAssemblies(File stagingDir) {
                logger.info 'Creating archive assemblies.'

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
                                        zipfileset(dir:stagingDir, prefix:assemblyFileName, filemode:'755')
                                }
                        }
                        else if (f == ClassWorldsPluginConstants.AssemblyFormats.TAR) {
                                ant.tar(destfile: assemblyFile) {
                                        tarfileset(dir:stagingDir, prefix:assemblyFileName, filemode:'755')
                                }
                        }
                }
        }

        protected void cleanupStagingArea(File stagingDir) {
                ant.delete(dir:stagingDir, includeEmptyDirs:true, verbose: true, failonerror: false, quiet:true)
        }

        protected void generateLauncherScripts(File binDir, String bootJarFileName, String appHome) {
                logger.info 'Generating launchers scripts.'

                ClassWorldsPluginConstants.ScriptConstants.LAUNCHER_TEMPLATES_FILENAMES.eachWithIndex { launcherName, listIndex ->
                        String launcherFilename = ClassWorldsPluginConstants.ScriptConstants.LAUNCHER_TEMPLATES_FILENAMES_MAPPING[listIndex]
                        File launcherFile = new File(binDir.absolutePath, launcherFilename)
                        String shellComment = ClassWorldsPluginConstants.ScriptConstants.BRANDING_COMMENTS[listIndex]
                        String shellHeader = ClassWorldsPluginConstants.ScriptConstants.SHELL_HEADERS[listIndex]
                        def scriptBinding = [appHome:appHome,
                                             bootJarFileName:bootJarFileName,
                                             shellHeader:shellHeader,
                                             shellComment:shellComment,
                                             jvmOptions:jvmOptions]

                        launcherFile.withWriter { w ->
                                def templateEngine = new groovy.text.GStringTemplateEngine()
                                def templateLocation = "${ClassWorldsPluginConstants.TEMPLATES_LOCATION}/${launcherName}"
                                def templateUrl = Thread.currentThread().contextClassLoader.getResource(templateLocation)
                                def template = templateEngine.createTemplate(templateUrl)

                                template.make(scriptBinding).writeTo(w)
                        }
                }
        }

        protected void generateLauncherConfigurationFile(File cfgDir, String mainClassName, Collection libs) {
                def launcherConfigurationTemplateLocation = "${ClassWorldsPluginConstants.TEMPLATES_LOCATION}/classworlds-template.txt"
                def launcherConfigurationOutputFile = new File(cfgDir.absolutePath, 'classworlds.conf')
                def filteredLibs = libs.findAll {
                        it.name.toLowerCase().endsWith(ClassWorldsPluginConstants.Archives.EXTENSION_ZIP) ||
                        it.name.toLowerCase().endsWith(ClassWorldsPluginConstants.Archives.EXTENSION_JAR)
                }
                def launcherConfigurationBinding = [libs:filteredLibs, mainClassName:mainClassName]

                launcherConfigurationOutputFile.withWriter { w ->
                        def templateEngine = new groovy.text.GStringTemplateEngine()
                        def templateUrl = Thread.currentThread().contextClassLoader.getResource(launcherConfigurationTemplateLocation)
                        def template = templateEngine.createTemplate(templateUrl)

                        template.make(launcherConfigurationBinding).writeTo(w)
                }
        }

}
