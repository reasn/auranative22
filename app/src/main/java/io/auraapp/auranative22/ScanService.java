package io.auraapp.auranative22;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.le.ScanSettings.SCAN_MODE_BALANCED;
import static io.auraapp.auranative22.FormattedLog.d;
import static io.auraapp.auranative22.FormattedLog.e;
import static io.auraapp.auranative22.FormattedLog.i;
import static io.auraapp.auranative22.FormattedLog.v;
import static io.auraapp.auranative22.FormattedLog.w;

public class ScanService extends Service {

    private final static String TAG = "@aura/ble";

    public static final int FOREGROUND_ID = 1338;

    private static Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private Handler mHandler;
    private UUID mServiceUuid;
    private UUID mSlogan1Uuid;
    private UUID mSlogan2Uuid;
    private UUID mSlogan3Uuid;
    private boolean mQueued = false;

    private static final long PEER_FORGET_AFTER = 1000 * 60 * 2;
    private static final long PEER_REFRESH_AFTER = 1000 * 60;
    private static final long PEER_CONNECT_TIMEOUT = 1000 * 10;

    private HashMap<String, Peer> peers = new HashMap<>();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        final Notification notification = new Notification.Builder(this)
                .setContentTitle("ContentTitle")
                .setContentText("ContentTExt")
                .setContentIntent(pendingIntent)
                .setTicker("ticker")
                .build();

        startForeground(FOREGROUND_ID, notification);

        mSlogan1Uuid = UUID.fromString(getString(R.string.ble_uuid_slogan_1));
        mSlogan2Uuid = UUID.fromString(getString(R.string.ble_uuid_slogan_2));
        mSlogan3Uuid = UUID.fromString(getString(R.string.ble_uuid_slogan_3));

        mServiceUuid = UUID.fromString(getString(R.string.ble_uuid_service));

        mHandler = new Handler();

        mHandler.postDelayed(() -> {
            scan();
            returnControl();
        }, 100);

