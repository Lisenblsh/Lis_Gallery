package com.lis.connecttosmb

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.share.DiskShare
import java.io.File

interface RemoteStorage {

    suspend fun uploadToRemote(listPath: List<String>)

    fun downloadFromRemote(pathFrom: String, pathTo: String): Boolean

    fun getFolderAndFileInRemote(path: String = ""): List<FileIdBothDirectoryInformation>
}