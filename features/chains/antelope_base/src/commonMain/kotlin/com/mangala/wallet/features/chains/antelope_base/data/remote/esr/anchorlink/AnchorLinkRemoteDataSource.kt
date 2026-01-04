package com.mangala.wallet.features.chains.antelope_base.data.remote.esr.anchorlink

class AnchorLinkRemoteDataSource(
    private val anchorLinkWebSocket: AnchorLinkWebSocket
) {
    suspend fun connect(url: String, onRead: (ByteArray) -> Unit, onClose: () -> Unit): Result<Unit> {
        return runCatching {
            anchorLinkWebSocket.start(
                url = url,
                onRead = onRead,
                onClose = onClose
            )
        }
    }
}