package dev.buildtool.ftechintegration;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.common.register.IEItems;
import com.mojang.logging.LogUtils;
import dev.buildtool.ftech.datageneration.OreDoublerRecipeBuilder;
import dev.buildtool.ftech.datageneration.OreThermalRecipeBuilder;
import mekanism.common.registries.MekanismItems;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import rearth.oritech.init.ItemContent;

import java.util.concurrent.CompletableFuture;

@Mod(FTechIntegration.MODID)
public class FTechIntegration {

    public static final String MODID = "f_tech_integration";

    private static final Logger LOGGER = LogUtils.getLogger();

    public FTechIntegration(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::generateData);
    }

    private void generateData(GatherDataEvent event)
    {
        DataGenerator dataGenerator=event.getGenerator();
        PackOutput packOutput=dataGenerator.getPackOutput();
        var lookupProvider=event.getLookupProvider();

        dataGenerator.addProvider(true, new RecipeProvider(packOutput,lookupProvider) {
            @Override
            protected void buildRecipes(RecipeOutput recipeOutput) {
                //Oritech
                new OreThermalRecipeBuilder(4, Ingredient.of(ItemContent.RAW_NICKEL), new ItemStack(ItemContent.NICKEL_INGOT)).save(recipeOutput);
                new OreThermalRecipeBuilder(4, Ingredient.of(ItemContent.RAW_PLATINUM), new ItemStack(ItemContent.PLATINUM_INGOT)).save(recipeOutput);
                new OreThermalRecipeBuilder(4, Ingredient.of(ItemContent.NICKEL_DUST), new ItemStack(ItemContent.NICKEL_INGOT)).save(recipeOutput, "nickel_ingot_2");
                new OreThermalRecipeBuilder(4, Ingredient.of(ItemContent.PLATINUM_DUST), new ItemStack(ItemContent.PLATINUM_INGOT)).save(recipeOutput, "platinum_ingot_2");
                new OreThermalRecipeBuilder(4, Ingredient.of(ItemContent.IRON_DUST), new ItemStack(Items.IRON_INGOT)).save(recipeOutput, "ot_iron_ingot");
                new OreThermalRecipeBuilder(4, Ingredient.of(ItemContent.GOLD_DUST), new ItemStack(Items.GOLD_INGOT)).save(recipeOutput, "ot_gold_ingot");
                new OreThermalRecipeBuilder(4, Ingredient.of(ItemContent.COPPER_DUST), new ItemStack(Items.COPPER_INGOT)).save(recipeOutput, "ot_copper_ingot");

                //Immersive Engineering
                new OreThermalRecipeBuilder(4, Ingredient.of(IEItems.Metals.RAW_ORES.get(EnumMetals.ALUMINUM)), new ItemStack(IEItems.Metals.INGOTS.get(EnumMetals.ALUMINUM))).save(recipeOutput);
                new OreThermalRecipeBuilder(4, Ingredient.of(IEItems.Metals.RAW_ORES.get(EnumMetals.NICKEL)), new ItemStack(IEItems.Metals.INGOTS.get(EnumMetals.NICKEL))).save(recipeOutput);
                new OreThermalRecipeBuilder(4, Ingredient.of(IEItems.Metals.RAW_ORES.get(EnumMetals.SILVER)), new ItemStack(IEItems.Metals.INGOTS.get(EnumMetals.SILVER))).save(recipeOutput);
                new OreThermalRecipeBuilder(4, Ingredient.of(IEItems.Metals.RAW_ORES.get(EnumMetals.LEAD)), new ItemStack(IEItems.Metals.INGOTS.get(EnumMetals.LEAD))).save(recipeOutput);
                new OreThermalRecipeBuilder(4, Ingredient.of(IEItems.Metals.DUSTS.get(EnumMetals.ALUMINUM)), new ItemStack(IEItems.Metals.INGOTS.get(EnumMetals.ALUMINUM))).save(recipeOutput, "aluminum_ingot");
                new OreThermalRecipeBuilder(4, Ingredient.of(IEItems.Metals.DUSTS.get(EnumMetals.NICKEL)), new ItemStack(IEItems.Metals.INGOTS.get(EnumMetals.NICKEL))).save(recipeOutput, "ie_nickel_ingot");
                new OreThermalRecipeBuilder(4, Ingredient.of(IEItems.Metals.DUSTS.get(EnumMetals.SILVER)), new ItemStack(IEItems.Metals.INGOTS.get(EnumMetals.SILVER))).save(recipeOutput, "silver_ingot");
                new OreThermalRecipeBuilder(4, Ingredient.of(IEItems.Metals.DUSTS.get(EnumMetals.LEAD)), new ItemStack(IEItems.Metals.INGOTS.get(EnumMetals.LEAD))).save(recipeOutput, "lead_ingot");

                //Mekanism
                Item rawLead = MekanismItems.PROCESSED_RESOURCES.get(ResourceType.RAW, PrimaryResource.LEAD).get();
                Item leadDust = MekanismItems.PROCESSED_RESOURCES.get(ResourceType.DUST, PrimaryResource.LEAD).get();
                new OreDoublerRecipeBuilder(rawLead, new ItemStack(leadDust, 2), 1).save(recipeOutput);
                Item rawOsmium = MekanismItems.PROCESSED_RESOURCES.get(ResourceType.RAW, PrimaryResource.OSMIUM).get();
                Item osmiumDust = MekanismItems.PROCESSED_RESOURCES.get(ResourceType.DUST, PrimaryResource.OSMIUM).get();
                new OreDoublerRecipeBuilder(rawOsmium, new ItemStack(osmiumDust, 2), 1).save(recipeOutput);
                Item rawTin = MekanismItems.PROCESSED_RESOURCES.get(ResourceType.RAW, PrimaryResource.TIN).get();
                Item tinDust = MekanismItems.PROCESSED_RESOURCES.get(ResourceType.DUST, PrimaryResource.TIN).get();
                new OreDoublerRecipeBuilder(rawTin, new ItemStack(tinDust, 2), 1).save(recipeOutput);

                Item leadIngot = MekanismItems.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.LEAD).get();
                new OreThermalRecipeBuilder(4, Ingredient.of(rawLead), new ItemStack(leadIngot)).save(recipeOutput);
                new OreThermalRecipeBuilder(4, Ingredient.of(leadDust), new ItemStack(leadIngot)).save(recipeOutput, "lead_ingot_2");
                Item tinIngot = MekanismItems.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.TIN).get();
                new OreThermalRecipeBuilder(4, Ingredient.of(rawTin), new ItemStack(tinIngot)).save(recipeOutput);
                new OreThermalRecipeBuilder(4, Ingredient.of(tinDust), new ItemStack(tinIngot)).save(recipeOutput, "tin_ingot_2");
                Item osmiumIngot = MekanismItems.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.OSMIUM).get();
                new OreThermalRecipeBuilder(4, Ingredient.of(rawOsmium), new ItemStack(osmiumIngot)).save(recipeOutput);
                new OreThermalRecipeBuilder(4, Ingredient.of(osmiumDust), new ItemStack(osmiumIngot)).save(recipeOutput, "osmium_ingot_2");
            }
        });
    }
}
