- You can either make your MainActivity to subclass GesturedeckActivity

```kotlin
class MainActivity : GesturedeckActivity()
```

- Or feed all touchEvents to Gesturedeck manually

```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var gesturedeck: Gesturedeck

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gesturedeck = Gesturedeck(this)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        // Feed all touchEvents to Gesturedeck
        gesturedeck.onTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }
}
```

- and to handle device's volume key action with Gesturedeck library , feed keyEvents as well

```kotlin
class MainActivity : AppCompatActivity() {

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {    
        // To hide device native volume Dialog , and Show only Gesturedeck UI
        return gesturedeckMapbox?.onKeyEvents(event) ?: false
        
        // To show both , Device native dialog , as well as GesturedeckUI
        // gesturedeckMapbox?.onKeyEvents(event)
        // return false
    }
}
```

To initialize Gesturedeck manually, we have two options:

- Either pass activity in Gesturedeck constructor like this

```kotlin
    gesturedeck = Gesturedeck(this)
```

- or add this tag in the Manifest file

```xml
    <application
        android:name="com.navideck.gesturedeck_android.globalActivity.GlobalApplication"
    />
```

and initialize without passing activity

```kotlin
    gesturedeck = Gesturedeck()
```

To render gesturedeck ui on your own view , pass rootView parameter

Note : If we pass rootView parameter , then there will be no `Blur` or `Dim` effect on that view
```kotlin
    gesturedeck = Gesturedeck(
        this,
        rootView = YOUR_VIEW_GROUP,
    )
```
