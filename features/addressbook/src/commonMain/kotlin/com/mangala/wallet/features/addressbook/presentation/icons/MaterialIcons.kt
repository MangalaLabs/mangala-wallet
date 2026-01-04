package com.mangala.wallet.features.addressbook.presentation.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Extension to add Material Icons that are not available in the Compose Material Icons library
 */
object MaterialIconsExtended {

    val Icons.Filled.QrCode: ImageVector
        get() {
            if (_qrCode != null) {
                return _qrCode!!
            }
            _qrCode = materialIcon(name = "Filled.QrCode") {
                materialPath {
                    moveTo(3.0f, 3.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(3.0f)
                    verticalLineTo(3.0f)
                    close()
                    
                    moveTo(5.0f, 5.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(5.0f)
                    verticalLineTo(5.0f)
                    close()
                    
                    moveTo(13.0f, 3.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(3.0f)
                    close()
                    
                    moveTo(15.0f, 5.0f)
                    horizontalLineTo(19.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(5.0f)
                    close()
                    
                    moveTo(3.0f, 13.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(3.0f)
                    verticalLineTo(13.0f)
                    close()
                    
                    moveTo(5.0f, 15.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(5.0f)
                    verticalLineTo(15.0f)
                    close()
                    
                    moveTo(13.0f, 13.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(13.0f)
                    close()
                    
                    moveTo(15.0f, 15.0f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(19.0f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(15.0f)
                    close()
                }
            }
            return _qrCode!!
        }
    private var _qrCode: ImageVector? = null

    val Icons.Filled.FilterList: ImageVector
        get() {
            if (_filterList != null) {
                return _filterList!!
            }
            _filterList = materialIcon(name = "Filled.FilterList") {
                materialPath {
                    moveTo(10.0f, 18.0f)
                    horizontalLineTo(14.0f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(10.0f)
                    verticalLineTo(18.0f)
                    close()
                    
                    moveTo(3.0f, 6.0f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(6.0f)
                    horizontalLineTo(3.0f)
                    close()
                    
                    moveTo(6.0f, 13.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(6.0f)
                    verticalLineTo(13.0f)
                    close()
                }
            }
            return _filterList!!
        }
    private var _filterList: ImageVector? = null
}
