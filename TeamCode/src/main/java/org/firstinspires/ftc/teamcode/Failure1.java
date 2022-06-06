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
@Autonomous(name = "Onyx Detection", group = "Concept")
public class Failure1 extends LinearOpMode {
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
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
      "Ball",
      "Cube",
      "Duck",
      "Marker",
    };
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor front_left = null;
    private DcMotor back_left = null;
    private DcMotor front_right = null;
    private DcMotor back_right = null;
    private DcMotor duck_spam = null;
    private DcMotor arm = null;
    private Servo left_arm_grasp;
    private Servo right_arm_grasp;

    private void moveForward() {
        back_left.setPower(0.5);
        back_right.setPower(0.5);
        front_left.setPower(0.5);
        front_right.setPower(0.5);
    }

    private void moveBackward() {
        back_left.setPower(-0.2);
        back_right.setPower(-0.2);
        front_left.setPower(-0.2);
        front_right.setPower(-0.2);
    }

    private void stopd() {
        back_left.setPower(0);
        back_right.setPower(0);
        front_left.setPower(0);
        front_right.setPower(0);
    }

    private void strafeRight() {
        back_left.setPower(-0.5);
        back_right.setPower(0.5);
        front_left.setPower(0.5);
        front_right.setPower(-0.5);
    }

    private void strafeLeft() {
        back_left.setPower(0.5);
        back_right.setPower(-0.5);
        front_left.setPower(-0.5);
        front_right.setPower(0.5);
    }

    private void closeClaw() {
        left_arm_grasp.setPosition(0.55);
        right_arm_grasp.setPosition(0.15);
    }

    private void openClaw() {
        left_arm_grasp.setPosition(0.2);
        right_arm_grasp.setPosition(0.5);
    }

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
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

        right_arm_grasp = hardwareMap.get(Servo.class, "right_arm_grasp");
        left_arm_grasp = hardwareMap.get(Servo.class, "left_arm_grasp");
        front_right = hardwareMap.get(DcMotor.class, "front_right");
        back_right = hardwareMap.get(DcMotor.class, "back_right");
        front_left = hardwareMap.get(DcMotor.class, "front_left");
        back_left = hardwareMap.get(DcMotor.class, "back_left");
        arm = hardwareMap.get(DcMotor.class, "arm");
        duck_spam = hardwareMap.get(DcMotor.class, "duck_spam");

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(1.5, 12.0/9.0);
        }

        /** Wait for the game to begin */
        back_left.setDirection(DcMotor.Direction.REVERSE);
        front_left.setDirection(DcMotor.Direction.REVERSE);
        closeClaw();
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        int level = 0;
        waitForStart();

        if (opModeIsActive()) {
            arm.setPower(-0.5);
            sleep(1400);
            arm.setPower(0);
            while (opModeIsActive()) {
                if (tfod != null) {
                    double left = 0;
                    level = 0;
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                      telemetry.addData("# Object Detected", updatedRecognitions.size());
                      // step through the list of recognitions and display boundary info.
                      int i = 0;
                      boolean isDuckDetected = false; //ADDED
                      for (Recognition recognition : updatedRecognitions) {
                        telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                        telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                recognition.getLeft(), recognition.getTop());
                        telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                recognition.getRight(), recognition.getBottom());
                        i++;
                          // check label to see if the camera now sees a Duck
                          if (recognition.getLabel().equals("Duck")) {
                              isDuckDetected = true;
                              left = recognition.getLeft();
                              telemetry.addData("Object Detected", "Duck");
                          } else {
                              isDuckDetected = false;
                          }
                      }
                      if (left != 0) {
                          if (left > 500) {
                              level = 2;
                              telemetry.addData("Level", level);
                              sleep(4000);
                              level2();
                          } else if (left < 500) {
                              level = 1;
                              telemetry.addData("Level", level);
                              sleep(4000);
                              level1();
                          } else {
                              level = 3;
                              telemetry.addData("Level", level);
                              sleep(4000);
                              level1();
                          }
                      }
                      telemetry.update();
                    }
                }
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

    private void level1() {
        strafeRight();
        sleep(1000);
        stopd();
        arm.setPower(0.5);
        sleep(50);
        arm.setPower(0);
        moveForward();
        sleep(1000);
        stopd();
        openClaw();
        sleep(500);
        moveBackward();
        sleep(200);
    }
    private void level2() {
        strafeRight();
        sleep(1000);
        moveForward();
        sleep(1000);
        stopd();
        openClaw();
        sleep(500);
        moveBackward();
        sleep(200);
    }
    private void level3() {
        strafeRight();
        sleep(1000);
        stopd();
        arm.setPower(-0.5);
        sleep(200);
        arm.setPower(0);
        moveForward();
        sleep(1000);
        stopd();
        openClaw();
        sleep(500);
        moveBackward();
        sleep(200);
    }
    private void duck() {
        strafeLeft();
        sleep(1300);
        moveBackward();
        sleep(800);
        duck_spam.setPower(1);
        sleep(5000);
    }
}
