package com.mechbuilder.ui.dnd;

import com.mechbuilder.model.WeaponComponent;
import com.mechbuilder.ui.components.WeaponPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JLabel;
import javax.swing.TransferHandler;
import java.awt.datatransfer.Transferable;

/**
 * Tests for WeaponTransferHandler drag export functionality.
 */
class WeaponTransferHandlerTest {
    
    private WeaponTransferHandler transferHandler;
    private WeaponComponent testWeapon;
    private WeaponPanel weaponPanel;
    
    @BeforeEach
    void setUp() {
        transferHandler = new WeaponTransferHandler();
        
        testWeapon = new WeaponComponent("Test Cannon", "Ballistic", 8.0, 0.0, 15, 90, 270, 1.0, 0, 1, 1.0);
        weaponPanel = new WeaponPanel(testWeapon);
    }
    
    @Test
    void testGetSourceActions() {
        int actions = transferHandler.getSourceActions(weaponPanel);
        assertEquals(TransferHandler.COPY, actions);
    }
    
    @Test
    void testCreateTransferableFromWeaponPanel() {
        Transferable transferable = transferHandler.createTransferable(weaponPanel);
        
        assertNotNull(transferable);
        assertInstanceOf(WeaponTransferable.class, transferable);
        
        WeaponTransferable weaponTransferable = (WeaponTransferable) transferable;
        assertEquals(testWeapon, weaponTransferable.getWeapon());
    }
    
    @Test
    void testCreateTransferableFromNonWeaponPanel() {
        JLabel nonWeaponPanel = new JLabel("Not a weapon panel");
        Transferable transferable = transferHandler.createTransferable(nonWeaponPanel);
        
        assertNull(transferable);
    }
    
    @Test
    void testExportDoneDoesNothing() {
        // Should not throw any exceptions for COPY operation
        Transferable transferable = transferHandler.createTransferable(weaponPanel);
        assertDoesNotThrow(() -> {
            transferHandler.exportDone(weaponPanel, transferable, TransferHandler.COPY);
        });
    }
}