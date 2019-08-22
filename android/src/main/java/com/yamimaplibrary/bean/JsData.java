package com.yamimaplibrary.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * JsData
 *
 * @Author zhangxing
 * @Data 2019/1/31
 * @Email zhangxing@yunjinginc.com
 * @Organization 西安云景智维科技有限公司
 */
public class JsData {

    private String errMsg = "onSearchBeacons:ok";
    private List<Beacon> beacons = new ArrayList<>();

    public String getErrMsg() {
        return errMsg;
    }

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<Beacon> beacons) {
        this.beacons = beacons;
    }
}
