//
//  GSTouchesShowingGestureRecognizer.swift
//  GSTouchesShowingWindow-Swift
//
//  Created by Lukas Petr on 8/25/17.
//  Copyright © 2017 Glimsoft. All rights reserved.
//

import UIKit
import UIKit.UIGestureRecognizerSubclass

public class GSTouchesShowingGestureRecognizer: UIGestureRecognizer, UIGestureRecognizerDelegate {
    let touchesShowingController = GSTouchesShowingController()
    var overlayView: UIView? = nil

    public init(overlayView: UIView? = nil) {
        super.init(target: nil, action: nil)
        self.cancelsTouchesInView = false
        self.delaysTouchesBegan = false
        self.delaysTouchesEnded = false
        self.delegate = self
        self.overlayView = overlayView
    }

    override public func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent) {
        let overlayView = self.overlayView ?? self.view!
        for touch in touches {
            self.touchesShowingController.touchBegan(touch, view: overlayView)
        }
    }

    override public func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent) {
        let overlayView = self.overlayView ?? self.view!
        for touch in touches {
            self.touchesShowingController.touchMoved(touch, view: overlayView)
        }
    }

    override public func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent) {
        let overlayView = self.overlayView ?? self.view!
        for touch in touches {
            self.touchesShowingController.touchEnded(touch, view: overlayView)
        }
    }

    override public func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent) {
        let overlayView = self.overlayView ?? self.view!
        for touch in touches {
            self.touchesShowingController.touchEnded(touch, view: overlayView)
        }
    }

    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        return true
    }
}
