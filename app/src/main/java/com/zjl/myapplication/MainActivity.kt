package com.zjl.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadingView.setLogo(resources.getDrawable(R.drawable.ic_launcher_background))
        Thread{
            for (i in 0..100){
                loadingView.setProgress(i)
                Thread.sleep(50)
            }
        }.start()
    }
}