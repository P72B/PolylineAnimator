package de.p72b.maps.animation

import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import de.p72b.maps.animation.util.PolylineOptionsExtensions.copyPolylineOptions
import de.p72b.maps.animation.util.PolylineOptionsExtensions.toPolylineOptions
import de.p72b.maps.animation.util.CalculationHelper


class AnimatedPolyline(
    private var map: GoogleMap,
    private var points: List<LatLng>,
    private var polylineOptions: PolylineOptions = PolylineOptions(),
    duration: Long = 3000,
    interpolator: TimeInterpolator? = null,
    animatorListenerAdapter: AnimatorListenerAdapter? = null
) {
    private var renderedPolyline: Polyline? = null
    private val legs: List<Double> = CalculationHelper.calculateLegsLengths(points)
    private var totalPathDistance: Double
    private var animator: ValueAnimator

    init {
        totalPathDistance = legs.sum()

        animator = ValueAnimator.ofFloat(0f, 100f)
        animator.duration = duration
                interpolator?.let {
                    animator.interpolator = it
                }
        animator.addUpdateListener { valueAnimator ->
            val fraction = valueAnimator.animatedValue as Float
            val pathSection = totalPathDistance * fraction / 100
            renderPolylineOnMap(
                CalculationHelper.polylineUntilSection(
                    points, legs, pathSection, polylineOptions.copyPolylineOptions()))
        }
        animatorListenerAdapter?.let {
            animator.addListener(it)
        }
    }

    fun replacePoints(pointList: List<LatLng>) {
        val polylineOptions = polylineOptions.toPolylineOptions(pointList)
        renderPolylineOnMap(polylineOptions)
    }

    fun start() {
        animator.start()
    }

    fun startWithDelay(milliseconds: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            start()
        }, milliseconds)
    }

    fun remove() {
        renderedPolyline?.remove()
    }

    private fun renderPolylineOnMap(polylineOptions: PolylineOptions) {
        val newPolyline = map.addPolyline(polylineOptions)
        renderedPolyline?.remove()
        renderedPolyline = newPolyline
    }
}