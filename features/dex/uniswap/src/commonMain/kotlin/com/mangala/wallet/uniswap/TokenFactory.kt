package com.mangala.wallet.uniswap

import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.blockchain.Chain
import com.mangala.wallet.uniswap.domain.models.Token

class TokenFactory(chain: Chain) {
    private val wethAddress = getWethAddress(chain)

    sealed class UnsupportedChainError : Throwable() {
        object NoWethAddress : UnsupportedChainError()
    }

    fun etherToken(): Token {
        return Token.Ether(wethAddress)
    }

    fun token(contractAddress: Address, decimals: Int): Token {
        return Token.Erc20(contractAddress, decimals)
    }

    companion object {
        private fun getWethAddress(chain: Chain): Address {
            val wethAddressHex = when (chain) {
                Chain.Ethereum -> "0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2"
                Chain.Optimism -> "0x4200000000000000000000000000000000000006"
                Chain.BinanceSmartChain -> "0xbb4cdb9cbd36b01bd1cbaebf2de08d9173bc095c"
                Chain.BinanceSmartChainTestNet -> "0x094616F0BdFB0b526bD735Bf66Eca0Ad254ca81F"
                Chain.Polygon -> "0x0d500B1d8E8eF31E21C99d1Db9A6444d3ADf1270"
                Chain.Mumbai -> "0x0d500B1d8E8eF31E21C99d1Db9A6444d3ADf1270"
                Chain.Avalanche -> "0xB31f66AA3C1e785363F0875A1B74E27b85FD66c7"
                Chain.EthereumGoerli -> "0xB4FBF271143F4FBf7B91A5ded31805e42b2208d6"
                Chain.EthereumSepolia -> "0x7b79995e5f793A07Bc00c21412e50Ecae098E7f9"
                Chain.EthereumHolesky -> "" //TODO: LEONARD - Update this address(Request LinhNV support)
                Chain.EosEvm -> "0xc00592aA41D32D137dC480d9f6d0Df19b860104F"
                 else -> throw UnsupportedChainError.NoWethAddress
            }
            return Address(wethAddressHex)
        }
    }

}
