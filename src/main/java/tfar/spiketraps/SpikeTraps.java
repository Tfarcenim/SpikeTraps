package tfar.spiketraps;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashSet;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SpikeTraps.MODID)
public class SpikeTraps {

    public static final String MODID = "spiketraps";
    public static final ITag<Enchantment> whitelist = ForgeTagHandler.makeWrapperTag(ForgeRegistries.ENCHANTMENTS,new ResourceLocation(MODID,"whitelisted"));


    public static final Block wood_spike = new SpikeBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(3).notSolid(), 4);
    public static final Block cobblestone_spike = new SpikeBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(3).notSolid(), 5);
    public static final Block iron_spike = new SpikeBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(3).notSolid(), 6);
    public static final Block gold_spike = new SpikeBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(3).notSolid(), 7);
    public static final Block diamond_spike = new SpikeBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(3).notSolid(), 8);
    public static final Block netherite_spike = new SpikeBlock(Block.Properties.create(Material.IRON)
            .hardnessAndResistance(6,3000).notSolid(), 10);

    public static final Item wood_spike_item = new EnchantableBlockItem(wood_spike,new Item.Properties().group(ItemGroup.DECORATIONS),0);
    public static final Item cobblestone_spike_item = new EnchantableBlockItem(cobblestone_spike,new Item.Properties().group(ItemGroup.DECORATIONS),0);
    public static final Item iron_spike_item = new EnchantableBlockItem(iron_spike,new Item.Properties().group(ItemGroup.DECORATIONS),0);
    public static final Item gold_spike_item = new EnchantableBlockItem(gold_spike,new Item.Properties().group(ItemGroup.DECORATIONS),0);
    public static final Item diamond_spike_item = new EnchantableBlockItem(diamond_spike,new Item.Properties().group(ItemGroup.DECORATIONS),10);
    public static final Item netherite_spike_item = new EnchantableBlockItem(netherite_spike,new Item.Properties().group(ItemGroup.DECORATIONS).isImmuneToFire(),25);


    public static final TileEntityType<?> spike = TileEntityType.Builder.create(SpikeTile::new, diamond_spike,netherite_spike).build(null);

    public SpikeTraps() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client);
    }

    public void client(FMLClientSetupEvent e) {
        RegistryEvents.MOD_BLOCKS.forEach(block -> RenderTypeLookup.setRenderLayer(block, RenderType.getCutoutMipped()));
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        static final Set<Block> MOD_BLOCKS = new HashSet<>();

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            // register a new block here
            register(wood_spike, "wood_spike", event.getRegistry());
            register(cobblestone_spike, "cobblestone_spike", event.getRegistry());
            register(iron_spike, "iron_spike", event.getRegistry());
            register(gold_spike, "gold_spike", event.getRegistry());
            register(diamond_spike, "diamond_spike", event.getRegistry());
            register(netherite_spike, "netherite_spike", event.getRegistry());
        }

        @SubscribeEvent
        public static void items(final RegistryEvent.Register<Item> event) {
            // register a new item here
            register(wood_spike_item,"wood_spike",event.getRegistry());
            register(cobblestone_spike_item,"cobblestone_spike",event.getRegistry());
            register(iron_spike_item,"iron_spike",event.getRegistry());
            register(gold_spike_item,"gold_spike",event.getRegistry());
            register(diamond_spike_item,"diamond_spike",event.getRegistry());
            register(netherite_spike_item,"netherite_spike",event.getRegistry());


        }

        @SubscribeEvent
        public static void onTilesRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            // register a new tile here
            register(spike, "spike", event.getRegistry());
        }

        private static <T extends IForgeRegistryEntry<T>> void register(T obj, String name, IForgeRegistry<T> registry) {
            registry.register(obj.setRegistryName(new ResourceLocation(MODID, name)));
            if (obj instanceof Block) MOD_BLOCKS.add((Block) obj);
        }
    }
}
