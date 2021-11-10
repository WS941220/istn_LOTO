package kr.co.istn.loto.util

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("android:toVisible")
fun View.setVisible(value: Boolean) {
    isVisible = value
    isGone = !value
}

fun AppCompatActivity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

