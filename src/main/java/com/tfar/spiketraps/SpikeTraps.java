package com.tfar.spiketraps;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.HashSet;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SpikeTraps.MODID)
public class SpikeTraps {

  public static final String MODID = "spiketraps";

  /*@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE)
  public static class no {
    @SubscribeEvent
    public static void setup(final ItemTooltipEvent event) {
      event.getToolTip().add(new StringTextComponent(event.getItemStack().getOrCreateTag().toString()));
    }
  }*/


  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {
    private static final Set<Block> MOD_BLOCKS = new HashSet<>();
    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
      // register a new block here
      registerBlock(new SpikeBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(3),4),"wood_spike",event.getRegistry());
      registerBlock(new SpikeBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(6),5),"cobble_spike",event.getRegistry());
      registerBlock(new SpikeBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(6),6),"iron_spike",event.getRegistry());
      registerBlock(new SpikeBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(6),7),"gold_spike",event.getRegistry());
      registerBlock(new SpikeBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(6),8),"diamond_spike",event.getRegistry());
    }
    private static void registerBlock(Block block, String registryname, IForgeRegistry<Block> registry){
      registry.register(block.setRegistryName(registryname));
      MOD_BLOCKS.add(block);
    }

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
      // register a new block here
      event.getRegistry().register(new FakeSword().setRegistryName("fake_sword"));
      MOD_BLOCKS.forEach(block -> event.getRegistry().register(
              new SpikeBlockItem(block, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(block.getRegistryName())));
    }
    @SubscribeEvent
    public static void onTilesRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
      // register a new block here
      event.getRegistry().register(TileEntityType.Builder.create(SpikeTile::new, Objects.diamond_spike).build(null).setRegistryName("spike_tile"));
    }
  }
  @ObjectHolder(MODID)
  public static class Objects {
    public static final TileEntityType<?> spike_tile = null;

    public static final Block wood_spike = null;
    public static final Block cobble_spike = null;
    public static final Block iron_spike = null;
    public static final Block gold_spike = null;
    public static final Block diamond_spike = null;
    public static final Item fake_sword = null;
  }
}
