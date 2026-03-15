import sys
import argparse
import json
import os
from datetime import datetime
from docx import Document
from docx.shared import Pt
from collections import defaultdict


def check_essay(file_path, expected_font='Times New Roman', expected_size=14):
    errors_by_paragraph = defaultdict(lambda: {'font_issues': [], 'size_issues': [], 'text': ''})

    try:
        doc = Document(file_path)
    except FileNotFoundError:
        return [{"error": f"Файл не знайдено: {file_path}"}]
    except Exception as e:
        return [{"error": f"Помилка відкриття файлу: {e}"}]

    for para_index, paragraph in enumerate(doc.paragraphs):
        para_text = paragraph.text
        if not para_text.strip():
            continue

        errors_by_paragraph[para_index + 1]['text'] = para_text[:200] + ('...' if len(para_text) > 200 else '')

        runs = list(paragraph.runs)
        for i, run in enumerate(runs):
            font_name = run.font.name
            font_size = run.font.size
            fragment_text = run.text

            start_pos = 0
            for j in range(i):
                start_pos += len(runs[j].text)
            end_pos = start_pos + len(fragment_text)

            is_space = fragment_text and not fragment_text.strip()
            if is_space:
                prev_text = ''
                next_text = ''
                for j in range(i - 1, -1, -1):
                    if runs[j].text.strip():
                        prev_text = runs[j].text.strip()
                        break
                for j in range(i + 1, len(runs)):
                    if runs[j].text.strip():
                        next_text = runs[j].text.strip()
                        break
                if prev_text or next_text:
                    display_fragment = f"пробіл між \"{prev_text}\" та \"{next_text}\""
                else:
                    display_fragment = "<пробіл без контексту>"
            else:
                display_fragment = fragment_text[:70] + ('...' if len(fragment_text) > 70 else '')

            if font_name and font_name.lower() != expected_font.lower():
                errors_by_paragraph[para_index + 1]['font_issues'].append({
                    'actual': font_name,
                    'fragment': display_fragment,
                    'text': fragment_text,
                    'start': start_pos,
                    'end': end_pos
                })

            if font_size and font_size != Pt(expected_size):
                errors_by_paragraph[para_index + 1]['size_issues'].append({
                    'actual': font_size.pt,
                    'fragment': display_fragment,
                    'text': fragment_text,
                    'start': start_pos,
                    'end': end_pos
                })

    json_errors = []
    error_id = 1
    for para_num, issues in errors_by_paragraph.items():
        if issues['font_issues'] or issues['size_issues']:
            for issue in issues['font_issues']:
                json_errors.append({
                    'id': f"err_{error_id:03d}",
                    'category': 'font',
                    'severity': 'error',
                    'title': f"Невірний шрифт: {issue['actual']}",
                    'paragraph_index': para_num,
                    'paragraph_text': issues['text'],
                    'highlight': {
                        'start': issue['start'],
                        'end': issue['end']
                    },
                    'found': issue['actual'],
                    'expected': expected_font,
                    'suggestions': [expected_font]
                })
                error_id += 1

            for issue in issues['size_issues']:
                json_errors.append({
                    'id': f"err_{error_id:03d}",
                    'category': 'font_size',
                    'severity': 'error',
                    'title': f"Невірний розмір шрифту: {issue['actual']}pt",
                    'paragraph_index': para_num,
                    'paragraph_text': issues['text'],
                    'highlight': {
                        'start': issue['start'],
                        'end': issue['end']
                    },
                    'found': f"{issue['actual']}pt",
                    'expected': f"{expected_size}pt",
                    'suggestions': [f"{expected_size}pt"]
                })
                error_id += 1

    return json_errors


def generate_json_report(file_path, errors, expected_font, expected_size):
    filename = os.path.basename(file_path)

    by_severity = {'error': 0, 'warning': 0}
    by_category = {}

    for err in errors:
        if 'error' in err:
            continue
        by_severity[err['severity']] = by_severity.get(err['severity'], 0) + 1
        category = err['category']
        by_category[category] = by_category.get(category, 0) + 1

    report = {
        "file_name": filename,
        "analyzed_at": datetime.now().isoformat(),
        "summary": {
            "total_errors": len([e for e in errors if 'error' not in e]),
            "by_severity": by_severity,
            "by_category": by_category
        },
        "errors": [e for e in errors if 'error' not in e]
    }
    return report


def save_json_report(report, base_filename, output_dir="reports"):
    os.makedirs(output_dir, exist_ok=True)

    safe_name = os.path.splitext(os.path.basename(base_filename))[0]
    safe_name = "".join(c if c.isalnum() or c in "._-" else "_" for c in safe_name)
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    output_filename = f"{safe_name}_{timestamp}.json"
    output_path = os.path.join(output_dir, output_filename)

    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(report, f, ensure_ascii=False, indent=2)

    return output_path


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description='Перевірка оформлення .docx файлів (шрифт Times New Roman 14)'
    )
    parser.add_argument(
        'files',
        nargs='+',
        help="Шляхи до файлів .docx (обов'язково вказати хоча б один файл)"
    )
    parser.add_argument(
        '--font',
        default='Times New Roman',
        help='Очікуваний шрифт (за замовчуванням Times New Roman)'
    )
    parser.add_argument(
        '--size',
        type=int,
        default=14,
        help='Очікуваний розмір шрифту (за замовчуванням 14)'
    )
    parser.add_argument(
        '--output-dir',
        default='reports',
        help='Папка для збереження JSON-звітів (за замовчуванням reports)'
    )

    args = parser.parse_args()

    for file_path in args.files:
        errors = check_essay(file_path, args.font, args.size)
        report = generate_json_report(file_path, errors, args.font, args.size)
        json_path = save_json_report(report, file_path, args.output_dir)
