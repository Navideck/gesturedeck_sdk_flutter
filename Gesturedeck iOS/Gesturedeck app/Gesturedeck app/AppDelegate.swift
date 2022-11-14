//
//  AppDelegate.swift
//  Gesturedeck app
//
//  Created by Fotios Dimanidis on 18.09.22.
//

import UIKit
import GesturedeckiOS

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    var gesturedeck: Gesturedeck?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        
        
        
        return true
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        gesturedeck = Gesturedeck()
        gesturedeck?.tapAction = { callback in
            print("tapped")
            callback(true)  // Without callback no icon is displayed
        }

        gesturedeck?.swipeLeftAction = {
            print("swiped Left")
        }

        gesturedeck?.swipeRightAction = {
            print("swiped Right")
        }
    }
    
    // MARK: UISceneSession Lifecycle
    
    @available(iOS 13.0, *)
    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }
    
    @available(iOS 13.0, *)
    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }
}

