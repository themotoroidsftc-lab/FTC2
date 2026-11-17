package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Test Telemetry", group = "TestBed")
@Disabled
public class TestTelem extends OpMode {
    @Override
    public void init() {
        telemetry.addData("Waiting...","  (init)");
        telemetry.update();
    }

    @Override
    public void loop() {
        telemetry.addData("Hello", "a");
        telemetry.update();
    }

}
