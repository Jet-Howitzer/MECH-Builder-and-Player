# Mech Builder and Player

A Java desktop application for building and configuring mechs for tabletop gaming or simulation. Built with Java Swing and powered by CSV data files for easy customization of mech chassis and weapon specifications.

## 🚀 Features

- **Dynamic Mech Configuration**: Select from 31 different mech chassis variants
- **Comprehensive Weapon Database**: 47 different weapons with detailed statistics
- **Interactive UI**: Grid-based visual representation of mech sections
- **Real-time Tonnage Calculation**: Track weight distribution across mech sections
- **Modular Design**: Easy to extend with new chassis and weapons via CSV files
- **Advanced Armor System**: Allocate armor tonnage per section with percentage-based limits
- **Damage Tracking System**: Track damage to armor and individual slots with section-specific HP values
- **Drag & Drop Weapon System**: Intuitive weapon placement with visual feedback
- **Collapsible Weapon Arsenal**: Organized weapon categories for easy browsing
- **Real-time Loadout Overview**: Bottom panel showing all equipped weapons and total tonnage
- **Field Repair System**: Temporary armor repair capabilities
- **Visual Slot Distinction**: Red-highlighted weapon hardpoints vs. general equipment slots

## 📋 Prerequisites

Before running this application, ensure you have the following installed:

