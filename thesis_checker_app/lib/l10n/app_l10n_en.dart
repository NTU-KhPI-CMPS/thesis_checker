// ignore: unused_import
import 'package:intl/intl.dart' as intl;

import 'app_l10n.dart';

// ignore_for_file: type=lint

/// The translations for English (`en`).
class AppLocalizationsEn extends AppLocalizations {
  AppLocalizationsEn([String locale = 'en']) : super(locale);

  @override
  String get appTitle => 'Thesis checker';

  @override
  String get navHome => 'Home';

  @override
  String get homeSectionTitle => 'What we check';

  @override
  String get uploadTitle => 'Upload document';

  @override
  String get uploadSubtitle => 'Drag a .docx file or click to choose';

  @override
  String get uploadSelectFile => '+ Select file';

  @override
  String get navAnalysis => 'Analysis';

  @override
  String get loadingNoFile => 'Please upload a document for analysis';

  @override
  String get loadingAnalyzing => 'Analyzing document...';

  @override
  String get navResults => 'Results';

  @override
  String get retryAnalysis => 'Retry';

  @override
  String get resultCategories => 'Categories';

  @override
  String get resultDetails => 'Error details';

  @override
  String get resultNoSelectedCategories => 'No categories selected to display';

  @override
  String resultNoErrorsForCategory(String category) {
    return 'No errors found for category \"$category\".';
  }

  @override
  String errorCount(int count) {
    String _temp0 = intl.Intl.pluralLogic(
      count,
      locale: localeName,
      other: '$count errors',
      one: '$count error',
    );
    return '$_temp0';
  }

  @override
  String warningCount(int count) {
    String _temp0 = intl.Intl.pluralLogic(
      count,
      locale: localeName,
      other: '$count warnings',
      one: '$count warning',
    );
    return '$_temp0';
  }

  @override
  String get errorFragmentMissing => 'Text fragment is missing.';

  @override
  String get errorArrowShouldBe => '→ should be';

  @override
  String get dialogSettingsTitle => 'Analysis settings';

  @override
  String get dialogChooseChecks => 'Choose checks to run';

  @override
  String get dialogChooseChecksHint => 'You may select multiple checks at once';

  @override
  String get dialogChooseOneError => 'Please select at least one check';

  @override
  String get dialogCancel => 'Cancel';

  @override
  String get dialogStartAnalysis => '▶ Start analysis';

  @override
  String get themeLight => 'Light';

  @override
  String get themeDark => 'Dark';

  @override
  String get themeSystem => 'System';

  @override
  String snackbarError(String error) {
    return 'An error occurred: $error';
  }

  @override
  String get checkTypeFontTitle => 'Font';

  @override
  String get checkTypeFontDescription => 'Font name and size';

  @override
  String get checkTypeSpacingTitle => 'Spacing';

  @override
  String get checkTypeSpacingDescription =>
      'Line spacing and paragraph spacing';

  @override
  String get checkTypeAlignmentTitle => 'Alignment';

  @override
  String get checkTypeAlignmentDescription => 'Paragraph and heading alignment';

  @override
  String get checkTypeFormulaTitle => 'Formulas';

  @override
  String get checkTypeFormulaDescription =>
      'Description, numbering, and spacing around formulas';

  @override
  String get checkTypeListTitle => 'Lists';

  @override
  String get checkTypeListDescription =>
      'Nesting, markers, and numbering format at each level';

  @override
  String get checkTypeOtherTitle => 'Other';

  @override
  String get checkTypeOtherDescription =>
      'Other checks that do not fit into categories';

  @override
  String unsupportedFileExtensionError(String extension) {
    return 'File with .$extension extension is not supported';
  }

  @override
  String get analysisFinishedWithError => 'Analysis finished with an error';

  @override
  String get analysisCouldNotComplete =>
      'Could not complete the analysis. Please try again.';

  @override
  String get errAlignment => 'Incorrect text alignment';

  @override
  String get errFont => 'Incorrect fonts in paragraph';

  @override
  String get errFontHeader => 'Incorrect fonts in header';

  @override
  String get errFontFooter => 'Incorrect fonts in footer';

  @override
  String get errFontTable => 'Incorrect fonts in table';

  @override
  String get errFontSize => 'Incorrect font size in paragraph';

  @override
  String get errFontSizeHeader => 'Incorrect font size in header';

  @override
  String get errFontSizeFooter => 'Incorrect font size in footer';

  @override
  String get errFontSizeTable => 'Incorrect font size in table';

  @override
  String get errLinespace => 'Incorrect line spacing in paragraph';

  @override
  String get errListLevelSkip => 'Skipped list nesting level';

  @override
  String get errListFormat => 'Inconsistent numbering format on one list level';

  @override
  String get errListMarkerFormat => 'Disallowed list marker format';

  @override
  String get errListMarkerSuffix =>
      'List item should end with a parenthesis, not a period';

  @override
  String get errListBulletChar =>
      'Disallowed list bullet character — should be a dash';

  @override
  String get warnListManual => 'Manual list formatting detected';

  @override
  String get errSpacingBefore => 'Incorrect spacing before paragraph';

  @override
  String get errSpacingAfter => 'Incorrect spacing after paragraph';

