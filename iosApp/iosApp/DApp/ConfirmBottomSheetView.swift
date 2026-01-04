//
//  ConfirmBottomSheetView.swift
//  iosApp
//
//  Created by Ethan Nguyen on 06/07/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import AlphaWalletFoundation

struct TransactionDetails {
    var url: String
    var accountId: String
    var coinDecimals: Int
    var chainId: Int
    var callbackId: Int
}

struct ConfirmBottomSheetView: View {
    var transactionDetails: TransactionDetails?
    var transaction: UnconfirmedTransaction?
    var onConfirm: () -> Void
    var onDecline: () -> Void
    
    var body: some View {
        VStack {
            HStack {
                Text("Verify transaction via")
                    .font(.title2)
                Spacer()
                // Image("ic_close_dapp")
                //     .resizable()
                //     .frame(width: 24, height: 24)
            }
            Spacer()
                .frame(height: 4)
            Text("exchange.biswap.org")
                .font(.title2)
                .foregroundColor(.blue)
            Spacer()
                .frame(height: 4)
            Text("0.123 BNB ~ $25")
                .font(.title2)
            Spacer()
                .frame(height: 4)
            VStack {
                Text("From")
                    .font(.subheadline)
                Text("My wallet")
                    .font(.caption)
                Text("0x4a824e...a60b")
                    .font(.body)
                Spacer().frame(height: 8)
                Text("To")
                    .font(.subheadline)
                VStack {
                    Text("0x4a824e...a60b")
                        .font(.body)
                    Spacer()
                        .frame(height: 8)
                    Text("On")
                        .font(.subheadline)
                    HStack {
                        // Image("network_icon")
                        //     .resizable()
                        //     .frame(width: 24, height: 24)
                        Spacer()
                            .frame(width: 4)
                        Text("Ethereum")
                            .font(.body)
                    }
                    Spacer()
                        .frame(height: 28)
                    Text("Fee:")
                        .font(.caption)
                        .foregroundColor(.black)
                    Spacer()
                        .frame(height: 24)
                    HStack {
                        Text("$0.57 -> ")
                            .font(.caption)
                            .foregroundColor(.black)
                        // Image("image_left_arrow")
                        //     .resizable()
                        //     .frame(width: 24, height: 24)
                        // Image("image_vehicle")
                        //     .resizable()
                        //     .frame(width: 24, height: 24)
                        Spacer()
                            .frame(width: 2)
                        Text("7 min")
                            .font(.caption)
                            .foregroundColor(.black)
                    }
                    .padding(EdgeInsets(top: 4, leading: 6, bottom: 4, trailing: 6))
                    .background(Color.gray.opacity(0.2))
                    .cornerRadius(4)
                }
                Text("0.000024 BNB")
                    .font(.caption)
                    .foregroundColor(.black)
                HStack {
                    Button(action: onDecline) {
                        Text("Decline")
                            .foregroundColor(.black)
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.red)
                    .cornerRadius(8)
                    Spacer()
                    Button(action: onConfirm) {
                        Text("Confirm")
                            .foregroundColor(.white)
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.green)
                    .cornerRadius(8)
                }
            }
        }
        .foregroundColor(.black)
        .background(Color.white)
    }
}
