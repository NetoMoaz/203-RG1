package com.example.integration.rg203

/**
 * Contrato oficial de intercâmbio de arquivos entre o 203 RG Desktop V4.8 e o 203 RG Mobile.
 *
 * Extensão oficial padronizada: .rg203
 */
object Rg203Constants {
    const val FILE_EXTENSION = "rg203"
    const val OFFICIAL_EXTENSION_SUFFIX = ".rg203"
    const val DESKTOP_VERSION_COMPATIBILITY = "4.8"
}

data class Rg203Metadata(
    val fileName: String,
    val sizeBytes: Long,
    val isValidExtension: Boolean,
    val uriString: String
)

sealed class Rg203ValidationResult {
    data class Valid(
        val metadata: Rg203Metadata,
        val message: String
    ) : Rg203ValidationResult()

    data class InvalidExtension(
        val actualName: String,
        val expectedExtension: String = Rg203Constants.FILE_EXTENSION,
        val message: String
    ) : Rg203ValidationResult()

    data class ReadError(
        val message: String,
        val throwable: Throwable? = null
    ) : Rg203ValidationResult()
}

sealed class Rg203OperationStatus {
    object Idle : Rg203OperationStatus()

    data class FileSelected(
        val metadata: Rg203Metadata,
        val note: String
    ) : Rg203OperationStatus()

    data class PendingDesktopContract(
        val metadata: Rg203Metadata,
        val message: String
    ) : Rg203OperationStatus()

    data class Error(
        val message: String
    ) : Rg203OperationStatus()
}
