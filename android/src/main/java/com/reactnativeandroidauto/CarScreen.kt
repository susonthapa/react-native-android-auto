package com.reactnativeandroidauto

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import com.facebook.react.bridge.ReadableMap

class CarScreen(carContext: CarContext) : Screen(carContext) {

  private var template: Template? = null

  fun setTemplate(template: Template?, args: ReadableMap) {
    if (template is NavigationTemplate) {
      Log.d(TAG, "setTemplate: received navigation template with args: $args")
      val moduleName = args.getString("id")
      if (moduleName == null) {
        Log.w(
          TAG,
          "setTemplate: moduleName is null, please make sure you are setting id for map-template in ReactNative",
        )
        return
      }
      VirtualRenderer(carContext, moduleName)
    }
    this.template = template
  }

  override fun onGetTemplate(): Template {
    return template ?: PaneTemplate.Builder(
      Pane.Builder().setLoading(true).build()
    ).setTitle("My App").build()
  }

  companion object {
    const val TAG = "CarScreen"
  }
}
