package com.mechbuilder.ui;

import com.opencsv.exceptions.CsvValidationException;
import com.mechbuilder.data.MechChassisRepository;
import com.mechbuilder.data.MechSectionFactory;
import com.mechbuilder.data.WeaponRepository;
import com.mechbuilder.model.MechChassis;
import com.mechbuilder.model.MechSection;
import com.mechbuilder.model.WeaponComponent;
import com.mechbuilder.ui.components.WeaponArsenalPanel;
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
    
    // UI Components
    private JComboBox<String> chassisDropdown;
    private JPanel mechPanel;
    private Map<String, JLabel> tonnageLabels;
    private Map<String, List<JComboBox<String>>> sectionDropdowns;
    private WeaponArsenalPanel weaponArsenalPanel;
    
    public MechBuilderUI() throws IOException, CsvValidationException {
        // Initialize repositories
        chassisRepo = new MechChassisRepository();
        weaponRepo = new WeaponRepository();
        sectionFactory = new MechSectionFactory();
        
        // Load data
        chassisList = chassisRepo.loadAll();
        weaponsList = weaponRepo.loadAll();
        
        tonnageLabels = new HashMap<>();
        sectionDropdowns = new HashMap<>();
        
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
        
        // Top panel with chassis selection
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Select Mech Chassis:"));
        
        chassisDropdown = new JComboBox<>();
        for (MechChassis chassis : chassisList) {
            chassisDropdown.addItem(chassis.getName());
        }
        chassisDropdown.addActionListener(e -> updateMechLayout());
        topPanel.add(chassisDropdown);
        
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
        
        add(mainPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private void updateMechLayout() {
        mechPanel.removeAll();
        tonnageLabels.clear();
        sectionDropdowns.clear();
        
        String selectedName = (String) chassisDropdown.getSelectedItem();
        if (selectedName == null) return;
        
        // Find selected chassis
        MechChassis selectedChassis = chassisList.stream()
                .filter(chassis -> chassis.getName().equals(selectedName))
                .findFirst()
                .orElse(null);
        
        if (selectedChassis == null) return;
        
        try {
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
        
        // Section info display  
        int totalHardpoints = section.getEnergyHardpoints() + section.getBallisticHardpoints() + section.getMissileHardpoints();
        String hardpointText = totalHardpoints > 0 ? 
            String.format("Weapon Hardpoints: %d", totalHardpoints) :
            "No Weapon Hardpoints";
            
        JLabel infoLabel = new JLabel(String.format(
            "<html>%s<br/>Equipment Slots: %d<br/>Tonnage: 0.0</html>",
            hardpointText,
            section.getTotalSlots()
        ));
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD));
        sectionPanel.add(infoLabel);
        
        // Store for tonnage updates
        tonnageLabels.put(sectionName, infoLabel);
        
        // Create weapon slot dropdowns - use hardpoint count from chassis (not total slots)
        List<JComboBox<String>> dropdowns = new ArrayList<>();
        int weaponHardpoints = section.getEnergyHardpoints() + section.getBallisticHardpoints() + section.getMissileHardpoints();
        
        if (weaponHardpoints > 0) {
            sectionPanel.add(Box.createVerticalStrut(3));
            
            // Create compact drop zones for each hardpoint
            for (int i = 0; i < weaponHardpoints; i++) {
                JPanel compactDropZone = new JPanel(new BorderLayout());
                compactDropZone.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLoweredBevelBorder(),
                    BorderFactory.createEmptyBorder(3, 6, 3, 6)
                ));
                compactDropZone.setBackground(new Color(250, 250, 250));
                compactDropZone.setPreferredSize(new Dimension(200, 25));
                compactDropZone.setMaximumSize(new Dimension(200, 25));
                compactDropZone.setTransferHandler(dropHandler);
                
                // Setup context menu for weapon removal
                dropHandler.setupContextMenu(compactDropZone, i);
                
                // Add slot indicator
                JLabel slotLabel = new JLabel((i + 1) + ":");
                slotLabel.setFont(slotLabel.getFont().deriveFont(Font.BOLD, 10f));
                slotLabel.setForeground(Color.DARK_GRAY);
                slotLabel.setPreferredSize(new Dimension(15, 25));
                
                // Add placeholder text for empty slot
                JLabel placeholderLabel = new JLabel("Drop weapon here");
                placeholderLabel.setFont(placeholderLabel.getFont().deriveFont(Font.ITALIC, 9f));
                placeholderLabel.setForeground(Color.LIGHT_GRAY);
                
                compactDropZone.add(slotLabel, BorderLayout.WEST);
                compactDropZone.add(placeholderLabel, BorderLayout.CENTER);
                
                sectionPanel.add(compactDropZone);
                sectionPanel.add(Box.createVerticalStrut(2));
            }
        } else {
            // Section has no weapon hardpoints (like legs) - show info message
            JLabel noWeaponsLabel = new JLabel("No weapon hardpoints");
            noWeaponsLabel.setFont(noWeaponsLabel.getFont().deriveFont(Font.ITALIC));
            noWeaponsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            sectionPanel.add(noWeaponsLabel);
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
    }
    
    /**
     * V1-style tonnage calculation per section
     */
    private void updateSectionTonnage(String sectionName) {
        List<JComboBox<String>> dropdowns = sectionDropdowns.get(sectionName);
        JLabel tonnageLabel = tonnageLabels.get(sectionName);
        MechSection section = currentSections.get(sectionName);
        
        if (dropdowns == null || tonnageLabel == null || section == null) return;
        
        double totalTonnage = 0.0;
        int usedSlots = 0;
        
        for (JComboBox<String> dropdown : dropdowns) {
            String selected = (String) dropdown.getSelectedItem();
            if (selected != null && !selected.equals("Empty")) {
                usedSlots++;
                // Find weapon and add its tonnage
                for (WeaponComponent weapon : weaponsList) {
                    if (weapon.getName().equals(selected)) {
                        totalTonnage += weapon.getTonnage();
                        break;
                    }
                }
            }
        }
        
        // Update section's used slots
        section.setUsedSlots(usedSlots);
        
        // Update display with comprehensive info
        int totalHardpoints = section.getEnergyHardpoints() + section.getBallisticHardpoints() + section.getMissileHardpoints();
        String hardpointDisplay = totalHardpoints > 0 ? 
            String.format("Weapon Hardpoints: %d/%d", usedSlots, totalHardpoints) :
            "No Weapon Hardpoints";
            
        tonnageLabel.setText(String.format(
            "<html>%s<br/>Equipment Slots: %d/%d<br/>Tonnage: %.1f</html>",
            hardpointDisplay,
            section.getUsedSlots(), section.getTotalSlots(),
            totalTonnage
        ));
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