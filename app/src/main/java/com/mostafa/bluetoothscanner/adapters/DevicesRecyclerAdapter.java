package com.mostafa.bluetoothscanner.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mostafa.bluetoothscanner.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.bluetooth.BluetoothClass.Device.Major.COMPUTER;
import static android.bluetooth.BluetoothClass.Device.Major.PERIPHERAL;
import static android.bluetooth.BluetoothClass.Device.Major.PHONE;
import static android.bluetooth.BluetoothClass.Device.Major.WEARABLE;

public class DevicesRecyclerAdapter extends RecyclerView.Adapter<DevicesRecyclerAdapter.ViewHolder> {

    private static final String DEVICES = "Devices";

    private ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private LayoutInflater layoutInflater;

    public DevicesRecyclerAdapter(@NonNull Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void saveInstanceState(Bundle outInstanceState) {
        outInstanceState.putParcelableArrayList(DEVICES, devices);
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        clear();
        List<BluetoothDevice> savedDevices = savedInstanceState.getParcelableArrayList(DEVICES);
        add(savedDevices);
    }

    public DevicesRecyclerAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    @NonNull
    @Override
    public DevicesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View contentView = layoutInflater.inflate(R.layout.list_item_device, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesRecyclerAdapter.ViewHolder holder, int position) {
        BluetoothDevice device = devices.get(position);
        holder.deviceNameTextView.setText(device.getName());
        holder.macAddressTextView.setText(device.getAddress());
        holder.deviceIconImageView.setImageResource(getResourceMajorClassDrawable(device));
    }

    @DrawableRes
    private int getResourceMajorClassDrawable(BluetoothDevice device) {
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

    public void add(BluetoothDevice device) {
        devices.add(device);
        notifyItemInserted(devices.size() - 1);
    }

    public void add(Collection<BluetoothDevice> devices) {
        int size = this.devices.size();
        this.devices.addAll(devices);
        notifyItemRangeInserted(size, devices.size());
    }

    public void clear() {
        int size = devices.size();
        devices.clear();
        notifyItemRangeRemoved(0, size);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView deviceNameTextView;
        TextView macAddressTextView;
        ImageView deviceIconImageView;

        ViewHolder(View v) {
            super(v);
            findViewsById(v);
            addListeners();
        }

        private void findViewsById(View v) {
            deviceNameTextView = v.findViewById(R.id.device_name_text_view);
            macAddressTextView = v.findViewById(R.id.mac_address_text_view);
            deviceIconImageView = v.findViewById(R.id.device_icon_image_view);
        }

        private void addListeners() {
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(devices.get(getAdapterPosition()), getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemLongClick(devices.get(getAdapterPosition()), getAdapterPosition());
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BluetoothDevice device, int position);

        void onItemLongClick(BluetoothDevice device, int position);
    }
}