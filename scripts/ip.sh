cp ipt /etc/my-iptables.rules
/etc/my-iptables.rules
cd /etc/network/if-pre-up.d
rm iptablesload
touch iptablesload
echo '#!/bin/sh'>>iptablesload
echo '/sbin/iptables-restore < /etc/my-iptables.rules'>>iptablesload
echo 'exit 0'>>iptablesload

cd ../if-post-down.d/
rm iptablessave
touch iptablessave
echo '#!/bin/sh'>>iptablessave
echo '/sbin/iptables-save -c > /etc/my-iptables.rules'>>iptablessave
echo 'if [ -f /etc/iptables.downrules ]; then'>>iptablessave
echo '   /sbin/iptables-restore < /etc/iptables.downrules'>>iptablessave
echo 'fi'>>iptablessave
echo 'exit 0'>>iptablessave

chmod +x /etc/network/if-post-down.d/iptablessave
chmod +x /etc/network/if-pre-up.d/iptablesload

