package com.example.bongtoo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bongtoo.R;
import com.example.bongtoo.model.Alimi;

import java.util.List;

public class AlimiRecyclerViewAdapter extends RecyclerView.Adapter<AlimiRecyclerViewAdapter.VerticalViewHolder> {
    List<Alimi> alimiList;
    Activity activity;


    public interface OnItemClickListener {
        void onItemClick(RecyclerView.ViewHolder holder, View view, int position) ;
    }
    private OnItemClickListener mListener = null ;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public AlimiRecyclerViewAdapter(Activity activity, List<Alimi> alimiList) {
        this.alimiList = alimiList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public VerticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_alimi, parent, false);

        VerticalViewHolder holder = new VerticalViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalViewHolder holder, int position) {
        Alimi item = alimiList.get(position);
        holder.alimi_TxtDate.setText(item.getAlimi_date());
        holder.alimi_TxtPlace.setText(item.getAlimi_place());
        holder.alimi_TxtContent.setText(item.getAlimi_content());

        if(item.getAlimi_type()==0) {   //결혼식
            holder.alimi_cardimage.setImageResource(R.drawable.card_wedding);
            holder.alimi_frameimage.setBackgroundResource(R.drawable.card_wedding_frame);
        } else if(item.getAlimi_type()==1) {    //장례식
            holder.alimi_cardimage.setImageResource(R.drawable.card_funeral);
            holder.alimi_frameimage.setBackgroundResource(R.drawable.card_funeral_frame);
        }
    }

    @Override
    public int getItemCount() {
        return alimiList.size();
    }

    public Alimi getItem(int position){
        return  alimiList.get(position);
    }



    class VerticalViewHolder extends RecyclerView.ViewHolder {
        public TextView alimi_TxtDate, alimi_TxtPlace, alimi_TxtContent;
        public ImageView alimi_cardimage;
        public FrameLayout alimi_frameimage;
        public VerticalViewHolder(@NonNull View itemView) {
            super(itemView);
            alimi_TxtDate = itemView.findViewById(R.id.alimi_TxtDate);
            alimi_TxtPlace = itemView.findViewById(R.id.alimi_TxtPlace);
            alimi_TxtContent = itemView.findViewById(R.id.alimi_TxtContent);
            alimi_cardimage = itemView.findViewById(R.id.alimi_cardimage);
            alimi_frameimage = itemView.findViewById(R.id.alimi_frameimage);

        }
    }
}
