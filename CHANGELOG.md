# Changelog
## v1.0.1
### Adds:
The ability to create a block with theoretically infinite layers.

Example
```java
package net.Pinary_Pi.pinaryslib.data.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;

import net.Pinary_Pi.pinaryslib.pinaryslib;
import net.Pinary_Pi.pinaryslib.lib.PinarysModelProvider;
import net.minecraft.data.DataGenerator;

public class ModModelProvider extends PinarysModelProvider {
    private String modid = pinaryslib.MOD_ID;

    public ModModelProvider(DataGenerator generator) {
        super(examplemod.MOD_ID, generator);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<JsonElement>[] registerModels() {
        List<String> test_block = new ArrayList<>();
        test_block.add("examplemod:block/texture_1");
        test_block.add("examplemod:block/texture_0");

        @SuppressWarnings("rawtypes")
        List[] array =  {
            simpleLayeredBlock("test_block", test_block, modid, "examplemod:block/particle")
        };

        return array;
    }
}
```
## v1.0.2
### Bug Fixes
Now works with Java 8

## v1.0.3
### Adds:
A function for creating slab models with theoretically infinite layers.

Makes it easier to provide textures for model generating functions.

Example:
```java
package net.Pinary_Pi.pinaryslib.data.client;

import java.util.List;

import com.google.gson.JsonElement;

import net.Pinary_Pi.pinaryslib.pinaryslib;
import net.Pinary_Pi.pinaryslib.lib.PinarysModelProvider;
import net.minecraft.data.DataGenerator;

public class ModModelProvider extends PinarysModelProvider {
    private String modid = pinaryslib.MOD_ID;

    public ModModelProvider(DataGenerator generator) {
        super(pinaryslib.MOD_ID, generator);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<JsonElement>[] registerModels() {

        @SuppressWarnings("rawtypes")
        List[] array =  {
            simpleLayeredBlock("test_block", textures("mortar", "brick"), modid, "mortar"),
            simpleLayeredSlab("test_slab", textures("mortar", "brick"), modid, "mortar")
        };

        return array;
    }
}
```
## v1.0.4
### Adds:
A function for generating stair models with theoretically infinite layers.
It works the same way as the other functions and has the name `simpleLayeredStairs()`.
