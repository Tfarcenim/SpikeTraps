package com.tfar.spiketraps;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.util.*;

public class SpikeBlock extends Block {

  public final float damage;

  public SpikeBlock(Properties p_i48440_1_, float damage) {
    super(p_i48440_1_);
    this.setDefaultState(this.stateContainer.getBaseState().with(PROPERTY_FACING, Direction.NORTH));
    this.damage = damage;
  }

  public static final DirectionProperty PROPERTY_FACING = BlockStateProperties.FACING;
  private static GameProfile PROFILE = new GameProfile(UUID.fromString("a42ac406-c797-4e0e-b147-f01ac5551be5"), "[SpikeTraps]");

  @Override
  public void onEntityWalk(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
    super.onEntityWalk(p_176199_1_, p_176199_2_, p_176199_3_);
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return this.getCollisionShape(state, worldIn, pos, context);
  }

  @Override
  public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    Direction dir = state.get(PROPERTY_FACING);
    switch (dir) {
      case NORTH:
        return Block.makeCuboidShape(0, 0, 9, 16, 16, 16);
      case SOUTH:
        return Block.makeCuboidShape(0, 0, 0, 16, 16, 7);
      case WEST:
        return Block.makeCuboidShape(9, 0, 0, 16, 16, 16);
      case UP:
        return Block.makeCuboidShape(0, 0, 0, 16, 7, 16);
      case EAST:
        return Block.makeCuboidShape(0, 0, 0, 7, 16, 16);
      case DOWN:
      default:
        return Block.makeCuboidShape(0, 9, 0, 16, 16, 16);
    }
  }

  /**
   * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   *
   * @deprecated call via {@link BlockState#rotate(Rotation)} whenever possible. Implementing/overriding is
   * fine.
   */
  public BlockState rotate(BlockState state, Rotation rot) {
    return state.with(PROPERTY_FACING, rot.rotate(state.get(PROPERTY_FACING)));
  }

  @Override
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  /**
   * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   *
   * @deprecated call via {@link BlockState#mirror(Mirror)} whenever possible. Implementing/overriding is fine.
   */
  public BlockState mirror(BlockState state, Mirror mirrorIn) {
    return state.rotate(mirrorIn.toRotation(state.get(PROPERTY_FACING)));
  }

  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(PROPERTY_FACING);
  }

  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState().with(PROPERTY_FACING, context.getFace());
  }

  @Override
  public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
    if (world.isRemote || !(entity instanceof LivingEntity)) return;
    if (hasTileEntity()) {
      FakePlayer fakePlayer = FakePlayerFactory.get((ServerWorld) world, PROFILE);
      SpikeTile tileEntity = (SpikeTile) world.getTileEntity(pos);
      ItemStack stick = new ItemStack(SpikeTraps.Objects.fake_sword);
      Map<Enchantment, Integer> ench = new HashMap<>();
      ench.put(Enchantments.SHARPNESS, tileEntity.getSharpness());
      ench.put(Enchantments.LOOTING, tileEntity.getLooting());
      EnchantmentHelper.setEnchantments(ench, stick);
      fakePlayer.setHeldItem(Hand.MAIN_HAND, stick);
      fakePlayer.attackTargetEntityWithCurrentItem(entity);
      fakePlayer.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
      ((LivingEntity) entity).setRevengeTarget(null);
    } else {
      if (state.getBlock() == SpikeTraps.Objects.gold_spike)
        ObfuscationReflectionHelper.setPrivateValue(LivingEntity.class, (LivingEntity) entity, 100, "field_70718_bc");
      if (state.getBlock() == SpikeTraps.Objects.wood_spike && ((LivingEntity) entity).getHealth() <= 1) return;
      if (state.getBlock() == SpikeTraps.Objects.wood_spike && ((LivingEntity) entity).getHealth() <= 4) {
        entity.attackEntityFrom(DamageSource.CACTUS, ((LivingEntity) entity).getHealth() - 1);
        return;
      }
      entity.attackEntityFrom(DamageSource.CACTUS, this.damage);
    }
  }

  @Override
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    TileEntity tileEntity = worldIn.getTileEntity(pos);
    if (tileEntity instanceof SpikeTile) {
      SpikeTile spikeTile = (SpikeTile) tileEntity;
      ItemStack item = new ItemStack(this);
      if (spikeTile.getLooting() > 0)
        item.addEnchantment(Enchantments.LOOTING, spikeTile.getLooting());
      if (spikeTile.getSharpness() > 0)
      item.addEnchantment(Enchantments.SHARPNESS,spikeTile.getSharpness());
      ItemEntity entity = new ItemEntity(worldIn, pos.getX() + .5, pos.getY(), pos.getZ() + .5, item);
      worldIn.addEntity(entity);
    }
    super.onReplaced(state, worldIn, pos, newState, isMoving);
  }

  @Override
  public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
    return state.getBlock() == SpikeTraps.Objects.diamond_spike ? Collections.emptyList() : super.getDrops(state, builder);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return state.getBlock() == SpikeTraps.Objects.diamond_spike;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new SpikeTile();
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof SpikeTile && !world.isRemote && placer != null) {
      ((SpikeTile) te).setEnch(EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.LOOTING, placer), EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.SHARPNESS, placer));
      // FakePlayer fakePlayer = FakePlayerFactory.get((ServerWorld) world,PROFILE);
      // for (Slot slot : fakePlayer.inventory.currentItem)
    }
  }

  @Mod.EventBusSubscriber(modid = SpikeTraps.MODID)
  public static class EventHandler {
    @SubscribeEvent
    public static void knockback(LivingKnockBackEvent e) {
      if (e.getAttacker() instanceof FakePlayer && e.getAttacker().getUniqueID().equals(PROFILE.getId()))
        e.setCanceled(true);
    }
  }
}

