package com.youjuke.optimalmaterialtreasure.app.me;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaeger.library.StatusBarUtil;
import com.youjuke.library.base.BaseActivity;
import com.youjuke.library.base.BaseFragment;
import com.youjuke.library.utils.L;
import com.youjuke.library.utils.ToastUtil;
import com.youjuke.optimalmaterialtreasure.OptimalMaterialTreasureApp;
import com.youjuke.optimalmaterialtreasure.R;
import com.youjuke.optimalmaterialtreasure.app.MainActivity;
import com.youjuke.optimalmaterialtreasure.app.login.LoginActivity;
import com.youjuke.optimalmaterialtreasure.app.order.OrderFragment;
import com.youjuke.optimalmaterialtreasure.app.rechargeable.RechargeableActivity;
import com.youjuke.optimalmaterialtreasure.app.setting.SettingActivity;
import com.youjuke.optimalmaterialtreasure.app.wallet.WalletActivity;
import com.youjuke.optimalmaterialtreasure.entity.MeInfo;
import com.youjuke.optimalmaterialtreasure.retrofit.ApiContstants;
import com.youjuke.optimalmaterialtreasure.retrofit.RequestBean;
import com.youjuke.optimalmaterialtreasure.retrofit.ResponseBean;
import com.youjuke.optimalmaterialtreasure.retrofit.RetrofitManager;
import com.youjuke.optimalmaterialtreasure.retrofit.api.CommonService;
import com.zhy.autolayout.AutoLinearLayout;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 描述：我的界面
 * author：zyb
 * Created by Administrator on 2017/2/8.
 */

public class MeFragment extends BaseFragment implements View.OnClickListener {

    private CircleImageView iv_head_pic;
    private TextView tv_user_name;
    private TextView account_money;
    private MeInfo info;
    private AutoLinearLayout pay;
    private AutoLinearLayout send;
    private AutoLinearLayout receipt;
    private OrderFragment orderFragment;
    private TextView tv_all;
    private TextView tv_site;
    private TextView tv_user_setting;
    private TextView tv_detail;
    private TextView tv_pay;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_me;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
        if (MainActivity.first==0){
            getInfo();
        }
    }

    private void getInfo() {
        params.clear();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        Subscription subscribe = RetrofitManager.getInstance().create(CommonService.class)
                .getData(new RequestBean.JsonMsg(ApiContstants.MY_CENTER, params).toJson())
                .compose(this.<ResponseBean>bindToLifecycle())  //生命周期绑定
                .observeOn(AndroidSchedulers.mainThread())      //设置观察者所在的线程
                .subscribeOn(Schedulers.io())                   //设置被观察者所在的线程
                .subscribe(new Subscriber<ResponseBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.i("数据加载失败！");
                        e.printStackTrace();
                        ToastUtil.show(mContext, "请求数据失败");
                    }

                    @Override
                    public void onNext(ResponseBean responseBean) {
                        L.i("数据Data:" + responseBean.getData());
                        if ("200".equals(responseBean.getStatus())) {
                            info = gson.fromJson(responseBean.getData(), MeInfo.class);
                            Glide.with(mContext).load(info.getLogo())
                                    .placeholder(R.mipmap.btn_tp).into(iv_head_pic);
                            tv_user_name.setText(info.getUsername());
                            account_money.setText(info.getAccount_money());
                        } else {
                            ToastUtil.show(mContext, responseBean.getMessage());
                            startActivity(new Intent(mContext, LoginActivity.class));
                        }
                    }
                });
        compositeSubscription.add(subscribe);
    }

    private void initView() {
        tv_user_setting = $(R.id.tv_user_setting);
        tv_user_setting.setOnClickListener(this);
        iv_head_pic = $(R.id.iv_head_pic);
        iv_head_pic.setOnClickListener(this);
        tv_user_name = $(R.id.tv_user_name);
        account_money = $(R.id.account_money);
        tv_all = $(R.id.tv_all);
        tv_all.setOnClickListener(this);
        tv_site = $(R.id.tv_site);
        tv_site.setOnClickListener(this);
        pay = $(R.id.all_pay);
        pay.setOnClickListener(this);
        send = $(R.id.all_send);
        send.setOnClickListener(this);
        receipt = $(R.id.all_receipt);
        receipt.setOnClickListener(this);
        tv_detail = $(R.id.tv_detail);
        tv_detail.setOnClickListener(this);
        tv_pay=$(R.id.tv_pay);
        tv_pay.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        if (this.isVisible()&&MainActivity.first==0){
            getInfo();
        }
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
       if (!hidden){
           getInfo();
       }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_pay://待付款
                selectOrderIndex(1);
                break;
            case R.id.all_send://待发货
                selectOrderIndex(2);
                break;
            case R.id.all_receipt://待收货
                selectOrderIndex(3);
                break;
            case R.id.tv_all://全部
                selectOrderIndex(0);
                break;
            case R.id.tv_site:
                StatusBarUtil.setColor(getActivity(), Color.parseColor(getString(com.youjuke.library.R.string.theme_color)), 0);
                ((MainActivity) getActivity()).getTabHost().setCurrentTab(2);
                break;
            case R.id.tv_user_setting:
            case R.id.iv_head_pic:
                Intent intent = new Intent(mContext, SettingActivity.class);
                intent.putExtra("meInfo", info);
                startActivity(intent);
                break;
            case R.id.tv_detail:
                startActivity(new Intent(mContext, WalletActivity.class));
                break;
            case R.id.tv_pay:
                startActivity(new Intent(mContext, RechargeableActivity.class));
                break;
        }
    }

    /**
     * @param index 订单的第几个页面
     *              跳转到订单页面
     */
    private void selectOrderIndex(int index) {
        StatusBarUtil.setColor(getActivity(), Color.parseColor(getString(com.youjuke.library.R.string.theme_color)), 0);
        orderFragment = (OrderFragment) ((BaseActivity) mContext).getSupportFragmentManager().findFragmentByTag("订单");//不能提取全局
        ((MainActivity) getActivity()).setCurrent(index);
        ((MainActivity) getActivity()).getTabHost().setCurrentTab(1);
        if (orderFragment != null) {
            orderFragment.getPager().setCurrentItem(index);
        }
    }
}
