# Gesturedeck iOS

## Getting Started

In order to use Gesturedeck there are 3 ways to set it up. Choose the one that suits the setup of your app.

Add the framework in the target and then `import GesturedeckiOS`.

## AppDelegate
It is recommended to instantiate the SDK in `applicationDidBecomeActive` or later as the window might not be instantiated in `didFinishLaunchingWithOptions` lifecycle event.

## SceneDelegate
Use `sceneDidBecomeActive` or later.

```swift
import GesturedeckiOS
```

and edit your SceneDelegate (or AppDelegate) so that it looks like this:

```swift
class SceneDelegate: UIResponder, UIWindowSceneDelegate {

  var window: UIWindow?
  var gesturedeck: Gesturedeck?

...

func sceneDidBecomeActive(_ scene: UIScene) {
    // Called when the scene has moved from an inactive state to an active state.
    // Use this method to restart any tasks that were paused (or not yet started) when the scene was inactive.
    
    gesturedeck = Gesturedeck()
    gesturedeck?.tapAction = { callback in
        print("tapped")
    }

    gesturedeck?.swipeLeftAction = {
        print("swiped Left")
    }

    gesturedeck?.swipeRightAction = {
        print("swiped Right")
    }
  }
```

## SwiftUI
No Appdelegate or Scenedelegate needed.

```swift
@main
struct gesturedeckApp: App {
    @Environment(\.scenePhase) var scenePhase
    @State var gesturedeck: Gesturedeck?
```

```swift
var body: some Scene {
        WindowGroup {
            ...
        }
        .onChange(of: scenePhase) { phase in
            if phase == .active {
                gesturedeck = Gesturedeck()
                gesturedeck?.tapAction = { callback in
                    print("Tapped")
                    callback(true)  // Without callback no icon is displayed
                }

                gesturedeck?.swipeLeftAction = {
                    print("Swiped Left")
                }

                gesturedeck?.swipeRightAction = {
                    print("Swiped Right")
                }
            }
        }
    }
```

## Create .xcframework

In order to create a binary framework (`.xcframework`) that works on all CPU architectures use Xcode 13 (not 14!) and run the `xcframework.sh` script from the `gesturedeck-ios` folder:

```
./xcframework.sh -target GesturedeckiOS
```
