package com.example.sensor.ui.discover

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sensor.databinding.FragmentDiscoverBinding
import com.example.sensor.persistence.ins.UserInsDatabase
import com.example.sensor.ui.activity.LoginActivity
import com.example.sensor.ui.activity.PostActivity
import com.example.sensor.ui.dialog.InsDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.math.abs
import kotlin.random.Random


class DiscoverFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentDiscoverBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var sensorManager: SensorManager? = null

    private var dialog: InsDialog? = null

    private var shakeIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val discoverViewModel =
            ViewModelProvider(this)[DiscoverViewModel::class.java]
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.post.setOnClickListener {
            startActivity(Intent(requireContext(), PostActivity::class.java))
        }

        binding.shake.setOnClickListener {
            shake()
        }

        binding.logout.setOnClickListener {
            val sp = requireContext().getSharedPreferences("remember_user", Context.MODE_PRIVATE)
            sp.edit().clear().apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        sensor()
    }

    private fun sensor() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun shake() {
        if(dialog != null && dialog?.isShowing == true) {
            return
        }
        runBlocking(Dispatchers.IO) {
            val insData = UserInsDatabase.getInstance(requireContext()).queryIns()
            requireActivity().runOnUiThread {
                if(insData.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                } else {
                    if(shakeIndex + 1 >= insData.size) {
                        shakeIndex = 0
                    }
                    val userIns = insData[shakeIndex]
                    shakeIndex++
                    dialog = InsDialog(requireContext(), userIns)
                    dialog?.show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Processing sensor information
        // Execute this method when the sensor information changes
        val values = event?.values ?: return
        val x = values[0] // Acceleration of gravity in the x-axis direction

        val y = values[1] // Acceleration of gravity in the y-axis direction

        val z = values[2] // Acceleration of gravity in the z-axis direction

        Log.i("onSensorChanged", "x轴方向的重力加速度$x；y轴方向的重力加速度$y；z轴方向的重力加速度$z")
        val value = 10 // Small value, high sens

        if (abs(x) > value || abs(y) > value || abs(z) > value) {
            shake()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //当传感器精度发生变化时，此方法会被调用。
    }
}