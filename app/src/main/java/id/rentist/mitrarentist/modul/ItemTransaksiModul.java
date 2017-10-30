package id.rentist.mitrarentist.modul;

/**
 * Created by mdhif on 18/06/2017.
 */

public class ItemTransaksiModul {
    private String mTitle, mIdTrans, mAsetName, mCodeTrans, mThumbnail, mDriverName;
    private Boolean mDriverIncluded;
//    private int mThumbnail;
    private int mMemberIco;
    private String mMember;
    private int mDateIco;
    private String mDate, mStartDate, mEndDate;
    private int mLocIco;
    private String mLoc;
    private int mPriceIco;
    private String mPrice;

    public String getIdTrans() {
        return mIdTrans;
    }
    public void setIdTrans(String mIdTrans) {
        this.mIdTrans = mIdTrans;
    }

    public String getCodeTrans() {
        return mCodeTrans;
    }
    public void setCodeTrans(String mCodeTrans) {
        this.mCodeTrans = mCodeTrans;
    }

    public String getAsetName() {
        return mAsetName;
    }
    public void setAsetName(String mAsetName) {
        this.mAsetName = mAsetName;
    }

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getThumbnail() {
        return mThumbnail;
    }
    public void setThumbnail(String mThumbnail) {
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
    public void setStartDate(String mStartDate) {
        this.mStartDate = mStartDate;
    }
    public String getStartDate() {
        return mStartDate;
    }

    public void setEndDate(String mEndDate) {
        this.mEndDate = mEndDate;
    }
    public String getEndDate() {
        return mEndDate;
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

    public void setDriverName(String mDriverName) {
        this.mDriverName = mDriverName;
    }
    public String getDriverName() {
        return mDriverName;
    }

    public Boolean getDriverIncluded() {
        return mDriverIncluded;
    }
    public void setDriverIncluded(Boolean mDriverIncluded) {
        this.mDriverIncluded = mDriverIncluded;
    }
}
