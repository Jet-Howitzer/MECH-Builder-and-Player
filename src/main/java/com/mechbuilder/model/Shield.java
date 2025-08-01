package com.mechbuilder.model;

/**
 * Represents a shield system with its specifications.
 * Loaded from Shields.csv
 */
public class Shield {
    private final String shield;
    private final int hp;
    private final int rechargeRate;
    private final int rechargeDelay;
    private final double tonnage;
    private final int heatGenerated;
    private final int rechargingHeat;

    public Shield(String shield, int hp, int rechargeRate, int rechargeDelay, 
                  double tonnage, int heatGenerated, int rechargingHeat) {
        this.shield = shield;
        this.hp = hp;
        this.rechargeRate = rechargeRate;
        this.rechargeDelay = rechargeDelay;
        this.tonnage = tonnage;
        this.heatGenerated = heatGenerated;
        this.rechargingHeat = rechargingHeat;
    }

    public String getShield() {
        return shield;
    }

    public int getHp() {
        return hp;
    }

    public int getRechargeRate() {
        return rechargeRate;
    }

    public int getRechargeDelay() {
        return rechargeDelay;
    }

    public double getTonnage() {
        return tonnage;
    }

    public int getHeatGenerated() {
        return heatGenerated;
    }

    public int getRechargingHeat() {
        return rechargingHeat;
    }

    /**
     * Calculates time to fully recharge from empty (in seconds)
     */
    public double getFullRechargeTime() {
        if (rechargeRate <= 0) return Double.POSITIVE_INFINITY;
        return rechargeDelay + ((double) hp / rechargeRate);
    }

    /**
     * Calculates efficiency rating (HP per ton)
     */
    public double getEfficiencyRating() {
        if (tonnage <= 0) return 0;
        return hp / tonnage;
    }

    /**
     * Checks if this is a "None" shield (no shield equipped)
     */
    public boolean isNoneShield() {
        return "None".equals(shield);
    }

    @Override
    public String toString() {
        if (isNoneShield()) {
            return "No Shield";
        }
        return String.format("%s - %d HP, %.1ft, %d heat", 
                           shield, hp, tonnage, heatGenerated);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Shield shieldObj = (Shield) obj;
        return hp == shieldObj.hp &&
               rechargeRate == shieldObj.rechargeRate &&
               rechargeDelay == shieldObj.rechargeDelay &&
               Double.compare(shieldObj.tonnage, tonnage) == 0 &&
               heatGenerated == shieldObj.heatGenerated &&
               rechargingHeat == shieldObj.rechargingHeat &&
               shield.equals(shieldObj.shield);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(shield, hp, rechargeRate, rechargeDelay, 
                                     tonnage, heatGenerated, rechargingHeat);
    }
}