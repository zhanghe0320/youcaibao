package com.youjuke.optimalmaterialtreasure.app.goods;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.youjuke.library.base.BaseFragment;
import com.youjuke.library.base.BaseRecyclerAdapter;
import com.youjuke.library.base.BaseRecyclerViewHolder;
import com.youjuke.library.rxbus.RxBus;
import com.youjuke.library.utils.L;
import com.youjuke.library.utils.ToastUtil;
import com.youjuke.optimalmaterialtreasure.OptimalMaterialTreasureApp;
import com.youjuke.optimalmaterialtreasure.R;
import com.youjuke.optimalmaterialtreasure.app.MainActivity;
import com.youjuke.optimalmaterialtreasure.app.login.LoginActivity;
import com.youjuke.optimalmaterialtreasure.app.shopping_cart.ShoppingCartActivity;
import com.youjuke.optimalmaterialtreasure.entity.GoodsList;
import com.youjuke.optimalmaterialtreasure.retrofit.ApiContstants;
import com.youjuke.optimalmaterialtreasure.retrofit.RequestBean;
import com.youjuke.optimalmaterialtreasure.retrofit.ResponseBean;
import com.youjuke.optimalmaterialtreasure.retrofit.RetrofitManager;
import com.youjuke.optimalmaterialtreasure.retrofit.api.CommonService;
import com.youjuke.optimalmaterialtreasure.weights.AmountView;
import com.zhy.autolayout.AutoLinearLayout;

import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.ListenerStubs;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by Administrator on 2017/2/8.
 */

public class GoodsFragment extends BaseFragment implements View.OnClickListener {

    private GoodsAdapter goodsAdapter;
    private boolean toolBarShows;
    private GoodsList goodsList;
    private boolean allIsChoose;

    private Toolbar toolBar;
    private AutoLinearLayout lyAllSelect;
    private ImageView ivAllSelect;
    private TextView tvMoney;
    private TextView tvGoPay;
    private RecyclerView rvGoods;
    private ImageView iv_all_select;
    private float off = 0f;
    private static final int MIXOFF = 100;//偏移量小于这个值不刷新

    private void assignViews() {
        toolBar = $(R.id.tool_bar);
        lyAllSelect = $(R.id.ly_all_select);
        tvMoney = $(R.id.tv_money);
        tvGoPay = $(R.id.tv_go_pay);
        rvGoods = $(R.id.rv_goods);
        iv_all_select = $(R.id.iv_all_select);
        tvGoPay.setOnClickListener(this);
        goodsList = new GoodsList();
        goodsAdapter = new GoodsAdapter(mContext, goodsList.getShopcartInfo());
        goodsAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<GoodsList.ShopcartInfoBean>() {
            @Override
            public void onItemClick(BaseRecyclerViewHolder view, final int position, final GoodsList.ShopcartInfoBean model) {

//                ivSelectChoose = (ImageView) view.findViewById(R.id.iv_select_choose);
//                tvMaterialName = (TextView) view.findViewById(R.id.tv_materialName);
//                tvBrandName = (TextView) view.findViewById(R.id.tv_brandName);
//                tvMaterialModel = (TextView) view.findViewById(R.id.tv_materialModel);
//                tvMaterialNorms = (TextView) view.findViewById(R.id.tv_materialNorms);
//                tvMaterialPrice = (TextView) view.findViewById(R.id.tv_materialPrice);
                final TextView tvCompile = (TextView) view.itemView.findViewById(R.id.tv_compile);
                final ImageView ivDeleteShopping = (ImageView) view.itemView.findViewById(R.id.iv_delete_shopping);
                final TextView tvNum = (TextView) view.itemView.findViewById(R.id.tv_num);
                final AmountView avCount = (AmountView) view.itemView.findViewById(R.id.Av_count);
                //编辑
                tvCompile.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View view) {
                        if (tvCompile.getText().toString().equals("编辑")) {
                            tvCompile.setText("完成");
                            ivDeleteShopping.setVisibility(View.VISIBLE);
                            avCount.setVisibility(View.VISIBLE);
                            tvNum.setVisibility(View.GONE);
                        } else if (tvCompile.getText().toString().equals("完成")) {
                            if (avCount.amount <= 0) {
                                ToastUtil.show(mContext, "亲数量不能少于1个");
                            } else {
                                tvCompile.setText("编辑");
                                ivDeleteShopping.setVisibility(View.INVISIBLE);
                                avCount.setVisibility(View.GONE);
                                tvNum.setVisibility(View.VISIBLE);
                                if (!tvNum.getText().toString().equals("x" + avCount.amount)) {
                                    modifyShopcartRequest(model.getMatId(), avCount.amount);
                                    tvNum.setText("x" + avCount.amount);
                                }
                            }
                        }
                    }
                });


