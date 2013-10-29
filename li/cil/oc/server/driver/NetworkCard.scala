package li.cil.oc.server.driver

import li.cil.oc.api.driver.Slot
import li.cil.oc.server.component
import li.cil.oc.{Config, Items}
import net.minecraft.item.ItemStack

object NetworkCard extends Item {
  def worksWith(item: ItemStack) = WorksWith(Items.lan)(item)

  override def createEnvironment(item: ItemStack) = new component.NetworkCard()

  def slot(item: ItemStack) = Slot.Card
}