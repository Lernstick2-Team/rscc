# RSCC - Remote Support Connection Client

RSCC is a remote support application to establish a connection between two clients and start a VNC-Connection, in multiple ways.

Key features:
 - Uses VNC server and viewer
 - Client-to-Client (P2P) connection using ICE and STUN and 
 - RUDP to add reliability to the transmission of UDP-packages 
   - Fallback solution using a relay-server
 - Runs on Linux only, tested with Lernstick
 - Open Source

The use cases always include a *requester* who seeks support and his *supporter*. The scenarios are the following:
 - Supporter has public IP 
    - Supporter starts vncviewer in listening mode ("Start Service")
    - Supporter is either in the addressbook (predefined) or lets the requester know his details (IP and port VNCViewer listens on)
    - Requester starts VNC server in connection mode (Reverse VNC)
    
 - Both clients (supporter and requester) are behind a NAT
    - The clients connects via SSH to the relay server. 
    - The relay server (keyserver) is used to generate a *key* (9-digit key) and transmits this key to the client.
    - The requester sends this key to his supporter. 
    - The supporter enters the received key
    -  run the ICE-protocol (ice4j) and try to establish a direct connection using STUN/UDP hole punching
    - If ICE is successful, the connection is tunneled over RUDP (some kind of TCP over UDP), which results in a direct (P2P) connection
    - If no direct connection is possible, the realy server is used for fallback and will route the traffic over the already existing TCP connection.
  

## Detailed Protocol Description
 - ICE (Interactive Connectivity Establishment) 
    - [ICE](https://tools.ietf.org/html/rfc5245) is a protocol which helps establishing connections between users. 
    - ICE uses STUN (Session Traversal Utilities for NAT [RFC 3489](https://www.ietf.org/rfc/rfc3489.txt), [RFC 5389](https://tools.ietf.org/html/rfc5389)) to find out the own networks public IP-address
    - After a client has sucessfully received its own routers public IP-address it 
      gathers all possible communication-candidates and generates an SDP (Session Description Protocol)
    - Both clients exchange this SDP and parse the content of it.
    - Both clients execute connectivity-tests trying to reach the remote machine. 
    
 - [RUDP](https://sourceforge.net/projects/rudp/)-Proxy
    - RUDP is a protocol which ensures reliable package transmission over UDP.
    - The RUDP-Proxy is a two-way proxy which forwards VNC traffic from localhost to the remote machine. 
    - Example: VNC-Server is listening on port 5900, VNC-Viewer connects to port 2601 localhost via TCP.
      The proxy forwards the incoming request on port 2601 to the remote machine to port 5050 via RUDP
      The proxy on the remote machine takes the incoming request and forwards it to the VNC Server on port 5900.
    - Vice versa
      
  ```         
 +--------------------------+               +----------------------------+
 | VNC Requester            |               |             VNC Supporter  |
 |  +-------------+         |               |           +--------------+ |
 |  |             | 5900    |               |           |              | |
 |  |             <-----+   |               |     +-----+              | |
 |  |  VNC Server |     |   |               |     |     |  VNC Viewer  | |
 |  |             |     |   |               |     |     |              | |
 |  |             |     |   |               | TCP |     |              | |
 |  +-------------+     |TCP|               |     |     +--------------+ |
 |                      |   |               |     |                      |
 |  +-------------+     |   |               |     |     +--------------+ |
 |  |             |-----+   |               |     | 2601|              | |
 |  |             |         |               |     +----->              | |
 |  |  Proxy      |         |               |           |  Proxy       | |
 |  |             | 5050    +     RUDP      +     5050  |              | |
 |  |             <-------------------------------------|              | |
 |  +-------------+         +               +           +--------------+ |
 |                          |               |                            |
 |                          |               |                            |
 |                          |               |                            |
 |                          |               |                            |
 +--------------------------+               +----------------------------+   
```      
      
## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

- Working Linux installation
  - preferably latest [Lernstick build](http://lernstick.ch/releases/lernstick_debian8_latest.iso)
- Java IDE of your choice (e.g. IntelliJ Idea, Eclipse, NetBeans, ...)
- Most recent []Java 8 JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- VNC server (x11vnc)
- VNC viewer (vncviewer)
- Python
- Maven
- Access to a running Server (use this [Docker image](https://hub.docker.com/r/jpduloch/p2p/) based on this [repository](https://github.com/jpduloch/p2p))


### Supported Systems

| Linux        | 
| -------------| 

| System        | Support       | Comment       |
| ------------- | ------------- | ------------- |
|  [Lernstick build](http://lernstick.ch/releases/lernstick_debian8_latest.iso)  | ✔  |  |
|  Arch Linux  | ✔  | Special dependencies must be installed |



| MAC OS       | 
| -------------| 

| System        | Support       | Comment       |
| ------------- | ------------- | ------------- |
| OSX 10.12.5   | ✘             |               |




| Windows       | 
| ------------- | 

| System        | Support       | Comment       |
| ------------- | ------------- | ------------- |
| Win 10        | ✘             |               |

```
Give examples
```

### Installing

Seting up the Development-Environment
1. Make sure you have all the prerequisites from above
2. Clone repository
3. Open project in Java IDE
4. Import pom.xml as Maven project and download all dependencies
5. Run mvn package
6. Run the application

## Install application only
You can either create a debian and/or jar package with maven. Alternatively you can download an upcoming version from the imedias servers or the [this github repository](https://github.com/Kennox/rscc/releases).
The ".jar"-file can be run with the following code:
```
sudo java -jar rsccfx.jar
```

To install the debian packge (recommend!) you can run the following command in the respective directory:

```
sudo dpkg -i rsccfx.deb
```
This will create all the necessary files on your local machine (e.g. desktop entry and default imedias supporters).

End with an example of getting some data out of the system or using it for a little demo

## Settings & Recommendations
Standard settings try to establish a direct connection between clients over ICE. 
This can be disabled by activating the setting "force server mode".
If any of the clients activates this setting, ICE process will be skipped.
The connection over the server might be faster. While using direct connection, it is recommended to use the
BGR 233 mode to reduce traffic and increase speed.
View only restricts the VNC protocoll to view only.

## Expertsettings
Keyserver IP address: Address of the server which generates the key and matches up clients.
Keyserver HTTP-Port: Communication Port of Keyserver
VNC Port: Port on which the Vnc-Server runs
ICE Port: Port over which the direct traffic is sent and received
UDP Packet Size: max Size of UDP-packets
Proxy Port: Redirection of VNC-Packets to this port.
Stunserver Port: try to reach Stun-Server over this port.
StunServers: List of potential Stun-Servers.


## Running the tests

The whitebox tests are being carried out using JUnit 4 and Mockito. Therefore you can run them in your favorite IDE or by using Maven.
````
$ mvn package
````

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

To ensure proper coding style, checkstyle enforces the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). Any checkstyle error in the rsccfx package will result into the build failing.
The checkstyle configuration file is located in the path ```config/checkstyle.xml``` and is based on the latest commit of the [official checkstyle implementation](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml) of the Google Java Style Guide.

To check the code for checkstyle violations, run:
```
mvn checkstyle:check
```

## Deployment

To use the RSCC you need a server running the Docker Image.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

 - 

## Authors

See also the list of [contributors](https://github.com/Kennox/rscc/contributors) who participated in this project.

## License

GPL

## Acknowledgments

* RUDP: https://sourceforge.net/projects/rudp/
* Proxy Server: http://www.java2s.com/Code/Java/Network-Protocol/Asimpleproxyserver.htm
* Ice4j https://github.com/jitsi/ice4j#readme


