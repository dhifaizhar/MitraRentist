package id.rentist.mitrarentist.modul;

/**
 * Created by Nugroho Tri Pambud on 7/16/2017.
 */

public class MessageListModul {
    private String mTitle;
    private String mName;
    private String mLastMessage;
    private String mLastMessageTime;
    private int mThumbnail;

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
    public String getTitle() {
        return mTitle;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
    public String getName() {
        return mName;
    }

    public void setLastMessage(String mLastMessage) {
        this.mLastMessage = mLastMessage;
    }
    public String getLastMessage() {
        return mLastMessage;
    }

    public void setLastMessageTime(String mLastMessageTime) {
        this.mLastMessageTime = mLastMessageTime;
    }
    public String getLastMessageTime() {
        return mLastMessageTime;
    }

    public void setThumbnail(int mThumbnail) {
        this.mThumbnail = mThumbnail;
    }
    public int getThumbnail() {
        return mThumbnail;
    }

}
