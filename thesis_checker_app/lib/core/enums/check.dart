/// Central enum with all available thesis checks.
enum Check {
  fontName('FONT_NAME'),
  fontSize('FONT_SIZE'),
  lineSpacing('LINE_SPACING'),
  spacing('SPACING'),
  alignment('ALIGNMENT'),
  indentation('INDENTATION'),
  firstLineIndentation('FIRST_LINE_INDENTATION');

  final String name;
  const Check(this.name);
}
