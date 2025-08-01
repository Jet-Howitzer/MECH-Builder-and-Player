package com.mechbuilder.data;

import com.mechbuilder.model.ArmorType;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ArmorTypeRepositoryTest {
    
    private ArmorTypeRepository repository;
    private List<ArmorType> armorTypes;
    
    @BeforeEach
    void setUp() throws IOException, CsvValidationException {
        repository = new ArmorTypeRepository();
        armorTypes = repository.loadAll(); // Load actual production data
    }
    
    @Test
    void testLoadAllArmorTypesFromProductionData() {
        // Given: Production CSV data
        
        // When: Loading all armor types
        // Then: Should load armor data successfully
        assertNotNull(armorTypes);
        assertTrue(armorTypes.size() > 0, "Should load at least one armor type");
        
        // Verify first armor type has required fields
        ArmorType firstArmor = armorTypes.get(0);
        assertNotNull(firstArmor.getArmorType(), "Armor should have name");
        assertNotNull(firstArmor.getType(), "Armor should have type");
        assertTrue(firstArmor.getHpPerTon() > 0, "HP per ton should be positive");
    }
    
    @Test
    void testFindArmorByNameExists() throws IOException, CsvValidationException {
        // Given: Loaded armor data
        if (!armorTypes.isEmpty()) {
            String firstArmorName = armorTypes.get(0).getArmorType();
            
            // When: Searching by exact name
            Optional<ArmorType> found = repository.findByName(firstArmorName);
            
            // Then: Should find the armor
            assertTrue(found.isPresent(), "Should find armor by exact name");
            assertEquals(firstArmorName, found.get().getArmorType());
        }
    }
    
    @Test
    void testFindArmorByNameNotExists() throws IOException, CsvValidationException {
        // Given: Loaded armor data
        
        // When: Searching for non-existent armor
        Optional<ArmorType> found = repository.findByName("NonexistentArmor");
        
        // Then: Should not find anything
        assertFalse(found.isPresent(), "Should not find non-existent armor");
    }
    
    @Test
    void testFindArmorByType() throws IOException, CsvValidationException {
        // Given: Loaded armor data
        
        // When: Searching for armor by type
        List<ArmorType> plateArmors = repository.findByType("Plate");
        List<ArmorType> refractiveArmors = repository.findByType("Refractive");
        List<ArmorType> ablativeArmors = repository.findByType("Ablative");
        
        // Then: Should find armors of each type
        assertNotNull(plateArmors);
        assertNotNull(refractiveArmors);
        assertNotNull(ablativeArmors);
        
        // Verify all returned armors match the requested type
        for (ArmorType armor : plateArmors) {
            assertEquals("Plate", armor.getType(), "All plate armors should have Plate type");
        }
        
        for (ArmorType armor : refractiveArmors) {
            assertEquals("Refractive", armor.getType(), "All refractive armors should have Refractive type");
        }
        
        for (ArmorType armor : ablativeArmors) {
            assertEquals("Ablative", armor.getType(), "All ablative armors should have Ablative type");
        }
    }
    
    @Test
    void testFindArmorByHpPerTonRange() throws IOException, CsvValidationException {
        // Given: Loaded armor data
        
        // When: Searching for armor in HP/Ton range
        List<ArmorType> lowTierArmors = repository.findByHpPerTonRange(30, 50);
        List<ArmorType> highTierArmors = repository.findByHpPerTonRange(70, 100);
        
        // Then: Should find armors in the specified ranges
        assertNotNull(lowTierArmors);
        assertNotNull(highTierArmors);
        
        // Verify all returned armors are within the specified range
        for (ArmorType armor : lowTierArmors) {
            assertTrue(armor.getHpPerTon() >= 30 && armor.getHpPerTon() <= 50,
                "Low tier armor HP/Ton should be between 30-50, was: " + armor.getHpPerTon());
        }
        
        for (ArmorType armor : highTierArmors) {
            assertTrue(armor.getHpPerTon() >= 70 && armor.getHpPerTon() <= 100,
                "High tier armor HP/Ton should be between 70-100, was: " + armor.getHpPerTon());
        }
    }
    
    @Test
    void testGetBestArmorByType() throws IOException, CsvValidationException {
        // Given: Loaded armor data
        
        // When: Getting best armor by type
        Optional<ArmorType> bestPlate = repository.getBestArmorByType("Plate");
        Optional<ArmorType> bestRefractive = repository.getBestArmorByType("Refractive");
        Optional<ArmorType> bestAblative = repository.getBestArmorByType("Ablative");
        
        // Then: Should find best armors (if any exist for each type)
        if (bestPlate.isPresent()) {
            assertEquals("Plate", bestPlate.get().getType());
            
            // Verify it's actually the best Plate armor
            List<ArmorType> allPlateArmors = repository.findByType("Plate");
            int maxPlateHp = allPlateArmors.stream()
                    .mapToInt(ArmorType::getHpPerTon)
                    .max()
                    .orElse(0);
            assertEquals(maxPlateHp, bestPlate.get().getHpPerTon());
        }
        
        if (bestAblative.isPresent()) {
            assertEquals("Ablative", bestAblative.get().getType());
            
            // Verify it's actually the best Ablative armor
            List<ArmorType> allAblativeArmors = repository.findByType("Ablative");
            int maxAblativeHp = allAblativeArmors.stream()
                    .mapToInt(ArmorType::getHpPerTon)
                    .max()
                    .orElse(0);
            assertEquals(maxAblativeHp, bestAblative.get().getHpPerTon());
        }
    }
    
    @Test
    void testArmorDataIntegrity() {
        // Given: Loaded armor data
        
        // When/Then: Verify data integrity
        for (ArmorType armor : armorTypes) {
            // Name should not be null or empty
            assertNotNull(armor.getArmorType(), "Armor name should not be null");
            assertFalse(armor.getArmorType().trim().isEmpty(), "Armor name should not be empty");
            
            // Type should be valid
            assertNotNull(armor.getType(), "Armor type should not be null");
            assertTrue(List.of("Plate", "Refractive", "Ablative").contains(armor.getType()),
                "Armor type should be one of: Plate, Refractive, Ablative. Was: " + armor.getType());
            
            // HP per ton should be reasonable
            assertTrue(armor.getHpPerTon() > 0 && armor.getHpPerTon() <= 200,
                "HP per ton should be reasonable (1-200) for " + armor.getArmorType() + 
                ", was: " + armor.getHpPerTon());
        }
    }
    
    @Test
    void testArmorTypeCalculations() throws IOException, CsvValidationException {
        // Given: A known armor type
        Optional<ArmorType> armorOpt = repository.findByName("MACM");
        
        if (armorOpt.isPresent()) {
            ArmorType armor = armorOpt.get();
            
            // When: Calculating total HP for tonnage
            int totalHp = armor.calculateTotalHp(5.0);
            
            // Then: Should calculate correctly
            assertEquals(armor.getHpPerTon() * 5, totalHp);
            
            // When: Calculating required tonnage for HP
            double requiredTonnage = armor.calculateRequiredTonnage(150);
            
            // Then: Should calculate correctly
            assertEquals(150.0 / armor.getHpPerTon(), requiredTonnage, 0.001);
        }
    }
}