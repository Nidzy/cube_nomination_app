package com.cube.cubeacademy.lib.util

import android.content.Context
import android.widget.Toast

object ToastUtil {
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}