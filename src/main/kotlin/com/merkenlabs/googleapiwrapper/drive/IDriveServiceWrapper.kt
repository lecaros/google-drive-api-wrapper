package com.merkenlabs.googleapiwrapper.drive

import com.google.api.services.drive.model.File
import java.io.File as FSFile

interface IDriveServiceWrapper {
    /**
     * Recursively copies all subfolders and files from one folder to another.
     *
     */
    fun copyFolderContentsIntoFolder(originFolderId: String, destinationFolderId: String)
    fun copyFolderStructureIntoFolder(originFolderId: String, destinationFolderId: String)
    fun getFilesInFolder(originFolderId: String): List<File>?
    fun getFilesInFolderByMimeType(folderId: String, mimeType: String): List<File>?
    fun getFolder(folderId: String): File?
    fun createFolder(mainFolderId: String, name: String): File
    fun findFolderByName(name: String, mainFolderId: String): File?
    fun findFilenameByUrl(url: String): String
    /**
    * Returns the new File.
     **/
    fun copyFile(originFile: File, destinationFolderId: String): File?
    fun exportFileAs(fileId: String, fileMimeType: String): FSFile
    fun getFileFromUrl(url: String): File?
}
