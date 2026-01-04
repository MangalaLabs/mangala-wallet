//
//  TabViewController.swift
//  DuckDuckGo
//
//  Copyright © 2017 DuckDuckGo. All rights reserved.
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
import UIKit
import StoreKit
import os.log
import BrowserServicesKit
import SwiftUI
import AlphaWalletFoundation
import AlphaWalletCore
import Combine
import JavaScriptCore
import PromiseKit
import AlphaWalletLogger
import composeApp

// swiftlint:disable file_length
// swiftlint:disable type_body_length
class TabViewController: UIViewController {
// swiftlint:enable type_body_length

    private struct Constants {
        static let frameLoadInterruptedErrorCode = 102
        
        static let trackerNetworksAnimationDelay: TimeInterval = 0.7
        
        static let secGPCHeader = "Sec-GPC"
    }
    
    @IBOutlet private(set) weak var error: UIView!
    @IBOutlet private(set) weak var errorInfoImage: UIImageView!
    @IBOutlet private(set) weak var errorHeader: UILabel!
    @IBOutlet private(set) weak var errorMessage: UILabel!
    @IBOutlet weak var webViewContainer: UIView!
    
    @IBOutlet var showBarsTapGestureRecogniser: UITapGestureRecognizer!
    var longPressGestureRecognizer: UILongPressGestureRecognizer?
    
    private let instrumentation = TabInstrumentation()

    var isLinkPreview = false
    
    var openedByPage = false
    weak var openingTab: TabViewController? {
        didSet {
            delegate?.tabLoadingStateDidChange(tab: self)
        }
    }
    
    weak var delegate: TabDelegate?
    weak var chromeDelegate: BrowserChromeDelegate?
    
    var findInPage: FindInPage? {
        get { return findInPageScript.findInPage }
        set { findInPageScript.findInPage = newValue }
    }
    
    let progressWorker = WebProgressWorker()

    private(set) var webView: WKWebView!
    private lazy var appRatingPrompt: AppRatingPrompt = AppRatingPrompt()
    private weak var privacyController: PrivacyProtectionController?
    
    private(set) lazy var appUrls: AppUrls = AppUrls()
    private var storageCache: StorageCache = AppDependencyProvider.shared.storageCache.current
    private lazy var appSettings = AppDependencyProvider.shared.appSettings
    
    internal lazy var featureFlagger = AppDependencyProvider.shared.featureFlagger
    private lazy var featureFlaggerInternalUserDecider = AppDependencyProvider.shared.featureFlaggerInternalUserDecider
    
    lazy var bookmarksManager = BookmarksManager()

    private(set) var siteRating: SiteRating?
    private(set) var tabModel: Tab
    
    private let requeryLogic = RequeryLogic()
    
    private var httpsForced: Bool = false
    private var lastUpgradedURL: URL?
    private var lastError: Error?
    private var shouldReloadOnError = false
    private var failingUrls = Set<String>()
    
    private var trackerNetworksDetectedOnPage = Set<String>()
    private var pageHasTrackers = false
    
    private var detectedLoginURL: URL?
    private var preserveLoginsWorker: PreserveLoginsWorker?
    
    private var trackersInfoWorkItem: DispatchWorkItem?

    // If no trackers dax dialog was shown recently in this tab, ie without the user navigating somewhere else, e.g. backgrounding or tab switcher
    private var woShownRecently = false

    // Temporary to gather some data.  Fire a follow up if no trackers dax dialog was shown and then trackers appear.
    private var fireWoFollowUp = false
    
    // In certain conditions we try to present a dax dialog when one is already showing, so check to ensure we don't
    var isShowingFullScreenDaxDialog = false
    
    var temporaryDownloadForPreviewedFile: Download?
    var mostRecentAutoPreviewDownloadID: UUID?
    
    var contentBlockingAssetsInstalled: Bool {
        ContentBlocking.contentBlockingManager.currentTDSRules != nil
    }
    
    let userAgentManager: UserAgentManager = DefaultUserAgentManager.shared

    public var url: URL? {
        didSet {
            updateTabModel()
            delegate?.tabLoadingStateDidChange(tab: self)
            checkLoginDetectionAfterNavigation()
        }
    }
    
    override var title: String? {
        didSet {
            updateTabModel()
            delegate?.tabLoadingStateDidChange(tab: self)
        }
    }
    
    public var canGoBack: Bool {
        let webViewCanGoBack = webView.canGoBack
        let navigatedToError = webView.url != nil && isError
        return webViewCanGoBack || navigatedToError || openingTab != nil
    }
    
    public var canGoForward: Bool {
        let webViewCanGoForward = webView.canGoForward
        return webViewCanGoForward && !isError
    }
    
    public var isError: Bool {
        return !error.isHidden
    }
    
    public var errorText: String? {
        return errorMessage.text
    }
    
    public var link: Link? {
        if isError {
            if let url = url ?? webView.url ?? URL(string: "") {
                return Link(title: errorText, url: url)
            }
        }
        
        guard let url = url else {
            return tabModel.link
        }
        
        let activeLink = Link(title: title, url: url)
        guard let storedLink = tabModel.link else {
            return activeLink
        }
        
        return activeLink.merge(with: storedLink)
    }
    
    private var faviconScript = FaviconUserScript()
    private var loginFormDetectionScript = LoginFormDetectionUserScript()
    private var fingerprintScript = FingerprintUserScript()
    private var navigatorPatchScript = NavigatorSharePatchUserScript()
    private var doNotSellScript = DoNotSellUserScript()
    private var documentScript = DocumentUserScript()
    private var findInPageScript = FindInPageUserScript()
    private var fullScreenVideoScript = FullScreenVideoUserScript()
    private var printingUserScript = PrintingUserScript()
    private var textSizeUserScript = TextSizeUserScript()
    private lazy var autofillUserScript = createAutofillUserScript()
    private var debugScript = DebugUserScript()
    private var shouldBlockJSAlert = false

    lazy var emailManager: EmailManager = {
        let emailManager = EmailManager()
        emailManager.aliasPermissionDelegate = self
        emailManager.requestDelegate = self
        return emailManager
    }()
    
    lazy var vaultManager: SecureVaultManager = {
        let manager = SecureVaultManager()
        manager.delegate = self
        return manager
    }()
    
    private static let debugEvents = EventMapping<AMPProtectionDebugEvents> { event, _, _, _, onComplete in
        let domainEvent: Pixel.Event
        switch event {
        case .ampBlockingRulesCompilationFailed:
            domainEvent = .ampBlockingRulesCompilationFailed
            Pixel.fire(pixel: domainEvent,
                       withAdditionalParameters: [:],
                       onComplete: onComplete)
        }
    }
    
    private lazy var linkProtection: LinkProtection = {
        LinkProtection(privacyManager: ContentBlocking.privacyConfigurationManager,
                       contentBlockingManager: ContentBlocking.contentBlockingManager,
                       errorReporting: Self.debugEvents)

    }()
    
    private var userScripts: [UserScript] = []
    
    private var canDisplayJavaScriptAlert: Bool {
        return !shouldBlockJSAlert && presentedViewController == nil && delegate?.tabCheckIfItsBeingCurrentlyPresented(self) ?? false
    }
    
    private var rulesCompiledCondition: RunLoop.ResumeCondition? = RunLoop.ResumeCondition()
    private let rulesCompilationMonitor = RulesCompilationMonitor.shared
    
    static func loadFromStoryboard(model: Tab) -> TabViewController {
        let storyboard = UIStoryboard(name: "Tab", bundle: nil)
        guard let controller = storyboard.instantiateViewController(withIdentifier: "TabViewController") as? TabViewController else {
            fatalError("Failed to instantiate controller as TabViewController")
        }
        controller.tabModel = model
        return controller
    }
    
    private var isAutofillEnabled: Bool {
        appSettings.autofill && featureFlagger.isFeatureOn(.autofill)
    }
    
    let isTokenScriptRenderer = false
    var rpcUrl = "https://bsc-dataseed.binance.org"
    var chainId = 56
//    let address = "0x7e30a18803d88364ab53f735b4050237f3e02c8"
    var address = "0x28c661d27cb5ed500f538cabe7a551dd966a336e"
    var accountId = "0x28c661d27cb5ed500f538cabe7a551dd966a336e"
//    let address = "0xCFe578f1709831d7BF77Dc4202CF2ea01993E9Ed"

