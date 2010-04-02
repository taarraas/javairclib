/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javax.net.vtirclib;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author taras
 */
    public class UserName implements Comparable {
        String name;
        static public String extractNick(String usersIdentifier)
        {        
            String res = "";       
            int i=0;
            while (i<usersIdentifier.length() && (":~&@%+".indexOf(usersIdentifier.charAt(i))!=-1)) i++;
            for (; i<usersIdentifier.length() && ("!@".indexOf(usersIdentifier.charAt(i))==-1); i++)
                    res += usersIdentifier.charAt(i);
            return res;
        }        
        public UserName(String username) {
            name=extractNick(username);
        }
        static public int compare(String a, String b) {
            String aa = extractNick(a).toLowerCase(),
                    bb = extractNick(b).toLowerCase();
            return aa.compareTo(bb);
        }
        public boolean equals(Object o) {
            return compare(name, o.toString())==0;
        }
        public String toString() {
            return name;
        }
        public int compareTo(Object o) {
            return name.compareTo(o.toString());
        }
        static public Set<String> convert(Set<UserName> from) {            
            Set<String> ret=new HashSet<String>();
            for (UserName userName : from) {
                ret.add(userName.toString());
            }
            return ret;
        }
        static public UserName valueOf(String username) {
            return new UserName(username);
        }        
        public int hashCode() {
            return name.hashCode();
        }
    }
