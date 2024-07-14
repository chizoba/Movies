package com.remote.ghibli.android

import android.app.Application
import com.remote.ghibli.database.AndroidDatabaseDriverFactory
import com.remote.ghibli.dependencies.DependenciesFactory
import com.remote.ghibli.dependencies.initWorld

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initWorld(DependenciesFactory().create(AndroidDatabaseDriverFactory(this)))
    }
}