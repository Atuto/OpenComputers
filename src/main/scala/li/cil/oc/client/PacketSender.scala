package li.cil.oc.client

import li.cil.oc.Settings
import li.cil.oc.common.tileentity._
import li.cil.oc.common.tileentity.traits.Computer
import li.cil.oc.common.{CompressedPacketBuilder, PacketBuilder, PacketType}
import net.minecraft.client.Minecraft
import net.minecraftforge.common.ForgeDirection

object PacketSender {
  // Timestamp after which the next clipboard message may be sent. Used to
  // avoid spamming large packets on key repeat.
  protected var clipboardCooldown = 0L

  def sendComputerPower(t: Computer, power: Boolean) {
    val pb = new PacketBuilder(PacketType.ComputerPower)

    pb.writeTileEntity(t)
    pb.writeBoolean(power)

    pb.sendToServer()
  }

  def sendKeyDown(address: String, char: Char, code: Int) {
    val pb = new PacketBuilder(PacketType.KeyDown)

    pb.writeUTF(address)
    pb.writeChar(char)
    pb.writeInt(code)

    pb.sendToServer()
  }

  def sendKeyUp(address: String, char: Char, code: Int) {
    val pb = new PacketBuilder(PacketType.KeyUp)

    pb.writeUTF(address)
    pb.writeChar(char)
    pb.writeInt(code)

    pb.sendToServer()
  }

  def sendClipboard(address: String, value: String) {
    if (value != null && !value.isEmpty) {
      if (value.length > 64 * 1024 || System.currentTimeMillis() < clipboardCooldown) {
        Minecraft.getMinecraft.sndManager.playSoundFX("note.harp", 3, 1)
      }
      else {
        clipboardCooldown = System.currentTimeMillis() + value.length / 10
        for (part <- value.grouped(16 * 1024)) {
          val pb = new CompressedPacketBuilder(PacketType.Clipboard)

          pb.writeUTF(address)
          pb.writeUTF(part)

          pb.sendToServer()
        }
      }
    }
  }

  def sendMouseClick(address: String, x: Int, y: Int, drag: Boolean, button: Int) {
    val pb = new PacketBuilder(PacketType.MouseClickOrDrag)

    pb.writeUTF(address)
    pb.writeShort(x)
    pb.writeShort(y)
    pb.writeBoolean(drag)
    pb.writeByte(button.toByte)

    pb.sendToServer()
  }

  def sendMouseScroll(address: String, x: Int, y: Int, scroll: Int) {
    val pb = new PacketBuilder(PacketType.MouseScroll)

    pb.writeUTF(address)
    pb.writeShort(x)
    pb.writeShort(y)
    pb.writeByte(scroll)

    pb.sendToServer()
  }

  def sendMouseUp(address: String, x: Int, y: Int, button: Int) {
    val pb = new PacketBuilder(PacketType.MouseUp)

    pb.writeUTF(address)
    pb.writeShort(x)
    pb.writeShort(y)
    pb.writeByte(button.toByte)

    pb.sendToServer()
  }

  def sendMultiPlace() {
    val pb = new PacketBuilder(PacketType.MultiPartPlace)
    pb.sendToServer()
  }

  def sendPetVisibility() {
    val pb = new PacketBuilder(PacketType.PetVisibility)

    pb.writeBoolean(!Settings.get.hideOwnPet)

    pb.sendToServer()
  }

  def sendRobotAssemblerStart(t: RobotAssembler) {
    val pb = new PacketBuilder(PacketType.RobotAssemblerStart)

    pb.writeTileEntity(t)

    pb.sendToServer()
  }

  def sendRobotStateRequest(dimension: Int, x: Int, y: Int, z: Int) {
    val pb = new PacketBuilder(PacketType.RobotStateRequest)

    pb.writeInt(dimension)
    pb.writeInt(x)
    pb.writeInt(y)
    pb.writeInt(z)

    pb.sendToServer()
  }

  def sendServerPower(t: ServerRack, number: Int, power: Boolean) {
    val pb = new PacketBuilder(PacketType.ComputerPower)

    pb.writeTileEntity(t)
    pb.writeInt(number)
    pb.writeBoolean(power)

    pb.sendToServer()
  }

  def sendServerRange(t: ServerRack, range: Int) {
    val pb = new PacketBuilder(PacketType.ServerRange)

    pb.writeTileEntity(t)
    pb.writeInt(range)

    pb.sendToServer()
  }

  def sendServerSide(t: ServerRack, number: Int, side: ForgeDirection) {
    val pb = new PacketBuilder(PacketType.ServerSide)

    pb.writeTileEntity(t)
    pb.writeInt(number)
    pb.writeDirection(side)

    pb.sendToServer()
  }

  def sendServerSwitchMode(t: ServerRack, internal: Boolean) {
    val pb = new PacketBuilder(PacketType.ServerSwitchMode)

    pb.writeTileEntity(t)
    pb.writeBoolean(internal)

    pb.sendToServer()
  }

  def sendTextBufferInit(address: String) {
    val pb = new PacketBuilder(PacketType.TextBufferInit)

    pb.writeUTF(address)

    pb.sendToServer()
  }
}