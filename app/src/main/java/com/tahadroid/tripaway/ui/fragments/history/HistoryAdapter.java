package com.tahadroid.tripaway.ui.fragments.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tahadroid.tripaway.R;
import com.tahadroid.tripaway.models.Trip;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<Trip> tripList;
    private Context mContext;
    private static HistoryViewClickListener itemListener;

    public HistoryAdapter(Context mContext, HistoryViewClickListener itemListener) {
        this.mContext = mContext;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.tripName.setText(tripList.get(position).getmTripTitle());
        holder.tripDate.setText(tripList.get(position).getmTripDate());
        holder.tripTime.setText(tripList.get(position).getmTripTime());

    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public void setList(List<Trip> list) {
        this.tripList = list;
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        tripList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tripList.size());
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tripName, tripDate, tripTime;
        Button showDetails,showOnMapBtn;
        ImageView deleteTrip;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tripName = itemView.findViewById(R.id.tripNameHistoryTV);
            tripDate = itemView.findViewById(R.id.tripDateHistoryTV);
            tripTime = itemView.findViewById(R.id.tripTimeHistoryTV);

            deleteTrip = itemView.findViewById(R.id.deleteTripHistoryIV);
            showDetails = itemView.findViewById(R.id.showDetailsHistoryBtn);
            showOnMapBtn = itemView.findViewById(R.id.showOnMapBtn);

            deleteTrip.setOnClickListener(this);
            showDetails.setOnClickListener(this);
            showOnMapBtn.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v == deleteTrip) {
                itemListener.historyViewListClicked(v, tripList.get(getAdapterPosition()), getAdapterPosition());
            } else if (v == showDetails) {
                itemListener.historyViewListClicked(v, tripList.get(getAdapterPosition()), getAdapterPosition());
            }else if (v == showOnMapBtn) {
                itemListener.historyViewListClicked(v, tripList.get(getAdapterPosition()), getAdapterPosition());
            }
        }
    }

    public interface HistoryViewClickListener {
        void historyViewListClicked(View v, Trip trip, int position);
    }
}