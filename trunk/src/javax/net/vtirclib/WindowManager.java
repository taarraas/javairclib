/*
 * LogWindowManager.java
 *
 * Created on 25 ρεπονÿ 2008, 19:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package javax.net.vtirclib;

import java.awt.Color;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author taras
 */
public class WindowManager implements IRCActionListener, ListSelectionListener{
    
    /** Creates a new instance of LogWindowManager */
    private JList chanList;
    private JTextComponent mainView;
    private JList userList;
    private JTextField topicEdit;
    private ChanModel chanModel;
    Map<String, StyledDocument> pages = new HashMap<String, StyledDocument>();
    private static final String ALERTPAGENAME = "Alert page";
    private static final String SOCKETAGENTPAGENAME = "Socket agent page";
    
    private void showUsersOnCurrentChannel() { 
        if (chanModel == null || !chanModel.isLoggedOnChannel(getCurrentChannel()))  {
            userList.setModel(new DefaultListModel());
            return;
        }
        Set<String> users = chanModel.getUsersOnChannel(getCurrentChannel());
        userList.setModel(new DefaultComboBoxModel(users.toArray()));
    }
    public void setChanModel(ChanModel data) {
        chanModel=data;        
    }
    
    public WindowManager(JList list, JTextComponent text, JList users, JTextField topic) {
        topicEdit=topic;
        
        userList=users;
        userList.addListSelectionListener(this);
        
        chanList=list;
        chanList.addListSelectionListener(this);
        chanList.setModel(new DefaultListModel());
        chanList.setSelectedIndex(0);
        chanList.setCellRenderer(new ColorCellRenderer());
        
        
        mainView=text;
        mainView.setEditable(false);        
        newChannel(ALERTPAGENAME);
        newChannel(SOCKETAGENTPAGENAME);
    }
    private void highlightChannel(String channel, Color color) {
        DefaultListModel model=((DefaultListModel) chanList.getModel());
        int indexhl=model.indexOf(new ColorString(channel)); 
        if (indexhl == -1) throw new IllegalArgumentException("No such channel in chanList:"+channel);
        ((ColorString)model.get(indexhl)).setBackground(color);
        chanList.repaint();
    }   
    private void printOnChannel(String channel, String data, Color foregroundColor, boolean isBold, boolean isItalic) {
        try {
            newChannel(channel);
            StyledDocument doc = pages.get(channel.toLowerCase());
            Style style = doc.addStyle("current", null);
            StyleConstants.setBold(style, isBold);
            StyleConstants.setItalic(style, isItalic);
            StyleConstants.setForeground(style,  foregroundColor);
            doc.insertString(doc.getLength(), data+"\n", style);           
            if (!channel.equals(getCurrentChannel())) {
                highlightChannel(channel, Color.YELLOW);
            }
        }
        catch (BadLocationException e) { }
    }    
    private void printOnChannel(String channel, String data, Color foregroundColor) {
        printOnChannel(channel, data, foregroundColor, false, false);
    }    
    private void printOnChannel(String channel, String data) {
        printOnChannel(channel, data, Color.BLACK, false, false);
    }
    public void newChannel(String channel) {
        if (((DefaultListModel)chanList.getModel()).contains(new ColorString(channel))) return;
        DefaultListModel model = (DefaultListModel)chanList.getModel();
        model.addElement(new ColorString(channel));
        pages.put(channel.toLowerCase(), new DefaultStyledDocument());
        //chanList.setSelectedValue(new ColorString(channel), true);
    }    
    public void closeChannel(String channel) {
        if (getCurrentChannel().equals(channel)) chanList.setSelectedIndex(0);
        ((DefaultListModel)chanList.getModel()).removeElement(new ColorString(channel));
        pages.remove(channel.toLowerCase());
    }
    private void changeChannelName(String oldname, String newname) {        
        DefaultListModel model = (DefaultListModel)chanList.getModel();
        if (!model.contains(new ColorString(oldname))) return;
        boolean mustReturnPosition = false;
        if (getCurrentChannel().equals(oldname)) {
            mustReturnPosition = true;
            chanList.setSelectedIndex(0);
        }                
        model.addElement(new ColorString(newname));
        model.removeElement(new ColorString(oldname));
        pages.put(newname.toLowerCase(), pages.get(oldname.toLowerCase()));
        pages.remove(oldname.toLowerCase());
        if (mustReturnPosition) chanList.setSelectedValue(new ColorString(newname), true);                        
    }
    public void ircNewMessage(String from, String to, String data, boolean isYourMessage, boolean isChannelMessage, boolean isNotice) {
        GregorianCalendar gc = new GregorianCalendar();        
        String time = (gc.get(GregorianCalendar.HOUR_OF_DAY)<10?"0":"")+Integer.toString(gc.get(GregorianCalendar.HOUR_OF_DAY))+":" +
                (gc.get(GregorianCalendar.MINUTE)<10?"0":"")+Integer.toString(gc.get(GregorianCalendar.MINUTE)) + ":" +
                (gc.get(GregorianCalendar.SECOND)<10?"0":"")+Integer.toString(gc.get(GregorianCalendar.SECOND));
      
        if (isYourMessage) printOnChannel(to, time + " <"+from+"> " + data, Color.BLUE);
        else {
            if (isChannelMessage) printOnChannel(to, time + " <"+from+"> " + data, Color.BLACK);
            else printOnChannel(from, time + " <"+from+"> " + data, Color.BLACK);
        }
    }
    
    public void ircActionPerformed(int actionType, String where, String data) {
        switch(actionType){
            case IRCActionListener.CHANNEL_CHANGED: {
                DefaultListModel model = (DefaultListModel)chanList.getModel();
                if (data.equals("LEAVE")) {
                    closeChannel(where);
                    break;
                }
                newChannel(where);                
                if (where.equals(getCurrentChannel())) {
                    showUsersOnCurrentChannel();
                }
                break;
            }
            case IRCActionListener.TOPIC_CHANGED: {
                if (where.equals(getCurrentChannel())) 
                         topicEdit.setText(data); 
                break;
            }
            case IRCActionListener.RAW_DATA:{
                printOnChannel(SOCKETAGENTPAGENAME, data);
                break;
            }
            case IRCActionListener.NICK_CHANGED:{
                printOnChannel(ALERTPAGENAME, "User " + where + " has changed nick to " + data);
                changeChannelName(where, data);
                showUsersOnCurrentChannel();
                break;
            }
            case IRCActionListener.CONNECTION_TERMINATED: {
                printOnChannel(ALERTPAGENAME, "Connection terminated by " + where + ". Reason: " + data, Color.RED);
            }
        }
    }
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource().equals(chanList)) 
        {
            showUsersOnCurrentChannel();
            highlightChannel(getCurrentChannel(), Color.WHITE);
            mainView.setDocument(pages.get(getCurrentChannel().toLowerCase()));
            if (isSystemPage(getCurrentChannel())) {
                topicEdit.setText("System page");                            
            } else
            if (chanModel!=null) {
                if (IRCConnection.isChannel(getCurrentChannel())) {
                    topicEdit.setText(chanModel.getTopic(getCurrentChannel()));
                }
                else topicEdit.setText("private");
            }
        }        
    }
    public void addAlertMessage(String msg) {
        printOnChannel(ALERTPAGENAME, msg);
    }
    public boolean isSystemPage(String page) {
        return page.equals(SOCKETAGENTPAGENAME) || page.equals(ALERTPAGENAME);
    }
    private String getCurrentChannel() {
        if (chanList.getSelectedValue()==null) 
            return null;
        else 
            return chanList.getSelectedValue().toString();
    }
}
