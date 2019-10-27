package com.tfar.spiketraps;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.HashSet;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SpikeTraps.MODID)
public class SpikeTraps {

  public static final String MODID = "spiketraps";

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {
    private static final Set<Block> MOD_BLOCKS = new HashSet<>();
    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
      // register a new block here
      register(new SpikeBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(3),4),"wood_spike",event.getRegistry());
      register(new SpikeBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(6),5),"cobble_spike",event.getRegistry());
      register(new SpikeBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(6),6),"iron_spike",event.getRegistry());
      register(new SpikeBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(6),7),"gold_spike",event.getRegistry());
      register(new SpikeBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(6),8),"diamond_spike",event.getRegistry());
    }

    @SubscribeEvent
    public static void items(final RegistryEvent.Register<Item> event) {
      // register a new item here
      register(new FakeSword(),"fake_sword",event.getRegistry());
      Item.Properties properties = new Item.Properties().group(ItemGroup.DECORATIONS);
      MOD_BLOCKS.forEach(block -> register(
              new SpikeBlockItem(block, properties),block.getRegistryName().getPath(),event.getRegistry()));
    }

    @SubscribeEvent
    public static void onTilesRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
      // register a new tile here
      register(TileEntityType.Builder.create(SpikeTile::new, Objects.diamond_spike).build(null),"spike_tile",event.getRegistry());
    }

    private static <T extends IForgeRegistryEntry<T>> void register(T obj, String name, IForgeRegistry<T> registry) {
      registry.register(obj.setRegistryName(new ResourceLocation(MODID, name)));
      if (obj instanceof Block) MOD_BLOCKS.add((Block) obj);
    }
  }
  @ObjectHolder(MODID)
  public static class Objects {
    public static final TileEntityType<?> spike_tile = null;

    public static final Block wood_spike = null;
    public static final Block gold_spike = null;
    public static final Block diamond_spike = null;
    public static final Item fake_sword = null;
  }
}
