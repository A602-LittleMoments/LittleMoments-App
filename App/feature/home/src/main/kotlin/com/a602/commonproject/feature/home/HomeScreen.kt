package com.a602.commonproject.feature.home

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.component.ProfileFullAstronaut
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.feature.home.components.MemorableMoments
import com.a602.commonproject.feature.home.components.timelineSection
import com.a602.commonproject.model.data.Baby
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.PagerState
import com.a602.commonproject.designsystem.component.BabyInfoRow
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.ripple
import androidx.compose.runtime.CompositionLocalProvider
import com.a602.commonproject.designsystem.component.LMNavigationDefaults.NavigationBarHeight
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color6
import com.a602.commonproject.designsystem.theme.lightblue
import com.a602.commonproject.designsystem.theme.main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import androidx.paging.compose.collectAsLazyPagingItems
import com.a602.commonproject.model.data.SharedMedia


/**
 * [HomeRoute] Composable
 *
 * 홈 화면 기능(Feature)의 최상위 진입점입니다.
 * 이 Composable은 UI 그리기보다는 '데이터 연결'과 '상태 관리'에 집중합니다.
 *
 * 주요 역할:
 * 1. [HomeViewModel] 주입 및 생명주기 관리 (Hilt 사용)
 * 2. ViewModel의 [HomeUiState]를 수집(Collect)하여 UI에 반응적으로 데이터를 공급
 * 3. 로딩/에러/성공 상태에 따른 화면 분기 처리 (When 식 사용)
 *
 * @param viewModel 비즈니스 로직과 UI 상태를 관리하는 ViewModel (기본값: hiltViewModel())
 * @param onNotificationClick 사용자가 알림 아이콘을 클릭했을 때 실행할 동작 (Navigation 등)
 * @param onNavigateToUpload 사용자가 업로드 버튼을 클릭했을 때 실행할 동작
 */
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onNotificationClick: () -> Unit,
    onNavigateToUpload: () -> Unit,
) {
    // CollectAsState: Flow 데이터를 Compose State로 변환하여, 데이터 변경 시 리컴포지션(Recomposition)을 유발합니다.
    val uiState by viewModel.uiState.collectAsState()

    // 상태(State) 기반 화면 렌더링 분기
    when (val state = uiState) {
        // [로딩 상태] 데이터가 준비되지 않았을 때 CircularProgressIndicator를 중앙에 표시
        is HomeUiState.Loading -> Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }

        // [에러 상태] 데이터 로드 실패 시 에러 메시지 표시 (추후 Retry 버튼 등 추가 가능)
        is HomeUiState.Error -> Box(Modifier.fillMaxSize()) {
            Text(
                text = "Error : ${state.errorMessage}",
                modifier = Modifier.align(Alignment.Center),
            )
        }

        // [성공 상태] 데이터가 준비되었을 때 실제 홈 화면(HomeScreen)을 렌더링
        is HomeUiState.Success -> {
            HomeScreen(
                uiState = state,
                onNotificationClick = onNotificationClick,
                onNavigateToUpload = onNavigateToUpload,
                // ViewModel 함수는 여기서 직접 전달하거나 람다로 래핑해서 전달할 수 있습니다.
                // 여기서는 탭 변경 로직 등을 위해 ViewModel 인스턴스를 주입했습니다.
            )
        }
    }
    // 디버깅용 로그: 상태 변화를 Logcat에서 추적하기 위함
    Log.d("uiState", "HomeRoute: $uiState")
}

