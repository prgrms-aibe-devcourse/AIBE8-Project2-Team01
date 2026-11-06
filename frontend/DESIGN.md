---
name: EFY
description: 요청서 한 장으로 여러 견적서를 받는 일상 서비스 매칭 플랫폼
colors:
  blue: "#1f6ae6"
  blue-strong: "#1857c4"
  blue-soft: "#edf4ff"
  blue-line: "#c8dbff"
  blue-fill: "#1f6ae6"
  blue-fill-strong: "#1857c4"
  brand-blue: "#2f7ffd"
  teal: "#037a76"
  brand-teal: "#03948f"
  ink-900: "#141833"
  ink-800: "#252a48"
  ink-600: "#4a5072"
  ink-500: "#5e647f"
  ink-400: "#8a90a8"
  ink-200: "#d9dce7"
  ink-100: "#e8eaf1"
  ink-50: "#f4f5f9"
  paper: "#ffffff"
  paper-soft: "#f6f7fb"
  canvas: "#fafafa"
  surface: "#ffffff"
  sample: "#4a5072"
  sample-soft: "#eef0f5"
  heart: "#d42a50"
  star: "#f5b301"
  col-moving: "#fff1d6"
  col-moving-ink: "#7a4a00"
  col-repair: "#e3edff"
  col-repair-ink: "#1d4fa8"
  col-learn: "#e0f3e8"
  col-learn-ink: "#1e6142"
  col-event: "#fde6ee"
  col-event-ink: "#9b2450"
  danger: "#c8283a"
  danger-soft: "#fdecee"
typography:
  display:
    fontFamily: "Black Han Sans, NanumSquare Neo, sans-serif"
    fontSize: "clamp(2.25rem, 1.4rem + 4vw, 4.5rem)"
    fontWeight: 400
    lineHeight: 1.1
    letterSpacing: "-0.01em"
  page-title:
    fontFamily: "NanumSquare Neo, Apple SD Gothic Neo, Malgun Gothic, system-ui, sans-serif"
    fontSize: "clamp(1.5rem, 1.1rem + 1.6vw, 2.125rem)"
    fontWeight: 800
    lineHeight: 1.25
    letterSpacing: "-0.03em"
  hero-lead:
    fontFamily: "NanumSquare Neo, sans-serif"
    fontSize: "clamp(1.125rem, 1rem + 0.5vw, 1.375rem)"
    fontWeight: 500
    lineHeight: 1.6
    letterSpacing: "-0.02em"
  headline:
    fontFamily: "NanumSquare Neo, sans-serif"
    fontSize: "clamp(1.5rem, 1.2rem + 1vw, 1.75rem)"
    fontWeight: 750
    lineHeight: 1.3
    letterSpacing: "-0.03em"
  subhead:
    fontFamily: "NanumSquare Neo, sans-serif"
    fontSize: "1.375rem"
    fontWeight: 750
    lineHeight: 1.3
    letterSpacing: "-0.03em"
  title:
    fontFamily: "NanumSquare Neo, sans-serif"
    fontSize: "1.25rem"
    fontWeight: 750
    lineHeight: 1.3
    letterSpacing: "-0.03em"
  lead:
    fontFamily: "NanumSquare Neo, sans-serif"
    fontSize: "1.125rem"
    fontWeight: 750
    lineHeight: 1.6
    letterSpacing: "-0.02em"
  body:
    fontFamily: "NanumSquare Neo, sans-serif"
    fontSize: "1rem"
    fontWeight: 400
    lineHeight: 1.6
    letterSpacing: "-0.01em"
  body-sm:
    fontFamily: "NanumSquare Neo, sans-serif"
    fontSize: "0.9375rem"
    fontWeight: 600
    lineHeight: 1.5
    letterSpacing: "-0.01em"
  label:
    fontFamily: "NanumSquare Neo, sans-serif"
    fontSize: "0.8125rem"
    fontWeight: 700
    lineHeight: 1.6
    letterSpacing: "-0.01em"
  tag:
    fontFamily: "NanumSquare Neo, sans-serif"
    fontSize: "12px"
    fontWeight: 650
    lineHeight: 1
    letterSpacing: "0em"
rounded:
  tag: "8px"
  control: "12px"
  card: "16px"
  tile: "20px"
  band: "28px"
  pill: "999px"
spacing:
  sp-1: "4px"
  sp-2: "8px"
  sp-3: "12px"
  sp-4: "16px"
  sp-5: "20px"
  sp-6: "24px"
  sp-8: "32px"
  sp-10: "40px"
  sp-12: "48px"
  sp-16: "64px"
  section-gap: "clamp(72px, 6vw + 40px, 128px)"
  gutter: "clamp(20px, 4vw, 40px)"
  page-max: "1200px"
  header-h: "72px"
