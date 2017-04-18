package kostiskag.unitynetwork.bluenode.socket.blueNodeService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.lang.Thread.sleep;
import kostiskag.unitynetwork.bluenode.App;
import kostiskag.unitynetwork.bluenode.RunData.instances.LocalRedNodeInstance;
import kostiskag.unitynetwork.bluenode.socket.trackClient.TrackingRedNodeFunctions;

/**
 *
 * @author kostis
 */
public class RedNodeFunctions {

    static void lease(Socket connectionSocket, BufferedReader socketReader, PrintWriter socketWriter, String hostname, String Username, String Password) {
        
    	LocalRedNodeInstance RNclient = new LocalRedNodeInstance(connectionSocket, hostname, Username, Password);
        if (RNclient.getStatus() > 0) {                        
            	
        		try {
					App.bn.localRedNodesTable.lease(RNclient);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
        		
                try {
                    sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                
                socketWriter.println("REG OK " + RNclient.getDown().getDownport() + " " + RNclient.getUp().getUpport() + " " + RNclient.getVaddress());
                App.bn.ConsolePrint("RED NODE OK " + RNclient.getVaddress() + "/" + RNclient.getHostname() + "/" + RNclient.getUsername() + " ~ " + RNclient.getPhAddress() + ":" + RNclient.getUp().getUpport() + ":" + RNclient.getDown().getDownport());
                
                //initTerm will use the session socket and will hold this thread
                RNclient.initTerm();
                //holds the thread as its statefull
                
                //after this point the thread is released and the release process follows
                //release from network
                if (App.bn.network) {
                    TrackingRedNodeFunctions.release(RNclient.getHostname());
                }
                
                //release from local red node table
                try {
					App.bn.localRedNodesTable.releaseByHostname(hostname);
				} catch (Exception e) {
					e.printStackTrace();
				}                 
        }
    }
}
