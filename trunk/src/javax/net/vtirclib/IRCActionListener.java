/*
 * IRCActionListener.java
 *
 * Created on 16 квітня 2008, 17:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package javax.net.vtirclib;

/**
 *
 * @author taras
 */
public interface IRCActionListener{
    public static final int TOPIC_CHANGED=0, // where - channel, data - new topic
            CHANNEL_CHANGED=1,//data - "LEAVE" or "JOIN" or "USER" if user join, quit or change nick
            RAW_DATA=2,
            NICK_CHANGED=3, // where - old nick, data - new nick
            USER_MODE_CHANGED=4, // where - nick, data - parameters (+/- a/i/o/O/w) or "QUIT" or "JOIN"
            CONNECTION_TERMINATED=5,   // where - who do it (equals "server" if server),data - terminate reasone
            SERVER_CONNECTED=6; // where - server name
    public void ircActionPerformed(int actionType, String where, String data);
    
    //isYourMessage - true, if from=yourNickname
    //isChannelMessage - true, if isChannel(to)
    public void ircNewMessage(String from, String to, String data, boolean isYourMessage, boolean isChannelMessage, boolean isNotice);

}
