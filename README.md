# Thesis Checker
A project to automatically verify thesis formatting.

## Development environment requirements
- Flutter SDK 3.9.2
- Java 25 ([GraalVM](https://www.graalvm.org/downloads/))
- Maven

## Building an OS bundle/Installer
### Prerequisites
Only for Windows: (TODO: update)
- Install WiX 3.
- Add it to the PATH. Path example: "C:\Program Files (x86)\WiX Toolset v3.14\bin"

### How to build
1. Build java app executing `mvn install`
2. Build flutter app executing a command for your platform
   - `flutter build macos`
   - `flutter build windows`

## Signing/Notarizing

### Prerequisites
On Windows:
- Azure Trusted Signing Certificate Profile
- SignTool installed
- Trusted Signing Client Tools installed
- Azure CLI installed and authenticated with your account

On macOS:
- Apple Developer Account
- Xcode Command Line Tools installed
- Developer ID certificate added to your Keychain
- `notarytool` authenticated with App Store Connect
