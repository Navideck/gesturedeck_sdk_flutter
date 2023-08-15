//
//  GesturedeckMediaHandler.swift
//  gesturedeck_flutter
//
//  Created by Rohit Sangwan on 08/08/23.
//

import GesturedeckiOS

class GesturedeckMediaHandler: NSObject, GesturedeckMediaChannel {
    var gesturedeckMediaCallback: GesturedeckMediaCallback
    var gesturedeckMedia: GesturedeckMedia?

    init(gesturedeckCallback: GesturedeckMediaCallback) {
        gesturedeckMediaCallback = gesturedeckCallback
    }

    func initialize(activationKey: String?, autoStart: Bool, reverseHorizontalSwipes: Bool, panSensitivity: Int64?, overlayConfig: OverlayConfig?) throws {
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
            longPressAction: { _ in
                self.gesturedeckMediaCallback.onLongPress {}
            },
            activationKey: activationKey,
            autoStart: autoStart,
            gesturedeckMediaOverlay: overlayConfig?.toGesturedeckMedia(reverseHorizontalSwipes),
            panSensitivity: panSensitivity?.toPanSensitivity() ?? .medium
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

    func setGesturedeckMediaOverlay(overlayConfig: OverlayConfig?) throws {
        if overlayConfig == nil { return }
        let reverseHorizontalSwipes = gesturedeckMedia?.gesturedeckMediaOverlay.reverseHorizontalSwipes ?? false
        gesturedeckMedia?.gesturedeckMediaOverlay = overlayConfig!.toGesturedeckMedia(reverseHorizontalSwipes)
    }
}

private extension OverlayConfig {
    func toGesturedeckMedia(_ reverseHorizontalSwipes: Bool) -> GesturedeckMediaOverlay {
        var tintUIColor: UIColor? = nil
        var overlayBackgroundUIColor: UIColor? = nil
        
        if let tintColor = tintColor {
            tintUIColor = UIColor(hexString: tintColor)
        }
        if let overlayBackgroundColor = overlayBackgroundColor {
            overlayBackgroundUIColor = UIColor(hexString: overlayBackgroundColor)
        }
        
        return GesturedeckMediaOverlay(
            tintColor: tintUIColor,
            overlayBackgroundColor: overlayBackgroundUIColor,
            topIcon: topIcon?.toUIImage(),
            iconTap: iconTap?.toUIImage(),
            iconTapToggled: iconTapToggled?.toUIImage(),
            iconSwipeLeft: iconSwipeLeft?.toUIImage(),
            iconSwipeRight: iconSwipeRight?.toUIImage(),
            reverseHorizontalSwipes: reverseHorizontalSwipes
        )
    }
}

private extension FlutterStandardTypedData {
    func toUIImage() -> UIImage? {
        return UIImage(data: data)
    }
}

private extension UIColor {
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

private extension Int64 {
    func toPanSensitivity() -> PanSensitivity? {
        switch self {
        case 0:
            return .low
        case 1:
            return .medium
        case 2:
            return .high
        default:
            return nil
        }
    }
}
