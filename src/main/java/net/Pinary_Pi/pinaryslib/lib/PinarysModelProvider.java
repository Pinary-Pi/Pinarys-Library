package net.Pinary_Pi.pinaryslib.lib;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.Pinary_Pi.pinaryslib.lib.BlockModel.Elements;
import net.Pinary_Pi.pinaryslib.lib.BlockModel.Faces;
import net.Pinary_Pi.pinaryslib.lib.BlockModel.Faces.Side;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;

public abstract class PinarysModelProvider implements IDataProvider {
    String modid;
    DataGenerator generator;

    public PinarysModelProvider(String modid, DataGenerator generator) {
        this.modid = modid;
        this.generator = generator;
    }

    protected Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    protected Logger logger = LogManager.getLogger();

    private JsonObject blockBases = new JsonObject();

    public List<JsonElement> simpleLayeredBlock(String name, List<String> textures, String particle) {
        List<JsonElement> jsonElements = new ArrayList<>();

        List<Elements> elements = new ArrayList<>();

        Integer[] from = { 0, 0, 0 };
        Integer[] to = { 16, 16, 16 };

        String baseName = PinarysGeneratorHelper.intToLetters(textures.size());

        Integer x = 0;

        if (blockBases.get(baseName + "layer_block") == null) {
            for (@SuppressWarnings("unused")
            String texture : textures) {
                if (x == 0) {
                    Side sourceBlock = new Side("#sourceblock");
                    Faces facesSourceBlock = new Faces(sourceBlock);

                    elements.add(new Elements(from, to, facesSourceBlock));

                    x++;
                } else {
                    String xString = PinarysGeneratorHelper.intToLetters(x);

                    Side overlay = new Side("#overlay" + xString);
                    Faces facesOverlay = new Faces(overlay);

                    elements.add(new Elements(from, to, facesOverlay));

                    x++;
                }
            }

            BlockModel baseBlock = new BlockModel("block/block", elements, baseName + "_layer_block_base");

            System.out.println("Made Block Base");

            JsonElement modelElement = gson.toJsonTree(baseBlock);

            jsonElements.add(modelElement);
        } else {
            jsonElements.add(null);

            System.out.println("Block Base was already made!");
        }

        blockBases.addProperty(baseName + "layer_block", true);

        x = 0;

        JsonObject innerJsonObject = new JsonObject();

        for (String texture : textures) {
            if (x == 0) {
                x++;
            } else {
                String xString = PinarysGeneratorHelper.intToLetters(x);

                BlockModel modelBlock = new BlockModel(
                        modid + ":block/" + baseName + "_layer_block_base", name);
                JsonElement jsonElement = gson.toJsonTree(modelBlock);

                innerJsonObject.addProperty("overlay" + xString, modid + ":block/" + texture);

                x++;

                if (x == textures.size()) {

                    innerJsonObject.addProperty("sourceblock", modid + ":block/" + textures.get(0));
                    innerJsonObject.addProperty("particle", modid + ":block/" + particle);

                    jsonElement.getAsJsonObject().add("textures", innerJsonObject);

                    jsonElements.add(jsonElement);

                    System.out.println("Made Block Model");
                }
            }
        }
        return jsonElements;
    }

