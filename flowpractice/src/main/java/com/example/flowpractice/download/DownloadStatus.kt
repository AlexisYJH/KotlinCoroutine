package com.example.flowpractice.download

import java.io.File

/**
 * @author AlexisYin
 */
sealed class DownloadStatus{
    object None: DownloadStatus()
    data class Progress(val value: Int) : DownloadStatus()
    data class Error(val throwable: Throwable) : DownloadStatus()
    data class Done(val file: File) : DownloadStatus()
}
