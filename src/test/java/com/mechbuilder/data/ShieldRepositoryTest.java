package com.mechbuilder.data;

import com.mechbuilder.model.Shield;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ShieldRepositoryTest {
    
    private ShieldRepository repository;
    private List<Shield> shields;
    
    @BeforeEach
    void setUp() throws IOException, CsvValidationException {
        repository = new ShieldRepository();
        shields = repository.loadAll(); // Load actual production data
    }
    
    @Test
    void testLoadAllShieldsFromProductionData() {
        // Given: Production CSV data
        
        // When: Loading all shields
        // Then: Should load shield data successfully
        assertNotNull(shields);
        assertTrue(shields.size() > 0, "Should load at least one shield");
        
        // Verify first shield has required fields
        Shield firstShield = shields.get(0);
        assertNotNull(firstShield.getShield(), "Shield should have name");
        assertTrue(firstShield.getHp() >= 0, "HP should be non-negative");
        assertTrue(firstShield.getTonnage() >= 0, "Tonnage should be non-negative");
        assertTrue(firstShield.getRechargeRate() >= 0, "Recharge rate should be non-negative");
    }
    
    @Test
    void testFindShieldByNameExists() throws IOException, CsvValidationException {
        // Given: Loaded shield data
        if (!shields.isEmpty()) {
            String firstShieldName = shields.get(0).getShield();
            
            // When: Searching by exact name
            Optional<Shield> found = repository.findByName(firstShieldName);
            
            // Then: Should find the shield
            assertTrue(found.isPresent(), "Should find shield by exact name");
            assertEquals(firstShieldName, found.get().getShield());
        }
    }
    
    @Test
    void testFindShieldByNameNotExists() throws IOException, CsvValidationException {
        // Given: Loaded shield data
        
        // When: Searching for non-existent shield
        Optional<Shield> found = repository.findByName("NonexistentShield");
        
        // Then: Should not find anything
        assertFalse(found.isPresent(), "Should not find non-existent shield");
    }
    
    @Test
    void testFindShieldsByTonnageRange() throws IOException, CsvValidationException {
        // Given: Loaded shield data
        
        // When: Searching for shields in tonnage range
        List<Shield> lightShields = repository.findByTonnageRange(0, 4);
        List<Shield> mediumShields = repository.findByTonnageRange(4, 6);
        
        // Then: Should find shields in the specified ranges
        assertNotNull(lightShields);
        assertNotNull(mediumShields);
        
        // Verify all returned shields are within the specified range
        for (Shield shield : lightShields) {
            assertTrue(shield.getTonnage() >= 0 && shield.getTonnage() <= 4,
                "Light shield tonnage should be 0-4, was: " + shield.getTonnage());
        }
        
        for (Shield shield : mediumShields) {
            assertTrue(shield.getTonnage() >= 4 && shield.getTonnage() <= 6,
                "Medium shield tonnage should be 4-6, was: " + shield.getTonnage());
        }
    }
    
    @Test
    void testFindShieldsByHpRange() throws IOException, CsvValidationException {
        // Given: Loaded shield data
        
        // When: Searching for shields in HP range
        List<Shield> lowHpShields = repository.findByHpRange(0, 200);
        List<Shield> highHpShields = repository.findByHpRange(300, 600);
        
        // Then: Should find shields in the specified ranges
        assertNotNull(lowHpShields);
        assertNotNull(highHpShields);
        
        // Verify all returned shields are within the specified range
        for (Shield shield : lowHpShields) {
            assertTrue(shield.getHp() >= 0 && shield.getHp() <= 200,
                "Low HP shield should be 0-200 HP, was: " + shield.getHp());
        }
        
        for (Shield shield : highHpShields) {
            assertTrue(shield.getHp() >= 300 && shield.getHp() <= 600,
                "High HP shield should be 300-600 HP, was: " + shield.getHp());
        }
    }
    
    @Test
    void testFindActualShields() throws IOException, CsvValidationException {
        // Given: Loaded shield data
        
        // When: Getting actual shields (excluding "None")
        List<Shield> actualShields = repository.findActualShields();
        
        // Then: Should exclude "None" shield
        assertNotNull(actualShields);
        
        for (Shield shield : actualShields) {
            assertFalse(shield.isNoneShield(), "Actual shields should not include 'None' option");
            assertNotEquals("None", shield.getShield(), "Shield name should not be 'None'");
        }
    }
    
    @Test
    void testGetMostEfficientShield() throws IOException, CsvValidationException {
        // Given: Loaded shield data
        
        // When: Getting most efficient shield
        Optional<Shield> mostEfficient = repository.getMostEfficientShield();
        
        // Then: Should find a shield (if any actual shields exist)
        List<Shield> actualShields = repository.findActualShields();
        if (!actualShields.isEmpty()) {
            assertTrue(mostEfficient.isPresent(), "Should find most efficient shield when actual shields exist");
            
            Shield efficient = mostEfficient.get();
            assertFalse(efficient.isNoneShield(), "Most efficient shield should not be 'None'");
            
            // Verify it's actually the most efficient
            double maxEfficiency = actualShields.stream()
                    .mapToDouble(Shield::getEfficiencyRating)
                    .max()
                    .orElse(0);
            assertEquals(maxEfficiency, efficient.getEfficiencyRating(), 0.001,
                "Should be the actual most efficient shield");
        }
    }
    
    @Test
    void testFindShieldsByEfficiency() throws IOException, CsvValidationException {
        // Given: Loaded shield data
        
        // When: Getting shields sorted by efficiency
        List<Shield> shieldsByEfficiency = repository.findShieldsByEfficiency();
        
        // Then: Should be sorted in descending order of efficiency
        assertNotNull(shieldsByEfficiency);
        
        for (int i = 0; i < shieldsByEfficiency.size() - 1; i++) {
            Shield current = shieldsByEfficiency.get(i);
            Shield next = shieldsByEfficiency.get(i + 1);
            
            assertTrue(current.getEfficiencyRating() >= next.getEfficiencyRating(),
                "Shields should be sorted by efficiency (descending): " +
                current.getEfficiencyRating() + " >= " + next.getEfficiencyRating());
        }
        
        // Should not include "None" shields
        for (Shield shield : shieldsByEfficiency) {
            assertFalse(shield.isNoneShield(), "Efficiency list should not include 'None' shields");
        }
    }
    
    @Test
    void testShieldDataIntegrity() {
        // Given: Loaded shield data
        
        // When/Then: Verify data integrity
        for (Shield shield : shields) {
            // Name should not be null or empty
            assertNotNull(shield.getShield(), "Shield name should not be null");
            assertFalse(shield.getShield().trim().isEmpty(), "Shield name should not be empty");
            
            // All numeric values should be non-negative
            assertTrue(shield.getHp() >= 0, "HP should be non-negative for " + shield.getShield());
            assertTrue(shield.getRechargeRate() >= 0, "Recharge rate should be non-negative for " + shield.getShield());
            assertTrue(shield.getRechargeDelay() >= 0, "Recharge delay should be non-negative for " + shield.getShield());
            assertTrue(shield.getTonnage() >= 0, "Tonnage should be non-negative for " + shield.getShield());
            assertTrue(shield.getHeatGenerated() >= 0, "Heat generated should be non-negative for " + shield.getShield());
            assertTrue(shield.getRechargingHeat() >= 0, "Recharging heat should be non-negative for " + shield.getShield());
            
            // Reasonable value ranges
            assertTrue(shield.getHp() <= 1000, "HP should be reasonable (≤1000) for " + shield.getShield());
            assertTrue(shield.getTonnage() <= 20, "Tonnage should be reasonable (≤20) for " + shield.getShield());
            assertTrue(shield.getRechargeRate() <= 100, "Recharge rate should be reasonable (≤100) for " + shield.getShield());
        }
    }
    
    @Test
    void testShieldCalculations() throws IOException, CsvValidationException {
        // Given: A known shield
        Optional<Shield> shieldOpt = repository.findByName("Aegis Class Barrier");
        
        if (shieldOpt.isPresent()) {
            Shield shield = shieldOpt.get();
            
            // When: Calculating full recharge time
            double rechargeTime = shield.getFullRechargeTime();
            
            // Then: Should calculate correctly
            if (shield.getRechargeRate() > 0) {
                double expectedTime = shield.getRechargeDelay() + ((double) shield.getHp() / shield.getRechargeRate());
                assertEquals(expectedTime, rechargeTime, 0.001, "Recharge time calculation should be correct");
            }
            
            // When: Calculating efficiency rating
            double efficiency = shield.getEfficiencyRating();
            
            // Then: Should calculate correctly
            if (shield.getTonnage() > 0) {
                double expectedEfficiency = shield.getHp() / shield.getTonnage();
                assertEquals(expectedEfficiency, efficiency, 0.001, "Efficiency calculation should be correct");
            }
        }
    }
    
    @Test
    void testNoneShieldIdentification() throws IOException, CsvValidationException {
        // Given: Loaded shield data
        
        // When: Looking for "None" shield
        Optional<Shield> noneShield = repository.findByName("None");
        
        // Then: Should identify it correctly if it exists
        if (noneShield.isPresent()) {
            assertTrue(noneShield.get().isNoneShield(), "'None' shield should be identified as none shield");
            assertEquals(0, noneShield.get().getHp(), "None shield should have 0 HP");
            assertEquals(0, noneShield.get().getTonnage(), 0.001, "None shield should have 0 tonnage");
        }
    }
}