package com.mostafa.bluetoothscanner.activities;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.mostafa.bluetoothscanner.R;
import com.mostafa.bluetoothscanner.adapters.DevicesRecyclerAdapter;
import com.mostafa.bluetoothscanner.app.BluetoothScanner;
import com.mostafa.bluetoothscanner.utils.DialogUtils;

public class MainActivity extends AppCompatActivity implements DevicesRecyclerAdapter.OnItemClickListener, View.OnClickListener, BluetoothScanner.Listener {

    private RecyclerView mRecyclerView;
    private DevicesRecyclerAdapter mDevicesRecyclerAdapter;
    private BluetoothScanner mBluetoothScanner;
    private Dialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeComponents();
        findViewsById();
        setupRecyclerView();
        addListeners();
    }

    private void initializeProgressDialog() {
        mProgressDialog = DialogUtils.showProgressDialog(this, R.string.scanning, false);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mBluetoothScanner.cancelScan();
            }
        });
    }

    private void initializeComponents() {
        initializeProgressDialog();
        buildAdapter();
        mBluetoothScanner = new BluetoothScanner(this);
    }

    private void buildAdapter() {
        mDevicesRecyclerAdapter =
                new DevicesRecyclerAdapter(this)
                        .setOnItemClickListener(this);
    }

    private void addListeners() {
        findViewById(R.id.scan_button).setOnClickListener(this);
        findViewById(R.id.paired_button).setOnClickListener(this);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mDevicesRecyclerAdapter);
    }

    private void findViewsById() {
        mRecyclerView = findViewById(R.id.recycler_view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mBluetoothScanner.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mBluetoothScanner.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mDevicesRecyclerAdapter.saveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mDevicesRecyclerAdapter.restoreInstanceState(savedInstanceState);
    }

    @Override
    public void onItemClick(BluetoothDevice device, int position) {
        Toast.makeText(this, device.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(BluetoothDevice device, int position) {
        Toast.makeText(this, device.getAddress(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.paired_button:
                onPairedClick();
                break;
            case R.id.scan_button:
                onScanClick();
                break;
        }
    }

    private void onPairedClick() {
        mDevicesRecyclerAdapter.clear();
        mBluetoothScanner.getPairedDevices();
    }

    private void onScanClick() {
        mDevicesRecyclerAdapter.clear();
        mBluetoothScanner.startScanning();
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        mDevicesRecyclerAdapter.add(device);
    }

    @Override
    public void onScanStarted() {
        mProgressDialog.show();
        mDevicesRecyclerAdapter.clear();
    }

    @Override
    public void onScanFinished() {
        mProgressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.scan_complete, Toast.LENGTH_SHORT).show();
    }
}

