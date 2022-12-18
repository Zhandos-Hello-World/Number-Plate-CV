package com.zhandos.numberplatecv.list

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import com.zhandos.numberplatecv.databinding.FragmentNumberPlateBinding
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc


class NumberPlateFragment : Fragment() {
    private val args by navArgs<NumberPlateFragmentArgs>()
    private var _binding: FragmentNumberPlateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNumberPlateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.location?.let { location ->
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, location)
            } else {
                val source = ImageDecoder.createSource(requireContext().contentResolver, location)
                ImageDecoder.decodeBitmap(
                    source
                ) { decoder, _, _ -> decoder.isMutableRequired = true }
            }

            val type = CvType.CV_8UC1
            //IMAGE
            val image = Mat(bitmap.height, bitmap.width, type)
            Utils.bitmapToMat(bitmap, image, true)

            //GRAY image
            val grayImage = Mat(bitmap.height, bitmap.width, type)
            Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY)

            val smoothedImage = Mat(bitmap.height, bitmap.width, type)
            Imgproc.bilateralFilter(grayImage, smoothedImage, 11, 3077.0, 3077.0)

            //edged
            val edged = Mat(bitmap.height, bitmap.width, type)
            Imgproc.Canny(smoothedImage, edged, 30.0, 200.0, 7)

            //added contours
            val contourImage = Mat(bitmap.height, bitmap.width, type)
            var contours: List<MatOfPoint> = ArrayList()
            Imgproc.findContours(
                edged.clone(),
                contours,
                contourImage,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
            )

            contours = contours.sortedWith(compareBy { Imgproc.contourArea(it) }).asReversed()
            val approx = MatOfPoint2f()


            for (contour in contours) {
                val c = MatOfPoint2f().apply { fromList(contour.toList()) }

                val perimeter = Imgproc.arcLength(c, true)
                Imgproc.approxPolyDP(c, approx, 0.018 * perimeter, true)

                if (approx.toList().size == 4) {
                    val rect = Imgproc.boundingRect(contour)
                    Imgproc.drawContours(
                        image,
                        listOf(contour),
                        -1,
                        Scalar(0.0, 255.0, 0.0),
                        10
                    )
                    break
                }
                c.release()
            }

            Utils.matToBitmap(image, bitmap)
            binding.numberPlate.load(bitmap)
        }
    }


}
