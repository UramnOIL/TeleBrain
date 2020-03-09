package com.uramnoil.telebrain.peeper

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import kotlin.coroutines.CoroutineContext

typealias OnNextLine = (handler: String) -> Unit

class ConsoleScanner(private val onNextLine: OnNextLine): CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private lateinit var inputStreamReader: InputStreamReader
    private lateinit var bufferedReader: BufferedReader

    private var isReading: Boolean = false

    fun read() {
        inputStreamReader = InputStreamReader(System.`in`)
        bufferedReader = BufferedReader(inputStreamReader)
        readEach()
        isReading = true
    }

    private fun readEach() = launch {
        while(isReading || job.isActive) {
            onNextLine.invoke(getNextLine())
        }
    }

    fun close() {
        inputStreamReader.close()
        bufferedReader.close()
        job.complete()
        isReading = false
    }

    private suspend fun getNextLine(): String = coroutineScope {
        withContext(Dispatchers.IO) {
            bufferedReader.readLine()
        }
    }
}

