package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous(name = "Auto test", group = "Auto")
public class Auto2 extends OpMode {
    private DcMotor motorLF;
    private DcMotor motorLB;
    private DcMotor motorRF;
    private DcMotor motorRB;
    int step = 0;
    double intakestrength = 0.0;
    double lf = 0;
    double lb = 0;
    double rf = 0;
    double rb = 0;

    @Override
    public void init() {
        motorLF = hardwareMap.get(DcMotor.class, "Motor1");
        //motorLF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorLF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ((DcMotorEx) motorLF ).setPositionPIDFCoefficients(3.0);

        motorLB = hardwareMap.get(DcMotor.class, "Motor2");
        motorLB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ((DcMotorEx) motorLB ).setPositionPIDFCoefficients(3.0);

        motorRF = hardwareMap.get(DcMotor.class, "Motor3");
        motorRF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ((DcMotorEx) motorRF ).setPositionPIDFCoefficients(3.0);

        motorRB = hardwareMap.get(DcMotor.class, "Motor4");
        motorRB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ((DcMotorEx) motorRB ).setPositionPIDFCoefficients(3.0);


        motorLF.setDirection(DcMotor.Direction.FORWARD);
        motorLB.setDirection(DcMotor.Direction.FORWARD);
        motorRF.setDirection(DcMotor.Direction.REVERSE);
        motorRB.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addLine("Initialized; verify wheel directions and hub orientation");

    }

    @Override
    public void start() {
    }

    public int inchestoticks(double inches) {
        return (int) Math.floor(inches * 5.92);
    }

    public void nextStep() {
        switch(step){
            case 1:
                motorLF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motorLF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motorLB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motorRF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motorRB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                telemetry.addData("GOT TO STOP_RESET", 1);
                telemetry.update();
                telemetry.addData("GOT TO STOP_RESET", 2);
                telemetry.update();

                motorLF.setTargetPosition(inchestoticks(48));
                motorLB.setTargetPosition(inchestoticks(48));
                motorRF.setTargetPosition(inchestoticks(48));
                motorRB.setTargetPosition(inchestoticks(48));
                telemetry.addData("GOT TO STOP_RESET", 3);
                telemetry.update();

                motorLF.setPower(0.7);
                motorLB.setPower(0.7);
                motorRF.setPower(0.7);
                motorRB.setPower(0.7);

                telemetry.addData("GOT TO STOP_RESET", 4);
                telemetry.update();

                motorLF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motorLB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motorRF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motorRB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                step++;


            default:
                step++;
        }
    }

    @Override
    public void loop() {

        nextStep();

        double maxPower = Math.max(Math.max(Math.abs(lf), Math.abs(lb)),
                Math.max(Math.abs(rf), Math.abs(rb)));
        if (maxPower > 1.0) {
            lf /= maxPower;
            lb /= maxPower;
            rf /= maxPower;
            rb /= maxPower;
        }
        //lf *= Queller;
        //lb *= Queller;
        //rf *= Queller;
        //rb *= Queller;


//        if (forwardval >= 0.3){   //0.16875 inches per encoding tick. OR 5.92 ticks per inch
//            motorLF.setPower(0.7);
//            motorLB.setPower(0.7);
//            motorRF.setPower(0.7);
//            motorRB.setPower(0.7);
//        }else {
//            forwardval = 0.0;
//        }
        //while (opModeIsActive() && motor.isBusy())



        motorLF.setPower(lf);
        motorLB.setPower(lb);
        motorRF.setPower(rf);
        motorRB.setPower(rb);

        telemetry.addData("GOT TO STOP_RESET", step);
        telemetry.update();
    }

    @Override
    public void stop() {
        motorLF.setPower(0);
        motorLB.setPower(0);
        motorRF.setPower(0);
        motorRB.setPower(0);
    }
}
