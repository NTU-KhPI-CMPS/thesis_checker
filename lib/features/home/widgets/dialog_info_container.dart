import 'package:flutter/widgets.dart';

class DialogInfoContainer extends StatelessWidget {
  final Color borderColor;
  final Color textColor;

  final String imageAsset;
  final String infoText;

  const DialogInfoContainer({
    super.key, 
    required this.borderColor, 
    required this.textColor,
    required this.imageAsset, 
    required this.infoText
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      alignment: Alignment.centerLeft,
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 12.0, vertical: 8.0),
      decoration: BoxDecoration(
        color: borderColor,
        borderRadius: BorderRadius.all(
          Radius.circular(8.0),
        ),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Image.asset(
            imageAsset,
            width: 20.0,
            height: 20.0,
          ),
          SizedBox(width: 10.0),
          Text(
            infoText,
            style: TextStyle(
              fontSize: 14.0,
              color: textColor,
              overflow: TextOverflow.ellipsis,
              fontFamily: 'FunnelSans',
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }
}
