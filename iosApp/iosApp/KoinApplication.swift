//
//  KoinApplication.swift
//  iosApp
//
//  Created by Ethan Nguyen on 11/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import composeApp
import Foundation

func startKoin() {
    let iapManager = iOSIapManager()
    let koinApplication = ShareModuleIOSKt.doInitKoinIos(iapManager: iapManager)
    
    _koin = koinApplication.koin
}

private var _koin: Koin_coreKoin? = nil
var koin: Koin_coreKoin {
    return _koin!
}

//typealias KoinApplication = Koin_coreKoinApplication
//typealias Koin = Koin_coreKoin
//
//extension KoinApplication {
//    static let shared = companion.start()
//
//    @discardableResult
//    static func start() -> KoinApplication {
//        shared
//    }
//}
//
//extension KoinApplication {
//    private static let keyPaths: [PartialKeyPath<Koin>] = [
//    ]
//
//    static func inject<T>() -> T {
//        shared.inject()
//    }
//
//    func inject<T>() -> T {
//        for partialKeyPath in Self.keyPaths {
//            guard let keyPath = partialKeyPath as? KeyPath<Koin, T> else { continue }
//            return koin[keyPath: keyPath]
//        }
//
//        fatalError("\(T.self) is not registered with KoinApplication")
//    }
//}
//
//@propertyWrapper
//struct LazyKoin<T> {
//    lazy var wrappedValue: T = { KoinApplication.shared.inject() }()
//
//    init() { }
//}
