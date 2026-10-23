# How to Add a New Error

Java returns errors in the `FormatErrorApi` format as-is: do not add, rename, or remove fields.

## Java

### 1. Create the error

In the appropriate checker (`checker/*.java`), create a `FormatError` and populate all fields:

```java
FormatError err = new FormatError();
err.setId("err_my_check");               // unique ID: snake_case, prefix err_ or warn_
err.setCategory(ErrorCategory.MY_CHECK); // checker category
err.setSeverity("error");                // "error" or "warning"
err.setTitle("Description in Ukrainian"); // for the console; not displayed in the app
err.setParagraphText(paragraphText);     // paragraph text, "" if unavailable
err.setFound(Set.of("12.0pt"));          // what was found
err.setExpected("14.0pt");               // what was expected
allErrors.add(err);
```

### 2. If a new checker is needed

This is only necessary if the error does not fit any existing checker.

1. Add a new category to `model/ErrorCategory.java`:

```java
public enum ErrorCategory {
    // ...
    MY_CHECK,
    FILE
}
```

2. Create `checker/MyChecker.java`:

```java
public class MyChecker implements Checker {

    @Override
    public ErrorCategory getErrorCategory() {
        return ErrorCategory.MY_CHECK;
    }

    @Override
    public List<FormatError> check(String filePath) {
        List<FormatError> allErrors = new ArrayList<>();
        // Open the document, perform the check, and add FormatError to allErrors (step 1).
        return allErrors;
    }
}
```

3. Add the checker to the `CHECKERS` list in `Main.java`:

```java
private static final List<Checker> CHECKERS = List.of(
        // ...
        new MyChecker()
);
```

4. In Flutter, add the check to `core/enums/check.dart`. The name in quotes must **exactly match** the name in `ErrorCategory`; Java uses it to identify the checker:

```dart
enum Check {
  // ...
  myCheck('MY_CHECK'),
}
```

5. In `core/constants/available_check_types.dart`, add `Check.myCheck` to the `checks` list for the category tab where the errors should appear:

```dart
CheckTypeInfo(
  category: CheckCategory.formula,
  checks: [Check.formula, Check.myCheck],
  iconPath: 'assets/images/formula.png',
),
```

### 3. Rebuild Java

After any Java changes, rebuild the native library and replace the files in `assets/checker/` for each platform. Otherwise, the app will continue using the old version.

## Flutter

1. Add the message translations to `l10n/app_en.arb` and `l10n/app_uk.arb`. The key is the `id` in `camelCase`: `err_my_check` → `errMyCheck`:

```json
"errMyCheck": "Incorrect value in paragraph",
"@errMyCheck": { "description": "Error shown when ..." }
```

2. Map the `id` to its translation in `l10n/error_message_resolvers.dart`:

```dart
'err_my_check': (l) => l.errMyCheck,
```

3. If Java returns a phrase or code in `found` or `expected` that needs translation (for example, `LEFT` or a Ukrainian phrase), add the `valueMyPhrase` key to both `.arb` files and map it in `l10n/error_value_resolvers.dart`. The map key must be the exact string returned by Java:

```dart
'my phrase': (l) => l.valueMyPhrase,
```

Numbers, `14.0pt`, and font names do not need to be translated.

4. Generate the localization code and run the tests:

```bash
flutter gen-l10n
flutter test
```

5. Add the new `id` to `test/features/l10n/error_message_resolvers_test.dart`. If you completed step 3, also add the value to `test/features/l10n/error_value_resolvers_test.dart`.
