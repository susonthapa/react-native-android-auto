package com.reactnativeandroidauto

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Pane
import androidx.car.app.model.Template

class CarScreen(carContext: CarContext) : Screen(carContext) {
  private var template: Template? = null
  fun setTemplate(template: Template?) {
    this.template = template
  }

  override fun onGetTemplate(): Template {
    return template ?: PaneTemplate.Builder(
      Pane.Builder().setLoading(true).build()
    ).setTitle("My App").build()
  }
}
