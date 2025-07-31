package com.mechbuilder.data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.mechbuilder.model.WeaponComponent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WeaponRepository {
    private static final String RESOURCE_PATH = "Weaponry Components.csv";
    
    public List<WeaponComponent> loadAll() throws IOException, CsvValidationException {
        List<WeaponComponent> list = new ArrayList<>();
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(RESOURCE_PATH);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + RESOURCE_PATH);
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
    
    public List<WeaponComponent> findByType(String type) throws IOException, CsvValidationException {
        return loadAll().stream()
                .filter(weapon -> weapon.getType().equals(type))
                .toList();
    }
    
    public List<WeaponComponent> findByName(String name) throws IOException, CsvValidationException {
        return loadAll().stream()
                .filter(weapon -> weapon.getName().contains(name))
                .toList();
    }
}