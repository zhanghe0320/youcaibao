package com.youjuke.optimalmaterialtreasure.entity;

/**
 * 描述: TODO
 * ------------------------------------------------------------------------
 * 工程:
 * #0000     tian xiao     创建日期: 2017-02-20 14:00
 */
public class MaterialsBean {
    /**
     * "brandName": "爱康保利",
     * "freight": "0",
     * "materialColor": "无",
     * "materialCount": "1",
     * "materialId": "1179",
     * "materialModel": "无",
     * "materialName": "PPR抗菌管",
     * "materialNorms": "25*3.5",
     * "materialPrice": "10.79",
     * "stairwayFee": "0",
     * "tip": "买家留言：测试测试"
     */

    private String brandName;
    private String freight;
    private String materialColor;
    private String materialCount;
    private String materialId;
    private String materialModel;
    private String materialName;
    private String materialNorms;
    private String materialPrice;
    private String stairwayFee;
    private String tip;

    @Override
    public String toString() {
        return "MaterialsBean{" +
                "brandName='" + brandName + '\'' +
                ", freight='" + freight + '\'' +
                ", materialColor='" + materialColor + '\'' +
                ", materialCount='" + materialCount + '\'' +
                ", materialId='" + materialId + '\'' +
                ", materialModel='" + materialModel + '\'' +
                ", materialName='" + materialName + '\'' +
                ", materialNorms='" + materialNorms + '\'' +
                ", materialPrice='" + materialPrice + '\'' +
                ", stairwayFee='" + stairwayFee + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    public String getMaterialColor() {
        return materialColor;
    }

    public void setMaterialColor(String materialColor) {
        this.materialColor = materialColor;
    }

    public String getMaterialCount() {
        return materialCount;
    }

    public void setMaterialCount(String materialCount) {
        this.materialCount = materialCount;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialModel() {
        return materialModel;
    }

    public void setMaterialModel(String materialModel) {
        this.materialModel = materialModel;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialNorms() {
        return materialNorms;
    }

    public void setMaterialNorms(String materialNorms) {
        this.materialNorms = materialNorms;
    }

    public String getMaterialPrice() {
        return materialPrice;
    }

    public void setMaterialPrice(String materialPrice) {
        this.materialPrice = materialPrice;
    }

    public String getStairwayFee() {
        return stairwayFee;
    }

    public void setStairwayFee(String stairwayFee) {
        this.stairwayFee = stairwayFee;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}
