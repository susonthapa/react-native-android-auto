package com.reactnativeandroidauto.template

import androidx.car.app.CarContext
import androidx.car.app.model.DateTimeWithZone
import androidx.car.app.model.Distance
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

  private fun parseDistance(map: ReadableMap): Distance {
    return Distance.create(map.getDouble("displayDistance"), map.getInt("displayUnit"))
  }

  private fun parseStep(map: ReadableMap): Step {
    return Step.Builder().apply {
      val lane = parseLane(map.getMap("lane")!!)
      addLane(lane)
      setCue(map.getString("cue")!!)
      setLanesImage(parseCarIcon(map.getMap("lanesImage")!!))
      setManeuver(parseManeuver(map.getMap("maneuver")!!))
      setRoad(map.getString("road")!!)
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
    builder.setImage(parseCarIcon(map.getMap("icon")!!))
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
        setCurrentStep(parseStep(map.getMap("step")!!), parseDistance(map.getMap("distance")!!))
        setJunctionImage(parseCarIcon(map.getMap("junctionImage")!!))
        setLoading(map.getBoolean("isLoading"))
        setNextStep(parseStep(map.getMap("nextStep")!!))
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
      val actionStrip = parseActionStrip(props.getMap("actionStrip"))!!
      builder.setActionStrip(actionStrip)
      val mapActionStrip = props.getMap("mapActionStrip")
      mapActionStrip?.let {
        builder.setMapActionStrip(parseActionStrip(it)!!)
      }
      props.getMap("navigationInfo")?.let {
        builder.setNavigationInfo(parseNavigationInfo(it))
      }
      props.getMap("destinationTravelEstimate")?.let {
        builder.setDestinationTravelEstimate(parseTravelEstimate(it))
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return builder.build()
  }

}
