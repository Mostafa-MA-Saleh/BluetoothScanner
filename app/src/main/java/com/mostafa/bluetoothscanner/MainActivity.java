package com.mostafa.bluetoothscanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class MainActivity extends AppCompatActivity {

    private final int RQST_BLUETOOTH_SCAN = 50;
    private final int RQST_BLUETOOTH_PAIRED = 80;

    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private RecyclerAdapter mRecyclerAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mRecyclerAdapter.add(device);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("Scanning for Devices...");
                progressDialog.setMessage("Press Anywhere to Cancel...");
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mBluetoothAdapter.cancelDiscovery();
                        Toast.makeText(getApplicationContext(), "Scan Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                progressDialog.show();
                mRecyclerAdapter.clear();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver, filter);
        setContentView(R.layout.activity_main);
        Button btnScan = (Button) findViewById(R.id.ScanButton);
        Button btnPaired = (Button) findViewById(R.id.PairedButton);
        mRecyclerAdapter = new RecyclerAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), 1));
        recyclerView.setAdapter(mRecyclerAdapter);
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Toast.makeText(getApplicationContext(), ((TextView) v.findViewById(R.id.DeviceNameTextView)).getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                Toast.makeText(getApplicationContext(), ((TextView) v.findViewById(R.id.MACTextView)).getText().toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, RQST_BLUETOOTH_PAIRED);
            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, RQST_BLUETOOTH_SCAN);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RQST_BLUETOOTH_SCAN) {
                mBluetoothAdapter.startDiscovery();
            } else if (requestCode == RQST_BLUETOOTH_PAIRED) {
                mRecyclerAdapter.clear();
                mRecyclerAdapter.add(new ArrayList<>(mBluetoothAdapter.getBondedDevices()));
            }
        }
    }
}

