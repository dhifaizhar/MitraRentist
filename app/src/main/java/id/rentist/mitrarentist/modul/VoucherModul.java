package id.rentist.mitrarentist.modul;

/**
 * Created by Nugroho Tri Pambud on 7/13/2017.
 */

public class VoucherModul {
    private String mTitle;
    private String mDiscount;
    private String mStartDate;
    private String mEndDate;
    private String mAmount;
    private String mStatus;

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
    public String getDiscount() {
        return mDiscount;
    }
    public void setDiscount(String mDiscount) {
        this.mDiscount = mDiscount;
    }
    public String getStartDate() {
        return mStartDate;
    }
    public void setStartDate(String mStartDate) {
        this.mStartDate = mStartDate;
    }
    public String getEndDate() {
        return mEndDate;
    }
    public void setEndDate(String mEndDate) {
        this.mEndDate = mEndDate;
    }
    public String getAmount() {
        return mAmount;
    }
    public void setAmount(String mAmount) {
        this.mAmount = mAmount;
    }
    public String getStatus() {
        return mStatus;
    }
    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }
}
