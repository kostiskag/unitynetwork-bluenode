package kostiskag.unitynetwork.bluenode.blueThreads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import kostiskag.unitynetwork.bluenode.App;
import kostiskag.unitynetwork.bluenode.Routing.packets.IPv4Packet;
import kostiskag.unitynetwork.bluenode.Routing.packets.UnityPacket;
import kostiskag.unitynetwork.bluenode.gui.*;
import kostiskag.unitynetwork.bluenode.RunData.instances.BlueNodeInstance;
import kostiskag.unitynetwork.bluenode.redThreads.RedlSend;

/**
 *
 * @author kostis
 */
public class BlueSend extends Thread {

    private final String pre;
    private final BlueNodeInstance blueNode;
    private final boolean isServer;
    //connection
    private int serverPort;
    private int portToSend;    
    private InetAddress blueNodePhAddress;
    private DatagramSocket socket;    
    //triggers
    private boolean didTrigger = false;
    private AtomicBoolean kill = new AtomicBoolean(false);  

    /**
     * This is the server constructor
     * It builds a socket based on a port collected from the available 
     * port pool
     */
    public BlueSend(BlueNodeInstance blueNode) {    	
    	this.isServer = true;
    	this.blueNode = blueNode;
    	this.pre = "^BlueSend "+blueNode.getName()+" ";
        this.blueNodePhAddress = blueNode.getPhaddress();
        this.serverPort = App.bn.UDPports.requestPort();        
    }
    
    /**
     * This is the client constructor
     * It collects a port to send from the auth
     */
    public BlueSend(BlueNodeInstance blueNode, int portToSend) {
    	this.isServer = false;
		this.blueNode = blueNode;
		this.pre = "^BlueSend " + blueNode.getName() + " ";
		this.blueNodePhAddress = blueNode.getPhaddress();		
		//when in client, port to send may be collected from auth
		this.portToSend = portToSend;
	}
    
    public int getServerPort() {
    	if (isServer) {
    		return serverPort;
    	} else {
    		return 0;
    	}
    }    
    
    public int getPortToSend() {
		return portToSend;
	}
    
    public BlueNodeInstance getBlueNode() {
		return blueNode;
	}
    
    public boolean isKilled() {
    	return kill.get();
    }

    @Override
    public void run() {
    	if (isServer) {
    		buildServer();
    	} else {
    		buildClient();
    	}
    	
    	App.bn.ConsolePrint(pre + "STARTED AT " + Thread.currentThread().getName());
    	
        while (!kill.get()) {        	
        	byte data[];
            try {
                data = blueNode.getQueueMan().poll();
            } catch (java.lang.NullPointerException ex1) {
                continue;
            } catch (java.util.NoSuchElementException ex) {
                continue;
            } catch (Exception e) {
            	e.printStackTrace();
            	break;
            }

            DatagramPacket sendUDPPacket = new DatagramPacket(data, data.length, blueNodePhAddress, portToSend);
            try {
                socket.send(sendUDPPacket);
                if (UnityPacket.isUnity(data)) {
					if (UnityPacket.isKeepAlive(data)) {
						//keep alive
						App.bn.TrafficPrint(pre +"KEEP ALIVE SENT", 0, 1);
					} else if (UnityPacket.isUping(data)) {
						//blue node uping!
						App.bn.TrafficPrint(pre + "UPING SENT", 1, 1);
					} else if (UnityPacket.isDping(data)) {
						//blue node dping!
						App.bn.TrafficPrint(pre + "DPING SENT", 1, 1);
					} else if (UnityPacket.isAck(data)) {
						try {
							App.bn.TrafficPrint(pre + "ACK-> "+UnityPacket.getDestAddress(data)+" SENT", 2, 1);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (UnityPacket.isMessage(data)) {
						App.bn.TrafficPrint(pre + "MESSAGE SENT", 3, 1);
					}
				} else if (IPv4Packet.isIPv4(data)) {
					App.bn.TrafficPrint(pre + "IPV4 SENT", 3, 1);
				}
                if (!didTrigger) {
                	if (App.bn.gui) {
                		MainWindow.jCheckBox6.setSelected(true);
                	}
                	didTrigger = true;
                }
            } catch (java.net.SocketException ex1) {
                App.bn.ConsolePrint(pre + " SOCKET ERROR");
                break;
            } catch (IOException ex) {
                App.bn.ConsolePrint(pre + "IO ERROR");
                break;
            }
        }
        socket.close();
        if (isServer) {
        	App.bn.UDPports.releasePort(serverPort);
        }
        App.bn.ConsolePrint(pre + "ENDED");
    }
    
    public void kill() {
        kill.set(true);
        socket.close();
    }  

    private void buildClient() {
    	App.bn.ConsolePrint(pre+"building client.");
		socket = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException ex) {
			blueNode.getQueueMan().clear();
			App.bn.ConsolePrint(pre + "socket could not be opened");
			ex.printStackTrace();
		}
	}

	private void buildServer() {
		App.bn.ConsolePrint(pre+"building server.");
        try {
            socket = new DatagramSocket(serverPort);
        } catch (SocketException ex) {
        	App.bn.ConsolePrint(pre + "socket could not be opened");
        	ex.printStackTrace();
            return;
        }
        try {
            socket.setSoTimeout(10000);
        } catch (SocketException ex) {
            ex.printStackTrace();
            return;
        }

        byte[] data = new byte[2048];
        DatagramPacket receivedUDPPacket = new DatagramPacket(data, data.length);
        try {
            socket.receive(receivedUDPPacket);
        } catch (java.net.SocketTimeoutException ex) {
            App.bn.ConsolePrint(pre +"FISH SOCKET TIMEOUT");
            return;
        } catch (java.net.SocketException ex) {
            App.bn.ConsolePrint(pre + "FISH SOCKET CLOSED, EXITING");
            return;
        } catch (IOException ex) {
            Logger.getLogger(RedlSend.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        //when in server, port to send may be found when a client sends a packet
        portToSend = receivedUDPPacket.getPort();
        blueNodePhAddress = receivedUDPPacket.getAddress();		
	}

}
