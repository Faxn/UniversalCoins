package universalcoins.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockVendor extends ItemBlock {

	public ItemBlockVendor(Block block) {
		super(block);
		setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int meta){
	return meta;
	}
}