package id.rentist.mitrarentist.modul;

/**
 * Created by Nugroho Tri Pambud on 8/9/2017.
 */

public class UserModul {
    private String mName, mPhone, mEmail, mRole, mThumbnail;
    private int mId;

    public void setName(String mName) {
        this.mName = mName;
    }
    public String getName() {
        return mName;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }
    public String getPhone() {
        return mPhone;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }
    public String getEmail() {
        return mEmail;
    }

    public void setRole(String mRole) {
        this.mRole = mRole;
    }
    public String getRole() {
        return mRole;
    }

    public void setThumbnail(String mThumbnail) {
        this.mThumbnail = mThumbnail;
    }
    public String getThumbnail() {
        return mThumbnail;
    }

    public int getId() {
        return mId;
    }
    public void setId(int mId) {
        this.mId = mId;
    }
}
