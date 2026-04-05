/// Reusable presentation model for UI items with icon and text blocks.
///
/// Can be used in info cards and any other compact list/grid UI sections.
class InfoItemModel {
  final String iconPath;
  final String title;
  final String subtitle;
  final String? description;
  final Map<String, String>? meta;

  const InfoItemModel({
    required this.iconPath,
    required this.title,
    required this.subtitle,
    this.description,
    this.meta,
  });
}
