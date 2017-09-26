package id.rentist.mitrarentist.modul;

/**
 * Created by Nugroho Tri Pambud on 7/13/2017.
 */

public class VoucherModul {
    private int mId;
    private String  mTitle, mDiscount, mStartDate, mEndDate,
        mName, mCode, mDesc, mPercen, mNominal,
        mAmount, mStatus, mAsCategory, mType, sId;

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
    public String getAsCategory() {
        return mAsCategory;
    }
    public void setAsCategory(String mAsCategory) {
        this.mAsCategory = mAsCategory;
    }
    public String getType() {
        return mType;
    }
    public void setType(String mType) {
        this.mType = mType;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String mCode) {
        this.mCode = mCode;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getPercen() {
        return mPercen;
    }

    public void setPercen(String mPercen) {
        this.mPercen = mPercen;
    }

    public String getNominal() {
        return mNominal;
    }

    public void setNominal(String mNominal) {
        this.mNominal = mNominal;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

}
