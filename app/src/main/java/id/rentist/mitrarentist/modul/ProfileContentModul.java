package id.rentist.mitrarentist.modul;

/**
 * Created by mdhif on 21/06/2017.
 */

public class ProfileContentModul {
    private String mRentName, mOwner, mAddress, mEmail, mTelp;
    private int mProfilePhoto, mProfilePhotoOwner;

    public void setProfilePhoto(int mProfilePhoto) {
        this.mProfilePhoto = mProfilePhoto;
    }
    public int getProfilePhoto() {
        return mProfilePhoto;
    }

    public void setProfilePhotoOwner(int mProfilePhotoOwner) {
        this.mProfilePhotoOwner = mProfilePhotoOwner;
    }

    public int getProfilePhotoOwner() {
        return mProfilePhotoOwner;
    }

    public void setRentName(String mRentName) {
        this.mRentName = mRentName;
    }
    public String getRentName() {
        return mRentName;
    }

    public void setOwner(String mOwner) {
        this.mOwner = mOwner;
    }
    public String getOwner() {
        return mOwner;
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

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }
    public String getAddress() {
        return mAddress;
    }
}
