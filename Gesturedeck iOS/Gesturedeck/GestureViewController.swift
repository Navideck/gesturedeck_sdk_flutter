//
//  GestureViewController.swift
//  TunedeckAppleMusic
//
//  Created by Foti Dim on 27.10.17.
//  Copyright © 2017 Navideck. All rights reserved.
//

import UIKit

enum GestureType {
    case pan(volumeBarOriginY: CGFloat, newVolumeLevel: Float)
    case swipe
    case tap(isHighlighted: Bool)
}

class GestureViewController: UIViewController, UIGestureRecognizerDelegate {

    @IBOutlet weak var volumeHUD: UIView!
    @IBOutlet weak var volumeBar: UIView?
    @IBOutlet weak var volumeLabel: UILabel!
    
    @IBOutlet weak var skipHUD: UIView!

    @IBOutlet weak var pauseHUD: UIView!
    
    @IBOutlet weak var volumeIcon: UIImageView?
    @IBOutlet weak var pauseIcon: UIImageView?
    @IBOutlet weak var skipIcon: UIImageView?
    
    var panGestureInProgress: Bool {
        return volumeBar?.frame.height != 0
    }
    
    var gestureType: GestureType?

    init(gestureType: GestureType? = nil,
         volumeIcon: UIImage? = nil,
         pauseIcon: UIImage? = nil,
         playIcon: UIImage? = nil,
         skipIcon: UIImage? = nil
    ) {
        self.gestureType = gestureType
        if let volumeIcon = volumeIcon {
            self.volumeIcon?.image = volumeIcon
        }
        if let pauseIcon = pauseIcon {
            self.pauseIcon?.image = pauseIcon
        }
        if let playIcon = playIcon {
            self.pauseIcon?.highlightedImage = playIcon
        } else {
            self.pauseIcon?.highlightedImage = self.pauseIcon?.image
        }
        if let skipIcon = skipIcon {
            self.skipIcon?.image = skipIcon
        }
        super.init(nibName: "GestureViewController", bundle: Bundle(for: GestureViewController.self))
    }
    
    required init?(coder aDecoder: NSCoder) {
        self.gestureType = .tap(isHighlighted: false) //giving a default gestureType in case this initializer is used
        super.init(coder: aDecoder)
    }
    
    func setVolume(with barHeight: Float, volumeLevel: Float){
        guard let volumeBar = volumeBar else { return }
        var VolumeBarY = volumeBar.frame.origin.y
        if barHeight < 0 {
            VolumeBarY += volumeBar.frame.size.height
        }
        volumeLabel.text = String(format: "%1.1f", round(volumeLevel * 10 * 2.0) / 2.0) //round to 0 and 0.5
        if volumeLevel != 0 && volumeLevel != 1 {
            UIView.animate(withDuration: 0.1, animations: {() -> Void in
                self.volumeBar?.frame = CGRect(x: 0, y: VolumeBarY, width: volumeBar.frame.size.width, height: CGFloat(barHeight))
                //TODO: Check if calling animation continuously is correct
            })
        } else {
            UniHaptic(style: .impact).vibrate(intensity: 1.0, sharpness: 1.0)
        }
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
        case .swipe:
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
            pauseIcon?.isHighlighted = isHighlighted
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
}
