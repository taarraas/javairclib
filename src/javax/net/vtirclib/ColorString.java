/*
 * ColorString.java
 *
 * Created on 21 липня 2008, 19:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package javax.net.vtirclib;

import java.awt.Color;

/**
 *
 * @author taras
 */
public class ColorString {
    
    /** Creates a new instance of ColorString */
    private Color background;
    private String string;
    public ColorString(String s, Color c) {
        background = c;
        string = s;
    }
    public ColorString(String s) {
        background = Color.WHITE;
        string = s;
    }
    public Color getBackground() {
        return background;
    }
    public void setBackground(Color c) {
        background = c;
    }
    public String toString() {
        return string;
    }
    public void setString(String s) {
        string = s;
    }    
    public boolean equals(Object obj) {
        if (obj instanceof ColorString) return ((ColorString)obj).toString().compareToIgnoreCase(string)==0;
        else return false;
    }
}
