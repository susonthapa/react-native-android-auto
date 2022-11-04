package com.reactnativeandroidauto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.car.app.Screen
import androidx.car.app.Session
import com.facebook.react.ReactInstanceManager
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableNativeMap
import com.facebook.react.modules.appregistry.AppRegistry
import com.facebook.react.modules.core.TimingModule

class CarServiceSession(private val reactInstanceManager: ReactInstanceManager) : Session() {
  private lateinit var screen: CarScreen

  override fun onCreateScreen(intent: Intent): Screen {
    Log.d(TAG, "On create screen " + intent.action + " - " + intent.dataString)
    screen = CarScreen(carContext)
    screen.marker = "root"
    runJsApplication()
    return screen
  }


  private fun runJsApplication() {
    val reactContext = reactInstanceManager.currentReactContext
    if (reactContext == null) {
      reactInstanceManager.addReactInstanceEventListener(
        object : ReactInstanceManager.ReactInstanceEventListener {
          override fun onReactContextInitialized(reactContext: ReactContext) {
            invokeStartTask(reactContext)
            reactInstanceManager.removeReactInstanceEventListener(this)
          }
        })
      reactInstanceManager.createReactContextInBackground()
    } else {
      invokeStartTask(reactContext)
    }
  }

  private fun invokeStartTask(reactContext: ReactContext) {
    try {
      val catalystInstance = reactContext.catalystInstance
      val jsAppModuleName = "androidAuto"
      val appParams = WritableNativeMap()
      appParams.putDouble("rootTag", 1.0)
      val appProperties = Bundle.EMPTY
      if (appProperties != null) {
        appParams.putMap("initialProps", Arguments.fromBundle(appProperties))
      }
      catalystInstance.getJSModule(AppRegistry::class.java)
        .runApplication(jsAppModuleName, appParams)
      val timingModule = reactContext.getNativeModule(
        TimingModule::class.java
      )
      val carModule = reactInstanceManager
        .currentReactContext?.getNativeModule(AndroidAutoModule::class.java)
      carModule!!.setCarContext(carContext, screen)
      timingModule!!.onHostResume()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  companion object {
    const val TAG = "CarServiceSession"
  }
}
