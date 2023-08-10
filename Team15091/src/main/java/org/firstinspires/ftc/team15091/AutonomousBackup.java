package org.firstinspires.ftc.team15091;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.team15091.examples.HSVPipeline;
@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Backup", group = "Example", preselectTeleOp="Gamepad")
public class AutonomousBackup extends AutonomousBase{
    HSVPipeline pipeline;
    int stored_colour;
    DcMotor armMotor;

    @Override
    public void runOpMode() throws InterruptedException {
        armMotor = hardwareMap.get(DcMotor.class, "arm_motor");
        pipeline = new HSVPipeline();
        pipeline.setupWebcam(hardwareMap);

        telemetry.addData("Analysis", () -> String.format("%d", pipeline.colour));

        setupAndWait();

        armMotor.setTargetPosition(250);
        armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armMotor.setPower(0.5);
        stored_colour = pipeline.colour;
        if (stored_colour == 1) {
            robotDriver.gyroSlide(0.5, 38, 0, 5, null);
            armMotor.setTargetPosition(0);
            robotDriver.gyroDrive(0.5, 75, 0, 5, null);
        }
        else if (stored_colour == 2) {
            robotDriver.gyroSlide(0.5, 38, 0, 5, null);
            robotDriver.gyroDrive(0.5, 75, 0, 5, null);
            armMotor.setTargetPosition(0);
            robotDriver.gyroSlide(0.5, -38, 0, 5, null);
        }
        else {
            robotDriver.gyroSlide(0.5, -38, 0, 5, null);
            armMotor.setTargetPosition(0);
            robotDriver.gyroDrive(0.5, 75, 0, 5, null);
        }

        armMotor.setPower(0d);
    }
}
