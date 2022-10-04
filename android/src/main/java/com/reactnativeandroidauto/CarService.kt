package com.reactnativeandroidauto

import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceManager

class CarService : CarAppService() {
  private lateinit var reactInstanceManager: ReactInstanceManager
  override fun onCreate() {
    reactInstanceManager =
      (application as ReactApplication).reactNativeHost.reactInstanceManager
  }

  override fun createHostValidator(): HostValidator {
    return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
  }

  override fun onCreateSession(): Session {
    return CarServiceSession(reactInstanceManager.currentReactContext)
  }
}
