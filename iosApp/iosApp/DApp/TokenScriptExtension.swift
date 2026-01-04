//
//  TokenScriptExtension.swift
//  iosApp
//
//  Created by Ethan Nguyen on 05/07/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import AlphaWalletAddress
import AlphaWalletCore
import AlphaWalletFoundation
import AlphaWalletLogger

extension TokenScript {
    static let baseTokenScriptFiles: [TokenType: String] = [
        .erc20: (try! String(contentsOf: R.file.erc20TokenScriptTsml()!)),
        .erc721: (try! String(contentsOf: R.file.erc721TokenScriptTsml()!)),
    ]
}
