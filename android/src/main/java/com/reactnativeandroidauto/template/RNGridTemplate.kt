package com.reactnativeandroidauto.template

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import com.facebook.react.bridge.ReadableMap
import com.reactnativeandroidauto.ReactCarRenderContext

class RNGridTemplate(
  context: CarContext,
  renderContext: ReactCarRenderContext,
) : RNTemplate(context, renderContext) {
  override fun parse(props: ReadableMap): GridTemplate {
    return GridTemplate.Builder().apply {
      setLoading(props.isLoading())
      props.getString("title")?.let { setTitle(it) }
      props.getString("headerAction")?.let { setHeaderAction(parseHeaderAction(it)) }
      props.getMap("actionStrip")?.let { setActionStrip(parseActionStrip(it)) }
      setSingleList(parseGrid(props))
    }.build()
  }

  private fun parseGrid(props: ReadableMap): ItemList {
    return ItemList.Builder().apply {
      props.getString("noItemMessage")?.let { setNoItemsMessage(it) }
      props.getArray("children")?.let {
        for (i in 0 until it.size()) {
          val child = it.getMap(i)
          if (child.getString("type") == "grid-item") {
            addItem(parseGridItem(child))
          } else {
            Log.w(TAG, "parseGrid: please provide item with type grid-item")
          }
        }
      }
    }.build()
  }

  private fun parseGridItem(props: ReadableMap): GridItem {
    return GridItem.Builder().apply {
      props.getString("title")?.let { setTitle(it) }
      props.getString("text")?.let { setText(it) }
      props.getMap("image")?.let { setImage(parseCarIcon(it)) }
      try {
        val onPress = props.getInt("onPress")
        setOnClickListener { invokeCallback(onPress) }
      } catch (e: Exception) {
        Log.w(TAG, "parseGridItem: failed to get the click handler for the GridItem")
      }
    }.build()
  }

  companion object {
    const val TAG = "RNGridTemplate"
  }
}
