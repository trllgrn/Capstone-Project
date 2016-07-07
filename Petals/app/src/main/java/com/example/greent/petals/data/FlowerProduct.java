package com.example.greent.petals.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by greent on 6/20/16.
 */

public class FlowerProduct implements Parcelable {

    public String code;
    public String description;
    public String img_url_lg;
    public String img_url_sml;
    public String name;
    public String dimension;
    public String type;
    public String occasion;
    public double price;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.description);
        dest.writeString(this.img_url_lg);
        dest.writeString(this.img_url_sml);
        dest.writeString(this.name);
        dest.writeString(this.dimension);
        dest.writeString(this.type);
        dest.writeString(this.occasion);
        dest.writeDouble(this.price);
    }

    protected FlowerProduct(Parcel in) {
        this.code = in.readString();
        this.description = in.readString();
        this.img_url_lg = in.readString();
        this.img_url_sml = in.readString();
        this.name = in.readString();
        this.dimension = in.readString();
        this.type = in.readString();
        this.occasion = in.readString();
        this.price = in.readDouble();
    }

    public static final Parcelable.Creator<FlowerProduct> CREATOR = new Parcelable.Creator<FlowerProduct>() {
        @Override
        public FlowerProduct createFromParcel(Parcel source) {
            return new FlowerProduct(source);
        }

        @Override
        public FlowerProduct[] newArray(int size) {
            return new FlowerProduct[size];
        }
    };
}
