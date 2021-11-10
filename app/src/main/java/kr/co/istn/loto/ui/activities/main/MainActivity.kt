package kr.co.istn.loto.ui.activities.main

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.noke.nokemobilelibrary.NokeDeviceManagerService
import dagger.hilt.android.AndroidEntryPoint
import kr.co.istn.loto.databinding.ActivityMainBinding
import kr.co.istn.loto.ui.viewmodels.MainViewModel
import kr.co.istn.loto.util.toast

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            lifecycleOwner = this@MainActivity
            viewModel = this@MainActivity.mainViewModel

            mainViewModel.apply {
                isLocked.observe(this@MainActivity, {
                    val animator: ValueAnimator = if (it)
                        ValueAnimator.ofFloat(0f, 0.5f).setDuration(1000)
                    else
                        ValueAnimator.ofFloat(0.5f, 1.0f).setDuration(1000)
                    animator.addUpdateListener { a ->
                        binding.lockAni.progress = a.animatedValue as Float
                    }
                    animator.start()
                })

                isError.observe(this@MainActivity, {
                    toast(it.peekContent().toString())
                })
                isState.observe(this@MainActivity, {
                    binding.state.text = it.peekContent()
                })
            }
            initNoke()
        }
    }
    private fun initNoke() {
        val nokeServiceIntent = Intent(this, NokeDeviceManagerService::class.java)
        mainViewModel.bindService(this, nokeServiceIntent)
    }

}