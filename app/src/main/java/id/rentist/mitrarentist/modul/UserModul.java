package id.rentist.mitrarentist.modul;

/**
 * Created by Nugroho Tri Pambud on 8/9/2017.
 */

public class UserModul {
    private String mName;
    private String mPhone;
    private String mEmail;
    private String mRole;
    private int mThumbnail;

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

    public void setThumbnail(int mThumbnail) {
        this.mThumbnail = mThumbnail;
    }
    public int getThumbnail() {
        return mThumbnail;
    }
}