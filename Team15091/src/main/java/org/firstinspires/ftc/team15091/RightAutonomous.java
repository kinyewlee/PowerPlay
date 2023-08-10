package org.firstinspires.ftc.team15091;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.team15091.examples.HSVPipeline;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "right", group = "Example", preselectTeleOp="Gamepad")
public class RightAutonomous extends AutonomousBase{

    HSVPipeline pipeline;
    DcMotor armMotor;
    Servo grabberServo;
    int stored_colour;
    int highPolePos = 1925, mediumPolePos = 1600, lowPolePos = 850, junctionPos = 200, currentTarget = 0, cone5Pos = 250;
    Thread armUp = new Thread() {
        public void run() {
            armMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            armMotor.setTargetPosition(highPolePos);
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armMotor.setPower(1);
        }
    };
    Thread armDown = new Thread() {
        public void run() {
            armMotor.setTargetPosition(0);
            armMotor.setPower(0.5);
            grabberServo.setPosition(0);
        }
    };
    Thread armToCone5 = new Thread() {
        public void run() {
            armMotor.setTargetPosition(cone5Pos);
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armMotor.setPower(1);
        }
    };
    Thread armToLowPole = new Thread() {
        public void run() {
            armMotor.setTargetPosition(lowPolePos);
            armMotor.setPower(1);
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
        telemetry.addData("Angle", () -> String.format("%.1f", robot.getHeading()));

        // Wait for the game to start (driver presses PLAY)
        // Abort this loop is started or stopped.
        setupAndWait();
        stored_colour = pipeline.colour;
        runtime.reset();
        armToCone5.start();
        robotDriver.gyroSlide(0.5, 38, 0, 5, null);
        robotDriver.gyroDrive(0.5, 75, 0, 5, null);
        armUp.start();
        sleep(500);
        //robotDriver.gyroSlide(0.8, -37, 0, 5, null);
        robotDriver.gyroTurn(0.5, -44, 3);
        robotDriver.gyroDrive(0.5, 13 , -44, 5, null);
        sleep(100);
        armMotor.setPower(0.5);
        armMotor.setTargetPosition(1800);
        sleep(200);
        grabberServo.setPosition(0d);
        sleep(200);

        armToCone5.start();
        robotDriver.gyroDrive(0.5, -11, -44, 5, null);
        robotDriver.gyroTurn(0.5, -90, 3);
        robotDriver.gyroDrive(0.5, 74, -90, 5, null);
        grabberServo.setPosition(1d);
        sleep(500);

        armToLowPole.start();
        sleep(100);
        robotDriver.gyroDrive(0.5, -7, -90, 5, null);
        robotDriver.gyroTurn(0.5, -220, 4);
        robotDriver.gyroDrive(0.5, 11, -220, 5, null);
        armDown.start();
        sleep(300);
        grabberServo.setPosition(0d);
        sleep(500);
        robotDriver.gyroDrive(0.5, -10, -225, 5, null);
        robotDriver.gyroTurn(0.5, 90, 3);
        if (stored_colour == 1) {
            robotDriver.gyroDrive(0.5, 63, 90, 5, null);
        }
        else if (stored_colour == 2) {
            robotDriver.gyroDrive(0.5, 30, 90, 5, null);
        }
    }
}
