package com.mechbuilder.ui;

import com.opencsv.exceptions.CsvValidationException;
import com.mechbuilder.data.MechChassisRepository;
import com.mechbuilder.data.MechSectionFactory;
import com.mechbuilder.data.WeaponRepository;
import com.mechbuilder.model.MechChassis;
import com.mechbuilder.model.MechSection;
import com.mechbuilder.model.WeaponComponent;
import com.mechbuilder.ui.components.WeaponArsenalPanel;
import com.mechbuilder.ui.components.EquippedWeaponsPanel;
import com.mechbuilder.ui.components.ArmorAllocationPanel;
import com.mechbuilder.ui.dnd.MechSectionDropHandler;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unified Mech Builder UI
 * Combines V2 functionality (chassis selection, repository pattern) 
 * with V1 layout approach (clean GridBagLayout, tonnage tracking)
 */
public class MechBuilderUI extends JFrame {
    
    // Data repositories and lists
    private final MechChassisRepository chassisRepo;
    private final WeaponRepository weaponRepo;
    private final MechSectionFactory sectionFactory;
    
    private List<MechChassis> chassisList;
    private List<WeaponComponent> weaponsList;
    private Map<String, MechSection> currentSections;
    private Map<String, Integer> armorHpPerTon; // Armor type -> HP per ton
    
    // UI Components
    private JComboBox<String> chassisDropdown;
    private JComboBox<String> armorDropdown;
    private JPanel mechPanel;
    private Map<String, JLabel> tonnageLabels;
    private Map<String, List<JComboBox<String>>> sectionDropdowns;
    private WeaponArsenalPanel weaponArsenalPanel;
    private EquippedWeaponsPanel equippedWeaponsPanel;
    private ArmorAllocationPanel armorAllocationPanel;
    private Map<String, MechSectionDropHandler> sectionDropHandlers;
    
    public MechBuilderUI() throws IOException, CsvValidationException {
        // Initialize repositories
        chassisRepo = new MechChassisRepository();
        weaponRepo = new WeaponRepository();
        sectionFactory = new MechSectionFactory();
        
        // Load data
        chassisList = chassisRepo.loadAll();
        weaponsList = weaponRepo.loadAll();
        loadArmorData();
        
        tonnageLabels = new HashMap<>();
        sectionDropdowns = new HashMap<>();
        sectionDropHandlers = new HashMap<>();
        
        initializeUI();
        if (!chassisList.isEmpty()) {
            updateMechLayout();
        }
    }
    
