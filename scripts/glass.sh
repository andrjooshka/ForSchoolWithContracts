sudo apt-get install unzip
 
#change to home dir of glassfish
mkdir /home/glassfish
cd /home/glassfish/
rm -r *
 
#create new directory if not already available
mkdir downloads
 
#go to the directory we created
cd /home/glassfish/downloads/
 
#download Glassfish and unzip
wget http://download.java.net/glassfish/3.1.2.2/release/glassfish-3.1.2.2.zip
unzip glassfish-3.1.2.2.zip
 
#move the relevant content to home directory
ads

#go to the directory we created
cd /home/glassfish/downloads/

#download Glassfish and unzip
wget http://download.java.net/glassfish/3.1.2.2/release/glassfish-3.1.2.2.zip
unzip glassfish-3.1.2.2.zip

#move the relevant content to home directory
mv /home/glassfish/downloads/glassfish3/* /home/glassfish/
#if something has not been moved, then move it manually, i.e.:
mv /home/glassfish/downloads/glassfish3/.org.opensolaris,pkg /home/glassfish/.org.opensolaris,pkg

#change group of glassfish home directory to glassfishadm
sudo chgrp -R glassfishadm /home/glassfish

#just to make sure: change owner of glassfish home directory to glassfish
sudo chown -R glassfish /home/glassfish

#make sure the relevant files are executable/modifyable/readable for owner and group
sudo chmod -R ug+rwx /home/glassfish/bin/
sudo chmod -R ug+rwx /home/glassfish/glassfish/bin/

#others are not allowed to execute/modify/read them
sudo chmod -R o-rwx /home/glassfish/bin/
sudo chmod -R o-rwx /home/glassfish/glassfish/bin/

mv /home/glassfish/downloads/glassfish3/* /home/glassfish/
#if something has not been moved, then move it manually, i.e.:
mv /home/glassfish/downloads/glassfish3/.org.opensolaris,pkg /home/glassfish/.org.opensolaris,pkg
 
#change group of glassfish home directory to glassfishadm
sudo chgrp -R glassfishadm /home/glassfish
 
#just to make sure: change owner of glassfish home directory to glassfish
sudo chown -R glassfish /home/glassfish
 
#make sure the relevant files are executable/modifyable/readable for owner and group
sudo chmod -R ug+rwx /home/glassfish/bin/
sudo chmod -R ug+rwx /home/glassfish/glassfish/bin/
 
#others are not allowed to execute/modify/read them
sudo chmod -R o-rwx /home/glassfish/bin/
sudo chmod -R o-rwx /home/glassfish/glassfish/bin/
