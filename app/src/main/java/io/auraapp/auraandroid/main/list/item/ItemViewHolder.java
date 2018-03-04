package io.auraapp.auraandroid.main.list.item;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class ItemViewHolder extends RecyclerView.ViewHolder {
    private final View mItemView;
    private ListItem mItem;

    ItemViewHolder(View itemView) {
        super(itemView);
        mItemView = itemView;
    }

    public abstract void bind(ListItem item, View mItemView);

    public final void setItem(ListItem listItem) {
        mItem = listItem;
        bind(mItem, mItemView);
    }

    public ListItem getLastBoundItem() {
        return mItem;
    }
}
