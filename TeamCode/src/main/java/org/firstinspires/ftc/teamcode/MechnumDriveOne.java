//This is a package that includes basic controls such as gamepads and also includes basic ...
// ...classes needed to handle things such as driver station - control hub connections
package org.firstinspires.ftc.teamcode;

//this is the imports for code needed to create the opMode and make it compile
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

//these are the imports for the hardware devices the code uses (a DcMotor, a servo, and a touch sensor)
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

//creates a class in the Teleop section (@Autonomous would be in the auto dropdown, none would not show up)
@TeleOp(name = "Mechnum Drive One", group = "Drive")
@Disabled // too deprecated and unsupported to run, missing values
public class MechnumDriveOne extends OpMode {

    //Declares our 4 motor devices ~ LF is left front, RF is right front, LB is left back, RB is right back
    //front is near intake, Left is Portside (if you're on the robot facing in the direction of travel
    // it's your left), while Right is Starboard, (if your in the center of the robot facing the intake, it
    // 's your right)
    DcMotor motorLF = null; // 10 o'clock (facing down on the robot with intake being 12 o'clock)
    DcMotor motorLB = null; // 7 o'clock

    DcMotor motorRF = null; // 2 o'clock
    DcMotor motorRB = null; // 5 o'clock

    Servo intake1 = null;
    Servo intake2 = null;


    // they're intitalized here as to prevent us from having to recreate each tick, filling up the garbage.
    double x = 0.0, y = 0.0, z = 0.0; // x is forward motion, y is left-right motion, z is rotation in clockwise
    double Ilf = 0.0, Ilb = 0.0, Irf = 0.0, Irb = 0.0; // this is the value we set each motor to.
    double intake = 0.0;
    boolean ab = false, bb = false, xb = false, yb = false;


    //the @Override 'overrides' the preexisting initialization statement inside of "import com.qualcomm.robotcore.eventloop.opmode.OpMode;", which is empty
    @Override
    public void init(){
        //instantiates the objects (a motor with name 'Motor', servo with name 'Servo', and a touch sensor with name 'ts')
        motorLF = hardwareMap.get(DcMotor.class, "Motor1");
        motorLB = hardwareMap.get(DcMotor.class, "Motor2");
        motorRF = hardwareMap.get(DcMotor.class, "Motor3");
        motorRB = hardwareMap.get(DcMotor.class, "Motor4");
        intake1 = hardwareMap.get(Servo.class, "servo1");
        intake2 = hardwareMap.get(Servo.class, "servo2");

    }

    //as with the init method, this does the same thing, just for a continuous loop of code, MUST BE CALLED 'loop()' for all codes
    @Override
    public void loop(){
        telemetry.addData("Is Running: ","TRUE");
        telemetry.addData("LeftForwards dir: ", motorLF.getDirection().toString());
        telemetry.addData("LeftBackwards dir: ", motorLB.getDirection().toString());
        telemetry.addData("RightForwards dir: ", motorRF.getDirection().toString());
        telemetry.addData("RightBackwards dir: ", motorRB.getDirection().toString());
        telemetry.addData("i1: ", intake1.getPosition());
        telemetry.addData("i2: ", intake2.getPosition());
        telemetry.addData("i intake: ", intake);

        // get values
        z = gamepad1.right_stick_x; // Right: <>
        x = gamepad1.left_stick_y; // Left: ^V
        y = gamepad1.left_stick_x; // Left: <>
        intake += gamepad1.left_trigger - gamepad1.right_trigger;;

        //x -- add the forward velocity to all 4 motors
        Ilf = x;
        Ilb = x;
        Irf = x;
        Irb = x;

        //y -- add the lateral force to RF and LB and subtract it from RB and LF
        Ilf -= y;
        Ilb += y;
        Irf += y;
        Irb -= y;

        //z -- add the rotational force to the left side and subtract from right side to get a yawing force
        Ilf += z;
        Ilb += z;
        Irf -= z;
        Irb -= z;

        // finally set the motors to our calculated forces.
        motorLF.setPower(Ilf);
        motorLB.setPower(Ilb);
        motorRF.setPower(Irf);
        motorRB.setPower(Irb);

        //intake
        intake1.setPosition(intake);
        intake2.setPosition(intake);


        // motor reversing logic for testing.
        if(gamepad1.a && !ab){
            DcMotor mot = motorRB;
            ab = true;
            if(mot.getDirection() == DcMotorSimple.Direction.REVERSE) {
                mot.setDirection(DcMotorSimple.Direction.FORWARD);
            }else{
                mot.setDirection(DcMotorSimple.Direction.REVERSE);
            }
        }
        if(!gamepad1.a){
            ab = false;
        }


        if(gamepad1.b && !bb){
            DcMotor mot = motorRF;
            bb = true;
            if(mot.getDirection() == DcMotorSimple.Direction.REVERSE) {
                mot.setDirection(DcMotorSimple.Direction.FORWARD);
            }else{
                mot.setDirection(DcMotorSimple.Direction.REVERSE);
            }
        }
        if(!gamepad1.b){
            bb = false;
        }

        if(gamepad1.x && !xb){
            DcMotor mot = motorLB;
            xb = true;
            if(mot.getDirection() == DcMotorSimple.Direction.REVERSE) {
                mot.setDirection(DcMotorSimple.Direction.FORWARD);
            }else{
                mot.setDirection(DcMotorSimple.Direction.REVERSE);
            }
        }
        if(!gamepad1.x){
            xb = false;
        }

        if(gamepad1.y && !yb){
            DcMotor mot = motorLF;
            yb = true;
            if(mot.getDirection() == DcMotorSimple.Direction.REVERSE) {
                mot.setDirection(DcMotorSimple.Direction.FORWARD);
            }else{
                mot.setDirection(DcMotorSimple.Direction.REVERSE);
            }
        }
        if(!gamepad1.y){
            yb = false;
        }

        // because we use 'telemetry.addData()', this updates the data on the screen, whatever we wrote to it.
        telemetry.update();
    }
}
