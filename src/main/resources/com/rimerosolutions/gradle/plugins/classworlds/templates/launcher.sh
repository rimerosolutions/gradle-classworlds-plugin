#
# Copyright 2013 Rimero Solutions, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

${shellHeader}
${shellComment}
DIRNAME=\$(dirname \$BASH_SOURCE)
USER_INSTALL=\$(cd \$DIRNAME/.. && pwd)
export ${appHome}=\${USER_INSTALL}
java -classpath \${$appHome}/boot/${bootJarFileName} -Dclassworlds.conf=\${$appHome}/etc/classworlds.conf -Dapp.home=\${$appHome} org.codehaus.plexus.classworlds.launcher.Launcher \$*
