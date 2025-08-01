package com.mechbuilder.data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.mechbuilder.model.Shield;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for loading and managing shield data from CSV file.
 */
public class ShieldRepository {
    private static final String RESOURCE_PATH = "Shields.csv";
    
    public List<Shield> loadAll() throws IOException, CsvValidationException {
        List<Shield> shields = new ArrayList<>();
        
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
                
                if (row.length < 7) continue; // Make sure there are enough columns
                
                try {
                    String shield = row[0].trim();
                    int hp = Integer.parseInt(row[1].trim());
                    int rechargeRate = Integer.parseInt(row[2].trim());
                    int rechargeDelay = Integer.parseInt(row[3].trim());
                    double tonnage = Double.parseDouble(row[4].trim());
                    int heatGenerated = Integer.parseInt(row[5].trim());
                    int rechargingHeat = Integer.parseInt(row[6].trim());
                    
                    // Skip empty rows
                    if (shield.isEmpty()) continue;
                    
                    Shield shieldObj = new Shield(shield, hp, rechargeRate, rechargeDelay, 
                                                tonnage, heatGenerated, rechargingHeat);
                    shields.add(shieldObj);
                } catch (NumberFormatException e) {
                    // Skip malformed rows
                    System.err.println("Skipping malformed shield row: " + String.join(",", row));
                }
            }
        }
        
        return shields;
    }
    
    /**
     * Find shield by name
     */
    public Optional<Shield> findByName(String name) throws IOException, CsvValidationException {
        return loadAll().stream()
                .filter(shield -> shield.getShield().equalsIgnoreCase(name))
                .findFirst();
    }
    
    /**
     * Find shields within tonnage range
     */
    public List<Shield> findByTonnageRange(double minTonnage, double maxTonnage) throws IOException, CsvValidationException {
        return loadAll().stream()
                .filter(shield -> shield.getTonnage() >= minTonnage && shield.getTonnage() <= maxTonnage)
                .toList();
    }
    
    /**
     * Find shields within HP range
     */
    public List<Shield> findByHpRange(int minHp, int maxHp) throws IOException, CsvValidationException {
        return loadAll().stream()
                .filter(shield -> shield.getHp() >= minHp && shield.getHp() <= maxHp)
                .toList();
    }
    
    /**
     * Get shields excluding "None" option
     */
    public List<Shield> findActualShields() throws IOException, CsvValidationException {
        return loadAll().stream()
                .filter(shield -> !shield.isNoneShield())
                .toList();
    }
    
    /**
     * Get the most efficient shield (highest HP per ton ratio)
     */
    public Optional<Shield> getMostEfficientShield() throws IOException, CsvValidationException {
        return findActualShields().stream()
                .max((s1, s2) -> Double.compare(s1.getEfficiencyRating(), s2.getEfficiencyRating()));
    }
    
    /**
     * Get shields sorted by efficiency (HP per ton)
     */
    public List<Shield> findShieldsByEfficiency() throws IOException, CsvValidationException {
        return findActualShields().stream()
                .sorted((s1, s2) -> Double.compare(s2.getEfficiencyRating(), s1.getEfficiencyRating()))
                .toList();
    }
}