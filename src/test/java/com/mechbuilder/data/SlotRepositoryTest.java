package com.mechbuilder.data;

import com.mechbuilder.model.SlotConfiguration;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SlotRepositoryTest {
    
    private SlotRepository repository;
    private List<SlotConfiguration> slotsList;
    
    @BeforeEach
    void setUp() throws IOException, CsvValidationException {
        repository = new SlotRepository();
        slotsList = repository.loadAll(); // Load actual production data
    }
    
    @Test
    void testLoadAllSlotsFromProductionData() {
        // Given: Production CSV data
        
        // When: Loading all slot configurations
        // Then: Should load slot data successfully
        assertNotNull(slotsList);
        assertTrue(slotsList.size() > 0, "Should load at least one slot configuration");
        
        // Verify first configuration has required fields
        SlotConfiguration firstConfig = slotsList.get(0);
        assertNotNull(firstConfig.getMechSize(), "Slot config should have mech size");
        assertTrue(firstConfig.getLeftArmSlots() >= 0, "Left arm slots should be non-negative");
        assertTrue(firstConfig.getRightArmSlots() >= 0, "Right arm slots should be non-negative");
    }
    
    @Test
    void testFindSlotsBySize() {
        // Given: Loaded slot data
        
        // When: Finding slots by mech size
        Optional<SlotConfiguration> lightSlots = slotsList.stream()
                .filter(slots -> slots.getMechSize().equals("Light"))
                .findFirst();
        
        Optional<SlotConfiguration> mediumSlots = slotsList.stream()
                .filter(slots -> slots.getMechSize().equals("Medium"))
                .findFirst();
        
        Optional<SlotConfiguration> largeSlots = slotsList.stream()
                .filter(slots -> slots.getMechSize().equals("Large"))
                .findFirst();
        
        // Then: Should find configurations for all sizes or validate what exists
        assertTrue(lightSlots.isPresent() || mediumSlots.isPresent() || largeSlots.isPresent(),
            "Should have at least one mech size configuration");
        
        // If medium exists, it should have more slots than light (if light exists)
        if (lightSlots.isPresent() && mediumSlots.isPresent()) {
            assertTrue(mediumSlots.get().getLeftArmSlots() >= lightSlots.get().getLeftArmSlots(),
                "Medium mech should have >= slots than light mech");
        }
    }
    
    @Test
    void testSlotConfigurationCompleteness() {
        // Given: Loaded slot data
        
        // When/Then: Every configuration should have all body parts with positive slots
        for (SlotConfiguration config : slotsList) {
            assertTrue(config.getLeftArmSlots() > 0, "Left arm should have slots for " + config.getMechSize());
            assertTrue(config.getLeftTorsoSlots() > 0, "Left torso should have slots for " + config.getMechSize());
            assertTrue(config.getCockpitSlots() > 0, "Cockpit should have slots for " + config.getMechSize());
            assertTrue(config.getCenterTorsoSlots() > 0, "Center torso should have slots for " + config.getMechSize());
            assertTrue(config.getRightTorsoSlots() > 0, "Right torso should have slots for " + config.getMechSize());
            assertTrue(config.getRightArmSlots() > 0, "Right arm should have slots for " + config.getMechSize());
            assertTrue(config.getLeftLegSlots() > 0, "Left leg should have slots for " + config.getMechSize());
            assertTrue(config.getRightLegSlots() > 0, "Right leg should have slots for " + config.getMechSize());
        }
    }
    
    @Test
    void testSlotSymmetry() {
        // Given: Loaded slot data
        
        // When/Then: Left and right sides should be symmetric
        for (SlotConfiguration config : slotsList) {
            assertEquals(config.getLeftArmSlots(), config.getRightArmSlots(), 
                    "Arms should have same slots for " + config.getMechSize());
            assertEquals(config.getLeftTorsoSlots(), config.getRightTorsoSlots(), 
                    "Torsos should have same slots for " + config.getMechSize());
            assertEquals(config.getLeftLegSlots(), config.getRightLegSlots(), 
                    "Legs should have same slots for " + config.getMechSize());
        }
    }
    
    @Test
    void testSlotDataIntegrity() {
        // Given: Loaded slot data
        
        // When/Then: Verify data integrity
        for (SlotConfiguration config : slotsList) {
            // Size should not be null or empty
            assertNotNull(config.getMechSize(), "Mech size should not be null");
            assertFalse(config.getMechSize().trim().isEmpty(), "Mech size should not be empty");
            
            // All slot counts should be reasonable (between 1 and 20 for BattleTech)
            assertTrue(config.getLeftArmSlots() >= 1 && config.getLeftArmSlots() <= 20,
                "Left arm slots should be reasonable for " + config.getMechSize());
            assertTrue(config.getCenterTorsoSlots() >= 1 && config.getCenterTorsoSlots() <= 20,
                "Center torso slots should be reasonable for " + config.getMechSize());
        }
    }
    
    @Test
    void testRepositoryFindBySize() throws IOException, CsvValidationException {
        // Given: Repository with loaded data
        if (!slotsList.isEmpty()) {
            String existingSize = slotsList.get(0).getMechSize();
            
            // When: Using repository's findBySize method
            Optional<SlotConfiguration> found = repository.findBySize(existingSize);
            
            // Then: Should find the configuration
            assertTrue(found.isPresent(), "Should find existing size configuration");
            assertEquals(existingSize, found.get().getMechSize());
        }
        
        // When: Searching for non-existent size
        Optional<SlotConfiguration> notFound = repository.findBySize("NonExistentSize_XYZ123");
        
        // Then: Should not find anything
        assertFalse(notFound.isPresent(), "Should not find non-existent size");
    }
    
    @Test
    void testSlotConfigurationToString() {
        // Given: Loaded slot data
        if (!slotsList.isEmpty()) {
            SlotConfiguration config = slotsList.get(0);
            
            // When: Converting to string
            String configString = config.toString();
            
            // Then: Should contain relevant information
            assertNotNull(configString);
            assertFalse(configString.trim().isEmpty());
            assertTrue(configString.contains(config.getMechSize()),
                "String representation should contain mech size");
        }
    }
}