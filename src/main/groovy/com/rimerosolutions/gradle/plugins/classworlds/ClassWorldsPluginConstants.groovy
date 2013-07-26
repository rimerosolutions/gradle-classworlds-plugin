/*
 * Copyright 2013 Rimero Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

/**
 * ClassWorlds Plugin Constants.
 *
 * @author Yves Zoundi
 */
final class ClassWorldsPluginConstants {

        /** Plugin extension name */
        static final String CLASSWORLDS_EXTENSION_NAME = 'classworlds'

        /** Classworlds artifact ID */
        static final String CLASSWORLDS_ARTIFACT_ID = 'plexus-classworlds'

        /** ClassWorlds artifact type */
        static final String CLASSWORLDS_ARTIFACT_TYPE = 'jar'

        /** ClassWorlds artifact group id */
        static final String CLASSWORLDS_GROUP_ID = 'org.codehaus.plexus'

        /** Launcher templates classpath location */
        static final String TEMPLATES_LOCATION = 'com/rimerosolutions/gradle/plugins/classworlds/templates'

        /** Default JVM options */
        static final String DEFAULT_JVM_OPTIONS = '-Xms128m -Xmx512m'

        enum AssemblyFormats {
                ZIP, TAR
        }
        
        /** ClassWorlds task constants */
        static final class TaskSettings {
                /** Gradle build task name */
                public static final String BUILD_TASK_NAME = 'build'
                
                /** Gradle ClassWorlds plugin task name */
                public static final String CLASSWORLDS_TASK_NAME = 'classworlds'

                /** Gradle ClassWorlds Plugin task description*/
                public static final String CLASSWORLDS_TASK_DESCRIPTION = 'Generates a runnable application assembly using ClassWorlds as application launcher.'

                /** Gradle ClassWorlds task group */
                public static final String CLASSWORLDS_GROUP = 'classworlds'
        }

        /** Assembly directory file names constants */
        static class AssemblyDirNames {
                /** Staging directory name */
                public static final String STAGING = 'classworlds'

                /** Boot directory name */
                public static final String BOOT = 'boot'

                /** Lib directory name */
                public static final String LIB = 'lib'

                /** Etc directory name */
                public static final String ETC = 'etc'
                
                /** Bin directory name */
                public static final String BIN = 'bin'
        }

}