    required init?(coder aDecoder: NSCoder) {
        tabModel = Tab(link: nil)
        super.init(coder: aDecoder)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
//        startSession()
        preserveLoginsWorker = PreserveLoginsWorker(controller: self)
        initUserScripts()
        applyTheme(ThemeManager.shared.currentTheme)
        addContentBlockerConfigurationObserver()
        addStorageCacheProviderObserver()
        addLoginDetectionStateObserver()
        addDoNotSellObserver()
        addTextSizeObserver()
        addDuckDuckGoEmailSignOutObserver()
        addAutofillEnabledObserver()
        registerForNotifications()
    }
    
//    func startSession(){
//        if(self.address.isEmpty == false){
//            let address = AlphaWallet.Address.make(address: address)
//            let  wallet: Wallet = Wallet(address: address, origin: .hd)
//            let config: Config = Config()
//            let analytics = AnalyticsService()
//
//            let serversProvider = BaseServersProvider(config: config)
//            let blockchainsProvider: BlockchainsProvider = BlockchainsProvider(
//                    serversProvider: serversProvider,
//                    blockchainFactory: BaseBlockchainFactory(
//                        config: config,
//                        analytics: analytics))
//            let networkService = BaseNetworkService(analytics: analytics)
//            let assetDefinitionStore = AssetDefinitionStore(
//                baseTokenScriptFiles: TokenScript.baseTokenScriptFiles,
//                networkService: networkService,
//                blockchainsProvider: blockchainsProvider)
//
//            let tokensDataStore: TokensDataStore = MultipleChainsTokensDataStore(store: .storage(for: wallet))
//            let eventsDataStore: NonActivityEventsDataStore = NonActivityMultiChainEventsDataStore(store: .storage(for: wallet))
//
//            let reachability = ReachabilityManager()
//            let apiTransporterFactory = ApiTransporterFactory()
//
//            sessionsProvider = BaseSessionsProvider(
//                config: config,
//                analytics: analytics,
//                blockchainsProvider: blockchainsProvider,
//                tokensDataStore: tokensDataStore,
//                eventsDataStore: eventsDataStore,
//                assetDefinitionStore: assetDefinitionStore,
//                reachability: reachability,
//                wallet: wallet,
//                apiTransporterFactory: apiTransporterFactory)
//
//            sessionsProvider?.start()
//            os_log("1991 sessionsProvider start")
//        }
//
//        switch chainId {
//            case 1: server = .main
//            case 61: server = .classic
//            case 104729: server = .callisto
//            case 100: server = .xDai
//            case 5: server = .goerli
//            case 56: server = .binance_smart_chain
//            case 97: server = .binance_smart_chain_testnet
//            case 128: server = .heco
//            case 256: server = .heco_testnet
//            case 250: server = .fantom
//            case 0xfa2: server = .fantom_testnet
//            case 0xa86a: server = .avalanche
//            case 0xa869: server = .avalanche_testnet
//            case 137: server = .polygon
//            case 80001: server = .mumbai_testnet
//            case 10: server = .optimistic
//            case 338: server = .cronosTestnet
//            case 25: server = .cronosMainnet
//            case 42161: server = .arbitrum
//            case 11297108109: server = .palm
//            case 11297108099: server = .palmTestnet
//            case 8217: server = .klaytnCypress
//            case 1001: server = .klaytnBaobabTestnet
//            case 4689: server = .ioTeX
//            case 4690: server = .ioTeXTestnet
//            case 420: server = .optimismGoerli
//            case 421613: server = .arbitrumGoerli
//            case 66: server = .okx
//            case 11155111: server = .sepolia
//        default:
//            server = .main
//        }
//
//    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        woShownRecently = false // don't fire if the user goes somewhere else first
        resetNavigationBar()
        delegate?.tabDidRequestShowingMenuHighlighter(tab: self)
        tabModel.viewed = true
    }

    override func buildActivities() -> [UIActivity] {
        var activities: [UIActivity] = [SaveBookmarkActivity(controller: self)]

        activities.append(SaveBookmarkActivity(controller: self, isFavorite: true))
        activities.append(FindInPageActivity(controller: self))

        return activities
    }
    
    // swiftlint:disable function_body_length
    func initUserScripts() {
        
        let currentTDSRules = ContentBlocking.contentBlockingManager.currentTDSRules
        let privacyConfig = ContentBlocking.privacyConfigurationManager.privacyConfig
        
        let contentBlockerConfig = DefaultContentBlockerUserScriptConfig(privacyConfiguration: privacyConfig,
                                                                         trackerData: currentTDSRules?.trackerData,
                                                                         ctlTrackerData: nil,
                                                                         trackerDataManager: ContentBlocking.trackerDataManager)
        let contentBlockerRulesScript = ContentBlockerRulesUserScript(configuration: contentBlockerConfig)
        
        let surrogates = FileStore().loadAsString(forConfiguration: .surrogates) ?? ""
        let surrogatesConfig = DefaultSurrogatesUserScriptConfig(privacyConfig: privacyConfig,
                                                                 surrogates: surrogates,
                                                                 trackerData: currentTDSRules?.trackerData,
                                                                 encodedSurrogateTrackerData: currentTDSRules?.encodedTrackerData,
                                                                 trackerDataManager: ContentBlocking.trackerDataManager,
                                                                 isDebugBuild: isDebugBuild)
        let surrogatesScript = SurrogatesUserScript(configuration: surrogatesConfig)
        autofillUserScript = createAutofillUserScript()
        
        userScripts = [
            debugScript,
            textSizeUserScript,
            findInPageScript,
            navigatorPatchScript,
            surrogatesScript,
            contentBlockerRulesScript,
            fingerprintScript,
            faviconScript,
            fullScreenVideoScript,
            autofillUserScript,
            printingUserScript
        ]
        
        if PreserveLogins.shared.loginDetectionEnabled {
            loginFormDetectionScript.delegate = self
            userScripts.append(loginFormDetectionScript)
        }
        
        if appSettings.sendDoNotSell {
            userScripts.append(doNotSellScript)
        }
        
        faviconScript.delegate = self
        debugScript.instrumentation = instrumentation
        surrogatesScript.delegate = self
        contentBlockerRulesScript.delegate = self
        autofillUserScript.emailDelegate = emailManager
        autofillUserScript.vaultDelegate = vaultManager
        printingUserScript.delegate = self
        textSizeUserScript.textSizeAdjustmentInPercents = appSettings.textSize
    }
    // swiftlint:enable function_body_length
    
    private func createAutofillUserScript() -> BrowserServicesKit.AutofillUserScript {
        let prefs = ContentScopeProperties(gpcEnabled: appSettings.sendDoNotSell,
                                           sessionKey: UUID().uuidString,
                                           featureToggles: ContentScopeFeatureToggles.supportedFeaturesOniOS)
        let provider = DefaultAutofillSourceProvider(privacyConfigurationManager: ContentBlocking.privacyConfigurationManager, properties: prefs)
        let autofillUserScript = AutofillUserScript(scriptSourceProvider: provider)
        return autofillUserScript
    }
    
    private func refreshAutofillUserScript() {
        autofillUserScript = createAutofillUserScript()
    }
    
    func updateTabModel() {
        if let url = url {
            tabModel.link = Link(title: title, url: url)
        } else {
            tabModel.link = nil
        }
    }
        
    @objc func onApplicationWillResignActive() {
        shouldReloadOnError = true
    }
    
    private func bind(){
        let output = transform()
        output.dappAction
            .sink { [weak self] data in
                guard let strongSelf = self else { return }
                strongSelf.didCall(action: data.action, callbackId: data.callbackId)
            }.store(in: &cancellable)
    }
    
    private func transform() -> BrowserViewModelOutput {
        return .init(
            universalLink: universalLinkSubject.eraseToAnyPublisher(),
            dappAction: dappActionSubject.eraseToAnyPublisher())
    }
    
    var sessionsProvider: SessionsProvider? = nil
    
    private func didCall(action: DappAction, callbackId: Int) {
        guard let session = sessionsProvider?.session(for: server) else {
            os_log("1991 didCall notifyFinish")
            notifyFinish(callbackId: callbackId, value: .failure(.requestRejected))
            return
        }

        func rejectDappAction() {
            notifyFinish(callbackId: callbackId, value: .failure(JsonRpcError.requestRejected))
//            navigationController.topViewController?.displayError(error: ActiveWalletViewModel.Error.onlyWatchAccount)
        }

//        switch wallet.type {
//        case .real, .hardware:
//            performDappAction(action: action, callbackId: callbackId, session: session, delegate: delegate)
//        case .watch:
//            if config.development.shouldPretendIsRealWallet {
//                performDappAction(action: action, callbackId: callbackId, session: session, delegate: delegate)
//            } else {
//                switch action {
//                case .signTransaction, .sendTransaction, .signMessage, .signPersonalMessage, .signTypedMessage, .signEip712v3And4, .unknown, .sendRawTransaction:
//                    rejectDappAction()
//                case .walletAddEthereumChain, .walletSwitchEthereumChain, .ethCall:
//                    performDappAction(action: action, callbackId: callbackId, session: session, delegate: delegate)
//                }
//            }
//        }
        
//        switch action {
//        case .signTransaction, .sendTransaction, .signMessage, .signPersonalMessage, .signTypedMessage, .signEip712v3And4, .unknown, .sendRawTransaction:
//            rejectDappAction()
//        case .walletAddEthereumChain, .walletSwitchEthereumChain, .ethCall:
            performDappAction(action: action, callbackId: callbackId, session: session)
//        }
    }
    
    private func performDappAction(action: DappAction,
                                   callbackId: Int,
                                   session: WalletSession) {
        switch action {
        case .signTransaction(let unconfirmedTransaction):
            requestSignTransaction(
                session: session,
                callbackId: callbackId,
                transaction: unconfirmedTransaction)
        case .sendTransaction(let unconfirmedTransaction):
            requestSendTransaction(
                session: session,
                callbackId: callbackId,
                transaction: unconfirmedTransaction)
        case .signMessage(let hexMessage):
            requestSignMessage(
                session: session,
                message: .message(hexMessage.asSignableMessageData),
                callbackId: callbackId)
        case .signPersonalMessage(let hexMessage):
            requestSignMessage(
                session: session,
                message: .personalMessage(hexMessage.asSignableMessageData),
                callbackId: callbackId)
        case .signTypedMessage(let typedData):
            requestSignMessage(
                session: session,
                message: .typedMessage(typedData),
                callbackId: callbackId)
        case .signEip712v3And4(let typedData):

            requestSignMessage(
                session: session,
                message: .eip712v3And4(typedData),
                callbackId: callbackId)
        case .ethCall(from: let from, to: let to, value: let value, data: let data):
            //Must use unchecked form for `Address `because `from` and `to` might be 0x0..0. We assume the dapp author knows what they are doing
            let from = AlphaWallet.Address(uncheckedAgainstNullAddress: from)
            let to = AlphaWallet.Address(uncheckedAgainstNullAddress: to)
            requestEthCall(
                session: session,
                callbackId: callbackId,
                from: from,
                to: to,
                value: value,
                data: data)
        case .walletAddEthereumChain(let customChain):
            requestAddCustomChain(
                session: session,
                callbackId: callbackId,
                customChain: customChain)
        case .walletSwitchEthereumChain(let targetChain):
            requestSwitchChain(
                session: session,
                callbackId: callbackId,
                targetChain: targetChain)
        case .unknown, .sendRawTransaction:
            break
        }
    }



    func forceUpdate(url: URL) {
//        browserNavBar?.display(url: url)
    }

    func handleUniversalLink(_ url: URL) {
//        delegate?.handleUniversalLink(url, forCoordinator: self)
    }
    
    private func requestSignTransaction(session: WalletSession,
                                        callbackId: Int,
                                        transaction: UnconfirmedTransaction) {
        
        os_log("1991 requestSignTransaction")

//        requestSignTransaction(session: session, source: .browser, requester: nil, transaction: transaction, configuration: .dappTransaction(confirmType: .sign))
//            .sink(receiveCompletion: { result in
//                guard case .failure = result else { return }
//                self.notifyFinish(callbackId: callbackId, value: .failure(.responseError))
//            }, receiveValue: { data in
//                let callback = DappCallback(id: callbackId, value: .signTransaction(data))
//                self.notifyFinish(callbackId: callbackId, value: .success(callback))
//            }).store(in: &cancellable)
    }
    
//    func requestSignTransaction(session: WalletSession,
//                                source: Analytics.TransactionConfirmationSource,
//                                requester: RequesterViewModel?,
//                                transaction: UnconfirmedTransaction,
//                                configuration: TransactionType.Configuration) -> AnyPublisher<Data, PromiseError> {
//
//        os_log("[\(source)] signTransaction: \(transaction) type: \(configuration.confirmType)")
//
//        return firstly {
//            TransactionConfirmationCoordinator.promise(navigationController, session: session, coordinator: self, transaction: transaction, configuration: configuration, analytics: analytics, domainResolutionService: domainResolutionService, source: source, delegate: self, keystore: keystore, tokensService: tokensPipeline, networkService: networkService)
//        }.map { data -> Data in
//            switch data {
//            case .signedTransaction(let data):
//                return data
//            case .sentRawTransaction, .sentTransaction:
//                throw PMKError.cancelled
//            }
//        }.publisher(queue: .main)
//    }
    
    private func validateMessage(session: WalletSession, message: SignMessageType) -> AnyPublisher<Void, PromiseError> {
        do {
            switch message {
            case .eip712v3And4(let typedData):
                let validator = DappOrTokenScriptEip712v3And4Validator(server: session.server, source: .dappBrowser)
                try validator.validate(message: typedData)
            case .typedMessage(let typedData):
                let validator = TypedMessageValidator()
                try validator.validate(message: typedData)
            case .message, .personalMessage:
                break
            }
            return .just(())
        } catch {
            return .fail(PromiseError(error: error))
        }
    }
    
    private func requestSignMessage(session: WalletSession,
                                    message: SignMessageType,
                                    callbackId: Int) {

        validateMessage(session: session, message: message)
            .flatMap { _ in
                self.requestSignMessage(
                    message: message,
                    server: session.server,
                    account: session.account.address,
                    source: .dappBrowser,
                    requester: nil)
            }.sink(receiveCompletion: { result in
                guard case .failure = result else { return }
                self.notifyFinish(callbackId: callbackId, value: .failure(.responseError))
            }, receiveValue: { data in
                let callback: DappCallback
                switch message {
                case .message:
                    callback = DappCallback(id: callbackId, value: .signMessage(data))
                case .personalMessage:
                    callback = DappCallback(id: callbackId, value: .signPersonalMessage(data))
                case .typedMessage:
                    callback = DappCallback(id: callbackId, value: .signTypedMessage(data))
                case .eip712v3And4:
                    callback = DappCallback(id: callbackId, value: .signEip712v3And4(data))
                }

                self.notifyFinish(callbackId: callbackId, value: .success(callback))
            }).store(in: &cancellable)
    }
    
    func requestSignMessage(message: SignMessageType,
                            server: RPCServer,
                            account: AlphaWallet.Address,
                            source: Analytics.SignMessageRequestSource,
                            requester: RequesterViewModel?) -> AnyPublisher<Data, PromiseError> {

        os_log("1991 requestSignMessage")
        
//        os_log("[\(source)] signMessage: \(message)")

//        return SignMessageCoordinator.promise(
//            analytics: analytics,
//            navigationController: navigationController,
//            keystore: keystore,
//            coordinator: self,
//            signType: message,
//            account: account,
//            source: source,
//            requester: requester)
//            .publisher(queue: .main)
        let defaultData = Data()
        return Just(defaultData)
            .setFailureType(to: PromiseError.self)
            .eraseToAnyPublisher()
    }
    
