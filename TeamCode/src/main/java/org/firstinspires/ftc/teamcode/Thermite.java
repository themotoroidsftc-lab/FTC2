package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
@TeleOp(name = "Thermite", group = "Drive")
@Disabled //ONLY DISABLED AS motor{launcher} doesn't exist yet
public class Thermite extends OpMode {
    private static final double STRAFE_MULTIPLIER = 1.4;
    private static final double TURN_DEADBAND = 0.05;
    // Keep heading hold gentle so it does not fight translation or chase IMU noise.
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
    double Queller = 0.8; //Value shrinker
    double minpower = 0.5;

    @Override
    public void init() {
        motorLF = hardwareMap.get(DcMotor.class, "Motor1");
        motorLB = hardwareMap.get(DcMotor.class, "Motor2");
        motorRF = hardwareMap.get(DcMotor.class, "Motor3");
        motorRB = hardwareMap.get(DcMotor.class, "Motor4");
        intakeRight = hardwareMap.get(DcMotor.class, "intake1");
        intakeLeft = hardwareMap.get(DcMotor.class, "intake2");
        launcher = hardwareMap.get(DcMotor.class, "launcher");

        // Verify these directions with the wheels off the ground before use.
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
        double forward = gamepad1.right_stick_y;
        double strafe = -(gamepad1.right_stick_x * STRAFE_MULTIPLIER);
        double manualTurn = -(gamepad1.left_stick_x);
        double currentHeading = getHeading();

        double turn = manualTurn;
        if (Math.abs(manualTurn) > TURN_DEADBAND) {
            // Manual turn takes over immediately; capture the new heading when released.
            targetHeading = currentHeading;
            headingHoldActive = false;
        } else {
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
            turn = Math.max(-MAX_HEADING_CORRECTION,
                    Math.min(MAX_HEADING_CORRECTION, turn));
        }

        if(gamepad2.a){
            Queller -= 0.01;
        }
        if(gamepad2.b){
            Queller += 0.01;
        }
        if(gamepad2.y){
            minpower += 0.01;
        }
        if(gamepad2.x){
            minpower -= 0.01;
        }
        // Wheel equations follow the mapping documented in notes.md.
        double lf = forward + strafe + turn;
        double lb = forward - strafe + turn;
        double rf = forward - strafe - turn;
        double rb = forward + strafe - turn;

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

        double intakestrength = 0.0;
        boolean intakebool = gamepad1.dpad_down;
        if(intakebool){
            intakestrength = 1.0;
        }else{
            intakestrength = 0.0;
        }
        intakebool = gamepad1.dpad_up;
        if(intakebool){
            intakestrength = -1;
        }
        intakeLeft.setPower(intakestrength);
        intakeRight.setPower(intakestrength);

        launcher.setPower(gamepad1.right_trigger);

        telemetry.addData("Heading", "%.1f deg", currentHeading);
        telemetry.addData("Target", "%.1f deg", targetHeading);
        telemetry.addData("Queller: ", Queller);
        telemetry.addData("Minimum Power: ", minpower);
        telemetry.update();
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
