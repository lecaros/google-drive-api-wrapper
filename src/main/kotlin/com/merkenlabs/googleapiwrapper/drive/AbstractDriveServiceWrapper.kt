package com.merkenlabs.googleapiwrapper.drive

import com.google.api.services.drive.model.File
import com.merkenlabs.googleapiwrapper.drive.AbstractDriveServiceWrapper.MimeTypes.FOLDER

abstract class AbstractDriveServiceWrapper : IDriveServiceWrapper {

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
        val newFile = createNewFileWithAttributes(originFile, destinationFolderId)
        return copyFileToFile(originFile, newFile)
    }

    private fun copyFileToFile(
        originFile: File,
        newFile: File
    ): File? {
        val copyFileRequest = getDriveService().files().copy(originFile.id, newFile)
        return copyFileRequest.execute()
    }

    private fun createNewFileWithAttributes(
        file: File,
        destinationFolderId: String
    ): File {
        val newFile = File()
        newFile.name = file.name
        newFile.parents = listOf(destinationFolderId)
        return newFile
    }

    override fun getFilesInFolder(originFolderId: String): List<File>? {
        val fileList = getDriveService().files().list()
            .setQ("'$originFolderId' in parents")
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

    object MimeTypes {
        // As defined in https://developers.google.com/drive/api/v3/mime-types
        const val FOLDER = "application/vnd.google-apps.folder"
        const val SPREADSHEET = "application/vnd.google-apps.spreadsheet"
        const val DOCUMENT = "application/vnd.google-apps.document"
        const val PRESENTATION = "application/vnd.google-apps.presentation"
    }
}