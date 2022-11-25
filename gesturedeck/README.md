# Gesturedeck Flutter

Flutter plugin for Gesturedeck. This plugin wraps the native iOS and Android SDKs

## Getting Started

Android requires minimum version : 21

Change `MainActivity.kt` of your native android project like this

```kotlin
import android.view.MotionEvent
import io.flutter.embedding.android.FlutterActivity
import  com.navideck.gesturedeck.GesturedeckPlugin

class MainActivity: FlutterActivity() {

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        GesturedeckPlugin.instance?.dispatchTouchEvent(event, activity)
        return super.dispatchTouchEvent(event)
    }
}
```

To extend Flutter App ui beyond Android camera cutouts ( Notch ) in landscape mode, add this block as well in your MainActivity ( Optional )

```kotlin
class MainActivity: FlutterActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GesturedeckPlugin.instance?.extendAroundCameraCutout(this)
    }
}
```
