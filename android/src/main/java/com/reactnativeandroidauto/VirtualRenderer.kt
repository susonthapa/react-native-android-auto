package com.reactnativeandroidauto

import android.app.Presentation
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.Log
import android.view.Display
import androidx.car.app.AppManager
import androidx.car.app.CarContext
import androidx.car.app.SurfaceCallback
import androidx.car.app.SurfaceContainer
import com.facebook.react.ReactApplication
import com.facebook.react.ReactRootView

/**
 * Renders the view tree into a surface using VirtualDisplay. It runs the ReactNative component registered
 */
class VirtualRenderer(private val context: CarContext, private val moduleName: String) {

  init {
    context.getCarService(AppManager::class.java).setSurfaceCallback(object : SurfaceCallback {
      override fun onSurfaceAvailable(surfaceContainer: SurfaceContainer) {
        val surface = surfaceContainer.surface
        if (surface == null) {
          Log.w(TAG, "surface is null")
        } else {
          renderPresentation(surfaceContainer)
        }
      }
    })
  }

  private fun renderPresentation(container: SurfaceContainer) {
    val manager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    val display = manager.createVirtualDisplay(
      "AndroidAutoMapTemplate",
      container.width,
      container.height,
      container.dpi,
      container.surface,
      DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION,
    )
    val presentation = MapPresentation(context, display.display, moduleName)
    presentation.show()
  }

  inner class MapPresentation(context: Context, display: Display, private val moduleName: String) :
    Presentation(context, display) {
    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      val instanceManager =
        (context.applicationContext as ReactApplication).reactNativeHost.reactInstanceManager
      val rootView = ReactRootView(context).apply {
        startReactApplication(instanceManager, moduleName)
        runApplication()
      }
      setContentView(rootView)
    }
  }

  companion object {
    const val TAG = "VirtualRenderer"
  }
}
