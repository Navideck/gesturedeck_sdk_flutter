import Flutter
import GesturedeckiOS
import UIKit

public class SwiftGesturedeckFlutterPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let messenger = registrar.messenger()
        let gesturedeckCallback = GesturedeckCallback(binaryMessenger: messenger)
        let gesturedeckMediaCallback = GesturedeckMediaCallback(binaryMessenger: messenger)
        GesturedeckFlutterSetup.setUp(binaryMessenger: messenger, api: GesturedeckHandler(gesturedeckCallback: gesturedeckCallback))
        GesturedeckMediaFlutterSetup.setUp(binaryMessenger: messenger, api: GesturedeckMediaHandler(gesturedeckCallback: gesturedeckMediaCallback))
    }
}

/// Gesturedeck
private class GesturedeckHandler: NSObject, GesturedeckFlutter {
    var gesturedeck: Gesturedeck?
    var gesturedeckCallback: GesturedeckCallback

    init(gesturedeckCallback: GesturedeckCallback) {
        self.gesturedeckCallback = gesturedeckCallback
    }

    func initialize(activationKey: String?, autoStart: Bool) throws {
        gesturedeck = Gesturedeck(
            tapAction: {
                self.gesturedeckCallback.onTap {}
            },
            swipeLeftAction: {
                self.gesturedeckCallback.onSwipeLeft {}
            },
            swipeRightAction: {
                self.gesturedeckCallback.onSwipeRight {}
            },
            panAction: { _ in
                self.gesturedeckCallback.onPan {}
            },
            activationKey: activationKey,
            autoStart: autoStart
        )
    }

    func start() throws {
        gesturedeck?.start()
    }

    func stop() throws {
        gesturedeck?.stop()
    }
}

/// GesturedeckMedia
private class GesturedeckMediaHandler: NSObject, GesturedeckMediaFlutter {
    var gesturedeckMediaCallback: GesturedeckMediaCallback
    var gesturedeckMedia: GesturedeckMedia?

    init(gesturedeckCallback: GesturedeckMediaCallback) {
        gesturedeckMediaCallback = gesturedeckCallback
    }

    func initialize(activationKey: String?, autoStart: Bool, reverseHorizontalSwipes: Bool, overlayConfig: OverlayConfig?) throws {
        let tintColorValue: String? = overlayConfig?.tintColor as? String
        var tintColor: UIColor? = nil
        if tintColorValue != nil {
            tintColor = UIColor(hexString: tintColorValue!)
        }
        gesturedeckMedia = GesturedeckMedia(
            tapAction: {
                self.gesturedeckMediaCallback.onTap {}
            },
            swipeLeftAction: {
                self.gesturedeckMediaCallback.onSwipeLeft {}
            },
            swipeRightAction: {
                self.gesturedeckMediaCallback.onSwipeRight {}
            },
            panAction: { _ in
                self.gesturedeckMediaCallback.onPan {}
            },
            activationKey: activationKey,
            autoStart: autoStart,
            gesturedeckMediaOverlay: GesturedeckMediaOverlay(
                tintColor: tintColor,
                topIcon: overlayConfig?.topIcon?.toUIImage(),
                iconTap: overlayConfig?.iconTap?.toUIImage(),
                iconTapToggled: overlayConfig?.iconTapToggled?.toUIImage(),
                iconSwipeLeft: overlayConfig?.iconSwipeLeft?.toUIImage(),
                iconSwipeRight: overlayConfig?.iconSwipeRight?.toUIImage(),
                reverseHorizontalSwipes: reverseHorizontalSwipes
            )
        )
    }

    func start() throws {
        gesturedeckMedia?.start()
    }

    func stop() throws {
        gesturedeckMedia?.stop()
    }

    func dispose() throws {
        // Nothing to dispose
    }

    func reverseHorizontalSwipes(value: Bool) throws {
        gesturedeckMedia?.gesturedeckMediaOverlay.reverseHorizontalSwipes = value
    }
}

/// Extensions

extension FlutterStandardTypedData {
    func toUIImage() -> UIImage? {
        return UIImage(data: data)
    }
}

extension UIColor {
    convenience init(hexString: String) {
        let hex = hexString.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int = UInt64()
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(red: CGFloat(r) / 255, green: CGFloat(g) / 255, blue: CGFloat(b) / 255, alpha: CGFloat(a) / 255)
    }
}
