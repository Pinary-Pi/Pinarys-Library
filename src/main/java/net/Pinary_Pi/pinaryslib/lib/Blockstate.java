package net.Pinary_Pi.pinaryslib.lib;

import java.util.ArrayList;
import java.util.List;

public class Blockstate {
    String name;

    public Blockstate(String name) {
        this.name = name;
    }

    List<MultiPart> multiparts;

    public Blockstate addMultiPart(MultiPart multiPart) {
        if (this.multiparts == null) {
            this.multiparts = new ArrayList<>();
        }
        this.multiparts.add(multiPart);
        return this;
    }

    List<Variant> variants;

    public Blockstate addVariant(Variant variant) {
        if (this.variants == null) {
            this.variants = new ArrayList<>();
        }
        this.variants.add(variant);
        return this;
    }

    public static class Variant {
        String condition;
        String model;
        Integer x;
        Integer y;
        Boolean uvlock;
        
        public Variant(String condition, String model) {
            this.condition = condition;
            this.model = model;
        }

        public Variant xRotation(Integer rotation) {
            this.x = rotation;
            return this;
        }

        public Variant yRotation(Integer rotation) {
            this.y = rotation;
            return this;
        }

        public Variant uvlock(Boolean uvlock) {
            this.uvlock = uvlock;
            return this;
        }
    }

    public static class MultiPart {
        When when;
        Apply apply;

        public MultiPart(Apply apply, When when) {
            this.apply = apply;
            this.when = when;
        }

        public static class Apply {
            String model;

            public Apply(String model) {
                this.model = model;
            }

            Integer x;
            Integer y;
            Boolean uvlock;
    
            public Apply xRotation(Integer rotation) {
                this.x = rotation;
                return this;
            }
    
            public Apply yRotation(Integer rotation) {
                this.y = rotation;
                return this;
            }
    
            public Apply uvlock(Boolean uvlock) {
                this.uvlock = uvlock;
                return this;
            }
        }

        public static class When {
            String variable;
            String value;

            public When(String variable, String value) {
                this.variable = variable;
                this.value = value;
            }
        }
    }
}