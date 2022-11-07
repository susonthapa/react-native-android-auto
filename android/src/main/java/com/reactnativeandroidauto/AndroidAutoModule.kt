package com.reactnativeandroidauto

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.ScreenManager
import androidx.car.app.model.Template
import com.facebook.react.bridge.*
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.facebook.react.modules.debug.DevSettingsModule
import com.reactnativeandroidauto.template.TemplateFactory
import java.util.*

@ReactModule(name = AndroidAutoModule.MODULE_NAME)
class AndroidAutoModule internal constructor(private val reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {
  private lateinit var carContext: CarContext
  private var currentCarScreen: CarScreen? = null
  private var screenManager: ScreenManager? = null
  private val carScreens: WeakHashMap<String, CarScreen> = WeakHashMap()
  private val reactCarRenderContextMap: WeakHashMap<CarScreen, ReactCarRenderContext> =
    WeakHashMap()
  private val handler: Handler = Handler(Looper.getMainLooper())

  override fun getName(): String {
    return MODULE_NAME
  }

  @ReactMethod
  fun invalidate(name: String) {
    handler.post {
      val screen = getScreen(name)
      if (screen === screenManager!!.top) {
        screen.invalidate()
      }
    }
  }

  @ReactMethod
  fun setTemplate(name: String, renderMap: ReadableMap, callback: Callback?) {
    val screen = getScreen(name)
    if (screen == null) {
      Log.d(TAG, "Screen $name not found!")
      return
    }
    val reactCarRenderContext = ReactCarRenderContext(screen.marker!!, callback)
    val template = parseTemplate(renderMap, reactCarRenderContext)
    reactCarRenderContextMap.remove(screen)
    reactCarRenderContextMap[screen] = reactCarRenderContext
    screen.setTemplate(template, renderMap)
  }

  @ReactMethod
  fun pushScreen(name: String?, renderMap: ReadableMap, callback: Callback?) {
    handler.post {
      val reactCarRenderContext = ReactCarRenderContext(name!!, callback)
      val screen = CarScreen(carContext)
      screenManager = screen.screenManager
      reactCarRenderContextMap.remove(screen)
      reactCarRenderContextMap[screen] = reactCarRenderContext
      screen.marker = name
      val template = parseTemplate(renderMap, reactCarRenderContext)
      screen.setTemplate(template, renderMap)
      carScreens[name] = screen
      currentCarScreen = screen
      screenManager!!.push(screen)
    }
  }

  @ReactMethod
  fun popScreen() {
    handler.post {
      screenManager!!.pop()
      removeScreen(currentCarScreen)
      currentCarScreen = screenManager!!.top as CarScreen
    }
  }

  @ReactMethod
  fun mapNavigate(address: String) {
    carContext.startCarApp(
      Intent(
        CarContext.ACTION_NAVIGATE,
        Uri.parse("geo:0,0?q=$address")
      )
    )
  }

  @ReactMethod
  fun toast(text: String, duration: Int) {
    CarToast.makeText(carContext, text, duration).show()
  }

  @ReactMethod
  fun reload() {
    val devSettingsModule = reactContext.getNativeModule(
      DevSettingsModule::class.java
    )
    devSettingsModule?.reload()
  }

  @ReactMethod
  fun finishCarApp() {
    carContext.finishCarApp()
  }

  @ReactMethod
  fun setEventCallback(name: String, callback: Callback?) {
    val screen = getScreen(name) ?: return
    val reactCarRenderContext = reactCarRenderContextMap[screen] ?: return
    reactCarRenderContext.eventCallback = callback
  }

  fun setCarContext(carContext: CarContext, currentCarScreen: CarScreen) {
    this.carContext = carContext
    this.currentCarScreen = currentCarScreen
    screenManager = currentCarScreen.screenManager
    carScreens["root"] = this.currentCarScreen
    val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        Log.d(TAG, "Back button pressed")
        sendEvent("android_auto:back_button", WritableNativeMap())
      }
    }
    carContext.onBackPressedDispatcher.addCallback(callback)
    sendEvent("android_auto:ready", WritableNativeMap())
  }

  private fun parseTemplate(
    renderMap: ReadableMap,
    reactCarRenderContext: ReactCarRenderContext
  ): Template {
    val factory = TemplateFactory(carContext, reactCarRenderContext)
    return factory.getTemplate(renderMap)
  }

  private fun getScreen(name: String): CarScreen? {
    return carScreens[name]
  }

  private fun removeScreen(screen: CarScreen?) {
    val params = WritableNativeMap()
    params.putString("screen", screen!!.marker)
    sendEvent("android_auto:remove_screen", params)
    carScreens.values.remove(screen)
  }

  private fun sendEvent(eventName: String, params: Any) {
    reactContext
      .getJSModule(RCTDeviceEventEmitter::class.java)
      .emit(eventName, params)
  }

  companion object {
    const val MODULE_NAME = "CarModule"
    const val TAG = "AndroidAutoModule"
  }

}
