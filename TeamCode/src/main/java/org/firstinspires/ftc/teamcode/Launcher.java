package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Launcher", group = "TestBed")
public class Launcher extends OpMode{
    private DcMotor motorLF;
    private DcMotor motorLB;
    private DcMotor motorRF;
    private DcMotor motorRB;
    private Servo intake1;
    private Servo intake2;
    double power = 0.0;
    int mode = 0;

    @Override
    public void init(){
        //instantiates the objects
        motorLF = hardwareMap.get(DcMotor.class, "Motor1");
        motorLB = hardwareMap.get(DcMotor.class, "Motor2");
        motorRF = hardwareMap.get(DcMotor.class, "Motor3");
        motorRB = hardwareMap.get(DcMotor.class, "Motor4");
        intake1 = hardwareMap.get(Servo.class, "servo1");
        intake2 = hardwareMap.get(Servo.class, "servo2");

    }

    @Override
    public void loop(){
        telemetry.addData("Is Running: ","TRUE");
        telemetry.addData("Launcher: ", motorLF.getPower());
        telemetry.addData("Mode: ", mode==1 ? "TRIG" : "VAR");
        telemetry.addData("RunDir: ", motorLF.getDirection().toString());
        if(mode==1){
            power = gamepad1.right_trigger;
        }else{
            if(gamepad1.right_trigger > 0.4){
                power += gamepad1.right_trigger *= 0.05;
            }
            if(gamepad1.left_trigger > 0.4){
                power -= gamepad1.left_trigger *= 0.05;
            }
        }
        if(gamepad1.a){
            if(motorLF.getDirection() == DcMotor.Direction.REVERSE){
                motorLF.setDirection(DcMotor.Direction.FORWARD);
            }else {
                motorLF.setDirection(DcMotorSimple.Direction.REVERSE);
            }
        }
        if(gamepad1.b){
            mode--;
            mode = Math.abs(mode);
        }
        motorLF.setPower(power);
    }
}
