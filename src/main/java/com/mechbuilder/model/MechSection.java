
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
    
    // Slot information from Slot Count CSV
    private int totalSlots;
    private int usedSlots;

    public MechSection(String name, int armorTons, int internalStructure,
                       int energyHardpoints, int ballisticHardpoints, int missileHardpoints,
                       int totalSlots) {
        this.name = name;
        this.armorTons = armorTons;
        this.internalStructure = internalStructure;
        this.energyHardpoints = energyHardpoints;
        this.ballisticHardpoints = ballisticHardpoints;
        this.missileHardpoints = missileHardpoints;
        this.totalSlots = totalSlots;
        this.usedSlots = 0; // Initially no slots are used

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

    public int getTotalSlots() {
        return totalSlots;
    }

    public int getUsedSlots() {
        return usedSlots;
    }

    public int getAvailableSlots() {
        return totalSlots - usedSlots;
    }

    public void setUsedSlots(int usedSlots) {
        this.usedSlots = Math.max(0, Math.min(usedSlots, totalSlots));
    }

    public String getSectionSummary() {
        return String.format(
            "%s\nArmor Tons: %d\nStructure: %d\nHP: %d / %d\nHardpoints [E:%d | B:%d | M:%d]\nSlots: %d/%d (%d available)",
            name, armorTons, internalStructure, currentHitPoints, maxHitPoints,
            energyHardpoints, ballisticHardpoints, missileHardpoints,
            usedSlots, totalSlots, getAvailableSlots()
        );
    }
}
