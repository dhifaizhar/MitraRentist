package id.rentist.mitrarentist.modul;

/**
 * Created by mdhif on 18/06/2017.
 */

public class ItemTransaksiModul {
    private String mTitle;
    private int mThumbnail;
    private int mMemberIco;
    private String mMember;
    private int mDateIco;
    private String mDate;
    private int mLocIco;
    private String mLoc;
    private int mPriceIco;
    private String mPrice;

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getThumbnail() {
        return mThumbnail;
    }
    public void setThumbnail(int mThumbnail) {
        this.mThumbnail = mThumbnail;
    }

    public void setMemberIco(int mMemberIco) {
        this.mMemberIco = mMemberIco;
    }
    public int getMemberIco() {
        return mMemberIco;
    }
    public void setMember(String mMember) {
        this.mMember = mMember;
    }
    public String getMember() {
        return mMember;
    }

    public void setDateIco(int mDateIco) {
        this.mDateIco = mDateIco;
    }
    public int getDateIco() {
        return mDateIco;
    }
    public void setDate(String mDate) {
        this.mDate = mDate;
    }
    public String getDate() {
        return mDate;
    }

    public void setLocIco(int mLocIco) {
        this.mLocIco = mLocIco;
    }
    public int getLocIco() {
        return mLocIco;
    }
    public void setLoc(String mLoc) {
        this.mLoc = mLoc;
    }
    public String getLoc() {
        return mLoc;
    }

    public void setPriceIco(int mPriceIco) {
        this.mPriceIco = mPriceIco;
    }
    public int getPriceIco() {
        return mPriceIco;
    }
    public void setPrice(String mPrice) {
        this.mPrice = mPrice;
    }
    public String getPrice() {
        return mPrice;
    }
}