components:
  button-primary:
    backgroundColor: "{colors.blue}"
    textColor: "{colors.paper}"
    typography: "{typography.body}"
    rounded: "{rounded.control}"
    padding: "0 22px"
    height: "48px"
  button-primary-hover:
    backgroundColor: "{colors.blue-strong}"
    textColor: "{colors.paper}"
  button-secondary:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink-900}"
    rounded: "{rounded.control}"
    padding: "0 22px"
    height: "48px"
  button-ghost:
    textColor: "{colors.ink-800}"
    rounded: "{rounded.control}"
    padding: "0 12px"
    height: "40px"
  button-ghost-hover:
    backgroundColor: "{colors.ink-50}"
  button-sm:
    typography: "{typography.body-sm}"
    padding: "0 16px"
    height: "40px"
  chip:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink-800}"
    typography: "{typography.body-sm}"
    rounded: "{rounded.pill}"
    padding: "0 16px"
    height: "40px"
  chip-selected:
    backgroundColor: "{colors.blue-soft}"
    textColor: "{colors.blue-strong}"
  tab:
    backgroundColor: "{colors.paper-soft}"
    textColor: "{colors.ink-600}"
    rounded: "{rounded.pill}"
    padding: "0 16px"
    height: "40px"
  tab-selected:
    backgroundColor: "{colors.ink-900}"
    textColor: "{colors.paper}"
  sample-tag:
    backgroundColor: "{colors.sample-soft}"
    textColor: "{colors.sample}"
    typography: "{typography.tag}"
    rounded: "{rounded.tag}"
    padding: "0 8px"
    height: "24px"
  searchbar:
    backgroundColor: "{colors.paper}"
    rounded: "{rounded.pill}"
    padding: "8px"
    width: "640px"
  searchbar-field-focus:
    backgroundColor: "{colors.blue-soft}"
    rounded: "{rounded.pill}"
    padding: "8px 20px 8px 22px"
  searchbar-submit:
    backgroundColor: "{colors.blue}"
    textColor: "{colors.paper}"
    rounded: "{rounded.pill}"
    padding: "0 24px"
    height: "56px"
  preview-request:
    backgroundColor: "{colors.paper}"
    rounded: "{rounded.tile}"
    padding: "18px 20px"
  preview-quotes:
    backgroundColor: "{colors.paper-soft}"
    rounded: "{rounded.tile}"
    padding: "16px"
  preview-arrived:
    textColor: "{colors.teal}"
    typography: "{typography.body-sm}"
  shelf-tile:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink-800}"
    rounded: "{rounded.tile}"
    padding: "10px 0 12px"
  shelf-tile-selected:
    backgroundColor: "{colors.blue-soft}"
    textColor: "{colors.blue-strong}"
  expert-photo:
    backgroundColor: "{colors.paper-soft}"
    rounded: "{rounded.tile}"
  expert-tag:
    backgroundColor: "{colors.paper-soft}"
    textColor: "{colors.ink-600}"
    typography: "{typography.tag}"
    rounded: "{rounded.tag}"
    padding: "0 9px"
    height: "26px"
  expert-tag-service:
    backgroundColor: "{colors.blue-soft}"
    textColor: "{colors.blue-strong}"
  post-card:
    backgroundColor: "{colors.paper}"
    rounded: "{rounded.card}"
    padding: "22px 20px 20px"
  collection-panel:
    backgroundColor: "{colors.paper-soft}"
    rounded: "{rounded.tile}"
    padding: "32px"
  join-band:
    backgroundColor: "{colors.blue-soft}"
    rounded: "{rounded.band}"
    padding: "clamp(36px, 5vw, 64px)"
  input-field:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink-900}"
    rounded: "{rounded.control}"
---

# Design System: EFY

## Overview

**Creative North Star: "Request to Quote"**

EFY 메인 화면은 흰 바탕과 엷은 회청색 면 위에서 고객의 요청 하나가 견적으로 이어지는 순서를 보여준다.
첫 화면에는 큰 제목과 알약형 검색창 하나와 요청서 미리보기만 두고 나머지 공간은 비워 둔다.
오른쪽 흐름 예시 화면은 요청서 보내기, 전달, 견적 비교, 견적 상세, 채팅 다섯 장면을 자동으로 반복한다.
검색창에 고른 서비스와 지역은 첫 장면의 요청서에 그대로 들어간다.

색은 행동을 맡는 파랑 하나로 좁힌다.
이 파랑은 팀 로고의 파랑을 한 단계 짙게 만든 값이다.
로고의 청록은 화면 안에서 견적 도착 문구 하나에만 작게 쓴다.
면은 흰색과 회청색 두 단계로만 나누고 장식 색판을 두지 않는다.
카테고리 아이콘과 고수 프로필 이미지는 프로젝트에서 직접 렌더링한 무광에 가까운 3D 이미지이다.
색이 필요한 자리는 이 3D 이미지가 맡는다.

