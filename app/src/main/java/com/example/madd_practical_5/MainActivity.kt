package com.example.madd_practical_5

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var imageView: ImageView

    private var posX = 0f
    private var posY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageView = findViewById(R.id.imageView)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { accel ->
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val ax = event.values[0]
            val ay = event.values[1]

            // Sensitivity multiplier
            val multiplier = 5f

            // Tilted left -> ax > 0 -> move left (-x)
            // Tilted right -> ax < 0 -> move right (+x)
            posX -= ax * multiplier

            // Tilted back (towards user) -> ay > 0 -> move down (+y)
            // Tilted forward (away from user) -> ay < 0 -> move up (-y)
            posY += ay * multiplier

            val parent = imageView.parent as? android.view.View
            if (parent != null && parent.width > 0 && parent.height > 0) {
                val maxTranslateX = (parent.width - imageView.width) / 2f
                val maxTranslateY = (parent.height - imageView.height) / 2f

                if (posX > maxTranslateX) posX = maxTranslateX
                if (posX < -maxTranslateX) posX = -maxTranslateX

                if (posY > maxTranslateY) posY = maxTranslateY
                if (posY < -maxTranslateY) posY = -maxTranslateY
            }

            imageView.translationX = posX
            imageView.translationY = posY
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}