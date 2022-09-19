# Gesturedeck iOS

## Getting Started

In order to use Gesturedeck:

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

TODO: Add instructions for all scenarios of SceneDelegate, AppDelegate or SwiftUI

## Create .xcframework

In order to create a binary framework (`.xcframework`) that works on all CPU architectures use Xcode 13 (not 14!) and run the `xcframework.sh` script from the `gesturedeck-ios` folder:

```
./xcframework.sh -target GesturedeckiOS
```