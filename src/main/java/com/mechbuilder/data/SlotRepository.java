package com.mechbuilder.data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.mechbuilder.model.SlotConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SlotRepository {
    private static final String RESOURCE_PATH = "Slot Count.csv";
    
    public List<SlotConfiguration> loadAll() throws IOException, CsvValidationException {
        List<SlotConfiguration> list = new ArrayList<>();
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(RESOURCE_PATH);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + RESOURCE_PATH);
        }
        
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] fields;
            reader.readNext(); // Skip header

            while ((fields = reader.readNext()) != null) {
                if (fields.length < 9) continue;

                try {
                    String mechSize = fields[0];
                    int leftArmSlots = Integer.parseInt(fields[1]);
                    int leftTorsoSlots = Integer.parseInt(fields[2]);
                    int cockpitSlots = Integer.parseInt(fields[3]);
                    int centerTorsoSlots = Integer.parseInt(fields[4]);
                    int rightTorsoSlots = Integer.parseInt(fields[5]);
                    int rightArmSlots = Integer.parseInt(fields[6]);
                    int leftLegSlots = Integer.parseInt(fields[7]);
                    int rightLegSlots = Integer.parseInt(fields[8]);

                    list.add(new SlotConfiguration(
                        mechSize, leftArmSlots, leftTorsoSlots, cockpitSlots,
                        centerTorsoSlots, rightTorsoSlots, rightArmSlots,
                        leftLegSlots, rightLegSlots));
                } catch (NumberFormatException ignored) {}
            }
        }

        return list;
    }
    
    public Optional<SlotConfiguration> findBySize(String mechSize) throws IOException, CsvValidationException {
        return loadAll().stream()
                .filter(config -> config.getMechSize().equals(mechSize))
                .findFirst();
    }
}