package com.reactnativeandroidauto.template

import android.graphics.Bitmap
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.model.*
import androidx.car.app.model.PlaceMarker.TYPE_IMAGE
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

  protected fun parseHeaderAction(type: String): Action {
    return if (type == "back") Action.BACK else Action.APP_ICON
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
        Log.w(TAG, "Couldn't parseAction, ${e.message}")
      }
    }
    return builder.build()
  }

  protected fun parseActionStrip(map: ReadableMap): ActionStrip {
    val builder = ActionStrip.Builder()
    val actions = map.getArray("actions")
    for (i in 0 until actions!!.size()) {
      val actionMap = actions.getMap(i)
      val action = parseAction(actionMap)
      builder.addAction(action)
    }
    return builder.build()
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

  private fun parsePlace(props: ReadableMap): Place {
    val builder = Place.Builder(
      CarLocation.create(
        props.getDouble("latitude"),
        props.getDouble("longitude"),
      )
    )
    PlaceMarker.Builder().apply {
      setIcon(parseCarIcon(props.getMap("icon")!!), PlaceMarker.TYPE_IMAGE)
      builder.setMarker(this.build())
    }

    return builder.build()
  }

  private fun parseMetaData(props: ReadableMap?): Metadata? {
    val type = props?.getString("type")
    if (props == null || type == null || type != "place") {
      Log.w(TAG, "parseMetaData: invalid type provided $type")
      return null
    }
    val builder = Metadata.Builder()
    builder.setPlace(parsePlace(props))

    return builder.build()
  }

  private fun getCarText(title: String, props: ReadableMap?): CarText {
    val spanBuilder = SpannableString(title)
    props?.let {
      try {
        val index = title.indexOf("%d")
        if (index != -1) {
          spanBuilder.setSpan(
            DistanceSpan.create(parseDistance(props)),
            index,
            index + 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
          )
        }
        it
      } catch (e: Exception) {
        Log.w(TAG, "getCarText: failed to parse the CarText")
      }
    }
    return CarText.Builder(spanBuilder).build()
  }

  protected fun buildRow(props: ReadableMap): Row {
    val builder = Row.Builder()
    builder.setTitle(
      getCarText(
        props.getString("title")!!,
        props.getMap("metadata")?.getMap("distance")
      )
    )
    props.getArray("texts")?.let {
      for (i in 0 until it.size()) {
        builder.addText(it.getString(i))
      }
    }
    props.getMap("image")?.let {
      builder.setImage(parseCarIcon(it))
    }
    try {
      val onPress = props.getInt("onPress")
      builder.setBrowsable(true)
      builder.setOnClickListener { invokeCallback(onPress) }
    } catch (e: Exception) {
      Log.w(TAG, "buildRow: failed to set clickListener on the row")
    }
    parseMetaData(props.getMap("metadata"))?.let {
      builder.setMetadata(it)
    }
    return builder.build()
  }

  protected fun parseDistance(map: ReadableMap): Distance {
    return Distance.create(map.getDouble("displayDistance"), map.getInt("displayUnit"))
  }

  protected fun parseItemListChildren(itemList: ReadableMap): ItemList {
    val children = itemList.getArray("children")
    val builder = ItemList.Builder()
    for (i in 0 until children!!.size()) {
      val child = children.getMap(i)
      val type = child.getString("type")
      if (type == "row") {
        builder.addItem(buildRow(child))
      } else {
        Log.w(
          RNListTemplate.TAG,
          "parseItemListChildren: children of item-list should be of type row, got $type"
        )
      }
    }
    return builder.build()
  }

  companion object {
    const val TAG = "RNTemplate"
  }

}
