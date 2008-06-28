Bouncer
---------

Author Shanbo Li (shanbo@kth.se) and Sike Huang (sikeh@kth.se)
Written for the KTH course IK2213

Version 1.6 of Sun's Java have been used [Pass Compile on Java1.5]

Before you run the bouncer, please make sure Jpcap is correctly installed on your PC.
If you have any question about installation, please refer to:
http://netresearch.ics.uci.edu/kfujii/jpcap/doc/install.html

Also notice Apache Ant is used to compile and run the bouncer, make sure you have it installed as well.
Please refer to:
http://ant.apache.org/manual/index.html



ant Compile: "ant all"
ant excute:  "ant run"


edit "program.parameters" in "build.properties" to change the parameters.
Use follow format:  
 
program.parameters=[interface] listen_ip:listen_port server_ip:server_port

*********************
please do not use port 8598~8698 or port 11240~11666 as listen_port, these ports are used by bouncer for forwarding packets to server

*********************


Please read "report.pdf" to know more about how to play around with these parameters

Here are few examples:

program.parameters=eth0 192.168.100.100 10.8.0.50
	let bouncer listern on eth0, accept all incoming packets with destionation IP 192.168.100.100
	from client, and forward those packets to IP 10.8.0.50 (in case of TCP packet, same ports are
	used in forwarding

program.parameters=192.168.100.100:8080 10.8.0.50:80
	User select the interface from a list of deviced given by bouncer,
	then bouncer accept all incoming packets with destionation socket 192.168.100.100:8080
	from client, and forward those packets to socket 10.8.0.50:80 (ICMP is not worked when ports are
	specified, use this to test TCP for instance)

program.parameters=eth0 192.168.100.100:21 10.8.0.50
	let bouncer listern on eth0, accept all incoming packets with destionation socket 192.168.100.100:21
	from client, and forward those packets to IP 10.8.0.50 with same port, a.k.a port 21
	(for example to test FTP)