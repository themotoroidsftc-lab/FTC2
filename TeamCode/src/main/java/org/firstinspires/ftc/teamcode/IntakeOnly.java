package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Intake Only", group = "TestBed")
public class IntakeOnly extends OpMode {
    private DcMotor intakeLeft;
    private DcMotor intakeRight;
    double Queller = 0.2; //Value shrinker
    double minpower = 0.0;

    @Override
    public void init() {
        intakeRight = hardwareMap.get(DcMotor.class, "intake1");
        intakeLeft = hardwareMap.get(DcMotor.class, "intake2");

        intakeRight.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {

        if(gamepad1.a){
            Queller -= 0.01;
        }
        if(gamepad1.b){
            Queller += 0.01;
        }
        if(gamepad1.y){
            minpower += 0.01;
        }
        if(gamepad1.x){
            minpower -= 0.01;

        }

        intakeLeft.setPower(gamepad1.right_trigger-gamepad1.left_trigger);
        intakeRight.setPower(gamepad1.right_trigger-gamepad1.left_trigger);

        telemetry.addData("Queller: ", Queller);
        telemetry.addData("Minimum Power: ", minpower);
        telemetry.update();
    }
}
