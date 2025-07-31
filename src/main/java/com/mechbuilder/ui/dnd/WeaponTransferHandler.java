package com.mechbuilder.ui.dnd;

import com.mechbuilder.model.WeaponComponent;
import com.mechbuilder.ui.components.WeaponPanel;
import javax.swing.*;
import java.awt.datatransfer.Transferable;

/**
 * TransferHandler for weapon components in the arsenal panel.
 * Handles exporting weapons for drag operations.
 */
public class WeaponTransferHandler extends TransferHandler {
    
    @Override
    public int getSourceActions(JComponent c) {
        // Weapons can be copied (not moved) from arsenal
        return COPY;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof WeaponPanel) {
            WeaponPanel weaponPanel = (WeaponPanel) c;
            WeaponComponent weapon = weaponPanel.getWeapon();
            return new WeaponTransferable(weapon);
        }
        return null;
    }
    
    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        // Arsenal weapons stay in place (COPY operation)
        // No cleanup needed
    }
}