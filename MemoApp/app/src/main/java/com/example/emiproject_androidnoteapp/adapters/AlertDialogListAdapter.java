package com.example.emiproject_androidnoteapp.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.emiproject_androidnoteapp.R;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class AlertDialogListAdapter extends ArrayAdapter<SimpleListItem> implements ListAdapter {

    public AlertDialogListAdapter(Context context) {
        super(context, R.layout.simplelist_item, android.R.id.title);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int index, View convertView, ViewGroup parent) {
        final View view = super.getView(index, convertView, parent);
        final SimpleListItem item = getItem(index);
        ImageView ic = (ImageView) view.findViewById(android.R.id.icon);
        if (item.getIcon() != null) {
            ic.setImageDrawable(item.getIcon());
            ic.setPadding(item.getIconPadding(), item.getIconPadding(),
                    item.getIconPadding(), item.getIconPadding());
        } else {
            ic.setVisibility(View.GONE);
        }
        TextView tv = (TextView) view.findViewById(android.R.id.title);
        tv.setText(item.getContent());

        return view;
    }
}
