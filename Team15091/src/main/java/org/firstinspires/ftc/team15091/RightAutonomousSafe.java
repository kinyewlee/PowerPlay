package org.firstinspires.ftc.team15091;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.team15091.examples.HSVPipeline;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Right Backup", group = "Example", preselectTeleOp="Gamepad")
public class RightAutonomousSafe extends AutonomousBase{

    HSVPipeline pipeline;
    volatile DcMotor armMotor;
    volatile Servo grabberServo;
    int stored_colour;
    Thread armUp = new Thread() {
        public void run() {
            armMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            armMotor.setTargetPosition(mediumPolePos);
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armMotor.setPower(1);
        }
    };
    Thread armDown = new Thread() {
        public void run() {
            if (opModeIsActive()) {
                armMotor.setTargetPosition(0);
                armMotor.setPower(1d);
//                grabberServo.setPosition(0);
            }
        }
    };
    Thread armToCone5 = new Thread() {
        public void run() {
            if (opModeIsActive()) {
                armMotor.setTargetPosition(cone5Pos);
                armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                armMotor.setPower(1d);
                cone5Pos -= 45;
            }
        }
    };
    Thread armToLowPole = new Thread() {
        public void run() {
            if (opModeIsActive()) {
                armMotor.setTargetPosition(lowPolePos);
                armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                armMotor.setPower(1);
            }
        }
    };
    ElapsedTime runtime = new ElapsedTime();
    @Override
    public void runOpMode() throws InterruptedException {
        armMotor = hardwareMap.get(DcMotor.class, "arm_motor");
        grabberServo = hardwareMap.get(Servo.class, "grabber_servo");
        pipeline = new HSVPipeline();
        pipeline.setupWebcam(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Analysis", () -> String.format("%d", pipeline.colour));
        telemetry.addData("Run Time", () -> runtime.toString());

        // Wait for the game to start (driver presses PLAY)
        // Abort this loop is started or stopped.
        setupAndWait();
        stored_colour = pipeline.colour;

        // save some memory from openCV processing
        pipeline.disposeWebcam();

        telemetry.update();
        runtime.reset();
        armToLowPole.start();

        robotDriver.gyroSlide(0.6d, -38d, 0, 4d, null);
        rightDetector.reset();
        robotDriver.gyroSlide(0.2d, -7d, 0, 3d, rightDetector);

        robotDriver.gyroDrive(0.6d, 40d, 0, 3d, null);

        rearDetector.reset();
        rearDetector.setThreshold(73.5d);
        robotDriver.gyroDrive(0.3d, 2d, 0, 2d, rearDetector);
        telemetry.update();

        robotDriver.gyroTurn(0.65d, 45d, 2d);
        robotDriver.gyroDrive(0.6d, 13.5d, 45d, 3d, null);

        // Drop first cone
        armMotor.setTargetPosition(lowPolePos - 260);
        sleep(200);
        robot.setGrabber(0.2d);
        sleep(250);

        robotDriver.gyroDrive(0.7d, -7d, 45d, 2d, null);
        armToLowPole.start();

        robotDriver.gyroTurn(0.6d, 0d, 2d);
        robotDriver.gyroDrive(0.7d, 26d, 0, 3d, null);
        sleep(50);

        robotDriver.gyroDrive(0.05d, 10d, 0, 3d, tapeDetector);

        for(int i = 0; i < 2; i++) {
            robotDriver.gyroTurn(0.6d, -90, 2);
            robot.setFrontServo(true);

            armToCone5.start();
            if (i == 0) {
                sleep(300);
            }

            robotDriver.gyroDrive(0.8d, 4d, -90, 2, null);

            robotDriver.gyroDrive(0.1d, 8.5d, -90, 2d, frontDetector);
            robot.setFrontServo(false);
            telemetry.update();

            // Pick up cone
            robot.setGrabber(1);
            sleep(400);
            armToLowPole.start();
            sleep(100);

            robotDriver.gyroDrive(0.6d, -8.7d, -90d, 3d, null);

            // Turn to pole
            robotDriver.gyroTurn(0.6d, 148d, 2d);
            robotDriver.gyroDrive(0.6d, 9.7d, 148d, 3d, null);
            armMotor.setTargetPosition(lowPolePos - 260);

            // Drop to low pole
            sleep(150);
            robot.setGrabber(0.2d);
            sleep(250);

            robotDriver.gyroDrive(0.6d, -8.5d, 148d, 3d, tapeDetector);
        }

        armDown.start();

        if (stored_colour == 0) {
            robotDriver.gyroTurn(0.6d, 180, 2d);
            robotDriver.gyroSlide(0.6d, 3d, 0, 2d, null);
        }
        else if (stored_colour == 2) {
            robotDriver.gyroTurn(1d, -90, 1d);
            robotDriver.gyroDrive(1d, -30, -90, 4d, null);
        }
        else if (stored_colour == 1) {
            robotDriver.gyroTurn(1d, -90, 1d);
            robotDriver.gyroDrive(1d, -64, -90, 4d, null);
        }

        armMotor.setPower(0d);
    }
}
