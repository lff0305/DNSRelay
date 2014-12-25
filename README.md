DNSRelay
========

A Java based DNS Relayer to go through the GFW.

Requirement:

1. JRE/JDK 6+.
2. A VPS server that can access a DNS service.

Many domains, are blocked in China. For example, facebook.com and twitter.com.

-) If you use a DNS server in China, querying ip addresses for such domains will result a wrong ip address.

-) If you use a DNS server outside China(Google Public DNS, OpenDNS, etc), the GFW (Great Fire Wall) will hijacking the DNS Request Packages and reply to you with a fake DNS response.

As a result, you cannot get the correct ip addresses for the domain.

Current solutions :
-) Modify host name file(/etc/host, c:\Windows\System32\Drivers\etc\host).
-) Use TCP DNS protocol. But GFW started to hijack the TCP DNS packages. Or, GFW started to hijack the packages to port 53.
-) Use a VPN. But today, most VPN services out of China can not be used smoothly, because the port 1723 for PPTP, 1701 for L2TP, GRE ptotocal are all blocked, or interferenced by the firewall.

So I created this project.

This DNS Relay, has 2 parts, one is client and the other is server.

The client side runs locally, which listenes on port 53 for DNS requests from your computer.
After the client receives a DNS request, it will encrypt, and send it to the remote server.
The remote server receives the encrypted data, decrypt it, and query the DNS.
After the remote server gets the response, it will encrypt and send it back the client.
At last, the client decrypted it, and response to local computer.
