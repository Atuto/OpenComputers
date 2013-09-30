package li.cil.oc.common.tileentity

import net.minecraft.tileentity.TileEntity
import li.cil.oc.api.network.{Visibility, Node}
import net.minecraftforge.common.{ForgeDirection, MinecraftForge}
import ic2.api.energy.event.{EnergyTileLoadEvent, EnergyTileUnloadEvent}
import cpw.mods.fml.common.FMLCommonHandler
import ic2.api.energy.tile.IEnergySink

/**
 * Created with IntelliJ IDEA.
 * User: lordjoda
 * Date: 30.09.13
 * Time: 20:37
 * To change this template use File | Settings | File Templates.
 */
class PowerSupply extends Rotatable with Node with IEnergySink{
  var addedToEnet = false
  override def name = "powersupply"

  override def visibility = Visibility.Network

  override def onChunkUnload(){
    super.onChunkUnload()
       onUnload()
  }
  def onUnload(){
    if(addedToEnet){
      MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this))
      addedToEnet = false
    }
  }

    override def updateEntity(){
      super.updateEntity()
      if(!addedToEnet) {
        onLoaded()
      }
    }


  /**
   * Notification that the TileEntity finished loaded, for advanced uses.
   * Either onUpdateEntity or onLoaded have to be used.
   */
  def onLoaded() {
    if (!addedToEnet && !FMLCommonHandler.instance.getEffectiveSide.isClient) {
      MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this))
      addedToEnet = true
    }
  }

   var storedEnergy = 0.0;
  var lastInjectedEnergy =0.0;
  var MAXENERGY = 1000;
  //IC2 stuff
  /**
   * Determine how much energy the sink accepts.
   *
   * This value is unrelated to getMaxSafeInput().
   *
   * Make sure that injectEnergy() does accepts energy if demandsEnergy() returns anything > 0.
   *
   * @return max accepted input in eu
   */
  override def demandedEnergyUnits: Double={
    val needed = MAXENERGY-storedEnergy
    if(needed>lastInjectedEnergy||needed>MAXENERGY/2)
       return needed
    0
  }

  /**
   * Transfer energy to the sink.
   *
   * It's highly recommended to accept all energy by letting the internal buffer overflow to
   * increase the performance and accuracy of the distribution simulation.
   *
   * @param directionFrom direction from which the energy comes from
   * @param amount energy to be transferred
   * @return Energy not consumed (leftover)
   */
  override def injectEnergyUnits(directionFrom: ForgeDirection, amount: Double): Double ={
    lastInjectedEnergy = amount;
    storedEnergy+=amount;
    0
  }

  /**
   * Determine the amount of eu which can be safely injected into the specific energy sink without exploding.
   *
   * Typical values are 32 for LV, 128 for MV, 512 for HV and 2048 for EV. A value of Integer.MAX_VALUE indicates no
   * limit.
   *
   * This value is unrelated to demandsEnergy().
   *
   * @return max safe input in eu
   */
  override def getMaxSafeInput: Int   =Integer.MAX_VALUE

  /**
   * Determine if this acceptor can accept current from an adjacent emitter in a direction.
   *
   * The TileEntity in the emitter parameter is what was originally added to the energy net,
   * which may be normal in-world TileEntity, a delegate or an IMetaDelegate.
   *
   * @param emitter energy emitter
   * @param direction direction the energy is being received from
   */
  override def acceptsEnergyFrom(emitter: TileEntity, direction: ForgeDirection): Boolean = true
}
