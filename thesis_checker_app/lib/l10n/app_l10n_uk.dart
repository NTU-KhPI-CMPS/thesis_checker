// ignore: unused_import
import 'package:intl/intl.dart' as intl;

import 'app_l10n.dart';

// ignore_for_file: type=lint

/// The translations for Ukrainian (`uk`).
class AppLocalizationsUk extends AppLocalizations {
  AppLocalizationsUk([String locale = 'uk']) : super(locale);

  @override
  String get appTitle => 'Thesis checker';

  @override
  String get navHome => 'Головна';

  @override
  String get homeSectionTitle => 'Що перевіряємо';

  @override
  String get uploadTitle => 'Завантажте документ';

  @override
  String get uploadSubtitle =>
      'Перетягніть .docx файл або натисніть щоб обрати';

  @override
  String get uploadSelectFile => '+ Обрати файл';

  @override
  String get navAnalysis => 'Аналіз';

  @override
  String get loadingNoFile => 'Завантажте, будь ласка, документ для аналізу';

  @override
  String get loadingAnalyzing => 'Аналізую документ...';

  @override
  String get navResults => 'Результати';

  @override
  String get retryAnalysis => 'Заново';

  @override
  String get resultCategories => 'Категорії';

  @override
  String get resultDetails => 'Деталі помилок';

  @override
  String get resultNoSelectedCategories =>
      'Немає обраних категорій для відображення';

  @override
  String resultNoErrorsForCategory(String category) {
    return 'Для категорії \"$category\" помилки відсутні.';
  }

  @override
  String get errorFragmentMissing => 'Фрагмент тексту відсутній.';

  @override
  String get errorArrowShouldBe => '→ має бути';

  @override
  String get dialogSettingsTitle => 'Налаштування перевірки';

  @override
  String get dialogClose => '✕';

  @override
  String get dialogChooseChecks => 'Оберіть що перевіряти';

  @override
  String get dialogChooseChecksHint =>
      'Можна обрати декілька перевірок одночасно';

  @override
  String get dialogChooseOneError => 'Оберіть хоча б одну перевірку';

  @override
  String get dialogCancel => 'Скасувати';

  @override
  String get dialogStartAnalysis => '▶ Почати аналіз';

  @override
  String get themeLight => 'Світла';

  @override
  String get themeDark => 'Темна';

  @override
  String get themeSystem => 'Системна';

  @override
  String snackbarError(String error) {
    return 'Виникла помилка: $error';
  }

  @override
  String get errAlignment => 'Невірне вирівнювання тексту';

  @override
  String get errFont => 'Невірні шрифти у параграфі';

  @override
  String get errFontHeader => 'Неправильні шрифти в верхньому колонтитулі';

  @override
  String get errFontFooter => 'Неправильні шрифти в нижньому колонтитулі';

  @override
  String get errFontTable => 'Невірні шрифти у таблиці';

  @override
  String get errFontSize => 'Неправильний розмір шрифта у параграфі';

  @override
  String get errFontSizeHeader =>
      'Неправильний розмір шрифта в верхньому колонтитулі';

  @override
  String get errFontSizeFooter =>
      'Неправильний розмір шрифта в нижньому колонтитулі';

  @override
  String get errFontSizeTable => 'Неправильний розмір шрифта у таблиці';

  @override
  String get errLinespace => 'Неправильні інтервали між рядками у параграфі';

  @override
  String get errListLevelSkip => 'Пропущено рівень вкладеності списку';

  @override
  String get errListFormat =>
      'Невідповідний формат нумерації на одному рівні списку';

  @override
  String get errListMarkerFormat => 'Недозволений формат позначення переліку';

  @override
  String get errListMarkerSuffix =>
      'Пункт переліку має закінчуватися дужкою, а не крапкою';

  @override
  String get errListBulletChar =>
      'Недозволений символ маркера переліку — має бути тире';

  @override
  String get warnListManual => 'Виявлено ручне форматування переліку';

  @override
  String get errSpacingBefore => 'Невірний відступ перед абзацом';

  @override
  String get errSpacingAfter => 'Невірний відступ після абзацу';

  @override
  String get errIndentationLeft => 'Невірний відступ зліва';

  @override
  String get errIndentationRight => 'Невірний відступ справа';

  @override
  String get errFormulaAlignment => 'Формула вирівняна по лівому краю';

  @override
  String get errFormulaMultiplePerLine => 'Кілька формул в одному рядку';

  @override
  String get errFormulaNotTool =>
      'Формула набрана вручну, а не через інструмент \"Формула\"';

  @override
  String get errFormulaChapterMismatch => 'Формула з неправильним розділом';

  @override
  String get errFormulaSequence => 'Формула з порушеною послідовністю';

  @override
  String get errFormulaFormat => 'Формула з неправильним форматом номера';

  @override
  String get errFormulaFontSize => 'Неправильний розмір шрифту у формулі';

  @override
  String get errFormulaSpacingBefore => 'Неправильний відступ перед формулою';

  @override
  String get errFormulaSpacingAfter => 'Неправильний відступ після формули';

  @override
  String get errFormulaSpacingBeforeNotation =>
      'Неправильний відступ перед позначенням формули';

  @override
  String get errFormulaNotationSpacing =>
      'Неправильний відступ у позначенні формули';

  @override
  String get errFigureAlignment => 'Невірне вирівнювання рисунка';

  @override
  String get errFigureBlankLine => 'Відсутній пустий рядок перед/після рисунка';

  @override
  String get errFigureMissingCaption => 'Відсутній підпис рисунка';

  @override
  String get errFigureUnexpectedInCaption => 'Креслення у підписі';

  @override
  String get errFigureCaptionFormat =>
      'Неправильний формат підпису рисунка (очікується: «Рисунок <номер> - <назва>»)';

  @override
  String get errStructuralElementMissing =>
      'Відсутній обов\'язковий структурний елемент';

  @override
  String get errStructuralElementOrder =>
      'Порушено порядок структурних елементів документа';

  @override
  String get warnStructuralElementNewPage =>
      'Структурний елемент не починається з нової сторінки';

  @override
  String get err000 => 'Помилка відкриття файлу';
}
