package com.mechbuilder.ui.components;

import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;

/**
 * Tests for WeaponArsenalPanel UI component.
 */
class WeaponArsenalPanelTest {
    
    private WeaponArsenalPanel arsenalPanel;
    
    @BeforeEach
    void setUp() throws IOException, CsvValidationException {
        arsenalPanel = new WeaponArsenalPanel();
    }
    
    @Test
    void testArsenalPanelCreation() {
        assertNotNull(arsenalPanel);
        assertInstanceOf(BorderLayout.class, arsenalPanel.getLayout());
    }
    
    @Test
    void testTitledBorder() {
        assertTrue(arsenalPanel.getBorder() instanceof TitledBorder);
        TitledBorder titledBorder = (TitledBorder) arsenalPanel.getBorder();
        assertEquals("Weapon Arsenal", titledBorder.getTitle());
    }
    
    @Test
    void testPreferredSize() {
        Component scrollPane = ((BorderLayout) arsenalPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        assertInstanceOf(JScrollPane.class, scrollPane);
        
        JScrollPane scroll = (JScrollPane) scrollPane;
        assertEquals(new Dimension(220, 600), scroll.getPreferredSize());
    }
    
    @Test
    void testFilterPanelExists() {
        Component filterPanel = ((BorderLayout) arsenalPanel.getLayout()).getLayoutComponent(BorderLayout.NORTH);
        assertNotNull(filterPanel);
        assertInstanceOf(JPanel.class, filterPanel);
    }
    
    @Test
    void testScrollPaneConfiguration() {
        Component scrollPane = ((BorderLayout) arsenalPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        JScrollPane scroll = (JScrollPane) scrollPane;
        
        assertEquals(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, scroll.getVerticalScrollBarPolicy());
        assertEquals(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER, scroll.getHorizontalScrollBarPolicy());
    }
    
    @Test
    void testWeaponGridExists() {
        Component scrollPane = ((BorderLayout) arsenalPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        JScrollPane scroll = (JScrollPane) scrollPane;
        Component viewport = scroll.getViewport().getView();
        
        assertNotNull(viewport);
        assertInstanceOf(JPanel.class, viewport);
        
        JPanel weaponGrid = (JPanel) viewport;
        assertInstanceOf(BoxLayout.class, weaponGrid.getLayout());
    }
    
    @Test
    void testRefreshWeaponsDoesNotThrow() {
        assertDoesNotThrow(() -> {
            arsenalPanel.refreshWeapons();
        });
    }
    
    @Test
    void testWeaponGridHasCompactBorder() {
        Component scrollPane = ((BorderLayout) arsenalPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        JScrollPane scroll = (JScrollPane) scrollPane;
        JPanel weaponGrid = (JPanel) scroll.getViewport().getView();
        
        assertNotNull(weaponGrid.getBorder());
    }
}