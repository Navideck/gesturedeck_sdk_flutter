import Flutter
import UIKit
import GesturedeckSDK

public class SwiftGesturedeckPlugin: NSObject, FlutterPlugin, FlutterStreamHandler {
    var gesturedeck: Gesturedeck?
    private var touchEventsSink: FlutterEventSink?
    
    private func initGesturedeck(){
        gesturedeck = Gesturedeck(tapAction: {
            self.touchEventsSink?("tap")
        }, swipeLeftAction: {
            self.touchEventsSink?("swipedLeft")
        }, swipeRightAction: {
            self.touchEventsSink?("swipedRight")
        })
    }
    
    private func disposeGesturedeck(){
        gesturedeck?.tapAction = nil
        gesturedeck?.swipeLeftAction = nil
        gesturedeck?.swipeRightAction = nil
    }
    
    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        guard let args = arguments as? Dictionary<String, Any>, let name = args["name"] as? String else {
              return nil
        }
        if name == "touchEvent" {
            touchEventsSink = events
            initGesturedeck()
        }
        return nil
    }
    
    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
        guard let args = arguments as? Dictionary<String, Any>, let name = args["name"] as? String else {
              return nil
        }
        if name == "touchEvent" {
            touchEventsSink = nil
            disposeGesturedeck()
        }
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
