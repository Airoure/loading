package com.zjl.acceleratorloading


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
            process += 10
            loadingView.setProgress(process)

        }
        btnLoading.setOnClickListener {
            loadingView.setState(LoadingView2.State.LOADING)
        }
        btnError.setOnClickListener {
            loadingView.setState(LoadingView2.State.ERROR)
        }
        btnComplete.setOnClickListener {
            loadingView.setState(LoadingView2.State.COMPLETE)
        }
        loadingView.setOnCompleteListener {
            Toast.makeText(
                this@MainActivity,
                "加载完成",
                Toast.LENGTH_SHORT
            ).show()
        }
        loadingView.setOnClickListener(LoadingView2.OnClickListener {
            Toast.makeText(
                this@MainActivity,
                "点击",
                Toast.LENGTH_SHORT
            ).show()
        })
        loadingView.setLogo("http://res.sumeow.com/pic/2021/01/30/570/7102420b62aa11ebaee85254007aeec4.png")

    }
}

