package com.eaglesakura.firearm.channel

import android.content.Intent

data class ActivityResult(
    val requestCode: Int,
    val result: Int,
    val data: Intent?
)