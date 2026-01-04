//
//  WKWebViewConfiguration.swift
//  DuckDuckGo
//
//  Copyright © 2023 DuckDuckGo. All rights reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

import WebKit
import AlphaWalletFoundation


extension WKWebViewConfiguration {

//    public static func persistent() -> WKWebViewConfiguration {
//        return configuration(persistsData: true)
//    }
//
//    public static func nonPersistent() -> WKWebViewConfiguration {
//        return configuration(persistsData: false)
//    }

    private static func configuration(persistsData: Bool) -> WKWebViewConfiguration {
        let configuration = WKWebViewConfiguration()
        if !persistsData {
            configuration.websiteDataStore = WKWebsiteDataStore.nonPersistent()
        }

        // Telephone numbers can be still be interacted with by selecting on them and using the popover menu
        configuration.dataDetectorTypes = []

        configuration.allowsAirPlayForMediaPlayback = true
        configuration.allowsInlineMediaPlayback = true
        configuration.allowsPictureInPictureMediaPlayback = true
        configuration.ignoresViewportScaleLimits = true
        configuration.preferences.isFraudulentWebsiteWarningEnabled = false

        return configuration
    }

    
    public static func injectJS(
        to configuration: WKWebViewConfiguration,
        isTokenScriptRenderer: Bool,
        rpcUrl: String,
        chainId: Int,
        address: String,
        messageHandler: WKScriptMessageHandler
    ){
        var js = ""
        if(isTokenScriptRenderer){
            js += javaScriptForTokenScriptRenderer(address: address)
            js += """
                  \n
                  web3.tokens = {
                      data: {
                          currentInstance: {
                          },
                          token: {
                          },
                          card: {
                          },
                      },
                      dataChanged: (old, updated, tokenCardId) => {
                        console.log(\"web3.tokens.data changed. You should assign a function to `web3.tokens.dataChanged` to monitor for changes like this:\\n    `web3.tokens.dataChanged = (old, updated, tokenCardId) => { //do something }`\")
                      }
                  }
                  """
        }else{
            guard
                    let bundlePath = Bundle.main.path(forResource: "AlphaWalletWeb3Provider", ofType: "bundle"),
                    let bundle = Bundle(path: bundlePath) else { return }
            if let filepath = bundle.path(forResource: "AlphaWallet-min", ofType: "js") {
                do {
                    js += try String(contentsOfFile: filepath)
                } catch { }
            }
            print("1991 path \(js.count)")
            let dapp = javaScriptForDappBrowser(rpcUrl: rpcUrl, chainId: chainId, address: address)
            print("1991 dapp \(dapp.count)")
            js += dapp
        }

        print("1991 js \(js.count)")
        let userScript = WKUserScript(source: js, injectionTime: .atDocumentStart, forMainFrameOnly: false)
        configuration.userContentController.addUserScript(userScript)

        if(isTokenScriptRenderer){
            configuration.setURLSchemeHandler(configuration, forURLScheme: "tokenscript-resource")
        }

        HackToAllowUsingSafaryExtensionCodeInDappBrowser.injectJs(to: configuration)
        configuration.userContentController.add(messageHandler, name: Method.sendTransaction.rawValue)
        configuration.userContentController.add(messageHandler, name: Method.signTransaction.rawValue)
        configuration.userContentController.add(messageHandler, name: Method.signPersonalMessage.rawValue)
        configuration.userContentController.add(messageHandler, name: Method.signMessage.rawValue)
        configuration.userContentController.add(messageHandler, name: Method.signTypedMessage.rawValue)
        configuration.userContentController.add(messageHandler, name: Method.ethCall.rawValue)
        configuration.userContentController.add(messageHandler, name: AddCustomChainCommand.Method.walletAddEthereumChain.rawValue)
        configuration.userContentController.add(messageHandler, name: SwitchChainCommand.Method.walletSwitchEthereumChain.rawValue)
        configuration.userContentController.add(messageHandler, name: Browser.locationChangedEventName)
        //TODO extract like `Method.signTypedMessage.rawValue` when we have more than 1
        configuration.userContentController.add(messageHandler, name: TokenScript.SetProperties.setActionProps)
    }

