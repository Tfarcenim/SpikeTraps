package com.tfar.spiketraps;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;

import java.util.Set;

public class SpikeBlockItem extends BlockItem {
  public SpikeBlockItem(Block blockIn, Properties builder) {
    super(blockIn, builder);
  }

  public static final Set<Enchantment> whitelist = Sets.newHashSet(
          Enchantments.SHARPNESS,Enchantments.LOOTING);

  @Override
  public int getItemEnchantability(ItemStack stack) {
    return Block.getBlockFromItem(stack.getItem()) == SpikeTraps.Objects.diamond_spike ? ItemTier.DIAMOND.getEnchantability() : 0;
  }

  @Override
  public boolean isEnchantable(ItemStack stack) {
    return Block.getBlockFromItem(stack.getItem()) == SpikeTraps.Objects.diamond_spike;
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
    return whitelist.contains(enchantment);
  }
}
