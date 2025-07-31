package com.mechbuilder.model;

import com.mechbuilder.data.MechSectionFactory;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.HashMap;

public class Mech {
    private String name;
    private MechChassis chassis;
    private HashMap<String, MechSection> sections;

    public Mech(String name, MechChassis chassis) throws IOException, CsvValidationException {
        this.name = name;
        this.chassis = chassis;
        this.sections = new HashMap<>();
        
        // Create sections using factory that combines hardpoint + slot data
        MechSectionFactory factory = new MechSectionFactory();
        this.sections.putAll(factory.createSectionsForChassis(chassis));
    }
    
    // Legacy constructor for backward compatibility (uses default slots)
    public Mech(String name) {
        this.name = name;
        this.sections = new HashMap<>();

        // Example section setup with default slot counts
        sections.put("Cockpit",        new MechSection("Cockpit", 1, 5, 0, 0, 0, 2));
        sections.put("Center Torso",   new MechSection("Center Torso", 6, 15, 1, 1, 1, 3));
        sections.put("Left Torso",     new MechSection("Left Torso", 4, 10, 2, 0, 2, 9));
        sections.put("Right Torso",    new MechSection("Right Torso", 4, 10, 1, 2, 1, 9));
        sections.put("Left Arm",       new MechSection("Left Arm", 3, 8, 2, 0, 1, 6));
        sections.put("Right Arm",      new MechSection("Right Arm", 3, 8, 0, 3, 0, 6));
        sections.put("Left Leg",       new MechSection("Left Leg", 5, 12, 0, 0, 0, 3));
        sections.put("Right Leg",      new MechSection("Right Leg", 5, 12, 0, 0, 0, 3));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MechChassis getChassis() {
        return chassis;
    }

    public void setChassis(MechChassis chassis) {
        this.chassis = chassis;
    }

    public HashMap<String, MechSection> getSections() {
        return sections;
    }
    
    public MechSection getSection(String sectionName) {
        return sections.get(sectionName);
    }
    
    public String getMechSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("Mech: %s\n", name));
        if (chassis != null) {
            summary.append(String.format("Chassis: %s (%s, %d tons)\n", 
                chassis.getName(), chassis.getChassisSize(), chassis.getTonnage()));
        }
        summary.append("Sections:\n");
        
        for (MechSection section : sections.values()) {
            summary.append("  ").append(section.getSectionSummary()).append("\n");
        }
        
        return summary.toString();
    }
}