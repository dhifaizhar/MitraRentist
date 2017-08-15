package id.rentist.mitrarentist.modul;

/**
 * Created by mdhif on 19/06/2017.
 */

public class ItemAsetModul {
    private int mId, mRatingIco, mThumbnail, mPriceIco, mFeatureIco;
    private String mMerk, mType, mYear, mColor, mPlat, mFuel, mSeat, mEngCap, mStatus, mTitle, mRating, mPrice, mFeature, mTransm;
    private boolean mAirCon, mAirBag, mDriver;

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
    public String getTitle() {
        return mTitle;
    }

    public void setRating(String mRating) {
        this.mRating = mRating;
    }
    public String getRating() {
        return mRating;
    }
    public void setRatingIco(int mRatingIco) {
        this.mRatingIco = mRatingIco;
    }
    public int getRatingIco() {
        return mRatingIco;
    }

    public void setThumbnail(int mThumbnail) {
        this.mThumbnail = mThumbnail;
    }
    public int getThumbnail() {
        return mThumbnail;
    }

    public void setPrice(String mPrice) {
        this.mPrice = mPrice;
    }
    public String getPrice() {
        return mPrice;
    }
    public void setPriceIco(int mPriceIco) {
        this.mPriceIco = mPriceIco;
    }
    public int getPriceIco() {
        return mPriceIco;
    }

    public void setFeature(String mFeature) {
        this.mFeature = mFeature;
    }
    public String getFeature() {
        return mFeature;
    }
    public void setFeatureIco(int mFeatureIco) {
        this.mFeatureIco = mFeatureIco;
    }
    public int getFeatureIco() {
        return mFeatureIco;
    }

    public int getAssetId() {
        return mId;
    }

    public void setAssetId(int mId) {
        this.mId = mId;
    }

    public String getMerk() {
        return mMerk;
    }

    public void setMerk(String mMerk) {
        this.mMerk = mMerk;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String mYear) {
        this.mYear = mYear;
    }

    public String getAssetColor() {
        return mColor;
    }

    public void setAssetColor(String mColor) {
        this.mColor = mColor;
    }

    public String getPlat() {
        return mPlat;
    }

    public void setPlat(String mPlat) {
        this.mPlat = mPlat;
    }

    public String getFuel() {
        return mFuel;
    }

    public void setFuel(String mFuel) {
        this.mFuel = mFuel;
    }

    public String getSeat() {
        return mSeat;
    }

    public void setSeat(String mSeat) {
        this.mSeat = mSeat;
    }

    public String getEngCap() {
        return mEngCap;
    }

    public void setEngCap(String mEngCap) {
        this.mEngCap = mEngCap;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }
    public String getTransm() {
        return mTransm;
    }

    public void setTransm(String mTransm) {
        this.mTransm = mTransm;
    }

    public boolean isAirCon() {
        return mAirCon;
    }

    public void setAirCon(boolean mAirCon) {
        this.mAirCon = mAirCon;
    }

    public boolean isAirBag() {
        return mAirBag;
    }

    public void setAirBag(boolean mAirBag) {
        this.mAirBag = mAirBag;
    }

    public boolean isDriver() {
        return mDriver;
    }

    public void setDriver(boolean mDriver) {
        this.mDriver = mDriver;
    }
}
