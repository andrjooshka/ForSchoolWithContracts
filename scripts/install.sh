export M2_HOME=/opt/maven
export PATH=$PATH:$M2_HOME/bin
mvn clean install -Dmaven.test.skip=true
