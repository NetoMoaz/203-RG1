package com.example.integration.rg203

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

class Rg203FileValidator {

    fun validateFileName(name: String): Boolean {
        return name.trim().endsWith(Rg203Constants.OFFICIAL_EXTENSION_SUFFIX, ignoreCase = true)
    }

    fun inspectFile(context: Context, uri: Uri): Rg203ValidationResult {
        var fileName = "desconhecido"
        var fileSize = 0L

        try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    if (nameIndex != -1) {
                        fileName = it.getString(nameIndex) ?: fileName
                    }
                    if (sizeIndex != -1 && !it.isNull(sizeIndex)) {
                        fileSize = it.getLong(sizeIndex)
                    }
                }
            }
        } catch (e: Exception) {
            // Fallback se query de content provider falhar
            val path = uri.path ?: uri.toString()
            fileName = path.substringAfterLast('/')
        }

        val isValidExt = validateFileName(fileName)

        val metadata = Rg203Metadata(
            fileName = fileName,
            sizeBytes = fileSize,
            isValidExtension = isValidExt,
            uriString = uri.toString()
        )

        return if (!isValidExt) {
            Rg203ValidationResult.InvalidExtension(
                actualName = fileName,
                message = "O arquivo '$fileName' não possui a extensão obrigatória .rg203 do 203 RG Desktop V4.8."
            )
        } else {
            Rg203ValidationResult.Valid(
                metadata = metadata,
                message = "Arquivo .rg203 válido selecionado. Compatível com a arquitetura de integração do Desktop V4.8."
            )
        }
    }
}
