package id.rentist.mitrarentist.modul;

/**
 * Created by mdhif on 12/10/2017.
 */

public class ItemFeatureModul {
    private String mId, mName, mPrice, mQty, mUseQty;

    public String getId() {
        return mId;
    }
    public void setId(String mId) {
        this.mId = mId;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
    public String getName() {
        return mName;
    }

    public String getPrice() {
        return mPrice;
    }
    public void setPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public String getQty() {
        return mQty;
    }

    public void setQty(String mQty) {
        this.mQty = mQty;
    }

    public String getUseQty() {
        return mUseQty;
    }

    public void setUseQty(String mUseQty) {
        this.mUseQty = mUseQty;
    }
}
