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
