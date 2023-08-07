//
//  GesturedeckMediaHandler.swift
//  gesturedeck_flutter
//
//  Created by Rohit Sangwan on 08/08/23.
//

import GesturedeckiOS

private class GesturedeckMediaHandler: NSObject, GesturedeckMediaFlutter {
    var gesturedeckMediaCallback: GesturedeckMediaCallback
    var gesturedeckMedia: GesturedeckMedia?

    init(gesturedeckCallback: GesturedeckMediaCallback) {
        gesturedeckMediaCallback = gesturedeckCallback
    }

    func initialize(activationKey: String?, autoStart: Bool, reverseHorizontalSwipes: Bool, overlayConfig: OverlayConfig?) throws {
        let tintColorValue: String? = overlayConfig?.tintColor as? String
        var tintColor: UIColor? = nil
        if tintColorValue != nil {
            tintColor = UIColor(hexString: tintColorValue!)
        }
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
            activationKey: activationKey,
            autoStart: autoStart,
            gesturedeckMediaOverlay: GesturedeckMediaOverlay(
                tintColor: tintColor,
                topIcon: overlayConfig?.topIcon?.toUIImage(),
                iconTap: overlayConfig?.iconTap?.toUIImage(),
                iconTapToggled: overlayConfig?.iconTapToggled?.toUIImage(),
                iconSwipeLeft: overlayConfig?.iconSwipeLeft?.toUIImage(),
                iconSwipeRight: overlayConfig?.iconSwipeRight?.toUIImage(),
                reverseHorizontalSwipes: reverseHorizontalSwipes
            )
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
}
