package com.yamimaplibrary.bean;

import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.MinewBeacon;

public class Beacon {

    /**
     * minor : 3202
     * rssi : -50
     * major : 10071
     * proximity : 2
     * accuracy : 0.31
     * uuid : AB8190D5-D11E-4941-ACC4-42F30510B408
     */

    private int minor;
    private String rssi;
    private int major;
    private String uuid;

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public static Beacon minewBeacon2Beacon(MinewBeacon minewBeacon) {
        Beacon beacon = null;
        String uuid = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_UUID).getStringValue();
        // 根据uuid过滤
        if ("FDA50693-A4E2-4FB1-AFCF-C6EB07647825".equalsIgnoreCase(uuid)) {
            beacon = new Beacon();
            beacon.setUuid(uuid);
        } else {
            return null;
        }
        String major = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Major).getStringValue();
        beacon.setMajor(Integer.parseInt(major));
        String minor = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Minor).getStringValue();
        beacon.setMinor(Integer.parseInt(minor));
        String rssi = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getStringValue();
        beacon.setRssi(rssi);
        return beacon;
    }
}