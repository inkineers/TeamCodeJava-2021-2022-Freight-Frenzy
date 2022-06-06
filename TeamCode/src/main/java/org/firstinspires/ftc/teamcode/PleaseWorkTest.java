/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

/**
 * This 2020-2021 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine the position of the Freight Frenzy game elements.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@Autonomous(name = "Pleaase Work Test", group = "Concept")
//@Disabled
public class PleaseWorkTest extends LinearOpMode {
  /* Note: This sample uses the all-objects Tensor Flow model (FreightFrenzy_BCDM.tflite), which contains
   * the following 4 detectable objects
   *  0: Ball,
   *  1: Cube,
   *  2: Duck,
   *  3: Marker (duck location tape marker)
   *
   *  Two additional model assets are available which only contain a subset of the objects:
   *  FreightFrenzy_BC.tflite  0: Ball,  1: Cube
   *  FreightFrenzy_DM.tflite  0: Duck,  1: Marker
   */
    private DcMotor front_left = null;
    private DcMotor front_right = null;
    private DcMotor back_left = null;
    private DcMotor back_right = null;
    private DcMotor duck_spam = null;
    private DcMotor arm = null;
    private Servo left_arm_grasp = null;
    private Servo right_arm_grasp = null;
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
      "Ball",
      "Cube",
      "Duck",
      "Marker"
    };

    private void level1() {
        arm.setPower(1);
        sleep(10);
        arm.setPower(0);
    }
    private void level2() {

    }
    private void level3() {
        arm.setPower(-1);
        sleep(400);
        arm.setPower(0);
    }
    private static final String VUFORIA_KEY =
            "AYP9k3r/////AAABmbsKp4S4+0RSpbJyMlJGbNQJWbthdpl1gIp8CO+DnDwIDkzifNXPuUMawrPbYmKwDfWtSi+PAKLOcvbHmHZxsTM24Sd32QsBy/RarvDqfIJgEIVDiUXpTlOvKCqFNCS5FGivU6Tz3C5FIhf5N/KapHhETsd2ExGtCtsZSE7QQw5SCjynKE+JvP/DnjZ8eBk6PYlS/TUdvQmonUSTkPwPCEXcL3HVO9Mw+QjvYT0eA93l7yn2NssK+37MjpJBn7kzME8FUmurwynqPJA5Ido5l/iafDl53Hndd+vl0H5ooXY0qVE1mc8HUK5lYoVXMygBDqa9Grkghg8bD791U09C20SnuKdwFCWH0Ic6zZUkeH9o";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();
        ElapsedTime myTimer;

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(1.5, 16.0/9.0);
        }

        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        front_left = hardwareMap.get(DcMotor.class, "front_left");
        back_left = hardwareMap.get(DcMotor.class, "back_left");
        front_right = hardwareMap.get(DcMotor.class, "front_right");
        back_right = hardwareMap.get(DcMotor.class, "back_right");
        duck_spam = hardwareMap.get(DcMotor.class, "duck_spam");
        arm = hardwareMap.get(DcMotor.class, "arm");
        left_arm_grasp = hardwareMap.get(Servo.class, "left_arm_grasp");
        right_arm_grasp = hardwareMap.get(Servo.class, "right_arm_grasp");
        myTimer = new ElapsedTime();

        back_left.setDirection(DcMotor.Direction.REVERSE);

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            int level = 3;
            double left = 0;
            closeClaw();
            sleep(1000);
            arm.setPower(-1);
            sleep(600);
            arm.setPower(0);
            sleep(1000);
            myTimer.reset();
            while (myTimer.time() < 8 && opModeIsActive()) {
                if (tfod != null) {
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        // step through the list of recognitions and display boundary info.
                        int i = 0;
                        for (Recognition deez : updatedRecognitions) {
                            telemetry.addData(String.format("label (%d)", i), deez.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    deez.getLeft(), deez.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                    deez.getRight(), deez.getBottom());
                            i++;
                            if (deez.getLabel().equals("Duck")) {
                                left = deez.getLeft();
                                if (left > 300) {
                                    level = 2;
                                } else if (left <= 300) {
                                    level = 1;
                                }
                            }
                        }
                        telemetry.addData("Run time", myTimer.time());
                        telemetry.addData("Level", level);
                        telemetry.update();
                    }
                }
            }
            tfod.deactivate();
            telemetry.addData("Level", level);
            telemetry.update();
            telemetry.addData("Programming Pain", "Why you do this to me java");
            sleep(2000);
            if (level == 1) {
                level1();
                sleep(5000);
            } else if (level == 2) {
                level2();
                sleep(5000);
            } else if (level == 3) {
                level3();
                sleep(5000);
            }
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
       tfodParameters.minResultConfidence = 0.8f;
       tfodParameters.isModelTensorFlow2 = true;
       tfodParameters.inputSize = 320;
       tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
       tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
    private void stop2() {
        back_left.setPower(0);
        front_left.setPower(0);
        back_right.setPower(0);
        front_right.setPower(0);
    }
    private void goBackward() {
        front_right.setPower(-0.2);
        back_right.setPower(-0.2);
        front_left.setPower(-0.2);
        back_left.setPower(-0.2);
    }
    private void goForward() {
        front_right.setPower(0.3);
        back_right.setPower(0.3);
        front_left.setPower(0.3);
        back_left.setPower(0.3);
    }
    private void strafeRight() {
        front_right.setPower(-0.5);
        back_right.setPower(0.5);
        front_left.setPower(0.5);
        back_left.setPower(-0.5);
    }
    private void strafeLeft() {
        front_right.setPower(0.5);
        back_right.setPower(-0.5);
        front_left.setPower(-0.5);
        back_left.setPower(0.5);
    }
    private void closeClaw() {
        right_arm_grasp.setPosition(0.15);
        left_arm_grasp.setPosition(0.55);
    }
    private void openClaw() {
        right_arm_grasp.setPosition(0.5);
        left_arm_grasp.setPosition(0.2);
    }
}
