package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.ChapterAIViewModel
import com.example.ui.theme.AmberTertiaryDark
import com.example.ui.theme.HighDensityBorder
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.TealSecondaryDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.util.concurrent.Executors

/**
 * CameraX Live Question Scanner Screen
 * Allows students to frame, focus, and capture high-resolution photos of study questions,
 * formulas, or textbook excerpts for step-by-step AI solving and notes generation.
 */
@Composable
fun CameraXQuestionScannerScreen(
    viewModel: ChapterAIViewModel,
    onCloseScanner: () -> Unit,
    onSolveRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Camera states
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var isTorchEnabled by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }
    var previewView: PreviewView? by remember { mutableStateOf(null) }
    var cameraControl: CameraControl? by remember { mutableStateOf(null) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    // Tap to focus state
    var tapFocusPoint by remember { mutableStateOf<Offset?>(null) }

    // Captured image review state
    var capturedPreviewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val selectedSubject by viewModel.snapSubject.collectAsState()
    val promptInput by viewModel.snapQuestionInput.collectAsState()

    val subjects = listOf("Mathematics", "Physics", "Chemistry", "Biology", "Computer Science", "Economics")

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    // Gallery Picker as alternative
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch(Dispatchers.IO) {
                try {
                    val stream = context.contentResolver.openInputStream(it)
                    val bitmap = BitmapFactory.decodeStream(stream)
                    stream?.close()
                    if (bitmap != null) {
                        viewModel.setCapturedBitmap(bitmap)
                        capturedPreviewBitmap = bitmap
                    }
                } catch (e: Exception) {
                    Log.e("CameraX", "Failed to load gallery image", e)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (!hasCameraPermission) {
            // Permission Request State
            CameraPermissionDeniedContent(
                onRequestPermission = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onPickFromGallery = {
                    galleryLauncher.launch("image/*")
                },
                onBack = onCloseScanner
            )
        } else if (capturedPreviewBitmap != null) {
            // Captured Image Review & AI Confirmation Screen
            CapturedImageReviewView(
                bitmap = capturedPreviewBitmap!!,
                selectedSubject = selectedSubject,
                onSelectSubject = { viewModel.snapSubject.value = it },
                promptInput = promptInput,
                onPromptChange = { viewModel.snapQuestionInput.value = it },
                onRetake = {
                    capturedPreviewBitmap = null
                    viewModel.setCapturedBitmap(null)
                },
                onConfirmSolve = {
                    viewModel.setCapturedBitmap(capturedPreviewBitmap)
                    onSolveRequested()
                },
                onClose = onCloseScanner
            )
        } else {
            // Live CameraX Viewfinder & Overlays
            AndroidView(
                factory = { ctx ->
                    val pv = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    previewView = pv

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder()
                                .build()
                                .also {
                                    it.setSurfaceProvider(pv.surfaceProvider)
                                }

                            val ic = ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                                .build()
                            imageCapture = ic

                            val cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(lensFacing)
                                .build()

                            cameraProvider.unbindAll()
                            val camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                ic
                            )
                            cameraControl = camera.cameraControl
                            cameraControl?.enableTorch(isTorchEnabled)
                        } catch (exc: Exception) {
                            Log.e("CameraX", "Use case binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    pv
                },
                update = { pv ->
                    previewView = pv
                },
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(lensFacing) {
                        detectTapGestures { offset ->
                            tapFocusPoint = offset
                            val pv = previewView ?: return@detectTapGestures
                            val factory = SurfaceOrientedMeteringPointFactory(
                                pv.width.toFloat(),
                                pv.height.toFloat()
                            )
                            val point = factory.createPoint(offset.x, offset.y)
                            val action = FocusMeteringAction.Builder(point).build()
                            cameraControl?.startFocusAndMetering(action)
                        }
                    }
            )

            // Scanning Framing Box Overlay with Cutout & Brackets
            ScannerOverlay(
                onTapFocusPoint = tapFocusPoint
            )

            // Top Camera Bar: Back, Flash, Lens Switch, AI Tag
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onCloseScanner,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .testTag("camera_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Camera",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // AI Scanner Pill Header
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, HighDensityPrimary.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = HighDensityPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "AI Question Snapper",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Flash / Torch Toggle
                    IconButton(
                        onClick = {
                            isTorchEnabled = !isTorchEnabled
                            cameraControl?.enableTorch(isTorchEnabled)
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isTorchEnabled) AmberTertiaryDark else Color.Black.copy(alpha = 0.55f))
                            .testTag("camera_torch_button")
                    ) {
                        Icon(
                            imageVector = if (isTorchEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Toggle Torch",
                            tint = if (isTorchEnabled) Color.Black else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Camera Switch (Front/Back)
                    IconButton(
                        onClick = {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                            // Rebind camera with updated lens
                            val pv = previewView ?: return@IconButton
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                            cameraProviderFuture.addListener({
                                try {
                                    val cameraProvider = cameraProviderFuture.get()
                                    cameraProvider.unbindAll()

                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(pv.surfaceProvider)
                                    }
                                    val ic = ImageCapture.Builder()
                                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                                        .build()
                                    imageCapture = ic

                                    val cameraSelector = CameraSelector.Builder()
                                        .requireLensFacing(lensFacing)
                                        .build()

                                    val camera = cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        ic
                                    )
                                    cameraControl = camera.cameraControl
                                } catch (e: Exception) {
                                    Log.e("CameraX", "Lens switch failed", e)
                                }
                            }, ContextCompat.getMainExecutor(context))
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                            .testTag("camera_switch_lens_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "Switch Camera",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Bottom Controls Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.75f),
                                Color.Black.copy(alpha = 0.95f)
                            )
                        )
                    )
                    .padding(bottom = 24.dp, start = 16.dp, end = 16.dp, top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Subject Quick Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    subjects.forEach { subj ->
                        val isSelected = subj == selectedSubject
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) HighDensityPrimary else Color.White.copy(alpha = 0.15f),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) HighDensityPrimary else Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .clickable { viewModel.snapSubject.value = subj }
                                .testTag("camera_subject_$subj")
                        ) {
                            Text(
                                text = subj,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Shutter Row: Gallery, Big Shutter Button, Quick Hints
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pick from Gallery
                    IconButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .testTag("camera_gallery_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Gallery",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Main Camera Shutter Button with outer glow
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .clickable(enabled = !isCapturing) {
                                val ic = imageCapture ?: return@clickable
                                isCapturing = true

                                ic.takePicture(
                                    cameraExecutor,
                                    object : ImageCapture.OnImageCapturedCallback() {
                                        override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                            try {
                                                val bitmap = imageProxyToBitmap(imageProxy)
                                                imageProxy.close()

                                                scope.launch(Dispatchers.Main) {
                                                    isCapturing = false
                                                    capturedPreviewBitmap = bitmap
                                                    viewModel.setCapturedBitmap(bitmap)
                                                }
                                            } catch (e: Exception) {
                                                Log.e("CameraX", "Error processing captured image", e)
                                                scope.launch(Dispatchers.Main) {
                                                    isCapturing = false
                                                }
                                            }
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
                                            scope.launch(Dispatchers.Main) {
                                                isCapturing = false
                                            }
                                        }
                                    }
                                )
                            }
                            .testTag("camera_shutter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(66.dp)
                                .clip(CircleShape)
                                .background(if (isCapturing) AmberTertiaryDark else Color.White)
                                .border(3.dp, HighDensityPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCapturing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    color = Color.Black,
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Capture Photo",
                                    tint = HighDensityPrimary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }

                    // Alignment Frame Guide / Tips Icon
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CropFree,
                            contentDescription = "Framing Guide",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Text(
                    text = "Tap shutter to snap question • Tap screen to focus",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Scanning Framing Box with animated scan line and focus point
 */
@Composable
private fun ScannerOverlay(
    onTapFocusPoint: Offset?
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan_line"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val boxWidth = maxWidth * 0.88f
        val boxHeight = maxHeight * 0.44f

        val boxWidthPx = with(LocalDensity.current) { boxWidth.toPx() }
        val boxHeightPx = with(LocalDensity.current) { boxHeight.toPx() }

        // Darkened Scrim Outside Framing Box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val left = (size.width - boxWidthPx) / 2f
                    val top = (size.height - boxHeightPx) / 2.3f

                    // Draw semi-transparent background
                    drawRect(color = Color.Black.copy(alpha = 0.45f))

                    // Clear out the center box
                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = Offset(left, top),
                        size = Size(boxWidthPx, boxHeightPx),
                        cornerRadius = CornerRadius(18.dp.toPx()),
                        blendMode = BlendMode.Clear
                    )

                    // Draw Corner Target Brackets
                    val bracketLength = 32.dp.toPx()
                    val strokeW = 4.dp.toPx()
                    val cornerR = 18.dp.toPx()
                    val bracketColor = Color(0xFF00E5FF)

                    // Top-Left
                    drawLine(bracketColor, Offset(left + cornerR, top), Offset(left + cornerR + bracketLength, top), strokeW)
                    drawLine(bracketColor, Offset(left, top + cornerR), Offset(left, top + cornerR + bracketLength), strokeW)

                    // Top-Right
                    val right = left + boxWidthPx
                    drawLine(bracketColor, Offset(right - cornerR, top), Offset(right - cornerR - bracketLength, top), strokeW)
                    drawLine(bracketColor, Offset(right, top + cornerR), Offset(right, top + cornerR + bracketLength), strokeW)

                    // Bottom-Left
                    val bottom = top + boxHeightPx
                    drawLine(bracketColor, Offset(left + cornerR, bottom), Offset(left + cornerR + bracketLength, bottom), strokeW)
                    drawLine(bracketColor, Offset(left, bottom - cornerR), Offset(left, bottom - cornerR - bracketLength), strokeW)

                    // Bottom-Right
                    drawLine(bracketColor, Offset(right - cornerR, bottom), Offset(right - cornerR - bracketLength, bottom), strokeW)
                    drawLine(bracketColor, Offset(right, bottom - cornerR), Offset(right, bottom - cornerR - bracketLength), strokeW)

                    // Scan Laser Line
                    val currentScanY = top + (boxHeightPx * scanProgress)
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF00E5FF).copy(alpha = 0.9f),
                                Color.White,
                                Color(0xFF00E5FF).copy(alpha = 0.9f),
                                Color.Transparent
                            )
                        ),
                        start = Offset(left + 8.dp.toPx(), currentScanY),
                        end = Offset(right - 8.dp.toPx(), currentScanY),
                        strokeWidth = 3.dp.toPx()
                    )
                }
        )

        // Text Guidance above frame
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-boxHeight / 2) - 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.65f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Text(
                    text = "📐 Align formula, question, or text inside frame",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Tap to focus ring animation
        if (onTapFocusPoint != null) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(onTapFocusPoint.x.toInt() - 25, onTapFocusPoint.y.toInt() - 25) }
                    .size(50.dp)
                    .border(2.dp, AmberTertiaryDark, CircleShape)
            )
        }
    }
}

