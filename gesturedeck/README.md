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

Optional: To extend the app's UI around Android camera cutouts (aka notch) in landscape mode, add this block in your MainActivity. This will also ensure that Gesturedeck's overlay tracks exactly the shape of the app (i.e. rounded corners)

```kotlin
class MainActivity: FlutterActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GesturedeckPlugin.instance?.extendAroundNotch(this)
    }
}
```
