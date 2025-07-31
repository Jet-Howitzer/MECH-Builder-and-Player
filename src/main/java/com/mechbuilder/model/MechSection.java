
package com.mechbuilder.model;

public class MechSection {
	private String name;
    private int maxHitPoints;
    private int currentHitPoints;
    private int armorTons;
    private int internalStructure;
    private int energyHardpoints;
    private int ballisticHardpoints;
    private int missileHardpoints;

    public MechSection(String name, int armorTons, int internalStructure,
                       int energyHardpoints, int ballisticHardpoints, int missileHardpoints) {
        this.name = name;
        this.armorTons = armorTons;
        this.internalStructure = internalStructure;
        this.energyHardpoints = energyHardpoints;
        this.ballisticHardpoints = ballisticHardpoints;
        this.missileHardpoints = missileHardpoints;

        this.maxHitPoints = (armorTons * 8) + internalStructure;
        this.currentHitPoints = maxHitPoints;
    }

    public String getName() {
        return name;
    }

    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    public int getCurrentHitPoints() {
        return currentHitPoints;
    }

    public int getArmorTons() {
        return armorTons;
    }

    public int getInternalStructure() {
        return internalStructure;
    }

    public int getEnergyHardpoints() {
        return energyHardpoints;
    }

    public int getBallisticHardpoints() {
        return ballisticHardpoints;
    }

    public int getMissileHardpoints() {
        return missileHardpoints;
    }

    public String getSectionSummary() {
        return String.format(
            "%s\nArmor Tons: %d\nStructure: %d\nHP: %d / %d\nHardpoints [E:%d | B:%d | M:%d]",
            name, armorTons, internalStructure, currentHitPoints, maxHitPoints,
            energyHardpoints, ballisticHardpoints, missileHardpoints
        );
    }
}
