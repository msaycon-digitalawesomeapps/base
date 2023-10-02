package com.digitalawesome.recweed

import android.app.Activity
import android.app.Application
import android.os.Bundle


class ActivityProvider(application: Application) {

    var activeActivity: Activity? = null

    init {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
                //
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                //
            }

            override fun onActivityDestroyed(activity: Activity) {
                //
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                //
            }

            override fun onActivityStarted(activity: Activity) {
                //
            }

            override fun onActivityResumed(activity: Activity) {
                activeActivity = activity
            }
        })
    }

}
