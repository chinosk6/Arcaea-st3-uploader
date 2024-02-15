package io.github.chinosk.arcst3upload

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.content.res.XModuleResources
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import com.bumptech.glide.Glide
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.chinosk.arcst3upload.utils.FileUploader


class GameHookMain : IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {
    private var modulePath: String? = null
    private var chieriIconId = 0
    private val targetPackages = arrayOf("moe.low.chinosk.arc", "moe.low.arc")
    private var menuCreated = false

    override fun initZygote(startupParam: StartupParam) {
        modulePath = startupParam.modulePath
    }

    override fun handleInitPackageResources(resparam: InitPackageResourcesParam) {
        if (!targetPackages.contains(resparam.packageName)) return

        val modRes = XModuleResources.createInstance(modulePath, resparam.res)
        chieriIconId = resparam.res.addResource(modRes, R.drawable.chieri)
    }


    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (targetPackages.contains(lpparam.packageName)) {
            XposedBridge.log("Loaded app: ${lpparam.packageName}")
            hookActivity(lpparam.classLoader)
        }
    }

    private fun hookActivity(classLoader: ClassLoader) {
        try {
            val appActivityClass = XposedHelpers.findClass("low.moe.AppActivity", classLoader)
            XposedBridge.hookAllMethods(appActivityClass, "onResume", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val activity = param!!.thisObject as Context
                    addFloatingButton(activity)
                }
            })
        } catch (e: Throwable) {
            XposedBridge.log("Error hooking activity: $e")
        }
    }

    private fun addFloatingButton(context: Context) {
        if (menuCreated) return
        menuCreated = true
        val activity = context as? Activity ?: return
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)

        val button = ImageButton(context)

        // button.setImageResource(chieriIconId)
        // button.setImageResource(chieriIconId)
        button.setBackgroundColor(Color.WHITE)
        // button.background = null
        button.scaleType = ImageView.ScaleType.FIT_CENTER

        button.setOnClickListener {
            showPopupMenu(context, button, rootView)
        }
        button.setOnTouchListener(FloatingButtonTouchListener)

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels

        val params = FrameLayout.LayoutParams(
            192, 192
        ).apply {
            leftMargin = screenWidth - 200
            rightMargin = 100
        }

        rootView.addView(button, params)

        Glide.with(rootView)
            .load("https://www.chinosk6.cn/chieri256.jpg")
            .into(button)
    }

    private fun showPopupMenu(context: Context, anchor: View, rootView: ViewGroup) {
        val popupMenu = PopupMenu(context, anchor)
        popupMenu.menu.add("Upload st3")
        popupMenu.menu.add("Close Menu")
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Upload st3" -> {
                    FileUploader(context).uploadSt3()
                }
                "Close Menu" -> {
                    rootView.removeView(anchor)
                    menuCreated = false
                }
            }
            true
        }
        popupMenu.show()
    }

    private object FloatingButtonTouchListener : View.OnTouchListener {
        private var lastX = 0
        private var lastY = 0
        private var startX = 0
        private var startY = 0

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val x = event.rawX.toInt()
            val y = event.rawY.toInt()
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    val lParams = v.layoutParams as FrameLayout.LayoutParams
                    lastX = x - lParams.leftMargin
                    lastY = y - lParams.topMargin
                    startX = x
                    startY = y
                }
                MotionEvent.ACTION_MOVE -> {
                    val lParams = v.layoutParams as FrameLayout.LayoutParams
                    val newX = x - lastX
                    val newY = y - lastY
                    lParams.leftMargin = newX
                    lParams.topMargin = newY
                    v.layoutParams = lParams
                }
                MotionEvent.ACTION_UP -> {
                    if (Math.abs(x - startX) < 5 && Math.abs(y - startY) < 5) {
                        v.performClick()
                    }
                }
            }
            return true
        }
    }
}
