package de.p72b.maps.animation.util

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil

object CalculationHelper {

    fun polylineUntilSection(
        path: List<LatLng>,
        legs: List<Double>,
        pathSection: Double,
        polylineOptions: PolylineOptions
    ): PolylineOptions {
        var legSum = 0.0
        var pastSections = 0.0

        for ((index, value) in path.withIndex()) {
            polylineOptions.add(value)

            if (index < path.size - 1) {
                val to = path[index + 1]
                val currentLeg = legs[index]
                legSum += currentLeg
                if (pathSection < legSum) {
                    val fraction = (pathSection - pastSections) / currentLeg
                    val pointToBeAdded = SphericalUtil.interpolate(value, to, fraction)
                    polylineOptions.add(pointToBeAdded)
                    return polylineOptions
                } else {
                    pastSections += currentLeg
                }
            }
        }
        polylineOptions.add(path.last())
        return polylineOptions
    }

    fun calculateLegsLengths(path: List<LatLng>): List<Double> {
        val legs = mutableListOf<Double>()
        for ((index, value) in path.withIndex()) {
            if (index < path.size - 1) {
                val to = path[index + 1]
                val legLength = SphericalUtil.computeDistanceBetween(value, to)
                legs.add(legLength)
            }
        }
        return legs
    }
}