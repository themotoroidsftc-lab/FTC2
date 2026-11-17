// VERY IMPORTANT FOR NEW CODERS DON'T DELETE

//This is a package that includes basic controls such as gamepads and also includes basic ...
// ...classes needed to handle things such as driver station - control hub connections
package org.firstinspires.ftc.teamcode;

//this is the imports for code needed to create the opMode and make it compile
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

//these are the imports for the hardware devices the code uses (a DcMotor, a servo, and a touch sensor)
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

//creates a class in the Teleop section (@Autonomous would be in the auto dropdown, none would not show up)
@TeleOp(name = "Motor Test", group = "TestBed")

//This is disabled to stop it from appearing on the Driver Station
@Disabled
public class MotorTest extends OpMode {

    //Declares a persistent variable that keeps track of whether we can use controller
    boolean active = false;

    //Declares a persistent variable that keeps track of whether the touch sensor was pressed last loop
    boolean CurPres = false;

    //Declares a persistent variable that keeps track of whether gamepad1's a was pressed last loop
    boolean aPres = false;

    //Declares our 3 devices
    DcMotor motor = null;
    Servo servo = null;
    TouchSensor Tsense = null;

    //the @Override 'overrides' the preexisting initialization statement inside of "import com.qualcomm.robotcore.eventloop.opmode.OpMode;", which is empty
    @Override
    public void init(){
        //instantiates the objects (a motor with name 'Motor', servo with name 'Servo', and a touch sensor with name 'ts')
        motor = hardwareMap.get(DcMotor.class, "Motor");
        servo = hardwareMap.get(Servo.class, "Servo");
        Tsense = hardwareMap.get(TouchSensor.class, "ts");

    }

    //as with the init method, this does the same thing, just for a continuous loop of code, MUST BE CALLED 'loop()' for all codes
    @Override
    public void loop(){

        //if statement that checks if Tsense (our touch sensor) is pressed, changing the telemetry
        if(Tsense.isPressed()){

            //displays true, for being pressed
            telemetry.addData("Touch Sensor Pressed","TRUE");
        }else{

            //displays false, for not being pressed
            telemetry.addData("Touch Sensor Pressed","FALSE");
        }

        //if statement that checks two conditions, a: gamepad1's a is pressed. or b: touch sensor is pressed
        if((gamepad1.a && aPres) || (Tsense.isPressed() && !CurPres)){

            //inverts active whenever a is pressed or the touch sensor is pressed and not previously pressed (acting like a button)
            active = !active;
            telemetry.addLine("TOGGLED ACTIVE!!!");
        }

        //SIM statement that shortens a long if-statement
        CurPres = Tsense.isPressed();
        aPres = gamepad1.a;
        //works if active is true
        if(active){

            //sets the motor power to the left stick y, if the toggle is active
            motor.setPower(gamepad1.left_stick_y);

            //sets servo pos, but only if the toggle is active (0.5 pushes us to null))
            servo.setPosition(gamepad1.left_stick_x + 0.5);
        }else{

            //sets the motor power to 0.0, if the toggle is not active
            motor.setPower(0.0);

            //0.5 pushes us to null, so always null
            servo.setPosition(0.5);
        }
        //adds telemetry
        telemetry.addData("Active", active);
        telemetry.addData("Pressed (ts)",CurPres);
        telemetry.addData("Pressed (a)",gamepad1.a);
        //telemetry.addData("",);


        // because we use 'telemetry.addData()', this updates the data on the screen, whatever we wrote to it.
        telemetry.update();
    }
}
