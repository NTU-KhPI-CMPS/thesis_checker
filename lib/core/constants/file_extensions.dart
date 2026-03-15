class FileExtensions {
  static const List<String> supportedExtensions = ['docx'];

  static bool isSupported(String fileExtension) {
    return supportedExtensions.contains(fileExtension.toLowerCase());
  }
}
