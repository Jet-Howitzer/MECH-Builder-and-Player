package com.mechbuilder.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArmorTypeTest {
    
    private ArmorType plateArmor;
    private ArmorType refractiveArmor;
    private ArmorType ablativeArmor;
    
    @BeforeEach
    void setUp() {
        plateArmor = new ArmorType("TKM", 45, "Plate");
        refractiveArmor = new ArmorType("ARP", 45, "Refractive");
        ablativeArmor = new ArmorType("KS MIV", 100, "Ablative");
    }
    
    @Test
    void testArmorTypeConstruction() {
        // Given: ArmorType constructor parameters
        String name = "Test Armor";
        int hpPerTon = 50;
        String type = "Plate";
        
        // When: Creating ArmorType
        ArmorType armor = new ArmorType(name, hpPerTon, type);
        
        // Then: Should set all properties correctly
        assertEquals(name, armor.getArmorType());
        assertEquals(hpPerTon, armor.getHpPerTon());
        assertEquals(type, armor.getType());
    }
    
    @Test
    void testCalculateTotalHp() {
        // Given: ArmorType with known HP/Ton
        
        // When: Calculating total HP for different tonnages
        int hp1 = plateArmor.calculateTotalHp(1.0);
        int hp2 = plateArmor.calculateTotalHp(2.5);
        int hp0 = plateArmor.calculateTotalHp(0.0);
        
        // Then: Should calculate correctly
        assertEquals(45, hp1, "1 ton should give 45 HP");
        assertEquals(112, hp2, "2.5 tons should give 112 HP (45 * 2.5 = 112.5, truncated to 112)");
        assertEquals(0, hp0, "0 tons should give 0 HP");
    }
    
    @Test
    void testCalculateRequiredTonnage() {
        // Given: ArmorType with known HP/Ton
        
        // When: Calculating required tonnage for different HP values
        double tonnage1 = plateArmor.calculateRequiredTonnage(45);
        double tonnage2 = plateArmor.calculateRequiredTonnage(90);
        double tonnage3 = plateArmor.calculateRequiredTonnage(0);
        double tonnage4 = ablativeArmor.calculateRequiredTonnage(500);
        
        // Then: Should calculate correctly
        assertEquals(1.0, tonnage1, 0.001, "45 HP should require 1 ton");
        assertEquals(2.0, tonnage2, 0.001, "90 HP should require 2 tons");
        assertEquals(0.0, tonnage3, 0.001, "0 HP should require 0 tons");
        assertEquals(5.0, tonnage4, 0.001, "500 HP with 100 HP/ton should require 5 tons");
    }
    
    @Test
    void testToString() {
        // Given: ArmorType instances
        
        // When: Converting to string
        String plateString = plateArmor.toString();
        String ablativeString = ablativeArmor.toString();
        
        // Then: Should format correctly
        assertEquals("TKM (Plate) - 45 HP/Ton", plateString);
        assertEquals("KS MIV (Ablative) - 100 HP/Ton", ablativeString);
    }
    
    @Test
    void testEquals() {
        // Given: ArmorType instances
        ArmorType samePlateArmor = new ArmorType("TKM", 45, "Plate");
        ArmorType differentArmor = new ArmorType("Different", 30, "Plate");
        
        // When/Then: Testing equality
        assertEquals(plateArmor, samePlateArmor, "Same armor types should be equal");
        assertNotEquals(plateArmor, differentArmor, "Different armor types should not be equal");
        assertNotEquals(plateArmor, null, "Armor should not equal null");
        assertNotEquals(plateArmor, "not an armor", "Armor should not equal different object type");
    }
    
    @Test
    void testHashCode() {
        // Given: ArmorType instances
        ArmorType samePlateArmor = new ArmorType("TKM", 45, "Plate");
        
        // When/Then: Testing hash code consistency
        assertEquals(plateArmor.hashCode(), samePlateArmor.hashCode(), 
            "Equal armor types should have same hash code");
    }
    
    @Test
    void testArmorTypeComparison() {
        // Given: Different armor types
        
        // When: Comparing efficiency
        // Then: Ablative should be most efficient
        assertTrue(ablativeArmor.getHpPerTon() > plateArmor.getHpPerTon(),
            "Ablative armor should be more efficient than plate");
        assertTrue(ablativeArmor.getHpPerTon() > refractiveArmor.getHpPerTon(),
            "Ablative armor should be more efficient than refractive");
        
        // Plate and refractive have same HP/ton in this test case
        assertEquals(plateArmor.getHpPerTon(), refractiveArmor.getHpPerTon(),
            "These specific plate and refractive armors have same efficiency");
    }
    
    @Test
    void testArmorTypeEfficiencyCalculations() {
        // Given: Different scenarios
        
        // When: Calculating for large tonnage values
        int massiveHp = ablativeArmor.calculateTotalHp(100.0);
        
        // Then: Should handle large values correctly
        assertEquals(10000, massiveHp, "100 tons of 100 HP/ton armor should give 10,000 HP");
        
        // When: Calculating fractional tonnage requirements
        double fractionalTonnage = ablativeArmor.calculateRequiredTonnage(150);
        
        // Then: Should handle fractions correctly
        assertEquals(1.5, fractionalTonnage, 0.001, "150 HP should require 1.5 tons of 100 HP/ton armor");
    }
    
    @Test
    void testEdgeCases() {
        // Given: Edge case values
        ArmorType zeroHpArmor = new ArmorType("Zero", 0, "Test");
        
        // When: Calculating with zero HP/ton
        double infiniteTonnage = zeroHpArmor.calculateRequiredTonnage(100);
        
        // Then: Should handle division by zero
        assertEquals(Double.POSITIVE_INFINITY, infiniteTonnage, 
            "Requiring HP from 0 HP/ton armor should result in infinite tonnage");
        
        // When: Calculating HP with zero tonnage
        int zeroHp = plateArmor.calculateTotalHp(0.0);
        
        // Then: Should return zero
        assertEquals(0, zeroHp, "Zero tonnage should give zero HP");
    }
}