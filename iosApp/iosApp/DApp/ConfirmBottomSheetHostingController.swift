//
//  ConfirmBottomSheetHostingController.swift
//  iosApp
//
//  Created by Ethan Nguyen on 06/07/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import AlphaWalletFoundation
import composeApp
import PanModal

protocol ConfirmBottomSheetDelegate {
    func onFail(callbackId: Int)
    
    func onSuccess(callbackId: Int, hexMsg: String)
}

class ConfirmBottomSheetHostingController: UIViewController {
    
    private var transaction: UnconfirmedTransaction
    private var transactionDetails: TransactionDetails
    private var hostingController: UIViewController? = nil
    
    var delegate: ConfirmBottomSheetDelegate?
    
    private func toAddress(transaction: UnconfirmedTransaction) -> String {
        switch transaction.transactionType {
        case .nativeCryptocurrency:
            return transaction.recipient?.eip55String ?? ""
        case .erc20Token, .erc875Token, .erc721Token, .erc721ForTicketToken, .erc1155Token, .prebuilt:
            return transaction.contract?.eip55String ?? ""
        }
    }
    
    private func getAmount(transaction: UnconfirmedTransaction) -> String {
        switch transaction.transactionType {
        case .nativeCryptocurrency: return transaction.value.data.toString() ?? ""
        case .erc20Token: return 0.toString()
        case .erc875Token: return 0.toString()
        case .erc721Token: return 0.toString()
        case .erc721ForTicketToken: return 0.toString()
        case .erc1155Token: return 0.toString()
        case .prebuilt: return transaction.value.data.toString() ?? ""
        }
    }
    
    init(transaction: UnconfirmedTransaction, transactionDetails: TransactionDetails) {
        self.transaction = transaction
        self.transactionDetails = transactionDetails
        
        super.init(nibName: nil, bundle: nil)
        
//        let recipient = self.toAddress(transaction: transaction)
        let recipient = toAddress(transaction: self.transaction)
        var amount = self.getAmount(transaction: transaction)
        if(amount.isEmpty){
            amount = "0"
        }
        
        hostingController = MainViewControllerKt.confirmTransactionController(
            url: self.transactionDetails.url,
            accountId: self.transactionDetails.accountId,
            coinDecimals: Int64(self.transactionDetails.coinDecimals),
            chainId: Int64(self.transactionDetails.chainId),
            callbackId: Int64(self.transactionDetails.callbackId),
            value: amount,
            recipient: recipient,
            payload: self.transaction.data.hexEncoded,
            nonce: Int64(self.transaction.nonce ?? 0),
            isLegacyTransaction: true,
            onSignMessageFail: {
                    // Handle sign message failure here.
                print("sign message fail")
                self.showAlert(message: "Verify transaction failure. Please try again.") {
                    self.dismiss(animated: true, completion: nil)
                }
//                DispatchQueue.main.async {
//                    self.showToast(message: "Verify transaction failure. Please try again.")
//                }
//                self.dismiss(animated: true, completion: nil)
                },
            onSignMessageSuccessful: { callbackId, signHex in
                // callbackId and signHex are available here.
                // You can use them to handle the successful signing of the message.
                self.delegate?.onSuccess(callbackId: transactionDetails.callbackId, hexMsg: signHex)
//                DispatchQueue.main.async {
//                    print("Sign message successful. callbackId: \(callbackId) signHex: \(signHex)")
//                    // Other code here...
//                }
            },
            onConfirm: {
                kotlinBoolean in
                    DispatchQueue.main.async {
//                        self.unlockSuccess = kotlinBoolean.boolValue
                        print("1991 onConfirm \(kotlinBoolean.boolValue)")
                        if(kotlinBoolean.boolValue){
//                            self.isShortFormEnabled = false
//                            self.panModalSetNeedsLayoutUpdate()
//                            self.panModalTransition(to: .shortForm)
                        }else{
//                            self.isShortFormEnabled = true
//                            self.panModalSetNeedsLayoutUpdate()
//                            self.panModalTransition(to: .shortForm)
                        }

                    }

            },
            onDecline: {
                    self.delegate?.onFail(callbackId: transactionDetails.callbackId)
                    self.dismiss(animated: true, completion: nil)
                }
            )
       
    }
    
