package com.yamimaplibrary;

public interface OnJsListener {

    /**
     * 开启查找周边ibeacon设备
     */
    void onStartBeacon();

    /**
     * 监听周边ibeacon设备
     */
    void onSearchBeacon();

    /**
     * 关闭查找周边ibeacon
     */
    void onStopBeacon();
}
