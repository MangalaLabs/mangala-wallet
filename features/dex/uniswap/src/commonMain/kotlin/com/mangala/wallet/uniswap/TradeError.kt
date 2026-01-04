package com.mangala.wallet.uniswap

sealed class TradeError : Throwable() {
    class TradeNotFound : TradeError()
}

sealed class TokenAmountError : Throwable() {
    class NegativeAmount : TokenAmountError()
}

sealed class PairError : Throwable() {
    class NotInvolvedToken : PairError()
    class InsufficientReserves : PairError()
    class InsufficientReserveOut : PairError()
}

sealed class RouteError : Throwable() {
    class EmptyPairs : RouteError()
    class InvalidPair(val index: Int) : RouteError()
}