- **Java Development Kit (JDK) 11 or higher**
  - Download from [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) or [OpenJDK](https://openjdk.org/)
  - Verify installation: `java -version`

**Note**: Maven is NOT required! This project includes a Maven wrapper that automatically downloads the correct Maven version.

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/Jet-Howitzer/MECH-Builder-and-Player.git
cd MECH-Builder-and-Player
```

### 2. Build the Project
```bash
# Compile the project and download dependencies
./mvnw clean compile

# Run tests
./mvnw test

# Package the application into a JAR file
./mvnw package
```

**Windows users**: Replace `./mvnw` with `mvnw.cmd` in all commands above.

## ▶️ Running the Application

### Option 1: Run with Maven Wrapper (Recommended)
```bash
# Run the application
./mvnw exec:java
```

### Option 2: Run from JAR
```bash
# After running ./mvnw package, use the fat JAR with all dependencies
java -jar target/mech-builder-and-player-1.0.0-SNAPSHOT-jar-with-dependencies.jar
```

## 🎮 How to Use

### Basic Operation
1. **Launch the application** using one of the methods above
2. **Select a Mech Chassis** from the dropdown menu
3. **Select Armor Type** from the armor dropdown (affects HP per ton)
4. **View the mech layout** - sections will appear based on the selected chassis
5. **Allocate Armor** using +/- buttons in each section header (0.25 ton increments)
6. **Equip Weapons** by dragging from the right panel to weapon slots (red-highlighted)
7. **Monitor tonnage** and other specifications in real-time

### Advanced Features

#### Armor Allocation System
- **Percentage-based Limits**: Each section has maximum armor allocation based on chassis
  - Head: 5% of total max armor tonnage
  - Arms: 5% each
  - Torsos: 15% each (Left/Right), 25% (Center)
  - Legs: 15% each
- **Real-time Calculation**: Armor HP updates based on selected armor type and allocated tons
- **Visual Feedback**: Current armor allocation displayed in section headers

#### Weapon System
- **Drag & Drop**: Intuitive weapon placement from arsenal to weapon slots
- **Visual Distinction**: Red-highlighted weapon hardpoints vs. gray general equipment slots
- **Double-click Removal**: Remove weapons by double-clicking on equipped slots
- **Collapsible Arsenal**: Organized weapon categories for easy browsing
- **Real-time Updates**: Weapon tonnage and slot usage updates immediately

#### Damage Tracking System
- **Individual Section Damage**: Each section tracks damage independently
- **Armor-First Damage**: Damage applied to armor before affecting slots
- **Section-Specific Slot HP**: 
  - Arms: 24 HP per slot
  - Torsos: 32 HP per slot (Left/Right), 60 HP per slot (Center)
  - Legs: 36 HP per slot
  - Head: 30 HP per slot
- **Damage Input**: Compact damage input boxes in each section
- **Reset Functionality**: Global "Reset All HP" button restores all sections to maximum
- **Field Repair**: Temporary armor repair capabilities

#### Loadout Management
- **Equipped Weapons Panel**: Bottom-left panel showing all equipped weapons by section
- **Total Tonnage Tracking**: Real-time calculation of weapon + armor tonnage
- **Overweight Warnings**: Console alerts for exceeding chassis limits
- **Section-by-Section Breakdown**: Individual tonnage tracking per mech section

### Application Features
- **MechBuilderUI**: Unified interface combining dynamic chassis selection with clean grid layout
- **Dynamic Weapon Slots**: Weapon slots adapt to each chassis's hardpoint configuration  
- **Real-time Tonnage**: Live weight calculation per section and overall mech
- **Armor Allocation Panel**: Centralized armor management with section-specific controls
- **Weapon Arsenal Panel**: Organized weapon selection with collapsible categories
- **Damage Control Integration**: Built-in damage tracking in each mech section

## 📁 Project Structure

```
mech-builder-and-player/
├── src/
│   ├── main/
│   │   ├── java/                    # Java source files
│   │   │   ├── com/mechbuilder/
│   │   │   │   ├── MechBuilderApplication.java # Main application entry point
│   │   │   │   ├── ui/
│   │   │   │   │   ├── MechBuilderUI.java      # Unified application UI
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── WeaponArsenalPanel.java    # Collapsible weapon arsenal
│   │   │   │   │   │   ├── EquippedWeaponsPanel.java  # Loadout overview
│   │   │   │   │   │   └── ArmorAllocationPanel.java  # Armor management
│   │   │   │   │   └── dnd/
│   │   │   │   │       ├── MechSectionDropHandler.java # Drag & drop logic
│   │   │   │   │       ├── WeaponTransferable.java     # Weapon transfer data
│   │   │   │   │       └── WeaponTransferHandler.java   # Weapon transfer handling
│   │   │   │   ├── model/
│   │   │   │   │   ├── MechChassis.java     # Mech chassis model
│   │   │   │   │   ├── WeaponComponent.java # Weapon component model
│   │   │   │   │   ├── MechSection.java     # Mech section model with damage tracking
│   │   │   │   │   ├── ArmorType.java       # Armor type definitions
│   │   │   │   │   └── Shield.java          # Shield component model
│   │   │   │   └── data/
│   │   │   │       ├── MechChassisRepository.java # Chassis data access
│   │   │   │       ├── WeaponRepository.java      # Weapon data access
│   │   │   │       ├── MechSectionFactory.java    # Section creation factory
│   │   │   │       └── ...                         # Other repositories
│   │   │   └── ...                  # Other source files
│   │   └── resources/               # Data files
│   │       ├── Mech Loadout Data.csv    # Chassis specifications
│   │       ├── Weaponry Components.csv  # Weapon database
│   │       ├── Armor Types.csv          # Armor type definitions with HP/ton
│   │       ├── Slot Count.csv          # Slot configurations
│   │       └── Shields.csv             # Shield component data
│   └── test/
│       └── java/                    # Test files
├── target/                          # Build output (generated)
├── pom.xml                          # Maven configuration
├── README.md                        # This file
└── .gitignore                       # Git ignore rules
```

## 🔧 Development

### Dependencies
This project uses the following external libraries:
- **OpenCSV 5.8**: For CSV file parsing and data loading
- **JUnit 5.9.3**: For unit testing (scope: test)

### Adding New Content
To add new mechs, weapons, or armor types:

1. **New Mech Chassis**: Edit `src/main/resources/Mech Loadout Data.csv`
2. **New Weapons**: Edit `src/main/resources/Weaponry Components.csv`
3. **New Armor Types**: Edit `src/main/resources/Armor Types.csv`
4. **Rebuild**: Run `./mvnw clean compile` to incorporate changes

### CSV File Structure

#### Mech Loadout Data.csv
- `Name`: Chassis name
- `Max Armor Tonnage`: Maximum armor weight allowed
- `Tonnage`: Total chassis weight limit
- `Energy Hardpoints`, `Ballistic Hardpoints`, `Missile Hardpoints`: Weapon slot counts

#### Weaponry Components.csv
- `name`: Weapon name
- `type`: Weapon category
- `tonnage`: Weight of the weapon
- `damage`: Damage output
- Additional combat statistics

#### Armor Types.csv
- `name`: Armor type name
- `HP per ton`: Hit points provided per ton of armor
- `type`: Armor classification (Plate, Refractive, Ablative)

### Building for Distribution
```bash
# Create a fat JAR with all dependencies included
./mvnw clean package

# The executable JAR will be created at:
# target/mech-builder-and-player-1.0.0-SNAPSHOT-jar-with-dependencies.jar
```

## 🐛 Troubleshooting

### Common Issues

**"Resource not found" Error**
- Ensure CSV files are in `src/main/resources/`
- Rebuild the project: `./mvnw clean compile`

**Java Version Issues**
- Verify Java 11+ is installed: `java -version`
- Check JAVA_HOME environment variable

**Build Failures**
- Try cleaning and rebuilding: `./mvnw clean compile`
- Clear Maven cache: `./mvnw dependency:purge-local-repository`

**Application Won't Start**
- Check console output for specific error messages
- Ensure all dependencies are resolved: `./mvnw dependency:tree`

**Weapons Not Placing Correctly**
- Ensure weapons are being dropped on red-highlighted weapon slots
- Check that sections have available hardpoints
- Verify weapon type compatibility (future enhancement)

**Armor HP Not Updating**
- Check that armor allocation panel is properly initialized
- Verify armor type selection in dropdown
- Ensure CSV data is properly loaded

### Getting Help
1. Check the console output for detailed error messages
2. Verify all prerequisites are installed correctly
3. Try running `./mvnw clean compile` to refresh dependencies
4. Check that all CSV files contain valid data

## 🏗️ Architecture

The application follows a layered architecture:
- **Presentation Layer**: Swing UI components (`MechBuilderUI`, panels, handlers)
- **Domain Layer**: Business models (`MechChassis`, `WeaponComponent`, `MechSection`)
- **Data Layer**: CSV parsers and repositories (`MechChassisRepository`, `WeaponRepository`)
- **Transfer Layer**: Drag & drop handling (`MechSectionDropHandler`, `WeaponTransferable`)

### Key Design Patterns
- **Repository Pattern**: Data access abstraction
- **Factory Pattern**: Section creation (`MechSectionFactory`)
- **Observer Pattern**: Callback system for UI updates
- **Transfer Handler Pattern**: Drag & drop implementation

## 📈 Future Enhancements

- [x] Weapon-to-hardpoint compatibility validation
- [x] Heat management system (basic structure)
- [x] Tonnage limit enforcement
- [x] Mech configuration save/load functionality (structure in place)
- [x] Combat simulation capabilities (damage tracking implemented)
- [ ] Enhanced weapon type restrictions (Energy/Ballistic/Missile)
- [ ] Heat sink management and placement
- [ ] Advanced damage visualization
- [ ] Mech blueprint export/import
- [ ] Multi-player support
- [ ] Campaign mode with persistent damage

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Make your changes and test thoroughly
4. Commit with clear messages: `git commit -m "Add feature description"`
5. Push to your fork: `git push origin feature-name`
6. Create a Pull Request

## 📄 License

This project is open source. Please check the LICENSE file for details.

## 🙏 Acknowledgments

- Built with Java Swing for cross-platform desktop compatibility
- Data management powered by OpenCSV
- Maven for dependency management and build automation
- Drag & drop implementation using Java TransferHandler API