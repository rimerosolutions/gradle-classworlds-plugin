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
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.ResolvedModuleVersion
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileTreeElement
import org.gradle.api.file.FileCollection.AntType
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.api.tasks.util.PatternSet
import org.gradle.api.tasks.Copy
import org.gradle.api.InvalidUserDataException

class ClassWorlds extends DefaultTask {

        @TaskAction assemble(){
                def compileDeps = project.configurations.compile.files
                def runtimeDeps = project.configurations.runtime.files
                def archiveDeps = project.configurations.archives.allArtifacts.files.files                
                def allDeps = compileDeps + runtimeDeps + archiveDeps

                def destDir = new File(project.buildDir.absolutePath, "classworlds")
                def bootDir = new File(destDir.absolutePath, "boot")
                def libDir  = new File(destDir.absolutePath, "lib")
                def etcDir  = new File(destDir.absolutePath, "etc")
                def binDir  = new File(destDir.absolutePath, "bin")

                ant.mkdir(dir:destDir)
                ant.mkdir(dir:etcDir)
                ant.mkdir(dir:binDir)
                ant.mkdir(dir:bootDir)
                ant.mkdir(dir:libDir)

                ResolvedArtifact classworldsJarArtifact = project.buildscript.configurations.classpath.resolvedConfiguration.resolvedArtifacts.find { it ->
                        ModuleVersionIdentifier mvi = it.moduleVersion.id
                        mvi.group == "classworlds" && mvi.name == "classworlds" && it.type == "jar"
                }

                project.copy  {
                        from allDeps
                        into libDir
                }

                def classworldsClosure = project.extensions.getByName('classworlds')

                validatePluginConfiguration(classworldsClosure)

                String appMainClassName = classworldsClosure.appMainClassName
                String appHomeEnvName = classworldsClosure.appLocationEnvVariableName

                copyClassWorldsJarToBootDir(classworldsJarArtifact.file, bootDir)
                generateLauncherConfiguration(etcDir, appMainClassName, allDeps)
                generateLauncherScripts(binDir, classworldsJarArtifact.file.name, appHomeEnvName)
        }

        private def validatePluginConfiguration(ClassWorldsPluginExtension classworldsClosure) {
                if (!classworldsClosure.appLocationEnvVariableName) {
                        StringBuilder sb = new StringBuilder(256)
                        sb.append("The property \"appLocationEnvVariableName\" is missing or empty from the \"classworlds\" block in the Gradle build file.\n")
                        sb.append("\"appLocationEnvVariableName\" should be assigned to the expected environment variable used to locate your app folder.\n")

                        throw new InvalidUserDataException(sb.append(sampleConfigurationBlock()).toString())
                }

                if (!classworldsClosure.appMainClassName) {
                        StringBuilder sb = new StringBuilder(256)
                        sb.append("The property \"appMainClassName\" is missing or empty from the \"classworlds\" block in the Gradle build file.\n")
                        sb.append("\"appMainClassName\" should be assigned to the main class of the generated launcher.\n")

                        throw new InvalidUserDataException(sb.append(sampleConfigurationBlock()).toString())
                }

        }

        private def copyClassWorldsJarToBootDir(File bootJarArtifact, File bootDir) {
                project.copy {
                        from bootJarArtifact
                        into bootDir
                }
        }

        private String sampleConfigurationBlock() {
                return "\nSample configuration:\n\nclassworlds {\n\tappMainClassName=\"com.Main\"\n\tappLocationEnvVariableName=\"grails_home\"\n}"
        }

        private def generateLauncherScripts(File binDir, String bootJarFileName, String appHome) {
                def templatesLocation = "com/rimerosolutions/gradle/plugins/classworlds/templates/"
                def shellHeader = "#!/bin/sh"
                def launcherFileNames = ["launcher.sh", "launcher.bat"]

                launcherFileNames.each { launcherName ->
                        File launcherFile = new File(binDir.absolutePath, launcherName)
                        def tplModel = [appHome:appHome, bootJarFileName:bootJarFileName, shellHeader:shellHeader]

                        launcherFile.withWriter { w ->
                                def engine = new groovy.text.GStringTemplateEngine()
                                def tplLocation = templatesLocation + launcherName
                                def templateUrl = Thread.currentThread().getContextClassLoader().getResource(tplLocation)
                                def template = engine.createTemplate(templateUrl)
                                
                                template.make(tplModel).writeTo(w)
                        }

                        launcherFile.setExecutable(true)
                }
        }

        private def generateLauncherConfiguration(File cfgDir, String mainClassName, Collection libs) {
                def tplLocation = "com/rimerosolutions/gradle/plugins/classworlds/templates/classworlds-template.groovy"
                File launcherCfg = new File(cfgDir.absolutePath, "classworlds.conf")
                def tplModel = [libs:libs, mainClassName:mainClassName]

                launcherCfg.withWriter { w ->
                        def engine = new groovy.text.GStringTemplateEngine()
                        def templateUrl = Thread.currentThread().getContextClassLoader().getResource(tplLocation)
                        def template = engine.createTemplate(templateUrl)
                        
                        template.make(tplModel).writeTo(w)
                }
        }

}
