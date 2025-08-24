# Changelog

All notable changes to the Mech Builder and Player application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.0] - 2024-08-24

### Added
- Field repair armor system for temporary armor HP restoration
- Enhanced damage visualization with separate Armor HP and Structure HP displays
- Compact damage input boxes with Enter key functionality
- Global "Reset All HP" button for restoring all sections to maximum HP
- Section-specific slot HP values (Arms: 24, Torsos: 32/60, Legs: 36, Head: 30)

### Changed
- Simplified damage input system - removed separate Apply/Repair buttons
- Reduced damage input box width for better UI compactness
- Enhanced armor HP display to show calculation breakdown (tons × HP/ton)

### Fixed
- Weapons now correctly place in weapon slots instead of armor sections
- Armor HP values now update properly when armor allocation changes
- Section tonnage displays update immediately when weapons are added/removed

## [0.1.0] - 2024-08-24

### Added
- **Core Application Structure**
  - Java Swing-based desktop application
  - Maven project with dependency management
  - CSV-based data loading system

- **Mech Chassis System**
  - Support for 31 different mech chassis variants
  - Dynamic chassis selection with dropdown menu
  - Chassis-specific slot configurations and tonnage limits

- **Weapon System**
  - Database of 47 different weapons with detailed statistics
  - Drag & drop weapon placement system
  - Visual distinction between weapon hardpoints and general equipment slots
  - Double-click weapon removal functionality
  - Collapsible weapon arsenal with organized categories

- **Armor Allocation System**
  - Percentage-based armor allocation limits per section
  - Real-time armor HP calculation based on selected armor type
  - Section-specific armor controls with 0.25-ton increments
  - Support for 14 different armor types (30-100 HP per ton)

- **Damage Tracking System**
  - Individual section damage tracking
  - Armor-first damage application (damage to armor before slots)
  - 24 HP base value per slot
  - Damage input system integrated into each section
  - Field repair armor capabilities

- **Loadout Management**
  - Real-time equipped weapons overview panel
  - Total tonnage tracking (weapons + armor)
  - Overweight warnings for exceeding chassis limits
  - Section-by-section tonnage breakdown

- **User Interface**
  - Grid-based mech layout visualization
  - Intuitive drag & drop weapon placement
  - Visual feedback for valid/invalid drop targets
  - Compact damage input controls
  - Responsive layout with scrollable mech panel

### Changed
- **Architecture Improvements**
  - Implemented Repository pattern for data access
  - Added Factory pattern for mech section creation
  - Observer pattern for UI update callbacks
  - Transfer Handler pattern for drag & drop functionality

- **Data Management**
  - Enhanced CSV parsing with OpenCSV library
  - Structured data models for all game components
  - Centralized data loading and validation

### Technical Details
- **Java Version**: JDK 11+
- **Build System**: Maven with wrapper
- **Dependencies**: OpenCSV 5.8, JUnit 5.9.3
- **UI Framework**: Java Swing with custom components
- **Data Format**: CSV files for easy customization

## [0.0.9] - 2024-08-23

### Added
- **Initial Project Setup**
  - Maven project structure
  - Basic Java Swing application framework
  - CSV data loading infrastructure

- **Core Models**
  - MechChassis class for chassis data
  - WeaponComponent class for weapon specifications
  - MechSection class for individual mech sections
  - Basic data validation and error handling

- **User Interface Foundation**
  - Basic Swing UI components
  - Chassis selection dropdown
  - Simple mech layout display

### Changed
- **Development Approach**
  - Unified V1 and V2 functionality
  - Clean GridBagLayout implementation
  - Repository pattern for data access

## [0.0.8] - 2024-08-22

### Added
- **Data Loading System**
  - CSV file parsers for mech and weapon data
  - Repository classes for data access
  - Factory pattern for object creation

- **Basic UI Components**
  - Chassis selection interface
  - Weapon arsenal display
  - Section-based mech layout

## [0.0.7] - 2024-08-21

### Added
- **Project Initialization**
  - Git repository setup
  - Maven project configuration
  - Basic Java application structure

---

## Development Notes

### Key Features Implemented
1. **Dynamic Mech Configuration**: Full chassis selection system with real-time layout updates
2. **Advanced Weapon System**: Drag & drop placement with visual feedback and removal
3. **Comprehensive Armor System**: Percentage-based allocation with real-time HP calculation
4. **Damage Tracking**: Individual section damage with armor-first application
5. **Loadout Management**: Real-time overview of equipped items and tonnage

### Technical Achievements
- **Performance**: Real-time updates without lag
- **Usability**: Intuitive drag & drop interface
- **Maintainability**: Clean architecture with design patterns
- **Extensibility**: CSV-based data system for easy customization

### Known Issues & Future Improvements
- Weapon type restrictions (Energy/Ballistic/Missile) not yet implemented
- Heat sink management system needs development
- Advanced damage visualization could be enhanced
- Mech blueprint export/import functionality planned

### Testing Status
- Core functionality tested and working
- Drag & drop system validated
- Armor allocation system verified
- Damage tracking system tested
- Tonnage calculations confirmed accurate

---

## Contributing to the Changelog

When adding new entries to the changelog, please follow these guidelines:

1. **Use the existing format** and structure
2. **Group changes** by type (Added, Changed, Fixed, Removed)
3. **Be descriptive** but concise
4. **Include version numbers** for releases
5. **Add dates** for significant releases
6. **Use present tense** for current changes
7. **Reference issue numbers** when applicable

### Change Types
- **Added**: New features
- **Changed**: Changes in existing functionality
- **Deprecated**: Soon-to-be removed features
- **Removed**: Removed features
- **Fixed**: Bug fixes
- **Security**: Vulnerability fixes 