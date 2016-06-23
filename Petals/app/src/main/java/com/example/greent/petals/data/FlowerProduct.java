package com.example.greent.petals.data;

/**
 * Created by greent on 6/20/16.
 */

public class FlowerProduct {

    String code;
    String description;
    String img_url_lg;
    String img_url_sml;
    String name;
    String dimension;
    String type;
    String occasion;
    double price;

    public FlowerProduct() {

    }

    @Override
    public String toString() {
        return "[code: " + this.code +
                "desc: " + this.description +
                "lg_img: " + this.img_url_lg +
                "sml_img: " + this.img_url_sml +
                "name: " + this.name +
                "dimens: " + this.dimension +
                "type: " + this.type +
                "occasion: " + this.occasion +
                "price: " + this.price +
                "]";
    }
}
