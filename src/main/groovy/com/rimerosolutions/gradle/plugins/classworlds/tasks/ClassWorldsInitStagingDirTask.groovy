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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Main task provided by the ClassWorlds plugin.
 *
 * @author Yves Zoundi
 */
class ClassWorldsInitStagingDirTask extends DefaultTask {
        /**
         * The task name.
         */
        static final String TASK_NAME = 'classworldsInitStaginDir'
        
        /**
         * The task description.
         */
        static final String TASK_DESCRIPTION = 'Initialize staging directory layout.'

        @OutputDirectory
        /**
         * The staging folder.
         */
        File stagingDir

        @TaskAction generateStagingDirLayout() {
                logger.info 'Creating staging directory layout'
                
                def bootDir    = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.BOOT)
                def libDir     = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.LIB)
                def etcDir     = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.ETC)
                def binDir     = new File(stagingDir.absolutePath, ClassWorldsPluginConstants.AssemblyDirNames.BIN)
                
                generateStagingDirectories(stagingDir, bootDir, libDir, etcDir, binDir)
        }

        protected void generateStagingDirectories(File[] dirs) {
                dirs.each { File dir ->
                        ant.mkdir(dir: dir)
                }
        }
}
