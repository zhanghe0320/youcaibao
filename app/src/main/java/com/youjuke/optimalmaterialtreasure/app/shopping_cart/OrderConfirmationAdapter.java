package com.youjuke.optimalmaterialtreasure.app.shopping_cart;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.youjuke.library.base.BaseRecyclerAdapter;
import com.youjuke.library.base.BaseRecyclerViewHolder;
import com.youjuke.library.utils.MoneySimpleFormat;
import com.youjuke.optimalmaterialtreasure.R;
import com.youjuke.optimalmaterialtreasure.entity.MaterialsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 确认订单列表
 * ------------------------------------------------------------------------
 * 工程:
 * #0000     tian xiao     创建日期: 2017-02-20 10:43
 */
public class OrderConfirmationAdapter extends BaseRecyclerAdapter<MaterialsBean> {

    private MaterialsBean materialsBean;
    private List<String> mMatText=new ArrayList<>();

    public OrderConfirmationAdapter(Context context, List<MaterialsBean> datas) {
        super(context, datas);
    }

    @Override
    protected void showViewHolder(BaseRecyclerViewHolder holder, final int position) {
        materialsBean=mDatas.get(position);
        mMatText.add(position,"");
        final ViewHolder viewHolder= (ViewHolder) holder;
        viewHolder.tvBrandName.setText(materialsBean.getBrandName().trim()+"    颜色:  "+
        materialsBean.getMaterialColor());
        viewHolder.tvMaterialModel.setText(materialsBean.getMaterialModel().trim());
        viewHolder.tvMaterialCount.setText(materialsBean.getMaterialCount().trim());
        viewHolder.tvMaterialName.setText(materialsBean.getMaterialName());
        viewHolder.tvMaterialPrice.setText(MoneySimpleFormat.getMoneyType(materialsBean.getMaterialPrice()));
        viewHolder.tvMaterialNorms.setText(materialsBean.getMaterialNorms());
        viewHolder.etLeaveMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                    mMatText.set(position,viewHolder.etLeaveMessage.getText().toString());
            }
        });
    }

    @Override
    protected BaseRecyclerViewHolder createViewHOldeHolder(ViewGroup parent, int viewType) {
        View view = null;
        ViewHolder holder = null;
        view = mInflater.inflate(R.layout.recycler_view_item_order_confirmation, parent, false);
        holder = new ViewHolder(view);
        return holder;
    }


    public List<String> getMatTxt(){
        return mMatText;
    }

    class ViewHolder extends BaseRecyclerViewHolder {
        private View view;
        private TextView tvMaterialName;
        private TextView tvBrandName;
        private TextView tvMaterialModel;
        private TextView tvMaterialNorms;
        private TextView tvMaterialPrice;
        private TextView tvMaterialCount;
        private EditText etLeaveMessage;

        private void assignViews() {
            etLeaveMessage= (EditText) view.findViewById(R.id.et_leave_message);
            tvMaterialName = (TextView) view.findViewById(R.id.tv_materialName);
            tvBrandName = (TextView) view.findViewById(R.id.tv_brandName);
            tvMaterialModel = (TextView) view.findViewById(R.id.tv_materialModel);
            tvMaterialNorms = (TextView) view.findViewById(R.id.tv_materialNorms);
            tvMaterialPrice = (TextView) view.findViewById(R.id.tv_materialPrice);
            tvMaterialCount = (TextView) view.findViewById(R.id.tv_material_count);
        }

        public ViewHolder(View mview) {
            super(mview);
            view = mview;
            assignViews();

        }
    }
}
