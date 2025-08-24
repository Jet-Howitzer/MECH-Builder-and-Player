
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
    
    // Damage tracking system
    private int armorDamage;
    private int[] slotDamage; // HP damage per slot (varies by section type)
    private int fieldRepairArmor; // Temporary armor HP from field repairs

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
        
        // Initialize damage tracking
        this.armorDamage = 0;
        this.slotDamage = new int[totalSlots];
        this.fieldRepairArmor = 0;
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
    
    // Damage tracking methods
    public int getArmorDamage() {
        return armorDamage;
    }
    
    public int getSlotDamage(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < slotDamage.length) {
            return slotDamage[slotIndex];
        }
        return 0;
    }
    
    public int getFieldRepairArmor() {
        return fieldRepairArmor;
    }
    
    public int getEffectiveArmorHP() {
        return (armorTons * 8) + fieldRepairArmor - armorDamage;
    }
    
    /**
     * Get the base HP per slot for this section type
     * @return HP per slot based on section type
     */
    private int getBaseSlotHP() {
        switch (name) {
            case "Left Arm":
            case "Right Arm":
                return 24;
            case "Left Torso":
            case "Right Torso":
                return 32;
            case "Left Leg":
            case "Right Leg":
                return 36;
            case "Center Torso":
                return 60;
            case "Head":
                return 30;
            default:
                return 24; // Default fallback
        }
    }
    
    public int getSlotHP(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < slotDamage.length) {
            return getBaseSlotHP() - slotDamage[slotIndex];
        }
        return 0;
    }
    
    public int getTotalSlotHP() {
        int total = 0;
        for (int i = 0; i < totalSlots; i++) {
            total += getSlotHP(i);
        }
        return total;
    }
    
    public int getCurrentTotalHP() {
        return getEffectiveArmorHP() + getTotalSlotHP();
    }
    
    public int getMaxTotalHP() {
        return (armorTons * 8) + (totalSlots * getBaseSlotHP());
    }
    
    /**
     * Apply damage to this section. Damage is absorbed by armor first, then slots from bottom to top.
     * @param damage Amount of damage to apply
     * @return Remaining damage if section is destroyed
     */
    public int applyDamage(int damage) {
        int remainingDamage = damage;
        
        // First, damage goes to armor (including field repair armor)
        int effectiveArmor = getEffectiveArmorHP();
        if (effectiveArmor > 0) {
            if (remainingDamage <= effectiveArmor) {
                // All damage absorbed by armor
                if (remainingDamage <= fieldRepairArmor) {
                    fieldRepairArmor -= remainingDamage;
                } else {
                    int fieldRepairDamage = Math.min(remainingDamage, fieldRepairArmor);
                    fieldRepairArmor -= fieldRepairDamage;
                    remainingDamage -= fieldRepairDamage;
                    armorDamage += remainingDamage;
                }
                remainingDamage = 0;
            } else {
                // Armor destroyed, remaining damage goes to slots
                if (fieldRepairArmor > 0) {
                    remainingDamage -= fieldRepairArmor;
                    fieldRepairArmor = 0;
                }
                armorDamage = (armorTons * 8);
                remainingDamage -= (armorTons * 8) - armorDamage;
            }
        }
        
        // Remaining damage goes to slots from bottom to top
        if (remainingDamage > 0) {
            for (int i = totalSlots - 1; i >= 0 && remainingDamage > 0; i--) {
                int slotHP = getSlotHP(i);
                if (slotHP > 0) {
                    int damageToSlot = Math.min(remainingDamage, slotHP);
                    slotDamage[i] += damageToSlot;
                    remainingDamage -= damageToSlot;
                }
            }
        }
        
        // Update current hit points
        currentHitPoints = getCurrentTotalHP();
        
        return remainingDamage;
    }
    
    /**
     * Apply field repair armor (temporary)
     * @param repairAmount Amount of temporary armor HP to add
     */
    public void applyFieldRepair(int repairAmount) {
        this.fieldRepairArmor += repairAmount;
        currentHitPoints = getCurrentTotalHP();
    }
    
    /**
     * Reset all damage and field repair armor to zero
     */
    public void resetDamage() {
        this.armorDamage = 0;
        this.fieldRepairArmor = 0;
        for (int i = 0; i < slotDamage.length; i++) {
            slotDamage[i] = 0;
        }
        currentHitPoints = getCurrentTotalHP();
    }
    
    /**
     * Get damage status for a specific slot
     * @param slotIndex Slot index (0-based)
     * @return Damage status string
     */
    public String getSlotDamageStatus(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < totalSlots) {
            int damage = slotDamage[slotIndex];
            int remainingHP = getSlotHP(slotIndex);
            if (damage == 0) {
                return String.format("Slot %d: %d/%d HP", slotIndex + 1, remainingHP, getBaseSlotHP());
            } else {
                return String.format("Slot %d: %d/%d HP (Damaged: %d)", slotIndex + 1, remainingHP, getBaseSlotHP(), damage);
            }
        }
        return "Invalid slot";
    }
    
    /**
     * Get overall damage status for this section
     */
    public String getDamageStatus() {
        StringBuilder status = new StringBuilder();
        status.append(String.format("Armor: %d/%d HP", getEffectiveArmorHP(), armorTons * 8));
        if (fieldRepairArmor > 0) {
            status.append(String.format(" (+%d field repair)", fieldRepairArmor));
        }
        status.append(String.format(" | Total HP: %d/%d", getCurrentTotalHP(), getMaxTotalHP()));
        return status.toString();
    }

    public String getSectionSummary() {
        return String.format(
            "%s\nArmor Tons: %d\nStructure: %d\nHP: %d / %d\nHardpoints [E:%d | B:%d | M:%d]\nSlots: %d/%d (%d available)\nDamage: %s",
            name, armorTons, internalStructure, currentHitPoints, maxHitPoints,
            energyHardpoints, ballisticHardpoints, missileHardpoints,
            usedSlots, totalSlots, getAvailableSlots(), getDamageStatus()
        );
    }
}