                //删除订单
                ivDeleteShopping.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog("确认商品从购物车中删除吗？", model.getMatId());
                    }
                });

                //选择状态
                view.itemView.findViewById(R.id.iv_select_choose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeShopcartChooseRequest(model.getMatId(), model.getIsChoose());
                    }
                });

            }

            @Override
            public void onItemLongClick(BaseRecyclerViewHolder view, int position, GoodsList.ShopcartInfoBean model) {

            }
        });
        rvGoods.setItemAnimator(new DefaultItemAnimator());
        rvGoods.setLayoutManager(new LinearLayoutManager(mContext));
        rvGoods.setAdapter(goodsAdapter);
        lyAllSelect.setOnClickListener(this);
    }

    public static GoodsFragment newInstance() {
        return new GoodsFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_goods;
    }

    @Override
    public void finishCreateView(Bundle state) {
        assignViews();
        initData();
        initScroll();
        Bundle bundle = getArguments();
        if (bundle != null) {
            toolBarShows = bundle.getBoolean("toolBarShow", false);
            if (toolBarShows) {
                toolBar.setNavigationIcon(R.mipmap.btn_return);
                toolBar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RxBus.get().post("outShoppingCart", 0);
                    }
                });
            }
        }
    }

    private void initScroll() {
        IOverScrollDecor iOverScrollDecor = OverScrollDecoratorHelper.setUpOverScroll(rvGoods, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
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


    @SuppressLint("SetTextI18n")
    private void fillView() {
        L.d("开始加载列表");
        goodsAdapter.addAll(goodsList.getShopcartInfo());
        goodsAdapter.notifyDataSetChanged();
        tvMoney.setText("￥" + goodsList.getTotalInfo().getTotalChoosePrice());
        if (goodsList.getTotalInfo().getTotalChoosePrice().equals("0") ||
                goodsList.getTotalInfo().getTotalChoosePrice().isEmpty()) {
            tvGoPay.setClickable(false);
            tvGoPay.setBackgroundColor(Color.parseColor("#999999"));
        } else {
            tvGoPay.setClickable(true);
            tvGoPay.setBackgroundColor(Color.parseColor("#f22e2d"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.isVisible() && MainActivity.first == 1) {
            initData();
        }
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initData();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1)
            initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tv_go_pay:
                if (goodsList.getShopcartInfo() != null && goodsList.getShopcartInfo().size() > 0) {
                    if (!toolBarShows) {
                        //首页跳转next
                        Intent intent = new Intent(mContext, ShoppingCartActivity.class);
                        intent.putExtra("index", 1);
                        startActivityForResult(intent, 1);
                    } else {
                        //购物车跳转next
                        RxBus.get().post("goShoppingCartNext", 0);
                    }
                } else {
                    ToastUtil.show(mContext, "没有商品可结算");
                }
                break;
            case R.id.ly_all_select:
                //全部选择状态
                if (goodsList != null && goodsList.getShopcartInfo() != null)
                    changeShopcartAll();
                break;
        }
    }

    /**
     * 请求数据
     */
    private void initData() {
        params.clear();
        myDialog.show();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        params.put("mobile", OptimalMaterialTreasureApp.user.getMobile());
        compositeSubscription.add(
                RetrofitManager.getInstance().create(CommonService.class)
                        .getData(new RequestBean.JsonMsg(ApiContstants.SHOPCART_LIST, params).toJson())
                        .compose(this.<ResponseBean>bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<ResponseBean>() {
                            @Override
                            public void call(ResponseBean responseBean) {
                                myDialog.dismiss();
                                if ("200".equals(responseBean.getStatus())) {
                                    allIsChoose = true;
                                    goodsList = gson.fromJson(responseBean.getData(), new TypeToken<GoodsList>() {
                                    }.getType());
                                    for (GoodsList.ShopcartInfoBean goods : goodsList.getShopcartInfo()) {
                                        if ("0".equals(goods.getIsChoose())) {
                                            allIsChoose = false;
                                        }
                                    }
                                    if (goodsList.getShopcartInfo().size()==0){
                                        allIsChoose = false;
                                    }
                                    if (allIsChoose) {
                                        iv_all_select.setImageResource(R.mipmap.btn_xzzt);
                                    } else {
                                        iv_all_select.setImageResource(R.mipmap.btn_wxzzt);
                                    }
                                    fillView();
                                } else if ("400".equals(responseBean.getStatus())) {
                                    ToastUtil.show(mContext, responseBean.getMessage());
                                    if (responseBean.getError().equals("020")||responseBean.getError().equals("007")||responseBean.getError().equals("010"))
                                    startActivity(new Intent(mContext, LoginActivity.class));
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                myDialog.dismiss();
                            }
                        })
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        myDialog.dismiss();
    }

    /**
     * 删除商品
     * @param maId
     */
    public void deleteShopcartRequest(final String maId) {
        params.clear();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        params.put("mobile", OptimalMaterialTreasureApp.user.getMobile());
        params.put("matId", maId);
        L.d("删除请求:");
        compositeSubscription.add(
                RetrofitManager.getInstance().create(CommonService.class)
                        .getData(new RequestBean.JsonMsg("delete_shopcart", params).toJson())
                        .compose(this.<ResponseBean>bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<ResponseBean>() {
                            @Override
                            public void call(ResponseBean responseBean) {
                                if ("200".equals(responseBean.getStatus())) {
                                    L.d("删除:" + responseBean.getData());
                                    initData();
                                } else if ("400".equals(responseBean.getStatus())) {
                                    ToastUtil.show(mContext, responseBean.getMessage());
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
     * 选中商品请求
     */
    public void changeShopcartChooseRequest(final String maId, String IsChoose) {
        myDialog.show();
        params.clear();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        params.put("mobile", OptimalMaterialTreasureApp.user.getMobile());
        params.put("matId", maId);
        if ("0".equals(IsChoose)) {
            params.put("isChoose", "1");
        } else if ("1".equals(IsChoose)) {
            params.put("isChoose", "0");
        }
        compositeSubscription.add(
                RetrofitManager.getInstance().create(CommonService.class)
                        .getData(new RequestBean.JsonMsg("change_shopcart_choose", params).toJson())
                        .compose(this.<ResponseBean>bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<ResponseBean>() {
                            @Override
                            public void call(ResponseBean responseBean) {

                                if ("200".equals(responseBean.getStatus())) {
                                    L.d("请求选择成功");
                                    initData();
                                } else if ("400".equals(responseBean.getStatus())) {
                                    ToastUtil.show(mContext, responseBean.getMessage());
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
     * 全部选择
     */
    public void changeShopcartAll() {
        allIsChoose = true;
        params.clear();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        params.put("mobile", OptimalMaterialTreasureApp.user.getMobile());

        for (GoodsList.ShopcartInfoBean goods : goodsList.getShopcartInfo()) {
            if ("0".equals(goods.getIsChoose())) {
                allIsChoose = false;
            }
        }
        if (!allIsChoose) {
            params.put("isChoose", "1");

        } else {
            params.put("isChoose", "0");
        }

        compositeSubscription.add(
                RetrofitManager.getInstance().create(CommonService.class)
                        .getData(new RequestBean.JsonMsg("change_all_shopcart_choose", params).toJson())
                        .compose(this.<ResponseBean>bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<ResponseBean>() {
                            @Override
                            public void call(ResponseBean responseBean) {

                                if ("200".equals(responseBean.getStatus())) {
                                    L.d("请求选择成功");
                                    initData();
                                } else if ("400".equals(responseBean.getStatus())) {
                                    ToastUtil.show(mContext, responseBean.getMessage());
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
     * 改变数量请求
     */
    public void modifyShopcartRequest(final String matId, final int count) {
        params.clear();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        params.put("mobile", OptimalMaterialTreasureApp.user.getMobile());
        params.put("matId", matId);
        params.put("count", count);
        compositeSubscription.add(
                RetrofitManager.getInstance().create(CommonService.class)
                        .getData(new RequestBean.JsonMsg("modify_shopcart", params).toJson())
                        .compose(this.<ResponseBean>bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<ResponseBean>() {
                            @Override
                            public void call(ResponseBean responseBean) {
                                if ("200".equals(responseBean.getStatus())) {
                                    L.d("请求选择成功");
                                    initData();
                                } else if ("400".equals(responseBean.getStatus())) {
                                    ToastUtil.show(mContext, responseBean.getMessage());
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        })
        );
    }

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

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
//        textViewCancel.setVisibility(View.GONE);
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
                deleteShopcartRequest(MatId);
                dialog.dismiss();
            }
        });
    }

}
