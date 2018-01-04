package id.rentist.mitrarentist.modul;

/**
 * Created by Nugroho Tri Pambud on 12/12/2017.
 */

public class ItemDeliveryPrice {
    private String id, category, max_distance, price_per_km, distace_free;
//    int category[];

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getCategory() {
        return category;
    }

    public void setMaxDistance(String max_distance) {
        this.max_distance = max_distance;
    }
    public String getMaxDistance() {
        return max_distance;
    }

    public void setPricePerKM(String price_per_km) {
        this.price_per_km = price_per_km;
    }
    public String getPricePerKM() {
        return price_per_km;
    }

    public void setDistaceFree(String distace_free) {
        this.distace_free = distace_free;
    }
    public String getDistaceFree() {
        return distace_free;
    }

}
