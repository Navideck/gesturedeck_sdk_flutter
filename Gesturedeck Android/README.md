- You can either make your MainActivity to subclass GesturedeckActivity

```
class MainActivity : GesturedeckActivity()
```

- Or feed all touchEvents to Gesturedeck manually

```
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
```

- and to handle device's volume key action with Gesturedeck library , feed keyEvents as well

```
class MainActivity : AppCompatActivity() {
    ......
    
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {    
        // To hide device native volume Dialog , and Show only Gesturedeck UI
        return gesturedeckMapbox?.onKeyEvents(event) ?: false
        
        // To show both , Device native dialog , as well as GesturedeckUI
        // gesturedeckMapbox?.onKeyEvents(event)
        // return false
    }
```