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

    func initialize(
        androidActivationKey _: String?,
        iOSActivationKey: String?,
        autoStart: Bool,
        gestureActionConfig: GestureActionConfig
    ) throws {
        gesturedeck = Gesturedeck(
            tapAction: !gestureActionConfig.enableTapAction ? nil : {
                self.gesturedeckCallback.onTap {}
            },
            swipeLeftAction: !gestureActionConfig.enableSwipeLeftAction ? nil : {
                self.gesturedeckCallback.onSwipeLeft {}
            },
            swipeRightAction: !gestureActionConfig.enableSwipeRightAction ? nil : {
                self.gesturedeckCallback.onSwipeRight {}
            },
            panAction: !gestureActionConfig.enablePanAction ? nil : { _ in
                self.gesturedeckCallback.onPan {}
            },
            longPressAction: !gestureActionConfig.enableLongPressAction ? nil : { _ in
                self.gesturedeckCallback.onLongPress {}
            },
            autoStart: autoStart,
            activationKey: iOSActivationKey
        )
    }

    func start() throws {
        gesturedeck?.start()
    }

    func stop() throws {
        gesturedeck?.stop()
    }
}
