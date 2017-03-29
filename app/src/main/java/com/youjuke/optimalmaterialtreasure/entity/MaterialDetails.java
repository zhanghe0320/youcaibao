package com.youjuke.optimalmaterialtreasure.entity;

/**
 * 描述：选择规格商品的具体商品的实体类
 * author：zyb
 * Created by Administrator on 2017/2/15.
 */
public class MaterialDetails {
    private String material_color;
    private String material_model;
    private String material_norms;
    private String material_price;
    private int material_id;
    private String brand;
    private String title;
    private String full_free;//满多少钱见面运费
    private String stair_fee;//楼梯费用
    private String lift_fee;//电梯费用
    private String freight;//运费
    private int  is_oil_paint;//是否是油漆涂料   0  不是    1 是

    public String getFull_free() {
        return full_free;
    }

    public void setFull_free(String full_free) {
        this.full_free = full_free;
    }

    public String getStair_fee() {
        return stair_fee;
    }

    public void setStair_fee(String stair_fee) {
        this.stair_fee = stair_fee;
    }

    public String getLift_fee() {
        return lift_fee;
    }

    public void setLift_fee(String lift_fee) {
        this.lift_fee = lift_fee;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    public int getIs_oil_paint() {
        return is_oil_paint;
    }

    public void setIs_oil_paint(int is_oil_paint) {
        this.is_oil_paint = is_oil_paint;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMaterial_color() {
        return material_color;
    }

    public void setMaterial_color(String material_color) {
        this.material_color = material_color;
    }

    public String getMaterial_model() {
        return material_model;
    }

    public void setMaterial_model(String material_model) {
        this.material_model = material_model;
    }

    public String getMaterial_norms() {
        return material_norms;
    }

    public void setMaterial_norms(String material_norms) {
        this.material_norms = material_norms;
    }

    public String getMaterial_price() {
        return material_price;
    }

    public void setMaterial_price(String material_price) {
        this.material_price = material_price;
    }

    public int getMaterial_id() {
        return material_id;
    }

    public void setMaterial_id(int material_id) {
        this.material_id = material_id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
