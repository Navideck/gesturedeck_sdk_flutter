//
//  GesturedeckHandler.swift
//  gesturedeck_flutter
//
//  Created by Rohit Sangwan on 08/08/23.
//

import GesturedeckiOS

class GesturedeckHandler: NSObject, GesturedeckChannel {
    var gesturedeck: Gesturedeck?
    var gesturedeckCallback: GesturedeckCallback

    init(gesturedeckCallback: GesturedeckCallback) {
        self.gesturedeckCallback = gesturedeckCallback
    }

    func initialize(androidActivationKey: String?, iosActivationKey: String?, autoStart: Bool) throws {
        gesturedeck = Gesturedeck(
            tapAction: {
                self.gesturedeckCallback.onTap {}
            },
            swipeLeftAction: {
                self.gesturedeckCallback.onSwipeLeft {}
            },
            swipeRightAction: {
                self.gesturedeckCallback.onSwipeRight {}
            },
            panAction: { _ in
                self.gesturedeckCallback.onPan {}
            },
            longPressAction: { _ in
                self.gesturedeckCallback.onLongPress {}
            },
            activationKey: iosActivationKey,
            autoStart: autoStart
        )
    }

    func start() throws {
        gesturedeck?.start()
    }

    func stop() throws {
        gesturedeck?.stop()
    }
}
