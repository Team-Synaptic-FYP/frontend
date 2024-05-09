# Lung Sound Classification App

<img src="./app/src/main/res/drawable/logo.png" alt="App Logo" width="200">

This Android application is designed for lung sound classification. It allows users to diagnose respiratory conditions based on recorded lung sounds.

**Organization :** [Team Synaptic](https://github.com/Team-Synaptic-FYP)

## Table of Contents
- [Features](#features)
- [Permissions](#permissions)
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Lung Sound Diagnosis:** classify lung sounds to diagnose respiratory conditions with explainability
- **Easy-to-Use Interface:** User-friendly interface for smooth navigation.
- **Light Weight CNN Backend:** Integrated with a light weight classification model backend deployed sever address [https://synaptic-gcp-2-ejgpqjuhtq-as.a.run.app/](https://synaptic-gcp-2-ejgpqjuhtq-as.a.run.app/)

## Permissions

This application requires the following permissions:

- **INTERNET:** Allows the app to access the internet for necessary operations.
- **ACCESS_NETWORK_STATE:** Enables the app to check network connectivity status.
- **READ_EXTERNAL_STORAGE:** Grants read access to external storage for reading recorded sound files.
- **WRITE_EXTERNAL_STORAGE:** Provides write access to external storage for saving recorded sound files.
- **ACCESS_WIFI_STATE:** Allows access to Wi-Fi state information for network operations.
- **ACCESS_BACKGROUND_LOCATION:** Grants access to the device's location information in the background for specific functionality.

## Installation

To install the Lung Sound Classification App:

1. Clone this repository: `git clone https://github.com/yourusername/lung-sound-classification.git`
2. Open the project in Android Studio.
3. Build and run the project on an Android device or emulator.

## Usage

1. Launch the application.
2. Navigate through the interface to upload lung sounds.
3. Use the classification feature to diagnose respiratory conditions.
4. View results and prediction explainability.

## Contributing

Contributions to this project are welcome! Here's how you can contribute:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/AmazingFeature`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add some amazing feature'`).
5. Push to the branch (`git push origin feature/AmazingFeature`).
6. Open a pull request.

## License

This project is licensed under the [MIT License](LICENSE).
