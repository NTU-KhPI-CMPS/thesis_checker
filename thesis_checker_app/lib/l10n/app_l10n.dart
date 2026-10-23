import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:intl/intl.dart' as intl;

import 'app_l10n_en.dart';
import 'app_l10n_uk.dart';

// ignore_for_file: type=lint

/// Callers can lookup localized strings with an instance of AppLocalizations
/// returned by `AppLocalizations.of(context)`.
///
/// Applications need to include `AppLocalizations.delegate()` in their app's
/// `localizationDelegates` list, and the locales they support in the app's
/// `supportedLocales` list. For example:
///
/// ```dart
/// import 'l10n/app_l10n.dart';
///
/// return MaterialApp(
///   localizationsDelegates: AppLocalizations.localizationsDelegates,
///   supportedLocales: AppLocalizations.supportedLocales,
///   home: MyApplicationHome(),
/// );
/// ```
///
/// ## Update pubspec.yaml
///
/// Please make sure to update your pubspec.yaml to include the following
/// packages:
///
/// ```yaml
/// dependencies:
///   # Internationalization support.
///   flutter_localizations:
///     sdk: flutter
///   intl: any # Use the pinned version from flutter_localizations
///
///   # Rest of dependencies
/// ```
///
/// ## iOS Applications
///
/// iOS applications define key application metadata, including supported
/// locales, in an Info.plist file that is built into the application bundle.
/// To configure the locales supported by your app, you’ll need to edit this
/// file.
///
/// First, open your project’s ios/Runner.xcworkspace Xcode workspace file.
/// Then, in the Project Navigator, open the Info.plist file under the Runner
/// project’s Runner folder.
///
/// Next, select the Information Property List item, select Add Item from the
/// Editor menu, then select Localizations from the pop-up menu.
///
/// Select and expand the newly-created Localizations item then, for each
/// locale your application supports, add a new item and select the locale
/// you wish to add from the pop-up menu in the Value field. This list should
/// be consistent with the languages listed in the AppLocalizations.supportedLocales
/// property.
abstract class AppLocalizations {
  AppLocalizations(String locale)
    : localeName = intl.Intl.canonicalizedLocale(locale.toString());

  final String localeName;

  static AppLocalizations? of(BuildContext context) {
    return Localizations.of<AppLocalizations>(context, AppLocalizations);
  }

  static const LocalizationsDelegate<AppLocalizations> delegate =
      _AppLocalizationsDelegate();

  /// A list of this localizations delegate along with the default localizations
  /// delegates.
  ///
  /// Returns a list of localizations delegates containing this delegate along with
  /// GlobalMaterialLocalizations.delegate, GlobalCupertinoLocalizations.delegate,
  /// and GlobalWidgetsLocalizations.delegate.
  ///
  /// Additional delegates can be added by appending to this list in
  /// MaterialApp. This list does not have to be used at all if a custom list
  /// of delegates is preferred or required.
  static const List<LocalizationsDelegate<dynamic>> localizationsDelegates =
      <LocalizationsDelegate<dynamic>>[
        delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
      ];

  /// A list of this localizations delegate's supported locales.
  static const List<Locale> supportedLocales = <Locale>[
    Locale('en'),
    Locale('uk'),
  ];

  /// Application title
  ///
  /// In en, this message translates to:
  /// **'Thesis checker'**
  String get appTitle;

  /// Navigation label for the home screen
  ///
  /// In en, this message translates to:
  /// **'Home'**
  String get navHome;

  /// Heading for the section that explains what the app checks
  ///
  /// In en, this message translates to:
  /// **'What we check'**
  String get homeSectionTitle;

  /// Heading for the document upload area
  ///
  /// In en, this message translates to:
  /// **'Upload document'**
  String get uploadTitle;

  /// Helper text explaining how to provide a DOCX file
  ///
  /// In en, this message translates to:
  /// **'Drag a .docx file or click to choose'**
  String get uploadSubtitle;

