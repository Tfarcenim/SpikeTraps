package tfar.spiketraps;

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
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpikeBlock extends Block {

  public final float damage;

  public SpikeBlock(Properties p_i48440_1_, float damage) {
    super(p_i48440_1_);
    this.setDefaultState(this.stateContainer.getBaseState().with(PROPERTY_FACING, Direction.NORTH));
    this.damage = damage;
  }

  public static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0, 0, 9, 16, 16, 16);
  public static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0, 0, 0, 16, 16, 7);
  public static final VoxelShape WEST_AABB = Block.makeCuboidShape(9, 0, 0, 16, 16, 16);
  public static final VoxelShape UP_AABB = Block.makeCuboidShape(0, 0, 0, 16, 7, 16);
  public static final VoxelShape EAST_AABB = Block.makeCuboidShape(0, 0, 0, 7, 16, 16);
  public static final VoxelShape DOWN_AABB = Block.makeCuboidShape(0, 9, 0, 16, 16, 16);

  public static final DirectionProperty PROPERTY_FACING = BlockStateProperties.FACING;
  private static final GameProfile PROFILE = new GameProfile(UUID.fromString("a42ac406-c797-4e0e-b147-f01ac5551be5"), "[SpikeTraps]");

  @Override
  public void onEntityWalk(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
    super.onEntityWalk(p_176199_1_, p_176199_2_, p_176199_3_);
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    Direction dir = state.get(PROPERTY_FACING);
    switch (dir) {
      case NORTH:
        return NORTH_AABB;
      case SOUTH:
        return SOUTH_AABB;
      case WEST:
        return WEST_AABB;
      case UP:
        return UP_AABB;
      case EAST:
        return EAST_AABB;
      case DOWN:
      default:
        return DOWN_AABB;
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
    if (world.isRemote || !(entity instanceof LivingEntity) || entity.world.getGameTime() % 10 != 0) return;
    //diamond spike
    if (hasTileEntity(state)) {
      attack((SpikeTile) world.getTileEntity(pos), (LivingEntity) entity);
    } else {
      if (state.getBlock() == SpikeTraps.gold_spike)
        ObfuscationReflectionHelper.setPrivateValue(LivingEntity.class, (LivingEntity) entity, 100, "field_70718_bc");
      if (state.getBlock() == SpikeTraps.wood_spike && ((LivingEntity) entity).getHealth() <= 1) return;
      if (state.getBlock() == SpikeTraps.wood_spike && ((LivingEntity) entity).getHealth() <= 4) {
        entity.attackEntityFrom(DamageSource.CACTUS, ((LivingEntity) entity).getHealth() - 1);
        return;
      }
      entity.attackEntityFrom(DamageSource.CACTUS, this.damage);
    }
  }

  /**
   *
   * @param tile the player attacking (in this case the spike
   * @param targetEntity the entity beeing attacked
   * @see PlayerEntity#attackTargetEntityWithCurrentItem(Entity)
   */
  public void attack(SpikeTile tile,LivingEntity targetEntity) {

    World world = tile.getWorld();

    Map<Enchantment, Integer> ench = tile.getEnchantments();
    ItemStack stick = damage == 9 ? new ItemStack(Items.NETHERITE_SWORD) : new ItemStack(Items.DIAMOND_SWORD);
    EnchantmentHelper.setEnchantments(ench, stick);
    targetEntity.setRevengeTarget(null);

    FakePlayer fakePlayer = new LessCrashProneFakePlayer(FakePlayerFactory.get((ServerWorld) world, PROFILE));
    fakePlayer.setHeldItem(Hand.MAIN_HAND, stick);


    if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(fakePlayer, targetEntity)) return;
    if (targetEntity.canBeAttackedWithItem()) {
      if (!targetEntity.hitByEntity(fakePlayer)) {
        float spikeDamage = damage;

        float damageModifier = EnchantmentHelper.getModifierForCreature(fakePlayer.getHeldItemMainhand(), targetEntity.getCreatureAttribute());

        final float attackStrength = 1;
        damageModifier = damageModifier * attackStrength;
        fakePlayer.resetCooldown();
        if (spikeDamage > 0.0F || damageModifier > 0.0F) {

          spikeDamage = spikeDamage + damageModifier;

          boolean caughtFire = false;
          int fireAspectLevel = EnchantmentHelper.getFireAspectModifier(fakePlayer);
          float targetEntityHealth = targetEntity.getHealth();
          if (fireAspectLevel > 0 && !targetEntity.isBurning()) {
            caughtFire = true;
            targetEntity.setFire(1);
          }

          Vector3d motion = targetEntity.getMotion();
          boolean attackSuccessful = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(fakePlayer), spikeDamage);
          if (attackSuccessful) {

            if (targetEntity instanceof ServerPlayerEntity && targetEntity.velocityChanged) {
              ((ServerPlayerEntity)targetEntity).connection.sendPacket(new SEntityVelocityPacket(targetEntity));
              targetEntity.velocityChanged = false;
              targetEntity.setMotion(motion);
            }

            if (damageModifier > 0.0F) {
              fakePlayer.onEnchantmentCritical(targetEntity);
            }

            fakePlayer.setLastAttackedEntity(targetEntity);
            EnchantmentHelper.applyThornEnchantments(targetEntity, fakePlayer);

            EnchantmentHelper.applyArthropodEnchantments(fakePlayer, targetEntity);

            targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(fakePlayer),spikeDamage);


            float damageDealt = targetEntityHealth - targetEntity.getHealth();
            fakePlayer.addStat(Stats.DAMAGE_DEALT, Math.round(damageDealt * 10.0F));
            if (fireAspectLevel > 0) {
              targetEntity.setFire(fireAspectLevel * 4);
            }

            if (fakePlayer.world instanceof ServerWorld && damageDealt > 2.0F) {
              int k = (int)(damageDealt * 0.5D);
              ((ServerWorld)fakePlayer.world).spawnParticle(ParticleTypes.DAMAGE_INDICATOR, targetEntity.getPosX(), targetEntity.getPosYHeight(0.5D), targetEntity.getPosZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
            }

          } else {
            if (caughtFire) {
              targetEntity.extinguish();
            }
          }
        }
      }
    }
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return state.getBlock() == SpikeTraps.diamond_spike || state.getBlock() == SpikeTraps.netherite_spike;
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
      ((SpikeTile) te).setEnch(EnchantmentHelper.deserializeEnchantments(stack.getEnchantmentTagList()));
    }
  }

  @Mod.EventBusSubscriber(modid = SpikeTraps.MODID)
  public static class EventHandler {
    @SubscribeEvent
    public static void knockback(LivingKnockBackEvent e) {
      if (e.getEntityLiving().getRevengeTarget() instanceof LessCrashProneFakePlayer)
        e.setCanceled(true);
    }
  }
}