    public List<JsonElement> simpleLayeredSlab(String name, List<String> textures, String particle) {
        List<JsonElement> jsonElements = new ArrayList<>();

        Integer[] fromTop = {0, 8, 0};
        Integer[] toTop = {16, 16, 16};

        Integer[] fromBottom = {0, 0, 0};
        Integer[] toBottom = {16, 8, 16};

        String baseName = PinarysGeneratorHelper.intToLetters(textures.size());

        List<Elements> elementsTop = new ArrayList<>();
        List<Elements> elementsBottom = new ArrayList<>();

        Integer x = 0;

        if (blockBases.get(baseName + "layer_slab") == null) {
            for (@SuppressWarnings("unused")
            String texture : textures) {
                if (x == 0) {
                    Side sourceBlock = new Side("#sourceblock");

                    Faces faces = new Faces(sourceBlock);

                    elementsTop.add(new Elements(fromTop, toTop, faces));
                    elementsBottom.add(new Elements(fromBottom, toBottom, faces));

                    x++;
                } else {
                    String xString = PinarysGeneratorHelper.intToLetters(x);

                    Side overlay = new Side("#overlay" + xString);

                    Faces faces = new Faces(overlay);

                    elementsTop.add(new Elements(fromTop, toTop, faces));
                    elementsBottom.add(new Elements(fromBottom, toBottom, faces));

                    x++;
                }
            }

            BlockModel slabTop = new BlockModel("block/block", elementsTop, baseName + "_layered_slab_top");
            BlockModel slabBottom = new BlockModel("block/block", elementsBottom, baseName + "_layered_slab");

            JsonElement slabTopElement = gson.toJsonTree(slabTop);
            JsonElement slabBottomElement = gson.toJsonTree(slabBottom);

            jsonElements.add(slabTopElement);
            jsonElements.add(slabBottomElement);

            blockBases.addProperty(baseName + "layer_slab", true);

            System.out.println("Made Slab Bases");
        } else {
            jsonElements.add(null);

            System.out.println("Slab Bases already made");
        }

        x = 0;

        JsonObject innerTexturesJsonObject = new JsonObject();

        for (String texture : textures) {
            if (x == 0) {
                x++;
            } else {
                String xString = PinarysGeneratorHelper.intToLetters(x);

                BlockModel slabTopModel = new BlockModel(modid + ":block/" + baseName + "_layered_slab_top", name + "_top");
                BlockModel slabBottomModel = new BlockModel(modid + ":block/" + baseName + "_layered_slab", name);

                JsonElement slabTopModelElement = gson.toJsonTree(slabTopModel);
                JsonElement slabBottomModelElement = gson.toJsonTree(slabBottomModel);

                innerTexturesJsonObject.addProperty("overlay" + xString, modid + ":block/" + texture);

                x++;

                if (x == textures.size()) {
                    innerTexturesJsonObject.addProperty("sourceblock", modid + ":block/" + textures.get(0));
                    innerTexturesJsonObject.addProperty("particle", modid + ":block/" + particle);

                    slabTopModelElement.getAsJsonObject().add("textures", innerTexturesJsonObject);
                    slabBottomModelElement.getAsJsonObject().add("textures", innerTexturesJsonObject);

                    jsonElements.add(slabTopModelElement);
                    jsonElements.add(slabBottomModelElement);

                    System.out.println("Slab Models Made");
                }
            }
        }
        return jsonElements;
    }

