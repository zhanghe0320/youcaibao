package com.youjuke.optimalmaterialtreasure.app.home;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.youjuke.library.base.BaseFragment;
import com.youjuke.library.utils.L;
import com.youjuke.library.utils.SPUtil;
import com.youjuke.library.utils.ToastUtil;
import com.youjuke.library.weights.AutoBanner;
import com.youjuke.library.weights.SpaceGridItemDecoration;
import com.youjuke.optimalmaterialtreasure.OptimalMaterialTreasureApp;
import com.youjuke.optimalmaterialtreasure.R;
import com.youjuke.optimalmaterialtreasure.entity.Index;
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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 描述：首页
 * author：zyb
 * Created by Administrator on 2017/2/8.
 */

public class HomeFragment extends BaseFragment {
    private AutoBanner banner;
    private RecyclerView recycle;
    private TopAdapter adapter;
    private ArrayList<Index.Goods> brtterdatas=new ArrayList<Index.Goods>();
    private ArrayList<Index.Goods> qualitydatas=new ArrayList<Index.Goods>();
    private RecyclerView rlvPopularityRecycler;
    private GoodsAdapter goodsdapter;
    private GoodsAdapter goodsdapter1;
    private RecyclerView rlvQualityRecycler;
    private Index index;
    private ScrollView scrollView;
    private float off = 0f;
    private static final int MIXOFF = 100;//偏移量小于这个值不刷新
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    public void finishCreateView(Bundle state) {

        initView();
        initScroll();
        getData();
    }

    private void initScroll() {
        IOverScrollDecor iOverScrollDecor = OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        View view= iOverScrollDecor.getView();
        view.setBackgroundResource(R.color.back_f0f0f0);
        iOverScrollDecor.setOverScrollStateListener(new ListenerStubs.OverScrollStateListenerStub() {
            @Override
            public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {
                super.onOverScrollStateChange(decor, oldState, newState);
                if (oldState == 1) {
                    if ( off > MIXOFF) {
                        getData();//从网络获取数据
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

    private void getData() {
        params.clear();
        params.put("uid", OptimalMaterialTreasureApp.user.getId());
        params.put("token", OptimalMaterialTreasureApp.user.getToken());
        Subscription subscribe = RetrofitManager.getInstance().create(CommonService.class)
                .getData(new RequestBean.JsonMsg(ApiContstants.INDEX_PAGE, params).toJson())
                .compose(this.<ResponseBean>bindToLifecycle())  //生命周期绑定
                .observeOn(AndroidSchedulers.mainThread())      //设置观察者所在的线程
                .subscribeOn(Schedulers.io())                   //设置被观察者所在的线程
                .subscribe(new Subscriber<ResponseBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.i("首页数据加载失败！");
                        e.printStackTrace();
                        ToastUtil.show(mContext,"首页数据加载失败！");
                    }

                    @Override
                    public void onNext(ResponseBean responseBean) {
                        L.i("首页数据Data:" + responseBean.getData());
                        if ("200".equals(responseBean.getStatus())) {
                            index = gson.fromJson(responseBean.getData(), Index.class);
                            initData();
                        }
                    }
                });
        compositeSubscription.add(subscribe);
    }
    private void initData() {
        //加载banner
        //noinspection unchecked
        banner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, index.getBanner_list())
                .startTurning(3000)
                .setPointViewVisible(true)//设置指示器是否可见
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused})
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
        //banner点击事件
        banner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        brtterdatas= (ArrayList<Index.Goods>) index.getHot_list();
        qualitydatas=(ArrayList<Index.Goods>) index.getBetter_list();
        goodsdapter.setdata(brtterdatas);
        goodsdapter1.setdata(qualitydatas);
        goodsdapter.notifyDataSetChanged();
        goodsdapter1.notifyDataSetChanged();
    }

    private void initView() {
        isLoadingStatus=(boolean) SPUtil.get(getApplicationContext(),"isLoadingStatus", false);
        banner = $(R.id.banner);
        scrollView=$(R.id.scroll_view);
        recycle = $(R.id.rlv_recycle);
        recycle.setLayoutManager(new GridLayoutManager(mContext, 3){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        adapter = new TopAdapter(mContext);
        recycle.setAdapter(adapter);


        rlvPopularityRecycler = $(R.id.rlv_popularity_recycler);
        rlvPopularityRecycler.addItemDecoration(new SpaceGridItemDecoration(3));
        rlvPopularityRecycler.setLayoutManager(new GridLayoutManager(mContext, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        goodsdapter = new GoodsAdapter(mContext, brtterdatas);
        rlvPopularityRecycler.setAdapter(goodsdapter);


        rlvQualityRecycler = $(R.id.rlv_quality_recycler);
        rlvQualityRecycler.addItemDecoration(new SpaceGridItemDecoration(3));
        rlvQualityRecycler.setLayoutManager(new GridLayoutManager(mContext, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        goodsdapter1 = new GoodsAdapter(mContext, qualitydatas);
        rlvQualityRecycler.setAdapter(goodsdapter1);
    }

    /**
     * banner轮换的holer
     */
    public class LocalImageHolderView implements Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, String  url) {
            Glide.with(context).load(url)
                    .into(imageView);
        }
    }
    private boolean isLoadingStatus = false;
    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        banner.startTurning(3000);
        if (isLoadingStatus!=(boolean) SPUtil.get(getApplicationContext(),"isLoadingStatus", false)){
            getData();
            isLoadingStatus = (boolean) SPUtil.get(getApplicationContext(),"isLoadingStatus", false);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden&&isLoadingStatus!=(boolean) SPUtil.get(getApplicationContext(),"isLoadingStatus", false)){
            getData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止自动翻页
        banner.stopTurning();

    }
}
