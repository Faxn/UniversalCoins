package universalcoins.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import universalcoins.UniversalCoins;
import universalcoins.tile.TileVendor;
import universalcoins.util.UCItemPricer;

public class ComponentVillageShop extends StructureVillagePieces.Village {

	private int averageGroundLevel = -1;

	public ComponentVillageShop(Start startPiece, int p5, Random random, StructureBoundingBox box, EnumFacing facing) {
		super(startPiece, p5);
		this.coordBaseMode = facing;
		this.boundingBox = box;
		MapGenStructureIO.registerStructureComponent(ComponentVillageShop.class, "ViUS");
	}

	public static ComponentVillageShop buildComponent(Start startPiece, List pieces, Random random, int p1, int p2,
			int p3, EnumFacing facing, int p5) {
		StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 5, 6, 6,
				facing);
		return canVillageGoDeeper(box) && StructureComponent.findIntersecting(pieces, box) == null
				? new ComponentVillageShop(startPiece, p5, random, box, facing) : null;
	}

	@Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb) {
		if (this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);

			if (this.averageGroundLevel < 0)
				return true;

			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 5 - 1, 0);
		}

		// Clear area
		fillWithAir(world, sbb, 0, 0, 0, 5, 6, 7);
		// start with block
		this.fillWithBlocks(world, sbb, 0, 0, 0, 5, 0, 7, Blocks.double_stone_slab.getDefaultState(),
				Blocks.double_stone_slab.getDefaultState(), false);
		// main wall
		this.fillWithBlocks(world, sbb, 0, 1, 0, 5, 1, 7, Blocks.planks.getDefaultState(),
				Blocks.planks.getDefaultState(), false);
		// front
		this.fillWithBlocks(world, sbb, 0, 2, 0, 5, 3, 0, Blocks.oak_fence.getDefaultState(),
				Blocks.oak_fence.getDefaultState(), false);
		// back
		this.fillWithBlocks(world, sbb, 0, 2, 7, 5, 3, 7, Blocks.oak_fence.getDefaultState(),
				Blocks.oak_fence.getDefaultState(), false);
		// top
		this.fillWithBlocks(world, sbb, 0, 4, 0, 5, 4, 7, Blocks.planks.getDefaultState(),
				Blocks.planks.getDefaultState(), false);
		this.fillWithBlocks(world, sbb, 1, 5, 1, 4, 5, 6, Blocks.planks.getDefaultState(),
				Blocks.planks.getDefaultState(), false);
		// clear main
		fillWithAir(world, sbb, 1, 1, 1, 4, 4, 6);
		// clear door
		fillWithAir(world, sbb, 2, 1, 0, 3, 2, 0);
		// clear back
		fillWithAir(world, sbb, 1, 2, 7, 4, 2, 7);
		// torches
		this.setBlockState(world, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 1,
				4, 1, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 4,
				4, 1, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 1,
				4, 6, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 4,
				4, 6, boundingBox);
		// signs
		this.fillWithBlocks(world, sbb, 1, 1, 1, 1, 1, 6,
				UniversalCoins.proxy.wall_ucsign.getStateFromMeta(getSignMeta(5)),
				UniversalCoins.proxy.wall_ucsign.getStateFromMeta(getSignMeta(5)), false);
		this.fillWithBlocks(world, sbb, 4, 1, 1, 4, 1, 6,
				UniversalCoins.proxy.wall_ucsign.getStateFromMeta(getSignMeta(4)),
				UniversalCoins.proxy.wall_ucsign.getStateFromMeta(getSignMeta(4)), false);
		// vending blocks
		this.fillWithBlocks(world, sbb, 0, 2, 1, 0, 2, 6, UniversalCoins.proxy.blockVendor.getDefaultState(),
				UniversalCoins.proxy.blockVendor.getDefaultState(), false);
		this.fillWithBlocks(world, sbb, 5, 2, 1, 5, 2, 6, UniversalCoins.proxy.blockVendor.getDefaultState(),
				UniversalCoins.proxy.blockVendor.getDefaultState(), false);

		// list of items
		List<Item> saleItems = new ArrayList();

		// fill left vending blocks
		for (int i = 0; i < 6; i++) {
			int priceModifier = random.nextInt(UniversalCoins.shopMaxPrice - UniversalCoins.shopMinPrice)
					+ UniversalCoins.shopMinPrice;
			ItemStack stack = UCItemPricer.getInstance().getRandomPricedStack();
			while (saleItems.contains(stack.getItem())) {
				stack = UCItemPricer.getInstance().getRandomPricedStack();
			}
			int price = UCItemPricer.getInstance().getItemPrice(stack);
			addVendorItems(world, 0, 2, i + 1, stack, price * priceModifier / 100);
			saleItems.add(stack.getItem());
		}
		// fill right vending blocks
		for (int i = 0; i < 6; i++) {
			int priceModifier = random.nextInt(UniversalCoins.shopMaxPrice - UniversalCoins.shopMinPrice)
					+ UniversalCoins.shopMinPrice;
			ItemStack stack = UCItemPricer.getInstance().getRandomPricedStack();
			while (saleItems.contains(stack.getItem())) {
				stack = UCItemPricer.getInstance().getRandomPricedStack();
			}
			int price = UCItemPricer.getInstance().getItemPrice(stack);
			addVendorItems(world, 5, 2, i + 1, stack, price * priceModifier / 100);
			saleItems.add(stack.getItem());
		}

		// add stairs if needed
		if (this.getBlockStateFromPos(world, 2, 0, -1, sbb).getBlock().getMaterial() == Material.air
				&& this.getBlockStateFromPos(world, 2, -1, -1, sbb).getBlock().getMaterial() != Material.air) {
			this.setBlockState(world,
					Blocks.oak_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.oak_stairs, 3)), 2, 0, -1,
					sbb);
		}

		// build foundation
		for (int l = 0; l < 6; ++l) {
			for (int k = 0; k < 5; ++k) {
				this.clearCurrentPositionBlocksUpwards(world, k, 7, l, sbb);
				this.replaceAirAndLiquidDownwards(world, Blocks.cobblestone.getDefaultState(), k, -1, l, sbb);
			}
		}
		return true;
	}

	protected void addVendorItems(World world, int par4, int par5, int par6, ItemStack stack, int price) {
		int i1 = this.getXWithOffset(par4, par6);
		int j1 = this.getYWithOffset(par5);
		int k1 = this.getZWithOffset(par4, par6);

		TileEntity tentity = world.getTileEntity(new BlockPos(i1, j1, k1));
		if (tentity instanceof TileVendor) {
			TileVendor tileentity = (TileVendor) tentity;
			tileentity.infiniteMode = true;
			tileentity.itemPrice = price;
			tileentity.setSellItem(stack);
			tileentity.updateTE();
			tileentity.updateSigns();
		}
	}

	private int getSignMeta(int meta) {
		// sign meta/rotation 2=S ,3=N ,4=E ,5=W
		// coordBaseMode 0=S ,1=W ,2=N ,3=E
		// returns meta value needed to rotate sign normally
		if (coordBaseMode == EnumFacing.SOUTH) {
			if (meta == 2)
				return 3;
			if (meta == 3)
				return 2;
			if (meta == 4)
				return 4;
			if (meta == 5)
				return 5;
			return 2;
		}
		if (coordBaseMode == EnumFacing.WEST) {
			if (meta == 2)
				return 4;
			if (meta == 3)
				return 5;
			if (meta == 4)
				return 2;
			if (meta == 5)
				return 3;
			return 4;
		}
		if (coordBaseMode == EnumFacing.NORTH) {
			if (meta == 2)
				return 2;
			if (meta == 3)
				return 3;
			if (meta == 4)
				return 4;
			if (meta == 5)
				return 5;
			return 3;
		}
		if (coordBaseMode == EnumFacing.EAST) {
			if (meta == 2)
				return 5;
			if (meta == 3)
				return 4;
			if (meta == 4)
				return 2;
			if (meta == 5)
				return 3;
			return 5;
		}
		return 5;
	}

	private boolean isReplaceableBlock(World world, int x, int y, int z) {
		IBlockState state = world.getBlockState(new BlockPos(x, y, z));
		if (state.getBlock().getMaterial() == Material.air || state.getBlock().getMaterial() == Material.water
				|| state.getBlock().getMaterial() == Material.grass) {
			return true;
		}
		return false;
	}
}
