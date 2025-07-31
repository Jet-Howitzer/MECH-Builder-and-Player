package com.mechbuilder.model;

public class WeaponComponent {
    private String name;
    private String type;         // e.g., Energy, Ballistic, Missile, System
    private double tonnage;
    private double heatGeneration;
    private int damage;
    private int OptimalRange;
    private int MaxRange;
    private double RecycleTime;
    private int AccuracyPenalty;
    private int ShotsperSalvo;
    private double DamageDrop;

    public WeaponComponent(String name, String type, double tonnage, double heatGeneration, int damage, int OptimalRange, int MaxRange, double RecycleTime, int AccuracyPenalty,
    int ShotsperSalvo, double DamageDrop) {
        this.name = name;
        this.type = type;
        this.tonnage = tonnage;
        this.heatGeneration = heatGeneration;
        this.damage = damage;
        this.OptimalRange = OptimalRange;
        this.MaxRange = MaxRange;
        this.RecycleTime = RecycleTime;
        this.AccuracyPenalty = AccuracyPenalty;
        this.ShotsperSalvo = ShotsperSalvo;
        this.DamageDrop = DamageDrop;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getTonnage() {
        return tonnage;
    }

    public double getHeatGeneration() {
        return heatGeneration;
    }

    public int getDamage() {
        return damage;
    }
    
    public int getOptimalRange() {
    	return OptimalRange;
    }
    
    public int getMaxRange() {
    	return MaxRange;
    }
    
    public double getRecycleTime() {
    	return RecycleTime;
    }
    
    public int getAccuracyPenalty() {
    	return AccuracyPenalty;
    }
    
    public int getShotsperSalvo() {
    	return ShotsperSalvo;
    }
    
    public double getDamageDrop() {
    	return DamageDrop;
    }
    
    
    @Override
    public String toString() {
        return String.format(
            "Name: %s, Type: %s, Tonnage: %.1f, Heat: %.1f, Damage: %d, Optimal: %d, Max: %d, Recycle: %.2f, Accuracy: %d, Salvo: %d, Drop: %.1f",
            name, type, tonnage, heatGeneration, damage, OptimalRange, MaxRange, RecycleTime, AccuracyPenalty, ShotsperSalvo, DamageDrop
        );
    }
}