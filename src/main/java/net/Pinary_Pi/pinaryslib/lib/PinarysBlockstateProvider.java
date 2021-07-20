package net.Pinary_Pi.pinaryslib.lib;

import java.io.IOException;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.Pinary_Pi.pinaryslib.lib.Blockstate.MultiPart;
import net.Pinary_Pi.pinaryslib.lib.Blockstate.Variant;
import net.Pinary_Pi.pinaryslib.lib.Blockstate.MultiPart.Apply;
import net.Pinary_Pi.pinaryslib.lib.Blockstate.MultiPart.When;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;

public abstract class PinarysBlockstateProvider implements IDataProvider {
    String modid;
    DataGenerator generator;

    public PinarysBlockstateProvider(String modid, DataGenerator generator) {
        this.modid = modid;
        this.generator = generator;
    }

    protected Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    protected Logger logger = LogManager.getLogger();

    @Override
    public String getName() {
        return "Pinary's Blockstates: " + modid;
    }

    public JsonElement blockstateToJson(Blockstate blockstate) {
        if (blockstate.multiparts != null && blockstate.variants != null) {
            throw new RuntimeException("Cannot create a blockstate with both variants and multiparts.");
        } else {
            if (blockstate.multiparts != null) {
                JsonArray multipartJsonArray = new JsonArray();

                for (MultiPart multipart : blockstate.multiparts) {
                    JsonObject multipartJsonObject = new JsonObject();
                    JsonObject whenJsonObject = new JsonObject();
                    JsonObject applyJsonObject = new JsonObject();

                    whenJsonObject.addProperty(multipart.when.variable, multipart.when.value);

                    applyJsonObject.addProperty("model", multipart.apply.model);

                    if (multipart.apply.uvlock != null) {
                        applyJsonObject.addProperty("uvlock", multipart.apply.uvlock);
                    }
                    
                    if (multipart.apply.x != null) {
                        applyJsonObject.addProperty("x", multipart.apply.x);
                    }

                    if (multipart.apply.y != null) {
                        applyJsonObject.addProperty("y", multipart.apply.y);
                    }

                    multipartJsonObject.add("when", whenJsonObject);
                    multipartJsonObject.add("apply", applyJsonObject);

                    multipartJsonArray.add(multipartJsonObject);
                }
                JsonObject blockstateJson = new JsonObject();

                blockstateJson.add("multipart", multipartJsonArray);
                return blockstateJson;
            } else if (blockstate.variants != null) {
                JsonObject variantsObject = new JsonObject();

                for (Variant variant : blockstate.variants) {
                    JsonObject variantObject = new JsonObject();

                    variantObject.addProperty("model", variant.model);

                    if (variant.uvlock != null) {
                        variantObject.addProperty("uvlock", variant.uvlock);
                    }
                    if (variant.x != null) {
                        variantObject.addProperty("x", variant.x);
                    }
                    if (variant.y != null) {
                        variantObject.addProperty("y", variant.y);
                    }

                    variantsObject.add(variant.condition, variantObject);
                }
                JsonObject blockstateJson = new JsonObject();

                blockstateJson.add("variants", variantsObject);

                return blockstateJson;
            } else {
                throw new RuntimeException("Add a variant or mulitpart.");
            }
        }
    }

    /**
     * Simply returns a blockstate for
     * a block with only one variant
     * 
     * @author <a href="https://github.com/Pinary-Pi/">Pinary-Pi</a>
     * @param block a {@link Block} used to get the name for the blockstate file
     * @return a {@link Blockstate} used by the data generator to generate blockstate files
     * @see Block
     */
    public Blockstate simpleBlock(Block block) {
        String name = block.getRegistryName().getPath();

        return new Blockstate(name).addVariant(
            new Variant("", 
                modid + ":block/" + name));
    }

    /**
     * Simply returns a blockstate for
     * a block with only one variant
     * <p>
     * The model parameter is for referencing
     * models that don't have the name of the block and/or have a 
     * different namespace
     * 
     * @author <a href="https://github.com/Pinary-Pi/">Pinary-Pi</a>
     * @param block a {@link Block} used to get the name for the blockstate file
     * @param model a {@link String}, must include namespace and path, so "modid:block/a_model"
     * @return a {@link Blockstate} used by the data generator to generate blockstate files
     */
    public Blockstate simpleBlock(Block block, String model) {
        String name = block.getRegistryName().getPath();
        
        return new Blockstate(name).addVariant(
            new Variant("", model));
    }

