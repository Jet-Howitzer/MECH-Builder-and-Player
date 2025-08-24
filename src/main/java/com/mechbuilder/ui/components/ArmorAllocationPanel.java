package com.mechbuilder.ui.components;

import com.mechbuilder.model.MechChassis;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel for allocating armor tons to different mech sections
 * with percentage-based limits for each section type.
 */
public class ArmorAllocationPanel extends JPanel {
    
    private final Map<String, JPanel> armorControlPanels;
    private final Map<String, JLabel> percentageLabels;
    private final Map<String, JLabel> maxTonnageLabels;
    private final Map<String, JLabel> currentValueLabels;
    private JLabel totalAllocatedLabel;
    private JLabel remainingLabel;
    
    private MechChassis currentChassis;
    private double maxArmorTonnage;
    private Runnable onArmorChangedCallback;
    
    // Armor allocation percentages for each section
    private static final double ARM_ARMOR_PERCENT = 0.05;      // 5% for arms
    private static final double HEAD_ARMOR_PERCENT = 0.05;     // 5% for head
    private static final double LEG_ARMOR_PERCENT = 0.15;      // 15% for legs
    private static final double TORSO_ARMOR_PERCENT = 0.15;    // 15% for left/right torso
    private static final double CENTER_TORSO_ARMOR_PERCENT = 0.25; // 25% for center torso
    
    public ArmorAllocationPanel() {
        this.armorControlPanels = new HashMap<>();
        this.percentageLabels = new HashMap<>();
        this.maxTonnageLabels = new HashMap<>();
        this.currentValueLabels = new HashMap<>();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
            "Armor Allocation",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        
        setPreferredSize(new Dimension(300, 400));
        setBackground(Color.WHITE);
        
        // Create main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        // Add total allocation info at top
        JPanel totalPanel = createTotalPanel();
        contentPanel.add(totalPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // Add section allocation controls
        addSectionControls(contentPanel, "Head", HEAD_ARMOR_PERCENT);
        addSectionControls(contentPanel, "Left Arm", ARM_ARMOR_PERCENT);
        addSectionControls(contentPanel, "Right Arm", ARM_ARMOR_PERCENT);
        addSectionControls(contentPanel, "Left Torso", TORSO_ARMOR_PERCENT);
        addSectionControls(contentPanel, "Center Torso", CENTER_TORSO_ARMOR_PERCENT);
        addSectionControls(contentPanel, "Right Torso", TORSO_ARMOR_PERCENT);
        addSectionControls(contentPanel, "Left Leg", LEG_ARMOR_PERCENT);
        addSectionControls(contentPanel, "Right Leg", LEG_ARMOR_PERCENT);
        
        // Add flexible space at bottom
        contentPanel.add(Box.createVerticalGlue());
        
        // Add scroll pane for content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Initialize with no chassis selected
        setChassis(null);
    }
    
    /**
     * Create the total allocation info panel
     */
    private JPanel createTotalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        totalAllocatedLabel = new JLabel("Total Allocated: 0.00 tons");
        totalAllocatedLabel.setFont(new Font("Arial", Font.BOLD, 11));
        
        remainingLabel = new JLabel("Remaining: 0.00 tons");
        remainingLabel.setFont(new Font("Arial", Font.BOLD, 11));
        remainingLabel.setForeground(Color.DARK_GRAY);
        
        panel.add(totalAllocatedLabel, BorderLayout.NORTH);
        panel.add(remainingLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Add controls for a specific mech section
     */
    private void addSectionControls(JPanel parent, String sectionName, double percentage) {
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setBackground(Color.WHITE);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        // Section name and percentage
        JLabel nameLabel = new JLabel(sectionName + " (" + (int)(percentage * 100) + "%)");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 10));
        nameLabel.setPreferredSize(new Dimension(120, 20));
        
        // Max tonnage label
        maxTonnageLabels.put(sectionName, new JLabel("Max: 0.00t"));
        maxTonnageLabels.get(sectionName).setFont(new Font("Arial", Font.PLAIN, 9));
        maxTonnageLabels.get(sectionName).setForeground(Color.DARK_GRAY);
        maxTonnageLabels.get(sectionName).setPreferredSize(new Dimension(70, 20));
        
        // Current value label
        currentValueLabels.put(sectionName, new JLabel("0.00t"));
        currentValueLabels.get(sectionName).setFont(new Font("Arial", Font.BOLD, 10));
        currentValueLabels.get(sectionName).setForeground(Color.BLUE);
        currentValueLabels.get(sectionName).setPreferredSize(new Dimension(50, 20));
        
        // Create armor control panel with +/- buttons
        JPanel armorControlPanel = createArmorControlPanel(sectionName, percentage);
        armorControlPanels.put(sectionName, armorControlPanel);
        
        // Add components to section panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(nameLabel, BorderLayout.WEST);
        leftPanel.add(maxTonnageLabels.get(sectionName), BorderLayout.CENTER);
        
        sectionPanel.add(leftPanel, BorderLayout.WEST);
        sectionPanel.add(armorControlPanel, BorderLayout.EAST);
        
        parent.add(sectionPanel);
        parent.add(Box.createVerticalStrut(2));
    }
    
