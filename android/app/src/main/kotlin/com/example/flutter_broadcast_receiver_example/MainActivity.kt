package com.example.flutter_broadcast_receiver_example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.yourapp/broadcast"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "sendBroadcast" -> {
                    val data = call.argument<String>("data")

                    println("DATA --> $data")

                    sendCustomBroadcast(data)
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }

        val filter = IntentFilter("com.example.yourapp.CUSTOM_INTENT")
        registerReceiver(broadcastReceiver, filter)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val data = intent?.getStringExtra("data")
            flutterEngine?.dartExecutor?.binaryMessenger?.let { messenger ->
                MethodChannel(messenger, CHANNEL).invokeMethod("broadcastReceived", data)
            }
        }
    }

    private fun sendCustomBroadcast(data: String?) {
        val intent = Intent("com.example.yourapp.CUSTOM_INTENT")
        intent.putExtra("data", data)
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}