/**
 * Preview & Confirmation Screen after snapping photo
 */
@Composable
private fun CapturedImageReviewView(
    bitmap: Bitmap,
    selectedSubject: String,
    onSelectSubject: (String) -> Unit,
    promptInput: String,
    onPromptChange: (String) -> Unit,
    onRetake: () -> Unit,
    onConfirmSolve: () -> Unit,
    onClose: () -> Unit
) {
    val subjects = listOf("Mathematics", "Physics", "Chemistry", "Biology", "Computer Science", "Economics")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onRetake,
                modifier = Modifier.size(36.dp).testTag("review_retake_top_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retake", tint = MaterialTheme.colorScheme.onSurface)
            }

            Text(
                text = "Question Photo Captured",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(36.dp).testTag("review_close_button")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
            }
        }

        // Captured Photo Preview Card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.Black.copy(alpha = 0.05f),
            border = BorderStroke(1.5.dp, HighDensityPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Captured Question",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                        Text("High-Res Photo Ready", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Subject Selector
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "SELECT SUBJECT",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subjects.forEach { subj ->
                    val isSelected = subj == selectedSubject
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else HighDensityBorder),
                        modifier = Modifier
                            .clickable { onSelectSubject(subj) }
                            .testTag("review_subject_$subj")
                    ) {
                        Text(
                            text = subj,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Optional Hint/Prompt Field
        OutlinedTextField(
            value = promptInput,
            onValueChange = onPromptChange,
            placeholder = {
                Text(
                    "Optional question context or specific part to solve (e.g. 'Solve part b only')...",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .testTag("review_question_hint_field"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = HighDensityBorder
            )
        )

        // Action Buttons: Retake vs Solve with AI
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onRetake,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("review_retake_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, HighDensityBorder)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Retake Photo", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onConfirmSolve,
                modifier = Modifier
                    .weight(1.5f)
                    .height(50.dp)
                    .testTag("review_solve_ai_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Solve with AI", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Camera Permission Denied View
 */
@Composable
private fun CameraPermissionDeniedContent(
    onRequestPermission: () -> Unit,
    onPickFromGallery: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Camera Permission Needed",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "To snap photos of study questions, textbook problems, and notes for instant AI reasoning, please grant camera access.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRequestPermission,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("grant_camera_permission_button")
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Grant Camera Permission", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onPickFromGallery,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("pick_gallery_fallback_button")
        ) {
            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pick Image from Gallery", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Back to Snap & Solve", fontWeight = FontWeight.Medium)
        }
    }
}

/**
 * Utility function to convert CameraX ImageProxy to Bitmap with correct rotation
 */
private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
    val buffer: ByteBuffer = imageProxy.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    val rotation = imageProxy.imageInfo.rotationDegrees
    return if (rotation != 0 && bitmap != null) {
        val matrix = Matrix().apply {
            postRotate(rotation.toFloat())
        }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
}
