// Autogenerated from Pigeon (v10.1.4), do not edit directly.
// See also: https://pub.dev/packages/pigeon

package com.navideck.gesturedeck_flutter

import android.util.Log
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MessageCodec
import io.flutter.plugin.common.StandardMessageCodec
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

private fun wrapResult(result: Any?): List<Any?> {
  return listOf(result)
}

private fun wrapError(exception: Throwable): List<Any?> {
  if (exception is FlutterError) {
    return listOf(
      exception.code,
      exception.message,
      exception.details
    )
  } else {
    return listOf(
      exception.javaClass.simpleName,
      exception.toString(),
      "Cause: " + exception.cause + ", Stacktrace: " + Log.getStackTraceString(exception)
    )
  }
}

/**
 * Error class for passing custom error details to Flutter via a thrown PlatformException.
 * @property code The error code.
 * @property message The error message.
 * @property details The error details. Must be a datatype supported by the api codec.
 */
class FlutterError (
  val code: String,
  override val message: String? = null,
  val details: Any? = null
) : Throwable()

/** Generated class from Pigeon that represents data sent in messages. */
data class OverlayConfig (
  val tintColor: String? = null,
  val topIcon: ByteArray? = null,
  val iconSwipeLeft: ByteArray? = null,
  val iconSwipeRight: ByteArray? = null,
  val iconTap: ByteArray? = null,
  val iconTapToggled: ByteArray? = null

) {
  companion object {
    @Suppress("UNCHECKED_CAST")
    fun fromList(list: List<Any?>): OverlayConfig {
      val tintColor = list[0] as String?
      val topIcon = list[1] as ByteArray?
      val iconSwipeLeft = list[2] as ByteArray?
      val iconSwipeRight = list[3] as ByteArray?
      val iconTap = list[4] as ByteArray?
      val iconTapToggled = list[5] as ByteArray?
      return OverlayConfig(tintColor, topIcon, iconSwipeLeft, iconSwipeRight, iconTap, iconTapToggled)
    }
  }
  fun toList(): List<Any?> {
    return listOf<Any?>(
      tintColor,
      topIcon,
      iconSwipeLeft,
      iconSwipeRight,
      iconTap,
      iconTapToggled,
    )
  }
}
/**
 * Gesturedeck
 *
 * Generated interface from Pigeon that represents a handler of messages from Flutter.
 */
interface GesturedeckFlutter {
  fun initialize(activationKey: String?, autoStart: Boolean)
  fun start()
  fun stop()