    private void initializeUI() {
        setTitle("Mech Builder - Unified Edition with Drag & Drop");
        setSize(1600, 900);  // Wider to accommodate arsenal panel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Top panel with chassis and armor selection
        JPanel topPanel = new JPanel(new FlowLayout());
        
        // Chassis selection
        topPanel.add(new JLabel("Select Mech Chassis:"));
        
        chassisDropdown = new JComboBox<>();
        for (MechChassis chassis : chassisList) {
            chassisDropdown.addItem(chassis.getName());
        }
        chassisDropdown.addActionListener(e -> updateMechLayout());
        topPanel.add(chassisDropdown);
        
        // Add some spacing
        topPanel.add(Box.createHorizontalStrut(20));
        
        // Armor selection
        topPanel.add(new JLabel("Select Armor Type:"));
        
        armorDropdown = new JComboBox<>();
        armorDropdown.addItem("MACM (30 HP/Ton, Plate)");
        armorDropdown.addItem("AS-19 (35 HP/Ton, Refractive)");
        armorDropdown.addItem("AX5 (35 HP/Ton, Refractive)");
        armorDropdown.addItem("PAS (36 HP/Ton, Plate)");
        armorDropdown.addItem("GRP (40 HP/Ton, Plate)");
        armorDropdown.addItem("MLC (40 HP/Ton, Refractive)");
        armorDropdown.addItem("TKM (45 HP/Ton, Plate)");
        armorDropdown.addItem("ARP (45 HP/Ton, Refractive)");
        armorDropdown.addItem("TLW (48 HP/Ton, Plate)");
        armorDropdown.addItem("HCC (54 HP/Ton, Plate)");
        armorDropdown.addItem("CNC (75 HP/Ton, Ablative)");
        armorDropdown.addItem("FRA (80 HP/Ton, Ablative)");
        armorDropdown.addItem("ROP (90 HP/Ton, Ablative)");
        armorDropdown.addItem("KS MIV (100 HP/Ton, Ablative)");
        
        // Set default selection to a middle-tier armor
        armorDropdown.setSelectedIndex(4); // GRP (40 HP/Ton, Plate)
        
        armorDropdown.addActionListener(e -> updateArmorDisplay());
        topPanel.add(armorDropdown);
        
        // Add some spacing
        topPanel.add(Box.createHorizontalStrut(20));
        
        // Reset button to restore all HP to maximum
        JButton resetButton = new JButton("Reset All HP");
        resetButton.setFont(new Font("Arial", Font.BOLD, 11));
        resetButton.setBackground(new Color(255, 255, 200));
        resetButton.setForeground(new Color(0, 100, 0)); // Dark green
        resetButton.addActionListener(e -> resetAllSectionHP());
        topPanel.add(resetButton);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Main content area with mech layout and weapon arsenal
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Center panel for mech layout
        mechPanel = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(mechPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Right panel for weapon arsenal
        try {
            weaponArsenalPanel = new WeaponArsenalPanel();
            mainPanel.add(weaponArsenalPanel, BorderLayout.EAST);
        } catch (IOException | CsvValidationException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading weapon arsenal: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        // Bottom panel containing equipped weapons, damage control, and armor allocation
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Left side - equipped weapons overview
        try {
            equippedWeaponsPanel = new EquippedWeaponsPanel();
            bottomPanel.add(equippedWeaponsPanel, BorderLayout.WEST);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading equipped weapons panel: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        // Center - spacer panel (damage control is now integrated into each section)
        JPanel centerSpacer = new JPanel();
        centerSpacer.setPreferredSize(new Dimension(300, 100));
        centerSpacer.setBackground(new Color(240, 240, 240));
        centerSpacer.setBorder(BorderFactory.createTitledBorder("Damage Control"));
        centerSpacer.add(new JLabel("Damage control is now integrated into each mech section"));
        bottomPanel.add(centerSpacer, BorderLayout.CENTER);
        
        // Right side - armor allocation
        try {
            armorAllocationPanel = new ArmorAllocationPanel();
            // Set callback to update section displays when armor changes
            armorAllocationPanel.setOnArmorChangedCallback(this::updateArmorAllocation);
            bottomPanel.add(armorAllocationPanel, BorderLayout.EAST);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading armor allocation panel: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private void updateMechLayout() {
        mechPanel.removeAll();
        tonnageLabels.clear();
        sectionDropdowns.clear();
        sectionDropHandlers.clear();
        
        String selectedName = (String) chassisDropdown.getSelectedItem();
        if (selectedName == null) return;
        
        // Find selected chassis
        MechChassis selectedChassis = chassisList.stream()
                .filter(chassis -> chassis.getName().equals(selectedName))
                .findFirst()
                .orElse(null);
        
        if (selectedChassis == null) return;
        
        try {
            // Update armor allocation panel with new chassis
            if (armorAllocationPanel != null) {
                armorAllocationPanel.setChassis(selectedChassis);
            }
            
            // Create sections with both hardpoints and slots using our factory
            currentSections = sectionFactory.createSectionsForChassis(selectedChassis);
            
            // Use V1's clean grid layout approach with dynamic slot counts
            // Head/Cockpit - top center
            if (currentSections.containsKey("Head")) {
                addSection("Head", 2, 0, 1, 1);
            }
            
            // Arms - outer sides  
            if (currentSections.containsKey("Left Arm")) {
                addSection("Left Arm", 0, 1, 1, 1);
            }
            if (currentSections.containsKey("Right Arm")) {
                addSection("Right Arm", 4, 1, 1, 1);
            }
            
            // Torsos - center area
            if (currentSections.containsKey("Left Torso")) {
                addSection("Left Torso", 1, 1, 1, 1);
            }
            if (currentSections.containsKey("Center Torso")) {
                addSection("Center Torso", 2, 1, 1, 1);
            }
            if (currentSections.containsKey("Right Torso")) {
                addSection("Right Torso", 3, 1, 1, 1);
            }
            
            // Legs - bottom
            if (currentSections.containsKey("Left Leg")) {
                addSection("Left Leg", 1, 2, 1, 1);
            }
            if (currentSections.containsKey("Right Leg")) {
                addSection("Right Leg", 3, 2, 1, 1);
            }
            
        } catch (IOException | CsvValidationException e) {
            JOptionPane.showMessageDialog(this, "Error loading section data: " + e.getMessage());
        }
        
        mechPanel.revalidate();
        mechPanel.repaint();
    }
    
    /**
     * V1-style addSection method but with dynamic slot counts from real data
     */
    private void addSection(String sectionName, int gridx, int gridy, int gridwidth, int gridheight) {
        MechSection section = currentSections.get(sectionName);
        if (section == null) return;
        
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBorder(BorderFactory.createTitledBorder(sectionName));
        sectionPanel.setMaximumSize(new Dimension(220, 400));
        
        // Set up drag and drop for this section
        MechSectionDropHandler dropHandler = new MechSectionDropHandler(section, sectionPanel);
        sectionPanel.setTransferHandler(dropHandler);
        sectionDropHandlers.put(sectionName, dropHandler);
        
        // Set callback to update equipped weapons panel when weapons change
        dropHandler.setOnWeaponsChangedCallback(this::updateEquippedWeaponsPanel);
        
        // Section info display with clear distinction between total slots and weapon hardpoints
        int totalHardpoints = section.getEnergyHardpoints() + section.getBallisticHardpoints() + section.getMissileHardpoints();
        int totalSlots = section.getTotalSlots();
        
        // Create clean info label with only essential information
        StringBuilder infoHtml = new StringBuilder("<html>");
        
        // Get allocated armor from armor allocation panel
        double allocatedArmorTons = 0.0;
        if (armorAllocationPanel != null) {
            allocatedArmorTons = armorAllocationPanel.getSectionArmor(sectionName);
        }
        
        // Calculate armor HP based on allocated armor tons and selected armor type
        int armorHP = (int)(allocatedArmorTons * getSelectedArmorHpPerTon());
        
        // Get structure HP (varies by section type)
        int structureHP = section.getTotalSlotHP();
        
        // Add damage information
        String damageStatus = section.getDamageStatus();
        
        infoHtml.append(String.format("<b>Weapon Tonnage: 0.0</b><br/>"));
        infoHtml.append(String.format("<b>Armor HP: %d (%.2ft × %d HP/t)</b><br/>", armorHP, allocatedArmorTons, getSelectedArmorHpPerTon()));
        infoHtml.append(String.format("<b>Structure HP: %d</b><br/>", structureHP));
        infoHtml.append(String.format("<b>Total Tonnage: %.2f</b><br/>", allocatedArmorTons));
        infoHtml.append(String.format("<b>Damage Status: %s</b></html>", damageStatus));
        
        JLabel infoLabel = new JLabel(infoHtml.toString());
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD));
        sectionPanel.add(infoLabel);
        
        // Store for tonnage updates
        tonnageLabels.put(sectionName, infoLabel);
        
        // Add armor allocation controls directly in the section header
        if (armorAllocationPanel != null) {
            JPanel armorHeaderPanel = createArmorHeaderPanel(sectionName);
            sectionPanel.add(armorHeaderPanel);
            sectionPanel.add(Box.createVerticalStrut(5));
        }
        
        // Add damage input box for this section
        JPanel damagePanel = createDamageInputPanel(sectionName);
        sectionPanel.add(damagePanel);
        sectionPanel.add(Box.createVerticalStrut(5));
        
        // Create weapon slot dropdowns - use hardpoint count from chassis (not total slots)
        List<JComboBox<String>> dropdowns = new ArrayList<>();
        
        if (totalHardpoints > 0) {
            sectionPanel.add(Box.createVerticalStrut(3));
            
            // Add a separator label to distinguish weapon slots from general slots
            JLabel weaponSlotsLabel = new JLabel("Weapon Slots (Red):");
            weaponSlotsLabel.setFont(weaponSlotsLabel.getFont().deriveFont(Font.BOLD, 10f));
            weaponSlotsLabel.setForeground(new Color(139, 0, 0)); // Dark red
            sectionPanel.add(weaponSlotsLabel);
            sectionPanel.add(Box.createVerticalStrut(2));
            
            // Create compact drop zones for each weapon hardpoint (highlighted in red)
            for (int i = 0; i < totalHardpoints; i++) {
                JPanel compactDropZone = new JPanel(new BorderLayout());
                compactDropZone.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLoweredBevelBorder(),
                    BorderFactory.createEmptyBorder(3, 6, 3, 6)
                ));
                compactDropZone.setBackground(new Color(255, 240, 240)); // Light red background
                compactDropZone.setPreferredSize(new Dimension(200, 25));
                compactDropZone.setMaximumSize(new Dimension(200, 25));
                compactDropZone.setTransferHandler(dropHandler);
                
                // Setup context menu for weapon removal
                dropHandler.setupContextMenu(compactDropZone, i);
                
                // Add slot indicator with red color
                JLabel slotLabel = new JLabel((i + 1) + ":");
                slotLabel.setFont(slotLabel.getFont().deriveFont(Font.BOLD, 10f));
                slotLabel.setForeground(Color.RED);
                slotLabel.setPreferredSize(new Dimension(15, 25));
                
                // Add placeholder text for empty weapon slot
                JLabel placeholderLabel = new JLabel("Drop weapon here");
                placeholderLabel.setFont(placeholderLabel.getFont().deriveFont(Font.ITALIC, 9f));
                placeholderLabel.setForeground(new Color(139, 0, 0)); // Dark red
                
                compactDropZone.add(slotLabel, BorderLayout.WEST);
                compactDropZone.add(placeholderLabel, BorderLayout.CENTER);
                
                // Add tooltip to show double-click functionality
                compactDropZone.setToolTipText("Double-click to remove weapon");
                
                sectionPanel.add(compactDropZone);
                sectionPanel.add(Box.createVerticalStrut(2));
            }
            
            // Add separator between weapon slots and general slots
            if (totalSlots > totalHardpoints) {
                sectionPanel.add(Box.createVerticalStrut(5));
                JLabel generalSlotsLabel = new JLabel("General Equipment Slots:");
                generalSlotsLabel.setFont(generalSlotsLabel.getFont().deriveFont(Font.BOLD, 10f));
                generalSlotsLabel.setForeground(Color.DARK_GRAY);
                sectionPanel.add(generalSlotsLabel);
                sectionPanel.add(Box.createVerticalStrut(2));
                
                // Show general equipment slots (non-weapon slots)
                int generalSlots = totalSlots - totalHardpoints;
                for (int i = 0; i < generalSlots; i++) {
                    JPanel generalSlot = new JPanel(new BorderLayout());
                    generalSlot.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLoweredBevelBorder(),
                        BorderFactory.createEmptyBorder(3, 6, 3, 6)
                    ));
                    generalSlot.setBackground(new Color(250, 250, 250)); // Default background
                    generalSlot.setPreferredSize(new Dimension(200, 25));
                    generalSlot.setMaximumSize(new Dimension(200, 25));
                    
                    // Add slot indicator
                    JLabel slotLabel = new JLabel("G" + (i + 1) + ":");
                    slotLabel.setFont(slotLabel.getFont().deriveFont(Font.BOLD, 10f));
                    slotLabel.setForeground(Color.DARK_GRAY);
                    slotLabel.setPreferredSize(new Dimension(20, 25));
                    
                    // Add placeholder text for general equipment
                    JLabel placeholderLabel = new JLabel("General equipment slot");
                    placeholderLabel.setFont(placeholderLabel.getFont().deriveFont(Font.ITALIC, 9f));
                    placeholderLabel.setForeground(Color.GRAY);
                    
                    generalSlot.add(slotLabel, BorderLayout.WEST);
                    generalSlot.add(placeholderLabel, BorderLayout.CENTER);
                    
                    // Add tooltip for general equipment slots
                    generalSlot.setToolTipText("General equipment slot");
                    
                    sectionPanel.add(generalSlot);
                    sectionPanel.add(Box.createVerticalStrut(2));
                }
            }
        } else {
            // Section has no weapon hardpoints (like legs) - show only general slots
            sectionPanel.add(Box.createVerticalStrut(3));
            JLabel generalSlotsLabel = new JLabel("General Equipment Slots:");
            generalSlotsLabel.setFont(generalSlotsLabel.getFont().deriveFont(Font.BOLD, 10f));
            generalSlotsLabel.setForeground(Color.DARK_GRAY);
            sectionPanel.add(generalSlotsLabel);
            sectionPanel.add(Box.createVerticalStrut(2));
            
            // Show all slots as general equipment slots
            for (int i = 0; i < totalSlots; i++) {
                JPanel generalSlot = new JPanel(new BorderLayout());
                generalSlot.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLoweredBevelBorder(),
                    BorderFactory.createEmptyBorder(3, 6, 3, 6)
                ));
                generalSlot.setBackground(new Color(250, 250, 250)); // Default background
                generalSlot.setPreferredSize(new Dimension(200, 25));
                generalSlot.setMaximumSize(new Dimension(200, 25));
                
