package com.youjuke.optimalmaterialtreasure.app.order;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.youjuke.library.base.BaseFragment;
import com.youjuke.library.utils.L;
import com.youjuke.library.utils.ToastUtil;
import com.youjuke.library.weights.MyDialog;
import com.youjuke.optimalmaterialtreasure.OptimalMaterialTreasureApp;
import com.youjuke.optimalmaterialtreasure.R;
import com.youjuke.optimalmaterialtreasure.app.login.LoginActivity;
import com.youjuke.optimalmaterialtreasure.app.rechargeable.PaySuccessActivity;
import com.youjuke.optimalmaterialtreasure.app.rechargeable.RechargeableActivity;
import com.youjuke.optimalmaterialtreasure.app.shopping_cart.DialogFragments.PaymentCodeDialogFragment;
import com.youjuke.optimalmaterialtreasure.entity.OrderInfo;
import com.youjuke.optimalmaterialtreasure.retrofit.ApiContstants;
import com.youjuke.optimalmaterialtreasure.retrofit.MSubscriber;
import com.youjuke.optimalmaterialtreasure.retrofit.RequestBean;
import com.youjuke.optimalmaterialtreasure.retrofit.ResponseBean;
import com.youjuke.optimalmaterialtreasure.retrofit.RetrofitManager;
import com.youjuke.optimalmaterialtreasure.retrofit.api.CommonService;
import com.youjuke.optimalmaterialtreasure.weights.PwdInputView;
import com.youjuke.optimalmaterialtreasure.weights.SweetAlertDialog;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.ListenerStubs;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 描述：订单界面里面的详情fragment
 * author：zyb
 * Created by Administrator on 2017/2/15.
 */

public class OrderDetailFragment extends BaseFragment implements OrderAdapter.OnClickListener {
    private String type = "全部";
    private RecyclerView recycler;
    private OrderAdapter orderAdapter;
    private ArrayList<OrderInfo> datas = new ArrayList();
    private float off = 0f;
    private static final int MIXOFF = 100;//偏移量小于这个值不刷新
    private ImageView ivNoOrder;
    private TextView tvHint;
    private PaymentCodeDialogFragment paymentCodeDialog;
    private String money = "";
    String OrderId = "";
    SweetAlertDialog.Builder builder;

    public OrderDetailFragment() {
    }

    private Context context;

    @SuppressLint("ValidFragment")
    public OrderDetailFragment(Context context, String type) {
        this.type = type;
        this.context = context;
    }

    @Override

    public int getLayoutResId() {
        return R.layout.fragment_order_detail;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
        initScroll();
    }

    private void initScroll() {
        IOverScrollDecor iOverScrollDecor = OverScrollDecoratorHelper.setUpOverScroll(recycler, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        View view = iOverScrollDecor.getView();
        view.setBackgroundResource(R.color.back_f0f0f0);
        iOverScrollDecor.setOverScrollStateListener(new ListenerStubs.OverScrollStateListenerStub() {
            @Override
            public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {
                super.onOverScrollStateChange(decor, oldState, newState);
                if (oldState == 1) {
                    if (off > MIXOFF) {
                        initData();
                    }
                }
            }
        });
        //滑动状态监听，state 0-1-3 往下滑动 0-2-3往上滑动 offset滑动的偏移量
        iOverScrollDecor.setOverScrollUpdateListener(new IOverScrollUpdateListener() {
            @Override
            public void onOverScrollUpdate(IOverScrollDecor decor, int state, float offset) {
                off = offset;
            }
        });
    }

    private void initView() {
        recycler = $(R.id.rlv_recycler);
        ivNoOrder = $(R.id.iv_no_orde);
        tvHint = $(R.id.tv_hin);
        orderAdapter = new OrderAdapter(mContext, datas);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.setAdapter(orderAdapter);
        orderAdapter.setOnClickListener(this);
    }

    /**
     * @param Order_id 订单id
     * @param payMoney 支付金额
     *                 描述：付款
     */
    @Override
    public void onPayClickListener(String Order_id, String payMoney,int position) {
                /*Intent intent = new Intent(mContext, ShoppingCartActivity.class);//付款的监听
                intent.putExtra("order_id",Order_id);
                intent.putExtra("index",1);
                startActivity(intent);*/
        OrderId = Order_id;
        money = payMoney;
        //进行支付
        payDialog();

    }

    /**
     * @param Order_id 订单id
     *                 描述：确认收货
     */
    @Override
    public void onConfirmReceipt(String Order_id, final int position) {
        params.clear();
        myDialog.show();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        params.put("order_id", Order_id);
        compositeSubscription.add(RetrofitManager.getInstance().create(CommonService.class)
                .getData(new RequestBean.JsonMsg(ApiContstants.ORDER_RECEIVE, params).toJson())
                .compose(this.<ResponseBean>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new MSubscriber(mContext, myDialog) {
                    @Override
                    public void mOnNextCorrect(ResponseBean responseBean) {
                        datas.get(position).setOrder_status("交易成功");
                        orderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void mOnNextFault(ResponseBean responseBean) {
                        ToastUtil.show(mContext, responseBean.getMessage());
                    }
                }));
    }

    /**
     * @param Order_id 订单id
     *                 描述：删除订单
     */
    @Override
    public void onDeleteOrder(String Order_id, final int position) {
        params.clear();
        myDialog.show();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        params.put("order_id", Order_id);
        compositeSubscription.add(RetrofitManager.getInstance().create(CommonService.class)
                .getData(new RequestBean.JsonMsg(ApiContstants.ORDER_REMOVE, params).toJson())
                .compose(this.<ResponseBean>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new MSubscriber(mContext, myDialog) {
                    @Override
                    public void mOnNextCorrect(ResponseBean responseBean) {
                        datas.remove(position);
                        orderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void mOnNextFault(ResponseBean responseBean) {
                        ToastUtil.show(mContext, responseBean.getMessage());
                    }
                }));
    }

    @Override
    public void onItemClickListener(String Order_id) {
        Intent intent = new Intent(mContext, ExamineOrderDetailsActivity.class);
        intent.putExtra("order_id", Order_id);
        startActivity(intent);
    }

    /**
     * 输入支付密码
     */
    public void payDialog() {
        //支付密码
        paymentCodeDialog = new PaymentCodeDialogFragment(mContext, money);
        paymentCodeDialog.getIvCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentCodeDialog.dismiss();
            }
        });
        paymentCodeDialog.setOnClickListener(new PaymentCodeDialogFragment.OnClickListener() {
            @Override
            public void setOnClick(PwdInputView pwdInputView) {
                pwdInputView.setInputCallBack(new PwdInputView.InputCallBack() {
                    @Override
                    public void onInputFinished(String payPwd) {
                        //支付
                        ordersPay(payPwd);
                    }
                });
            }

            @Override
            public void setOnKeyBlack() {
                paymentCodeDialog.dismiss();
            }
        });
        paymentCodeDialog.show(getFragmentManager(), "setPasswordDialog");
    }

