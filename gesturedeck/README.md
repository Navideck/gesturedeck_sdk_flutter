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

Optional: To extend the app's UI beyond Android camera cutouts (aka notch) in landscape mode, add this block in your MainActivity

```kotlin
class MainActivity: FlutterActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GesturedeckPlugin.instance?.extendAroundCameraCutout(this)
    }
}
```
