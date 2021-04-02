package com.tahadroid.tripaway.ui.map;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tahadroid.tripaway.R;
import com.tahadroid.tripaway.models.Note;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private List<Note> noteList;

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_note_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.textViewNoteTitlewedget.setText(noteList.get(position).getmNoteTitle());
        holder.textViewNoteDescWedget.setText(noteList.get(position).getmNoteDescription());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void setList(List<Note> list) {
        this.noteList = list;
        notifyDataSetChanged();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNoteDescWedget, textViewNoteTitlewedget;


        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNoteTitlewedget = itemView.findViewById(R.id.textViewNoteTitlewedget);
            textViewNoteDescWedget = itemView.findViewById(R.id.textViewNoteDescWedget);
        }
    }
}