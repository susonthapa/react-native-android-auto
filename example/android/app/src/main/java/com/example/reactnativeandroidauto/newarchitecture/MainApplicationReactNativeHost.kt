package com.example.reactnativeandroidauto.newarchitecture

import android.app.Application
import com.example.reactnativeandroidauto.BuildConfig
import com.example.reactnativeandroidauto.newarchitecture.components.MainComponentsRegistry.Companion.register
import com.example.reactnativeandroidauto.newarchitecture.modules.MainApplicationTurboModuleManagerDelegate
import com.facebook.react.PackageList
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.ReactPackageTurboModuleManagerDelegate
import com.facebook.react.bridge.*
import com.facebook.react.fabric.ComponentFactory
import com.facebook.react.fabric.CoreComponentsRegistry
import com.facebook.react.fabric.EmptyReactNativeConfig
import com.facebook.react.fabric.FabricJSIModuleProvider
import com.facebook.react.uimanager.ViewManagerRegistry

/**
 * A [ReactNativeHost] that helps you load everything needed for the New Architecture, both
 * TurboModule delegates and the Fabric Renderer.
 *
 *
 * Please note that this class is used ONLY if you opt-in for the New Architecture (see the
 * `newArchEnabled` property). Is ignored otherwise.
 */
class MainApplicationReactNativeHost(application: Application?) : ReactNativeHost(application) {
  override fun getUseDeveloperSupport(): Boolean {
    return BuildConfig.DEBUG
  }

  override fun getPackages(): List<ReactPackage> {
    // Packages that cannot be autolinked yet can be added manually here, for example:
    //     packages.add(new MyReactNativePackage());
    // TurboModules must also be loaded here providing a valid TurboReactPackage implementation:
    //     packages.add(new TurboReactPackage() { ... });
    // If you have custom Fabric Components, their ViewManagers should also be loaded here
    // inside a ReactPackage.
    return PackageList(this).packages
  }

  override fun getJSMainModuleName(): String {
    return "index"
  }

  override fun getReactPackageTurboModuleManagerDelegateBuilder(): ReactPackageTurboModuleManagerDelegate.Builder {
    // Here we provide the ReactPackageTurboModuleManagerDelegate Builder. This is necessary
    // for the new architecture and to use TurboModules correctly.
    return MainApplicationTurboModuleManagerDelegate.Builder()
  }

  override fun getJSIModulePackage(): JSIModulePackage {
    return JSIModulePackage { reactApplicationContext, _ ->
      val specs: MutableList<JSIModuleSpec<*>> = ArrayList()

      // Here we provide a new JSIModuleSpec that will be responsible of providing the
      // custom Fabric Components.
      specs.add(
        object : JSIModuleSpec<JSIModule> {
          override fun getJSIModuleType(): JSIModuleType {
            return JSIModuleType.UIManager
          }

          override fun getJSIModuleProvider(): JSIModuleProvider<JSIModule> {
            val componentFactory = ComponentFactory()
            CoreComponentsRegistry.register(componentFactory)

            // Here we register a Components Registry.
            // The one that is generated with the template contains no components
            // and just provides you the one from React Native core.
            register(componentFactory)
            val reactInstanceManager = reactInstanceManager
            val viewManagerRegistry = ViewManagerRegistry(
              reactInstanceManager.getOrCreateViewManagers(reactApplicationContext)
            )
            return FabricJSIModuleProvider(
              reactApplicationContext,
              componentFactory,
              EmptyReactNativeConfig(),
              viewManagerRegistry
            ) as JSIModuleProvider<JSIModule>
          }
        })
      specs
    }
  }
}
