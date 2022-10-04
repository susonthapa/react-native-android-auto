package com.reactnativeandroidauto;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.car.app.CarContext;
import androidx.car.app.CarToast;
import androidx.car.app.ScreenManager;
import androidx.car.app.model.Template;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.debug.DevSettingsModule;

import java.util.WeakHashMap;

@ReactModule(name = AndroidAutoModule.MODULE_NAME)
public class AndroidAutoModule extends ReactContextBaseJavaModule {
    static final String MODULE_NAME = "CarModule";

    private static ReactApplicationContext mReactContext;
    private CarContext mCarContext;
    private CarScreen mCurrentCarScreen;
    private ScreenManager mScreenManager;

    private WeakHashMap<String, CarScreen> carScreens;
    private WeakHashMap<CarScreen, ReactCarRenderContext> reactCarRenderContextMap;

    public String getName() {
        return MODULE_NAME;
    }

    private Handler handler;

    AndroidAutoModule(ReactApplicationContext context) {
        super(context);
        carScreens = new WeakHashMap();
        reactCarRenderContextMap = new WeakHashMap();
        handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(() -> {
            Log.d("TODO", "sending event to the react side");
            sendEvent("autoTesting", new WritableNativeMap());
        }, 5000);
        mReactContext = context;
    }

    @ReactMethod
    public void invalidate(String name) {
        handler.post(() -> {
            CarScreen screen = getScreen(name);
            if (screen == mScreenManager.getTop()) {
                screen.invalidate();
            }
        });
    }

    @ReactMethod
    public void setTemplate(String name, ReadableMap renderMap, Callback callback) {
        ReactCarRenderContext reactCarRenderContext = new ReactCarRenderContext();
        CarScreen screen = getScreen(name);
        if (screen == null) {
            screen = mCurrentCarScreen;
            Log.d("AUTO", "Screen " + name + " not found!");
        }

        reactCarRenderContext.setEventCallback(callback).setScreenMarker(screen.getMarker());

        Template template = parseTemplate(renderMap, reactCarRenderContext);
        reactCarRenderContextMap.remove(screen);
        reactCarRenderContextMap.put(screen, reactCarRenderContext);

        screen.setTemplate(template);
    }

    @ReactMethod
    public void pushScreen(String name, ReadableMap renderMap, Callback callback) {
        ReactCarRenderContext reactCarRenderContext = new ReactCarRenderContext();
        reactCarRenderContext.setEventCallback(callback).setScreenMarker(name);

        Template template = parseTemplate(renderMap, reactCarRenderContext);

        CarScreen screen = new CarScreen(mCarContext);
        reactCarRenderContextMap.remove(screen);
        reactCarRenderContextMap.put(screen, reactCarRenderContext);

        screen.setMarker(name);
        screen.setTemplate(template);
        carScreens.put(name, screen);
        mCurrentCarScreen = screen;
        mScreenManager.push(screen);
    }

    @ReactMethod
    public void popScreen() {
        mScreenManager.pop();
        removeScreen(mCurrentCarScreen);
        mCurrentCarScreen = (CarScreen) mScreenManager.getTop();
    }

    @ReactMethod
    public void mapNavigate(String address) {
        mCarContext.startCarApp(new Intent(CarContext.ACTION_NAVIGATE, Uri.parse("geo:0,0?q=" + address)));
    }

    @ReactMethod
    public void toast(String text, int duration) {
        CarToast.makeText(mCarContext, text, duration).show();
    }

    @ReactMethod
    public void reload() {
        DevSettingsModule devSettingsModule = mReactContext.getNativeModule(DevSettingsModule.class);
        if (devSettingsModule != null) {
            devSettingsModule.reload();
        }
    }

    @ReactMethod
    public void finishCarApp() {
        mCarContext.finishCarApp();
    }

    @ReactMethod
    public void setEventCallback(String name, Callback callback) {
        CarScreen screen = (CarScreen) getScreen(name);

        Log.d("AUTO", "Set callback 1 for " + name);
        if (screen == null) {
            return;
        }

        Log.d("AUTO", "Set callback 2 for " + name);
        ReactCarRenderContext reactCarRenderContext = reactCarRenderContextMap.get(screen);

        if (reactCarRenderContext == null) {
            return;
        }

        Log.d("AUTO", "Set callback 3 for " + name);
        reactCarRenderContext.setEventCallback(callback);
    }

    public void setCarContext(CarContext carContext, CarScreen currentCarScreen) {
        mCarContext = carContext;
        mCurrentCarScreen = currentCarScreen;
        mScreenManager = currentCarScreen.getScreenManager();
        carScreens.put("root", mCurrentCarScreen);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d("AUTO", "Back button pressed");
                sendEvent("android_auto:back_button", new WritableNativeMap());
            }
        };

        carContext.getOnBackPressedDispatcher().addCallback(callback);

        handler.postDelayed(() -> {
            Log.d("TODO", "sending event to the react side");
            sendEvent("android_auto:ready", new WritableNativeMap());
            sendEvent("autoTesting", new WritableNativeMap());
        }, 1000);
    }

    private Template parseTemplate(ReadableMap renderMap, ReactCarRenderContext reactCarRenderContext) {
        TemplateParser templateParser = new TemplateParser(reactCarRenderContext);
        return templateParser.parseTemplate(renderMap);
    }

    private CarScreen getScreen(String name) {
        return carScreens.get(name);
    }

    private void removeScreen(CarScreen screen) {
        WritableNativeMap params = new WritableNativeMap();
        params.putString("screen", screen.getMarker());

        sendEvent("android_auto:remove_screen", params);

        carScreens.values().remove(screen);
    }

    private void sendEvent(String eventName, Object params) {
        mReactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}
