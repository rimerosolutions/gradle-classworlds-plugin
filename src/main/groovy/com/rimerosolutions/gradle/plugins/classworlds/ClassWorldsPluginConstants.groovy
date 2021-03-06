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

        /** Plugin extension name. */
        static final String CLASSWORLDS_EXTENSION_NAME = 'classworlds'

        /** Classworlds artifact ID. */
        static final String CLASSWORLDS_ARTIFACT_ID = 'plexus-classworlds'

        /** ClassWorlds artifact type. */
        static final String CLASSWORLDS_ARTIFACT_TYPE = 'jar'

        /** ClassWorlds artifact group id. */
        static final String CLASSWORLDS_GROUP_ID = 'org.codehaus.plexus'

        /** Launcher templates classpath location. */
        static final String TEMPLATES_LOCATION = 'com/rimerosolutions/gradle/plugins/classworlds/templates'

        /** Default JVM options. */
        static final String DEFAULT_JVM_OPTIONS = '-Xms128m -Xmx512m'        

        /** Assembly file formats */
        enum AssemblyFormats {
                /** Zip format. */
                ZIP,

                /** Tar format. */
                TAR
        }

        /** Scripts constants. */
        static final class ScriptConstants {
                /** Launcher script headers. */
                static final List<String> SHELL_HEADERS = ['#!/usr/bin/env bash', '']

                /** Launcher script comments for branding. */
                static final List<String> BRANDING_COMMENTS = ['#Generated by Gradle ClassWorlds Plugin', '@REM Generated by Gradle ClassWorlds Plugin']

                /** Launcher template names. */
                static final List<String> LAUNCHER_TEMPLATES_FILENAMES = ['launcher_unix.txt', 'launcher_windows.txt']

                /** Windows launcher script filename. */
                static final String LAUNCHER_SCRIPT_FILENAME_WINDOWS = 'run.bat'
                
                /** UNIX Launcher script filename. */
                static final String LAUNCHER_SCRIPT_FILENAME_UNIX = 'run.sh'
                
                /** Launcher mapping file names from template names. */
                static final List<String> LAUNCHER_TEMPLATES_FILENAMES_MAPPING = [LAUNCHER_SCRIPT_FILENAME_UNIX, LAUNCHER_SCRIPT_FILENAME_WINDOWS]
                
                /** Default Application home system property */
                static final String DEFAULT_APP_HOME_SYSPROPERTY = 'APP_HOME'
        }

        /** Archives constants. */
        static final class Archives {
                /** The archives file permissions. */
                static final String ARCHIVE_FILE_MODE = '755'
                
                /** Zip files extension. */
                static final String EXTENSION_ZIP = '.zip'

                /** Jar files extension. */
                static final String EXTENSION_JAR = '.jar'

                /** Recursive wildcard for zip files. */
                static final String WILDCARD_ZIP = '**/*.zip'

                /** Recursive wildcard for jar files. */
                static final String WILDCARD_JAR = '**/*.jar'
        }
        
        /** ClassWorlds task constants. */
        static final class TaskSettings {
                /** Gradle build task name. */
                static final String BUILD_TASK_NAME = 'build'
                
                /** Gradle ClassWorlds plugin task name. */
                static final String CLASSWORLDS_TASK_NAME = 'classworlds'

                /** Gradle ClassWorlds Plugin task description. */
                static final String CLASSWORLDS_TASK_DESCRIPTION = 'Generates a runnable application assembly using ClassWorlds as application launcher.'

                /** Gradle ClassWorlds task group. */
                static final String CLASSWORLDS_GROUP = 'classworlds'
        }

        /** Assembly directory file names constants. */
        static final class AssemblyDirNames {
                /** Staging directory name. */
                static final String STAGING = 'classworlds'

                /** Boot directory name. */
                static final String BOOT = 'boot'

                /** Lib directory name. */
                static final String LIB = 'lib'

                /** Etc directory name. */
                static final String ETC = 'etc'
                
                /** Bin directory name. */
                static final String BIN = 'bin'                
        }

}