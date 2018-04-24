package io.auraapp.auraandroid.ui.profile;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import io.auraapp.auraandroid.R;
import io.auraapp.auraandroid.common.Slogan;
import io.auraapp.auraandroid.ui.common.ColorHelper;
import io.auraapp.auraandroid.ui.common.lists.LegacyItemViewHolder;
import io.auraapp.auraandroid.ui.common.lists.LegacyRecyclerAdapter;
import io.auraapp.auraandroid.ui.common.lists.ListItem;
import io.auraapp.auraandroid.ui.profile.profileModel.MyProfileManager;
import io.auraapp.auraandroid.ui.world.list.PeersDiffCallback;

import static io.auraapp.auraandroid.common.FormattedLog.d;
import static io.auraapp.auraandroid.common.FormattedLog.i;
import static io.auraapp.auraandroid.ui.profile.profileModel.MyProfileManager.EVENT_ADOPTED;
import static io.auraapp.auraandroid.ui.profile.profileModel.MyProfileManager.EVENT_COLOR_CHANGED;
import static io.auraapp.auraandroid.ui.profile.profileModel.MyProfileManager.EVENT_DROPPED;
import static io.auraapp.auraandroid.ui.profile.profileModel.MyProfileManager.EVENT_REPLACED;

public class MySlogansRecycleAdapter extends LegacyRecyclerAdapter {

    private final MyProfileManager mMyProfileManager;

    @FunctionalInterface
    public interface OnMySloganActionCallback {
        int ACTION_EDIT = 2;
        int ACTION_DROP = 3;

        void onActionTaken(Slogan slogan, int action);
    }

    private static final String TAG = "@aura/" + MySlogansRecycleAdapter.class.getSimpleName();

    private final static int TYPE_SPACER = 197;
    private final static int TYPE_MY_SLOGAN = 198;
    private final OnMySloganActionCallback mOnMySloganActionCallback;

    public MySlogansRecycleAdapter(@NonNull Context context,
                                   RecyclerView listView,
                                   OnMySloganActionCallback onMySloganActionCallback,
                                   MyProfileManager myProfileManager) {
        super(context, listView);
        mOnMySloganActionCallback = onMySloganActionCallback;
        mMyProfileManager = myProfileManager;
        mItems.add(new SpacerItem());

        mMyProfileManager.addChangedCallback(event -> {

            if (event == EVENT_COLOR_CHANGED) {
                notifyDataSetChanged();

            } else if (event == EVENT_ADOPTED
                    || event == EVENT_DROPPED
                    || event == EVENT_REPLACED) {
                i(TAG, "Slogans changes (%s), synchronizing view", MyProfileManager.nameEvent(event));
                notifyMySlogansChanged(mMyProfileManager.getProfile().getSlogans());
            }
        });
    }

    public void notifyMySlogansChanged(TreeSet<Slogan> mySlogans) {
        d(TAG, "Updating list, mySlogans: %d", mySlogans.size());

        final List<ListItem> newItems = new ArrayList<>();
        for (Slogan mySlogan : mySlogans) {
            newItems.add(new MySloganListItem(mySlogan));
        }
        newItems.add(new SpacerItem());

        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new PeersDiffCallback(mItems, newItems));
        mItems.clear();
        mItems.addAll(newItems);
        diff.dispatchUpdatesTo(this);

//        ListSynchronizer.syncLists(
//                mItems,
//                newItems,
//                this,
//                (item, newItem) -> item instanceof SpacerItem || item.compareIndex(newItem) > 0
//        );
    }

    @NonNull
    @Override
    public LegacyItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SPACER) {
            return new LegacySpacerHolder(mInflater.inflate(R.layout.common_list_spacer, parent, false));
        }
        return new MySloganHolder(
                mInflater.inflate(R.layout.profile_list_item_slogan, parent, false),
                mContext,
                mOnMySloganActionCallback,
                this.mCollapseExpandHandler
        );
    }

    @Override
    public void onBindViewHolder(@NonNull LegacyItemViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if (holder instanceof LegacySpacerHolder) {
            return;
        }

        // Alternating colors
        // The first slogan is colored with accent because because for white background it otherwise
        // would be indistinguishable from my text
        int color = Color.parseColor(mMyProfileManager.getColor());
        holder.colorize(
                position % 2 == 0
                        ? ColorHelper.getAccent(color)
                        : color,
                ColorHelper.getTextColor(color)
        );

    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof SpacerItem) {
            return TYPE_SPACER;
        }
        return TYPE_MY_SLOGAN;
    }
}

