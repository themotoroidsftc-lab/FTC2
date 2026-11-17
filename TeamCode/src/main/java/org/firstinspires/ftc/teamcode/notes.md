

# TeleOp Drive Optimization & IMU Stabilization

## Overview

Targeted enhancements for FTC Mecanum drive TeleOp control: closed-loop IMU heading stabilization, optimized strafe vectoring, turn locking via joystick deadbands, PD angle correction, and wheel power normalization.

---

## Requirements & Implementation Specifications

### 1. IMU Heading Stabilization

* **Goal:** Eliminate chassis drift during straight drives and lateral strafes caused by Mecanum wheel slippage or uneven weight distribution.
* **Strategy:** Use closed-loop feedback from the REV Hub IMU yaw angle to continuously correct unintentional heading deviations.

---

### 2. Enhanced Strafe Power Multiplier

* **Goal:** Compensate for higher mechanical friction and roller slippage when strafing sideways (`< >`) relative to driving forward/backward.
* **Implementation:** Scale the strafe input vector (`x`) by a factor of `1.1` to `1.2`:
```java
double x = gamepad1.left-stick-x * 1.1; // Counteract strafe slippage

```



---

### 3. Dynamic Turn Locking & Driver Override

* **Goal:** Lock and maintain the current angle whenever the driver is not actively turning, while granting instant manual control when turning is commanded.
* **Deadband Threshold:** $\vert{}\text{right\-stick\-x}\vert{} > 0.05$
* **State Machine:**
* **Driver Turning (`|right-stick-x| > 0.05`):** Disengage heading lock (`headingHoldActive = false`) and set `targetHeading = currentHeading`.
* **Stick Released (`|right-stick-x| <= 0.05`):** Lock the current angle (`targetHeading = currentHeading`) once and engage heading lock (`headingHoldActive = true`).



---

### 4. PD Angle Hold Control Loop

* **Angle Normalization:** Use `AngleUnit.normalizeDegrees(...)` to restrict heading error to $[-180^\circ, +180^\circ]$, preventing radical turns when crossing the $+180^\circ / -180^\circ$ boundary.
* **Derivative Damping:** Use the $Z$-axis angular velocity directly from `imu.getRobotAngularVelocity()` instead of computing discrete delta time derivatives manually.
* **Control Math:**
```java
if (headingHoldActive) {
    // Calculate normalized heading error [-180 to 180]
    double headingError = AngleUnit.normalizeDegrees(targetHeading - currentHeading);

    // Get rotation rate (deg/s) directly from gyro Z-axis for derivative damping
    double turnRate = imu.getRobotAngularVelocity(AngleUnit.DEGREES).zRotationRate;

    // Calculate PD output
    turnPower = (headingError * kP) - (turnRate * kD);
}

```



---

### 5. Wheel Power Normalization

* **Goal:** Prevent wheel speed saturation ($> 1.0$) from distorting strafe, forward, and turn proportions when combining vector powers.
* **Implementation:** Find the maximum magnitude among all four motors and divide each motor power by this maximum if it exceeds `1.0`.
```java
// Determine peak power across all four wheels
double maxPower = Math.max(Math.abs(frontLeft), 
                  Math.max(Math.abs(backLeft), 
                  Math.max(Math.abs(frontRight), Math.abs(backRight))));

// Scale down powers proportionally if saturation occurs
if (maxPower > 1.0) {
    frontLeft  /= maxPower;
    backLeft   /= maxPower;
    frontRight /= maxPower;
    backRight  /= maxPower;
}

```



---

## Complete Java TeleOp Drive Integration Reference

```java
// --- TeleOp Loop Execution ---

double y  =  gamepad1.left_stick_y;       // Forward / Backward
double x  =  gamepad1.left_stick_x * 1.1; // Strafe input with multiplier
double z =  gamepad1.right_stick_x;      // Manual turn input

double currentHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);

// 1. Turn Locking Deadband Logic
if (Math.abs(z) > 0.05) {
    targetHeading = currentHeading;
    headingHoldActive = false;
} else {
    if (!headingHoldActive) {
        targetHeading = currentHeading;
        headingHoldActive = true;
    }
}

double turnPower = z;

// 2. Closed-Loop Heading Hold Logic
if (headingHoldActive) {
    double headingError = AngleUnit.normalizeDegrees(targetHeading - currentHeading);
    double turnRate = imu.getRobotAngularVelocity(AngleUnit.DEGREES).zRotationRate;
    
    turnPower = (headingError * kP) - (turnRate * kD);
}

// 3. Mecanum Kinematics Power Mixing
double frontLeftPower  = y + x + turnPower;
double backLeftPower   = y - x + turnPower;
double frontRightPower = y - x - turnPower;
double backRightPower  = y + x - turnPower;

// 4. Max Power Normalization
double maxPower = Math.max(Math.abs(frontLeftPower), 
                  Math.max(Math.abs(backLeftPower), 
                  Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))));

if (maxPower > 1.0) {
    frontLeftPower  /= maxPower;
    backLeftPower   /= maxPower;
    frontRightPower /= maxPower;
    backRightPower  /= maxPower;
}

// 5. Output Powers to Motors
frontLeft.setPower(frontLeftPower);
backLeft.setPower(backLeftPower);
frontRight.setPower(frontRightPower);
backRight.setPower(backRightPower);

```
