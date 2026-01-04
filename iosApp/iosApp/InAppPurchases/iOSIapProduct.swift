//
//  iOSIapProduct.swift
//  iosApp
//
//  Created by Macgala on 10/12/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import composeApp
import StoreKit

class iOSIapProduct: IapProduct {
    private let product: Product
    
    lazy var formattedPrice: String? = self.product.displayPrice
    lazy var productId: String = self.product.id
    
    init(product: Product) {
        self.product = product
    }
}
