package universalcoins.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import universalcoins.UniversalCoins;
import universalcoins.render.TileEntityCardStationRenderer;
import universalcoins.render.TileEntitySignalRenderer;
import universalcoins.render.TileEntityUCSignRenderer;
import universalcoins.render.VendorFrameRenderer;
import universalcoins.tile.TileCardStation;
import universalcoins.tile.TileSignal;
import universalcoins.tile.TileUCSign;
import universalcoins.tile.TileVendorFrame;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderers() {
		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

		// Items
		mesher.register(itemCoin, 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ itemCoin.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(itemSmallCoinStack, 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ itemSmallCoinStack.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(itemLargeCoinStack, 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ itemLargeCoinStack.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(itemSmallCoinBag, 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ itemSmallCoinBag.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(itemLargeCoinBag, 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ itemLargeCoinBag.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(itemEnderCard, 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ itemEnderCard.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(itemPackage, 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ itemPackage.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(itemSeller, 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ itemSeller.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(itemUCCard, 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ itemUCCard.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(itemVendorWrench, 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ itemVendorWrench.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(itemUCSign, 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ itemUCSign.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(itemLinkCard, 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ itemLinkCard.getUnlocalizedName().substring(5), "inventory"));

		// Blocks
		mesher.register(Item.getItemFromBlock(blockBandit), 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ blockBandit.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(Item.getItemFromBlock(blockBase), 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ blockBase.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(Item.getItemFromBlock(blockCardStation), 0, new ModelResourceLocation(UniversalCoins.modid
				+ ":" + blockCardStation.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(Item.getItemFromBlock(blockPackager), 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ blockPackager.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(Item.getItemFromBlock(blockSafe), 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ blockSafe.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(Item.getItemFromBlock(blockSignal), 0, new ModelResourceLocation(UniversalCoins.modid + ":"
				+ blockSignal.getUnlocalizedName().substring(5), "inventory"));
		mesher.register(Item.getItemFromBlock(blockTradeStation), 0, new ModelResourceLocation(UniversalCoins.modid
				+ ":" + blockTradeStation.getUnlocalizedName().substring(5), "inventory"));
		//mesher.register(Item.getItemFromBlock(blockVendor), 0, new ModelResourceLocation(UniversalCoins.modid
		//		+ ":" + blockVendor.getUnlocalizedName().substring(5), "inventory"));

		
		ClientRegistry.bindTileEntitySpecialRenderer(TileCardStation.class, new TileEntityCardStationRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileUCSign.class, new TileEntityUCSignRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSignal.class, new TileEntitySignalRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileVendorFrame.class,new VendorFrameRenderer());
		// ClientRegistry.bindTileEntitySpecialRenderer(TileVendor.class, new TileEntityVendorRenderer());
		// RenderingRegistry.registerBlockHandler(new BlockVendorRenderer(RenderingRegistry.getNextAvailableRenderId()));
		
	}
}