    // swiftlint:disable function_body_length
    public static func javaScriptForDappBrowser(rpcUrl: String, chainId: Int, address: String) -> String {
        print("1991 server.web3InjectedRpcURL.absoluteString \(rpcUrl.count)")
        print("1991 address.eip55String \(address.count)")
        print("1991 server.chainID \(chainId)")
        return """
               //Space is needed here because it is sometimes cut off by websites.


               const addressHex = "\(address)"
               const rpcURL = "\(rpcUrl)"
               const chainID = "\(chainId)"

               function executeCallback (id, error, value) {
                   AlphaWallet.executeCallback(id, error, value)
               }

               AlphaWallet.init(rpcURL, {
                   getAccounts: function (cb) { cb(null, [addressHex]) },
                   processTransaction: function (tx, cb){
                       console.log('signing a transaction', tx)
                       const { id = 8888 } = tx
                       AlphaWallet.addCallback(id, cb)
                       webkit.messageHandlers.sendTransaction.postMessage({"name": "sendTransaction", "object":     tx, id: id})
                   },
                   signMessage: function (msgParams, cb) {
                       const { data } = msgParams
                       const { id = 8888 } = msgParams
                       console.log("signing a message", msgParams)
                       AlphaWallet.addCallback(id, cb)
                       webkit.messageHandlers.signMessage.postMessage({"name": "signMessage", "object": { data }, id:    id} )
                   },
                   signPersonalMessage: function (msgParams, cb) {
                       const { data } = msgParams
                       const { id = 8888 } = msgParams
                       console.log("signing a personal message", msgParams)
                       AlphaWallet.addCallback(id, cb)
                       webkit.messageHandlers.signPersonalMessage.postMessage({"name": "signPersonalMessage", "object":  { data }, id: id})
                   },
                   signTypedMessage: function (msgParams, cb) {
                       const { data } = msgParams
                       const { id = 8888 } = msgParams
                       console.log("signing a typed message", msgParams)
                       AlphaWallet.addCallback(id, cb)
                       webkit.messageHandlers.signTypedMessage.postMessage({"name": "signTypedMessage", "object":     { data }, id: id})
                   },
                   ethCall: function (msgParams, cb) {
                       const data = msgParams
                       const { id = Math.floor((Math.random() * 100000) + 1) } = msgParams
                       console.log("eth_call", msgParams)
                       AlphaWallet.addCallback(id, cb)
                       webkit.messageHandlers.ethCall.postMessage({"name": "ethCall", "object": data, id: id})
                   },
                   walletAddEthereumChain: function (msgParams, cb) {
                       const data = msgParams
                       const { id = Math.floor((Math.random() * 100000) + 1) } = msgParams
                       console.log("walletAddEthereumChain", msgParams)
                       AlphaWallet.addCallback(id, cb)
                       webkit.messageHandlers.walletAddEthereumChain.postMessage({"name": "walletAddEthereumChain", "object": data, id: id})
                   },
                   walletSwitchEthereumChain: function (msgParams, cb) {
                       const data = msgParams
                       const { id = Math.floor((Math.random() * 100000) + 1) } = msgParams
                       console.log("walletSwitchEthereumChain", msgParams)
                       AlphaWallet.addCallback(id, cb)
                       webkit.messageHandlers.walletSwitchEthereumChain.postMessage({"name": "walletSwitchEthereumChain", "object": data, id: id})
                   },
                   enable: function() {
                      return new Promise(function(resolve, reject) {
                          //send back the coinbase account as an array of one
                          resolve([addressHex])
                      })
                   }
               }, {
                   address: addressHex,
                   networkVersion: "0x" + parseInt(chainID).toString(16) || null
               })

               web3.setProvider = function () {
                   console.debug('AlphaWallet Wallet - overrode web3.setProvider')
               }

               web3.eth.defaultAccount = addressHex

               web3.version.getNetwork = function(cb) {
                   cb(null, chainID)
               }

              web3.eth.getCoinbase = function(cb) {
               return cb(null, addressHex)
             }
             window.ethereum = web3.currentProvider

             // So we can detect when sites use History API to generate the page location. Especially common with React and similar frameworks
             ;(function() {
               var pushState = history.pushState;
               var replaceState = history.replaceState;

               history.pushState = function() {
                 pushState.apply(history, arguments);
                 window.dispatchEvent(new Event('locationchange'));
               };

               history.replaceState = function() {
                 replaceState.apply(history, arguments);
                 window.dispatchEvent(new Event('locationchange'));
               };

               window.addEventListener('popstate', function() {
                 window.dispatchEvent(new Event('locationchange'))
               });
             })();

             window.addEventListener('locationchange', function(){
               webkit.messageHandlers.\(Browser.locationChangedEventName).postMessage(window.location.href)
             })
             """
        }
    // swiftlint:enable function_body_length


