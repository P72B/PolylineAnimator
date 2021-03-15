package de.p72b.animation.polyline

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import de.p72b.maps.animation.AnimatedPolyline


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var animatedPolyline: AnimatedPolyline

    private val wayPoints = mutableListOf(
        LatLng(52.516331, 13.377721),
        LatLng(52.517755, 13.376884),
        LatLng(52.517807, 13.370865),
        LatLng(52.520901, 13.370790),
        LatLng(52.522351, 13.367990),
        LatLng(52.522579, 13.368269),
        LatLng(52.523284, 13.368247),
        LatLng(52.523297, 13.367207),
        LatLng(52.524080, 13.367196),
        LatLng(52.524067, 13.368226),
        LatLng(52.524198, 13.368333),
        LatLng(52.524211, 13.369942)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        findViewById<Button>(R.id.clear).setOnClickListener {
            clearAnimation()
        }
        findViewById<Button>(R.id.start).setOnClickListener {
            animatedPolyline.start()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        addOriginAndDestination()
        zoomToOrigin()

        animatedPolyline = AnimatedPolyline(
            mMap,
            wayPoints,
            polylineOptions = getPolylineOptions(),
            duration = 1250,
            interpolator = DecelerateInterpolator(),
            animatorListenerAdapter = getListener()
        )
        animatedPolyline.startWithDelay(1000)
    }

    private fun clearAnimation() {
        animatedPolyline.remove()
    }

    private fun getListener(): AnimatorListenerAdapter {
        return object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                animatedPolyline.start()
            }
        }
    }

    private fun getPolylineOptions(): PolylineOptions {
        return PolylineOptions()
            .color(ContextCompat.getColor(this, R.color.colorPrimary))
            .pattern(
                listOf(
                    Dot(), Gap(20F)
                )
            )
    }

    private fun addOriginAndDestination() {
        mMap.addMarker(MarkerOptions().position(wayPoints.first()))
        mMap.addMarker(MarkerOptions().position(wayPoints.last()))
    }

    private fun zoomToOrigin() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(wayPoints.first(), 15f))
    }
}