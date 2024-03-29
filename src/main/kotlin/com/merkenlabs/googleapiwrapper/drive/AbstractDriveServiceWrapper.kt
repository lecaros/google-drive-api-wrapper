package com.merkenlabs.googleapiwrapper.drive

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.merkenlabs.googleapiwrapper.drive.AbstractDriveServiceWrapper.MimeTypes.FOLDER
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

abstract class AbstractDriveServiceWrapper : IDriveServiceWrapper {

    override fun deleteFileById(fileId: String) {
        getDriveService().files().delete(fileId).execute()
    }

    override fun copyFolderStructureIntoFolder(originFolderId: String, destinationFolderId: String) {
        getFilesInFolderByMimeType(originFolderId, FOLDER)?.forEach { file ->
                val newFolder = createFolder(destinationFolderId, file.name)
                copyFolderStructureIntoFolder(file.id, newFolder.id)
        }
    }

    override fun copyFolderContentsIntoFolder(originFolderId: String, destinationFolderId: String) {
        getFilesInFolder(originFolderId)?.forEach { file ->
            when (file.mimeType) {
                FOLDER -> {
                    val newFolder = createFolder(destinationFolderId, file.name)
                    copyFolderContentsIntoFolder(file.id, newFolder.id)
                }
                else -> {
                    copyFile(file, destinationFolderId)
                }
            }
        }
    }

    override fun copyFile(originFile: File, destinationFolderId: String): File? {
        val newFile = prepareNewFileForCopy(originFile, destinationFolderId)
        return copyFileToFile(originFile, newFile)
    }

    override fun exportFileAs(fileId: String, fileMimeType: String, fileName: String?): java.io.File {
        val outputStream = ByteArrayOutputStream()
        getDriveService().files().export(fileId, fileMimeType)
            .executeMediaAndDownloadTo(outputStream)
        writeToFileSystem(fileName?:fileId, outputStream)
        return java.io.File(fileName?:fileId)
    }

    private fun writeToFileSystem(fileId: String, outputStream: ByteArrayOutputStream) {
        val file = FileOutputStream(fileId)
        outputStream.writeTo(file)
        outputStream.close()
        file.close()
    }

    private fun copyFileToFile(
        originFile: File,
        destinationFile: File
    ): File? {
        val copyFileRequest = getDriveService().files().copy(originFile.id, destinationFile)
        return copyFileRequest.execute()
    }

    private fun prepareNewFileForCopy(
        originFile: File,
        destinationFolderId: String
    ): File {
        val newFile = File()
        newFile.name = originFile.name
        newFile.parents = listOf(destinationFolderId)
        return newFile
    }

    override fun getFilesInFolder(originFolderId: String): List<File>? {
        val fileList = getDriveService().files().list()
            .setQ("'$originFolderId' in parents and trashed = false")
            .setFields("files(id, name, mimeType)")
            .execute()
        return fileList.files
    }

    override fun getFilesInFolderByMimeType(folderId: String, mimeType: String): List<File>?{
        val fileList = getDriveService().files().list()
            .setQ("'$folderId' in parents and mimeType = '$mimeType' and trashed = false")
            .setFields("files(id, name, mimeType)")
            .execute()
        return fileList.files
    }

    override fun getFolder(folderId: String): File? {
        return getDriveService().files().get(folderId).execute()
    }

    override fun createFolder(mainFolderId: String, name: String): File {
        val foundFolder = findFolderByName(name, mainFolderId)
        foundFolder?.let { return it }
        val newFolder =
            File().setName(name).setMimeType(FOLDER).setParents(listOf(mainFolderId))
        return getDriveService().files().create(newFolder).setFields("id, parents").execute()
    }

    override fun findFolderByName(name: String, mainFolderId: String): File? {
        val fileList = getDriveService().files().list()
            .setQ("mimeType='${FOLDER}' and name = '$name' and trashed = false and '$mainFolderId' in parents")
            .execute()
        return fileList.files.firstOrNull()
    }

    override fun findFilenameByUrl(url: String): String {
        val file = getFileFromUrl(url)
        return file.name
    }

    override fun getFileFromUrl(url: String): File {
        val idFromUrl = url.split("google.com")[1].split("/")[3]
        return getDriveService().files().get(idFromUrl).execute()
    }

    protected abstract fun getDriveService(): Drive

    object MimeTypes {
        // As defined in https://developers.google.com/drive/api/v3/mime-types
        const val FOLDER = "application/vnd.google-apps.folder"
        const val SPREADSHEET = "application/vnd.google-apps.spreadsheet"
        const val DOCUMENT = "application/vnd.google-apps.document"
        const val PRESENTATION = "application/vnd.google-apps.presentation"
        const val PDF = "application/pdf"
    }
}
