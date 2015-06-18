package universalcoins.tile;

import universalcoins.UniversalCoins;
import universalcoins.blocks.BlockVendorFrame;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;

public class TileVendorBlock extends TileVendor {

	String signText[] = {"","","",""};
	
	@Override
	public String getName() {
		return StatCollector.translateToLocal("tile.blockVendor.name");
	}
	
	public void updateSigns() {

		if (inventory[itemTradeSlot] != null) {
			signText[0] = sellMode ? "&"+ Integer.toHexString(textColor) + "Selling" 
					: "&"+ Integer.toHexString(textColor) + "Buying";
			//add out of stock notification if not infinite and no stock found
			if (!infiniteMode && sellMode && ooStockWarning) {
				signText[0] = "&"+ Integer.toHexString(textColor) + (StatCollector.translateToLocal("sign.warning.stock"));
			}
			//add out of coins notification if buying and no funds available
			if (!sellMode && ooCoinsWarning && !infiniteMode) {
				signText[0] = "&"+ Integer.toHexString(textColor) + (StatCollector.translateToLocal("sign.warning.coins"));
			}
			//add inventory full notification
			if (!sellMode && inventoryFullWarning) {
				signText[0] = "&"+ Integer.toHexString(textColor) + (StatCollector.translateToLocal("sign.warning.inventoryfull"));
			}
			if (inventory[itemTradeSlot].stackSize > 1) {
				signText[1] = "&"+ Integer.toHexString(textColor) + inventory[itemTradeSlot].stackSize 
						+ " " + inventory[itemTradeSlot].getDisplayName();
			} else { 
				signText[1] = "&"+ Integer.toHexString(textColor) + inventory[itemTradeSlot].getDisplayName();
			}
			if (inventory[itemTradeSlot].isItemEnchanted()) {
				signText[2] = "&"+ Integer.toHexString(textColor);
				NBTTagList tagList = inventory[itemTradeSlot].getEnchantmentTagList();
				for (int i = 0; i < tagList.tagCount(); i++) {
					NBTTagCompound enchant = ((NBTTagList) tagList).getCompoundTagAt(i);
					signText[2] = signText[2].concat(Enchantment.enchantmentsBookList[enchant
							.getInteger("id")].getTranslatedName(enchant
							.getInteger("lvl")) + ", ");
				}
			} else signText[2] = "";
			if (inventory[itemTradeSlot].getItem() == UniversalCoins.proxy.itemPackage) {
				signText[2] = "&"+ Integer.toHexString(textColor);
				if( inventory[itemTradeSlot].getTagCompound() != null ) {
					NBTTagList tagList = inventory[itemTradeSlot].getTagCompound().getTagList("Inventory",
							Constants.NBT.TAG_COMPOUND);
					for (int i = 0; i < tagList.tagCount(); i++) {
						NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
						byte slot = tag.getByte("Slot");
							int itemCount = ItemStack.loadItemStackFromNBT(tag).stackSize;
							String itemName = ItemStack.loadItemStackFromNBT(tag).getDisplayName();
							signText[2] += itemCount + ":" + itemName + " ";
					}
				}
			}
			signText[3] = "&"+ Integer.toHexString(textColor) + "Price: " + itemPrice;
			
			//find and update all signs
			TileEntity te;
			te = super.worldObj.getTileEntity(new BlockPos(pos.getX()  + 1, pos.getY() - 1, pos.getZ()));
				if (te != null && te instanceof TileUCSign) {
					TileUCSign tesign = (TileUCSign) te;
					//tesign.signText = this.signText;
					tesign.updateSign();
					tesign.markDirty();
				} 
			te = super.worldObj.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY() - 1, pos.getZ()));
				if (te != null && te instanceof TileUCSign) {
					TileUCSign tesign = (TileUCSign) te;
					//tesign.signText = this.signText;
					tesign.updateSign();
					tesign.markDirty();
				}
			te = super.worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ() - 1));
				if (te != null && te instanceof TileUCSign) {
					TileUCSign tesign = (TileUCSign) te;
					//tesign.signText = this.signText;
					tesign.updateSign();
					tesign.markDirty();
				} 
			te = super.worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ() + 1));
				if (te != null && te instanceof TileUCSign) {
					TileUCSign tesign = (TileUCSign) te;
					//tesign.signText = this.signText;
					tesign.updateSign();
					tesign.markDirty();
				}
		}
	}
}
