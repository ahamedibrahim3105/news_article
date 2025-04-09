package com.example.articles.activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.articles.databinding.ActivitySplashBinding


class SplashActivity: BaseActivity() {
    lateinit var bind: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(bind.root)


        Handler(mainLooper).postDelayed({
            startActivity(
                Intent(
                    this,
                    ActivityMain::class.java
                ).apply { Intent.FLAG_ACTIVITY_SINGLE_TOP })
            finish()
        }, 3000)
    }
}