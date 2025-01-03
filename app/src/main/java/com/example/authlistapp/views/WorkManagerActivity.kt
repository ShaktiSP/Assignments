package com.example.authlistapp.views

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.WorkManager
import com.example.authlistapp.databinding.ActivityWorkManagerBinding
import com.example.authlistapp.utils.FetchDataWorker
import com.example.authlistapp.utils.createNotificationChannel
import java.util.concurrent.TimeUnit
import androidx.work.PeriodicWorkRequestBuilder
import androidx.activity.result.contract.ActivityResultContracts
import com.example.authlistapp.R

class WorkManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityWorkManagerBinding.inflate(layoutInflater)

        createNotificationChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission()
            }
        }

        binding.btnStartWork.setOnClickListener {
            schedulePeriodicWork()
            binding.tvStatus.text = "Background task scheduled every 15 minutes."
        }

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun schedulePeriodicWork() {
        val workRequest = PeriodicWorkRequestBuilder<FetchDataWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "FetchDataWork",
            androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun requestNotificationPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (!isGranted) {
                    binding.tvStatus.text =
                        "Notification permission denied. Notifications won't appear."
                }
            }
        requestPermissionLauncher.launch(POST_NOTIFICATIONS)
    }
}
