//
//  GestureViewController.swift
//  TunedeckAppleMusic
//
//  Created by Foti Dim on 27.10.17.
//  Copyright © 2017 Navideck. All rights reserved.
//

import UIKit

class GestureViewControllerOverlay: UIViewController {
    
    init(gestureType: GestureType? = nil) {
        super.init(nibName: "GestureViewControllerOverlay", bundle: Bundle(for: GestureViewController.self))
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
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
