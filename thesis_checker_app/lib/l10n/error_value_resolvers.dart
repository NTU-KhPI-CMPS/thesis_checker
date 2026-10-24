import 'package:thesis_checker/l10n/error_message_resolvers.dart';
import 'package:thesis_checker/l10n/l10n.dart';

/// Maps raw `found` / `expected` values from the Java checker to localized resolvers.
final Map<String, MessageResolver> errorValueResolvers = {
  // Font
  'Шрифт за замовчуванням': (l) => l.valueDefaultFont,

  // Alignment
  'LEFT': (l) => l.valueAlignmentLeft,
  'По лівому краю': (l) => l.valueAlignmentLeft,
  'RIGHT': (l) => l.valueAlignmentRight,
  'По правому краю': (l) => l.valueAlignmentRight,
  'CENTER': (l) => l.valueAlignmentCenter,
  'По центру': (l) => l.valueAlignmentCenter,
  'BOTH': (l) => l.valueAlignmentBoth,
  'По ширині': (l) => l.valueAlignmentBoth,
  'DISTRIBUTE': (l) => l.valueAlignmentDistribute,
  'По ширині або правому краю': (l) => l.valueAlignmentBothOrRight,

  // List numbering formats
  'арабські цифри': (l) => l.valueListFormatDecimal,
  'літери (нижній регістр)': (l) => l.valueListFormatLowerLetter,
  'літери (верхній регістр)': (l) => l.valueListFormatUpperLetter,
  'римські числа (нижній регістр)': (l) => l.valueListFormatLowerRoman,
  'римські числа (верхній регістр)': (l) => l.valueListFormatUpperRoman,
  'позначка (маркер)': (l) => l.valueListFormatBullet,
  'інший формат': (l) => l.valueListFormatOther,
  'consistent': (l) => l.valueListConsistent,

  // Figures
  'немає пустого рядка перед': (l) => l.valueFigureNoBlankBefore,
  'немає пустого рядка після': (l) => l.valueFigureNoBlankAfter,
  'один пустий рядок': (l) => l.valueFigureOneBlankLine,
  'Параграф з підписом «Рисунок»': (l) => l.valueFigureCaptionParagraph,
  'відсутній': (l) => l.valueFigureCaptionMissing,
  'Відсутність креслення': (l) => l.valueFigureNoDrawing,
  'Креслення присутнє': (l) => l.valueFigureDrawingPresent,
  '«Рисунок <номер> - <назва>»': (l) => l.valueFigureCaptionFormat,

  // Formulas
  'Формула має бути вставлена через Вставлення → Формула': (l) => l.valueFormulaInsertViaTool,
  '1 формула в рядку': (l) => l.valueFormulaOnePerLine,
  'Порожній рядок перед формулою': (l) => l.valueFormulaBlankLineBefore,
  'Порожній рядок після формули': (l) => l.valueFormulaBlankLineAfter,
  'Пояснення «де» безпосередньо під формулою (без відступу)': (l) => l.valueFormulaNotationDirectlyBelow,
  'Порожній рядок після блоку пояснень': (l) => l.valueFormulaBlankLineAfterNotation,
  'Початок документу': (l) => l.valueDocumentStart,
  '[Формула]': (l) => l.valuePlaceholderFormula,
  '[Порожній рядок]': (l) => l.valuePlaceholderBlankLine,

  // Structural elements
  'Розрив сторінки перед елементом': (l) => l.valueStructuralPageBreakBefore,
  'РЕФЕРАТ': (l) => l.valueStructuralAbstract,
  'ЗМІСТ': (l) => l.valueStructuralContents,
  'ПЕРЕЛІК ПОЗНАК ТА СКОРОЧЕНЬ': (l) => l.valueStructuralAbbreviations,
  'ВСТУП': (l) => l.valueStructuralIntroduction,
  'ВИСНОВКИ': (l) => l.valueStructuralConclusions,
  'СПИСОК ДЖЕРЕЛ ІНФОРМАЦІЇ': (l) => l.valueStructuralReferences,
  'ДОДАТОК': (l) => l.valueStructuralAppendix,
};

/// Matches a length in centimeters, e.g. "1.3 см".
final RegExp _centimetersPattern = RegExp(r'^(-?\d+(?:[.,]\d+)?)\s*см$');

/// Returns the localized value for a single raw value, or null if it is not recognized.
String? _resolveValue(AppLocalizations l10n, String value) {
  final resolver = errorValueResolvers[value];
  if (resolver != null) {
    return resolver(l10n);
  }

  final centimeters = _centimetersPattern.firstMatch(value);
  if (centimeters != null) {
    return l10n.valueCentimeters(centimeters.group(1)!);
  }

  return null;
}

/// Returns the localized error value, leaving unrecognized parts unchanged.
String localizeErrorValue(AppLocalizations l10n, String value) {
  final trimmed = value.trim();
  if (trimmed.isEmpty) {
    return value;
  }

  final whole = _resolveValue(l10n, trimmed);
  if (whole != null) {
    return whole;
  }

  return trimmed
      .split(' -> ')
      .map(
        (chain) => chain
        .split(', ')
        .map((part) => _resolveValue(l10n, part.trim()) ?? part)
        .join(', ')).join(' -> ');
}
