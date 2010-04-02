/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javax.net.vtirclib;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author taras
 */
public class Users {

    Map<UserName, IRCUserMode> users=new HashMap<UserName, IRCUserMode>();
    public void add(String username) {
        if (!contains(username)) {
            users.put(UserName.valueOf(username), new IRCUserMode());
        }
    }
    public void remove(String username) {
        users.remove(UserName.valueOf(username));
    }
    public boolean contains(String username) {
        return users.containsKey(UserName.valueOf(username));
    }
    public IRCUserMode get(String username) {
        return users.get(UserName.valueOf(username));
    }
    public void put(String username, IRCUserMode mode) {
        users.put(UserName.valueOf(username), mode);
    }
    public Set<String> toStringSet() {
        Set<String> ret=new HashSet<String>();
        for (UserName userName : users.keySet()) {
            ret.add(userName.toString());
        }
        return ret;
    }
}
