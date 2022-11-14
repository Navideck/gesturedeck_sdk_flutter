//
//  GestureViewController.swift
//  TunedeckAppleMusic
//
//  Created by Foti Dim on 27.10.17.
//  Copyright © 2017 Navideck. All rights reserved.
//

import UIKit

enum GestureType: Equatable {
    case pan(volumeBarOriginY: CGFloat, newVolumeLevel: Float)
    case swipeLeft
    case swipeRight
    case tap(isHighlighted: Bool)
}

class GestureViewController: UIViewController, UIGestureRecognizerDelegate {
    
    @IBOutlet weak var volumeHUD: UIView!
    @IBOutlet weak var volumeBar: UIView?
    @IBOutlet weak var volumeLabel: UILabel!
    
    @IBOutlet weak var skipHUD: UIView!
    
    @IBOutlet weak var pauseHUD: UIView!
    
    @IBOutlet weak var volumeIconOutlet: UIImageView?
    @IBOutlet weak var swipeIconOutlet: UIImageView?
    @IBOutlet weak var pauseIconOutlet: UIImageView?
    
    @IBOutlet weak var volumeIcon: UIImage?
    @IBOutlet weak var swipeIcon: UIImage?
    @IBOutlet weak var playIcon: UIImage?
    @IBOutlet weak var pauseIcon: UIImage?
    
    var panGestureInProgress: Bool {
        return volumeBar?.frame.height != 0
    }
    
    var gestureType: GestureType?
    
    init(gestureType: GestureType? = nil,
         volumeIcon: UIImage? = nil,
         pauseIcon: UIImage? = nil,
         playIcon: UIImage? = nil,
         swipeIcon: UIImage? = nil
    ) {
        self.gestureType = gestureType
        super.init(nibName: "GestureViewController", bundle: BundleHelper.bundle)
    }
    
    required init?(coder aDecoder: NSCoder) {
        self.gestureType = .tap(isHighlighted: false) //giving a default gestureType in case this initializer is used
        super.init(coder: aDecoder)
    }
    
    override func willTransition(to newCollection: UITraitCollection, with coordinator: UIViewControllerTransitionCoordinator) {
        adjustForNotch()
        super.willTransition(to: newCollection, with: coordinator)
    }
    
    func setVolume(with distance: Float, volumeLevel: Float){
        guard let volumeBar = volumeBar else { return }
        var volumeBarY = volumeBar.frame.origin.y
        if distance < 0 {
            volumeBarY += volumeBar.frame.size.height
        }
        volumeLabel.text = String(format: "%1.1f", round(volumeLevel * 10 * 2.0) / 2.0) //round to 0 and 0.5
        if volumeLevel < 1.00 - volumeStep && volumeLevel > volumeStep {
            UIView.animate(withDuration: 0.1, animations: {() -> Void in
                self.volumeBar?.frame = CGRect(x: volumeBar.frame.origin.x, y: volumeBarY, width: volumeBar.frame.size.width, height: CGFloat(distance))
                //TODO: Check if calling animation continuously is correct
            })
        } else {
            UniHaptic(style: .impact).vibrate(intensity: 1.0, sharpness: 1.0)
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        loadIcons()
        super.viewWillAppear(animated)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        switch gestureType {
        case let .pan(volumeBarOriginY, newVolumeLevel):
            volumeBar?.frame.size.height = 0
            volumeBar?.frame.origin.y = volumeBarOriginY
            volumeLabel.text = String(format: "%1.1f", round(newVolumeLevel * 10 * 2.0) / 2.0) //round to 0 and 0.5
            volumeHUD.isHidden = false
        case .swipeLeft, .swipeRight:
            self.skipHUD.isHidden = false
            UIView.animate(withDuration: 0.7, animations: {
                self.skipHUD.alpha = 0.0
                self.skipHUD.transform = CGAffineTransform(scaleX: 3,y: 3)
            }, completion: { (animationsCompleted) in
                if let parent = self.parent{
                    self.animateDisappearance(from: parent)
                }
            })
        case .tap(isHighlighted: let isHighlighted):
            pauseIconOutlet?.isHighlighted = isHighlighted
            self.pauseHUD.isHidden = false
            UIView.animate(withDuration: 0.7, animations: {
                self.pauseHUD.alpha = 0.0
                self.pauseHUD.transform = CGAffineTransform(scaleX: 3,y: 3)
            }, completion: { (animationsCompleted) in
                if let parent = self.parent{
                    self.animateDisappearance(from: parent)
                }
            })
        case .none:
            break
        }
    }
    
    override func viewDidLayoutSubviews() {
        adjustForNotch()
        super.viewDidLayoutSubviews()
    }
    
    private func loadIcons() {
        if let volumeIcon = volumeIcon {
            self.volumeIconOutlet?.image = volumeIcon
        }
        if let pauseIcon = pauseIcon {
            self.pauseIconOutlet?.image = pauseIcon
        }
        if let playIcon = playIcon {
            self.pauseIconOutlet?.highlightedImage = playIcon
        } else {
            self.pauseIconOutlet?.highlightedImage = pauseIcon
        }
        if let swipeIcon = swipeIcon {
            self.swipeIconOutlet?.image = swipeIcon
        } else if gestureType == .swipeRight {
            self.swipeIconOutlet?.image = UIImage(named: "icon_skip", in: BundleHelper.bundle, compatibleWith: nil)
        } else {
            
            let image = UIImage(named: "icon_skip", in: BundleHelper.bundle, compatibleWith: nil)
            self.swipeIconOutlet?.image = UIImage(cgImage: image!.cgImage!, scale: image!.scale, orientation: .down)
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /*
     // MARK: - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
     override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
     // Get the new view controller using segue.destinationViewController.
     // Pass the selected object to the new view controller.
     }
     */
    
    private func adjustForNotch() {
        guard let volumeBar = volumeBar else {
            return
        }
        if #available(iOS 13.0, *) {
            if (self.view.window?.windowScene?.interfaceOrientation == .landscapeRight) {
                var volumeBarX: CGFloat = 0
                
                volumeBarX = view.frame.width - volumeBar.frame.width
                
                volumeBar.frame.origin.x = volumeBarX
            }
        }
    }
}
