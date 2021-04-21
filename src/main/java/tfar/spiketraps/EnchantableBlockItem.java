package tfar.spiketraps;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class EnchantableBlockItem extends BlockItem {
  private final int enchantability;

  public EnchantableBlockItem(Block blockIn, Properties builder, int enchantability) {
    super(blockIn, builder);
    this.enchantability = enchantability;
  }

  @Override
  public int getItemEnchantability(ItemStack stack) {
    return enchantability;
  }

  @Override
  public boolean isEnchantable(ItemStack stack) {
    return enchantability > 0;
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
    return SpikeTraps.whitelist.contains(enchantment);
  }
}
