package com.yamimaplibrary;

import android.webkit.WebView;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

public class RNWebViewLibraryManager extends ViewGroupManager<WebView> {
    ReactApplicationContext context;
    @Override
    public String getName() {
        return "RNWebViewLibraryManager";
    }

    public RNWebViewLibraryManager( ReactApplicationContext context ) {
        this.context = context;
    }



    @Override
    protected WebView createViewInstance(ThemedReactContext reactContext) {
        RNYamiMapLibraryModule module = new RNYamiMapLibraryModule(context);
        WebView mWebView = module.initView();
        return mWebView;
    }


}
