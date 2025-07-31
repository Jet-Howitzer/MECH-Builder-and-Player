import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MechBuilderUI extends JFrame {
    private final List<WeaponComponent> components;
    private final List<JComboBox<String>> allDropdowns = new ArrayList<>();
    private final JLabel[] tonnageLabels = new JLabel[8]; // One for each section

    public MechBuilderUI(String csvFilePath) throws IOException, CsvValidationException {
        components = loadComponents(csvFilePath);

        setTitle("Mech Builder");
        setSize(1000, 1000); // Adjusted size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mechPanel = createMechPanel();
        add(mechPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private List<WeaponComponent> loadComponents(String resourcePath) throws IOException, CsvValidationException {
        List<WeaponComponent> list = new ArrayList<>();
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }
        
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] fields;
            reader.readNext(); // Skip header

            while ((fields = reader.readNext()) != null) {
                if (fields.length < 11) continue;

                try {
                    String name = fields[0];
                    String type = fields[1];
                    double tonnage = Double.parseDouble(fields[2]);
                    int heat = Integer.parseInt(fields[3]);
                    int damage = Integer.parseInt(fields[4]);
                    int optimalRange = Integer.parseInt(fields[5]);
                    int maxRange = Integer.parseInt(fields[6]);
                    double recycle = Double.parseDouble(fields[7]);
                    int accuracyPenalty = Integer.parseInt(fields[8]);
                    int shotsPerSalvo = Integer.parseInt(fields[9]);
                    double damageDrop = Double.parseDouble(fields[10]);

                    list.add(new WeaponComponent(
                        name, type, tonnage, heat, damage,
                        optimalRange, maxRange, recycle,
                        accuracyPenalty, shotsPerSalvo, damageDrop));
                } catch (NumberFormatException ignored) {}
            }
        }

        return list;
    }

    private JPanel createMechPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Cockpit
        addSection(panel, "Cockpit", 2, 0, 2, 1, 2);
        // Center Torso
        addSection(panel, "Center Torso", 2, 1, 2, 1, 2);
        // Left and Right Torso
        addSection(panel, "Left Torso", 1, 1, 1, 1, 7);
        addSection(panel, "Right Torso", 4, 1, 1, 1, 7);
        // Left and Right Arms
        addSection(panel, "Left Arm", 0, 1, 1, 1, 4);
        addSection(panel, "Right Arm", 5, 1, 1, 1, 4);
        // Left and Right Legs
        addSection(panel, "Left Leg", 1, 2, 1, 1, 2);
        addSection(panel, "Right Leg", 4, 2, 1, 1, 2);

        return panel;
    }

    private void addSection(JPanel panel, String name, int gridx, int gridy, int gridwidth, int gridheight, int slotCount) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBorder(BorderFactory.createTitledBorder(name));

        JLabel tonnageLabel = new JLabel("Tonnage: 0.0");
        tonnageLabel.setFont(tonnageLabel.getFont().deriveFont(Font.BOLD));
        sectionPanel.add(tonnageLabel);

        List<JComboBox<String>> dropdowns = new ArrayList<>();

        for (int i = 0; i < slotCount; i++) {
            JComboBox<String> dropdown = new JComboBox<>();
            dropdown.addItem("Empty");
            for (WeaponComponent wc : components) {
                dropdown.addItem(wc.getName());
            }
            dropdown.setMaximumSize(new Dimension(200, 24));
            int finalI = allDropdowns.size();
            dropdown.addActionListener(e -> updateTonnage(dropdowns, tonnageLabel));
            dropdowns.add(dropdown);
            allDropdowns.add(dropdown);
            sectionPanel.add(dropdown);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(sectionPanel, gbc);
    }

    private void updateTonnage(List<JComboBox<String>> dropdowns, JLabel tonnageLabel) {
        double total = 0.0;
        for (JComboBox<String> dropdown : dropdowns) {
            String selected = (String) dropdown.getSelectedItem();
            if (selected != null && !selected.equals("Empty")) {
                for (WeaponComponent wc : components) {
                    if (wc.getName().equals(selected)) {
                        total += wc.getTonnage();
                        break;
                    }
                }
            }
        }
        tonnageLabel.setText("Tonnage: " + total);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MechBuilderUI("Weaponry Components.csv");
            } catch (IOException | CsvValidationException e) {
                e.printStackTrace();
            }
        });
    }
}
