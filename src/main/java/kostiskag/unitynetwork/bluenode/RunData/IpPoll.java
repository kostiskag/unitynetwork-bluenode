package kostiskag.unitynetwork.bluenode.RunData;

/**
 *
 * @author Konstantinos Kagiampakis
 */
public class IpPoll {
    
    int count;
    
    public IpPoll() {
        count = 0;
    }
    
    public synchronized int poll(){
        count++;
        return count;
    }
}
