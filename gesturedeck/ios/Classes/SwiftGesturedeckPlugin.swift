import Flutter
import UIKit
import GesturedeckSDK

public class SwiftGesturedeckPlugin: NSObject, FlutterPlugin, FlutterStreamHandler {
    var gesturedeck: Gesturedeck?
    
    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        gesturedeck = Gesturedeck(tapAction: {
            events("tap")
        }, swipeLeftAction: {
            events("swipe left")
        }, swipeRightAction: {
            events("swipe right")
        })
        return nil
    }
    
    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
        gesturedeck?.tapAction = nil
        gesturedeck?.swipeLeftAction = nil
        gesturedeck?.swipeRightAction = nil
        return nil
    }
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        
        let channel = FlutterMethodChannel(name: "gesturedeck", binaryMessenger: registrar.messenger())
        let instance = SwiftGesturedeckPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
                
        let eventChannel = FlutterEventChannel(name: "com.navideck.gesturedeck", binaryMessenger: registrar.messenger())
        eventChannel.setStreamHandler(instance)
    }
}
