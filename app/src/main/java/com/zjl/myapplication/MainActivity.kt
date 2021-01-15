package com.zjl.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var process = 0
        btnReset.setOnClickListener {
//            loadingView.setProgress(0)
//            process = 0
            loadingView.setState(LoadingView.State.ERROR)
        }
        btnIncrease.setOnClickListener {
            process +=10
            loadingView.setProgress(process)
        }

    }
}