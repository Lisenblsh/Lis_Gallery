package com.lis.connecttosmb

import android.os.Environment
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.msdtyp.FileTime
import com.hierynomus.msfscc.fileinformation.FileBasicInformation
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.share.DiskShare
import com.hierynomus.smbj.utils.SmbFiles
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*

class RemoteStorageImpl(private val lifecycleScope: LifecycleCoroutineScope) : RemoteStorage {

    private var share: DiskShare? = null

    /**
     * @param hostName: String, ip of the server that contains the remote file storage
     * @param shareName: String,
     * @param userName: String,
     * @param password: String,
     * @param domain: String? = null
     * @return An established connection.
     * @throws IOException If the connection could not be established.
     */
    suspend fun create(
        hostName: String,
        shareName: String,
        userName: String,
        password: String,
        domain: String? = null
    ) {
        val job = lifecycleScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val client = SMBClient()
            val connection = client.connect(hostName)
            val ac = AuthenticationContext(userName, password.toCharArray(), domain)
            val session = connection.authenticate(ac)

            share = session.connectShare(shareName) as DiskShare
        }
        job.join()
    }

    val progress = MutableLiveData(0)
    val isComplete = MutableLiveData(false)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    override suspend fun uploadToRemote(listPath: List<String>) {
        val listFile = listPath.map { File(it) }
        val directoryName = if (listFile[0].parentFile?.name == null) {
            ""
        } else {
            "\\${listFile[0].parentFile?.name}"
        }
        val path = "Lis Gallery\\user$directoryName"
        val mkDir = lifecycleScope.launch(Dispatchers.IO) {
            if (!share!!.folderExists(path)) {
                share!!.mkdir(path)
            }
        }
        mkDir.join()
        if (share != null) {
            val job = listFile.map {
                lifecycleScope.async(Dispatchers.IO + coroutineExceptionHandler) {
                    copyToRemote(it, path)
                    return@async true
                }
            }
            lifecycleScope.launch(Dispatchers.IO) {
                var counter = 0
                job.forEach {
                    if (it.await()) {
                        counter++
                        progress.postValue(counter)
                    }
                }
                isComplete.postValue(true)
                share?.close()
            }

        }
    }

    private fun copyToRemote(file: File, path: String) = try {
        SmbFiles.copy(file, share, "$path\\${file.name}", true)//запись

        //Дальше идет процесс изменения даты создания файла, ибо по умолчанию ставится текушее время
        val file2 = share!!.openFile(
            "$path\\${file.name}",
            EnumSet.of(
                AccessMask.FILE_WRITE_ATTRIBUTES,
                AccessMask.FILE_WRITE_EA,
                AccessMask.FILE_READ_ATTRIBUTES,
                AccessMask.FILE_READ_EA
            ),
            null,
            SMB2ShareAccess.ALL,
            SMB2CreateDisposition.FILE_OPEN,
            null

        )//получение файла из ремота
        val bfi = FileBasicInformation(
            FileTime.ofEpochMillis(file.lastModified()),
            FileTime.ofEpochMillis(file.lastModified()),
            FileTime.ofEpochMillis(file.lastModified()),
            FileTime.ofEpochMillis(file.lastModified()),
            file2.fileInformation.basicInformation.fileAttributes
        )//создание новой информации о файле
        file2.setFileInformation(bfi)//обновление данных файла (время и атрибуты)

    } catch (e: Exception) {
        Log.e(e.cause.toString(), e.stackTraceToString())
    }

    override fun downloadFromRemote(pathFrom: String, pathTo: String): Boolean {
        val file2 = share!!.openFile(
            pathFrom,
            EnumSet.of(
                AccessMask.FILE_READ_DATA
            ),
            null,
            SMB2ShareAccess.ALL,
            SMB2CreateDisposition.FILE_OPEN,
            null

        )//получение файла из ремота

        val f = File(pathTo,file2.uncPath.split("/").last())

        file2.inputStream.use { input ->
            f.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return true
    }


    override fun getFolderAndFileInRemote(path: String): List<FileIdBothDirectoryInformation> {
        if (share != null) {
            for (f in share!!.list(path, "*").drop(2)) {
                Log.e("asd", f.fileName)
            }
        }
        return share!!.list(path, "*").drop(2)
    }
}