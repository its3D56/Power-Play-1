package org.firstinspires.ftc.teamcode.src.models

import android.content.Context
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.*
import  org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName

open class BotHardware(Instance:LinearOpMode, hardware: HardwareMap, gamepad: Gamepad? = null, t: Telemetry) {
    open val vuforiaKey: String = hardware.appContext.assets.open("vuforiaKey.txt").bufferedReader().use { it.readText() }
    open val fl: DcMotor = hardware.get("FL") as DcMotor
    open val fr: DcMotor = hardware.get("FR") as DcMotor
    open val br: DcMotor = hardware.get("BR") as DcMotor
    open val bl: DcMotor = hardware.get("BL") as DcMotor
    open var camera: CameraName = hardware.get("Webcam") as CameraName
    open var forward:Double? = -gamepad?.left_stick_y?.toDouble()!!
    open var pivot: Double? = gamepad?.left_stick_x?.toDouble()!!
    private val telemetry: Telemetry = t
    private val pi: Double = Math.PI
    private val radius: Double = 7.036308765
    private val instance = Instance



    init {
        fl.direction = DcMotorSimple.Direction.FORWARD
        fr.direction = DcMotorSimple.Direction.REVERSE
        bl.direction = DcMotorSimple.Direction.FORWARD
        br.direction = DcMotorSimple.Direction.REVERSE
    }

    fun move(Direction: String = "Forward", Inches: Double, Power: Double) {
        try {
                val dir = Direction
                var power: Double = Power
                val distance = inchToTick(Inches)

                if (Direction == "Backwards"){
                    power = -Power
                }

                fl.power = power
                fr.power = power
                bl.power = power
                br.power = power

//                while (instance.opModeIsActive())
        } catch (e: Exception) {
            telemetry.addData("Error: ", e.message)
            print(e.message)
            telemetry.update()
        }
    }

    private fun inchToTick(inches: Double): Double{
        return inches * 37.9498
    }
    private fun targetDegrees(degrees: Double) : Double {
        return inchToTick((radius * pi * degrees)/180)
    }
}

