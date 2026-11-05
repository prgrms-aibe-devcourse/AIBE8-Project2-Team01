# EFY 프론트엔드 이미지 에셋

카테고리 아이콘 16개와 고수 프로필 사진 20개를 제공한다. 프로필 v2는 기존 가상 인물을 바탕으로 OpenAI 내장 image_gen에서 서비스별 작업·수업·상담 자세로 편집했다. 기존 프로젝트 소스는 수정하지 않았다.

## 파일

- `icons/`: 투명 배경 PNG, 768×768. 원본의 작은 부품과 재질을 살린 3D 렌더 스타일이다.
- `portraits/`: JPG, 768×768. `expert-101.jpg`부터 `expert-120.jpg`까지 제공한다.
- `originals/`: 고해상도 생성 원본. 용량을 줄인 프론트엔드용 ZIP에서는 제외한다.
- `manifest.json`: 파일별 ID, 종류, 상대 경로, 크기, 생성 이미지 표시.
- `preview.html` / `preview.png`: 전체 미리보기.
- `prompts.json`: 생성 프롬프트와 아트 디렉션.

## 적용

프론트엔드 프로젝트의 `public/efy-assets/`에 `icons`, `portraits`, `manifest.json`을 복사한다. 이 배포 방식에서는 다음 URL로 사용할 수 있다.

```html
<img src="/efy-assets/icons/moving-truck.png" alt="이사 서비스" width="96" height="96" />
<img src="/efy-assets/portraits/expert-101.jpg" alt="시안용 가상 고수" width="160" height="160" />
```

```css
.category-icon { width: 96px; height: 96px; object-fit: contain; }
.expert-photo { width: 160px; height: 160px; object-fit: cover; border-radius: 50%; }
```

이미지 자체에는 카드 배경이나 라벨이 없다. 아이콘의 여백을 임의로 잘라내기보다 `object-fit: contain`으로 표시한다. 프로필 v2는 서비스별 작업·수업·상담 장면이다. 사각 사진 카드는 전체 이미지를 표시하고, 작은 원형 아바타는 얼굴이 잘 보이도록 개별 object-position을 조정한다. 파일별 서비스는 manifest.json의 service에서 확인한다. 목록에서는 첫 화면 밖의 이미지에 `loading="lazy"`를 적용할 수 있다.

## 기존 아이콘 슬롯과 연결

| 기존 이름 | 새 PNG 파일 |
|---|---|
| truck | moving-truck.png |
| toolbox | toolbox.png |
| paint_roller | paint-roller.png |
| balloons | balloons.png |
| books | books.png |
| laptop | laptop.png |
| briefcase | briefcase.png |
| palette | palette.png |
| car | car.png |
| scale | scale.png |
| shapes | shapes.png |
| package | package.png |
| broom | broom.png |
| wrench | wrench.png |
| gift | gift.png |
| camera | camera.png |

## 사용 시 구분

프로필은 AI로 생성한 가상 인물이다. 실제 등록 고수나 실제 경력·리뷰의 증거로 사용하지 않는다. 시안·데모에서 사용하고 실제 서비스의 고수 사진은 해당 고수가 제공한 사진으로 교체한다. 데모 화면에서 가상 인물임을 안내한다.

## 확인 결과

36개 파일 생성, 웹용 변환, 파일 목록과 실제 파일 일치, 전체 미리보기의 구도와 잘림을 확인했다. 실제 프론트엔드 화면에 연결하거나 기존 코드를 변경하지는 않았다.
