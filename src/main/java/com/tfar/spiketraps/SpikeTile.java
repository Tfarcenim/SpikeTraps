package com.tfar.spiketraps;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public class SpikeTile extends TileEntity {
  private int looting = 0;
  private int sharpness = 0;
  public SpikeTile() {
    super(SpikeTraps.Objects.spike_tile);
  }

  @Override
  public void read(CompoundNBT nbt) {
    super.read(nbt);
    looting = nbt.getInt("looting");
    sharpness = nbt.getInt("sharpness");
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT nbt) {
    super.write(nbt);
    nbt.putInt("looting", looting);
    nbt.putInt("sharpness", sharpness);
    return nbt;
  }

  public void setEnch(int looting, int sharpness){
    this.looting = looting;
    this.sharpness = sharpness;
  }

  public int getLooting() {
    return looting;
  }

  public int getSharpness() {
    return sharpness;
  }
}