  /// Button text for opening the file picker
  ///
  /// In en, this message translates to:
  /// **'+ Select file'**
  String get uploadSelectFile;

  /// Navigation label for the analysis screen
  ///
  /// In en, this message translates to:
  /// **'Analysis'**
  String get navAnalysis;

  /// Message shown when no document has been uploaded yet
  ///
  /// In en, this message translates to:
  /// **'Please upload a document for analysis'**
  String get loadingNoFile;

  /// Status text shown while the document is being analyzed
  ///
  /// In en, this message translates to:
  /// **'Analyzing document...'**
  String get loadingAnalyzing;

  /// Navigation label for the results screen
  ///
  /// In en, this message translates to:
  /// **'Results'**
  String get navResults;

  /// Button text for running the analysis again
  ///
  /// In en, this message translates to:
  /// **'Retry'**
  String get retryAnalysis;

  /// Heading for the list of result categories
  ///
  /// In en, this message translates to:
  /// **'Categories'**
  String get resultCategories;

  /// Heading for the detailed error list
  ///
  /// In en, this message translates to:
  /// **'Error details'**
  String get resultDetails;

  /// Message shown when no categories are selected
  ///
  /// In en, this message translates to:
  /// **'No categories selected to display'**
  String get resultNoSelectedCategories;

  /// Message when no errors are found for the chosen category
  ///
  /// In en, this message translates to:
  /// **'No errors found for category \"{category}\".'**
  String resultNoErrorsForCategory(String category);

  /// Number of errors with correct plural form
  ///
  /// In en, this message translates to:
  /// **'{count, plural, one{{count} error} other{{count} errors}}'**
  String errorCount(int count);

  /// Number of warnings with correct plural form
  ///
  /// In en, this message translates to:
  /// **'{count, plural, one{{count} warning} other{{count} warnings}}'**
  String warningCount(int count);

  /// Message shown when a text fragment is missing in error
  ///
  /// In en, this message translates to:
  /// **'Text fragment is missing.'**
  String get errorFragmentMissing;

  /// Label used in error fragments to show the expected text
  ///
  /// In en, this message translates to:
  /// **'→ should be'**
  String get errorArrowShouldBe;

  /// Title of the analysis settings dialog
  ///
  /// In en, this message translates to:
  /// **'Analysis settings'**
  String get dialogSettingsTitle;

  /// Prompt asking the user to choose checks to run
  ///
  /// In en, this message translates to:
  /// **'Choose checks to run'**
  String get dialogChooseChecks;

  /// Helper text explaining that multiple checks can be selected
  ///
  /// In en, this message translates to:
  /// **'You may select multiple checks at once'**
  String get dialogChooseChecksHint;

  /// Validation message shown when no checks are selected
  ///
  /// In en, this message translates to:
  /// **'Please select at least one check'**
  String get dialogChooseOneError;

  /// Button text for closing the dialog without changes
  ///
  /// In en, this message translates to:
  /// **'Cancel'**
  String get dialogCancel;

  /// Button text for starting analysis from the dialog
  ///
  /// In en, this message translates to:
  /// **'▶ Start analysis'**
  String get dialogStartAnalysis;

  /// Label for the light theme option
  ///
  /// In en, this message translates to:
  /// **'Light'**
  String get themeLight;

  /// Label for the dark theme option
  ///
  /// In en, this message translates to:
  /// **'Dark'**
  String get themeDark;

  /// Label for the system theme option
  ///
  /// In en, this message translates to:
  /// **'System'**
  String get themeSystem;

  /// Error message shown in a snackbar
  ///
  /// In en, this message translates to:
  /// **'An error occurred: {error}'**
  String snackbarError(String error);

  /// Title for the font check category
  ///
  /// In en, this message translates to:
  /// **'Font'**
  String get checkTypeFontTitle;

