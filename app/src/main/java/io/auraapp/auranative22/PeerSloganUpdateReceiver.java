package io.auraapp.auranative22;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.auraapp.auranative22.Communicator.Peer;
import io.auraapp.auranative22.Communicator.Slogan;

import static io.auraapp.auranative22.Communicator.Communicator.INTENT_PEERS_CHANGED_PEERS;
import static io.auraapp.auranative22.FormattedLog.d;
import static io.auraapp.auranative22.FormattedLog.v;
import static io.auraapp.auranative22.FormattedLog.w;

public class PeerSloganUpdateReceiver extends BroadcastReceiver {

    private static final String TAG = "@aura/peerSloganReceiver";
    private final TreeSet<Slogan> mPeerSlogans;
    private final SloganListAdapter mAdapter;

    public PeerSloganUpdateReceiver(TreeSet<Slogan> peerSlogans, SloganListAdapter Adapter) {
        mPeerSlogans = peerSlogans;
        mAdapter = Adapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        v(TAG, "onReceive, intent: %s", intent);

        Bundle extras = intent.getExtras();
        if (extras == null) {
            v(TAG, "Intent has no extras, ignoring it, intent: %s", intent);
            return;
        }

        @SuppressWarnings("unchecked")
        Set<Peer> peers = (Set<Peer>) extras.getSerializable(INTENT_PEERS_CHANGED_PEERS);

        if (peers == null) {
            w(TAG, "Peers payload is null, ignoring it, intent: %s", intent);
            return;
        }


        final Set<Slogan> uniqueSlogans = new HashSet<>();
        for (Peer peer : peers) {
            uniqueSlogans.addAll(peer.mSlogans);
        }

        v(TAG, "Syncing %d previous slogans to %d slogans from %d peers", mPeerSlogans.size(), uniqueSlogans.size(), peers.size());

        w(TAG, "xx %s", uniqueSlogans);
        w(TAG, "xy %s", mPeerSlogans);

        if (mPeerSlogans.retainAll(uniqueSlogans) || mPeerSlogans.addAll(uniqueSlogans)) {
            w(TAG, "xz %s", mPeerSlogans);
            mAdapter.notifyDataSetChanged();
        }
//
//
//        int found = 0;
//        int gone = 0;
//
//        // Remove slogans that are gone
//        for (ListItem item : mList.subList(0, mList.size())) {
//            if (!uniqueSlogans.contains(item.getSlogan())) {
//                mList.remove(item);
//                gone++;
//            }
//        }
//
//        for (Slogan slogan : uniqueSlogans) {
//            ListItem foundSlogan = new ListItem(slogan, false);
//            if (!mList.contains(foundSlogan)) {
//                mList.add(foundSlogan);
//                found++;
//            }
//        }
//
//        if (found == 0 && gone == 0) {
//            v(TAG, "Received updated peers but nothing changed, peers: %d, unique logans: %d", peers.size(), uniqueSlogans.size());
//        } else {
//            d(TAG, "Received updated peers, %d slogans found, %d slogans gone, peers: %d, unique logans: %d", found, gone, peers.size(), uniqueSlogans.size());
//            mAdapter.notifyDataSetChanged();
//        }
    }
}
