package com.yamimaplibrary;

import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;

/**
 * JsInterface
 *
 * @Author zhangxing
 * @Data 2019/1/31
 * @Email zhangxing@yunjinginc.com
 * @Organization 西安云景智维科技有限公司
 */
public class JsInterface {

    private static final int START_BEACON = 1;
    private static final int SEARCH_BEACON = 2;
    private static final int STOP_BEACON = 3;

    private OnJsListener mOnJsListener;

    @JavascriptInterface
    public void startBeacon() {
        Message msg = new Message();
        msg.what = START_BEACON;
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void searchBeacon() {
        Message msg = new Message();
        msg.what = SEARCH_BEACON;
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void stopBeacon() {
        Message msg = new Message();
        msg.what = STOP_BEACON;
        mHandler.sendMessage(msg);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_BEACON:
                    if (mOnJsListener != null) {
                        mOnJsListener.onStartBeacon();
                    }
                    break;
                case SEARCH_BEACON:
                    if (mOnJsListener != null) {
                        mOnJsListener.onSearchBeacon();
                    }
                    break;
                case STOP_BEACON:
                    if (mOnJsListener != null) {
                        mOnJsListener.onStopBeacon();
                    }
                    break;
                default:
            }
        }
    };

    public void setOnJsListener(OnJsListener listener) {
        mOnJsListener = listener;
    }

}