//    private let (promise, seal) = Promise<Data>.pending()


    private func requestSendTransaction(session: WalletSession,
                                        callbackId: Int,
                                        transaction: UnconfirmedTransaction) {
        
        os_log("1991 requestSendTransaction")
        let transactionDetails = TransactionDetails( url: webView.url?.absoluteString ?? "",
                                                     accountId: accountId,
                                                     coinDecimals: session.server.decimals,
                                                     chainId: session.server.chainID,
                                                     callbackId: callbackId)
        let viewController = ConfirmBottomSheetHostingController(transaction: transaction, transactionDetails: transactionDetails)
        
        viewController.delegate = self
        viewController.modalPresentationStyle = .custom
        viewController.preferredContentSize = CGSize(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height / 2)
        
        let navVC = CustomNavigationController(rootViewController: viewController)
        navVC.presentationController?.delegate = navVC
        navVC.onDismiss = {
            // Handle the dismissal here
            os_log("dismissal here")
            self.onFail(callbackId: transactionDetails.callbackId)
        }

        self.present(navVC, animated: true, completion: nil)
        
//        self.presentPanModal(viewController)
//        self.present(viewController, animated: true, completion: nil)
        
//        var recipient = toAddress(transaction: transaction)
//        var amount = getAmount(transaction: transaction)
//
//        let hostingController = ComposeRootControllerKt.confirmTransactionController(
//            url: webView.url?.absoluteString ?? "",
//            balance: "",
//            coinDecimals: Int64(transactionDetails.coinDecimals),
//            chainId: Int64(transactionDetails.chainId),
//            callbackId: Int64(transactionDetails.callbackId),
//            value: amount,
//            recipient: recipient,
//            payload: transaction.data.hexEncoded,
//            nonce: Int64(transaction.nonce ?? 0),
//            isLegacyTransaction: true,
//            onConfirm: {
////                let unlockPinScreenController = ComposeRootControllerKt.unlockScreenController { kotlinBoolean in
////                    DispatchQueue.main.async {
////                        self.unlockSuccess = kotlinBoolean.boolValue
////                        print("1991 unlockSuccess \(kotlinBoolean.boolValue)")
////
////                    }
////                }
//            },
//            onDecline: {
//                        self.dismiss(animated: true, completion: nil)
//                    }
//            )
//
//        self.present(hostingController, animated: true, completion: nil)

//        requestSendTransaction(session: session, source: .browser, requester: nil, transaction: transaction, configuration: .dappTransaction(confirmType: .signThenSend))
//            .sink(receiveCompletion: { result in
//                guard case .failure = result else { return }
//                self.notifyFinish(callbackId: callbackId, value: .failure(.responseError))
//            }, receiveValue: { transaction in
//                let callback = DappCallback(id: callbackId, value: .sentTransaction(Data(_hex: transaction.id)))
//                self.notifyFinish(callbackId: callbackId, value: .success(callback))
//            }).store(in: &cancellable)
    }
    
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
    
//    func requestSendTransaction(session: WalletSession,
//                                source: Analytics.TransactionConfirmationSource,
//                                requester: RequesterViewModel?,
//                                transaction: UnconfirmedTransaction,
//                                configuration: TransactionType.Configuration) -> AnyPublisher<SentTransaction, PromiseError> {
//
//        os_log("[\(source)] sendTransaction: \(transaction) type: \(configuration.confirmType)")
//
//        return firstly {
//            TransactionConfirmationCoordinator.promise(navigationController, session: session, coordinator: self, transaction: transaction, configuration: configuration, analytics: analytics, domainResolutionService: domainResolutionService, source: .walletConnect, delegate: self, keystore: keystore, tokensService: tokensPipeline, networkService: networkService)
//        }.map { data -> SentTransaction in
//            switch data {
//            case .sentTransaction(let transaction):
//                return transaction
//            case .signedTransaction, .sentRawTransaction:
//                throw PMKError.cancelled
//            }
//        }.get { _ in
//            TransactionInProgressCoordinator.promise(self.navigationController, coordinator: self, server: session.server).done { _ in }.cauterize()
//        }.publisher(queue: .main)
//    }

    private func requestEthCall(session: WalletSession,
                                callbackId: Int,
                                from: AlphaWallet.Address?,
                                to: AlphaWallet.Address?,
                                value: String?,
                                data: String) {

        requestEthCall(from: from, to: to, value: value, data: data, source: .dappBrowser, session: session)
            .sink(receiveCompletion: {result in
                guard case .failure(let error) = result else { return }

                if case JSONRPCError.responseError(let code, let message, _) = error.embedded {
                    self.notifyFinish(callbackId: callbackId, value: .failure(.init(code: code, message: message)))
                } else {
                    //TODO better handle. User didn't cancel
                    self.notifyFinish(callbackId: callbackId, value: .failure(.responseError))
                }

            }, receiveValue: { value in
                let callback = DappCallback(id: callbackId, value: .ethCall(value))
                self.notifyFinish(callbackId: callbackId, value: .success(callback))
            }).store(in: &cancellable)
    }
    
    func requestEthCall(from: AlphaWallet.Address?,
                        to: AlphaWallet.Address?,
                        value: String?,
                        data: String,
                        source: Analytics.SignMessageRequestSource,
                        session: WalletSession) -> AnyPublisher<String, PromiseError> {

        os_log("[\(source)] ethCall")
        let provider = session.blockchainProvider
        return provider.call(from: from, to: to, value: value, data: data)
            .receive(on: RunLoop.main)
            .mapError { PromiseError(error: $0) }
            .eraseToAnyPublisher()
    }
    
    private func requestAddCustomChain(session: WalletSession,
                                       callbackId: Int,
                                       customChain: WalletAddEthereumChainObject) {
        os_log("1991 requestAddCustomChain")
//        requestAddCustomChain(server: server, customChain: customChain)
//            .sink(receiveCompletion: { [weak self] result in
//                guard case .failure(let e) = result else { return }
//                let error = e.embedded as? JsonRpcError ?? .internalError
//
//                self?.notifyFinish(callbackId: callbackId, value: .failure(error))
//            }, receiveValue: { [weak self] operation in
//                switch operation {
//                case .notifySuccessful:
//                    let callback = DappCallback(id: callbackId, value: .walletAddEthereumChain)
//                    self?.notifyFinish(callbackId: callbackId, value: .success(callback))
//                case .switchBrowserToExistingServer:
//                    break //no-op handled in parent
//                case .restartToEnableAndSwitchBrowserToServer:
//                    break
//                case .restartToAddEnableAndSwitchBrowserToServer:
//                    guard let server = customChain.server else { return }
//                    self?.switch(toServer: server)
//                }
//            }).store(in: &cancellable)
    }
    

    func requestSwitchChain(session: WalletSession,
                                    callbackId: Int,
                                    targetChain: WalletSwitchEthereumChainObject) {
        os_log("1991 requestSwitchChain")
//        requestSwitchChain(server: server, currentUrl: currentUrl, targetChain: targetChain)
//            .sink(receiveCompletion: { [weak self] result in
//                guard case .failure(let e) = result else { return }
//                let error = e.embedded as? JsonRpcError ?? .internalError
//
//                self?.notifyFinish(callbackId: callbackId, value: .failure(error))
//            }, receiveValue: { [weak self] operation in
//                switch operation {
//                case .notifySuccessful:
//                    let callback = DappCallback(id: callbackId, value: .walletSwitchEthereumChain)
//                    self?.notifyFinish(callbackId: callbackId, value: .success(callback))
//                case .switchBrowserToExistingServer(let server, let url):
//                    self?.switch(toServer: server, url: url)
//                case .restartToEnableAndSwitchBrowserToServer:
//                    break
//                }
//            }).store(in: &cancellable)
    }
    
    func notifyFinish(callbackId: Int, value: Swift.Result<DappCallback, JsonRpcError>) {
        
        switch value {
        case .success(let result):
            os_log("1991 notifyFinish success callbackId \(callbackId)")
            webView.evaluateJavaScript("executeCallback(\(callbackId), null, \"\(result.value.object)\")")
        case .failure(let error):
            os_log("1991 notifyFinish failure callbackId \(callbackId)")
            webView.evaluateJavaScript("executeCallback(\(callbackId), {message: \"\(error.message)\", code: \(error.code)}, null)")
        }
    }
    
    private let recordUrlSubject = PassthroughSubject<Void, Never>()
    private let universalLinkSubject = PassthroughSubject<URL, Never>()
    private let dappActionSubject = PassthroughSubject<(action: DappAction, callbackId: Int), Never>()
    private var cancellable = Set<AnyCancellable>()
//    private var keyboardStatePublisher: AnyPublisher<KeyboardChecker.KeyboardState, Never> {
//        let keyboardNotifications: [NSNotification.Name] = [
//            UIResponder.keyboardWillShowNotification,
//            UIResponder.keyboardWillHideNotification,
//        ]
//
//        return Publishers.MergeMany(keyboardNotifications.map { NotificationCenter.default.publisher(for: $0) })
//            .map { KeyboardChecker.KeyboardState(with: $0) }
//            .eraseToAnyPublisher()
//    }
    
    var server: RPCServer = .binance_smart_chain
    
