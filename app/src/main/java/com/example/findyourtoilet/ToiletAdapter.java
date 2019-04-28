package com.example.findyourtoilet;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class ToiletAdapter extends RecyclerView.Adapter<ToiletAdapter.ViewHolder> {

    private ArrayList<Toilet> mToilets;
    final private OnListItemClickListener mOnListItemClickListener;

    ToiletAdapter(ArrayList<Toilet> toilets, OnListItemClickListener listener){
        mToilets = toilets;
        mOnListItemClickListener = listener;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.toilet_list_item, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.name.setText("Address: "+mToilets.get(position).getAddress());
        // viewHolder.icon.setImageResource(mPokemons.get(position).getIconId());
    }

    public int getItemCount() {
        return mToilets.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnListItemClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public interface OnListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
}
