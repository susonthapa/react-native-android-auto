package com.reactnativeandroidauto.template

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.model.*
import com.facebook.react.bridge.ReadableMap
import com.reactnativeandroidauto.ReactCarRenderContext

/**
 * Creates [PlaceListMapTemplate] from the given props
 *
 * @constructor
 *
 *
 * @param context
 * @param renderContext
 */
class RNPlaceListMapTemplate(
  context: CarContext,
  renderContext: ReactCarRenderContext,
) : RNTemplate(context, renderContext) {

  override fun parse(props: ReadableMap): PlaceListMapTemplate {
    val builder = PlaceListMapTemplate.Builder()
    builder.setTitle(props.getString("title")!!)
    val children = props.getArray("children")
    try {
      builder.setHeaderAction(getHeaderAction(props.getString("headerAction"))!!)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    val loading: Boolean = try {
      props.getBoolean("isLoading")
    } catch (e: Exception) {
      children == null || children.size() == 0
    }
    Log.d(TAG, "Rendering " + if (loading) "Yes" else "No")
    builder.setLoading(loading)
    if (!loading) {
      val itemListBuilder = ItemList.Builder()
      for (i in 0 until children!!.size()) {
        val child = children.getMap(i)
        val type = child.getString("type")
        Log.d(TAG, "Adding $type to row")
        if (type == "row") {
          itemListBuilder.addItem(buildRow(child))
        }
      }
      builder.setItemList(itemListBuilder.build())
    }
    try {
      val actionStripMap = props.getMap("actionStrip")!!
      builder.setActionStrip(parseActionStrip(actionStripMap))
    } catch (e: Exception) {
      Log.w(TAG, "parse: failed to set the actionStrip: ${e.message}")
    }
    return builder.build()
  }

  companion object {
    const val TAG = "RNPlaceListMapTemplate"
  }

}
