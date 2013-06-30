${shellHeader}
${shellComments}
java -classpath \${$appHome}/boot/${bootJarFileName} -Dclassworlds.conf=\${$appHome}/etc/classworlds.conf -Dapp.home=\${$appHome} org.codehaus.plexus.classworlds.launcher.Launcher \$*
