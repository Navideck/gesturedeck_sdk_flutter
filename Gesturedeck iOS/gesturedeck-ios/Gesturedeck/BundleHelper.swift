//
//  BundleHelper.swift
//  GesturedeckSDK
//
//  Created by Fotios Dimanidis on 14.11.22.
//

import Foundation

struct BundleHelper {
    static var bundle: Bundle {
        get {
            let podBundle = Bundle(for: GestureViewController.self)
            let bundleURL = podBundle.url(forResource: "GesturedeckSDKBundle", withExtension: "bundle")
            return (bundleURL != nil ? Bundle(url: bundleURL!) : podBundle) ?? Bundle.main
        }
    }
}
