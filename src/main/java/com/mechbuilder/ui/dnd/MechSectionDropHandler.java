package com.mechbuilder.ui.dnd;

import com.mechbuilder.model.MechSection;
import com.mechbuilder.model.WeaponComponent;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * TransferHandler for mech sections that can receive weapon drops.
 * Handles importing weapons into mech section slots.
 */
public class MechSectionDropHandler extends TransferHandler {
    
    private final MechSection mechSection;
    private final JPanel sectionPanel;
    private final java.util.Map<Integer, WeaponComponent> equippedWeapons = new java.util.HashMap<>();
    
    private final Color originalBackground;
    private Runnable onWeaponsChangedCallback;
    
    public MechSectionDropHandler(MechSection mechSection, JPanel sectionPanel) {
        this.mechSection = mechSection;
        this.sectionPanel = sectionPanel;
        this.originalBackground = sectionPanel.getBackground();
        
        // Add mouse listener to reset background when drag exits
        sectionPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                // Reset background when mouse/drag exits
                if (!sectionPanel.getBounds().contains(e.getPoint())) {
                    sectionPanel.setBackground(originalBackground);
                    sectionPanel.repaint();
                }
            }
        });
    }
    
    @Override
    public boolean canImport(TransferSupport support) {
        // Only accept drops (not paste operations)
        if (!support.isDrop()) {
            return false;
        }
        
        // Check if we support the data flavor
        if (!support.isDataFlavorSupported(WeaponTransferable.WEAPON_FLAVOR)) {
            return false;
        }
        
        try {
            // Get the weapon being dropped
            Transferable transferable = support.getTransferable();
            WeaponComponent weapon = (WeaponComponent) transferable.getTransferData(WeaponTransferable.WEAPON_FLAVOR);
            
            // Check if this section can accept this weapon type
            if (!canAcceptWeapon(weapon)) {
                // Add visual feedback for rejection
                sectionPanel.setBackground(new java.awt.Color(255, 200, 200)); // Light red
                return false;
            }
            
            // Check if there are available hardpoints
            if (!hasAvailableHardpoints()) {
                // Add visual feedback for no available slots
                sectionPanel.setBackground(new java.awt.Color(255, 240, 200)); // Light orange
                return false;
            }
            
            // Add visual feedback for valid drop target
            sectionPanel.setBackground(new java.awt.Color(200, 255, 200)); // Light green
            
            // Accept the transfer
            support.setDropAction(COPY);
            return true;
            
        } catch (UnsupportedFlavorException | IOException e) {
            return false;
        }
    }
    
    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        
        try {
            // Get the weapon being dropped
            Transferable transferable = support.getTransferable();
            WeaponComponent weapon = (WeaponComponent) transferable.getTransferData(WeaponTransferable.WEAPON_FLAVOR);
            
            // Find an available slot and add the weapon
            if (addWeaponToSection(weapon)) {
                // Reset background and update the UI
                sectionPanel.setBackground(originalBackground);
                updateSectionDisplay();
                return true;
            }
            
        } catch (UnsupportedFlavorException | IOException e) {
            System.err.println("Error importing weapon: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if this section can accept the given weapon type
     */
    private boolean canAcceptWeapon(WeaponComponent weapon) {
        String weaponType = weapon.getType();
        
        // Legs can't have weapons
        if (mechSection.getName().contains("Leg")) {
            return false;
        }
        
        // For now, all non-leg sections can accept any weapon type
        // TODO: Add hardpoint type restrictions (Energy/Ballistic/Missile)
        return true;
    }
    
    /**
     * Check if there are available hardpoints in this section
     */
    private boolean hasAvailableHardpoints() {
        int totalHardpoints = mechSection.getEnergyHardpoints() + 
                            mechSection.getBallisticHardpoints() + 
                            mechSection.getMissileHardpoints();
        
        // Check against currently equipped weapons
        return equippedWeapons.size() < totalHardpoints;
    }
    
    /**
     * Add the weapon to this section
     */
    private boolean addWeaponToSection(WeaponComponent weapon) {
        if (hasAvailableHardpoints()) {
            // Find the first available slot
            int slotIndex = findFirstAvailableSlot();
            if (slotIndex >= 0) {
                equippedWeapons.put(slotIndex, weapon);
                System.out.println("Added " + weapon.getName() + " to " + mechSection.getName() + 
                                 " (" + equippedWeapons.size() + "/" + getTotalHardpoints() + " slots used)");
                notifyWeaponsChanged();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Find the first available slot index
     */
    private int findFirstAvailableSlot() {
        int totalHardpoints = getTotalHardpoints();
        for (int i = 0; i < totalHardpoints; i++) {
            if (!equippedWeapons.containsKey(i)) {
                return i;
            }
        }
        return -1; // No available slots
    }
    
    private int getTotalHardpoints() {
        return mechSection.getEnergyHardpoints() + 
               mechSection.getBallisticHardpoints() + 
               mechSection.getMissileHardpoints();
    }
    
    /**
     * Update the visual display of this section
     */
    private void updateSectionDisplay() {
        // Find weapon slot drop zones specifically by looking for components with slot labels
        Component[] components = sectionPanel.getComponents();
        int slotIndex = 0;
        
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                
                // Check if this panel contains a weapon slot label (1:, 2:, etc.)
                boolean isWeaponSlot = false;
                Component[] panelComponents = panel.getComponents();
                for (Component pComp : panelComponents) {
                    if (pComp instanceof JLabel && ((JLabel) pComp).getText().matches("\\d+:")) {
                        isWeaponSlot = true;
                        break;
                    }
                }
                
                // Only process weapon slot panels
                if (isWeaponSlot) {
                    // Find the center label component (the placeholder text)
                    for (Component dzComp : panelComponents) {
                        if (dzComp instanceof JLabel && !((JLabel) dzComp).getText().matches("\\d+:")) {
                            JLabel centerLabel = (JLabel) dzComp;
                            
                            // Check if this slot has a weapon
                            WeaponComponent weapon = equippedWeapons.get(slotIndex);
                            if (weapon != null) {
                                // Show equipped weapon
                                centerLabel.setText(weapon.getName());
                                centerLabel.setFont(centerLabel.getFont().deriveFont(Font.BOLD, 9f));
                                centerLabel.setForeground(Color.BLACK);
                                panel.setBackground(new Color(230, 255, 230)); // Light green
                                // Add tooltip for equipped weapon
                                panel.setToolTipText("Double-click to remove " + weapon.getName());
                            } else {
                                // Show empty weapon slot
                                centerLabel.setText("Drop weapon here");
                                centerLabel.setFont(centerLabel.getFont().deriveFont(Font.ITALIC, 9f));
                                centerLabel.setForeground(new Color(139, 0, 0)); // Dark red
                                panel.setBackground(new Color(255, 240, 240)); // Light red
                                panel.setToolTipText("Double-click to remove weapon");
                            }
                            
                            slotIndex++;
                            break;
                        }
                    }
                    
                    panel.revalidate();
                    panel.repaint();
                }
            }
        }
        
        sectionPanel.repaint();
    }
    
    /**
     * Removes a weapon from the specified slot
     */
    public void removeWeapon(int slotIndex) {
        WeaponComponent removedWeapon = equippedWeapons.remove(slotIndex);
        if (removedWeapon != null) {
            updateSectionDisplay();
            System.out.println("Removed " + removedWeapon.getName() + " from " + mechSection.getName());
            notifyWeaponsChanged();
        }
    }
    
    /**
     * Gets the weapon equipped in the specified slot
     */
    public WeaponComponent getWeaponInSlot(int slotIndex) {
        return equippedWeapons.get(slotIndex);
    }
    
    /**
     * Get all currently equipped weapons in this section
     */
    public List<WeaponComponent> getEquippedWeapons() {
        return new ArrayList<>(equippedWeapons.values());
    }
    
    /**
     * Calculate the total tonnage of equipped items in this section
     */
    public double getEquippedTonnage() {
        double totalTonnage = 0.0;
        for (WeaponComponent weapon : equippedWeapons.values()) {
            totalTonnage += weapon.getTonnage();
        }
        return totalTonnage;
    }
    
    /**
     * Get the number of equipped items in this section
     */
    public int getEquippedCount() {
        return equippedWeapons.size();
    }
    
    /**
     * Get the section panel for UI operations
     */
    public JPanel getSectionPanel() {
        return sectionPanel;
    }
    
    /**
     * Set callback to be called when weapons change
     */
    public void setOnWeaponsChangedCallback(Runnable callback) {
        this.onWeaponsChangedCallback = callback;
    }
    
    /**
     * Notify that weapons have changed
     */
    private void notifyWeaponsChanged() {
        if (onWeaponsChangedCallback != null) {
            onWeaponsChangedCallback.run();
        }
    }
    
    /**
     * Sets up a context menu for weapon removal on a drop zone
     */
    public void setupContextMenu(JPanel dropZone, int slotIndex) {
        dropZone.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e, slotIndex);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e, slotIndex);
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Double-click to remove weapon
                if (e.getClickCount() == 2) {
                    WeaponComponent weapon = equippedWeapons.get(slotIndex);
                    if (weapon != null) {
                        removeWeapon(slotIndex);
                        System.out.println("Double-clicked to remove " + weapon.getName() + " from " + mechSection.getName());
                    }
                }
            }
        });
    }
    
    /**
     * Shows context menu for weapon management
     */
    private void showContextMenu(MouseEvent e, int slotIndex) {
        WeaponComponent weapon = equippedWeapons.get(slotIndex);
        
        if (weapon != null) {
            JPopupMenu contextMenu = new JPopupMenu();
            
            JMenuItem removeItem = new JMenuItem("Remove " + weapon.getName());
            removeItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    removeWeapon(slotIndex);
                }
            });
            
            JMenuItem weaponInfo = new JMenuItem("Info: " + weapon.getDamage() + " dmg, " + weapon.getTonnage() + "t");
            weaponInfo.setEnabled(false); // Info only, not clickable
            
            contextMenu.add(weaponInfo);
            contextMenu.addSeparator();
            contextMenu.add(removeItem);
            
            contextMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}