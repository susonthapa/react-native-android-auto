package com.reactnativeandroidauto.template

import android.graphics.Bitmap
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.model.*
import androidx.core.graphics.drawable.IconCompat
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSources
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.image.CloseableBitmap
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableNativeMap
import com.facebook.react.views.imagehelper.ImageSource
import com.reactnativeandroidauto.ReactCarRenderContext

/**
 * Base class for parsing the template based on the props passed from ReactNative
 *
 * @property context
 * @property renderContext
 */
abstract class RNTemplate(
  protected val context: CarContext,
  protected val renderContext: ReactCarRenderContext,
) {

  /**
   * Function that should be implemented by the children of this class
   *
   * @param props the props that was passed from the ReactNative
   * @return the template
   */
  abstract fun parse(props: ReadableMap): Template

  protected fun invokeCallback(callbackId: Int, parameters: WritableNativeMap? = null) {
    var params = parameters
    if (params == null) {
      params = WritableNativeMap()
    }
    params.putInt("id", callbackId)
    params.putString("screen", renderContext.screenMarker)
    renderContext.eventCallback!!.invoke(params)
  }

  protected fun parseCarIcon(map: ReadableMap): CarIcon {
    val source = ImageSource(context, map.getString("uri"))
    val imageRequest = ImageRequestBuilder.newBuilderWithSource(source.uri).build()
    val dataSource = Fresco.getImagePipeline().fetchDecodedImage(imageRequest, context)
    val result = DataSources.waitForFinalResult(dataSource) as CloseableReference<CloseableBitmap>
    val bitmap = result.get().underlyingBitmap

    CloseableReference.closeSafely(result)
    dataSource.close()

    return CarIcon.Builder(IconCompat.createWithBitmap(bitmap)).build()
  }

  protected fun getColor(colorName: String?): CarColor {
    return when (colorName) {
      "blue" -> CarColor.BLUE
      "green" -> CarColor.GREEN
      "primary" -> CarColor.PRIMARY
      "red" -> CarColor.RED
      "secondary" -> CarColor.SECONDARY
      "yellow" -> CarColor.YELLOW
      "default" -> CarColor.DEFAULT
      else -> CarColor.DEFAULT
    }
  }

  private fun getBitmapFromSource(map: ReadableMap): Bitmap {
    val source = ImageSource(context, map.getString("uri"))
    val imageRequest = ImageRequestBuilder.newBuilderWithSource(source.uri).build()
    val dataSource = Fresco.getImagePipeline().fetchDecodedImage(imageRequest, context)
    val result = DataSources.waitForFinalResult(dataSource) as CloseableReference<CloseableBitmap>
    val bitmap = result.get().underlyingBitmap

    CloseableReference.closeSafely(result)
    dataSource.close()

    return bitmap
  }

  protected fun parseAction(map: ReadableMap?): Action {
    val builder = Action.Builder()
    if (map != null) {
      map.getString("title")?.let {
        builder.setTitle(it)
      }
      map.getMap("icon")?.let {
        val bitmap = getBitmapFromSource(it)
        val icon = IconCompat.createWithBitmap(bitmap)
        builder.setIcon(CarIcon.Builder(icon).build())
      }
      try {
        builder.setBackgroundColor(getColor(map.getString("backgroundColor")))
      } catch (e: Exception) {
        e.printStackTrace()
      }
      try {
        val onPress = map.getInt("onPress")
        builder.setOnClickListener { invokeCallback(onPress) }
      } catch (e: Exception) {
        Log.d("AUTO", "Couldn't parseAction", e)
        e.printStackTrace()
      }
    }
    return builder.build()
  }

  protected fun parseActionStrip(map: ReadableMap?): ActionStrip? {
    val builder = ActionStrip.Builder()
    return if (map != null) {
      val actions = map.getArray("actions")
      for (i in 0 until actions!!.size()) {
        val actionMap = actions.getMap(i)
        val action = parseAction(actionMap)
        builder.addAction(action)
      }
      builder.build()
    } else {
      null
    }
  }

  protected fun getHeaderAction(actionName: String?): Action? {
    return if (actionName == null) {
      null
    } else {
      when (actionName) {
        "back" -> Action.BACK
        "app_icon" -> Action.APP_ICON
        else -> null
      }
    }
  }

  protected fun buildRow(rowRenderMap: ReadableMap): Row {
    val builder = Row.Builder()
    builder.setTitle(rowRenderMap.getString("title")!!)
    try {
      val texts = rowRenderMap.getArray("texts")
      for (i in 0 until texts!!.size()) {
        builder.addText(texts.getString(i))
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    try {
      val onPress = rowRenderMap.getInt("onPress")
      builder.setBrowsable(true)
      builder.setOnClickListener { invokeCallback(onPress) }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    try {
//            builder.setMetadata(parseMetaData(rowRenderMap.getMap("metadata")));
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return builder.build()
  }

}
