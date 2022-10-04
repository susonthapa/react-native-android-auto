package com.shopify.rnandroidauto;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

@ReactModule(name = TestModule.MODULE_NAME)
public class TestModule extends ReactContextBaseJavaModule {

    static final String MODULE_NAME = "Test";

    public TestModule(ReactApplicationContext context) {
        super(context);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.d("TODO", "sending Testing event to RN");
            sendEvent(context, "Testing", null);
        }, 5000);
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @NonNull
    @Override
    public String getName() {
        return MODULE_NAME;
    }


}