    /**
     * Create armor control panel with +/- buttons
     */
    private JPanel createArmorControlPanel(String sectionName, double percentage) {
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(Color.WHITE);
        
        // Current value display
        JLabel valueLabel = currentValueLabels.get(sectionName);
        controlPanel.add(valueLabel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        // Decrease button
        JButton decreaseBtn = new JButton("-");
        decreaseBtn.setPreferredSize(new Dimension(25, 20));
        decreaseBtn.setFont(new Font("Arial", Font.BOLD, 12));
        decreaseBtn.addActionListener(e -> adjustArmor(sectionName, -0.25));
        
        // Increase button
        JButton increaseBtn = new JButton("+");
        increaseBtn.setPreferredSize(new Dimension(25, 20));
        increaseBtn.setFont(new Font("Arial", Font.BOLD, 12));
        increaseBtn.addActionListener(e -> adjustArmor(sectionName, 0.25));
        
        buttonPanel.add(decreaseBtn);
        buttonPanel.add(increaseBtn);
        
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        
        return controlPanel;
    }
    
    /**
     * Adjust armor for a specific section
     */
    private void adjustArmor(String sectionName, double adjustment) {
        double currentValue = getSectionArmor(sectionName);
        double newValue = currentValue + adjustment;
        
        // Get max allowed for this section
        double maxAllowed = 0.0;
        if (currentChassis != null) {
            if (sectionName.equals("Head")) maxAllowed = maxArmorTonnage * HEAD_ARMOR_PERCENT;
            else if (sectionName.equals("Left Arm") || sectionName.equals("Right Arm")) maxAllowed = maxArmorTonnage * ARM_ARMOR_PERCENT;
            else if (sectionName.equals("Left Torso") || sectionName.equals("Right Torso")) maxAllowed = maxArmorTonnage * TORSO_ARMOR_PERCENT;
            else if (sectionName.equals("Center Torso")) maxAllowed = maxArmorTonnage * CENTER_TORSO_ARMOR_PERCENT;
            else if (sectionName.equals("Left Leg") || sectionName.equals("Right Leg")) maxAllowed = maxArmorTonnage * LEG_ARMOR_PERCENT;
        }
        
        // Clamp to valid range
        newValue = Math.max(0.0, Math.min(newValue, maxAllowed));
        
        // Update the current value label
        currentValueLabels.get(sectionName).setText(String.format("%.2ft", newValue));
        
        // Update totals and notify callback
        updateTotalAllocation();
        if (onArmorChangedCallback != null) {
            onArmorChangedCallback.run();
        }
    }
    
    /**
     * Set the current chassis and update limits
     */
    public void setChassis(MechChassis chassis) {
        this.currentChassis = chassis;
        
        if (chassis != null) {
            this.maxArmorTonnage = chassis.getMaxArmorTonnage();
            updateSectionLimits();
            updateTotalAllocation();
        } else {
            this.maxArmorTonnage = 0;
            disableAllControls();
        }
    }
    
    /**
     * Update the maximum armor limits for each section
     */
    private void updateSectionLimits() {
        if (currentChassis == null) return;
        
        // Update max tonnage labels for each section
        updateSectionLimit("Head", HEAD_ARMOR_PERCENT);
        updateSectionLimit("Left Arm", ARM_ARMOR_PERCENT);
        updateSectionLimit("Right Arm", ARM_ARMOR_PERCENT);
        updateSectionLimit("Left Torso", TORSO_ARMOR_PERCENT);
        updateSectionLimit("Center Torso", CENTER_TORSO_ARMOR_PERCENT);
        updateSectionLimit("Right Torso", TORSO_ARMOR_PERCENT);
        updateSectionLimit("Left Leg", LEG_ARMOR_PERCENT);
        updateSectionLimit("Right Leg", LEG_ARMOR_PERCENT);
    }
    
    /**
     * Update the limit for a specific section
     */
    private void updateSectionLimit(String sectionName, double percentage) {
        double maxTons = maxArmorTonnage * percentage;
        maxTonnageLabels.get(sectionName).setText(String.format("Max: %.2ft", maxTons));
        
        // Reset to 0 if current value exceeds new maximum
        double currentValue = getSectionArmor(sectionName);
        if (currentValue > maxTons) {
            currentValueLabels.get(sectionName).setText("0.00t");
        }
    }
    
    /**
     * Update the total allocation display
     */
    private void updateTotalAllocation() {
        double totalAllocated = 0.0;
        
        for (JLabel valueLabel : currentValueLabels.values()) {
            String text = valueLabel.getText();
            if (text.endsWith("t")) {
                try {
                    totalAllocated += Double.parseDouble(text.substring(0, text.length() - 1));
                } catch (NumberFormatException e) {
                    // Ignore parsing errors, treat as 0
                }
            }
        }
        
        double remaining = maxArmorTonnage - totalAllocated;
        
        totalAllocatedLabel.setText(String.format("Total Allocated: %.2f tons", totalAllocated));
        remainingLabel.setText(String.format("Remaining: %.2f tons", remaining));
        
        // Color code the remaining label
        if (remaining < 0) {
            remainingLabel.setForeground(Color.RED);
        } else if (remaining < 1.0) {
            remainingLabel.setForeground(Color.ORANGE);
        } else {
            remainingLabel.setForeground(Color.DARK_GRAY);
        }
    }
    
    /**
     * Disable all controls when no chassis is selected
     */
    private void disableAllControls() {
        for (JPanel controlPanel : armorControlPanels.values()) {
            controlPanel.setEnabled(false);
        }
        
        for (JLabel label : maxTonnageLabels.values()) {
            label.setText("Max: 0.00t");
        }
        
        for (JLabel label : currentValueLabels.values()) {
            label.setText("0.00t");
        }
        
        totalAllocatedLabel.setText("Total Allocated: 0.00 tons");
        remainingLabel.setText("Remaining: 0.00 tons");
        remainingLabel.setForeground(Color.DARK_GRAY);
    }
    
    /**
     * Get the allocated armor for a specific section
     */
    public double getSectionArmor(String sectionName) {
        JLabel valueLabel = currentValueLabels.get(sectionName);
        if (valueLabel != null) {
            String text = valueLabel.getText();
            if (text.endsWith("t")) {
                try {
                    return Double.parseDouble(text.substring(0, text.length() - 1));
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        }
        return 0.0;
    }
    
    /**
     * Get all allocated armor values
     */
    public Map<String, Double> getAllAllocatedArmor() {
        Map<String, Double> allocated = new HashMap<>();
        for (String sectionName : currentValueLabels.keySet()) {
            allocated.put(sectionName, getSectionArmor(sectionName));
        }
        return allocated;
    }
    
    /**
     * Reset all armor allocations to 0
     */
    public void resetAllocations() {
        for (JLabel valueLabel : currentValueLabels.values()) {
            valueLabel.setText("0.00t");
        }
        updateTotalAllocation();
    }
    
    /**
     * Check if current allocation is valid (within limits)
     */
    public boolean isAllocationValid() {
        double totalAllocated = 0.0;
        
        for (String sectionName : currentValueLabels.keySet()) {
            totalAllocated += getSectionArmor(sectionName);
        }
        
        return totalAllocated <= maxArmorTonnage;
    }
    
    /**
     * Set callback to be called when armor allocation changes
     */
    public void setOnArmorChangedCallback(Runnable callback) {
        this.onArmorChangedCallback = callback;
    }
    
    /**
     * Set armor value for a specific section
     */
    public void setSectionArmor(String sectionName, double armorTons) {
        JLabel valueLabel = currentValueLabels.get(sectionName);
        if (valueLabel != null) {
            valueLabel.setText(String.format("%.2ft", armorTons));
            updateTotalAllocation();
        }
    }
} 