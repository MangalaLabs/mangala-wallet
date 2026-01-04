//
//  OpenBrowserViewModel.swift
//  iosApp
//
//  Created by Ethan Nguyen on 15/07/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import composeApp

class OpenBrowserViewModel: ObservableObject, OpenBrowserClick {
    
    @Published var isOpenBrowser: Bool = false
    @Published var chainId: Int = 1
    @Published var address: String = ""
    @Published var rpcUrl: String = ""
    @Published var accountId: String = ""
    
   func openBrowserAction(isClick: Bool) {
       isOpenBrowser = isClick
   }

   func openBrowser(chainId: Int64, address: String, rpcUrl: String, accountId: String){
       print("2000 OpenBrowserViewModel \(address)")
       self.chainId = Int(chainId)
       self.address = address
       self.rpcUrl = rpcUrl
       self.accountId = accountId
   }
}
