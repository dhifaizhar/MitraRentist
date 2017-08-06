package id.rentist.mitrarentist.modul;

/**
 * Created by mdhif on 16/07/2017.
 */

public class ItemDriverModul {
    private String mName, mTelp, mEmail;
    private int mThumbnail;

    public void setName(String mName) {
        this.mName = mName;
    }
    public String getName() {
        return mName;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }
    public String getEmail() {
        return mEmail;
    }

    public void setTelp(String mTelp) {
        this.mTelp = mTelp;
    }
    public String getTelp() {
        return mTelp;
    }

    public void setThumbnail(int mThumbnail) {
        this.mThumbnail = mThumbnail;
    }
    public int getThumbnail() {
        return mThumbnail;
    }
}
