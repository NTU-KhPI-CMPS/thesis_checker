import 'package:flutter/material.dart';
import 'package:thesis_checker/data/models/format_error_api.dart';
import 'package:thesis_checker/l10n/l10n.dart';

extension ErrorLocalizationX on FormatErrorApi {
  String? getLocalizedMessage(BuildContext context) {
    final l10n = AppLocalizations.of(context);

    switch (id) {
      case 'err_figure_alignment':
        return l10n?.errFigureAlignment;
      case 'err_figure_blank_line':
        return l10n?.errFigureBlankLine;
      case 'err_figure_missing_caption':
        return l10n?.errFigureMissingCaption;
      case 'err_figure_unexpected_in_caption':
        return l10n?.errFigureUnexpectedInCaption;
      case 'err_figure_caption_format':
        return l10n?.errFigureCaptionFormat;

      case 'err_indentation_left':
        return l10n?.errIndentationLeft;
      case 'err_indentation_right':
        return l10n?.errIndentationRight;

      case 'err_linespace':
        return l10n?.errLinespace;

      case 'err_structural_element_missing':
        return l10n?.errStructuralElementMissing;
      case 'err_structural_element_order':
        return l10n?.errStructuralElementOrder;
      case 'warn_structural_element_new_page':
        return l10n?.warnStructuralElementNewPage;

      case 'err_alignment':
        return l10n?.errAlignment;

      case 'err_list_level_skip':
        return l10n?.errListLevelSkip;
      case 'err_list_format':
        return l10n?.errListFormat;
      case 'err_list_marker_format':
        return l10n?.errListMarkerFormat;
      case 'err_list_marker_suffix':
        return l10n?.errListMarkerSuffix;
      case 'err_list_bullet_char':
        return l10n?.errListBulletChar;
      case 'warn_list_manual':
        return l10n?.warnListManual;

      case 'err_spacing_before':
        return l10n?.errSpacingBefore;
      case 'err_spacing_after':
        return l10n?.errSpacingAfter;

      case 'err_font':
        return l10n?.errFont;
      case 'err_font_header':
        return l10n?.errFontHeader;
      case 'err_font_footer':
        return l10n?.errFontFooter;
      case 'err_font_table':
        return l10n?.errFontTable;

      case 'err_font_size':
        return l10n?.errFontSize;
      case 'err_font_size_header':
        return l10n?.errFontSizeHeader;
      case 'err_font_size_footer':
        return l10n?.errFontSizeFooter;
      case 'err_font_size_table':
        return l10n?.errFontSizeTable;

      case 'err_formula_alignment':
        return l10n?.errFormulaAlignment;
      case 'err_formula_multiple_per_line':
        return l10n?.errFormulaMultiplePerLine;
      case 'err_formula_not_tool':
        return l10n?.errFormulaNotTool;
      case 'err_formula_chapter_mismatch':
        return l10n?.errFormulaChapterMismatch;
      case 'err_formula_sequence':
        return l10n?.errFormulaSequence;
      case 'err_formula_format':
        return l10n?.errFormulaFormat;
      case 'err_formula_font_size':
        return l10n?.errFormulaFontSize;
      case 'err_formula_spacing_before':
        return l10n?.errFormulaSpacingBefore;
      case 'err_formula_spacing_after':
        return l10n?.errFormulaSpacingAfter;
      case 'err_formula_spacing_before_notation':
        return l10n?.errFormulaSpacingBeforeNotation;
      case 'err_formula_notation_spacing':
        return l10n?.errFormulaNotationSpacing;

      case 'err_000':
        return l10n?.err000;

      default:
        return id;
    }
  }
}
