# Mech Builder and Player

A Java desktop application for building and configuring mechs for tabletop gaming or simulation. Built with Java Swing and powered by CSV data files for easy customization of mech chassis and weapon specifications.

## 🚀 Features

- **Dynamic Mech Configuration**: Select from 31 different mech chassis variants
- **Comprehensive Weapon Database**: 47 different weapons with detailed statistics
- **Interactive UI**: Grid-based visual representation of mech sections
- **Real-time Tonnage Calculation**: Track weight distribution across mech sections
- **Modular Design**: Easy to extend with new chassis and weapons via CSV files

## 📋 Prerequisites

Before running this application, ensure you have the following installed:

- **Java Development Kit (JDK) 11 or higher**
  - Download from [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) or [OpenJDK](https://openjdk.org/)
  - Verify installation: `java -version`

**Note**: Maven is NOT required! This project includes a Maven wrapper that automatically downloads the correct Maven version.

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/mech-builder-and-player.git
cd mech-builder-and-player
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


## 📁 Project Structure

```
mech-builder-and-player/
├── src/
│   ├── main/
│   │   ├── java/                    # Java source files
│   │   │   ├── MechBuilderUI.java # Unified application UI
│   │   │   ├── MechChassis.java     # Mech chassis model
│   │   │   ├── WeaponComponent.java # Weapon component model
│   │   │   ├── MechSection.java     # Mech section model
│   │   │   └── ...                  # Other source files
│   │   └── resources/               # Data files
│   │       ├── Mech Loadout Data.csv    # Chassis specifications
│   │       ├── Weaponry Components.csv  # Weapon database
│   │       └── Slot Count.csv          # Slot configurations
│   └── test/
│       └── java/                    # Test files (future)
├── target/                          # Build output (generated)
├── pom.xml                          # Maven configuration
├── README.md                        # This file
└── .gitignore                       # Git ignore rules
```

## 🎮 How to Use

### Basic Operation
1. **Launch the application** using one of the methods above
2. **Select a Mech Chassis** from the dropdown menu
3. **View the mech layout** - sections will appear based on the selected chassis
4. **Configure weapons** by selecting from dropdown menus in each section (future feature)
5. **Monitor tonnage** and other specifications in real-time

### Application Features
- **MechBuilderUI**: Unified interface combining dynamic chassis selection with clean grid layout
- **Dynamic Weapon Slots**: Weapon dropdowns adapt to each chassis's hardpoint configuration  
- **Real-time Tonnage**: Live weight calculation per section and overall mech

## 🔧 Development

### Dependencies
This project uses the following external libraries:
- **OpenCSV 5.8**: For CSV file parsing and data loading
- **JUnit 5.9.3**: For unit testing (scope: test)

### Adding New Content
To add new mechs or weapons:

1. **New Mech Chassis**: Edit `src/main/resources/Mech Loadout Data.csv`
2. **New Weapons**: Edit `src/main/resources/Weaponry Components.csv`
3. **Rebuild**: Run `./mvnw clean compile` to incorporate changes

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

### Getting Help
1. Check the console output for detailed error messages
2. Verify all prerequisites are installed correctly
3. Try running `./mvnw clean compile` to refresh dependencies

## 🏗️ Architecture

The application follows a layered architecture:
- **Presentation Layer**: Swing UI components (`MechBuilderUI*`)
- **Domain Layer**: Business models (`MechChassis`, `WeaponComponent`, etc.)
- **Data Layer**: CSV parsers and loaders (`MechChassisLoader`, `ComponentManager`)

## 📈 Future Enhancements

- [ ] Weapon-to-hardpoint compatibility validation
- [ ] Heat management system
- [ ] Tonnage limit enforcement
- [ ] Mech configuration save/load functionality
- [ ] Combat simulation capabilities
- [ ] Database backend option
- [ ] Multi-player support

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