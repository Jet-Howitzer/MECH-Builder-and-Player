package com.mechbuilder.data;

import com.mechbuilder.model.MechChassis;
import com.mechbuilder.model.MechSection;
import com.mechbuilder.model.SlotConfiguration;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Factory for creating complete MechSections by combining:
 * - Hardpoint data from MechChassis (from Mech Loadout Data.csv)
 * - Slot data from SlotConfiguration (from Slot Count.csv)
 */
public class MechSectionFactory {
    private final SlotRepository slotRepository;
    
    // Mapping between CSV column names and section names
    private static final Map<String, String> SECTION_MAPPING = new HashMap<>();
    static {
        SECTION_MAPPING.put("Left Arm", "L Arm Hardpoint");
        SECTION_MAPPING.put("Left Torso", "L Torso Hardpoint");
        SECTION_MAPPING.put("Head", "Head Hardpoint");
        SECTION_MAPPING.put("Center Torso", "C Torso Hardpoint");
        SECTION_MAPPING.put("Right Torso", "R Torso Hardpoint");
        SECTION_MAPPING.put("Right Arm", "R Arm Hardpoint");
    }
    
    public MechSectionFactory() {
        this.slotRepository = new SlotRepository();
    }
    
    /**
     * Creates complete MechSections for a chassis, combining hardpoint and slot data
     */
    public Map<String, MechSection> createSectionsForChassis(MechChassis chassis) 
            throws IOException, CsvValidationException {
        
        // Get slot configuration for this chassis size
        Optional<SlotConfiguration> slotConfig = slotRepository.findBySize(chassis.getChassisSize());
        if (slotConfig.isEmpty()) {
            throw new IllegalArgumentException("No slot configuration found for chassis size: " + chassis.getChassisSize());
        }
        
        SlotConfiguration config = slotConfig.get();
        Map<String, MechSection> sections = new HashMap<>();
        
        // Create sections with both hardpoints and slots
        sections.put("Left Arm", createSection("Left Arm", 
            chassis.getHardpoints().get("Left Arm"), config.getLeftArmSlots()));
        
        sections.put("Left Torso", createSection("Left Torso", 
            chassis.getHardpoints().get("Left Torso"), config.getLeftTorsoSlots()));
        
        sections.put("Head", createSection("Head", 
            chassis.getHardpoints().get("Head"), config.getCockpitSlots()));
        
        sections.put("Center Torso", createSection("Center Torso", 
            chassis.getHardpoints().get("Center Torso"), config.getCenterTorsoSlots()));
        
        sections.put("Right Torso", createSection("Right Torso", 
            chassis.getHardpoints().get("Right Torso"), config.getRightTorsoSlots()));
        
        sections.put("Right Arm", createSection("Right Arm", 
            chassis.getHardpoints().get("Right Arm"), config.getRightArmSlots()));
        
        // Leg sections (usually no hardpoints but have slots)
        sections.put("Left Leg", createSection("Left Leg", 0, config.getLeftLegSlots()));
        sections.put("Right Leg", createSection("Right Leg", 0, config.getRightLegSlots()));
        
        return sections;
    }
    
    /**
     * Creates a single MechSection with hardpoints and slots
     * For now, assumes all hardpoints are energy (can be enhanced later)
     */
    private MechSection createSection(String name, int totalHardpoints, int totalSlots) {
        // Default values for armor and structure (can be enhanced later)
        int armorTons = 2;
        int internalStructure = 5;
        
        // For simplicity, treat all hardpoints as energy hardpoints
        // This can be enhanced later to parse hardpoint types
        return new MechSection(name, armorTons, internalStructure, 
                             totalHardpoints, 0, 0, totalSlots);
    }
}