package com.alexandercasal.androidhelpers.file

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.zip.ZipFile

class ZipUtilTest {

    private lateinit var zipUtil: ZipUtil

    @BeforeEach
    fun setup() {
        zipUtil = ZipUtil()
    }

    @Test
    fun `invalid file parameters return false`() {
        val invalidFile = File("")
        val validFile = File("notEmpty")

        assertFalse(zipUtil.zip(invalidFile, invalidFile))
        assertFalse(zipUtil.zip(invalidFile, validFile))
        assertFalse(zipUtil.zip(validFile, invalidFile))

        assertFalse(zipUtil.unzip(invalidFile, invalidFile))
        assertFalse(zipUtil.unzip(invalidFile, validFile))
        assertFalse(zipUtil.unzip(validFile, invalidFile))
    }

    @Test
    fun `zip single file`(@TempDir tempTestDir: File) {
        // Prepare single file to be zipped
        val sourceFile = File(tempTestDir, "exampleContent.txt")
        assertFalse(zipUtil.zip(sourceFile, File("empty.zip"))) // source file doesn't exist yet

        sourceFile.createNewFile()
        assertTrue(sourceFile.exists())

        // Zip in current directory (null/no parent file in abstract path name)
        val currentDirZip = File("currentDirZip.zip")
        assertTrue(zipUtil.zip(sourceFile, currentDirZip))
        assertTrue(currentDirZip.exists())
        ZipFile(currentDirZip).use { zipFile ->
            assertEquals(1, zipFile.size())
            assertEquals("exampleContent.txt", zipFile.entries().nextElement().name)
        }
        currentDirZip.delete()

        // Alternate directories zip
        val zip1 = File(tempTestDir, "zip1.zip")
        assertTrue(zipUtil.zip(sourceFile, zip1))
        assertTrue(zip1.exists())
        ZipFile(zip1).use { zipFile ->
            assertEquals(1, zipFile.size())
            assertEquals("exampleContent.txt", zipFile.entries().nextElement().name)
        }

        val zip2 = File(tempTestDir, "nested1/nested2/zip2")
        assertTrue(zipUtil.zip(sourceFile, zip2))
        assertTrue(zip2.exists())
        ZipFile(zip2).use { zipFile ->
            assertEquals(1, zipFile.size())
            assertEquals("exampleContent.txt", zipFile.entries().nextElement().name)
        }
    }

    @Test
    fun `zip multiple files with nested directories`(@TempDir tempTestDir: File) {
        // Prepare zip content
        File(tempTestDir, "myDirectory").mkdir()
        File(tempTestDir, "myDirectory/content1.txt").createNewFile()
        File(tempTestDir, "myDirectory/content2.txt").createNewFile()
        File(tempTestDir, "myDirectory/dir1/dir1_2").mkdirs()
        File(tempTestDir, "myDirectory/dir1/dir1_2/content3.txt").createNewFile()

        val zip = File(tempTestDir, "myZip.zip")
        assertTrue(zipUtil.zip(File(tempTestDir, "myDirectory"), zip))
        assertTrue(zip.exists())
        ZipFile(zip).use { zipFile ->
            assertEquals(3, zipFile.size())
            val entries = zipFile.entries()
            assertEquals("content1.txt", entries.nextElement().name)
            assertEquals("content2.txt", entries.nextElement().name)
            assertEquals(File("dir1/dir1_2/content3.txt").normalize().path, entries.nextElement().name)
        }
    }

    @Test
    fun `unzip single file`(@TempDir tempTestDir: File) {
        // Setup
        val extractionDirectory = File(tempTestDir, "extractions")
        extractionDirectory.mkdir()
        assertTrue(extractionDirectory.isDirectory)

        val sourceFile = File(tempTestDir, "exampleContent.txt")
        sourceFile.createNewFile()

        // Unzip directly to extraction directory that exists
        val zip1 = File(tempTestDir, "zip1.zip")
        zipUtil.zip(sourceFile, zip1)
        assertTrue(zip1.exists())
        assertTrue(zipUtil.unzip(zip1, extractionDirectory))
        assertTrue(File(extractionDirectory, "exampleContent.txt").exists())

        // Unzip to nested directory that does not exist
        val zip2 = File(tempTestDir, "zip2.zip")
        zipUtil.zip(sourceFile, zip2)
        assertTrue(zip2.exists())
        assertTrue(zipUtil.unzip(zip2, File(extractionDirectory, "nested1/nested2")))
        assertTrue(File(extractionDirectory, "nested1/nested2/exampleContent.txt").exists())
    }

    @Test
    fun `unzip multiple files with nested directories`(@TempDir tempTestDir: File) {
        // Setup
        val extractionDirectory = File(tempTestDir, "extractions").apply { mkdir() }
        assertTrue(extractionDirectory.isDirectory)

        val contentToZipDirectory = File(tempTestDir, "myContent").apply { mkdir() }
        File(contentToZipDirectory, "file1.txt").createNewFile()
        File(contentToZipDirectory, "file2.txt").createNewFile()
        File(contentToZipDirectory, "dir1").mkdir()
        File(contentToZipDirectory, "dir1/file3.txt").createNewFile()
        File(contentToZipDirectory, "dir2/dir3").mkdirs()
        File(contentToZipDirectory, "dir2/dir3/file4.txt").createNewFile()

        val zip = File(tempTestDir, "myZip.zip")
        zipUtil.zip(contentToZipDirectory, zip)
        assertTrue(zip.exists())
        assertTrue(zipUtil.unzip(zip, extractionDirectory))
        assertTrue(File(extractionDirectory, "file1.txt").exists())
        assertTrue(File(extractionDirectory, "file2.txt").exists())
        assertTrue(File(extractionDirectory, "dir1").isDirectory)
        assertTrue(File(extractionDirectory, "dir1/file3.txt").exists())
        assertTrue(File(extractionDirectory, "dir2/dir3").isDirectory)
        assertTrue(File(extractionDirectory, "dir2/dir3/file4.txt").exists())
    }

    @Test
    fun `zips containing Zip Slip vulnerability fail to unzip`(@TempDir tempTestDir: File) {
        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val vulnerableZip = if (System.getProperty("os.name").toLowerCase().contains("win")) {
            File(javaClass.classLoader.getResource("zipUtilTest\\zip-slip-win.zip").toURI())
        } else {
            File(javaClass.classLoader.getResource("zipUtilTest/zip-slip.zip").toURI())
        }

        assertTrue(vulnerableZip.exists())
        assertFalse(zipUtil.unzip(vulnerableZip, tempTestDir))
    }
}