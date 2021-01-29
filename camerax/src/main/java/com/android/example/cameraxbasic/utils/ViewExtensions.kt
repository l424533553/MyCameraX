/*
 * Copyright 2020 The Android Open Source Project
 *
 */

package com.android.example.cameraxbasic.utils

import android.os.Build
import android.view.DisplayCutout
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog

/** Combination of all flags required to put activity into immersive mode
 *  将活动置于沉浸式模式所需的所有标志的组合
 */
const val FLAGS_FULLSCREEN = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

/** Milliseconds used for UI animations
 * 用于UI动画的毫秒
 */
const val ANIMATION_FAST_MILLIS = 50L
const val ANIMATION_SLOW_MILLIS = 100L

/**
 * Simulate a button click, including a small delay while it is being pressed to trigger the
 * appropriate animations.
 * 模拟按钮单击，包括在按下按钮以触发*适当的动画时稍有延迟
 */
fun ImageButton.simulateClick(delay: Long = ANIMATION_FAST_MILLIS) {
    performClick()
    isPressed = true
    invalidate()
    postDelayed({
        invalidate()
        isPressed = false
    }, delay)
}

/** Pad this view with the insets provided by the device cutout (i.e. notch)
 * 使用设备切口提供的插图填充该视图（即刻槽）
 */
@RequiresApi(Build.VERSION_CODES.P)
fun View.padWithDisplayCutout() {

    /** Helper method that applies padding from cutout's safe insets
     * 从抠图的安全插图中应用填充的辅助方法
     */
    fun doPadding(cutout: DisplayCutout) = setPadding(
            cutout.safeInsetLeft,
            cutout.safeInsetTop,
            cutout.safeInsetRight,
            cutout.safeInsetBottom)

    // Apply padding using the display cutout designated "safe area"
    // 使用指定为“安全区域”的显示切口进行填充
    rootWindowInsets?.displayCutout?.let { doPadding(it) }

    // Set a listener for window insets since view.rootWindowInsets may not be ready yet
    // 由于view.rootWindowInsets可能尚未准备好，因此请为窗口插入设置监听器
    setOnApplyWindowInsetsListener { _, insets ->
        insets.displayCutout?.let { doPadding(it) }
        insets
    }
}

/** Same as [AlertDialog.show] but setting immersive mode in the dialog's window 
 * 与[AlertDialog.show]相同，但在对话框的窗口中设置沉浸模式
 */
fun AlertDialog.showImmersive() {
    // Set the dialog to not focusable
    // 将对话框设置为非焦点
    window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

    // Make sure that the dialog's window is in full screen
    // 确保对话框的窗口处于全屏状态
    window?.decorView?.systemUiVisibility = FLAGS_FULLSCREEN

    // Show the dialog while still in immersive mode
    // 在沉浸模式下显示对话框
    show()

    // Set the dialog to focusable again
    // 将对话框再次设置为可聚焦
    window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
}
