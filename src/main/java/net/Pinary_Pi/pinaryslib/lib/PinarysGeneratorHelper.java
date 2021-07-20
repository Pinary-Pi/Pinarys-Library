package net.Pinary_Pi.pinaryslib.lib;

public class PinarysGeneratorHelper {
    PinarysGeneratorHelper() {}

    /**
     * A function that turns any int to some letters.
     * <p>
     * 0 = a
     * <p>
     * 1 = b
     * <p>
     * 2 = c
     * <p>
     * 3 = d
     * <p>
     * 4 = e
     * <p>
     * 5 = f
     * <p>
     * 6 = g
     * <p>
     * 7 = h
     * <p>
     * 8 = i
     * <p>
     * 9 = j
     * <p>
     * Example:
     * 8765 = ihgf
     * 
     * @param layerCount an int
     * @return {@link String}
     * @author <a href="https://github.com/Pinary-Pi/">Pinary-Pi</a>
     */
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
