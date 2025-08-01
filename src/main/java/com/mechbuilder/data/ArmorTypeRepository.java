package com.mechbuilder.data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.mechbuilder.model.ArmorType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for loading and managing armor type data from CSV file.
 */
public class ArmorTypeRepository {
    private static final String RESOURCE_PATH = "Armor Types.csv";
    
    public List<ArmorType> loadAll() throws IOException, CsvValidationException {
        List<ArmorType> armorTypes = new ArrayList<>();
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(RESOURCE_PATH);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + RESOURCE_PATH);
        }
        
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] header = reader.readNext(); // Skip header row
            if (header == null) {
                throw new IOException("CSV file is empty or improperly formatted.");
            }

            while (true) {
                String[] row = reader.readNext();
                if (row == null) break;
                
                if (row.length < 3) continue; // Make sure there are enough columns
                
                try {
                    String armorType = row[0].trim();
                    int hpPerTon = Integer.parseInt(row[1].trim());
                    String type = row[2].trim();
                    
                    // Skip empty rows
                    if (armorType.isEmpty()) continue;
                    
                    ArmorType armor = new ArmorType(armorType, hpPerTon, type);
                    armorTypes.add(armor);
                } catch (NumberFormatException e) {
                    // Skip malformed rows
                    System.err.println("Skipping malformed armor row: " + String.join(",", row));
                }
            }
        }
        
        return armorTypes;
    }
    
    /**
     * Find armor type by name
     */
    public Optional<ArmorType> findByName(String name) throws IOException, CsvValidationException {
        return loadAll().stream()
                .filter(armor -> armor.getArmorType().equalsIgnoreCase(name))
                .findFirst();
    }
    
    /**
     * Find armor types by category (Plate, Refractive, Ablative)
     */
    public List<ArmorType> findByType(String type) throws IOException, CsvValidationException {
        return loadAll().stream()
                .filter(armor -> armor.getType().equalsIgnoreCase(type))
                .toList();
    }
    
    /**
     * Find armor types within HP/Ton range
     */
    public List<ArmorType> findByHpPerTonRange(int minHp, int maxHp) throws IOException, CsvValidationException {
        return loadAll().stream()
                .filter(armor -> armor.getHpPerTon() >= minHp && armor.getHpPerTon() <= maxHp)
                .toList();
    }
    
    /**
     * Get the best armor type (highest HP/Ton) for a given category
     */
    public Optional<ArmorType> getBestArmorByType(String type) throws IOException, CsvValidationException {
        return findByType(type).stream()
                .max((a1, a2) -> Integer.compare(a1.getHpPerTon(), a2.getHpPerTon()));
    }
}