                // Add slot indicator
                JLabel slotLabel = new JLabel("G" + (i + 1) + ":");
                slotLabel.setFont(slotLabel.getFont().deriveFont(Font.BOLD, 10f));
                slotLabel.setForeground(Color.DARK_GRAY);
                slotLabel.setPreferredSize(new Dimension(20, 25));
                
                // Add placeholder text for general equipment
                JLabel placeholderLabel = new JLabel("General equipment slot");
                placeholderLabel.setFont(placeholderLabel.getFont().deriveFont(Font.ITALIC, 9f));
                placeholderLabel.setForeground(Color.GRAY);
                
                generalSlot.add(slotLabel, BorderLayout.WEST);
                generalSlot.add(placeholderLabel, BorderLayout.CENTER);
                
                // Add tooltip for general equipment slots
                generalSlot.setToolTipText("General equipment slot");
                
                sectionPanel.add(generalSlot);
                sectionPanel.add(Box.createVerticalStrut(2));
            }
        }
        
        sectionDropdowns.put(sectionName, dropdowns);
        
        // Add to grid using V1's clean positioning approach
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTH;
        
        mechPanel.add(sectionPanel, gbc);
        
        // Damage control is now integrated into each section, no mouse listener needed
    }
    
    /**
     * Update armor display across all sections
     */
    private void updateArmorDisplay() {
        if (currentSections != null) {
            for (String sectionName : currentSections.keySet()) {
                updateSectionTonnage(sectionName);
            }
        }
        // Force a complete UI update
        if (mechPanel != null) {
            mechPanel.revalidate();
            mechPanel.repaint();
        }
    }
    
    /**
     * Load armor data from CSV
     */
    private void loadArmorData() {
        armorHpPerTon = new HashMap<>();
        try {
            // Parse the armor CSV data
            armorHpPerTon.put("MACM (30 HP/Ton, Plate)", 30);
            armorHpPerTon.put("AS-19 (35 HP/Ton, Refractive)", 35);
            armorHpPerTon.put("AX5 (35 HP/Ton, Refractive)", 35);
            armorHpPerTon.put("PAS (36 HP/Ton, Plate)", 36);
            armorHpPerTon.put("GRP (40 HP/Ton, Plate)", 40);
            armorHpPerTon.put("MLC (40 HP/Ton, Refractive)", 40);
            armorHpPerTon.put("TKM (45 HP/Ton, Plate)", 45);
            armorHpPerTon.put("ARP (45 HP/Ton, Refractive)", 45);
            armorHpPerTon.put("TLW (48 HP/Ton, Plate)", 48);
            armorHpPerTon.put("HCC (54 HP/Ton, Plate)", 54);
            armorHpPerTon.put("CNC (75 HP/Ton, Ablative)", 75);
            armorHpPerTon.put("FRA (80 HP/Ton, Ablative)", 80);
            armorHpPerTon.put("ROP (90 HP/Ton, Ablative)", 90);
            armorHpPerTon.put("KS MIV (100 HP/Ton, Ablative)", 100);
        } catch (Exception e) {
            System.err.println("Error loading armor data: " + e.getMessage());
        }
    }
    
    /**
     * Get HP per ton for selected armor type
     */
    private int getSelectedArmorHpPerTon() {
        String selectedArmor = (String) armorDropdown.getSelectedItem();
        if (selectedArmor != null && armorHpPerTon.containsKey(selectedArmor)) {
            return armorHpPerTon.get(selectedArmor);
        }
        return 40; // Default to GRP if something goes wrong
    }
    
    /**
     * Update section displays when armor allocation changes
     */
    public void updateArmorAllocation() {
        if (currentSections != null) {
            for (String sectionName : currentSections.keySet()) {
                updateSectionTonnage(sectionName);
            }
        }
        updateTotalMechTonnage();
    }
    
    /**
     * Calculate and display total mech tonnage across all sections
     */
    private void updateTotalMechTonnage() {
        if (currentSections == null || sectionDropHandlers == null) return;
        
        double totalWeaponTonnage = 0.0;
        double totalArmorTonnage = 0.0;
        double totalMechTonnage = 0.0;
        
        // Calculate totals from all sections
        for (String sectionName : currentSections.keySet()) {
            MechSectionDropHandler dropHandler = sectionDropHandlers.get(sectionName);
            if (dropHandler != null) {
                totalWeaponTonnage += dropHandler.getEquippedTonnage();
            }
            
            if (armorAllocationPanel != null) {
                totalArmorTonnage += armorAllocationPanel.getSectionArmor(sectionName);
            }
        }
        
        totalMechTonnage = totalWeaponTonnage + totalArmorTonnage;
        
        // Get chassis limits
        MechChassis selectedChassis = getSelectedChassis();
        if (selectedChassis != null) {
            double maxTonnage = selectedChassis.getTonnage();
            double maxArmorTonnage = selectedChassis.getMaxArmorTonnage();
            
            // Update the equipped weapons panel with total tonnage info
            if (equippedWeaponsPanel != null) {
                equippedWeaponsPanel.updateTotalTonnage(totalWeaponTonnage, totalArmorTonnage, totalMechTonnage, maxTonnage);
            }
            
            // Check for overage and show warnings
            checkTonnageLimits(totalMechTonnage, maxTonnage, totalArmorTonnage, maxArmorTonnage);
        }
    }
    
    /**
     * Check if mech exceeds tonnage limits and show warnings
     */
    private void checkTonnageLimits(double totalMechTonnage, double maxTonnage, double totalArmorTonnage, double maxArmorTonnage) {
        boolean hasWarnings = false;
        StringBuilder warningMessage = new StringBuilder();
        
        if (totalMechTonnage > maxTonnage) {
            hasWarnings = true;
            warningMessage.append(String.format("⚠️ MECH OVERWEIGHT: %.2f/%.1f tons (+%.2f over)\n", 
                totalMechTonnage, maxTonnage, totalMechTonnage - maxTonnage));
        }
        
        if (totalArmorTonnage > maxArmorTonnage) {
            hasWarnings = true;
            warningMessage.append(String.format("⚠️ ARMOR OVERWEIGHT: %.2f/%.1f tons (+%.2f over)\n", 
                totalArmorTonnage, maxArmorTonnage, totalArmorTonnage - maxArmorTonnage));
        }
        
        // Show warnings in the UI (could be enhanced with a dedicated warning panel)
        if (hasWarnings) {
            System.out.println("TONNAGE WARNINGS:\n" + warningMessage.toString());
        }
    }
    
    /**
     * Get the currently selected chassis
     */
    private MechChassis getSelectedChassis() {
        String selectedName = (String) chassisDropdown.getSelectedItem();
        if (selectedName == null) return null;
        
        return chassisList.stream()
            .filter(chassis -> chassis.getName().equals(selectedName))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Update the equipped weapons panel with current weapon loadout
     */
    private void updateEquippedWeaponsPanel() {
        if (equippedWeaponsPanel != null) {
            Map<String, List<WeaponComponent>> weaponsBySection = new HashMap<>();
            
            // Collect weapons from all sections using drop handlers
            for (String sectionName : sectionDropHandlers.keySet()) {
                MechSectionDropHandler dropHandler = sectionDropHandlers.get(sectionName);
                if (dropHandler != null) {
                    List<WeaponComponent> sectionWeapons = dropHandler.getEquippedWeapons();
                    if (sectionWeapons != null && !sectionWeapons.isEmpty()) {
                        weaponsBySection.put(sectionName, sectionWeapons);
                    }
                }
            }
            
            equippedWeaponsPanel.updateEquippedWeapons(weaponsBySection);
        }
        
        // Update all section displays to reflect new weapon tonnage
        updateAllSectionDisplays();
        
        // Also update total mech tonnage when weapons change
        updateTotalMechTonnage();
    }
    
    /**
     * Update all section displays to reflect current weapon and armor tonnage
     */
    private void updateAllSectionDisplays() {
        if (currentSections != null) {
            for (String sectionName : currentSections.keySet()) {
                updateSectionTonnage(sectionName);
            }
        }
    }
    
    /**
     * Select a section for damage control operations (no longer needed)
     */
    private void selectSectionForDamageControl(String sectionName) {
        // Damage control is now integrated into each section
        // This method is kept for compatibility but no longer needed
    }
    
    /**
     * Create damage input panel for a section
     */
    private JPanel createDamageInputPanel(String sectionName) {
        JPanel damagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        damagePanel.setBackground(new Color(255, 240, 240));
        damagePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        
        // Damage label
        JLabel damageLabel = new JLabel("Dmg:");
        damageLabel.setFont(new Font("Arial", Font.BOLD, 8));
        damageLabel.setForeground(new Color(139, 0, 0)); // Dark red
        
        // Damage input field (very compact)
        JTextField damageField = new JTextField("0", 2);
        damageField.setFont(new Font("Arial", Font.PLAIN, 8));
        damageField.setHorizontalAlignment(JTextField.CENTER);
        damageField.setName("damage_" + sectionName); // For easy identification
        damageField.setToolTipText("Enter damage amount and press Enter");
        
        // Add Enter key listener to apply damage
        damageField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    applyDamageToSection(sectionName, damageField);
                }
            }
        });
        
        damagePanel.add(damageLabel);
        damagePanel.add(damageField);
        
        return damagePanel;
    }
    
    /**
     * Create armor allocation header panel for a section
     */
    private JPanel createArmorHeaderPanel(String sectionName) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        
        // Armor allocation label
        JLabel armorLabel = new JLabel("Armor Allocation:");
        armorLabel.setFont(new Font("Arial", Font.BOLD, 10));
        armorLabel.setForeground(Color.DARK_GRAY);
        
        // Current armor value display
        JLabel currentArmorLabel = new JLabel("0.00t");
        currentArmorLabel.setFont(new Font("Arial", Font.BOLD, 10));
        currentArmorLabel.setForeground(Color.BLUE);
        currentArmorLabel.setName("armor_" + sectionName); // For easy identification
        
        // Store reference for updates
        if (tonnageLabels.containsKey(sectionName + "_armor")) {
            tonnageLabels.put(sectionName + "_armor", currentArmorLabel);
        } else {
            tonnageLabels.put(sectionName + "_armor", currentArmorLabel);
        }
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        // Decrease button
        JButton decreaseBtn = new JButton("-");
        decreaseBtn.setPreferredSize(new Dimension(25, 20));
        decreaseBtn.setFont(new Font("Arial", Font.BOLD, 12));
        decreaseBtn.addActionListener(e -> adjustSectionArmor(sectionName, -0.25));
        
        // Increase button
        JButton increaseBtn = new JButton("+");
        increaseBtn.setPreferredSize(new Dimension(25, 20));
        increaseBtn.setFont(new Font("Arial", Font.BOLD, 12));
        increaseBtn.addActionListener(e -> adjustSectionArmor(sectionName, 0.25));
        
        buttonPanel.add(decreaseBtn);
        buttonPanel.add(increaseBtn);
        
        headerPanel.add(armorLabel, BorderLayout.WEST);
        headerPanel.add(currentArmorLabel, BorderLayout.CENTER);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Apply damage to a specific section
     */
    private void applyDamageToSection(String sectionName, JTextField damageField) {
        try {
            int damage = Integer.parseInt(damageField.getText());
            if (damage < 0) {
                JOptionPane.showMessageDialog(this, "Damage cannot be negative!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            MechSection section = currentSections.get(sectionName);
            if (section != null) {
                int remainingDamage = section.applyDamage(damage);
                
                // Log the damage application
                System.out.println(String.format("Applied %d damage to %s", damage, sectionName));
                if (remainingDamage > 0) {
                    System.out.println(String.format("Section destroyed! %d damage overflow", remainingDamage));
                }
                
                // Update the section display
                updateSectionTonnage(sectionName);
                
                // Clear the damage field
                damageField.setText("0");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for damage!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Apply field repair to a specific section
     */
    private void applyFieldRepairToSection(String sectionName, JTextField repairField) {
        try {
            int repairAmount = Integer.parseInt(repairField.getText());
            if (repairAmount < 0) {
                JOptionPane.showMessageDialog(this, "Repair amount cannot be negative!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            MechSection section = currentSections.get(sectionName);
            if (section != null) {
                section.applyFieldRepair(repairAmount);
                
                // Log the repair application
                System.out.println(String.format("Applied %d field repair armor to %s", repairAmount, sectionName));
                
                // Update the section display
                updateSectionTonnage(sectionName);
                
                // Clear the repair field
                repairField.setText("0");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for repair amount!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Adjust armor for a specific section
     */
    private void adjustSectionArmor(String sectionName, double adjustment) {
        if (armorAllocationPanel != null) {
            // Get current value from armor allocation panel
            double currentValue = armorAllocationPanel.getSectionArmor(sectionName);
            double newValue = currentValue + adjustment;
            
            // Get max allowed for this section
            MechChassis selectedChassis = chassisList.stream()
                .filter(chassis -> chassis.getName().equals(chassisDropdown.getSelectedItem()))
                .findFirst()
                .orElse(null);
            
            if (selectedChassis != null) {
                double maxArmorTonnage = selectedChassis.getMaxArmorTonnage();
                double maxAllowed = 0.0;
                
                // Calculate max based on section type
                if (sectionName.equals("Head")) maxAllowed = maxArmorTonnage * 0.05;
                else if (sectionName.equals("Left Arm") || sectionName.equals("Right Arm")) maxAllowed = maxArmorTonnage * 0.05;
                else if (sectionName.equals("Left Torso") || sectionName.equals("Right Torso")) maxAllowed = maxArmorTonnage * 0.15;
                else if (sectionName.equals("Center Torso")) maxAllowed = maxArmorTonnage * 0.25;
                else if (sectionName.equals("Left Leg") || sectionName.equals("Right Leg")) maxAllowed = maxArmorTonnage * 0.15;
                
                // Clamp to valid range
                newValue = Math.max(0.0, Math.min(newValue, maxAllowed));
                
                // Update the armor allocation panel (this will trigger the callback to update displays)
                armorAllocationPanel.setSectionArmor(sectionName, newValue);
                
                // Update all section displays
                updateArmorAllocation();
            }
        }
    }
    
    /**
     * Reset all section HP to maximum
     */
    private void resetAllSectionHP() {
        if (currentSections != null) {
            for (String sectionName : currentSections.keySet()) {
                MechSection section = currentSections.get(sectionName);
                if (section != null) {
                    section.resetDamage();
                }
            }
            
            // Update all displays
            updateAllSectionDisplays();
            
            // Show confirmation
            JOptionPane.showMessageDialog(this, 
                "All section HP has been restored to maximum!", 
                "HP Reset Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Update section tonnage display with real-time equipped item data
     */
    private void updateSectionTonnage(String sectionName) {
        JLabel tonnageLabel = tonnageLabels.get(sectionName);
        MechSection section = currentSections.get(sectionName);
        MechSectionDropHandler dropHandler = sectionDropHandlers.get(sectionName);
        
        if (tonnageLabel == null || section == null) return;
        
        // Get actual equipped weapon tonnage from drop handler
        double weaponTonnage = 0.0;
        int usedSlots = 0;
        if (dropHandler != null) {
            weaponTonnage = dropHandler.getEquippedTonnage();
            usedSlots = dropHandler.getEquippedCount();
        }
        
        // Update section's used slots
        section.setUsedSlots(usedSlots);
        
        // Get allocated armor from armor allocation panel
        double allocatedArmorTons = 0.0;
        if (armorAllocationPanel != null) {
            allocatedArmorTons = armorAllocationPanel.getSectionArmor(sectionName);
        }
        
        // Calculate armor HP based on allocated armor tons and selected armor type
        int armorHP = (int)(allocatedArmorTons * getSelectedArmorHpPerTon());
        
        // Update display with clean, essential information only
        StringBuilder infoHtml = new StringBuilder("<html>");
        
        // Get structure HP (varies by section type)
        int structureHP = section.getTotalSlotHP();
        
        infoHtml.append(String.format("<b>Weapon Tonnage: %.1f</b><br/>", weaponTonnage));
        infoHtml.append(String.format("<b>Armor HP: %d (%.2ft × %d HP/t)</b><br/>", armorHP, allocatedArmorTons, getSelectedArmorHpPerTon()));
        infoHtml.append(String.format("<b>Structure HP: %d</b><br/>", structureHP));
        infoHtml.append(String.format("<b>Total Tonnage: %.2f</b><br/>", weaponTonnage + allocatedArmorTons));
        
        // Add damage status
        String damageStatus = section.getDamageStatus();
        infoHtml.append(String.format("<b>Damage Status: %s</b></html>", damageStatus));
        
        tonnageLabel.setText(infoHtml.toString());
        
        // Also update the armor header panel if it exists
        JLabel armorLabel = tonnageLabels.get(sectionName + "_armor");
        if (armorLabel != null) {
            armorLabel.setText(String.format("%.2ft", allocatedArmorTons));
        }
        
        // Force the panel to update
        if (tonnageLabel.getParent() != null) {
            tonnageLabel.getParent().revalidate();
            tonnageLabel.getParent().repaint();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MechBuilderUI();
            } catch (IOException | CsvValidationException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error starting Mech Builder: " + e.getMessage());
            }
        });
    }
}