        return START_STICKY;
    }


    private void returnControl() {
        if (mQueued) {
            return;
        }
        mHandler.postDelayed(this::main, 1000);
        mQueued = true;
    }


    private void main() {
        mQueued = false;
        v(TAG, "Making decisions for %d peers", peers.size());

        long now = System.currentTimeMillis();

        for (String address : peers.keySet()) {

            Peer peer = peers.get(address);

            w(TAG, "%s", peer.toLogString());

            try {
                if (peer.shouldDisconnect) {
                    d(TAG, "Disconnecting peer, device: %s", address);
                    peer.gatt.close();
                    peer.gatt = null;
                    peer.service = null;
                    peer.connected = false;
                    peer.shouldDisconnect = false;
                    // Letting some time pass before we do the next connection attempt
                    continue;
                }

                if (!peer.connected) {
                    if (peer.lastConnectAttempt != null && now - peer.lastConnectAttempt <= PEER_CONNECT_TIMEOUT) {
                        v(TAG, "Nothing to do, connection attempt is in progress, device: %s", address);

                    } else if (peer.lastConnectAttempt != null && now - peer.lastConnectAttempt > PEER_CONNECT_TIMEOUT) {
                        d(TAG, "Connection timeout, closing gatt, device: %s", address);
                        peer.shouldDisconnect = true;
                        peer.errors++;

                    } else if (now - peer.lastSeenTimestamp > PEER_FORGET_AFTER) {
                        v(TAG, "Forgetting peer, device: %s", address);
                        peer.device = null;
                        peers.remove(address);

                    } else if (peer.lastFullRetrievalTimestamp == null || now - peer.lastFullRetrievalTimestamp > PEER_REFRESH_AFTER) {
                        d(TAG, "Connecting to gatt server to refresh, device: %s", address);
                        peer.connectionAttempts++;
                        peer.lastConnectAttempt = now;
                        peer.gatt = peer.device.connectGatt(this, false, mGattCallback);

                    } else {
                        v(TAG, "Nothing to do for disconnected peer, device: %s", address);
                    }
                    continue;
                }

                // peer is currently connected

                if (peer.service == null) {
                    i(TAG, "Connected to %s, discovering services", address);
                    peer.gatt.discoverServices();
                    continue;
                }

                if (peer.slogan1 == null) {
                    requestSlogan(peer, mSlogan1Uuid);
                    continue;
                }
                if (peer.slogan2 == null) {
                    requestSlogan(peer, mSlogan2Uuid);
                    continue;
                }
                if (peer.slogan3 == null) {
                    requestSlogan(peer, mSlogan3Uuid);
                    continue;
                }

                d(TAG, "All slogans retrieved, should disconnect, address: %s", address);
                peer.lastFullRetrievalTimestamp = now;
                peer.shouldDisconnect = true;
            } catch (Exception e) {
                e(TAG, "Unhandled exception, peer: %s", peer.toLogString());
                throw e;
            }
        }
        returnControl();
    }

    private void requestSlogan(Peer peer, UUID uuid) {
        d(TAG, "Requesting characteristic, gatt: %s, characteristic: %s", peer.device.getAddress(), uuid);
        BluetoothGattCharacteristic chara = peer.service.getCharacteristic(UUID.fromString(getString(R.string.ble_uuid_slogan_1)));
        if (!peer.gatt.readCharacteristic(chara)) {
            d(TAG, "Failed to request slogan. Disconnecting, gatt: %s, characteristic: %s", peer.device.getAddress(), uuid);
            peer.shouldDisconnect = true;
            peer.errors++;
        }
    }

    private void scan() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();

        if (scanner == null) {
            throw new RuntimeException("BT disabled");
        }

        ScanSettings settings = new ScanSettings.Builder()
                //   .setCallbackType(CALLBACK_TYPE_ALL_MATCHES)
                //   .setMatchMode(MATCH_NUM_MAX_ADVERTISEMENT)
                .setScanMode(SCAN_MODE_BALANCED)
                .build();

        List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
        scanFilters.add(new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(UUID.fromString(getString(R.string.ble_uuid_service))))
                .build());

        scanner.startScan(scanFilters, settings, new ScanCallback() {

            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                v(TAG, "onScanResult callbackType: %d, result: %s", callbackType, result.getDevice().getAddress());

                handleResults(new ScanResult[]{result});
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                v(TAG, "onBatchScanResults callbackType: %d, result: %s", results == null ? "null" : results.toString());
                if (results != null) {
                    handleResults((ScanResult[]) results.toArray());
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                d(TAG, "onScanFailed errorCode: %d", errorCode);
                e(TAG, "error during scan " + errorCode);
            }
        });
        i(TAG, "started scanning");
    }

    private boolean assertPeer(String address, BluetoothGatt gatt, String operation) {
        if (peers.containsKey(address)) {
            return true;
        }
        d(TAG, "No peer available for connection change (probably already forgotten), operation: %s, address: %s", operation, address);

        // TODO can this throw errors? Are they safe to be ignored?;
        gatt.close();

        return false;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String address = gatt.getDevice().getAddress();
            d(TAG, "onConnectionStateChange, gatt: %s, status: %d, newState: %d", address, status, newState);
            if (!assertPeer(address, gatt, "onConnectionStateChange")) {
                return;
            }

            if (newState == STATE_CONNECTED) {
                d(TAG, "Connected to %s", address);
                peers.get(address).connected = true;
                returnControl();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                d(TAG, "Disconnected from %s", address);
                peers.get(address).connected = false;
                returnControl();
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            String address = gatt.getDevice().getAddress();
            v(TAG, "onServicesDiscovered, gatt: %s, status: %d", address, status);
            if (!assertPeer(address, gatt, "onServicesDiscovered")) {
                return;
            }

            if (status != BluetoothGatt.GATT_SUCCESS) {
                w(TAG, "onServicesDiscovered unsuccessful, status: %d", status);
                peers.get(address).shouldDisconnect = true;
                peers.get(address).errors++;
                returnControl();
                return;
            }

            d(TAG, "Discovered %d services, gatt: %s, services: %s", gatt.getServices().size(), address, gatt.getServices().toString());

            BluetoothGattService service = gatt.getService(mServiceUuid);

            if (service == null) {
                d(TAG, "Service is null, disconnecting, address: %s", address);
                peers.get(address).shouldDisconnect = true;
                peers.get(address).errors++;
                returnControl();
                return;
            }
            peers.get(address).service = service;
            returnControl();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            String address = gatt.getDevice().getAddress();
            d(TAG, "onCharacteristicRead, gatt: %s, characteristic: %s, status: %d", address, characteristic.getUuid(), status);

            if (!assertPeer(address, gatt, "onCharacteristicRead")) {
                return;
            }

            if (status != BluetoothGatt.GATT_SUCCESS) {
                w(TAG, "onCharacteristicRead unsuccessful, status: %d", status);
                peers.get(address).shouldDisconnect = true;
                peers.get(address).errors++;
                returnControl();
                return;
            }
            byte[] value = characteristic.getValue();
            if (value == null) {
                w(TAG, "Retrieved null slogan, address: %s", address);
                peers.get(address).shouldDisconnect = true;
                peers.get(address).errors++;
                returnControl();
                return;
            }

            UUID uuid = characteristic.getUuid();
            String slogan = new String(value, UTF8_CHARSET);
            w(TAG, "Retrieved slogan, device: %s, slogan: %s", address, slogan);
            if (mSlogan1Uuid.equals(uuid)) {
                peers.get(address).slogan1 = slogan;

            } else if (mSlogan2Uuid.equals(uuid)) {
                peers.get(address).slogan2 = slogan;

            } else if (mSlogan3Uuid.equals(uuid)) {
                peers.get(address).slogan3 = slogan;
            } else {
                w(TAG, "Characteristic retrieved matches no slogan UUID, address: %s, uuid: %s", address, uuid);
            }
            returnControl();
        }
    };

    private void handleResults(ScanResult[] results) {
        for (ScanResult result : results) {

            String address = result.getDevice().getAddress();
            if (peers.containsKey(address)) {
                v(TAG, "Device %s is already known, doing nothing", address);
            } else {
                i(TAG, "Device %s is yet unknown", address);
                peers.put(address, Peer.create(result.getDevice()));
            }
            peers.get(address).lastSeenTimestamp = System.currentTimeMillis();
        }
        returnControl();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
