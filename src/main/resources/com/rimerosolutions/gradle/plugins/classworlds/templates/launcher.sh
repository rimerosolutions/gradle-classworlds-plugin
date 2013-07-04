${shellHeader}
${shellComment}
DIRNAME=\$(dirname \$BASH_SOURCE)
USER_INSTALL=\$(cd \$DIRNAME/.. && pwd)
export ${appHome}=\${USER_INSTALL}
java -classpath \${$appHome}/boot/${bootJarFileName} -Dclassworlds.conf=\${$appHome}/etc/classworlds.conf -Dapp.home=\${$appHome} org.codehaus.plexus.classworlds.launcher.Launcher \$*
