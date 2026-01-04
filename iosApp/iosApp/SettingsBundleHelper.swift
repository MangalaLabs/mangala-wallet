//
//  SettingsBundleHelper.swift
//  iosApp
//
//  Created by Ethan Nguyen on 11/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation

class SettingsBundleHelper {
    
    private static let UseComposeKey = "use_compose_preference"
    
    class func initialize() {
        UserDefaults.standard.register(defaults: [UseComposeKey : true])
    }
    
    class func getUseComposeValue() -> Bool {
        return UserDefaults.standard.bool(forKey: UseComposeKey)
    }
    
    class func setUseComposeValue(newValue: Bool) {
        UserDefaults.standard.set(newValue, forKey: UseComposeKey)
    }
}