  /// Description for the font check category
  ///
  /// In en, this message translates to:
  /// **'Font name and size'**
  String get checkTypeFontDescription;

  /// Title for the spacing check category
  ///
  /// In en, this message translates to:
  /// **'Spacing'**
  String get checkTypeSpacingTitle;

  /// Description for the spacing check category
  ///
  /// In en, this message translates to:
  /// **'Line spacing and paragraph spacing'**
  String get checkTypeSpacingDescription;

  /// Title for the alignment check category
  ///
  /// In en, this message translates to:
  /// **'Alignment'**
  String get checkTypeAlignmentTitle;

  /// Description for the alignment check category
  ///
  /// In en, this message translates to:
  /// **'Paragraph and heading alignment'**
  String get checkTypeAlignmentDescription;

  /// Title for the formulas check category
  ///
  /// In en, this message translates to:
  /// **'Formulas'**
  String get checkTypeFormulaTitle;

  /// Description for the formulas check category
  ///
  /// In en, this message translates to:
  /// **'Description, numbering, and spacing around formulas'**
  String get checkTypeFormulaDescription;

  /// Title for the lists check category
  ///
  /// In en, this message translates to:
  /// **'Lists'**
  String get checkTypeListTitle;

  /// Description for the lists check category
  ///
  /// In en, this message translates to:
  /// **'Nesting, markers, and numbering format at each level'**
  String get checkTypeListDescription;

  /// Title for the other checks category
  ///
  /// In en, this message translates to:
  /// **'Other'**
  String get checkTypeOtherTitle;

  /// Description for the other checks category
  ///
  /// In en, this message translates to:
  /// **'Other checks that do not fit into categories'**
  String get checkTypeOtherDescription;

  /// Error shown when the selected file extension is not supported
  ///
  /// In en, this message translates to:
  /// **'File with .{extension} extension is not supported'**
  String unsupportedFileExtensionError(String extension);

  /// Error shown when analysis completes with an error
  ///
  /// In en, this message translates to:
  /// **'Analysis finished with an error'**
  String get analysisFinishedWithError;

  /// Error shown when analysis cannot be completed
  ///
  /// In en, this message translates to:
  /// **'Could not complete the analysis. Please try again.'**
  String get analysisCouldNotComplete;

  /// Error shown when text alignment does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect text alignment'**
  String get errAlignment;

  /// Error shown when paragraph fonts do not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect fonts in paragraph'**
  String get errFont;

  /// Error shown when header fonts do not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect fonts in header'**
  String get errFontHeader;

  /// Error shown when footer fonts do not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect fonts in footer'**
  String get errFontFooter;

  /// Error shown when table fonts do not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect fonts in table'**
  String get errFontTable;

  /// Error shown when paragraph font size does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect font size in paragraph'**
  String get errFontSize;

  /// Error shown when header font size does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect font size in header'**
  String get errFontSizeHeader;

  /// Error shown when footer font size does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect font size in footer'**
  String get errFontSizeFooter;

  /// Error shown when table font size does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect font size in table'**
  String get errFontSizeTable;

  /// Error shown when paragraph line spacing does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect line spacing in paragraph'**
  String get errLinespace;

  /// Error shown when a list nesting level is skipped
  ///
  /// In en, this message translates to:
  /// **'Skipped list nesting level'**
  String get errListLevelSkip;

  /// Error shown when numbering is inconsistent within one list level
  ///
  /// In en, this message translates to:
  /// **'Inconsistent numbering format on one list level'**
  String get errListFormat;

  /// Error shown when a list marker uses a disallowed format
  ///
  /// In en, this message translates to:
  /// **'Disallowed list marker format'**
  String get errListMarkerFormat;

  /// Error shown when a list item ends with a period instead of a parenthesis
  ///
  /// In en, this message translates to:
  /// **'List item should end with a parenthesis, not a period'**
  String get errListMarkerSuffix;