    public List<JsonElement> simpleLayeredStairs(String name, List<String> textures, String particle) {
        List<JsonElement> jsonElements = new ArrayList<>();

        // Stairs elements' from and to
        Integer[] stairsElementOneFrom = {0, 0, 0};
        Integer[] stairsElementOneTo = {16, 8, 16};
        Integer[] stairsElementTwoFrom = {8, 8, 0};
        Integer[] stairsElementTwoTo = {16, 16, 16};
        
        // Inner Stairs elements' from and to
        Integer[] innerStairsElementOneFrom = {0, 0, 0};
        Integer[] innerStairsElementOneTo = {16, 8, 16};
        Integer[] innerStairsElementTwoFrom = {8, 8, 0};
        Integer[] innerStairsElementTwoTo = {16, 16, 16};
        Integer[] innerStairsElementThreeFrom = {0, 8, 8};
        Integer[] innerStairsElementThreeTo = {8, 16, 16};

        // Outer Stairs elements' from and to
        Integer[] outerStairsElementOneFrom = {0, 0, 0};
        Integer[] outerStairsElementOneTo = {16, 8, 16};
        Integer[] outerStairsElementTwoFrom = {8, 8, 8};
        Integer[] outerStairsElementTwoTo = {16, 16, 16};

        String baseName = PinarysGeneratorHelper.intToLetters(textures.size());

        List<Elements> elementsStairs = new ArrayList<>();
        List<Elements> elementsInnerStairs = new ArrayList<>();
        List<Elements> elementsOuterStairs = new ArrayList<>();

        Integer x = 0;

        if (blockBases.get(baseName + "layer_stair") == null) {
            for (@SuppressWarnings("unused")
            String texture : textures) {
                if (x == 0) {
                    Side side = new Side("#sourceblock");

                    Faces sourceblock = new Faces(side);

                    elementsStairs.add(new Elements(stairsElementOneFrom, stairsElementOneTo, sourceblock));
                    elementsStairs.add(new Elements(stairsElementTwoFrom, stairsElementTwoTo, sourceblock));

                    elementsInnerStairs.add(new Elements(innerStairsElementOneFrom, innerStairsElementOneTo, sourceblock));
                    elementsInnerStairs.add(new Elements(innerStairsElementTwoFrom, innerStairsElementTwoTo, sourceblock));
                    elementsInnerStairs.add(new Elements(innerStairsElementThreeFrom, innerStairsElementThreeTo, sourceblock));

                    elementsOuterStairs.add(new Elements(outerStairsElementOneFrom, outerStairsElementOneTo, sourceblock));
                    elementsOuterStairs.add(new Elements(outerStairsElementTwoFrom, outerStairsElementTwoTo, sourceblock));

                    x++;
                } else {
                    String xString = PinarysGeneratorHelper.intToLetters(x);

                    Side side = new Side("#overlay" + xString);

                    Faces overlay = new Faces(side);

                    elementsStairs.add(new Elements(stairsElementOneFrom, stairsElementOneTo, overlay));
                    elementsStairs.add(new Elements(stairsElementTwoFrom, stairsElementTwoTo, overlay));

                    elementsInnerStairs.add(new Elements(innerStairsElementOneFrom, innerStairsElementOneTo, overlay));
                    elementsInnerStairs.add(new Elements(innerStairsElementTwoFrom, innerStairsElementTwoTo, overlay));
                    elementsInnerStairs.add(new Elements(innerStairsElementThreeFrom, innerStairsElementThreeTo, overlay));

                    elementsOuterStairs.add(new Elements(outerStairsElementOneFrom, outerStairsElementOneTo, overlay));
                    elementsOuterStairs.add(new Elements(outerStairsElementTwoFrom, outerStairsElementTwoTo, overlay));

                    x++;
                }
            }
            BlockModel stairsBase = new BlockModel("minecraft:block/stairs", elementsStairs, baseName + "_layered_stairs");
            BlockModel innerStairsBase = new BlockModel("block/block", elementsInnerStairs, baseName + "_layered_inner_stairs");
            BlockModel outerStairsBase = new BlockModel("block/block", elementsOuterStairs, baseName + "_layered_outer_stairs");

            JsonElement stairsElement = gson.toJsonTree(stairsBase);
            JsonElement innerStairsElement = gson.toJsonTree(innerStairsBase);
            JsonElement outerStairsElement = gson.toJsonTree(outerStairsBase);

            jsonElements.add(stairsElement);
            jsonElements.add(innerStairsElement);
            jsonElements.add(outerStairsElement);

            System.out.println("Made Stairs Bases");

            blockBases.addProperty(baseName + "layer_stair", true);
        } else {
            jsonElements.add(null);

            System.out.println("Stairs Bases were already made");
        }
        x = 0;

        JsonObject innerJsonTextures = new JsonObject();
        for (String texture : textures) {
            if (x == 0) {
                x++;
            } else {
                String xString = PinarysGeneratorHelper.intToLetters(x);

                BlockModel stairsModel = new BlockModel(modid + ":block/" + baseName + "_layered_stairs", name);
                BlockModel innerStairsModel = new BlockModel(modid + ":block/" + baseName + "_layered_inner_stairs", name + "_inner");
                BlockModel outerStairsModel = new BlockModel(modid + ":block/" + baseName + "_layered_outer_stairs", name + "_outer");

                JsonElement stairsElement = gson.toJsonTree(stairsModel);
                JsonElement innerStairsElement = gson.toJsonTree(innerStairsModel);
                JsonElement outerStairsElement = gson.toJsonTree(outerStairsModel);

                innerJsonTextures.addProperty("overlay" + xString, modid + ":block/" + texture);

                x++;

                if (x == textures.size()) {
                    innerJsonTextures.addProperty("sourceblock", modid + ":block/" + textures.get(0));
                    innerJsonTextures.addProperty("particle", modid + ":block/" + particle);

                    stairsElement.getAsJsonObject().add("textures", innerJsonTextures);
                    innerStairsElement.getAsJsonObject().add("textures", innerJsonTextures);
                    outerStairsElement.getAsJsonObject().add("textures", innerJsonTextures);

                    jsonElements.add(stairsElement);
                    jsonElements.add(innerStairsElement);
                    jsonElements.add(outerStairsElement);

                    System.out.println("Made stair models");
                }
            }
        }
        return jsonElements;
    }

