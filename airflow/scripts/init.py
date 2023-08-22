import sys
import os
from init.init_get_api_aladin import main

if __name__ == "__main__":
    # 현재 스크립트의 디렉토리 조회
    package_directory = os.path.dirname(os.path.abspath(__file__))

    # utils 디렉토리 조회 (init 파일을 돌리기 위한 진입점)
    utils_directory = os.path.join(package_directory, 'utils')

    # sys.path에 패키지 디렉토리를 추가합니다.
    sys.path.append(utils_directory)

    main()
