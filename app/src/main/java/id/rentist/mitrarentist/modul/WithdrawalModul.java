package id.rentist.mitrarentist.modul;

/**
 * Created by Nugroho Tri Pambud on 7/8/2017.
 */

public class WithdrawalModul {
    private String mCredit, mDebit, mNama, mStatus, mDate;

    public void setNama(String mNama) {
        this.mNama = mNama;
    }
    public String getNama() {
        return mNama;
    }

    public void setCredit(String mCredit) {
        this.mCredit = mCredit;
    }
    public String getCredit() {
        return mCredit;
    }

    public void setDebit(String mDebit) {
        this.mDebit = mDebit;
    }
    public String getDebit() {
        return mDebit;
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