    public static func javaScriptForTokenScriptRenderer(address: String) -> String {
        return """
               window.web3CallBacks = {}
               window.tokenScriptCallBacks = {}

               function executeCallback (id, error, value) {
                   window.web3CallBacks[id](error, value)
                   delete window.web3CallBacks[id]
               }

               function executeTokenScriptCallback (id, error, value) {
                   let cb = window.tokenScriptCallBacks[id]
                   if (cb) {
                       window.tokenScriptCallBacks[id](error, value)
                       delete window.tokenScriptCallBacks[id]
                   } else {
                   }
               }

               web3 = {
                 personal: {
                   sign: function (msgParams, cb) {
                     const { data } = msgParams
                     const { id = 8888 } = msgParams
                     window.web3CallBacks[id] = cb
                     webkit.messageHandlers.signPersonalMessage.postMessage({"name": "signPersonalMessage", "object":  { data }, id: id})
                   }
                 },
                 action: {
                   setProps: function (object, cb) {
                     const id = 8888
                     window.tokenScriptCallBacks[id] = cb
                     webkit.messageHandlers.\(TokenScript.SetProperties.setActionProps).postMessage({"object":  object, id: id})
                   }
                 }
               }
               """
    }

    fileprivate static func contentBlockingRulesJson() -> String {
        //TODO read from TokenScript, when it's designed and available
        let whiteListedUrls = [
            "https://unpkg.com/",
            "^tokenscript-resource://",
            "^http://stormbird.duckdns.org:8080/api/getChallenge$",
            "^http://stormbird.duckdns.org:8080/api/checkSignature"
        ]
        //Blocks everything, except the whitelisted URL patterns
        var json = """
                   [
                       {
                           "trigger": {
                               "url-filter": ".*"
                           },
                           "action": {
                               "type": "block"
                           }
                       }
                   """
        for each in whiteListedUrls {
            json += """
                    ,
                    {
                        "trigger": {
                            "url-filter": "\(each)"
                        },
                        "action": {
                            "type": "ignore-previous-rules"
                        }
                    }
                    """
        }
        json += "]"
        return json
    }
}


class CustomURLSchemeHandler: NSObject, WKURLSchemeHandler {
    public func webView(_ webView: WKWebView, start urlSchemeTask: WKURLSchemeTask) {
        if urlSchemeTask.request.url?.path != nil {
            if let fileExtension = urlSchemeTask.request.url?.pathExtension, fileExtension == "otf", let nameWithoutExtension = urlSchemeTask.request.url?.deletingPathExtension().lastPathComponent {
                //TODO maybe good to fail with didFailWithError(error:)
                guard let url = Bundle.main.url(forResource: nameWithoutExtension, withExtension: fileExtension) else { return }
                guard let data = try? Data(contentsOf: url) else { return }
                //mimeType doesn't matter. Blocking is done based on how browser intends to use it
                let response = URLResponse(url: urlSchemeTask.request.url!, mimeType: "font/opentype", expectedContentLength: data.count, textEncodingName: nil)
                urlSchemeTask.didReceive(response)
                urlSchemeTask.didReceive(data)
                urlSchemeTask.didFinish()
                return
            }
        }
        //TODO maybe good to fail:
        //urlSchemeTask.didFailWithError(error:)
    }

    public func webView(_ webView: WKWebView, stop urlSchemeTask: WKURLSchemeTask) {
        //Do nothing
    }
}

public struct HackToAllowUsingSafaryExtensionCodeInDappBrowser {
    private static func javaScriptForSafaryExtension() -> String {
        var js = String()

        if let filepath = Bundle.main.path(forResource: "config", ofType: "js"), let content = try? String(contentsOfFile: filepath) {
            js += content
        }
        if let filepath = Bundle.main.path(forResource: "helpers", ofType: "js"), let content = try? String(contentsOfFile: filepath) {
            js += content
        }
        return js
    }

    static func injectJs(to webViewConfig: WKWebViewConfiguration) {
        func encodeStringTo64(fromString: String) -> String? {
            let plainData = fromString.data(using: .utf8)
            return plainData?.base64EncodedString(options: [])
        }
        var js = javaScriptForSafaryExtension()
        js += """
                const overridenElementsForAlphaWalletExtension = new Map();
                function runOnStart() {
                    function applyURLsOverriding(options, url) {
                        let elements = overridenElementsForAlphaWalletExtension.get(url);
                        if (typeof elements != 'undefined') {
                            overridenElementsForAlphaWalletExtension(elements)
                        }

                        overridenElementsForAlphaWalletExtension.set(url, retrieveAllURLs(document, options));
                    }

                    const url = document.URL;
                    applyURLsOverriding(optionsByDefault, url);
                }

                if(document.readyState !== 'loading') {
                    runOnStart();
                } else {
                    document.addEventListener('DOMContentLoaded', function() {
                        runOnStart()
                    });
                }
        """

        let jsStyle = """
            javascript:(function() {
            var parent = document.getElementsByTagName('body').item(0);
            var script = document.createElement('script');
            script.type = 'text/javascript';
            script.innerHTML = window.atob('\(encodeStringTo64(fromString: js)!)');
            parent.appendChild(script)})()
        """

        let userScript = WKUserScript(source: jsStyle, injectionTime: .atDocumentEnd, forMainFrameOnly: false)
        webViewConfig.userContentController.addUserScript(userScript)
    }
}
