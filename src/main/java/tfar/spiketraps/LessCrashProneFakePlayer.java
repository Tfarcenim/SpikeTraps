package tfar.spiketraps;

import com.mojang.authlib.GameProfile;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.OptionalInt;

public class LessCrashProneFakePlayer extends FakePlayer {

  public LessCrashProneFakePlayer(ServerWorld world, GameProfile name) {
    super(world, name);
  }

  public LessCrashProneFakePlayer(FakePlayer fakePlayer){
    this((ServerWorld) fakePlayer.world,fakePlayer.getGameProfile());
}

  @Override
  protected void playEquipSound(ItemStack stack) {
    //no
  }

  @Override
  public OptionalInt openContainer(@Nullable INamedContainerProvider containerProvider) {
    return OptionalInt.empty();
  }

  @Override
  public boolean isPotionApplicable(EffectInstance potioneffectIn) {
    return false;
  }
}
