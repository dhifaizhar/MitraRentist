package id.rentist.mitrarentist.modul;

/**
 * Created by Nugroho Tri Pambud on 10/11/2017.
 */

public class PriceModul {
    private String mRangeName, mPrice, mStartDate, mEndDate, mDriverPrice;

    public void setRangeName(String mRangeName) {
        this.mRangeName = mRangeName;
    }
    public String getRangeName() {
        return mRangeName;
    }

    public void setPrice(String mPrice) {
        this.mPrice = mPrice;
    }
    public String getPrice() {
        return mPrice;
    }

    public void setDriverPrice(String mDriverPrice) {
        this.mDriverPrice = mDriverPrice;
    }
    public String getDriverPrice() {
        return mDriverPrice;
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
}
