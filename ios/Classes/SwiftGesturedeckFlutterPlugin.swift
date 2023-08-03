import Flutter
import GesturedeckiOS
import UIKit

public class SwiftGesturedeckFlutterPlugin: NSObject, FlutterPlugin, FlutterStreamHandler {
    var gesturedeck: Gesturedeck?
    var gesturedeckMedia: GesturedeckMedia?
    private var touchEventsSink: FlutterEventSink?

    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "com.navideck.gesturedeck.method", binaryMessenger: registrar.messenger())
        let instance = SwiftGesturedeckFlutterPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
        let eventChannel = FlutterEventChannel(name: "com.navideck.gesturedeck", binaryMessenger: registrar.messenger())
        eventChannel.setStreamHandler(instance)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "initialize":
            let args = call.arguments as? [String: Any]
            let activationKey: String? = args?["activationKey"] as? String
            let reverseHorizontalSwipes: Bool? = args?["reverseHorizontalSwipes"] as? Bool
            let autoStart: Bool? = args?["autoStart"] as? Bool
            let enableGesturedeckMedia: Bool? = args?["enableGesturedeckMedia"] as? Bool
            let overlayConfig: [String: Any]? = args?["overlayConfig"] as? [String: Any]
            initGesturedeck(
                activationKey: activationKey,
                autoStart: autoStart,
                reverseHorizontalSwipes: reverseHorizontalSwipes ?? false,
                enableGesturedeckMedia: enableGesturedeckMedia ?? false,
                overlayConfig: overlayConfig
            )
            result(nil)
        case "reverseHorizontalSwipes":
            let args = call.arguments as? [String: Any]
            let value: Bool? = args?["value"] as? Bool
            gesturedeckMedia?.gesturedeckMediaOverlay.reverseHorizontalSwipes = value ?? false
            result(nil)
        case "start":
            gesturedeckMedia?.start()
            gesturedeck?.start()
            result(nil)
        case "stop":
            gesturedeckMedia?.stop()
            gesturedeck?.stop()
            result(nil)
        case "dispose":
            result(nil)
        default:
            result(FlutterMethodNotImplemented)
        }
    }

    private func initGesturedeck(
        activationKey: String?,
        autoStart: Bool?,
        reverseHorizontalSwipes: Bool,
        enableGesturedeckMedia: Bool,
        overlayConfig: [String: Any]?
    ) {
        if enableGesturedeckMedia {
            let tintColorValue: String? = overlayConfig?["tintColor"] as? String
            var tintColor: UIColor? = nil
            if tintColorValue != nil {
                tintColor = UIColor(hexString: tintColorValue!)
            }
            var topIcon: UIImage? = nil
            var iconSwipeLeft: UIImage? = nil
            var iconSwipeRight: UIImage? = nil
            var iconTap: UIImage? = nil
            var iconTapToggled: UIImage? = nil
            if let overlayConfig = overlayConfig {
                for (key, value) in overlayConfig {
                    if let typedData = value as? FlutterStandardTypedData {
                        switch key {
                        case "topIcon":
                            topIcon = UIImage(data: typedData.data)
                        case "iconSwipeLeft":
                            iconSwipeLeft = UIImage(data: typedData.data)
                        case "iconSwipeRight":
                            iconSwipeRight = UIImage(data: typedData.data)
                        case "iconTap":
                            iconTap = UIImage(data: typedData.data)
                        case "iconTapToggled":
                            iconTapToggled = UIImage(data: typedData.data)
                        default:
                            break
                        }
                    }
                }
            }
            gesturedeckMedia = GesturedeckMedia(
                tapAction: sendTapEvent,
                swipeLeftAction: sendSwipeLeftEvent,
                swipeRightAction: sendSwipeRightEvent,
                activationKey: activationKey,
                autoStart: autoStart ?? false,
                gesturedeckMediaOverlay: GesturedeckMediaOverlay(
                    tintColor: tintColor,
                    topIcon: topIcon,
                    iconTap: iconTap,
                    iconTapToggled: iconTapToggled,
                    iconSwipeLeft: iconSwipeLeft,
                    iconSwipeRight: iconSwipeRight,
                    reverseHorizontalSwipes: reverseHorizontalSwipes
                )
            )
            gesturedeck = nil
        } else {
            gesturedeck = Gesturedeck(
                tapAction: sendTapEvent,
                swipeLeftAction: sendSwipeLeftEvent,
                swipeRightAction: sendSwipeRightEvent,
                activationKey: activationKey,
                autoStart: autoStart ?? false
            )
            gesturedeckMedia = nil
        }
    }

    private func hexStringToUIColor(hex: String) -> UIColor {
        var cString: String = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        if cString.hasPrefix("#") {
            cString.remove(at: cString.startIndex)
        }
        if (cString.count) != 6 {
            return UIColor.gray
        }
        var rgbValue: UInt64 = 0
        Scanner(string: cString).scanHexInt64(&rgbValue)
        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }

    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        guard let args = arguments as? [String: Any], let name = args["name"] as? String else {
            return nil
        }
        if name == "touchEvent" {
            touchEventsSink = events
        }
        return nil
    }

    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
        guard let args = arguments as? [String: Any], let name = args["name"] as? String else {
            return nil
        }
        if name == "touchEvent" {
            touchEventsSink = nil
        }
        return nil
    }

    private func sendTapEvent() {
        touchEventsSink?("tap")
    }

    private func sendSwipeLeftEvent() {
        touchEventsSink?("swipedLeft")
    }

    private func sendSwipeRightEvent() {
        touchEventsSink?("swipedRight")
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
