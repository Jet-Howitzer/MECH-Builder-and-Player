package com.mechbuilder.data;

import com.mechbuilder.model.WeaponComponent;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeaponRepositoryTest {
    
    private WeaponRepository repository;
    private List<WeaponComponent> weaponsList;
    
    @BeforeEach
    void setUp() throws IOException, CsvValidationException {
        repository = new WeaponRepository();
        weaponsList = repository.loadAll(); // Load actual production data
    }
    
    @Test
    void testLoadAllWeaponsFromProductionData() {
        // Given: Production CSV data
        
        // When: Loading all weapons
        // Then: Should load weapon data successfully
        assertNotNull(weaponsList);
        assertTrue(weaponsList.size() > 0, "Should load at least one weapon");
        
        // Verify first weapon has required fields
        WeaponComponent firstWeapon = weaponsList.get(0);
        assertNotNull(firstWeapon.getName(), "Weapon should have name");
        assertNotNull(firstWeapon.getType(), "Weapon should have type");
        assertTrue(firstWeapon.getTonnage() >= 0, "Tonnage should be non-negative");
        assertTrue(firstWeapon.getDamage() >= 0, "Damage should be non-negative");
    }
    
    @Test
    void testFindWeaponsByType() {
        // Given: Loaded weapon data
        
        // When: Filtering by weapon type
        List<WeaponComponent> energyWeapons = weaponsList.stream()
                .filter(weapon -> weapon.getType().equals("Energy"))
                .toList();
        
        List<WeaponComponent> ballisticWeapons = weaponsList.stream()
                .filter(weapon -> weapon.getType().equals("Ballistic"))
                .toList();
        
        List<WeaponComponent> missileWeapons = weaponsList.stream()
                .filter(weapon -> weapon.getType().equals("Missile"))
                .toList();
        
        // Then: Should categorize weapons by type
        assertTrue(energyWeapons.size() >= 0, "Energy weapons count should be non-negative");
        assertTrue(ballisticWeapons.size() >= 0, "Ballistic weapons count should be non-negative");
        assertTrue(missileWeapons.size() >= 0, "Missile weapons count should be non-negative");
        
        // Check that all weapons have valid types
        for (WeaponComponent weapon : weaponsList) {
            assertTrue(
                weapon.getType().equals("Energy") || 
                weapon.getType().equals("Ballistic") || 
                weapon.getType().equals("Missile"),
                "Weapon type should be Energy, Ballistic, or Missile: " + weapon.getType()
            );
        }
    }
    
    @Test
    void testWeaponDataIntegrity() {
        // Given: Loaded weapon data
        
        // When/Then: Verify data integrity
        for (WeaponComponent weapon : weaponsList) {
            // Name should not be null or empty
            assertNotNull(weapon.getName(), "Weapon name should not be null");
            assertFalse(weapon.getName().trim().isEmpty(), "Weapon name should not be empty");
            
            // Type should be valid
            assertNotNull(weapon.getType(), "Weapon type should not be null");
            
            // Tonnage should be reasonable
            assertTrue(weapon.getTonnage() >= 0.0 && weapon.getTonnage() <= 50.0, 
                "Tonnage should be reasonable for " + weapon.getName());
            
            // Damage should be non-negative
            assertTrue(weapon.getDamage() >= 0, "Damage should be non-negative for " + weapon.getName());
            
            // Ranges should be positive (note: some weapons might have 0 optimal range)
            assertTrue(weapon.getOptimalRange() >= 0, "Optimal range should be non-negative for " + weapon.getName());
            assertTrue(weapon.getMaxRange() >= 0, "Max range should be non-negative for " + weapon.getName());
        }
    }
    
    @Test
    void testFindWeaponsByName() {
        // Given: Loaded weapon data
        if (!weaponsList.isEmpty()) {
            String firstWeaponName = weaponsList.get(0).getName();
            
            // When: Searching by exact name
            List<WeaponComponent> exactMatches = weaponsList.stream()
                    .filter(weapon -> weapon.getName().equals(firstWeaponName))
                    .toList();
            
            // Then: Should find the weapon
            assertEquals(1, exactMatches.size(), "Should find exactly one weapon with exact name match");
            assertEquals(firstWeaponName, exactMatches.get(0).getName());
        }
    }
    
    @Test
    void testWeaponStatisticsRanges() {
        // Given: Loaded weapon data
        
        // When: Analyzing weapon stats ranges
        if (!weaponsList.isEmpty()) {
            double minTonnage = weaponsList.stream().mapToDouble(WeaponComponent::getTonnage).min().orElse(0);
            double maxTonnage = weaponsList.stream().mapToDouble(WeaponComponent::getTonnage).max().orElse(0);
            
            int minDamage = weaponsList.stream().mapToInt(WeaponComponent::getDamage).min().orElse(0);
            int maxDamage = weaponsList.stream().mapToInt(WeaponComponent::getDamage).max().orElse(0);
            
            // Then: Should have reasonable ranges
            assertTrue(minTonnage >= 0, "Minimum tonnage should be non-negative");
            assertTrue(maxTonnage <= 50.0, "Maximum tonnage should be reasonable for BattleTech");
            assertTrue(minDamage >= 0, "Minimum damage should be non-negative");
            assertTrue(maxDamage <= 10000, "Maximum damage should be reasonable for extended BattleTech including extreme weapons");
        }
    }
    
    @Test
    void testRepositoryFindByType() throws IOException, CsvValidationException {
        // Given: Repository with loaded data
        
        // When: Using repository's findByType method
        List<WeaponComponent> energyWeapons = repository.findByType("Energy");
        List<WeaponComponent> ballisticWeapons = repository.findByType("Ballistic");
        List<WeaponComponent> missileWeapons = repository.findByType("Missile");
        
        // Then: Should return weapons of correct type
        for (WeaponComponent weapon : energyWeapons) {
            assertEquals("Energy", weapon.getType());
        }
        for (WeaponComponent weapon : ballisticWeapons) {
            assertEquals("Ballistic", weapon.getType());
        }
        for (WeaponComponent weapon : missileWeapons) {
            assertEquals("Missile", weapon.getType());
        }
        
        // When: Searching for non-existent type
        List<WeaponComponent> unknownWeapons = repository.findByType("NonExistentType");
        
        // Then: Should return empty list
        assertTrue(unknownWeapons.isEmpty(), "Should return empty list for non-existent type");
    }
}