서체는 본문과 제목에 나눔스퀘어 네오(NanumSquare Neo)를 쓰고 히어로 제목 한 곳에만 Black Han Sans를 쓴다. 강사님 피드백에 따라 팀원 저장소(personal_AIBE8-Project2-Team01_FE)와 맞췄다.
위계는 크기와 굵기와 자간으로만 만든다.
이전 방향이었던 사본 용지 재질과 남색 행동 색과 파스텔 종이 색판은 이번에 모두 교체했다.

**Key Characteristics:**
- 흰 바탕과 회청색 면 두 단계의 표면
- 로고 파랑을 짙게 만든 단일 행동 색
- 청록은 견적 도착 문구에만 쓰는 최소 사용
- 직접 렌더링한 3D 오브젝트와 3D 고수 흉상
- 나눔스퀘어 네오 한 가족(400·700·800·900)과 히어로 전용 Black Han Sans
- 1200px 고정 폭 본문과 양옆 여백
- 회색 예시 표기 하나로 통일한 예시 데이터 표시

## Colors

팔레트는 남색 기운의 잉크 회색 계열과 행동용 파랑 하나로 이루어진다.

### Primary
- **Action Blue** (blue): 주요 버튼과 검색창의 요청서 작성 버튼과 진행 방법 번호 원과 포커스 윤곽선과 입력 커서에 쓴다. 흰 글자 대비를 위해 로고 파랑보다 한 단계 짙다.
- **Pressed Blue** (blue-strong): 주요 버튼 hover 배경이다. 옅은 파랑 바탕 위의 글자색으로도 쓴다. 서비스 태그와 선택된 칩과 선택된 카테고리 이름과 신규 고수 배지 글자가 여기에 해당한다.
- **Blue Mist** (blue-soft): 선택 상태와 포커스 상태의 바탕이다. 선택된 카테고리 타일과 선택된 칩과 검색 필드 포커스와 팝오버의 활성 옵션과 고수 활동 안내 띠의 바탕에 쓴다.
- **Blue Thread** (blue-line): 진행 방법의 점선 연결선과 텍스트 선택 바탕에 쓴다.
- **Logo Blue** (brand-blue): 로고 원본 색이다. 로고 PNG 안에만 있고 UI 요소에 직접 칠하지 않는다.

### Secondary
- **Quote Teal** (teal): 요청서 미리보기의 "견적 N건 도착" 문구에만 쓴다.
- **Collection Tints** (col-moving, col-repair, col-learn, col-event): 상황에 맞춰 한 번에 찾기 카드 네 장의 바탕에만 쓴다. 이사는 따뜻한 모래색, 집 수리는 하늘색, 배움은 연두색, 결혼·행사는 분홍색이다. 카드 제목은 잉크색 그대로 쓰고 짝이 되는 -ink 색은 칩에 마우스를 올렸을 때의 글자와 선에만 쓴다. 바탕 위 칩은 선 없는 흰 바탕이다.
- **Logo Teal** (brand-teal): 로고 원본 색이다. 로고 PNG 안에만 있다.

### Neutral
- **Ink** (ink-900): 제목과 본문 글자이다. 선택된 탭의 바탕으로도 쓴다.
- **Ink Deep** (ink-800): 메뉴 링크와 카테고리 이름과 칩 글자처럼 조작 가능한 글자에 쓴다.
- **Ink Muted** (ink-600): 한 줄 소개와 보조 설명과 회색 태그 글자에 쓴다.
- **Ink Quiet** (ink-500): 메타 정보와 placeholder와 섹션 머리 설명에 쓴다.
- **Ink Faint** (ink-400): 비활성에 가까운 아이콘과 찜 버튼 기본 상태와 보조 버튼 hover 윤곽선에 쓴다.
- **Line** (ink-200): 버튼과 칩과 입력 칸의 1px 안쪽 윤곽선과 푸터 구분선이다.
- **Hairline** (ink-100): 카드 윤곽선과 목록 구분선과 스크롤 후 상단 바 아래 선이다.
- **Wash** (ink-50): 고스트 버튼과 메뉴와 찜 버튼의 hover 바탕이다.
- **Paper** (paper): 페이지 바탕과 카드와 검색창 바탕이다.
- **Paper Soft** (paper-soft): 회청색 면이다. 고수 이미지 바탕과 견적 패널과 상황별 묶음과 진행 방법 영역과 푸터와 회색 태그에 쓴다.

