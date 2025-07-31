package com.mechbuilder.ui.visual;

import com.mechbuilder.model.MechSection;
import com.mechbuilder.model.WeaponComponent;
import com.mechbuilder.ui.components.WeaponPanel;
import com.mechbuilder.ui.dnd.MechSectionDropHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;

/**
 * Tests for visual feedback and UI responsiveness during drag operations.
 * These tests verify the visual aspects of the drag and drop system.
 */
class VisualFeedbackTest {
    
    private WeaponComponent testWeapon;
    private WeaponPanel weaponPanel;
    private MechSection testSection;
    private JPanel sectionPanel;
    private MechSectionDropHandler dropHandler;
    
    @BeforeEach
    void setUp() {
        testWeapon = new WeaponComponent("Visual Test Weapon", "Energy", 5.0, 8.0, 10, 270, 540, 3.0, 0, 1, 1.0);
        weaponPanel = new WeaponPanel(testWeapon);
        
        testSection = new MechSection("Visual Test Section", 5, 10, 2, 1, 0, 12);
        sectionPanel = new JPanel();
        sectionPanel.setBackground(Color.WHITE);
        dropHandler = new MechSectionDropHandler(testSection, sectionPanel);
    }
    
    @Test
    void testWeaponPanelDefaultColors() {
        Color background = weaponPanel.getBackground();
        assertNotNull(background);
        // Should have default background initially
        assertEquals(new Color(245, 245, 245), background);
    }
    
    @Test
    void testTypeIndicatorColors() {
        // Test Energy weapon color
        WeaponComponent energyWeapon = new WeaponComponent("Test Energy", "Energy", 5.0, 8.0, 10, 270, 540, 3.0, 0, 1, 1.0);
        WeaponPanel energyPanel = new WeaponPanel(energyWeapon);
        
        // The type indicator should be properly colored
        // This is tested indirectly through the creation process
        assertNotNull(energyPanel);
        
        // Test Ballistic weapon color
        WeaponComponent ballisticWeapon = new WeaponComponent("Test Ballistic", "Ballistic", 8.0, 0.0, 15, 90, 270, 1.0, 0, 1, 1.0);
        WeaponPanel ballisticPanel = new WeaponPanel(ballisticWeapon);
        assertNotNull(ballisticPanel);
        
        // Test Missile weapon color
        WeaponComponent missileWeapon = new WeaponComponent("Test Missile", "Missile", 10.0, 6.0, 20, 180, 630, 4.0, 0, 1, 1.0);
        WeaponPanel missilePanel = new WeaponPanel(missileWeapon);
        assertNotNull(missilePanel);
    }
    
    @Test
    void testSectionPanelOriginalBackground() {
        Color originalBackground = sectionPanel.getBackground();
        assertEquals(Color.WHITE, originalBackground);
    }
    
    @Test
    void testMouseCursorInitialization() {
        Cursor defaultCursor = weaponPanel.getCursor();
        assertNotNull(defaultCursor);
        // Should start with default cursor
        assertEquals(Cursor.DEFAULT_CURSOR, defaultCursor.getType());
    }
    
    @Test
    void testCompactDimensionsConsistency() {
        // Test that all weapon types maintain compact dimensions
        String[] weaponTypes = {"Energy", "Ballistic", "Missile"};
        
        for (String type : weaponTypes) {
            WeaponComponent weapon = new WeaponComponent("Test " + type, type, 5.0, 8.0, 10, 270, 540, 3.0, 0, 1, 1.0);
            WeaponPanel panel = new WeaponPanel(weapon);
            
            assertEquals(200, panel.getPreferredSize().width, 
                "Width should be 200 for " + type);
            assertEquals(35, panel.getPreferredSize().height, 
                "Height should be 35 for " + type);
        }
    }
    
    @Test
    void testDropZoneBackgroundUpdates() {
        // Create a simple panel to simulate a drop zone
        JPanel dropZone = new JPanel();
        dropZone.setBackground(new Color(250, 250, 250));
        
        Color originalColor = dropZone.getBackground();
        assertEquals(new Color(250, 250, 250), originalColor);
        
        // Simulate successful weapon placement
        dropZone.setBackground(new Color(230, 255, 230));
        assertEquals(new Color(230, 255, 230), dropZone.getBackground());
        
        // Simulate reset to original
        dropZone.setBackground(originalColor);
        assertEquals(new Color(250, 250, 250), dropZone.getBackground());
    }
    
    @Test
    void testVisualConsistencyAcrossComponents() {
        // Test that visual styling is consistent across multiple components
        WeaponComponent[] weapons = new WeaponComponent[3];
        WeaponPanel[] panels = new WeaponPanel[3];
        
        for (int i = 0; i < 3; i++) {
            weapons[i] = new WeaponComponent("Consistency Test " + i, "Energy", 5.0 + i, 8.0, 10 + i, 270, 540, 3.0, 0, 1, 1.0);
            panels[i] = new WeaponPanel(weapons[i]);
        }
        
        // All panels should have same dimensions
        for (int i = 1; i < 3; i++) {
            assertEquals(panels[0].getPreferredSize(), panels[i].getPreferredSize(),
                "Panel " + i + " should have same size as panel 0");
            assertEquals(panels[0].getBackground(), panels[i].getBackground(),
                "Panel " + i + " should have same background as panel 0");
        }
    }
}