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
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

import com.rimerosolutions.gradle.plugins.classworlds.ClassWorldsPluginConstants

/**
 * Main task provided by the ClassWorlds plugin.
 *
 * @author Yves Zoundi
 */
class ClassWorldsAssemblyTask extends DefaultTask {

        static final String TASK_NAME = 'classWorldsAssemblies'
        static final String TASK_DESCRIPTION = 'Generate ClassWorlds assemblies.'

        @InputDirectory
        File stagingDir

        @Input
        List<String> assemblyFormats

        @Input
        String assemblyFileName

        @TaskAction generateArchiveAssemblies() {
                logger.info 'Creating archive assemblies.'

                if (project.extensions.classworlds.hasProperty('doWithStagingDir')) {
                        project.extensions.classworlds.doWithStagingDir(stagingDir)
                }

                for (String fileFormat in assemblyFormats) {
                        ClassWorldsPluginConstants.AssemblyFormats f = null

                        try {
                                f = ClassWorldsPluginConstants.AssemblyFormats.valueOf(fileFormat.toUpperCase())
                        }
                        catch (IllegalArgumentException err) {
                                throw new InvalidUserDataException("Invalid format. Valid formats are ${ClassWorldsPluginConstants.AssemblyFormats.values()}")
                        }

                        def assemblyFile = new File(project.buildDir.absolutePath, "${assemblyFileName}.${f.name().toLowerCase()}")

                        if (f == ClassWorldsPluginConstants.AssemblyFormats.ZIP) {
                                ant.zip(destfile: assemblyFile) {
                                        zipfileset(dir:stagingDir, prefix:assemblyFileName, filemode:ClassWorldsPluginConstants.Archives.ARCHIVE_FILE_MODE)
                                }
                        }
                        else if (f == ClassWorldsPluginConstants.AssemblyFormats.TAR) {
                                ant.tar(destfile: assemblyFile) {
                                        tarfileset(dir:stagingDir, prefix:assemblyFileName, filemode:ClassWorldsPluginConstants.Archives.ARCHIVE_FILE_MODE)
                                }
                        }
                }
        }
}
