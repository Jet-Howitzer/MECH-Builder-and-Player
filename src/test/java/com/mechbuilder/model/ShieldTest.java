package com.mechbuilder.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShieldTest {
    
    private Shield aegisShield;
    private Shield bastionShield;
    private Shield noneShield;
    private Shield umbraShield;
    
    @BeforeEach
    void setUp() {
        aegisShield = new Shield("Aegis Class Barrier", 500, 25, 5, 6.0, 20, 2);
        bastionShield = new Shield("Bastion Wave Repeater", 300, 60, 5, 5.0, 12, 6);
        noneShield = new Shield("None", 0, 0, 0, 0.0, 0, 0);
        umbraShield = new Shield("Umbra Phase Curtain", 60, 10, 0, 4.0, 3, 30);
    }
    
    @Test
    void testShieldConstruction() {
        // Given: Shield constructor parameters
        String name = "Test Shield";
        int hp = 400;
        int rechargeRate = 30;
        int rechargeDelay = 3;
        double tonnage = 5.5;
        int heatGenerated = 15;
        int rechargingHeat = 8;
        
        // When: Creating Shield
        Shield shield = new Shield(name, hp, rechargeRate, rechargeDelay, tonnage, heatGenerated, rechargingHeat);
        
        // Then: Should set all properties correctly
        assertEquals(name, shield.getShield());
        assertEquals(hp, shield.getHp());
        assertEquals(rechargeRate, shield.getRechargeRate());
        assertEquals(rechargeDelay, shield.getRechargeDelay());
        assertEquals(tonnage, shield.getTonnage(), 0.001);
        assertEquals(heatGenerated, shield.getHeatGenerated());
        assertEquals(rechargingHeat, shield.getRechargingHeat());
    }
    
    @Test
    void testGetFullRechargeTime() {
        // Given: Shield instances with different recharge characteristics
        
        // When: Calculating full recharge time
        double aegisRechargeTime = aegisShield.getFullRechargeTime();
        double bastionRechargeTime = bastionShield.getFullRechargeTime();
        double umbraRechargeTime = umbraShield.getFullRechargeTime();
        double noneRechargeTime = noneShield.getFullRechargeTime();
        
        // Then: Should calculate correctly
        // Aegis: delay=5, hp=500, rate=25 -> 5 + (500/25) = 5 + 20 = 25
        assertEquals(25.0, aegisRechargeTime, 0.001, "Aegis should take 25 seconds to fully recharge");
        
        // Bastion: delay=5, hp=300, rate=60 -> 5 + (300/60) = 5 + 5 = 10
        assertEquals(10.0, bastionRechargeTime, 0.001, "Bastion should take 10 seconds to fully recharge");
        
        // Umbra: delay=0, hp=60, rate=10 -> 0 + (60/10) = 0 + 6 = 6
        assertEquals(6.0, umbraRechargeTime, 0.001, "Umbra should take 6 seconds to fully recharge");
        
        // None: rate=0 -> should be infinite
        assertEquals(Double.POSITIVE_INFINITY, noneRechargeTime, "None shield should have infinite recharge time");
    }
    
    @Test
    void testGetEfficiencyRating() {
        // Given: Shield instances with different tonnage
        
        // When: Calculating efficiency rating (HP per ton)
        double aegisEfficiency = aegisShield.getEfficiencyRating();
        double bastionEfficiency = bastionShield.getEfficiencyRating();
        double noneEfficiency = noneShield.getEfficiencyRating();
        
        // Then: Should calculate correctly
        // Aegis: 500 HP / 6 tons = 83.333...
        assertEquals(500.0/6.0, aegisEfficiency, 0.001, "Aegis efficiency should be ~83.33 HP/ton");
        
        // Bastion: 300 HP / 5 tons = 60
        assertEquals(60.0, bastionEfficiency, 0.001, "Bastion efficiency should be 60 HP/ton");
        
        // None: 0 HP / 0 tons = 0 (special case)
        assertEquals(0.0, noneEfficiency, 0.001, "None shield efficiency should be 0");
    }
    
    @Test
    void testIsNoneShield() {
        // Given: Shield instances
        
        // When/Then: Testing none shield identification
        assertTrue(noneShield.isNoneShield(), "Shield named 'None' should be identified as none shield");
        assertFalse(aegisShield.isNoneShield(), "Aegis shield should not be identified as none shield");
        assertFalse(bastionShield.isNoneShield(), "Bastion shield should not be identified as none shield");
        
        // Test case sensitivity
        Shield caseTestShield = new Shield("none", 0, 0, 0, 0.0, 0, 0);
        assertFalse(caseTestShield.isNoneShield(), "Should be case sensitive - 'none' != 'None'");
    }
    
    @Test
    void testToString() {
        // Given: Shield instances
        
        // When: Converting to string
        String aegisString = aegisShield.toString();
        String noneString = noneShield.toString();
        
        // Then: Should format correctly
        assertEquals("Aegis Class Barrier - 500 HP, 6.0t, 20 heat", aegisString);
        assertEquals("No Shield", noneString);
    }
    
    @Test
    void testEquals() {
        // Given: Shield instances
        Shield sameAegis = new Shield("Aegis Class Barrier", 500, 25, 5, 6.0, 20, 2);
        Shield differentShield = new Shield("Different Shield", 400, 20, 3, 5.0, 15, 5);
        
        // When/Then: Testing equality
        assertEquals(aegisShield, sameAegis, "Same shields should be equal");
        assertNotEquals(aegisShield, differentShield, "Different shields should not be equal");
        assertNotEquals(aegisShield, null, "Shield should not equal null");
        assertNotEquals(aegisShield, "not a shield", "Shield should not equal different object type");
    }
    
    @Test
    void testHashCode() {
        // Given: Shield instances
        Shield sameAegis = new Shield("Aegis Class Barrier", 500, 25, 5, 6.0, 20, 2);
        
        // When/Then: Testing hash code consistency
        assertEquals(aegisShield.hashCode(), sameAegis.hashCode(), 
            "Equal shields should have same hash code");
    }
    
    @Test
    void testShieldEfficiencyComparison() {
        // Given: Different shields
        
        // When: Comparing efficiency ratings
        double aegisEff = aegisShield.getEfficiencyRating();
        double bastionEff = bastionShield.getEfficiencyRating();
        double umbraEff = umbraShield.getEfficiencyRating();
        
        // Then: Should allow comparison
        // Aegis: 500/6 = ~83.33, Bastion: 300/5 = 60, Umbra: 60/4 = 15
        assertTrue(aegisEff > bastionEff, "Aegis should be more efficient than Bastion");
        assertTrue(bastionEff > umbraEff, "Bastion should be more efficient than Umbra");
        assertTrue(aegisEff > umbraEff, "Aegis should be more efficient than Umbra");
    }
    
    @Test
    void testShieldRechargeTimeComparison() {
        // Given: Different shields
        
        // When: Comparing recharge times
        double aegisTime = aegisShield.getFullRechargeTime();
        double bastionTime = bastionShield.getFullRechargeTime();
        double umbraTime = umbraShield.getFullRechargeTime();
        
        // Then: Should allow comparison
        // Aegis: 25s, Bastion: 10s, Umbra: 6s
        assertTrue(umbraTime < bastionTime, "Umbra should recharge faster than Bastion");
        assertTrue(bastionTime < aegisTime, "Bastion should recharge faster than Aegis");
        assertTrue(umbraTime < aegisTime, "Umbra should recharge fastest");
    }
    
    @Test
    void testEdgeCases() {
        // Given: Edge case shields
        Shield zeroRechargeShield = new Shield("Zero Recharge", 100, 0, 5, 3.0, 10, 5);
        Shield instantRechargeShield = new Shield("Instant", 200, 1000, 0, 4.0, 8, 2);
        
        // When: Testing edge cases
        double zeroRechargeTime = zeroRechargeShield.getFullRechargeTime();
        double instantRechargeTime = instantRechargeShield.getFullRechargeTime();
        
        // Then: Should handle correctly
        assertEquals(Double.POSITIVE_INFINITY, zeroRechargeTime, 
            "Zero recharge rate should result in infinite recharge time");
        assertEquals(0.2, instantRechargeTime, 0.001, 
            "High recharge rate with no delay should be very fast: 0 + (200/1000) = 0.2");
    }
    
    @Test
    void testZeroTonnageEfficiency() {
        // Given: Shield with zero tonnage
        Shield zeroTonnageShield = new Shield("Zero Tonnage", 100, 10, 2, 0.0, 5, 3);
        
        // When: Calculating efficiency
        double efficiency = zeroTonnageShield.getEfficiencyRating();
        
        // Then: Should handle division by zero
        assertEquals(0.0, efficiency, 0.001, "Zero tonnage shield should have 0 efficiency rating");
    }
}