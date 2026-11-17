package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "Advanced Auto", group = "Drive")
public class AdvancedMovementSTRAIGHT extends OpMode {

    //motor(OID)s
    DcMotorEx motorLF;
    DcMotorEx motorLB;
    DcMotorEx motorRF;
    DcMotorEx motorRB;

    //just some housekeeping instants
    double currentTicks_lf;
    double currentTicks_rf;
    double currentTicks_lb;
    double currentTicks_rb;
    double averageTicks;
    boolean polling = false;
    double distanceTargetTicks = 0.0;
    final int r = 1; //max speed lock
    double velocity = 0.0; //actual general velocity
    double velocitylf = 0.0; //tuned velocity for the Upper Left motor
    double velocityrf = 0.0; //tuned velocity for the Upper Right motor
    double velocityrb = 0.0; //tuned velocity for the Bottom Right motor
    double velocitylb = 0.0; //tuned velocity for the Bottom Left motor
    double progress = 0.0; // % complete
    final double PI = Math.PI; // what can I say? it's Pie 3.141492
    double accelerationCoefficient; //calculated AT runtime

    //these 3 values are named after desmos Colors
    // Blue is a constant, red is slowdown, green is speedup
    double red;
    double blue = 0.7; // Represents MAX POWER, 1 is the total max.
    double green;

    // these are used in the Orientation Keeping System or OKS
    private static final double HEADING_KP = 0.012;
    private static final double HEADING_KD = 0.002;
    private static final double HEADING_ERROR_DEADBAND = 0.0;
    private static final double MAX_HEADING_CORRECTION = 0.25;
    double currentHeading = 0.0;
    private IMU imu;
    double turn = 0.0;



    //===CONFIG=VALUES===//
        //Wheels
    double distanceTargetInches = 0.0;
    double wheelDia = 2.76; // in inches, 70mm rev mecnum wheels -> 2.76 inches
    double gearRatio = -4.0;
    int ticksPerRev = 28;


        //Acceleration
    double rampUpInches = 5;
    double accelerationConstant =0.0;

        //Turning
    private double targetHeading = 0.0;

    //===END=CONFIG=VALUES===//
    @Override
    public void init() {
        motorLF = hardwareMap.get(DcMotorEx.class, "Motor1");
        motorLB = hardwareMap.get(DcMotorEx.class, "Motor2");
        motorRF = hardwareMap.get(DcMotorEx.class, "Motor3");
        motorRB = hardwareMap.get(DcMotorEx.class, "Motor4");
        motorLF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorLB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRF.setDirection(DcMotor.Direction.REVERSE);
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP)));

        telemetry.addLine("Motors And I.M.U.  Successfully Initialized");
        telemetry.addData("IMU heading", "%.1f deg", getHeading());
        telemetry.update();
    }

    public void create(){
        motorLF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorLB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRF.setDirection(DcMotor.Direction.REVERSE);
        distanceTargetTicks = ((distanceTargetInches / (wheelDia * PI)) * gearRatio) * ticksPerRev;
        accelerationCoefficient = (wheelDia * PI)/(rampUpInches * gearRatio * (ticksPerRev));
        targetHeading = getHeading();
    }

    @Override
    public void loop() {
        if (polling) {

            // generate values used per frame
            currentTicks_lf = motorLF.getCurrentPosition();
            currentTicks_rf = motorRF.getCurrentPosition();
            currentTicks_lb = motorLB.getCurrentPosition();
            currentTicks_rb = motorRB.getCurrentPosition();
            averageTicks = ((currentTicks_lf + (1)*currentTicks_rf + currentTicks_lb + (1)*currentTicks_rb) / 4);
            progress = (averageTicks / distanceTargetTicks); // out of 1.0


            //Calculate general Velocity
            green = (averageTicks * accelerationCoefficient) + accelerationConstant;
            red = (accelerationCoefficient * (distanceTargetTicks - averageTicks))+(accelerationConstant * 0.5);
            // blue = 1 {it's a constant}

            velocity = Math.min(green, Math.min(red, Math.min(r, blue))); // Get the lowest value



            //use IMU to get turning Delta, and error correct
            currentHeading = getHeading();
            turn = 0.0;

            double headingError = AngleUnit.normalizeDegrees(targetHeading - currentHeading);
            if (Math.abs(headingError) < HEADING_ERROR_DEADBAND) {
                headingError = 0.0;
            }
            double turnRate = imu.getRobotAngularVelocity(AngleUnit.DEGREES).zRotationRate;
            turn = (headingError * HEADING_KP) - (turnRate * HEADING_KD);
            turn = Math.max(-MAX_HEADING_CORRECTION, Math.min(MAX_HEADING_CORRECTION, turn));




            // personalize each motor with its turn value, and set the motors to it
            velocitylf = velocity;// + turn;
            velocitylb = velocity;// + turn;
            velocityrf = velocity;// - turn;
            velocityrb = velocity;// - turn;
            motorLF.setPower(velocitylf);
            motorLB.setPower(velocitylb);
            motorRF.setPower(velocityrf);
            motorRB.setPower(velocityrb);



            // Update Telemetry for Debugging
            telemetry.addData("Current Progress: ", progress);
            telemetry.addData("Current Distance on average: ", averageTicks);
            telemetry.addData("     LF: ", currentTicks_lf);
            telemetry.addData("     LB: ", currentTicks_lb);
            telemetry.addData("     RF: ", currentTicks_rf);
            telemetry.addData("     RB: ", currentTicks_rb);
            telemetry.addLine("\n...");
            telemetry.addData("\nTarget Distance: ", distanceTargetInches);
            telemetry.addData("\nTarget Distance: ", distanceTargetTicks);
            telemetry.addData("Command Velocity", velocity);
            telemetry.update();

            if(progress > 0.99 && progress < 1.01 || gamepad1.a){
                polling = false;
                motorLF.setPower(0);
                motorLB.setPower(0);
                motorRF.setPower(0);
                motorRB.setPower(0);
            }

        }else{
            if(gamepad1.b){
                distanceTargetInches+=0.1; //ADD 0.1 inches in target distance
            }
            if(gamepad1.a){
                distanceTargetInches-=0.1; //minus 0.1 inches in target distance
            }
            if(gamepad1.x){
                polling = true; //set distance in stone
                create();
            }
            telemetry.addLine("YOU ARE IN DISTANCE SET MODE!!! \nUSE {B} to increase target Distance\nUSE {A} to subtract\nPress {Y} to start the command.\n...");
            telemetry.addData("Target Distance", distanceTargetInches);
            if(Math.abs(distanceTargetTicks) > 0.6){
                telemetry.addLine("\nYOU previously ran an auto command for " + distanceTargetInches + " inches.");
                telemetry.addLine("It exited at a predicted " + progress*100 + "% of the way there, if this was wrong, please tune values.");
                telemetry.addLine("Travelled a predicted " + averageTicks + " ticks of " + distanceTargetTicks + " total.");
                telemetry.addData("Current Distance on average: ", averageTicks);
                telemetry.addData("     LF: ", currentTicks_lf);
                telemetry.addData("     LB: ", currentTicks_lb);
                telemetry.addData("     RF: ", currentTicks_rf);
                telemetry.addData("     RB: ", currentTicks_rb);
                telemetry.addLine("\n...");
                telemetry.addData("\nTarget Distance: ", distanceTargetInches);
                telemetry.addData("Command Velocity", velocity);
                telemetry.update();
            }
            telemetry.update();
        }
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