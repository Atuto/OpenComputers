package li.cil.oc.client.renderer.font

import scala.io.Source
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import li.cil.oc.{OpenComputers, Settings}
import java.util.logging.Level
import li.cil.oc.client.Textures
import org.lwjgl.opengl.GL11

/**
 * Font renderer using a user specified texture file, meaning the list of
 * supported characters is fixed. But at least this one works.
 */
class StaticFontRenderer extends TextureFontRenderer {
  protected val (chars, charWidth, charHeight) = try {
    val lines = Source.fromInputStream(Minecraft.getMinecraft.getResourceManager.getResource(new ResourceLocation(Settings.resourceDomain, "textures/font/chars.txt")).getInputStream)("UTF-8").getLines()
    val chars = lines.next()
    val (w, h) = if (lines.hasNext) {
      val size = lines.next().split(" ", 2)
      (size(0).toInt, size(1).toInt)
    } else (10, 18)
    (chars, w, h)
  }
  catch {
    case t: Throwable =>
      OpenComputers.log.log(Level.WARNING, "Failed reading font metadata, using defaults.", t)
      (basicChars, 10, 18)
  }

  private val cols = 256 / charWidth
  private val uStep = charWidth / 256.0
  private val uSize = uStep
  private val vStep = (charHeight + 1) / 256.0
  private val vSize = charHeight / 256.0
  private val s = Settings.get.fontCharScale
  private val dw = charWidth * s - charWidth
  private val dh = charHeight * s - charHeight

  override protected def textureCount = 1

  override protected def bindTexture(index: Int) {
    if (Settings.get.textAntiAlias) {
      Minecraft.getMinecraft.getTextureManager.bindTexture(Textures.fontAntiAliased)
    }
    else {
      Minecraft.getMinecraft.getTextureManager.bindTexture(Textures.fontAliased)
    }
  }

  override protected def drawChar(tx: Float, ty: Float, char: Char) {
    val index = 1 + (chars.indexOf(char) match {
      case -1 => chars.indexOf('?')
      case i => i
    })
    val x = (index - 1) % cols
    val y = (index - 1) / cols
    val u = x * uStep
    val v = y * vStep
    GL11.glTexCoord2d(u, v + vSize)
    GL11.glVertex3d(tx - dw, ty + charHeight * s, 0)
    GL11.glTexCoord2d(u + uSize, v + vSize)
    GL11.glVertex3d(tx + charWidth * s, ty + charHeight * s, 0)
    GL11.glTexCoord2d(u + uSize, v)
    GL11.glVertex3d(tx + charWidth * s, ty - dh, 0)
    GL11.glTexCoord2d(u, v)
    GL11.glVertex3d(tx - dw, ty - dh, 0)
  }

  override protected def generateChar(char: Char) {}
}