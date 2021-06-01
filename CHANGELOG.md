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