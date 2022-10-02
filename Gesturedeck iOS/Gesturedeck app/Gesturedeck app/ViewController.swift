//
//  ViewController.swift
//  Gesturedeck app
//
//  Created by Fotios Dimanidis on 18.09.22.
//

import UIKit

class ViewController: UIViewController {
    @IBAction func buttonPress(_ sender: Any) {
        if view.backgroundColor == UIColor.white {
            view.backgroundColor = UIColor.black
        } else {
            view.backgroundColor = UIColor.white
        }
    }
    
  override func viewDidLoad() {
    super.viewDidLoad()
    // Do any additional setup after loading the view.
  }


}

