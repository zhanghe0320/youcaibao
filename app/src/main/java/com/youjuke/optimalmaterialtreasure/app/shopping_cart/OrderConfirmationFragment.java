package com.youjuke.optimalmaterialtreasure.app.shopping_cart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.youjuke.library.base.BaseFragment;
import com.youjuke.library.manager.ActivityManager;
import com.youjuke.library.rxbus.RxBus;
import com.youjuke.library.utils.L;
import com.youjuke.library.utils.MoneySimpleFormat;
import com.youjuke.library.utils.SPUtil;
import com.youjuke.library.utils.ToastUtil;
import com.youjuke.optimalmaterialtreasure.OptimalMaterialTreasureApp;
import com.youjuke.optimalmaterialtreasure.R;
import com.youjuke.optimalmaterialtreasure.app.MainActivity;
import com.youjuke.optimalmaterialtreasure.app.login.LoginActivity;
import com.youjuke.optimalmaterialtreasure.app.rechargeable.PaySuccessActivity;
import com.youjuke.optimalmaterialtreasure.app.rechargeable.RechargeableActivity;
import com.youjuke.optimalmaterialtreasure.app.shopping_cart.DialogFragments.PaymentCodeDialogFragment;
import com.youjuke.optimalmaterialtreasure.app.shopping_cart.DialogFragments.SelelctAddressDialogFragment;
import com.youjuke.optimalmaterialtreasure.app.shopping_cart.DialogFragments.SetPasswordDialogFragment;
import com.youjuke.optimalmaterialtreasure.entity.Materials;
import com.youjuke.optimalmaterialtreasure.entity.MaterialsBean;
import com.youjuke.optimalmaterialtreasure.entity.OrderConfirmation;
import com.youjuke.optimalmaterialtreasure.entity.OrderFee;
import com.youjuke.optimalmaterialtreasure.entity.SiteInfo;
import com.youjuke.optimalmaterialtreasure.entity.createOrderInfo;
import com.youjuke.optimalmaterialtreasure.retrofit.ApiContstants;
import com.youjuke.optimalmaterialtreasure.retrofit.RequestBean;
import com.youjuke.optimalmaterialtreasure.retrofit.ResponseBean;
import com.youjuke.optimalmaterialtreasure.retrofit.RetrofitManager;
import com.youjuke.optimalmaterialtreasure.retrofit.api.CommonService;
import com.youjuke.optimalmaterialtreasure.weights.PwdInputView;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 描述: 确认订单页面
 * ------------------------------------------------------------------------
 * 工程:
 * #0000     tian xiao     创建日期: 2017-02-13 14:26
 */
public class OrderConfirmationFragment extends BaseFragment implements View.OnClickListener {

    private SetPasswordDialogFragment setPasswordDialog;
    private PaymentCodeDialogFragment paymentCodeDialog;

    public PaymentCodeDialogFragment getPaymentCodeDialog() {
        return paymentCodeDialog;
    }

    public void setPaymentCodeDialog(PaymentCodeDialogFragment paymentCodeDialog) {
        this.paymentCodeDialog = paymentCodeDialog;
    }

    private ArrayList<SiteInfo> datas;
    private Toolbar toolbar;
    private SelelctAddressDialogFragment selelctAddressDialog;
    private OrderConfirmation order;
    private OrderConfirmationAdapter adapter;
    private List<Materials> materialsList;
    private String orderId;
    private Boolean updataOrder = false;
    private ScrollView scrollView;
    private AutoLinearLayout llAddAddress;
    private AutoLinearLayout llSelectAddress;
    private RecyclerView rvOrder;
    private TextView tvTotalCount;
    private TextView tvAccountMoney;
    private AppCompatButton btnRechargeable;
    private AutoLinearLayout autoLinearLayout;
    private TextView tvTotalPrice;
    private AppCompatButton btnPayment;
    private TextView tvOrderTotalPrice;
    private TextView tvOrderAddTime;
    private TextView tvOrderId;
    private TextView tvOwnerName;
    private TextView tvOwnerMobile;
    private TextView tvAcceptAddress;
    private LinearLayout lyAccountMoney;
    public static boolean isRefresh = false;
    private TextView floorPrice;
    private TextView freight;
    private TextView tv_check_time;
    private String gongdiId;//测试生成订单
    private OrderFee orderFee;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private AutoLinearLayout ll_isOilPaint;

