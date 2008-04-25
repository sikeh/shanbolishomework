WebServer
---------

Author Mikael Rudholm (rudholm@kth.se)
Written for the KTH course 2E1633

Version 1.5 of Sun's Java have been used

Compile all .java files
and execute:
    
    java MultiThreadedServer

The server listens on all available interfaces on the default port 8888.

Connect for example to http://localhost:8888/
after the server is started.

Server classes
--------------
MultiThreadedServer.java	Listens for new incomming connections,
				starts a seperat HandlerThread to take care of each new connection

HandlerThread.java		Fairly general class to handle string processing of incoming
				and outgoing data.

ServerHandlerProtocol.java	Server logic. Gets input from HandlerThread and returns output.


Data structures
---------------
HTTPRequest.java	Contains a HTTP request
WebFormData.java	Contains a pair of webform data i.e. value=name
