package id.rentist.mitrarentist.modul;

/**
 * Created by mdhif on 18/06/2017.
 */

public class ItemTransaksiModul {
    private String mTitle, mIdTrans, mAsetName, mCodeTrans, mDriverName, mPrice, mAsetThumb, mIdAddtional, mOrderDate,
            mStatus;
    private String mDriverIncluded;
    private String mIdMember, mMember, mThumbnail;
    private String mDate, mStartDate, mEndDate, mPickTime, mNote, mLat, mLong, mAddress;

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

    public String getStatus() {
        return mStatus;
    }
    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
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

    public String getIdMember() {
        return mIdMember;
    }
    public void setIdMember(String mIdMember) {
        this.mIdMember = mIdMember;
    }

    public void setMember(String mMember) {
        this.mMember = mMember;
    }
    public String getMember() {
        return mMember;
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

    public void setAsetThumb(String mAsetThumb) {
        this.mAsetThumb = mAsetThumb;
    }
    public String getAsetThumb() {
        return mAsetThumb;
    }

    public void setPickTime(String mPickTime) {
        this.mPickTime = mPickTime;
    }
    public String getPickTime() {
        return mPickTime;
    }

    public void setNote(String mNote) {
        this.mNote = mNote;
    }
    public String getNote() {
        return mNote;
    }

    public void setIdAddtional(String mIdAddtional) {
        this.mIdAddtional = mIdAddtional;
    }
    public String getIdAddtional() {
        return mIdAddtional;
    }

    public void setLat(String mLat) {
        this.mLat = mLat;
    }
    public String getLat() {
        return mLat;
    }

    public void setLong(String mLong) {
        this.mLong = mLong;
    }
    public String getLong() {
        return mLong;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }
    public String getAddress() {
        return mAddress;
    }

    public String getDriverIncluded() {
        return mDriverIncluded;
    }
    public void setDriverIncluded(String mDriverIncluded) {
        this.mDriverIncluded = mDriverIncluded;
    }

    public void setOrderDate(String mOrderDate) {
        this.mOrderDate = mOrderDate;
    }
    public String getOrderDate() {
        return mOrderDate;
    }
}