/**
 * [HomeScreen] Composable
 *
 * 실제 사용자에게 보여지는 홈 화면의 UI 구성을 담당합니다.
 * Stateless(상태를 가지지 않음)에 가깝게 설계하려고 했으나, PagerState 등 UI 종속적인 상태는 내부에서 관리합니다.
 *
 * 주요 구성요소:
 * - [Scaffold]: 머티리얼 디자인 레이아웃 구조 (TopBar, Content 등)
 * - [TopBar]: 상단 영역 (아기 선택 탭바 + 알림 아이콘)
 * - [LazyColumn]: 스크롤 가능한 메인 컨텐츠 영역 (프로필, 컬렉션, 타임라인 등)
 * - [HorizontalPager]: 아기 프로필을 좌우로 넘겨볼 수 있는 슬라이더
 *
 * @param uiState 홈 화면에 표시할 성공 상태의 데이터 객체
 * @param onNotificationClick 상위에서 전달받은 알림 클릭 이벤트 핸들러
 * @param onNavigateToUpload 상위에서 전달받은 업로드 클릭 이벤트 핸들러
 * @param viewModel 탭 변경(아기 선택) 시 상태 업데이트를 위해 사용
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState.Success,
    onNotificationClick: () -> Unit,
    onNavigateToUpload: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    // region [UI State Definitions]

    // PagerState: 아기 프로필 슬라이더의 현재 페이지 및 스크롤 상태 관리
    // initialPage: ViewModel에서 관리하는 선택된 인덱스로 초기화
    val pagerState = rememberPagerState(
        initialPage = uiState.selectedBabyIndex,
        pageCount = { uiState.babies.size },
    )

    // CoroutineScope: 비동기 작업(예: 스크롤 애니메이션)을 시작하기 위한 스코프
    val coroutineScope = rememberCoroutineScope()
    // endregion

    val pagingItems = viewModel.mediaPagingFlow.collectAsLazyPagingItems()
    val currentBaby = if (uiState.babies.isNotEmpty()) {
        uiState.babies.getOrNull(pagerState.currentPage)
    } else null

    // region [Side Effects - State Synchronization]

    // 1. Pager -> ViewModel 동기화
    // 사용자가 손으로 Pager를 스크롤했을 때, 뷰모델의 현재 선택된 아기 인덱스를 업데이트합니다.
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != uiState.selectedBabyIndex) {
            viewModel.updateSelectedBaby(pagerState.currentPage)
        }
    }


    // region [Layout Structure]
    Scaffold(
        contentColor = background,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = NavigationBarHeight)
            .navigationBarsPadding()
            .statusBarsPadding()
    ) { paddingValues ->

        // [Main Content: Scrollable List]
        // LazyColumn을 사용하여 성능 최적화된 스크롤 목록 구현
        LazyColumn(
            contentPadding = paddingValues, // Scaffold가 제공하는 padding 적용 (TopBar 등에 가려지지 않게)
            modifier = Modifier.fillMaxSize(),
        ) {

            // 1. [아기 프로필 섹션] (Pager)
            // 1. [Top Section] Pager (Image) -> Tabs -> Info
            item {
                if (uiState.babies.isNotEmpty()) {
                    // A. Astronaut Pager (Images only)
                     HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth(),
                    ) { page ->
                        BabyProfileSection(baby = uiState.babies[page])
                    }

                    // B. Tabs (Middle, below image)
                    TopTabSection(
                        uiState = uiState,
                        onNotificationClick = onNotificationClick,
                        pagerState = pagerState,
                        coroutineScope = coroutineScope,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // C. Baby Info (MyPage Style)
                    if (currentBaby != null) {
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            BabyInfoRow(
                                baby = currentBaby,
                                onEditClick = { /* TODO: Navigate to Edit or ignore in Home */ }
                            )
                        }
                    }
                } else {
                    EmptyBabyState()
                }
            }

            // [Divider] 섹션 구분선
            item {
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                )
            }


            // 2. [기억하고 싶은 순간 섹션] (Collection Slider)
            // 컬렉션 데이터가 있을 때만 영역을 표시합니다.
            if (uiState.collections.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    MemorableMoments(collections = uiState.collections)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // 3. [타임라인 섹션] (Photo List)


                timelineSection(
                    pagingItems = pagingItems,
                    birthDate = currentBaby?.birthDate ?: "",
                    onPhotoClick = {},
                )

            // [Empty Timeline Handling]
            if (pagingItems.itemCount == 0) {
                item {
                    EmptyTimelineState()
                }
            } else {
                item {
                    // 리스트 하단 여백 확보
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
// endregion
}


@Composable
fun TopTabSection(
    uiState: HomeUiState.Success,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    onNotificationClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp), // 좌우 여백 디테일,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (uiState.babies.size >= 2) {
            SecondaryScrollableTabRow(
                modifier = Modifier.weight(1f),
                selectedTabIndex = uiState.selectedBabyIndex,
                edgePadding = 0.dp,
                containerColor = Color.Transparent, // 탭 배경을 투명하게 처리 (깔끔한 디자인)
                contentColor = Color.Black, // 텍스트 및 아이콘 기본 색상
                divider = {}, // 기본 하단 구분선(Divider) 제거하여 심플하게 표현
            ) {
                // 각 아기별 탭 생성
                uiState.babies.forEachIndexed { index, baby ->
                    Tab(
                        modifier = Modifier
                            .padding(horizontal = 4.dp) // 1. 탭끼리 너무 붙지 않게 약간 띄움
                            .clip(RoundedCornerShape(80))
                            .background(Color.Transparent)// 2. 🚨 핵심: 탭 모양을 둥근 알약으로 깎음 -> 리플도 둥글게 나옴!
                            .wrapContentWidth(), // 내용만큼만 크기 잡기
                        selected = uiState.selectedBabyIndex == index,
                        // 🚨 핵심 1: 탭에게 "내용물 색(=리플 색)은 노란색(main)이야"라고 선언
                        selectedContentColor = background,
                        unselectedContentColor = background,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = baby.babyName,
                                // 선택 여부에 따라 텍스트 색상과 굵기 변경 (가독성 향상)
                                color = if (uiState.selectedBabyIndex == index) main else color6,
                                fontWeight = if (uiState.selectedBabyIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp,
                            )
                        },
                    )
                }
            }
        } else {
            Spacer(Modifier.weight(1f)) // 탭과 알림 사이 빈 공간 채우기
        }


        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier.padding(end = 8.dp).size(48.dp),
        ) {
            Icon(
                imageVector = LMicons.Notifications,
                contentDescription = null,
                tint = main,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}


/**
 * [BabyProfileSection] Composable
 *
 * 아기 프로필 카드를 렌더링합니다.
 * - 구성: 우주복 입은 아기 이미지 + 장식(별) + 이름 + D-Day + 성장 게이지
 *
 * @param baby 표시할 아기 정보 객체
 */
@Composable
fun BabyProfileSection(baby: Baby)
{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 0.dp), // Remove bottom padding, keep top
        contentAlignment = Alignment.Center,
    ) {
        // region [Background Decoration - Stars]
        // Star 1 (좌측 상단)
        Icon(
            painter = painterResource(id = R.drawable.star),
            contentDescription = null,
            tint = Color(0xFFFFC107), // Amber 색상
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-150).dp, y = (-70).dp)
                .size(34.dp)
                .rotate(-15f),
        )
        // Star 2 (우측 상단)
        Icon(
            painter = painterResource(id = R.drawable.star),
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 150.dp, y = (-50).dp)
                .size(30.dp)
                .rotate(15f),
        )
        // Star 3 (좌측 하단)
        Icon(
            painter = painterResource(id = R.drawable.star),
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-120).dp, y = 70.dp)
                .size(28.dp)
                .rotate(-10f),
        )
        // Star 4 (우측 하단)
        Icon(
            painter = painterResource(id = R.drawable.star),
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 150.dp, y = 50.dp)
                .size(24.dp)
                .rotate(20f),
        )
        // endregion

        // region [Center Content - Profile Only]
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // [Custom Component: ProfileFullAstronaut]
            // 아기 얼굴 이미지와 우주복 이미지를 합성하여 보여주는 컴포넌트입니다.
            ProfileFullAstronaut(
                selectedImageUri = null, // 로컬 이미지가 없을 경우 null
                remoteImageUrl = baby.imageUrl, // 서버 URL 이미지 사용
                clickableEnabled = false, // 클릭 이벤트 비활성화
            )
            // 텍스트 정보 제거됨 (BabyInfoRow로 대체)
        }
        // endregion
    }
}


