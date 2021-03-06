package org.kostiskag.unitynetwork.bluenode.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Iterator;
import java.util.LinkedList;

import javax.crypto.SecretKey;

import org.kostiskag.unitynetwork.bluenode.App;
import org.kostiskag.unitynetwork.bluenode.rundata.entry.BlueNodeInstance;
import org.kostiskag.unitynetwork.bluenode.rundata.entry.RemoteRedNodeInstance;
import org.kostiskag.unitynetwork.common.utilities.SocketUtilities;

/**
 * 
 * @author Konstantinos Kagiampakis
 */
public class GlobalSocketFunctions {

	public static void sendLocalRedNodes(DataOutputStream socketWriter, SecretKey sessionKey) {
		LinkedList<String> fetched = App.bn.localRedNodesTable.buildAddrHostStringList();
        int size = fetched.size();
        try {
        	StringBuilder str = new StringBuilder();
        	str.append("SENDING_LOCAL_RED_NODES "+size+"\n");
        	Iterator<String> it = fetched.listIterator();			
	        while(it.hasNext()) {
	        	str.append(it.next()+"\n");	        	
	        }
			SocketUtilities.sendAESEncryptedStringData(str.toString(), socketWriter, sessionKey);
	    } catch (Exception e) {
			e.printStackTrace();
		}            
    }
	
	public static void getRemoteRedNodes(BlueNodeInstance bn, DataInputStream socketReader, SecretKey sessionKey) {
		try {
			String received = SocketUtilities.receiveAESEncryptedString(socketReader, sessionKey);
			String[] lines = received.split("\n+"); //split into sentences
			String[] args = lines[0].split("\\s+"); //the first sentence contains the number
			int count = Integer.parseInt(args[1]);  //for the given number read the rest sentences
	        for (int i = 1; i < count+1; i++) {        	
				args = lines[i].split("\\s+");
	            try {
	            	App.bn.blueNodeTable.leaseRRn(bn, args[0], args[1]);
				} catch (Exception e) {
					e.printStackTrace();
				}				
	        }
	   } catch (Exception e1) {
		   e1.printStackTrace();
	   }
    }
	
	/**
	 * The difference of this method from getRemoteRedNodes is that the former requests and
	 * then internally leases the returned results although this method requests but returns the 
	 * results as a built object.
	 * 
	 * @return a linked list with all the retrieved RemoteRedNodeInstance
	 */
	public static LinkedList<RemoteRedNodeInstance> getRemoteRedNodesObj(BlueNodeInstance bn, DataInputStream socketReader, SecretKey sessionKey) {
		LinkedList<RemoteRedNodeInstance> fetched = new LinkedList<RemoteRedNodeInstance>();
		try {
			String received = SocketUtilities.receiveAESEncryptedString(socketReader, sessionKey);
			String[] lines = received.split("\n+"); //split into sentences
			String[] args = lines[0].split("\\s+"); //the first sentence contains the number
			int count = Integer.parseInt(args[1]);  //for the given number read the rest sentences
	        for (int i = 1; i < count+1; i++) {        	
				args = lines[i].split("\\s+");
	            try {
	            	RemoteRedNodeInstance r =  new RemoteRedNodeInstance(args[0], args[1], bn);                    
		            fetched.add(r); 
				} catch (Exception e) {
					e.printStackTrace();
				}				
	        }
	   } catch (Exception e1) {
		   e1.printStackTrace();
	   }
	   return fetched;	
    }
}
