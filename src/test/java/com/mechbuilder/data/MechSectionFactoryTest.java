package com.mechbuilder.data;

import com.mechbuilder.model.MechChassis;
import com.mechbuilder.model.MechSection;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MechSectionFactoryTest {
    
    private MechSectionFactory factory;
    private MechChassis testLightChassis;
    private MechChassis testMediumChassis;
    
    @BeforeEach
    void setUp() throws IOException, CsvValidationException {
        factory = new MechSectionFactory();
        
        // Create test chassis data
        Map<String, Integer> lightHardpoints = new HashMap<>();
        lightHardpoints.put("Left Arm", 1);
        lightHardpoints.put("Left Torso", 2);
        lightHardpoints.put("Head", 0);
        lightHardpoints.put("Center Torso", 2);
        lightHardpoints.put("Right Torso", 2);
        lightHardpoints.put("Right Arm", 1);
        
        testLightChassis = new MechChassis("TestLight", "Light", 20, 10, 9, lightHardpoints);
        
        Map<String, Integer> mediumHardpoints = new HashMap<>();
        mediumHardpoints.put("Left Arm", 2);
        mediumHardpoints.put("Left Torso", 3);
        mediumHardpoints.put("Head", 1);
        mediumHardpoints.put("Center Torso", 3);
        mediumHardpoints.put("Right Torso", 3);
        mediumHardpoints.put("Right Arm", 2);
        
        testMediumChassis = new MechChassis("TestMedium", "Medium", 50, 25, 6, mediumHardpoints);
    }
    
    @Test
    void testCreateSectionsForLightChassis() throws IOException, CsvValidationException {
        // Given: Light chassis with known hardpoints
        
        // When: Creating sections
        Map<String, MechSection> sections = factory.createSectionsForChassis(testLightChassis);
        
        // Then: Should create all 8 sections
        assertEquals(8, sections.size());
        assertTrue(sections.containsKey("Left Arm"));
        assertTrue(sections.containsKey("Left Torso"));
        assertTrue(sections.containsKey("Head"));
        assertTrue(sections.containsKey("Center Torso"));
        assertTrue(sections.containsKey("Right Torso"));
        assertTrue(sections.containsKey("Right Arm"));
        assertTrue(sections.containsKey("Left Leg"));
        assertTrue(sections.containsKey("Right Leg"));
    }
    
    @Test
    void testSectionHardpointIntegration() throws IOException, CsvValidationException {
        // Given: Light chassis with specific hardpoints
        
        // When: Creating sections
        Map<String, MechSection> sections = factory.createSectionsForChassis(testLightChassis);
        
        // Then: Sections should have correct hardpoint counts
        MechSection leftArm = sections.get("Left Arm");
        assertEquals(1, leftArm.getEnergyHardpoints()); // All hardpoints treated as energy for now
        assertEquals(0, leftArm.getBallisticHardpoints());
        assertEquals(0, leftArm.getMissileHardpoints());
        
        MechSection leftTorso = sections.get("Left Torso");
        assertEquals(2, leftTorso.getEnergyHardpoints());
        
        MechSection head = sections.get("Head");
        assertEquals(0, head.getEnergyHardpoints()); // Head has 0 hardpoints
    }
    
    @Test
    void testSectionSlotIntegration() throws IOException, CsvValidationException {
        // Given: Light chassis
        
        // When: Creating sections
        Map<String, MechSection> sections = factory.createSectionsForChassis(testLightChassis);
        
        // Then: Sections should have reasonable slot counts
        for (MechSection section : sections.values()) {
            assertTrue(section.getTotalSlots() > 0, 
                "Section should have positive total slots: " + section.getName());
            assertEquals(0, section.getUsedSlots(), 
                "Section should start with 0 used slots: " + section.getName());
            assertEquals(section.getTotalSlots(), section.getAvailableSlots(),
                "Available slots should equal total slots initially: " + section.getName());
        }
    }
    
    @Test
    void testLegSectionsHaveNoHardpoints() throws IOException, CsvValidationException {
        // Given: Any chassis
        
        // When: Creating sections
        Map<String, MechSection> sections = factory.createSectionsForChassis(testLightChassis);
        
        // Then: Leg sections should have 0 hardpoints but have slots
        MechSection leftLeg = sections.get("Left Leg");
        assertEquals(0, leftLeg.getEnergyHardpoints());
        assertEquals(0, leftLeg.getBallisticHardpoints());
        assertEquals(0, leftLeg.getMissileHardpoints());
        assertTrue(leftLeg.getTotalSlots() > 0, "Legs should have equipment slots");
        
        MechSection rightLeg = sections.get("Right Leg");
        assertEquals(0, rightLeg.getEnergyHardpoints());
        assertEquals(0, rightLeg.getBallisticHardpoints());
        assertEquals(0, rightLeg.getMissileHardpoints());
        assertTrue(rightLeg.getTotalSlots() > 0, "Legs should have equipment slots");
    }
    
    @Test
    void testMediumChassisScaling() throws IOException, CsvValidationException {
        // Given: Medium chassis (larger than light)
        
        // When: Creating sections for both chassis
        Map<String, MechSection> lightSections = factory.createSectionsForChassis(testLightChassis);
        Map<String, MechSection> mediumSections = factory.createSectionsForChassis(testMediumChassis);
        
        // Then: Medium should have more or equal slots than light
        assertTrue(mediumSections.get("Left Arm").getTotalSlots() >= 
                  lightSections.get("Left Arm").getTotalSlots(),
                  "Medium should have >= slots than light");
        
        // And more hardpoints
        assertTrue(mediumSections.get("Left Arm").getEnergyHardpoints() >= 
                  lightSections.get("Left Arm").getEnergyHardpoints(),
                  "Medium should have >= hardpoints than light");
        assertTrue(mediumSections.get("Left Torso").getEnergyHardpoints() >= 
                  lightSections.get("Left Torso").getEnergyHardpoints(),
                  "Medium should have >= hardpoints than light");
    }
    
    @Test
    void testSectionAvailableSlots() throws IOException, CsvValidationException {
        // Given: Created sections
        Map<String, MechSection> sections = factory.createSectionsForChassis(testLightChassis);
        
        // When: Checking available slots
        MechSection leftArm = sections.get("Left Arm");
        int totalSlots = leftArm.getTotalSlots();
        
        // Then: Available slots should equal total slots initially
        assertEquals(totalSlots, leftArm.getAvailableSlots());
        assertEquals(0, leftArm.getUsedSlots());
        
        // When: Using some slots
        int slotsToUse = Math.min(2, totalSlots); // Don't exceed total
        leftArm.setUsedSlots(slotsToUse);
        
        // Then: Available should decrease
        assertEquals(slotsToUse, leftArm.getUsedSlots());
        assertEquals(totalSlots - slotsToUse, leftArm.getAvailableSlots());
    }
    
    @Test
    void testFactoryConsistency() throws IOException, CsvValidationException {
        // Given: Same chassis
        
        // When: Creating sections multiple times
        Map<String, MechSection> sections1 = factory.createSectionsForChassis(testLightChassis);
        Map<String, MechSection> sections2 = factory.createSectionsForChassis(testLightChassis);
        
        // Then: Should produce consistent results
        assertEquals(sections1.size(), sections2.size());
        
        for (String sectionName : sections1.keySet()) {
            MechSection section1 = sections1.get(sectionName);
            MechSection section2 = sections2.get(sectionName);
            
            assertEquals(section1.getTotalSlots(), section2.getTotalSlots(),
                "Total slots should be consistent for " + sectionName);
            assertEquals(section1.getEnergyHardpoints(), section2.getEnergyHardpoints(),
                "Energy hardpoints should be consistent for " + sectionName);
        }
    }
    
    @Test
    void testAllSectionTypes() throws IOException, CsvValidationException {
        // Given: Test chassis
        
        // When: Creating sections
        Map<String, MechSection> sections = factory.createSectionsForChassis(testLightChassis);
        
        // Then: Should have all expected sections with valid properties
        String[] expectedSections = {"Left Arm", "Left Torso", "Head", "Center Torso", 
                                   "Right Torso", "Right Arm", "Left Leg", "Right Leg"};
        
        for (String sectionName : expectedSections) {
            assertTrue(sections.containsKey(sectionName), 
                "Should contain section: " + sectionName);
            
            MechSection section = sections.get(sectionName);
            assertNotNull(section, "Section should not be null: " + sectionName);
            assertEquals(sectionName, section.getName(), "Section name should match: " + sectionName);
            assertTrue(section.getTotalSlots() > 0, "Section should have slots: " + sectionName);
        }
    }
}