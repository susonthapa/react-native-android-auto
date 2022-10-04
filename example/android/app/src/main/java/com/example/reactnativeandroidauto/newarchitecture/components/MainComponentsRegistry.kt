package com.example.reactnativeandroidauto.newarchitecture.components

import com.facebook.soloader.SoLoader
import com.facebook.jni.HybridData
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.fabric.ComponentFactory

/**
 * Class responsible to load the custom Fabric Components. This class has native methods and needs a
 * corresponding C++ implementation/header file to work correctly (already placed inside the jni/
 * folder for you).
 *
 *
 * Please note that this class is used ONLY if you opt-in for the New Architecture (see the
 * `newArchEnabled` property). Is ignored otherwise.
 */
@DoNotStrip
class MainComponentsRegistry @DoNotStrip private constructor(componentFactory: ComponentFactory) {
  companion object {
    @kotlin.jvm.JvmStatic
    @DoNotStrip
    fun register(componentFactory: ComponentFactory): MainComponentsRegistry {
      return MainComponentsRegistry(componentFactory)
    }

    init {
      SoLoader.loadLibrary("fabricjni")
    }
  }

  @DoNotStrip
  private val hybridData: HybridData

  @DoNotStrip
  private external fun initHybrid(componentFactory: ComponentFactory): HybridData

  init {
    hybridData = initHybrid(componentFactory)
  }
}