    /**
     * 支付
     */
    public void ordersPay(String paypwd) {
        params.clear();
        myDialog.show();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        params.put("mobile", OptimalMaterialTreasureApp.user.getMobile());
        params.put("paypwd", paypwd);
        List<String> list = new ArrayList<>();
        list.add(OrderId);
        params.put("orderIds", list);
        compositeSubscription.add(
                RetrofitManager.getInstance().create(CommonService.class)
                        .getData(new RequestBean.JsonMsg("orders_pay", params).toJson())
                        .compose(this.<ResponseBean>bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new MSubscriber(mContext, myDialog) {
                                       @Override
                                       public void mOnNextCorrect(ResponseBean responseBean) {
                                           paymentCodeDialog.dismiss();
                                           startActivity(new Intent(mContext, PaySuccessActivity.class));
                                       }

                                       @Override
                                       public void mOnNextFault(ResponseBean responseBean) {
                                           paymentCodeDialog.dismiss();
                                           if (responseBean.getError().equals("00005")) {//余额不足
                                               showDialogRechargeable();//充值
                                               return;
                                           }
                                           ToastUtil.show(mContext, responseBean.getMessage());
                                       }
                                   }
//                                new Action1<ResponseBean>() {
//                            @Override
//                            public void call(ResponseBean responseBean) {
//                                L.d("订单详情:" + responseBean.getData());
//                                if ("200".equals(responseBean.getStatus())) {
//                                    paymentCodeDialog.dismiss();
//                                    startActivity(new Intent(mContext, PaySuccessActivity.class));
//                                    ActivityManager.getInstance().finishActivity(ShoppingCartActivity.class);
//                                } else if ("400".equals(responseBean.getStatus())) {
//                                    ToastUtil.show(mContext, responseBean.getMessage());
//                                }
//                                myDialog.dismiss();
//                            }
//                        }, new Action1<Throwable>() {
//                            @Override
//                            public void call(Throwable throwable) {
//                                myDialog.dismiss();
//                            }
//                        }
                        )
        );
    }


    /**
     * 初始化数据
     */
    public void initData() {
        params.clear();
        final MyDialog myDialog = new MyDialog(context);
        myDialog.show();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        params.put("type", type);
        RetrofitManager.getInstance().create(CommonService.class)
                .getData(new RequestBean.JsonMsg(ApiContstants.ORDER_LIST, params).toJson())
                .compose(this.<ResponseBean>bindToLifecycle())  //生命周期绑定
                .observeOn(AndroidSchedulers.mainThread())      //设置观察者所在的线程
                .subscribeOn(Schedulers.io())                   //设置被观察者所在的线程
                .subscribe(new Subscriber<ResponseBean>() {
                    @Override
                    public void onCompleted() {
                        myDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.i("数据加载失败！");
                        e.printStackTrace();
                        myDialog.dismiss();
                        ToastUtil.show(mContext, "请求数据失败");
                    }

                    @Override
                    public void onNext(ResponseBean responseBean) {
                        L.i("数据Data:" + responseBean.getData());
                        if ("200".equals(responseBean.getStatus())) {
                            datas = gson.fromJson(responseBean.getData(), new TypeToken<ArrayList<OrderInfo>>() {
                            }.getType());
                            if (datas.size() != 0) {
                                recycler.setVisibility(View.VISIBLE);
                                ivNoOrder.setVisibility(View.GONE);
                                tvHint.setVisibility(View.GONE);
                                orderAdapter.setdata(datas);
                                orderAdapter.notifyDataSetChanged();
                            } else {
                                recycler.setVisibility(View.GONE);
                                ivNoOrder.setVisibility(View.VISIBLE);
                                tvHint.setVisibility(View.VISIBLE);
                            }
                        } else {
                            ToastUtil.show(mContext, responseBean.getMessage());
                            if (responseBean.getError().equals("020") || responseBean.getError().equals("007") || responseBean.getError().equals("010"))
                                startActivity(new Intent(mContext, LoginActivity.class));
                        }
                    }
                });
    }

    /***
     * 提示
     */
    public void showDialogRechargeable() {
        if (builder == null) {
            builder = new SweetAlertDialog.Builder(mContext);
        }
        builder.setCancelable(true)
                .setMessage("余额不足，请前去充值")
                .setTitle("提示")
                .setCancelableoutSide(false)
                .setPositiveButton("充值", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        startActivity(new Intent(mContext, RechargeableActivity.class));
                        dialog.dismiss();
                    }
                }).show();
    }

}
