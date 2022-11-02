package com.reactnativeandroidauto.template

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.model.*
import com.facebook.react.bridge.ReadableMap
import com.reactnativeandroidauto.ReactCarRenderContext

/**
 * Creates a [ListTemplate] from the given props
 *
 * @constructor
 * @see RNTemplate
 *
 * @param context
 * @param renderContext
 */
class RNListTemplate(
  context: CarContext,
  renderContext: ReactCarRenderContext,
) : RNTemplate(context, renderContext) {

  override fun parse(props: ReadableMap): ListTemplate {
    val children = props.getArray("children")
    val childrenCount = children?.size() ?: 0
    val loading: Boolean = try {
      props.getBoolean("isLoading")
    } catch (e: Exception) {
      childrenCount == 0
    }
    val builder = ListTemplate.Builder()
    builder.setLoading(loading)
    if (!loading) {
      for (i in 0 until childrenCount) {
        val child = children!!.getMap(i)
        val type = child.getString("type")
        if (type == "item-list") {
          builder.addSectionedList(
            SectionedItemList.create(
              parseItemListChildren(child.getMap("children")!!),
              child.getString("header")!!,
            )
          )
        } else {
          Log.w(TAG, "parse: please pass children of type item-list, got $type")
        }
      }
    }
    try {
      builder.setHeaderAction(getHeaderAction(props.getString("headerAction"))!!)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    try {
      val actionStripMap = props.getMap("actionStrip")
      builder.setActionStrip(parseActionStrip(actionStripMap!!))
    } catch (e: Exception) {
      e.printStackTrace()
    }
    builder.setTitle(props.getString("title")!!)
    return builder.build()
  }

  companion object {
    const val TAG = "RNListTemplate"
  }

}
