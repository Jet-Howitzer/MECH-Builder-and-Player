package com.mechbuilder;

import com.mechbuilder.data.MechChassisRepository;
import com.mechbuilder.data.MechSectionFactory;
import com.mechbuilder.model.Mech;
import com.mechbuilder.model.MechChassis;
import com.mechbuilder.model.MechSection;
import com.mechbuilder.ui.MechBuilderUIv2;
import com.opencsv.exceptions.CsvValidationException;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Main application class for Mech Builder
 * Demonstrates the enhanced MechSection functionality with slot integration
 */
public class MechBuilderApplication {
    
    public static void main(String[] args) {
        if (args.length > 0 && "demo".equals(args[0])) {
            // Demo mode - show enhanced MechSection functionality
            runDemo();
        } else {
            // Normal mode - run the GUI
            SwingUtilities.invokeLater(() -> {
                try {
                    new MechBuilderUIv2();
                } catch (IOException | CsvValidationException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    
    /**
     * Demonstrates the enhanced MechSection functionality
     */
    private static void runDemo() {
        try {
            System.out.println("🤖 MECH BUILDER - Enhanced Section Demo");
            System.out.println("=====================================");
            
            // Load a chassis
            MechChassisRepository chassisRepo = new MechChassisRepository();
            List<MechChassis> chassisList = chassisRepo.loadAll();
            
            if (chassisList.isEmpty()) {
                System.out.println("❌ No chassis data found!");
                return;
            }
            
            // Pick the first chassis for demo
            MechChassis testChassis = chassisList.get(0);
            System.out.println("🎯 Testing with chassis: " + testChassis.getName());
            System.out.println("   Size: " + testChassis.getChassisSize());
            System.out.println("   Tonnage: " + testChassis.getTonnage());
            System.out.println();
            
            // Create sections using the factory
            MechSectionFactory factory = new MechSectionFactory();
            Map<String, MechSection> sections = factory.createSectionsForChassis(testChassis);
            
            System.out.println("📊 ENHANCED SECTIONS (Hardpoints + Slots):");
            System.out.println("==========================================");
            
            for (Map.Entry<String, MechSection> entry : sections.entrySet()) {
                MechSection section = entry.getValue();
                System.out.println("🔧 " + section.getName() + ":");
                System.out.println("   Hardpoints: " + section.getEnergyHardpoints() + 
                                 " energy, " + section.getBallisticHardpoints() + 
                                 " ballistic, " + section.getMissileHardpoints() + " missile");
                System.out.println("   Slots: " + section.getUsedSlots() + "/" + 
                                 section.getTotalSlots() + " (" + 
                                 section.getAvailableSlots() + " available)");
                System.out.println();
            }
            
            // Create a complete mech
            Mech testMech = new Mech("Test Mech", testChassis);
            System.out.println("🚀 COMPLETE MECH SUMMARY:");
            System.out.println("========================");
            System.out.println(testMech.getMechSummary());
            
        } catch (IOException | CsvValidationException e) {
            System.err.println("❌ Error during demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}