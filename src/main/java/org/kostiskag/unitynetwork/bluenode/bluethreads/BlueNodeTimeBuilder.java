package org.kostiskag.unitynetwork.bluenode.bluethreads;

import java.util.concurrent.atomic.AtomicBoolean;
import org.kostiskag.unitynetwork.bluenode.App;
import org.kostiskag.unitynetwork.bluenode.AppLogger;
import org.kostiskag.unitynetwork.bluenode.rundata.entry.BlueNodeInstance;

/**
 *
 * @author Konstantinos Kagiampakis
 */
public class BlueNodeTimeBuilder extends Thread {

    private final String pre;
    private final BlueNodeInstance bn;
    private final int buildStepSec;
    private final int maxWaitTimeSec;
    private AtomicBoolean kill = new AtomicBoolean(false);
    
    public BlueNodeTimeBuilder(BlueNodeInstance bn, int buildStepSec, int maxWaitTimeSec) {
    	this.bn = bn;
    	this.pre = "^BlueNodeTimeBuilder "+bn.getName()+" ";
    	this.buildStepSec = buildStepSec;
    	this.maxWaitTimeSec = maxWaitTimeSec;
    }
    
    @Override
    public void run() {

        AppLogger.getInstance().consolePrint(pre+"JUST STARTED");
        while (!kill.get()){
            AppLogger.getInstance().consolePrint(pre+"WAITING");
            try {
                sleep(buildStepSec*1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (kill.get()) break;
            int passedTime = bn.idleTime.addAndGet(buildStepSec*1000);
            if (passedTime > maxWaitTimeSec*1000) {
                AppLogger.getInstance().consolePrint(pre+"BlueNode is not responding releasing from the local bn table");
            	try {
					App.bn.blueNodeTable.releaseBn(bn.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        }
        AppLogger.getInstance().consolePrint(pre+"ENDED");
    }

    public void Kill() {
        kill.set(true);
    }       
}
