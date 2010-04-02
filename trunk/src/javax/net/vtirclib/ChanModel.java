/*
 * ChanModel.java
 *
 * Created on 25 ρεπονÿ 2008, 21:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package javax.net.vtirclib;

import java.util.Set;

/**
 *
 * @author taras
 */
public interface ChanModel {
        public Set<String> getLoggedChannels();
        public boolean isLoggedOnChannel(String channel);
        public String getTopic(String channelName);
        public Set<String> getUsersOnChannel(String ChannelName);
        public Set<String> getUsers();
        public String getNick();
}
