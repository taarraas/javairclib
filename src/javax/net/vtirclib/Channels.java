/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javax.net.vtirclib;
import java.util.Set;
import java.util.HashSet;
/**
 *
 * @author taras
 */

public class Channels {
    private Set<ChannelName> channels=new HashSet<ChannelName>();
    public Set<String> toStringSet() {
        return ChannelName.convert(channels);
    }
    public void add(String channelname) {
        channels.add(ChannelName.valueOf(channelname));
    }
    public void remove(String channelname) {
        channels.remove(ChannelName.valueOf(channelname));
    }
}
