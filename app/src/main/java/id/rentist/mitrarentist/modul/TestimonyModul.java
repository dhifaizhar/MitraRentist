package id.rentist.mitrarentist.modul;

/**
 * Created by mdhif on 18/06/2017.
 */

public class TestimonyModul {
    private String tContent, tDate, tMember;
    private float cleanliness, neatness, honesty, comunication;

    public String getMember() {
        return tMember;
    }
    public void setMember(String tMember) {
        this.tMember = tMember;
    }

    public String getContent() {
        return tContent;
    }
    public void setContent(String tContent) {
        this.tContent = tContent;
    }

    public String getDate() {
        return tDate;
    }
    public void setDate(String tDate) {
        this.tDate = tDate;
    }

    public float getCleanliness() { return cleanliness;}
    public void setCleanliness(float cleanliness) { this.cleanliness = cleanliness;}

    public float getNeatness() { return neatness;}
    public void setNeatness(float neatness) { this.neatness = neatness;}

    public float getHonesty() { return honesty;}
    public void setHonesty(float honesty) { this.honesty = honesty;}

    public float getComunication() { return comunication;}
    public void setComunication(float comunication) { this.comunication = comunication;}
}
