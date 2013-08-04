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
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Main task provided by the ClassWorlds plugin.
 *
 * @author Yves Zoundi
 */
class ClassWorldsCleanupTask extends DefaultTask {
        /**
         * The task name.
         */
        static final String TASK_NAME = 'classworldsCleanupStagingArea'
        
        /**
         * The task description.
         */
        static final String TASK_DESCRIPTION = 'Cleanup staging area.'

        @InputDirectory
        /**
         * The staging area.
         */
        File stagingDir

        @TaskAction cleanupStagingArea() {
                logger.info 'Deleting staging directory'
                
                ant.delete(dir:stagingDir, includeEmptyDirs:true, quiet:true)
        }

}
