//
//  UIViewController.swift
//  Gesturedeck
//
//  Created by Foti Dim on 17.11.19.
//  Copyright © 2019 Navideck. All rights reserved.
//

import UIKit


extension UIViewController {

    func animateAppearance(on hostViewController: UIViewController, appeared: (()->())? = nil) {
//        guard self is GestureViewController || self is GestureViewControllerOverlay else { return }
//        self.navigationController?.interactivePopGestureRecognizer?.isEnabled = false
//        hostViewController.addChild(self)
        view.frame.size = hostViewController.view.frame.size

        UIView.transition(with: hostViewController.view, duration: 0.2, options: [.transitionCrossDissolve, .allowUserInteraction], animations: {
            hostViewController.view.addSubview(self.view)
        }, completion: { (completion) in
//            self.didMove(toParent: hostViewController)
            appeared?()
        })
    }

    func animateDisappearance(from hostViewController: UIViewController, disappeared: (()->())? = nil) {
//        guard self is GestureViewController || self is GestureViewControllerOverlay else { return }
//        willMove(toParent: nil)
        UIView.transition(with: hostViewController.view, duration: 0.7, options: [.transitionCrossDissolve, .allowUserInteraction], animations: {
            self.view.removeFromSuperview()
//            self.removeFromParent()
        }, completion: { (completion) in
//            self.navigationController?.interactivePopGestureRecognizer?.isEnabled = true
            disappeared?()
        })
    }
}
