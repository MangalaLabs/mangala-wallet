package com.mangala.wallet.features.addressbook.presentation.components.calendar

import android.os.Bundle
import android.os.Parcel
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream

/**
 * Android-specific utility to check Bundle/Parcel sizes
 */
object BundleSizeChecker {
    
    private const val TAG = "BundleSizeChecker"
    
    fun checkBundleSize(bundle: Bundle): Int {
        val parcel = Parcel.obtain()
        try {
            bundle.writeToParcel(parcel, 0)
            val size = parcel.dataSize()
            Log.d(TAG, "Bundle size: $size bytes (${size / 1024.0} KB)")
            
            // Warning if approaching transaction limit
            if (size > 500_000) { // 500KB
                Log.w(TAG, "WARNING: Bundle size approaching 1MB transaction limit!")
            }
            
            return size
        } finally {
            parcel.recycle()
        }
    }
    
    fun checkSerializableSize(obj: Any): Int? {
        return try {
            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)
            oos.writeObject(obj)
            oos.close()
            
            val size = baos.size()
            Log.d(TAG, "${obj.javaClass.simpleName} serialized size: $size bytes")
            size
        } catch (e: Exception) {
            Log.e(TAG, "Failed to serialize: ${e.message}")
            null
        }
    }
    
    fun logNavigationStackSize() {
        // This would be called in your Activity
        val bundle = Bundle()
        
        // Simulate adding screens to bundle
        bundle.putString("screen_type", "CalendarBottomSheetScreen")
        bundle.putString("screen_id", "test_123")
        bundle.putString("existing_date_id", "date_001")
        bundle.putString("existing_date_title", "Birthday")
        bundle.putInt("existing_date_day", 15)
        bundle.putInt("existing_date_month", 8)
        bundle.putInt("existing_date_year", 2024)
        
        val size = checkBundleSize(bundle)
        Log.d(TAG, "CalendarBottomSheetScreen in Bundle: $size bytes")
    }
}