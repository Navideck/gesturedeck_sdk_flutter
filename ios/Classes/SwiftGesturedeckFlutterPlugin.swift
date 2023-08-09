import Flutter

public class SwiftGesturedeckFlutterPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let messenger = registrar.messenger()
        let gesturedeckCallback = GesturedeckCallback(binaryMessenger: messenger)
        let gesturedeckMediaCallback = GesturedeckMediaCallback(binaryMessenger: messenger)
        GesturedeckChannelSetup.setUp(binaryMessenger: messenger, api: GesturedeckHandler(gesturedeckCallback: gesturedeckCallback))
        GesturedeckMediaChannelSetup.setUp(binaryMessenger: messenger, api: GesturedeckMediaHandler(gesturedeckCallback: gesturedeckMediaCallback))
    }
}
