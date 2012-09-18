nds here change the file at
# /home/glassfish/glassfish/domains/domain1/config/domain.xml
 
#first we have to start Glassfish
/home/glassfish/bin/asadmin start-domain domain1
 
# enable https for remote access to admin console
# requests to http://xxx:4848 are redirected to https://xxx:4848
/home/glassfish/bin/asadmin set server-config.network-config.protocols.protocol.admin-listener.security-enabled=true
/home/glassfish/bin/asadmin enable-secure-admin
 
#change JVM Options
#list current jvm options
/home/glassfish/bin/asadmin list-jvm-options
#now start setting some important jvm settings
/home/glassfish/bin/asadmin delete-jvm-options -- -client
/home/glassfish/bin/asadmin create-jvm-options -- -server
/home/glassfish/bin/asadmin delete-jvm-options -- -Xmx512m
/home/glassfish/bin/asadmin create-jvm-options -- -Xmx2048m
/home/glassfish/bin/asadmin create-jvm-options -- -Xms1024m
#get rid of http header field value "server" (Glassfish obfuscation)
/home/glassfish/bin/asadmin create-jvm-options -Dproduct.name=""
#restart to take effect
/home/glassfish/bin/asadmin stop-domain domain1
/home/glassfish/bin/asadmin start-domain domain1
#what jvm options are configured now?
/home/glassfish/bin/asadmin list-jvm-options
 
#disable sending x-powered-by in http header (Glassfish obfuscation)
/home/glassfish/bin/asadmin set server.network-config.protocols.protocol.http-listener-1.http.xpowered-by=false
/home/glassfish/bin/asadmin set server.network-config.protocols.protocol.http-listener-2.http.xpowered-by=false
/home/glassfish/bin/asadmin set server.network-config.protocols.protocol.admin-listener.http.xpowered-by=false
 
#we are done with user glassfish
exit
