package tfar.spiketraps;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpikeTile extends TileEntity {
  private Map<Enchantment,Integer> enchantmentDatas = new HashMap<>();

  public SpikeTile() {
    super(SpikeTraps.spike);
  }

  @Override
  public void read(BlockState state, CompoundNBT nbt) {
    super.read(state,nbt);
    enchantmentDatas = EnchantmentHelper.deserializeEnchantments(nbt.getList("Enchantments", Constants.NBT.TAG_COMPOUND));
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT nbt) {
    super.write(nbt);
    ListNBT listnbt = new ListNBT();

    for(Map.Entry<Enchantment, Integer> entry : enchantmentDatas.entrySet()) {
      Enchantment enchantment = entry.getKey();
      if (enchantment != null) {
        int i = entry.getValue();
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(enchantment)));
        compoundnbt.putShort("lvl", (short)i);
        listnbt.add(compoundnbt);
      }
    }

    if (!listnbt.isEmpty()) {
      nbt.put("Enchantments", listnbt);
    }
    return nbt;
  }

  public void setEnch(Map<Enchantment,Integer> enchantmentDatas) {
    this.enchantmentDatas = enchantmentDatas;
    markDirty();
  }


  public Map<Enchantment,Integer> getEnchantments() {
    return enchantmentDatas;
  }
}
