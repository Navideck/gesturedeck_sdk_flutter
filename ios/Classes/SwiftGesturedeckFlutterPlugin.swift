import Flutter
import UIKit
import Gesturedeck

public class SwiftGesturedeckFlutterPlugin: NSObject, FlutterPlugin, FlutterStreamHandler {
    var gesturedeck: Gesturedeck?
    private var touchEventsSink: FlutterEventSink?
    
    private func initGesturedeck(){
        gesturedeck = Gesturedeck(tapAction: {
            self.touchEventsSink?("tap")
        }, swipeLeftAction: {
            self.touchEventsSink?("swipedLeft")
        }, swipeRightAction: {
            self.touchEventsSink?("swipedRight")
        },autoStart: false)
    }
    
    
    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        guard let args = arguments as? Dictionary<String, Any>, let name = args["name"] as? String else {
              return nil
        }
        if name == "touchEvent" {
            touchEventsSink = events
            gesturedeck?.start()
        }
        return nil
    }
    
    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
        guard let args = arguments as? Dictionary<String, Any>, let name = args["name"] as? String else {
              return nil
        }
        if name == "touchEvent" {
            touchEventsSink = nil
            gesturedeck?.stop()
        }
        return nil
    }
    
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "com.navideck.gesturedeck.method", binaryMessenger: registrar.messenger())
        let instance = SwiftGesturedeckFlutterPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
                
        let eventChannel = FlutterEventChannel(name: "com.navideck.gesturedeck", binaryMessenger: registrar.messenger())
        eventChannel.setStreamHandler(instance)
        
        instance.initGesturedeck()
    }
}
