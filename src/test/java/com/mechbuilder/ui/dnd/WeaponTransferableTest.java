package com.mechbuilder.ui.dnd;

import com.mechbuilder.model.WeaponComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Tests for WeaponTransferable drag and drop data transfer object.
 */
class WeaponTransferableTest {
    
    private WeaponComponent testWeapon;
    private WeaponTransferable transferable;
    
    @BeforeEach
    void setUp() {
        testWeapon = new WeaponComponent("Test Laser", "Energy", 5.0, 10.0, 10, 270, 540, 3.0, 0, 1, 1.0);
        transferable = new WeaponTransferable(testWeapon);
    }
    
    @Test
    void testGetTransferDataFlavors() {
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        
        assertNotNull(flavors);
        assertEquals(2, flavors.length);
        assertTrue(flavors[0].equals(WeaponTransferable.WEAPON_FLAVOR) || 
                  flavors[1].equals(WeaponTransferable.WEAPON_FLAVOR));
        assertTrue(flavors[0].equals(DataFlavor.stringFlavor) || 
                  flavors[1].equals(DataFlavor.stringFlavor));
    }
    
    @Test
    void testIsDataFlavorSupported() {
        assertTrue(transferable.isDataFlavorSupported(WeaponTransferable.WEAPON_FLAVOR));
        assertTrue(transferable.isDataFlavorSupported(DataFlavor.stringFlavor));
        assertFalse(transferable.isDataFlavorSupported(DataFlavor.imageFlavor));
    }
    
    @Test
    void testGetTransferDataWeaponFlavor() throws UnsupportedFlavorException, IOException {
        Object data = transferable.getTransferData(WeaponTransferable.WEAPON_FLAVOR);
        
        assertNotNull(data);
        assertInstanceOf(WeaponComponent.class, data);
        assertEquals(testWeapon, data);
        assertEquals("Test Laser", ((WeaponComponent) data).getName());
    }
    
    @Test
    void testGetTransferDataStringFlavor() throws UnsupportedFlavorException, IOException {
        Object data = transferable.getTransferData(DataFlavor.stringFlavor);
        
        assertNotNull(data);
        assertInstanceOf(String.class, data);
        assertEquals("Test Laser", data);
    }
    
    @Test
    void testGetTransferDataUnsupportedFlavor() {
        assertThrows(UnsupportedFlavorException.class, () -> {
            transferable.getTransferData(DataFlavor.imageFlavor);
        });
    }
    
    @Test
    void testWeaponFlavorClass() {
        assertEquals(WeaponComponent.class, WeaponTransferable.WEAPON_FLAVOR.getRepresentationClass());
        assertEquals("WeaponComponent", WeaponTransferable.WEAPON_FLAVOR.getHumanPresentableName());
    }
    
    @Test
    void testGetWeapon() {
        assertEquals(testWeapon, transferable.getWeapon());
    }
}