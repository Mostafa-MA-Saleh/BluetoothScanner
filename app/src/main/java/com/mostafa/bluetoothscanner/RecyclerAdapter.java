package com.mostafa.bluetoothscanner;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.bluetooth.BluetoothClass.Device.Major.COMPUTER;
import static android.bluetooth.BluetoothClass.Device.Major.PERIPHERAL;
import static android.bluetooth.BluetoothClass.Device.Major.PHONE;
import static android.bluetooth.BluetoothClass.Device.Major.WEARABLE;

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<BluetoothDevice> devices;

    RecyclerAdapter() {
        this.devices = new ArrayList<>();
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        holder.device = devices.get(position);
        holder.lblName.setText(holder.device.getName());
        holder.lblMACAddress.setText(holder.device.getAddress());
        holder.imgIcon.setImageResource(getResourceMajorClass(holder.device));
    }

    private int getResourceMajorClass(BluetoothDevice device) {

        switch (device.getBluetoothClass().getMajorDeviceClass()) {
            case COMPUTER:
                return R.drawable.computer;
            case PERIPHERAL:
                return R.drawable.peripheral;
            case PHONE:
                return R.drawable.phone;
            case WEARABLE:
                return R.drawable.wearable;
            default:
                return R.drawable.other;
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    void add(BluetoothDevice device) {
        devices.add(device);
        notifyDataSetChanged();
    }

    void add(ArrayList<BluetoothDevice> devices) {
        for (BluetoothDevice device : devices)
            this.devices.add(device);
        notifyDataSetChanged();
    }

    void clear() {
        devices.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView lblName;
        TextView lblMACAddress;
        ImageView imgIcon;
        BluetoothDevice device;

        ViewHolder(View v) {
            super(v);
            lblName = (TextView) v.findViewById(R.id.DeviceNameTextView);
            lblMACAddress = (TextView) v.findViewById(R.id.MACTextView);
            imgIcon = (ImageView) v.findViewById(R.id.imageView);
        }
    }
}