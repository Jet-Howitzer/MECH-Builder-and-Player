import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComponentManager {

    private List<WeaponComponent> components;

    public ComponentManager(String filePath) {
        components = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] fields;

            // Skip the header
            reader.readNext();

            // Read lines
            while ((fields = reader.readNext()) != null) {
                if (fields.length < 11) {
                    System.err.println("Too few fields in line: " + String.join(",", fields));
                    continue;
                }

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

                    WeaponComponent weapon = new WeaponComponent(
                        name, type, tonnage, heat, damage,
                        optimalRange, maxRange, recycle,
                        accuracyPenalty, shotsPerSalvo, damageDrop
                    );

                    components.add(weapon);

                } catch (NumberFormatException e) {
                    System.err.println("Number formatting error: " + e.getMessage());
                }
            }

        } catch (IOException | CsvValidationException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<WeaponComponent> getComponents() {
        return components;
    }
}