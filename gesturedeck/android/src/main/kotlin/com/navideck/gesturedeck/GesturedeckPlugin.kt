package com.navideck.gesturedeck

import android.app.Activity
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.*
import io.flutter.plugin.common.MethodChannel.Result
import kotlin.math.log

/** GesturedeckPlugin */
class GesturedeckPlugin: FlutterPlugin, MethodCallHandler, ActivityAware , EventChannel.StreamHandler {
  companion object {
    var instance: GesturedeckPlugin? = null
  }

  private var touchEventSink: EventChannel.EventSink? = null
  private lateinit var channel : MethodChannel
  private lateinit var touchEventResult : EventChannel

  var gesturedeck: GesturedeckMapbox? = null
  //var gesturedeck: GesturedeckIOS? = null
  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    gesturedeck = GesturedeckMapbox(binding.activity) { gesture: GestureEvent ->
      onGesturesCallback(gesture)
    }
  }

  fun dispatchTouchEvent(event: MotionEvent, activity: Activity) {
    gesturedeck?.onTouchEvent(event)
  }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "gesturedeck")
    channel.setMethodCallHandler(this)
    touchEventResult = EventChannel(flutterPluginBinding.binaryMessenger, "com.navideck.gesturedeck")
    touchEventResult.setStreamHandler(this)
    instance = this
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
      result.notImplemented()
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    touchEventResult.setStreamHandler(null)
    instance = null
  }



  private fun onGesturesCallback(gesture: GestureEvent) {
    Log.d("FlutterGestureCallback", gesture.name)
    when (gesture) {
        GestureEvent.SWIPED_RIGHT -> {
          touchEventSink?.success("swipedRight")
        }
        GestureEvent.SWIPED_LEFT -> {
          touchEventSink?.success("swipedLeft")
        }
        GestureEvent.LongPress -> {
          touchEventSink?.success("tap")
        }
    }
  }

  override fun onDetachedFromActivity() {
    TODO("Not yet implemented")
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }


  override fun onListen(args: Any?, eventSink: EventChannel.EventSink?) {
    val map = args as? Map<String, Any> ?: return
    when (map["name"]) {
      "touchEvent" -> {
        touchEventSink = eventSink
      }
    }
  }

  override fun onCancel(args: Any?) {
    val map = args as? Map<String, Any> ?: return
    when (map["name"]) {
      "touchEvent" -> touchEventSink = null
    }
  }

}
