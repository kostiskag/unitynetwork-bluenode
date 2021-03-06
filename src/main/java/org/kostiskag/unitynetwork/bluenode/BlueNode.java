package org.kostiskag.unitynetwork.bluenode;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.concurrent.atomic.AtomicInteger;

import org.kostiskag.unitynetwork.bluenode.routing.FlyRegister;
import org.kostiskag.unitynetwork.bluenode.rundata.IpPoll;
import org.kostiskag.unitynetwork.bluenode.rundata.table.AccountTable;
import org.kostiskag.unitynetwork.bluenode.rundata.table.BlueNodeTable;
import org.kostiskag.unitynetwork.bluenode.rundata.table.LocalRedNodeTable;
import org.kostiskag.unitynetwork.bluenode.bluethreads.BlueNodeTimeBuilder;
import org.kostiskag.unitynetwork.bluenode.service.PortHandle;
import org.kostiskag.unitynetwork.bluenode.gui.MainWindow;
import org.kostiskag.unitynetwork.bluenode.service.bluenodeclient.BlueNodeSonarService;
import org.kostiskag.unitynetwork.bluenode.service.bluenodeservice.BlueNodeServer;
import org.kostiskag.unitynetwork.bluenode.service.trackclient.TrackerClient;
import org.kostiskag.unitynetwork.bluenode.service.trackclient.TrackerTimeBuilder;
import org.kostiskag.unitynetwork.common.utilities.CryptoUtilities;

/**
 * 
 * @author Konstantinos Kagiampakis
 */
public class BlueNode extends Thread{
	private static final String pre = "^BlueNode ";
	// Initial settings
	public final boolean network;
	public final String trackerAddress;
	public final int trackerPort;	
	public final int trackerMaxIdleTimeMin;
	public final String name;
	public final boolean useList;
	public final int authPort;
	public final int startPort;
	public final int endPort;
	public final int maxRednodeEntries;
	public final boolean gui;
	public final boolean soutTraffic;	
	public final boolean log;
	public final AccountTable accounts;
	public final KeyPair bluenodeKeys;
	public PublicKey trackerPublicKey;
	// tracker data
	public String echoAddress;
	// run data
	public boolean joined = false;	
	public PortHandle UDPports;	
	// timings
	// tracker
	public final int keepAliveSec = 10;
	public final int trackerCheckSec = 20;
	public final int trackerMaxIdleTime = 5;
	// bluenodes
	public final int blueNodeTimeStepSec = 20;
	public final int blueNodeCheckTimeSec = 120;
	public final int blueNodeMaxIdleTimeSec = 2*blueNodeCheckTimeSec;	
	// our most important tables
	public LocalRedNodeTable localRedNodesTable;
	public BlueNodeTable blueNodeTable;
	// objects
	public IpPoll bucket;
	public MainWindow window;
	public BlueNodeServer auth;
	public FlyRegister flyreg;
	public BlueNodeSonarService bnSornar;
	public BlueNodeTimeBuilder bnTimeBuilder;
	public TrackerTimeBuilder trackerTimeBuilder;
	// triggers
	public AtomicInteger trackerRespond = new AtomicInteger(0);

	/**
	 * This is a bluenode constructor. In a typical scenario
	 * there will be only one instance of this class
	 * accessible from App.bn . However in cases of network testing
	 * it is very useful to be in a position to start multiple bluenodes
	 * this is why a bluenode is an object.
	 * 
	 * @param network
	 * @param trackerAddress
	 * @param trackerPort
	 * @param name
	 * @param useList
	 * @param authPort
	 * @param startPort
	 * @param endPort
	 * @param maxRednodeEntries
	 * @param gui
	 * @param soutTraffic
	 * @param log
	 * @param accounts
	 * @param bluenodeKeys
	 * @param trackerPublicKey
	 */
	public BlueNode(
		boolean network,
		String trackerAddress, 
		int trackerPort, 
		int trackerMaxIdleTimeMin,
		String name,
		boolean useList,
		int authPort,
		int startPort,
		int endPort,
		int maxRednodeEntries,
		boolean gui,
		boolean soutTraffic,        
		boolean log,
		AccountTable accounts,
		KeyPair bluenodeKeys,
		PublicKey trackerPublicKey
	) {
		this.network = network;
		this.trackerAddress = trackerAddress;
		this.trackerPort = trackerPort;
		this.trackerMaxIdleTimeMin = trackerMaxIdleTimeMin;
		this.name = name;
		this.useList = useList;
		this.authPort = authPort;
		this.startPort = startPort;
		this.endPort = endPort;
		this.maxRednodeEntries = maxRednodeEntries;
		this.gui = gui;
		this.soutTraffic = soutTraffic;
		this.log = log;
		this.accounts = accounts;
		this.bluenodeKeys = bluenodeKeys;
		this.trackerPublicKey = trackerPublicKey;
	}
	
