# 📄 Scripts

웹 스크래핑과 API를 통해 데이터를 수집하는 여러 스크립트를 포함하고 있습니다.

## 🗂️ 폴더 구조

- data
- init
- utils

### data

스크립트를 실행하여 생성되는 웹 스크래핑 또는 API로부터 수집된 데이터 파일을 저장힙니다.

### init

초기 seed 데이터를 수집하고 처리하는 스크립트들이 담겨 있습니다. 프로젝트의 시작 시점에서 필요한 기본 데이터를 수집하거나 기초 설정을 위한 스크립트를 포함하고 있습니다.

### utils

스크립트에서 공통으로 사용되는 여러 유틸리티 함수들이 있습니다. 이 함수들은 다양한 스크립트에서 중복 없이 사용할 수 있도록 공통 로직을 분리하여 담았습니다.

---

## 🛠️ 웹 스크래핑 툴 - Playwright

웹 스크래핑 작업에서는 Playwright라는 툴을 사용합니다. Playwright는 최신 웹 브라우저 환경에서 빠르고 신뢰성 있는 웹 스크래핑 및 브라우저 자동화 작업을 위한 Node.js 오픈소스 라이브러리입니다. </br>
</br>
**Github** : https://github.com/oxylabs/playwright-web-scraping </br>
**참고문서** : https://scrapfly.io/blog/web-scraping-with-playwright-and-python/

### Playwright 설치

```
pip install playwright
playwright install
```

### 스크립트 실행을 위해 필요한 기타 라이브러리 설치

```
pip install nest_asyncio s3fs
```
