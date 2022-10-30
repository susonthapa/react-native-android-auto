package com.reactnativeandroidauto.template

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import com.facebook.react.bridge.ReadableMap
import com.reactnativeandroidauto.ReactCarRenderContext

/**
 * Creates [PaneTemplate] from the given props
 *
 * @constructor
 * @see RNTemplate
 *
 * @param context
 * @param renderContext
 */
class RNPaneTemplate(
  context: CarContext,
  renderContext: ReactCarRenderContext,
) : RNTemplate(context, renderContext) {

  override fun parse(props: ReadableMap): PaneTemplate {
    val paneBuilder = Pane.Builder()
    val children = props.getArray("children")
    val loading: Boolean = try {
      props.getBoolean("isLoading")
    } catch (e: Exception) {
      children == null || children.size() == 0
    }

    paneBuilder.setLoading(loading)
    val actions = mutableListOf<Action>()
    if (!loading) {
      for (i in 0 until children!!.size()) {
        val child = children.getMap(i)
        val type = child.getString("type")
        Log.d(TAG, "Adding child to row")
        if (type == "row") {
          paneBuilder.addRow(buildRow(child))
        }
        if (type == "action") {
          actions.add(parseAction(child))
        }
      }
      for (i in actions.indices) {
        paneBuilder.addAction(actions[i])
      }
    }
    val builder = PaneTemplate.Builder(paneBuilder.build())
    val title = props.getString("title")
    if (title == null || title.isEmpty()) {
      builder.setTitle("<No Title>")
    } else {
      builder.setTitle(title)
    }
    try {
      builder.setHeaderAction(getHeaderAction(props.getString("headerAction"))!!)
    } catch (e: Exception) {
      e.printStackTrace()
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
    const val TAG = "RNPaneTemplate"
  }
}