    /**
     * Returns a slab blockstate
     * 
     * @author <a href="https://github.com/Pinary-Pi/">Pinary-Pi</a>
     * @param slab a {@link SlabBlock} used to get the name for the blockstate file
     * @param doubleSlab a {@link Block} which is for when the slab is a double slab
     * @return a {@link Blockstate} used by the data generator to generate blockstate files
     */
    public Blockstate simpleSlab(SlabBlock slab, Block doubleSlab) {
        String name = slab.getRegistryName().getPath();

        return new Blockstate(name)
            .addVariant(new Variant(
                "type=top", 
                modid + ":block/" + name + "_top"))
            .addVariant(new Variant(
                "type=bottom", 
                modid + ":block/" + name))
            .addVariant(new Variant(
                "type=double", 
                doubleSlab.getRegistryName().toString().replace(":", ":block/")));
    }

    /**
     * Returns a slab blockstate
     * <p>
     * The model parameter is for referencing
     * models that don't have the name of the slab and/or have a 
     * different namespace
     * <p>
     * The function does assume that the model given
     * is not the bottom slab model and that the top
     * model has the same name, but with "_top" added.
     * 
     * @author <a href="https://github.com/Pinary-Pi/">Pinary-Pi</a>
     * @param slab a {@link SlabBlock} used to get the name for the blockstate file
     * @param doubleSlab a {@link Block} which is for when the slab is a double slab
     * @param model a {@link String}, must include namespace and path, so "modid:block/a_model"
     * @return a {@link Blockstate} used by the data generator to generate blockstate files 
     */
    public Blockstate simpleSlab(SlabBlock slab, Block doubleSlab, String model) {
        String name = slab.getRegistryName().getPath();

        return new Blockstate(name)
            .addVariant(new Variant(
                "type=top", 
                model + "_top"))
            .addVariant(new Variant(
                "type=bottom", 
                model))
            .addVariant(new Variant(
                "type=double", 
                doubleSlab.getRegistryName().toString().replace(":", ":block/")));
    }

