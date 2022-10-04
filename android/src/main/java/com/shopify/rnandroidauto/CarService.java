package com.shopify.rnandroidauto;

import androidx.annotation.NonNull;
import androidx.car.app.CarAppService;
import androidx.car.app.Session;
import androidx.car.app.validation.HostValidator;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;

public final class CarService extends CarAppService {
    private ReactInstanceManager mReactInstanceManager;

    @Override
    public void onCreate() {
        mReactInstanceManager = ((ReactApplication) getApplication()).getReactNativeHost().getReactInstanceManager();
    }

    @NonNull
    @Override
    public HostValidator createHostValidator() {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR;
    }

    @NonNull
    @Override
    public Session onCreateSession() {
        return new CarServiceSession(mReactInstanceManager.getCurrentReactContext());
    }
}