/**
 * [Helper Function] calculateDaysSince
 *
 * 주어진 날짜 문자열(YYYY-MM-DD)로부터 오늘까지의 경과 일수를 계산합니다.
 * @param dateString "YYYY-MM-DD" 형식의 문자열
 * @return 경과 일수 (Long type), 파싱 에러 시 0 반환
 */
fun calculateDaysSince(dateString: String): Long {
    return try {
        val today = java.time.LocalDate.now()
        val birth = java.time.LocalDate.parse(dateString) // ISO_LOCAL_DATE 포맷 가정
        java.time.temporal.ChronoUnit.DAYS.between(birth, today) + 1 // D+1 (태어난 날이 1일)
    } catch (e: Exception) {
        // 날짜 파싱 실패 시 예외 처리 (로그 추가 권장)
        Log.e("DateCalculation", "Failed to parse date: $dateString", e)
        0
    }
}

/**
 * [Data Class] BirthdayInfo
 * 생일 관련 계산 결과를 담는 데이터 클래스입니다.
 */
data class BirthdayInfo(
    val daysUntil: Long,
    val progress: Float
)

/**
 * [Helper Function] getBirthdayInfo
 *
 * 생년월일을 받아 다음 생일까지 남은 일수와, 지난 생일부터 다음 생일까지의 기간 중 현재 진행률(%)을 계산합니다.
 */
