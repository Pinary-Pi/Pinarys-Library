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

    public List<JsonElement> simpleLayeredBlock(String name, List<String> textures, String modid, String particle) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

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

    public List<JsonElement> simpleLayeredSlab(String name, List<String> textures, String modid, String particle) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

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

/* 

Receive layerCount convert to string for concatenation with file name. 
Assumption: 2,147,483,647 > layerCount > 0 

Example 10 = ba www.ba.com 
String layerCountName = ""
while layerCount > 9 do {
    layerCountName = layerCountName + (char)((layerCount mod 10) + 97)
    layerCount = (int)(layerCount/10)
} 

loop
layerCountName = char(layerCount + 97) + layerCountName;


*/