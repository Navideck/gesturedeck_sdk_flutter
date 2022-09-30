//
//  GestureManager.swift
//  TunedeckAppleMusic
//
//  Created by Foti Dim on 28.10.17.
//  Copyright © 2017 Navideck. All rights reserved.
//

import UIKit
import MediaPlayer

var volumeStep: Float = 0.02

public class Gesturedeck: NSObject, UIGestureRecognizerDelegate {
    
    public var tapAction: ((@escaping (Bool) -> Void) -> Void)?
    public var swipeLeftAction: (() -> Void)?
    public var swipeRightAction: (() -> Void)?
    public var strokeColor = UIColor(red: 28.0/255, green: 30.0/255, blue: 57.0/255, alpha: 0.9).cgColor
    public var volumeIcon: UIImage?
    public var pauseIcon: UIImage?
    public var playIcon: UIImage?
    public var swipeLeftIcon: UIImage?
    public var swipeRightIcon: UIImage?
    private var volumeSlider: UISlider!
    private var hostViewController: UIViewController!
    private var gestureViewController: GestureViewController?
    private let overlayGestureViewController = GestureViewControllerOverlay()
    private var lastYPan: Float = 0
    private lazy var panGestureRecognizer = UIPanGestureRecognizer(target: self, action: #selector(panned))
    private lazy var leftSwipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(swipedLeft))
    private lazy var rightSwipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(swipedRight))
    private lazy var dualTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(tapped))
    private let insomnia = Insomnia(mode: .whenCharging)
    
    public init(on hostViewController: UIViewController? = nil,
                tapAction: (((Bool) -> Void) -> Void)? = { callback in
                    var isHighlighted = false
                    if MPMusicPlayerController.systemMusicPlayer.playbackState == .playing {
                    MPMusicPlayerController.systemMusicPlayer.pause()
                        isHighlighted = false
                    } else if MPMusicPlayerController.systemMusicPlayer.playbackState == .paused {
                        MPMusicPlayerController.systemMusicPlayer.play()
                        isHighlighted = true
                    } else if AVAudioSession.sharedInstance().isOtherAudioPlaying {
                        try! AVAudioSession.sharedInstance().setActive(true, options: .notifyOthersOnDeactivation)
                        isHighlighted = false
                    } else {
                        try! AVAudioSession.sharedInstance().setActive(false, options: .notifyOthersOnDeactivation)
                        isHighlighted = true
                    }
                    callback(isHighlighted)
                },
                swipeLeftAction: (() -> Void)? = {
                    if MPMusicPlayerController.systemMusicPlayer.playbackState == .playing {
                        MPMusicPlayerController.systemMusicPlayer.skipToPreviousItem()
                    }
                },
                swipeRightAction: (() -> Void)? = {
                    if MPMusicPlayerController.systemMusicPlayer.playbackState == .playing {
                        MPMusicPlayerController.systemMusicPlayer.skipToNextItem()
                    }
                },
                volumeIcon: UIImage? = nil,
                pauseIcon: UIImage? = nil,
                playIcon: UIImage? = nil,
                swipeLeftIcon: UIImage? = nil,
                swipeRightIcon: UIImage? = nil
    ) {
        super.init()
        self.tapAction = tapAction
        self.swipeLeftAction = swipeLeftAction
        self.swipeRightAction = swipeRightAction
        self.volumeIcon = volumeIcon
        self.pauseIcon = pauseIcon
        self.playIcon = playIcon
        self.swipeLeftIcon = swipeLeftIcon
        self.swipeRightIcon = swipeRightIcon
        guard let hostViewController = { hostViewController ?? UIApplication.shared.windows.first?.rootViewController }() else {
            print("Failed to setup Gesturedeck")
            return
        }
        self.hostViewController = hostViewController
        volumeSlider = makeInvisibleVolumeSlider(on: hostViewController)
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) { [weak self] in    //Delay observing because the slider has an initial value of 0 and then it updates to the actual volume value
            self?.volumeSlider?.addTarget(self, action: #selector(self?.volumeChanged), for: .valueChanged)
        }
        self.addGestureRecognizers(with: hostViewController.view)
    }
    
    @objc private func volumeChanged() {
        //        lastYPan = 0
        overlayGestureViewController.animateAppearance(on: hostViewController)
        let panPoint = CGPoint(x: 0, y: hostViewController.view.bounds.height)
        let gestureViewController = GestureViewController(
            gestureType: .pan(volumeBarOriginY: panPoint.y, newVolumeLevel: volumeSlider.value),
            volumeIcon: volumeIcon
        )
        gestureViewController.animateAppearance(on: hostViewController, appeared: { [weak self] in
            guard let self = self else { return }
            gestureViewController.animateDisappearance(from: self.hostViewController)
            self.overlayGestureViewController.animateDisappearance(from: self.hostViewController)
        })
    }
    
    func addGestureRecognizers(with view: UIView) {
        //        print(view)
        panGestureRecognizer.delegate = self
        panGestureRecognizer.cancelsTouchesInView = false
        panGestureRecognizer.minimumNumberOfTouches = 2
        view.addGestureRecognizer(panGestureRecognizer)
        
        leftSwipeGestureRecognizer.delegate = self
        leftSwipeGestureRecognizer.direction = .left
        leftSwipeGestureRecognizer.cancelsTouchesInView = false
        leftSwipeGestureRecognizer.numberOfTouchesRequired = 2
        view.addGestureRecognizer(leftSwipeGestureRecognizer)
        
        rightSwipeGestureRecognizer.delegate = self
        rightSwipeGestureRecognizer.direction = .right
        rightSwipeGestureRecognizer.cancelsTouchesInView = false
        rightSwipeGestureRecognizer.numberOfTouchesRequired = 2
        view.addGestureRecognizer(rightSwipeGestureRecognizer)
        
        dualTapGestureRecognizer.delegate = self
        //        dualTapGestureRecognizer.cancelsTouchesInView = false // Needs to be disabled for Flutter
        dualTapGestureRecognizer.delaysTouchesBegan = false
        dualTapGestureRecognizer.numberOfTouchesRequired = 2
        view.addGestureRecognizer(dualTapGestureRecognizer)
        
        let doubleTapGestureRecognizer = UILongPressGestureRecognizer(target: self, action: #selector(longPressed))
        doubleTapGestureRecognizer.minimumPressDuration = 0.1
        doubleTapGestureRecognizer.numberOfTapsRequired = 1
        doubleTapGestureRecognizer.delegate = self
        doubleTapGestureRecognizer.cancelsTouchesInView = false
        view.addGestureRecognizer(doubleTapGestureRecognizer)
        
        let twoFingerOnScreenGestureRecognizer = UILongPressGestureRecognizer(target: self, action: #selector(longPressed))
        twoFingerOnScreenGestureRecognizer.minimumPressDuration = 0.1
        twoFingerOnScreenGestureRecognizer.numberOfTouchesRequired = 2
        twoFingerOnScreenGestureRecognizer.delegate = self
        twoFingerOnScreenGestureRecognizer.cancelsTouchesInView = false
        view.addGestureRecognizer(twoFingerOnScreenGestureRecognizer)
        
        //        view.addGestureRecognizer(GSTouchesShowingGestureRecognizer(overlayView: overlayGestureViewController.view) )
    }
    
    @objc func panned(gestureRecognizer: UIPanGestureRecognizer) {
        switch gestureRecognizer.state {
        case .began:
            lastYPan = 0
            let panPoint: CGPoint = gestureRecognizer.location(in: hostViewController.view)
            gestureViewController = GestureViewController(
                gestureType: .pan(volumeBarOriginY: panPoint.y, newVolumeLevel: volumeSlider.value),
                volumeIcon: volumeIcon
            )
            overlayGestureViewController.animateAppearance(on: hostViewController)
            gestureViewController?.animateAppearance(on: hostViewController)
        case .changed:
            let YPan: Float = Float(gestureRecognizer.translation(in: hostViewController.view).y)
            guard abs(abs(lastYPan) - abs(YPan)) > 20 else { return }
            let newVolume = volumeSlider.value - copysign(volumeStep, YPan - lastYPan)
            if (0...1).contains(newVolume) {    // Only set new volume if it is within range
                volumeSlider.value = newVolume
                volumeSlider.sendActions(for: .touchUpInside)
            }
            gestureViewController?.setVolume(with: YPan, volumeLevel: volumeSlider.value)
            lastYPan = YPan
        default:
            //All fingers are lifted.
            if let gestureViewController = gestureViewController {
                overlayGestureViewController.animateDisappearance(from: hostViewController)
                gestureViewController.animateDisappearance(from: hostViewController)
            }
        }
    }
    
    @objc func swipedLeft() {
        overlayGestureViewController.animateAppearance(on: hostViewController)
        swipeLeftAction?()
        GestureViewController(
            gestureType: .swipeLeft,
            swipeIcon: swipeLeftIcon
        ).animateAppearance(on: hostViewController, appeared: { [weak self] in
            guard let self = self else { return }
            self.overlayGestureViewController.animateDisappearance(from: self.hostViewController)
        })
    }
  
  @objc func swipedRight() {
      overlayGestureViewController.animateAppearance(on: hostViewController)
      swipeRightAction?()
      GestureViewController(
          gestureType: .swipeRight,
          swipeIcon: swipeRightIcon
      ).animateAppearance(on: hostViewController, appeared: { [weak self] in
          guard let self = self else { return }
          self.overlayGestureViewController.animateDisappearance(from: self.hostViewController)
      })
  }
    
    @objc func tapped() {
        overlayGestureViewController.animateAppearance(on: hostViewController)
        tapAction?() { [weak self] isHighlighted in
            guard let self = self else { return }
            GestureViewController(
                gestureType: .tap(isHighlighted: isHighlighted),
                pauseIcon: self.pauseIcon,
                playIcon: self.playIcon
            ).animateAppearance(on: self.hostViewController, appeared: { [weak self] in
                guard let self = self else { return }
                self.overlayGestureViewController.animateDisappearance(from: self.hostViewController)
            })
        }
    }
    
    @objc func longPressed(_ sender: UILongPressGestureRecognizer) {
        if sender.state == .began {
            UniHaptic(style: .selection).vibrate(intensity: 0.7, sharpness: 0.7)
            overlayGestureViewController.animateAppearance(on: hostViewController)
            panGestureRecognizer.minimumNumberOfTouches = 1
            dualTapGestureRecognizer.numberOfTouchesRequired = 1
//            leftSwipeGestureRecognizer.numberOfTouchesRequired = 1  // Causes memory related crashes
        } else if sender.state == .ended || sender.state == .cancelled || sender.state == .failed {
            if sender.numberOfTouches > 1 {
                overlayGestureViewController.animateDisappearance(from: hostViewController)
            }
            panGestureRecognizer.minimumNumberOfTouches = 2
            dualTapGestureRecognizer.numberOfTouchesRequired = 2
//            leftSwipeGestureRecognizer.numberOfTouchesRequired = 2  // Causes memory related crashes
        }
    }
    
    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        if (gestureRecognizer is UITapGestureRecognizer && otherGestureRecognizer is UIPanGestureRecognizer) ||
            (gestureRecognizer is UIPanGestureRecognizer && otherGestureRecognizer is UITapGestureRecognizer) ||
            (gestureRecognizer is UITapGestureRecognizer && otherGestureRecognizer is UISwipeGestureRecognizer) ||
            (gestureRecognizer is UISwipeGestureRecognizer && otherGestureRecognizer is UITapGestureRecognizer) {
            return false
        }
        
        return true
    }
    
    public func gestureRecognizerShouldBegin(_ panGestureRecognizer: UIGestureRecognizer) -> Bool {
        if let panGestureRecognizer = panGestureRecognizer as? UIPanGestureRecognizer {
            let velocity: CGPoint = panGestureRecognizer.velocity(in: hostViewController.view) //TODO: Replace rootViewController?.view with overlay view
            return abs(velocity.y) > abs(velocity.x)
        }
        else {
            return true
        }
    }
}

extension Gesturedeck {
    fileprivate func makeInvisibleVolumeSlider(on viewController: UIViewController) -> UISlider {
        func makeInvisibleVolumeView() -> MPVolumeView {
            let volumeView = MPVolumeView(frame: .zero)
            volumeView.frame.origin.x = -1000
            volumeView.clipsToBounds = true
            return volumeView
        }
        
        let volumeView = makeInvisibleVolumeView()
        viewController.view.addSubview(volumeView)
        return volumeView.subviews.filter{NSStringFromClass($0.classForCoder) == "MPVolumeSlider"}.first as! UISlider
    }
}
