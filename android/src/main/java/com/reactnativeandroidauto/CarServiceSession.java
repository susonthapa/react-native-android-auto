package com.reactnativeandroidauto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.car.app.Screen;
import androidx.car.app.Session;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.appregistry.AppRegistry;
import com.facebook.react.modules.core.TimingModule;

public class CarServiceSession extends Session {
    private CarScreen screen;
    private final ReactInstanceManager reactInstanceManager;
    private final ReactContext reactContext;

    public CarServiceSession(ReactContext reactContext) {
        super();
        this.reactContext = reactContext;
        reactInstanceManager = ((ReactApplication) reactContext.getApplicationContext()).getReactNativeHost().getReactInstanceManager();
    }

    @NonNull
    @Override
    public Screen onCreateScreen(@NonNull Intent intent) {
        Log.d("Auto", "On create screen " + intent.getAction() + " - " + intent.getDataString());
        screen = new CarScreen(getCarContext());
        screen.setMarker("root");
        runJsApplication();

        return screen;
    }

    private void runJsApplication() {
        if (reactContext == null) {
            reactInstanceManager.addReactInstanceEventListener(
                    new ReactInstanceManager.ReactInstanceEventListener() {
                        @Override
                        public void onReactContextInitialized(ReactContext reactContext) {
                            invokeStartTask(reactContext);
                            reactInstanceManager.removeReactInstanceEventListener(this);
                        }
                    });
            reactInstanceManager.createReactContextInBackground();
        } else {
            invokeStartTask(reactContext);
        }
    }

    private void invokeStartTask(ReactContext reactContext) {
        try {
            if (reactInstanceManager == null) {
                return;
            }

            if (reactContext == null) {
                return;
            }

            CatalystInstance catalystInstance = reactContext.getCatalystInstance();
            String jsAppModuleName = "androidAuto";

            WritableNativeMap appParams = new WritableNativeMap();
            appParams.putDouble("rootTag", 1.0);
            @Nullable Bundle appProperties = Bundle.EMPTY;
            if (appProperties != null) {
                appParams.putMap("initialProps", Arguments.fromBundle(appProperties));
            }

            catalystInstance.getJSModule(AppRegistry.class).runApplication(jsAppModuleName, appParams);
            TimingModule timingModule = reactContext.getNativeModule(TimingModule.class);

            AndroidAutoModule carModule = reactInstanceManager
                    .getCurrentReactContext()
                    .getNativeModule(AndroidAutoModule.class);
            carModule.setCarContext(getCarContext(), screen);

            timingModule.onHostResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
