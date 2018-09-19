package com.vendor.scrapy.vendorscrapy.ratecard;

/**
 * Created by shadaf on 10/2/18.
 */

public class RateCardSetter {

    String name         = "",
           description  = "",
           unit         = "";

    double price        = 0,
           quantity     = 0,
           minQty       = 0,
           maxQty       = 0;

    int    rateId       = 0;

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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getMinQty() {
        return minQty;
    }

    public void setMinQty(double minQty) {
        this.minQty = minQty;
    }

    public double getMaxQty() {
        return maxQty;
    }

    public void setMaxQty(double maxQty) {
        this.maxQty = maxQty;
    }

    public int getRateId() {
        return rateId;
    }

    public void setRateId(int rateId) {
        this.rateId = rateId;
    }
}
