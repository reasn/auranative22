package io.auraapp.auraandroid.Communicator;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import io.auraapp.auraandroid.common.PeerStatsSet;

class Device {

    final String mId;
    boolean mOutdated = true;
    long lastSeenTimestamp = 0;
    long lastConnectAttempt = 0;

    final PeerStatsSet stats = new PeerStatsSet();

    final PeerBtServiceSet bt = new PeerBtServiceSet();

    boolean isDiscoveringServices = false;

    boolean connected = false;
    boolean shouldDisconnect = false;
    int connectionAttempts = 0;

    boolean mFetchingAProp = false;

    byte mAdvertisementVersion = 0;

    private final Map<UUID, Boolean> mFreshMap;
    private final Map<UUID, String> mPropertyMap;
    boolean mSynchronizing = false;

    private Device(String id) {
        mFreshMap = new HashMap<>();
        mId = id;

        setAllPropertiesOutdated();

        mPropertyMap = new HashMap<>();

        for (UUID uuid : AdvertisementSet.ADVERTISED_UUIDS) {
            mFreshMap.put(uuid, null);
        }
    }

    static Device create(String id, @NonNull BluetoothDevice btDevice) {
        Device device = new Device(id);
        device.bt.device = btDevice;
        return device;
    }

    Map<UUID, String> props() {
        return mPropertyMap;
    }

    void setAllPropertiesOutdated() {
        for (UUID uuid : AdvertisementSet.ADVERTISED_UUIDS) {
            mFreshMap.put(uuid, false);
        }
    }

    Set<String> buildSlogans() {
        Set<String> slogans = new HashSet<>();
        for (UUID uuid : AdvertisementSet.ADVERTISED_SLOGAN_UUIDS) {
            String slogan = mPropertyMap.get(uuid);
            if (slogan != null && !slogan.equals("")) {
                slogans.add(mPropertyMap.get(uuid));
            }
        }
        return slogans;
    }

    boolean updateWithReceivedAttribute(UUID uuid, String value) throws UnknownAdvertisementException {

        if (!Arrays.asList(AdvertisementSet.ADVERTISED_UUIDS).contains(uuid)) {
            throw new UnknownAdvertisementException(uuid);
        }

        boolean changed = !Objects.equals(mPropertyMap.get(uuid), value);
        mPropertyMap.put(uuid, value);
        mFreshMap.put(uuid, true);

        return changed;
    }

    UUID getFirstOutdatedPropertyUuid() {

        for (UUID uuid : AdvertisementSet.ADVERTISED_UUIDS) {
            if (!mFreshMap.get(uuid)) {
                return uuid;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Device{" +
                "mId='" + mId + '\'' +
                ", mOutdated=" + mOutdated +
                ", lastSeenTimestamp=" + lastSeenTimestamp +
                ", lastConnectAttempt=" + lastConnectAttempt +
                ", stats=" + stats +
                ", bt=" + bt +
                ", isDiscoveringServices=" + isDiscoveringServices +
                ", connected=" + connected +
                ", shouldDisconnect=" + shouldDisconnect +
                ", connectionAttempts=" + connectionAttempts +
                ", mFetchingAProp=" + mFetchingAProp +
                ", mAdvertisementVersion=" + mAdvertisementVersion +
                ", mFreshMap=" + mFreshMap +
                ", mPropertyMap=" + mPropertyMap +
                ", mSynchronizing=" + mSynchronizing +
                '}';
    }
}
