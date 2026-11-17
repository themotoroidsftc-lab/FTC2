package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;



public class GrayProcessor implements VisionProcessor {
    Mat gray = new Mat();
    Mat small = new Mat();
    Bitmap bitmap;

    int frame_h = 64;
    int frame_w = 100;

    int[][] pixelsI = new int[frame_h][frame_w];


    @Override
    public void init(int width, int height, CameraCalibration calibration) {}

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos){
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.resize(gray, small, new Size(frame_w, frame_h));
        bitmap = Bitmap.createBitmap(
                small.cols(),
                small.rows(),
                Bitmap.Config.ARGB_8888
        );
        Utils.matToBitmap(small, bitmap);

//        for (int y = 0; y < frame_h; y++) {
//            for (int x = 0; x < frame_w; x++) {
//                pixelsI[y][x] = 255;
//            }
//        }
        byte[] data = new byte[frame_w * frame_h];
        small.get(0, 0, data);

        for (int y = 0; y < frame_h; y++) {
            for (int x = 0; x < frame_w; x++) {
                pixelsI[y][x] = data[y * frame_w + x] & 0xFF;
            }
        }
        return null;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int width, int height, float scale, float density, Object userContext){
        if (bitmap != null){
            Paint paint = new Paint();
            canvas.drawBitmap(bitmap, null, new android.graphics.Rect(0,0,width, height), paint);
        }
    }

    public synchronized float[][] get_frame(){
        float[][] pixelsF = new float[frame_h][frame_w];
        for(int y = 0; y < frame_h; y++){
            for(int x = 0; x < frame_w; x++){
                pixelsF[y][x] = ((float) pixelsI[y][x] / 255.0f);
            }
        }
        return pixelsF;
    }

//    public float getpx(){
//        return (float) pixelsI[0][0];
//    }
}
