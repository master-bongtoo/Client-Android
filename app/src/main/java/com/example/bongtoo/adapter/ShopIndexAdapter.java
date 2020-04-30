package com.example.bongtoo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bongtoo.R;

import java.util.ArrayList;


public class ShopIndexAdapter extends RecyclerView.Adapter<ShopIndexAdapter.VerticalViewHolder> {
    ArrayList<String> shopList;
    Activity activity;

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.ViewHolder holder, View view, int position) ;
    }
    private OnItemClickListener mListener = null ;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public  String getItem(int position){
        return  shopList.get(position);
    }




    public ShopIndexAdapter(Activity activity, ArrayList<String> shopList) {
        this.shopList = shopList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public VerticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_shoplist_index, parent, false);

        VerticalViewHolder holder = new VerticalViewHolder(view);

        return holder;
    }

    // 클릭리스너는 여기서 구현
    @Override
    public void onBindViewHolder(@NonNull final VerticalViewHolder holder, final int position) {

        String storeSubject = shopList.get(position);
        holder.textView.setText(storeSubject);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(holder, v, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    class VerticalViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public VerticalViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.shop_index);
        }
    }
}