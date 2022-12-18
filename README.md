# Number Plate recognition(Computer vision) Android application
For Abylay Omar

## Teammate: :shipit:
> Turdaly Aruzhan
> 
> Nayashova Ayala
> 
> Baiguziyev Iliyas
> 
> Yerkinova Aksaule
> 
> Baimurat Zhandos


# Documentation

###### Load image from Bitmap
            val image = Mat()
            Utils.bitmapToMat(bitmap, image, true)
###### Change colors in pictures to gray
            val grayImage = Mat(bitmap.height, bitmap.width, CvType.CV_8UC1)
            Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY)
###### Convert to anti-aliased image to clean up tiny objects
           val smoothedImage = Mat(bitmap.height, bitmap.width, CvType.CV_8UC1)
           Imgproc.bilateralFilter(grayImage, smoothedImage, 11, 5077.0, 5077.0)
###### Change to edge to show white line
            val edged = Mat(bitmap.height, bitmap.width, CvType.CV_8UC1)
            Imgproc.Canny(smoothedImage, edged, 30.0, 200.0, 3)
###### Load to contour all lines in an image
            val contourImage = Mat(bitmap.height, bitmap.width, CvType.CV_8UC1)
            var contours: List<MatOfPoint> = ArrayList()
            Imgproc.findContours(
                edged.clone(),
                contours,
                contourImage,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
            )
###### Sorting contours to get all lines
            contours = contours.sortedWith(compareBy { Imgproc.contourArea(it) }).asReversed()

###### If we find a contour with 4 points (Rectangle), then draw its edges in green.
                    Imgproc.drawContours(
                        image,
                        listOf(contour),
                        -1,
                        Scalar(0.0, 255.0, 0.0),
                        10
                    )


            
            




