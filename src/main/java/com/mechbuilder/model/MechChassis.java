package com.mechbuilder.model;

import java.util.Map;

public class MechChassis {
    private final String name;
    private final String chassisSize;
    private final int tonnage;
    private final int maxArmorTonnage;
    private final int hexSpeed;
    private final Map<String, Integer> weaponHardpoints;

    public MechChassis(String name, String chassisSize, int tonnage, int maxArmorTonnage, int hexSpeed, Map<String, Integer> weaponHardpoints) {
        this.name = name;
        this.chassisSize = chassisSize;
        this.tonnage = tonnage;
        this.maxArmorTonnage = maxArmorTonnage;
        this.hexSpeed = hexSpeed;
        this.weaponHardpoints = weaponHardpoints;
    }

    public String getName() { return name; }
    public String getChassisSize() { return chassisSize; }
    public int getTonnage() { return tonnage; }
    public int getMaxArmorTonnage() { return maxArmorTonnage; }
    public int getHexSpeed() { return hexSpeed; }

    public int getHardpointCount(String section) {
        return weaponHardpoints.getOrDefault(section, 0);
    }

    public Map<String, Integer> getHardpoints() {
        return weaponHardpoints;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %dT, %d Armor, %d Speed", name, chassisSize, tonnage, maxArmorTonnage, hexSpeed);
    }
}