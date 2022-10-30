package com.reactnativeandroidauto.template

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.model.DateTimeWithZone
import androidx.car.app.navigation.model.*
import com.facebook.react.bridge.ReadableMap
import com.reactnativeandroidauto.ReactCarRenderContext
import java.util.*

/**
 * Creates [NavigationTemplate] from the given props
 *
 * @constructor
 * @see RNTemplate
 *
 * @param context
 * @param renderContext
 */
class RNNavigationTemplate(
  context: CarContext,
  renderContext: ReactCarRenderContext,
) : RNTemplate(context, renderContext) {

  private fun parseStep(map: ReadableMap): Step {
    return Step.Builder().apply {
      map.getMap("lane")?.let { addLane(parseLane(it)) }
      map.getString("cue")?.let { setCue(it) }
      map.getMap("lanesImage")?.let { setLanesImage(parseCarIcon(it)) }
      map.getMap("maneuver")?.let { setManeuver(parseManeuver(it)) }
      map.getString("road")?.let { setRoad(it) }
    }.build()
  }

  private fun parseLane(map: ReadableMap): Lane {
    val laneBuilder = Lane.Builder()
    val shape = map.getInt("shape")
    val isRecommended = map.getBoolean("isRecommended")
    return laneBuilder.addDirection(LaneDirection.create(shape, isRecommended)).build()
  }

  private fun parseManeuver(map: ReadableMap): Maneuver {
    val type = map.getInt("type")
    val builder = Maneuver.Builder(type)
    builder.setIcon(parseCarIcon(map.getMap("icon")!!))
    if (type == Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CW_WITH_ANGLE
      || type == Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CCW_WITH_ANGLE
    ) {
      builder.setRoundaboutExitAngle(map.getInt("roundaboutExitAngle"))
    }

    if (type == Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CW
      || type == Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CCW
      || type == Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CW_WITH_ANGLE
      || type == Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CCW_WITH_ANGLE
    ) {
      builder.setRoundaboutExitNumber(map.getInt("roundaboutExitNumber"))
    }

    return builder.build()
  }

  private fun parseMessageInfo(map: ReadableMap): MessageInfo {
    val builder = MessageInfo.Builder(map.getString("title")!!)
    map.getMap("icon")?.let { builder.setImage(parseCarIcon(it)) }
    return builder.build()
  }

  private fun parseTravelEstimate(map: ReadableMap): TravelEstimate {
    val dateTimeMap = map.getMap("destinationTime")!!
    val destinationDateTime = DateTimeWithZone.create(
      dateTimeMap.getDouble("timeSinceEpochMillis").toLong(),
      TimeZone.getTimeZone(dateTimeMap.getString("id")),
    )
    val builder = TravelEstimate.Builder(
      parseDistance(map.getMap("remainingDistance")!!),
      destinationDateTime,
    )
    builder.setRemainingTimeSeconds(map.getDouble("remainingTimeSeconds").toLong())
    return builder.build()
  }

  private fun parseRoutingInfo(map: ReadableMap): RoutingInfo {
    return RoutingInfo.Builder()
      .apply {
        setLoading(map.getBoolean("isLoading"))
        setCurrentStep(parseStep(map.getMap("step")!!), parseDistance(map.getMap("distance")!!))
        map.getMap("junctionImage")?.let { setJunctionImage(parseCarIcon(it)) }
        map.getMap("nextStep")?.let { setNextStep(parseStep(it)) }
      }.build()
  }

  private fun parseNavigationInfo(map: ReadableMap): NavigationTemplate.NavigationInfo {
    val type = map.getString("type")
    return if (type == "routingInfo") {
      parseRoutingInfo(map.getMap("info")!!)
    } else {
      parseMessageInfo(map.getMap("info")!!)
    }
  }

  override fun parse(props: ReadableMap): NavigationTemplate {
    val builder = NavigationTemplate.Builder()
    try {
      val actionStrip = parseActionStrip(props.getMap("actionStrip")!!)
      builder.setActionStrip(actionStrip)
      props.getMap("mapActionStrip")?.let {
        builder.setMapActionStrip(parseActionStrip(it))
      }
      props.getMap("navigationInfo")?.let {
        builder.setNavigationInfo(parseNavigationInfo(it))
      }
      props.getMap("destinationTravelEstimate")?.let {
        builder.setDestinationTravelEstimate(parseTravelEstimate(it))
      }
    } catch (e: Exception) {
      Log.w(TAG, "parse: failed to parse the navigation props, ${e.message}")
    }
    return builder.build()
  }

  companion object {
    const val TAG = "RNNavigationTemplate"
  }

}
