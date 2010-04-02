/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javax.net.vtirclib;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author taras
 */
public class Topics {
    private Map<ChannelName, String> topics = new TreeMap<ChannelName, String>();    
    public void put(String channelName, String topic) {
        topics.put(ChannelName.valueOf(channelName), topic);
    }
    public void remove(String channelName) {
        topics.remove(ChannelName.valueOf(channelName));
    }
    public String get(String channelName) {
        return topics.get(ChannelName.valueOf(channelName));
    }
}