//    lazy var config: WKWebViewConfiguration = {
//        let address = AlphaWallet.Address.make(address: "0xCFe578f1709831d7BF77Dc4202CF2ea01993E9Ed")
//        let config = WKWebViewConfiguration.make(forType: .dappBrowser(server), address: address, messageHandler: ScriptMessageProxy(delegate: self))
//        config.websiteDataStore = WKWebsiteDataStore.default()
//        return config
//    }()
    
    func switchChain(newChainId: Int){
        self.chainId = newChainId
        
        switch chainId {
            case 1: server = .main
            case 61: server = .classic
            case 104729: server = .callisto
            case 100: server = .xDai
            case 5: server = .goerli
            case 56: server = .binance_smart_chain
            case 97: server = .binance_smart_chain_testnet
            case 128: server = .heco
            case 256: server = .heco_testnet
            case 250: server = .fantom
            case 0xfa2: server = .fantom_testnet
            case 0xa86a: server = .avalanche
            case 0xa869: server = .avalanche_testnet
            case 137: server = .polygon
            case 80001: server = .mumbai_testnet
            case 10: server = .optimistic
            case 338: server = .cronosTestnet
            case 25: server = .cronosMainnet
            case 42161: server = .arbitrum
            case 11297108109: server = .palm
            case 11297108099: server = .palmTestnet
            case 8217: server = .klaytnCypress
            case 1001: server = .klaytnBaobabTestnet
            case 4689: server = .ioTeX
            case 4690: server = .ioTeXTestnet
            case 420: server = .optimismGoerli
            case 421613: server = .arbitrumGoerli
            case 66: server = .okx
            case 11155111: server = .sepolia
        default:
            server = .main
        }
        os_log("2022 newChainId \(newChainId)")
        let configuration =  WKWebViewConfiguration.persistent()
        attachWebView(configuration:configuration, andLoadRequest: URLRequest.userInitiated(link!.url), consumeCookies: false)
//        webView.reloadFromOrigin()
//        webView.reload()
    }

    
    // The `consumeCookies` is legacy behaviour from the previous Fireproofing implementation. Cookies no longer need to be consumed after invocations
    // of the Fire button, but the app still does so in the event that previously persisted cookies have not yet been consumed.
    func attachWebView(configuration: WKWebViewConfiguration, andLoadRequest request: URLRequest?, consumeCookies: Bool) {
        instrumentation.willPrepareWebView()
        
        WKWebViewConfiguration.injectJS(to: configuration,
                                                     isTokenScriptRenderer: isTokenScriptRenderer,
                                                     rpcUrl: rpcUrl,
                                                     chainId: chainId,
                                                     address: address,
                                                     messageHandler: ScriptMessageProxy(delegate: self))
                                                     
        webView = WKWebView(frame: view.bounds, configuration: configuration)
        webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        
        bind()
        
        webView.allowsLinkPreview = true
        webView.allowsBackForwardNavigationGestures = true
        
        addObservers()
        
        webView.navigationDelegate = self
        webView.uiDelegate = self
        webViewContainer.addSubview(webView)

        reloadUserScripts()
        updateContentMode()
        
        instrumentation.didPrepareWebView()
        
        if consumeCookies {
            consumeCookiesThenLoadRequest(request)
        } else if let request = request {
            if let url = request.url {
                linkProtection.getCleanURL(from: url,
                                           onStartExtracting: { showProgressIndicator() },
                                           onFinishExtracting: { },
                                           completion: { [weak self] cleanURL in self?.load(urlRequest: .userInitiated(cleanURL)) })
            } else {
                load(urlRequest: request)
            }
        }
    }

    private func addObservers() {
        webView.addObserver(self, forKeyPath: #keyPath(WKWebView.estimatedProgress), options: .new, context: nil)
        webView.addObserver(self, forKeyPath: #keyPath(WKWebView.hasOnlySecureContent), options: .new, context: nil)
        webView.addObserver(self, forKeyPath: #keyPath(WKWebView.url), options: .new, context: nil)
        webView.addObserver(self, forKeyPath: #keyPath(WKWebView.canGoBack), options: .new, context: nil)
        webView.addObserver(self, forKeyPath: #keyPath(WKWebView.canGoForward), options: .new, context: nil)
        webView.addObserver(self, forKeyPath: #keyPath(WKWebView.title), options: .new, context: nil)
    }
    
    private func registerForNotifications() {
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(downloadDidStart),
                                               name: .downloadStarted,
                                               object: nil)
        NotificationCenter.default.addObserver(self, selector:
                                                #selector(downloadDidFinish),
                                               name: .downloadFinished,
                                               object: nil)
    }

    private func consumeCookiesThenLoadRequest(_ request: URLRequest?) {
        webView.configuration.websiteDataStore.fetchDataRecords(ofTypes: WKWebsiteDataStore.allWebsiteDataTypes()) { _ in
            WebCacheManager.shared.consumeCookies { [weak self] in
                guard let strongSelf = self else { return }
                
                if let request = request {
                    strongSelf.load(urlRequest: request)
                }
                
                if request != nil {
                    strongSelf.delegate?.tabLoadingStateDidChange(tab: strongSelf)
                    strongSelf.onWebpageDidStartLoading(httpsForced: false)
                }
            }
        }
    }
    
    public func load(url: URL) {
        load(url: url, didUpgradeURL: false)
    }
    
    public func load(backForwardListItem: WKBackForwardListItem) {
        webView.stopLoading()
        updateContentMode()
        webView.go(to: backForwardListItem)
    }
    
    private func load(url: URL, didUpgradeURL: Bool) {
        if !didUpgradeURL {
            lastUpgradedURL = nil
        }
        
        if !url.isBookmarklet() {
            self.url = url
        }
        
        lastError = nil
        updateContentMode()
        linkProtection.getCleanURL(from: url,
                                   onStartExtracting: { showProgressIndicator() },
                                   onFinishExtracting: { },
                                   completion: { [weak self] url in
            self?.load(urlRequest: .userInitiated(url))
        })
    }
    
    func prepareForDataClearing() {
        webView.navigationDelegate = nil
        webView.uiDelegate = nil
        delegate = nil
        
        webView.stopLoading()
        webView.loadHTMLString("", baseURL: nil)
    }
    
    private func load(urlRequest: URLRequest) {
        loadViewIfNeeded()
        
        if let url = urlRequest.url, !shouldReissueSearch(for: url) {
            requeryLogic.onNewNavigation(url: url)
        }

        if #available(iOS 15.0, *) {
            assert(urlRequest.attribution == .user, "WebView requests should be user attributed")
        }

        webView.stopLoading()
        webView.load(urlRequest)
    }
    
    // swiftlint:disable block_based_kvo
    open override func observeValue(forKeyPath keyPath: String?,
                                    of object: Any?,
                                    change: [NSKeyValueChangeKey: Any]?,
                                    context: UnsafeMutableRawPointer?) {
        // swiftlint:enable block_based_kvo

        guard let keyPath = keyPath else { return }
        
        switch keyPath {
            
        case #keyPath(WKWebView.estimatedProgress):
            progressWorker.progressDidChange(webView.estimatedProgress)
            
        case #keyPath(WKWebView.hasOnlySecureContent):
            hasOnlySecureContentChanged(hasOnlySecureContent: webView.hasOnlySecureContent)
            
        case #keyPath(WKWebView.url):
            webViewUrlHasChanged()
            
        case #keyPath(WKWebView.canGoBack):
            delegate?.tabLoadingStateDidChange(tab: self)
            
        case #keyPath(WKWebView.canGoForward):
            delegate?.tabLoadingStateDidChange(tab: self)

        case #keyPath(WKWebView.title):
            title = webView.title

        default:
            os_log("Unhandled keyPath %s", log: generalLog, type: .debug, keyPath)
        }
    }
    
    func webViewUrlHasChanged() {
        if url == nil {
            url = webView.url
        } else if let currentHost = url?.host, let newHost = webView.url?.host, currentHost == newHost {
            url = webView.url
        }
    }
    
    func hasOnlySecureContentChanged(hasOnlySecureContent: Bool) {
        guard webView.url?.host == siteRating?.url.host else { return }
        siteRating?.hasOnlySecureContent = hasOnlySecureContent
        updateSiteRating()
    }
    
    func enableFireproofingForDomain(_ domain: String) {
        PreserveLoginsAlert.showConfirmFireproofWebsite(usingController: self, forDomain: domain) { [weak self] in
            Pixel.fire(pixel: .browsingMenuFireproof)
            self?.preserveLoginsWorker?.handleUserEnablingFireproofing(forDomain: domain)
        }
    }
    
    func disableFireproofingForDomain(_ domain: String) {
        preserveLoginsWorker?.handleUserDisablingFireproofing(forDomain: domain)
    }
    
    private func checkForReloadOnError() {
        guard shouldReloadOnError else { return }
        shouldReloadOnError = false
        reload(scripts: false)
    }
    
    private func shouldReissueDDGStaticNavigation(for url: URL) -> Bool {
        guard appUrls.isDuckDuckGoStatic(url: url) else { return false }
        return  !appUrls.hasCorrectSearchHeaderParams(url: url)
    }
    
    private func reissueNavigationWithSearchHeaderParams(for url: URL) {
        load(url: appUrls.applySearchHeaderParams(for: url))
    }
    
    private func shouldReissueSearch(for url: URL) -> Bool {
        guard appUrls.isDuckDuckGoSearch(url: url) else { return false }
        return  !appUrls.hasCorrectMobileStatsParams(url: url) || !appUrls.hasCorrectSearchHeaderParams(url: url)
    }
    
    private func reissueSearchWithRequiredParams(for url: URL) {
        let mobileSearch = appUrls.applyStatsParams(for: url)
        reissueNavigationWithSearchHeaderParams(for: mobileSearch)
    }
    
    private func showProgressIndicator() {
        progressWorker.didStartLoading()
    }
    
    private func hideProgressIndicator() {
        progressWorker.didFinishLoading()
    }
    
    public func reload(scripts: Bool) {
        if scripts {
            reloadUserScripts()
        }
        updateContentMode()
        webView.reload()
    }
    
    func updateContentMode() {
        webView.configuration.defaultWebpagePreferences.preferredContentMode = tabModel.isDesktop ? .desktop : .mobile
    }
    
    func goBack() {
        if isError {
            hideErrorMessage()
            url = webView.url
            onWebpageDidStartLoading(httpsForced: false)
            onWebpageDidFinishLoading()
        } else if webView.canGoBack {
            webView.goBack()
            chromeDelegate?.omniBar.resignFirstResponder()
        } else if openingTab != nil {
            delegate?.tabDidRequestClose(self)
        }
    }
    
    func goForward() {
        if webView.goForward() != nil {
            chromeDelegate?.omniBar.resignFirstResponder()
        }
    }
    
    private func showError(message: String) {
        webView.isHidden = true
        error.isHidden = false
        errorMessage.text = message
        error.layoutIfNeeded()
    }
    
    private func hideErrorMessage() {
        error.isHidden = true
        webView.isHidden = false
    }
    
    private func reloadUserScripts() {
        removeMessageHandlers() // incoming config might be a copy of an existing confg with handlers
//        webView.configuration.userContentController.removeAllUserScripts()
        
        refreshAutofillUserScript()
        initUserScripts()
        
        userScripts.forEach { script in

            webView.configuration.userContentController.addUserScript(WKUserScript(source: script.source,
                                                                                   injectionTime: script.injectionTime,
                                                                                   forMainFrameOnly: script.forMainFrameOnly))
            
            if #available(iOS 14, *),
               let replyHandler = script as? WKScriptMessageHandlerWithReply {
                script.messageNames.forEach { messageName in
                    webView.configuration.userContentController.addScriptMessageHandler(replyHandler, contentWorld: .page, name: messageName)
                }
            } else {
                script.messageNames.forEach { messageName in
                    webView.configuration.userContentController.add(script, name: messageName)
                }
            }

        }

    }
    
    private func isDuckDuckGoUrl() -> Bool {
        guard let url = url else { return false }
        return appUrls.isDuckDuckGo(url: url)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {

        guard let chromeDelegate = chromeDelegate else { return }

        if let controller = segue.destination as? PrivacyProtectionController {
            controller.popoverPresentationController?.delegate = controller

            if let siteRatingView = chromeDelegate.omniBar.siteRatingContainer.siteRatingView {
                controller.popoverPresentationController?.sourceView = siteRatingView
                controller.popoverPresentationController?.sourceRect = siteRatingView.bounds
            }

            controller.privacyProtectionDelegate = self
            privacyController = controller
            controller.omniDelegate = chromeDelegate.omniBar.omniDelegate
            controller.omniBarText = chromeDelegate.omniBar.textField.text
            controller.siteRating = siteRating
            controller.errorText = isError ? errorText : nil
        }
        
        if let controller = segue.destination as? FullscreenDaxDialogViewController {
            controller.spec = sender as? DaxDialogs.BrowsingSpec
            controller.woShown = woShownRecently
            controller.delegate = self
            
            if controller.spec?.highlightAddressBar ?? false {
                chromeDelegate.omniBar.cancelAllAnimations()
            }
        }
        
    }
    
    private func addLoginDetectionStateObserver() {
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(onLoginDetectionStateChanged),
                                               name: PreserveLogins.Notifications.loginDetectionStateChanged,
                                               object: nil)
    }
    
    private func addContentBlockerConfigurationObserver() {
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(onContentBlockerConfigurationChanged),
                                               name: ContentBlockerProtectionChangedNotification.name,
                                               object: nil)
    }

    private func addStorageCacheProviderObserver() {
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(onStorageCacheChange),
                                               name: StorageCacheProvider.didUpdateStorageCacheNotification,
                                               object: nil)
    }
    
    private func addDoNotSellObserver() {
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(onDoNotSellChange),
                                               name: AppUserDefaults.Notifications.doNotSellStatusChange,
                                               object: nil)
    }
    
    private func addTextSizeObserver() {
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(onTextSizeChange),
                                               name: AppUserDefaults.Notifications.textSizeChange,
                                               object: nil)
    }

    private func addDuckDuckGoEmailSignOutObserver() {
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(onDuckDuckGoEmailSignOut),
                                               name: .emailDidSignOut,
                                               object: nil)
    }
    
    private func addAutofillEnabledObserver() {
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(onAutofillEnabledChange),
                                               name: AppUserDefaults.Notifications.autofillEnabledChange,
                                               object: nil)
    }
    
    @objc func onLoginDetectionStateChanged() {
        reload(scripts: true)
    }
    
    @objc func onContentBlockerConfigurationChanged(notification: Notification) {
        if let rules = ContentBlocking.contentBlockingManager.currentTDSRules,
           ContentBlocking.privacyConfigurationManager.privacyConfig.isEnabled(featureKey: .contentBlocking) {
            
            rulesCompiledCondition?.resolve()
            rulesCompiledCondition = nil

            webView.configuration.userContentController.removeAllContentRuleLists()
            webView.configuration.userContentController.add(rules.rulesList)
            
            let diffKey = ContentBlockerProtectionChangedNotification.diffKey
            let tdsKey = DefaultContentBlockerRulesListsSource.Constants.trackerDataSetRulesListName
            
            if let diff = notification.userInfo?[diffKey] as? [String: ContentBlockerRulesIdentifier.Difference] {
                if diff[tdsKey]?.contains(.unprotectedSites) ?? false {
                    reload(scripts: true)
                } else {
                    reloadUserScripts()
                }
            } else {
                reloadUserScripts()
            }

        } else {
            webView.configuration.userContentController.removeAllContentRuleLists()
        }
    }

    @objc func onStorageCacheChange() {
        DispatchQueue.main.async {
            self.reload(scripts: true)
        }
    }
    
    @objc func onDoNotSellChange() {
        reload(scripts: true)
    }
    
    @objc func onTextSizeChange() {
        webView.adjustTextSize(appSettings.textSize)
        reloadUserScripts()
    }

    @objc func onDuckDuckGoEmailSignOut(_ notification: Notification) {
        guard let url = webView.url else { return }
        if AppUrls().isDuckDuckGoEmailProtection(url: url) {
            webView.evaluateJavaScript("window.postMessage({ emailProtectionSignedOut: true }, window.origin);")
        }
    }
    
    @objc func onAutofillEnabledChange() {
        reloadUserScripts()
    }

    private func resetNavigationBar() {
        chromeDelegate?.setNavigationBarHidden(false)
    }

    @IBAction func onBottomOfScreenTapped(_ sender: UITapGestureRecognizer) {
        showBars(animated: false)
    }

    private func showBars(animated: Bool = true) {
        chromeDelegate?.setBarsHidden(false, animated: animated)
    }

    func showPrivacyDashboard() {
        Pixel.fire(pixel: .privacyDashboardOpened)
        performSegue(withIdentifier: "PrivacyProtection", sender: self)
    }

    private func resetSiteRating() {
        if let url = url {
            siteRating = makeSiteRating(url: url)
        } else {
            siteRating = nil
        }
        onSiteRatingChanged()
    }
    
    private func makeSiteRating(url: URL) -> SiteRating {
        let entityMapping = EntityMapping()
        let privacyPractices = PrivacyPractices(tld: storageCache.tld,
                                                termsOfServiceStore: storageCache.termsOfServiceStore,
                                                entityMapping: entityMapping)
        
        return SiteRating(url: url,
                          httpsForced: httpsForced,
                          entityMapping: entityMapping,
                          privacyPractices: privacyPractices)
    }

    private func updateSiteRating() {
        if isError {
            siteRating = nil
        }
        onSiteRatingChanged()
    }

    private func onSiteRatingChanged() {
        delegate?.tab(self, didChangeSiteRating: siteRating)
        privacyController?.updateSiteRating(siteRating)
    }
    
    func didLaunchBrowsingMenu() {
        Pixel.fire(pixel: .browsingMenuOpened)
        DaxDialogs.shared.resumeRegularFlow()
    }
    
    private func launchLongPressMenu(atPoint point: Point, forUrl url: URL) {
        let alert = buildLongPressMenu(atPoint: point, forUrl: url)
        present(controller: alert, fromView: webView, atPoint: point)
    }
    
    private func openExternally(url: URL) {
        self.url = webView.url
        delegate?.tabLoadingStateDidChange(tab: self)
        UIApplication.shared.open(url, options: [:]) { opened in
            if !opened {
                ActionMessageView.present(message: UserText.failedToOpenExternally)
            }

            // just showing a blank tab at this point, so close it
            if self.webView.url == nil {
                self.delegate?.tabDidRequestClose(self)
            }
        }
    }
    
    func presentOpenInExternalAppAlert(url: URL) {
        let title = UserText.customUrlSchemeTitle
        let message = UserText.customUrlSchemeMessage
        let open = UserText.customUrlSchemeOpen
        let dontOpen = UserText.customUrlSchemeDontOpen
        
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.overrideUserInterfaceStyle()
        alert.addAction(UIAlertAction(title: dontOpen, style: .cancel, handler: { _ in
            if self.webView.url == nil {
                self.delegate?.tabDidRequestClose(self)
            } else {
                self.url = self.webView.url
            }
        }))
        alert.addAction(UIAlertAction(title: open, style: .destructive, handler: { _ in
            self.openExternally(url: url)
        }))
        delegate?.tab(self, didRequestPresentingAlert: alert)
    }

    func dismiss() {
        progressWorker.progressBar = nil
        chromeDelegate?.omniBar.cancelAllAnimations()
        cancelTrackerNetworksAnimation()
        willMove(toParent: nil)
        removeFromParent()
        view.removeFromSuperview()
    }
    
    private func removeMessageHandlers() {
        let controller = webView.configuration.userContentController
        userScripts.forEach { script in
            script.messageNames.forEach { messageName in
                controller.removeScriptMessageHandler(forName: messageName)
            }
        }
    }
    
    private func removeObservers() {
        webView.removeObserver(self, forKeyPath: #keyPath(WKWebView.estimatedProgress))
        webView.removeObserver(self, forKeyPath: #keyPath(WKWebView.hasOnlySecureContent))
        webView.removeObserver(self, forKeyPath: #keyPath(WKWebView.url))
        webView.removeObserver(self, forKeyPath: #keyPath(WKWebView.canGoForward))
        webView.removeObserver(self, forKeyPath: #keyPath(WKWebView.canGoBack))
        webView.removeObserver(self, forKeyPath: #keyPath(WKWebView.title))
    }
        
    public func getCurrentWebsiteInfo() -> BrokenSiteInfo {
        let blockedTrackerDomains = siteRating?.trackersBlocked.compactMap { $0.domain } ?? []
        
        return BrokenSiteInfo(url: url,
                              httpsUpgrade: httpsForced,
                              blockedTrackerDomains: blockedTrackerDomains,
                              installedSurrogates: siteRating?.installedSurrogates.map { $0 } ?? [],
                              isDesktop: tabModel.isDesktop,
                              tdsETag: ContentBlocking.contentBlockingManager.currentTDSRules?.etag ?? "",
                              ampUrl: linkProtection.lastAMPURLString,
                              urlParametersRemoved: linkProtection.urlParametersRemoved)
    }
    
    public func print() {
        let printFormatter = webView.viewPrintFormatter()
        
        let printInfo = UIPrintInfo(dictionary: nil)
        printInfo.jobName = Bundle.main.infoDictionary!["CFBundleName"] as? String ?? "DuckDuckGo"
        printInfo.outputType = .general
        
        let printController = UIPrintInteractionController.shared
        printController.printInfo = printInfo
        printController.printFormatter = printFormatter
        printController.present(animated: true, completionHandler: nil)
    }
    
    func onCopyAction(forUrl url: URL) {
        let copyText: String
        if appUrls.isDuckDuckGo(url: url) {
            let cleanURL = appUrls.removeInternalSearchParameters(fromUrl: url)
            copyText = cleanURL.absoluteString
        } else {
            copyText = url.absoluteString
        }
        
        onCopyAction(for: copyText)
    }
    
    func onCopyAction(for text: String) {
        UIPasteboard.general.string = text
    }
    
    deinit {
        temporaryDownloadForPreviewedFile?.cancel()
        removeMessageHandlers()
        removeObservers()
        rulesCompilationMonitor.tabWillClose(self)
    }
    
    @objc private func downloadDidFinish(_ notification: Notification) {
        if let error = notification.userInfo?[DownloadManager.UserInfoKeys.error] as? Error {
            let nserror = error as NSError
            let downloadWasCancelled = nserror.domain == "NSURLErrorDomain" && nserror.code == -999
            
            if !downloadWasCancelled {
                ActionMessageView.present(message: UserText.messageDownloadFailed)
            }
            
            return
        }
        
        guard let download = notification.userInfo?[DownloadManager.UserInfoKeys.download] as? Download else { return }
        
        DispatchQueue.main.async {
            if !download.temporary {
                let attributedMessage = DownloadActionMessageViewHelper.makeDownloadFinishedMessage(for: download)
                ActionMessageView.present(message: attributedMessage, numberOfLines: 2, actionTitle: UserText.actionGenericShow) {
                    Pixel.fire(pixel: .downloadsListOpened,
                               withAdditionalParameters: [PixelParameters.originatedFromMenu: "0"])
                    self.delegate?.tabDidRequestDownloads(tab: self)
                }
            } else {
                self.previewDownloadedFileIfNecessary(download)
            }
        }
    }
    
    @objc private func downloadDidStart(_ notification: Notification) {
        guard let download = notification.userInfo?[DownloadManager.UserInfoKeys.download] as? Download,
                  !download.temporary else { return }
        
        let attributedMessage = DownloadActionMessageViewHelper.makeDownloadStartedMessage(for: download)
        
        DispatchQueue.main.async {
            ActionMessageView.present(message: attributedMessage, numberOfLines: 2, actionTitle: UserText.actionGenericShow) {
                Pixel.fire(pixel: .downloadsListOpened,
                           withAdditionalParameters: [PixelParameters.originatedFromMenu: "0"])
                self.delegate?.tabDidRequestDownloads(tab: self)
            }
        }
    }

    private func previewDownloadedFileIfNecessary(_ download: Download) {
        guard let delegate = self.delegate,
              delegate.tabCheckIfItsBeingCurrentlyPresented(self),
              FilePreviewHelper.canAutoPreviewMIMEType(download.mimeType),
              let fileHandler = FilePreviewHelper.fileHandlerForDownload(download, viewController: self)
        else { return }
        
        if mostRecentAutoPreviewDownloadID == download.id {
            fileHandler.preview()
        } else {
            Pixel.fire(pixel: .downloadTriedToPresentPreviewWithoutTab)
        }
    }
}

extension TabViewController: LoginFormDetectionDelegate {
    
    func loginFormDetectionUserScriptDetectedLoginForm(_ script: LoginFormDetectionUserScript) {
        detectedLoginURL = webView.url
    }
    
}

extension TabViewController: WKNavigationDelegate {
    
    func webView(_ webView: WKWebView,
                 didReceive challenge: URLAuthenticationChallenge,
                 completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {
        if challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodHTTPBasic {
            performBasicHTTPAuthentication(protectionSpace: challenge.protectionSpace, completionHandler: completionHandler)
        } else {
            completionHandler(.performDefaultHandling, nil)
            guard let serverTrust = challenge.protectionSpace.serverTrust else { return }
            ServerTrustCache.shared.put(serverTrust: serverTrust, forDomain: challenge.protectionSpace.host)
        }
    }
    
    func performBasicHTTPAuthentication(protectionSpace: URLProtectionSpace,
                                        completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {
        let isHttps = protectionSpace.protocol == "https"
        let alert = BasicAuthenticationAlert(host: protectionSpace.host,
                                             isEncrypted: isHttps,
                                             logInCompletion: { (login, password) in
            completionHandler(.useCredential, URLCredential(user: login, password: password, persistence: .forSession))
        }, cancelCompletion: {
            completionHandler(.rejectProtectionSpace, nil)
        })
        
        delegate?.tab(self, didRequestPresentingAlert: alert)
    }
    
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        if let url = webView.url {
            instrumentation.willLoad(url: url)
        }
                
        url = webView.url
        let tld = storageCache.tld
        let httpsForced = tld.domain(lastUpgradedURL?.host) == tld.domain(webView.url?.host)
        onWebpageDidStartLoading(httpsForced: httpsForced)
    }
    
    private func onWebpageDidStartLoading(httpsForced: Bool) {
        os_log("webpageLoading started", log: generalLog, type: .debug)

        // Only fire when on the same page that the without trackers Dax Dialog was shown
        self.fireWoFollowUp = false

        self.httpsForced = httpsForced
        delegate?.showBars()

        resetSiteRating()
        
        tabModel.link = link
        delegate?.tabLoadingStateDidChange(tab: self)

        trackerNetworksDetectedOnPage.removeAll()
        pageHasTrackers = false
        NetworkLeaderboard.shared.incrementPagesLoaded()
        
        appRatingPrompt.registerUsage()
        if appRatingPrompt.shouldPrompt() {
            SKStoreReviewController.requestReview()
            appRatingPrompt.shown()
        }
    }

    // swiftlint:disable function_body_length
    func webView(_ webView: WKWebView,
                 decidePolicyFor navigationResponse: WKNavigationResponse,
                 decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
        let mimeType = MIMEType(from: navigationResponse.response.mimeType)
        
        let httpResponse = navigationResponse.response as? HTTPURLResponse
        let isSuccessfulResponse = (httpResponse?.validateStatusCode(statusCode: 200..<300) == nil)
        
        let didMarkAsInternal = featureFlaggerInternalUserDecider.markUserAsInternalIfNeeded(forUrl: webView.url, response: httpResponse)
        if didMarkAsInternal {
            reloadUserScripts()
        }

        if let scheme = navigationResponse.response.url?.scheme, scheme.hasPrefix("blob") {
            Pixel.fire(pixel: .downloadAttemptToOpenBLOB)
        }
      
        if navigationResponse.canShowMIMEType && !FilePreviewHelper.canAutoPreviewMIMEType(mimeType) {
            setupOrClearTemporaryDownload(for: navigationResponse)
            url = webView.url
            decisionHandler(.allow)
        } else if isSuccessfulResponse {
            let downloadManager = AppDependencyProvider.shared.downloadManager
            
            let startDownload: () -> Download? = {
                let cookieStore = webView.configuration.websiteDataStore.httpCookieStore
                if let download = downloadManager.makeDownload(navigationResponse: navigationResponse, cookieStore: cookieStore) {
                    downloadManager.startDownload(download)
                    return download
                } else {
                    return nil
                }
            }
            
            if FilePreviewHelper.canAutoPreviewMIMEType(mimeType) {
                let download = startDownload()
                mostRecentAutoPreviewDownloadID = download?.id
                Pixel.fire(pixel: .downloadStarted,
                           withAdditionalParameters: [PixelParameters.canAutoPreviewMIMEType: "1"])
            } else {
                if let downloadMetadata = downloadManager.downloadMetaData(for: navigationResponse) {
                    let alert = SaveToDownloadsAlert.makeAlert(downloadMetadata: downloadMetadata) {
                        _ = startDownload()
                        Pixel.fire(pixel: .downloadStarted,
                                   withAdditionalParameters: [PixelParameters.canAutoPreviewMIMEType: "0"])
                        
                        if downloadMetadata.mimeType != .octetStream {
                            let mimeType = downloadMetadata.mimeTypeSource
                            Pixel.fire(pixel: .downloadStartedDueToUnhandledMIMEType,
                                       withAdditionalParameters: [PixelParameters.mimeType: mimeType])
                        }
                    }
                    DispatchQueue.main.async {
                        self.present(alert, animated: true, completion: nil)
                    }
                }
            }
        
            decisionHandler(.cancel)
        } else {
            // MIME type should trigger download but response has no 2xx status code
            decisionHandler(.allow)
        }
    }
    // swiftlint:enable function_body_length
    
    /*
     Some files might be previewed by webkit but in order to share them
     we need to download them first.
     This method stores the temporary download or clears it if necessary
     */
    private func setupOrClearTemporaryDownload(for navigationResponse: WKNavigationResponse) {
        let downloadManager = AppDependencyProvider.shared.downloadManager
        
        if let downloadMetaData = downloadManager.downloadMetaData(for: navigationResponse), !downloadMetaData.mimeType.isHTML {
            let cookieStore = webView.configuration.websiteDataStore.httpCookieStore
            temporaryDownloadForPreviewedFile = downloadManager.makeDownload(navigationResponse: navigationResponse,
                                                                             cookieStore: cookieStore,
                                                                             temporary: true)
        } else {
            temporaryDownloadForPreviewedFile?.cancel()
            temporaryDownloadForPreviewedFile = nil
        }
    }
    
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        lastError = nil
        cancelTrackerNetworksAnimation()
        shouldReloadOnError = false
        hideErrorMessage()
        showProgressIndicator()
        chromeDelegate?.omniBar.startLoadingAnimation(for: webView.url)
        linkProtection.cancelOngoingExtraction()
        linkProtection.setMainFrameUrl(webView.url)
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        hideProgressIndicator()
        onWebpageDidFinishLoading()
        instrumentation.didLoadURL()
        checkLoginDetectionAfterNavigation()
        
        // definitely finished with any potential login cycle by this point, so don't try and handle it any more
        detectedLoginURL = nil
        updatePreview()
        linkProtection.setMainFrameUrl(nil)
    }
    
    func preparePreview(completion: @escaping (UIImage?) -> Void) {
        DispatchQueue.main.async { [weak self] in
            guard let webView = self?.webView else { completion(nil); return }
            UIGraphicsBeginImageContextWithOptions(webView.bounds.size, false, UIScreen.main.scale)
            webView.drawHierarchy(in: webView.bounds, afterScreenUpdates: true)
            let image = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            completion(image)
        }
    }
    
    private func updatePreview() {
        preparePreview { image in
            if let image = image {
                self.delegate?.tab(self, didUpdatePreview: image)
            }
        }
    }
    
    private func onWebpageDidFinishLoading() {
        os_log("webpageLoading finished", log: generalLog, type: .debug)
        
        siteRating?.finishedLoading = true
        updateSiteRating()
        tabModel.link = link
        delegate?.tabLoadingStateDidChange(tab: self)

        showDaxDialogOrStartTrackerNetworksAnimationIfNeeded()
    }

    func showDaxDialogOrStartTrackerNetworksAnimationIfNeeded() {
        guard !isLinkPreview else { return }

        if DaxDialogs.shared.isAddFavoriteFlow {
            delegate?.tabDidRequestShowingMenuHighlighter(tab: self)
            return
        }

        guard let siteRating = self.siteRating,
              !isShowingFullScreenDaxDialog else {
                            
                scheduleTrackerNetworksAnimation(collapsing: true)
                return
        }
        
        if let url = link?.url, AppUrls().isDuckDuckGoEmailProtection(url: url) {
            scheduleTrackerNetworksAnimation(collapsing: true)
            return
        }
        
        guard let spec = DaxDialogs.shared.nextBrowsingMessage(siteRating: siteRating) else {
            
            if DaxDialogs.shared.shouldShowFireButtonPulse {
                delegate?.tabDidRequestFireButtonPulse(tab: self)
            }
            
            scheduleTrackerNetworksAnimation(collapsing: true)
            return
        }
        
        isShowingFullScreenDaxDialog = true
        scheduleTrackerNetworksAnimation(collapsing: !spec.highlightAddressBar)
        let daxDialogSourceURL = self.url
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) { [weak self] in
            // https://app.asana.com/0/414709148257752/1201620790053163/f
            if self?.url != daxDialogSourceURL {
                DaxDialogs.shared.overrideShownFlagFor(spec, flag: false)
                self?.isShowingFullScreenDaxDialog = false
                return
            }

            self?.chromeDelegate?.omniBar.resignFirstResponder()
            self?.chromeDelegate?.setBarsHidden(false, animated: true)
            self?.performSegue(withIdentifier: "DaxDialog", sender: spec)

            if spec == DaxDialogs.BrowsingSpec.withoutTrackers {
                self?.woShownRecently = true
                self?.fireWoFollowUp = true
            }
        }
    }
    
    private func scheduleTrackerNetworksAnimation(collapsing: Bool) {
        let trackersWorkItem = DispatchWorkItem {
            guard let siteRating = self.siteRating else { return }
            self.delegate?.tab(self, didRequestPresentingTrackerAnimation: siteRating, isCollapsing: collapsing)
        }
        trackersInfoWorkItem = trackersWorkItem
        DispatchQueue.main.asyncAfter(deadline: .now() + Constants.trackerNetworksAnimationDelay,
                                      execute: trackersWorkItem)
    }
    
    private func cancelTrackerNetworksAnimation() {
        trackersInfoWorkItem?.cancel()
        trackersInfoWorkItem = nil
    }
    
    private func checkLoginDetectionAfterNavigation() {
        if preserveLoginsWorker?.handleLoginDetection(detectedURL: detectedLoginURL, currentURL: url) ?? false {
            detectedLoginURL = nil
        }
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        hideProgressIndicator()
        webpageDidFailToLoad()
        checkForReloadOnError()
        scheduleTrackerNetworksAnimation(collapsing: true)
        linkProtection.setMainFrameUrl(nil)
    }

    private func webpageDidFailToLoad() {
        os_log("webpageLoading failed", log: generalLog, type: .debug)
        if isError {
            showBars(animated: true)
        }
        siteRating?.finishedLoading = true
        updateSiteRating()
        self.delegate?.tabLoadingStateDidChange(tab: self)
    }
    
    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        hideProgressIndicator()
        linkProtection.setMainFrameUrl(nil)
        lastError = error
        let error = error as NSError

        // Ignore Frame Load Interrupted that will be caused when a download starts
        if error.code == 102 && error.domain == "WebKitErrorDomain" {
            return
        }
        
        if let url = url,
            let domain = url.host,
            error.code == Constants.frameLoadInterruptedErrorCode {
            // prevent loops where a site keeps redirecting to itself (e.g. bbc)
            failingUrls.insert(domain)

            // Reset the URL, e.g if opened externally
            self.url = webView.url
        }

        // wait before showing errors in case they recover automatically
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
            self.showErrorNow()
        }
    }

    func webView(_ webView: WKWebView, didReceiveServerRedirectForProvisionalNavigation navigation: WKNavigation!) {
        guard let url = webView.url else { return }
        self.url = url
        self.siteRating = makeSiteRating(url: url)
        updateSiteRating()
        checkLoginDetectionAfterNavigation()
    }
    
    private func requestForDoNotSell(basedOn incomingRequest: URLRequest) -> URLRequest? {
        /*
         For now, the GPC header is only applied to sites known to be honoring GPC (nytimes.com, washingtonpost.com),
         while the DOM signal is available to all websites.
         This is done to avoid an issue with back navigation when adding the header (e.g. with 't.co').
         */
        guard let url = incomingRequest.url, appUrls.isGPCEnabled(url: url) else { return nil }
        
        var request = incomingRequest
        // Add Do Not sell header if needed
        let config = ContentBlocking.privacyConfigurationManager.privacyConfig
        let domain = incomingRequest.url?.host
        let urlAllowed = config.isFeature(.gpc, enabledForDomain: domain)
        
        if appSettings.sendDoNotSell && urlAllowed {
            if let headers = request.allHTTPHeaderFields,
               headers.firstIndex(where: { $0.key == Constants.secGPCHeader }) == nil {
                request.addValue("1", forHTTPHeaderField: Constants.secGPCHeader)

                if #available(iOS 15.0, *) {
                    request.attribution = .user
                }

                return request
            }
        } else {
            // Check if DN$ header is still there and remove it
            if let headers = request.allHTTPHeaderFields, headers.firstIndex(where: { $0.key == Constants.secGPCHeader }) != nil {
                request.setValue(nil, forHTTPHeaderField: Constants.secGPCHeader)

                if #available(iOS 15.0, *) {
                    request.attribution = .user
                }

                return request
            }
        }
        return nil
    }
    
    
    // swiftlint:disable function_body_length
    // swiftlint:disable cyclomatic_complexity
    func webView(_ webView: WKWebView,
                 decidePolicyFor navigationAction: WKNavigationAction,
                 decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        
        if let url = navigationAction.request.url, !appUrls.isDuckDuckGoSearch(url: url) {
            waitUntilRulesAreCompiled()
        }
        
        // This check needs to happen before GPC checks. Otherwise the navigation type may be rewritten to `.other`
        // which would skip link rewrites.
        if navigationAction.navigationType == .linkActivated {
            let didRewriteLink = linkProtection.requestTrackingLinkRewrite(initiatingURL: webView.url,
                                                                           navigationAction: navigationAction,
                                                                           onStartExtracting: { showProgressIndicator() },
                                                                           onFinishExtracting: { },
                                                                           onLinkRewrite: { [weak self] newURL, navigationAction in
                guard let self = self else { return }
                if self.isNewTargetBlankRequest(navigationAction: navigationAction) {
                    self.delegate?.tab(self, didRequestNewTabForUrl: newURL, openedByPage: true)
                } else {
                    self.load(url: newURL)
                }
            },
                                                                           policyDecisionHandler: decisionHandler)
            
            if didRewriteLink {
                return
            }
        }

        if navigationAction.isTargetingMainFrame(),
           !(navigationAction.request.url?.isCustomURLScheme() ?? false),
           navigationAction.navigationType != .backForward,
           let request = requestForDoNotSell(basedOn: navigationAction.request) {
            
            decisionHandler(.cancel)
            load(urlRequest: request)
            return
        }
            
        if navigationAction.navigationType == .linkActivated,
           let url = navigationAction.request.url,
           let modifierFlags = delegate?.tabWillRequestNewTab(self) {

            if modifierFlags.contains(.command) {
                if modifierFlags.contains(.shift) {
                    decisionHandler(.cancel)
                    delegate?.tab(self, didRequestNewTabForUrl: url, openedByPage: false)
                    return
                } else {
                    decisionHandler(.cancel)
                    delegate?.tab(self, didRequestNewBackgroundTabForUrl: url)
                    return
                }
            }
        }
        
        decidePolicyFor(navigationAction: navigationAction) { [weak self] decision in
            if let url = navigationAction.request.url, decision != .cancel {
                if let isDdg = self?.appUrls.isDuckDuckGoSearch(url: url), isDdg {
                    StatisticsLoader.shared.refreshSearchRetentionAtb()
                }
                
                self?.findInPage?.done()
            }
            decisionHandler(decision)
        }
    }
    // swiftlint:enable function_body_length
    // swiftlint:enable cyclomatic_complexity
    
    private func waitUntilRulesAreCompiled() {
        if contentBlockingAssetsInstalled {
            rulesCompilationMonitor.reportNavigationDidNotWaitForRules()
        } else {
            rulesCompilationMonitor.tabWillWaitForRulesCompilation(self)
            showProgressIndicator()
            if let rulesCompiledCondition = rulesCompiledCondition {
                RunLoop.current.run(until: rulesCompiledCondition)
            }
        }
        rulesCompilationMonitor.reportTabFinishedWaitingForRules(self)
    }
    
    private func decidePolicyFor(navigationAction: WKNavigationAction, completion: @escaping (WKNavigationActionPolicy) -> Void) {
        let allowPolicy = determineAllowPolicy()
        
        let tld = storageCache.tld
        
        if navigationAction.isTargetingMainFrame()
            && tld.domain(navigationAction.request.mainDocumentURL?.host) != tld.domain(lastUpgradedURL?.host) {
            lastUpgradedURL = nil
        }
        
        guard navigationAction.request.mainDocumentURL != nil else {
            completion(allowPolicy)
            return
        }
        
        guard let url = navigationAction.request.url else {
            completion(allowPolicy)
            return
        }

        if url.isBookmarklet() {
            completion(.cancel)

            if let js = url.toDecodedBookmarklet() {
                webView.evaluateJavaScript(js)
            }
            return
        }
        
        let schemeType = SchemeHandler.schemeType(for: url)
        
        switch schemeType {
        case .navigational:
            performNavigationFor(url: url,
                                 navigationAction: navigationAction,
                                 allowPolicy: allowPolicy,
                                 completion: completion)
            
        case .external(let action):
            performExternalNavigationFor(url: url, action: action)
            completion(.cancel)
            
        case .unknown:
            if navigationAction.navigationType == .linkActivated {
                openExternally(url: url)
            } else {
                presentOpenInExternalAppAlert(url: url)
            }
            completion(.cancel)
        }
    }
    
    private func performNavigationFor(url: URL,
                                      navigationAction: WKNavigationAction,
                                      allowPolicy: WKNavigationActionPolicy,
                                      completion: @escaping (WKNavigationActionPolicy) -> Void) {
        
        if shouldReissueSearch(for: url) {
            reissueSearchWithRequiredParams(for: url)
            completion(.cancel)
            return
        }
        
        if shouldReissueDDGStaticNavigation(for: url) {
            reissueNavigationWithSearchHeaderParams(for: url)
            completion(.cancel)
            return
        }
        
        if isNewTargetBlankRequest(navigationAction: navigationAction) {
            delegate?.tab(self, didRequestNewTabForUrl: url, openedByPage: true)
            completion(.cancel)
            return
        }
        
        if allowPolicy != WKNavigationActionPolicy.cancel {
            userAgentManager.update(webView: webView, isDesktop: tabModel.isDesktop, url: url)
        }
        
        if !ContentBlocking.privacyConfigurationManager.privacyConfig.isProtected(domain: url.host) {
            completion(allowPolicy)
            return
        }

        if shouldUpgradeToHttps(url: url, navigationAction: navigationAction) {
            upgradeToHttps(url: url, allowPolicy: allowPolicy, completion: completion)
        } else {
            completion(allowPolicy)
        }
    }
    
    private func upgradeToHttps(url: URL,
                                allowPolicy: WKNavigationActionPolicy,
                                completion: @escaping (WKNavigationActionPolicy) -> Void) {
        Task {
            let result = await PrivacyFeatures.httpsUpgrade.upgrade(url: url)
            switch result {
            case let .success(upgradedUrl):
                if lastUpgradedURL != upgradedUrl {
                    NetworkLeaderboard.shared.incrementHttpsUpgrades()
                    lastUpgradedURL = upgradedUrl
                    load(url: upgradedUrl, didUpgradeURL: true)
                    completion(.cancel)
                } else {
                    completion(allowPolicy)
                }
            case .failure:
                completion(allowPolicy)
            }
        }
    }
    
    private func shouldUpgradeToHttps(url: URL, navigationAction: WKNavigationAction) -> Bool {
        return !failingUrls.contains(url.host ?? "") && navigationAction.isTargetingMainFrame()
    }

    private func performExternalNavigationFor(url: URL, action: SchemeHandler.Action) {
        switch action {
        case .open:
            openExternally(url: url)
        case .askForConfirmation:
            presentOpenInExternalAppAlert(url: url)
        case .cancel:
            break
        }
    }
    
    private func isNewTargetBlankRequest(navigationAction: WKNavigationAction) -> Bool {
        return navigationAction.navigationType == .linkActivated && navigationAction.targetFrame == nil
    }

    private func determineAllowPolicy() -> WKNavigationActionPolicy {
        let allowWithoutUniversalLinks = WKNavigationActionPolicy(rawValue: WKNavigationActionPolicy.allow.rawValue + 2) ?? .allow
        return AppUserDefaults().allowUniversalLinks ? .allow : allowWithoutUniversalLinks
    }
    
    private func showErrorNow() {
        guard let error = lastError as NSError? else { return }
        hideProgressIndicator()
        ViewHighlighter.hideAll()

        if !(error.failedUrl?.isCustomURLScheme() ?? false) {
            url = error.failedUrl
            showError(message: error.localizedDescription)
        }

        webpageDidFailToLoad()
        checkForReloadOnError()
    }
    
    @available(iOS 14.0, *)
    private func showLoginDetails(with account: SecureVaultModels.WebsiteAccount) {
        if let navController = SettingsViewController.loadFromStoryboard() as? UINavigationController,
           let settingsController = navController.topViewController as? SettingsViewController {
            settingsController.loadViewIfNeeded()
            
            settingsController.showAutofillAccountDetails(account, animated: false)
            self.present(navController, animated: true)
        }
    }
    
    @objc private func dismissLoginDetails() {
        dismiss(animated: true)
    }
}

