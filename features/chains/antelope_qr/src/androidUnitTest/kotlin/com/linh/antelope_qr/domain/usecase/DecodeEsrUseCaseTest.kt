package com.linh.antelope_qr.domain.usecase

import com.memtrip.eos.chain.actions.transaction.esr.EsrInfoArgs
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DecodeEsrUseCaseTest() {

    val sut = DecodeEsrUseCase()

    @Test
    fun `Given an ESR transaction signing request with compression, when decode, then get correct payload`() {
        val uri =
            "esr:gmMsfmIRpc7x7DpLh8nvg-zz9VdvrLYRihbJ-mIxXW5CYY4vEwMKYASTrwxCQZRH8V47RoGZc7L8uNZnggRWvDUyUoAJwGl1iFYWV_9gMIPJOqOkpKDYSl8_OUkvMS85I79ILyczL1vfxCTFNNXcIFHXPDnZVNfE3DxJ18IsyUzX0tQg0dIixcjQNC2FkQWk9CTIHVcYeTJtGJg1l6jOW5nmccjHufDzJtMDKdfXCIg9W7UrRm9n2uHJ9bLGcxkdwXb4gKww1jPTM1BwKsovL04tCilKzCsuyC8qgQr75ldl5uQk6psC2Rq-icmZeSX5xRnWCp55Jak5CkABBf9ghQgFQ4N4Q9N4c00Fx4KCnNTw1CTvzBJ9U2NzPWMzBQ1vjxBfHx2FnMzsVAX31OTsfE0F54yi_NxUfUMjcz0DEFQITkxLLMqEagEA"

        val result = sut(uri)

        assertEquals(1, result.transaction!!.actions.size)
        assertEquals(
            "10999c6a4e0aaf6910999c6a4e0aaf69102700000000000004454f5300000000",
            result.transaction!!.actions[0].data
        )
        assertEquals(false, result.shouldBroadcast)
        assertEquals(true, result.shouldPerformBackgroundCallback)
    }

    @Test
    fun `Given an ESR identity request with compression, when decode, then get correct payload`() {
        val uri =
            "esr:g2MsfmIRpc7x7DpLh8nvg-zz9VdvrLYRihbJ-mIxXW5CYY4vMwMDwxVGnkwbBibrjJKSgmIrff3kJL3EvOSM_CK9nMy8bP201OQ0U2PTZF0LIwMzXZNEQ1Ndi0RzCyAr2cg0zdDCIDktiYkFpPQkI9w0ZgeNO_s5q3hXnNggF2zMm74zPs59ab7uffFvsqc77DiPis9ldATb4QOywljPTM9Awakov7w4tSikKDGvuCC_qAQq7JtflZmTk6hvCmRr-CYmZ-aV5BdnWCt45pWk5igABRT8gxUiFAwN4g1N4801FRwLCnJSw1OTvDNL9E2NzfWMzRQ0vD1CfH10FHIys1MV3FOTs_M1FZwzivJzU_UNjcz1DEBQITgxLbEoE6qFtTg5vyCVIyknP7tYLzMfAA"

        val result = sut(uri)

        assertNull(result.identityRequest?.permission)
        assertEquals(false, result.shouldBroadcast)
        assertEquals(true, result.shouldPerformBackgroundCallback)
    }

    @Test
    fun `Given an ESR identity request with Anchor Link data, when decode, then get correct Anchor Link data`() {
        val uri =
            "esr:g2MsfmIRpc7x7DpLh8nvg-zz9VdvrLYRihbJ-mIxXW5CYY4vMwMDwxVGnkwbBibrjJKSgmIrff3kJL3EvOSM_CK9nMy8bP3ERMtEi6S0NN2kpDQzXRMLcwvdpJREU13jlOREAzNDIzPDNEsmFpDSk4wI0652rNtg3BckNyvZ66z7Z-FvbWYHC3vPcocdn6a2-d2HFZpzGR3BdviArDDWM9MzUHAqyi8vTi0KKUrMKy7ILyqBCvvmV2Xm5CTqmwLZGr6JyZl5JfnFGdYKnnklqTkKQAEF_2CFCAVDg3hD03hzTQXHgoKc1PDUJO_MEn1TY3M9YzMFDW-PEF8fHYWczOxUBffU5Ox8TQXnjKL83FR9QyMLPQMQVAhOTEssyoRqYS1Ozi9I5UjKyc8u1svMBwA"

        val result = sut(uri)

        val link = result.info.find { it is EsrInfoArgs.Link } as EsrInfoArgs.Link

        assertEquals(
            link.requestKey.toString(),
            "PUB_K1_6WXnpuXeSmqkrkfGDfwrbfgeR3LZeAzzP2f3oKA4ET3TZXWrEW"
        )
        assertEquals(link.sessionName, "bloks.io")
    }

    @Test
    fun `Given an ESR signing request decoded from Anchor Link, when decode, then get correct data`() {
        val uri =
            "esr:gmMsfmIRpc7x7DpLh8nvg-zz9VdvrLYRihbJ-mIxXW5CYY4vEwMKYASTrwxCQZRH8V47xgZTtuue033ugARWvDUyUoAJwGgBdYhWFlf_YDCDyTqjpKSg2EpfPzlJLzEvOSO_SC8nMy9bP9XYODUxzSJJ19jEzEzXJNUwUdfSzMJSNyUx0djMwiDJIjHViJEFpJTl99praQA"

        val result = sut(uri)

        println(result)
    }
}