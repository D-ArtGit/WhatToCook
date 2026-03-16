package ru.dartx.feature_edit_recipe_card

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.runtime.MutableState
import com.image.cropview.ImageCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.compose.material3.Text
import com.image.cropview.CropType

class ImagePicker internal constructor(
    private val context: Context,
    private val launchGallery: () -> Unit,
    private val launchCamera: () -> Unit,
    private val pendingCallbackState: MutableState<(String?) -> Unit>,
    private val pendingCropUriState: MutableState<Uri?>,
    private val showSourceDialogState: MutableState<Boolean>,
) {
    fun pickImage(onResult: (String?) -> Unit) {
        val activity = context as? Activity
        if (activity == null) {
            onResult(null)
            return
        }
        pendingCallbackState.value = onResult
        showSourceDialogState.value = true
    }

    @Composable
    fun SourcePickerHost() {
        if (!showSourceDialogState.value) return
        AlertDialog(
            onDismissRequest = { deliver(null) },
            title = { Text(text = context.getString(R.string.image_picker_title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSourceDialogState.value = false
                        launchGallery()
                    }
                ) {
                    Text(text = context.getString(R.string.image_picker_gallery))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSourceDialogState.value = false
                        launchCamera()
                    }
                ) {
                    Text(text = context.getString(R.string.image_picker_camera))
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CropperHost() {
        val pendingUri = pendingCropUriState.value ?: return
        val scope = rememberCoroutineScope()
        val imageCropState = remember(pendingUri) { mutableStateOf<ImageCrop?>(null) }

        LaunchedEffect(pendingUri) {
            val bitmap = loadBitmap(context, pendingUri)
            imageCropState.value = ImageCrop(bitmap)
        }

        Dialog(
            onDismissRequest = { deliver(null) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = "") },
                        navigationIcon = {
                            IconButton(onClick = { deliver(null) }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    val imageCrop = imageCropState.value ?: return@IconButton
                                    scope.launch {
                                        val cropped = imageCrop.onCrop()
                                        val uri = saveBitmapToFile(context, cropped)
                                        deliver(uri.toString())
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    val imageCrop = imageCropState.value
                    imageCrop?.ImageCropView(
                        modifier = Modifier.fillMaxSize(),
                        cropType = CropType.SQUARE
                    )
                }
            }
        }
    }

    private fun deliver(result: String?) {
        val callback = pendingCallbackState.value
        pendingCallbackState.value = {}
        pendingCropUriState.value = null
        showSourceDialogState.value = false
        callback(result)
    }
}

@Composable
fun rememberImagePicker(): ImagePicker? {
    val context = LocalContext.current
    val activity = context as? Activity ?: return null
    val pendingCallbackState = remember { mutableStateOf<(String?) -> Unit>({}) }
    val cameraOutputUriState = remember { mutableStateOf<Uri?>(null) }
    val pendingCropUriState = remember { mutableStateOf<Uri?>(null) }
    val showSourceDialogState = remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) {
            val callback = pendingCallbackState.value
            pendingCallbackState.value = {}
            callback(null)
        } else {
            pendingCropUriState.value = uri
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = if (success) cameraOutputUriState.value else null
        cameraOutputUriState.value = null
        if (uri == null) {
            val callback = pendingCallbackState.value
            pendingCallbackState.value = {}
            callback(null)
        } else {
            pendingCropUriState.value = uri
        }
    }

    val imagePicker = remember(context, activity) {
        ImagePicker(
            context = context,
            launchGallery = { galleryLauncher.launch("image/*") },
            launchCamera = {
                val outputFile = createTempImageFile(context.cacheDir)
                val outputUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    outputFile
                )
                cameraOutputUriState.value = outputUri
                cameraLauncher.launch(outputUri)
            },
            pendingCallbackState = pendingCallbackState,
            pendingCropUriState = pendingCropUriState,
            showSourceDialogState = showSourceDialogState
        )
    }

    return imagePicker
}

private fun createTempImageFile(baseDir: File): File {
    val dir = File(baseDir, "image_picker").apply { mkdirs() }
    return File(dir, "camera_${System.currentTimeMillis()}.jpg")
}

private fun createImageFile(baseDir: File): File {
    val dir = File(baseDir, "recipe_images").apply { mkdirs() }
    return File(dir, "recipe_${System.currentTimeMillis()}.jpg")
}

private suspend fun loadBitmap(context: Context, uri: Uri): Bitmap =
    withContext(Dispatchers.IO) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
    }

private suspend fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri =
    withContext(Dispatchers.IO) {
        val outputFile = createImageFile(context.filesDir)
        FileOutputStream(outputFile).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, stream)
        }
        Uri.fromFile(outputFile)
    }
