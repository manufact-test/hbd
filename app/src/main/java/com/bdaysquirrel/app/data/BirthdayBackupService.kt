package com.bdaysquirrel.app.data

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.time.YearMonth
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

private const val BACKUP_VERSION = 1
private const val MANIFEST_ENTRY = "birthdays.json"
private const val PHOTOS_PREFIX = "photos/"

data class BackupExportResult(
    val birthdays: Int,
    val photos: Int,
)

data class BackupImportResult(
    val birthdays: Int,
    val photos: Int,
)

class BirthdayBackupService(
    context: Context,
    private val repository: BirthdayRepository,
) {
    private val appContext = context.applicationContext
    private val contentResolver = appContext.contentResolver

    suspend fun exportTo(destination: Uri): BackupExportResult = withContext(Dispatchers.IO) {
        val birthdays = repository.snapshot()
        var exportedPhotos = 0
        val rows = JSONArray()

        val output = contentResolver.openOutputStream(destination, "w")
            ?: error("Не удалось открыть файл для резервной копии")

        output.use { rawOutput ->
            ZipOutputStream(rawOutput.buffered()).use { zip ->
                birthdays.forEachIndexed { index, birthday ->
                    val row = JSONObject()
                        .put("name", birthday.name)
                        .put("day", birthday.day)
                        .put("month", birthday.month)
                        .put("year", birthday.year ?: JSONObject.NULL)
                        .put("note", birthday.note)
                        .put("createdAt", birthday.createdAt)

                    val photoEntry = birthday.photoUri
                        ?.let { exportPhoto(zip, it, birthday.id, index) }
                    if (photoEntry != null) {
                        row.put("photoEntry", photoEntry)
                        exportedPhotos += 1
                    }
                    rows.put(row)
                }

                val manifest = JSONObject()
                    .put("format", "BdaySquirrel")
                    .put("version", BACKUP_VERSION)
                    .put("exportedAt", System.currentTimeMillis())
                    .put("birthdays", rows)

                zip.putNextEntry(ZipEntry(MANIFEST_ENTRY))
                zip.write(manifest.toString(2).toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
        }

        BackupExportResult(
            birthdays = birthdays.size,
            photos = exportedPhotos,
        )
    }

    suspend fun importFrom(source: Uri): BackupImportResult = withContext(Dispatchers.IO) {
        val input = contentResolver.openInputStream(source)
            ?: error("Не удалось открыть резервную копию")
        val stagingDir = File(
            appContext.cacheDir,
            "bdaysquirrel-import-${UUID.randomUUID()}",
        ).apply { mkdirs() }

        try {
            var manifestText: String? = null
            val stagedPhotos = mutableMapOf<String, File>()

            input.use { rawInput ->
                ZipInputStream(rawInput.buffered()).use { zip ->
                    while (true) {
                        val entry = zip.nextEntry ?: break
                        val entryName = entry.name
                        when {
                            entry.isDirectory -> Unit
                            entryName == MANIFEST_ENTRY -> {
                                manifestText = zip.readBytes().toString(Charsets.UTF_8)
                            }
                            isSafePhotoEntry(entryName) -> {
                                val target = File(stagingDir, entryName.substringAfterLast('/'))
                                target.outputStream().buffered().use { output ->
                                    zip.copyTo(output)
                                }
                                stagedPhotos[entryName] = target
                            }
                        }
                        zip.closeEntry()
                    }
                }
            }

            val manifest = JSONObject(
                manifestText ?: error("В резервной копии отсутствует $MANIFEST_ENTRY"),
            )
            require(manifest.optString("format") == "BdaySquirrel") {
                "Это не резервная копия BdaySquirrel"
            }
            require(manifest.optInt("version", -1) == BACKUP_VERSION) {
                "Версия резервной копии пока не поддерживается"
            }

            val array = manifest.getJSONArray("birthdays")
            val restoredParent = File(appContext.filesDir, "restored_photos")
            val restoredDir = File(restoredParent, System.currentTimeMillis().toString())
            var restoredPhotos = 0

            val restoredBirthdays = buildList {
                for (index in 0 until array.length()) {
                    val row = array.getJSONObject(index)
                    val name = row.getString("name").trim()
                    val day = row.getInt("day")
                    val month = row.getInt("month")
                    val year = if (row.isNull("year")) null else row.getInt("year")
                    val note = row.optString("note", "").trim()
                    val createdAt = row.optLong("createdAt", System.currentTimeMillis())

                    validateBirthday(name, day, month, year)

                    val photoUri = row.optString("photoEntry", "")
                        .takeIf(::isSafePhotoEntry)
                        ?.let { photoEntry ->
                            val staged = stagedPhotos[photoEntry] ?: return@let null
                            restoredDir.mkdirs()
                            val extension = photoEntry.substringAfterLast('.', "")
                                .lowercase()
                                .takeIf { it.matches(Regex("[a-z0-9]{1,8}")) }
                            val targetName = buildString {
                                append(index)
                                if (extension != null) {
                                    append('.')
                                    append(extension)
                                }
                            }
                            val target = File(restoredDir, targetName)
                            staged.copyTo(target, overwrite = true)
                            restoredPhotos += 1
                            Uri.fromFile(target).toString()
                        }

                    add(
                        BirthdayEntity(
                            id = 0,
                            name = name,
                            day = day,
                            month = month,
                            year = year,
                            note = note,
                            photoUri = photoUri,
                            createdAt = createdAt,
                        ),
                    )
                }
            }

            try {
                repository.replaceAll(restoredBirthdays)
            } catch (error: Throwable) {
                restoredDir.deleteRecursively()
                throw error
            }

            if (restoredParent.exists()) {
                restoredParent.listFiles()
                    .orEmpty()
                    .filter { it != restoredDir }
                    .forEach(File::deleteRecursively)
            }

            BackupImportResult(
                birthdays = restoredBirthdays.size,
                photos = restoredPhotos,
            )
        } finally {
            stagingDir.deleteRecursively()
        }
    }

    private fun exportPhoto(
        zip: ZipOutputStream,
        uriString: String,
        birthdayId: Long,
        index: Int,
    ): String? {
        val uri = runCatching { Uri.parse(uriString) }.getOrNull() ?: return null
        val input = runCatching { openPhotoInput(uri) }.getOrNull() ?: return null
        val extension = resolveExtension(uri)
        val entryName = buildString {
            append(PHOTOS_PREFIX)
            append(birthdayId.takeIf { it > 0 } ?: index)
            append('-')
            append(index)
            if (extension != null) {
                append('.')
                append(extension)
            }
        }

        return input.use { photoInput ->
            var success = false
            zip.putNextEntry(ZipEntry(entryName))
            try {
                photoInput.copyTo(zip)
                success = true
            } finally {
                zip.closeEntry()
            }
            entryName.takeIf { success }
        }
    }

    private fun openPhotoInput(uri: Uri): InputStream? = when (uri.scheme) {
        "file" -> uri.path?.let(::File)?.takeIf(File::exists)?.let(::FileInputStream)
        else -> contentResolver.openInputStream(uri)
    }

    private fun resolveExtension(uri: Uri): String? {
        val fromMime = contentResolver.getType(uri)
            ?.let(MimeTypeMap.getSingleton()::getExtensionFromMimeType)
        val fromPath = uri.lastPathSegment
            ?.substringAfterLast('.', "")
            ?.takeIf { it.isNotBlank() }
        return (fromMime ?: fromPath)
            ?.lowercase()
            ?.takeIf { it.matches(Regex("[a-z0-9]{1,8}")) }
    }

    private fun validateBirthday(
        name: String,
        day: Int,
        month: Int,
        year: Int?,
    ) {
        require(name.isNotBlank()) { "В резервной копии найдено пустое имя" }
        require(month in 1..12) { "Некорректный месяц рождения" }
        if (year != null) {
            require(year in 1..9999) { "Некорректный год рождения" }
        }
        val validationYear = year ?: 2000
        val yearMonth = YearMonth.of(validationYear, month)
        require(day in 1..yearMonth.lengthOfMonth()) { "Некорректный день рождения" }
    }

    private fun isSafePhotoEntry(entryName: String): Boolean =
        entryName.startsWith(PHOTOS_PREFIX) &&
            !entryName.contains("..") &&
            !entryName.contains('\\') &&
            entryName.substringAfter(PHOTOS_PREFIX).isNotBlank()
}
