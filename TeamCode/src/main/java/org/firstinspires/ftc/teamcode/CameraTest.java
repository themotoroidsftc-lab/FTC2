package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.GrayProcessor;
import org.firstinspires.ftc.vision.VisionPortal;

@TeleOp(name="Camera Test", group="Vision")
@Disabled
public class CameraTest extends LinearOpMode {
    GrayProcessor proc = new GrayProcessor();
    @Override
    public void runOpMode() {

        WebcamName webcam = hardwareMap.get(WebcamName.class,"Webcam 1");
        VisionPortal portal = new VisionPortal.Builder()
                .setCamera(webcam)
                .enableLiveView(true)
                .addProcessor(proc)
                .build();

        telemetry.addLine("Camera initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            //telemetry.addLine("Running");
            float avgB = avgBright(proc.get_frame());
            telemetry.addLine("Avg Brightness: " + avgB);
            //float[][] frame = proc.get_frame();


            telemetry.update();
        }

        portal.close();
    }

    public float avgBright(float[][] frame){
        float total = 0;
        float px = 0f;
        for(float[] i : frame){
            for(float j : i){
                total += j;
                px++;
            }
        }
        return total/px;
    }
}