    func showAlert(message: String, completion: @escaping () -> Void) {
        DispatchQueue.main.async {
            let alert = UIAlertController(title: nil, message: message, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { _ in
                completion()
            }))
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
//    let confirmTransactionViewModel = koin.confirmTransactionViewModel
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
       
        var recipient = toAddress(transaction: transaction)
        var amount = getAmount(transaction: transaction)
        print("1991 transaction type \(transaction.transactionType)")
        print("1991 recipient \(recipient)")
        print("1991 amount oke \(amount)")
        if(amount.isEmpty){
            amount = "0"
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
//        view.setNeedsLayout()
        
        if hostingController?.parent == nil {
            addChild(hostingController!)
//            composeController.view.frame = view.bounds
            hostingController?.view.backgroundColor = UIColor.clear
            
            view.addSubview(hostingController!.view)
            
            view.backgroundColor = UIColor.clear
            
            hostingController?.view.translatesAutoresizingMaskIntoConstraints = false
            hostingController?.view.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
            hostingController?.view.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
            hostingController?.view.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
            hostingController?.view.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
                    
            
            hostingController?.didMove(toParent: self)
        }
    }
    
    @State private var unlockSuccess: Bool = false

    
    override func dismiss(animated flag: Bool, completion: (() -> Void)? = nil) {
        super.dismiss(animated: flag, completion: completion)
            print("dismiss now")
            // Your code to handle the dismissal
        self.delegate?.onFail(callbackId: transactionDetails.callbackId)
    }

    
    // MARK: - Pan Modal Presentable
//
//    var panScrollable: UIScrollView? {
//        return nil
//    }
//
////    var longFormHeight: PanModalHeight {
////        return .intrinsicHeight
////    }
//    
//    var isShortFormEnabled = true
//    
//    var shortFormHeight: PanModalHeight {
//        return isShortFormEnabled ? .contentHeight(480.0) : longFormHeight
//    }
//
//    var anchorModalToLongForm: Bool {
//        return false
//    }
//
//    var shouldRoundTopCorners: Bool {
//        return true
//    }
//    
//    func willTransition(to state: PanModalPresentationController.PresentationState) {
//        guard isShortFormEnabled, case .longForm = state
//            else { return }
//
//        isShortFormEnabled = false
//        panModalSetNeedsLayoutUpdate()
//    }
//    
//    func panModalDidDismiss(){
//        self.delegate?.onFail(callbackId: transactionDetails.callbackId)
//    }

}

extension UIViewController {
    func showToast(message : String) {
        let toastLabel = UILabel(frame: CGRect(x: self.view.frame.size.width/2 - 150, y: self.view.frame.size.height-100, width: 300, height: 35))
        toastLabel.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        toastLabel.textColor = UIColor.white
        toastLabel.textAlignment = .center;
        toastLabel.font = UIFont(name: "Montserrat-Light", size: 12.0)
        toastLabel.text = message
        toastLabel.alpha = 1.0
        toastLabel.layer.cornerRadius = 10;
        toastLabel.clipsToBounds  =  true

        if let window = UIApplication.shared.windows.first(where: { $0.isKeyWindow }) {
            window.rootViewController?.view.addSubview(toastLabel)
            UIView.animate(withDuration: 4.0, delay: 0.1, options: .curveEaseOut, animations: {
                toastLabel.alpha = 0.0
            }, completion: {(isCompleted) in
                toastLabel.removeFromSuperview()
            })
        }
    }
}

//extension ConfirmBottomSheetHostingController: UIAdaptivePresentationControllerDelegate {
//    func presentationControllerDidDismiss(_ presentationController: UIPresentationController) {
//        // This method is called after the view controller is dismissed.
//        print("Dismissed ConfirmBottomSheetHostingController")
//        // Here you can call your delegate method or handle the dismissal.
//        self.delegate?.onFail(callbackId: transactionDetails.callbackId)
//    }
//}


class CustomNavigationController: UINavigationController, UIAdaptivePresentationControllerDelegate {
    var onDismiss: (() -> Void)?

    func presentationControllerDidDismiss(_ presentationController: UIPresentationController) {
        print("Dismissed ConfirmBottomSheetHostingController")
        onDismiss?()
    }
}