  /// Error shown when a list bullet character is not a dash
  ///
  /// In en, this message translates to:
  /// **'Disallowed list bullet character — should be a dash'**
  String get errListBulletChar;

  /// Warning shown when manual list formatting is detected
  ///
  /// In en, this message translates to:
  /// **'Manual list formatting detected'**
  String get warnListManual;

  /// Error shown when spacing before a paragraph does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect spacing before paragraph'**
  String get errSpacingBefore;

  /// Error shown when spacing after a paragraph does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect spacing after paragraph'**
  String get errSpacingAfter;

  /// Error shown when left indentation does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect left indentation'**
  String get errIndentationLeft;

  /// Error shown when right indentation does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect right indentation'**
  String get errIndentationRight;

  /// Error shown when a formula is aligned to the left
  ///
  /// In en, this message translates to:
  /// **'Formula aligned to the left'**
  String get errFormulaAlignment;

  /// Error shown when more than one formula appears on a line
  ///
  /// In en, this message translates to:
  /// **'Multiple formulas in one line'**
  String get errFormulaMultiplePerLine;

  /// Error shown when a formula is typed manually instead of using the Formula tool
  ///
  /// In en, this message translates to:
  /// **'Formula entered manually rather than using the \"Formula\" tool'**
  String get errFormulaNotTool;

  /// Error shown when a formula appears in the wrong chapter
  ///
  /// In en, this message translates to:
  /// **'Formula in incorrect chapter'**
  String get errFormulaChapterMismatch;

  /// Error shown when a formula numbering sequence is broken
  ///
  /// In en, this message translates to:
  /// **'Formula with broken sequence'**
  String get errFormulaSequence;

  /// Error shown when formula numbering format does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Formula with incorrect numbering format'**
  String get errFormulaFormat;

  /// Error shown when formula font size does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect font size in formula'**
  String get errFormulaFontSize;

  /// Error shown when spacing before a formula does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect spacing before formula'**
  String get errFormulaSpacingBefore;

  /// Error shown when spacing after a formula does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect spacing after formula'**
  String get errFormulaSpacingAfter;

  /// Error shown when spacing before formula notation does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect spacing before formula notation'**
  String get errFormulaSpacingBeforeNotation;

  /// Error shown when spacing inside formula notation does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect spacing in formula notation'**
  String get errFormulaNotationSpacing;

  /// Error shown when figure alignment does not match the requirements
  ///
  /// In en, this message translates to:
  /// **'Incorrect figure alignment'**
  String get errFigureAlignment;

  /// Error shown when blank lines before or after a figure are missing
  ///
  /// In en, this message translates to:
  /// **'Missing blank line before/after figure'**
  String get errFigureBlankLine;

  /// Error shown when a figure caption is missing
  ///
  /// In en, this message translates to:
  /// **'Missing figure caption'**
  String get errFigureMissingCaption;

  /// Error shown when drawing content appears inside a figure caption
  ///
  /// In en, this message translates to:
  /// **'Drawing inside caption'**
  String get errFigureUnexpectedInCaption;

  /// Error shown when a figure caption does not match the required format
  ///
  /// In en, this message translates to:
  /// **'Incorrect figure caption format (expected: «Figure <number> - <title>»)'**
  String get errFigureCaptionFormat;

  /// Error shown when a required structural element is missing
  ///
  /// In en, this message translates to:
  /// **'Required structural element is missing'**
  String get errStructuralElementMissing;

  /// Error shown when the document structural elements are in the wrong order
  ///
  /// In en, this message translates to:
  /// **'Document structural elements order violated'**
  String get errStructuralElementOrder;

  /// Warning shown when a structural element does not start on a new page
  ///
  /// In en, this message translates to:
  /// **'Structural element does not start on a new page'**
  String get warnStructuralElementNewPage;

  /// Error shown when the file cannot be opened
  ///
  /// In en, this message translates to:
  /// **'File open error'**
  String get err000;

