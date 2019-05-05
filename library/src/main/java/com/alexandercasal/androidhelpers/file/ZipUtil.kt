package com.alexandercasal.androidhelpers.file

import androidx.annotation.WorkerThread
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Simple zipping utility to zip and unzip single files and directories.
 */
class ZipUtil {

    /**
     * Given that the [sourceFile] or directory exists, it will be zipped
     * to the specified [destinationFile] as a zip archive. If the path to the [destinationFile]
     * has not yet been created, an attempt to automatically generate the path will be made.
     * Should there be an error while creating the zip, an attempt will be made to delete the
     * destination file as to not leave behind a corrupted or partially complete zip.
     *
     * This method should be called on a background worker thread.
     *
     * @param sourceFile The file or directory that needs to be zipped.
     * @param destinationFile The file that will be created and saved as a zip. If the path to this file does
     * not yet exist, it will be created for you.
     * @return true if the zip was successful and the destinationFile has been created;
     *
     * false if there was an error during the zip process and the destinationFile has not been created.
     */
    @WorkerThread
    fun zip(sourceFile: File, destinationFile: File): Boolean {
        try {
            if (sourceFile.name.isBlank()) throw IllegalArgumentException("Source file name cannot be blank")
            if (destinationFile.name.isBlank()) throw java.lang.IllegalArgumentException("Destination file name cannot be blank")

            if (sourceFile.exists()) {
                makeParentDirectoriesIfNotExists(destinationFile)
                ZipOutputStream(BufferedOutputStream(FileOutputStream(destinationFile))).use { zipOutputStream ->
                    sourceFile.walkBottomUp()
                        .filter { it.isFile } // We only need to write an entry for physical files. Directories are handled automatically based on the entry name
                        .forEach { file ->
                            // When the sourceFile contains children directories, we need the relative path to the file being written in order to preserve the originating file structure.
                            // Ex: The entryName "InnerFolder/myContent.txt" will result in the zip file containing an entry of "InnerFolder/myContent.txt"
                            // while the entryName "myContent.txt" will result in the file "myContent.txt" being written at the root of the zip.
                            val entryName = file.relativeTo(getBaseFileForEntryRelativePath(sourceFile)).path
                            writeFileToZip(file, entryName, zipOutputStream)
                        }

                    zipOutputStream.flush()
                    zipOutputStream.closeEntry()
                }
                return true
            }
        } catch (e: Exception) {
            Timber.e(e, "Error while zipping '${destinationFile.name}'")
            cleanupSilently(destinationFile)
        }

        return false
    }

    private fun makeParentDirectoriesIfNotExists(destinationFile: File) {
        if (!destinationFile.exists() && destinationFile.parentFile != null) {
            destinationFile.parentFile.mkdirs()
        }
    }

    private fun getBaseFileForEntryRelativePath(sourceFile: File): File {
        return when (getFileType(sourceFile)) {
            FileType.DIRECTORY -> sourceFile
            FileType.FILE -> sourceFile.parentFile // This ensures when we relativize the entryName, we are getting the single file name rather than an empty string
        }
    }

    private fun writeFileToZip(file: File, zipEntryName: String, zipOutputStream: ZipOutputStream) {
        BufferedInputStream(FileInputStream(file)).use { fileInputStream ->
            val byteBuffer = ByteArray(BUFFER_SIZE)
            zipOutputStream.putNextEntry(ZipEntry(zipEntryName))

            var length = fileInputStream.read(byteBuffer, NO_OFFSET, BUFFER_SIZE)
            while (length != -1) {
                zipOutputStream.write(byteBuffer, NO_OFFSET, length)
                length = fileInputStream.read(byteBuffer, NO_OFFSET, BUFFER_SIZE)
            }
        }
    }

    /**
     * Unzips the specified [zipFile]'s contents inside the [extractionDirectory] directory. If the [extractionDirectory]
     * already exists, it must be a directory. If the [extractionDirectory] does not exist it will be
     * created as a directory.
     *
     * Unlike the zip operation, if there is an error while unzipping, any files that were already
     * unzipped will remain in the [extractionDirectory] directory.
     */
    @WorkerThread
    fun unzip(zipFile: File, extractionDirectory: File): Boolean {
        try {
            if (zipFile.name.isBlank()) throw IllegalArgumentException("Zip file name cannot be blank")
            if (extractionDirectory.name.isBlank()) throw IllegalArgumentException("Extraction directory name cannot be blank")

            makeExtractionDirectoryIfNotExists(extractionDirectory)
            if (zipFile.exists() && extractionDirectory.isDirectory) {
                ZipFile(zipFile).use { zipResource ->
                    val zipEntries = zipResource.entries()
                    while (zipEntries.hasMoreElements()) {
                        val entry = zipEntries.nextElement()
                        if (!verifyNoZipSlip(extractionDirectory, entry)) throw IOException("Entry is outside of the target directory. Potential malicious file: ${entry.name}")
                        extractZipEntry(entry, extractionDirectory, zipResource)
                    }
                }
                return true
            }
        } catch (e: Exception) {
            Timber.e(e, "Error while unzipping '${zipFile.name}'")
        }
        return false
    }

    private fun makeExtractionDirectoryIfNotExists(extractionDirectory: File) {
        if (!extractionDirectory.exists()) {
            extractionDirectory.mkdirs()
        }
    }

    /**
     * Verification to help guard against Zip Slip (https://snyk.io/research/zip-slip-vulnerability)
     *
     * @return true if the [ZipEntry] is destined to the target directory and does not contain
     * any directory traversal vulnerabilities (ex: ../../../badfile.sh);
     *
     * false if the [ZipEntry] contains a path outside of the destination and could potentially
     * be a malicious file
     */
    private fun verifyNoZipSlip(destinationDir: File, entry: ZipEntry): Boolean {
        val canonicalDestinationDirPath = destinationDir.canonicalPath
        val destinationFile = File(destinationDir, entry.name)
        val canonicalDestinationFile = destinationFile.canonicalPath

        if (!canonicalDestinationFile.startsWith(canonicalDestinationDirPath + File.separator)) {
            return false
        }

        return true
    }

    private fun extractZipEntry(entry: ZipEntry, extractionDirectory: File, zipFile: ZipFile) {
        if (entry.isDirectory) {
            File(extractionDirectory, entry.name).mkdirs()
        } else {
            val entryFile = File(extractionDirectory, entry.name)
            entryFile.parentFile.mkdirs()

            zipFile.getInputStream(entry).use { zipInputStream ->
                val byteBuffer = ByteArray(BUFFER_SIZE)
                FileOutputStream(entryFile).use { fileOutputStream ->
                    var length = zipInputStream.read(byteBuffer, NO_OFFSET, BUFFER_SIZE)
                    while (length != -1) {
                        fileOutputStream.write(byteBuffer, NO_OFFSET, length)
                        length = zipInputStream.read(byteBuffer, NO_OFFSET, BUFFER_SIZE)
                    }
                }
            }
        }
    }

    private fun getFileType(file: File): FileType {
        return when {
            file.isDirectory -> FileType.DIRECTORY
            file.isFile ->FileType.FILE
            else -> throw IllegalArgumentException("file must be a normal file or directory")
        }
    }

    private fun cleanupSilently(destinationFile: File) {
        try {
            if (destinationFile.exists()) {
                destinationFile.deleteRecursively()
            }
        } catch (e: Exception) {
            // Do nothing
        }
    }

    private enum class FileType {
        DIRECTORY,
        FILE
    }

    companion object {
        private const val BUFFER_SIZE = 1024
        private const val NO_OFFSET = 0
    }
}