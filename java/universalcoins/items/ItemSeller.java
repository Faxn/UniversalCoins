package universalcoins.items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import universalcoins.UniversalCoins;

public class ItemSeller extends Item {

	public ItemSeller() {
		super();
		this.setUnlocalizedName("itemSeller");
		this.setCreativeTab(UniversalCoins.tabUniversalCoins);
		this.setMaxStackSize(1);
	}	
}
