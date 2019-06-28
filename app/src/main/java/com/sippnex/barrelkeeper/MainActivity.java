package com.sippnex.barrelkeeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.sippnex.barrelkeeper.bluetooth.BluetoothConnectionListener;
import com.sippnex.barrelkeeper.bluetooth.BluetoothService;

public class MainActivity extends AppCompatActivity {

    private BluetoothService bluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothService = new BluetoothService(MainActivity.this, new BluetoothConnectionListener() {
            @Override
            public void onReceivingData(Object data) {
                Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        bluetoothService.startConnection();
    }
}
