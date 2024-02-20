package org.firstinspires.ftc.team22012;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "April Tag Detection", group = "Tutorial Code")
public class TutorialCode extends LinearOpMode {

    /*
    * Camera Calibration Values
    * */

    public double fx = 1234;
    public double fy = 1502.91;
    public double cx = 970.487;
    public double cy = 389.479;

    public final boolean calibratedCamera = false;
    private AprilTagProcessor aprilTagProcessor;

    @Override
    public void runOpMode() throws InterruptedException {

        /*
        Currently the SDK has calibration data for 10 resolutions spread among 4 webcams:

        Logitech HD Webcam C270, 640x480

        Logitech HD Pro Webcam C920, 640x480, 800x600, 640x360, 1920x1080, 800x448, 864x480

        Logitech HD Webcam C310, 640x480, 640x360

        Microsoft Lifecam HD 3000 v1/v2, 640x480

        These are found in the SDK file builtinwebcamcalibrations.xml.
        In Android Studio, navigate to the subfolders RobotCore, res, xml.
        * */
        // If your camera is one of the ones mentioned above then you can just use this code:
        if (calibratedCamera){
            aprilTagProcessor = AprilTagProcessor.easyCreateWithDefaults();
        } else {
            aprilTagProcessor = new AprilTagProcessor.Builder()
                    .setLensIntrinsics(fx, fy, cx, cy)
                    .build();
        }


        VisionPortal visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTagProcessor)
                .setCameraResolution(new Size(640, 480))
                .build();

        waitForStart();

        while (opModeIsActive()) {
            for (AprilTagDetection detection : aprilTagProcessor.getDetections()) {
                if (detection.metadata != null) {
                    telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                    telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                    telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                    telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
                } else {
                    telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                    telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
                }
            }   // end for() loop

            // Add "key" information to telemetry
            telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
            telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
            telemetry.addLine("RBE = Range, Bearing & Elevation");

            telemetry.update();

            sleep(20);
        }
    }
}
