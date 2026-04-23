import 'package:thesis_checker/data/models/format_error_api.dart';

class ReportApi {
  const ReportApi({
    required this.errors,
  });

  final List<FormatErrorApi> errors;

  factory ReportApi.fromJson(Map<String, dynamic> json) {
    return ReportApi(
      errors: (json['errors'] as List<dynamic>? ?? const [])
          .map(
            (item) => FormatErrorApi.fromJson(
              Map<String, dynamic>.from(item as Map),
            ),
          ).toList(),
    );
  }
}
