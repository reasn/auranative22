package io.auraapp.auraandroid.main.list;

import android.view.View;
import android.widget.TextView;

import io.auraapp.auraandroid.R;

class MyExpandedHolder extends ItemViewHolder {

    private final TextView mSloganTextView;

    MyExpandedHolder(View itemView) {
        super(itemView);

        mSloganTextView = itemView.findViewById(R.id.slogan_text);
    }

    @Override
    void bind(ListItem item) {
        if (!(item instanceof MySloganListItem)) {
            throw new RuntimeException("Expecting " + MySloganListItem.class.getSimpleName());
        }
        MySloganListItem castItem = (MySloganListItem) item;
        mSloganTextView.setText(castItem.getSlogan().getText());
    }
}