extension TabViewController: PrivacyProtectionDelegate {
    func omniBarTextTapped() {
        chromeDelegate?.omniBar.becomeFirstResponder()
    }
}

extension TabViewController: WKUIDelegate {

    public func webView(_ webView: WKWebView,
                        createWebViewWith configuration: WKWebViewConfiguration,
                        for navigationAction: WKNavigationAction,
                        windowFeatures: WKWindowFeatures) -> WKWebView? {
        return delegate?.tab(self, didRequestNewWebViewWithConfiguration: configuration, for: navigationAction)
    }

    func webViewDidClose(_ webView: WKWebView) {
        if openedByPage {
            delegate?.tabDidRequestClose(self)
        }
    }

    public func webViewWebContentProcessDidTerminate(_ webView: WKWebView) {
        Pixel.fire(pixel: .webKitDidTerminate)
        delegate?.tabContentProcessDidTerminate(tab: self)
    }
    
     func webView(_ webView: WKWebView,
                  runJavaScriptAlertPanelWithMessage message: String,
                  initiatedByFrame frame: WKFrameInfo,
                  completionHandler: @escaping () -> Void) {
        if canDisplayJavaScriptAlert {
            let alertController = WebJSAlert(message: message, alertType: .alert(handler: { [weak self] blockAlerts in
                self?.shouldBlockJSAlert = blockAlerts
                completionHandler()
            })).createAlertController()
            
            self.present(alertController, animated: true, completion: nil)
        } else {
            completionHandler()
        }
     }

