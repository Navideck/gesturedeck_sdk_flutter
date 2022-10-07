package com.navideck.gesturedeck_android.globalActivity

import android.app.Activity
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

class GlobalApplication : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    init {
        instance = this
    }

    val mGlobalActivityLifecycleCallbacks = GlobalActivityLifecycleCallbacks()

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(mGlobalActivityLifecycleCallbacks)
    }

    companion object {
        private var instance: GlobalApplication? = null
        fun currentActivity(): Activity? {
            return instance?.mGlobalActivityLifecycleCallbacks?.currentActivity
        }
    }

}