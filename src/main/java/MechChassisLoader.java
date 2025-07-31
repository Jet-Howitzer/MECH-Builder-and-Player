import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class MechChassisLoader {
    public static List<MechChassis> loadChassisFromCSV(String resourcePath) throws IOException, CsvValidationException {
        List<MechChassis> chassisList = new ArrayList<>();

        InputStream inputStream = MechChassisLoader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] header = reader.readNext(); // Read header row
            if (header == null) {
                throw new IOException("CSV file is empty or improperly formatted.");
            }

            while (true) {
                String[] row = reader.readNext();
                if (row == null) break;

                if (row.length < 11) continue; // Make sure there are enough columns

                String name = row[0];
                String chassisSize = row[1];
                int tonnage = Integer.parseInt(row[2]);
                int maxArmorTonnage = Integer.parseInt(row[3]);
                int hexSpeed = Integer.parseInt(row[4]);

                Map<String, Integer> hardpoints = new HashMap<>();
                hardpoints.put("Left Arm", Integer.parseInt(row[5]));
                hardpoints.put("Left Torso", Integer.parseInt(row[6]));
                hardpoints.put("Head", Integer.parseInt(row[7]));
                hardpoints.put("Center Torso", Integer.parseInt(row[8]));
                hardpoints.put("Right Torso", Integer.parseInt(row[9]));
                hardpoints.put("Right Arm", Integer.parseInt(row[10]));

                MechChassis chassis = new MechChassis(name, chassisSize, tonnage, maxArmorTonnage, hexSpeed, hardpoints);
                chassisList.add(chassis);
            }
            
        }

        return chassisList;
    }
}