package universalcoins.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import universalcoins.UniversalCoins;
import universalcoins.tile.TileCardStation;

public class BlockCardStation extends BlockRotatable {

	public BlockCardStation() {
		super();
		setHardness(3.0F);
		setCreativeTab(UniversalCoins.tabUniversalCoins);
		setResistance(30.0F);
		setLightLevel(8);
	}
	
	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		String ownerName = ((TileCardStation) world.getTileEntity(pos)).blockOwner;
		if (player.getDisplayName().equals(ownerName)) {
			this.setHardness(1.0F);
		} else {
			this.setHardness(-1.0F);
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity != null && tileEntity instanceof TileCardStation) {
			if (((TileCardStation) tileEntity).inUse) {
				if (!world.isRemote) {
					player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("chat.warning.inuse")));
				}
				return true;
			} else {
				player.openGui(UniversalCoins.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
				((TileCardStation) tileEntity).playerName = player.getName();
				((TileCardStation) tileEntity).playerUID = player.getUniqueID().toString();
				((TileCardStation) tileEntity).inUse = true;
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player,
			ItemStack stack) {
		((TileCardStation) world.getTileEntity(pos)).blockOwner = player.getName();
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		String ownerName = ((TileCardStation) world.getTileEntity(pos)).blockOwner;
		if (player.capabilities.isCreativeMode) {
			super.removedByPlayer(world, pos, player, willHarvest);
		}
		if (player.getDisplayName().equals(ownerName) && !world.isRemote) {
			super.removedByPlayer(world, pos, player, willHarvest);
			return true;
		}
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileCardStation();
	}

	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		world.setBlockToAir(pos);
		onBlockDestroyedByExplosion(world, pos, explosion);
		EntityItem entityItem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this, 1));
		if (!world.isRemote)
			world.spawnEntityInWorld(entityItem);
	}
}
