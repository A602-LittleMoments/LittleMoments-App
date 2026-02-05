package com.a602.commonproject.feature.baby

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.component.ProfileFullAstronaut
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.feature.baby.components.MemorableMoments
import com.a602.commonproject.model.data.Baby
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.SecondaryScrollableTabRow
import com.a602.commonproject.designsystem.component.LMNavigationDefaults.NavigationBarHeight
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color6
import com.a602.commonproject.designsystem.theme.main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme


/**
 * [BabyRoute] Composable
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
fun BabyRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onNotificationClick: () -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToAddBaby: () -> Unit,
    onNavigateToEditBaby: (String) -> Unit,
    onNavigateToGallery: (String, Int) -> Unit,
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
            BabyScreen(
                uiState = state,
                onNotificationClick = onNotificationClick,
                onNavigateToUpload = onNavigateToUpload,
                onNavigateToAddBaby = onNavigateToAddBaby,
                onNavigateToEditBaby = onNavigateToEditBaby,
                onNavigateToGallery = onNavigateToGallery,
                // ViewModel 함수는 여기서 직접 전달하거나 람다로 래핑해서 전달할 수 있습니다.
                // 여기서는 탭 변경 로직 등을 위해 ViewModel 인스턴스를 주입했습니다.
            )
        }
    }
    // 디버깅용 로그: 상태 변화를 Logcat에서 추적하기 위함
    Log.d("uiState", "HomeRoute: $uiState")
}

/**
 * [BabyScreen] Composable
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BabyScreen(
    uiState: HomeUiState.Success,
    onNotificationClick: () -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToAddBaby: () -> Unit,
    onNavigateToEditBaby: (String) -> Unit,
    onNavigateToGallery: (String, Int) -> Unit, // babyId, year
    viewModel: HomeViewModel = hiltViewModel(),
) {
    // region [UI State Definitions]
    val pagerState = rememberPagerState(
        initialPage = uiState.selectedBabyIndex,
        pageCount = { uiState.babies.size },
    )
    val coroutineScope = rememberCoroutineScope()
    // endregion

    val currentBaby = if (uiState.babies.isNotEmpty()) {
        uiState.babies.getOrNull(pagerState.currentPage)
    } else null



    // region [Side Effects - State Synchronization]
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != uiState.selectedBabyIndex) {
            viewModel.updateSelectedBaby(pagerState.currentPage)
        }
    }

    // region [Layout Structure]
    Scaffold(
        containerColor = Color.Transparent, // 배경 이미지를 위해 투명
        modifier = Modifier
            .fillMaxSize()
            // .padding(bottom = NavigationBarHeight) // [Fix] Removed to allow background behind nav bar
            // .navigationBarsPadding() // [Fix] Removed to allow background behind nav bar
            // .statusBarsPadding() // [Fix] Removed to allow background behind status bar
    ) { paddingValues ->

        // Background Image
        Box(Modifier.fillMaxSize()) {
             Image(
                painter = painterResource(id = R.drawable.baby_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // 꽉 차게
            )

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(bottom = NavigationBarHeight + 16.dp) // Ensure content clears Nav Bar
            ) {
                // 1. [아기 프로필 섹션] (Edit 버튼 포함)
                Box(modifier = Modifier.fillMaxWidth()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth(),
                    ) { page ->
                        BabyProfileSection(baby = uiState.babies[page])
                    }

                    // Edit Button (Top Right)
                    // [Fix] Enhanced visibility with background and border because user said it's hard to see
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(48.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)) // Semi-transparent glass effect
                            .border(1.dp, Color.White.copy(alpha = 0.5f), androidx.compose.foundation.shape.CircleShape)
                            .clickable { currentBaby?.let { onNavigateToEditBaby(it.babyId) } },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = LMicons.Edit, // Keeping same icon but now framing it
                            contentDescription = "Edit Baby",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp) // adjusted inner icon size
                        )
                    }
                }

                if (uiState.babies.isEmpty()) {
                    EmptyBabyState()
                }

                // 2. [아기 선택 & 추가 섹션] (Tabs + Add Button)
                BabySelectionSection(
                    uiState = uiState,
                    pagerState = pagerState,
                    coroutineScope = coroutineScope,
                    onAddBabyClick = onNavigateToAddBaby
                )

                // [Fix] Added Spacer below TabRow as requested
                Spacer(modifier = Modifier.height(60.dp)) // Increased spacing (24dp -> 60dp) to prevent overlap

                // 3. [발자국 필터 섹션]
                // Spacer(Modifier.weight(1f)) // Optional: Push footprints to bottom if needed, but user didn't specify.
                FootprintFilterSection(
                    onYearSelected = { yearOffset ->
                        val currentYear = java.time.LocalDate.now().year
                        val targetYear = currentYear - yearOffset
                        currentBaby?.let { baby ->
                             onNavigateToGallery(baby.babyId, targetYear)
                        }
                    }
                )
            }
        }

    }
}

@Composable
fun BabySelectionSection(
    uiState: HomeUiState.Success,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    onAddBabyClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (uiState.babies.size >= 1) { // 1명 이상이면 탭 표시
            SecondaryScrollableTabRow(
                modifier = Modifier
                    .weight(1f)
                    // [Fix] Added outline border as requested for better visibility
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp)),
                selectedTabIndex = uiState.selectedBabyIndex,
                edgePadding = 12.dp, // Added padding inside the border
                containerColor = Color.White.copy(alpha = 0.1f), // Slight background for glass effect
                contentColor = Color.White,
                divider = {},
                indicator = {}
            ) {
                uiState.babies.forEachIndexed { index, baby ->
                    Tab(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .wrapContentWidth(),
                        selected = uiState.selectedBabyIndex == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = baby.babyName,
                                    // [Fix] Make unselected text visible (LightGray instead of Gray)
                                    color = if (uiState.selectedBabyIndex == index) Color.White else Color.White.copy(alpha = 0.6f),
                                    fontWeight = if (uiState.selectedBabyIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 18.sp,
                                )
                                if (uiState.selectedBabyIndex == index) {
                                    Spacer(modifier = Modifier.height(4.dp)) // Increased spacing
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)   // [Fix] Increased width (20 -> 40)
                                            .height(4.dp)   // [Fix] Increased height (2 -> 4) for visibility
                                            .clip(RoundedCornerShape(2.dp)) // Rounded for niceness
                                            .background(Color.White)
                                    )
                                }
                            }
                        },
                    )
                }
            }
        } else {
            Spacer(Modifier.weight(1f))
        }

        // Add Baby Button
        // [Fix] Applied outline frame style to match TabRow
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(56.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .clickable(onClick = onAddBabyClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Outlined.PersonAdd,
                contentDescription = "Add Baby",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun FootprintFilterSection(
    onYearSelected: (Int) -> Unit // 0: All, 1: 1year ago...
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Adjusted height for horizontal layout
            .padding(bottom = 20.dp)
    ) {
        // 배경은 상위에서 처리됨 (Moon surface included in baby_background)

        // 발자국 배치 (Walking Trail Pattern: Horizontal Left -> Right)
        // [Fix] Removed Rotation
        // [Fix] Adjusted Vertical Spacing (Y offsets)

        // 4년 전 (Far Left) - 가장 먼저 등장
        FootprintItem(
            resId = R.drawable.fourth_footprint,
            label = "4년 전",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(x = (-130).dp, y = (-20).dp), 
            entranceDelay = 0,
            onClick = { onYearSelected(4) }
        )
 
        // 3년 전 (Mid Left)
        FootprintItem(
            resId = R.drawable.third_footprint,
            label = "3년 전",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(x = (-50).dp, y = (-100).dp), 
            entranceDelay = 200,
            onClick = { onYearSelected(3) }
        )
 
        // 2년 전 (Mid Right)
        FootprintItem(
            resId = R.drawable.second_footprint,
            label = "2년 전",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(x = 30.dp, y = (-20).dp), 
            entranceDelay = 400,
            onClick = { onYearSelected(2) }
        )
 
         // 1년 전 (Far Right)
        FootprintItem(
            resId = R.drawable.first_footprint,
            label = "1년 전",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(x = 110.dp, y = (-100).dp), 
            entranceDelay = 600,
            onClick = { onYearSelected(1) }
        )
    }
}

@Composable
fun FootprintItem(
    resId: Int,
    label: String,
    modifier: Modifier = Modifier,
    entranceDelay: Int = 0,
    onClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    // 등장 애니메이션 트리거
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(entranceDelay.toLong())
        isVisible = true
    }

    // 1. 등장 애니메이션 (Scale & Alpha)
    val entranceScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isVisible) 1.0f else 0.0f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "EntranceScale"
    )
    val entranceAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isVisible) 1.0f else 0.0f,
        animationSpec = androidx.compose.animation.core.tween(500),
        label = "EntranceAlpha"
    )

    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // 2. 클릭 시 '딛는' 느낌을 주기 위한 스케일 및 투명도 애니메이션
    val clickScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1.0f, // 더 깊이 눌리도록 조정
        animationSpec = androidx.compose.animation.core.spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy),
        label = "ClickScale"
    )
    val clickAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.6f else 1.0f,
        label = "ClickAlpha"
    )

    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = entranceScale * clickScale
                scaleY = entranceScale * clickScale
                alpha = entranceAlpha * clickAlpha
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, 
                    onClick = onClick
                ),
            contentScale = ContentScale.Fit,
        )
        // [Fix] 글씨를 더 진하게 하고 그림자를 강화하여 시인성 개선
        Text(
            text = label,
            modifier = Modifier.offset(y = (-20).dp), 
            style = MaterialTheme.typography.titleMedium.copy(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black.copy(alpha = 0.8f), 
                    offset = androidx.compose.ui.geometry.Offset(2f, 4f), 
                    blurRadius = 6f 
                )
            ),
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 19.sp 
        )
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
            // [Fix] Increased top padding to lower profile, reduced bottom to avoid pushing layout down
            .padding(top = 50.dp, bottom = 10.dp), // was vertical = 24.dp,
        contentAlignment = Alignment.Center,
    ) {
        // region [Background Decoration - Stars]
        // Box 내에서 Absolute Offset을 사용하여 별들을 배치합니다.
        // rotate() Modifier를 활용해 각기 다른 각도로 회전시켜 자연스러움을 줍니다.

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

        // region [Center Content - Profile & Info]
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // [Custom Component: ProfileFullAstronaut]
            // 아기 얼굴 이미지와 우주복 이미지를 합성하여 보여주는 컴포넌트입니다.
            // 비율(Ratio) 중요: headSize와 bodyWidth의 비율이 자연스러워야 합니다.
            ProfileFullAstronaut(
                selectedImageUri = null, // 로컬 이미지가 없을 경우 null
                remoteImageUrl = baby.imageUrl, // 서버 URL 이미지 사용
                clickableEnabled = false, // 클릭 이벤트 비활성화

             /*   headSize = 170.dp,
                bodyWidth = 110.dp,
                bodyOffsetY = 140.dp,*/
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 아기 이름 텍스트
            Text(
                text = baby.babyName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White // Text Color Changed
            )

            Spacer(modifier = Modifier.height(4.dp))

            // D-Day 텍스트 (예: D+100)
            if (baby.birthDate.isNotEmpty()) {
                Text(
                    text = "D+${calculateDaysSince(baby.birthDate)}",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.8f), // Text Color Changed
                )
            }

            Spacer(modifier = Modifier.height(12.dp))


            val birthdayInfo = getBirthdayInfo(baby.birthDate)
            // [Growth Progress Bar]
            // 성장을 시각적으로 보여주는 프로그레스 바 (가짜 데이터 사용 중)
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp)) // 둥근 모서리 처리
                    .background(Color.White.copy(alpha = 0.3f)), // 배경색 (반투명 흰색)
            ) {
                // 진행률(Value) 표시바

                Box(
                    modifier = Modifier
                        .fillMaxWidth(birthdayInfo.progress) // 계산된 진행률 적용
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color(0xFFFFC107)), // 포인트 컬러 (Amber)
                )
            }
            // 생일 D-Day 텍스트 추가
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "생일까지 D-${birthdayInfo.daysUntil}",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f) // Text Color Changed
            )
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





