package com.merkenlabs.googleapiwrapper.drive

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File

abstract class AbstractDriveServiceWrapper : IDriveServiceWrapper {

    override fun copyFolderContentsIntoFolder(originFolderId: String, destinationFolderId: String) {
        val foundFiles = getFilesInFolder(originFolderId)
        if (foundFiles != null) {
            for (file in foundFiles) {
                if (file.mimeType.equals(MimeTypes.FOLDER)) {
                    val newFolder = createFolder(destinationFolderId, file.name)
                    copyFolderContentsIntoFolder(file.id, newFolder.id)
                    continue
                }
                val newFile = createNewFileWithAttributes(file, destinationFolderId)
                val copyFileRequest = getDriveService().files().copy(file.id, newFile)
                copyFileRequest.execute()
            }
        }
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
        val foundFiles = fileList.files
        foundFiles.sortBy { it.name }
        return foundFiles
    }

    override fun getFolder(folderId: String): Drive.Files.Get {
        return getDriveService().files().get(folderId)
    }

    override fun createFolder(mainFolderId: String, name: String): File {
        val foundFolder = findFolderByName(name, mainFolderId)
        if (!foundFolder.isNullOrEmpty()) {
            return foundFolder
        }
        val newFolder =
            File().setName(name).setMimeType(MimeTypes.FOLDER).setParents(listOf(mainFolderId))
        return getDriveService().files().create(newFolder).setFields("id, parents").execute()
    }

    override fun findFolderByName(name: String, mainFolderId: String): File? {
        val fileList = getDriveService().files().list()
            .setQ("mimeType='${MimeTypes.FOLDER}' and name = '$name' and trashed = false and '$mainFolderId' in parents")
            .execute()
        if (fileList.files.isNullOrEmpty()) {
            return null
        }
        return fileList.files.first()
    }

    object MimeTypes {
        const val FOLDER = "application/vnd.google-apps.folder"
    }
}