package com.example.emiproject_androidnoteapp.adapters;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.emiproject_androidnoteapp.R;
import com.example.emiproject_androidnoteapp.models.Note;
import com.example.emiproject_androidnoteapp.utils.RecyclerViewItemListener;

import java.util.List;

import io.realm.RealmResults;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private final Context context;
    private RealmResults<Note> items;
    private RecyclerViewItemListener itemListener;

    public NotesAdapter(Context context, RealmResults<Note> notes, RecyclerViewItemListener itemListener) {
        this.context = context;
        this.items = notes;
        this.itemListener = itemListener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);

        final NoteViewHolder viewHolder = new NoteViewHolder(itemView);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemListener != null) {
                    itemListener.onItemClicked(v, viewHolder.getAdapterPosition());
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return itemListener != null && itemListener.onItemLongClick(v, viewHolder.getAdapterPosition());
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final NoteViewHolder holder, int position) {
        Note note = getItem(position);

        if (TextUtils.isEmpty(note.getTitle())){
            holder.noteTitle_tv.setVisibility(View.GONE);
        } else {
            holder.noteTitle_tv.setText(note.getTitle());
        }

        String text = note.getText();
        if (!TextUtils.isEmpty(text)) {
            if (text.length() <= 10) {
                holder.noteText_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
            } else if (text.length() <= 30) {
                holder.noteText_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 34);
            } else if (text.length() <= 50) {
                holder.noteText_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            } else if (text.length() <= 70) {
                holder.noteText_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            } else {
                holder.noteText_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
            holder.noteText_tv.setVisibility(View.VISIBLE);
            holder.textIcon.setVisibility(View.VISIBLE);
        } else {
            holder.noteText_tv.setVisibility(View.GONE);
            holder.textIcon.setVisibility(View.GONE);
        }

        holder.noteText_tv.setText(text);
        holder.micIcon.setVisibility(note.getAudioClip() != null ? View.VISIBLE : View.GONE);
        holder.imageIcon.setVisibility(!note.getImages().isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Note getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getLocalId();
    }


    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView noteTitle_tv, noteText_tv;
        ImageView micIcon, textIcon, imageIcon;

        public NoteViewHolder(View itemView) {
            super(itemView);

            noteTitle_tv = (TextView) itemView.findViewById(R.id.noteTitle_tv);
            noteText_tv = (TextView) itemView.findViewById(R.id.noteText_tv);

            micIcon = (ImageView) itemView.findViewById(R.id.micIcon);
            textIcon = (ImageView) itemView.findViewById(R.id.textIcon);
            imageIcon = (ImageView) itemView.findViewById(R.id.imageIcon);
        }
    }

}
