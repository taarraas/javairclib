/*
 * IRCActionPerformer.java
 *
 * Created on 24 ρεπονÿ 2008, 10:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package javax.net.vtirclib;

import java.util.Vector;

/**
 *
 * @author taras
 */
public class IRCActionPerformer {
    Vector<IRCActionListener> ircActionListeners=new Vector<IRCActionListener>(0);
    synchronized protected  void addIRCActionListener(IRCActionListener l) {
        ircActionListeners.add(l);        
    }
    synchronized protected void removeIRCActionListener(IRCActionListener l) {
        ircActionListeners.remove(l);
    }    
    synchronized protected void performIRCAction(int actionType, String where, String data) {
        try {
            for (IRCActionListener elem : ircActionListeners) {
                elem.ircActionPerformed(actionType, where, data);
            }
        } catch (Throwable thr) {
            System.err.println("<performIRCAction\naction:"+actionType+"\nwhere:"+where+"\ndata:"+data);
            thr.printStackTrace();
            System.err.println(">");
        }        
    }
    synchronized protected void performIRCNewMessage(String from, String to, String data, boolean isYourMessage, boolean isChannelMessage, boolean isNotice) {
        String tfrom=UserName.extractNick(from),
                tTo=isChannelMessage?to:UserName.extractNick(to);
        try {
            for (IRCActionListener elem : ircActionListeners) {
                elem.ircNewMessage(tfrom, tTo, data, isYourMessage, isChannelMessage, isNotice);
            }
        } catch (Throwable thr) {
            System.err.println(from+" "+data);
            thr.printStackTrace();
        }
    }
    public IRCActionPerformer() {        
    }
}
