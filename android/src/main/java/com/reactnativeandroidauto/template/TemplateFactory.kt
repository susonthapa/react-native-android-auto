package com.reactnativeandroidauto.template

import androidx.car.app.CarContext
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Template
import com.facebook.react.bridge.ReadableMap
import com.reactnativeandroidauto.ReactCarRenderContext

/**
 * Factory to create [Template]
 * This class exposes a function [getTemplate] that can be used to create various template
 * based on the passed arguments
 * @constructor takes two arguments, the [CarContext] and the [ReactCarRenderContext]
 */
class TemplateFactory(
  private val context: CarContext,
  private val renderContext: ReactCarRenderContext,
) {


  /**
   * creates a new [Template] based on the type parameter of the provided props
   *
   * @param props the props to use for the construction of the template
   * @return the created template or a [PaneTemplate] with loading set to true if no valid templates found
   */
  fun getTemplate(props: ReadableMap): Template {
    val template = when (props.getString("type")) {
      "list-template" -> RNListTemplate(context, renderContext)
      "grid-template" -> RNGridTemplate(context, renderContext)
      "place-list-map-template" -> RNPlaceListMapTemplate(context, renderContext)
      "pane-template" -> RNPaneTemplate(context, renderContext)
      "navigation-template" -> RNNavigationTemplate(context, renderContext)
      else -> null
    }

    return template?.parse(props) ?: PaneTemplate
      .Builder(
        Pane.Builder().setLoading(true).build()
      ).setTitle("Pane Template").build()
  }

}