    /**
     * Returns a blockstate for a stair block.
     * <p>
     * The model parameter is for referencing
     * models that don't have the name of the block and/or have a 
     * different namespace
     * <p>
     * The function does assume that the models
     * are block_name, block_name_inner, block_name_outer
     * 
     * @author <a href="https://github.com/Pinary-Pi/">Pinary-Pi</a>
     * @param stair a {@link StairsBlock} used to get the name of the block
     * @return a {@link Blockstate} used by the data generator to generate blockstate files
     */
    public Blockstate simpleStair(StairsBlock stair) {
        String name = stair.getRegistryName().getPath();

        String model = modid + ":block/" + name;
        String inner = model + "_inner";
        String outer = model + "_outer";

        return new Blockstate(name)
            .addVariant(new Variant(
                "facing=north,half=top,shape=straight", 
                model)
                .xRotation(180)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=top,shape=straight", 
                model)
                .xRotation(180)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=top,shape=straight", 
                model)
                .xRotation(180)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=top,shape=straight", 
                model)
                .xRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=north,half=bottom,shape=straight", 
                model)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=bottom,shape=straight", 
                model)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=bottom,shape=straight", 
                model)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=bottom,shape=straight", 
                model))
            .addVariant(new Variant(
                "facing=north,half=top,shape=inner_left", 
                inner)
                .xRotation(180)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=top,shape=inner_left", 
                inner)
                .xRotation(180)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=top,shape=inner_left", 
                inner)
                .xRotation(180)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=top,shape=inner_left", 
                inner)
                .xRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=north,half=bottom,shape=inner_left", 
                inner)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=bottom,shape=inner_left", 
                inner))
            .addVariant(new Variant(
                "facing=west,half=bottom,shape=inner_left", 
                inner)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=bottom,shape=inner_left", 
                inner)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=north,half=top,shape=inner_right", 
                inner)
                .xRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=top,shape=inner_right", 
                inner)
                .xRotation(180)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=top,shape=inner_right", 
                inner)
                .xRotation(180)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=top,shape=inner_right", 
                inner)
                .xRotation(180)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=north,half=bottom,shape=inner_right", 
                inner)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=bottom,shape=inner_right", 
                inner)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=bottom,shape=inner_right", 
                inner)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=bottom,shape=inner_right", 
                inner))
            .addVariant(new Variant(
                "facing=north,half=top,shape=outer_left", 
                outer)
                .xRotation(180)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=top,shape=outer_left", 
                outer)
                .xRotation(180)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=top,shape=outer_left", 
                outer)
                .xRotation(180)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=top,shape=outer_left", 
                outer)
                .xRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=north,half=bottom,shape=outer_left", 
                outer)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=bottom,shape=outer_left", 
                outer))
            .addVariant(new Variant(
                "facing=west,half=bottom,shape=outer_left", 
                outer)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=bottom,shape=outer_left", 
                outer)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=north,half=top,shape=outer_right", 
                outer)
                .xRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=top,shape=outer_right", 
                outer)
                .xRotation(180)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=top,shape=outer_right", 
                outer)
                .xRotation(180)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=top,shape=outer_right", 
                outer)
                .xRotation(180)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=north,half=bottom,shape=outer_right", 
                outer)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=bottom,shape=outer_right", 
                outer)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=bottom,shape=outer_right", 
                outer)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=bottom,shape=outer_right", 
                outer));
    }

    /**
     * Returns a blockstate for a stair block
     * <p>
     * The function does assume that the model given
     * is not the inner or outer model and that the inner and 
     * outer models have the same name with an add "_inner" or
     * "_outer" added
     * 
     * @author <a href="https://github.com/Pinary-Pi/">Pinary-Pi</a>
     * @param stair a {@link StairsBlock} used to get the name of the block
     * @param model a {@link String}, must include namespace and path, so "modid:block/a_model"
     * @return a {@link Blockstate} used by the data generator to generate blockstate files
     */
    public Blockstate simpleStair(StairsBlock stair, String model) {
        String name = stair.getRegistryName().getPath();

        String inner = model + "_inner";
        String outer = model + "_outer";

        return new Blockstate(name)
            .addVariant(new Variant(
                "facing=north,half=top,shape=straight", 
                model)
                .xRotation(180)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=top,shape=straight", 
                model)
                .xRotation(180)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=top,shape=straight", 
                model)
                .xRotation(180)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=top,shape=straight", 
                model)
                .xRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=north,half=bottom,shape=straight", 
                model)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=bottom,shape=straight", 
                model)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=bottom,shape=straight", 
                model)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=bottom,shape=straight", 
                model))
            .addVariant(new Variant(
                "facing=north,half=top,shape=inner_left", 
                inner)
                .xRotation(180)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=top,shape=inner_left", 
                inner)
                .xRotation(180)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=top,shape=inner_left", 
                inner)
                .xRotation(180)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=top,shape=inner_left", 
                inner)
                .xRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=north,half=bottom,shape=inner_left", 
                inner)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=bottom,shape=inner_left", 
                inner))
            .addVariant(new Variant(
                "facing=west,half=bottom,shape=inner_left", 
                inner)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=bottom,shape=inner_left", 
                inner)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=north,half=top,shape=inner_right", 
                inner)
                .xRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=top,shape=inner_right", 
                inner)
                .xRotation(180)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=top,shape=inner_right", 
                inner)
                .xRotation(180)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=top,shape=inner_right", 
                inner)
                .xRotation(180)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=north,half=bottom,shape=inner_right", 
                inner)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=bottom,shape=inner_right", 
                inner)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=bottom,shape=inner_right", 
                inner)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=bottom,shape=inner_right", 
                inner))
            .addVariant(new Variant(
                "facing=north,half=top,shape=outer_left", 
                outer)
                .xRotation(180)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=top,shape=outer_left", 
                outer)
                .xRotation(180)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=top,shape=outer_left", 
                outer)
                .xRotation(180)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=top,shape=outer_left", 
                outer)
                .xRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=north,half=bottom,shape=outer_left", 
                outer)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=bottom,shape=outer_left", 
                outer))
            .addVariant(new Variant(
                "facing=west,half=bottom,shape=outer_left", 
                outer)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=bottom,shape=outer_left", 
                outer)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=north,half=top,shape=outer_right", 
                outer)
                .xRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=top,shape=outer_right", 
                outer)
                .xRotation(180)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=top,shape=outer_right", 
                outer)
                .xRotation(180)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=top,shape=outer_right", 
                outer)
                .xRotation(180)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=north,half=bottom,shape=outer_right", 
                outer)
                .yRotation(270)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=south,half=bottom,shape=outer_right", 
                outer)
                .yRotation(90)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=west,half=bottom,shape=outer_right", 
                outer)
                .yRotation(180)
                .uvlock(true))
            .addVariant(new Variant(
                "facing=east,half=bottom,shape=outer_right", 
                outer));
    }

    /**
     * Returns a blockstate for a wall block
     * <p>
     * The function does assume that the models
     * are block_name_post, block_name_side, block_name_side_tall
     * 
     * 
     * @author <a href="https://github.com/Pinary-Pi/">Pinary-Pi</a>
     * @param wall a {@link WallBlock} used to get the name of the block
     * @return a {@link Blockstate} used by the data generator to generate blockstate files
     */
    public Blockstate simpleWall(WallBlock wall) {
        String name = wall.getRegistryName().getPath();

        String post = modid + ":block/" + name + "_post";
        String side = modid + ":block/" + name + "_side";
        String tall = side + "_tall";

        return new Blockstate(name)
            .addMultiPart(new MultiPart(
                new Apply(post), 
                new When("up", "true")))
            .addMultiPart(new MultiPart(
                new Apply(side)
                    .yRotation(90)
                    .uvlock(true),
                new When("east", "low")))
            .addMultiPart(new MultiPart(
                new Apply(tall)
                    .yRotation(90)
                    .uvlock(true), 
                new When("east", "tall")))
            .addMultiPart(new MultiPart(
                new Apply(side)
                    .uvlock(true), 
                new When("north", "low")))
            .addMultiPart(new MultiPart(
                new Apply(tall)
                    .uvlock(true), 
                new When("north", "tall")))
            .addMultiPart(new MultiPart(
                new Apply(side)
                    .yRotation(180)
                    .uvlock(true),
                new When("south", "low")))
            .addMultiPart(new MultiPart(
                new Apply(tall)
                    .yRotation(180)
                    .uvlock(true), 
                new When("south", "tall")))
            .addMultiPart(new MultiPart(
                new Apply(side)
                    .yRotation(270)
                    .uvlock(true), 
                new When("west", "low")))
            .addMultiPart(new MultiPart(
                new Apply(tall)
                    .yRotation(270)
                    .uvlock(true), 
                new When("west", "tall")));
    }

    /**
     * Returns a blockstate for a wall block
     * 
     * @author <a href="https://github.com/Pinary-Pi/">Pinary-Pi</a>
     * @param wall a {@link WallBlock} used to get the name of the block
     * @param postModel a {@link String} in the format modid:block/post_model
     * @param sideModel a {@link String} in the format modid:block/side_model
     * @param sideTallModel a {@link String} in the format modid:block/side_tall_model
     * @return a {@link Blockstate} used by the data generator to generate blockstate files
     */
    public Blockstate simpleWall(WallBlock wall, String postModel, String sideModel, String sideTallModel) {
        String name = wall.getRegistryName().getPath();

        String post = postModel;
        String side = sideModel;
        String tall = sideTallModel;

        return new Blockstate(name)
            .addMultiPart(new MultiPart(
                new Apply(post), 
                new When("up", "true")))
            .addMultiPart(new MultiPart(
                new Apply(side)
                    .yRotation(90)
                    .uvlock(true),
                new When("east", "low")))
            .addMultiPart(new MultiPart(
                new Apply(tall)
                    .yRotation(90)
                    .uvlock(true), 
                new When("east", "tall")))
            .addMultiPart(new MultiPart(
                new Apply(side)
                    .uvlock(true), 
                new When("north", "low")))
            .addMultiPart(new MultiPart(
                new Apply(tall)
                    .uvlock(true), 
                new When("north", "tall")))
            .addMultiPart(new MultiPart(
                new Apply(side)
                    .yRotation(180)
                    .uvlock(true),
                new When("south", "low")))
            .addMultiPart(new MultiPart(
                new Apply(tall)
                    .yRotation(180)
                    .uvlock(true), 
                new When("south", "tall")))
            .addMultiPart(new MultiPart(
                new Apply(side)
                    .yRotation(270)
                    .uvlock(true), 
                new When("west", "low")))
            .addMultiPart(new MultiPart(
                new Apply(tall)
                    .yRotation(270)
                    .uvlock(true), 
                new When("west", "tall")));
    }

    /**
     * When overrided, this must return an array of blockstates,
     * which are then turned into .json files and generated by the
     * data generator.
     * 
     * <a href="https://gist.github.com/Pinary-Pi/645998ecc53b91233c707fa8ef5fdb50">Here</a>
     * is an example of how to use it
     * 
     * @author <a href="https://github.com/Pinary-Pi/">Pinary-Pi</a>
     * @return a {@link Blockstate}
     */
    protected abstract Blockstate[] registerBlockstates();

    private void saveBlockstates(Blockstate[] blockstates, DirectoryCache cache) {
        for (Blockstate blockstate : blockstates) {
            JsonElement jsonElement = blockstateToJson(blockstate);

            String name = blockstate.name;
            Path mainOutput = generator.getOutputFolder();
            String pathSuffix = "assets/" + modid + "/blockstates/" + name + ".json";
            Path outputPath = mainOutput.resolve(pathSuffix);

            try {
                IDataProvider.save(gson, cache, jsonElement, outputPath);
            } catch (Exception e) {
                logger.error("Couldn't save blockstate to {}", outputPath, e);
            }
        }
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        saveBlockstates(registerBlockstates(), cache);
    }
}
