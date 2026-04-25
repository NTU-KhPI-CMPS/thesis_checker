# Thesis Checker
A project to automatically verify thesis formatting.

## Development environment requirements
- Flutter SDK 3.9.2
- Java 25 ([GraalVM](https://www.graalvm.org/downloads/))
- Maven

## Building an OS bundle/Installer
### Prerequisites
On Windows:
- Install Visual Studio with "Desktop development with C++" workload.

On macOS:
- Install Xcode

### How to build
1. Build java app executing `mvn install`
2. Build flutter app executing a command for your platform
   - macOS: `flutter build macos`
   - Windows: `dart run msix:create`

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

### Signing Windows Installer
- In [signing.json](tools/signing.json) set `CodeSigningAccountName` and `CertificateProfileName`
- Run: [sign.bat](tools/sign.bat)
