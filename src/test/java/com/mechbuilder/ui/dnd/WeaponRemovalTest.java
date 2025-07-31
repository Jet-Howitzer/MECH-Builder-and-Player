package com.mechbuilder.ui.dnd;

import com.mechbuilder.model.MechSection;
import com.mechbuilder.model.WeaponComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JPanel;

/**
 * Tests for weapon removal functionality in the drag and drop system.
 */
class WeaponRemovalTest {
    
    private MechSection testSection;
    private JPanel sectionPanel;
    private MechSectionDropHandler dropHandler;
    private WeaponComponent testWeapon1;
    private WeaponComponent testWeapon2;
    
    @BeforeEach
    void setUp() {
        testSection = new MechSection("Test Torso", 5, 10, 2, 1, 1, 12);
        sectionPanel = new JPanel();
        dropHandler = new MechSectionDropHandler(testSection, sectionPanel);
        
        testWeapon1 = new WeaponComponent("Test Laser", "Energy", 5.0, 8.0, 10, 270, 540, 3.0, 0, 1, 1.0);
        testWeapon2 = new WeaponComponent("Test Cannon", "Ballistic", 8.0, 0.0, 15, 90, 270, 1.0, 0, 1, 1.0);
    }
    
    @Test
    void testRemoveWeaponFromEmptySlot() {
        // Should handle removal from empty slot gracefully
        assertDoesNotThrow(() -> {
            dropHandler.removeWeapon(0);
        });
        
        assertNull(dropHandler.getWeaponInSlot(0));
    }
    
    @Test
    void testRemoveWeaponAfterAdding() {
        // Simulate adding a weapon (normally done through importData)
        addWeaponDirectly(0, testWeapon1);
        
        // Verify weapon was added
        assertEquals(testWeapon1, dropHandler.getWeaponInSlot(0));
        
        // Remove the weapon
        dropHandler.removeWeapon(0);
        
        // Verify weapon was removed
        assertNull(dropHandler.getWeaponInSlot(0));
    }
    
    @Test
    void testRemoveSpecificWeaponFromMultiple() {
        // Add multiple weapons
        addWeaponDirectly(0, testWeapon1);
        addWeaponDirectly(1, testWeapon2);
        
        // Verify both are present
        assertEquals(testWeapon1, dropHandler.getWeaponInSlot(0));
        assertEquals(testWeapon2, dropHandler.getWeaponInSlot(1));
        
        // Remove only the first weapon
        dropHandler.removeWeapon(0);
        
        // Verify correct weapon was removed
        assertNull(dropHandler.getWeaponInSlot(0));
        assertEquals(testWeapon2, dropHandler.getWeaponInSlot(1));
    }
    
    @Test
    void testContextMenuSetup() {
        JPanel dropZone = new JPanel();
        
        // Should not throw exception when setting up context menu
        assertDoesNotThrow(() -> {
            dropHandler.setupContextMenu(dropZone, 0);
        });
        
        // Verify mouse listeners were added
        assertTrue(dropZone.getMouseListeners().length > 0);
    }
    
    @Test
    void testGetWeaponInSlotReturnsCorrectWeapon() {
        addWeaponDirectly(0, testWeapon1);
        addWeaponDirectly(2, testWeapon2);
        
        assertEquals(testWeapon1, dropHandler.getWeaponInSlot(0));
        assertNull(dropHandler.getWeaponInSlot(1)); // Empty slot
        assertEquals(testWeapon2, dropHandler.getWeaponInSlot(2));
    }
    
    @Test
    void testRemovalFromHighIndexSlot() {
        // Test removal from a higher index slot
        addWeaponDirectly(3, testWeapon1);
        
        assertEquals(testWeapon1, dropHandler.getWeaponInSlot(3));
        
        dropHandler.removeWeapon(3);
        
        assertNull(dropHandler.getWeaponInSlot(3));
    }
    
    @Test
    void testSlotReuseAfterRemoval() {
        // Add weapon, remove it, then add another
        addWeaponDirectly(0, testWeapon1);
        assertEquals(testWeapon1, dropHandler.getWeaponInSlot(0));
        
        dropHandler.removeWeapon(0);
        assertNull(dropHandler.getWeaponInSlot(0));
        
        addWeaponDirectly(0, testWeapon2);
        assertEquals(testWeapon2, dropHandler.getWeaponInSlot(0));
    }
    
    /**
     * Helper method to directly add weapons for testing (bypasses transfer system)
     */
    private void addWeaponDirectly(int slotIndex, WeaponComponent weapon) {
        // Directly manipulate the equipped weapons map for testing
        // In real usage, this would happen through importData()
        try {
            java.lang.reflect.Field field = dropHandler.getClass().getDeclaredField("equippedWeapons");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<Integer, WeaponComponent> equippedWeapons = 
                (java.util.Map<Integer, WeaponComponent>) field.get(dropHandler);
            equippedWeapons.put(slotIndex, weapon);
        } catch (Exception e) {
            fail("Could not access equippedWeapons field for testing: " + e.getMessage());
        }
    }
}