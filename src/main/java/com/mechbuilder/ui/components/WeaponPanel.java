package com.mechbuilder.ui.components;

import com.mechbuilder.model.WeaponComponent;
import com.mechbuilder.ui.dnd.WeaponTransferHandler;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A visual component representing a weapon in the arsenal.
 * Supports drag and drop operations.
 */
public class WeaponPanel extends JPanel {
    
    private final WeaponComponent weapon;
    private final JLabel nameLabel;
    private final JLabel typeLabel;
    private final JLabel damageLabel;
    private final JLabel tonnageLabel;
    
    private static final Color DEFAULT_BACKGROUND = new Color(245, 245, 245);
    private static final Color HOVER_BACKGROUND = new Color(230, 240, 255);
    private static final Color DRAG_BACKGROUND = new Color(200, 220, 255);
    
    private static final Border DEFAULT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.GRAY, 1),
        BorderFactory.createEmptyBorder(2, 4, 2, 4)
    );
    
    private static final Border HOVER_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(100, 150, 255), 2),
        BorderFactory.createEmptyBorder(1, 3, 1, 3)
    );
    
    public WeaponPanel(WeaponComponent weapon) {
        this.weapon = weapon;
        
        setLayout(new BorderLayout());
        setBackground(DEFAULT_BACKGROUND);
        setBorder(DEFAULT_BORDER);
        setPreferredSize(new Dimension(200, 35));
        setMaximumSize(new Dimension(200, 35));
        setMinimumSize(new Dimension(200, 35));
        
        // Create compact main label with name and key stats
        nameLabel = new JLabel(String.format("<html><b>%s</b><br/><small>%s | D:%d | T:%.1f</small></html>", 
            weapon.getName(), 
            weapon.getType().substring(0, 1), // Just first letter of type
            weapon.getDamage(),
            weapon.getTonnage()));
        nameLabel.setFont(nameLabel.getFont().deriveFont(10f));
        nameLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // Create type indicator label
        typeLabel = new JLabel(weapon.getType().substring(0, 1));
        typeLabel.setFont(typeLabel.getFont().deriveFont(Font.BOLD, 12f));
        typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        typeLabel.setOpaque(true);
        typeLabel.setPreferredSize(new Dimension(20, 35));
        
        // Color code by weapon type
        switch (weapon.getType()) {
            case "Energy":
                typeLabel.setBackground(new Color(255, 200, 200));
                typeLabel.setForeground(Color.RED);
                break;
            case "Ballistic":
                typeLabel.setBackground(new Color(200, 255, 200));
                typeLabel.setForeground(Color.DARK_GRAY);
                break;
            case "Missile":
                typeLabel.setBackground(new Color(200, 200, 255));
                typeLabel.setForeground(Color.BLUE);
                break;
            default:
                typeLabel.setBackground(Color.LIGHT_GRAY);
        }
        
        // Unused labels for compatibility
        damageLabel = new JLabel();
        tonnageLabel = new JLabel();
        
        // Add components
        add(typeLabel, BorderLayout.WEST);
        add(nameLabel, BorderLayout.CENTER);
        
        // Set up drag and drop
        setTransferHandler(new WeaponTransferHandler());
        
        // Add mouse listeners for visual feedback
        setupMouseListeners();
    }
    
    private void setupMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(HOVER_BACKGROUND);
                setBorder(HOVER_BORDER);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(DEFAULT_BACKGROUND);
                setBorder(DEFAULT_BORDER);
                setCursor(Cursor.getDefaultCursor());
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    setBackground(DRAG_BACKGROUND);
                    
                    // Start drag operation
                    TransferHandler handler = getTransferHandler();
                    if (handler != null) {
                        handler.exportAsDrag(WeaponPanel.this, e, TransferHandler.COPY);
                    }
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isMouseInside(e.getPoint())) {
                    setBackground(DEFAULT_BACKGROUND);
                    setBorder(DEFAULT_BORDER);
                } else {
                    setBackground(HOVER_BACKGROUND);
                    setBorder(HOVER_BORDER);
                }
            }
        };
        
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }
    
    private boolean isMouseInside(Point point) {
        return point.x >= 0 && point.y >= 0 && 
               point.x < getWidth() && point.y < getHeight();
    }
    
    public WeaponComponent getWeapon() {
        return weapon;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Add subtle gradient effect
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        GradientPaint gradient = new GradientPaint(
            0, 0, getBackground(),
            0, getHeight(), getBackground().darker()
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.dispose();
    }
}