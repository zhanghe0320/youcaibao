package com.youjuke.optimalmaterialtreasure.app.order;

import android.annotation.SuppressLint;
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
import com.youjuke.optimalmaterialtreasure.app.shopping_cart.ShoppingCartActivity;
import com.youjuke.optimalmaterialtreasure.entity.OrderInfo;
import com.youjuke.optimalmaterialtreasure.retrofit.ApiContstants;
import com.youjuke.optimalmaterialtreasure.retrofit.RequestBean;
import com.youjuke.optimalmaterialtreasure.retrofit.ResponseBean;
import com.youjuke.optimalmaterialtreasure.retrofit.RetrofitManager;
import com.youjuke.optimalmaterialtreasure.retrofit.api.CommonService;

import java.util.ArrayList;

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

public class OrderDetailFragment extends BaseFragment {
    private String type="全部";
    private RecyclerView recycler;
    private Orderdapter orderdapter;
    private ArrayList<OrderInfo> datas = new ArrayList();
    private float off = 0f;
    private static final int MIXOFF = 100;//偏移量小于这个值不刷新
    private ImageView ivNoOrder;
    private TextView tvHint;

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
        orderdapter = new Orderdapter(mContext, datas);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.setAdapter(orderdapter);


        orderdapter.setOnPayClickListener(new Orderdapter.OnPayClickListener() {
            @Override
            public void onPayClickListener(String Order_id) {
                Intent intent = new Intent(mContext, ShoppingCartActivity.class);//付款的监听
                intent.putExtra("order_id",Order_id);
                intent.putExtra("index",1);
                startActivity(intent);
            }
        });
    }


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
                            if (datas.size()!=0){
                                recycler.setVisibility(View.VISIBLE);
                                ivNoOrder.setVisibility(View.GONE);
                                tvHint.setVisibility(View.GONE);
                                orderdapter.setdata(datas);
                                orderdapter.notifyDataSetChanged();
                            }else {
                                recycler.setVisibility(View.GONE);
                                ivNoOrder.setVisibility(View.VISIBLE);
                                tvHint.setVisibility(View.VISIBLE);
                            }
                        } else {
                            ToastUtil.show(mContext, responseBean.getMessage());
                            startActivity(new Intent(mContext, LoginActivity.class));
                        }
                    }
                });
    }

}