	@Override
	public void run() {
		super.run();
		System.out.println(pre + "started BlueNode at thread " + Thread.currentThread().getName());
		
		/*
		 *  1. gui goes first to verbose the following on console
		 */
		if (gui) {
			window = new MainWindow();
			window.setVisible(true);
			window.setBlueNodeInfo();
		}
		
		//rsa public key
		ConsolePrint("Your public key is:\n" + CryptoUtilities.bytesToBase64String(bluenodeKeys.getPublic().getEncoded()));
		
		/*
		 *  2. Initialize table
		 */
		UDPports = new PortHandle(startPort, endPort);
		localRedNodesTable = new LocalRedNodeTable(maxRednodeEntries);
		if (network) {
			blueNodeTable = new BlueNodeTable();
		}

		/*
		 *  3. Initialize auth server 
		 *  
		 *  The service to receive responses from RBNs RNs Tracker
		 */
		auth = new BlueNodeServer(authPort);
		auth.start();
		
		/* 
		 * 4. Initialize sonar
		 * 
		 * sonarService periodically checks the remote BNs associated as clients
		 * whereas the timeBuilder keeps track of the remote BNs associated as servers
		 * 
		 */
		if (network) {
			bnSornar = new BlueNodeSonarService(blueNodeCheckTimeSec);
			bnSornar.start();
		}

		/* 
		 * 5. Initialize Register On The Fly
		 * 
		 *  when a packet heading to an unknown destination is received
		 *  the FlyReg may do all the tasks in order to dynamically build 
		 *  the path from this BN to a remote BN where the target RN exists,
		 *  unknown router the one that manages new hosts new
		 *  FlyReg is meaningful to work only in a network.
		 *  
		 */
		if (network) {
			flyreg = new FlyRegister();
			flyreg.start();
		}

		/*
		 *  6. lease with the network, use a predefined user's list or dynamically allocate
		 *  virtual addresses to connected RNs
		 */
		if (network) {
			try {
				if (joinNetwork()) {
					joined = true;
					trackerTimeBuilder = new TrackerTimeBuilder();
					trackerTimeBuilder.start();
				} else {
					ConsolePrint("This bluenode is not connected in the network.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				die();
			}
		} else if (!useList) {
			bucket = new IpPoll();
			ConsolePrint("WARNING! BLUENODE DOES NOT USE EITHER NETWORK NOR A USERLIST\nWHICH MEANS THAT ANYONE WHO KNOWS THE BN'S ADDRESS AND IS PHYSICALY ABLE TO CONNECT CAN LOGIN");
		}
	}

	public void ConsolePrint(String message) {		
		System.out.println(message);
		if (gui) {
			window.ConsolePrint(message);
		}
		if (log) {
			App.writeToLogFile(message);
		}
	}

	/**
	 *  Prints a message to traffic console. 
	 *  
	 *  @param messageType ~ 0 keep alive, 1 pings, 2 acks, 3 routing
	 *  @param hostType ~ 0 reds, 1 blues
	 */
	public void TrafficPrint(String message, int messageType, int hostType) {
		if (gui) {
			window.TrafficPrint(message, messageType, hostType);
		} else if (soutTraffic) {
			System.out.println(message);
		}
	}

	public boolean joinNetwork() throws Exception {
		if (trackerPublicKey == null) {
			ConsolePrint("You do not have an availlable public key for the given tracker.");
			ConsolePrint("In order to download the key press the Collect Tracker Key button and follow the guide.");
			ConsolePrint("After you have a Public Key for the provided tracker you may restart the bluenode.");
			return false;
		} else if (network && !name.isEmpty() && authPort > 0 && authPort <= 65535) {
			TrackerClient tr = new TrackerClient();			
			boolean leased = tr.leaseBn(authPort);
			if (tr.isConnected() && leased) {
				ConsolePrint("^SUCCESFULLY REGISTERED WITH THE NETWORK");
				return true;
			} else {
				ConsolePrint("^FAILED TO REGISTER WITH THE NETWORK");
				return false;
			}
		} else {
			throw new Exception("bad credentials");
		}
	}

	public void leaveNetworkAndDie() throws Exception {
		if (joined) {
			//release from tracker
			TrackerClient tr = new TrackerClient();	
			tr.releaseBn();			
			//release from bns
			blueNodeTable.sendKillSigsAndReleaseForAll();
			joined = false;
			trackerTimeBuilder.Kill();
			die();
		} else {
			throw new Exception("called leaveNetwork whithout join first.");
		}
	}
	
	public void leaveNetworkAfterRevoke() throws Exception {
		if (joined) {
			//release from bns
			blueNodeTable.sendKillSigsAndReleaseForAll();
			joined = false;
			trackerTimeBuilder.Kill();
			die();
		} else {
			throw new Exception("called leaveNetwork whithout join first.");
		}
	}

	public void die() {
		App.bn.ConsolePrint("Blue Node "+name+" is going to die.");
		System.exit(1);
	}
}
