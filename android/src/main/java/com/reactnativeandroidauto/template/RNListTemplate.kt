package com.reactnativeandroidauto.template

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
    val builder = ListTemplate.Builder()
    val loading: Boolean = try {
      props.getBoolean("isLoading")
    } catch (e: Exception) {
      children!!.size() == 0
    }
    builder.setLoading(loading)
    if (!loading) {
      for (i in 0 until children!!.size()) {
        val child = children.getMap(i)
        val type = child.getString("type")
        if (type == "item-list") {
          builder.addSectionedList(
            SectionedItemList.create(
              parseItemListChildren(child),
              child.getString("header")!!
            )
          )
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
      builder.setActionStrip(parseActionStrip(actionStripMap)!!)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    builder.setTitle(props.getString("title")!!)
    return builder.build()
  }

  private fun parseItemListChildren(itemList: ReadableMap): ItemList {
    val children = itemList.getArray("children")
    val builder = ItemList.Builder()
    for (i in 0 until children!!.size()) {
      val child = children.getMap(i)
      val type = child.getString("type")
      if (type == "row") {
        builder.addItem(buildRow(child))
      }
    }
    try {
      builder.setNoItemsMessage(itemList.getString("noItemsMessage")!!)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return builder.build()
  }

}
