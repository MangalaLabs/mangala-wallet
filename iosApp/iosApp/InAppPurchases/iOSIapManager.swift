//
//  IAPManager.swift
//  iosApp
//
//  Created by Macgala on 10/12/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import StoreKit
import composeApp

class iOSIapManager: IAPManager {
    
    private var onPurchaseFlowEmit: (PaymentInfo) -> Void = { _ in }
    private var onBillingResultEmit: (BillingResult) -> Void = { _ in }

    var products = [Product]()
    static let shared = iOSIapManager()
    private var updates: Task<Void, Never>?
    @Published private(set) var activeTransactions: Set<StoreKit.Transaction> = []
    
    init() {
        updates = Task {
            for await update in StoreKit.Transaction.updates {
                switch update {
                case .verified(let transaction):
                    if let uuid = transaction.appAccountToken {
                        
                    }
                    let paymentInfo = PaymentInfo(
                        orderId: transaction.originalID.formatted(),
                        packageName: nil,
                        productId: transaction.productID,
                        purchaseTime: KotlinLong(value: Int64(transaction.purchaseDate.timeIntervalSince1970)),
                        purchaseState: KotlinInt(int: PurchaseState.purchased.value),
                        purchaseToken: update.jwsRepresentation,
                        quantity: nil,
                        acknowledged: false,
                        obfuscatedAccountId: nil,
                        obfuscatedProfileId: transaction.appAccountToken?.uuidString
                    )
                    onPurchaseFlowEmit(paymentInfo)
                case .unverified(let transsaction, let verificationError):
                    print("Unverified transaction \(verificationError.localizedDescription)")
                }
            }
        }
    }
    
    func finishTransaction(purchaseToken: String, purchaseId: String) async throws {
        let transactions = StoreKit.Transaction.unfinished
        
        let transaction = await transactions.first(where: { update in
            switch update {
            case .verified(let transaction):
                do {
                    let payloadValue = try update.payloadValue
                    if update.jwsRepresentation == purchaseToken || String(payloadValue.id) == purchaseId {
                        await transaction.finish()
                        return true
                    } else {
                        return false
                    }
                } catch {
                    print("Error accessing payloadValue: \(error)")
                    return false
                }
                
            case .unverified(_ , _):
                print("Unverified transaction")
                return false
            }
        })
    }
    
    func initialize(onPurchaseFlowEmit: @escaping (PaymentInfo) -> Void, onBillingResultEmit: @escaping (BillingResult) -> Void) {
        self.onPurchaseFlowEmit = onPurchaseFlowEmit
        self.onBillingResultEmit = onBillingResultEmit
    }
    
    func getPurchaseStatus(purchaseToken: String, purchaseId: String) async throws -> KotlinInt {
        // Need to get all transactions because currentEntitlements does not include consumables
        let allTransactions = StoreKit.Transaction.unfinished
        
        let transaction = await allTransactions.first(where: { transaction in
            print("Transaction \(transaction.jwsRepresentation)")

            do {
                let payloadValue = try transaction.payloadValue
                print("Transaction json \(String(describing: String(data: transaction.payloadData, encoding: .utf8)))")
                return transaction.jwsRepresentation == purchaseToken || String(payloadValue.id) == purchaseId
            } catch {
                print("Error accessing payloadValue: \(error)")
                return false
            }
        })
        
        if (transaction != nil) {
            return KotlinInt(int: Int32(PURCHASE_STATE_PURCHASED))
        } else {
            return KotlinInt(int: Int32(PURCHASE_STATE_PENDING))
        }
    }
    
    func getPurchases(isPremiumAccount: Bool) async throws -> [PaymentInfo] {
        let allTransactions = StoreKit.Transaction.unfinished
        
        let productId = if (isPremiumAccount) {
            eosPremiumAccountProductId
        } else {
            eosAccountProductId
        }
            
        let transactionsFromProduct = allTransactions.filter {
            do {
                let payloadValue = try $0.payloadValue
                return payloadValue.productID == productId
            } catch {
                return false
            }
        }
        
        var paymentInfo: [PaymentInfo] = []
        
        for await transaction in transactionsFromProduct {
           do {
               let payloadValue = try transaction.payloadValue

               let info = PaymentInfo(
                   orderId: String(payloadValue.id),
                   packageName: nil,
                   productId: payloadValue.productID,
                   purchaseTime: KotlinLong(value: Int64(payloadValue.purchaseDate.timeIntervalSince1970)),
                   purchaseState: KotlinInt(int: PurchaseState.purchased.value),
                   purchaseToken: transaction.jwsRepresentation,
                   quantity: nil,
                   acknowledged: false,
                   obfuscatedAccountId: nil,
                   obfuscatedProfileId: payloadValue.appAccountToken?.uuidString
               )

               paymentInfo.append(info)
           } catch {
               print("Error fetching purchases data \(error)")
           }
       }

        return paymentInfo
    }
    
