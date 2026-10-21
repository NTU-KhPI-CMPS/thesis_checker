import 'package:thesis_checker/l10n/l10n.dart';

typedef MessageResolver = String Function(AppLocalizations l10n);

/// A mapping of error message identifiers to their corresponding localized message resolvers.
final Map<String, MessageResolver> errorMessageResolvers = {
  'err_figure_alignment': (l) => l.errFigureAlignment,
  'err_figure_blank_line': (l) => l.errFigureBlankLine,
  'err_figure_missing_caption': (l) => l.errFigureMissingCaption,
  'err_figure_unexpected_in_caption': (l) => l.errFigureUnexpectedInCaption,
  'err_figure_caption_format': (l) => l.errFigureCaptionFormat,

  'err_indentation_left': (l) => l.errIndentationLeft,
  'err_indentation_right': (l) => l.errIndentationRight,

  'err_linespace': (l) => l.errLinespace,

  'err_structural_element_missing': (l) => l.errStructuralElementMissing,
  'err_structural_element_order': (l) => l.errStructuralElementOrder,
  'warn_structural_element_new_page': (l) => l.warnStructuralElementNewPage,

  'err_alignment': (l) => l.errAlignment,

  'err_list_level_skip': (l) => l.errListLevelSkip,
  'err_list_format': (l) => l.errListFormat,
  'err_list_marker_format': (l) => l.errListMarkerFormat,
  'err_list_marker_suffix': (l) => l.errListMarkerSuffix,
  'err_list_bullet_char': (l) => l.errListBulletChar,
  'warn_list_manual': (l) => l.warnListManual,

  'err_spacing_before': (l) => l.errSpacingBefore,
  'err_spacing_after': (l) => l.errSpacingAfter,

  'err_font': (l) => l.errFont,
  'err_font_header': (l) => l.errFontHeader,
  'err_font_footer': (l) => l.errFontFooter,
  'err_font_table': (l) => l.errFontTable,

  'err_font_size': (l) => l.errFontSize,
  'err_font_size_header': (l) => l.errFontSizeHeader,
  'err_font_size_footer': (l) => l.errFontSizeFooter,
  'err_font_size_table': (l) => l.errFontSizeTable,

  'err_formula_alignment': (l) => l.errFormulaAlignment,
  'err_formula_multiple_per_line': (l) => l.errFormulaMultiplePerLine,
  'err_formula_not_tool': (l) => l.errFormulaNotTool,
  'err_formula_chapter_mismatch': (l) => l.errFormulaChapterMismatch,
  'err_formula_sequence': (l) => l.errFormulaSequence,
  'err_formula_format': (l) => l.errFormulaFormat,
  'err_formula_font_size': (l) => l.errFormulaFontSize,
  'err_formula_spacing_before': (l) => l.errFormulaSpacingBefore,
  'err_formula_spacing_after': (l) => l.errFormulaSpacingAfter,
  'err_formula_spacing_before_notation': (l) => l.errFormulaSpacingBeforeNotation,
  'err_formula_notation_spacing': (l) => l.errFormulaNotationSpacing,

  'err_000': (l) => l.err000,
};

/// Returns the localized error message for the given error identifier.
/// If the identifier is not found in the mapping, it returns the identifier itself.
String localizeByErrorId(AppLocalizations l10n, String id) =>
    errorMessageResolvers[id]?.call(l10n) ?? id;
