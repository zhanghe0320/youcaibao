package com.youjuke.optimalmaterialtreasure.entity;

/**
 * 
 * Created by Administrator on 2017/2/17.
 */

public class WalletInfo {
    private int deal_id;
    private String deal_money;
    private String deal_time;
    private int deal_type;
    private String no;
    private String order_id;
    private String pay_tool;

    public int getDeal_id() {
        return deal_id;
    }

    public void setDeal_id(int deal_id) {
        this.deal_id = deal_id;
    }

    public String getDeal_money() {
        return deal_money;
    }

    public void setDeal_money(String deal_money) {
        this.deal_money = deal_money;
    }

    public String getDeal_time() {
        return deal_time;
    }

    public void setDeal_time(String deal_time) {
        this.deal_time = deal_time;
    }

    public int getDeal_type() {
        return deal_type;
    }

    public void setDeal_type(int deal_type) {
        this.deal_type = deal_type;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPay_tool() {
        return pay_tool;
    }

    public void setPay_tool(String pay_tool) {
        this.pay_tool = pay_tool;
    }
}