     func webView(_ webView: WKWebView,
                  runJavaScriptConfirmPanelWithMessage message: String,
                  initiatedByFrame frame: WKFrameInfo,
                  completionHandler: @escaping (Bool) -> Void) {
        
        if canDisplayJavaScriptAlert {
            let alertController = WebJSAlert(message: message,
                                             alertType: .confirm(handler: { [weak self] blockAlerts, confirm in
                self?.shouldBlockJSAlert = blockAlerts
                completionHandler(confirm)
            })).createAlertController()
            
            self.present(alertController, animated: true, completion: nil)
        } else {
            completionHandler(false)
        }
     }
    
    func webView(_ webView: WKWebView,
                 runJavaScriptTextInputPanelWithPrompt prompt: String,
                 defaultText: String?,
                 initiatedByFrame frame: WKFrameInfo,
                 completionHandler: @escaping (String?) -> Void) {
        if canDisplayJavaScriptAlert {
            let alertController = WebJSAlert(message: prompt,
                                             alertType: .text(handler: { [weak self] blockAlerts, text in
                
                self?.shouldBlockJSAlert = blockAlerts
                completionHandler(text)
            }, defaultText: defaultText)).createAlertController()
            
            self.present(alertController, animated: true, completion: nil)
        } else {
            completionHandler(nil)
        }
    }
}

