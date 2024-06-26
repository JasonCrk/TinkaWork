package com.latinka.tinkawork

import android.app.Application

import com.google.firebase.FirebaseApp

class TinkaWorkApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}