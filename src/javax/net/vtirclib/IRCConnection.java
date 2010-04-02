/*
 * Connection.java
 *
 * Created on 10 РђРїСЂРµР»СЊ 2008 Рі., 17:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package javax.net.vtirclib;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author taras
 */


public class IRCConnection {
    public static final int IRCDEFAULTPORT=6667;
    protected static final String ClientID="KVIrc 3.2.6 'Anomalies' 20070106 - build Jan  9 2007 00:01:02 - Windows XP Professional Service Pack 2 (Build 2600)";
    protected static final String DefaultQuitMessage="Мене більше нема...";
    private Socket socket;
    private BufferedReader sockIn;
    private PrintWriter sockOut;
    private String[] autoJoin;
    private ChannelManager channelManager;
    private String loggedToServer=null;
    private boolean isSendingRawData=false;
    private IRCActionPerformer ircActionPerformer=new IRCActionPerformer();
    private boolean isDisconnected=false;
    public boolean isDisconnected() {
        return isDisconnected;
    }
    private class ThreadReader extends Thread{
        private IRCConnection conn;
        public ThreadReader(IRCConnection conn){
            super();
            this.setPriority(Thread.MIN_PRIORITY);
            this.conn = conn;
            this.start();
        }
        public void run(){
            try {
                String s;
                while ((s=conn.sockIn.readLine())!=null){
                    if (isDisconnected)
                        return;
                    try {
                        conn.parseLine(s);
                    } catch (Exception thr) {
                        System.err.println("Error during parsing line:"+s);
                        thr.printStackTrace();
                    }
                }
            }
            catch (IOException e) {               
            }
        }
    }
    ThreadReader threadReader;
    static public boolean isChannel(String name) {
        return name.charAt(0)=='#';
    }
    public IRCConnection(String url, String encoding, IRCActionListener ircal, 
            String nickname, String password, String autoJoinChannels[]) 
            throws IOException, UnknownHostException, SocketTimeoutException {
        addIRCActionListener(ircal);
        SocketAddress sockAddr;
        if (url.indexOf(':')==-1)  {
            InetAddress iaddr = InetAddress.getByName(url);        
            sockAddr = new InetSocketAddress(iaddr, IRCDEFAULTPORT);            
        } else {
            InetAddress iaddr = InetAddress.getByName(url.substring(0, url.indexOf(':')));
            sockAddr = new InetSocketAddress(iaddr, Integer.valueOf(url.substring(url.indexOf(':')+1)));
        }
        socket = new Socket();
        socket.connect(sockAddr, 2000);
        sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), encoding));
        sockOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), encoding));
        threadReader = new ThreadReader(this);
        channelManager=new ChannelManager(ircActionPerformer, nickname);
        login(nickname, password);
        autoJoin = autoJoinChannels==null ? new String[0] : autoJoinChannels;
    }
    public void finalize() throws Throwable {
        try
        {
            quit(DefaultQuitMessage);
            sockIn.close();
            sockOut.close();
            socket.close();
        }
        catch (IOException e) {            
        }

    }    
    public void addIRCActionListener(IRCActionListener l) {
        if (l!=null) ircActionPerformer.addIRCActionListener(l);
    }
    public void removeIRCActionListener(IRCActionListener l) {
        ircActionPerformer.addIRCActionListener(l);
    }
    synchronized void parseLine(String s) throws IOException
    {      
        if (isSendingRawData) ircActionPerformer.performIRCAction(IRCActionListener.RAW_DATA, null, s);
        
                // parse from s to Vector<String> curLine
        String curWord="";
        Vector<String> curLine = new Vector<String>();
        boolean spaceIgnored=false;
        for (int i=0; i<s.length(); i++)
        {
            switch(s.charAt(i)) {
                case ':':{
                    if (i==0 || !curWord.isEmpty()) curWord+=':';
                    else spaceIgnored = true;
                    break;
                }
                case ' ':{
                    if (spaceIgnored) curWord+=' ';
                    else
                    {
                        if (!curWord.isEmpty()) 
                        {
                            curLine.add(curWord);
                            curWord="";
                        }
                    }                          
                    break;
                }
                default:{
                    curWord+=s.charAt(i);
                }                
            }
        }
        if (!curWord.isEmpty() || spaceIgnored) curLine.add(curWord);
        if (curLine.isEmpty()) return;
         
                //parse command from curLine
        String a[] = curLine.toArray(new String[0]);
        if (a[0].startsWith(":"))  // parse command begining with prefix ":"
        {
            if (loggedToServer == null) {
                //get server name
                //:irc.hostel16 004 taras irc.hostel16 Unreal3.2.7 iowghraAsORTVSxNCWqBzvdHtGp lvhopsmntikrRcaqOALQbSeIKVfMCuzNTGj           
                if (a[1].equals("004")) {
                loggedToServer = a[3];
                ircActionPerformer.performIRCAction(IRCActionListener.SERVER_CONNECTED, loggedToServer, "");
                return;
            } else {
                    //bad method of detecting errors....
              /*      if (!a[1].toUpperCase().equals("NOTICE")) {
                        performIRCAction(ircActionListener.CONNECTION_TERMINATED, "server", a[4]);
                    }*/
            }                
        }

            // new topic setted
            //:irc.hostel16 332 taras #chat :Так, тільки не прикалуватись.....         
            if (a[1].equals("332")) {
                channelManager.setTopic(a[3], a[4]);
                return;
            }
                        
            //get users on channel 
            //:irc.hostel16 353 taras = #chat :taras |криветко| |GooD| @Макц _yagub_            
            if (a[1].equals("353")) {
                String names[]=a[5].split(" ");
                for (String string : names) {
                    if (string.equals("")) continue;
                    assert(string!=null);
                    channelManager.addUserToChannel(string, a[4]);
                }
                return;
            }
            
            // end of hello message
            // autojoin here
            // :irc.foonet.com 376 kriv :End of /MOTD command.
            if (a[1].equals("005")) {
                for (int i=0; i<autoJoin.length; i++) joinChannel(autoJoin[i]);
            }
            
            //new user joined to channel
            //:улідтко!~ulidtko@10.45.67.37 JOIN :#chat
            if (a[1].toUpperCase().equals("JOIN")) {
                channelManager.addChannel(a[2]);
                channelManager.addUserToChannel(a[0], a[2]);
            }
            
            //user kicked
            //:Erroneo3!krivetko@rox-9DE917E3 KICK #zooo krivetke :reason here
            if (a[1].toUpperCase().equals("KICK")) {
                channelManager.removeUserFromChannel(a[0], a[2]);
            }
            
            //user killed
            //:Cain!~veritas@deware.lo KILL Blonde_ :irc.run.net[unknown@82.137.161.42]!*.avalon.com[unknown@1.1.0.1]!deware.lo!Ca
            if (a[1].toUpperCase().equals("KILL")) {
                if (channelManager.isYourNick(a[2])) {
                    setDisconnectedState(a[0], a[3]);                    
                } else {
                    channelManager.removeUser(a[2]);
                }                
            }
            
            //user quitted
            //:|криветко|!krivetko@10.45.73.113 QUIT :Quit: KVIrc 3.2.6 Anomalies http://www.kvirc.net/
            if (a[1].toUpperCase().equals("QUIT")) {
                 channelManager.removeUser(a[0]);
            }
            
            //leave channel
            //:|криветко|!krivetko@10.45.73.113 PART #zoopark :Time makes no sense
            if (a[1].toUpperCase().equals("PART")) {
                channelManager.removeUserFromChannel(a[0], a[2]);
            }
            
            //mode changed
            //:gelraen|sex_with_wifi!~gelraen@10.45.71.120 MODE #chat +v Blonde
            //Blonde MODE Blonde :+i
//            if (a[1].toUpperCase().equals("MODE")) {
//                channelManager.setUserMode(a[4], a[3], a[2]);
//            }
            
            //nick changed
            //:улідтко!~ulidtko@10.45.67.37 NICK :улідтко|алгебра
            if (a[1].toUpperCase().equals("NICK")) {
                channelManager.renameUser(a[0], a[2]);                
            }
            
            //topic changed
            //:kriv!JIR@rox-9DE917E3 TOPIC #zoo :bridke buttja
            if (a[1].toUpperCase().equals("TOPIC")) {
                channelManager.setTopic(a[2], a[3]);
            }
            
            //new message received
            //:|криветко|!krivetko@10.45.73.113 PRIVMSG #zoopark :вийди й зайди            
            if (a[1].toUpperCase().equals("PRIVMSG")) {
                //CTCP VERSION
                if (a[3].toUpperCase().equals("VERSION"))
                {
                    sendNotice(a[0], "VERSION "+ClientID);
                    return;
                }
                if (a[3].indexOf(1)!=-1 || a[3].indexOf(2)!=-1 || a[3].indexOf(3)!=-1 || a[3].indexOf(4)!=-1) return;
               //message
               ircActionPerformer.performIRCNewMessage(a[0], a[2],
                        a[3], channelManager.isYourNick(a[0]), isChannel(a[2]), false);
            }
             
        }
        else        // parse other commands
        {
            if (a[0].toUpperCase().equals("PING"))
            {
                writeRaw("PONG :"+curLine.elementAt(1));
                return;
            }
            if (a[0].toUpperCase().equals("ERROR"))
            {
                setDisconnectedState("", "error");
                throw(new Error("Error from server"));
            }
        }        
    }
    public void writeRaw(String data)
    {
        if (isSendingRawData) ircActionPerformer.performIRCAction(IRCActionListener.RAW_DATA, null, data);
        sockOut.print(data+"\n");
        sockOut.flush();
    }
    public void leaveChannel(String channelName, String reason)
    {
        writeRaw("PART "+channelName+" :"+reason);
    }
    public void joinChannel(String channelName)
    {
        writeRaw("JOIN "+channelName);
    }
    public void leaveAllChannels()
    {
        writeRaw("JOIN 0");
    }
    public void setTopic(String channelName, String newTopic)
    {
        writeRaw("TOPIC "+channelName+" :"+newTopic);
    }
    public void quit(String quitMessage)
    {
        writeRaw("QUIT :"+quitMessage);
    }
    public void sendMessage(String to, String data)
    {
        ircActionPerformer.performIRCNewMessage(channelManager.getNick(), to, data, true, isChannel(to), false);
        writeRaw("PRIVMSG " + to + " :"+data);
    }
    public void sendNotice(String to, String data)
    {
        //performIRCNewMessage(yourNickname, to, data, true, isChannel(to), true);
        writeRaw("NOTICE " + to + " :"+data);
    }    
    private void login(String nickname, String password) {
        writeRaw("NICK " + nickname);
        writeRaw("USER "+nickname+" 0 * :"+nickname);
        if (!password.isEmpty()) writeRaw("PASS "+password);
    }
    public boolean getSendingRawData() {
        return isSendingRawData;
    }
    public void setSendingRawData(boolean isNeeded) {
        isSendingRawData=isNeeded;
    }
    public String getLoggedServer() {
        return loggedToServer;
    }
    public ChanModel getChanModel() {
        return channelManager;
    }
    private void setDisconnectedState(String who, String reason) {
        isDisconnected=true;
        ircActionPerformer.performIRCAction(IRCActionListener.CONNECTION_TERMINATED, who, reason);
    }
}

