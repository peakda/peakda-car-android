# PEAKDA Car

**지금 가장 예쁜 곳을, 가는 길에.** — 단풍·은행나무·꽃 명소의 '절정도'를 보여 주고, 지금 안내 중인 길에 경유지로 바로 추가해 주는 Pleos Connect 차량용 앱입니다.

## APK

| 항목 | 내용 |
| --- | --- |
| APK 파일 | [`apk/PEAKDA_PEAKDACar_release.apk`](apk/PEAKDA_PEAKDACar_release.apk) |
| 빌드 종류 | release (서명됨, R8 코드 축소 적용) |
| 패키지명 | `ai.pleos.playground.sdk.navidevapp` |
| 버전 | 2.5.0 (versionCode 50) |

## 주요 화면

| 번호 | 화면 | 설명 |
| --- | --- | --- |
| 01 | 홈 · 지금 가장 예쁜 곳 | 근처에서 지금 절정인 명소 추천, 바로 안내 시작 |
| 02 | 후보 비교 | 후보를 3곳씩 비교 |
| 03 | 명소 상세 | 절정도, 거리·시간, 방문자 기록 |
| 04 | 경유지 · 목적지 확인 | 지금 가는 길에 경유지로 추가하거나 새 목적지로 변경 |
| 05 | 절정도 기록 | 다녀온 곳의 절정도 기록 |
| 06 | 예측 캘린더 | 앞으로의 절정 시기 예측 |
| 07–09 | 정차 잠금 · 주차 복귀 · 주행 중 안내 | 운전 상황에 맞춘 화면 전환 |
| 10–14 | 음성 | 듣는 중 → 생각 중 → 후보 결과 → 상세 / 추가 질문 |
| 15–17 | 로그인 · 설정 · 지역 선택 | 로그인은 선택 사항, 라이트/다크 테마 전환 |
| 18–21 | 예외 상태 · 자동 기록 | 정보 없음, 통신 오류, 내비 연결 실패, 출발 시 자동 기록 |

## 구현 범위

| 기능 | 상태 |
| --- | --- |
| 경유지로 추가 | ✅ Pleos NaviHelper SDK `addWaypoint`로 기본 내비에 실제 요청. 안내 중인 길이 없으면 `requestRoute`로 목적지 안내 |
| 목적지로 설정 | ✅ `requestRoute`로 기본 내비에 실제 길 안내 요청 |
| 내비 결과 처리 | ✅ `onWaypointChanged`·`onRouteStarted`로 성공 확인, `onError`면 "내비 연결 실패" 화면 |
| 화면 21개 · 라이트/다크 테마 | ✅ Figma 디자인 기준 |
| 명소·절정도 정보, 음성 검색, 로그인, 기록 저장 | 샘플 데이터·화면 흐름만 구현 (서버 연동 없음) |

명소 5곳(충남 아산 은행나무길 등)의 좌표는 실제 위치 기준 대략 값입니다.

내비 연동 코드는 [`peakda/navi/PeakdaNavi.kt`](app/src/main/java/ai/pleos/playground/navidevapp/peakda/navi/PeakdaNavi.kt)에 있습니다.

## 실행 방법

### APK로 바로 실행

1. Pleos Connect 에뮬레이터를 실행합니다.
2. APK를 설치합니다.
   ```bash
   adb install -r apk/PEAKDA_PEAKDACar_release.apk
   ```
3. **내비게이션 앱을 한 번 열어** 지도가 뜨는지 확인합니다. 에뮬레이터를 막 켠 직후에는 내비가 준비 중이라 경로 요청을 받지 않습니다.
4. 앱 목록에서 **PEAKDA Car**를 실행합니다.
5. 홈 → `안내시작` 또는 명소 상세 → `경유지로 추가` / `목적지로 설정`을 누르면 내비에 길 안내가 요청됩니다.
   에뮬레이터 내비는 경로 탐색에 20~30초, 경유지 추가 후 경로 선택까지 1분 정도 걸립니다.
   ```bash
   # 또는 명령으로 실행
   adb shell am start -n ai.pleos.playground.sdk.navidevapp/ai.pleos.playground.navidevapp.MainActivity
   ```

