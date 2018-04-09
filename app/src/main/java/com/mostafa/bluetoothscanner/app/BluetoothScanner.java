package com.mostafa.bluetoothscanner.app;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mostafa.bluetoothscanner.utils.PermissionUtils;

import java.lang.ref.WeakReference;
import java.util.Collection;

public class BluetoothScanner extends BroadcastReceiver implements LifecycleObserver {

    private static final int REQUEST_BLUETOOTH_SCAN = 50;
    private static final int REQUEST_BLUETOOTH_PAIRED = 80;
    private static final int REQUEST_COARSE_LOCATION_PERMISSION = 40;

    @NonNull
    private Listener mListener;
    private WeakReference<Activity> mContextWeakReference;
    private boolean mShouldScanOnStart = true;

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean mRegistered;

    public BluetoothScanner(@NonNull Activity activity) {
        mContextWeakReference = new WeakReference<>(activity);
        if (activity instanceof Listener) {
            mListener = (Listener) activity;
            mListener.getLifecycle().addObserver(this);
        } else {
            throw new IllegalArgumentException("The activity has to implement The Listener interface!");
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onStart() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(this, filter);
        mRegistered = true;
        if (mShouldScanOnStart) {
            mBluetoothAdapter.startDiscovery();
            mShouldScanOnStart = false;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onStop() {
        mBluetoothAdapter.cancelDiscovery();
        if (mRegistered) {
            getActivity().unregisterReceiver(this);
            mRegistered = false;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_BLUETOOTH_SCAN:
                    mBluetoothAdapter.startDiscovery();
                    break;
                case REQUEST_BLUETOOTH_PAIRED:
                    Collection<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    for (BluetoothDevice pairedDevice : pairedDevices) {
                        mListener.onDeviceFound(pairedDevice);
                    }
                    break;
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_COARSE_LOCATION_PERMISSION
                && grantResults.length != 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableBluetooth(REQUEST_BLUETOOTH_SCAN);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    mListener.onDeviceFound(device);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    mListener.onScanStarted();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    mListener.onScanFinished();
                    break;
            }
        }
    }

    public void getPairedDevices() {
        enableBluetooth(REQUEST_BLUETOOTH_PAIRED);
    }

    public void enableBluetooth(int requestCode) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        getActivity().startActivityForResult(enableBtIntent, requestCode);
    }

    public void startScanning() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !PermissionUtils.hasPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            PermissionUtils.requestPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_COARSE_LOCATION_PERMISSION);
        } else {
            enableBluetooth(REQUEST_BLUETOOTH_SCAN);
        }
    }

    public void cancelScan() {
        mBluetoothAdapter.cancelDiscovery();
    }

    private Activity getActivity() {
        return mContextWeakReference.get();
    }

    public interface Listener extends LifecycleOwner {
        void onDeviceFound(BluetoothDevice device);

        void onScanStarted();

        void onScanFinished();
    }
}
