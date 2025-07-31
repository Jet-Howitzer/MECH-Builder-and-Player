package com.mechbuilder.ui.components;

import com.mechbuilder.model.WeaponComponent;
import com.mechbuilder.ui.dnd.WeaponTransferHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

/**
 * Tests for WeaponPanel UI component.
 */
class WeaponPanelTest {
    
    private WeaponComponent energyWeapon;
    private WeaponComponent ballisticWeapon;
    private WeaponComponent missileWeapon;
    
    @BeforeEach
    void setUp() {
        energyWeapon = new WeaponComponent("Large Laser", "Energy", 5.0, 8.0, 8, 450, 900, 3.0, 0, 1, 1.0);
        ballisticWeapon = new WeaponComponent("AC/20", "Ballistic", 14.0, 0.0, 20, 90, 270, 1.0, 0, 1, 1.0);
        missileWeapon = new WeaponComponent("LRM 20", "Missile", 10.0, 6.0, 20, 180, 630, 4.0, 0, 1, 1.0);
    }
    
    @Test
    void testWeaponPanelCreation() {
        WeaponPanel panel = new WeaponPanel(energyWeapon);
        
        assertNotNull(panel);
        assertEquals(energyWeapon, panel.getWeapon());
        assertInstanceOf(WeaponTransferHandler.class, panel.getTransferHandler());
    }
    
    @Test
    void testCompactDimensions() {
        WeaponPanel panel = new WeaponPanel(energyWeapon);
        
        Dimension expected = new Dimension(200, 35);
        assertEquals(expected, panel.getPreferredSize());
        assertEquals(expected, panel.getMaximumSize());
        assertEquals(expected, panel.getMinimumSize());
    }
    
    @Test
    void testBorderLayoutUsed() {
        WeaponPanel panel = new WeaponPanel(energyWeapon);
        assertInstanceOf(BorderLayout.class, panel.getLayout());
    }
    
    @Test
    void testEnergyWeaponTypeIndicator() {
        WeaponPanel panel = new WeaponPanel(energyWeapon);
        
        // Find the type indicator label (should be in WEST position)
        Component westComponent = ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.WEST);
        assertInstanceOf(JLabel.class, westComponent);
        
        JLabel typeLabel = (JLabel) westComponent;
        assertEquals("E", typeLabel.getText());
        assertEquals(Color.RED, typeLabel.getForeground());
        assertEquals(new Color(255, 200, 200), typeLabel.getBackground());
    }
    
    @Test
    void testBallisticWeaponTypeIndicator() {
        WeaponPanel panel = new WeaponPanel(ballisticWeapon);
        
        Component westComponent = ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.WEST);
        JLabel typeLabel = (JLabel) westComponent;
        
        assertEquals("B", typeLabel.getText());
        assertEquals(Color.DARK_GRAY, typeLabel.getForeground());
        assertEquals(new Color(200, 255, 200), typeLabel.getBackground());
    }
    
    @Test
    void testMissileWeaponTypeIndicator() {
        WeaponPanel panel = new WeaponPanel(missileWeapon);
        
        Component westComponent = ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.WEST);
        JLabel typeLabel = (JLabel) westComponent;
        
        assertEquals("M", typeLabel.getText());
        assertEquals(Color.BLUE, typeLabel.getForeground());
        assertEquals(new Color(200, 200, 255), typeLabel.getBackground());
    }
    
    @Test
    void testMainLabelContent() {
        WeaponPanel panel = new WeaponPanel(energyWeapon);
        
        Component centerComponent = ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        assertInstanceOf(JLabel.class, centerComponent);
        
        JLabel mainLabel = (JLabel) centerComponent;
        String text = mainLabel.getText();
        
        assertTrue(text.contains("Large Laser"));
        assertTrue(text.contains("E"));  // Type abbreviation
        assertTrue(text.contains("D:8")); // Damage
        assertTrue(text.contains("T:5.0")); // Tonnage
    }
    
    @Test
    void testCompactHTMLFormatting() {
        WeaponPanel panel = new WeaponPanel(ballisticWeapon);
        
        Component centerComponent = ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        JLabel mainLabel = (JLabel) centerComponent;
        String text = mainLabel.getText();
        
        assertTrue(text.startsWith("<html>"));
        assertTrue(text.contains("<b>AC/20</b>"));
        assertTrue(text.contains("<small>"));
        assertTrue(text.contains("B | D:20 | T:14.0"));
    }
}