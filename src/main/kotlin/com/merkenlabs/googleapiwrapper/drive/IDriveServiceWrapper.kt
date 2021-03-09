package com.merkenlabs.googleapiwrapper.drive

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File

interface IDriveServiceWrapper {
    /**
     * Recursively copies all subfolders and files from one folder to another.
     *
     */
    fun copyFolderContentsIntoFolder(originFolderId: String, destinationFolderId: String)
    fun getFilesInFolder(originFolderId: String): List<File>?
    fun getFolder(folderId: String): Drive.Files.Get
    fun createFolder(mainFolderId: String, name: String): File
    fun findFolderByName(name: String, mainFolderId: String): File?
    fun getDriveService(): Drive
}