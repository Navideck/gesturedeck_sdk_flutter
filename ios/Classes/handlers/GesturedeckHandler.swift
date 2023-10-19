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
            tapAction: gestureActionConfig.tapAction(gesturedeckCallback),
            swipeLeftAction: gestureActionConfig.swipeLeftAction(gesturedeckCallback),
            swipeRightAction: gestureActionConfig.swipeRightAction(gesturedeckCallback),
            panAction: gestureActionConfig.panAction(gesturedeckCallback),
            longPressAction: gestureActionConfig.longPressAction(gesturedeckCallback),
            autoStart: autoStart,
            activationKey: iOSActivationKey
        )
    }

    func updateActionConfig(gestureActionConfig: GestureActionConfig) throws {
        if gestureActionConfig.enableTapAction != nil {
            gesturedeck?.tapAction = gestureActionConfig.tapAction(gesturedeckCallback)
        }
        if gestureActionConfig.enableSwipeLeftAction != nil {
            gesturedeck?.swipeLeftAction = gestureActionConfig.swipeLeftAction(gesturedeckCallback)
        }
        if gestureActionConfig.enableSwipeRightAction != nil {
            gesturedeck?.swipeRightAction = gestureActionConfig.swipeRightAction(gesturedeckCallback)
        }
        if gestureActionConfig.enablePanAction != nil {
            gesturedeck?.panAction = gestureActionConfig.panAction(gesturedeckCallback)
        }
        if gestureActionConfig.enableLongPressAction != nil {
            gesturedeck?.longPressAction = gestureActionConfig.longPressAction(gesturedeckCallback)
        }
    }

    func start() throws {
        gesturedeck?.start()
    }

    func stop() throws {
        gesturedeck?.stop()
    }
}

private extension GestureActionConfig {
    func tapAction(_ callback: GesturedeckCallback) -> (() -> Void)? {
        return enableTapAction != false ? {
            callback.onTap {}
        } : nil
    }

    func swipeLeftAction(_ callback: GesturedeckCallback) -> (() -> Void)? {
        return enableSwipeLeftAction != false ? {
            callback.onSwipeLeft {}
        } : nil
    }

    func swipeRightAction(_ callback: GesturedeckCallback) -> (() -> Void)? {
        return enableSwipeRightAction != false ? {
            callback.onSwipeRight {}
        } : nil
    }

    func panAction(_ callback: GesturedeckCallback) -> ((_ gestureRecognizer: UIPanGestureRecognizer) -> Void)? {
        return enablePanAction != false ? { _ in
            callback.onPan {}
        } : nil
    }

    func longPressAction(_ callback: GesturedeckCallback) -> ((_ sender: UILongPressGestureRecognizer) -> Void)? {
        return enableLongPressAction != false ? { _ in
            callback.onLongPress {}
        } : nil
    }
}