  /// Alignment value: left
  ///
  /// In en, this message translates to:
  /// **'Left'**
  String get valueAlignmentLeft;

  /// Alignment value: right
  ///
  /// In en, this message translates to:
  /// **'Right'**
  String get valueAlignmentRight;

  /// Alignment value: centered
  ///
  /// In en, this message translates to:
  /// **'Centered'**
  String get valueAlignmentCenter;

  /// Alignment value: justified
  ///
  /// In en, this message translates to:
  /// **'Justified'**
  String get valueAlignmentBoth;

  /// Alignment value: distributed
  ///
  /// In en, this message translates to:
  /// **'Distributed'**
  String get valueAlignmentDistribute;

  /// Allowed alignment for formulas: justified or right
  ///
  /// In en, this message translates to:
  /// **'Justified or right'**
  String get valueAlignmentBothOrRight;

  /// List numbering format: arabic numerals
  ///
  /// In en, this message translates to:
  /// **'Arabic numerals'**
  String get valueListFormatDecimal;

  /// List numbering format: lowercase letters
  ///
  /// In en, this message translates to:
  /// **'Letters (lowercase)'**
  String get valueListFormatLowerLetter;

  /// List numbering format: uppercase letters
  ///
  /// In en, this message translates to:
  /// **'Letters (uppercase)'**
  String get valueListFormatUpperLetter;

  /// List numbering format: lowercase roman numerals
  ///
  /// In en, this message translates to:
  /// **'Roman numerals (lowercase)'**
  String get valueListFormatLowerRoman;

  /// List numbering format: uppercase roman numerals
  ///
  /// In en, this message translates to:
  /// **'Roman numerals (uppercase)'**
  String get valueListFormatUpperRoman;

  /// List numbering format: bullet marker
  ///
  /// In en, this message translates to:
  /// **'Marker (bullet)'**
  String get valueListFormatBullet;

  /// List numbering format: other
  ///
  /// In en, this message translates to:
  /// **'Other format'**
  String get valueListFormatOther;

  /// Expected value: one numbering format per list level
  ///
  /// In en, this message translates to:
  /// **'Consistent format'**
  String get valueListConsistent;

  /// Found value: blank line before a figure is missing
  ///
  /// In en, this message translates to:
  /// **'no blank line before'**
  String get valueFigureNoBlankBefore;

  /// Found value: blank line after a figure caption is missing
  ///
  /// In en, this message translates to:
  /// **'no blank line after'**
  String get valueFigureNoBlankAfter;

  /// Expected value: exactly one blank line
  ///
  /// In en, this message translates to:
  /// **'one blank line'**
  String get valueFigureOneBlankLine;

  /// Expected value: a paragraph with the figure caption
  ///
  /// In en, this message translates to:
  /// **'Paragraph with the «Figure» caption'**
  String get valueFigureCaptionParagraph;

  /// Found value: figure caption is missing
  ///
  /// In en, this message translates to:
  /// **'missing'**
  String get valueFigureCaptionMissing;

  /// Expected value: no drawing inside the caption
  ///
  /// In en, this message translates to:
  /// **'No drawing'**
  String get valueFigureNoDrawing;

  /// Found value: a drawing is present inside the caption
  ///
  /// In en, this message translates to:
  /// **'Drawing present'**
  String get valueFigureDrawingPresent;

  /// Expected figure caption format
  ///
  /// In en, this message translates to:
  /// **'«Figure <number> - <title>»'**
  String get valueFigureCaptionFormat;

  /// Expected value: use the Word formula tool
  ///
  /// In en, this message translates to:
  /// **'The formula must be inserted via Insert → Formula'**
  String get valueFormulaInsertViaTool;

  /// Expected value: one formula per line
  ///
  /// In en, this message translates to:
  /// **'1 formula per line'**
  String get valueFormulaOnePerLine;

