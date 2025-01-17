package org.firstinspires.ftc.teamcode.src.testCodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.tfod.Recognition
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector
import org.firstinspires.ftc.teamcode.src.models.ABot.AutoInstance

@Autonomous(name = "CamDetection", group = "AutoTest")
class TensorFlowCamTest: LinearOpMode() {
    private var bot: AutoInstance? = null
    private val TFOD_MODEL_ASSET: String = "cones_models/model_unquant.tflite"
    private val LABELS: Array<String> = arrayOf<String>("BlueCone", "RedCone")
    override fun runOpMode() {
        try {
            bot = AutoInstance(this, hardwareMap, telemetry)
            val tfod = initTF()
            tfod.activate()
            tfod.setZoom(1.0, 16.0/9.0)

            telemetry.addData(">", "Press Start")
            telemetry.update()

            waitForStart()

            if (opModeIsActive()) {
                while (opModeIsActive()) {
                    val updatedRec: List<Recognition> = tfod.updatedRecognitions
                    telemetry.addData("# Objects Detected", updatedRec.size)

                    for (rec: Recognition in updatedRec) {
                        val col: Double = (rec.left + rec.right) / 2.toDouble()
                        val row: Double = (rec.top + rec.bottom) / 2.toDouble()
                        val width: Double = (rec.right - rec.left).toDouble()
                        val height: Double = (rec.top - rec.bottom).toDouble()

                        telemetry.addData("", "")
                        telemetry.addData("Image", String.format("%s (%.0f %% Conf.)", rec.label, rec.confidence * 100))
                        telemetry.addData("- Position (Row/Col)", String.format("%.0f / %.0f", row, col))
                        telemetry.addData("- Size (Width/Height)", String.format("%.0f / %.0f", width, height))
                    }
                    telemetry.update()
                }
            }
        } catch(e: Exception) {
            println(e.message)
            telemetry.addData("Major Error", e.message)
            telemetry.update()
        }
    }

    // Initialize Vuforia localization engine
    private fun initVuforia(): VuforiaLocalizer {
        val parameters: VuforiaLocalizer.Parameters = VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = bot?.vuforiaKey
        parameters.cameraName = bot?.camera

        return ClassFactory.getInstance().createVuforia(parameters)
    }
    // Initialize the TensorFlow Object Detection engine.
    private fun initTF(): TFObjectDetector {
        val vuforia: VuforiaLocalizer = initVuforia()
        val tfMonitorViewId: Int = hardwareMap.appContext.resources.getIdentifier(
            "tfMonitorViewId", "id", hardwareMap.appContext.packageName
        )
        val tfParameters: TFObjectDetector.Parameters = TFObjectDetector.Parameters(tfMonitorViewId)
        tfParameters.minResultConfidence = 0.75f
        tfParameters.isModelTensorFlow2 = true
        tfParameters.inputSize = 300
        val TFOD = ClassFactory.getInstance().createTFObjectDetector(tfParameters, vuforia)
        TFOD.loadModelFromAsset(TFOD_MODEL_ASSET, *LABELS)

        return TFOD
    }
}