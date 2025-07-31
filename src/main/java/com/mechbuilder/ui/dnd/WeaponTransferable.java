package com.mechbuilder.ui.dnd;

import com.mechbuilder.model.WeaponComponent;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Custom Transferable implementation for WeaponComponent objects.
 * This enables drag and drop of weapons with full object data.
 */
public class WeaponTransferable implements Transferable {
    
    public static final DataFlavor WEAPON_FLAVOR = new DataFlavor(WeaponComponent.class, "WeaponComponent");
    
    private final WeaponComponent weapon;
    
    public WeaponTransferable(WeaponComponent weapon) {
        this.weapon = weapon;
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{WEAPON_FLAVOR, DataFlavor.stringFlavor};
    }
    
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return WEAPON_FLAVOR.equals(flavor) || DataFlavor.stringFlavor.equals(flavor);
    }
    
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (WEAPON_FLAVOR.equals(flavor)) {
            return weapon;
        } else if (DataFlavor.stringFlavor.equals(flavor)) {
            return weapon.getName(); // Fallback for external applications
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
    
    public WeaponComponent getWeapon() {
        return weapon;
    }
}