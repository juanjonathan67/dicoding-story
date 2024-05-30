package com.dicoding.storyapp.utils


import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Patterns
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

private const val DATE_PATTERN = "EE, d MMMM yyyy HH:mm"
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
private const val MAXIMAL_SIZE = 1000000

fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun parseTimeInstant(dateTime: String, locale: Locale): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT
    val parsedInstant = Instant.from(formatter.parse(dateTime))
    val localDateTime = LocalDateTime.ofInstant(parsedInstant, ZoneId.systemDefault())
    val stringDateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN, locale)

    return localDateTime.format(stringDateTimeFormatter)
}

fun parseTimeInstantRelative(dateTime: String): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT
    val parsedInstant = Instant.from(formatter.parse(dateTime))
    val currentInstant = Instant.now()

    val parsedZonedDateTime = ZonedDateTime.ofInstant(parsedInstant, ZoneId.systemDefault())
    val currentZonedDateTime = ZonedDateTime.ofInstant(currentInstant, ZoneId.systemDefault())

    val differenceYears = ChronoUnit.YEARS.between(parsedZonedDateTime, currentZonedDateTime)
    val differenceMonths = ChronoUnit.MONTHS.between(parsedZonedDateTime, currentZonedDateTime)
    val differenceWeeks = ChronoUnit.WEEKS.between(parsedZonedDateTime, currentZonedDateTime)
    val differenceDays = ChronoUnit.DAYS.between(parsedZonedDateTime, currentZonedDateTime)
    val differenceHours = ChronoUnit.HOURS.between(parsedZonedDateTime, currentZonedDateTime)
    val differenceMinutes = ChronoUnit.MINUTES.between(parsedZonedDateTime, currentZonedDateTime)

    return when {
        differenceYears > 0 -> "$differenceYears year${addPluralTime(differenceYears)} ago"
        differenceMonths > 0 -> "$differenceMonths month${addPluralTime(differenceMonths)} ago"
        differenceWeeks > 0 -> "$differenceWeeks week${addPluralTime(differenceWeeks)} ago"
        differenceDays > 0 -> "$differenceDays day${addPluralTime(differenceDays)} ago"
        differenceHours > 0 -> "$differenceHours hour${addPluralTime(differenceHours)} ago"
        differenceMinutes > 0 -> "$differenceMinutes minute${addPluralTime(differenceMinutes)} ago"
        else -> "Less than a minute ago"
    }
}

fun addPluralTime(value: Long): String {
    return if (value > 1) "s" else ""
}

fun getImageUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
    return uri ?: getImageUriForPreQ(context)
}

private fun getImageUriForPreQ(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    return inputStreamToFile(inputStream, context)
}

fun urlToFile(imageUrl: URL, context: Context): File {
    val inputStream = imageUrl.openStream()
    return inputStreamToFile(inputStream, context)
}

private fun inputStreamToFile(inputStream: InputStream, context: Context): File {
    val myFile = createCustomTempFile(context)
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}


fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}