/*
 * LogWriter.java
 *
 * Created on 25 ρεπονÿ 2008, 21:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package javax.net.vtirclib;
import java.io.FileWriter;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;
/**
 *
 * @author taras
 */
public class LogWriter implements IRCActionListener{
    
    /** Creates a new instance of LogWriter */
    String directory, servername;
    static String BAD_FOR_FILE_NAME_CHARS="?/\\|*:<>";
    static char REPLACE_CHAR='-';
    Map<String, FileWriter> sessions=new TreeMap<String, FileWriter>();
    boolean isRAWLogging=false;
    public LogWriter(String server, String dir) {
        directory=dir;
        servername=server;
    }
    public LogWriter(String server) {
        directory="";
        servername=server;
    }    
    private String formatForFilename(String data) {
        if (data==null) {
            return "null";
        }
        char ret[]=data.toCharArray();
        for (int i = 0; i < ret.length; i++) {
            if (BAD_FOR_FILE_NAME_CHARS.indexOf(ret[i])!=-1) {
                ret[i]=REPLACE_CHAR;
            }
        }
        return String.valueOf(ret);
    }
    public void setRAWDataLogging(boolean isLogging) {
        isRAWLogging=isLogging;
    }
    public boolean getRAWDataLogging() {
        return isRAWLogging;
    }
    public FileWriter getSession(String name) {
        FileWriter tmp=sessions.get(name.toLowerCase());
        if (tmp==null) {
            try {
                tmp=new FileWriter(directory+formatForFilename(servername)+"."+formatForFilename(name)+".log", true);
                sessions.put(name, tmp);
            } catch (java.io.IOException e) {
                System.err.println("Cannot create log file");
                e.printStackTrace();
            }
        }
        return tmp;
    }
    /**
     * 
     * @param actionType
     * @param where
     * @param data
     */
    public void ircActionPerformed(int actionType, String where, String data) {
        if (actionType==IRCActionListener.RAW_DATA && getRAWDataLogging()) {
            write("RAWDATA" ,data);
        }
    }
    public void write(String to, String data) {
        FileWriter fw=getSession(to);
        try {
            fw.write(data+"\n");
            fw.flush();
        } catch (java.io.IOException e) {
            System.err.println("Cannot to save log "+to+":"+data);
            e.printStackTrace();
        }
    }
    public void ircNewMessage(String from, String to, String data, boolean isYourMessage, boolean isChannelMessage, boolean isNotice) {
        GregorianCalendar gc = new GregorianCalendar();        
        String time = (gc.get(GregorianCalendar.HOUR_OF_DAY)<10?"0":"")+Integer.toString(gc.get(GregorianCalendar.HOUR_OF_DAY))+":" +
                (gc.get(GregorianCalendar.MINUTE)<10?"0":"")+Integer.toString(gc.get(GregorianCalendar.MINUTE)) + ":" +
                (gc.get(GregorianCalendar.SECOND)<10?"0":"")+Integer.toString(gc.get(GregorianCalendar.SECOND));
        if (isYourMessage) write(to, time + " <"+from+"> " + data);
        else {
            if (isChannelMessage) write(to, time + " <"+from+"> " + data);
            else write(from, time + " <"+from+"> " + data);
        }
    }
    public void finalize() throws Throwable{
        super.finalize();
        for (FileWriter elem : sessions.values()) {
            try {
            elem.flush();
            } catch (java.io.IOException e) {                
                e.printStackTrace();
            }
        }
    }    
}
