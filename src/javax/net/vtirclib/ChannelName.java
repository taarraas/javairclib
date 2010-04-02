/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javax.net.vtirclib;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author taras
 */
    public class ChannelName implements Comparable {
        String name;
        public ChannelName(String channelname) {
            name=channelname;
        }
        private int compare(String a, String b) {
            Locale loc = Locale.ENGLISH;
            String aa = a.toLowerCase(loc),
                    bb = b.toLowerCase(loc);
            return aa.compareTo(bb);
        }
        public boolean equals(Object o) {
            return compare(name, o.toString())==0;
        }
        public int compareTo(Object o) {
            return compare(name, o.toString());
        }
        public String toString() {
            return name;
        }
        static public Set<String> convert(Set<ChannelName> from) {            
            Set<String> ret=new HashSet<String>();
            for (ChannelName channelName : from) {
                ret.add(channelName.toString());
            }
            return ret;
        }
        static public ChannelName valueOf(String channelname) {
            return new ChannelName(channelname);
        }
        public int hashCode() {
            return name.hashCode();
        }
    }    