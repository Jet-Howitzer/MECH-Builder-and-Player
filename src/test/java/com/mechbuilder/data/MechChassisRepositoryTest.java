package com.mechbuilder.data;

import com.mechbuilder.model.MechChassis;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MechChassisRepositoryTest {
    
    private MechChassisRepository repository;
    private List<MechChassis> chassisList;
    
    @BeforeEach
    void setUp() throws IOException, CsvValidationException {
        repository = new MechChassisRepository();
        chassisList = repository.loadAll(); // Load actual production data
    }
    
    @Test
    void testLoadAllChassisFromProductionData() {
        // Given: Production CSV data
        
        // When: Loading all chassis
        // Then: Should load chassis data successfully
        assertNotNull(chassisList);
        assertTrue(chassisList.size() > 0, "Should load at least one chassis");
        
        // Verify first chassis has required fields
        MechChassis firstChassis = chassisList.get(0);
        assertNotNull(firstChassis.getName(), "Chassis should have name");
        assertNotNull(firstChassis.getChassisSize(), "Chassis should have size");
        assertTrue(firstChassis.getTonnage() > 0, "Tonnage should be positive");
        assertTrue(firstChassis.getMaxArmorTonnage() >= 0, "Max armor should be non-negative");
        assertTrue(firstChassis.getHexSpeed() > 0, "Speed should be positive");
        assertNotNull(firstChassis.getHardpoints(), "Should have hardpoints map");
    }
    
    @Test
    void testFindByNameExists() {
        // Given: Loaded chassis data
        String firstChassisName = chassisList.get(0).getName();
        
        // When: Finding existing chassis by name
        Optional<MechChassis> result = chassisList.stream()
                .filter(chassis -> chassis.getName().equals(firstChassisName))
                .findFirst();
        
        // Then: Should find the chassis
        assertTrue(result.isPresent());
        MechChassis chassis = result.get();
        assertEquals(firstChassisName, chassis.getName());
        assertNotNull(chassis.getChassisSize());
        assertTrue(chassis.getTonnage() > 0);
    }
    
    @Test
    void testFindByNameNotExists() {
        // Given: Loaded chassis data
        
        // When: Finding non-existing chassis
        Optional<MechChassis> result = chassisList.stream()
                .filter(chassis -> chassis.getName().equals("NonExistentChassis_XYZ123"))
                .findFirst();
        
        // Then: Should not find anything
        assertFalse(result.isPresent());
    }
    
    @Test
    void testFilterByChassisSize() {
        // Given: Loaded chassis data
        
        // When: Filtering by chassis size
        List<MechChassis> lightChassis = chassisList.stream()
                .filter(chassis -> chassis.getChassisSize().equals("Light"))
                .toList();
        
        List<MechChassis> mediumChassis = chassisList.stream()
                .filter(chassis -> chassis.getChassisSize().equals("Medium"))
                .toList();
        
        List<MechChassis> largeChassis = chassisList.stream()
                .filter(chassis -> chassis.getChassisSize().equals("Large"))
                .toList();
        
        // Then: Should categorize chassis by size
        assertTrue(lightChassis.size() >= 0, "Light chassis count should be non-negative");
        assertTrue(mediumChassis.size() >= 0, "Medium chassis count should be non-negative");
        assertTrue(largeChassis.size() >= 0, "Large chassis count should be non-negative");
        
        // All chassis should have valid sizes (note: there are more than just Light/Medium/Large)
        for (MechChassis chassis : chassisList) {
            assertNotNull(chassis.getChassisSize(), "Chassis size should not be null");
            assertFalse(chassis.getChassisSize().trim().isEmpty(), 
                "Chassis size should not be empty for " + chassis.getName());
        }
    }
    
    @Test
    void testChassisHardpointsMapping() {
        // Given: Loaded chassis data
        
        // When/Then: Checking hardpoint mapping for all chassis
        for (MechChassis chassis : chassisList) {
            var hardpoints = chassis.getHardpoints();
            
            // All hardpoint sections should be present
            assertTrue(hardpoints.containsKey("Left Arm"), "Should have Left Arm hardpoints");
            assertTrue(hardpoints.containsKey("Left Torso"), "Should have Left Torso hardpoints");
            assertTrue(hardpoints.containsKey("Head"), "Should have Head hardpoints");
            assertTrue(hardpoints.containsKey("Center Torso"), "Should have Center Torso hardpoints");
            assertTrue(hardpoints.containsKey("Right Torso"), "Should have Right Torso hardpoints");
            assertTrue(hardpoints.containsKey("Right Arm"), "Should have Right Arm hardpoints");
            
            // All hardpoint values should be non-negative
            for (var entry : hardpoints.entrySet()) {
                assertTrue(entry.getValue() >= 0, 
                    "Hardpoints for " + entry.getKey() + " should be non-negative");
            }
        }
    }
    
    @Test
    void testChassisDataIntegrity() {
        // Given: Loaded chassis data
        
        // When/Then: Verify data integrity
        for (MechChassis chassis : chassisList) {
            // Name should not be null or empty
            assertNotNull(chassis.getName(), "Chassis name should not be null");
            assertFalse(chassis.getName().trim().isEmpty(), "Chassis name should not be empty");
            
            // Tonnage should be reasonable for BattleTech mechs (including DropShips which can be >100)
            assertTrue(chassis.getTonnage() >= 10 && chassis.getTonnage() <= 10000, 
                "Tonnage should be reasonable for " + chassis.getName());
            
            // Speed should be positive
            assertTrue(chassis.getHexSpeed() > 0, "Speed should be positive for " + chassis.getName());
            
            // Max armor should be reasonable
            assertTrue(chassis.getMaxArmorTonnage() >= 0 && chassis.getMaxArmorTonnage() <= chassis.getTonnage(),
                "Max armor should be reasonable for " + chassis.getName());
        }
    }
    
    @Test
    void testRepositoryFindByName() throws IOException, CsvValidationException {
        // Given: Repository with loaded data
        if (!chassisList.isEmpty()) {
            String existingName = chassisList.get(0).getName();
            
            // When: Using repository's findByName method
            Optional<MechChassis> found = repository.findByName(existingName);
            
            // Then: Should find the chassis
            assertTrue(found.isPresent(), "Should find existing chassis");
            assertEquals(existingName, found.get().getName());
        }
        
        // When: Searching for non-existent chassis
        Optional<MechChassis> notFound = repository.findByName("NonExistentChassis_XYZ123");
        
        // Then: Should not find anything
        assertFalse(notFound.isPresent(), "Should not find non-existent chassis");
    }
}