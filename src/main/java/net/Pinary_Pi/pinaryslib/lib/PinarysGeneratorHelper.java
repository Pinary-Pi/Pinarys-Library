package net.Pinary_Pi.pinaryslib.lib;

public class PinarysGeneratorHelper {
    PinarysGeneratorHelper() {

    }

    public static String intToLetters(int layerCount) {
        String layerCountName = "";

        while (layerCount > 9) {
            layerCountName = (char)((layerCount % 10) + 97) + layerCountName;
            layerCount = (int)(layerCount/10);
        }

        layerCountName = (char)(layerCount + 97) + layerCountName;

        return layerCountName;
    }
}