  companion object {
    /** The codec used by GesturedeckFlutter. */
    val codec: MessageCodec<Any?> by lazy {
      StandardMessageCodec()
    }
    /** Sets up an instance of `GesturedeckFlutter` to handle messages through the `binaryMessenger`. */
    @Suppress("UNCHECKED_CAST")
    fun setUp(binaryMessenger: BinaryMessenger, api: GesturedeckFlutter?) {
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckFlutter.initialize", codec)
        if (api != null) {
          channel.setMessageHandler { message, reply ->
            val args = message as List<Any?>
            val activationKeyArg = args[0] as String?
            val autoStartArg = args[1] as Boolean
            var wrapped: List<Any?>
            try {
              api.initialize(activationKeyArg, autoStartArg)
              wrapped = listOf<Any?>(null)
            } catch (exception: Throwable) {
              wrapped = wrapError(exception)
            }
            reply.reply(wrapped)
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckFlutter.start", codec)
        if (api != null) {
          channel.setMessageHandler { _, reply ->
            var wrapped: List<Any?>
            try {
              api.start()
              wrapped = listOf<Any?>(null)
            } catch (exception: Throwable) {
              wrapped = wrapError(exception)
            }
            reply.reply(wrapped)
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckFlutter.stop", codec)
        if (api != null) {
          channel.setMessageHandler { _, reply ->
            var wrapped: List<Any?>
            try {
              api.stop()
              wrapped = listOf<Any?>(null)
            } catch (exception: Throwable) {
              wrapped = wrapError(exception)
            }
            reply.reply(wrapped)
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
    }
  }
}
@Suppress("UNCHECKED_CAST")
private object GesturedeckMediaFlutterCodec : StandardMessageCodec() {
  override fun readValueOfType(type: Byte, buffer: ByteBuffer): Any? {
    return when (type) {
      128.toByte() -> {
        return (readValue(buffer) as? List<Any?>)?.let {
          OverlayConfig.fromList(it)
        }
      }
      else -> super.readValueOfType(type, buffer)
    }
  }
  override fun writeValue(stream: ByteArrayOutputStream, value: Any?)   {
    when (value) {
      is OverlayConfig -> {
        stream.write(128)
        writeValue(stream, value.toList())
      }
      else -> super.writeValue(stream, value)
    }
  }
}

/**
 * GesturedeckMedia
 *
 * Generated interface from Pigeon that represents a handler of messages from Flutter.
 */
interface GesturedeckMediaFlutter {
  fun initialize(activationKey: String?, autoStart: Boolean, reverseHorizontalSwipes: Boolean, overlayConfig: OverlayConfig?)
  fun start()
  fun stop()
  fun dispose()
  fun reverseHorizontalSwipes(value: Boolean)

  companion object {
    /** The codec used by GesturedeckMediaFlutter. */
    val codec: MessageCodec<Any?> by lazy {
      GesturedeckMediaFlutterCodec
    }
    /** Sets up an instance of `GesturedeckMediaFlutter` to handle messages through the `binaryMessenger`. */
    @Suppress("UNCHECKED_CAST")
    fun setUp(binaryMessenger: BinaryMessenger, api: GesturedeckMediaFlutter?) {
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaFlutter.initialize", codec)
        if (api != null) {
          channel.setMessageHandler { message, reply ->
            val args = message as List<Any?>
            val activationKeyArg = args[0] as String?
            val autoStartArg = args[1] as Boolean
            val reverseHorizontalSwipesArg = args[2] as Boolean
            val overlayConfigArg = args[3] as OverlayConfig?
            var wrapped: List<Any?>
            try {
              api.initialize(activationKeyArg, autoStartArg, reverseHorizontalSwipesArg, overlayConfigArg)
              wrapped = listOf<Any?>(null)
            } catch (exception: Throwable) {
              wrapped = wrapError(exception)
            }
            reply.reply(wrapped)
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaFlutter.start", codec)
        if (api != null) {
          channel.setMessageHandler { _, reply ->
            var wrapped: List<Any?>
            try {
              api.start()
              wrapped = listOf<Any?>(null)
            } catch (exception: Throwable) {
              wrapped = wrapError(exception)
            }
            reply.reply(wrapped)
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaFlutter.stop", codec)
        if (api != null) {
          channel.setMessageHandler { _, reply ->
            var wrapped: List<Any?>
            try {
              api.stop()
              wrapped = listOf<Any?>(null)
            } catch (exception: Throwable) {
              wrapped = wrapError(exception)
            }
            reply.reply(wrapped)
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaFlutter.dispose", codec)
        if (api != null) {
          channel.setMessageHandler { _, reply ->
            var wrapped: List<Any?>
            try {
              api.dispose()
              wrapped = listOf<Any?>(null)
            } catch (exception: Throwable) {
              wrapped = wrapError(exception)
            }
            reply.reply(wrapped)
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaFlutter.reverseHorizontalSwipes", codec)
        if (api != null) {
          channel.setMessageHandler { message, reply ->
            val args = message as List<Any?>
            val valueArg = args[0] as Boolean
            var wrapped: List<Any?>
            try {
              api.reverseHorizontalSwipes(valueArg)
              wrapped = listOf<Any?>(null)
            } catch (exception: Throwable) {
              wrapped = wrapError(exception)
            }
            reply.reply(wrapped)
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
    }
  }
}
/** Generated class from Pigeon that represents Flutter messages that can be called from Kotlin. */
@Suppress("UNCHECKED_CAST")
class GesturedeckCallback(private val binaryMessenger: BinaryMessenger) {
  companion object {
    /** The codec used by GesturedeckCallback. */
    val codec: MessageCodec<Any?> by lazy {
      StandardMessageCodec()
    }
  }
  fun onTap(callback: () -> Unit) {
    val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckCallback.onTap", codec)
    channel.send(null) {
      callback()
    }
  }
  fun onSwipeLeft(callback: () -> Unit) {
    val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckCallback.onSwipeLeft", codec)
    channel.send(null) {
      callback()
    }
  }
  fun onSwipeRight(callback: () -> Unit) {
    val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckCallback.onSwipeRight", codec)
    channel.send(null) {
      callback()
    }
  }
  fun onPan(callback: () -> Unit) {
    val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckCallback.onPan", codec)
    channel.send(null) {
      callback()
    }
  }
}
/** Generated class from Pigeon that represents Flutter messages that can be called from Kotlin. */
@Suppress("UNCHECKED_CAST")
class GesturedeckMediaCallback(private val binaryMessenger: BinaryMessenger) {
  companion object {
    /** The codec used by GesturedeckMediaCallback. */
    val codec: MessageCodec<Any?> by lazy {
      StandardMessageCodec()
    }
  }
  fun onTap(callback: () -> Unit) {
    val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaCallback.onTap", codec)
    channel.send(null) {
      callback()
    }
  }
  fun onSwipeLeft(callback: () -> Unit) {
    val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaCallback.onSwipeLeft", codec)
    channel.send(null) {
      callback()
    }
  }
  fun onSwipeRight(callback: () -> Unit) {
    val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaCallback.onSwipeRight", codec)
    channel.send(null) {
      callback()
    }
  }
  fun onPan(callback: () -> Unit) {
    val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.gesturedeck_flutter.GesturedeckMediaCallback.onPan", codec)
    channel.send(null) {
      callback()
    }
  }
}