import Flutter
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
