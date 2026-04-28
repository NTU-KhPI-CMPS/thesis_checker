# Thesis Checker
A project to automatically verify thesis formatting.

## Development environment requirements
- Flutter SDK 3.9.2
- Java 25 ([GraalVM](https://www.graalvm.org/downloads/))
- Maven

## Building and Signing/Notarising an OS Installer/bundle
### Prerequisites for building
On Windows:
- Install Visual Studio with "Desktop development with C++" workload

On macOS:
- Install Xcode
- Install create-dmg executing `brew install create-dmg`

### Prerequisites for signing/notarising
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

### Build and sign app on Windows
1. Build java app executing `mvn install`
2. Build an installer executing `dart run msix:create`
3. In [signing.json](tools/signing.json) set `CodeSigningAccountName` and `CertificateProfileName`
4. Run: [sign.bat](tools/sign.bat)

### Build, sign and notarise app on macOS
1. Build java app executing `mvn install`
2. In [build-macos.sh](tools/build-macos.sh) set `TEAM_ID` and `KEYCHAIN_PROFILE`
3. Run: [build-macos.sh](tools/build-macos.sh)
