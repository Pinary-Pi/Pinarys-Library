package net.Pinary_Pi.pinaryslib.lib;

import java.util.List;

public class BlockModel {
    String name;
    String parent;
    List<Elements> elements;

    public BlockModel(String parent, String name) {
        this.name = name;
        this.parent = parent;
    }

    public BlockModel(String parent, List<Elements> elements, String name) {
        this.name = name;
        this.parent = parent;
        this.elements = elements;
    }

    public static class Elements {
        Integer[] from;
        Integer[] to;
        Faces faces;
        public Elements(Integer[] from, Integer[] to, Faces faces) {
            this.from = from;
            this.to = to;
            this.faces = faces;
        }
    }

    public static class Faces {
        Side up;
        Side down;
        Side north;
        Side south;
        Side east;
        Side west;

        public Faces(Side up, Side down, Side north, Side south, Side east, Side west) {
            this.up = up;
            this.down = down;
            this.north = north;
            this.south = south;
            this.east = east;
            this.west = west;
        }

        public Faces (Side all) {
            this.up = all;
            this.down = all;
            this.north = all;
            this.south = all;
            this.east = all;
            this.west = all;
        }

        public static class Side {
            String texture;

            public Side(String texture) {
                this.texture = texture;
            }
        }
    }
}