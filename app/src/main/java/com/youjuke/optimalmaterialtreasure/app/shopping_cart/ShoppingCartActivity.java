package com.youjuke.optimalmaterialtreasure.app.shopping_cart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.youjuke.library.base.BaseActivity;
import com.youjuke.library.rxbus.annotation.Subscribe;
import com.youjuke.library.rxbus.annotation.Tag;
import com.youjuke.library.rxbus.thread.EventThread;
import com.youjuke.library.utils.L;
import com.youjuke.optimalmaterialtreasure.R;
import com.youjuke.optimalmaterialtreasure.app.goods.GoodsFragment;

/**
 * 描述: 购物车
 * ------------------------------------------------------------------------
 * 工程:
 * #0000     tian xiao     创建日期: 2017-02-09 10:25
 */
public class ShoppingCartActivity extends BaseActivity {

    private FrameLayout flShoppingCart;
    private Fragment[] fragments;
    private String orderId;
    private int index = 0;

    private void assignViews() {
        flShoppingCart = (FrameLayout) findViewById(R.id.fl_shopping_cart);
    }

    /**
     * 初始化Fragment
     */
    private void initFragments() {
        GoodsFragment goodsFragment = GoodsFragment.newInstance();
        OrderConfirmationFragment orderFragment = OrderConfirmationFragment.newInstance();

        Bundle  goodsBundle = new Bundle();
        goodsBundle.putBoolean("toolBarShow", true);
        goodsFragment.setArguments(goodsBundle);
        L.d("order"+orderId);

        if (orderId!=null&&!orderId.isEmpty()) {
            Bundle orderBundle = new Bundle();
            orderBundle.putString("order_id", orderId);
            orderFragment.setArguments(orderBundle);
        }

        fragments = new Fragment[]{goodsFragment, orderFragment};
        // 添加显示第一个fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_shopping_cart, fragments[index])
                .show(fragments[index]).commit();
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        index = getIntent().getIntExtra("index", 0);
        orderId=getIntent().getStringExtra("order_id");
        L.d("order"+orderId);
        assignViews();
        initFragments();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_shopping_cart;
    }

    @Override
    public void initToolBar() {

    }

    /**
     * Fragment切换
     *
     * @param result
     */
    public void setShowingFragment(Integer result, int typeNum) {
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.hide(fragments[result]);


        if (!fragments[result + typeNum].isAdded()) {
            trx.add(R.id.fl_shopping_cart, fragments[result + typeNum]);
        }else {
            trx.show(fragments[result + typeNum]);
        }
        trx.commit();
    }


    /**
     * 退出
     *
     * @param result
     */
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag("outShoppingCart")
            })
    public void outShoppingCart(Integer result) {
        if (result > index) {
            setShowingFragment(result, -1);
        } else {
            finish();
        }
    }

    /**
     * 切换
     *
     * @param result
     */
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag("goShoppingCartNext")
            })
    public void goShoppingCartNext(Integer result){
        setShowingFragment( result,+1);
    }




}
