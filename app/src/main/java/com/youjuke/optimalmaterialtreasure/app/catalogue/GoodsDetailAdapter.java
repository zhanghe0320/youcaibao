package com.youjuke.optimalmaterialtreasure.app.catalogue;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youjuke.library.utils.DensityUtil;
import com.youjuke.library.utils.MoneySimpleFormat;
import com.youjuke.library.utils.NumUtil;
import com.youjuke.optimalmaterialtreasure.R;
import com.youjuke.optimalmaterialtreasure.entity.MaterialDetails;

import java.util.ArrayList;

/**
 * 描述：选择商品规格的adapter
 * author：zyb
 * Created by Administrator on 2017/2/10.
 */

public class GoodsDetailAdapter extends RecyclerView.Adapter<GoodsDetailAdapter.MyHolder> {
    private Context context;
    private ArrayList<MaterialDetails> datas;

    public GoodsDetailAdapter(Context context, ArrayList<MaterialDetails> datas) {
        this.context = context;
        this.datas = datas;
    }
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        MyHolder myHolder = new MyHolder(inflater.inflate(R.layout.item_goods_detail_adapter, parent, false));
        return myHolder;
    }
    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        MaterialDetails materialDetails = datas.get(position);
        holder.tv_brand.setText(materialDetails.getBrand());
        holder.tv_material_color.setText(materialDetails.getMaterial_color());
        holder.tv_material_model.setText(materialDetails.getMaterial_model());
        holder.tv_material_norms.setText(materialDetails.getMaterial_norms());
        holder.tv_title.setText(materialDetails.getTitle());
        String full_free = materialDetails.getFull_free();
        if (full_free!=null&&full_free.trim().length()!=0){
            holder.tv_description.setVisibility(View.VISIBLE);
            holder.tv_description.setText(full_free);
        }else {
            holder.tv_description.setVisibility(View.GONE);
        }
        holder.tv_fee.setText(Html.fromHtml("运费:<font color='red'>"+materialDetails.getFreight()+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;楼梯房:<font color='red' >"+materialDetails.getStair_fee()+"</font>元/层&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;电梯房:<font color='red'>"+materialDetails.getLift_fee()+"</font>元/层"));
        if (!NumUtil.isNumber(materialDetails.getMaterial_price())){
            holder.tv_price.setText(materialDetails.getMaterial_price());
        }else{
            String money = MoneySimpleFormat.getMoneyType(materialDetails.getMaterial_price());
            SpannableString span = new SpannableString(money);
            span.setSpan(new AbsoluteSizeSpan(DensityUtil.sp2px(context,18)), 1, money.lastIndexOf("."), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tv_price.setText(span);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null)
                    onItemClickListener.ItemClickListener(datas.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setdata(ArrayList<MaterialDetails> datas) {
        this.datas = datas;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private TextView tv_price;
        private TextView tv_material_norms;
        private TextView tv_material_model;
        private TextView tv_material_color;
        private TextView tv_brand;
        private TextView tv_title;
        private TextView tv_fee;
        private TextView tv_description;
        public MyHolder(View itemView) {
            super(itemView);
            tv_price= (TextView) itemView.findViewById(R.id.tv_price);
            tv_description= (TextView) itemView.findViewById(R.id.tv_description);
            tv_fee= (TextView) itemView.findViewById(R.id.tv_fee);
            tv_material_norms= (TextView) itemView.findViewById(R.id.tv_material_norms);
            tv_material_model= (TextView) itemView.findViewById(R.id.tv_material_model);
            tv_material_color= (TextView) itemView.findViewById(R.id.tv_material_color);
            tv_brand= (TextView) itemView.findViewById(R.id.tv_brand);
            tv_title= (TextView) itemView.findViewById(R.id.tv_title);

        }
    }
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }
    public interface OnItemClickListener{
        void ItemClickListener(MaterialDetails gooddetail);
    }
}
