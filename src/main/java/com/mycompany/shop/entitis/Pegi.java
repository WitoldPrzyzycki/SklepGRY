
package com.mycompany.shop.entitis;


public enum Pegi {
    PEGI_3("PEGI 3"),
    PEGI_7("PEGI 7"),
    PEGI_12("PEGI 12"),
    PEGI_16("PEGI 16"),
    PEGI_18("PEGI 18");

    private String value;

    private Pegi(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.replace(" ", "_");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
