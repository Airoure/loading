package com.zjl.acceleratorloading

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zjl.loading.LoadingView
import com.zjl.acceleratorloading.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var process = 0
        btnReset.setOnClickListener {
            loadingView.setProgress(0)
            process = 0
        }
        btnIncrease.setOnClickListener {
            process +=10
            loadingView.setProgress(process)
        }
        btnLoading.setOnClickListener {
            loadingView.setState(LoadingView.State.LOADING)
        }
        btnError.setOnClickListener {
            loadingView.setState(LoadingView.State.ERROR)
        }
        btnComplete.setOnClickListener {
            loadingView.setState(LoadingView.State.COMPLETE)
        }
    }
}