### 소스에서 빌드

```bash
git clone <이 저장소 주소>
cd <저장소 폴더>
./gradlew assembleDebug      # 디버그 APK: app/build/outputs/apk/debug/
./gradlew assembleRelease    # 릴리스 APK: app/build/outputs/apk/release/
```

- release 서명 정보(`keystore.properties`, `*.jks`)는 저장소에 포함하지 않습니다. 이 파일이 없으면 `assembleRelease`는 서명되지 않은 APK(`app-release-unsigned.apk`)를 만듭니다. 바로 설치해 보려면 `assembleDebug`를 사용해 주세요.
- 직접 서명하려면 루트에 아래 형식의 `keystore.properties`를 만듭니다.
  ```properties
  storeFile=../my-release.jks
  storePassword=...
  keyAlias=...
  keyPassword=...
  ```

### 특정 화면 바로 열기 (심사·확인용)

```bash
# screen: 01~21, variant: b / c (03·05 화면의 상태 변형), theme: light / dark
adb shell am start -n ai.pleos.playground.sdk.navidevapp/ai.pleos.playground.navidevapp.MainActivity \
  --es screen 04 --es theme dark
```

홈 화면 왼쪽 위 로고를 길게 누르면 전체 화면 목록(개발용)으로 이동합니다.

## 테스트 환경 및 필요한 설정

| 항목 | 내용 |
| --- | --- |
| 에뮬레이터 | **Pleos Connect v2.1.0** (`RELEASE.release_connect_v2.1.0`) |
| Android | Android 14 (API 34), Android Automotive, x86_64 |
| 화면 규격 | 1920 × 1080, density 160 (AVD 기본 1024 × 768 `automotive_1024p_landscape`, 시스템 오버라이드 1920 × 1080) |
| 앱 표시 영역 | Figma 기준 628 × 855 창을 화면 오른쪽 앱 영역 가운데에 표시 (높이가 모자라면 비율 유지 축소) |
| minSdk / targetSdk | 33 / 35 |
| 빌드 도구 | Android Gradle Plugin 8.8.0, Gradle 8.10.2, Kotlin 1.9.22, JDK 17 이상 |
| UI | Jetpack Compose (BOM 2024.04.01), Hilt 2.51.1 |
| SDK | Pleos NaviHelper 3.0.5 (`https://nexus-playground.pleos.ai/repository/maven-releases/`, 토큰 불필요) |

필요한 설정

- 별도 API 키나 계정 설정은 필요 없습니다.
- 내비게이션 권한(`pleos.car.permission.NAVI_ROUTE` 등)은 `AndroidManifest.xml`에 선언되어 있습니다.
- 소스 빌드 시 위 Pleos Playground Maven 저장소에 접근할 수 있어야 합니다.

## 프로젝트 구조

```
app/src/main/java/ai/pleos/playground/navidevapp/
├── MainActivity.kt          # 진입점, 화면 지정 인텐트 처리
└── peakda/
    ├── PeakdaApp.kt         # 화면 라우팅
    ├── PeakdaState.kt       # 화면 목록(21개)과 상태
    ├── navi/PeakdaNavi.kt   # Pleos NaviHelper SDK 연동(경유지 추가·목적지 설정)
    ├── components/          # 공통 UI 컴포넌트, 아이콘
    ├── screens/             # 홈·상세·기록·예외 상태 등 화면
    └── theme/               # 색상, 글꼴(Pretendard·Asta Sans), 라이트/다크 테마
```

`data/`, `screen/`, `ui/`, `utils/`는 바탕이 된 NaviHelper SDK 샘플 코드입니다.

이 프로젝트는 [Pleos NaviHelper SDK 샘플 앱](https://github.com/Pleos-Playground/pleos-sample-navi-app)을 바탕으로 만들었습니다.
