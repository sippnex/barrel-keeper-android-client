package com.sippnex.barrelkeeper.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.UUID;

public class BluetoothService {

    private static final String TAG = "BluetoothService";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
    private static final String deviceAddress = "B8:27:EB:5B:59:0B";

    private Context context;
    private BluetoothAdapter adapter;
    private BluetoothDevice device;
    private UUID uuid;
    private BluetoothConnectionListener listener;
    private BluetoothConnectionThread connectionThread;

    public BluetoothService(Context context, BluetoothConnectionListener listener) {
        this.context = context;
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        this.device = adapter.getRemoteDevice(deviceAddress);
        this.uuid = MY_UUID_INSECURE;
        this.listener = listener;
    }

    public void startConnection() {
        connectionThread = new BluetoothConnectionThread(context, adapter, device, uuid, listener);
        connectionThread.start();
    }

}
