package com.zjl.acceleratorloading


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zjl.loading.LoadingView
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
            Toast.makeText(this, "${loadingView.getProgress()}", Toast.LENGTH_SHORT).show()

        }
        btnLoading.setOnClickListener {
            loadingView.setState(LoadingView.State.LOADING)
            Toast.makeText(this, "${loadingView.getProgress()}", Toast.LENGTH_SHORT).show()
        }
        btnError.setOnClickListener {
            loadingView.setState(LoadingView.State.ERROR)
            Toast.makeText(this, "${loadingView.getProgress()}", Toast.LENGTH_SHORT).show()
        }
        btnComplete.setOnClickListener {
            loadingView.setState(LoadingView.State.COMPLETE)
            Toast.makeText(this, "${loadingView.getProgress()}", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "${loadingView.getProgress()}", Toast.LENGTH_SHORT).show()
        })
        //loadingView.setLogo("https://up.enterdesk.com/edpic_360_360/27/8f/93/278f938be4b460a57962d542eee989f6.jpg")

    }
}
