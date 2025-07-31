package com.mechbuilder.model;

import java.util.HashMap;

public class Mech {
    private String name;
    private HashMap<String, MechSection> sections;

    public Mech(String name) {
        this.name = name;
        sections = new HashMap<>();

        // Example section setup (replace with real data later)
        sections.put("Cockpit",        new MechSection("Cockpit", 1, 5, 0, 0, 0));
        sections.put("Center Torso",   new MechSection("Center Torso", 6, 15, 1, 1, 1));
        sections.put("Left Torso",     new MechSection("Left Torso", 4, 10, 2, 0, 2));
        sections.put("Right Torso",    new MechSection("Right Torso", 4, 10, 1, 2, 1));
        sections.put("Left Arm",       new MechSection("Left Arm", 3, 8, 2, 0, 1));
        sections.put("Right Arm",      new MechSection("Right Arm", 3, 8, 0, 3, 0));
        sections.put("Left Leg",       new MechSection("Left Leg", 5, 12, 0, 0, 0));
        sections.put("Right Leg",      new MechSection("Right Leg", 5, 12, 0, 0, 0));
    }

    public String getName() {
        return name;
    }

    public MechSection getSection(String sectionName) {
        return sections.get(sectionName);
    }
}