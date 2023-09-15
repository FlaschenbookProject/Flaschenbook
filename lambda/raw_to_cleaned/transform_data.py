import json
from key_mapping import key_mapping
from datetime import datetime


def format_date(date_str, input_format, output_format):
    if date_str:
        date_obj = datetime.strptime(date_str, input_format)
        return date_obj.strftime(output_format)
    else:
        return ""


def transform_data(content, source):
    dict_content = json.loads(content)

    try:
        if source == 'naver':
            print("source naver start")
            items = [entry['items'][0] for entry in dict_content['items']
                     if entry.get("items") and len(entry['items']) > 0]
        elif source == 'kakao':
            items = [entry['documents'][0] for entry in dict_content['items'] if entry.get(
                "documents") and len(entry['documents']) > 0]
        else:  # source == 'aladin'
            items = [entry['item'][0] for entry in dict_content['items']
                     if entry.get("item") and len(entry['item']) > 0]
    except Exception as e:
        raise e

    transformed_data = []

    for item in items:
        transformed_item = {}
        if source == 'aladin' and item.get("subInfo"):
            subinfo = item.get("subInfo")
            transformed_item['PAGE_CNT'] = subinfo.get("itemPage", 0)
            transformed_item['RANK'] = subinfo.get("bestSellerRank", "")

        for raw_key, cleaned_key in key_mapping.items():
            if raw_key in item:
                if source == 'naver' and raw_key == 'author':
                    item[raw_key] = item[raw_key].replace("^", ", ")
                    print(item[raw_key])
                elif source == 'naver' and raw_key == 'pubdate':
                    item[raw_key] = format_date(
                        item[raw_key], "%Y%m%d", "%Y-%m-%d")
                    print(item[raw_key])
                elif source == 'kakao' and (raw_key == 'authors' or raw_key == 'translators'):
                    if not item.get(raw_key):
                        item[raw_key] = ""
                        continue
                    item[raw_key] = ", ".join(item[raw_key])
                elif source == 'kakao' and raw_key == 'datetime':
                    try:
                        item[raw_key] = item[raw_key][:10]
                    except:
                        print(f"인덱스 에러{item[raw_key]}")
                if source == 'kakao' and raw_key == 'isbn':
                    item[raw_key] = item[raw_key].strip() if item[raw_key].startswith(
                        " ") else item[raw_key].split(" ")[1]
                elif source == 'aladin' and raw_key == 'isbn':
                    continue
                transformed_item[cleaned_key] = item[raw_key]

        transformed_data.append(transformed_item)

    return transformed_data
