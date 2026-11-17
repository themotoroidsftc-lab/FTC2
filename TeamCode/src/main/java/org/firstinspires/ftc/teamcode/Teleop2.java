
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
//Go Motoroids!!!!!!

@Autonomous
@Disabled
//@Disabled
public class Teleop2 extends OpMode {

    //sets up motors, and elapsed time
    private final ElapsedTime runtime = new ElapsedTime();

    private DcMotor leftForwards = null;

    public void init(){

    }

    //motor method that declares motor
    public void Motors(){

        leftForwards = hardwareMap.get(DcMotor.class, "Left Forwards");

    }

    //this is the main code, that runs once
    @Override
    public void loop(){
        Motors();
        runtime.reset();
        /*
        //While opmode is active, this code will loop. running infenitely
        while(opModeIsActive()) {

            //I hope this works
            frontLeftDrive.setPower(1);
            leftForwards.setPower(1);

        }
        */
    }
}