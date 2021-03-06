package universalcoins.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import universalcoins.UniversalCoins;
import universalcoins.util.UniversalAccounts;

public class TileSafe extends TileEntity implements IInventory, ISidedInventory {
	private ItemStack[] inventory = new ItemStack[2];
	public static final int itemInputSlot = 0;
	public static final int itemOutputSlot = 1;
	public String blockOwner = "nobody";
	public String accountNumber = "0";
	public long accountBalance = 0;

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] { 0 };
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStack, EnumFacing direction) {
		if (index == 0) {
			Item itemInStack = itemStack.getItem();
			return (itemInStack == UniversalCoins.proxy.iron_coin || itemInStack == UniversalCoins.proxy.emerald_coin
					|| itemInStack == UniversalCoins.proxy.gold_coin || itemInStack == UniversalCoins.proxy.diamond_coin
					|| itemInStack == UniversalCoins.proxy.obsidian_coin);
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot >= inventory.length) {
			return null;
		}
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= size) {
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(size);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}
			}
		}
		coinsTaken(stack);
		return stack;
	}

	public void coinsTaken(ItemStack stack) {
		int coinValue = 0;
		switch (stack.getUnlocalizedName()) {
		case "item.iron_coin":
			coinValue = UniversalCoins.coinValues[0];
			break;
		case "item.gold_coin":
			coinValue = UniversalCoins.coinValues[1];
			break;
		case "item.emerald_coin":
			coinValue = UniversalCoins.coinValues[2];
			break;
		case "item.diamond_coin":
			coinValue = UniversalCoins.coinValues[3];
			break;
		case "item.obsidian_coin":
			coinValue = UniversalCoins.coinValues[4];
			break;
		}
		int debitAmount = 0;
		int accountCapacity = (int) (Long.MAX_VALUE - accountBalance > Integer.MAX_VALUE ? Integer.MAX_VALUE
				: Long.MAX_VALUE - accountBalance);
		debitAmount = Math.min(stack.stackSize, accountCapacity / coinValue);
		if (!worldObj.isRemote) {
			UniversalAccounts.getInstance().debitAccount(accountNumber, debitAmount * coinValue);
			updateAccountBalance();
		}
		fillOutputSlot();
	}

	// @Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getStackInSlot(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;
		int coinValue = 0;
		if (stack != null) {
			if (slot == itemInputSlot) {
				switch (stack.getUnlocalizedName()) {
				case "item.iron_coin":
					coinValue = UniversalCoins.coinValues[0];
					break;
				case "item.gold_coin":
					coinValue = UniversalCoins.coinValues[1];
					break;
				case "item.emerald_coin":
					coinValue = UniversalCoins.coinValues[2];
					break;
				case "item.diamond_coin":
					coinValue = UniversalCoins.coinValues[3];
					break;
				case "item.obsidian_coin":
					coinValue = UniversalCoins.coinValues[4];
					break;
				}
				if (coinValue > 0) {
					int accountCapacity = (int) (Long.MAX_VALUE - accountBalance > Integer.MAX_VALUE ? Integer.MAX_VALUE
							: Long.MAX_VALUE - accountBalance);
					int depositAmount = Math.min(accountCapacity / coinValue, stack.stackSize);
					if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
						//Only deposit on server side, otherwise we deposit twice.
						UniversalAccounts.getInstance().creditAccount(accountNumber, depositAmount * coinValue);
					}
					inventory[slot].stackSize -= depositAmount;
					if (inventory[slot].stackSize == 0) {
						inventory[slot] = null;
					}
					fillOutputSlot();
					if (!worldObj.isRemote)
						updateAccountBalance();
				}
			}
		}
	}

	public void fillOutputSlot() {
		if (accountBalance > UniversalCoins.coinValues[4]) {
			inventory[itemOutputSlot] = new ItemStack(UniversalCoins.proxy.obsidian_coin);
			inventory[itemOutputSlot].stackSize = (int) Math.min(accountBalance / UniversalCoins.coinValues[4], 64);
		} else if (accountBalance > UniversalCoins.coinValues[3]) {
			inventory[itemOutputSlot] = new ItemStack(UniversalCoins.proxy.diamond_coin);
			inventory[itemOutputSlot].stackSize = (int) Math.min(accountBalance / UniversalCoins.coinValues[3], 64);
		} else if (accountBalance > UniversalCoins.coinValues[2]) {
			inventory[itemOutputSlot] = new ItemStack(UniversalCoins.proxy.emerald_coin);
			inventory[itemOutputSlot].stackSize = (int) Math.min(accountBalance / UniversalCoins.coinValues[2], 64);
		} else if (accountBalance > UniversalCoins.coinValues[1]) {
			inventory[itemOutputSlot] = new ItemStack(UniversalCoins.proxy.gold_coin);
			inventory[itemOutputSlot].stackSize = (int) Math.min(accountBalance / UniversalCoins.coinValues[1], 64);
		} else if (accountBalance > UniversalCoins.coinValues[0]) {
			inventory[itemOutputSlot] = new ItemStack(UniversalCoins.proxy.iron_coin);
			inventory[itemOutputSlot].stackSize = (int) Math.min(accountBalance / UniversalCoins.coinValues[0], 64);
		}
	}

	public void updateAccountBalance() {
		accountBalance = UniversalAccounts.getInstance().getAccountBalance(accountNumber);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(UniversalCoins.proxy.safe.getLocalizedName());
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return worldObj.getTileEntity(pos) == this
				&& entityplayer.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		// we only have a coin input slot
		return true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < inventory.length; i++) {
			ItemStack stack = inventory[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		tagCompound.setTag("Inventory", itemList);
		tagCompound.setString("Owner", blockOwner);
		tagCompound.setString("AccountNumber", accountNumber);
		tagCompound.setLong("Balance", accountBalance);

		return tagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);

		NBTTagList tagList = tagCompound.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inventory.length) {
				inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		try {
			blockOwner = tagCompound.getString("Owner");
		} catch (Throwable ex2) {
			blockOwner = "nobody";
		}
		try {
			accountNumber = tagCompound.getString("AccountNumber");
		} catch (Throwable ex2) {
			accountNumber = "0";
		}
		try {
			accountBalance = tagCompound.getLong("Balance");
		} catch (Throwable ex2) {
			accountBalance = 0;
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, getBlockMetadata(), getUpdateTag());
	}

	// required for sync on chunk load
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return nbt;
	}

	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
		if (accountBalance == 0)
			inventory[itemOutputSlot] = null;
	}

	public void updateTE() {
		markDirty();
		worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
	}

	public void setSafeAccount(String playerName) {
		accountNumber = UniversalAccounts.getInstance().getOrCreatePlayerAccount(getPlayerUID(playerName));
	}

	private String getPlayerUID(String playerName) {
		EntityPlayer player = worldObj.getPlayerEntityByName(playerName);
		return player.getUniqueID().toString();
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return UniversalCoins.proxy.safe.getLocalizedName();
	}
}
