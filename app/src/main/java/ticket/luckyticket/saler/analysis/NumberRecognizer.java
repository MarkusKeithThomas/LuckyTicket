package ticket.luckyticket.saler.analysis;

import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberRecognizer {
    private TessBaseAPI tessBaseAPI;

    public NumberRecognizer(TessBaseAPI tessBaseAPI) {
        this.tessBaseAPI = tessBaseAPI;
    }

    /**
     * Recognizes numbers from the given Bitmap image using OpenCV and Tesseract.
     *
     * @param bitmap The input image in Bitmap format.
     * @return A string representing the recognized numbers.
     */
    public String recognizeNumbers(Bitmap bitmap) {
        // Convert the Bitmap to a Mat (OpenCV format)
        Mat imgMat = new Mat();
        Utils.bitmapToMat(bitmap, imgMat);

        // Convert the image to grayscale
        Mat grayMat = new Mat();
        Imgproc.cvtColor(imgMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        // Apply GaussianBlur to smooth the image and reduce noise
        Imgproc.GaussianBlur(grayMat, grayMat, new Size(7, 7), 0);

        // Apply Otsu's thresholding to highlight the number regions
        Mat binaryMat = new Mat();
        Imgproc.threshold(grayMat, binaryMat, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

        // Apply morphological operations to remove small details
        Mat morphMat = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.morphologyEx(binaryMat, morphMat, Imgproc.MORPH_CLOSE, kernel);

        // Find contours (boundaries) in the image
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(morphMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Filter contours based on size and extract bounding boxes for potential number regions
        List<Rect> boundingBoxes = filterContoursBySize(contours);

        // Sort bounding boxes from top-to-bottom and left-to-right
        Collections.sort(boundingBoxes, (r1, r2) -> {
            if (Math.abs(r1.y - r2.y) > 10) {
                return Integer.compare(r1.y, r2.y);
            } else {
                return Integer.compare(r1.x, r2.x);
            }
        });

        // Recognize text within each bounding box using Tesseract
        String recognizedText = recognizeTextFromRegions(grayMat, boundingBoxes);

        // Release memory of OpenCV objects
        imgMat.release();
        grayMat.release();
        morphMat.release();
        binaryMat.release();
        hierarchy.release();

        // Check if recognized text contains exactly 6 digits
        return (recognizedText.length() == 6) ? recognizedText : "Lỗi Nhận Diện Số. Mời Bạn Nhập Tay.";
    }

    /**
     * Filters the contours based on area size to remove noise.
     *
     * @param contours The list of contours found in the image.
     * @return A list of bounding boxes around valid contours.
     */
    private List<Rect> filterContoursBySize(List<MatOfPoint> contours) {
        List<Rect> boundingBoxes = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            double contourArea = Imgproc.contourArea(contour);
            if (contourArea > 1000) {  // Adjust threshold as needed
                boundingBoxes.add(Imgproc.boundingRect(contour));
            }
        }
        return boundingBoxes;
    }

    /**
     * Recognizes text from each bounding box using Tesseract.
     *
     * @param grayMat       The grayscale image.
     * @param boundingBoxes The list of bounding boxes containing potential numbers.
     * @return A string containing the recognized numbers.
     */
    private String recognizeTextFromRegions(Mat grayMat, List<Rect> boundingBoxes) {
        StringBuilder recognizedTextBuilder = new StringBuilder();

        for (Rect boundingBox : boundingBoxes) {
            // Extract region of interest (ROI) from the grayscale image
            Mat roi = new Mat(grayMat, boundingBox);
            Bitmap roiBitmap = Bitmap.createBitmap(roi.cols(), roi.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(roi, roiBitmap);

            // Use Tesseract to recognize text in the ROI
            tessBaseAPI.setImage(roiBitmap);
            String recognizedText = tessBaseAPI.getUTF8Text();

            // Append recognized text without adding line breaks
            recognizedTextBuilder.append(recognizedText);
        }

        return recognizedTextBuilder.toString();
    }
}
