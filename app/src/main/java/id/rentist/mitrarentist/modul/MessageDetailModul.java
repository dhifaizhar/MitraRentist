package id.rentist.mitrarentist.modul;

/**
 * Created by mdhif on 02/01/2018.
 */

public class MessageDetailModul {
    private String messageText;
    private String messageUser;
    private String messageTime;
    private int type;

    public String getMessageText() {
        return messageText;
    }
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }
    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageTime() {
        return messageTime;
    }
    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
}
