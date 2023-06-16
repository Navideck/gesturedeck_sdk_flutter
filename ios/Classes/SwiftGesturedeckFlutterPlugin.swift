import Flutter
import Gesturedeck
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
            let enableGesturedeckMedia: Bool? = args?["enableGesturedeckMedia"] as? Bool
            initGesturedeck(
                activationKey: activationKey,
                reverseHorizontalSwipes: reverseHorizontalSwipes ?? false,
                enableGesturedeckMedia: enableGesturedeckMedia ?? false
            )
            result(nil)
        case "reverseHorizontalSwipes":
            let args = call.arguments as? [String: Any]
            let value: Bool? = args?["value"] as? Bool
            gesturedeckMedia?.reverseHorizontalSwipes = value ?? false
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
            gesturedeckMedia?.dispose()
            gesturedeck?.dispose()
        default:
            result(FlutterMethodNotImplemented)
        }
    }
    
    private func initGesturedeck(activationKey: String?, reverseHorizontalSwipes: Bool, enableGesturedeckMedia: Bool) {
        if(enableGesturedeckMedia){
            gesturedeckMedia = GesturedeckMedia(
                tapAction: sendTapEvent,
                swipeLeftAction: sendSwipeLeftEvent,
                swipeRightAction: sendSwipeRightEvent,
                autoStart: false,
                activationKey: activationKey,
                overlayConfig:  OverlayConfig(
                    reverseHorizontalSwipes: reverseHorizontalSwipes
                )
            )
        }else{
            gesturedeck = Gesturedeck(
                tapAction: sendTapEvent,
                swipeLeftAction: sendSwipeLeftEvent,
                swipeRightAction: sendSwipeRightEvent,
                autoStart: false,
                activationKey: activationKey
            )
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
    
    private func sendTapEvent(){
        self.touchEventsSink?("tap")
    }
    
    private func sendSwipeLeftEvent(){
        self.touchEventsSink?("swipedLeft")
    }
    
    private func sendSwipeRightEvent(){
        self.touchEventsSink?("swipedRight")
    }
    
}
