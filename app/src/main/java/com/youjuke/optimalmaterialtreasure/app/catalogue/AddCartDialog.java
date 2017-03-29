package com.youjuke.optimalmaterialtreasure.app.catalogue;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.youjuke.library.base.BaseActivity;
import com.youjuke.library.utils.DensityUtil;
import com.youjuke.library.utils.MoneySimpleFormat;
import com.youjuke.library.utils.NumUtil;
import com.youjuke.library.utils.ToastUtil;
import com.youjuke.optimalmaterialtreasure.R;
import com.youjuke.optimalmaterialtreasure.entity.MaterialDetails;
import com.youjuke.optimalmaterialtreasure.weights.AmountView;
import com.zhy.autolayout.AutoLinearLayout;

/**
 * 描述：添加购物车的弹出框
 * Created by Administrator on 2017/2/6.
 * author   zyb
 */

public abstract class AddCartDialog implements View.OnClickListener {
    private Context context;
    private Dialog dialog;
    private View inflate;
    private TextView tvTitle;
    private ImageView ivBack;
    private TextView tv_price;
    private TextView tv_material_norms;
    private TextView tv_material_model;
    private TextView tv_material_color;
    private TextView tv_brand;
    private AmountView av_num;
    MaterialDetails materialDetails;
    private Button btn_add_cart;
    private TextView tv_description;
    private  TextView tv_freight;//运费
    private TextView tv_stair_fee;//楼梯
    private TextView tv_elevator_fee;//电梯
    private CheckBox cb_check;
    private EditText et_color;
    private TextView tv_hint;
    private AutoLinearLayout all_iol;
    private String mix_color="";

    public AddCartDialog(Context context) {
        this.context = context;
        initView();
    }

    private void initView() {
        inflate = LayoutInflater.from(context).inflate(R.layout.dialog_add_cart, null);
        tvTitle = (TextView) inflate.findViewById(R.id.tv_title);
        all_iol = (AutoLinearLayout) inflate.findViewById(R.id.all_iol);
        tv_freight = (TextView) inflate.findViewById(R.id.tv_freight);
        tv_stair_fee = (TextView) inflate.findViewById(R.id.tv_stair_fee);
        tv_elevator_fee = (TextView) inflate.findViewById(R.id.tv_elevator_fee);
        tv_description = (TextView) inflate.findViewById(R.id.tv_description);
        cb_check = (CheckBox) inflate.findViewById(R.id.cb_check);
        et_color = (EditText) inflate.findViewById(R.id.et_color);
        tv_hint = (TextView) inflate.findViewById(R.id.tv_hint);
        cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    et_color.setVisibility(View.VISIBLE);
                    tv_hint.setVisibility(View.VISIBLE);
                }else {
                    et_color.setVisibility(View.GONE);
                    tv_hint.setVisibility(View.GONE);
                }
            }
        });
        ivBack = (ImageView) inflate.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        btn_add_cart = (Button) inflate.findViewById(R.id.btn_add_cart);
        btn_add_cart.setOnClickListener(this);
        tv_price = (TextView) inflate.findViewById(R.id.tv_price);
        av_num = (AmountView) inflate.findViewById(R.id.av_num);
        tv_material_norms = (TextView) inflate.findViewById(R.id.tv_material_norms);
        tv_material_model = (TextView) inflate.findViewById(R.id.tv_material_model);
        tv_material_color = (TextView) inflate.findViewById(R.id.tv_material_color);
        tv_brand = (TextView) inflate.findViewById(R.id.tv_brand);
        dialog = new Dialog(context, R.style.Sweet_Alert_Dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(inflate);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        dialogWindow.setWindowAnimations(R.style.cartDialogStyle);
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }

    int status;

    public void setData(MaterialDetails materialDetails) {
        this.materialDetails = materialDetails;
        /*if (materialDetails.getIs_oil_paint()==1){
            all_iol.setVisibility(View.VISIBLE);
        }else {
            all_iol.setVisibility(View.GONE);
        }*/
        tvTitle.setText(materialDetails.getTitle());
        tv_brand.setText(materialDetails.getBrand());
        tv_material_color.setText(materialDetails.getMaterial_color());
        tv_material_model.setText(materialDetails.getMaterial_model());
        tv_material_norms.setText(materialDetails.getMaterial_norms());
        String full_free = materialDetails.getFull_free();
        if (full_free!=null&&full_free.trim().length()!=0){
            tv_description.setVisibility(View.VISIBLE);
            tv_description.setText(full_free);
        }else {
            tv_description.setVisibility(View.GONE);
        }
        if (materialDetails.getMaterial_price().trim().contains(context.getString(R.string.no_sales_in_the_region))) {
            btn_add_cart.setBackgroundColor(Color.parseColor("#808080"));
//            btn_add_cart.setEnabled(false);
            status = 1;//表示该地区暂不销售此商品
        } else {
            btn_add_cart.setBackgroundColor(Color.parseColor("#518ced"));
//            btn_add_cart.setEnabled(true);
            status = 2;//有价格或者是登录可见
        }


        String freight = MoneySimpleFormat.getMoneyType(materialDetails.getFreight());
        SpannableString span1 = new SpannableString(freight);
        span1.setSpan(new AbsoluteSizeSpan(DensityUtil.sp2px(context,18)), 1, freight.lastIndexOf("."), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_freight.setText(span1);

        String stair_fee = MoneySimpleFormat.getMoneyType(materialDetails.getStair_fee());
        SpannableString span2 = new SpannableString(stair_fee+"/层");
        span2.setSpan(new AbsoluteSizeSpan(DensityUtil.sp2px(context,18)), 1, stair_fee.lastIndexOf("."), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_stair_fee.setText(span2);

        String lift_fee = MoneySimpleFormat.getMoneyType(materialDetails.getLift_fee());
        SpannableString span3 = new SpannableString(lift_fee+"/层");
        span3.setSpan(new AbsoluteSizeSpan(DensityUtil.sp2px(context,18)), 1, lift_fee.lastIndexOf("."), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_elevator_fee.setText(span3);


        if (NumUtil.isNumber(materialDetails.getMaterial_price())) {
            String money = MoneySimpleFormat.getMoneyType(materialDetails.getMaterial_price());
            SpannableString span = new SpannableString(money);
            span.setSpan(new AbsoluteSizeSpan(DensityUtil.sp2px(context,18)), 1, money.lastIndexOf("."), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tv_price.setText(span);
            return;
        }
        tv_price.setText(materialDetails.getMaterial_price());
    }

    public void show() {
        cb_check.setChecked(false);
        et_color.setText("");
        if (dialog != null)
            av_num.setAmount("1");
        dialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                InputMethodManager inputmanger = (InputMethodManager) (context).getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (inputmanger.hideSoftInputFromWindow(inflate.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)) {
                    View mView = ((BaseActivity) context).getWindow().peekDecorView();
                    inputmanger.hideSoftInputFromWindow(inflate.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                dialog.dismiss();
                break;
            case R.id.btn_add_cart:
                if (status == 1) {
                    ToastUtil.show(context, context.getString(R.string.your_location_don_not_sale_the_goods));
                } else {
                    if (cb_check.isChecked()){
                        mix_color=et_color.getText().toString().trim();
                        if (TextUtils.isEmpty(mix_color)||mix_color.length()==0){
                            ToastUtil.show(context,"请输入色值");
                            break;
                        }
                    }else {
                        mix_color="";
                    }
                    addCart(materialDetails.getMaterial_id(), av_num.getNum(),mix_color);
                }
                break;
        }
    }

    public abstract void addCart(int id, String num,String color);


    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
