package com.tfar.spiketraps;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.util.DamageSource;

public class FakeSword extends SwordItem {
  public FakeSword() {
    super(ItemTier.DIAMOND, (int)ItemTier.DIAMOND.getAttackDamage(), 1000, new Properties());
  }
  @Override
  public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    target.attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) attacker), getAttackDamage() + EnchantmentHelper.getModifierForCreature(stack, target.getCreatureAttribute()));
    return true;
  }
}
