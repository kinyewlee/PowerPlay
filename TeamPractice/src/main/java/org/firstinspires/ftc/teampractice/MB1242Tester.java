package org.firstinspires.ftc.teampractice;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "MB1242 test", group = "Example")
public class MB1242Tester extends LinearOpMode {

    private MB1242 ultrasonicSensor;

    @Override
    public void runOpMode() throws InterruptedException {
        ultrasonicSensor = hardwareMap.get(MB1242.class, "ultrasonic_sensor");

        telemetry.addData("Manufacturer ID", () -> ultrasonicSensor.getManufacturerIDRaw());

        // Wait for the game to start (driver presses PLAY)
        while (!(isStarted() || isStopRequested())) {
            telemetry.update();
        }
    }
}
