package com.mangala.wallet.features.addressbook.presentation.contact.recent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.FilterNone
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.model.blockchain.BlockchainType
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource

/**
 * Đối tượng chứa các biểu tượng blockchain
 */
object BlockchainIcons {
    // Material Icons cho các blockchain chính (fallback)
    val BitcoinIcon = Icons.Default.AttachMoney
    val EthereumIcon = Icons.Default.Hexagon
    val BinanceIcon = Icons.Default.Diamond
    val SolanaIcon = Icons.Default.Bolt
    val AvalancheIcon = Icons.Default.AccountBalance
    val EosIcon = Icons.Default.Radar
    val DefaultIcon = Icons.Default.Water
    
    // Blockchain images from MOKO resources
    val Bitcoin = BlockchainType.Bitcoin.localImage
    val Ethereum = BlockchainType.Ethereum.localImage
    val BinanceSmartChain = BlockchainType.BinanceSmartChain.localImage
    val Solana = BlockchainType.Solana.localImage
    val Avalanche = BlockchainType.Avalanche.localImage
    val Eos = BlockchainType.EosEvm.localImage
    
    /**
     * Lấy icon ImageVector dựa trên ký hiệu blockchain (Material Icons)
     * @param symbol Ký hiệu blockchain (ETH, BTC, v.v.)
     * @return ImageVector biểu tượng tương ứng
     */
    fun getIconVectorForSymbol(symbol: String): ImageVector {
        return when (symbol.uppercase()) {
            "BTC" -> BitcoinIcon
            "ETH" -> EthereumIcon
            "BSC", "BNB" -> BinanceIcon
            "SOL" -> SolanaIcon
            "AVAX" -> AvalancheIcon
            "EOS" -> EosIcon
            else -> DefaultIcon
        }
    }
    
    /**
     * Lấy biểu tượng ImageResource dựa trên ký hiệu blockchain (MOKO Resources)
     * @param symbol Ký hiệu blockchain (ETH, BTC, v.v.)
     * @return ImageResource biểu tượng tương ứng hoặc null
     */
    fun getImageResourceForSymbol(symbol: String): ImageResource? {
        return when (symbol.uppercase()) {
            "BTC" -> Bitcoin
            "ETH" -> Ethereum
            "BSC", "BNB" -> BinanceSmartChain
            "SOL" -> Solana
            "AVAX" -> Avalanche
            "EOS" -> Eos
            else -> null
        }
    }
    
    /**
     * Lấy biểu tượng dựa trên ký hiệu blockchain (backward compatibility)
     * @param symbol Ký hiệu blockchain (ETH, BTC, v.v.)
     * @return ImageResource biểu tượng tương ứng hoặc null
     */
    fun getIconForSymbol(symbol: String): ImageResource? {
        return getImageResourceForSymbol(symbol)
    }
    
    /**
     * Lấy màu chính tương ứng với blockchain
     * @param symbol Ký hiệu blockchain
     * @return Color màu chính
     */
    fun getColorForSymbol(symbol: String): Color {
        return when (symbol.uppercase()) {
            "BTC" -> Color(0xFFF7931A) // Bitcoin orange
            "ETH" -> Color(0xFF627EEA) // Ethereum blue
            "BSC", "BNB" -> Color(0xFFF3BA2F) // Binance yellow
            "MATIC" -> Color(0xFF8247E5) // Polygon purple
            "SOL" -> Color(0xFF00FFA3) // Solana green
            "AVAX" -> Color(0xFFE84142) // Avalanche red
            "FTM" -> Color(0xFF1969FF) // Fantom blue
            "EOS" -> Color(0xFF000000) // EOS black
            else -> Color(0xFF607D8B) // Default grey blue
        }
    }
    
    /**
     * Lấy màu nền phù hợp cho blockchain
     * @param symbol Ký hiệu blockchain
     * @return Color màu nền (với alpha thấp)
     */
    fun getBackgroundColorForSymbol(symbol: String): Color {
        return getColorForSymbol(symbol).copy(alpha = 0.15f)
    }
}

/**
 * Component hiển thị biểu tượng blockchain trong hộp tròn
 * @param symbol Ký hiệu blockchain
 * @param modifier Modifier tùy chọn
 * @param size Kích thước của hộp
 * @param iconSize Kích thước của biểu tượng
 */
//@Composable
//fun BlockchainIconBox(
//    symbol: String,
//    modifier: Modifier = Modifier,
//    size: Dp = 24.dp,
//    iconSize: Dp = 16.dp
//) {
//    val backgroundColor = BlockchainIcons.getBackgroundColorForSymbol(symbol)
//    val iconColor = BlockchainIcons.getColorForSymbol(symbol)
//    val icon = BlockchainIcons.getIconForSymbol(symbol)
//
//    Box(
//        modifier = modifier
//            .size(size)
//            .clip(RoundedCornerShape(4.dp))
//            .background(backgroundColor),
//        contentAlignment = Alignment.Center
//    ) {
//        Icon(
//            imageVector = icon,
//            contentDescription = "Blockchain $symbol",
//            tint = iconColor,
//            modifier = Modifier.size(iconSize)
//        )
//    }
//}

///**
// * Component hiển thị nhiều biểu tượng blockchain trong một hàng
// * @param symbols Danh sách ký hiệu blockchain
// * @param modifier Modifier tùy chọn
// */
//@Composable
//fun BlockchainIconsRow(
//    symbols: List<String>,
//    modifier: Modifier = Modifier
//) {
//    Row(modifier = modifier) {
//        symbols.forEachIndexed { index, symbol ->
//            if (index > 0) {
//                Spacer(modifier = Modifier.width(4.dp))
//            }
//
//            BlockchainIconBox(symbol = symbol)
//        }
//    }
//}
