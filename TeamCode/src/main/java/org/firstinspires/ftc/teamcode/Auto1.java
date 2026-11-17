package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
@Autonomous(name = "Auto Far", group = "Auto")
public class Auto1 extends OpMode {
    private static final double STRAFE_MULTIPLIER = 1.4;
    private static final double TURN_DEADBAND = 0.05;
    private static final double HEADING_KP = 0.012;
    private static final double HEADING_KD = 0.002;
    private static final double HEADING_ERROR_DEADBAND = 1.5;
    private static final double MAX_HEADING_CORRECTION = 0.25;
    private DcMotor motorLF;
    private DcMotor motorLB;
    private DcMotor motorRF;
    private DcMotor motorRB;
    private DcMotor intakeLeft;
    private DcMotor intakeRight;
    private DcMotor launcher;
    private IMU imu;

    private boolean headingHoldActive = false;
    private double targetHeading = 0.0;
    final double Queller = 0.6; //Value shrinker    // TO SET VALUE
    final double minpower = 0.1;                    // TO SET VALUE
    int step = 0;

    double intakestrength = 0.0;

    double forwardval = 0.0;

    @Override
    public void init() {
        motorLF = hardwareMap.get(DcMotor.class, "Motor1");
        //motorLF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorLF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorLB = hardwareMap.get(DcMotor.class, "Motor2");
        motorLF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorRF = hardwareMap.get(DcMotor.class, "Motor3");
        motorLF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorRB = hardwareMap.get(DcMotor.class, "Motor4");
        motorLF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        intakeRight = hardwareMap.get(DcMotor.class, "intake1");
        intakeLeft = hardwareMap.get(DcMotor.class, "intake2");
        launcher = hardwareMap.get(DcMotor.class, "launcher");


        motorLF.setDirection(DcMotor.Direction.FORWARD);
        motorLB.setDirection(DcMotor.Direction.FORWARD);
        motorRF.setDirection(DcMotor.Direction.REVERSE);
        motorRB.setDirection(DcMotor.Direction.FORWARD);

        intakeRight.setDirection(DcMotor.Direction.REVERSE);

        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP)));

        telemetry.addLine("Initialized; verify wheel directions and hub orientation");
        telemetry.addData("IMU heading", "%.1f deg", getHeading());

    }

    @Override
    public void start() {
        targetHeading = getHeading();
        headingHoldActive = true;
    }

    @Override
    public void loop() {
        // FTC gamepad forward is negative on left_stick_y.
        double manualTurn = 0.0;
        double currentHeading = getHeading();

        double turn = manualTurn;
        if (!headingHoldActive) {
            targetHeading = currentHeading;
            headingHoldActive = true;
        }
        double headingError = AngleUnit.normalizeDegrees(targetHeading - currentHeading);
        if (Math.abs(headingError) < HEADING_ERROR_DEADBAND) {
            headingError = 0.0;
        }
        double turnRate = imu.getRobotAngularVelocity(AngleUnit.DEGREES).zRotationRate;
        turn = (headingError * HEADING_KP) - (turnRate * HEADING_KD);
        turn = Math.max(-MAX_HEADING_CORRECTION, Math.min(MAX_HEADING_CORRECTION, turn));


        double lf = turn;
        double lb = turn;
        double rf = -turn;
        double rb = -turn;

        double maxPower = Math.max(Math.max(Math.abs(lf), Math.abs(lb)),
                Math.max(Math.abs(rf), Math.abs(rb)));
        if (maxPower > 1.0) {
            lf /= maxPower;
            lb /= maxPower;
            rf /= maxPower;
            rb /= maxPower;
        }
        lf *= Queller;
        lb *= Queller;
        rf *= Queller;
        rb *= Queller;


        if (forwardval >= 0.3){   //0.16875 inches per encoding tick. OR 5.92 ticks per inch
            motorLF.setPower(0.7);
            motorLB.setPower(0.7);
            motorRF.setPower(0.7);
            motorRB.setPower(0.7);
        }else{
            forwardval = 0.0;
        }
        motorLF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //while (opModeIsActive() && motor.isBusy())

        if((Math.abs(lf)+Math.abs(lf)+Math.abs(rf)+Math.abs(rb))>minpower) {
            motorLF.setPower(lf);
            motorLB.setPower(lb);
            motorRF.setPower(rf);
            motorRB.setPower(rb);
        }else{
            motorLF.setPower(0);
            motorLB.setPower(0);
            motorRF.setPower(0);
            motorRB.setPower(0);
        }

        intakeLeft.setPower(intakestrength);
        intakeRight.setPower(intakestrength);

        launcher.setPower(gamepad1.right_trigger);
    }

    @Override
    public void stop() {
        motorLF.setPower(0);
        motorLB.setPower(0);
        motorRF.setPower(0);
        motorRB.setPower(0);
    }

    private double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }
}
