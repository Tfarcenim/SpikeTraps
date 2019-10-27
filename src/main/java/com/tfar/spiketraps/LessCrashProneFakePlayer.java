package com.tfar.spiketraps;

import com.mojang.authlib.GameProfile;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;

public class LessCrashProneFakePlayer extends FakePlayer {

  public LessCrashProneFakePlayer(ServerWorld world, GameProfile name) {
    super(world, name);
  }

  public LessCrashProneFakePlayer(FakePlayer fakePlayer){
    this((ServerWorld) fakePlayer.world,fakePlayer.getGameProfile());
}

  @Override
  public boolean isPotionApplicable(EffectInstance potioneffectIn) {
    return false;
  }
}
