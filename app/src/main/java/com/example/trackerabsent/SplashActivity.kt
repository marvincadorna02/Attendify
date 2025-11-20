package com.example.trackerabsent

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var logo: ImageView
    private var progress = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        progressBar = findViewById(R.id.progressBar)
        logo = findViewById(R.id.applogo)

        val animation = AnimationUtils.loadAnimation(this, R.anim.logo_anim)
        logo.startAnimation(animation)

        progressBar.max = 100
        progressBar.progress = 0
        progressBar.isIndeterminate = false

        smoothProgress()
    }

    private fun smoothProgress() {
        handler.post(object : Runnable {
            override fun run() {
                progress += 1
                progressBar.progress = progress

                if (progress < 100) {
                    handler.postDelayed(this, 15)
                } else {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }
        })
    }
}
