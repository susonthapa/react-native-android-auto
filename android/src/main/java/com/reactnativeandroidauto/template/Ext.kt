package com.reactnativeandroidauto.template

import com.facebook.react.bridge.ReadableMap

fun ReadableMap.isLoading(): Boolean {
  return try {
    getBoolean("isLoading")
  } catch (e: Exception) {
    // check for children props
    (getArray("children")?.size() ?: 0) == 0
  }
}
