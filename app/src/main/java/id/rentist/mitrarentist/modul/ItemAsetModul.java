package id.rentist.mitrarentist.modul;

/**
 * Created by mdhif on 19/06/2017.
 */

public class ItemAsetModul {
    private String mTitle;
    private String mRating;
    private int mRatingIco;
    private int mThumbnail;
    private int mPriceIco;
    private String mPrice;
    private int mFeatureIco;
    private String mFeature;

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
}
