import json
from key_mapping import key_mapping
from datetime import datetime


def format_date(date_str, input_format, output_format):
    date_obj = datetime.strptime(date_str, input_format)
    return date_obj.strftime(output_format)


def transform_data_naver(content):
    dict_content = json.loads(content)
    items = [entry['items'][0] for entry in dict_content['items']]
    transformed_data = []

    for item in items:
        transformed_item = {}

        for raw_key, cleaned_key in key_mapping.items():
            if raw_key in item:
                if raw_key == 'author':
                    item[raw_key] = item[raw_key].replace("^", ", ")
                elif raw_key == 'pubdate':
                    item[raw_key] = format_date(
                        item[raw_key], "%Y%m%d", "%Y-%m-%d")
                transformed_item[cleaned_key] = item[raw_key]

        transformed_data.append(transformed_item)

    return transformed_data


def transform_data_kakao(content):
    dict_content = json.loads(content)
    items = [entry['documents'][0] for entry in dict_content['items']]
    transformed_data = []

    for item in items:
        transformed_item = {}

        for raw_key, cleaned_key in key_mapping.items():
            if raw_key in item:
                if raw_key == 'authors':
                    item[raw_key] = ", ".join(item[raw_key])
                elif raw_key == 'translators':
                    item[raw_key] = ", ".join(item[raw_key])
                elif raw_key == 'datetime':
                    item[raw_key] = item[raw_key][:10]
                elif raw_key == 'isbn':
                    item[raw_key] = item[raw_key].split()[1]
                transformed_item[cleaned_key] = item[raw_key]

        transformed_data.append(transformed_item)

    return transformed_data


def transform_data_aladin(content):
    dict_content = json.loads(content)
    items = [entry['item'][0] for entry in dict_content['items']]
    transformed_data = []

    for item in items:
        transformed_item = {}
        if item.get("subInfo"):
            subinfo = item.get("subInfo")
            transformed_item['PAGE_CNT'] = subinfo.get("itemPage", 0)
            transformed_item['RANK'] = subinfo.get("bestSellerRank", "")
        for raw_key, cleaned_key in key_mapping.items():
            if raw_key in item:
                if raw_key == 'isbn':
                    continue
                transformed_item[cleaned_key] = item[raw_key]

        transformed_data.append(transformed_item)

    return transformed_data
