package com.mechbuilder.ui.components;

import com.mechbuilder.data.WeaponRepository;
import com.mechbuilder.model.WeaponComponent;
import com.opencsv.exceptions.CsvValidationException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Panel containing the weapon arsenal - displays all available weapons
 * for drag and drop operations.
 */
public class WeaponArsenalPanel extends JPanel {
    
    private final WeaponRepository weaponRepository;
    private final JPanel weaponGrid;
    private final JScrollPane scrollPane;
    private JComboBox<String> typeFilter;
    
    public WeaponArsenalPanel() throws IOException, CsvValidationException {
        this.weaponRepository = new WeaponRepository();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 2), 
            "Weapon Arsenal", 
            TitledBorder.CENTER, 
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        
        // Create filter panel
        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.NORTH);
        
        // Create weapon grid
        weaponGrid = new JPanel();
        weaponGrid.setLayout(new BoxLayout(weaponGrid, BoxLayout.Y_AXIS));
        weaponGrid.setBackground(Color.WHITE);
        
        // Add scroll pane
        scrollPane = new JScrollPane(weaponGrid);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(220, 600));
        
        // Make the weapon grid more compact
        weaponGrid.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        add(scrollPane, BorderLayout.CENTER);
        
        // Load weapons
        loadWeapons("All");
    }
    
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        filterPanel.setBackground(getBackground());
        
        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        typeFilter = new JComboBox<>(new String[]{"All", "Energy", "Ballistic", "Missile"});
        typeFilter.addActionListener(e -> {
            String selectedType = (String) typeFilter.getSelectedItem();
            try {
                loadWeapons(selectedType);
            } catch (IOException | CsvValidationException ex) {
                showError("Error loading weapons: " + ex.getMessage());
            }
        });
        
        filterPanel.add(filterLabel);
        filterPanel.add(typeFilter);
        
        return filterPanel;
    }
    
    private void loadWeapons(String typeFilter) throws IOException, CsvValidationException {
        weaponGrid.removeAll();
        
        List<WeaponComponent> weapons;
        if ("All".equals(typeFilter)) {
            weapons = weaponRepository.loadAll();
        } else {
            weapons = weaponRepository.findByType(typeFilter);
        }
        
        for (WeaponComponent weapon : weapons) {
            WeaponPanel weaponPanel = new WeaponPanel(weapon);
            weaponGrid.add(weaponPanel);
            weaponGrid.add(Box.createVerticalStrut(1)); // Minimal spacing between weapons
        }
        
        // Add flexible space at the bottom
        weaponGrid.add(Box.createVerticalGlue());
        
        weaponGrid.revalidate();
        weaponGrid.repaint();
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void refreshWeapons() {
        String selectedType = (String) typeFilter.getSelectedItem();
        try {
            loadWeapons(selectedType);
        } catch (IOException | CsvValidationException ex) {
            showError("Error refreshing weapons: " + ex.getMessage());
        }
    }
}