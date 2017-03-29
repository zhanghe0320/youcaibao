package com.youjuke.optimalmaterialtreasure.app.setting;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youjuke.library.base.BaseActivity;
import com.youjuke.library.manager.ActivityManager;
import com.youjuke.library.utils.L;
import com.youjuke.library.utils.SPUtil;
import com.youjuke.library.utils.ToastUtil;
import com.youjuke.optimalmaterialtreasure.OptimalMaterialTreasureApp;
import com.youjuke.optimalmaterialtreasure.R;
import com.youjuke.optimalmaterialtreasure.app.MainActivity;
import com.youjuke.optimalmaterialtreasure.entity.MeInfo;
import com.youjuke.optimalmaterialtreasure.retrofit.ApiContstants;
import com.youjuke.optimalmaterialtreasure.retrofit.RequestBean;
import com.youjuke.optimalmaterialtreasure.retrofit.ResponseBean;
import com.youjuke.optimalmaterialtreasure.retrofit.RetrofitManager;
import com.youjuke.optimalmaterialtreasure.retrofit.api.CommonService;
import com.youjuke.optimalmaterialtreasure.weights.SweetAlertDialog;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 描述：设置界面
 * author：zyb
 * Created by Administrator on 2017/2/17.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private MeInfo meInfo;
    private CircleImageView ci_head_pic;
    private TextView userName;
    private TextView tv_company_name;
    private ImageView iv_back;
    private TextView tv_password;
    private TextView tv_pay_password;
    private Button btn_out;
    SweetAlertDialog.Builder builder;

    @Override
    public void initViews(Bundle savedInstanceState) {
        meInfo = (MeInfo) getIntent().getSerializableExtra("meInfo");
        ci_head_pic = (CircleImageView) findViewById(R.id.ci_head_pic);
        userName = (TextView) findViewById(R.id.tv_user_name);
        tv_company_name = (TextView) findViewById(R.id.tv_company_name);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_password = (TextView) findViewById(R.id.tv_password);
        tv_pay_password = (TextView) findViewById(R.id.tv_pay_password);
        tv_pay_password.setOnClickListener(this);
        tv_password.setOnClickListener(this);
        btn_out = (Button) findViewById(R.id.btn_out);
        btn_out.setOnClickListener(this);
        if (meInfo != null) {
            Glide.with(this).load(meInfo.getLogo())
                    .placeholder(R.mipmap.btn_tp).dontAnimate().into(ci_head_pic);
            userName.setText(meInfo.getUsername());
            tv_company_name.setText(meInfo.getCompany_name());
        }


    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                ActivityManager.getInstance().finishActivity();
                break;
            case R.id.tv_password:
                Intent intent = new Intent(this, AlterPawActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
            case R.id.tv_pay_password:
                Intent intent1 = new Intent(this, AlterPawActivity.class);
                intent1.putExtra("type", 1);
                startActivity(intent1);
                break;
            case R.id.btn_out:
                if (builder == null) {
                    builder = new SweetAlertDialog.Builder(this);
                }
                builder.setCancelable(true)
                        .setMessage("您确定要退出吗？")
                        .setTitle("提示")
                        .setCancelableoutSide(false)
                        .setPositiveButton("确定", new SweetAlertDialog.OnDialogClickListener() {
                            @Override
                            public void onClick(Dialog dialog, int which) {
                                outLogin();
                            }
                        }).show();

                break;

        }
    }

    private void outLogin() {
        myDialog.show();
        params.clear();

        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        compositeSubscription.add(
                RetrofitManager.getInstance().create(CommonService.class)
                        .getData(new RequestBean.JsonMsg(ApiContstants.LOGIN_OUT, params).toJson())
                        .compose(this.<ResponseBean>bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<ResponseBean>() {
                            @Override
                            public void onCompleted() {
                                myDialog.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                myDialog.dismiss();
                            }

                            @Override
                            public void onNext(ResponseBean responseBean) {
                                L.i("Data:" + responseBean.getData());
                                if ("200".equals(responseBean.getStatus())) {
                                    MainActivity.first = 1;
                                    SPUtil.put(getApplicationContext(), "isLoadingStatus", false);

                                    ActivityManager.getInstance().finishActivity();
                                } else if ("400".equals(responseBean.getStatus())) {
                                    ToastUtil.show(SettingActivity.this, responseBean.getMessage());
                                }

                            }
                        })
        );
    }
}