    public static OrderConfirmationFragment newInstance() {
        return new OrderConfirmationFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_order_confirmation;
    }

    @Override
    public void finishCreateView(Bundle state) {
        assignViews();

    }
    class PayOrder {
        private List<String> order_ids;

        public List<String> getOrder_ids() {
            return order_ids;
        }

        public void setOrder_ids(List<String> order_ids) {
            this.order_ids = order_ids;
        }
    }

    private void assignViews() {
        lyAccountMoney = $(R.id.ly_account_money);
        tvAcceptAddress = $(R.id.tv_accept_address);
        tv_check_time = $(R.id.tv_check_time);
        tv_check_time.setOnClickListener(this);
        tvOwnerMobile = $(R.id.tv_owner_mobile);
        tvOwnerName = $(R.id.tv_owner_name);
        tvOrderId = $(R.id.tv_order_id);
        tvOrderAddTime = $(R.id.tv_order_addtime);
        tvOrderTotalPrice = $(R.id.tv_order_total_price);
        scrollView = $(R.id.scrollView);
        llAddAddress = $(R.id.ll_add_address);
        llSelectAddress = $(R.id.ll_select_address);
        rvOrder = $(R.id.rv_order);
        tvTotalCount = $(R.id.tv_total_count);
        tvAccountMoney = $(R.id.tv_account_money);
        btnRechargeable = $(R.id.btn_rechargeable);
        autoLinearLayout = $(R.id.autoLinearLayout);
        tvTotalPrice = $(R.id.tv_total_price);
        btnPayment = $(R.id.btn_payment);
        toolbar = $(R.id.tool_bar);
        floorPrice = $(R.id.floor_price);
        freight = $(R.id.freight);
        ll_isOilPaint=$(R.id.ll_isOilPaint);
        freight.setText(MoneySimpleFormat.getMoneyType("0"));
        floorPrice.setText(MoneySimpleFormat.getMoneyType("0"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxBus.get().post("outShoppingCart", 1);
                updataOrder = false;
                order = null;
            }
        });
        btnRechargeable.setOnClickListener(this);
        btnPayment.setOnClickListener(this);
        llAddAddress.setOnClickListener(this);
        llSelectAddress.setOnClickListener(this);
        adapter = new OrderConfirmationAdapter(mContext, null);
        rvOrder.setLayoutManager(new LinearLayoutManager(mContext));
        rvOrder.setItemAnimator(new DefaultItemAnimator());

        rvOrder.setAdapter(adapter);
        materialsList = new ArrayList<>();

