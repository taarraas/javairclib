/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javax.net.vtirclib;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author taras
 */
public class UsersOnChannel {
    private Map<ChannelName, Users> usersOnChannel = new HashMap<ChannelName, Users>();
    public boolean containsKey(String channel) {
        return usersOnChannel.containsKey(ChannelName.valueOf(channel));
    }
    public Users get(String chanelname) {
        return usersOnChannel.get(ChannelName.valueOf(chanelname));
    }
    public void add(String channelname) {
        usersOnChannel.put(ChannelName.valueOf(channelname), new Users());
    }
    public void remove(String channelname) {
        usersOnChannel.remove(ChannelName.valueOf(channelname));
    }
}