extension TabViewController: UIPopoverPresentationControllerDelegate {

    func adaptivePresentationStyle(for controller: UIPresentationController, traitCollection: UITraitCollection) -> UIModalPresentationStyle {
        return .none
    }
}

extension TabViewController: UIGestureRecognizerDelegate {
    func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        if isShowBarsTap(gestureRecognizer) {
            return true
        }
        if gestureRecognizer == longPressGestureRecognizer {
            let x = Int(gestureRecognizer.location(in: webView).x)
            let y = Int(gestureRecognizer.location(in: webView).y)
            let url = documentScript.getUrlAtPointSynchronously(x: x, y: y)
            return url != nil
        }
        return false
    }

    private func isShowBarsTap(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        let y = gestureRecognizer.location(in: webView).y
        return gestureRecognizer == showBarsTapGestureRecogniser && chromeDelegate?.isToolbarHidden == true && isBottom(yPosition: y)
    }

    private func isBottom(yPosition y: CGFloat) -> Bool {
        guard let chromeDelegate = chromeDelegate else { return false }
        return y > (view.frame.size.height - chromeDelegate.toolbarHeight)
    }

    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldBeRequiredToFailBy otherRecognizer: UIGestureRecognizer) -> Bool {
        guard gestureRecognizer == showBarsTapGestureRecogniser || gestureRecognizer == longPressGestureRecognizer else {
            return false
        }

        if gestureRecognizer == showBarsTapGestureRecogniser,
            otherRecognizer is UITapGestureRecognizer {
            return true
        } else if gestureRecognizer == longPressGestureRecognizer,
            otherRecognizer is UILongPressGestureRecognizer || String(describing: otherRecognizer).contains("action=_highlightLongPressRecognized:") {
            return true
        }
        
        return false
    }

    func requestFindInPage() {
        guard findInPage == nil else { return }
        findInPage = FindInPage(webView: webView)
        delegate?.tabDidRequestFindInPage(tab: self)
    }

    func refresh() {
        requeryLogic.onRefresh()
        if isError {
            if let url = URL(string: chromeDelegate?.omniBar.textField.text ?? "") {
                load(url: url)
            }
        } else {
            reload(scripts: false)
        }
    }
}

extension TabViewController: ContentBlockerRulesUserScriptDelegate {
    
    func contentBlockerRulesUserScriptShouldProcessTrackers(_ script: ContentBlockerRulesUserScript) -> Bool {
        return siteRating?.isFor(self.url) ?? false
    }
    
    func contentBlockerRulesUserScriptShouldProcessCTLTrackers(_ script: ContentBlockerRulesUserScript) -> Bool {
        return false
    }

    func contentBlockerRulesUserScript(_ script: ContentBlockerRulesUserScript,
                                       detectedTracker tracker: DetectedTracker) {
        userScriptDetectedTracker(tracker)
    }

