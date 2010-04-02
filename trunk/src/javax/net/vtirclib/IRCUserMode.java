/*
 * IrcUserMode.java
 *
 * Created on 26 червня 2008, 12:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package javax.net.vtirclib;

import java.util.IllegalFormatFlagsException;

/**
 *
 * @author taras
 */
public class IRCUserMode {
    public boolean away=false, 
            invisible=false,
            wallops=false,
            restrictedConnection=false,
            operator=false,
            OperatorLocal=false;
    public void set(char op, boolean value)
    {
        switch (op)
        {
            case 'a':away = value; break;
            case 'i':invisible = value; break;
            case 'w':wallops = value; break;
            case 'r':restrictedConnection = value; break;
            case 'o':operator = value; break;
            case 'O':OperatorLocal = value; break;
            case 'x':break;
            //default: throw new IllegalFormatFlagsException("unsupported mode letter " + op);
        }
    }
    public void set(String op)
    {
        boolean newValue=false;
        if (op.charAt(0)=='+')
            newValue=true;
        else if (op.charAt(0)!='-') throw new IllegalFormatFlagsException("must starts from + or -");
        for (int i=1; i<op.length(); i++) set(op.charAt(i), newValue);
    }
    /** Creates a new instance of IrcUserMode */
    public IRCUserMode() {
    }
    
}
