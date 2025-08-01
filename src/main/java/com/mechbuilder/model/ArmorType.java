package com.mechbuilder.model;

/**
 * Represents an armor type with its specifications.
 * Loaded from Armor Types.csv
 */
public class ArmorType {
    private final String armorType;
    private final int hpPerTon;
    private final String type;

    public ArmorType(String armorType, int hpPerTon, String type) {
        this.armorType = armorType;
        this.hpPerTon = hpPerTon;
        this.type = type;
    }

    public String getArmorType() {
        return armorType;
    }

    public int getHpPerTon() {
        return hpPerTon;
    }

    public String getType() {
        return type;
    }

    /**
     * Calculates total HP for a given tonnage
     */
    public int calculateTotalHp(double tonnage) {
        return (int) (hpPerTon * tonnage);
    }

    /**
     * Calculates required tonnage for desired HP
     */
    public double calculateRequiredTonnage(int desiredHp) {
        return (double) desiredHp / hpPerTon;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %d HP/Ton", armorType, type, hpPerTon);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ArmorType armorType1 = (ArmorType) obj;
        return hpPerTon == armorType1.hpPerTon &&
               armorType.equals(armorType1.armorType) &&
               type.equals(armorType1.type);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(armorType, hpPerTon, type);
    }
}