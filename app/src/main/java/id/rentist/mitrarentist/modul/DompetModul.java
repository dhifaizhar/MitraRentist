package id.rentist.mitrarentist.modul;

/**
 * Created by mdhif on 06/07/2017.
 */

public class DompetModul {
    private String mId, mCredit, mTunai, mDebit, mNama, mStatus, mDate;

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

    public void setTunai(String mTunai) {
        this.mTunai = mTunai;
    }
    public String getTunai() {
        return mTunai;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getId() {
        return mId;
    }

    public void setId(String aId) {
        this.mId = aId;
    }
}
