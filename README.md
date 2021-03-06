# Unity Network
Unity Network is a virtual network (VPN) capable to be deployed in any kind of IP network as a LAN network or over the Internet.

* It is based on a divide and conquer logic with distributed roles, behavior and decupoled network traffic from the network logic which allows it to serve a large number of host-clients from many platforms.
* It is based in software written in Java rather than hardware wich provides enchanced resilience and easy deployment, thanks to maven, to any kind of platform which may support Java.

## key-principles
The network is based in three key-principles:

* **Identification**: Each registered user or organisation may own multiple host-clients where each host-client may receive the same network address each time the device connects to the network
* **Freedom**: Each client may host any kind of service and transfer any kind of data towards any other host-client without limitations of any kind.
* **Privacy**: In order to defend its host-client privacy, the network is built with public key distribution and provides RSA and AES algorithms for authentication and confidentiality.
(If you do not want to use the encrypted version you may use the non_encrypted version from the appropriate branch)

## Reasoning
This software was build as part of my BSc Thesis in order to demonstare a live and tangible example of a better version of today's Internet. Inside the network, users may experience a much more vivid communication, the ability to share any kind of data or services between them and the ability to know each other. 

In order to learn more about the Thesis please visit the following url:
https://kostiskag.wordpress.com/2017/05/25/unity-network/

### Some feasible examples of the network's behaviour are:
In general, a host-client may perform any kind of task that is currently being done in an IP network as the Internet
**plus** that IP to IP communication is enhanced and the connected host clients may have a direct communication between them in the form of:

[someone] uses [one of his devices] to directly exchange data with [someone else's device]
* Bob may directly send a file from his laptop to David's Laptop
* Lucy may connect from her mobile phone to a social media server
* Steve may video-call Jenny from his computer to her mobile phone
* May leaves a message from her laptop to her home's noticeboard
* Bill calls Dave from his mobile to Dave's mobile

## Applications
UnityNetwork is composed by three software applications which may be found on their respective repositories:
* unitynetwork-tracker  [https://github.com/kostiskag/unitynetwork-tracker] : The tracker is responsible to keep the network authentication and identification data but does not forward any network traffic.
* unitynetwork-bluenode [you are here] : Bluenode hosts are responsible to forward the network traffic from rednode to bluenode and from bluenode to bluenode. All the bluenodes use a tracker to authenticate.
* unitynetwork-rednode  [https://github.com/kostiskag/unitynetwork-rednode] : The rednode is the host-client application which is able to transfer a host to the network and exchange traffic towards the closest bluenode.

# unitynetwork-bluenode
Bluenode hosts are responsible to forward the network traffic from rednode to bluenode and from bluenode to bluenode. All the bluenodes use a main network tracker to authenticate.

## Option A - Build from source code
In order to build this project, Java JDK 1.7 or greater and Apache Maven have to already be installed on your system.
```
git clone https://github.com/kostiskag/unitynetwork-bluenode.git
cd unitynetwork-bluenode
mvn package
```

## Option B - Download and use a pre-built version
You can download, unzip and use a pre-built version of Red Node from this url:
https://drive.google.com/file/d/0BzPrI7NjFz2SdWlrQW1NNVV2RWc/view?usp=sharing

In order to establish data integrity, you should verify the zip file's signature to be:

| Algorithm | Hash Signature |
| --- | --- |
| MD5 | 88738eafa3de851bd851d5c53d64e26a |
| SHA256 | 5dca33c145242a729d4093e64fab46b8d15ed9e85c300877be959304d25b9b56 |

## Run
```
cd target
```
Edit **bluenode.conf** file with a text editor to define the bluenode's behaviour
```
java -jar UnityNetwork_BlueNode-1.0.jar 
```

## Authorship
The source code was written by Konstantinos Kagiampakis

## License
The project's article and source code are licensed under Creative Commons Atribution 4.0 International: https://creativecommons.org/licenses/by/4.0/

You may use the source code commercially. You should provide the appropriate attribution for all the authors involved in this project.

## Looking for developers in order to deploy the platform for real use on the Internet
In order for the platform to be fully operational it needs the following todo list to be done:
* improve the control flow algorithm
* fix minor bugs
* deploy and test on the Internet

If you are interested in joining the project or to provide professional advice and guidance please send me an email.
