export M2_HOME=/opt/maven
export PATH=$PATH:$M2_HOME/bin
mvn -Dmaven.test.skip=true jetty:run
