package com.mechbuilder.ui.dnd;

import com.mechbuilder.model.MechSection;
import com.mechbuilder.model.WeaponComponent;
import com.mechbuilder.ui.components.WeaponPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JPanel;

/**
 * Simplified tests for the drag and drop system focusing on core functionality.
 */
class SimpleDragDropTest {
    
    private WeaponComponent testWeapon;
    private WeaponPanel weaponPanel;
    private MechSection testSection;
    private JPanel sectionPanel;
    private MechSectionDropHandler dropHandler;
    
    @BeforeEach
    void setUp() {
        testWeapon = new WeaponComponent("Test Laser", "Energy", 5.0, 8.0, 10, 270, 540, 3.0, 0, 1, 1.0);
        weaponPanel = new WeaponPanel(testWeapon);
        
        testSection = new MechSection("Test Torso", 5, 10, 2, 1, 1, 12);
        sectionPanel = new JPanel();
        dropHandler = new MechSectionDropHandler(testSection, sectionPanel);
    }
    
    @Test
    void testWeaponTransferableCreation() {
        WeaponTransferable transferable = new WeaponTransferable(testWeapon);
        
        assertNotNull(transferable);
        assertEquals(testWeapon, transferable.getWeapon());
        assertTrue(transferable.isDataFlavorSupported(WeaponTransferable.WEAPON_FLAVOR));
    }
    
    @Test
    void testWeaponPanelHasTransferHandler() {
        assertNotNull(weaponPanel.getTransferHandler());
        assertInstanceOf(WeaponTransferHandler.class, weaponPanel.getTransferHandler());
    }
    
    @Test
    void testMechSectionDropHandlerCreation() {
        assertNotNull(dropHandler);
        // Drop handler should be properly initialized
    }
    
    @Test
    void testWeaponPanelGetWeapon() {
        assertEquals(testWeapon, weaponPanel.getWeapon());
        assertEquals("Test Laser", weaponPanel.getWeapon().getName());
        assertEquals("Energy", weaponPanel.getWeapon().getType());
    }
    
    @Test
    void testTransferableDataFlavors() {
        WeaponTransferable transferable = new WeaponTransferable(testWeapon);
        
        assertEquals(2, transferable.getTransferDataFlavors().length);
        assertTrue(transferable.isDataFlavorSupported(WeaponTransferable.WEAPON_FLAVOR));
        assertTrue(transferable.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor));
    }
    
    @Test
    void testSectionHardpointsAccess() {
        assertEquals(2, testSection.getEnergyHardpoints());
        assertEquals(1, testSection.getBallisticHardpoints());
        assertEquals(1, testSection.getMissileHardpoints());
        assertEquals(12, testSection.getTotalSlots());
    }
    
    @Test
    void testWeaponTransferHandlerActions() {
        WeaponTransferHandler handler = new WeaponTransferHandler();
        
        assertEquals(javax.swing.TransferHandler.COPY, handler.getSourceActions(weaponPanel));
    }
}