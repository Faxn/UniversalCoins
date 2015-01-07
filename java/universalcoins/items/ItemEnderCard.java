package universalcoins.items;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import universalcoins.UniversalCoins;
import universalcoins.util.UCWorldData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEnderCard extends Item {
	
	private static final int[] multiplier = new int[] {1, 9, 81, 729, 6561};
	
	public ItemEnderCard() {
		super();
		this.maxStackSize = 1;
		setCreativeTab(UniversalCoins.tabUniversalCoins);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister){
		this.itemIcon = par1IconRegister.registerIcon(UniversalCoins.modid + ":" + this.getUnlocalizedName().substring(5));
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {		
		if( stack.stackTagCompound != null ) {
			list.add("Owner: " + stack.stackTagCompound.getString("Owner"));
			list.add("Account: " + stack.stackTagCompound.getString("Account"));
		}	
	}
	
	@Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float px, float py, float pz){
		if (world.isRemote) return true;
		if (!itemstack.hasTagCompound()) {
			if (getPlayerAccount(world, player.getDisplayName()) == "") {
				player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal(
						"item.itemEnderCard.noaccount")));
				return true;
			}
			//add player account info
			itemstack.stackTagCompound = new NBTTagCompound();
			itemstack.stackTagCompound.setString("Owner", player.getDisplayName());
			itemstack.stackTagCompound.setString("Account", getPlayerAccount(world, player.getDisplayName()));
		}
		int accountBalance = getAccountBalance(world, itemstack.stackTagCompound.getString("Account"));
		DecimalFormat formatter = new DecimalFormat("#,###,###,###");//TODO localization
		ItemStack[] inventory = player.inventory.mainInventory;
		String accountNumber = itemstack.stackTagCompound.getString("Account");
		int coinsDeposited = 0;
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] != null && (inventory[i].getItem() == UniversalCoins.proxy.itemCoin ||
					inventory[i].getItem() == UniversalCoins.proxy.itemSmallCoinStack ||
					inventory[i].getItem() == UniversalCoins.proxy.itemLargeCoinStack ||
					inventory[i].getItem() == UniversalCoins.proxy.itemSmallCoinBag ||
					inventory[i].getItem() == UniversalCoins.proxy.itemLargeCoinBag	)) {
				if (accountBalance == -1) return true; //get out of here if the card is invalid
				int coinType = getCoinType(inventory[i].getItem());
				if (coinType == -1) return true; //something went wrong
				int coinValue = multiplier[coinType];
				int depositAmount = Math.min(inventory[i].stackSize, (Integer.MAX_VALUE - accountBalance ) / coinValue);
				creditAccount(world, accountNumber, depositAmount * coinValue);
				coinsDeposited += depositAmount * coinValue;
				inventory[i].stackSize -= depositAmount;
				if (inventory[i].stackSize == 0) {
					player.inventory.setInventorySlotContents(i, null);
					player.inventoryContainer.detectAndSendChanges();
				}
			}
		}
		if (coinsDeposited > 0) {
			player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal(
				"item.itemEnderCard.message.deposit") + " " + formatter.format(coinsDeposited) + " " 
				+ StatCollector.translateToLocal("item.itemCoin.name")));
		}
		player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal(
				"item.itemEnderCard.balance") + " " + formatter.format(getAccountBalance(world, accountNumber))));
        return true;
    }
	
	private int getCoinType(Item item) {
		final Item[] coins = new Item[] { UniversalCoins.proxy.itemCoin,
			UniversalCoins.proxy.itemSmallCoinStack, UniversalCoins.proxy.itemLargeCoinStack, 
			UniversalCoins.proxy.itemSmallCoinBag, UniversalCoins.proxy.itemLargeCoinBag };
		for (int i = 0; i < 5; i++) {
			if (item == coins[i]) {
				return i;
			}
		}
		return -1;
	}
	
	private String getPlayerAccount(World world, String player) {
		//returns an empty string if no account found
		return getWorldString(world, player);
	}
	
	private int getAccountBalance(World world, String accountNumber) {
		return getWorldInt(world, accountNumber);
	}
	
	private void creditAccount(World world, String accountNumber, int amount) {
		if (getWorldString(world, accountNumber) != "") {
			int balance = getWorldInt(world, accountNumber);
			balance += amount;
			setWorldData(world, accountNumber, balance);
		}
	}

	private int getWorldInt(World world, String tag) {
		UCWorldData wData = UCWorldData.get(world);
		NBTTagCompound wdTag = wData.getData();
		return wdTag.getInteger(tag);
	}
	
	private String getWorldString(World world,String tag) {
		UCWorldData wData = UCWorldData.get(world);
		NBTTagCompound wdTag = wData.getData();
		return wdTag.getString(tag);
	}
	
	private void setWorldData(World world, String tag, int data) {
		UCWorldData wData = UCWorldData.get(world);
		NBTTagCompound wdTag = wData.getData();
		wdTag.setInteger(tag, data);
		wData.markDirty();
	}
}
