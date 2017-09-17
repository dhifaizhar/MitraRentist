package id.rentist.mitrarentist.modul;

/**
 * Created by mdhif on 17/09/2017.
 */

public class ItemCategoryModul {
    private int cId,cThumbnail;
    private String cTitle;

    public int getId() {
        return cId;
    }
    public void setId(int cId) {
        this.cId = cId;
    }

    public int getThumbnail() {
        return cThumbnail;
    }
    public void setThumbnail(int cThumbnail) {
        this.cThumbnail = cThumbnail;
    }

    public String getTitle() {
        return cTitle;
    }
    public void setTitle(String cTitle) {
        this.cTitle = cTitle;
    }
}
