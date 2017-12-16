package id.rentist.mitrarentist.modul;

/**
 * Created by Nugroho Tri Pambud on 7/8/2017.
 */

public class KebijakanModul {
    private int mId;

    private String sId, mTitle, aCategory;
    private String mDesc;

    public int getId() {
        return mId;
    }
    public void setId(int mId) {
        this.mId = mId;
    }

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDesc() {
        return mDesc;
    }
    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getaCategory() {
        return aCategory;
    }

    public void setaCategory(String aCategory) {
        this.aCategory = aCategory;
    }
}
