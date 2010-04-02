/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javax.net.vtirclib;

import java.util.Set;

/**
 *
 * @author taras
 */
public class ChannelManager implements ChanModel{

    private Topics topics=new Topics();
    private UsersOnChannel usersOnChannel=new UsersOnChannel();
    private Channels channels=new Channels();
    private Users users=new Users();
    private IRCActionPerformer ircActionPerformer;
    private String yourNickname;
    public boolean isLoggedOnChannel(String channel) {
        return usersOnChannel.containsKey(channel);
    }
    public Set<String> getLoggedChannels() {
        return channels.toStringSet();
    }
    protected void addUserToChannel(String username, String channelname) {
        addUser(username);
        usersOnChannel.get(channelname).add(username);
        ircActionPerformer.performIRCAction(IRCActionListener.CHANNEL_CHANGED, channelname, UserName.extractNick(username));
    }
    protected void removeUserFromChannel(String username, String channelname) {
        if (!usersOnChannel.containsKey(channelname)) {
            addChannel(channelname);
        }
        if (isYourNick(username)) {
            removeChannel(channelname);
            ircActionPerformer.performIRCAction(IRCActionListener.CHANNEL_CHANGED, channelname, "LEAVE");
        } else {
            usersOnChannel.get(channelname).remove(username);
            ircActionPerformer.performIRCAction(IRCActionListener.CHANNEL_CHANGED, channelname, UserName.extractNick(username));
        }
    }
    protected void removeUser(String username) {
        users.remove(username);
        ircActionPerformer.performIRCAction(IRCActionListener.USER_MODE_CHANGED, UserName.extractNick(username), "QUIT");
        for (String string : channels.toStringSet()) {
            removeUserFromChannel(username, string);
        }
    }
    public boolean isLogged(String username) {
        return users.contains(username);
    }
    //mode in form +/- aiwroOx
    public void setUserMode(String username, String mode, String channelname) {
        //assert getUserMode(username)==null:username;
        getUserMode(username).set(mode);
    }
    private IRCUserMode getUserMode(String username) {
        return users.get(username);
    }    
    protected void renameUser(String oldnick, String newnick) {
        if (isYourNick(oldnick)) {
            setNick(newnick);
        }
        users.put(newnick, getUserMode(oldnick));
        users.remove(oldnick);
        ircActionPerformer.performIRCAction(IRCActionListener.NICK_CHANGED, UserName.extractNick(oldnick), UserName.extractNick(newnick));
        
        for (String string : channels.toStringSet()) {
            if (usersOnChannel.get(string).contains(oldnick)) {
                usersOnChannel.get(string).remove(oldnick);
                usersOnChannel.get(string).add(newnick);
                ircActionPerformer.performIRCAction(IRCActionListener.CHANNEL_CHANGED, string, "USER");                
            }
        }
    }
    protected void addChannel(String channelname) {
        channels.add(channelname);
        topics.put(channelname, "");
        usersOnChannel.add(channelname);
    }
    protected void removeChannel(String channelname) {
        channels.remove(channelname);
        topics.remove(channelname);
        usersOnChannel.remove(channelname);
    }
    protected void addUser(String username) {
        if (!users.contains(username)) {
            users.put(username, new IRCUserMode());
            ircActionPerformer.performIRCAction(IRCActionListener.USER_MODE_CHANGED, UserName.extractNick(username), "JOIN");
        }
    }
    
    public String getTopic(String channelName) {
        return topics.get(channelName);
    }
    protected void setTopic(String channel, String topic) {
        topics.put(channel, topic);
        ircActionPerformer.performIRCAction(IRCActionListener.TOPIC_CHANGED, channel, topic);
    }
    public Set<String> getUsersOnChannel(String ChannelName) {
        return usersOnChannel.get(ChannelName).toStringSet();
    }    
    public Set<String> getUsers() {
        return users.toStringSet();
    }
    public String getNick() {
        return yourNickname;
    }
    public boolean isYourNick(String username) {
        return UserName.valueOf(username).equals(getNick());
    }
    protected void setNick(String nick) {
        yourNickname=nick;
    }
    public ChannelManager(IRCActionPerformer performer, String yourNick) {
        ircActionPerformer=performer;
        setNick(yourNick);
        addUser(getNick());
    }
}