    func launchPurchaseFlow(productId: String, userUuid: String) {
        Task {
            guard let product = self.products.first(where: { product in
                product.id == productId
            }) else { return }
            
            do {
                try await purchase(product, userUuid: userUuid)
            } catch {
                print("An error occurred when launching the purchase flow")
            }
        }
    }
    
    func loadProduct(isPremiumAccount: Bool) async throws -> (any IapProduct)? {
        do {
            guard let product = try await fetchProductsAsync(isPremiumAccount: isPremiumAccount) else {
                return nil
            }
            
            products = [product]
            
            return iOSIapProduct(product: product)
        } catch {
            print("iOSIAPManager error \(error)")
            return nil
        }
    }
        
    func fetchProductsAsync(isPremiumAccount: Bool) async throws -> Product? {
        var productIds = [String]()
        
        if isPremiumAccount {
            productIds = [eosPremiumAccountProductId]
        } else {
            productIds = [eosAccountProductId]
        }
        
        return try await Product.products(for: productIds).first
    }
    
    private func purchase(_ product: Product, userUuid: String) async {
        do {
            let uuid = UUID(uuidString: userUuid)!
            
            let result = try await product.purchase(
                options: [.appAccountToken(uuid)]
            )
            
            switch result {
            case .success(let verificationResult):
                switch verificationResult {
                case .verified(let transaction):
                    let paymentInfo = PaymentInfo(
                        orderId: transaction.originalID.formatted(),
                        packageName: nil,
                        productId: transaction.productID,
                        purchaseTime: KotlinLong(value: Int64(transaction.purchaseDate.timeIntervalSince1970)),
                        purchaseState: KotlinInt(int: PurchaseState.purchased.value),
                        purchaseToken: verificationResult.jwsRepresentation,
                        quantity: nil,
                        acknowledged: false,
                        obfuscatedAccountId: nil,
                        obfuscatedProfileId: userUuid
                    )
                    print("iOS purchase \(String(describing: transaction.appAccountToken?.uuidString))")
                    onPurchaseFlowEmit(paymentInfo)
                case .unverified(_, let verificationError):
                    print("Unverified transaction \(verificationError.localizedDescription)")
                    onBillingResultEmit(BillingResult(responseCode: .error))
                }
            case .userCancelled:
                onBillingResultEmit(BillingResult(responseCode: .userCanceled))
            case .pending:
                let paymentInfo = PaymentInfo(
                    orderId: nil,
                    packageName: nil,
                    productId: product.id,
                    purchaseTime: nil,
                    purchaseState: KotlinInt(int: PurchaseState.pending.value),
                    purchaseToken: nil,
                    quantity: nil,
                    acknowledged: false,
                    obfuscatedAccountId: nil,
                    obfuscatedProfileId: userUuid
                )
                onPurchaseFlowEmit(paymentInfo)
            @unknown default:
                break
            }
        } catch {
            if let storeKitError = error as? StoreKitError {
                switch storeKitError {
                case .networkError(let underlyingError):
                    print("Network error: \(underlyingError.localizedDescription)")
                    onBillingResultEmit(BillingResult(responseCode: .networkError))
                case .notAvailableInStorefront:
                    print("The product is not available in the current storefront.")
                    onBillingResultEmit(BillingResult(responseCode: .itemUnavailable))
                case .userCancelled:
                    print("User cancelled the purchase.")
                    onBillingResultEmit(BillingResult(responseCode: .userCanceled))
                case .unknown:
                    print("An unknown error occurred")
                    onBillingResultEmit(BillingResult(responseCode: .error))
                case .notEntitled:
                    print("App not entitled for purchase")
                    onBillingResultEmit(BillingResult(responseCode: .error))
                case .systemError(let error):
                    print("System error \(error.localizedDescription)")
                    onBillingResultEmit(BillingResult(responseCode: .serviceUnavailable))
                default:
                    print("StoreKit error: \(storeKitError.localizedDescription)")
                    onBillingResultEmit(BillingResult(responseCode: .error))
                }
            } else {
                print("Purchase failed with error: \(error.localizedDescription)")
                onBillingResultEmit(BillingResult(responseCode: .error))
            }
        }
    }
    
    func fetchActiveTransactions() async -> Set<StoreKit.Transaction> {
        var activeTransactions: Set<StoreKit.Transaction> = []
        
        for await entitlement in StoreKit.Transaction.currentEntitlements {
            if let transaction = try? entitlement.payloadValue {
                activeTransactions.insert(transaction)
            }
        }
        
        return activeTransactions
    }
    
    deinit {
        updates?.cancel()
    }
    
    private let eosAccountProductId = "premium_eos_account"
    private let eosPremiumAccountProductId = "premium_eos_account_pro"
    
    private let PURCHASE_STATE_PURCHASED = 0
    private let PURCHASE_STATE_PENDING = 1
}