        setPasswordDialog = new SetPasswordDialogFragment(mContext);
        setPasswordDialog.setOnClickListener(new SetPasswordDialogFragment.OnClickListener() {
            @Override
            public void setOnClick(final AppCompatEditText etPassword, final AppCompatEditText etPayPwd,
                                   final AppCompatEditText etPayPwd2, AppCompatButton button) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        if (inputmanger.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)) {
                            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        if (etPassword.getText().toString().isEmpty()) {
                            ToastUtil.show(mContext, "登录密码不能为空");
                        } else if (etPayPwd.getText().toString().isEmpty() || etPayPwd.getText().toString().length() != 6) {
                            ToastUtil.show(mContext, "支付密码不能为空,且必须是6位数字");
                        } else if (etPayPwd2.getText().toString().isEmpty() || etPayPwd2.getText().toString().length() != 6) {
                            ToastUtil.show(mContext, "重复密码不能为空,且必须是6位数字");
                        } else if (!etPayPwd.getText().toString().equals(etPayPwd2.getText().toString())) {
                            ToastUtil.show(mContext, "支付密码两次输入不相同");
                        } else {
                            setPaypwd(etPassword.getText().toString(), etPayPwd.getText().toString(),
                                    etPayPwd2.getText().toString());
                        }
                    }
                });
            }
        });
    }

    /**
     * 选择地址
     */
    private void selelctAddress() {
        params.clear();
        myDialog.show();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        compositeSubscription.add(RetrofitManager.getInstance().create(CommonService.class)
                .getData(new RequestBean.JsonMsg(ApiContstants.ADDRESS_LIST, params).toJson())
                .compose(this.<ResponseBean>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBean>() {
                               @Override
                               public void onCompleted() {
                                   myDialog.dismiss();
                               }

                               @Override
                               public void onError(Throwable e) {
                                   myDialog.dismiss();
                               }

                               @Override
                               public void onNext(ResponseBean responseBean) {
                                   //成功
                                   if (responseBean.getStatus().equals("200")) {
                                       datas = gson.fromJson(responseBean.getData(), new TypeToken<ArrayList<SiteInfo>>() {
                                       }.getType());
                                       selelctAddressDialog = new SelelctAddressDialogFragment(mContext, datas);
                                       selelctAddressDialog.setOnClickListener(new SelelctAddressDialogFragment.OnClickListener() {
                                           @Override
                                           public void setOnClick(int gd_id, SiteInfo siteInfo) {
                                               gongdiId = gd_id + "";
                                               orderSelectAddress(gd_id, siteInfo);
                                           }
                                       });
                                       selelctAddressDialog.show(getFragmentManager(), "selectAddressDialog");
                                   } else if (responseBean.getStatus().equals("400") && responseBean.getError().equals("020")) {
                                       ToastUtil.show(getApplicationContext(), responseBean.getMessage());
                                       startActivity(new Intent(mContext, LoginActivity.class));
                                   }
                               }
                           }
                )
        );
    }

    /**
     * 接口说明：订单选择送货地址返回 运费、搬楼费、小计
     * 接口调用地址为：
     * 外侧：http://api.51youjuke.com/ycb/order_select_address
     * 返回参数:
     * total_shipping_fee	总运费
     * total_stair_fee	总搬楼费
     * all_total_price	小计
     *
     * @param gd_id
     */
    private void orderSelectAddress(int gd_id, SiteInfo siteInfo) {

        //更新地址
        llAddAddress.setVisibility(View.GONE);
        llSelectAddress.setVisibility(View.VISIBLE);
        tvOwnerName.setText(siteInfo.getYezhu_name());
        tvOwnerMobile.setText(siteInfo.getYezhu_mobile());
        tvAcceptAddress.setText(Html.fromHtml(siteInfo.getAddress()));
        params.clear();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        params.put("gongdi_id", gd_id + "");
        compositeSubscription.add(
                RetrofitManager.getInstance().create(CommonService.class)
                        .getData(new RequestBean.JsonMsg("order_select_address", params).toJson())
                        .compose(this.<ResponseBean>bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<ResponseBean>() {
                            @Override
                            public void call(ResponseBean responseBean) {
                                if ("200".equals(responseBean.getStatus())) {
                                    orderFee = gson.fromJson(responseBean.getData(), new TypeToken<OrderFee>() {
                                    }.getType());
                                    updatePrice(orderFee);
                                } else if ("400".equals(responseBean.getData())) {
                                    ToastUtil.show(mContext, responseBean.getMessage().trim());
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        })
        );
    }

    /**
     * 刷新搬楼费 订单合计
     *
     * @param orderFee
     */
    @MainThread
    private void updatePrice(@NonNull OrderFee orderFee) {
        floorPrice.setText(orderFee.getTotal_stair_fee().trim());//搬楼费
        freight.setText(orderFee.getTotal_shipping_fee().trim());//总运费
        tvOrderTotalPrice.setText(orderFee.getAll_total_price().trim());//小计
        tvTotalPrice.setText(orderFee.getAll_total_price().trim());
    }

    /**
     * 确认订单
     */
    public void getOrderInfo() {
        params.clear();
        myDialog.show();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        params.put("mobile", OptimalMaterialTreasureApp.user.getMobile());
        if (order != null) {
            for (MaterialsBean material : order.getMaterials()) {
                Materials materials = new Materials();
                materials.setMatId(material.getMaterialId());
                materials.setCount(material.getMaterialCount());
                materialsList.add(materials);
            }
            params.put("materials", materialsList);
        }
        compositeSubscription.add(
                RetrofitManager.getInstance().create(CommonService.class)
                        .getData(new RequestBean.JsonMsg("confirm_order", params).toJson())
                        .compose(this.<ResponseBean>bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<ResponseBean>() {
                            @Override
                            public void call(ResponseBean responseBean) {
                                myDialog.dismiss();
                                L.d("填充数据" + "200".equals(responseBean.getStatus()));
                                if ("200".equals(responseBean.getStatus())) {
                                    L.d("解析json");
                                    order = gson.fromJson(responseBean.getData(), OrderConfirmation.class);
                                    L.d("填充数据");
                                    confirmOrder();
                                } else if ("400".equals(responseBean.getStatus())) {
                                    ToastUtil.show(mContext, responseBean.getMessage());
                                    if (responseBean.getError().equals("020") || responseBean.getError().equals("007") || responseBean.getError().equals("010"))
                                        startActivity(new Intent(mContext, LoginActivity.class));
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                L.e(throwable.toString());
                                myDialog.dismiss();
                            }
                        })
        );
    }

    /**
     * 填充确认订单
     */
    private void confirmOrder() {
        //余额
        tvAccountMoney.setText("(" + MoneySimpleFormat.getCustomType("", order.getTotalInfo().getAccountMoney().trim()) + ")");//钱包余额
        if (!updataOrder) {
            if ("1".equals(order.getTotalInfo().getIsOilPaint())){
                ll_isOilPaint.setVisibility(View.VISIBLE);
            }else {
                ll_isOilPaint.setVisibility(View.GONE);
            }
            adapter.addAll(order.getMaterials());
            adapter.notifyDataSetChanged();
            tvTotalCount.setText(order.getTotalInfo().getTotalCount().trim());//商品数量
            tvTotalPrice.setText(MoneySimpleFormat.getMoneyType(order.getTotalInfo().getTotalPrice().trim()));//合计金额
            tvOrderTotalPrice.setText(MoneySimpleFormat.getMoneyType(order.getTotalInfo().getTotalPrice().trim()));
        }
    }


    /**
     * 设置支付密码
     */
    public void setPaypwd(String etPassword, String etPayPwd, String etPayPwd2) {
        params.clear();
        myDialog.show();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        params.put("password", etPassword);
        params.put("paypwd", etPayPwd);
        params.put("paypwd2", etPayPwd2);
        compositeSubscription.add(
                RetrofitManager.getInstance().create(CommonService.class)
                        .getData(new RequestBean.JsonMsg("set_paypwd", params).toJson())
                        .compose(this.<ResponseBean>bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<ResponseBean>() {
                            @Override
                            public void call(ResponseBean responseBean) {
                                L.d("订单详情:" + responseBean.getData());
                                if ("200".equals(responseBean.getStatus())) {
                                    ToastUtil.show(mContext, responseBean.getMessage());
                                    OptimalMaterialTreasureApp.user.setHas_paypwd("1");
                                    SPUtil.setObject(getApplicationContext(), "user", OptimalMaterialTreasureApp.user);
                                    setPasswordDialog.dismiss();
                                } else if ("400".equals(responseBean.getStatus())) {
                                    ToastUtil.show(mContext, responseBean.getMessage());
                                }
                                myDialog.dismiss();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                myDialog.dismiss();
                            }
                        })
        );
    }

    /**
     * 支付
     */
    public void ordersPay(String paypwd, List<String> orderIds) {
        L.d("开始支付");
        params.clear();
        myDialog.show();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        params.put("mobile", OptimalMaterialTreasureApp.user.getMobile());
        params.put("paypwd", paypwd);
        params.put("orderIds", orderIds);
        compositeSubscription.add(
                RetrofitManager.getInstance().create(CommonService.class)
                        .getData(new RequestBean.JsonMsg("orders_pay", params).toJson())
                        .compose(this.<ResponseBean>bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<ResponseBean>() {
                            @Override
                            public void call(ResponseBean responseBean) {
                                L.d("订单详情:" + responseBean.getData());
                                if ("200".equals(responseBean.getStatus())) {
                                    paymentCodeDialog.dismiss();
                                    startActivity(new Intent(mContext, PaySuccessActivity.class));
                                    ActivityManager.getInstance().finishActivity(ShoppingCartActivity.class);
                                } else if ("400".equals(responseBean.getStatus())) {
                                    ToastUtil.show(mContext, responseBean.getMessage());
                                }
                                myDialog.dismiss();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                myDialog.dismiss();
                            }
                        })
        );
    }



    /***
     * 提示
     *
     * @param title
     */
    public void showDialog(String title, final String MatId) {
        builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View layout = inflater.inflate(R.layout.alert_dialog_delect_order, null);
        builder.setView(layout);
        dialog = builder.show();
        TextView textViewGoBack = (TextView) layout.findViewById(R.id.text_go_back);
        TextView textViewCancel = (TextView) layout.findViewById(R.id.text_cancel);
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextView textViewTiele = (TextView) layout.findViewById(R.id.textView_title);
        textViewTiele.setText(title);
        textViewGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, RechargeableActivity.class));
                dialog.dismiss();
            }
        });
    }

    /**
     * 提交订单
     */
    private void createNewOrder() {

        params.clear();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        params.put("gongdi_id", gongdiId);
        List<createOrderInfo> strings = new ArrayList<>();
        for (int i = 0; i < adapter.getDatas().size(); i++) {
            createOrderInfo mOrderInfo = new createOrderInfo();
            mOrderInfo.setMat_id(adapter.getDatas().get(i).getMaterialId());
            mOrderInfo.setMat_txt(adapter.getMatTxt().get(i));
            strings.add(mOrderInfo);
        }
        params.put("material_list", strings);
        compositeSubscription.add(
                RetrofitManager.getInstance().create(CommonService.class)
                        .getData(new RequestBean.JsonMsg("create_new_order", params).toJson())
                        .compose(this.<ResponseBean>bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<ResponseBean>() {
                            @Override
                            public void call(final ResponseBean responseBean) {
                                if ("200".equals(responseBean.getStatus())) {
                                    L.d("订单创建成功");
                                    //支付密码
                                    paymentCodeDialog = new PaymentCodeDialogFragment(mContext, MoneySimpleFormat.getMoneyType(tvTotalPrice.getText().toString()));

                                    paymentCodeDialog.getIvCancel().setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            MainActivity.second=3;
                                            paymentCodeDialog.dismiss();
                                        }
                                    });
                                    paymentCodeDialog.setOnClickListener(new PaymentCodeDialogFragment.OnClickListener() {
                                        @Override
                                        public void setOnClick(PwdInputView pwdInputView) {
                                            pwdInputView.setInputCallBack(new PwdInputView.InputCallBack() {
                                                @Override
                                                public void onInputFinished(String pwd) {
                                                    PayOrder payOrder = gson.fromJson(responseBean.getData(), PayOrder.class);
                                                    ordersPay(pwd, payOrder.getOrder_ids());
                                                }
                                            });
                                        }

                                        @Override
                                        public void setOnKeyBlack() {
                                            MainActivity.second=3;
                                            paymentCodeDialog.dismiss();
                                        }
                                    });
                                    paymentCodeDialog.show(getFragmentManager(), "setPasswordDialog");

                                } else if ("400".equals(responseBean.getStatus())) {
                                    ToastUtil.show(mContext, responseBean.getMessage());
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                L.e(throwable.toString());

                            }
                        })
        );

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updataOrder = !hidden;
            //获取购物车订单
            getOrderInfo();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        order = null;
        updataOrder = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取购物车订单
        getOrderInfo();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //选择地址
            case R.id.ll_add_address:
            case R.id.ll_select_address:
                selelctAddress();
                break;
            //支付
            case R.id.btn_payment:
                if (llSelectAddress.getVisibility() == View.VISIBLE) {
                    //进行支付
                    if ("0".equals(OptimalMaterialTreasureApp.user.getHas_paypwd())) {
                        //设置支付密码
                        setPasswordDialog.show(getFragmentManager(), "setPasswordDialog");
                    } else if ("1".equals(OptimalMaterialTreasureApp.user.getHas_paypwd())) {
                        double accountMoney = Double.parseDouble(order.getTotalInfo().getAccountMoney());
                        double totalPrice = Double.parseDouble(orderFee.getAll_total_price().trim());
                        if (accountMoney >= totalPrice) {
                            createNewOrder();

                        } else {
                            showDialog("余额不足,请先充值", "");
                        }
                    }
                } else {
                    ToastUtil.show(mContext, "请选择施工地址");
                }
                break;
            case R.id.btn_rechargeable:
                //充值
                startActivity(new Intent(mContext, RechargeableActivity.class));
                break;
            case R.id.tv_check_time://查看时间
                new DialogTime(mContext).show();
                break;
        }
    }

}
