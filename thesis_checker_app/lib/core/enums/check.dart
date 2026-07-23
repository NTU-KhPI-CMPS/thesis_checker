/// Central enum with all available thesis checks.
enum Check {
  fontName('FONT_NAME'),
  fontSize('FONT_SIZE'),
  lineSpacing('LINE_SPACING'),
  alignment('ALIGNMENT'),
  listFormatting('LIST_FORMATTING'),
  spacing('SPACING'),
  indentation('INDENTATION'),
  // firstLineIndentation('FIRST_LINE_INDENTATION'), need to add this code when the checker will be implemented
  structuralElement('STRUCTURAL_ELEMENT'),
  formula('FORMULA');

  final String name;
  const Check(this.name);
}