fun getBirthdayInfo(birthDate: String): BirthdayInfo {
    return try {
        if (birthDate.isEmpty()) return BirthdayInfo(0, 0f)

        val today = java.time.LocalDate.now()
        val birth = java.time.LocalDate.parse(birthDate)

        // 올해의 생일 날짜 계산
        val currentYearBirthday = birth.withYear(today.year)

        // 다음 생일 계산: 올해 생일이 지났거나 오늘이면 내년 생일, 아니면 올해 생일
        // * 요구사항에 따라 '오늘이 생일'인 경우 처리가 달라질 수 있음. (여기서는 D-0, Progress 100% 등으로 처리 가능)
        // 일단 단순히 "앞으로 다가올 생일"을 기준으로 한다면:
        val nextBirthday = if (currentYearBirthday.isBefore(today) || currentYearBirthday.isEqual(today)) {
             currentYearBirthday.plusYears(1)
        } else {
             currentYearBirthday
        }

        // 만약 오늘이 생일이라면?
        // 위 로직대로면 오늘이 생일일 때 nextBirthday는 내년이 됨. (D-365, Progress 0%)
        // 하지만 사용자 입장에서 오늘 생일이면 "D-Day"라고 크게 뜨고 축하해주길 바랄 수도 있음.
        // 여기서는 "생일까지 남은 기간"을 보여주는 게 목적이므로, 오늘이 생일인 경우
        // ->  daysUntil = 0
        // ->  progress = 1.0f (꽉 채움)
        // 로 처리하고, 내일부터 다시 0%로 시작하는 게 자연스러움.
        if (currentYearBirthday.isEqual(today)) {
             return BirthdayInfo(0, 1.0f)
        }

        // 지난 생일 (Growth Bar의 시작점)
        val lastBirthday = nextBirthday.minusYears(1)

        val totalDaysInCycle = java.time.temporal.ChronoUnit.DAYS.between(lastBirthday, nextBirthday)
        val daysPassed = java.time.temporal.ChronoUnit.DAYS.between(lastBirthday, today)
        val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, nextBirthday)

        val progress = daysPassed.toFloat() / totalDaysInCycle.toFloat()

        BirthdayInfo(daysUntil, progress.coerceIn(0f, 1f))
    } catch (e: Exception) {
        Log.e("DateCalculation", "Failed to calculate birthday info: $birthDate", e)
        BirthdayInfo(0, 0f)
    }
}

/**
 * [Empty State] EmptyBabyState
 * 아기 데이터가 하나도 없을 때 보여주는 컴포넌트입니다.
 */
@Composable
fun EmptyBabyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("아기를 등록해주세요!", color = Color.Gray)
    }
}

/**
 * [Empty State] EmptyTimelineState
 * 타임라인(사진)이 비어있을 때 사용자에게 업로드를 유도하는 안내 문구를 표시합니다.
 */
@Composable
fun EmptyTimelineState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "사진 속에 자라는 우리 아이,\nAI가 성장의 기록을\n앨범으로 담아드려요.",
            textAlign = TextAlign.Center,
            color = Color.Gray,
        )
    }
}



