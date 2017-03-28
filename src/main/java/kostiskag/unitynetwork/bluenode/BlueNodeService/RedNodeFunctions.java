package kostiskag.unitynetwork.bluenode.BlueNodeService;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.lang.Thread.sleep;
import kostiskag.unitynetwork.bluenode.App;
import kostiskag.unitynetwork.bluenode.RunData.Instances.RedNodeInstance;

/**
 *
 * @author kostis
 */
public class RedNodeFunctions {

    static void Lease(Socket connectionSocket, String hostname, String Username, String Password) {
        PrintWriter outputWriter = null;
        RedNodeInstance RNclient = new RedNodeInstance(connectionSocket, hostname, Username, Password);
        if (RNclient.getStatus() > 0) {                        
            try {
                App.localRedNodesTable.lease(RNclient);
                RNclient.startServices();                
                
                try {
                    sleep(3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RedNodeInstance.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                outputWriter = new PrintWriter(connectionSocket.getOutputStream(), true);
                outputWriter.println("REG OK " + RNclient.getDown().getDownport() + " " + RNclient.getUp().getUpport() + " " + RNclient.getVaddress());
                App.ConsolePrint("RED NODE OK " + RNclient.getVaddress() + "/" + RNclient.getHostname() + "/" + RNclient.getUsername() + " ~ " + RNclient.getPhAddress() + ":" + RNclient.getUp().getUpport() + ":" + RNclient.getDown().getDownport());
                App.localRedNodesTable.updateTable();
                RNclient.initTerm();
                
            } catch (IOException ex) {
                Logger.getLogger(RedNodeFunctions.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                outputWriter.close();
            }
        }
    }
}