    fileprivate func userScriptDetectedTracker(_ tracker: DetectedTracker) {
        if tracker.blocked && fireWoFollowUp {
            fireWoFollowUp = false
            Pixel.fire(pixel: .daxDialogsWithoutTrackersFollowUp)
        }

        siteRating?.trackerDetected(tracker)
        onSiteRatingChanged()

        if !pageHasTrackers {
            NetworkLeaderboard.shared.incrementPagesWithTrackers()
            pageHasTrackers = true
        }

        if let networkName = tracker.knownTracker?.owner?.name {
            if !trackerNetworksDetectedOnPage.contains(networkName) {
                trackerNetworksDetectedOnPage.insert(networkName)
                NetworkLeaderboard.shared.incrementDetectionCount(forNetworkNamed: networkName)
            }
            NetworkLeaderboard.shared.incrementTrackersCount(forNetworkNamed: networkName)
        }
    }
}

extension TabViewController: SurrogatesUserScriptDelegate {

    func surrogatesUserScriptShouldProcessTrackers(_ script: SurrogatesUserScript) -> Bool {
        return siteRating?.isFor(self.url) ?? false
    }

    func surrogatesUserScript(_ script: SurrogatesUserScript,
                              detectedTracker tracker: DetectedTracker,
                              withSurrogate host: String) {
        if siteRating?.url.absoluteString == tracker.pageUrl {
            siteRating?.surrogateInstalled(host)
        }
        userScriptDetectedTracker(tracker)
    }

}

extension TabViewController: FaviconUserScriptDelegate {
    
    func faviconUserScriptDidRequestCurrentHost(_ script: FaviconUserScript) -> String? {
        return webView.url?.host
    }
    
    func faviconUserScript(_ script: FaviconUserScript, didFinishLoadingFavicon image: UIImage) {
        tabModel.didUpdateFavicon()
    }
    
}

extension TabViewController: PrintingUserScriptDelegate {

    func printingUserScriptDidRequestPrintController(_ script: PrintingUserScript) {
        let controller = UIPrintInteractionController.shared
        controller.printFormatter = webView.viewPrintFormatter()
        controller.present(animated: true, completionHandler: nil)
    }

}

extension TabViewController: EmailManagerAliasPermissionDelegate {

    func emailManager(_ emailManager: EmailManager,
                      didRequestPermissionToProvideAliasWithCompletion completionHandler: @escaping (EmailManagerPermittedAddressType) -> Void) {

        DispatchQueue.main.async {
            let alert = UIAlertController(title: UserText.emailAliasAlertTitle, message: nil, preferredStyle: .actionSheet)
            alert.overrideUserInterfaceStyle()

            var pixelParameters: [String: String] = [:]

            if let cohort = emailManager.cohort {
                pixelParameters[PixelParameters.emailCohort] = cohort
            }

            if let userEmail = emailManager.userEmail {
                let actionTitle = String(format: UserText.emailAliasAlertUseUserAddress, userEmail)
                alert.addAction(title: actionTitle) {
                    pixelParameters[PixelParameters.emailLastUsed] = emailManager.lastUseDate
                    emailManager.updateLastUseDate()

                    Pixel.fire(pixel: .emailUserPressedUseAddress, withAdditionalParameters: pixelParameters, includedParameters: [])

                    completionHandler(.user)
                }
            }

            alert.addAction(title: UserText.emailAliasAlertGeneratePrivateAddress) {
                pixelParameters[PixelParameters.emailLastUsed] = emailManager.lastUseDate
                emailManager.updateLastUseDate()

                Pixel.fire(pixel: .emailUserPressedUseAlias, withAdditionalParameters: pixelParameters, includedParameters: [])

                completionHandler(.generated)
            }

            alert.addAction(title: UserText.emailAliasAlertDecline) {
                Pixel.fire(pixel: .emailTooltipDismissed, withAdditionalParameters: pixelParameters, includedParameters: [])

                completionHandler(.none)
            }

            if UIDevice.current.userInterfaceIdiom == .pad {
                // make sure the completion handler is called if the alert is dismissed by tapping outside the alert
                alert.addAction(title: "", style: .cancel) {
                    Pixel.fire(pixel: .emailTooltipDismissed, withAdditionalParameters: pixelParameters)
                    completionHandler(.none)
                }
            }

            alert.popoverPresentationController?.permittedArrowDirections = []
            alert.popoverPresentationController?.delegate = self
            let bounds = self.view.bounds
            let point = Point(x: Int((bounds.maxX - bounds.minX) / 2.0), y: Int(bounds.maxY))
            self.present(controller: alert, fromView: self.view, atPoint: point)
        }

    }

}

extension TabViewController: EmailManagerRequestDelegate {

    // swiftlint:disable function_parameter_count
    func emailManager(_ emailManager: EmailManager,
                      requested url: URL,
                      method: String,
                      headers: [String: String],
                      parameters: [String: String]?,
                      httpBody: Data?,
                      timeoutInterval: TimeInterval,
                      completion: @escaping (Data?, Error?) -> Void) {
        APIRequest.request(url: url,
                           method: APIRequest.HTTPMethod(rawValue: method) ?? .post,
                           parameters: parameters,
                           headers: headers,
                           httpBody: httpBody,
                           timeoutInterval: timeoutInterval) { response, error in
            
            completion(response?.data, error)
        }
    }
    // swiftlint:enable function_parameter_count

}

extension TabViewController: Themable {

    func decorate(with theme: Theme) {
        view.backgroundColor = theme.backgroundColor
        error?.backgroundColor = theme.backgroundColor
        errorHeader.textColor = theme.barTintColor
        errorMessage.textColor = theme.barTintColor
        
        switch theme.currentImageSet {
        case .light:
            errorInfoImage?.image = UIImage(named: "ErrorInfoLight")
        case .dark:
            errorInfoImage?.image = UIImage(named: "ErrorInfoDark")
        }
    }
    
}

extension NSError {

    var failedUrl: URL? {
        return userInfo[NSURLErrorFailingURLErrorKey] as? URL
    }
    
}

extension TabViewController: SecureVaultManagerDelegate {
 
    private func presentSavePasswordModal(with vault: SecureVaultManager, credentials: SecureVaultModels.WebsiteCredentials) {
        if !isAutofillEnabled {
            return
        }
        
        let manager = SaveAutofillLoginManager(credentials: credentials, vaultManager: vault, autofillScript: autofillUserScript)
        manager.prepareData { [weak self] in

            let saveLoginController = SaveLoginViewController(credentialManager: manager)
            saveLoginController.delegate = self
            if #available(iOS 15.0, *) {
                if let presentationController = saveLoginController.presentationController as? UISheetPresentationController {
                    presentationController.detents = [.medium(), .large()]
                }
            }
            self?.present(saveLoginController, animated: true, completion: nil)
        }
    }
    
    func secureVaultInitFailed(_ error: SecureVaultError) {
        SecureVaultErrorReporter.shared.secureVaultInitFailed(error)
    }
    
    func secureVaultManager(_ vault: SecureVaultManager, promptUserToStoreAutofillData data: AutofillData) {
        if let credentials = data.credentials, isAutofillEnabled {
            presentSavePasswordModal(with: vault, credentials: credentials)
        }
    }
    
    func secureVaultManager(_: SecureVaultManager,
                            promptUserToAutofillCredentialsForDomain domain: String,
                            withAccounts accounts: [SecureVaultModels.WebsiteAccount],
                            completionHandler: @escaping (SecureVaultModels.WebsiteAccount?) -> Void) {
  
        if !isAutofillEnabled {
            completionHandler(nil)
            return
        }
        
        if #available(iOS 14, *), accounts.count > 0 {
            if !AutofillLoginPromptViewController.canAuthenticate {
                Pixel.fire(pixel: .autofillLoginsFillLoginInlineAuthenticationDeviceAuthUnavailable)
                completionHandler(nil)
                return
            }
            
            let autofillPromptViewController = AutofillLoginPromptViewController(accounts: accounts) { account in
                completionHandler(account)
            }
            
            if #available(iOS 15.0, *) {
                if let presentationController = autofillPromptViewController.presentationController as? UISheetPresentationController {
                    presentationController.detents = accounts.count > 3 ? [.medium(), .large()] : [.medium()]
                }
            }
            self.present(autofillPromptViewController, animated: true, completion: nil)
        } else {
            completionHandler(nil)
        }
    }

    func secureVaultManager(_: SecureVaultManager, didAutofill type: AutofillType, withObjectId objectId: Int64) {
        // No-op, don't need to do anything here
    }
    
    func secureVaultManagerShouldAutomaticallyUpdateCredentialsWithoutUsername(_: SecureVaultManager) -> Bool {
        false
    }
    
    // swiftlint:disable:next identifier_name
    func secureVaultManager(_: SecureVaultManager, didRequestAuthenticationWithCompletionHandler: @escaping (Bool) -> Void) {
        // We don't have auth yet
    }
}

extension TabViewController: SaveLoginViewControllerDelegate {

    private func saveCredentials(_ credentials: SecureVaultModels.WebsiteCredentials, withSuccessMessage message: String) {
        do {
            let credentialID = try SaveAutofillLoginManager.saveCredentials(credentials,
                                                                            with: SecureVaultFactory.default)
            
            let vault = try SecureVaultFactory.default.makeVault(errorReporter: SecureVaultErrorReporter.shared)
            
            if let newCredential = try vault.websiteCredentialsFor(accountId: credentialID) {
                DispatchQueue.main.async {
                    ActionMessageView.present(message: message,
                                              actionTitle: UserText.autofillLoginSaveToastActionButton) {
                        
                        if #available(iOS 14.0, *) {
                            self.showLoginDetails(with: newCredential.account)
                        }
                    }
                }
            }
        } catch {
            os_log("%: failed to store credentials %s", type: .error, #function, error.localizedDescription)
        }
    }
    
    func saveLoginViewController(_ viewController: SaveLoginViewController, didSaveCredentials credentials: SecureVaultModels.WebsiteCredentials) {
        viewController.dismiss(animated: true)
        saveCredentials(credentials, withSuccessMessage: UserText.autofillLoginSavedToastMessage)
    }
    
    func saveLoginViewController(_ viewController: SaveLoginViewController, didUpdateCredentials credentials: SecureVaultModels.WebsiteCredentials) {
        viewController.dismiss(animated: true)
        saveCredentials(credentials, withSuccessMessage: UserText.autofillLoginUpdatedToastMessage)
    }
    
    func saveLoginViewControllerDidCancel(_ viewController: SaveLoginViewController) {
        viewController.dismiss(animated: true)
    }
}

extension TabViewController: ConfirmBottomSheetDelegate{
    func onFail(callbackId: Int) {
        notifyFinish(callbackId: callbackId, value: .failure(.requestRejected))
    }
    
    func onSuccess(callbackId: Int, hexMsg: String) {
        let data = Data(_hex: hexMsg)
        let callback = DappCallback(id: callbackId, value: .signTransaction(data))
        notifyFinish(callbackId: callbackId, value: .success(callback))
    }
    
}

// swiftlint:enable file_length

extension TabViewController: WKScriptMessageHandler {
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        guard let command = DappAction.fromMessage(message) else {
            if message.name == Browser.locationChangedEventName {
                recordUrlSubject.send(())
            }
            return
        }
        infoLog("1991 [Browser] dapp command: \(command)")
        let action = DappAction.fromCommand(command, server: server, transactionType: .prebuilt(server))
        infoLog("1991 [Browser] dapp action: \(action)")
        dappActionSubject.send((action: action, callbackId: command.id))
    }
}


extension AlphaWallet.Address {
    static func make(address: String = "0x1000000000000000000000000000000000000000") -> AlphaWallet.Address {
        return AlphaWallet.Address(string: address)!
    }

    static func makeStormBird(address: String = "0x007bEe82BDd9e866b2bd114780a47f2261C684E3") -> AlphaWallet.Address {
        return AlphaWallet.Address(string: address)!
    }
}

struct BrowserViewModelInput {
    let progress: AnyPublisher<Double, Never>
//    let decidePolicy: AnyPublisher<BrowserViewModel.DecidePolicy, Never>
}

struct BrowserViewModelOutput {
//    let progressBarState: AnyPublisher<BrowserViewModel.ProgressBarState, Never>
    let universalLink: AnyPublisher<URL, Never>
//    let recordUrl: AnyPublisher<Void, Never>
    let dappAction: AnyPublisher<(action: DappAction, callbackId: Int), Never>
//    let keyboardAction: AnyPublisher<BrowserViewModel.KeyboardAction, Never>
}
