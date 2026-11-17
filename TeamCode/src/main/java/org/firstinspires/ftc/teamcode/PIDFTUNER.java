package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "PIDFTUNER", group = "TestBed")
public class PIDFTUNER extends OpMode {
    DcMotorEx motorLF;
    DcMotorEx motorLB;
    DcMotorEx motorRF;
    DcMotorEx motorRB;

    double currentVelocity_left_front;
    double currentVelocity_right_front;
    double currentVelocity_left_back;
    double currentVelocity_right_back;
    double maxVelocity = 0.0;
    double averageVelocity;
    double polling = 0.0;
    double velocity = 0.0;

    @Override
    public void init() {
        motorLF = hardwareMap.get(DcMotorEx.class, "Motor1");
        motorLB = hardwareMap.get(DcMotorEx.class, "Motor2");
        motorRF = hardwareMap.get(DcMotorEx.class, "Motor3");
        motorRB = hardwareMap.get(DcMotorEx.class, "Motor4");
        motorLF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorLB.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorRF.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorRB.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorLF.setDirection(DcMotor.Direction.REVERSE);
        motorLB.setDirection(DcMotor.Direction.REVERSE);
        motorRB.setDirection(DcMotor.Direction.REVERSE);
        telemetry.addLine("PLACE ROBOT ON A LONG LINE OF FOAM TILES, IT WILL GO AT MAX SPEED");
        telemetry.update();
    }

    @Override
    public void loop() {
        motorLF.setPower(velocity);
        motorLB.setPower(velocity);
        motorRF.setPower(velocity);
        motorRB.setPower(velocity);
        currentVelocity_left_front = motorLF.getVelocity();
        currentVelocity_right_front = motorRF.getVelocity();
        currentVelocity_left_back = motorLB.getVelocity();
        currentVelocity_right_back = motorRB.getVelocity();
        averageVelocity = ((currentVelocity_left_back + currentVelocity_left_front + currentVelocity_right_front + currentVelocity_right_back) / 4);

        if (polling == 1) {
            if (averageVelocity > maxVelocity) {
                maxVelocity = averageVelocity;
            }
        }else{
            if (polling > 1){polling = 1;}else{
                telemetry.addLine("DATA IS NOT ACCURATE!!!");
                telemetry.addLine("");
                telemetry.addLine("\n...");
                telemetry.addLine("\n...");
            }

        }
        if(gamepad1.a){
            polling = 1;
        }
        if(gamepad1.b){
            polling = 0;
        }
        if(gamepad1.x){
            polling = -0.3;
        }
        velocity = polling;
        telemetry.addData("Current Velocity", averageVelocity);
        telemetry.addData("     LF: ", currentVelocity_left_front);
        telemetry.addData("     LB: ", currentVelocity_left_back);
        telemetry.addData("     RF: ", currentVelocity_right_front);
        telemetry.addData("     RB: ", currentVelocity_right_back);
        telemetry.addLine("\n...");
        telemetry.addData("Maximum Velocity", maxVelocity);
        telemetry.addData("\nTarget Velocity: ", polling);
        telemetry.addData("Command Velocity", velocity);
        telemetry.update();
    }

}