package com.mechbuilder.ui;

import com.opencsv.exceptions.CsvValidationException;
import com.mechbuilder.data.MechChassisRepository;
import com.mechbuilder.data.WeaponRepository;
import com.mechbuilder.model.MechChassis;
import com.mechbuilder.model.WeaponComponent;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MechBuilderUIv2 extends JFrame {
    private List<MechChassis> chassisList;
    private List<WeaponComponent> weaponsList;
    private JComboBox<String> chassisDropdown;
    private JPanel mechPanel;
    private JPanel centerPanel;

    public MechBuilderUIv2() throws IOException, CsvValidationException {
        setTitle("Mech Builder v2");
        setSize(1200, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        MechChassisRepository chassisRepo = new MechChassisRepository();
        WeaponRepository weaponRepo = new WeaponRepository();
        
        chassisList = chassisRepo.loadAll();
        weaponsList = weaponRepo.loadAll();

        chassisDropdown = new JComboBox<>();
        for (MechChassis chassis : chassisList) {
            chassisDropdown.addItem(chassis.getName());
        }
        chassisDropdown.addActionListener(e -> updateMechLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Mech Chassis:"));
        topPanel.add(chassisDropdown);
        add(topPanel, BorderLayout.NORTH);

        centerPanel = new JPanel(new BorderLayout());
        mechPanel = new JPanel(new GridBagLayout());
        centerPanel.add(mechPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
        updateMechLayout();
    }

    private void updateMechLayout() {
        mechPanel.removeAll();

        String selectedName = (String) chassisDropdown.getSelectedItem();
        if (selectedName == null) return;

        MechChassis selectedChassis = chassisList.stream()
                .filter(m -> m.getName().equals(selectedName))
                .findFirst()
                .orElse(null);

        if (selectedChassis == null) return;

        Map<String, Integer> hardpoints = selectedChassis.getHardpoints();

        Map<String, GridPlacement> sectionLayout = new HashMap<>();
        sectionLayout.put("Head", new GridPlacement(2, 0));
        sectionLayout.put("Left Arm", new GridPlacement(0, 1));
        sectionLayout.put("Left Torso", new GridPlacement(1, 1));
        sectionLayout.put("Center Torso", new GridPlacement(2, 1));
        sectionLayout.put("Right Torso", new GridPlacement(3, 1));
        sectionLayout.put("Right Arm", new GridPlacement(4, 1));
        sectionLayout.put("Left Leg", new GridPlacement(1, 2));
        sectionLayout.put("Right Leg", new GridPlacement(3, 2));

        for (Map.Entry<String, Integer> entry : hardpoints.entrySet()) {
            String section = entry.getKey();
            int weaponSlots = entry.getValue();

            GridPlacement placement = sectionLayout.get(section);
            if (placement == null) continue;

                    JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBorder(BorderFactory.createTitledBorder(section));
        sectionPanel.setMaximumSize(new Dimension(220, 300)); // Prevent section from growing too large

            JLabel label = new JLabel("Weapon Slots: " + weaponSlots);
            sectionPanel.add(label);

            for (int i = 0; i < weaponSlots; i++) {
                JComboBox<String> slot = new JComboBox<>();
                slot.addItem("Empty");
                for (WeaponComponent weapon : weaponsList) {
                    slot.addItem(weapon.getName());
                }
                
                // Set fixed size to prevent layout breaking
                slot.setMaximumSize(new Dimension(200, 25));
                slot.setPreferredSize(new Dimension(200, 25));
                
                sectionPanel.add(slot);
            }

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = placement.x;
            gbc.gridy = placement.y;
            gbc.insets = new Insets(10, 10, 10, 10);
            mechPanel.add(sectionPanel, gbc);
        }

        mechPanel.revalidate();
        mechPanel.repaint();
    }



    private static class GridPlacement {
        int x, y;
        GridPlacement(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MechBuilderUIv2();
            } catch (IOException | CsvValidationException e) {
                e.printStackTrace();
            }
        });
    }
}
