# User management
sudo useradd --home /home/glassfish --system --shell /bin/bash glassfish
sudo groupadd glassfishadm
sudo usermod -a -G glassfishadm glassfish,root

# IP Tables 
./ip.sh
wait

# Set up Java
sudo apt-get remove openjdk-6-jre openjdk-6-jdk
sudo apt-get install openjdk-7-jre openjdk-7-jdk
sudo apt-get autoremove

echo JAVA_HOME=/usr/lib/jvm/java-7-openjdk>>/etc/environment
echo AS_JAVA=/usr/lib/jvm/java-7-openjdk>>/etc/environment

# Set up glassfish
sudo apt-get install unzip
su glassfish
cd /home/glassfish/
mkdir downloads
cd downloads
wget http://download.java.net/glassfish/3.1.2.2/release/glassfish-3.1.2.2.zip
unzip glassfish-3.1.2.2.zip
mv glassfish3/* ..
mv glassfish3/.org.opensolaris,pkg ..
exit

sudo chgrp -R glassfishadm /home/glassfish
sudo chown -R glassfish /home/glassfish
sudo chmod -R ug+rwx /home/glassfish/bin/
sudo chmod -R ug+rwx /home/glassfish/glassfish/bin/
sudo chmod -R o-rwx /home/glassfish/bin/
sudo chmod -R o-rwx /home/glassfish/glassfish/bin/

mv init.sh /etc/init.d/glassfish

sudo chmod a+x /etc/init.d/glassfish
sudo update-rc.d glassfish defaults

# Glassfish configure
sudo su glassfish
cd /home/glassfish
bin/asadmin change-master-password --savemasteropassword=true
bin/asadmin start-domain
bin/asadmin change-admin-password
bin/asadmin login
bin/asadmin stop-domain

