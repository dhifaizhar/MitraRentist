package id.rentist.mitrarentist.modul;

/**
 * Created by Nugroho Tri Pambud on 7/8/2017.
 */

public class WithdrawalModul {
    private String mNominal, mDesc, mStatus, mDate;

    public void setNominal(String mNominal) {
        this.mNominal = mNominal;
    }
    public String getNominal() {
        return mNominal;
    }

    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }
    public String getDesc() {
        return mDesc;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }
    public String getDate() {
        return mDate;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getStatus() {
        return mStatus;
    }
}
