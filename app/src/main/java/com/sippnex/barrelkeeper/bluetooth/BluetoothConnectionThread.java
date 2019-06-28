package com.sippnex.barrelkeeper.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

class BluetoothConnectionThread extends Thread {

    private static final String TAG = "BluetoothConnection";

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;
    private UUID uuid;
    private BluetoothSocket socket;
    private ProgressDialog progressDialog;
    private BluetoothConnectionListener listener;

    public BluetoothConnectionThread(Context context, BluetoothAdapter bluetoothAdapter, BluetoothDevice device, UUID uuid, BluetoothConnectionListener listener) {
        Log.d(TAG, "Thread started.");
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
        this.device = device;
        this.uuid = uuid;
        this.listener = listener;
    }

    public void run(){
        connect();
    }

    private void connect() {

        progressDialog = ProgressDialog.show(context,"Connecting via Bluetooth"
                ,"Please wait ...",true);

        BluetoothSocket tmp = null;
        Log.i(TAG, "Thread is running ");

        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
        try {
            Log.d(TAG, "Trying to create InsecureRfcommSocket using UUID: "
                    + uuid );
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e(TAG, "Could not create InsecureRfcommSocket " + e.getMessage());
            if(progressDialog != null) progressDialog.dismiss();
        }

        socket = tmp;

        // Always cancel discovery because it will slow down a connection
        bluetoothAdapter.cancelDiscovery();

        // Make a connection to the BluetoothSocket

        try {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            socket.connect();
            Log.d(TAG, "Bluetooth connection established.");
            if(progressDialog != null) progressDialog.dismiss();

            InputStream mmin = socket.getInputStream();
            int length;
            byte[] buffer = new byte[256];
            //socketInputStream never returns -1 unless connection is broken
            while ((length = mmin.read(buffer)) != -1) {
                receive(buffer, length);
            }
            socket.close();

        } catch (IOException e) {
            // Close the socket
            try {
                socket.close();
                Log.d(TAG, "Bluetooth connection closed.");
            } catch (IOException e1) {
                Log.e(TAG, "Unable to close bluetooth connection: " + e1.getMessage());
                if(progressDialog != null) progressDialog.dismiss();
            }
            Log.d(TAG, "Could not connect to UUID: " + uuid );
            if(progressDialog != null) progressDialog.dismiss();
        }
    }

    private void receive(byte[] data, int length) {
        byte[] outputBuffer = new byte[length];
        for(int i=0; i<length; i++) {
            outputBuffer[i] = data[i];
        }
        try {
            String textData = new String(outputBuffer, "UTF-8");
            Log.d(TAG, "Received data from server: " + textData);
            listener.onReceivingData(textData);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
