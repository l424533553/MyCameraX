//package com.android.example.cameraxbasic.fragments
//
//import android.app.Activity
//import android.content.Context
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.util.DisplayMetrics
//import android.util.Log
//import android.widget.ImageView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.*
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.lifecycleScope
//import com.android.example.cameraxbasic.R
//import com.room.database.room.database.RoomRepository
//import com.room.database.room.persistence.OrderInfo
//import com.room.database.room.persistence.axe.AxeImage
//import com.room.database.room.persistence.fpms.FpmsImage
//import com.xuanyuan.basemodule.jetpack.LiveBus
//import kotlinx.android.synthetic.main.activity_camerax.*
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import java.io.File
//import java.nio.ByteBuffer
//import java.text.SimpleDateFormat
//import java.util.*
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//import kotlin.collections.ArrayList
//import kotlin.math.abs
//import kotlin.math.max
//import kotlin.math.min
//
///** Helper type alias used for analysis use case callbacks */
//typealias LumaListener = (luma: Double) -> Unit
//
//val EXTENSION_WHITELIST = arrayOf("JPG")
//
//open class CameraXActivity : AppCompatActivity() {
//
//    private lateinit var mContext: Context
//    private lateinit var mActivity: Activity
//
//    public open var imagePhoto: ImageView? = null
//
//    private lateinit var outputDirectory: File
////    private lateinit var broadcastManager: LocalBroadcastManager
//
//    private var displayId: Int = -1
//    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
//    private var preview: Preview? = null
//    private var imageCapture: ImageCapture? = null
//    private var imageAnalyzer: ImageAnalysis? = null
//    private var camera: Camera? = null
//
//    private var cameraProvider: ProcessCameraProvider? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_camerax)
//        mContext = this
//        mActivity = this
//        cameraExecutor = Executors.newSingleThreadExecutor()
//
//    }
//
//    protected open fun initCameraX() {
//        // Initialize our background executor
//
//
////        broadcastManager = LocalBroadcastManager.getInstance(this)
////
////        // Set up the intent filter that will receive events from our main activity
////        val filter = IntentFilter().apply { addAction(KEY_EVENT_ACTION) }
////        broadcastManager.registerReceiver(volumeDownReceiver, filter)
//
//
//        // Determine the output directory
//        outputDirectory = getOutputDirectory(this)
//
//        // Wait for the views to be properly laid out
//        viewFinder.post {
//
//            // Keep track of the display in which this view is attached
//            displayId = viewFinder.display.displayId
//
//            // Build UI controls
//            updateCameraUi()
//
//            // Set up the camera and its use cases
//            setUpCamera()
//        }
//    }
//
//    companion object {
//
//        /** Use external media if it is available, our app's file directory otherwise */
//        fun getOutputDirectory(context: Context): File {
//            val appContext = context.applicationContext
//            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
//                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
//            }
//            return if (mediaDir != null && mediaDir.exists())
//                mediaDir else appContext.filesDir
//        }
//
//        const val EVENT_DATA_NOTIFY_IMAGE = "EVENT_DATA_NOTIFY_IMAGE"
//        private const val TAG = "CameraXBasic"
//        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
//        private const val PHOTO_EXTENSION = ".jpg"
//        private const val RATIO_4_3_VALUE = 4.0 / 3.0
//        private const val RATIO_16_9_VALUE = 16.0 / 9.0
//
//
//        /** Helper function used to create a timestamped file */
//        private fun createFile(baseFolder: File, format: String, extension: String) =
//                File(baseFolder, SimpleDateFormat(format, Locale.US)
//                        .format(System.currentTimeMillis()) + extension)
//    }
//
//
//    /** Blocking camera operations are performed using this executor */
//    private var cameraExecutor: ExecutorService? = null
//
//    override fun onDestroy() {
//        super.onDestroy()
//        cameraExecutor?.shutdown()
//    }
//
//    /** Initialize CameraX, and prepare to bind the camera use cases
//     * 初始化CameraX，并准备绑定相机用例
//     */
//    private fun setUpCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(mContext)
//        cameraProviderFuture.addListener(Runnable {
//            cameraProvider = cameraProviderFuture.get()
//            // Select lensFacing depending on the available cameras
////            lensFacing = when {
////                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
////                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
////                else -> throw IllegalStateException("Back and front camera are unavailable")
////            }
//            // Build and bind the camera use cases
//            bindCameraUseCases()
//        }, ContextCompat.getMainExecutor(mContext))
//    }
//
//    /** Declare and bind preview, capture and analysis use cases
//     * 声明并绑定预览，捕获和分析用例
//     * */
//    private fun bindCameraUseCases() {
//        // Get screen metrics used to setup camera for full screen resolution
//        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
//        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")
//
//        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
//        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")
//
//        val rotation = viewFinder.display.rotation
//
//        // CameraProvider
//        val cameraProvider = cameraProvider
//                ?: throw IllegalStateException("Camera initialization failed.")
//
//        // CameraSelector
//        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
//
//        // Preview
//        preview = Preview.Builder()
//                // We request aspect ratio but no resolution
//                .setTargetAspectRatio(screenAspectRatio)
//                // Set initial target rotation
//                .setTargetRotation(rotation)
//                .build()
//
//        // ImageCapture
//        imageCapture = ImageCapture.Builder()
//                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//                // We request aspect ratio but no resolution to match preview config, but letting
//                // CameraX optimize for whatever specific resolution best fits our use cases
//                .setTargetAspectRatio(screenAspectRatio)
//                // Set initial target rotation, we will have to call this again if rotation changes
//                // during the lifecycle of this use case
//                .setTargetRotation(rotation)
//                .build()
//
//        // ImageAnalysis
//        imageAnalyzer = ImageAnalysis.Builder()
//                // We request aspect ratio but no resolution
//                .setTargetAspectRatio(screenAspectRatio)
//                // Set initial target rotation, we will have to call this again if rotation changes
//                // during the lifecycle of this use case
//                .setTargetRotation(rotation)
//                .build()
//                // The analyzer can then be assigned to the instance
//                .also {
//                    cameraExecutor?.let { it1 ->
//                        it.setAnalyzer(it1, LuminosityAnalyzer { luma ->
//                            // Values returned from our analyzer are passed to the attached listener
//                            // We log image analysis results here - you should do something useful
//                            // instead!
//                            // Log.d(TAG, "Average luminosity: $luma")
//                        })
//                    }
//                }
//
//        // Must unbind the use-cases before rebinding them
//        cameraProvider.unbindAll()
//
//        try {
//            // A variable number of use-cases can be passed here -
//            // camera provides access to CameraControl & CameraInfo
//            camera = cameraProvider.bindToLifecycle(
//                    this, cameraSelector, preview, imageCapture, imageAnalyzer)
//
//            // Attach the viewfinder's surface provider to preview use case
//            preview?.setSurfaceProvider(viewFinder.createSurfaceProvider())
//        } catch (exc: Exception) {
//            Log.e(TAG, "Use case binding failed", exc)
//        }
//    }
//
//    /**
//     *  [androidx.camera.core.ImageAnalysisConfig] requires enum value of
//     *  [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
//     *
//     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
//     *  of preview ratio to one of the provided values.
//     *
//     *  @param width - preview width
//     *  @param height - preview height
//     *  @return suitable aspect ratio
//     */
//    private fun aspectRatio(width: Int, height: Int): Int {
//        val previewRatio = max(width, height).toDouble() / min(width, height)
//        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
//            return AspectRatio.RATIO_4_3
//        }
//        return AspectRatio.RATIO_16_9
//    }
//
//    /** Method used to re-draw the camera UI controls, called every time configuration changes. */
//    private fun updateCameraUi() {
//        // Inflate a new view containing all UI for controlling the camera
////        val controls = View.inflate(mContext, R.layout.camera_ui_container, container)
//
//        // In the background, load latest photo taken (if any) for gallery thumbnail
//        lifecycleScope.launch(Dispatchers.IO) {
//            outputDirectory.listFiles { file ->
//                EXTENSION_WHITELIST.contains(file.extension.toUpperCase(Locale.ROOT))
//            }?.max()?.let {
////                setGalleryThumbnail(Uri.fromFile(it))
//            }
//        }
//    }
//
//    protected open fun takePhoto(orderInfo: OrderInfo, fileName: String, deviceno: String,istemp:Boolean) {
//        var orderNo: String
//        var takePhotoTime: Long
//        var deviceNo: String
//        orderInfo.let { orderInfo ->
//            orderNo = orderInfo.billcode
//            takePhotoTime = orderInfo.longTime
//            if (takePhotoTime < 10000) {
//                return
//            }
//            deviceNo = deviceno
//        }
//        imageCapture?.let { imageCapture ->
//            // 创建照片保存文件
//            val sno = "$orderNo-$fileName"
//            val photoFile = File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "$sno.jpg")
//
//
////            val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
//
//            // 设置图像捕获元数据
//            val metadata = ImageCapture.Metadata().apply {
//                // Mirror image when using the front camera
//                isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
//            }
//
//            // Create output options object which contains file + metadata
//            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
//                    .setMetadata(metadata)
//                    .build()
//
//
//            // Setup image capture listener which is triggered after photo has been taken
//            cameraExecutor?.let {
//                imageCapture.takePicture(outputOptions, it, object : ImageCapture.OnImageSavedCallback {
//                    override fun onError(exc: ImageCaptureException) {
//                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
//                    }
//
//                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                        val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
//                        Log.d(TAG, "Photo capture succeeded: $savedUri")
//                        val path = savedUri.path ?: return
//                        LiveBus.sendLiveBean(EVENT_DATA_NOTIFY_IMAGE, path)
//
//                        saveImage(deviceNo, orderNo, sno, path, takePhotoTime,istemp)
//                        otherSet()
//                    }
//                })
//            }
//
//        }
//    }
//
//    private fun otherSet() {
//        //                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//        ////                        sendBroadcast(Intent(android.hardware.Camera.ACTION_NEW_PICTURE, savedUri))
//        //                    }
//
//        // If the folder selected is an external media directory, this is unnecessary but otherwise other apps will not be able to access our  images unless we scan them using [MediaScannerConnection]
//        // 如果选择的文件夹是外部媒体目录，则没有必要，但是其它应用程序将无法访问我们的图像，除非我们使用[MediaScannerConnection]对其进行扫描
//        //                    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(savedUri.toFile().extension)
//        //                    MediaScannerConnection.scanFile(
//        //                            mContext,
//        //                            arrayOf(savedUri.toFile().absolutePath),
//        //                            arrayOf(mimeType)
//        //                    ) { _, uri ->
//        //                        Log.d(TAG, "Image capture scanned into media store: $uri")
//        //                    }
//
//    }
//
//    private fun saveImage(deviceNo: String, orderNo: String, sno: String, path: String, takePhotoTime: Long,istemp:Boolean) {
//        val yyyyTossFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA)
//        val photoTime = yyyyTossFormat.format(takePhotoTime)
//        // 保存 图片信息,
//        val upImage = AxeImage()
//        upImage.deviceNo = deviceNo
//        upImage.sno = sno
//        upImage.orderNo = orderNo
//        upImage.longTime = takePhotoTime
//        upImage.path = path
//        upImage.isTemp=istemp
//        upImage.orderTime = photoTime
//        RoomRepository.getAxeImageDao(mContext).insert(upImage)
//
//        val fpmsImage = FpmsImage()
//        fpmsImage.deviceNo = deviceNo
//        fpmsImage.sno = sno
//        fpmsImage.orderNo = orderNo
//        fpmsImage.path = path
//        fpmsImage.isTemp=istemp
//        fpmsImage.longTime = takePhotoTime
//        fpmsImage.orderTime = photoTime
//        RoomRepository.getFpmsImageDao(mContext).insert(fpmsImage)
//    }
//
//
//    /**
//     * Our custom image analysis class.
//     * 我们的自定义图像分析课程
//     *
//     * <p>All we need to do is override the function `analyze` with our desired operations.
//     * 我们需要做的就是用我们想要的操作覆盖函数“ analyze”
//     * Here,we compute the average luminosity of the image by looking at the Y plane of the YUV frame.
//     * 在这里，我们通过查看YUV帧的Y平面来计算图像的平均亮度
//     */
//    private class LuminosityAnalyzer(listener: LumaListener? = null) : ImageAnalysis.Analyzer {
//        private val frameRateWindow = 8
//        private val frameTimestamps = ArrayDeque<Long>(5)
//        private val listeners = ArrayList<LumaListener>().apply { listener?.let { add(it) } }
//        private var lastAnalyzedTimestamp = 0L
//        var framesPerSecond: Double = -1.0
//            private set
//
//        /**
//         * Used to add listeners that will be called with each luma computed
//         */
//        fun onFrameAnalyzed(listener: LumaListener) = listeners.add(listener)
//
//        /**
//         * Helper extension function used to extract a byte array from an image plane buffer
//         */
//        private fun ByteBuffer.toByteArray(): ByteArray {
//            rewind()    // Rewind the buffer to zero
//            val data = ByteArray(remaining())
//            get(data)   // Copy the buffer into a byte array
//            return data // Return the byte array
//        }
//
//        /**
//         * Analyzes an image to produce a result.
//         *
//         * <p>The caller is responsible for ensuring this analysis method can be executed quickly
//         * enough to prevent stalls in the image acquisition pipeline. Otherwise, newly available
//         * images will not be acquired and analyzed.
//         *
//         * <p>The image passed to this method becomes invalid after this method returns. The caller
//         * should not store external references to this image, as these references will become
//         * invalid.
//         *
//         * @param image image being analyzed VERY IMPORTANT: Analyzer method implementation must
//         * call image.close() on received images when finished using them. Otherwise, new images
//         * may not be received or the camera may stall, depending on back pressure setting.
//         *
//         */
//        override fun analyze(image: ImageProxy) {
//            // If there are no listeners attached, we don't need to perform analysis
//            if (listeners.isEmpty()) {
//                image.close()
//                return
//            }
//
//            // Keep track of frames analyzed
//            val currentTime = System.currentTimeMillis()
//            frameTimestamps.push(currentTime)
//
//            // Compute the FPS using a moving average
//            while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
//            val timestampFirst = frameTimestamps.peekFirst() ?: currentTime
//            val timestampLast = frameTimestamps.peekLast() ?: currentTime
//            framesPerSecond = 1.0 / ((timestampFirst - timestampLast) /
//                    frameTimestamps.size.coerceAtLeast(1).toDouble()) * 1000.0
//
//            // Analysis could take an arbitrarily long amount of time
//            // Since we are running in a different thread, it won't stall other use cases
//
//            lastAnalyzedTimestamp = frameTimestamps.first
//
//            // Since format in ImageAnalysis is YUV, image.planes[0] contains the luminance plane
//            val buffer = image.planes[0].buffer
//
//            // Extract image data from callback object
//            val data = buffer.toByteArray()
//
//            // Convert the data into an array of pixel values ranging 0-255
//            val pixels = data.map { it.toInt() and 0xFF }
//
//            // Compute average luminance for the image
//            val luma = pixels.average()
//
//            // Call all listeners with new value
//            listeners.forEach { it(luma) }
//
//            image.close()
//        }
//    }
//
//}
