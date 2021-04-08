package com.minecraftabnormals.environmental.common.block;

import com.minecraftabnormals.abnormals_core.core.util.item.filling.TargetedItemGroupFiller;
import com.minecraftabnormals.environmental.core.registry.EnvironmentalBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nullable;

public class GiantLilyPadBlock extends BushBlock implements IPlantable {
	protected static final VoxelShape GIANT_LILY_PAD_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.5D, 16.0D);
	public static final EnumProperty<LilyPadPosition> POSITION = EnumProperty.create("position", LilyPadPosition.class);
	private static final TargetedItemGroupFiller FILLER = new TargetedItemGroupFiller(() -> EnvironmentalBlocks.LARGE_LILY_PAD.get().asItem());

	public GiantLilyPadBlock(AbstractBlock.Properties builder) {
		super(builder);
	}

//	@SuppressWarnings("deprecation")
//	@Override
//	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
//		super.onEntityCollision(state, worldIn, pos, entityIn);
//		if (entityIn.isSuppressingBounce()) {
//			super.onLanded(worldIn, entityIn);
//		} else {
//			this.bounce(entityIn);
//		}
//	}
//
//	public void bounce(Entity entity) {
//		Vector3d vector3d = entity.getMotion();
//		if (vector3d.y < 0.0D) {
//			double d0 = entity instanceof LivingEntity ? 1.5D : 1.2D;
//			entity.setMotion(vector3d.x, -vector3d.y * d0, vector3d.z);
//		}
//	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(POSITION);
	}

	public static void placeAt(World world, BlockPos pos, BlockState state, int flags) {
		world.setBlockState(pos, state.with(POSITION, LilyPadPosition.CENTER), flags);

		world.setBlockState(pos.offset(Direction.NORTH), state.with(POSITION, LilyPadPosition.NORTH), flags);
		world.setBlockState(pos.offset(Direction.EAST), state.with(POSITION, LilyPadPosition.EAST), flags);
		world.setBlockState(pos.offset(Direction.SOUTH), state.with(POSITION, LilyPadPosition.SOUTH), flags);
		world.setBlockState(pos.offset(Direction.WEST), state.with(POSITION, LilyPadPosition.WEST), flags);

		world.setBlockState(pos.offset(Direction.NORTH).offset(Direction.EAST), state.with(POSITION, LilyPadPosition.NORTHEAST), flags);
		world.setBlockState(pos.offset(Direction.SOUTH).offset(Direction.EAST), state.with(POSITION, LilyPadPosition.SOUTHEAST), flags);
		world.setBlockState(pos.offset(Direction.SOUTH).offset(Direction.WEST), state.with(POSITION, LilyPadPosition.SOUTHWEST), flags);
		world.setBlockState(pos.offset(Direction.NORTH).offset(Direction.WEST), state.with(POSITION, LilyPadPosition.NORTHWEST), flags);
	}

	public static boolean checkPositions(World world, BlockPos pos, BlockState state) {
		if (!isValidPosAndAir(state.with(POSITION, LilyPadPosition.NORTH), world, pos.offset(Direction.NORTH)))
			return false;
		if (!isValidPosAndAir(state.with(POSITION, LilyPadPosition.EAST), world, pos.offset(Direction.EAST)))
			return false;
		if (!isValidPosAndAir(state.with(POSITION, LilyPadPosition.SOUTH), world, pos.offset(Direction.SOUTH)))
			return false;
		if (!isValidPosAndAir(state.with(POSITION, LilyPadPosition.WEST), world, pos.offset(Direction.WEST)))
			return false;

		if (!isValidPosAndAir(state.with(POSITION, LilyPadPosition.NORTHEAST), world, posToBlockPos(LilyPadPosition.NORTHEAST, pos, false)))
			return false;
		if (!isValidPosAndAir(state.with(POSITION, LilyPadPosition.SOUTHEAST), world, pos.offset(Direction.SOUTH).offset(Direction.EAST)))
			return false;
		if (!isValidPosAndAir(state.with(POSITION, LilyPadPosition.SOUTHWEST), world, pos.offset(Direction.SOUTH).offset(Direction.WEST)))
			return false;
		if (!isValidPosAndAir(state.with(POSITION, LilyPadPosition.NORTHWEST), world, pos.offset(Direction.NORTH).offset(Direction.WEST)))
			return false;

		return true;
	}

	public static boolean isValidPosAndAir(BlockState state, World world, BlockPos pos) {
		return state.isValidPosition(world, pos) && world.getBlockState(pos).isAir();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return GIANT_LILY_PAD_AABB;
	}

	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
		super.onFallenUpon(worldIn, pos, entityIn, fallDistance * 0.25F);
	}

	@Override
	public void onLanded(IBlockReader worldIn, Entity entityIn) {
		if (entityIn.isSuppressingBounce()) {
			super.onLanded(worldIn, entityIn);
		} else {
			this.bounce(entityIn);
		}
	}

	private void bounce(Entity entity) {
		Vector3d vector3d = entity.getMotion();
		if (vector3d.y < 0.0D) {
			double d0 = entity instanceof LivingEntity ? 1.5D : 1.2D;
			entity.setMotion(vector3d.x, -vector3d.y * d0, vector3d.z);
		}
	}

	@Override
	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
		FluidState fluidstate = worldIn.getFluidState(pos);
		FluidState fluidstate1 = worldIn.getFluidState(pos.up());
		return (fluidstate.getFluid() == Fluids.WATER || state.getMaterial() == Material.ICE) && fluidstate1.getFluid() == Fluids.EMPTY;
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.down();
		if (state.getBlock() == this) {
			return worldIn.getBlockState(blockpos).canSustainPlant(worldIn, blockpos, Direction.UP, this);
		}
		return this.isValidGround(worldIn.getBlockState(blockpos), worldIn, blockpos);
	}

	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		return (!stateIn.isValidPosition(worldIn, currentPos) || !this.isConnected(stateIn, worldIn, currentPos)) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	public boolean isConnected(BlockState state, IBlockReader worldIn, BlockPos pos) {
		LilyPadPosition position = state.get(POSITION);
		pos = posToBlockPos(position, pos, true);
		for (LilyPadPosition newPosition : LilyPadPosition.values()) {
			if (worldIn.getBlockState(posToBlockPos(newPosition, pos, false)).isIn(this.getBlock()) && worldIn.getBlockState(posToBlockPos(newPosition, pos, false)).get(POSITION) == newPosition) {
			} else
				return false;
		}
		return true;
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!worldIn.isRemote) {
			if (player.isCreative()) {
				removeEachBlock(worldIn, pos, state, player);
			} else {
				spawnDrops(state, worldIn, pos, (TileEntity) null, player, player.getHeldItemMainhand());
			}
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	private static void removeEachBlock(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		LilyPadPosition position = state.get(POSITION);
		pos = posToBlockPos(position, pos, true);
		for (LilyPadPosition lilyPadPos : LilyPadPosition.values()) {
			removeBlock(lilyPadPos, world, pos, state, player);
		}
	}

	private static void removeBlock(LilyPadPosition position, World world, BlockPos pos, BlockState state, PlayerEntity player) {
		BlockPos blockpos = posToBlockPos(position, pos, false);
		BlockState blockstate = world.getBlockState(blockpos);
		if (blockstate.getBlock() == state.getBlock() && blockstate.get(POSITION) == position) {
			world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 51);
			world.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
		}
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
	}

	public static BlockPos posToBlockPos(LilyPadPosition position, BlockPos pos, boolean revert) {
		if (!revert) {
			switch (position) {

				case NORTH:
					return pos.offset(Direction.NORTH);
				case EAST:
					return pos.offset(Direction.EAST);
				case SOUTH:
					return pos.offset(Direction.SOUTH);
				case WEST:
					return pos.offset(Direction.WEST);

				case NORTHEAST:
					return pos.offset(Direction.NORTH).offset(Direction.EAST);
				case NORTHWEST:
					return pos.offset(Direction.NORTH).offset(Direction.WEST);
				case SOUTHEAST:
					return pos.offset(Direction.SOUTH).offset(Direction.EAST);
				case SOUTHWEST:
					return pos.offset(Direction.SOUTH).offset(Direction.WEST);
				case CENTER:
				default:
					return pos;
			}
		} else {
			switch (position) {

				case NORTH:
					return pos.offset(Direction.SOUTH);
				case EAST:
					return pos.offset(Direction.WEST);
				case SOUTH:
					return pos.offset(Direction.NORTH);
				case WEST:
					return pos.offset(Direction.EAST);

				case NORTHEAST:
					return pos.offset(Direction.SOUTH).offset(Direction.WEST);
				case NORTHWEST:
					return pos.offset(Direction.SOUTH).offset(Direction.EAST);
				case SOUTHEAST:
					return pos.offset(Direction.NORTH).offset(Direction.WEST);
				case SOUTHWEST:
					return pos.offset(Direction.NORTH).offset(Direction.EAST);
				case CENTER:
				default:
					return pos;
			}
		}
	}

	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return PlantType.WATER;
	}

	public static enum LilyPadPosition implements IStringSerializable {
		CENTER("center"),
		NORTH("north"),
		NORTHEAST("northeast"),
		EAST("east"),
		SOUTHEAST("southeast"),
		SOUTH("south"),
		SOUTHWEST("southwest"),
		WEST("west"),
		NORTHWEST("northwest");

		private final String heightName;

		private LilyPadPosition(String nameIn) {
			this.heightName = nameIn;
		}

		public String toString() {
			return this.getString();
		}

		public String getString() {
			return this.heightName;
		}
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		FILLER.fillItem(this.asItem(), group, items);
	}
}