import Flutter
import Gesturedeck
import UIKit

public class SwiftGesturedeckFlutterPlugin: NSObject, FlutterPlugin, FlutterStreamHandler {
    var gesturedeck: Gesturedeck?
    private var touchEventsSink: FlutterEventSink?

    private func initGesturedeck(activationKey: String?, shouldSwipeLeftToSkipNext: Bool) {
        gesturedeck = Gesturedeck(tapAction: {
            self.touchEventsSink?("tap")
        }, swipeLeftAction: {
            self.touchEventsSink?("swipedLeft")
        }, swipeRightAction: {
            self.touchEventsSink?("swipedRight")
        },
        autoStart: false,
        activationKey: activationKey,
        shouldSwipeLeftToSkipNext: shouldSwipeLeftToSkipNext)
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
            let shouldSwipeLeftToSkipNext: Bool? = args?["shouldSwipeLeftToSkipNext"] as? Bool
            initGesturedeck(activationKey: activationKey, shouldSwipeLeftToSkipNext: shouldSwipeLeftToSkipNext ?? false)
            result(nil)
        case "shouldSwipeLeftToSkipNext":
            let args = call.arguments as? [String: Any]
            let value: Bool? = args?["value"] as? Bool
            gesturedeck?.shouldSwipeLeftToSkipNext = value ?? false
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