### Status
- **Sample Gray** (sample)와 **Sample Wash** (sample-soft): 예시 데이터 표기 하나에만 쓴다.
- **Heart** (heart): 찜이 켜진 하트에만 쓴다.
- **Danger** (danger)와 **Danger Wash** (danger-soft): 요청서 입력 오류 윤곽선과 오류 문구에 쓴다.

### Named Rules
**The One Action Color Rule.** 누를 수 있는 주요 행동은 Action Blue 하나로만 칠한다. 두 번째 행동 색을 만들지 않는다.

**The Teal Restraint Rule.** 사용자가 청록을 쓰지 않거나 아주 조금만 쓰라고 정했다. 화면 안의 청록은 견적 도착 문구 하나뿐이다. 새 화면에서 청록을 버튼과 배경과 아이콘과 강조에 쓰지 않는다.

**The Gray Sample Rule.** 예시 데이터는 회색 예시 표기 하나로만 밝힌다. 예시를 색이나 다른 모양으로 구분하지 않는다.

### Dark Mode
- 처음 방문하면 시스템 설정을 따른다. 헤더의 해·달 버튼으로 바꾸면 그 선택을 localStorage `efy.theme`에 저장한다.
- index.html의 첫 스크립트가 화면을 그리기 전에 `<html data-theme>`를 붙인다. 다크 값은 tokens.css의 `:root[data-theme='dark']` 한 곳에만 있다.
- 바탕은 남색이 도는 어두운 종이(#0f1222)이고 한 단계 올라온 면은 #161a2e이다. 잉크 단계는 밝기만 뒤집고 이름은 그대로 쓴다.
- 파랑은 두 갈래로 나눈다. 글자와 선에 쓰는 blue는 다크에서 #78a8ff로 밝아진다. 흰 글자를 얹는 버튼 면은 blue-fill(#2a66e0)을 써서 흰 글자 대비 5.1:1을 지킨다.
- 잉크 900 바탕에 얹는 글자는 #fff 대신 paper를 쓴다. 그래야 다크에서 밝은 면 위에 어두운 글자가 놓인다.
- 그림자와 덮개는 `rgb(var(--shade) / 알파)`로 쓴다. 다크에서 shade는 검정이다.
- 묶음 카드 바탕은 어두운 같은 계열 색으로 바뀌고 -ink 색은 밝은 색으로 바뀐다. 커뮤니티 썸네일 바탕은 글마다 정한 색을 paper-soft에 24%만 섞는다.
- 다크 본문 대비: ink-900 16.6:1, ink-600 8.7:1, ink-500 6.6:1, 글자용 blue 7.8:1, danger 6.8:1.

### Functional Screens
- 메인을 뺀 화면은 팀 프론트엔드의 화면 코드와 클래스 이름을 그대로 쓰고 `src/styles/app.css`가 모양을 정한다. 규칙은 `main.page`와 모달(`.dim`) 안으로만 적용한다.
- 바탕은 메인과 기능 화면 모두 canvas(라이트 #fafafa, 다크 #0b0e1c)이고 헤더·카드·푸터는 surface(라이트 #ffffff, 다크 #141830)이다. 강사님 피드백에 따라 팀원 저장소처럼 거의 흰색인 무채색 바탕으로 바꿨다. 면은 1px ink-100 안쪽 선으로 구분하고 그림자는 떠 있는 요소에만 쓴다.
- 버튼 기본값은 ink-50 면이고 `primary`만 blue-fill이다. 위험 동작은 danger-ghost(선) 다음 danger(면) 순서로 강해진다.
- 상태 배지는 점 하나와 알약 모양이다. 접수 중은 blue-soft, 매칭은 blue-fill 면, 마감은 회색, 취소는 danger-soft를 쓴다. 기존 화면의 teal 배지도 Teal Restraint Rule에 따라 파랑 계열로 그린다.
- 요청서·받은 요청·팁 글에는 메인과 같은 입체 분야 아이콘(`CategoryIcon`)을 붙인다. 사진이 없는 팁 글은 묶음 카드 색(col-*) 바탕에 아이콘을 올린다.
- 고수 프로필 사진은 메인 카드처럼 큰 정사각형(r-tile)으로 보여 준다.
- 모바일에서는 하단 탭(64px)을 쓰고 목록과 상세가 나란한 화면은 한 번에 하나만 보여 준다.

## Typography

**Display Font:** Black Han Sans (히어로 제목 "Experts for you!" 한 곳만, 작게 쓰면 뭉개져서 다른 곳에 쓰지 않는다)
**Body Font:** NanumSquare Neo (with Apple SD Gothic Neo and Malgun Gothic). 굵기별 woff 네 개(400·700·800·900)만 있어서 500은 400으로, 600·650·700은 700으로, 750은 800으로 그려진다.

**Character:** 한 가족 안에서 400부터 800까지 굵기 차이로 위계를 만든다. 제목은 자간을 강하게 좁혀 단단하게 보이고 본문은 거의 기본 자간으로 읽기 쉽게 둔다.

### Hierarchy
- **Display** (800, `clamp(2.5rem, 1.4rem + 3.6vw, 4.25rem)`, 1.08, -0.04em): 히어로 제목 "Experts for you!" 한 곳에만 쓴다.
- **Hero Lead** (500, `clamp(1.125rem, 1rem + 0.5vw, 1.375rem)`, -0.02em): 히어로 제목 아래 한 줄 설명이다.
- **Headline** (750, `clamp(1.375rem, 1.15rem + 0.8vw, 1.75rem)`, 1.3): 섹션 제목과 진행 방법 제목과 고수 활동 안내 띠 제목이다.
- **Subhead** (750, 1.25rem): 상황별 묶음 제목과 요청서 시트 제목과 완료 제목이다.
- **Title** (750, 1.125rem): 카테고리 패널 제목과 진행 단계 제목이다. 순위 번호는 같은 크기를 800으로 쓴다.
- **Lead** (750, 1.0625rem, -0.02em): 고수 이름과 미리보기 서비스 이름이다. 입력 값은 같은 크기를 600으로 쓴다.
- **Body** (400, 1rem, 1.6): 기본 본문과 버튼 글자(650)와 게시글 제목(700)이다.
- **Body Small** (0.9375rem): 메뉴와 칩과 탭과 한 줄 소개와 카테고리 이름과 메타 정보에 쓴다. 굵기는 500부터 700 사이에서 고른다.
- **Label** (700, 0.8125rem): 검색창 필드 이름과 요청서 질문 이름과 작은 메타 정보이다.
- **Tag** (650, 12px, 0em): 예시 표기와 고수 태그와 게시판 분류와 필수 표시와 배지이다.

### Named Rules
**The Two Family Rule.** 화면 전체는 나눔스퀘어 네오 한 가족으로 쓰고 Black Han Sans는 히어로 제목 한 곳에만 쓴다. 세 번째 서체를 더하지 않는다.

**The Tight Heading Rule.** 제목 계열은 음수 자간을 쓴다. Display는 -0.04em이고 h1부터 h3는 -0.03em이다. 12px 태그만 0em이다.

## Layout

본문은 1200px 고정 폭(page-max)이다.
양옆 여백은 gutter로 화면 폭에 따라 20px에서 40px 사이로 바뀐다.
간격은 4px 기반 단계(sp-1부터 sp-16)를 쓰고 섹션 사이는 section-gap의 절반을 위아래 padding으로 나누어 쓴다.
상단 바 높이는 72px이고 스크롤하면 축약 검색창이 상단 바 가운데에 나타난다.

히어로는 12칸 grid이다.
제목과 검색창(최대 540px)은 1칸부터 7칸을 쓰고 흐름 예시 화면(최대 340px)은 9칸부터 4칸을 쓴다.
1080px 이하에서는 흐름 예시 화면이 8칸부터 5칸으로 넓어진다.
900px 이하에서는 흐름 예시 화면을 숨긴다.
900px 이하에서는 두 열이 한 열로 합쳐진다.
이때 미리보기는 서비스를 고른 뒤에만 나타나고 3D 오브젝트와 서비스와 지역과 견적 도착 문구만 담은 회청색 띠로 줄어든다.
검색창도 세로로 쌓이고 모서리는 tile 반경으로 바뀐다.

카테고리 선반은 데스크톱에서 11칸 한 줄이다.
1080px 이하에서 6칸이 되고 640px 이하에서 4칸이 된다.
고수 카드 grid는 4열이고 1080px 이하에서 3열이 된다.
900px 이하와 640px 이하에서는 2열이 된다.
커뮤니티 카드는 4열이고 900px 이하에서 2열이 되며 480px 이하에서 1열이 된다.
가로 레일은 4장 단위로 넘기고 1080px 이하에서 3장이 된다.
900px 이하에서는 2.2장이 보이고 640px 이하에서는 화면 가장자리까지 넓어진다.
900px 이하에서는 화면 아래에 요청서 작성 버튼이 고정된다.

**The Quiet First View Rule.** 첫 화면에는 제목과 검색창과 미리보기만 둔다. 배너와 칩 묶음과 설명 문단을 첫 화면에 더하지 않는다.

## Elevation & Depth

이 화면은 평평한 면과 안쪽 1px 윤곽선을 기본으로 쓴다.
그림자는 떠 있는 요소와 hover 반응에만 쓴다.
그림자 색은 Ink 계열의 반투명 값이다.
검색창 하나만 파랑 기운의 그림자를 쓴다.
모든 그림자는 흐림과 음수 spread를 함께 쓰며 흐림이 없는 오프셋 그림자는 쓰지 않는다.

### Shadow Vocabulary
- **Sheet** (`0 1px 2px rgba(20, 24, 51, 0.06), 0 18px 40px -18px rgba(20, 24, 51, 0.28)`): 미리보기 요청서 카드에 쓴다.
- **Float** (`0 1px 2px rgba(20, 24, 51, 0.08), 0 10px 24px -10px rgba(20, 24, 51, 0.24)`): 축약 검색창과 모바일 하단 고정 버튼에 쓴다.
- **Lift** (`0 2px 4px rgba(20, 24, 51, 0.06), 0 14px 28px -14px rgba(20, 24, 51, 0.26)`): 커뮤니티 카드 hover에 쓴다.
- **Search Glow** (`inset 0 0 0 1px #e8eaf1, 0 2px 4px rgba(20, 24, 51, 0.04), 0 16px 40px -20px rgba(31, 106, 230, 0.35)`): 히어로 검색창에만 쓴다. 바깥 그림자 색은 Action Blue의 35% 값이다.
- **Popover** (`inset 0 0 0 1px #e8eaf1, 0 2px 4px rgba(20, 24, 51, 0.06), 0 24px 48px -20px rgba(20, 24, 51, 0.32)`): 서비스 선택과 지역 선택 팝오버에 쓴다.
- **Side Sheet** (`-24px 0 48px -24px rgba(20, 24, 51, 0.35)`): 오른쪽에서 열리는 요청서 시트에 쓴다. 뒤 배경은 `rgba(20, 24, 51, 0.42)` 막으로 덮는다.
- **Rest** (`0 1px 2px rgba(20, 24, 51, 0.05)`부터 `0.08`까지): 견적 한 줄과 고수 이미지 위 배지에 쓰는 가장 얕은 그림자이다.
- **Ring** (`0 0 0 6px` paper-soft와 `0 0 0 2px` paper-soft): 진행 번호 원과 겹친 고수 얼굴을 바탕에서 떼어 내는 테두리이다.
- **Header Line** (`0 1px 0` ink-100): 스크롤한 뒤 상단 바 아래에 생기는 선이다.

### Named Rules
**The Flat Rest Rule.** 카드와 타일은 쉬는 상태에서 평평하다. 커뮤니티 카드만 hover에서 Lift 그림자와 2px 상승을 받는다.

**The Inset Line Rule.** 윤곽선은 border 대신 안쪽 box-shadow 1px로 그린다. 선택과 포커스는 같은 방식으로 1.5px나 2px Action Blue 선을 쓴다. 오류는 1.5px Danger 선을 쓴다.

## Shapes

모서리는 요소의 크기와 역할에 따라 여섯 단계로 나뉜다.
작은 표기는 8px(tag)이다.
버튼과 입력 칸과 메뉴 링크와 팝오버 옵션은 12px(control)이다.
커뮤니티 카드와 견적 한 줄과 빈 상태 상자는 16px(card)이다.
카테고리 타일과 고수 이미지와 미리보기 카드와 상황별 묶음과 팝오버는 20px(tile)이다.
고수 활동 안내 띠와 진행 방법 영역과 모바일 아래 시트의 위쪽 모서리는 28px(band)이다.
검색창과 칩과 탭과 축약 검색창은 알약형(pill)이다.
아바타와 찜 버튼과 원형 이동 버튼과 진행 번호는 원형(50%)이다.
포커스 윤곽선은 8px 반경을 따른다.

**The Nested Radius Rule.** 바깥 요소가 안쪽 요소보다 모서리가 크거나 같다. 알약형 검색창 안의 필드도 알약형이고 tile 패널 안의 견적 한 줄은 card 반경이다.

## Components

### Buttons
- **Shape:** 12px 모서리(control)이고 높이는 기본 48px이다. 작은 버튼은 40px이다.
- **Primary:** Action Blue 바탕과 흰 글자 650이다. 고수 활동 안내 띠의 첫 행동과 시트의 제출 버튼에 쓴다.
- **Hover / Focus:** hover에서 Pressed Blue로 바뀌고 누르면 1px 내려간다. 포커스는 2px Action Blue 윤곽선과 2px 간격이다.
- **Secondary:** 흰 바탕과 ink-200 안쪽 1px 선이다. hover에서 선이 ink-400으로 짙어진다.
- **Ghost:** 바탕 없이 ink-800 글자이고 hover에서 Wash 바탕이 깔린다. 상단 바의 고수로 활동하기와 로그인에 쓴다.

### Chips
- **Style:** 흰 바탕과 ink-200 안쪽 선과 알약형 모서리와 40px 높이이다.
- **State:** hover에서 선과 글자가 Action Blue가 된다. 선택되면 Blue Mist 바탕과 1.5px Action Blue 선과 Pressed Blue 글자가 된다.

### Tabs
- **Style:** Paper Soft 바탕의 알약형 탭이다. 선택된 탭은 Ink 바탕과 흰 글자이다.
- **Quiet:** 바탕 없이 글자만 두고 선택된 탭 아래에 2px Ink 선을 긋는 변형이다.

### Sample Tag
- 회색 바탕과 ink-100 안쪽 선과 8px 모서리와 24px 높이와 12px 글자이다.
- "예시 데이터"와 "예시 화면"과 "예시 글"처럼 글은 달라도 모양은 하나이다.

### Work Showcase Card
- 고수들의 작업 소개 레일의 카드다. 4:3 작업 사진(r-card), 두 줄까지의 제목(800), 고수·서비스·지역 한 줄(ink-500)로 이루어진다.
- 레일은 신규 고수 레일과 같은 양옆 화살표로 한 장씩 넘긴다. 예시 데이터 표기를 붙인다.

### Hero Search Bar (Signature)
- 최대 560px 폭의 paper 알약형 막대이고 Search Glow 그림자를 쓴다. 창을 줄여도 서비스 | 지역 | 버튼이 한 줄을 유지한다.
- 640px 이하에서는 버튼이 48px 돋보기 원형 버튼이 되고 글자는 화면 낭독기에만 남긴다. 900px 이하에서는 펼쳐지는 목록이 검색창 전체 폭으로 열린다.
- 안에는 서비스 필드와 지역 필드와 1px 세로 구분선과 56px 높이의 알약형 요청서 작성 버튼이 있다.
- 필드는 hover에서 Paper Soft가 되고 포커스가 안에 들어오면 Blue Mist가 된다.
- 필드 이름은 Label이고 값은 Body Small이다.
- 선택 팝오버는 검색창 아래 18px 지점에 열린다.

### Request Preview (Signature)
- 흰 요청서 카드(20px 모서리와 Sheet 그림자)와 그 아래 Paper Soft 견적 패널로 이루어진다.
- 요청서 카드에는 72px 3D 오브젝트와 서비스 이름과 지역이 있다.
- 검색창에서 서비스와 지역을 고르면 값이 흐림을 풀며 새로 찍힌다.
- 견적 패널은 Quote Teal 도착 문구 아래에 흰 견적 한 줄 세 개를 180ms 간격으로 올린다.
- 오른쪽 위에 예시 화면 표기를 둔다.

### Category Shelf
- 흰 바탕 위에 3D 오브젝트(최대 88px)와 이름만 둔다. 색판을 깔지 않는다.
- hover에서 Paper Soft 바탕이 깔리고 오브젝트가 살짝 들리며 기운다.
- 선택된 타일은 Blue Mist 바탕과 Pressed Blue 이름이다.
- 선택하면 아래에 Paper Soft 패널이 펼쳐지고 소분류 칩이 나온다.

### Expert Card
- 사진과 본문을 한 장의 카드로 묶는다. surface 바탕에 1px ink-100 안쪽 선과 r-tile 모서리를 쓰고 사진은 위쪽을 꽉 채운다. 본문은 16px 안쪽 여백 안에 이름·한 줄 소개·태그를 두고 찜 하트는 카드 오른쪽 아래 8px 안쪽에 둔다.
- 위에는 Paper Soft 바탕의 큰 정사각형 프로필 이미지가 카드 위 모서리에 맞춰 들어간다.
- 아래에는 이름(Lead)과 한 줄로 자른 소개(Body Small)와 태그 줄이 있다.
- 태그 순서는 서비스 태그(Blue Mist 바탕과 Pressed Blue 글자)와 경력과 지역이다. 경력과 지역 태그는 Paper Soft 바탕이다.
- 찜 하트는 오른쪽 아래 44px 원형 버튼이다. 켜지면 Heart 색으로 채워진다.
- 신규 고수 배지는 이미지 왼쪽 위의 흰 알약형 표기이다.
- 카드 전체가 링크이고 hover에서 이미지가 1.04배 커지며 이름에 밑줄이 생긴다.
- 640px 이하에서는 2열이 되고 서비스 태그만 남긴다.

### Cards / Containers
- **Community Card:** 흰 바탕과 ink-100 안쪽 선과 16px 모서리이다. 분류 태그와 제목과 작성자 메타를 세로로 둔다. 데스크톱에서 4열이다.
- **Collection Panel:** Paper Soft 바탕과 20px 모서리와 32px 안쪽 여백이다. 오른쪽에 3D 오브젝트 세 개를 기울여 겹친다.
- **How It Works:** Paper Soft 바탕과 28px 모서리 영역 안에 번호 원 세 개를 Blue Thread 점선으로 잇는다.
- **Expert Join Band:** Blue Mist 바탕과 28px 모서리 띠이다. 왼쪽에 제목과 Primary 버튼과 Secondary 버튼을 두고 오른쪽에 흰 44px 아이콘 칸과 설명 목록을 둔다.

### Inputs / Fields
- **Style:** 흰 바탕과 ink-200 안쪽 1px 선과 12px 모서리이다.
- **Focus:** 안쪽 선이 2px Action Blue로 바뀐다. 커서 색도 Action Blue이다.
- **Error:** 안쪽 선이 1.5px Danger로 바뀌고 Label 크기 오류 문구가 붙는다.

### Navigation
- 상단 바는 흰 바탕이고 왼쪽에 29px 높이 로고와 링크 두 개를 둔다. 오른쪽에 Ghost 버튼 두 개와 Secondary 버튼 하나를 둔다.
- 링크는 Body Small 600이고 hover에서 Wash 바탕과 12px 모서리가 생긴다.
- 900px 이하에서는 링크를 숨기고 메뉴 버튼과 아래로 펼치는 목록으로 바꾼다.

### Motion
- 기본 전환은 220ms와 `cubic-bezier(0.16, 1, 0.3, 1)`이다.
- 오브젝트와 이미지 움직임은 520ms부터 620ms까지 길게 쓴다.
- prefers-reduced-motion이 reduce이면 모든 애니메이션과 전환을 사실상 끈다.

## Motion

히어로의 흐름 예시 화면 하나만 자동으로 움직인다.
나머지 움직임은 사용자의 동작에 대한 응답이다.

- **장면 순서:** 요청서 보내기 3.0초, 전달 2.0초, 견적 비교 2.8초, 견적 상세 3.2초, 채팅 3.6초를 반복한다.
- **장면 전환:** 새 장면은 오른쪽 28px에서 4px 흐림과 함께 420ms 동안 들어온다. 장면이 머무는 동안 3.5%만큼 천천히 다가간다.
- **문구:** 장면마다 한 줄 문구가 왼쪽부터 700ms 동안 드러나고 끝에 파란 커서가 깜빡인다.
- **누름 표시:** 누르는 위치에 원이 생겼다 퍼지고 버튼이 0.97배로 눌린다.
- **숫자와 도장:** 견적 금액은 0에서 900ms 동안 올라가고 의뢰 요청을 누르면 파란 원형 "의뢰 요청" 도장이 찍힌다.
- **진행선:** 아래 단계 표시는 현재 단계까지 파란 선이 그어진다. 단계를 누르면 그 장면으로 이동한다.
- **제어:** 일시정지 버튼이 있다. 화면 밖으로 나가거나 탭이 숨겨지면 멈춘다. 동작 줄이기 설정이면 자동 재생하지 않고 장면 전환 효과를 끈다.
- **곡선:** 모든 움직임은 `cubic-bezier(0.16, 1, 0.3, 1)` 감속 곡선을 쓴다. 튕기는 곡선은 쓰지 않는다.

## Do's and Don'ts

### Do:
- **Do** 주요 행동에는 Action Blue(blue)를 쓰고 hover에는 Pressed Blue(blue-strong)를 쓴다.
- **Do** 선택과 포커스 상태는 Blue Mist(blue-soft) 바탕과 Action Blue 안쪽 선으로 표시한다.
- **Do** 색이 필요한 자리는 `public/efy-assets/`의 입체 오브젝트와 고수 이미지로 채운다.
- **Do** 예시 데이터에는 회색 예시 표기(sample-soft와 sample) 하나를 붙인다.
- **Do** 윤곽선은 안쪽 1px box-shadow로 그린다.
- **Do** 모서리는 tag와 control과 card와 tile과 band와 pill 여섯 단계 안에서 고른다.

### Don't:
- **Don't** 청록(teal)을 견적 도착 문구 밖에서 쓰지 않는다. 사용자가 청록을 피하거나 아주 조금만 쓰라고 정했다.
- **Don't** 로고 원본 색(brand-blue와 brand-teal)을 UI 요소에 직접 칠하지 않는다.
- **Don't** 카테고리 타일이나 카드에 색판이나 파스텔 바탕을 깔지 않는다.
- **Don't** 노란 메모지 느낌이나 종이 사본 재질을 쓰지 않는다. 사용자가 금지했다.
- **Don't** 외부 이모지 세트나 글자 기호 아이콘을 쓰지 않는다. 아이콘은 SVG 선 아이콘과 직접 렌더링한 3D 이미지만 쓴다.
- **Don't** 별점과 후기와 집계 수치처럼 보이는 장식을 만들지 않는다.
- **Don't** 흐림이 없는 오프셋 그림자를 쓰지 않는다.
