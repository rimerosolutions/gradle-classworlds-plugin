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

public final class ClassWorldsPluginConstants {

        static final String CLASSWORLDS_EXTENSION_NAME = "classworlds";
        static final String CLASSWORLDS_ARTIFACT_ID = "classworlds";
        static final String CLASSWORLDS_ARTIFACT_TYPE = "jar";
        static final String CLASSWORLDS_GROUP_ID = "classworlds";
        static final String TEMPLATES_LOCATION = "com/rimerosolutions/gradle/plugins/classworlds/templates";

        public static final class TaskSettings {
                public static final String BUILD_TASK_NAME = "build";
                public static final String CLASSWORLDS_TASK_NAME = "classworlds";
                public static final String CLASSWORLDS_TASK_DESCRIPTION = "Generates a runnable application assembly using ClassWorlds as application launcher.";
                public static final String CLASSWORLDS_GROUP = "classworlds";
        }

        public static class AssemblyDirNames {
                public static final String STAGING = "classworlds";
                public static final String BOOT = "boot";
                public static final String LIB = "lib";
                public static final String ETC = "etc";
                public static final String BIN = "bin";
        }

}