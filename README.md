
![image](https://github.com/FlaschenbookProject/Flaschenbook/assets/39427152/b646501e-f98a-450c-b758-a9b10272eedb)

<div align=center>
  <h3> 📚 도서 추천 웹사이트 Flaschenbook </h3>
  <p> 사용자가 선택한 문장, 제목, 장르 등을 활용하여 사용자 기반의 책 추천을 해 주는 웹 페이지입니다. <br> 파도를 타고 밀려오는 유리병의 쪽지를 뜻하는 Flaschenpost라는 단어에서 모티브를 얻어 파도를 타고 오는 책이라는 의미로 Flaschenbook이 되었습니다. <br> 로그인을 하지 않은 사용자에 한해서는 그 주의 베스트 셀러, 그 달의 신간, 랜덤한 장르의 책과 리뷰 수와 리뷰 평점을 통한 책 추천이 이루어지며
  <br> 로그인을 한 사용자는 사용자가 선택했던 항목들을 기반으로 그와 유사한 책들을 추천받을 수 있습니다.
  </p>
</div>
<br>

> 프로젝트 관련 링크입니다. <br> <br>
> [Notion - 프로젝트에 대한 기록을 남겼습니다.](https://beaded-sink-0ff.notion.site/Flaschenbook-8b7db5e44c004f7b8d10069a0659db58) <br> <br>
> [Git Project - 프로젝트의 진행 사항을 관리했습니다.](https://github.com/orgs/FlaschenbookProject/projects/1/views/5)

 <br> <br>

## 목차
- [프로젝트 개요](#1.-프로젝트-개요)
- [Tech Stack](#2.-Tech-Stack)
- [Architecture](#3.-Architecture)
- [Data Flow](#4.-Data-Flow)
- [Data Pipeline](#5.-Data-Pipeline)
- [ERD](#6.-ERD)
- [결과](#7.-결과)
 <br> <br>
 

## 1. 프로젝트 개요
### 목적
- 사용자에게 도서 판매 사이트의 리뷰 수와 리뷰 평점, 그리고 장르 기반의 추천을 제공합니다. 또한 사용자들은 흥미로운 책을 눌러 상세 정보, 구입 정보, 리뷰에서 가장 많이 나온 단어들을 확인하며 책을 선별할 수 있습니다. 
- 사용자가 선택한 문장, 제목, 장르 등의 정보를 활용해 사용자의 취향에 맞는 책을 추천합니다. 이를 통해 사용자들은 새로운 책을 발견하고 다양한 독서 경험을 즐길 수 있습니다.

### 개발 기간
- 23.08.08 ~ 23.09.04 (4주)

### 팀원 소개 및 역할
|  이름  | 역할 | GitHub | 
| :---: | :---: | :--- |
| 송지혜 |  데이터 수집, ETL DataPipeline 생성, Airflow, AWS 인프라 구축, 웹 프론트엔드/백엔드 | [@ssongjj](https://github.com/ssongjj) |
| 오유정 |  데이터 수집, ETL DataPipeline 생성, Airflow, AWS 인프라 구축, 웹 프론트엔드/백엔드 | [@ujeongoh](https://github.com/ujeongoh) |
<br>
<br>

## 2. Tech Stack
| Field | Stack |
|:---:|:---:|
| Data Storage | <img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"> <img src="https://img.shields.io/badge/amazonrds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">  <img src="https://img.shields.io/badge/amazonredshift-8C4FFF?style=for-the-badge&logo=amazonredshift&logoColor=white">||
| Web Front-end | <img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=white"> ||
| Web Back-end | <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">  <img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white"> ||
| Data Scheduler |<img src="https://img.shields.io/badge/Airflow-017CEE?style=for-the-badge&logo=Apache%20Airflow&logoColor=white"/>||
| Data Processing | <img src="https://img.shields.io/badge/python-3776AB?style=for-the-badge&logo=python&logoColor=white"> <img src="https://img.shields.io/badge/playwright-2EAD33?style=for-the-badge&logo=playwright&logoColor=white"> <img src="https://img.shields.io/badge/awslambda-FF9900?style=for-the-badge&logo=awslambda&logoColor=white"> <img src="https://img.shields.io/badge/awsglue-BC52EE?style=for-the-badge&logo=awsglue&logoColor=white"> ||
| Container Orchestration and Deployment |  <img src="https://img.shields.io/badge/amazonecs-FF9900?style=for-the-badge&logo=amazonecs&logoColor=white"/> <img src="https://img.shields.io/badge/amazonecr-FF9900?style=for-the-badge&logo=amazonecr&logoColor=white"/> <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>  ||
| CI/CD |<img src="https://img.shields.io/badge/githubactions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"/> ||
| Infrastructure | <img src="https://img.shields.io/badge/terraform-844FBA?style=for-the-badge&logo=terraform&logoColor=white"> ||
| Monitoring | <img src="https://img.shields.io/badge/amazoncloudwatch-FF4F8B?style=for-the-badge&logo=amazoncloudwatch&logoColor=white"> ||
| Project Management Tool | <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"> <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">||
<br>
<br>


## 3. Architecture
<img width="825" alt="image" src="https://github.com/FlaschenbookProject/Flaschenbook/assets/39427152/f4886459-b301-46f5-92d2-ab9b6b4358e0">

<br>
<br>

## 4. Data Flow
![image](https://github.com/FlaschenbookProject/Flaschenbook/assets/39427152/c7d09f0f-7a53-4b42-ba98-40df5f1b703a)

<br>
<br>

## 5. Data Pipeline
### 외부 API 및 웹사이트 데이터 수집
- Python 스크립트가 도서 판매 사이트 API와 스크래핑을 통해 데이터를 수집한다.
- 수집한 데이터를 처리하고 가공한 뒤, 도커 이미지로 패키징한다.
- Python 스크립트를 도커 이미지로 실행하며, 이를 Airflow DAG로 관리한다.
### Raw 데이터 저장
- 수집한 데이터는 S3의 raw 데이터 버킷에 저장된다.
### Lambda 트리거로 Cleaning 수행
- S3에 저장된 raw 데이터를 Lambda 함수를 사용하여 트리거한다.
- Lambda 함수는 raw 데이터에서 필요한 요소들만 남기고 json 파일을 parquet 파일로 변환하여 cleaned 데이터로 저장한다.
### Lambda 트리거로 Curated 수행
- cleaned 데이터가 Lambda 함수에 의해 트리거되어 curated 데이터로 최종 가공된다.
- 이때 각 사이트별로 저장되어 있는 데이터를 하나로 통합하고, 데이터가 필요한 테이블의 이름으로 적재한다.
### Glue Crawler 실행
- Glue Crawler를 사용하여 curated 데이터를 크롤링한다.
- 크롤된 데이터 스키마를 추출하고 메타데이터를 생성한다.
### ProductionDB, DataWarehouse에 데이터 저장
- Glue를 사용하여 parquet 파일 형식으로 curated 데이터를 Redshift(Data Warehouse) 및 RDS(ProductionDB)에 복사한다.
<br>
<br>


## 5. ERD
![image](https://github.com/FlaschenbookProject/Flaschenbook/assets/39427152/5be47a4c-bbae-497f-9af0-399a92f5c69a)

<br>
<br>

## 6. 결과 
- 현재 프로젝트 당시 제공되었던 AWS 서비스가 종료되어 리팩토링 단계에 있습니다. 
<br>

### 메인 화면
 ![image](https://github.com/FlaschenbookProject/Flaschenbook/assets/39427152/1e8c36d6-0ef3-465a-b12c-22df379e0aa5)
 
### 날 위한 서재
![image](https://github.com/FlaschenbookProject/Flaschenbook/assets/39427152/74a88fd7-91da-4049-afe7-22880909e963)


<br>
