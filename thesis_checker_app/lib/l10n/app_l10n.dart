import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:intl/intl.dart' as intl;

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
  static const List<Locale> supportedLocales = <Locale>[Locale('uk')];

  /// No description provided for @appTitle.
  ///
  /// In uk, this message translates to:
  /// **'Thesis checker'**
  String get appTitle;

  /// No description provided for @navHome.
  ///
  /// In uk, this message translates to:
  /// **'Головна'**
  String get navHome;

  /// No description provided for @homeSectionTitle.
  ///
  /// In uk, this message translates to:
  /// **'Що перевіряємо'**
  String get homeSectionTitle;

  /// No description provided for @uploadTitle.
  ///
  /// In uk, this message translates to:
  /// **'Завантажте документ'**
  String get uploadTitle;

  /// No description provided for @uploadSubtitle.
  ///
  /// In uk, this message translates to:
  /// **'Перетягніть .docx файл або натисніть щоб обрати'**
  String get uploadSubtitle;

  /// No description provided for @uploadSelectFile.
  ///
  /// In uk, this message translates to:
  /// **'+ Обрати файл'**
  String get uploadSelectFile;

  /// No description provided for @navAnalysis.
  ///
  /// In uk, this message translates to:
  /// **'Аналіз'**
  String get navAnalysis;

  /// No description provided for @loadingNoFile.
  ///
  /// In uk, this message translates to:
  /// **'Завантажте, будь ласка, документ для аналізу'**
  String get loadingNoFile;

  /// No description provided for @loadingAnalyzing.
  ///
  /// In uk, this message translates to:
  /// **'Аналізую документ...'**
  String get loadingAnalyzing;

  /// No description provided for @navResults.
  ///
  /// In uk, this message translates to:
  /// **'Результати'**
  String get navResults;

  /// No description provided for @retryAnalysis.
  ///
  /// In uk, this message translates to:
  /// **'Заново'**
  String get retryAnalysis;

  /// No description provided for @resultCategories.
  ///
  /// In uk, this message translates to:
  /// **'Категорії'**
  String get resultCategories;

  /// No description provided for @resultDetails.
  ///
  /// In uk, this message translates to:
  /// **'Деталі помилок'**
  String get resultDetails;

  /// No description provided for @resultNoSelectedCategories.
  ///
  /// In uk, this message translates to:
  /// **'Немає обраних категорій для відображення'**
  String get resultNoSelectedCategories;

  /// Повідомлення, коли для обраної категорії не знайдено помилок
  ///
  /// In uk, this message translates to:
  /// **'Для категорії \"{category}\" помилки відсутні.'**
  String resultNoErrorsForCategory(String category);

  /// No description provided for @errorFragmentMissing.
  ///
  /// In uk, this message translates to:
  /// **'Фрагмент тексту відсутній.'**
  String get errorFragmentMissing;

  /// No description provided for @errorArrowShouldBe.
  ///
  /// In uk, this message translates to:
  /// **'→ має бути'**
  String get errorArrowShouldBe;

  /// No description provided for @dialogSettingsTitle.
  ///
  /// In uk, this message translates to:
  /// **'Налаштування перевірки'**
  String get dialogSettingsTitle;

  /// No description provided for @dialogChooseChecks.
  ///
  /// In uk, this message translates to:
  /// **'Оберіть що перевіряти'**
  String get dialogChooseChecks;

  /// No description provided for @dialogChooseChecksHint.
  ///
  /// In uk, this message translates to:
  /// **'Можна обрати декілька перевірок одночасно'**
  String get dialogChooseChecksHint;

  /// No description provided for @dialogChooseOneError.
  ///
  /// In uk, this message translates to:
  /// **'Оберіть хоча б одну перевірку'**
  String get dialogChooseOneError;

  /// No description provided for @dialogCancel.
  ///
  /// In uk, this message translates to:
  /// **'Скасувати'**
  String get dialogCancel;

  /// No description provided for @dialogStartAnalysis.
  ///
  /// In uk, this message translates to:
  /// **'▶ Почати аналіз'**
  String get dialogStartAnalysis;

  /// No description provided for @themeLight.
  ///
  /// In uk, this message translates to:
  /// **'Світла'**
  String get themeLight;

  /// No description provided for @themeDark.
  ///
  /// In uk, this message translates to:
  /// **'Темна'**
  String get themeDark;

  /// No description provided for @themeSystem.
  ///
  /// In uk, this message translates to:
  /// **'Системна'**
  String get themeSystem;

  /// Повідомлення про помилку у snackbar
  ///
  /// In uk, this message translates to:
  /// **'Виникла помилка: {error}'**
  String snackbarError(String error);

  /// No description provided for @errAlignment.
  ///
  /// In uk, this message translates to:
  /// **'Невірне вирівнювання тексту'**
  String get errAlignment;

  /// No description provided for @errFont.
  ///
  /// In uk, this message translates to:
  /// **'Невірні шрифти у параграфі'**
  String get errFont;

  /// No description provided for @errFontHeader.
  ///
  /// In uk, this message translates to:
  /// **'Неправильні шрифти в верхньому колонтитулі'**
  String get errFontHeader;

  /// No description provided for @errFontFooter.
  ///
  /// In uk, this message translates to:
  /// **'Неправильні шрифти в нижньому колонтитулі'**
  String get errFontFooter;

  /// No description provided for @errFontTable.
  ///
  /// In uk, this message translates to:
  /// **'Невірні шрифти у таблиці'**
  String get errFontTable;

  /// No description provided for @errFontSize.
  ///
  /// In uk, this message translates to:
  /// **'Неправильний розмір шрифта у параграфі'**
  String get errFontSize;

  /// No description provided for @errFontSizeHeader.
  ///
  /// In uk, this message translates to:
  /// **'Неправильний розмір шрифта в верхньому колонтитулі'**
  String get errFontSizeHeader;

  /// No description provided for @errFontSizeFooter.
  ///
  /// In uk, this message translates to:
  /// **'Неправильний розмір шрифта в нижньому колонтитулі'**
  String get errFontSizeFooter;

  /// No description provided for @errFontSizeTable.
  ///
  /// In uk, this message translates to:
  /// **'Неправильний розмір шрифта у таблиці'**
  String get errFontSizeTable;

  /// No description provided for @errLinespace.
  ///
  /// In uk, this message translates to:
  /// **'Неправильні інтервали між рядками у параграфі'**
  String get errLinespace;

  /// No description provided for @errListLevelSkip.
  ///
  /// In uk, this message translates to:
  /// **'Пропущено рівень вкладеності списку'**
  String get errListLevelSkip;

  /// No description provided for @errListFormat.
  ///
  /// In uk, this message translates to:
  /// **'Невідповідний формат нумерації на одному рівні списку'**
  String get errListFormat;

  /// No description provided for @errListMarkerFormat.
  ///
  /// In uk, this message translates to:
  /// **'Недозволений формат позначення переліку'**
  String get errListMarkerFormat;

  /// No description provided for @errListMarkerSuffix.
  ///
  /// In uk, this message translates to:
  /// **'Пункт переліку має закінчуватися дужкою, а не крапкою'**
  String get errListMarkerSuffix;

  /// No description provided for @errListBulletChar.
  ///
  /// In uk, this message translates to:
  /// **'Недозволений символ маркера переліку — має бути тире'**
  String get errListBulletChar;

  /// No description provided for @warnListManual.
  ///
  /// In uk, this message translates to:
  /// **'Виявлено ручне форматування переліку'**
  String get warnListManual;

  /// No description provided for @errSpacingBefore.
  ///
  /// In uk, this message translates to:
  /// **'Невірний відступ перед абзацом'**
  String get errSpacingBefore;

  /// No description provided for @errSpacingAfter.
  ///
  /// In uk, this message translates to:
  /// **'Невірний відступ після абзацу'**
  String get errSpacingAfter;

  /// No description provided for @errIndentationLeft.
  ///
  /// In uk, this message translates to:
  /// **'Невірний відступ зліва'**
  String get errIndentationLeft;

  /// No description provided for @errIndentationRight.
  ///
  /// In uk, this message translates to:
  /// **'Невірний відступ справа'**
  String get errIndentationRight;

  /// No description provided for @errFormulaAlignment.
  ///
  /// In uk, this message translates to:
  /// **'Формула вирівняна по лівому краю'**
  String get errFormulaAlignment;

  /// No description provided for @errFormulaMultiplePerLine.
  ///
  /// In uk, this message translates to:
  /// **'Кілька формул в одному рядку'**
  String get errFormulaMultiplePerLine;

  /// No description provided for @errFormulaNotTool.
  ///
  /// In uk, this message translates to:
  /// **'Формула набрана вручну, а не через інструмент \"Формула\"'**
  String get errFormulaNotTool;

  /// No description provided for @errFormulaChapterMismatch.
  ///
  /// In uk, this message translates to:
  /// **'Формула з неправильним розділом'**
  String get errFormulaChapterMismatch;

  /// No description provided for @errFormulaSequence.
  ///
  /// In uk, this message translates to:
  /// **'Формула з порушеною послідовністю'**
  String get errFormulaSequence;

  /// No description provided for @errFormulaFormat.
  ///
  /// In uk, this message translates to:
  /// **'Формула з неправильним форматом номера'**
  String get errFormulaFormat;

  /// No description provided for @errFormulaFontSize.
  ///
  /// In uk, this message translates to:
  /// **'Неправильний розмір шрифту у формулі'**
  String get errFormulaFontSize;

  /// No description provided for @errFormulaSpacingBefore.
  ///
  /// In uk, this message translates to:
  /// **'Неправильний відступ перед формулою'**
  String get errFormulaSpacingBefore;

  /// No description provided for @errFormulaSpacingAfter.
  ///
  /// In uk, this message translates to:
  /// **'Неправильний відступ після формули'**
  String get errFormulaSpacingAfter;

  /// No description provided for @errFormulaSpacingBeforeNotation.
  ///
  /// In uk, this message translates to:
  /// **'Неправильний відступ перед позначенням формули'**
  String get errFormulaSpacingBeforeNotation;

  /// No description provided for @errFormulaNotationSpacing.
  ///
  /// In uk, this message translates to:
  /// **'Неправильний відступ у позначенні формули'**
  String get errFormulaNotationSpacing;

  /// No description provided for @errFigureAlignment.
  ///
  /// In uk, this message translates to:
  /// **'Невірне вирівнювання рисунка'**
  String get errFigureAlignment;

  /// No description provided for @errFigureBlankLine.
  ///
  /// In uk, this message translates to:
  /// **'Відсутній пустий рядок перед/після рисунка'**
  String get errFigureBlankLine;

  /// No description provided for @errFigureMissingCaption.
  ///
  /// In uk, this message translates to:
  /// **'Відсутній підпис рисунка'**
  String get errFigureMissingCaption;

  /// No description provided for @errFigureUnexpectedInCaption.
  ///
  /// In uk, this message translates to:
  /// **'Креслення у підписі'**
  String get errFigureUnexpectedInCaption;

  /// No description provided for @errFigureCaptionFormat.
  ///
  /// In uk, this message translates to:
  /// **'Неправильний формат підпису рисунка (очікується: «Рисунок <номер> - <назва>»)'**
  String get errFigureCaptionFormat;

  /// No description provided for @errStructuralElementMissing.
  ///
  /// In uk, this message translates to:
  /// **'Відсутній обов\'язковий структурний елемент'**
  String get errStructuralElementMissing;

  /// No description provided for @errStructuralElementOrder.
  ///
  /// In uk, this message translates to:
  /// **'Порушено порядок структурних елементів документа'**
  String get errStructuralElementOrder;

  /// No description provided for @warnStructuralElementNewPage.
  ///
  /// In uk, this message translates to:
  /// **'Структурний елемент не починається з нової сторінки'**
  String get warnStructuralElementNewPage;

  /// No description provided for @err000.
  ///
  /// In uk, this message translates to:
  /// **'Помилка відкриття файлу'**
  String get err000;
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
      <String>['uk'].contains(locale.languageCode);

  @override
  bool shouldReload(_AppLocalizationsDelegate old) => false;
}

AppLocalizations lookupAppLocalizations(Locale locale) {
  // Lookup logic when only language code is specified.
  switch (locale.languageCode) {
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
