${shellHeader}
java -classpath \${$appHome}/boot/${bootJarFileName} -Dclassworlds.conf=\${$appHome}/etc/classworlds.conf -Dapp.home=\${$appHome} org.codehaus.classworlds.Launcher \$*
