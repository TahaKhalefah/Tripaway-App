package com.tahadroid.tripaway.ui.fragments.trips;

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

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> tripList;
    private Context mContext;
    private  RecyclerViewClickListener itemListener;

    public TripAdapter(Context mContext, RecyclerViewClickListener itemListener) {
        this.mContext = mContext;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TripViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_trip_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        holder.tripName.setText(tripList.get(position).getmTripTitle());
        holder.tripDesc.setText(tripList.get(position).getmTripDescription());
        holder.tripDate.setText(tripList.get(position).getmTripDate());
        holder.tripTime.setText(tripList.get(position).getmTripTime());
        holder.startAddress.setText(tripList.get(position).getmStartAddress());
        holder.endAddress.setText(tripList.get(position).getmEndAddress());
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
        notifyItemRemoved(position);
        notifyDataSetChanged();
        notifyItemRangeChanged(position, tripList.size());
    }
    public class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tripName,tripDesc,tripDate,tripTime,startAddress,endAddress;
        Button addNoteBtn,startBtn,doneBtn;
        ImageView deleteTrip,updateUpcomeTripIV;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tripName = itemView.findViewById(R.id.tripNameUpcomeTV);
            tripDesc = itemView.findViewById(R.id.tripDescUpcomeTV);
            tripDate = itemView.findViewById(R.id.tripDateUpcomeTV);
            tripTime = itemView.findViewById(R.id.tripTimeUpcomeTV);
            startAddress = itemView.findViewById(R.id.textViewStartAddess);
            endAddress = itemView.findViewById(R.id.textViewEndAddress);

            addNoteBtn = itemView.findViewById(R.id.addNoteUpcomeBtn);
            startBtn = itemView.findViewById(R.id.startUpcomeBtn);
            doneBtn = itemView.findViewById(R.id.doneUpcomeBtn);
            deleteTrip = itemView.findViewById(R.id.deleteUpcomeIV);
            updateUpcomeTripIV = itemView.findViewById(R.id.updateUpcomeTripIV);

            addNoteBtn.setOnClickListener(this);
            startBtn.setOnClickListener(this);
            deleteTrip.setOnClickListener(this);
            doneBtn.setOnClickListener(this);
            updateUpcomeTripIV.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
          if(v == addNoteBtn){
              itemListener.recyclerViewListClicked(v,tripList.get(getAdapterPosition()),getAdapterPosition());
          }else if(v == startBtn){
              itemListener.recyclerViewListClicked(v,tripList.get(getAdapterPosition()),getAdapterPosition());
          }else if(v == deleteTrip){
              itemListener.recyclerViewListClicked(v,tripList.get(getAdapterPosition()),getAdapterPosition());
          }else if(v == doneBtn){
              itemListener.recyclerViewListClicked(v,tripList.get(getAdapterPosition()),getAdapterPosition());
          }else if(v == updateUpcomeTripIV){
              itemListener.recyclerViewListClicked(v,tripList.get(getAdapterPosition()),getAdapterPosition());
          }
        }
    }

    public interface RecyclerViewClickListener {
        void recyclerViewListClicked(View v, Trip trip,int position);
    }
}