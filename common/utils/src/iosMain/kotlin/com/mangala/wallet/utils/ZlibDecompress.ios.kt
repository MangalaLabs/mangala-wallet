package com.mangala.wallet.utils

import kotlinx.cinterop.*
import platform.darwin.COMPRESSION_ZLIB
import platform.darwin.compression_encode_buffer

@OptIn(ExperimentalForeignApi::class)
actual fun ByteArray.decompressRawZlib(): ByteArray {
    return try {
        memScoped {
            val inputData = this@decompressRawZlib
            val destinationBuffer = allocArray<UByteVar>(capacity)

            val newSize = compression_encode_buffer(
                destinationBuffer,
                capacity.convert(),
                inputData.toUByteArray().toCValues(), inputData.size.convert(),
                null,
                COMPRESSION_ZLIB
            )

            return destinationBuffer.readBytes(newSize.convert())
        }
    } catch (e: Exception) {
        byteArrayOf()
    }

//    memScoped {
//        val strm: z_stream = alloc()
//
//        var ret: Int
//
//        val inpArray = ByteArray(CHUNK)
//        val outArray = ByteArray(CHUNK)
//
//        var readCount = 0
//        var writeCount = 0
//        var inflateCount = 0
//
//        try {
//            inpArray.usePinned { _inp ->
//                outArray.usePinned { _out ->
//                    val inp = _inp.addressOf(0)
//                    val out = _out.addressOf(0)
//                    var tempInputSize = 0
//                    memset(strm.ptr, 0, z_stream.size.convert())
//                    ret = inflateInit2_(strm.ptr, -windowBits, zlibVersion()?.toKString(), sizeOf<z_stream>().toInt());
//                    if (ret != Z_OK) error("Invalid inflateInit2_")
//
//                    do {
//                        //println("strm.avail_in: ${strm.avail_in}")
//                        strm.avail_in = input.read(inpArray, 0, CHUNK).convert()
//                        readCount++
//                        if (strm.avail_in == 0u || strm.avail_in > CHUNK.convert()) break
//                        tempInputSize = strm.avail_in.convert()
//                        strm.next_in = inp.reinterpret()
//
//                        do {
//                            strm.avail_out = CHUNK.convert()
//                            strm.next_out = out.reinterpret()
//                            inflateCount++
//                            ret = platform.zlib.inflate(strm.ptr, platform.zlib.Z_NO_FLUSH)
//                            check(ret != Z_STREAM_ERROR)
//                            when (ret) {
//                                Z_NEED_DICT -> ret = Z_DATA_ERROR
//                                Z_DATA_ERROR -> error("data error")
//                                Z_MEM_ERROR  -> error("mem error")
//                            }
//                            val have = CHUNK - strm.avail_out.toInt()
//                            //buffer.write(outArray, 0, have)
//                            //flush(false)
//                            output.write(outArray, 0, have)
//                            writeCount++
//                        } while (strm.avail_out == 0u)
//                    } while (ret != Z_STREAM_END)
//
//                    // Return read bytes that were not consumed
//                    val remaining = strm.avail_in.toInt()
//                    if (remaining > 0) {
//                        input.returnToBuffer(inpArray, tempInputSize - remaining, remaining)
//                        //error("too much data in DeflateNative stream")
//                    }
//
//                    //flush(true)
//                }
//            }
//        } finally {
//            inflateEnd(strm.ptr)
//        }
}

private val capacity: Long = 10_000_000 /* 10 MB, to be tuned */
