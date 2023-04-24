import Flutter
import Gesturedeck
import UIKit

public class SwiftGesturedeckFlutterPlugin: NSObject, FlutterPlugin, FlutterStreamHandler {
    var gesturedeck: Gesturedeck?
    private var touchEventsSink: FlutterEventSink?

    private func initGesturedeck(activationKey: String?, reverseHorizontalSwipes: Bool) {
        gesturedeck = Gesturedeck(tapAction: {
            self.touchEventsSink?("tap")
        }, swipeLeftAction: {
            self.touchEventsSink?("swipedLeft")
        }, swipeRightAction: {
            self.touchEventsSink?("swipedRight")
        },
        autoStart: false,
        activationKey: activationKey,
        reverseHorizontalSwipes: reverseHorizontalSwipes)
    }

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
            initGesturedeck(activationKey: activationKey, reverseHorizontalSwipes: reverseHorizontalSwipes ?? false)
            result(nil)
        case "reverseHorizontalSwipes":
            let args = call.arguments as? [String: Any]
            let value: Bool? = args?["value"] as? Bool
            gesturedeck?.reverseHorizontalSwipes = value ?? false
            result(nil)
        case "start":
            gesturedeck?.start()
            result(nil)
        case "stop":
            gesturedeck?.stop()
            result(nil)
        default:
            result(FlutterMethodNotImplemented)
        }
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
}