  /// Expected value: blank line before a formula
  ///
  /// In en, this message translates to:
  /// **'Blank line before the formula'**
  String get valueFormulaBlankLineBefore;

  /// Expected value: blank line after a formula
  ///
  /// In en, this message translates to:
  /// **'Blank line after the formula'**
  String get valueFormulaBlankLineAfter;

  /// Expected value: explanation starts right below the formula
  ///
  /// In en, this message translates to:
  /// **'The «де» («where») explanation directly below the formula, without a blank line'**
  String get valueFormulaNotationDirectlyBelow;

  /// Expected value: blank line after the formula explanation block
  ///
  /// In en, this message translates to:
  /// **'Blank line after the explanation block'**
  String get valueFormulaBlankLineAfterNotation;

  /// Found value: the formula is the first paragraph of the document
  ///
  /// In en, this message translates to:
  /// **'Start of document'**
  String get valueDocumentStart;

  /// Placeholder for a paragraph that contains a formula
  ///
  /// In en, this message translates to:
  /// **'[Formula]'**
  String get valuePlaceholderFormula;

  /// Placeholder for an empty paragraph
  ///
  /// In en, this message translates to:
  /// **'[Blank line]'**
  String get valuePlaceholderBlankLine;

  /// Expected value: structural element starts on a new page
  ///
  /// In en, this message translates to:
  /// **'Page break before the element'**
  String get valueStructuralPageBreakBefore;

  /// Structural element title: abstract
  ///
  /// In en, this message translates to:
  /// **'ABSTRACT (РЕФЕРАТ)'**
  String get valueStructuralAbstract;

  /// Structural element title: table of contents
  ///
  /// In en, this message translates to:
  /// **'TABLE OF CONTENTS (ЗМІСТ)'**
  String get valueStructuralContents;

  /// Structural element title: list of symbols and abbreviations
  ///
  /// In en, this message translates to:
  /// **'LIST OF SYMBOLS AND ABBREVIATIONS (ПЕРЕЛІК ПОЗНАК ТА СКОРОЧЕНЬ)'**
  String get valueStructuralAbbreviations;

  /// Structural element title: introduction
  ///
  /// In en, this message translates to:
  /// **'INTRODUCTION (ВСТУП)'**
  String get valueStructuralIntroduction;

  /// Structural element title: conclusions
  ///
  /// In en, this message translates to:
  /// **'CONCLUSIONS (ВИСНОВКИ)'**
  String get valueStructuralConclusions;

  /// Structural element title: list of references
  ///
  /// In en, this message translates to:
  /// **'REFERENCES (СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ)'**
  String get valueStructuralReferences;

  /// Structural element title: appendix
  ///
  /// In en, this message translates to:
  /// **'APPENDIX (ДОДАТОК)'**
  String get valueStructuralAppendix;

  /// Length in centimeters
  ///
  /// In en, this message translates to:
  /// **'{value} cm'**
  String valueCentimeters(String value);
}

class _AppLocalizationsDelegate
    extends LocalizationsDelegate<AppLocalizations> {
  const _AppLocalizationsDelegate();

  @override
  Future<AppLocalizations> load(Locale locale) {
    return SynchronousFuture<AppLocalizations>(lookupAppLocalizations(locale));
  }

  @override
  bool isSupported(Locale locale) =>
      <String>['en', 'uk'].contains(locale.languageCode);

  @override
  bool shouldReload(_AppLocalizationsDelegate old) => false;
}

AppLocalizations lookupAppLocalizations(Locale locale) {
  // Lookup logic when only language code is specified.
  switch (locale.languageCode) {
    case 'en':
      return AppLocalizationsEn();
    case 'uk':
      return AppLocalizationsUk();
  }

  throw FlutterError(
    'AppLocalizations.delegate failed to load unsupported locale "$locale". This is likely '
    'an issue with the localizations generation tool. Please file an issue '
    'on GitHub with a reproducible sample app and the gen-l10n configuration '
    'that was used.',
  );
}