    public List<JsonElement> simpleLayeredWall(String name, List<String> textures, String particle) {
        List<JsonElement> jsonElements = new ArrayList<>();

        Integer[] wallPostFrom = {4, 0, 4};
        Integer[] wallPostTo = {12, 16, 12};

        Integer[] wallSideFrom = {5, 0, 0};
        Integer[] wallSideTo = {11, 14, 8};

        Integer[] wallTallFrom = {5, 0, 0};
        Integer[] wallTallTo = {11, 16, 8};

        String baseName = PinarysGeneratorHelper.intToLetters(textures.size());

        List<Elements> wallPostElements = new ArrayList<>();
        List<Elements> wallSideElements = new ArrayList<>();
        List<Elements> wallTallElements = new ArrayList<>();

        Integer x = 0;
        if (blockBases.get(baseName + "layer_wall") == null) {
            for (@SuppressWarnings("unused")
            String texture : textures) {
                if (x == 0) {
                    Side side = new Side("#sourceblock");

                    Faces sourceblock = new Faces(side);

                    Elements wallPostElement = new Elements(wallPostFrom, wallPostTo, sourceblock);
                    Elements wallSideElement = new Elements(wallSideFrom, wallSideTo, sourceblock);
                    Elements wallTallElement = new Elements(wallTallFrom, wallTallTo, sourceblock);

                    wallPostElements.add(wallPostElement);
                    wallSideElements.add(wallSideElement);
                    wallTallElements.add(wallTallElement);

                    x++;
                } else {
                    String xString = PinarysGeneratorHelper.intToLetters(x);

                    Side side = new Side("#overlay" + xString);

                    Faces overlay = new Faces(side);

                    Elements wallPostElement = new Elements(wallPostFrom, wallPostTo, overlay);
                    Elements wallSideElement = new Elements(wallSideFrom, wallSideTo, overlay);
                    Elements wallTallElement = new Elements(wallTallFrom, wallTallTo, overlay);

                    wallPostElements.add(wallPostElement);
                    wallSideElements.add(wallSideElement);
                    wallTallElements.add(wallTallElement);

                    x++;
                }
            }
            BlockModel wallPostBase = new BlockModel("block/block", wallPostElements, baseName + "_layered_wall_post");
            BlockModel wallSideBase = new BlockModel("block/block", wallSideElements, baseName + "_layered_wall_side");
            BlockModel wallTallBase = new BlockModel("block/block", wallTallElements, baseName + "_layered_wall_side_tall");

            JsonElement wallPostElement = gson.toJsonTree(wallPostBase);
            JsonElement wallSideElement = gson.toJsonTree(wallSideBase);
            JsonElement wallTallElement = gson.toJsonTree(wallTallBase);

            jsonElements.add(wallPostElement);
            jsonElements.add(wallSideElement);
            jsonElements.add(wallTallElement);

            System.out.println("Made wall bases");
        } else {
            jsonElements.add(null);

            System.out.println("Wall bases were already made!");
        }
        x = 0;

        JsonObject innerJsonTextures = new JsonObject();

        for (String texture : textures) {
            if (x == 0) {
                x++;
            } else {
                String xString = PinarysGeneratorHelper.intToLetters(x);

                BlockModel wallPostModel = new BlockModel(modid + ":block/" + baseName + "_layered_wall_post", name + "_post");
                BlockModel wallSideModel = new BlockModel(modid + ":block/" + baseName + "_layered_wall_side", name + "_side");
                BlockModel wallTallModel = new BlockModel(modid + ":block/" + baseName + "_layered_wall_side_tall", name + "_side_tall");

                JsonElement wallPostElement = gson.toJsonTree(wallPostModel);
                JsonElement wallSideElement = gson.toJsonTree(wallSideModel);
                JsonElement wallTallElement = gson.toJsonTree(wallTallModel);

                innerJsonTextures.addProperty("overlay" + xString, modid + ":block/" + texture);

                x++;

                if (x == textures.size()) {
                    innerJsonTextures.addProperty("sourceblock", modid + ":block/" + textures.get(0));
                    innerJsonTextures.addProperty("particle", modid + ":block/" + particle);

                    wallPostElement.getAsJsonObject().add("textures", innerJsonTextures);
                    wallSideElement.getAsJsonObject().add("textures", innerJsonTextures);
                    wallTallElement.getAsJsonObject().add("textures", innerJsonTextures);

                    jsonElements.add(wallPostElement);
                    jsonElements.add(wallSideElement);
                    jsonElements.add(wallTallElement);

                    System.out.println("Made wall models");
                }
            }
        }
        return jsonElements;
    }

    @Override
    public String getName() {
        return "Pinary's Block Models: " + modid;
    }

    protected abstract List<JsonElement>[] registerModels();

    private void saveModels(List<JsonElement>[] registeredModels, DirectoryCache cache) {
        for (List<JsonElement> jsonElementList : registeredModels) {
            for (JsonElement jsonElement : jsonElementList) {
                if (jsonElement != null) {
                    String name = jsonElement.getAsJsonObject().get("name").getAsString();
                    Path mainOutput = generator.getOutputFolder();
                    String pathSuffix = "assets/" + modid + "/models/block/" + name + ".json";
                    Path outputPath = mainOutput.resolve(pathSuffix);

                    try {
                        IDataProvider.save(gson, cache, jsonElement, outputPath);
                    } catch (IOException e) {
                        logger.error("Couldn't save model to {}", outputPath, e);
                    }
                }
            }
        }
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        saveModels(registerModels(), cache);
    }

    public List<String> textures(String ... textures) {
        List<String> texturesList = new ArrayList<>();

        for (String texture : textures) {
            texturesList.add(texture);
        }

        return texturesList;
    }
}