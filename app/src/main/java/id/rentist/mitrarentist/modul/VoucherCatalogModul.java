package id.rentist.mitrarentist.modul;

/**
 * Created by Nugroho Tri Pambud on 12/1/2017.
 */

public class VoucherCatalogModul {
    private String id, name, description, price, quantity, created_date, thumb;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCreatedDate() {
        return created_date;
    }
    public void setCreatedDate(String created_date) {
        this.created_date = created_date;
    }

    public String getThumb() {
        return thumb;
    }
    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
