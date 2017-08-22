package id.rentist.mitrarentist.modul;

/**
 * Created by mdhif on 16/07/2017.
 */

public class ItemDriverModul {
    private String mId, mName, mTelp, mEmail, mSIM, mGender, mBDate;
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

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getSIM() {
        return mSIM;
    }

    public void setSIM(String mSIM) {
        this.mSIM = mSIM;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String mGender) {
        this.mGender = mGender;
    }

    public String getBDate() {
        return mBDate;
    }

    public void setBDate(String mBDate) {
        this.mBDate = mBDate;
    }
}
