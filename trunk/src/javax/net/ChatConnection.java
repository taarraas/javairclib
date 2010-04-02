/*
 * ChatConnection.java
 *
 * Created on 28 ρεπονÿ 2008, 18:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package javax.net;

/**
 *
 * @author taras
 */
public interface ChatConnection {
    public void say(String to, String data);
    public void joinUserGroup(String name);
    public String[] getUsersInGroup(String group);
    public String[] getGroups();
}