  @override
  String get errIndentationLeft => 'Incorrect left indentation';

  @override
  String get errIndentationRight => 'Incorrect right indentation';

  @override
  String get errFormulaAlignment => 'Formula aligned to the left';

  @override
  String get errFormulaMultiplePerLine => 'Multiple formulas in one line';

  @override
  String get errFormulaNotTool =>
      'Formula entered manually rather than using the \"Formula\" tool';

  @override
  String get errFormulaChapterMismatch => 'Formula in incorrect chapter';

  @override
  String get errFormulaSequence => 'Formula with broken sequence';

  @override
  String get errFormulaFormat => 'Formula with incorrect numbering format';

  @override
  String get errFormulaFontSize => 'Incorrect font size in formula';

  @override
  String get errFormulaSpacingBefore => 'Incorrect spacing before formula';

  @override
  String get errFormulaSpacingAfter => 'Incorrect spacing after formula';

  @override
  String get errFormulaSpacingBeforeNotation =>
      'Incorrect spacing before formula notation';

  @override
  String get errFormulaNotationSpacing =>
      'Incorrect spacing in formula notation';

  @override
  String get errFigureAlignment => 'Incorrect figure alignment';

  @override
  String get errFigureBlankLine => 'Missing blank line before/after figure';

  @override
  String get errFigureMissingCaption => 'Missing figure caption';

  @override
  String get errFigureUnexpectedInCaption => 'Drawing inside caption';

  @override
  String get errFigureCaptionFormat =>
      'Incorrect figure caption format (expected: «Figure <number> - <title>»)';

  @override
  String get errStructuralElementMissing =>
      'Required structural element is missing';

  @override
  String get errStructuralElementOrder =>
      'Document structural elements order violated';

  @override
  String get warnStructuralElementNewPage =>
      'Structural element does not start on a new page';

  @override
  String get err000 => 'File open error';

  @override
  String get valueAlignmentLeft => 'Left';

  @override
  String get valueAlignmentRight => 'Right';

  @override
  String get valueAlignmentCenter => 'Centered';

  @override
  String get valueAlignmentBoth => 'Justified';

  @override
  String get valueAlignmentDistribute => 'Distributed';

  @override
  String get valueAlignmentBothOrRight => 'Justified or right';

  @override
  String get valueListFormatDecimal => 'Arabic numerals';

  @override
  String get valueListFormatLowerLetter => 'Letters (lowercase)';

  @override
  String get valueListFormatUpperLetter => 'Letters (uppercase)';

  @override
  String get valueListFormatLowerRoman => 'Roman numerals (lowercase)';

  @override
  String get valueListFormatUpperRoman => 'Roman numerals (uppercase)';

  @override
  String get valueListFormatBullet => 'Marker (bullet)';

  @override
  String get valueListFormatOther => 'Other format';

  @override
  String get valueListConsistent => 'Consistent format';

  @override
  String get valueFigureNoBlankBefore => 'no blank line before';

  @override
  String get valueFigureNoBlankAfter => 'no blank line after';

  @override
  String get valueFigureOneBlankLine => 'one blank line';

  @override
  String get valueFigureCaptionParagraph =>
      'Paragraph with the «Figure» caption';

  @override
  String get valueFigureCaptionMissing => 'missing';

  @override
  String get valueFigureNoDrawing => 'No drawing';

  @override
  String get valueFigureDrawingPresent => 'Drawing present';

  @override
  String get valueFigureCaptionFormat => '«Figure <number> - <title>»';

  @override
  String get valueFormulaInsertViaTool =>
      'The formula must be inserted via Insert → Formula';

  @override
  String get valueFormulaOnePerLine => '1 formula per line';

  @override
  String get valueFormulaBlankLineBefore => 'Blank line before the formula';

  @override
  String get valueFormulaBlankLineAfter => 'Blank line after the formula';

  @override
  String get valueFormulaNotationDirectlyBelow =>
      'The «де» («where») explanation directly below the formula, without a blank line';

  @override
  String get valueFormulaBlankLineAfterNotation =>
      'Blank line after the explanation block';

  @override
  String get valueDocumentStart => 'Start of document';

  @override
  String get valuePlaceholderFormula => '[Formula]';

  @override
  String get valuePlaceholderBlankLine => '[Blank line]';

  @override
  String get valueStructuralPageBreakBefore => 'Page break before the element';

  @override
  String get valueStructuralAbstract => 'ABSTRACT (РЕФЕРАТ)';

  @override
  String get valueStructuralContents => 'TABLE OF CONTENTS (ЗМІСТ)';

  @override
  String get valueStructuralAbbreviations =>
      'LIST OF SYMBOLS AND ABBREVIATIONS (ПЕРЕЛІК ПОЗНАК ТА СКОРОЧЕНЬ)';

  @override
  String get valueStructuralIntroduction => 'INTRODUCTION (ВСТУП)';

  @override
  String get valueStructuralConclusions => 'CONCLUSIONS (ВИСНОВКИ)';

  @override
  String get valueStructuralReferences =>
      'REFERENCES (СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ)';

  @override
  String get valueStructuralAppendix => 'APPENDIX (ДОДАТОК)';

  @override
  String valueCentimeters(String value) {
    return '$value cm';
  }
}
