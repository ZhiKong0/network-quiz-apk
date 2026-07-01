package com.dz.networkquiz;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.InputType;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity {
    private static final String UPDATE_LOG_TAG = "ExamPrepHandbookUpdater";
    private static final String ALL_TYPES = "全部题型";
    private static final String ALL_CHAPTERS = "全部章节";
    private static final String PREF_WRONG_REQUIRED = "wrong_required_correct";
    private static final String PREF_THEME_MODE = "theme_mode";
    private static final String PREF_UPDATE_REPO_SLUG = "update_repo_slug";
    private static final String PREF_UPDATE_STATUS_TEXT = "update_status_text";
    private static final String PREF_UPDATE_STATUS_VERSION_CODE = "update_status_version_code";
    private static final String PREF_LAST_LAUNCHED_VERSION_CODE = "last_launched_version_code";
    private static final String PREF_UPDATE_PENDING_CLEANUP = "update_pending_cleanup_paths";
    private static final String PREF_PENDING_INSTALL_APK_PATH = "pending_install_apk_path";
    private static final String PREF_PENDING_INSTALL_VERSION_NAME = "pending_install_version_name";
    private static final String PREF_LAST_TYPE_FILTER = "last_type_filter";
    private static final String PREF_LAST_CHAPTER_FILTER = "last_chapter_filter";
    private static final String PREF_LAST_STUDY_MODE = "last_study_mode";
    private static final String PREF_LAST_QUESTION_PREFIX = "last_question_";
    private static final String PREF_CURRENT_COURSE = "current_course";
    private static final String PREF_EXPORT_PROMPT_TEMPLATE = "export_prompt_template";
    private static final String PREF_SUGGESTION_DRAFT = "suggestion_draft";
    private static final String PREF_FLOAT_EXPORT_X = "float_export_x";
    private static final String PREF_FLOAT_EXPORT_Y = "float_export_y";
    private static final String LEGACY_UPDATE_REPO_SLUG = "ZhiKong0/network-quiz-apk";
    private static final String LEGACY_REVIEW_UPDATE_REPO_SLUG = "ZhiKong0/review-baodian-apk";
    private static final String THEME_DARK = "dark";
    private static final String THEME_LIGHT = "light";
    private static final String STUDY_MODE_QUIZ = "quiz";
    private static final String STUDY_MODE_REMEMBER = "remember";
    private static final String STUDY_MODE_WRONG = "wrong";
    private static final String STUDY_MODE_CARD = "card";
    private static final String QUESTION_TYPE_ESSAY = "essay";
    private static final String PREF_LAST_CARD_CHAPTER = "last_card_chapter";
    private static final String COURSE_NETWORK = "network";
    private static final String COURSE_SIGNAL_SYSTEM = "signal_system";
    private static final String COURSE_XI_THOUGHT = "xi_thought";
    private static final String COURSE_DATA_STRUCTURES = "data_structures_pta";
    private static final String COURSE_NETWORK_NAME = "计算机网络";
    private static final String COURSE_SIGNAL_SYSTEM_NAME = "信号与系统";
    private static final String COURSE_XI_THOUGHT_NAME = "习近平思想";
    private static final String COURSE_DATA_STRUCTURES_NAME = "数据结构";
    private static final String DEFAULT_UPDATE_REPO_SLUG = "ZhiKong0/exam-prep-handbook-apk";
    private static final String TAG_MARKDOWN_TABLE_SCROLL = "markdown_table_scroll";
    private static final String TAG_MIND_MAP_BOARD = "mind_map_board";
    private static final String TAG_FLOATING_EXPORT_BUTTON = "floating_export_button";
    private static final String UPDATE_METADATA_NAME = "exam-prep-handbook-update.json";
    private static final String LEGACY_UPDATE_METADATA_NAME = "network_quiz_update.json";
    private static final String RELEASE_APK_NAME = "exam-prep-handbook.apk";
    private static final String LEGACY_RELEASE_APK_NAME = "review-baodian.apk";
    private static final String UPDATE_CACHE_DIR = "updates";
    private static final String UPDATE_INSTALL_ACTION = "com.dz.networkquiz.UPDATE_INSTALL_STATUS";
    private static final String UPDATE_STATUS_NOT_CHECKED = "\u672a\u68c0\u67e5";
    private static final String UPDATE_STATUS_REPO_NOT_CONFIGURED = "\u672a\u914d\u7f6e GitHub \u4ed3\u5e93";
    private static final String UPDATE_STATUS_UPDATED_TO_PREFIX = "\u5df2\u66f4\u65b0\u5230 ";
    private static final int HTTP_TIMEOUT_MS = 15000;
    private static final int FAST_HTTP_TIMEOUT_MS = 6000;
    private static final int DOWNLOAD_CONNECT_TIMEOUT_MS = 8000;
    private static final int DOWNLOAD_READ_TIMEOUT_MS = 15000;
    private static final int DEFAULT_WRONG_REQUIRED = 2;
    private static final int MIN_WRONG_REQUIRED = 1;
    private static final int MAX_WRONG_REQUIRED = 10;
    private static final float MIND_MAP_MIN_SCALE = 0.12f;
    private static final float MIND_MAP_MAX_SCALE = 1.75f;
    private static final float QUESTION_ZOOM_MIN_SCALE = 1.0f;
    private static final float QUESTION_ZOOM_MAX_SCALE = 2.6f;

    private final List<Question> allQuestions = new ArrayList<>();
    private final List<MemoryCard> allMemoryCards = new ArrayList<>();
    private final List<Question> visibleQuestions = new ArrayList<>();
    private final List<MemoryCard> visibleCards = new ArrayList<>();
    private final Set<String> pendingUpdateCleanupPaths = new LinkedHashSet<>();
    private final Set<String> selected = new LinkedHashSet<>();
    private final List<EditText> blankInputs = new ArrayList<>();
    private LinearLayout optionList;
    private LinearLayout imageList;
    private LinearLayout mathStemContainer;
    private FrameLayout pageFrame;
    private FrameLayout rootFrame;
    private ScrollView scrollView;
    private FrameLayout questionZoomFrame;
    private LinearLayout contentContainer;
    private LinearLayout swipePreview;
    private TextView swipePreviewTitle;
    private TextView swipePreviewBody;
    private LinearLayout headerContainer;
    private View menuButton;
    private TextView titleView;
    private LinearLayout metaRowView;
    private TextView metaView;
    private TextView progressPeekView;
    private TextView updateVersionLineView;
    private TextView updateRepoLineView;
    private TextView updateStatusLineView;
    private TextView exportPromptLineView;
    private TextView floatingExportButton;
    private SeekBar questionSeekBar;
    private FrameLayout questionSeekShell;
    private TextView stemView;
    private LinearLayout memoryReasonContainer;
    private LinearLayout feedbackContainer;
    private LinearLayout filterRowView;
    private Button typeFilterButton;
    private Button chapterFilterButton;
    private Button actionButton;
    private LinearLayout bottomNavBar;
    private TextView rememberNavButton;
    private TextView quizNavButton;
    private TextView wrongNavButton;
    private TextView cardsNavButton;
    private FrameLayout sideDrawerOverlay;
    private LinearLayout sideDrawerPanel;
    private Spinner typeSpinner;
    private Spinner chapterSpinner;
    private Button submitButton;
    private Button prevButton;
    private Button nextButton;
    private Button wrongModeButton;
    private Button allModeButton;
    private Button shuffleButton;
    private Button cardModeButton;
    private Button settingsButton;
    private Button updateCheckButton;
    private SharedPreferences prefs;
    private int currentIndex = 0;
    private boolean rememberMode = false;
    private boolean wrongMode = false;
    private boolean submitted = false;
    private boolean filterReady = false;
    private Boolean lastAnswerOk = null;
    private boolean cardMode = false;
    private boolean settingsMode = false;
    private boolean homeMode = true;
    private boolean suggestionsMode = false;
    private boolean sideDrawerOpen = false;
    private String currentCardChapter = null;
    private String typeFilter = ALL_TYPES;
    private String chapterFilter = ALL_CHAPTERS;
    private String currentCourseId = COURSE_NETWORK;
    private int currentCardIndex = 0;
    private boolean cardBackVisible = false;
    private float swipeStartX = 0;
    private float swipeStartY = 0;
    private long lastCardSwipeAtMillis = 0L;
    private int questionSwipeMode = 0;
    private boolean questionPageAnimating = false;
    private int touchSlop = 0;
    private int swipePreviewDelta = 0;
    private String themeMode = THEME_DARK;
    private boolean questionSeekTracking = false;
    private boolean questionSeekSyncing = false;
    private boolean suppressQuestionPageSwipe = false;
    private boolean questionZoomGestureActive = false;
    private boolean markdownTableGestureActive = false;
    private boolean mindMapGestureActive = false;
    private boolean floatingExportGestureActive = false;
    private boolean floatingExportDragging = false;
    private boolean floatingExportPositionReady = false;
    private float floatingExportDownRawX = 0f;
    private float floatingExportDownRawY = 0f;
    private float floatingExportStartX = 0f;
    private float floatingExportStartY = 0f;
    private boolean updateBusy = false;
    private boolean autoUpdateCheckScheduled = false;
    private String updateRepoSlug = "";
    private String updateStatusText = UPDATE_STATUS_NOT_CHECKED;
    private String pendingInstallApkPath = null;
    private String pendingInstallVersionName = null;
    private UpdateInfo lastUpdateInfo = null;
    private BroadcastReceiver updateInstallReceiver;
    private ScaleGestureDetector questionScaleGestureDetector;
    private float questionZoomScale = QUESTION_ZOOM_MIN_SCALE;

    private int BG;
    private int BG_ALT;
    private int PANEL;
    private int PANEL_ELEVATED;
    private int TEXT;
    private int MUTED;
    private int GREEN;
    private int RED;
    private int BLUE;
    private int HIGHLIGHT;
    private int CARD_SURFACE;
    private int CARD_SURFACE_BACK;
    private int CARD_SECTION;
    private int CARD_SECTION_BACK;
    private int CARD_BORDER;
    private int AMBER;
    private int DIVIDER;
    private int GLASS_BAR;
    private int GLASS_STROKE;
    private int GLASS_ACTIVE;
    private int GLASS_ACTIVE_TEXT;
    private int CHIP_FILL;
    private int CHIP_STROKE;
    private int CHIP_ACTIVE_FILL;
    private int CHIP_ACTIVE_TEXT;
    private int PREVIEW_SHEET;
    private int OPTION_FILL;
    private int OPTION_STROKE;
    private int OPTION_SELECTED_FILL;
    private int OPTION_SELECTED_STROKE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("quiz_state", MODE_PRIVATE);
        themeMode = prefs.getString(PREF_THEME_MODE, THEME_LIGHT);
        currentCourseId = normalizeCourseId(prefs.getString(PREF_CURRENT_COURSE, COURSE_NETWORK));
        String savedRepoSlug = prefs.getString(PREF_UPDATE_REPO_SLUG, "");
        updateRepoSlug = ensureDefaultUpdateRepoSlug(savedRepoSlug);
        if (!updateRepoSlug.equals(normalizeRepoSlug(savedRepoSlug))) {
            prefs.edit().putString(PREF_UPDATE_REPO_SLUG, updateRepoSlug).apply();
        }
        updateStatusText = hasUpdateRepoConfig() ? UPDATE_STATUS_NOT_CHECKED : UPDATE_STATUS_REPO_NOT_CONFIGURED;
        restorePendingInstallState();
        loadPendingUpdateCleanupPaths();
        restorePersistedUpdateStatus();
        applyThemePalette();
        touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        questionScaleGestureDetector = new ScaleGestureDetector(this,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScaleBegin(ScaleGestureDetector detector) {
                        questionZoomGestureActive = true;
                        suppressQuestionPageSwipe = true;
                        requestNoIntercept(true);
                        hideQuestionSwipePreview();
                        return true;
                    }

                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        setQuestionZoomScale(questionZoomScale * detector.getScaleFactor());
                        return true;
                    }

                    @Override
                    public void onScaleEnd(ScaleGestureDetector detector) {
                        if (questionZoomScale <= QUESTION_ZOOM_MIN_SCALE + 0.02f) {
                            resetQuestionZoom(false);
                        }
                    }
                });
        ensureUpdateInstallReceiver();
        cleanupUpdateCache(pendingInstallKeepPath());
        loadQuestions();
        loadMemoryCards();
        restoreStudyFilters();
        buildLayout();
        applySystemBars();
        showRestoredStudyMode();
        scheduleAutoUpdateCheck();
    }

    @Override
    public void onBackPressed() {
        if (sideDrawerOpen) {
            hideSideDrawer();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cleanupUpdateCache(pendingInstallKeepPath());
        maybeResumePendingInstall();
        scheduleAutoUpdateCheck();
    }

    @Override
    protected void onDestroy() {
        if (updateInstallReceiver != null) {
            try {
                unregisterReceiver(updateInstallReceiver);
            } catch (Exception ignored) {
            }
            updateInstallReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN
                && cardMode
                && isTouchInsideMindMapBoard(event)) {
            mindMapGestureActive = true;
            suppressQuestionPageSwipe = true;
            requestNoIntercept(true);
        }
        if (action == MotionEvent.ACTION_DOWN
                && isTouchInsideFloatingExportButton(event)) {
            floatingExportGestureActive = true;
            suppressQuestionPageSwipe = true;
            requestNoIntercept(true);
        }
        if (handleQuestionZoomTouch(event)) {
            return true;
        }
        if (action == MotionEvent.ACTION_DOWN
                && isQuestionPageSwipeEnabled()
                && isTouchInsideMarkdownTable(event)) {
            markdownTableGestureActive = true;
            suppressQuestionPageSwipe = true;
        }
        if (!markdownTableGestureActive
                && !mindMapGestureActive
                && !floatingExportGestureActive
                && isQuestionPageSwipeEnabled()
                && handleQuestionPageSwipe(event)) {
            return true;
        }
        boolean handled = super.dispatchTouchEvent(event);
        if (markdownTableGestureActive
                && (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)) {
            markdownTableGestureActive = false;
            suppressQuestionPageSwipe = false;
            requestNoIntercept(false);
        }
        if (mindMapGestureActive
                && (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)) {
            mindMapGestureActive = false;
            suppressQuestionPageSwipe = false;
            requestNoIntercept(false);
        }
        if (floatingExportGestureActive
                && (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)) {
            floatingExportGestureActive = false;
            suppressQuestionPageSwipe = false;
            requestNoIntercept(false);
        }
        if (questionZoomGestureActive
                && (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)) {
            questionZoomGestureActive = false;
            suppressQuestionPageSwipe = false;
            requestNoIntercept(false);
            questionSwipeMode = 0;
        }
        return handled;
    }

    private boolean isQuestionPageSwipeEnabled() {
        return !suppressQuestionPageSwipe
                && !questionZoomGestureActive
                && questionZoomScale <= QUESTION_ZOOM_MIN_SCALE + 0.02f
                && !mindMapGestureActive
                && !floatingExportGestureActive
                && !sideDrawerOpen
                && !homeMode
                && !suggestionsMode
                && !cardMode
                && !settingsMode;
    }

    private boolean isQuestionPageZoomEnabled() {
        return !sideDrawerOpen
                && !homeMode
                && !suggestionsMode
                && !cardMode
                && !settingsMode
                && scrollView != null
                && contentContainer != null
                && !visibleQuestions.isEmpty();
    }

    private boolean handleQuestionZoomTouch(MotionEvent event) {
        if (questionScaleGestureDetector == null || !isQuestionPageZoomEnabled()) {
            return false;
        }
        int action = event.getActionMasked();
        boolean multiTouch = event.getPointerCount() >= 2
                || action == MotionEvent.ACTION_POINTER_DOWN
                || action == MotionEvent.ACTION_POINTER_UP;
        if ((action == MotionEvent.ACTION_POINTER_DOWN
                || (action == MotionEvent.ACTION_MOVE && event.getPointerCount() >= 2))
                && isTouchInsideQuestionContent(event)) {
            questionZoomGestureActive = true;
            suppressQuestionPageSwipe = true;
            questionSwipeMode = 0;
            requestNoIntercept(true);
            hideQuestionSwipePreview();
            if (contentContainer != null) {
                contentContainer.animate().cancel();
            }
            if (scrollView != null) {
                scrollView.animate().cancel();
                scrollView.setTranslationX(0f);
                scrollView.setAlpha(1f);
                scrollView.setScaleX(1f);
                scrollView.setScaleY(1f);
            }
        }
        if (!questionZoomGestureActive) {
            return false;
        }
        questionScaleGestureDetector.onTouchEvent(event);
        if (action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL) {
            questionZoomGestureActive = false;
            suppressQuestionPageSwipe = false;
            requestNoIntercept(false);
            questionSwipeMode = 0;
        }
        return multiTouch || questionZoomScale > QUESTION_ZOOM_MIN_SCALE + 0.02f;
    }

    private boolean isTouchInsideQuestionContent(MotionEvent event) {
        View target = questionZoomFrame != null ? questionZoomFrame : scrollView;
        return target != null
                && target.getVisibility() == View.VISIBLE
                && isRawPointInsideView(target, event.getRawX(), event.getRawY());
    }

    private boolean isTouchInsideMarkdownTable(MotionEvent event) {
        Window window = getWindow();
        if (window == null || window.getDecorView() == null) {
            return false;
        }
        return isTouchInsideTaggedView(window.getDecorView(), event.getRawX(), event.getRawY(),
                TAG_MARKDOWN_TABLE_SCROLL);
    }

    private boolean isTouchInsideMindMapBoard(MotionEvent event) {
        Window window = getWindow();
        if (window == null || window.getDecorView() == null) {
            return false;
        }
        return isTouchInsideTaggedView(window.getDecorView(), event.getRawX(), event.getRawY(),
                TAG_MIND_MAP_BOARD);
    }

    private boolean isTouchInsideFloatingExportButton(MotionEvent event) {
        Window window = getWindow();
        if (window == null || window.getDecorView() == null) {
            return false;
        }
        return isTouchInsideTaggedView(window.getDecorView(), event.getRawX(), event.getRawY(),
                TAG_FLOATING_EXPORT_BUTTON);
    }

    private boolean isTouchInsideTaggedView(View view, float rawX, float rawY, String tag) {
        if (view == null || view.getVisibility() != View.VISIBLE || !isRawPointInsideView(view, rawX, rawY)) {
            return false;
        }
        Object viewTag = view.getTag();
        if (tag.equals(viewTag)) {
            return true;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = group.getChildCount() - 1; i >= 0; i--) {
                if (isTouchInsideTaggedView(group.getChildAt(i), rawX, rawY, tag)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isRawPointInsideView(View view, float rawX, float rawY) {
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return rawX >= location[0]
                && rawX <= location[0] + view.getWidth()
                && rawY >= location[1]
                && rawY <= location[1] + view.getHeight();
    }

    private void applyThemePalette() {
        boolean light = THEME_LIGHT.equals(themeMode);
        if (light) {
            BG = Color.rgb(242, 245, 250);
            BG_ALT = Color.rgb(255, 255, 255);
            PANEL = Color.argb(176, 255, 255, 255);
            PANEL_ELEVATED = Color.argb(228, 255, 255, 255);
            TEXT = Color.rgb(20, 24, 33);
            MUTED = Color.rgb(102, 112, 128);
            GREEN = Color.rgb(42, 184, 108);
            RED = Color.rgb(236, 93, 93);
            BLUE = Color.rgb(64, 108, 238);
            HIGHLIGHT = Color.rgb(249, 240, 211);
            CARD_SURFACE = Color.rgb(255, 255, 255);
            CARD_SURFACE_BACK = Color.rgb(252, 248, 242);
            CARD_SECTION = Color.rgb(246, 248, 252);
            CARD_SECTION_BACK = Color.rgb(248, 244, 237);
            CARD_BORDER = Color.rgb(219, 225, 236);
            AMBER = Color.rgb(208, 147, 28);
            DIVIDER = Color.rgb(230, 235, 242);
            GLASS_BAR = Color.argb(184, 255, 255, 255);
            GLASS_STROKE = Color.argb(112, 196, 207, 225);
            GLASS_ACTIVE = Color.argb(244, 255, 255, 255);
            GLASS_ACTIVE_TEXT = Color.rgb(17, 21, 29);
            CHIP_FILL = Color.argb(140, 255, 255, 255);
            CHIP_STROKE = Color.argb(120, 206, 216, 233);
            CHIP_ACTIVE_FILL = Color.argb(234, 255, 255, 255);
            CHIP_ACTIVE_TEXT = BLUE;
            PREVIEW_SHEET = Color.argb(238, 255, 255, 255);
            OPTION_FILL = Color.argb(248, 255, 255, 255);
            OPTION_STROKE = Color.rgb(224, 229, 238);
            OPTION_SELECTED_FILL = Color.rgb(234, 242, 255);
            OPTION_SELECTED_STROKE = Color.rgb(149, 183, 255);
        } else {
            BG = Color.rgb(17, 20, 28);
            BG_ALT = Color.rgb(34, 39, 53);
            PANEL = Color.argb(150, 34, 40, 54);
            PANEL_ELEVATED = Color.argb(194, 30, 35, 47);
            TEXT = Color.rgb(247, 249, 252);
            MUTED = Color.rgb(174, 181, 194);
            GREEN = Color.rgb(96, 216, 138);
            RED = Color.rgb(243, 113, 113);
            BLUE = Color.rgb(126, 157, 255);
            HIGHLIGHT = Color.rgb(82, 70, 39);
            CARD_SURFACE = Color.rgb(29, 34, 46);
            CARD_SURFACE_BACK = Color.rgb(34, 33, 45);
            CARD_SECTION = Color.rgb(38, 44, 58);
            CARD_SECTION_BACK = Color.rgb(43, 41, 53);
            CARD_BORDER = Color.rgb(67, 78, 100);
            AMBER = Color.rgb(246, 208, 111);
            DIVIDER = Color.rgb(58, 67, 85);
            GLASS_BAR = Color.argb(132, 37, 43, 57);
            GLASS_STROKE = Color.argb(58, 201, 214, 236);
            GLASS_ACTIVE = Color.argb(42, 255, 255, 255);
            GLASS_ACTIVE_TEXT = Color.rgb(250, 251, 253);
            CHIP_FILL = Color.argb(104, 39, 45, 60);
            CHIP_STROKE = Color.argb(82, 115, 129, 155);
            CHIP_ACTIVE_FILL = Color.argb(114, 255, 255, 255);
            CHIP_ACTIVE_TEXT = Color.rgb(242, 246, 255);
            PREVIEW_SHEET = Color.argb(226, 27, 32, 44);
            OPTION_FILL = Color.argb(170, 33, 38, 51);
            OPTION_STROKE = Color.argb(96, 112, 126, 149);
            OPTION_SELECTED_FILL = Color.rgb(45, 56, 86);
            OPTION_SELECTED_STROKE = Color.rgb(124, 157, 255);
        }
    }

    private void applySystemBars() {
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        int visibility = getWindow().getDecorView().getSystemUiVisibility();
        if (THEME_LIGHT.equals(themeMode)) {
            if (Build.VERSION.SDK_INT >= 23) {
                visibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            if (Build.VERSION.SDK_INT >= 26) {
                visibility |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                visibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            if (Build.VERSION.SDK_INT >= 26) {
                visibility &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        }
        getWindow().getDecorView().setSystemUiVisibility(visibility);
    }

    private String normalizeCourseId(String value) {
        if (COURSE_SIGNAL_SYSTEM.equals(value)) return COURSE_SIGNAL_SYSTEM;
        if (COURSE_XI_THOUGHT.equals(value)) return COURSE_XI_THOUGHT;
        if (COURSE_DATA_STRUCTURES.equals(value)) return COURSE_DATA_STRUCTURES;
        return COURSE_NETWORK;
    }

    private String courseName(String courseId) {
        String normalized = normalizeCourseId(courseId);
        if (COURSE_SIGNAL_SYSTEM.equals(normalized)) return COURSE_SIGNAL_SYSTEM_NAME;
        if (COURSE_XI_THOUGHT.equals(normalized)) return COURSE_XI_THOUGHT_NAME;
        if (COURSE_DATA_STRUCTURES.equals(normalized)) return COURSE_DATA_STRUCTURES_NAME;
        return COURSE_NETWORK_NAME;
    }

    private String currentCourseName() {
        return courseName(currentCourseId);
    }

    private boolean isSignalSystemCourse() {
        return COURSE_SIGNAL_SYSTEM.equals(currentCourseId);
    }

    private boolean courseHasMemoryCards(String courseId) {
        String normalized = normalizeCourseId(courseId);
        return COURSE_NETWORK.equals(normalized) || COURSE_DATA_STRUCTURES.equals(normalized);
    }

    private boolean currentCourseHasMemoryCards() {
        return courseHasMemoryCards(currentCourseId);
    }

    private String currentQuestionsAssetName() {
        return questionsAssetName(currentCourseId);
    }

    private String questionsAssetName(String courseId) {
        String normalized = normalizeCourseId(courseId);
        if (COURSE_SIGNAL_SYSTEM.equals(normalized)) return "signal_system_questions.json";
        if (COURSE_XI_THOUGHT.equals(normalized)) return "xi_thought_questions.json";
        if (COURSE_DATA_STRUCTURES.equals(normalized)) return "data_structures_questions.json";
        return "questions.json";
    }

    private String memoryCardsAssetName(String courseId) {
        String normalized = normalizeCourseId(courseId);
        if (COURSE_DATA_STRUCTURES.equals(normalized)) return "data_structures_chapter_cards.json";
        return "chapter_cards.json";
    }

    private String memoryCardOverridesAssetName(String courseId) {
        String normalized = normalizeCourseId(courseId);
        if (COURSE_DATA_STRUCTURES.equals(normalized)) return "data_structures_chapter_card_overrides.json";
        return "chapter_card_overrides.json";
    }

    private String coursePrefName(String base) {
        if (COURSE_NETWORK.equals(currentCourseId)) return base;
        return base + "_" + currentCourseId;
    }

    private int assetQuestionCount(String courseId) {
        if (normalizeCourseId(courseId).equals(currentCourseId)) {
            return allQuestions.size();
        }
        try {
            return new JSONArray(readAssetText(questionsAssetName(courseId))).length();
        } catch (Exception ignored) {
            return 0;
        }
    }

    private int assetChapterCount(String courseId) {
        if (normalizeCourseId(courseId).equals(currentCourseId)) {
            return chapterList().size();
        }
        Set<String> chapters = new LinkedHashSet<>();
        try {
            JSONArray arr = new JSONArray(readAssetText(questionsAssetName(courseId)));
            for (int i = 0; i < arr.length(); i++) {
                String chapter = arr.getJSONObject(i).optString("chapter", "");
                if (chapter.length() > 0) chapters.add(chapter);
            }
        } catch (Exception ignored) {
        }
        return chapters.size();
    }

    private void loadQuestions() {
        allQuestions.clear();
        try {
            String json = readAssetText(currentQuestionsAssetName());
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Question q = new Question();
                q.id = obj.getInt("id");
                q.label = obj.getString("label");
                q.type = obj.getString("type");
                q.typeName = obj.getString("typeName");
                q.stem = obj.getString("stem");
                q.answer = obj.get("answer");
                q.chapter = obj.optString("chapter", "");
                q.knowledge = obj.optString("knowledge", "");
                q.quickExplanation = obj.optString("quickExplanation", "");
                q.knowledgeDetail = obj.optString("knowledgeDetail", "");
                q.explanation = obj.optString("explanation", "");
                q.courseId = currentCourseId;
                JSONArray opts = obj.optJSONArray("options");
                if (opts != null) {
                    for (int j = 0; j < opts.length(); j++) {
                        JSONObject opt = opts.getJSONObject(j);
                        q.options.add(new Option(
                                opt.getString("key"),
                                opt.getString("text"),
                                opt.optString("image", "")
                        ));
                    }
                }
                JSONArray imgs = obj.optJSONArray("images");
                if (imgs != null) {
                    for (int j = 0; j < imgs.length(); j++) {
                        q.images.add(imgs.getString(j));
                    }
                }
                JSONArray stemImgs = obj.optJSONArray("stemImages");
                if (stemImgs != null) {
                    for (int j = 0; j < stemImgs.length(); j++) {
                        q.stemImages.add(stemImgs.getString(j));
                    }
                }
                JSONArray answerImgs = obj.optJSONArray("answerImages");
                if (answerImgs != null) {
                    for (int j = 0; j < answerImgs.length(); j++) {
                        q.answerImages.add(answerImgs.getString(j));
                    }
                }
                q.blankCount = obj.optInt("blankCount", 0);
                allQuestions.add(q);
            }
        } catch (Exception e) {
            Toast.makeText(this, "题库加载失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadMemoryCards() {
        allMemoryCards.clear();
        if (!currentCourseHasMemoryCards()) {
            return;
        }
        try {
            String json = readAssetText(memoryCardsAssetName(currentCourseId));
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                List<String> labels = jsonStringList(obj.optJSONArray("labels"));
                allMemoryCards.add(new MemoryCard(
                        obj.optString("chapter", ""),
                        obj.optString("knowledge", ""),
                        obj.optInt("questionCount", labels.size()),
                        join(labels, "、"),
                        obj.optString("layerHint", ""),
                        obj.optString("chapterMap", ""),
                        obj.optString("typeDistribution", ""),
                        jsonStringList(obj.optJSONArray("eyeLines")),
                        jsonStringList(obj.optJSONArray("selfChecks")),
                        jsonStringList(obj.optJSONArray("corePoints")),
                        jsonStringList(obj.optJSONArray("mustRemember")),
                        jsonStringList(obj.optJSONArray("traps")),
                        jsonStringList(obj.optJSONArray("questionTips")),
                        "",
                        ""));
            }
            applyMemoryCardOverrides();
            ensureAllCardsHaveMindMaps();
        } catch (Exception ignored) {
            allMemoryCards.clear();
        }
    }

    private void applyMemoryCardOverrides() {
        try {
            String json = readAssetText(memoryCardOverridesAssetName(currentCourseId));
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String mode = obj.optString("mode", "replace");
                if ("insert".equals(mode)) {
                    insertMemoryCardOverride(obj);
                } else {
                    replaceMemoryCardOverride(obj);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void replaceMemoryCardOverride(JSONObject obj) {
        int index = findMemoryCardOverrideIndex(obj);
        if (index < 0) return;
        MemoryCard base = allMemoryCards.get(index);
        allMemoryCards.set(index, mergeMemoryCard(base, obj, false));
    }

    private void insertMemoryCardOverride(JSONObject obj) {
        String chapter = obj.optString("chapter", "");
        if (chapter.length() == 0) return;
        int insertAt = allMemoryCards.size();
        for (int i = 0; i < allMemoryCards.size(); i++) {
            MemoryCard card = allMemoryCards.get(i);
            if (chapter.equals(card.chapter)) {
                insertAt = i;
                break;
            }
        }
        MemoryCard card = createInsertedMemoryCard(obj, chapter);
        allMemoryCards.add(insertAt, card);
    }

    private MemoryCard createInsertedMemoryCard(JSONObject obj, String chapter) {
        List<MemoryCard> chapterCards = chapterCardsWithoutOverview(chapter);
        int questionCount = obj.optInt("questionCount", 0);
        if (questionCount <= 0) {
            for (MemoryCard card : chapterCards) {
                questionCount += card.questionCount;
            }
        }
        List<String> labels = jsonStringList(obj.optJSONArray("labels"));
        if (labels.isEmpty()) {
            LinkedHashSet<String> dedup = new LinkedHashSet<>();
            for (MemoryCard card : chapterCards) {
                addDelimitedLabels(dedup, card.labels);
            }
            labels = new ArrayList<>(dedup);
        }
        String chapterMap = obj.optString("chapterMap", "");
        if (chapterMap.length() == 0 && !chapterCards.isEmpty()) {
            chapterMap = chapterCards.get(0).chapterMap;
        }
        return new MemoryCard(
                chapter,
                obj.optString("knowledge", ""),
                questionCount,
                join(labels, "、"),
                obj.optString("layerHint", ""),
                chapterMap,
                obj.optString("typeDistribution", ""),
                jsonStringList(obj.optJSONArray("eyeLines")),
                jsonStringList(obj.optJSONArray("selfChecks")),
                jsonStringList(obj.optJSONArray("corePoints")),
                jsonStringList(obj.optJSONArray("mustRemember")),
                jsonStringList(obj.optJSONArray("traps")),
                jsonStringList(obj.optJSONArray("questionTips")),
                obj.optString("frontMarkdown", ""),
                obj.optString("backMarkdown", ""),
                true,
                obj.optString("mindMapTitle", ""),
                jsonMindMapNodes(obj.optJSONArray("mindMapNodes")));
    }

    private MemoryCard mergeMemoryCard(MemoryCard base, JSONObject obj, boolean overviewCard) {
        return new MemoryCard(
                emptyFallback(obj.optString("chapter", ""), base.chapter),
                emptyFallback(obj.optString("knowledge", ""), base.knowledge),
                obj.optInt("questionCount", base.questionCount),
                join(jsonStringListOrFallback(obj.optJSONArray("labels"), base.labels), "、"),
                emptyFallback(obj.optString("layerHint", ""), base.layerHint),
                emptyFallback(obj.optString("chapterMap", ""), base.chapterMap),
                emptyFallback(obj.optString("typeDistribution", ""), base.typeDistribution),
                listFallback(jsonStringList(obj.optJSONArray("eyeLines")), base.eyeLines),
                listFallback(jsonStringList(obj.optJSONArray("selfChecks")), base.selfChecks),
                listFallback(jsonStringList(obj.optJSONArray("corePoints")), base.corePoints),
                listFallback(jsonStringList(obj.optJSONArray("mustRemember")), base.mustRemember),
                listFallback(jsonStringList(obj.optJSONArray("traps")), base.traps),
                listFallback(jsonStringList(obj.optJSONArray("questionTips")), base.questionTips),
                emptyFallback(obj.optString("frontMarkdown", ""), base.frontMarkdown),
                emptyFallback(obj.optString("backMarkdown", ""), base.backMarkdown),
                overviewCard || base.overviewCard,
                emptyFallback(obj.optString("mindMapTitle", ""), base.mindMapTitle),
                mindMapFallback(jsonMindMapNodes(obj.optJSONArray("mindMapNodes")), base.mindMapNodes));
    }

    private int findMemoryCardOverrideIndex(JSONObject obj) {
        String chapter = obj.optString("chapter", "");
        String knowledge = obj.optString("knowledge", "");
        String matchLabel = obj.optString("matchLabel", "");
        for (int i = 0; i < allMemoryCards.size(); i++) {
            MemoryCard card = allMemoryCards.get(i);
            if (chapter.length() > 0 && !chapter.equals(card.chapter)) continue;
            if (knowledge.length() > 0 && !knowledge.equals(card.knowledge)) continue;
            if (matchLabel.length() > 0 && !containsDelimitedLabel(card.labels, matchLabel)) continue;
            return i;
        }
        return -1;
    }

    private boolean containsDelimitedLabel(String labels, String expected) {
        if (labels == null || labels.length() == 0) return false;
        String[] parts = labels.split("、");
        for (String part : parts) {
            if (expected.equals(part.trim())) return true;
        }
        return false;
    }

    private List<String> jsonStringListOrFallback(JSONArray arr, String delimitedFallback) {
        List<String> values = jsonStringList(arr);
        if (!values.isEmpty()) return values;
        LinkedHashSet<String> dedup = new LinkedHashSet<>();
        addDelimitedLabels(dedup, delimitedFallback);
        return new ArrayList<>(dedup);
    }

    private void addDelimitedLabels(Set<String> target, String labels) {
        if (labels == null || labels.trim().length() == 0) return;
        String[] parts = labels.split("、");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.length() > 0) {
                target.add(trimmed);
            }
        }
    }

    private List<String> listFallback(List<String> candidate, List<String> fallback) {
        return candidate == null || candidate.isEmpty() ? new ArrayList<>(fallback) : candidate;
    }

    private List<MindMapNode> mindMapFallback(List<MindMapNode> candidate, List<MindMapNode> fallback) {
        return candidate == null || candidate.isEmpty() ? new ArrayList<>(fallback) : candidate;
    }

    private String emptyFallback(String candidate, String fallback) {
        return candidate == null || candidate.trim().length() == 0 ? fallback : candidate;
    }

    private List<MindMapNode> jsonMindMapNodes(JSONArray arr) {
        List<MindMapNode> nodes = new ArrayList<>();
        if (arr == null) return nodes;
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.optJSONObject(i);
            if (obj == null) continue;
            nodes.add(new MindMapNode(
                    obj.optString("title", ""),
                    obj.optString("summary", ""),
                    obj.optString("badge", ""),
                    jsonStringList(obj.optJSONArray("points")),
                    jsonMindMapNodes(obj.optJSONArray("children"))));
        }
        return nodes;
    }

    private List<MemoryCard> chapterCardsWithoutOverview(String chapter) {
        List<MemoryCard> cards = new ArrayList<>();
        for (MemoryCard card : allMemoryCards) {
            if (chapter.equals(card.chapter) && !card.overviewCard) {
                cards.add(card);
            }
        }
        return cards;
    }

    private void ensureAllCardsHaveMindMaps() {
        for (int i = 0; i < allMemoryCards.size(); i++) {
            MemoryCard card = allMemoryCards.get(i);
            List<MindMapNode> nodes = card.hasMindMap()
                    ? normalizeMindMapNodes(card.mindMapNodes, 0)
                    : buildGeneratedMindMapNodes(card);
            String title = cleanMindMapText(card.mindMapTitle.length() > 0 ? card.mindMapTitle : card.knowledge);
            allMemoryCards.set(i, new MemoryCard(
                    card.chapter,
                    card.knowledge,
                    card.questionCount,
                    card.labels,
                    card.layerHint,
                    card.chapterMap,
                    card.typeDistribution,
                    card.eyeLines,
                    card.selfChecks,
                    card.corePoints,
                    card.mustRemember,
                    card.traps,
                    card.questionTips,
                    card.frontMarkdown,
                    card.backMarkdown,
                    card.overviewCard,
                    title,
                    nodes));
        }
    }

    private List<MindMapNode> buildGeneratedMindMapNodes(MemoryCard card) {
        if (card.usesRichNotebookCard()) {
            List<MindMapNode> markdownNodes = buildMarkdownMindMapNodes(card);
            if (!markdownNodes.isEmpty()) {
                return markdownNodes;
            }
        }

        List<MindMapNode> nodes = new ArrayList<>();
        if (card.layerHint.trim().length() > 0) {
            nodes.add(branchFromText("层次定位", card.layerHint, card.questionCount + "题"));
        }
        if (card.chapterMap.trim().length() > 0) {
            nodes.add(branchFromText("本章地图", card.chapterMap, card.chapter));
        }
        if (card.typeDistribution.trim().length() > 0) {
            nodes.add(branchFromText("题型覆盖", card.typeDistribution, "覆盖面"));
        }
        if (!card.eyeLines.isEmpty()) {
            nodes.add(branchFromList("先抓题眼", "先从题干关键字判断它在考哪一层、哪类协议或哪类设备。", "", card.eyeLines));
        }
        if (!card.selfChecks.isEmpty()) {
            nodes.add(branchFromList("考场三问", "做题前先自问这三句，能大幅减少概念串层。", "", card.selfChecks));
        }
        if (!card.corePoints.isEmpty()) {
            nodes.add(branchFromList("核心知识点", "这部分是本组题最该先吃透的底层知识。", "", card.corePoints));
        }
        if (!card.mustRemember.isEmpty()) {
            nodes.add(branchFromList("必须背会", "常考标准词、固定数字、关键缩写通常都在这里。", "", card.mustRemember));
        }
        if (!card.traps.isEmpty()) {
            nodes.add(branchFromList("易错辨析", "这些地方最容易把相近概念、协议功能和层次定位混在一起。", "", card.traps));
        }
        if (!card.questionTips.isEmpty()) {
            nodes.add(branchFromList("逐题覆盖", "这组分支直接对应这批题常见的出题角度和陷阱。", "", card.questionTips));
        }

        List<String> labelLines = new ArrayList<>();
        LinkedHashSet<String> dedup = new LinkedHashSet<>();
        addDelimitedLabels(dedup, card.labels);
        labelLines.addAll(dedup);
        if (!labelLines.isEmpty()) {
            nodes.add(branchFromList("覆盖题号", "可用题号快速回定位这组题。", card.questionCount + "题", labelLines));
        }

        if (nodes.isEmpty()) {
            nodes.add(branchFromText(card.knowledge, card.chapter + " / " + card.knowledge, card.questionCount + "题"));
        }
        return normalizeMindMapNodes(nodes, 0);
    }

    private List<MindMapNode> buildMarkdownMindMapNodes(MemoryCard card) {
        List<MindMapNode> nodes = new ArrayList<>();
        appendMarkdownMindMapNodes(nodes, card.frontMarkdown);
        appendMarkdownMindMapNodes(nodes, card.backMarkdown);
        return normalizeMindMapNodes(nodes, 0);
    }

    private void appendMarkdownMindMapNodes(List<MindMapNode> target, String markdown) {
        if (target == null || markdown == null || markdown.trim().length() == 0) return;

        List<MindMapDraftNode> drafts = new ArrayList<>();
        MindMapDraftNode currentTop = null;
        MindMapDraftNode currentChild = null;
        StringBuilder introSummary = new StringBuilder();
        List<String> introPoints = new ArrayList<>();

        String[] lines = markdown.replace("\r\n", "\n").replace('\r', '\n').split("\n");
        for (String rawLine : lines) {
            String line = rawLine == null ? "" : rawLine.trim();
            if (line.length() == 0) continue;

            if (line.startsWith("### ")) {
                String rawTitle = cleanMarkdownHeading(line.substring(4));
                currentChild = new MindMapDraftNode(stripHeadingBadge(rawTitle));
                currentChild.badge = extractHeadingBadge(rawTitle);
                if (currentTop == null) {
                    currentTop = new MindMapDraftNode("总览");
                    drafts.add(currentTop);
                }
                currentTop.children.add(currentChild);
                continue;
            }

            if (line.startsWith("## ")) {
                String rawTitle = cleanMarkdownHeading(line.substring(3));
                currentTop = new MindMapDraftNode(stripHeadingBadge(rawTitle));
                currentTop.badge = extractHeadingBadge(rawTitle);
                drafts.add(currentTop);
                currentChild = null;
                continue;
            }

            if (line.startsWith("# ")) {
                String rawTitle = cleanMarkdownHeading(line.substring(2));
                if (drafts.isEmpty() && introSummary.length() == 0 && introPoints.isEmpty()) {
                    introSummary.append(stripHeadingBadge(rawTitle));
                }
                continue;
            }

            if (line.startsWith("- ") || line.startsWith("* ")) {
                String bullet = cleanMindMapText(stripMarkdownDecoration(line.substring(2)));
                if (bullet.length() == 0) continue;
                if (currentChild != null) {
                    currentChild.points.add(bullet);
                } else if (currentTop != null) {
                    currentTop.points.add(bullet);
                } else {
                    introPoints.add(bullet);
                }
                continue;
            }

            String paragraph = cleanMindMapText(stripMarkdownDecoration(line));
            if (paragraph.length() == 0) continue;
            if (currentChild != null) {
                appendDraftSummary(currentChild, paragraph);
            } else if (currentTop != null) {
                appendDraftSummary(currentTop, paragraph);
            } else {
                if (introSummary.length() > 0) introSummary.append(" ");
                introSummary.append(paragraph);
            }
        }

        if (introSummary.length() > 0 || !introPoints.isEmpty()) {
            MindMapDraftNode intro = new MindMapDraftNode("总览");
            intro.badge = "导读";
            intro.summary.append(cleanMindMapText(introSummary.toString()));
            intro.points.addAll(introPoints);
            target.add(draftToMindMapNode(intro));
        }

        for (MindMapDraftNode draft : drafts) {
            target.add(draftToMindMapNode(draft));
        }
    }

    private void appendDraftSummary(MindMapDraftNode node, String text) {
        if (node == null || text == null || text.trim().length() == 0) return;
        if (node.summary.length() > 0) node.summary.append(" ");
        node.summary.append(text.trim());
    }

    private MindMapNode draftToMindMapNode(MindMapDraftNode draft) {
        List<MindMapNode> children = new ArrayList<>();
        for (MindMapDraftNode child : draft.children) {
            children.add(draftToMindMapNode(child));
        }
        return new MindMapNode(
                cleanMindMapText(draft.title),
                cleanMindMapText(draft.summary.toString()),
                cleanMindMapText(draft.badge),
                new ArrayList<>(draft.points),
                children);
    }

    private String cleanMarkdownHeading(String value) {
        String cleaned = stripMarkdownDecoration(value);
        cleaned = cleaned.replaceFirst("^\\d+[\\.、]\\s*", "").trim();
        return cleaned;
    }

    private String stripHeadingBadge(String title) {
        String cleaned = cleanMindMapText(title);
        int left = Math.max(cleaned.lastIndexOf('（'), cleaned.lastIndexOf('('));
        int right = Math.max(cleaned.lastIndexOf('）'), cleaned.lastIndexOf(')'));
        if (left >= 0 && right > left && right == cleaned.length() - 1) {
            return cleaned.substring(0, left).trim();
        }
        return cleaned;
    }

    private String extractHeadingBadge(String title) {
        String cleaned = cleanMindMapText(title);
        int left = Math.max(cleaned.lastIndexOf('（'), cleaned.lastIndexOf('('));
        int right = Math.max(cleaned.lastIndexOf('）'), cleaned.lastIndexOf(')'));
        if (left >= 0 && right > left && right == cleaned.length() - 1) {
            return cleaned.substring(left + 1, right).trim();
        }
        return "";
    }

    private String stripMarkdownDecoration(String value) {
        if (value == null) return "";
        return value
                .replace("**", "")
                .replace("__", "")
                .replace("`", "")
                .replace("~~", "")
                .replace("&nbsp;", " ")
                .trim();
    }

    private List<MindMapNode> normalizeMindMapNodes(List<MindMapNode> nodes, int depth) {
        List<MindMapNode> normalized = new ArrayList<>();
        if (nodes == null) return normalized;
        for (MindMapNode node : nodes) {
            if (node == null) continue;
            normalized.add(normalizeMindMapNode(node, depth));
        }
        return normalized;
    }

    private MindMapNode normalizeMindMapNode(MindMapNode node, int depth) {
        List<String> points = new ArrayList<>();
        if (node.points != null) {
            for (String point : node.points) {
                String cleaned = cleanMindMapText(point);
                if (cleaned.length() > 0) {
                    points.add(cleaned);
                }
            }
        }
        if (points.isEmpty()) {
            List<String> fallback = splitMindMapText(node.summary);
            if (fallback.isEmpty() && node.summary.trim().length() > 0) {
                points.add(cleanMindMapText(node.summary));
            } else {
                points.addAll(fallback);
            }
        }

        List<MindMapNode> children = normalizeMindMapNodes(node.children, depth + 1);
        if (children.isEmpty()) {
            children = autoChildrenFromList(points, depth + 1);
            if (children.isEmpty() && node.summary.trim().length() > 0) {
                children = autoChildrenFromText(node.summary, depth + 1);
            }
        }

        return new MindMapNode(
                cleanMindMapText(node.title),
                cleanMindMapText(node.summary),
                cleanMindMapText(node.badge),
                points,
                children);
    }

    private MindMapNode branchFromText(String title, String text, String badge) {
        String summary = cleanMindMapText(text);
        List<String> parts = splitMindMapText(summary);
        List<String> points = new ArrayList<>();
        if (parts.isEmpty()) {
            if (summary.length() > 0) {
                points.add(summary);
            }
        } else {
            points.addAll(parts);
        }
        return new MindMapNode(
                title,
                summary,
                badge,
                points,
                autoChildrenFromList(points, 1));
    }

    private MindMapNode branchFromList(String title, String summary, String badge, List<String> items) {
        List<String> cleaned = new ArrayList<>();
        for (String item : items) {
            String value = cleanMindMapText(item);
            if (value.length() > 0) {
                cleaned.add(value);
            }
        }
        return new MindMapNode(
                title,
                cleanMindMapText(summary),
                badge,
                cleaned,
                autoChildrenFromList(cleaned, 1));
    }

    private List<MindMapNode> autoChildrenFromText(String text, int depth) {
        return autoChildrenFromList(splitMindMapText(text), depth);
    }

    private List<MindMapNode> autoChildrenFromList(List<String> items, int depth) {
        List<MindMapNode> children = new ArrayList<>();
        if (items == null || items.isEmpty() || depth > 2) return children;
        for (String item : items) {
            String cleaned = cleanMindMapText(item);
            if (cleaned.length() == 0) continue;
            List<String> fragments = splitMindMapText(cleaned);
            List<String> childPoints = new ArrayList<>();
            if (fragments.size() <= 1) {
                childPoints.add(cleaned);
            } else {
                childPoints.addAll(fragments);
            }
            List<MindMapNode> grandChildren = new ArrayList<>();
            if (depth < 2 && fragments.size() > 1) {
                for (String fragment : fragments) {
                    String part = cleanMindMapText(fragment);
                    if (part.length() == 0 || part.equals(cleaned)) continue;
                    grandChildren.add(new MindMapNode(
                            shortenMindMapTitle(part),
                            part,
                            "",
                            Collections.singletonList(part),
                            new ArrayList<MindMapNode>()));
                }
            }
            children.add(new MindMapNode(
                    shortenMindMapTitle(cleaned),
                    cleaned,
                    "",
                    childPoints,
                    grandChildren));
        }
        return children;
    }

    private List<String> splitMindMapText(String text) {
        List<String> result = new ArrayList<>();
        String normalized = cleanMindMapText(text);
        if (normalized.length() == 0) return result;

        String prepared = normalized.replace('\n', '；').replace('\r', '；');
        String[] firstPass = prepared.split("[；;。！？!?]");
        for (String part : firstPass) {
            String cleaned = cleanMindMapText(part);
            if (cleaned.length() == 0) continue;
            if (cleaned.indexOf('、') >= 0 && cleaned.length() > 8) {
                String[] secondPass = cleaned.split("[、]");
                int added = 0;
                for (String child : secondPass) {
                    String fragment = cleanMindMapText(child);
                    if (fragment.length() == 0) continue;
                    result.add(fragment);
                    added++;
                }
                if (added > 1) {
                    continue;
                }
                if (added == 1) {
                    result.remove(result.size() - 1);
                }
            }
            result.add(cleaned);
        }

        if (result.isEmpty()) {
            result.add(normalized);
        }
        return result;
    }

    private String cleanMindMapText(String text) {
        if (text == null) return "";
        String value = text.replace("\r\n", "\n").replace('\r', '\n').trim();
        value = value.replaceAll("^[\\-•·\\d\\s\\.\\)\\(]+", "");
        value = value.replaceAll("\\s+", " ").trim();
        return value;
    }

    private String shortenMindMapTitle(String text) {
        String value = cleanMindMapText(text);
        if (value.length() == 0) return "要点";
        int colon = value.indexOf('：');
        if (colon < 0) colon = value.indexOf(':');
        if (colon > 0 && colon <= 12) {
            value = value.substring(0, colon).trim();
        }
        int comma = value.indexOf('，');
        if (comma < 0) comma = value.indexOf(',');
        if (comma > 0 && comma <= 18) {
            value = value.substring(0, comma).trim();
        }
        if (value.length() > 18) {
            value = value.substring(0, 18).trim() + "…";
        }
        return value;
    }

    private List<String> jsonStringList(JSONArray arr) {
        List<String> values = new ArrayList<>();
        if (arr == null) return values;
        for (int i = 0; i < arr.length(); i++) {
            String value = arr.optString(i, "").trim();
            if (value.length() > 0) {
                values.add(value);
            }
        }
        return values;
    }

    private String readAssetText(String path) throws Exception {
        InputStream in = getAssets().open(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }
        reader.close();
        return sb.toString();
    }

    private void buildLayout() {
        rootFrame = new FrameLayout(this);
        rootFrame.setBackground(appBackground());
        setContentView(rootFrame);

        LinearLayout shell = new LinearLayout(this);
        shell.setOrientation(LinearLayout.VERTICAL);
        shell.setPadding(dp(18), statusBarInset() + dp(2), dp(18), 0);
        rootFrame.addView(shell, new FrameLayout.LayoutParams(-1, -1));

        headerContainer = new LinearLayout(this);
        headerContainer.setOrientation(LinearLayout.VERTICAL);
        shell.addView(headerContainer, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout infoCard = new LinearLayout(this);
        infoCard.setOrientation(LinearLayout.VERTICAL);
        infoCard.setPadding(dp(16), dp(9), dp(16), dp(9));
        infoCard.setBackground(headerPanelBackground());
        infoCard.setElevation(dp(8));
        headerContainer.addView(infoCard, new LinearLayout.LayoutParams(-1, -2));

        FrameLayout titleRow = new FrameLayout(this);
        titleRow.setMinimumHeight(dp(30));
        infoCard.addView(titleRow, new LinearLayout.LayoutParams(-1, -2));

        menuButton = new MenuIconButton(this);
        menuButton.setContentDescription("打开导航菜单");
        menuButton.setBackground(menuButtonBackground());
        menuButton.setClickable(true);
        menuButton.setFocusable(true);
        installPressFeedback(menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSideDrawer();
            }
        });
        titleRow.addView(menuButton, new FrameLayout.LayoutParams(dp(44), dp(44), Gravity.END | Gravity.CENTER_VERTICAL));

        titleView = text("", 17, BLUE, true);
        titleView.setIncludeFontPadding(false);
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        titleView.setGravity(Gravity.CENTER);
        titleView.setPadding(dp(16), dp(6), dp(16), dp(6));
        titleRow.addView(titleView, new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER));

        metaRowView = new LinearLayout(this);
        metaRowView.setOrientation(LinearLayout.HORIZONTAL);
        metaRowView.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams metaRowLp = new LinearLayout.LayoutParams(-1, -2);
        metaRowLp.topMargin = dp(6);
        infoCard.addView(metaRowView, metaRowLp);

        progressPeekView = text("", 10, MUTED, true);
        progressPeekView.setIncludeFontPadding(false);
        progressPeekView.setGravity(Gravity.CENTER);
        progressPeekView.setSingleLine(true);
        progressPeekView.setPadding(dp(9), dp(4), dp(9), dp(4));
        LinearLayout.LayoutParams progressLp = new LinearLayout.LayoutParams(-2, -2);
        progressLp.rightMargin = dp(8);
        metaRowView.addView(progressPeekView, progressLp);

        metaView = text("", 11, MUTED, false);
        metaView.setIncludeFontPadding(false);
        metaView.setSingleLine(true);
        metaView.setEllipsize(TextUtils.TruncateAt.END);
        metaView.setGravity(Gravity.CENTER_VERTICAL);
        metaRowView.addView(metaView, new LinearLayout.LayoutParams(0, -2, 1f));

        questionSeekBar = new SeekBar(this);
        questionSeekBar.setSplitTrack(false);
        questionSeekBar.setPadding(0, 0, 0, 0);
        questionSeekBar.setMinHeight(dp(24));
        questionSeekBar.setMaxHeight(dp(24));
        questionSeekBar.setScaleY(0.72f);
        questionSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    suppressQuestionPageSwipe = true;
                    requestNoIntercept(true);
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    suppressQuestionPageSwipe = false;
                    requestNoIntercept(false);
                }
                return false;
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            questionSeekBar.setProgressTintList(ColorStateList.valueOf(BLUE));
            questionSeekBar.setProgressBackgroundTintList(ColorStateList.valueOf(
                    THEME_LIGHT.equals(themeMode) ? CARD_BORDER : CHIP_STROKE));
            questionSeekBar.setThumbTintList(ColorStateList.valueOf(BLUE));
        }
        questionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (questionSeekSyncing) return;
                updateQuestionSeekPreview(progress, fromUser || questionSeekTracking);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                questionSeekTracking = true;
                updateQuestionSeekPreview(seekBar.getProgress(), true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                questionSeekTracking = false;
                if (visibleQuestions.isEmpty()) {
                    syncQuestionSeekBar();
                    return;
                }
                int target = clampIndex(seekBar.getProgress(), visibleQuestions.size());
                if (target != currentIndex) {
                    currentIndex = target;
                    renderQuestion();
                    if (scrollView != null) {
                        scrollView.scrollTo(0, 0);
                    }
                } else {
                    syncQuestionSeekBar();
                }
            }
        });
        questionSeekShell = new FrameLayout(this);
        questionSeekShell.setPadding(dp(10), dp(2), dp(10), dp(2));
        questionSeekShell.setBackground(headerSeekShellBackground());
        LinearLayout.LayoutParams seekShellLp = new LinearLayout.LayoutParams(-1, dp(28));
        seekShellLp.topMargin = dp(6);
        infoCard.addView(questionSeekShell, seekShellLp);
        questionSeekShell.addView(questionSeekBar, new FrameLayout.LayoutParams(-1, dp(22), Gravity.CENTER));

        buildFilterControls(headerContainer);

        pageFrame = new FrameLayout(this);
        shell.addView(pageFrame, new LinearLayout.LayoutParams(-1, 0, 1));

        swipePreview = new LinearLayout(this);
        swipePreview.setOrientation(LinearLayout.VERTICAL);
        swipePreview.setGravity(Gravity.CENTER);
        swipePreview.setPadding(dp(8), dp(18), dp(8), bottomSafeInset() + dp(118));
        swipePreview.setBackground(appBackground());
        swipePreview.setVisibility(View.GONE);

        LinearLayout previewCard = new LinearLayout(this);
        previewCard.setOrientation(LinearLayout.VERTICAL);
        previewCard.setPadding(dp(24), dp(20), dp(24), dp(20));
        previewCard.setBackground(roundedStrokeBackground(PREVIEW_SHEET, GLASS_STROKE, 30, 1));
        previewCard.setElevation(dp(4));
        swipePreviewTitle = text("", 12, AMBER, true);
        swipePreviewBody = text("", 22, TEXT, true);
        swipePreviewBody.setLineSpacing(dp(5), 1.0f);
        previewCard.addView(swipePreviewTitle, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout.LayoutParams previewBodyParams = new LinearLayout.LayoutParams(-1, -2);
        previewBodyParams.topMargin = dp(12);
        previewCard.addView(swipePreviewBody, previewBodyParams);
        swipePreview.addView(previewCard, new LinearLayout.LayoutParams(-1, -2));
        pageFrame.addView(swipePreview, new FrameLayout.LayoutParams(-1, -1));

        scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.TRANSPARENT);
        scrollView.setClipToPadding(false);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollView.setPadding(0, dp(8), 0, bottomSafeInset() + dp(184));
        installSwipeNavigation(scrollView);
        pageFrame.addView(scrollView, new FrameLayout.LayoutParams(-1, -1));

        questionZoomFrame = new FrameLayout(this);
        questionZoomFrame.setClipChildren(false);
        questionZoomFrame.setClipToPadding(false);
        scrollView.addView(questionZoomFrame, new ScrollView.LayoutParams(-1, -2));

        contentContainer = new LinearLayout(this);
        contentContainer.setOrientation(LinearLayout.VERTICAL);
        contentContainer.setPadding(0, 0, 0, dp(24));
        contentContainer.setPivotX(0f);
        contentContainer.setPivotY(0f);
        questionZoomFrame.addView(contentContainer, new FrameLayout.LayoutParams(-1, -2));

        stemView = text("", 24, TEXT, true);
        stemView.setLineSpacing(dp(6), 1.0f);
        installQuestionPageSwipeProxy(stemView);
        contentContainer.addView(stemView, new LinearLayout.LayoutParams(-1, -2));

        mathStemContainer = new LinearLayout(this);
        mathStemContainer.setOrientation(LinearLayout.VERTICAL);
        mathStemContainer.setVisibility(View.GONE);
        installQuestionPageSwipeProxy(mathStemContainer);
        contentContainer.addView(mathStemContainer, new LinearLayout.LayoutParams(-1, -2));

        imageList = new LinearLayout(this);
        imageList.setOrientation(LinearLayout.VERTICAL);
        imageList.setPadding(0, dp(14), 0, 0);
        installQuestionPageSwipeProxy(imageList);
        contentContainer.addView(imageList, new LinearLayout.LayoutParams(-1, -2));

        optionList = new LinearLayout(this);
        optionList.setOrientation(LinearLayout.VERTICAL);
        optionList.setPadding(0, dp(16), 0, dp(8));
        contentContainer.addView(optionList, new LinearLayout.LayoutParams(-1, -2));

        memoryReasonContainer = new LinearLayout(this);
        memoryReasonContainer.setOrientation(LinearLayout.VERTICAL);
        memoryReasonContainer.setPadding(dp(16), dp(14), dp(16), dp(16));
        memoryReasonContainer.setBackground(roundedStrokeBackground(PANEL_ELEVATED, GLASS_STROKE, 26, 1));
        memoryReasonContainer.setElevation(dp(2));
        memoryReasonContainer.setVisibility(View.GONE);
        installQuestionPageSwipeProxy(memoryReasonContainer);
        contentContainer.addView(memoryReasonContainer, new LinearLayout.LayoutParams(-1, -2));

        submitButton = bigButton("提交", true);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer();
            }
        });
        LinearLayout.LayoutParams inlineSubmitParams = new LinearLayout.LayoutParams(-1, dp(50));
        inlineSubmitParams.bottomMargin = dp(12);
        contentContainer.addView(submitButton, inlineSubmitParams);

        feedbackContainer = new LinearLayout(this);
        feedbackContainer.setOrientation(LinearLayout.VERTICAL);
        feedbackContainer.setPadding(dp(16), dp(14), dp(16), dp(16));
        feedbackContainer.setBackground(roundedStrokeBackground(PANEL_ELEVATED, GLASS_STROKE, 26, 1));
        feedbackContainer.setElevation(dp(2));
        feedbackContainer.setVisibility(View.GONE);
        contentContainer.addView(feedbackContainer, new LinearLayout.LayoutParams(-1, -2));

        bottomNavBar = buildBottomNav();
        FrameLayout.LayoutParams navLp = new FrameLayout.LayoutParams(-1, -2);
        navLp.gravity = Gravity.BOTTOM;
        navLp.leftMargin = dp(18);
        navLp.rightMargin = dp(18);
        navLp.bottomMargin = bottomSafeInset() + dp(12);
        rootFrame.addView(bottomNavBar, navLp);
        buildFloatingExportButton();
        buildSideDrawer();

        prevButton = bigButton("上一个", false);
        nextButton = bigButton("下一个", true);
        filterReady = true;
    }

    private void buildFloatingExportButton() {
        floatingExportButton = text("↗", 19, THEME_LIGHT.equals(themeMode) ? BLUE : Color.WHITE, true);
        floatingExportButton.setTag(TAG_FLOATING_EXPORT_BUTTON);
        floatingExportButton.setContentDescription("导出当前题目");
        floatingExportButton.setGravity(Gravity.CENTER);
        floatingExportButton.setIncludeFontPadding(false);
        floatingExportButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        floatingExportButton.setBackground(floatingExportBackground());
        floatingExportButton.setAlpha(0.48f);
        floatingExportButton.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= 21) {
            floatingExportButton.setElevation(dp(9));
        }
        floatingExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCurrentQuestion();
            }
        });
        floatingExportButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                if (action == MotionEvent.ACTION_DOWN) {
                    floatingExportGestureActive = true;
                    floatingExportDragging = false;
                    suppressQuestionPageSwipe = true;
                    requestNoIntercept(true);
                    floatingExportDownRawX = event.getRawX();
                    floatingExportDownRawY = event.getRawY();
                    floatingExportStartX = v.getX();
                    floatingExportStartY = v.getY();
                    v.setAlpha(0.86f);
                    v.bringToFront();
                    return true;
                }
                if (action == MotionEvent.ACTION_MOVE) {
                    float dx = event.getRawX() - floatingExportDownRawX;
                    float dy = event.getRawY() - floatingExportDownRawY;
                    if (!floatingExportDragging
                            && Math.max(Math.abs(dx), Math.abs(dy)) > Math.max(dp(3), touchSlop / 2)) {
                        floatingExportDragging = true;
                    }
                    if (floatingExportDragging) {
                        placeFloatingExportButton(floatingExportStartX + dx, floatingExportStartY + dy);
                    }
                    return true;
                }
                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    boolean wasDragging = floatingExportDragging;
                    v.setAlpha(0.48f);
                    if (wasDragging) {
                        saveFloatingExportPosition();
                    } else if (action == MotionEvent.ACTION_UP) {
                        v.performClick();
                    }
                    floatingExportDragging = false;
                    floatingExportGestureActive = false;
                    suppressQuestionPageSwipe = false;
                    requestNoIntercept(false);
                    return true;
                }
                return true;
            }
        });
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(dp(46), dp(46));
        lp.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        lp.rightMargin = dp(4);
        rootFrame.addView(floatingExportButton, lp);
    }

    private void refreshFloatingExportButton() {
        if (floatingExportButton == null) return;
        floatingExportButton.setBackground(floatingExportBackground());
        floatingExportButton.setTextColor(THEME_LIGHT.equals(themeMode) ? BLUE : Color.WHITE);
        boolean show = !homeMode
                && !suggestionsMode
                && !cardMode
                && !settingsMode
                && !sideDrawerOpen
                && !visibleQuestions.isEmpty();
        floatingExportButton.setVisibility(show ? View.VISIBLE : View.GONE);
        if (!show) return;
        floatingExportButton.bringToFront();
        if (!floatingExportPositionReady) {
            floatingExportButton.post(new Runnable() {
                @Override
                public void run() {
                    restoreFloatingExportPosition();
                }
            });
        } else {
            floatingExportButton.post(new Runnable() {
                @Override
                public void run() {
                    placeFloatingExportButton(floatingExportButton.getX(), floatingExportButton.getY());
                }
            });
        }
    }

    private void buildSideDrawer() {
        sideDrawerOverlay = new FrameLayout(this);
        sideDrawerOverlay.setVisibility(View.GONE);
        sideDrawerOverlay.setClickable(true);
        sideDrawerOverlay.setAlpha(0f);

        View scrim = new View(this);
        scrim.setBackgroundColor(THEME_LIGHT.equals(themeMode)
                ? Color.argb(88, 18, 24, 38)
                : Color.argb(132, 0, 0, 0));
        scrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSideDrawer();
            }
        });
        sideDrawerOverlay.addView(scrim, new FrameLayout.LayoutParams(-1, -1));

        sideDrawerPanel = new LinearLayout(this);
        sideDrawerPanel.setOrientation(LinearLayout.VERTICAL);
        sideDrawerPanel.setPadding(dp(18), statusBarInset() + dp(18), dp(18), bottomSafeInset() + dp(18));
        sideDrawerPanel.setBackground(drawerPanelBackground());
        sideDrawerPanel.setClickable(true);
        if (Build.VERSION.SDK_INT >= 21) {
            sideDrawerPanel.setElevation(dp(24));
        }
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int drawerWidth = Math.max(dp(282), Math.min(dp(340), screenWidth - dp(42)));
        FrameLayout.LayoutParams panelLp = new FrameLayout.LayoutParams(drawerWidth, -1, Gravity.END);
        sideDrawerOverlay.addView(sideDrawerPanel, panelLp);

        rootFrame.addView(sideDrawerOverlay, new FrameLayout.LayoutParams(-1, -1));
    }

    private void showSideDrawer() {
        if (sideDrawerOverlay == null || sideDrawerPanel == null) return;
        populateSideDrawer();
        sideDrawerOpen = true;
        suppressQuestionPageSwipe = true;
        requestNoIntercept(true);
        refreshFloatingExportButton();
        sideDrawerOverlay.setVisibility(View.VISIBLE);
        sideDrawerOverlay.bringToFront();
        sideDrawerOverlay.setAlpha(0f);
        sideDrawerPanel.setTranslationX(sideDrawerPanel.getWidth() > 0 ? sideDrawerPanel.getWidth() : dp(340));
        sideDrawerOverlay.animate().alpha(1f).setDuration(160).setInterpolator(new DecelerateInterpolator()).start();
        sideDrawerPanel.animate().translationX(0f).setDuration(220).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void hideSideDrawer() {
        if (sideDrawerOverlay == null || sideDrawerPanel == null || !sideDrawerOpen) return;
        sideDrawerOpen = false;
        suppressQuestionPageSwipe = false;
        requestNoIntercept(false);
        int width = sideDrawerPanel.getWidth() > 0 ? sideDrawerPanel.getWidth() : dp(340);
        sideDrawerOverlay.animate().alpha(0f).setDuration(160).setInterpolator(new AccelerateInterpolator()).start();
        sideDrawerPanel.animate()
                .translationX(width)
                .setDuration(180)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (!sideDrawerOpen && sideDrawerOverlay != null) {
                            sideDrawerOverlay.setVisibility(View.GONE);
                            refreshFloatingExportButton();
                        }
                    }
                })
                .start();
    }

    private void populateSideDrawer() {
        if (sideDrawerPanel == null) return;
        sideDrawerPanel.removeAllViews();
        sideDrawerPanel.setBackground(drawerPanelBackground());

        TextView appTitle = text("备考宝典", 22, TEXT, true);
        appTitle.setIncludeFontPadding(false);
        sideDrawerPanel.addView(appTitle, new LinearLayout.LayoutParams(-1, -2));

        TextView courseTitle = text(currentCourseName() + " · " + currentVersionSummary(), 12, MUTED, false);
        courseTitle.setIncludeFontPadding(false);
        LinearLayout.LayoutParams courseLp = new LinearLayout.LayoutParams(-1, -2);
        courseLp.topMargin = dp(6);
        sideDrawerPanel.addView(courseTitle, courseLp);

        addDrawerSection("更多");
        addDrawerRow("课程选择", "切换三门课程与学习进度", BLUE, homeMode, new Runnable() {
            @Override
            public void run() {
                showCoursesHome();
            }
        });
        addDrawerRow("设置", "主题、错题规则、导出提示词、版本更新", GREEN, settingsMode, new Runnable() {
            @Override
            public void run() {
                showSettingsMode();
            }
        });
        addDrawerRow("建议", "复制或分享反馈模板", AMBER, suggestionsMode, new Runnable() {
            @Override
            public void run() {
                showSuggestionsMode();
            }
        });
    }

    private String activeStudyModeForDrawer() {
        if (!homeMode && !settingsMode && !suggestionsMode) {
            return currentStudyModeValue();
        }
        return prefs == null ? STUDY_MODE_QUIZ : prefs.getString(coursePrefName(PREF_LAST_STUDY_MODE), STUDY_MODE_QUIZ);
    }

    private void addDrawerSection(String label) {
        TextView section = text(label, 12, MUTED, true);
        section.setIncludeFontPadding(false);
        section.setLetterSpacing(0.08f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(22);
        lp.bottomMargin = dp(8);
        sideDrawerPanel.addView(section, lp);
    }

    private void addDrawerRow(String title, String body, int accent, boolean active, final Runnable action) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dp(14), dp(11), dp(14), dp(11));
        row.setBackground(drawerRowBackground(active, accent));
        row.setClickable(true);
        row.setFocusable(true);
        installPressFeedback(row);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSideDrawer();
                if (action != null) {
                    v.postDelayed(action, 120);
                }
            }
        });
        TextView titleView = text(title, 15, active ? accent : TEXT, true);
        titleView.setIncludeFontPadding(false);
        row.addView(titleView, new LinearLayout.LayoutParams(-1, -2));
        if (body != null && body.length() > 0) {
            TextView bodyView = text(body, 11, MUTED, false);
            bodyView.setLineSpacing(dp(2), 1.0f);
            LinearLayout.LayoutParams bodyLp = new LinearLayout.LayoutParams(-1, -2);
            bodyLp.topMargin = dp(5);
            row.addView(bodyView, bodyLp);
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(7);
        sideDrawerPanel.addView(row, lp);
    }

    private void restoreFloatingExportPosition() {
        if (rootFrame == null || floatingExportButton == null
                || rootFrame.getWidth() <= 0 || rootFrame.getHeight() <= 0) {
            return;
        }
        float savedX = prefs.getFloat(PREF_FLOAT_EXPORT_X, Float.NaN);
        float savedY = prefs.getFloat(PREF_FLOAT_EXPORT_Y, Float.NaN);
        if (Float.isNaN(savedX) || Float.isNaN(savedY)) {
            savedX = rootFrame.getWidth() - dp(50);
            savedY = statusBarInset() + dp(168);
        }
        placeFloatingExportButton(savedX, savedY);
        floatingExportPositionReady = true;
    }

    private void placeFloatingExportButton(float x, float y) {
        if (rootFrame == null || floatingExportButton == null
                || rootFrame.getWidth() <= 0 || rootFrame.getHeight() <= 0) {
            return;
        }
        int width = Math.max(floatingExportButton.getWidth(), dp(46));
        int height = Math.max(floatingExportButton.getHeight(), dp(46));
        float minX = dp(2);
        float maxX = Math.max(minX, rootFrame.getWidth() - width - dp(2));
        float minY = statusBarInset() + dp(82);
        float maxY = Math.max(minY, rootFrame.getHeight() - height - bottomSafeInset() - dp(92));
        floatingExportButton.setX(clampFloat(x, minX, maxX));
        floatingExportButton.setY(clampFloat(y, minY, maxY));
    }

    private void saveFloatingExportPosition() {
        if (floatingExportButton == null) return;
        prefs.edit()
                .putFloat(PREF_FLOAT_EXPORT_X, floatingExportButton.getX())
                .putFloat(PREF_FLOAT_EXPORT_Y, floatingExportButton.getY())
                .apply();
    }

    private float clampFloat(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private int statusBarInset() {
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            return getResources().getDimensionPixelSize(resId);
        }
        return dp(24);
    }

    private int bottomSafeInset() {
        int resId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resId <= 0) {
            return dp(8);
        }
        int raw = getResources().getDimensionPixelSize(resId);
        return Math.max(dp(8), raw - dp(20));
    }

    private int clampIndex(int index, int size) {
        if (size <= 0) return 0;
        if (index < 0) return 0;
        return Math.min(index, size - 1);
    }

    private void showAllMode() {
        homeMode = false;
        suggestionsMode = false;
        rememberMode = false;
        cardMode = false;
        wrongMode = false;
        settingsMode = false;
        rebuildVisibleQuestions();
        currentIndex = restoredQuestionIndexForActiveGroup();
        renderQuestion();
    }

    private void showRememberMode() {
        homeMode = false;
        suggestionsMode = false;
        rememberMode = true;
        cardMode = false;
        wrongMode = false;
        settingsMode = false;
        rebuildVisibleQuestions();
        currentIndex = restoredQuestionIndexForActiveGroup();
        renderQuestion();
    }

    private void showWrongMode() {
        homeMode = false;
        suggestionsMode = false;
        rememberMode = false;
        cardMode = false;
        wrongMode = true;
        settingsMode = false;
        rebuildVisibleQuestions();
        currentIndex = restoredQuestionIndexForActiveGroup();
        renderQuestion();
    }

    private void showSettingsMode() {
        homeMode = false;
        suggestionsMode = false;
        settingsMode = true;
        rememberMode = false;
        cardMode = false;
        wrongMode = false;
        renderQuestion();
    }

    private void showCoursesHome() {
        homeMode = true;
        suggestionsMode = false;
        settingsMode = false;
        cardMode = false;
        rememberMode = false;
        wrongMode = false;
        renderQuestion();
    }

    private void showSuggestionsMode() {
        homeMode = false;
        suggestionsMode = true;
        settingsMode = false;
        cardMode = false;
        rememberMode = false;
        wrongMode = false;
        renderQuestion();
    }

    private void rebuildVisibleQuestions() {
        visibleQuestions.clear();
        Set<String> wrong = getWrongSet();
        for (Question q : allQuestions) {
            if (wrongMode && !wrong.contains(q.label)) {
                continue;
            }
            if (!ALL_TYPES.equals(typeFilter) && !q.typeName.equals(typeFilter)) {
                continue;
            }
            if (!ALL_CHAPTERS.equals(chapterFilter) && !q.chapter.equals(chapterFilter)) {
                continue;
            }
            visibleQuestions.add(q);
        }
    }

    private void applyFilters() {
        if (!filterReady) return;
        if (homeMode || suggestionsMode || cardMode || settingsMode) return;
        rebuildVisibleQuestions();
        currentIndex = restoredQuestionIndexForActiveGroup();
        renderQuestion();
    }

    private void restoreStudyFilters() {
        String savedType = prefs.getString(coursePrefName(PREF_LAST_TYPE_FILTER), ALL_TYPES);
        typeFilter = typeItems().contains(savedType) ? savedType : ALL_TYPES;
        String savedChapter = prefs.getString(coursePrefName(PREF_LAST_CHAPTER_FILTER), ALL_CHAPTERS);
        chapterFilter = chapterItems().contains(savedChapter) ? savedChapter : ALL_CHAPTERS;
    }

    private void showRestoredStudyMode() {
        String mode = prefs.getString(coursePrefName(PREF_LAST_STUDY_MODE), STUDY_MODE_QUIZ);
        if (STUDY_MODE_REMEMBER.equals(mode)) {
            showRememberMode();
        } else if (STUDY_MODE_WRONG.equals(mode)) {
            showWrongMode();
        } else if (STUDY_MODE_CARD.equals(mode)) {
            showCardMode(prefs.getString(coursePrefName(PREF_LAST_CARD_CHAPTER), null));
        } else {
            showAllMode();
        }
    }

    private String currentStudyModeValue() {
        if (cardMode) return STUDY_MODE_CARD;
        if (rememberMode) return STUDY_MODE_REMEMBER;
        if (wrongMode) return STUDY_MODE_WRONG;
        return STUDY_MODE_QUIZ;
    }

    private String lastQuestionKey(String mode, String type, String chapter) {
        return coursePrefName(PREF_LAST_QUESTION_PREFIX) + mode + "|" + String.valueOf(type) + "|" + String.valueOf(chapter);
    }

    private int restoredQuestionIndexForActiveGroup() {
        if (visibleQuestions.isEmpty()) return 0;
        String label = prefs.getString(lastQuestionKey(currentStudyModeValue(), typeFilter, chapterFilter), "");
        if (label != null && label.length() > 0) {
            for (int i = 0; i < visibleQuestions.size(); i++) {
                if (label.equals(visibleQuestions.get(i).label)) {
                    return i;
                }
            }
        }
        return 0;
    }

    private void persistStudyProgress(String label) {
        if (prefs == null || homeMode || suggestionsMode || cardMode || settingsMode) return;
        String mode = currentStudyModeValue();
        SharedPreferences.Editor editor = prefs.edit()
                .putString(coursePrefName(PREF_LAST_STUDY_MODE), mode)
                .putString(coursePrefName(PREF_LAST_TYPE_FILTER), typeFilter)
                .putString(coursePrefName(PREF_LAST_CHAPTER_FILTER), chapterFilter);
        if (label != null && label.length() > 0) {
            editor.putString(lastQuestionKey(mode, typeFilter, chapterFilter), label);
        }
        editor.apply();
    }

    private void installSwipeNavigation(View target) {
        target.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleSwipeNavigationTouch(target, event);
            }
        });
    }

    private void installQuestionPageSwipeProxy(View target) {
        target.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isQuestionPageSwipeEnabled()) return false;
                return handleQuestionPageSwipe(event);
            }
        });
    }

    private boolean handleSwipeNavigationTouch(View target, MotionEvent event) {
        if (mindMapGestureActive) {
            return false;
        }
        if (homeMode || suggestionsMode || settingsMode) {
            return false;
        }
        if (!cardMode) {
            return handleQuestionPageSwipe(event);
        }
        return handleCardNavigationTouch(target, event, false);
    }

    private boolean handleCardNavigationTouch(View target, MotionEvent event, boolean allowTapFlip) {
        if (mindMapGestureActive) {
            return false;
        }
        if (!cardMode) {
            return handleQuestionPageSwipe(event);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            swipeStartX = event.getX();
            swipeStartY = event.getY();
            feedbackContainer.animate().cancel();
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = event.getX() - swipeStartX;
            float dy = event.getY() - swipeStartY;
            if (Math.abs(dx) > dp(16) && Math.abs(dx) > Math.abs(dy) * 1.2f) {
                float width = Math.max(1, target.getWidth());
                feedbackContainer.setTranslationX(dx * 0.18f);
                feedbackContainer.setAlpha(Math.max(0.84f, 1f - Math.abs(dx) / (width * 1.8f)));
            }
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float dx = event.getX() - swipeStartX;
            float dy = event.getY() - swipeStartY;
            if (allowTapFlip && Math.abs(dx) < dp(12) && Math.abs(dy) < dp(12)) {
                flipCurrentCard();
                return true;
            }
            if (Math.abs(dx) > dp(90) && Math.abs(dx) > Math.abs(dy) * 1.5f) {
                lastCardSwipeAtMillis = System.currentTimeMillis();
                if (dx < 0) {
                    move(1);
                } else {
                    move(-1);
                }
                return true;
            }
            settleCardDrag();
        }
        if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            settleCardDrag();
        }
        return false;
    }

    private boolean handleQuestionPageSwipe(MotionEvent event) {
        if (!isQuestionPageSwipeEnabled()
                || visibleQuestions.isEmpty()
                || contentContainer == null
                || scrollView == null
                || questionPageAnimating) {
            return false;
        }
        int action = event.getActionMasked();
        if (event.getPointerCount() > 1
                || action == MotionEvent.ACTION_POINTER_DOWN
                || action == MotionEvent.ACTION_POINTER_UP) {
            return false;
        }
        if (action == MotionEvent.ACTION_DOWN) {
            swipeStartX = event.getRawX();
            swipeStartY = event.getRawY();
            questionSwipeMode = 0;
            contentContainer.animate().cancel();
            scrollView.animate().cancel();
            hideQuestionSwipePreview();
            return false;
        }

        float dx = event.getRawX() - swipeStartX;
        float dy = event.getRawY() - swipeStartY;
        float absX = Math.abs(dx);
        float absY = Math.abs(dy);
        boolean answerVisible = submitted || feedbackContainer.getVisibility() == View.VISIBLE;
        int slop = Math.max(touchSlop, dp(10));

        if (action == MotionEvent.ACTION_MOVE) {
            if (questionSwipeMode == 0) {
                if (absX < slop && absY < slop) {
                    return false;
                }
                if (isQuestionHorizontalIntent(absX, absY, answerVisible)) {
                    questionSwipeMode = 1;
                } else if (isQuestionVerticalIntent(absX, absY)) {
                    questionSwipeMode = 2;
                } else {
                    return false;
                }
            } else if (questionSwipeMode == 2
                    && absX > dp(70) && absX > absY * 0.72f) {
                questionSwipeMode = 1;
            }
            if (questionSwipeMode == 1) {
                requestNoIntercept(true);
                updateQuestionDragSurface(dx);
                return true;
            }
            return false;
        }

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (questionSwipeMode == 1) {
                requestNoIntercept(false);
                float width = Math.max(1, getWindow().getDecorView().getWidth());
                boolean enoughDistance = absX > Math.max(dp(72), width * 0.13f);
                boolean enoughDirection = absX > absY * 0.72f;
                if (action == MotionEvent.ACTION_UP && enoughDistance && enoughDirection) {
                    moveQuestionAnimated(dx < 0 ? 1 : -1);
                } else {
                    settleQuestionDrag();
                }
                questionSwipeMode = 0;
                return true;
            }
            questionSwipeMode = 0;
        }
        return false;
    }

    private boolean isQuestionHorizontalIntent(float absX, float absY, boolean answerVisible) {
        return absX > dp(22) && absX > absY * 0.72f;
    }

    private boolean isQuestionVerticalIntent(float absX, float absY) {
        return absY > dp(24) && absY > absX * 1.35f;
    }

    private void updateQuestionDragSurface(float dx) {
        if (scrollView == null) return;
        float width = Math.max(dp(320), pageFrame != null ? pageFrame.getWidth() : getWindow().getDecorView().getWidth());
        int delta = dx < 0 ? 1 : -1;
        boolean hasTarget = prepareQuestionSwipePreview(delta);
        float limit = hasTarget ? width * 0.92f : width * 0.22f;
        float dragX = hasTarget ? dx : dx * 0.28f;
        dragX = Math.max(-limit, Math.min(limit, dragX));
        float progress = Math.min(1f, Math.abs(dragX) / (width * 0.72f));

        scrollView.setTranslationX(dragX);
        scrollView.setAlpha(hasTarget ? Math.max(0.82f, 1f - progress * 0.18f) : 0.96f);
        scrollView.setScaleY(hasTarget ? 1f - progress * 0.015f : 0.996f);

        if (swipePreview != null) {
            float previewOffset = (delta > 0 ? width * 0.13f : -width * 0.13f) * (1f - progress);
            swipePreview.setTranslationX(previewOffset);
            swipePreview.setAlpha(hasTarget ? Math.max(0.22f, progress) : 0.36f);
        }
    }

    private boolean prepareQuestionSwipePreview(int delta) {
        if (swipePreview == null || swipePreviewTitle == null || swipePreviewBody == null) return false;
        if (swipePreviewDelta == delta && swipePreview.getVisibility() == View.VISIBLE) {
            int target = currentIndex + delta;
            return target >= 0 && target < visibleQuestions.size();
        }
        swipePreviewDelta = delta;
        int target = currentIndex + delta;
        boolean hasTarget = target >= 0 && target < visibleQuestions.size();
        String side = delta > 0 ? "下一题" : "上一题";
        int textGravity = delta > 0 ? Gravity.RIGHT : Gravity.LEFT;
        swipePreview.setGravity(Gravity.CENTER_VERTICAL | textGravity);
        swipePreviewTitle.setGravity(textGravity);
        swipePreviewBody.setGravity(textGravity);
        if (hasTarget) {
            Question q = visibleQuestions.get(target);
            swipePreviewTitle.setText(side + "  " + (target + 1) + "/" + visibleQuestions.size()
                    + " · " + q.label + " · " + q.typeName);
            swipePreviewBody.setText(shortPreview(q.stem));
        } else {
            swipePreviewTitle.setText(delta > 0 ? "已经是最后一题" : "已经是第一题");
            swipePreviewBody.setText("松手后会回弹");
        }
        swipePreview.setVisibility(View.VISIBLE);
        return hasTarget;
    }

    private void hideQuestionSwipePreview() {
        swipePreviewDelta = 0;
        if (swipePreview != null) {
            swipePreview.animate().cancel();
            swipePreview.setVisibility(View.GONE);
            swipePreview.setTranslationX(0f);
            swipePreview.setAlpha(0f);
        }
    }

    private void resetQuestionPageSurface() {
        if (scrollView != null) {
            scrollView.animate().cancel();
            scrollView.setTranslationX(0f);
            scrollView.setAlpha(1f);
            scrollView.setScaleX(1f);
            scrollView.setScaleY(1f);
        }
        if (contentContainer != null) {
            contentContainer.animate().cancel();
            contentContainer.setTranslationX(0f);
            contentContainer.setAlpha(1f);
        }
    }

    private void setQuestionZoomScale(float scale) {
        questionZoomScale = clampFloat(scale, QUESTION_ZOOM_MIN_SCALE, QUESTION_ZOOM_MAX_SCALE);
        applyQuestionZoom();
    }

    private void applyQuestionZoom() {
        if (contentContainer == null) return;
        contentContainer.setPivotX(0f);
        contentContainer.setPivotY(0f);
        contentContainer.setScaleX(questionZoomScale);
        contentContainer.setScaleY(questionZoomScale);
        scheduleQuestionZoomFrameSync();
    }

    private void scheduleQuestionZoomFrameSync() {
        if (questionZoomFrame == null) return;
        questionZoomFrame.post(new Runnable() {
            @Override
            public void run() {
                syncQuestionZoomFrameSize();
            }
        });
    }

    private void syncQuestionZoomFrameSize() {
        if (questionZoomFrame == null || contentContainer == null) return;
        int measuredHeight = contentContainer.getMeasuredHeight();
        if (measuredHeight <= 0) return;
        ViewGroup.LayoutParams lp = questionZoomFrame.getLayoutParams();
        if (lp == null) return;
        int targetHeight = questionZoomScale <= QUESTION_ZOOM_MIN_SCALE + 0.02f
                ? ViewGroup.LayoutParams.WRAP_CONTENT
                : Math.max(measuredHeight, (int) Math.ceil(measuredHeight * questionZoomScale));
        if (lp.height != targetHeight) {
            lp.height = targetHeight;
            questionZoomFrame.setLayoutParams(lp);
        }
    }

    private void resetQuestionZoom(boolean animated) {
        questionZoomGestureActive = false;
        suppressQuestionPageSwipe = false;
        requestNoIntercept(false);
        questionZoomScale = QUESTION_ZOOM_MIN_SCALE;
        if (contentContainer == null) return;
        contentContainer.animate().cancel();
        if (animated) {
            contentContainer.animate()
                    .scaleX(QUESTION_ZOOM_MIN_SCALE)
                    .scaleY(QUESTION_ZOOM_MIN_SCALE)
                    .setDuration(150)
                    .setInterpolator(new DecelerateInterpolator(1.2f))
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            syncQuestionZoomFrameSize();
                        }
                    })
                    .start();
        } else {
            contentContainer.setScaleX(QUESTION_ZOOM_MIN_SCALE);
            contentContainer.setScaleY(QUESTION_ZOOM_MIN_SCALE);
            syncQuestionZoomFrameSize();
        }
    }

    private String shortPreview(String value) {
        String compact = value == null ? "" : value.replace('\n', ' ').replace('\r', ' ').trim();
        return compact.length() > 74 ? compact.substring(0, 74) + "..." : compact;
    }

    private void requestNoIntercept(boolean disallow) {
        if (scrollView != null) {
            scrollView.requestDisallowInterceptTouchEvent(disallow);
        }
        if (getWindow() == null || getWindow().getDecorView() == null
                || getWindow().getDecorView().getParent() == null) {
            return;
        }
        getWindow().getDecorView().getParent().requestDisallowInterceptTouchEvent(disallow);
    }

    private void buildFilterControls(LinearLayout top) {
        LinearLayout filterRow = new LinearLayout(this);
        filterRowView = filterRow;
        filterRow.setGravity(Gravity.CENTER_VERTICAL);
        filterRow.setPadding(dp(4), dp(3), dp(4), dp(3));
        filterRow.setBackground(filterTrayBackground());
        filterRow.setElevation(dp(3));

        typeFilterButton = filterButton("");
        chapterFilterButton = filterButton("");
        actionButton = filterButton("");

        filterRow.addView(typeFilterButton, new LinearLayout.LayoutParams(0, dp(36), 1));
        LinearLayout.LayoutParams chapterParams = new LinearLayout.LayoutParams(0, dp(36), 1);
        chapterParams.leftMargin = dp(4);
        filterRow.addView(chapterFilterButton, chapterParams);
        LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(0, dp(36), 1);
        actionParams.leftMargin = dp(4);
        filterRow.addView(actionButton, actionParams);
        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(-1, -2);
        rowLp.topMargin = dp(6);
        top.addView(filterRow, rowLp);
    }

    private LinearLayout buildBottomNav() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(12), dp(8), dp(12), dp(8));
        bar.setBackground(navBarBackground());
        bar.setElevation(dp(18));

        rememberNavButton = navButton("记题", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRememberMode();
            }
        });
        quizNavButton = navButton("刷题", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllMode();
            }
        });
        wrongNavButton = navButton("错题", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWrongMode();
            }
        });
        cardsNavButton = navButton("导图", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCardMode(currentCardChapter);
            }
        });

        bar.addView(navSlot(rememberNavButton), new LinearLayout.LayoutParams(0, dp(44), 1));
        LinearLayout.LayoutParams quizLp = new LinearLayout.LayoutParams(0, dp(44), 1);
        quizLp.leftMargin = dp(6);
        bar.addView(navSlot(quizNavButton), quizLp);
        LinearLayout.LayoutParams wrongLp = new LinearLayout.LayoutParams(0, dp(44), 1);
        wrongLp.leftMargin = dp(6);
        bar.addView(navSlot(wrongNavButton), wrongLp);
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(0, dp(44), 1);
        cardLp.leftMargin = dp(6);
        bar.addView(navSlot(cardsNavButton), cardLp);
        return bar;
    }

    private TextView navButton(String label, View.OnClickListener listener) {
        TextView view = text(label, 12, MUTED, true);
        view.setGravity(Gravity.CENTER);
        view.setIncludeFontPadding(false);
        view.setSingleLine(true);
        view.setPadding(dp(12), dp(8), dp(12), dp(8));
        view.setBackground(navPillBackground(false));
        view.setOnClickListener(listener);
        installPressFeedback(view);
        return view;
    }

    private FrameLayout navSlot(View child) {
        FrameLayout slot = new FrameLayout(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, dp(34), Gravity.CENTER);
        slot.addView(child, lp);
        return slot;
    }

    private void updateBottomNav() {
        if (rememberNavButton == null) return;
        boolean onStudyPage = !homeMode && !settingsMode && !suggestionsMode;
        styleNavButton(rememberNavButton, onStudyPage && rememberMode);
        styleNavButton(quizNavButton, onStudyPage && !rememberMode && !wrongMode && !cardMode);
        styleNavButton(wrongNavButton, onStudyPage && wrongMode);
        styleNavButton(cardsNavButton, onStudyPage && cardMode);
    }

    private void styleNavButton(TextView view, boolean active) {
        if (view == null) return;
        view.setTextColor(active ? GLASS_ACTIVE_TEXT : MUTED);
        view.setAlpha(active ? 1f : 0.92f);
        view.setBackground(navPillBackground(active));
    }

    private void refreshChrome() {
        if (titleView == null || metaView == null) return;
        updateBottomNav();
        setHeaderMenuVisible(true);
        if (homeMode) {
            applyHeaderTitleStyle("备考宝典", BLUE);
            clearHeaderTitleAction();
            setMetaRowVisible(false);
            setQuestionSeekVisible(false);
            if (filterRowView != null) filterRowView.setVisibility(View.GONE);
            return;
        }

        if (suggestionsMode) {
            applyHeaderTitleStyle("建议", AMBER);
            clearHeaderTitleAction();
            setMetaRowVisible(false);
            setQuestionSeekVisible(false);
            if (filterRowView != null) filterRowView.setVisibility(View.GONE);
            return;
        }

        if (settingsMode) {
            applyHeaderTitleStyle("设置", GREEN);
            clearHeaderTitleAction();
            setMetaRowVisible(false);
            setQuestionSeekVisible(false);
            if (filterRowView != null) filterRowView.setVisibility(View.GONE);
            return;
        }

        if (cardMode) {
            applyHeaderTitleStyle("导图", AMBER);
            clearHeaderTitleAction();
            setMetaRowVisible(true);
            setQuestionSeekVisible(false);
            if (filterRowView != null) filterRowView.setVisibility(View.VISIBLE);
            bindFilterButton(typeFilterButton, currentCardChapter == null ? "全部章节" : truncate(currentCardChapter, 8), currentCardChapter != null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCardMode(null);
                }
            });
            bindFilterButton(chapterFilterButton, "切换章节", false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showChapterCardsDialog();
                }
            });
            bindFilterActionButton(actionButton, "导出导图", AMBER, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentCardChapter == null) {
                        exportAllChapterCards();
                    } else {
                        exportChapterCard(currentCardChapter);
                    }
                }
            });
            return;
        }

        String title = rememberMode ? "记题" : (wrongMode ? "错题" : "刷题");
        int accent = rememberMode ? AMBER : (wrongMode ? RED : BLUE);
        applyHeaderTitleStyle(title, accent);
        clearHeaderTitleAction();
        setMetaRowVisible(true);
        setQuestionSeekVisible(true);
        if (filterRowView != null) filterRowView.setVisibility(View.VISIBLE);
        bindFilterButton(typeFilterButton, shortFilterLabel("题型", typeFilter, ALL_TYPES), !ALL_TYPES.equals(typeFilter), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeFilterDialog();
            }
        });
        bindFilterButton(chapterFilterButton, shortFilterLabel("章节", chapterFilter, ALL_CHAPTERS), !ALL_CHAPTERS.equals(chapterFilter), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChapterFilterDialog();
            }
        });
        bindFilterActionButton(actionButton, wrongMode ? "导出错题" : "随机抽题", accent, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wrongMode) {
                    exportWrongQuestions();
                } else {
                    rebuildVisibleQuestions();
                    Collections.shuffle(visibleQuestions);
                    currentIndex = 0;
                    renderQuestion();
                }
            }
        });
        syncQuestionSeekBar();
    }

    private void setMetaRowVisible(boolean visible) {
        int state = visible ? View.VISIBLE : View.GONE;
        if (metaRowView != null) {
            metaRowView.setVisibility(state);
        }
        if (metaView != null) {
            metaView.setVisibility(state);
        }
        if (progressPeekView != null) {
            progressPeekView.setVisibility(state);
        }
    }

    private void setHeaderMenuVisible(boolean visible) {
        if (menuButton == null) return;
        menuButton.setBackground(menuButtonBackground());
        menuButton.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (visible) {
            menuButton.bringToFront();
        }
        menuButton.invalidate();
    }

    private void setQuestionSeekVisible(boolean visible) {
        int state = visible ? View.VISIBLE : View.GONE;
        if (questionSeekShell != null) {
            questionSeekShell.setVisibility(state);
        }
        if (questionSeekBar != null) {
            questionSeekBar.setVisibility(state);
        }
    }

    private void clearHeaderTitleAction() {
        titleView.setOnClickListener(null);
        titleView.setClickable(false);
        titleView.setFocusable(false);
        titleView.setContentDescription(null);
    }

    private void applyHeaderTitleStyle(String text, int accent) {
        titleView.setText(text);
        titleView.setTextColor(accent);
        titleView.setBackground(headerTitleBackground(accent));
        progressPeekView.setTextColor(accent);
        progressPeekView.setBackground(headerProgressBackground(accent));
    }
    private void bindChromeButton(Button button, String label, boolean active, View.OnClickListener listener) {
        if (button == null) return;
        button.setText(label);
        styleChromeButton(button, active);
        button.setOnClickListener(listener);
    }

    private void bindFilterButton(Button button, String label, boolean active, View.OnClickListener listener) {
        if (button == null) return;
        button.setText(label + " ▾");
        styleFilterButton(button, active);
        button.setOnClickListener(listener);
    }

    private void bindFilterActionButton(Button button, String label, int accent, View.OnClickListener listener) {
        if (button == null) return;
        button.setText(label);
        styleFilterActionButton(button, accent);
        button.setOnClickListener(listener);
    }

    private String shortFilterLabel(String prefix, String value, String allValue) {
        if (value == null || allValue.equals(value)) {
            return prefix;
        }
        return truncate(value, 8);
    }

    private void showTypeFilterDialog() {
        final List<String> items = typeItems();
        int checked = Math.max(0, items.indexOf(typeFilter));
        showChoiceSheet("选择题型", items, checked, new ChoiceHandler() {
            @Override
            public void onChosen(int which, String item) {
                typeFilter = item;
                applyFilters();
            }
        });
    }
    private void showChapterFilterDialog() {
        final List<String> items = chapterItems();
        int checked = Math.max(0, items.indexOf(chapterFilter));
        showChoiceSheet("选择章节", items, checked, new ChoiceHandler() {
            @Override
            public void onChosen(int which, String item) {
                chapterFilter = item;
                applyFilters();
            }
        });
    }
    private LinearLayout filterRow(String label) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(6), 0, 0);
        TextView labelView = text(label, 14, MUTED, true);
        labelView.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(labelView, new LinearLayout.LayoutParams(dp(44), dp(44)));
        return row;
    }

    private ArrayAdapter<String> spinnerAdapter(List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(TEXT);
                view.setTextSize(12);
                view.setSingleLine(true);
                view.setEllipsize(TextUtils.TruncateAt.END);
                view.setBackgroundColor(PANEL);
                view.setPadding(dp(8), 0, dp(8), 0);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(Color.rgb(24, 26, 32));
                view.setTextSize(15);
                view.setPadding(dp(12), dp(12), dp(12), dp(12));
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private List<String> typeItems() {
        List<String> items = new ArrayList<>();
        items.add(ALL_TYPES);
        Set<String> present = new LinkedHashSet<>();
        for (Question q : allQuestions) {
            if (q.typeName != null && q.typeName.length() > 0) {
                present.add(q.typeName);
            }
        }
        String[] order = new String[] {"判断题", "单选题", "多选题", "填空题", "大题"};
        for (String name : order) {
            if (present.contains(name)) {
                items.add(name);
                present.remove(name);
            }
        }
        items.addAll(present);
        return items;
    }

    private List<String> chapterItems() {
        Set<String> seen = new LinkedHashSet<>();
        for (Question q : allQuestions) {
            if (q.chapter != null && q.chapter.length() > 0) {
                seen.add(q.chapter);
            }
        }
        List<String> chapters = new ArrayList<>(seen);
        Collections.sort(chapters);
        List<String> items = new ArrayList<>();
        items.add(ALL_CHAPTERS);
        items.addAll(chapters);
        return items;
    }

    private String activeFilterText() {
        List<String> parts = new ArrayList<>();
        if (!ALL_TYPES.equals(typeFilter)) {
            parts.add(typeFilter);
        }
        if (!ALL_CHAPTERS.equals(chapterFilter)) {
            parts.add(chapterFilter);
        }
        return parts.isEmpty() ? "全部题型 / 全部章节" : join(parts, " / ");
    }

    private int filteredTotalCount() {
        int count = 0;
        for (Question q : allQuestions) {
            if (!ALL_TYPES.equals(typeFilter) && !q.typeName.equals(typeFilter)) {
                continue;
            }
            if (!ALL_CHAPTERS.equals(chapterFilter) && !q.chapter.equals(chapterFilter)) {
                continue;
            }
            count++;
        }
        return count;
    }

    private int filteredWrongCount() {
        int count = 0;
        Set<String> wrong = getWrongSet();
        for (Question q : allQuestions) {
            if (!wrong.contains(q.label)) {
                continue;
            }
            if (!ALL_TYPES.equals(typeFilter) && !q.typeName.equals(typeFilter)) {
                continue;
            }
            if (!ALL_CHAPTERS.equals(chapterFilter) && !q.chapter.equals(chapterFilter)) {
                continue;
            }
            count++;
        }
        return count;
    }

    private String emptyMessage() {
        if (wrongMode) {
            return "当前筛选下错题本是空的。答错的题会自动加入这里。";
        }
        if (rememberMode) {
            return "当前筛选下没有可记忆的题目，请换一个题型或章节。";
        }
        return "当前筛选下没有题目，请换一个题型或章节。";
    }

    private void refreshMeta(Question q) {
        progressPeekView.setText((currentIndex + 1) + "/" + visibleQuestions.size() + " · " + q.label);
        metaView.setText(q.typeName + " · " + truncate(TextUtils.isEmpty(q.knowledge) ? q.chapter : q.knowledge, 24));
    }

    private void refreshEmptyMeta() {
        int total = wrongMode ? filteredWrongCount() : filteredTotalCount();
        progressPeekView.setText("0/" + total);
        metaView.setText(activeFilterText());
    }

    private void syncQuestionSeekBar() {
        if (questionSeekBar == null || homeMode || suggestionsMode || cardMode || settingsMode) return;
        int size = visibleQuestions.size();
        questionSeekSyncing = true;
        questionSeekBar.setEnabled(size > 1);
        questionSeekBar.setMax(Math.max(0, size - 1));
        questionSeekBar.setProgress(size == 0 ? 0 : clampIndex(currentIndex, size));
        updateQuestionSeekPreview(questionSeekBar.getProgress(), questionSeekTracking);
        questionSeekSyncing = false;
    }

    private void updateQuestionSeekPreview(int progress, boolean dragging) {
        if (progressPeekView == null) return;
        if (visibleQuestions.isEmpty()) {
            progressPeekView.setText("0/0");
            return;
        }
        int target = clampIndex(progress, visibleQuestions.size());
        Question targetQuestion = visibleQuestions.get(target);
        String text = (target + 1) + "/" + visibleQuestions.size() + " · " + targetQuestion.label;
        if (dragging) {
            text = "定位 " + text;
        }
        progressPeekView.setText(text);
        metaView.setText(targetQuestion.typeName + " · "
                + truncate(TextUtils.isEmpty(targetQuestion.knowledge) ? targetQuestion.chapter : targetQuestion.knowledge, 24));
        progressPeekView.setAlpha(dragging ? 1f : 0.9f);
    }

    private void renderQuestion() {
        refreshChrome();
        refreshFloatingExportButton();
        if ((homeMode || suggestionsMode || settingsMode || cardMode) && contentContainer != null) {
            resetQuestionZoom(false);
        }
        if (homeMode) {
            renderCoursesHomePage();
            return;
        }
        if (suggestionsMode) {
            renderSuggestionsPage();
            return;
        }
        if (settingsMode) {
            renderSettingsPage();
            return;
        }
        if (cardMode) {
            renderCardView();
            return;
        }
        if (filterRowView != null) filterRowView.setVisibility(View.VISIBLE);
        stemView.setVisibility(View.VISIBLE);
        if (!questionPageAnimating) {
            resetQuestionPageSurface();
            resetQuestionZoom(false);
            hideQuestionSwipePreview();
        }
        feedbackContainer.setPadding(dp(16), dp(14), dp(16), dp(14));
        feedbackContainer.setOnClickListener(null);
        installQuestionPageSwipeProxy(feedbackContainer);
        stemView.setTextSize(24);
        selected.clear();
        blankInputs.clear();
        submitted = false;
        lastAnswerOk = null;
        optionList.removeAllViews();
        imageList.removeAllViews();
        memoryReasonContainer.removeAllViews();
        memoryReasonContainer.setVisibility(View.GONE);
        feedbackContainer.removeAllViews();
        feedbackContainer.setVisibility(View.GONE);

        if (visibleQuestions.isEmpty()) {
            persistStudyProgress(null);
            if (mathStemContainer != null) {
                mathStemContainer.removeAllViews();
                mathStemContainer.setVisibility(View.GONE);
            }
            stemView.setVisibility(View.VISIBLE);
            stemView.setText(emptyMessage());
            refreshEmptyMeta();
            submitButton.setText("提交");
            submitButton.setEnabled(false);
            submitButton.setVisibility(View.GONE);
            return;
        }

        Question q = currentQuestion();
        persistStudyProgress(q.label);
        if ("tf".equals(q.type) || "single".equals(q.type)) {
            submitButton.setText("");
            submitButton.setEnabled(false);
            submitButton.setVisibility(View.GONE);
        } else if ("multi".equals(q.type)) {
            submitButton.setText("提交多选");
            submitButton.setEnabled(true);
            submitButton.setVisibility(rememberMode ? View.GONE : View.VISIBLE);
        } else if (isEssayQuestion(q)) {
            submitButton.setText("");
            submitButton.setEnabled(false);
            submitButton.setVisibility(View.GONE);
        } else {
            submitButton.setText("提交填空");
            submitButton.setEnabled(true);
            submitButton.setVisibility(rememberMode ? View.GONE : View.VISIBLE);
        }
        refreshMeta(q);
        renderQuestionStem(q);

        for (String path : q.stemImages) {
            addImage(path);
        }
        for (String path : q.images) {
            addImage(path);
        }
        if ("blank".equals(q.type)) {
            renderBlankInputs(q);
        } else if (isEssayQuestion(q)) {
            renderEssayActions(q);
        } else {
            for (Option opt : q.options) {
                addOptionView(q, opt);
            }
        }
        if (rememberMode) {
            revealRememberModeAnswer(q);
        }
        scheduleQuestionZoomFrameSync();
    }

    private void prepareStaticPageContent() {
        selected.clear();
        blankInputs.clear();
        submitted = false;
        lastAnswerOk = null;
        optionList.removeAllViews();
        if (mathStemContainer != null) {
            mathStemContainer.removeAllViews();
            mathStemContainer.setVisibility(View.GONE);
        }
        imageList.removeAllViews();
        memoryReasonContainer.removeAllViews();
        memoryReasonContainer.setVisibility(View.GONE);
        feedbackContainer.removeAllViews();
        feedbackContainer.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        stemView.setVisibility(View.GONE);
        if (filterRowView != null) filterRowView.setVisibility(View.GONE);
        optionList.setPadding(0, dp(6), 0, dp(12));
    }

    private void renderCoursesHomePage() {
        prepareStaticPageContent();

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(18), dp(18), dp(18), dp(18));
        hero.setBackground(homeHeroBackground());
        if (Build.VERSION.SDK_INT >= 21) {
            hero.setElevation(dp(3));
        }
        TextView heroTitle = text("备考宝典", 26, TEXT, true);
        heroTitle.setIncludeFontPadding(false);
        hero.addView(heroTitle, new LinearLayout.LayoutParams(-1, -2));
        TextView heroBody = text("选择课程后继续上次的筛选分组和题目位置。", 13, MUTED, false);
        heroBody.setLineSpacing(dp(3), 1.0f);
        LinearLayout.LayoutParams heroBodyLp = new LinearLayout.LayoutParams(-1, -2);
        heroBodyLp.topMargin = dp(8);
        hero.addView(heroBody, heroBodyLp);
        optionList.addView(hero, new LinearLayout.LayoutParams(-1, -2));

        TextView section = text("课程", 14, BLUE, true);
        LinearLayout.LayoutParams sectionLp = new LinearLayout.LayoutParams(-1, -2);
        sectionLp.topMargin = dp(18);
        optionList.addView(section, sectionLp);

        addCourseEntryCard(COURSE_NETWORK);
        addCourseEntryCard(COURSE_SIGNAL_SYSTEM);
        addCourseEntryCard(COURSE_XI_THOUGHT);
        addCourseEntryCard(COURSE_DATA_STRUCTURES);

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        });
    }

    private void addCourseEntryCard(final String courseId) {
        boolean active = normalizeCourseId(courseId).equals(currentCourseId);
        LinearLayout courseCard = new LinearLayout(this);
        courseCard.setOrientation(LinearLayout.VERTICAL);
        courseCard.setPadding(dp(18), dp(16), dp(18), dp(16));
        courseCard.setBackground(courseEntryBackground());
        courseCard.setClickable(true);
        courseCard.setFocusable(true);
        courseCard.setContentDescription("进入" + courseName(courseId) + "课程");
        if (Build.VERSION.SDK_INT >= 21) {
            courseCard.setElevation(dp(4));
        }
        installPressFeedback(courseCard);
        courseCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCourse(courseId);
            }
        });
        TextView courseTitle = text(courseName(courseId), 20, active ? BLUE : TEXT, true);
        courseTitle.setIncludeFontPadding(false);
        courseCard.addView(courseTitle, new LinearLayout.LayoutParams(-1, -2));
        String features = courseHasMemoryCards(courseId)
                ? "刷题 / 记题 / 错题 / 导图"
                : "刷题 / 记题 / 错题";
        TextView courseMeta = text(assetQuestionCount(courseId) + " 题 · " + assetChapterCount(courseId)
                + " 章 · " + features, 13, MUTED, false);
        courseMeta.setLineSpacing(dp(3), 1.0f);
        LinearLayout.LayoutParams courseMetaLp = new LinearLayout.LayoutParams(-1, -2);
        courseMetaLp.topMargin = dp(8);
        courseCard.addView(courseMeta, courseMetaLp);
        TextView courseAction = text(active ? "继续当前课程" : "切换并学习", 13, BLUE, true);
        LinearLayout.LayoutParams courseActionLp = new LinearLayout.LayoutParams(-1, -2);
        courseActionLp.topMargin = dp(14);
        courseCard.addView(courseAction, courseActionLp);
        LinearLayout.LayoutParams courseLp = new LinearLayout.LayoutParams(-1, -2);
        courseLp.topMargin = dp(10);
        optionList.addView(courseCard, courseLp);
    }

    private void switchCourse(String courseId) {
        String next = normalizeCourseId(courseId);
        if (!next.equals(currentCourseId)) {
            currentCourseId = next;
            prefs.edit().putString(PREF_CURRENT_COURSE, currentCourseId).apply();
            loadQuestions();
            loadMemoryCards();
            restoreStudyFilters();
            visibleQuestions.clear();
            visibleCards.clear();
            currentIndex = 0;
            currentCardIndex = 0;
            currentCardChapter = null;
            submitted = false;
            lastAnswerOk = null;
            Toast.makeText(this, "已切换到" + currentCourseName(), Toast.LENGTH_SHORT).show();
        }
        showRestoredStudyMode();
    }

    private void renderSuggestionsPage() {
        prepareStaticPageContent();
        addSettingsSectionTitle(optionList, "反馈建议", "");

        LinearLayout suggestCard = settingsCard();
        TextView intro = text("把问题或想法写在这里，提交时会打开 GitHub 留言页并自动填好内容。", 14, MUTED, false);
        intro.setLineSpacing(dp(4), 1.0f);
        suggestCard.addView(intro, new LinearLayout.LayoutParams(-1, -2));

        final EditText suggestionInput = new EditText(this);
        suggestionInput.setTextColor(TEXT);
        suggestionInput.setHintTextColor(MUTED);
        suggestionInput.setTextSize(15);
        suggestionInput.setSingleLine(false);
        suggestionInput.setMinLines(6);
        suggestionInput.setGravity(Gravity.TOP | Gravity.START);
        suggestionInput.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        suggestionInput.setHint("写下你的建议、问题页面、题号、截图情况或期望效果");
        suggestionInput.setText(prefs == null ? "" : prefs.getString(PREF_SUGGESTION_DRAFT, ""));
        suggestionInput.setPadding(dp(12), dp(10), dp(12), dp(10));
        suggestionInput.setBackground(roundedStrokeBackground(
                THEME_LIGHT.equals(themeMode) ? Color.argb(246, 255, 255, 255) : Color.argb(132, 40, 46, 60),
                THEME_LIGHT.equals(themeMode) ? Color.argb(86, 205, 214, 232) : Color.argb(48, 255, 255, 255),
                12,
                1
        ));
        LinearLayout.LayoutParams inputLp = new LinearLayout.LayoutParams(-1, -2);
        inputLp.topMargin = dp(10);
        suggestCard.addView(suggestionInput, inputLp);

        addSettingsActionCardButton(suggestCard, "提交到 GitHub 留言", true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSuggestionToGithub(suggestionInput.getText().toString());
            }
        });
        addSettingsActionCardButton(suggestCard, "复制填写内容", false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copySuggestionDraft(suggestionInput.getText().toString());
            }
        });
        addSettingsActionCardButton(suggestCard, "分享建议文本", false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareSuggestionDraft(suggestionInput.getText().toString());
            }
        });
        optionList.addView(suggestCard, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout previewCard = settingsCard();
        TextView previewTitle = text("提交内容会附带这些上下文", 14, BLUE, true);
        previewCard.addView(previewTitle, new LinearLayout.LayoutParams(-1, -2));
        TextView preview = text(buildFeedbackContext(), 13, TEXT, false);
        preview.setLineSpacing(dp(4), 1.0f);
        preview.setTextIsSelectable(true);
        LinearLayout.LayoutParams previewLp = new LinearLayout.LayoutParams(-1, -2);
        previewLp.topMargin = dp(10);
        previewCard.addView(preview, previewLp);
        optionList.addView(previewCard, new LinearLayout.LayoutParams(-1, -2));

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        });
    }

    private void renderCardView() {
        selected.clear();
        blankInputs.clear();
        submitted = false;
        lastAnswerOk = null;
        optionList.setPadding(0, dp(16), 0, dp(8));
        optionList.removeAllViews();
        if (mathStemContainer != null) {
            mathStemContainer.removeAllViews();
            mathStemContainer.setVisibility(View.GONE);
        }
        imageList.removeAllViews();
        memoryReasonContainer.removeAllViews();
        memoryReasonContainer.setVisibility(View.GONE);
        feedbackContainer.removeAllViews();
        submitButton.setVisibility(View.GONE);

        if (visibleCards.isEmpty()) {
            if (filterRowView != null) filterRowView.setVisibility(View.VISIBLE);
            stemView.setVisibility(View.VISIBLE);
            stemView.setText(currentCourseHasMemoryCards()
                    ? "当前没有可显示的思维导图"
                    : currentCourseName() + "导图暂未生成");
            progressPeekView.setText("0/0");
            metaView.setText(currentCourseHasMemoryCards() ? "请重新选择章节" : "先用刷题 / 记题 / 错题复习");
            return;
        }
        if (currentCardIndex < 0) currentCardIndex = 0;
        if (currentCardIndex >= visibleCards.size()) currentCardIndex = visibleCards.size() - 1;
        MemoryCard card = visibleCards.get(currentCardIndex);
        cardBackVisible = true;
        final boolean allowTapFlip = false;
        if (filterRowView != null) filterRowView.setVisibility(View.VISIBLE);
        stemView.setVisibility(View.GONE);
        progressPeekView.setText((currentCardIndex + 1) + "/" + visibleCards.size());
        metaView.setText(card.chapter + " · 思维导图 · 画板内拖动缩放，按钮切换分区");
        feedbackContainer.setPadding(dp(2), dp(8), dp(2), dp(16));
        feedbackContainer.setTranslationX(0f);
        feedbackContainer.setAlpha(1f);
        feedbackContainer.setScaleX(1f);
        feedbackContainer.setScaleY(1f);
        feedbackContainer.setOnClickListener(null);
        feedbackContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleCardNavigationTouch(v, event, allowTapFlip);
            }
        });
        renderMemoryCard(feedbackContainer, card, true);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        });
    }

    private void renderSettingsPage() {
        selected.clear();
        blankInputs.clear();
        submitted = false;
        lastAnswerOk = null;
        optionList.removeAllViews();
        imageList.removeAllViews();
        memoryReasonContainer.removeAllViews();
        memoryReasonContainer.setVisibility(View.GONE);
        feedbackContainer.removeAllViews();
        feedbackContainer.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        stemView.setVisibility(View.GONE);
        if (filterRowView != null) filterRowView.setVisibility(View.GONE);

        optionList.setPadding(0, dp(4), 0, dp(8));
        addSettingsSectionTitle(optionList, "界面模式", "");

        LinearLayout themeCard = settingsCard();
        themeCard.addView(settingsValueLine("当前模式", THEME_LIGHT.equals(themeMode) ? "浅色" : "深色"));
        LinearLayout themeRow = new LinearLayout(this);
        themeRow.setOrientation(LinearLayout.HORIZONTAL);
        themeRow.setGravity(Gravity.CENTER_VERTICAL);
        Button lightButton = chromeButton("浅色");
        Button darkButton = chromeButton("深色");
        styleChromeButton(lightButton, THEME_LIGHT.equals(themeMode));
        styleChromeButton(darkButton, THEME_DARK.equals(themeMode));
        lightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setThemeMode(THEME_LIGHT);
            }
        });
        darkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setThemeMode(THEME_DARK);
            }
        });
        themeRow.addView(lightButton, new LinearLayout.LayoutParams(0, dp(44), 1));
        LinearLayout.LayoutParams darkLp = new LinearLayout.LayoutParams(0, dp(44), 1);
        darkLp.leftMargin = dp(8);
        themeRow.addView(darkButton, darkLp);
        LinearLayout.LayoutParams themeRowLp = new LinearLayout.LayoutParams(-1, -2);
        themeRowLp.topMargin = dp(12);
        themeCard.addView(themeRow, themeRowLp);
        optionList.addView(themeCard, new LinearLayout.LayoutParams(-1, -2));

        addSettingsSectionTitle(optionList, "错题规则", "");
        LinearLayout wrongRuleCard = settingsCard();
        wrongRuleCard.addView(settingsValueLine("当前移除阈值", "连续答对 " + wrongRequiredCorrectCount() + " 次"));
        LinearLayout countRow = new LinearLayout(this);
        countRow.setOrientation(LinearLayout.HORIZONTAL);
        countRow.setGravity(Gravity.CENTER_VERTICAL);
        final TextView countText = text("", 16, TEXT, true);
        updateWrongRequiredSettingText(countText);
        Button minusButton = chromeButton("−");
        Button plusButton = chromeButton("+");
        styleChromeButton(minusButton, false);
        styleChromeButton(plusButton, false);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustWrongRequiredCorrectCount(-1, countText);
                renderQuestion();
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustWrongRequiredCorrectCount(1, countText);
                renderQuestion();
            }
        });
        countRow.addView(minusButton, new LinearLayout.LayoutParams(dp(58), dp(44)));
        LinearLayout.LayoutParams countValueLp = new LinearLayout.LayoutParams(0, -2, 1);
        countValueLp.leftMargin = dp(10);
        countValueLp.rightMargin = dp(10);
        countRow.addView(countText, countValueLp);
        countRow.addView(plusButton, new LinearLayout.LayoutParams(dp(58), dp(44)));
        LinearLayout.LayoutParams countRowLp = new LinearLayout.LayoutParams(-1, -2);
        countRowLp.topMargin = dp(12);
        wrongRuleCard.addView(countRow, countRowLp);
        optionList.addView(wrongRuleCard, new LinearLayout.LayoutParams(-1, -2));
        addSettingsSectionTitle(optionList, "导出与分享", "");
        LinearLayout exportCard = settingsCard();
        exportPromptLineView = settingsValueLine("题目导出提示词", exportPromptSummary());
        exportCard.addView(exportPromptLineView, new LinearLayout.LayoutParams(-1, -2));
        addSettingsActionCardButton(exportCard, "设置题目导出提示词", false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExportPromptConfigDialog();
            }
        });
        addSettingsActionCardButton(exportCard, "分享当前题目", false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCurrentQuestion();
            }
        });
        addSettingsActionCardButton(exportCard, "导出错题本 Markdown", false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportWrongQuestions();
            }
        });
        addSettingsActionCardButton(exportCard, "导出当前章节导图", false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCardChapter == null) {
                    exportAllChapterCards();
                } else {
                    exportChapterCard(currentCardChapter);
                }
            }
        });
        addSettingsActionCardButton(exportCard, "导出全部章节导图", true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportAllChapterCards();
            }
        });
        optionList.addView(exportCard, new LinearLayout.LayoutParams(-1, -2));

        addSettingsSectionTitle(optionList, "版本更新", "");
        LinearLayout updateCard = settingsCard();
        updateVersionLineView = settingsValueLine("当前版本", currentVersionSummary());
        updateRepoLineView = settingsValueLine("GitHub 仓库", updateRepoSummary());
        updateStatusLineView = settingsValueLine("更新状态", updateStatusSummary());
        updateCard.addView(updateVersionLineView);
        LinearLayout.LayoutParams updateRepoLp = new LinearLayout.LayoutParams(-1, -2);
        updateRepoLp.topMargin = dp(8);
        updateCard.addView(updateRepoLineView, updateRepoLp);
        LinearLayout.LayoutParams updateStatusLp = new LinearLayout.LayoutParams(-1, -2);
        updateStatusLp.topMargin = dp(8);
        updateCard.addView(updateStatusLineView, updateStatusLp);
        updateCheckButton = bigButton("检查更新", false);
        updateCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForUpdates(true);
            }
        });
        LinearLayout.LayoutParams updateCheckLp = new LinearLayout.LayoutParams(-1, dp(46));
        updateCheckLp.topMargin = dp(12);
        updateCard.addView(updateCheckButton, updateCheckLp);
        Button configRepoButton = bigButton("更换更新源", false);
        configRepoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateRepoConfigDialog();
            }
        });
        LinearLayout.LayoutParams configRepoLp = new LinearLayout.LayoutParams(-1, dp(46));
        configRepoLp.topMargin = dp(8);
        updateCard.addView(configRepoButton, configRepoLp);
        optionList.addView(updateCard, new LinearLayout.LayoutParams(-1, -2));
        refreshUpdateSettingViews();

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        });
    }

    private void showCardMode(String chapter) {
        homeMode = false;
        suggestionsMode = false;
        cardMode = true;
        rememberMode = false;
        wrongMode = false;
        settingsMode = false;
        currentCardChapter = chapter;
        visibleCards.clear();
        visibleCards.addAll(buildMemoryCards(chapter));
        currentCardIndex = 0;
        cardBackVisible = true;
        SharedPreferences.Editor editor = prefs.edit().putString(coursePrefName(PREF_LAST_STUDY_MODE), STUDY_MODE_CARD);
        if (chapter == null || chapter.length() == 0) {
            editor.remove(coursePrefName(PREF_LAST_CARD_CHAPTER));
        } else {
            editor.putString(coursePrefName(PREF_LAST_CARD_CHAPTER), chapter);
        }
        editor.apply();
        renderQuestion();
    }

    private void moveCard(int delta) {
        moveCardAnimated(delta);
    }

    private void moveCardAnimated(final int delta) {
        if (!cardMode || visibleCards.isEmpty()) return;
        final int next = currentCardIndex + delta;
        if (next < 0 || next >= visibleCards.size()) {
            settleCardDrag();
            Toast.makeText(this, delta > 0 ? "已经是最后一张导图" : "已经是第一张导图", Toast.LENGTH_SHORT).show();
            return;
        }
        final float exitX = delta > 0 ? -Math.max(dp(220), feedbackContainer.getWidth() * 0.55f)
                : Math.max(dp(220), feedbackContainer.getWidth() * 0.55f);
        feedbackContainer.animate()
                .translationX(exitX)
                .alpha(0f)
                .setDuration(130)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        currentCardIndex = next;
                        cardBackVisible = true;
                        renderCardView();
                        feedbackContainer.setTranslationX(-exitX);
                        feedbackContainer.setAlpha(0f);
                        feedbackContainer.animate()
                                .translationX(0f)
                                .alpha(1f)
                                .setDuration(190)
                                .start();
                    }
                })
                .start();
    }

    private void flipCurrentCard() {
        if (!cardMode || visibleCards.isEmpty()) return;
        if (System.currentTimeMillis() - lastCardSwipeAtMillis < 500L) return;
        feedbackContainer.animate()
                .scaleX(0.94f)
                .scaleY(0.98f)
                .alpha(0f)
                .setDuration(95)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        cardBackVisible = !cardBackVisible;
                        renderCardView();
                        feedbackContainer.setScaleX(0.94f);
                        feedbackContainer.setScaleY(0.98f);
                        feedbackContainer.setAlpha(0f);
                        feedbackContainer.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .alpha(1f)
                                .setDuration(150)
                                .start();
                    }
                })
                .start();
    }

    private void settleCardDrag() {
        feedbackContainer.animate()
                .translationX(0f)
                .alpha(1f)
                .setDuration(140)
                .start();
    }

    private void renderMemoryCard(LinearLayout container, MemoryCard card, boolean backVisible) {
        container.removeAllViews();
        container.setVisibility(View.VISIBLE);

        LinearLayout cardBox = new LinearLayout(this);
        cardBox.setOrientation(LinearLayout.VERTICAL);
        cardBox.setPadding(dp(20), dp(18), dp(20), dp(18));
        cardBox.setMinimumHeight(dp(520));
        int surface = CARD_SURFACE_BACK;
        int accent = AMBER;
        cardBox.setBackground(roundedStrokeBackground(surface, GLASS_STROKE, 28, 1));
        cardBox.setElevation(dp(4));
        cardBox.setClickable(true);
        final boolean allowTapFlip = false;
        cardBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleCardNavigationTouch(v, event, allowTapFlip);
            }
        });

        addCardKicker(cardBox, card.overviewCard ? "章节总览 · 思维导图" : "知识点 · 思维导图", accent);
        addCardTitle(cardBox, card.knowledge);
        addCardSubhead(cardBox, card.chapter + "  ·  覆盖 " + card.questionCount + " 题");
        addCardDivider(cardBox);
        addMindMapSection(cardBox, card);
        addCardHint(cardBox, "导图内可拖动、双指缩放 · 轻点节点展开分支 · 用上/下一页按钮切换左右分区");
        addCardBottomSpacer(cardBox, 56);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(2);
        lp.bottomMargin = dp(8);
        container.addView(cardBox, lp);
    }

    private void renderNotebookMemoryCard(LinearLayout cardBox, MemoryCard card, boolean backVisible, int sectionColor) {
        String markdown = backVisible ? card.backMarkdown : card.frontMarkdown;
        addNotebookMarkdownShell(cardBox, markdown, sectionColor, dp(4));
        addCardHint(cardBox, "用上/下一页按钮切换左右分区");
    }

    private void addNotebookMarkdownShell(LinearLayout parent, String markdown, int sectionColor, int topMargin) {
        if (markdown == null || markdown.trim().length() == 0) return;
        LinearLayout proseShell = new LinearLayout(this);
        proseShell.setOrientation(LinearLayout.VERTICAL);
        proseShell.setPadding(dp(14), dp(10), dp(14), dp(12));
        proseShell.setBackground(roundedBackground(sectionColor, 20));
        renderMarkdown(proseShell, markdown);
        LinearLayout.LayoutParams proseLp = new LinearLayout.LayoutParams(-1, -2);
        proseLp.topMargin = topMargin;
        parent.addView(proseShell, proseLp);
    }

    private void addCardBottomSpacer(LinearLayout parent, int heightDp) {
        View spacer = new View(this);
        parent.addView(spacer, new LinearLayout.LayoutParams(-1, dp(heightDp)));
    }

    private void addCardKicker(LinearLayout parent, String value, int color) {
        TextView tv = text(value, 13, color, true);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setSingleLine(true);
        tv.setPadding(dp(10), dp(6), dp(10), dp(6));
        tv.setBackground(roundedBackground(Color.argb(46, Color.red(color), Color.green(color), Color.blue(color)), 999));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
        lp.bottomMargin = dp(10);
        parent.addView(tv, lp);
    }

    private void addCardTitle(LinearLayout parent, String value) {
        TextView tv = text(value, 23, TEXT, true);
        tv.setLineSpacing(dp(4), 1.0f);
        parent.addView(tv, new LinearLayout.LayoutParams(-1, -2));
    }

    private void addCardSubhead(LinearLayout parent, String value) {
        TextView tv = text(value, 12, MUTED, false);
        tv.setSingleLine(false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(6);
        parent.addView(tv, lp);
    }

    private void addCardDivider(LinearLayout parent) {
        View divider = new View(this);
        divider.setBackgroundColor(DIVIDER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(1));
        lp.topMargin = dp(14);
        lp.bottomMargin = dp(12);
        parent.addView(divider, lp);
    }

    private void addCardSection(LinearLayout parent, String title, String body, int bg, boolean strong) {
        if (body == null || body.trim().length() == 0) return;
        TextView titleView = text(title, 15, strong ? AMBER : BLUE, true);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(-1, -2);
        titleLp.topMargin = dp(10);
        parent.addView(titleView, titleLp);

        TextView bodyView = text(body, strong ? 17 : 15, TEXT, strong);
        bodyView.setLineSpacing(dp(5), 1.0f);
        bodyView.setPadding(dp(14), dp(12), dp(14), dp(12));
        bodyView.setBackground(roundedBackground(bg, 16));
        LinearLayout.LayoutParams bodyLp = new LinearLayout.LayoutParams(-1, -2);
        bodyLp.topMargin = dp(7);
        parent.addView(bodyView, bodyLp);
    }

    private void addCardBullets(LinearLayout parent, String title, List<String> bullets, int bg) {
        TextView titleView = text(title, 15, BLUE, true);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(-1, -2);
        titleLp.topMargin = dp(10);
        parent.addView(titleView, titleLp);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(14), dp(10), dp(14), dp(10));
        box.setBackground(roundedBackground(bg, 16));
        List<String> source = bullets == null || bullets.isEmpty()
                ? defaultCardBullets()
                : bullets;
        int index = 1;
        for (String bullet : source) {
            TextView item = text(index++ + ". " + bullet, 15, TEXT, false);
            item.setLineSpacing(dp(4), 1.0f);
            LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(-1, -2);
            itemLp.topMargin = dp(4);
            itemLp.bottomMargin = dp(4);
            box.addView(item, itemLp);
        }
        LinearLayout.LayoutParams boxLp = new LinearLayout.LayoutParams(-1, -2);
        boxLp.topMargin = dp(7);
        parent.addView(box, boxLp);
    }

    private void addCardHint(LinearLayout parent, String value) {
        TextView tv = text(value, 13, MUTED, false);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0, dp(12), 0, 0);
        parent.addView(tv, new LinearLayout.LayoutParams(-1, -2));
    }

    private void addMindMapSection(LinearLayout parent, MemoryCard card) {
        if (!card.hasMindMap()) return;

        TextView titleView = text(card.mindMapTitle.length() == 0 ? "章节导图" : card.mindMapTitle, 15, BLUE, true);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(-1, -2);
        titleLp.topMargin = dp(12);
        parent.addView(titleView, titleLp);

        final LinearLayout mapStage = new LinearLayout(this);
        mapStage.setOrientation(LinearLayout.VERTICAL);
        mapStage.setPadding(dp(14), dp(14), dp(14), dp(14));
        mapStage.setBackground(roundedBackground(CARD_SECTION, 20));

        TextView guide = text("导图模式：单指拖动画布，双指缩放；轻点节点展开分支，并同步下方详细说明。上一页 / 下一页可快速跳到左右分区。画板内横滑不会切换题目或知识点。", 13, MUTED, false);
        guide.setLineSpacing(dp(3), 1.0f);
        mapStage.addView(guide, new LinearLayout.LayoutParams(-1, -2));

        final MindMapCanvasView canvasView = new MindMapCanvasView(this,
                card.mindMapTitle.length() == 0 ? card.knowledge : card.mindMapTitle,
                card.mindMapNodes);

        FrameLayout boardShell = new FrameLayout(this);
        boardShell.setTag(TAG_MIND_MAP_BOARD);
        boardShell.setBackground(roundedStrokeBackground(
                THEME_LIGHT.equals(themeMode) ? Color.argb(238, 20, 25, 34) : Color.argb(244, 13, 18, 28),
                Color.argb(THEME_LIGHT.equals(themeMode) ? 112 : 76, 115, 152, 219),
                22, 1));
        LinearLayout.LayoutParams boardLp = new LinearLayout.LayoutParams(-1, dp(520));
        boardLp.topMargin = dp(12);
        mapStage.addView(boardShell, boardLp);

        boardShell.addView(canvasView, new FrameLayout.LayoutParams(-1, -1));

        final TextView boardTag = text("可拖动画板", 12, Color.WHITE, true);
        boardTag.setPadding(dp(10), dp(6), dp(10), dp(6));
        boardTag.setBackground(roundedBackground(Color.argb(58, 255, 255, 255), 999));
        FrameLayout.LayoutParams tagLp = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.START);
        tagLp.topMargin = dp(12);
        tagLp.leftMargin = dp(12);
        boardShell.addView(boardTag, tagLp);

        final TextView pageChip = text("第 1 / 1 页", 12, Color.WHITE, true);
        pageChip.setPadding(dp(10), dp(6), dp(10), dp(6));
        pageChip.setBackground(roundedBackground(Color.argb(84, 255, 196, 86), 999));
        FrameLayout.LayoutParams chipLp = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.END);
        chipLp.topMargin = dp(12);
        chipLp.rightMargin = dp(12);
        boardShell.addView(pageChip, chipLp);

        LinearLayout boardControls = new LinearLayout(this);
        boardControls.setOrientation(LinearLayout.HORIZONTAL);
        boardControls.setGravity(Gravity.CENTER);
        boardControls.setPadding(dp(8), dp(6), dp(8), dp(6));
        boardControls.setBackground(roundedBackground(Color.argb(76, 255, 255, 255), 999));
        Button expandAllButton = mindMapActionButton("全展", BLUE);
        Button collapseAllButton = mindMapActionButton("收起", AMBER);
        Button fullScreenButton = mindMapActionButton("全屏", GREEN);
        boardControls.addView(expandAllButton, mindMapOverlayButtonLayout(true));
        boardControls.addView(collapseAllButton, mindMapOverlayButtonLayout(true));
        boardControls.addView(fullScreenButton, mindMapOverlayButtonLayout(false));
        FrameLayout.LayoutParams controlsLp = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        controlsLp.topMargin = dp(60);
        boardShell.addView(boardControls, controlsLp);

        LinearLayout pagerRow = new LinearLayout(this);
        pagerRow.setOrientation(LinearLayout.HORIZONTAL);
        pagerRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams pagerLp = new LinearLayout.LayoutParams(-1, -2);
        pagerLp.topMargin = dp(12);
        mapStage.addView(pagerRow, pagerLp);

        final TextView prevButton = text("上一页", 13, BLUE, true);
        prevButton.setGravity(Gravity.CENTER);
        prevButton.setPadding(dp(14), dp(8), dp(14), dp(8));
        pagerRow.addView(prevButton, new LinearLayout.LayoutParams(-2, -2));

        final TextView pagerMeta = text("根节点总览", 12, MUTED, true);
        pagerMeta.setGravity(Gravity.CENTER);
        pagerMeta.setPadding(dp(12), dp(0), dp(12), dp(0));
        LinearLayout.LayoutParams pagerMetaLp = new LinearLayout.LayoutParams(0, -2, 1);
        pagerRow.addView(pagerMeta, pagerMetaLp);

        final TextView nextButton = text("下一页", 13, AMBER, true);
        nextButton.setGravity(Gravity.CENTER);
        nextButton.setPadding(dp(14), dp(8), dp(14), dp(8));
        pagerRow.addView(nextButton, new LinearLayout.LayoutParams(-2, -2));

        styleMindMapPagerButton(prevButton, BLUE, false);
        styleMindMapPagerButton(nextButton, AMBER, false);

        LinearLayout detailShell = new LinearLayout(this);
        detailShell.setOrientation(LinearLayout.VERTICAL);
        detailShell.setPadding(dp(14), dp(12), dp(14), dp(12));
        detailShell.setBackground(roundedBackground(Color.argb(THEME_LIGHT.equals(themeMode) ? 96 : 74, 255, 255, 255), 18));
        LinearLayout.LayoutParams detailLp = new LinearLayout.LayoutParams(-1, -2);
        detailLp.topMargin = dp(12);
        mapStage.addView(detailShell, detailLp);

        final TextView detailTitle = text("节点要点", 16, TEXT, true);
        detailShell.addView(detailTitle, new LinearLayout.LayoutParams(-1, -2));

        final TextView detailMeta = text("轻点导图里的任意节点查看详细说明", 12, MUTED, false);
        LinearLayout.LayoutParams metaLp = new LinearLayout.LayoutParams(-1, -2);
        metaLp.topMargin = dp(4);
        detailShell.addView(detailMeta, metaLp);

        final LinearLayout detailPoints = new LinearLayout(this);
        detailPoints.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams pointsLp = new LinearLayout.LayoutParams(-1, -2);
        pointsLp.topMargin = dp(10);
        detailShell.addView(detailPoints, pointsLp);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.goToPreviousPage();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.goToNextPage();
            }
        });
        expandAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.expandAllBranches();
            }
        });
        collapseAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.collapseAllBranches();
            }
        });
        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMindMapFullScreen(card);
            }
        });

        canvasView.setSelectionListener(new MindMapSelectionListener() {
            @Override
            public void onNodeSelected(MindMapNode node, int accentColor) {
                populateMindMapDetail(detailTitle, detailMeta, detailPoints, node, accentColor);
            }
        });
        canvasView.setPageStateListener(new MindMapPageStateListener() {
            @Override
            public void onPageStateChanged(int page, int totalPages, boolean canGoPrevious, boolean canGoNext) {
                pageChip.setText("第 " + page + " / " + totalPages + " 页");
                pagerMeta.setText(page == 1 ? "根节点总览" : "更深层分支");
                styleMindMapPagerButton(prevButton, BLUE, canGoPrevious);
                styleMindMapPagerButton(nextButton, AMBER, canGoNext);
            }
        });
        canvasView.selectInitialNode();

        LinearLayout.LayoutParams stageLp = new LinearLayout.LayoutParams(-1, -2);
        stageLp.topMargin = dp(8);
        parent.addView(mapStage, stageLp);
    }

    private int mindMapAccentColor(int index) {
        int[] palette = new int[] {
                Color.rgb(133, 164, 255),
                Color.rgb(118, 186, 169),
                Color.rgb(255, 170, 112),
                Color.rgb(193, 149, 255),
                Color.rgb(255, 140, 173)
        };
        return palette[Math.abs(index) % palette.length];
    }

    private void populateMindMapDetail(TextView titleView, TextView metaView, LinearLayout pointsContainer, MindMapNode node, int accentColor) {
        titleView.setText(node.title);
        StringBuilder meta = new StringBuilder();
        if (node.badge.length() > 0) {
            meta.append(node.badge);
        }
        if (!node.children.isEmpty()) {
            if (meta.length() > 0) meta.append("  ·  ");
            meta.append(node.children.size()).append(" 个下级分支");
        }
        if (meta.length() == 0) {
            meta.append("当前选中节点");
        }
        metaView.setText(meta.toString());
        metaView.setTextColor(Color.argb(228, Color.red(accentColor), Color.green(accentColor), Color.blue(accentColor)));

        pointsContainer.removeAllViews();
        if (node.summary.length() > 0) {
            TextView summary = text(node.summary, 14, TEXT, true);
            summary.setLineSpacing(dp(3), 1.0f);
            pointsContainer.addView(summary, new LinearLayout.LayoutParams(-1, -2));
        }

        for (String point : node.points) {
            TextView bullet = text("• " + point, 14, TEXT, false);
            bullet.setLineSpacing(dp(3), 1.0f);
            LinearLayout.LayoutParams bulletLp = new LinearLayout.LayoutParams(-1, -2);
            bulletLp.topMargin = dp(8);
            pointsContainer.addView(bullet, bulletLp);
        }

        if (!node.children.isEmpty()) {
            TextView childTitle = text("下一级分支", 13, accentColor, true);
            LinearLayout.LayoutParams childTitleLp = new LinearLayout.LayoutParams(-1, -2);
            childTitleLp.topMargin = dp(12);
            pointsContainer.addView(childTitle, childTitleLp);
            for (MindMapNode child : node.children) {
                TextView childLine = text("· " + child.title, 13, MUTED, false);
                LinearLayout.LayoutParams childLp = new LinearLayout.LayoutParams(-1, -2);
                childLp.topMargin = dp(6);
                pointsContainer.addView(childLine, childLp);
            }
        }
    }

    private void styleMindMapPagerButton(TextView button, int color, boolean enabled) {
        button.setEnabled(enabled);
        button.setAlpha(enabled ? 1f : 0.42f);
        button.setTextColor(color);
        button.setBackground(roundedStrokeBackground(
                enabled
                        ? Color.argb(THEME_LIGHT.equals(themeMode) ? 236 : 88, 255, 255, 255)
                        : Color.argb(THEME_LIGHT.equals(themeMode) ? 188 : 58, 255, 255, 255),
                enabled
                        ? Color.argb(THEME_LIGHT.equals(themeMode) ? 124 : 84, Color.red(color), Color.green(color), Color.blue(color))
                        : Color.argb(THEME_LIGHT.equals(themeMode) ? 74 : 42, 168, 178, 198),
                999,
                1));
    }

    private Button mindMapActionButton(String label, int color) {
        Button button = chromeButton(label);
        button.setTextSize(12);
        button.setTextColor(color);
        button.setContentDescription(label);
        button.setBackground(roundedStrokeBackground(
                Color.argb(THEME_LIGHT.equals(themeMode) ? 226 : 74, 255, 255, 255),
                Color.argb(THEME_LIGHT.equals(themeMode) ? 112 : 82, Color.red(color), Color.green(color), Color.blue(color)),
                999,
                1));
        return button;
    }

    private LinearLayout.LayoutParams mindMapActionButtonLayout(boolean withRightMargin) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(40), 1f);
        if (withRightMargin) {
            lp.rightMargin = dp(8);
        }
        return lp;
    }

    private LinearLayout.LayoutParams mindMapOverlayButtonLayout(boolean withRightMargin) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(64), dp(36));
        if (withRightMargin) {
            lp.rightMargin = dp(6);
        }
        return lp;
    }

    private LinearLayout.LayoutParams mindMapFullScreenButtonLayout(boolean withRightMargin) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(52), dp(32));
        if (withRightMargin) {
            lp.rightMargin = dp(5);
        }
        return lp;
    }

    private void showMindMapFullScreen(final MemoryCard card) {
        if (card == null || !card.hasMindMap()) return;

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        FrameLayout shell = new FrameLayout(this);
        shell.setPadding(0, 0, 0, 0);
        shell.setBackgroundColor(THEME_LIGHT.equals(themeMode) ? Color.rgb(18, 24, 34) : Color.rgb(10, 15, 24));

        FrameLayout boardShell = new FrameLayout(this);
        boardShell.setTag(TAG_MIND_MAP_BOARD);
        boardShell.setBackgroundColor(THEME_LIGHT.equals(themeMode) ? Color.rgb(18, 24, 34) : Color.rgb(10, 15, 24));
        shell.addView(boardShell, new FrameLayout.LayoutParams(-1, -1));

        final MindMapCanvasView canvasView = new MindMapCanvasView(this,
                card.mindMapTitle.length() == 0 ? card.knowledge : card.mindMapTitle,
                card.mindMapNodes);
        boardShell.addView(canvasView, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout titleOverlay = new LinearLayout(this);
        titleOverlay.setOrientation(LinearLayout.HORIZONTAL);
        titleOverlay.setGravity(Gravity.CENTER_VERTICAL);
        titleOverlay.setPadding(dp(12), dp(7), dp(12), dp(7));
        titleOverlay.setBackground(roundedBackground(Color.argb(76, 255, 255, 255), 999));
        TextView boardTag = text("全屏导图", 12, Color.WHITE, true);
        titleOverlay.addView(boardTag, new LinearLayout.LayoutParams(-2, -2));
        FrameLayout.LayoutParams titleLp = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        titleLp.topMargin = dp(12);
        boardShell.addView(titleOverlay, titleLp);

        final TextView pageChip = text("第 1 / 1 页", 12, Color.WHITE, true);
        pageChip.setPadding(dp(10), dp(6), dp(10), dp(6));
        pageChip.setBackground(roundedBackground(Color.argb(92, 255, 196, 86), 999));
        FrameLayout.LayoutParams chipLp = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.END);
        chipLp.topMargin = dp(12);
        chipLp.rightMargin = dp(12);
        boardShell.addView(pageChip, chipLp);

        LinearLayout boardControls = new LinearLayout(this);
        boardControls.setOrientation(LinearLayout.HORIZONTAL);
        boardControls.setGravity(Gravity.CENTER);
        boardControls.setPadding(dp(6), dp(5), dp(6), dp(5));
        boardControls.setBackground(roundedBackground(Color.argb(82, 255, 255, 255), 999));
        final TextView prevButton = text("上一页", 13, BLUE, true);
        final TextView nextButton = text("下一页", 13, AMBER, true);
        Button expandButton = mindMapActionButton("全展", BLUE);
        Button collapseButton = mindMapActionButton("收起", AMBER);
        Button closeButton = mindMapActionButton("退出", RED);
        prevButton.setGravity(Gravity.CENTER);
        nextButton.setGravity(Gravity.CENTER);
        prevButton.setPadding(dp(12), 0, dp(12), 0);
        nextButton.setPadding(dp(12), 0, dp(12), 0);
        prevButton.setTextSize(11);
        nextButton.setTextSize(11);
        expandButton.setTextSize(11);
        collapseButton.setTextSize(11);
        closeButton.setTextSize(11);
        boardControls.addView(prevButton, mindMapFullScreenButtonLayout(true));
        boardControls.addView(nextButton, mindMapFullScreenButtonLayout(true));
        boardControls.addView(expandButton, mindMapFullScreenButtonLayout(true));
        boardControls.addView(collapseButton, mindMapFullScreenButtonLayout(true));
        boardControls.addView(closeButton, mindMapFullScreenButtonLayout(false));
        FrameLayout.LayoutParams controlsLp = new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        controlsLp.bottomMargin = dp(10);
        boardShell.addView(boardControls, controlsLp);
        styleMindMapPagerButton(prevButton, BLUE, false);
        styleMindMapPagerButton(nextButton, AMBER, false);

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.expandAllBranches();
            }
        });
        collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.collapseAllBranches();
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.goToPreviousPage();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.goToNextPage();
            }
        });
        canvasView.setPageStateListener(new MindMapPageStateListener() {
            @Override
            public void onPageStateChanged(int page, int totalPages, boolean canGoPrevious, boolean canGoNext) {
                pageChip.setText("第 " + page + " / " + totalPages + " 页");
                styleMindMapPagerButton(prevButton, BLUE, canGoPrevious);
                styleMindMapPagerButton(nextButton, AMBER, canGoNext);
            }
        });
        canvasView.selectInitialNode();
        canvasView.post(new Runnable() {
            @Override
            public void run() {
                canvasView.fitWholeMap();
            }
        });

        dialog.setContentView(shell);
        dialog.show();
        if (dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    private List<String> defaultCardBullets() {
        List<String> values = new ArrayList<>();
        values.add("先抓题干关键词，再判断它问的是层次、设备、地址、协议还是计算。");
        values.add("把相邻概念分开：不要把应用层服务、网络层转发和链路层传输混在一起。");
        values.add("能说出为什么对，也能说出为什么其他说法错，才算掌握。");
        return values;
    }

    private boolean usesMathRenderer(Question q) {
        return q != null && COURSE_SIGNAL_SYSTEM.equals(q.courseId);
    }

    private void renderQuestionStem(Question q) {
        if (mathStemContainer != null) {
            mathStemContainer.removeAllViews();
            mathStemContainer.setVisibility(View.GONE);
        }
        if (q != null && !q.stemImages.isEmpty()) {
            stemView.setText("");
            stemView.setVisibility(View.GONE);
            return;
        }
        stemView.setVisibility(View.VISIBLE);
        stemView.setText(inlineMarkdown(q == null ? "" : q.stem));
    }

    private void renderQuestionMarkdown(LinearLayout container, String markdown, Question q) {
        renderMarkdown(container, markdown);
        scheduleQuestionZoomFrameSync();
    }

    private void renderMathMarkdown(final LinearLayout container, String markdown, int initialHeightPx) {
        container.removeAllViews();
        container.setVisibility(View.VISIBLE);
        WebView webView = new WebView(this);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setTextZoom(100);
        settings.setLoadWithOverviewMode(false);
        settings.setUseWideViewPort(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, String url) {
                resizeMathWebView(view, 120);
                resizeMathWebView(view, 600);
                resizeMathWebView(view, 1200);
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, Math.max(dp(64), initialHeightPx));
        lp.bottomMargin = dp(8);
        container.addView(webView, lp);
        webView.loadDataWithBaseURL(
                "file:///android_asset/",
                buildMathHtml(markdown == null ? "" : markdown),
                "text/html",
                "UTF-8",
                null);
    }

    private void resizeMathWebView(final WebView view, int delayMs) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT < 19) return;
                view.evaluateJavascript("(function(){return Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);})()",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                try {
                                    String clean = value == null ? "0" : value.replace("\"", "").trim();
                                    int cssHeight = (int) Math.ceil(Double.parseDouble(clean));
                                    int px = Math.min(dp(12000), Math.max(dp(64), (int) (cssHeight * getResources().getDisplayMetrics().density) + dp(18)));
                                    ViewGroup.LayoutParams lp = view.getLayoutParams();
                                    if (lp != null && Math.abs(lp.height - px) > dp(4)) {
                                        lp.height = px;
                                        view.setLayoutParams(lp);
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                        });
            }
        }, delayMs);
    }

    private String buildMathHtml(String markdown) {
        return "<!doctype html><html><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1\">"
                + "<style>"
                + "html,body{margin:0;padding:0;background:transparent;color:" + cssColor(TEXT) + ";font-family:sans-serif;font-size:16px;line-height:1.58;}"
                + "body{padding:2px 2px 10px 2px;}"
                + "h1{font-size:20px;line-height:1.35;color:" + cssColor(BLUE) + ";margin:0 0 10px;font-weight:800;}"
                + "h2{font-size:18px;color:" + cssColor(BLUE) + ";margin:14px 0 8px;font-weight:800;}"
                + "h3{font-size:16px;color:" + cssColor(TEXT) + ";margin:12px 0 6px;font-weight:800;}"
                + "p{margin:0 0 10px;}"
                + "ul{margin:0 0 10px 20px;padding:0;}"
                + "li{margin:0 0 7px;}"
                + "code{background:rgba(128,128,128,.16);border-radius:6px;padding:1px 4px;}"
                + ".reason{background:" + cssColor(HIGHLIGHT) + ";border-radius:10px;padding:9px 10px;margin:8px 0;font-weight:700;}"
                + "table{border-collapse:separate;border-spacing:0;width:100%;margin:8px 0 12px;font-size:14px;}"
                + "th,td{border:1px solid " + cssColor(CHIP_STROKE) + ";padding:7px 8px;vertical-align:top;}"
                + "th{background:rgba(120,150,255,.14);font-weight:800;}"
                + "mjx-container[jax='SVG']{overflow-x:auto;overflow-y:hidden;max-width:100%;padding:2px 0;}"
                + "</style>"
                + "<script>window.MathJax={tex:{inlineMath:[['$','$'],['\\\\(','\\\\)']],displayMath:[['$$','$$'],['\\\\[','\\\\]']],processEscapes:true},svg:{fontCache:'none'},options:{skipHtmlTags:['script','noscript','style','textarea','pre']}};</script>"
                + "<script src=\"mathjax/tex-svg-full.js\"></script>"
                + "</head><body>" + markdownToHtml(markdown) + "</body></html>";
    }

    private String markdownToHtml(String markdown) {
        String[] lines = markdown.replace("\r\n", "\n").replace('\r', '\n').split("\n");
        StringBuilder html = new StringBuilder();
        StringBuilder paragraph = new StringBuilder();
        boolean inList = false;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i] == null ? "" : lines[i].trim();
            if (line.startsWith("$$")) {
                flushHtmlParagraph(html, paragraph);
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                StringBuilder math = new StringBuilder(line);
                while (!line.endsWith("$$") || math.length() <= 2) {
                    i++;
                    if (i >= lines.length) break;
                    line = lines[i] == null ? "" : lines[i].trim();
                    math.append("\n").append(line);
                    if (line.endsWith("$$") && math.length() > 2) break;
                }
                html.append("<div>").append(escapeHtml(math.toString())).append("</div>");
                continue;
            }
            if (line.length() == 0) {
                flushHtmlParagraph(html, paragraph);
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                continue;
            }
            if (isMarkdownTableStart(lines, i)) {
                flushHtmlParagraph(html, paragraph);
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                i = appendHtmlTable(html, lines, i);
                continue;
            }
            if (line.startsWith("# ")) {
                flushHtmlParagraph(html, paragraph);
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                html.append("<h1>").append(inlineHtml(line.substring(2))).append("</h1>");
            } else if (line.startsWith("## ")) {
                flushHtmlParagraph(html, paragraph);
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                html.append("<h2>").append(inlineHtml(line.substring(3))).append("</h2>");
            } else if (line.startsWith("### ")) {
                flushHtmlParagraph(html, paragraph);
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                html.append("<h3>").append(inlineHtml(line.substring(4))).append("</h3>");
            } else if (isStandaloneReasonHighlightLine(line)) {
                flushHtmlParagraph(html, paragraph);
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                html.append("<div class=\"reason\">").append(inlineHtml(line)).append("</div>");
            } else if (line.startsWith("- ")) {
                flushHtmlParagraph(html, paragraph);
                if (!inList) {
                    html.append("<ul>");
                    inList = true;
                }
                String item = line.substring(2);
                if (isReasonHighlightLine(line)) {
                    html.append("</ul><div class=\"reason\">").append(inlineHtml(item)).append("</div><ul>");
                } else {
                    html.append("<li>").append(inlineHtml(item)).append("</li>");
                }
            } else {
                if (paragraph.length() > 0) paragraph.append("<br>");
                paragraph.append(inlineHtml(line));
            }
        }
        flushHtmlParagraph(html, paragraph);
        if (inList) html.append("</ul>");
        return html.toString();
    }

    private int appendHtmlTable(StringBuilder html, String[] lines, int start) {
        List<String> header = splitMarkdownTableRow(lines[start]);
        html.append("<table><thead><tr>");
        for (String cell : header) {
            html.append("<th>").append(inlineHtml(cell)).append("</th>");
        }
        html.append("</tr></thead><tbody>");
        int i = start + 2;
        while (i < lines.length && looksLikeMarkdownTableRow(lines[i])) {
            List<String> row = splitMarkdownTableRow(lines[i]);
            html.append("<tr>");
            for (String cell : row) {
                html.append("<td>").append(inlineHtml(cell)).append("</td>");
            }
            html.append("</tr>");
            i++;
        }
        html.append("</tbody></table>");
        return i - 1;
    }

    private void flushHtmlParagraph(StringBuilder html, StringBuilder paragraph) {
        if (paragraph.length() == 0) return;
        html.append("<p>").append(paragraph).append("</p>");
        paragraph.setLength(0);
    }

    private String inlineHtml(String value) {
        String escaped = escapeHtml(value == null ? "" : value);
        StringBuilder out = new StringBuilder();
        int index = 0;
        while (index < escaped.length()) {
            int start = escaped.indexOf("**", index);
            if (start < 0) {
                out.append(escaped.substring(index));
                break;
            }
            out.append(escaped, index, start);
            int end = escaped.indexOf("**", start + 2);
            if (end < 0) {
                out.append(escaped.substring(start));
                break;
            }
            out.append("<strong>").append(escaped, start + 2, end).append("</strong>");
            index = end + 2;
        }
        return out.toString()
                .replace("`", "")
                .replace("\\n", "<br>");
    }

    private String escapeHtml(String value) {
        return value == null ? "" : value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String cssColor(int color) {
        return String.format(Locale.US, "rgba(%d,%d,%d,%.3f)",
                Color.red(color),
                Color.green(color),
                Color.blue(color),
                Color.alpha(color) / 255f);
    }

    private Question currentQuestion() {
        return visibleQuestions.get(currentIndex);
    }

    private void addImage(String path) {
        addAssetImage(imageList, path);
    }

    private void addAssetImage(LinearLayout target, String path) {
        try {
            ImageView img = createAssetImageView(path);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.bottomMargin = dp(12);
            target.addView(img, lp);
        } catch (Exception e) {
            TextView missing = text("图片加载失败：" + path, 14, RED, false);
            target.addView(missing);
        }
    }

    private InputStream openAsset(String path) throws IOException {
        try {
            return getAssets().open(path);
        } catch (IOException first) {
            if (path != null && path.indexOf('/') >= 0) {
                return getAssets().open(path.replace('/', '\\'));
            }
            throw first;
        }
    }

    private ImageView createAssetImageView(String path) throws IOException {
        InputStream in = null;
        try {
            in = openAsset(path);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            ImageView img = new ImageView(this);
            img.setImageBitmap(bitmap);
            img.setAdjustViewBounds(true);
            img.setBackgroundColor(Color.TRANSPARENT);
            img.setPadding(0, 0, 0, 0);
            return img;
        } finally {
            closeQuietly(in);
        }
    }

    private void renderBlankInputs(Question q) {
        int count = Math.max(1, q.blankCount);
        List<String> answers = answerList(q);
        for (int i = 0; i < count; i++) {
            EditText input = new EditText(this);
            input.setTextColor(TEXT);
            input.setHintTextColor(MUTED);
            input.setTextSize(18);
            input.setSingleLine(false);
            input.setMinLines(1);
            input.setHint("第 " + (i + 1) + " 空");
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            input.setTextColor(TEXT);
            input.setHintTextColor(MUTED);
            input.setBackground(optionBackground(false));
            input.setPadding(dp(14), dp(10), dp(14), dp(10));
            if (rememberMode) {
                if (i < answers.size()) {
                    input.setText(answers.get(i));
                }
                input.setEnabled(false);
                input.setFocusable(false);
                input.setFocusableInTouchMode(false);
                input.setCursorVisible(false);
                input.setBackground(optionResultBackground(true));
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.bottomMargin = dp(10);
            optionList.addView(input, lp);
            blankInputs.add(input);
        }
    }

    private void renderEssayActions(final Question q) {
        if (rememberMode) {
            return;
        }
        Button showAnswer = bigButton("查看参考答案", true);
        showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEssayAnswer(q);
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(52));
        lp.topMargin = dp(4);
        lp.bottomMargin = dp(10);
        optionList.addView(showAnswer, lp);

        TextView hint = text("大题不会自动判分。先看答案，再按自己的掌握程度点“会了 / 不会”。", 13, MUTED, false);
        hint.setLineSpacing(dp(3), 1.0f);
        optionList.addView(hint, new LinearLayout.LayoutParams(-1, -2));
    }

    private void showEssayAnswer(final Question q) {
        if (!isEssayQuestion(q)) return;
        if (!q.answerImages.isEmpty()) {
            feedbackContainer.removeAllViews();
            feedbackContainer.setVisibility(View.VISIBLE);
            for (String path : q.answerImages) {
                addAssetImage(feedbackContainer, path);
            }
            addEssaySelfAssessmentButtons(q);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, feedbackContainer.getTop());
                }
            });
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("# 参考答案\n\n");
        sb.append(displayAnswer(q)).append("\n\n");
        if (q.quickExplanation != null && q.quickExplanation.trim().length() > 0) {
            sb.append("## 怎么抓这题\n\n");
            sb.append(markdownizeExplanationBlock(q.quickExplanation)).append("\n\n");
        }
        if (q.knowledgeDetail != null && q.knowledgeDetail.trim().length() > 0) {
            sb.append("## 知识点与推导\n\n");
            sb.append(markdownizeExplanationBlock(q.knowledgeDetail)).append("\n");
        } else if (q.explanation != null && q.explanation.trim().length() > 0) {
            sb.append("## 解析\n\n");
            sb.append(markdownizeExplanationBlock(q.explanation)).append("\n");
        }
        renderQuestionMarkdown(feedbackContainer, sb.toString(), q);
        addEssaySelfAssessmentButtons(q);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, feedbackContainer.getTop());
            }
        });
    }

    private void addEssaySelfAssessmentButtons(final Question q) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        row.setPadding(0, dp(8), 0, 0);

        Button knowButton = bigButton("会了", true);
        Button unknownButton = bigButton("不会，加入错题本", false);
        unknownButton.setTextColor(RED);
        unknownButton.setBackground(roundedStrokeBackground(
                THEME_LIGHT.equals(themeMode) ? Color.rgb(255, 244, 244) : Color.rgb(64, 38, 42),
                RED,
                24,
                1
        ));
        knowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEssaySelfAssessment(q, true);
            }
        });
        unknownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEssaySelfAssessment(q, false);
            }
        });

        LinearLayout.LayoutParams yesLp = new LinearLayout.LayoutParams(0, dp(48), 1f);
        yesLp.rightMargin = dp(8);
        row.addView(knowButton, yesLp);
        LinearLayout.LayoutParams noLp = new LinearLayout.LayoutParams(0, dp(48), 1f);
        row.addView(unknownButton, noLp);

        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(-1, -2);
        rowLp.bottomMargin = bottomSafeInset() + dp(168);
        feedbackContainer.addView(row, rowLp);
    }

    private void submitEssaySelfAssessment(Question q, boolean ok) {
        if (submitted) {
            Toast.makeText(this, "本题已经记录过一次自评", Toast.LENGTH_SHORT).show();
            return;
        }
        submitted = true;
        lastAnswerOk = ok;
        recordAttempt(q, ok);
        updateWrongBookState(q, ok);
        showFeedback(q, ok);
    }

    private void addOptionView(final Question q, final Option opt) {
        View view;
        if (opt.image != null && opt.image.trim().length() > 0) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setMinimumHeight(dp(62));
            card.setPadding(dp(12), dp(10), dp(12), dp(10));
            try {
                ImageView image = createAssetImageView(opt.image);
                image.setBackgroundColor(Color.TRANSPARENT);
                card.addView(image, new LinearLayout.LayoutParams(-1, -2));
            } catch (Exception e) {
                TextView fallback = text("", 18, TEXT, false);
                fallback.setText(inlineMarkdown(opt.text));
                fallback.setGravity(Gravity.CENTER_VERTICAL);
                card.addView(fallback, new LinearLayout.LayoutParams(-1, -2));
            }
            view = card;
        } else {
            TextView textView = text("", 18, TEXT, false);
            textView.setText(inlineMarkdown(opt.text));
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setPadding(dp(16), dp(14), dp(16), dp(14));
            view = textView;
        }
        view.setMinimumHeight(dp(62));
        view.setBackground(optionBackground(false));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submitted || rememberMode) return;
                if ("multi".equals(q.type)) {
                    if (selected.contains(opt.key)) selected.remove(opt.key);
                    else selected.add(opt.key);
                    refreshOptionStyles(q);
                    if (selected.size() >= correctChoiceCount(q)) {
                        submitAnswer();
                    }
                } else {
                    selected.clear();
                    selected.add(opt.key);
                    refreshOptionStyles(q);
                    submitAnswer();
                }
            }
        });
        view.setTag(opt.key);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.bottomMargin = dp(10);
        optionList.addView(view, lp);
    }

    private void refreshOptionStyles(Question q) {
        for (int i = 0; i < optionList.getChildCount(); i++) {
            View child = optionList.getChildAt(i);
            String key = String.valueOf(child.getTag());
            child.setBackground(selected.contains(key)
                    ? optionBackground(true)
                    : optionBackground(false));
        }
    }

    private void submitAnswer() {
        if (visibleQuestions.isEmpty()) return;
        if (submitted) return;
        Question q = currentQuestion();
        if (!"blank".equals(q.type) && selected.isEmpty()) {
            Toast.makeText(this, "先选择一个答案", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean ok = isCorrect(q);
        submitted = true;
        lastAnswerOk = ok;
        recordAttempt(q, ok);
        updateWrongBookState(q, ok);
        showFeedback(q, ok);
        if (!"blank".equals(q.type)) {
            paintSubmittedOptions(q);
        }
    }

    private boolean isCorrect(Question q) {
        if ("blank".equals(q.type)) {
            List<String> right = answerList(q);
            if (right.size() != blankInputs.size()) return false;
            for (int i = 0; i < right.size(); i++) {
                String user = normalize(blankInputs.get(i).getText().toString());
                String ans = normalize(right.get(i));
                if (!ans.equals(user)) return false;
            }
            return true;
        }
        String user = sortedKeys(selected);
        String ans = answerString(q);
        return user.equals(ans);
    }

    private void showFeedback(Question q, boolean ok) {
        if (!q.answerImages.isEmpty()) {
            renderQuestionMarkdown(feedbackContainer, buildFeedbackSummaryMarkdown(q, ok), q);
            for (String path : q.answerImages) {
                addAssetImage(feedbackContainer, path);
            }
            Toast.makeText(this, shortProgressToast(q, ok), Toast.LENGTH_SHORT).show();
            refreshMeta(q);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, feedbackContainer.getTop());
                }
            });
            return;
        }
        renderQuestionMarkdown(feedbackContainer, buildFeedbackMarkdown(q, ok), q);
        Toast.makeText(this, shortProgressToast(q, ok), Toast.LENGTH_SHORT).show();
        refreshMeta(q);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, feedbackContainer.getTop());
            }
        });
    }

    private String buildFeedbackSummaryMarkdown(Question q, boolean ok) {
        StringBuilder sb = new StringBuilder();
        if (isEssayQuestion(q)) {
            sb.append("# ").append(ok ? "已标记会了" : "已加入错题本").append("\n\n");
        } else {
            sb.append("# ").append(ok ? "回答正确" : "回答错误，已加入错题本").append("\n\n");
        }
        sb.append("## 本题结果\n\n");
        sb.append("- **正确答案：** ").append(displayAnswer(q)).append("\n");
        sb.append("- **知识点：** ").append(q.chapter).append(" / ").append(q.knowledge).append("\n");
        sb.append("- **错题本：** ").append(cleanProgressText(wrongBookProgressText(q, ok))).append("\n");
        return sb.toString();
    }

    private String buildFeedbackMarkdown(Question q, boolean ok) {
        StringBuilder sb = new StringBuilder();
        if (isEssayQuestion(q)) {
            sb.append("# ").append(ok ? "已标记会了" : "已加入错题本").append("\n\n");
        } else {
            sb.append("# ").append(ok ? "回答正确" : "回答错误，已加入错题本").append("\n\n");
        }
        sb.append("## 本题结果\n\n");
        sb.append("- **正确答案：** ").append(displayAnswer(q)).append("\n");
        sb.append("- **知识点：** ").append(q.chapter).append(" / ").append(q.knowledge).append("\n");
        sb.append("- **错题本：** ").append(cleanProgressText(wrongBookProgressText(q, ok))).append("\n\n");

        if (q.quickExplanation != null && q.quickExplanation.trim().length() > 0) {
            sb.append("## 快速做题\n\n");
            sb.append(markdownizeExplanationBlock(q.quickExplanation)).append("\n\n");
        }
        if (q.knowledgeDetail != null && q.knowledgeDetail.trim().length() > 0) {
            sb.append("## 知识点详解\n\n");
            sb.append(markdownizeExplanationBlock(q.knowledgeDetail)).append("\n");
        } else if (q.explanation != null && q.explanation.trim().length() > 0) {
            sb.append("## 解析\n\n");
            sb.append(markdownizeExplanationBlock(q.explanation)).append("\n");
        }
        return sb.toString();
    }

    private String cleanProgressText(String text) {
        return oneLine(text).replace("错题本规则：", "");
    }

    private String markdownizeExplanationBlock(String text) {
        StringBuilder sb = new StringBuilder();
        String[] lines = text.replace("\r\n", "\n").replace('\r', '\n').split("\n");
        boolean inTable = false;
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.length() == 0) {
                sb.append("\n");
                inTable = false;
                continue;
            }
            if (looksLikeMarkdownTableRow(line)) {
                sb.append(line).append("\n");
                inTable = true;
                continue;
            } else if (inTable) {
                sb.append("\n");
                inTable = false;
            }
            if (line.startsWith("- ")) {
                sb.append(line).append("\n");
                continue;
            }
            if (line.startsWith("【") && line.endsWith("】")) {
                sb.append("### ").append(line.substring(1, line.length() - 1)).append("\n\n");
                continue;
            }
            if (line.endsWith("：") || line.endsWith(":")) {
                sb.append("### ").append(line.substring(0, line.length() - 1)).append("\n\n");
                continue;
            }
            int colon = firstColonIndex(line);
            if (colon > 0 && colon <= 8) {
                String label = line.substring(0, colon + 1);
                String value = line.substring(colon + 1).trim();
                sb.append("- **").append(label).append("** ");
                if (value.length() == 0) {
                    sb.append("\n");
                } else {
                    sb.append(value).append("\n");
                }
                continue;
            }
            sb.append(line).append("\n\n");
        }
        return sb.toString().trim();
    }

    private int firstColonIndex(String line) {
        int cn = line.indexOf('：');
        int en = line.indexOf(':');
        if (cn < 0) return en;
        if (en < 0) return cn;
        return Math.min(cn, en);
    }

    private void renderMarkdown(LinearLayout container, String markdown) {
        container.removeAllViews();
        container.setVisibility(View.VISIBLE);
        String[] lines = markdown.replace("\r\n", "\n").replace('\r', '\n').split("\n");
        StringBuilder paragraph = new StringBuilder();
        boolean inOptionJudgmentSection = false;
        boolean inQuickReasonSection = false;
        for (int i = 0; i < lines.length; i++) {
            String rawLine = lines[i];
            String line = rawLine.trim();
            if (line.length() == 0) {
                flushMarkdownParagraph(container, paragraph, inQuickReasonSection && !inOptionJudgmentSection);
                addMarkdownSpacer(container, dp(4));
                continue;
            }
            if (isMarkdownTableStart(lines, i)) {
                flushMarkdownParagraph(container, paragraph, inQuickReasonSection && !inOptionJudgmentSection);
                i = addMarkdownTableBlock(container, lines, i);
                inOptionJudgmentSection = false;
                continue;
            }
            if (line.startsWith("# ")) {
                flushMarkdownParagraph(container, paragraph, inQuickReasonSection && !inOptionJudgmentSection);
                int titleColor = line.contains("错误") ? RED : GREEN;
                inOptionJudgmentSection = false;
                inQuickReasonSection = false;
                addMarkdownText(container, line.substring(2), 20, titleColor, true, 0, 6, 0);
            } else if (line.startsWith("## ")) {
                flushMarkdownParagraph(container, paragraph, inQuickReasonSection && !inOptionJudgmentSection);
                String title = line.substring(3);
                inOptionJudgmentSection = false;
                inQuickReasonSection = title.contains("快速做题") || title.contains("理由与辨析");
                addMarkdownText(container, title, 18, BLUE, true, 12, 6, 0);
            } else if (line.startsWith("### ")) {
                flushMarkdownParagraph(container, paragraph, inQuickReasonSection && !inOptionJudgmentSection);
                String title = line.substring(4);
                inOptionJudgmentSection = title.contains("选项判断");
                addMarkdownText(container, title, 16, TEXT, true, 10, 4, 0);
            } else if (line.startsWith("- ")) {
                flushMarkdownParagraph(container, paragraph, inQuickReasonSection && !inOptionJudgmentSection);
                if (isReasonHighlightLine(line)) {
                    addMarkdownHighlight(container, line.substring(2));
                } else if (inOptionJudgmentSection || isOptionJudgmentLine(line)) {
                    addMarkdownOptionJudgment(container, line.substring(2));
                } else {
                    addMarkdownText(container, "• " + line.substring(2), 15, TEXT, false, 2, 4, dp(8));
                }
            } else {
                if (isStandaloneReasonHighlightLine(line)) {
                    flushMarkdownParagraph(container, paragraph, inQuickReasonSection && !inOptionJudgmentSection);
                    addMarkdownHighlight(container, line);
                    inOptionJudgmentSection = false;
                    continue;
                }
                if (!looksLikeMarkdownTableRow(line)) {
                    inOptionJudgmentSection = false;
                }
                if (paragraph.length() > 0) paragraph.append('\n');
                paragraph.append(line);
            }
        }
        flushMarkdownParagraph(container, paragraph, inQuickReasonSection && !inOptionJudgmentSection);
    }

    private boolean isMarkdownTableStart(String[] lines, int index) {
        if (lines == null || index < 0 || index + 1 >= lines.length) return false;
        String header = lines[index] == null ? "" : lines[index].trim();
        String separator = lines[index + 1] == null ? "" : lines[index + 1].trim();
        return looksLikeMarkdownTableRow(header) && isMarkdownTableSeparator(separator);
    }

    private boolean looksLikeMarkdownTableRow(String line) {
        if (line == null) return false;
        String trimmed = line.trim();
        if (trimmed.length() < 5) return false;
        if (trimmed.startsWith("- ")) return false;
        int first = trimmed.indexOf('|');
        if (first < 0) return false;
        int second = trimmed.indexOf('|', first + 1);
        if (second < 0) return false;
        return second > first + 1;
    }

    private boolean isMarkdownTableSeparator(String line) {
        if (!looksLikeMarkdownTableRow(line)) return false;
        List<String> cells = splitMarkdownTableRow(line);
        if (cells.isEmpty()) return false;
        for (String cell : cells) {
            String normalized = cell.replace(" ", "").replace("\u00A0", "");
            if (!normalized.matches(":?-{3,}:?")) {
                return false;
            }
        }
        return true;
    }

    private List<String> splitMarkdownTableRow(String line) {
        List<String> cells = new ArrayList<>();
        if (line == null) return cells;
        String trimmed = line.trim();
        if (trimmed.startsWith("|")) trimmed = trimmed.substring(1);
        if (trimmed.endsWith("|")) trimmed = trimmed.substring(0, trimmed.length() - 1);
        String[] parts = trimmed.split("\\|", -1);
        for (String part : parts) {
            cells.add(part.trim());
        }
        return cells;
    }

    private int addMarkdownTableBlock(LinearLayout container, String[] lines, int startIndex) {
        List<List<String>> rows = new ArrayList<>();
        List<String> header = splitMarkdownTableRow(lines[startIndex]);
        int columnCount = header.size();
        int index = startIndex + 2;
        while (index < lines.length) {
            String rowLine = lines[index] == null ? "" : lines[index].trim();
            if (!looksLikeMarkdownTableRow(rowLine) || isMarkdownTableSeparator(rowLine)) break;
            List<String> row = splitMarkdownTableRow(rowLine);
            if (!row.isEmpty()) {
                while (row.size() < columnCount) row.add("");
                if (row.size() > columnCount) row = new ArrayList<>(row.subList(0, columnCount));
                rows.add(row);
            }
            index++;
        }
        addMarkdownTable(container, header, rows);
        return index - 1;
    }

    private void addMarkdownTable(LinearLayout container, List<String> header, List<List<String>> rows) {
        if (header == null || header.isEmpty()) return;

        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setTag(TAG_MARKDOWN_TABLE_SCROLL);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        installMarkdownTableSwipeGuard(scroll);

        LinearLayout shell = new LinearLayout(this);
        shell.setOrientation(LinearLayout.VERTICAL);
        shell.setPadding(dp(1), dp(1), dp(1), dp(1));
        shell.setBackground(markdownTableShellBackground());

        TableLayout table = new TableLayout(this);
        table.setShrinkAllColumns(false);
        table.setStretchAllColumns(false);
        table.setPadding(0, 0, 0, 0);

        table.addView(buildMarkdownTableRow(header, true, false, header.size()));
        for (int i = 0; i < rows.size(); i++) {
            table.addView(buildMarkdownTableRow(rows.get(i), false, i % 2 == 1, header.size()));
        }

        shell.addView(table, new LinearLayout.LayoutParams(-2, -2));
        scroll.addView(shell, new ViewGroup.LayoutParams(-2, -2));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(8);
        lp.bottomMargin = dp(10);
        container.addView(scroll, lp);
    }

    private void installMarkdownTableSwipeGuard(final HorizontalScrollView tableScroll) {
        final float[] start = new float[2];
        final boolean[] horizontalDrag = new boolean[1];
        tableScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                if (action == MotionEvent.ACTION_DOWN) {
                    start[0] = event.getRawX();
                    start[1] = event.getRawY();
                    horizontalDrag[0] = false;
                    suppressQuestionPageSwipe = true;
                    return false;
                }
                if (action == MotionEvent.ACTION_MOVE) {
                    float dx = event.getRawX() - start[0];
                    float dy = event.getRawY() - start[1];
                    float absX = Math.abs(dx);
                    float absY = Math.abs(dy);
                    int slop = Math.max(touchSlop, dp(8));
                    if (!horizontalDrag[0] && absX > slop && absX > absY * 1.15f) {
                        horizontalDrag[0] = true;
                    }
                    if (horizontalDrag[0]) {
                        tableScroll.getParent().requestDisallowInterceptTouchEvent(true);
                        requestNoIntercept(true);
                    }
                    suppressQuestionPageSwipe = true;
                    return false;
                }
                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    horizontalDrag[0] = false;
                    suppressQuestionPageSwipe = false;
                    tableScroll.getParent().requestDisallowInterceptTouchEvent(false);
                    requestNoIntercept(false);
                    return false;
                }
                return false;
            }
        });
    }

    private TableRow buildMarkdownTableRow(List<String> cells, boolean headerRow, boolean alternateRow, int columnCount) {
        TableRow row = new TableRow(this);
        row.setPadding(0, 0, 0, 0);
        for (int i = 0; i < columnCount; i++) {
            String value = i < cells.size() ? cells.get(i) : "";
            TextView tv = text("", headerRow ? 14 : 14, TEXT, headerRow);
            tv.setText(inlineMarkdown(value));
            tv.setPadding(dp(12), dp(headerRow ? 11 : 10), dp(12), dp(headerRow ? 11 : 10));
            tv.setLineSpacing(dp(3), 1.0f);
            tv.setMinWidth(dp(columnCount <= 2 ? 160 : columnCount == 3 ? 126 : 104));
            tv.setBackground(markdownTableCellBackground(headerRow, alternateRow));
            TableRow.LayoutParams lp = new TableRow.LayoutParams(-2, -2);
            lp.setMargins(0, 0, dp(1), dp(1));
            row.addView(tv, lp);
        }
        return row;
    }

    private GradientDrawable markdownTableShellBackground() {
        int fill = THEME_LIGHT.equals(themeMode)
                ? Color.argb(210, 248, 250, 255)
                : Color.argb(120, 255, 255, 255);
        int stroke = THEME_LIGHT.equals(themeMode)
                ? Color.argb(96, 196, 208, 228)
                : Color.argb(58, 220, 230, 250);
        GradientDrawable drawable = roundedBackground(fill, 18);
        drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private GradientDrawable markdownTableCellBackground(boolean headerRow, boolean alternateRow) {
        int fill;
        int stroke;
        if (headerRow) {
            fill = THEME_LIGHT.equals(themeMode)
                    ? Color.argb(244, 239, 243, 250)
                    : Color.argb(170, 53, 60, 76);
            stroke = THEME_LIGHT.equals(themeMode)
                    ? Color.argb(82, 205, 214, 232)
                    : Color.argb(42, 255, 255, 255);
        } else if (alternateRow) {
            fill = THEME_LIGHT.equals(themeMode)
                    ? Color.argb(238, 251, 253, 255)
                    : Color.argb(106, 34, 39, 51);
            stroke = THEME_LIGHT.equals(themeMode)
                    ? Color.argb(58, 215, 223, 238)
                    : Color.argb(28, 255, 255, 255);
        } else {
            fill = THEME_LIGHT.equals(themeMode)
                    ? Color.argb(246, 255, 255, 255)
                    : Color.argb(132, 40, 46, 60);
            stroke = THEME_LIGHT.equals(themeMode)
                    ? Color.argb(52, 215, 223, 238)
                    : Color.argb(30, 255, 255, 255);
        }
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private void flushMarkdownParagraph(LinearLayout container, StringBuilder paragraph) {
        flushMarkdownParagraph(container, paragraph, false);
    }

    private void flushMarkdownParagraph(LinearLayout container, StringBuilder paragraph, boolean highlight) {
        if (paragraph.length() == 0) return;
        if (highlight) {
            addMarkdownHighlight(container, paragraph.toString());
        } else {
            addMarkdownText(container, paragraph.toString(), 15, TEXT, false, 4, 8, 0);
        }
        paragraph.setLength(0);
    }

    private void addMarkdownSpacer(LinearLayout container, int height) {
        View spacer = new View(this);
        container.addView(spacer, new LinearLayout.LayoutParams(-1, height));
    }

    private void addMarkdownText(LinearLayout container, String value, int sp, int color,
                                 boolean bold, int topMargin, int bottomMargin, int leftMargin) {
        TextView tv = text("", sp, color, bold);
        tv.setText(inlineMarkdown(value));
        tv.setLineSpacing(dp(3), 1.0f);
        tv.setTextIsSelectable(false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(topMargin);
        lp.bottomMargin = dp(bottomMargin);
        lp.leftMargin = leftMargin;
        container.addView(tv, lp);
    }

    private boolean isReasonHighlightLine(String line) {
        String compact = line.replace(" ", "").replace(':', '：');
        String[] labels = new String[] {
                "理由：", "本题理由：", "直接理由：", "核心理由：", "底层理由：",
                "做题理由：", "判题理由：", "判断理由：",
                "选择理由：", "关键理由：", "为什么选它：", "为什么这样判题：",
                "原因：", "依据：", "本题判断：", "本题答案：", "本题填：",
                "题眼：", "判断：", "答案：", "关键：", "错点：", "易错：",
                "知识点：", "核心知识点：", "答题抓手：",
                "本题重点：", "答案逻辑：", "答题主线：", "答题要点：",
                "为什么这样答：", "关键答题点：", "复习抓手：", "本题压缩记忆："
        };
        for (String label : labels) {
            if (compact.startsWith("-**" + label + "**") || compact.startsWith("-" + label)) {
                return true;
            }
        }
        return false;
    }

    private boolean isStandaloneReasonHighlightLine(String line) {
        String compact = line == null ? "" : line.replace(" ", "").replace(':', '：');
        String[] labels = new String[] {
                "理由：", "本题理由：", "直接理由：", "核心理由：", "底层理由：",
                "做题理由：", "判题理由：", "判断理由：",
                "选择理由：", "关键理由：", "为什么选它：", "为什么这样判题：",
                "原因：", "依据：", "本题判断：", "本题答案：", "本题填：",
                "题眼：", "判断：", "答案：", "关键：", "错点：", "易错：",
                "知识点：", "核心知识点：", "答题抓手：",
                "本题重点：", "答案逻辑：", "答题主线：", "答题要点：",
                "为什么这样答：", "关键答题点：", "复习抓手：", "本题压缩记忆："
        };
        for (String label : labels) {
            if (compact.startsWith(label) || compact.startsWith("**" + label + "**")) {
                return true;
            }
        }
        return false;
    }

    private boolean isOptionJudgmentLine(String line) {
        String trimmed = line == null ? "" : line.trim();
        if (!trimmed.startsWith("- ")) return false;
        return isOptionBulletBody(trimmed.substring(2));
    }

    private boolean isOptionBulletBody(String value) {
        String body = cleanOptionJudgmentDisplay(value).replace("**", "").trim();
        if (body.length() < 2) return false;
        char first = body.charAt(0);
        if (!(first >= 'A' && first <= 'Z')) return false;
        int index = 1;
        while (index < body.length() && Character.isWhitespace(body.charAt(index))) {
            index++;
        }
        if (index >= body.length()) return false;
        char delimiter = body.charAt(index);
        return delimiter == '：'
                || delimiter == ':'
                || delimiter == '、'
                || delimiter == '.'
                || delimiter == '．'
                || delimiter == ')'
                || delimiter == '）';
    }

    private String cleanOptionJudgmentDisplay(String value) {
        String body = value == null ? "" : value.trim();
        boolean changed = true;
        while (changed && body.length() > 0) {
            changed = false;
            if (body.startsWith("•") || body.startsWith("·")) {
                body = body.substring(1).trim();
                changed = true;
            } else if (body.startsWith("- ")) {
                body = body.substring(2).trim();
                changed = true;
            } else if (body.startsWith("-")) {
                body = body.substring(1).trim();
                changed = true;
            }
        }
        return body;
    }

    private void addMarkdownHighlight(LinearLayout container, String value) {
        TextView tv = text("", 15, TEXT, true);
        tv.setText(inlineMarkdown(value));
        tv.setLineSpacing(dp(4), 1.0f);
        tv.setTextIsSelectable(false);
        tv.setPadding(dp(12), dp(9), dp(12), dp(9));
        tv.setBackground(roundedBackground(HIGHLIGHT, 8));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(6);
        lp.bottomMargin = dp(8);
        lp.leftMargin = dp(4);
        lp.rightMargin = dp(2);
        container.addView(tv, lp);
    }

    private void addMarkdownOptionJudgment(LinearLayout container, String value) {
        value = cleanOptionJudgmentDisplay(value);
        String normalized = (value == null ? "" : value)
                .replace("**", "")
                .replace('：', ':')
                .replace(" ", "");
        boolean negative = normalized.contains(":不选")
                || normalized.contains("不应选")
                || normalized.contains("不能选")
                || normalized.contains(":错")
                || normalized.contains("错误")
                || normalized.contains("不正确")
                || normalized.contains("不成立")
                || normalized.contains("不符合");
        boolean positive = normalized.contains(":应选")
                || normalized.contains(":选")
                || normalized.contains(":对")
                || normalized.contains("正确")
                || normalized.contains("成立")
                || normalized.contains("符合");
        boolean correct = positive && !negative;
        int tint = correct ? GREEN : RED;
        int bgAlpha = THEME_LIGHT.equals(themeMode) ? 28 : 44;
        int strokeAlpha = THEME_LIGHT.equals(themeMode) ? 72 : 112;

        TextView tv = text("", 15, TEXT, false);
        tv.setText(inlineMarkdown(value));
        tv.setLineSpacing(dp(4), 1.0f);
        tv.setTextIsSelectable(false);
        tv.setPadding(dp(12), dp(10), dp(12), dp(10));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.argb(bgAlpha, Color.red(tint), Color.green(tint), Color.blue(tint)));
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(1), Color.argb(strokeAlpha, Color.red(tint), Color.green(tint), Color.blue(tint)));
        tv.setBackground(bg);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(4);
        lp.bottomMargin = dp(6);
        lp.leftMargin = dp(6);
        lp.rightMargin = dp(2);
        container.addView(tv, lp);
    }

    private GradientDrawable roundedBackground(int color, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));
        return drawable;
    }

    private GradientDrawable appBackground() {
        return new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{BG_ALT, BG}
        );
    }

    private GradientDrawable roundedStrokeBackground(int color, int strokeColor, int radiusDp, int strokeDp) {
        GradientDrawable drawable = roundedBackground(color, radiusDp);
        drawable.setStroke(dp(strokeDp), strokeColor);
        return drawable;
    }

    private GradientDrawable floatingExportBackground() {
        int fill = THEME_LIGHT.equals(themeMode)
                ? Color.argb(154, 255, 255, 255)
                : Color.argb(104, 41, 47, 63);
        int stroke = Color.argb(THEME_LIGHT.equals(themeMode) ? 118 : 82,
                Color.red(BLUE), Color.green(BLUE), Color.blue(BLUE));
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        fill,
                        THEME_LIGHT.equals(themeMode)
                                ? Color.argb(116, 239, 244, 255)
                                : Color.argb(74, 21, 25, 36)
                }
        );
        drawable.setCornerRadius(dp(23));
        drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private GradientDrawable menuButtonBackground() {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        THEME_LIGHT.equals(themeMode) ? Color.argb(178, 255, 255, 255) : Color.argb(118, 48, 55, 72),
                        THEME_LIGHT.equals(themeMode) ? Color.argb(116, 238, 244, 255) : Color.argb(72, 25, 30, 42)
                }
        );
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke(dp(1), THEME_LIGHT.equals(themeMode)
                ? Color.argb(120, 202, 214, 238)
                : Color.argb(76, 220, 230, 250));
        return drawable;
    }

    private GradientDrawable drawerPanelBackground() {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        THEME_LIGHT.equals(themeMode) ? Color.argb(244, 255, 255, 255) : Color.argb(232, 34, 40, 54),
                        THEME_LIGHT.equals(themeMode) ? Color.argb(218, 241, 246, 255) : Color.argb(214, 23, 29, 42)
                }
        );
        drawable.setCornerRadii(new float[]{
                dp(30), dp(30),
                0, 0,
                0, 0,
                dp(30), dp(30)
        });
        drawable.setStroke(dp(1), THEME_LIGHT.equals(themeMode)
                ? Color.argb(126, 216, 226, 244)
                : Color.argb(72, 218, 230, 250));
        return drawable;
    }

    private GradientDrawable drawerRowBackground(boolean active, int accent) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                active
                        ? new int[]{
                        Color.argb(THEME_LIGHT.equals(themeMode) ? 74 : 96,
                                Color.red(accent), Color.green(accent), Color.blue(accent)),
                        THEME_LIGHT.equals(themeMode) ? Color.argb(170, 255, 255, 255) : Color.argb(42, 255, 255, 255)
                }
                        : new int[]{
                        THEME_LIGHT.equals(themeMode) ? Color.argb(126, 255, 255, 255) : Color.argb(62, 255, 255, 255),
                        THEME_LIGHT.equals(themeMode) ? Color.argb(72, 246, 249, 255) : Color.argb(28, 255, 255, 255)
                }
        );
        drawable.setCornerRadius(dp(22));
        drawable.setStroke(dp(1), active
                ? Color.argb(THEME_LIGHT.equals(themeMode) ? 116 : 82,
                Color.red(accent), Color.green(accent), Color.blue(accent))
                : (THEME_LIGHT.equals(themeMode) ? Color.argb(70, 210, 220, 238) : Color.argb(38, 210, 222, 244)));
        return drawable;
    }

    private GradientDrawable homeHeroBackground() {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        THEME_LIGHT.equals(themeMode) ? Color.argb(250, 255, 255, 255) : Color.argb(218, 39, 46, 62),
                        THEME_LIGHT.equals(themeMode) ? Color.argb(222, 236, 242, 255) : Color.argb(184, 24, 31, 46)
                }
        );
        drawable.setCornerRadii(new float[]{
                dp(24), dp(24),
                dp(14), dp(14),
                dp(30), dp(30),
                dp(14), dp(14)
        });
        drawable.setStroke(dp(1), THEME_LIGHT.equals(themeMode)
                ? Color.argb(130, 214, 224, 244)
                : Color.argb(74, 214, 225, 245));
        return drawable;
    }

    private GradientDrawable courseEntryBackground() {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                        Color.argb(THEME_LIGHT.equals(themeMode) ? 42 : 82,
                                Color.red(BLUE), Color.green(BLUE), Color.blue(BLUE)),
                        THEME_LIGHT.equals(themeMode) ? Color.argb(240, 255, 255, 255) : Color.argb(168, 31, 37, 51)
                }
        );
        drawable.setCornerRadius(dp(22));
        drawable.setStroke(dp(1), THEME_LIGHT.equals(themeMode)
                ? Color.argb(122, 194, 208, 238)
                : Color.argb(72, 190, 204, 232));
        return drawable;
    }

    private GradientDrawable headerPanelBackground() {
        int start = THEME_LIGHT.equals(themeMode)
                ? Color.argb(246, 255, 255, 255)
                : Color.argb(214, 41, 47, 63);
        int end = THEME_LIGHT.equals(themeMode)
                ? Color.argb(222, 241, 246, 255)
                : Color.argb(188, 27, 32, 44);
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{start, end}
        );
        drawable.setCornerRadius(dp(28));
        drawable.setStroke(dp(1), THEME_LIGHT.equals(themeMode)
                ? Color.argb(136, 220, 230, 244)
                : Color.argb(70, 214, 225, 245));
        return drawable;
    }

    private GradientDrawable headerTitleBackground(int accent) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                        Color.argb(THEME_LIGHT.equals(themeMode) ? 48 : 88,
                                Color.red(accent), Color.green(accent), Color.blue(accent)),
                        Color.argb(THEME_LIGHT.equals(themeMode) ? 22 : 42,
                                Color.red(accent), Color.green(accent), Color.blue(accent))
                }
        );
        drawable.setCornerRadii(new float[]{
                dp(18), dp(18),
                dp(26), dp(26),
                dp(26), dp(26),
                dp(18), dp(18)
        });
        drawable.setStroke(dp(1), Color.argb(THEME_LIGHT.equals(themeMode) ? 112 : 72,
                Color.red(accent), Color.green(accent), Color.blue(accent)));
        return drawable;
    }

    private GradientDrawable headerProgressBackground(int accent) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        THEME_LIGHT.equals(themeMode) ? Color.argb(228, 255, 255, 255) : Color.argb(94, 255, 255, 255),
                        THEME_LIGHT.equals(themeMode) ? Color.argb(194, 247, 250, 255) : Color.argb(56, 255, 255, 255)
                }
        );
        drawable.setCornerRadius(dp(16));
        drawable.setStroke(dp(1), Color.argb(THEME_LIGHT.equals(themeMode) ? 74 : 42,
                Color.red(accent), Color.green(accent), Color.blue(accent)));
        return drawable;
    }

    private GradientDrawable headerSeekShellBackground() {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                        THEME_LIGHT.equals(themeMode) ? Color.argb(132, 255, 255, 255) : Color.argb(66, 255, 255, 255),
                        THEME_LIGHT.equals(themeMode) ? Color.argb(96, 245, 249, 255) : Color.argb(32, 255, 255, 255)
                }
        );
        drawable.setCornerRadius(dp(18));
        drawable.setStroke(dp(1), THEME_LIGHT.equals(themeMode)
                ? Color.argb(80, 214, 224, 240)
                : Color.argb(44, 194, 206, 228));
        return drawable;
    }

    private GradientDrawable filterTrayBackground() {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                        THEME_LIGHT.equals(themeMode) ? Color.argb(208, 255, 255, 255) : Color.argb(138, 39, 45, 58),
                        THEME_LIGHT.equals(themeMode) ? Color.argb(182, 247, 250, 255) : Color.argb(118, 31, 36, 47)
                }
        );
        drawable.setCornerRadius(dp(24));
        drawable.setStroke(dp(1), THEME_LIGHT.equals(themeMode)
                ? Color.argb(92, 210, 220, 238)
                : Color.argb(52, 180, 194, 224));
        return drawable;
    }

    private GradientDrawable filterButtonBackground(boolean active) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        active
                                ? (THEME_LIGHT.equals(themeMode) ? Color.argb(236, 255, 255, 255) : Color.argb(102, 255, 255, 255))
                                : Color.argb(0, 0, 0, 0),
                        active
                                ? (THEME_LIGHT.equals(themeMode) ? Color.argb(212, 246, 250, 255) : Color.argb(70, 255, 255, 255))
                                : Color.argb(0, 0, 0, 0)
                }
        );
        drawable.setCornerRadius(dp(18));
        drawable.setStroke(dp(1), active
                ? (THEME_LIGHT.equals(themeMode) ? Color.argb(116, 176, 196, 236) : Color.argb(72, 208, 220, 245))
                : Color.argb(THEME_LIGHT.equals(themeMode) ? 38 : 28, 164, 176, 201));
        return drawable;
    }

    private GradientDrawable filterActionBackground(int accent) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        Color.argb(THEME_LIGHT.equals(themeMode) ? 236 : 214,
                                Color.red(accent), Color.green(accent), Color.blue(accent)),
                        Color.argb(THEME_LIGHT.equals(themeMode) ? 208 : 194,
                                Math.max(0, Color.red(accent) - 8),
                                Math.max(0, Color.green(accent) - 8),
                                Math.max(0, Color.blue(accent) - 8))
                }
        );
        drawable.setCornerRadius(dp(18));
        drawable.setStroke(dp(1), Color.argb(THEME_LIGHT.equals(themeMode) ? 84 : 44, 255, 255, 255));
        return drawable;
    }

    private GradientDrawable navBarBackground() {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        THEME_LIGHT.equals(themeMode) ? Color.argb(228, 255, 255, 255) : Color.argb(156, 49, 56, 72),
                        THEME_LIGHT.equals(themeMode) ? Color.argb(176, 240, 245, 255) : Color.argb(128, 33, 39, 53)
                }
        );
        drawable.setCornerRadius(dp(30));
        drawable.setStroke(dp(1), THEME_LIGHT.equals(themeMode)
                ? Color.argb(96, 228, 236, 250)
                : Color.argb(58, 220, 230, 250));
        return drawable;
    }

    private GradientDrawable navPillBackground(boolean active) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                active
                        ? new int[]{
                        THEME_LIGHT.equals(themeMode) ? Color.argb(248, 255, 255, 255) : Color.argb(108, 255, 255, 255),
                        THEME_LIGHT.equals(themeMode) ? Color.argb(210, 246, 249, 255) : Color.argb(66, 255, 255, 255)
                }
                        : new int[]{
                        THEME_LIGHT.equals(themeMode) ? Color.argb(82, 255, 255, 255) : Color.argb(20, 255, 255, 255),
                        THEME_LIGHT.equals(themeMode) ? Color.argb(24, 255, 255, 255) : Color.argb(8, 255, 255, 255)
                }
        );
        drawable.setCornerRadius(dp(18));
        drawable.setStroke(dp(1), active
                ? (THEME_LIGHT.equals(themeMode) ? Color.argb(116, 220, 230, 246) : Color.argb(56, 240, 245, 255))
                : Color.argb(THEME_LIGHT.equals(themeMode) ? 22 : 12, 255, 255, 255));
        return drawable;
    }

    private GradientDrawable choiceSheetBackground() {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        THEME_LIGHT.equals(themeMode) ? Color.argb(248, 255, 255, 255) : Color.argb(234, 31, 37, 50),
                        THEME_LIGHT.equals(themeMode) ? Color.argb(232, 243, 247, 255) : Color.argb(214, 24, 29, 40)
                }
        );
        drawable.setCornerRadius(dp(28));
        drawable.setStroke(dp(1), THEME_LIGHT.equals(themeMode)
                ? Color.argb(110, 215, 225, 242)
                : Color.argb(64, 210, 222, 244));
        return drawable;
    }

    private GradientDrawable choiceRowBackground(boolean selectedState) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                selectedState
                        ? new int[]{
                        Color.argb(THEME_LIGHT.equals(themeMode) ? 236 : 102, Color.red(BLUE), Color.green(BLUE), Color.blue(BLUE)),
                        Color.argb(THEME_LIGHT.equals(themeMode) ? 208 : 78, 255, 255, 255)
                }
                        : new int[]{
                        THEME_LIGHT.equals(themeMode) ? Color.argb(168, 255, 255, 255) : Color.argb(90, 41, 46, 60),
                        THEME_LIGHT.equals(themeMode) ? Color.argb(120, 247, 250, 255) : Color.argb(68, 34, 39, 52)
                }
        );
        drawable.setCornerRadius(dp(20));
        drawable.setStroke(dp(1), selectedState
                ? Color.argb(THEME_LIGHT.equals(themeMode) ? 118 : 72, Color.red(BLUE), Color.green(BLUE), Color.blue(BLUE))
                : Color.argb(THEME_LIGHT.equals(themeMode) ? 74 : 42, 188, 199, 220));
        return drawable;
    }

    private GradientDrawable optionBackground(boolean selectedState) {
        return roundedStrokeBackground(
                selectedState ? OPTION_SELECTED_FILL : OPTION_FILL,
                selectedState ? OPTION_SELECTED_STROKE : OPTION_STROKE,
                24,
                1
        );
    }

    private GradientDrawable optionResultBackground(boolean correct) {
        int fill = correct
                ? Color.argb(THEME_LIGHT.equals(themeMode) ? 255 : 235, correct ? 229 : 229, correct ? 246 : 52, correct ? 236 : 52)
                : Color.argb(THEME_LIGHT.equals(themeMode) ? 255 : 235, 255, 238, 238);
        int stroke = correct ? GREEN : RED;
        if (!correct && !THEME_LIGHT.equals(themeMode)) {
            fill = Color.rgb(49, 31, 34);
        }
        return roundedStrokeBackground(fill, stroke, 22, correct ? 2 : 1);
    }

    private LinearLayout settingsCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        card.setBackground(roundedStrokeBackground(PANEL_ELEVATED, GLASS_STROKE, 26, 1));
        card.setElevation(dp(2));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.bottomMargin = dp(14);
        card.setLayoutParams(lp);
        return card;
    }

    private void addSettingsSectionTitle(LinearLayout parent, String title, String body) {
        TextView titleView = text(title, 14, BLUE, true);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(-1, -2);
        titleLp.topMargin = dp(4);
        parent.addView(titleView, titleLp);

        if (body == null || body.trim().length() == 0) {
            return;
        }
        TextView bodyView = text(body, 12, MUTED, false);
        bodyView.setLineSpacing(dp(3), 1.0f);
        LinearLayout.LayoutParams bodyLp = new LinearLayout.LayoutParams(-1, -2);
        bodyLp.topMargin = dp(6);
        bodyLp.bottomMargin = dp(10);
        parent.addView(bodyView, bodyLp);
    }

    private TextView settingsValueLine(String label, String value) {
        TextView line = text(label + " · " + value, 15, TEXT, true);
        line.setLineSpacing(dp(3), 1.0f);
        return line;
    }

    private void addSettingsActionCardButton(LinearLayout parent, String label, boolean primary, View.OnClickListener listener) {
        Button button = bigButton(label, primary);
        button.setOnClickListener(listener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(46));
        lp.topMargin = dp(8);
        parent.addView(button, lp);
    }

    private void setThemeMode(String value) {
        if (value == null || value.equals(themeMode)) return;
        themeMode = value;
        prefs.edit().putString(PREF_THEME_MODE, value).apply();
        recreate();
    }

    private CharSequence inlineMarkdown(String value) {
        SpannableStringBuilder out = new SpannableStringBuilder();
        if (value == null || value.length() == 0) return out;
        String text = value.replace("\r\n", "\n").replace('\r', '\n');
        int index = 0;
        while (index < text.length()) {
            if (text.startsWith("**", index)) {
                int end = text.indexOf("**", index + 2);
                if (end < 0) {
                    out.append(text.substring(index));
                    break;
                }
                int spanStart = out.length();
                out.append(text, index + 2, end);
                out.setSpan(new StyleSpan(Typeface.BOLD), spanStart, out.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                index = end + 2;
                continue;
            }
            if (text.startsWith("$$", index)) {
                int end = text.indexOf("$$", index + 2);
                if (end < 0) {
                    appendFormulaFragment(out, text.substring(index + 2));
                    break;
                }
                appendFormulaFragment(out, text.substring(index + 2, end));
                index = end + 2;
                continue;
            }
            if (text.startsWith("$", index)) {
                int end = text.indexOf("$", index + 1);
                if (end < 0) {
                    out.append(text.substring(index));
                    break;
                }
                appendFormulaFragment(out, text.substring(index + 1, end));
                index = end + 1;
                continue;
            }
            if (text.startsWith("\\(", index)) {
                int end = text.indexOf("\\)", index + 2);
                if (end < 0) {
                    out.append(text.substring(index));
                    break;
                }
                appendFormulaFragment(out, text.substring(index + 2, end));
                index = end + 2;
                continue;
            }
            if (text.startsWith("\\[", index)) {
                int end = text.indexOf("\\]", index + 2);
                if (end < 0) {
                    out.append(text.substring(index));
                    break;
                }
                appendFormulaFragment(out, text.substring(index + 2, end));
                index = end + 2;
                continue;
            }
            out.append(text.charAt(index));
            index++;
        }
        return out;
    }

    private void appendFormulaFragment(SpannableStringBuilder out, String raw) {
        if (raw == null || raw.length() == 0) return;
        String text = normalizeFormulaText(raw);
        int index = 0;
        while (index < text.length()) {
            char ch = text.charAt(index);
            if (ch == '^' || ch == '_') {
                boolean superscript = ch == '^';
                index++;
                if (index >= text.length()) break;
                String token;
                if (text.charAt(index) == '{') {
                    int end = findMatchingBrace(text, index);
                    if (end < 0) {
                        token = text.substring(index + 1);
                        index = text.length();
                    } else {
                        token = text.substring(index + 1, end);
                        index = end + 1;
                    }
                } else {
                    int start = index;
                    while (index < text.length() && isFormulaScriptChar(text.charAt(index))) {
                        index++;
                    }
                    token = text.substring(start, index);
                }
                token = stripFormulaGrouping(token);
                int spanStart = out.length();
                out.append(token);
                if (out.length() > spanStart) {
                    out.setSpan(new RelativeSizeSpan(0.82f), spanStart, out.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (superscript) {
                        out.setSpan(new SuperscriptSpan(), spanStart, out.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        out.setSpan(new SubscriptSpan(), spanStart, out.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                continue;
            }
            if (ch != '{' && ch != '}') {
                out.append(ch);
            }
            index++;
        }
    }

    private boolean isFormulaScriptChar(char c) {
        return Character.isLetterOrDigit(c)
                || c == '+' || c == '-' || c == '.' || c == '/'
                || c == '(' || c == ')'
                || c == 'ω' || c == 'Ω' || c == 'π' || c == 'θ'
                || c == 'δ' || c == 'λ' || c == 'α' || c == 'β'
                || c == 'γ' || c == 'μ' || c == 'φ' || c == 'τ'
                || c == 'σ' || c == 'ρ' || c == 'ψ';
    }

    private int findMatchingBrace(String text, int openIndex) {
        if (text == null || openIndex < 0 || openIndex >= text.length() || text.charAt(openIndex) != '{') return -1;
        int depth = 0;
        for (int i = openIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private String normalizeFormulaText(String text) {
        if (text == null || text.length() == 0) return "";
        String out = text.replace("\r\n", "\n").replace('\r', '\n');
        out = normalizeFormulaFractions(out);
        out = normalizeFormulaSquareRoots(out);
        out = normalizeOneGroupCommand(out, "\\mathrm", "", "");
        out = normalizeOneGroupCommand(out, "\\operatorname", "", "");
        out = out.replace("\\begin{aligned}", "");
        out = out.replace("\\end{aligned}", "");
        out = out.replace("\\begin{cases}", "");
        out = out.replace("\\end{cases}", "");
        out = out.replace("\\left", "");
        out = out.replace("\\right", "");
        out = out.replace("\\,", "");
        out = out.replace("\\;", "");
        out = out.replace("\\!", "");
        out = out.replace("\\\\", "\n");
        out = out.replace("&=", "=");
        out = out.replace("&", "");
        out = out.replace("\\cdot", "·");
        out = out.replace("\\times", "×");
        out = out.replace("\\pm", "±");
        out = out.replace("\\infty", "∞");
        out = out.replace("\\omega", "ω");
        out = out.replace("\\Omega", "Ω");
        out = out.replace("\\pi", "π");
        out = out.replace("\\theta", "θ");
        out = out.replace("\\delta", "δ");
        out = out.replace("\\lambda", "λ");
        out = out.replace("\\alpha", "α");
        out = out.replace("\\beta", "β");
        out = out.replace("\\gamma", "γ");
        out = out.replace("\\mu", "μ");
        out = out.replace("\\varphi", "φ");
        out = out.replace("\\phi", "φ");
        out = out.replace("\\tau", "τ");
        out = out.replace("\\sigma", "σ");
        out = out.replace("\\rho", "ρ");
        out = out.replace("\\psi", "ψ");
        out = out.replace("\\sin", "sin");
        out = out.replace("\\cos", "cos");
        out = out.replace("\\tan", "tan");
        out = out.replace("\\ln", "ln");
        out = out.replace("\\log", "log");
        out = out.replace("\\exp", "exp");
        out = out.replace("\\int", "∫");
        out = out.replace("\\sum", "∑");
        out = out.replace("\\leq", "≤");
        out = out.replace("\\le", "≤");
        out = out.replace("\\geq", "≥");
        out = out.replace("\\ge", "≥");
        out = out.replace("\\neq", "≠");
        out = out.replace("\\approx", "≈");
        out = out.replace("\\rightarrow", "→");
        out = out.replace("\\leftarrow", "←");
        out = out.replace("\\to", "→");
        out = out.replace("\\", "");
        return out.replaceAll("[ \\t\\x0B\\f]+", " ").trim();
    }

    private String normalizeFormulaFractions(String value) {
        return normalizeTwoGroupCommand(value, "\\frac", "(", ")/(", ")");
    }

    private String normalizeFormulaSquareRoots(String value) {
        return normalizeOneGroupCommand(value, "\\sqrt", "√(", ")");
    }

    private String normalizeOneGroupCommand(String value, String command, String prefix, String suffix) {
        if (value == null || value.length() == 0) return "";
        StringBuilder out = new StringBuilder();
        int index = 0;
        while (index < value.length()) {
            int start = value.indexOf(command + "{", index);
            if (start < 0) {
                out.append(value.substring(index));
                break;
            }
            out.append(value, index, start);
            int groupStart = start + command.length();
            int groupEnd = findMatchingBrace(value, groupStart);
            if (groupEnd < 0) {
                out.append(value.substring(start));
                break;
            }
            String body = value.substring(groupStart + 1, groupEnd);
            out.append(prefix).append(body).append(suffix);
            index = groupEnd + 1;
        }
        return out.toString();
    }

    private String normalizeTwoGroupCommand(String value, String command, String prefix, String middle, String suffix) {
        if (value == null || value.length() == 0) return "";
        StringBuilder out = new StringBuilder();
        int index = 0;
        while (index < value.length()) {
            int start = value.indexOf(command + "{", index);
            if (start < 0) {
                out.append(value.substring(index));
                break;
            }
            out.append(value, index, start);
            int firstStart = start + command.length();
            int firstEnd = findMatchingBrace(value, firstStart);
            if (firstEnd < 0 || firstEnd + 1 >= value.length() || value.charAt(firstEnd + 1) != '{') {
                out.append(value.substring(start));
                break;
            }
            int secondStart = firstEnd + 1;
            int secondEnd = findMatchingBrace(value, secondStart);
            if (secondEnd < 0) {
                out.append(value.substring(start));
                break;
            }
            String first = value.substring(firstStart + 1, firstEnd);
            String second = value.substring(secondStart + 1, secondEnd);
            out.append(prefix).append(first).append(middle).append(second).append(suffix);
            index = secondEnd + 1;
        }
        return out.toString();
    }

    private String stripFormulaGrouping(String token) {
        if (token == null) return "";
        String out = token.trim();
        while (out.startsWith("{") && out.endsWith("}") && findMatchingBrace(out, 0) == out.length() - 1) {
            out = out.substring(1, out.length() - 1).trim();
        }
        return normalizeFormulaText(out);
    }

    private void paintSubmittedOptions(Question q) {
        String ans = answerString(q);
        for (int i = 0; i < optionList.getChildCount(); i++) {
            View child = optionList.getChildAt(i);
            String key = String.valueOf(child.getTag());
            if (ans.contains(key)) {
                child.setBackground(optionResultBackground(true));
            } else if (selected.contains(key)) {
                child.setBackground(optionResultBackground(false));
            } else {
                child.setBackground(optionBackground(false));
            }
        }
    }

    private void revealRememberModeAnswer(Question q) {
        if (!"blank".equals(q.type) && !isEssayQuestion(q)) {
            paintSubmittedOptions(q);
        }
        showRememberReason(q);
    }

    private void showRememberReason(Question q) {
        if (memoryReasonContainer == null) return;
        if (!q.answerImages.isEmpty()) {
            memoryReasonContainer.removeAllViews();
            for (String path : q.answerImages) {
                addAssetImage(memoryReasonContainer, path);
            }
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) memoryReasonContainer.getLayoutParams();
            if (lp != null) {
                lp.topMargin = dp(12);
                lp.bottomMargin = dp(8);
                memoryReasonContainer.setLayoutParams(lp);
            }
            memoryReasonContainer.setVisibility(View.VISIBLE);
            return;
        }
        String markdown = rememberReasonMarkdown(q);
        if (markdown.length() == 0) {
            memoryReasonContainer.removeAllViews();
            memoryReasonContainer.setVisibility(View.GONE);
            return;
        }
        renderQuestionMarkdown(memoryReasonContainer, "## 理由与辨析\n\n" + markdown, q);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) memoryReasonContainer.getLayoutParams();
        if (lp != null) {
            lp.topMargin = dp(12);
            lp.bottomMargin = dp(8);
            memoryReasonContainer.setLayoutParams(lp);
        }
        memoryReasonContainer.setVisibility(View.VISIBLE);
    }

    private String rememberReasonMarkdown(Question q) {
        String quick = markdownizeExplanationBlock(q.quickExplanation == null ? "" : q.quickExplanation.trim());
        if (isEssayQuestion(q)) {
            StringBuilder sb = new StringBuilder();
            sb.append("- **参考答案：** ").append(displayAnswer(q)).append("\n\n");
            if (quick.length() > 0) {
                sb.append(quick).append("\n\n");
            }
            String detail = markdownizeExplanationBlock(q.knowledgeDetail == null ? "" : q.knowledgeDetail.trim());
            if (detail.length() > 0) {
                sb.append("### 知识点与推导\n\n").append(detail);
            }
            return sb.toString().trim();
        }
        if (quick.length() > 0) {
            return quick;
        }
        String detail = markdownizeExplanationBlock(q.knowledgeDetail == null ? "" : q.knowledgeDetail.trim());
        if (detail.length() > 0) {
            return detail;
        }
        String reason = lineValue(q.explanation, "理由：");
        if (reason.length() == 0) {
            reason = quickTipForQuestion(q);
        }
        if (reason.length() == 0) {
            return "";
        }
        return "- **理由：** " + reason;
    }

    private void move(int delta) {
        if (cardMode) {
            moveCard(delta);
            return;
        }
        moveQuestionAnimated(delta);
    }

    private void moveQuestionAnimated(final int delta) {
        if (visibleQuestions.isEmpty()) return;
        final int next = currentIndex + delta;
        if (next < 0 || next >= visibleQuestions.size()) {
            settleQuestionDrag();
            Toast.makeText(this, delta > 0 ? "已经是最后一题" : "已经是第一题", Toast.LENGTH_SHORT).show();
            return;
        }
        if (questionPageAnimating || scrollView == null) return;
        resetQuestionZoom(false);
        questionPageAnimating = true;
        prepareQuestionSwipePreview(delta);
        final float pageWidth = Math.max(dp(320), getWindow().getDecorView().getWidth());
        final float exitX = delta > 0 ? -pageWidth * 0.96f : pageWidth * 0.96f;
        scrollView.animate()
                .translationX(exitX)
                .alpha(0f)
                .scaleY(0.985f)
                .setDuration(180)
                .setInterpolator(new AccelerateInterpolator(1.15f))
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        currentIndex = next;
                        renderQuestion();
                        scrollView.scrollTo(0, 0);
                        scrollView.setTranslationX(-exitX);
                        scrollView.setAlpha(0f);
                        scrollView.setScaleY(0.985f);
                        prepareQuestionSwipePreview(-delta);
                        scrollView.animate()
                                .translationX(0f)
                                .alpha(1f)
                                .scaleY(1f)
                                .setDuration(260)
                                .setInterpolator(new DecelerateInterpolator(1.35f))
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        resetQuestionPageSurface();
                                        hideQuestionSwipePreview();
                                        questionPageAnimating = false;
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    private void settleQuestionDrag() {
        if (scrollView == null) return;
        scrollView.animate()
                .translationX(0f)
                .alpha(1f)
                .scaleY(1f)
                .setDuration(160)
                .setInterpolator(new DecelerateInterpolator(1.4f))
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        resetQuestionPageSurface();
                        hideQuestionSwipePreview();
                    }
                })
                .start();
    }

    private void recordAttempt(Question q, boolean ok) {
        int attempts = prefs.getInt("attempt_" + q.label, 0) + 1;
        int correct = prefs.getInt("correct_" + q.label, 0) + (ok ? 1 : 0);
        prefs.edit().putInt("attempt_" + q.label, attempts).putInt("correct_" + q.label, correct).apply();
    }

    private Set<String> getWrongSet() {
        return new HashSet<>(prefs.getStringSet(coursePrefName("wrong"), new HashSet<>()));
    }

    private void addWrong(String label) {
        Set<String> wrong = getWrongSet();
        wrong.add(label);
        prefs.edit().putStringSet(coursePrefName("wrong"), wrong).putInt(wrongMasteryKey(label), 0).apply();
    }

    private void removeWrong(String label) {
        Set<String> wrong = getWrongSet();
        if (wrong.remove(label)) {
            prefs.edit().putStringSet(coursePrefName("wrong"), wrong).remove(wrongMasteryKey(label)).apply();
        }
    }

    private void updateWrongBookState(Question q, boolean ok) {
        if (!ok) {
            addWrong(q.label);
            return;
        }
        if (!getWrongSet().contains(q.label)) {
            return;
        }
        if (!wrongMode) {
            return;
        }
        int required = wrongRequiredCorrectCount();
        int progress = getWrongMasteryCount(q.label) + 1;
        if (progress >= required) {
            removeWrong(q.label);
        } else {
            prefs.edit().putInt(wrongMasteryKey(q.label), progress).apply();
        }
    }

    private String wrongBookProgressText(Question q, boolean ok) {
        int required = wrongRequiredCorrectCount();
        if (!ok) {
            return "\n错题本规则：已加入错题本；连续答对进度重置为 0/" + required + "。";
        }
        boolean isWrong = getWrongSet().contains(q.label);
        if (wrongMode) {
            if (isWrong) {
                int progress = getWrongMasteryCount(q.label);
                return "\n错题本规则：已在错题本答对 " + progress + "/" + required + "；再连续答对 "
                        + Math.max(0, required - progress) + " 次才会移出。";
            }
            return "\n错题本规则：已在错题本连续答对 " + required + "/" + required + "，已移出错题本。";
        }
        if (isWrong) {
            return "\n错题本规则：普通刷题答对不会移出错题；请到错题本连续答对 " + required + " 次。";
        }
        return "\n错题本规则：本题当前不在错题本。";
    }

    private String shortProgressToast(Question q, boolean ok) {
        int required = wrongRequiredCorrectCount();
        if (!ok) return "已加入错题本";
        if (wrongMode && !getWrongSet().contains(q.label)) return "错题已答对 " + required + "/" + required + "，已移出";
        if (wrongMode && getWrongSet().contains(q.label)) return "错题答对 " + getWrongMasteryCount(q.label) + "/" + required;
        if (getWrongSet().contains(q.label)) return "普通模式答对，不移出错题本";
        return "回答正确";
    }

    private String wrongMasteryKey(String label) {
        return coursePrefName("wrong_mastery_" + label);
    }

    private int getWrongMasteryCount(String label) {
        return prefs.getInt(wrongMasteryKey(label), 0);
    }

    private int wrongRequiredCorrectCount() {
        int value = prefs.getInt(PREF_WRONG_REQUIRED, DEFAULT_WRONG_REQUIRED);
        if (value < MIN_WRONG_REQUIRED || value > MAX_WRONG_REQUIRED) {
            value = DEFAULT_WRONG_REQUIRED;
            prefs.edit().putInt(PREF_WRONG_REQUIRED, value).apply();
        }
        return value;
    }

    private void setWrongRequiredCorrectCount(int value) {
        int safeValue = Math.max(MIN_WRONG_REQUIRED, Math.min(MAX_WRONG_REQUIRED, value));
        prefs.edit().putInt(PREF_WRONG_REQUIRED, safeValue).apply();
    }

    private void showSettingsDialog() {
        showSettingsMode();
    }

    private void updateWrongRequiredSettingText(TextView target) {
        int required = wrongRequiredCorrectCount();
        target.setText("当前设定：连续答对 " + required + " 次后移出错题本");
    }

    private void adjustWrongRequiredCorrectCount(int delta, TextView countText) {
        int before = wrongRequiredCorrectCount();
        int after = Math.max(MIN_WRONG_REQUIRED, Math.min(MAX_WRONG_REQUIRED, before + delta));
        setWrongRequiredCorrectCount(after);
        updateWrongRequiredSettingText(countText);
        refreshAfterWrongRequiredChanged();
        if (after == before) {
            Toast.makeText(this, after == MIN_WRONG_REQUIRED ? "已经是最小 1 次" : "已经是最大 10 次", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "已设置为连续答对 " + after + " 次移出错题本", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshAfterWrongRequiredChanged() {
        if (settingsMode) {
            renderQuestion();
            return;
        }
        if (visibleQuestions.isEmpty()) {
            refreshEmptyMeta();
        } else {
            refreshMeta(currentQuestion());
            if (submitted && lastAnswerOk != null) {
                Question q = currentQuestion();
                renderQuestionMarkdown(feedbackContainer, buildFeedbackMarkdown(q, lastAnswerOk), q);
            }
        }
    }

    private void addSettingsDivider(LinearLayout box) {
        View divider = new View(this);
        divider.setBackgroundColor(Color.rgb(55, 58, 68));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(1));
        lp.topMargin = dp(16);
        lp.bottomMargin = dp(12);
        box.addView(divider, lp);
    }

    private void addSettingsAction(LinearLayout box, String label, View.OnClickListener listener) {
        Button button = bigButton(label, false);
        button.setOnClickListener(listener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(46));
        lp.bottomMargin = dp(8);
        box.addView(button, lp);
    }

    private void showChapterCardsDialog() {
        final List<String> chapters = chapterList();
        final List<String> items = new ArrayList<>();
        items.add("全部章节思维导图");
        for (int i = 0; i < chapters.size(); i++) {
            items.add(chapters.get(i));
        }
        int checked = currentCardChapter == null ? 0 : Math.max(0, items.indexOf(currentCardChapter));
        showChoiceSheet("打开章节思维导图", items, checked, new ChoiceHandler() {
            @Override
            public void onChosen(int which, String item) {
                if (which == 0) {
                    showCardMode(null);
                } else {
                    showCardMode(chapters.get(which - 1));
                }
            }
        });
    }
    private void exportWrongQuestions() {
        try {
            Set<String> wrong = getWrongSet();
            String md = buildWrongMarkdown(wrong);
            shareMarkdownFile(
                    ExportProvider.EXPORT_NAME,
                    currentCourseName() + "错题本",
                    "分享错题本",
                    md,
                    currentCourseName() + "错题本 Markdown 文件见附件。",
                    "已生成错题本 Markdown，并打开分享"
            );
        } catch (Exception e) {
            Toast.makeText(this, "导出失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void copyFeedbackTemplate() {
        String text = buildFeedbackTemplate();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("备考宝典反馈建议", text));
        }
        Toast.makeText(this, "已复制建议模板", Toast.LENGTH_SHORT).show();
    }

    private void shareFeedbackTemplate() {
        try {
            String text = buildFeedbackTemplate();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboard != null) {
                clipboard.setPrimaryClip(ClipData.newPlainText("备考宝典反馈建议", text));
            }
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_SUBJECT, "备考宝典反馈建议");
            send.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(send, "发送反馈建议"));
            Toast.makeText(this, "已复制模板，并打开分享", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "分享建议失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void submitSuggestionToGithub(String draft) {
        String body = buildSuggestionIssueBody(draft);
        if (body.trim().length() == 0) {
            Toast.makeText(this, "先写一点建议内容再提交", Toast.LENGTH_SHORT).show();
            return;
        }
        saveSuggestionDraft(draft);
        copyTextToClipboard("备考宝典反馈建议", body);
        openUrl(buildGithubIssueUrl(body));
        Toast.makeText(this, "已打开 GitHub 留言页，内容也已复制", Toast.LENGTH_LONG).show();
    }

    private void copySuggestionDraft(String draft) {
        String body = buildSuggestionIssueBody(draft);
        if (body.trim().length() == 0) {
            Toast.makeText(this, "先写一点建议内容再复制", Toast.LENGTH_SHORT).show();
            return;
        }
        saveSuggestionDraft(draft);
        copyTextToClipboard("备考宝典反馈建议", body);
        Toast.makeText(this, "已复制建议内容", Toast.LENGTH_SHORT).show();
    }

    private void shareSuggestionDraft(String draft) {
        try {
            String body = buildSuggestionIssueBody(draft);
            if (body.trim().length() == 0) {
                Toast.makeText(this, "先写一点建议内容再分享", Toast.LENGTH_SHORT).show();
                return;
            }
            saveSuggestionDraft(draft);
            copyTextToClipboard("备考宝典反馈建议", body);
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_SUBJECT, suggestionIssueTitle(draft));
            send.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(Intent.createChooser(send, "发送反馈建议"));
            Toast.makeText(this, "已复制建议，并打开分享", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "分享建议失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void copyTextToClipboard(String label, String value) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText(label, value));
        }
    }

    private void saveSuggestionDraft(String draft) {
        if (prefs == null) return;
        prefs.edit().putString(PREF_SUGGESTION_DRAFT, draft == null ? "" : draft).apply();
    }

    private String buildSuggestionIssueBody(String draft) {
        String trimmed = draft == null ? "" : draft.trim();
        if (trimmed.length() == 0) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("## 用户留言\n\n");
        sb.append(trimmed).append("\n\n");
        sb.append(buildFeedbackContext());
        return sb.toString();
    }

    private String buildFeedbackContext() {
        StringBuilder sb = new StringBuilder();
        sb.append("## App 上下文\n\n");
        sb.append("- App 版本：").append(currentVersionSummary()).append("\n");
        sb.append("- 当前课程：").append(currentCourseName()).append("\n");
        sb.append("- 当前页面：").append(currentPageSummary()).append("\n");
        sb.append("- 当前筛选：").append(activeFilterText()).append("\n");
        sb.append("- 当前题目：").append(currentQuestionSummaryForFeedback()).append("\n");
        return sb.toString();
    }

    private String buildGithubIssueUrl(String body) {
        String repo = DEFAULT_UPDATE_REPO_SLUG;
        String title = suggestionIssueTitle(body);
        String issueBody = body == null ? "" : body;
        if (issueBody.length() > 3800) {
            issueBody = issueBody.substring(0, 3800) + "\n\n（内容较长，已截断；完整内容已复制到剪贴板。）";
        }
        return "https://github.com/" + repo + "/issues/new"
                + "?title=" + encodeUrlQueryParam(title)
                + "&body=" + encodeUrlQueryParam(issueBody);
    }

    private String suggestionIssueTitle(String value) {
        String text = value == null ? "" : value.trim();
        if (text.startsWith("## 用户留言")) {
            int marker = text.indexOf("\n\n");
            if (marker >= 0) text = text.substring(marker + 2).trim();
        }
        String firstLine = text.split("\\r?\\n", 2)[0].trim();
        if (firstLine.length() == 0) firstLine = "反馈建议";
        if (firstLine.length() > 34) firstLine = firstLine.substring(0, 34) + "...";
        return "反馈建议：" + firstLine;
    }

    private String buildFeedbackTemplate() {
        StringBuilder sb = new StringBuilder();
        sb.append("# 备考宝典反馈建议\n\n");
        sb.append(buildFeedbackContext()).append("\n");
        sb.append("## 问题描述\n\n");
        sb.append("请在这里写：哪里不好用、哪里显示不对、或你希望新增什么。\n\n");
        sb.append("## 截图情况\n\n");
        sb.append("请写：是否有截图、截图里大概是哪一页/哪一题。\n\n");
        sb.append("## 期望效果\n\n");
        sb.append("请写：你希望它改成什么样。");
        return sb.toString();
    }

    private String currentPageSummary() {
        if (homeMode) return "课程首页";
        if (settingsMode) return "设置";
        if (suggestionsMode) return "建议";
        if (cardMode) return "导图";
        if (rememberMode) return "记题";
        if (wrongMode) return "错题";
        return "刷题";
    }

    private String currentQuestionSummaryForFeedback() {
        if (visibleQuestions.isEmpty()) {
            return "无";
        }
        int index = clampIndex(currentIndex, visibleQuestions.size());
        Question q = visibleQuestions.get(index);
        return q.label + " / " + q.typeName + " / " + q.chapter;
    }

    private String defaultExportPromptTemplate() {
        return "请你像“备考宝典：" + currentCourseName() + "”的老师一样，帮我把这道题讲到小白能懂。\n"
                + "请基于 App 的短解析和知识点详解继续展开：题眼是什么、为什么这样选、其他常见想法错在哪里、我应该怎么记。";
    }

    private String exportPromptTemplate() {
        String value = prefs.getString(PREF_EXPORT_PROMPT_TEMPLATE, "");
        if (value == null || value.trim().length() == 0) {
            return defaultExportPromptTemplate();
        }
        return value;
    }

    private String exportPromptSummary() {
        String value = prefs.getString(PREF_EXPORT_PROMPT_TEMPLATE, "");
        if (value == null || value.trim().length() == 0) {
            return "默认";
        }
        return "自定义";
    }

    private String applyExportPromptTemplate(Question q) {
        String text = exportPromptTemplate();
        if (q == null) return text;
        return text
                .replace("{label}", safeText(q.label))
                .replace("{type}", safeText(q.typeName))
                .replace("{chapter}", safeText(q.chapter))
                .replace("{knowledge}", safeText(q.knowledge))
                .replace("{answer}", safeText(displayAnswer(q)))
                .replace("{stem}", safeText(q.stem))
                .replace("{course}", safeText(currentCourseName()));
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private void showExportPromptConfigDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        FrameLayout shell = new FrameLayout(this);
        shell.setPadding(dp(18), statusBarInset() + dp(18), dp(18), bottomSafeInset() + dp(14));
        shell.setBackgroundColor(Color.argb(THEME_LIGHT.equals(themeMode) ? 54 : 84, 11, 17, 26));

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(18), dp(18), dp(18), dp(16));
        panel.setBackground(choiceSheetBackground());
        panel.setElevation(dp(12));
        shell.addView(panel, new FrameLayout.LayoutParams(-1, -2, Gravity.CENTER));

        TextView title = text("题目导出提示词", 18, TEXT, true);
        panel.addView(title, new LinearLayout.LayoutParams(-1, -2));

        final EditText input = new EditText(this);
        input.setText(exportPromptTemplate());
        input.setHint("可用 {label}、{type}、{chapter}、{knowledge}、{answer}、{stem}");
        input.setSingleLine(false);
        input.setMinLines(5);
        input.setMaxLines(9);
        input.setGravity(Gravity.TOP | Gravity.START);
        input.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setTextColor(TEXT);
        input.setHintTextColor(MUTED);
        input.setPadding(dp(14), dp(12), dp(14), dp(12));
        input.setBackground(roundedStrokeBackground(PANEL, GLASS_STROKE, 18, 1));
        input.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    requestNoIntercept(true);
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    requestNoIntercept(false);
                }
                return false;
            }
        });
        LinearLayout.LayoutParams inputLp = new LinearLayout.LayoutParams(-1, -2);
        inputLp.topMargin = dp(14);
        panel.addView(input, inputLp);

        Button saveButton = bigButton("保存", true);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = input.getText().toString().trim();
                SharedPreferences.Editor editor = prefs.edit();
                if (value.length() == 0 || value.equals(defaultExportPromptTemplate())) {
                    editor.remove(PREF_EXPORT_PROMPT_TEMPLATE);
                } else {
                    editor.putString(PREF_EXPORT_PROMPT_TEMPLATE, value);
                }
                editor.apply();
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "题目导出提示词已保存", Toast.LENGTH_SHORT).show();
                renderQuestion();
            }
        });
        LinearLayout.LayoutParams saveLp = new LinearLayout.LayoutParams(-1, dp(46));
        saveLp.topMargin = dp(14);
        panel.addView(saveButton, saveLp);

        Button resetButton = bigButton("恢复默认", false);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setText(defaultExportPromptTemplate());
                prefs.edit().remove(PREF_EXPORT_PROMPT_TEMPLATE).apply();
                if (exportPromptLineView != null) {
                    exportPromptLineView.setText("题目导出提示词 · " + exportPromptSummary());
                }
                Toast.makeText(MainActivity.this, "已恢复默认提示词", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout.LayoutParams resetLp = new LinearLayout.LayoutParams(-1, dp(46));
        resetLp.topMargin = dp(8);
        panel.addView(resetButton, resetLp);

        Button closeButton = chromeButton("关闭");
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(-1, dp(44));
        closeLp.topMargin = dp(8);
        panel.addView(closeButton, closeLp);

        dialog.setContentView(shell);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    private void shareCurrentQuestion() {
        if (cardMode) {
            Toast.makeText(this, "当前是思维导图模式，请回到题目后再分享本题", Toast.LENGTH_SHORT).show();
            return;
        }
        if (visibleQuestions.isEmpty()) {
            Toast.makeText(this, "当前没有可导出的题目", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Question q = currentQuestion();
            String text = buildCurrentQuestionShareText(q);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboard != null) {
                clipboard.setPrimaryClip(ClipData.newPlainText("备考宝典：" + currentCourseName() + "当前题目", text));
            }

            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_SUBJECT, "备考宝典：" + currentCourseName() + "题目 " + q.label);
            send.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(send, "发送当前题目给 AI / QQ"));
            Toast.makeText(this, "已复制当前题目，并打开分享", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "导出本题失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void shareMarkdownFile(String fileName, String subject, String chooserTitle,
                                   String md, String longTextHint, String successToast) throws Exception {
        File dir = new File(getCacheDir(), "exports");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, safeExportFileName(fileName));
        FileOutputStream out = new FileOutputStream(file);
        out.write(md.getBytes(StandardCharsets.UTF_8));
        out.close();

        Uri uri = Uri.parse("content://" + ExportProvider.AUTHORITY + "/" + Uri.encode(file.getName()));
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/markdown");
        send.putExtra(Intent.EXTRA_SUBJECT, subject);
        send.putExtra(Intent.EXTRA_TEXT, md.length() > 3000 ? longTextHint : md);
        send.putExtra(Intent.EXTRA_STREAM, uri);
        send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(send, chooserTitle));
        Toast.makeText(this, successToast, Toast.LENGTH_SHORT).show();
    }

    private String safeExportFileName(String fileName) {
        String safe = fileName == null ? "" : fileName.replaceAll("[\\\\/:*?\"<>|]+", "_").trim();
        if (safe.length() == 0) {
            safe = "export.md";
        }
        if (!safe.endsWith(".md")) {
            safe = safe + ".md";
        }
        return safe;
    }

    private String buildCurrentQuestionShareText(Question q) {
        StringBuilder sb = new StringBuilder();
        sb.append(applyExportPromptTemplate(q).trim()).append("\n\n");
        sb.append("# 备考宝典：").append(currentCourseName()).append(" ").append(q.label).append(" ").append(q.typeName).append("\n\n");
        sb.append("- 章节：").append(q.chapter).append("\n");
        sb.append("- 知识点：").append(q.knowledge).append("\n");
        sb.append("- 正确答案：").append(displayAnswer(q)).append("\n\n");
        sb.append("## 题目\n\n").append(q.stem).append("\n\n");
        if (!q.options.isEmpty()) {
            sb.append("## 选项\n\n");
            for (Option opt : q.options) {
                sb.append("- ").append(opt.text).append("\n");
            }
            sb.append("\n");
        }
        sb.append("## App 短解析与知识点详解\n\n").append(explanationText(q)).append("\n");
        return sb.toString();
    }

    private String buildWrongMarkdown(Set<String> wrong) {
        StringBuilder sb = new StringBuilder();
        int required = wrongRequiredCorrectCount();
        sb.append("# ").append(currentCourseName()).append("错题本\n\n");
        sb.append("- 导出时间：").append(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date())).append("\n");
        sb.append("- 错题数量：").append(wrong.size()).append("\n\n");
        sb.append("- 当前移除规则：错题必须在错题本模式中连续答对 ").append(required).append(" 次才会移出。\n\n");
        if (wrong.isEmpty()) {
            sb.append("当前没有错题。\n");
            return sb.toString();
        }
        List<Question> list = new ArrayList<>();
        for (Question q : allQuestions) {
            if (wrong.contains(q.label)) list.add(q);
        }
        for (Question q : list) {
            sb.append("## ").append(q.label).append(" ").append(q.typeName).append("\n\n");
            sb.append("- 知识点：").append(q.chapter).append(" / ").append(q.knowledge).append("\n");
            sb.append("- 正确答案：").append(displayAnswer(q)).append("\n\n");
            sb.append("- 错题本连续答对进度：").append(getWrongMasteryCount(q.label)).append("/").append(required).append("\n\n");
            sb.append(q.stem).append("\n\n");
            if (!q.options.isEmpty()) {
                for (Option opt : q.options) {
                    sb.append("- ").append(opt.text).append("\n");
                }
                sb.append("\n");
            }
            sb.append("**解析：**\n\n").append(explanationText(q)).append("\n\n");
        }
        return sb.toString();
    }

    private void exportChapterCard(String chapter) {
        try {
            String md = buildChapterMemoryCard(chapter);
            String fileName = safeExportFileName(chapter.replace(". ", "_") + "_思维导图.md");
            shareMarkdownFile(
                    fileName,
                    chapter + " 思维导图",
                    "分享章节思维导图",
                    md,
                    chapter + " 思维导图 Markdown 文件见附件。",
                    "已生成章节思维导图，并打开分享"
            );
        } catch (Exception e) {
            Toast.makeText(this, "导出章节导图失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void exportAllChapterCards() {
        try {
            String md = buildAllChapterCardsMarkdown();
            shareMarkdownFile(
                    "计算机网络_章节思维导图全集.md",
                    "计算机网络章节思维导图全集",
                    "分享全部章节思维导图",
                    md,
                    "计算机网络章节思维导图全集 Markdown 文件见附件。",
                    "已生成全部章节思维导图，并打开分享"
            );
        } catch (Exception e) {
            Toast.makeText(this, "导出全部导图失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String buildAllChapterCardsMarkdown() {
        if (!allMemoryCards.isEmpty()) {
            List<String> labels = allCardLabels();
            StringBuilder sb = new StringBuilder();
            sb.append("# 备考宝典：计算机网络章节思维导图全集\n\n");
            sb.append("- 覆盖题目：").append(labels.size()).append(" 题\n");
            sb.append("- 覆盖章节：").append(cardChapterList().size()).append(" 章\n");
            sb.append("- 覆盖知识点导图：").append(allMemoryCards.size()).append(" 组\n");
            sb.append("- 生成方式：使用 App 内置 `chapter_cards.json`，每组导图都包含底层知识点、必背标准词/数字、易错开关和逐题覆盖线索。\n\n");
            sb.append("## 覆盖总览\n\n");
            for (String chapter : cardChapterList()) {
                List<MemoryCard> cards = memoryCardsInChapter(chapter);
                int questionCount = 0;
                for (MemoryCard card : cards) {
                    if (!card.overviewCard) {
                        questionCount += card.questionCount;
                    }
                }
                sb.append("- ").append(chapter).append("：").append(questionCount)
                        .append(" 题，").append(cards.size()).append(" 组导图\n");
            }
            sb.append("\n");
            for (String chapter : cardChapterList()) {
                sb.append(buildChapterMemoryCard(chapter)).append("\n\n");
            }
            return sb.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("# 计算机网络考试练习：章节思维导图全集\n\n");
        sb.append("- 覆盖题目：").append(allQuestions.size()).append(" 题\n");
        sb.append("- 覆盖章节：").append(chapterList().size()).append(" 章\n");
        sb.append("- 覆盖知识点：").append(knowledgePairCount(allQuestions)).append(" 个\n");
        sb.append("- 生成方式：直接从 App 内置 ").append(allQuestions.size()).append(" 题题库按章节与知识点分组生成。\n\n");
        sb.append("## 覆盖总览\n\n");
        for (String chapter : chapterList()) {
            sb.append("- ").append(chapter).append("：").append(questionsInChapter(chapter).size()).append(" 题，")
                    .append(groupByKnowledge(questionsInChapter(chapter)).size()).append(" 个知识点\n");
        }
        sb.append("\n");
        for (String chapter : chapterList()) {
            sb.append(buildChapterMemoryCard(chapter)).append("\n\n");
        }
        return sb.toString();
    }

    private List<MemoryCard> buildMemoryCards(String chapter) {
        if (!allMemoryCards.isEmpty()) {
            List<MemoryCard> cards = new ArrayList<>();
            for (MemoryCard card : allMemoryCards) {
                if (chapter == null) {
                    cards.add(card);
                } else if (chapter.equals(card.chapter)) {
                    cards.add(card);
                }
            }
            return cards;
        }
        List<Question> source = new ArrayList<>();
        for (Question q : allQuestions) {
            if (chapter == null || chapter.equals(q.chapter)) {
                source.add(q);
            }
        }
        Map<String, List<Question>> groups = groupByKnowledge(source);
        List<MemoryCard> cards = new ArrayList<>();
        for (Map.Entry<String, List<Question>> entry : groups.entrySet()) {
            List<Question> qs = entry.getValue();
            if (qs.isEmpty()) continue;
            String cardChapter = qs.get(0).chapter;
            String knowledge = entry.getKey();
            String labels = joinLabels(qs);
            String front = buildMemoryCardFront(cardChapter, knowledge, qs, labels);
            String back = buildMemoryCardBack(cardChapter, knowledge, qs, labels);
            List<String> eyeLines = collectLineValues(qs, "题眼：", 4);
            List<String> traps = collectLineValues(qs, "易错：", 4);
            if (traps.isEmpty()) {
                traps = collectLineValues(qs, "本题陷阱：", 4);
            }
            List<String> corePoints = new ArrayList<>();
            String summary = compactModelSummary(qs);
            if (summary.length() > 0) {
                corePoints.add(summary);
            }
            cards.add(new MemoryCard(
                    cardChapter,
                    knowledge,
                    qs.size(),
                    labels,
                    knowledgeLayerHint(cardChapter, knowledge),
                    chapterLayerHint(cardChapter),
                    typeDistribution(qs),
                    eyeLines,
                    selfCheckPrompts(cardChapter, knowledge),
                    corePoints,
                    new ArrayList<String>(),
                    traps,
                    new ArrayList<String>(),
                    front,
                    back));
        }
        return cards;
    }

    private List<MemoryCard> memoryCardsInChapter(String chapter) {
        List<MemoryCard> cards = new ArrayList<>();
        for (MemoryCard card : allMemoryCards) {
            if (chapter.equals(card.chapter)) {
                cards.add(card);
            }
        }
        return cards;
    }

    private String buildMemoryCardMarkdown(MemoryCard card, int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("### 导图 ").append(index).append("：").append(card.knowledge).append("\n\n");
        sb.append("- 覆盖题目：").append(card.questionCount).append(" 题\n");
        sb.append("- 题型分布：").append(card.typeDistribution).append("\n");
        sb.append("- 题号：").append(card.labels).append("\n\n");
        sb.append("**导图重点：看到什么题眼要想到它？**\n\n");
        appendLimitedBullets(sb, card.eyeLines);
        sb.append("\n**底层知识点：小白必须掌握什么？**\n\n");
        sb.append("- 层次定位：").append(card.layerHint).append("\n");
        appendLimitedBullets(sb, card.corePoints);
        sb.append("\n**必须原样背 / 会算**\n\n");
        appendLimitedBullets(sb, card.mustRemember);
        sb.append("\n**易错开关**\n\n");
        appendLimitedBullets(sb, card.traps);
        sb.append("\n**逐题覆盖线索**\n\n");
        appendLimitedBullets(sb, card.questionTips);
        sb.append("\n");
        return sb.toString();
    }

    private String buildChapterCardsMarkdownFromAsset(String chapter, List<MemoryCard> cards) {
        StringBuilder sb = new StringBuilder();
        int questionCount = 0;
        for (MemoryCard card : cards) {
            if (!card.overviewCard) {
                questionCount += card.questionCount;
            }
        }
        sb.append("# ").append(chapter).append(" 思维导图\n\n");
        sb.append("- 覆盖题目：").append(questionCount).append(" 题\n");
        sb.append("- 覆盖知识点：").append(cards.size()).append(" 个\n");
        if (!cards.isEmpty()) {
            sb.append("- 本章总地图：").append(cards.get(0).chapterMap).append("\n");
        }
        sb.append("- 生成方式：使用 App 内置 `chapter_cards.json`，逐题覆盖线索覆盖本章全部题号。\n\n");
        int index = 1;
        for (MemoryCard card : cards) {
            sb.append(buildMemoryCardMarkdown(card, index++));
        }
        return sb.toString();
    }

    private List<String> cardChapterList() {
        Set<String> seen = new LinkedHashSet<>();
        for (MemoryCard card : allMemoryCards) {
            if (card.chapter != null && card.chapter.length() > 0) {
                seen.add(card.chapter);
            }
        }
        return new ArrayList<>(seen);
    }

    private List<String> allCardLabels() {
        List<String> labels = new ArrayList<>();
        for (MemoryCard card : allMemoryCards) {
            if (card.labels.length() > 0) {
                String[] parts = card.labels.split("、");
                for (String part : parts) {
                    String label = part.trim();
                    if (label.length() > 0) labels.add(label);
                }
            }
        }
        return labels;
    }

    private List<String> selfCheckPrompts(String chapter, String knowledge) {
        List<String> prompts = new ArrayList<>();
        prompts.add("它属于哪一层？" + truncate(knowledgeLayerHint(chapter, knowledge), 42));
        prompts.add("它解决什么问题？先判断是地址、转发、可靠性、介质，还是具体应用。");
        prompts.add("题干有没有偷换概念？重点看层次、设备、地址和协议名字。");
        return prompts;
    }

    private String compactModelSummary(List<Question> qs) {
        String value = firstGroupLineValue(qs, "底层模型：");
        if (value.length() == 0) {
            value = firstGroupLineValue(qs, "易混对比：");
        }
        if (value.length() == 0) {
            value = firstGroupLineValue(qs, "底层原理：");
        }
        if (value.length() == 0) {
            value = firstGroupLineValue(qs, "相邻概念区别：");
        }
        if (value.length() == 0) {
            value = firstGroupLineValue(qs, "为什么这样判题：");
        }
        if (value.length() == 0) {
            value = oneLine(groupKnowledgeSummary(qs));
        }
        return truncate(value, 260);
    }

    private String firstGroupLineValue(List<Question> qs, String prefix) {
        for (Question q : qs) {
            String value = lineValue(q.knowledgeDetail, prefix);
            if (value.length() == 0) {
                value = lineValue(q.explanation, prefix);
            }
            value = oneLine(value);
            if (value.length() > 0) return value;
        }
        return "";
    }

    private String buildMemoryCardFront(String chapter, String knowledge, List<Question> qs, String labels) {
        StringBuilder sb = new StringBuilder();
        sb.append("# 导图导读\n\n");
        sb.append("## ").append(knowledge).append("\n\n");
        sb.append("- **章节：** ").append(chapter).append("\n");
        sb.append("- **覆盖：** ").append(qs.size()).append(" 题\n");
        sb.append("- **题号：** ").append(truncate(labels, 220)).append("\n\n");
        sb.append("### 看到这些题眼就想到它\n\n");
        appendLimitedBullets(sb, collectLineValues(qs, "题眼：", 6));
        sb.append("\n### 先自问\n\n");
        sb.append("- 它属于 OSI/TCP-IP 哪一层？\n");
        sb.append("- 它解决的是地址、转发、可靠性、介质，还是具体应用？\n");
        sb.append("- 题干有没有把相邻概念偷换？\n\n");
        sb.append("进入导图模式后，先看主干，再沿分支复述关键知识点。\n");
        return sb.toString();
    }

    private String buildMemoryCardBack(String chapter, String knowledge, List<Question> qs, String labels) {
        StringBuilder sb = new StringBuilder();
        sb.append("# 导图展开\n\n");
        sb.append("## ").append(knowledge).append("\n\n");
        sb.append("- **层次定位：** ").append(knowledgeLayerHint(chapter, knowledge)).append("\n\n");
        String summary = groupKnowledgeSummary(qs);
        if (summary.length() > 0) {
            sb.append("### 底层模型\n\n");
            sb.append(truncate(oneLine(summary), 520)).append("\n\n");
        }
        sb.append("### 易错开关\n\n");
        List<String> traps = collectLineValues(qs, "易错：", 6);
        if (traps.isEmpty()) {
            traps = collectLineValues(qs, "本题陷阱：", 6);
        }
        appendLimitedBullets(sb, traps);
        sb.append("\n### 覆盖题号\n\n");
        sb.append("- ").append(labels).append("\n\n");
        sb.append("复习动作：只看题眼和导图主干，能说出层次、机制和易错点才算过。\n");
        return sb.toString();
    }

    private String buildChapterMemoryCard(String chapter) {
        if (!allMemoryCards.isEmpty()) {
            List<MemoryCard> cards = memoryCardsInChapter(chapter);
            if (!cards.isEmpty()) {
                return buildChapterCardsMarkdownFromAsset(chapter, cards);
            }
        }
        List<Question> chapterQuestions = questionsInChapter(chapter);
        Map<String, List<Question>> groups = groupByKnowledge(chapterQuestions);
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(chapter).append(" 思维导图\n\n");
        sb.append("- 覆盖题目：").append(chapterQuestions.size()).append(" 题\n");
        sb.append("- 覆盖知识点：").append(groups.size()).append(" 个\n");
        sb.append("- 题型分布：").append(typeDistribution(chapterQuestions)).append("\n");
        sb.append("- 用法：先看导图主干，再顺着分支复述知识点；最后用题号清单回到 App 刷对应题。\n\n");

        sb.append("## 本章总地图\n\n");
        sb.append(chapterLayerHint(chapter)).append("\n\n");
        int groupIndex = 1;
        for (Map.Entry<String, List<Question>> entry : groups.entrySet()) {
            sb.append(groupIndex++).append(". **").append(entry.getKey()).append("**：")
                    .append(entry.getValue().size()).append(" 题，题号 ")
                    .append(joinLabels(entry.getValue())).append("\n");
        }
        sb.append("\n");

        sb.append("## 思维导图摘要\n\n");
        groupIndex = 1;
        for (Map.Entry<String, List<Question>> entry : groups.entrySet()) {
            String knowledge = entry.getKey();
            List<Question> qs = entry.getValue();
            sb.append("### 导图 ").append(groupIndex++).append("：").append(knowledge).append("\n\n");
            sb.append("**导图重点：看到什么题眼要想到它？**\n\n");
            appendLimitedBullets(sb, collectLineValues(qs, "题眼：", 10));
            sb.append("\n**底层模型：小白必须掌握什么？**\n\n");
            sb.append("- 层次定位：").append(knowledgeLayerHint(chapter, knowledge)).append("\n");
            String summary = groupKnowledgeSummary(qs);
            if (summary.length() > 0) {
                sb.append("\n").append(summary).append("\n");
            }
            sb.append("\n**易错开关**\n\n");
            List<String> traps = collectLineValues(qs, "易错：", 10);
            if (traps.isEmpty()) {
                traps = collectLineValues(qs, "本题陷阱：", 10);
            }
            appendLimitedBullets(sb, traps);
            sb.append("\n**快速自测**\n\n");
            sb.append("- Q：这个知识点主要解决哪一层/哪类问题？\n");
            sb.append("  A：").append(knowledgeLayerHint(chapter, knowledge)).append("\n");
            sb.append("- Q：做题时第一步看什么？\n");
            sb.append("  A：先抓题眼，再判断层次、对象和关键词是否被偷换。\n");
            sb.append("- Q：这一组题刷完后怎样算掌握？\n");
            sb.append("  A：能说出题眼、正确答案理由，并能解释为什么其他说法错。\n\n");

            sb.append("**本组题目覆盖（").append(qs.size()).append(" 题）**\n\n");
            sb.append("- ").append(joinLabels(qs)).append("\n\n");
            sb.append("**逐题快刷提示**\n\n");
            for (Question q : qs) {
                sb.append("- ").append(q.label).append("：").append(quickTipForQuestion(q))
                        .append("；答案：").append(displayAnswer(q)).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private List<String> chapterList() {
        Set<String> seen = new LinkedHashSet<>();
        for (Question q : allQuestions) {
            if (q.chapter != null && q.chapter.length() > 0) {
                seen.add(q.chapter);
            }
        }
        List<String> chapters = new ArrayList<>(seen);
        Collections.sort(chapters);
        return chapters;
    }

    private List<Question> questionsInChapter(String chapter) {
        List<Question> list = new ArrayList<>();
        for (Question q : allQuestions) {
            if (chapter.equals(q.chapter)) {
                list.add(q);
            }
        }
        return list;
    }

    private Map<String, List<Question>> groupByKnowledge(List<Question> questions) {
        Map<String, List<Question>> groups = new LinkedHashMap<>();
        for (Question q : questions) {
            String key = q.knowledge == null || q.knowledge.length() == 0 ? "未归类知识点" : q.knowledge;
            List<Question> list = groups.get(key);
            if (list == null) {
                list = new ArrayList<>();
                groups.put(key, list);
            }
            list.add(q);
        }
        return groups;
    }

    private int knowledgePairCount(List<Question> questions) {
        Set<String> pairs = new HashSet<>();
        for (Question q : questions) {
            pairs.add(q.chapter + " / " + q.knowledge);
        }
        return pairs.size();
    }

    private String typeDistribution(List<Question> questions) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Question q : questions) {
            Integer old = counts.get(q.typeName);
            counts.put(q.typeName, old == null ? 1 : old + 1);
        }
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            parts.add(entry.getKey() + " " + entry.getValue());
        }
        return join(parts, "，");
    }

    private String chapterLayerHint(String chapter) {
        if (chapter.startsWith("1.")) {
            return "本章是全局地图：先分清互联网、Web、接入网、协议与分层模型，再谈具体层的协议。";
        }
        if (chapter.startsWith("2.")) {
            return "本章站在物理层：只关心比特怎样变成信号、通过什么介质传、怎样复用信道。";
        }
        if (chapter.startsWith("3.")) {
            return "本章站在数据链路层：把比特组织成帧，用 MAC 地址在同一链路/局域网内传送。";
        }
        if (chapter.startsWith("4.")) {
            return "本章站在网络层：用 IP 地址、路由和转发把分组从源主机送到目的主机。";
        }
        if (chapter.startsWith("5.")) {
            return "本章站在运输层：用端口把主机间通信交给具体进程，TCP 负责可靠性，UDP 追求简单快速。";
        }
        if (chapter.startsWith("6.")) {
            return "本章站在应用层：HTTP、DNS、FTP、邮件等协议直接服务用户应用。";
        }
        if (chapter.startsWith("7.")) {
            return "本章是安全视角：密码、鉴别、防火墙、VPN 等机制会横跨多个层次保护通信。";
        }
        return "先把题眼放回对应层次，再判断协议、设备、地址或服务对象。";
    }

    private String knowledgeLayerHint(String chapter, String knowledge) {
        String base = chapterLayerHint(chapter);
        if (knowledge.contains("MAC") || knowledge.contains("以太网") || knowledge.contains("交换")) {
            return "数据链路层重点。看 MAC/帧/交换机/冲突域，就不要误判成网络层 IP 路由。";
        }
        if (knowledge.contains("IP") || knowledge.contains("路由") || knowledge.contains("ARP") || knowledge.contains("ICMP")) {
            return "网络层重点。IP 负责逻辑寻址和分组转发，ARP/ICMP/IGMP 是围绕网络层工作的辅助协议。";
        }
        if (knowledge.contains("TCP") || knowledge.contains("UDP") || knowledge.contains("端口")) {
            return "运输层重点。端口定位进程，TCP 讲可靠和控制，UDP 讲无连接和低开销。";
        }
        if (knowledge.contains("HTTP") || knowledge.contains("DNS") || knowledge.contains("FTP") || knowledge.contains("邮件")) {
            return "应用层重点。先看应用协议本身，再看它下方依赖 TCP/UDP。";
        }
        return base;
    }

    private String groupKnowledgeSummary(List<Question> questions) {
        for (Question q : questions) {
            if (q.knowledgeDetail != null && q.knowledgeDetail.trim().length() > 0) {
                String detail = q.knowledgeDetail.trim();
                int cut = detail.indexOf("题目变形：");
                if (cut <= 0) {
                    cut = detail.indexOf("本题怎么一步步做：");
                }
                if (cut <= 0) {
                    cut = detail.indexOf("本题怎么套：");
                }
                if (cut > 0) {
                    detail = detail.substring(0, cut).trim();
                }
                return detail;
            }
        }
        return "";
    }

    private List<String> collectLineValues(List<Question> questions, String prefix, int max) {
        List<String> values = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Question q : questions) {
            String value = lineValue(q.quickExplanation, prefix);
            if (value.length() == 0) {
                value = lineValue(q.knowledgeDetail, prefix);
            }
            value = oneLine(value);
            if (value.length() > 0 && !seen.contains(value)) {
                seen.add(value);
                values.add(value);
                if (values.size() >= max) break;
            }
        }
        return values;
    }

    private void appendLimitedBullets(StringBuilder sb, List<String> values) {
        if (values.isEmpty()) {
            sb.append("- 回到本组题目的题干关键词，判断它问的是定义、层次、计算还是易混概念。\n");
            return;
        }
        for (String value : values) {
            sb.append("- ").append(value).append("\n");
        }
    }

    private String quickTipForQuestion(Question q) {
        List<String> parts = new ArrayList<>();
        addPart(parts, "题眼", lineValue(q.quickExplanation, "题眼："));
        String decision = lineValue(q.quickExplanation, "判断：");
        if (decision.length() == 0) decision = lineValue(q.quickExplanation, "答案：");
        addPart(parts, "结论", decision);
        addPart(parts, "理由", lineValue(q.quickExplanation, "理由："));
        addPart(parts, "易错", lineValue(q.quickExplanation, "易错："));
        if (parts.isEmpty()) {
            return truncate(oneLine(q.quickExplanation), 140);
        }
        return truncate(join(parts, "；"), 220);
    }

    private void addPart(List<String> parts, String name, String value) {
        value = oneLine(value);
        if (value.length() > 0) {
            parts.add(name + "：" + value);
        }
    }

    private String lineValue(String text, String prefix) {
        if (text == null || text.length() == 0) return "";
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith(prefix)) {
                return trimmed.substring(prefix.length()).trim();
            }
        }
        return "";
    }

    private String oneLine(String text) {
        if (text == null) return "";
        return text.replace("\r", " ").replace("\n", " ").replaceAll("\\s+", " ").trim();
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        if (text.length() <= max) return text;
        return text.substring(0, max) + "…";
    }

    private String joinLabels(List<Question> questions) {
        List<String> labels = new ArrayList<>();
        for (Question q : questions) {
            labels.add(q.label);
        }
        return join(labels, "、");
    }

    private String displayAnswer(Question q) {
        if (isEssayQuestion(q)) {
            return answerString(q);
        }
        if ("blank".equals(q.type)) {
            return join(answerList(q), "；");
        }
        if ("tf".equals(q.type)) {
            return "TRUE".equals(answerString(q)) ? "T（正确）" : "F（错误）";
        }
        String ans = answerString(q);
        List<String> parts = new ArrayList<>();
        for (Option opt : q.options) {
            if (ans.contains(opt.key)) {
                parts.add(opt.text);
            }
        }
        return ans + (parts.isEmpty() ? "" : "（" + join(parts, "；") + "）");
    }

    private String answerString(Question q) {
        if (q.answer instanceof String) return (String) q.answer;
        return String.valueOf(q.answer);
    }

    private boolean isEssayQuestion(Question q) {
        return q != null && QUESTION_TYPE_ESSAY.equals(q.type);
    }

    private int correctChoiceCount(Question q) {
        String ans = answerString(q);
        int count = 0;
        for (int i = 0; i < ans.length(); i++) {
            char c = ans.charAt(i);
            if (c >= 'A' && c <= 'Z') count++;
        }
        return Math.max(1, count);
    }

    private String explanationText(Question q) {
        if ((q.quickExplanation != null && q.quickExplanation.trim().length() > 0)
                || (q.knowledgeDetail != null && q.knowledgeDetail.trim().length() > 0)) {
            StringBuilder sb = new StringBuilder();
            if (q.quickExplanation != null && q.quickExplanation.trim().length() > 0) {
                sb.append("【快速做题】\n").append(q.quickExplanation.trim());
            }
            if (q.knowledgeDetail != null && q.knowledgeDetail.trim().length() > 0) {
                if (sb.length() > 0) sb.append("\n\n");
                sb.append("【知识点详解】\n").append(q.knowledgeDetail.trim());
            }
            return sb.toString();
        }
        if (q.explanation != null && q.explanation.trim().length() > 0) {
            return q.explanation;
        }
        return "这题属于「" + q.chapter + " / " + q.knowledge
                + "」。先背该知识点的定义、所属层次、服务对象和常见易错说法，再回到题干逐词核对。";
    }

    private List<String> answerList(Question q) {
        List<String> list = new ArrayList<>();
        if (q.answer instanceof JSONArray) {
            JSONArray arr = (JSONArray) q.answer;
            for (int i = 0; i < arr.length(); i++) {
                list.add(arr.optString(i));
            }
        } else {
            list.add(String.valueOf(q.answer));
        }
        return list;
    }

    private String sortedKeys(Set<String> keys) {
        List<String> list = new ArrayList<>(keys);
        Collections.sort(list);
        StringBuilder sb = new StringBuilder();
        for (String k : list) sb.append(k);
        return sb.toString();
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().replace(" ", "").replace("\n", "");
    }

    private String join(List<String> values, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(sep);
            sb.append(values.get(i));
        }
        return sb.toString();
    }

    private TextView text(String s, int sp, int color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(s);
        tv.setTextSize(sp);
        tv.setTextColor(color);
        if (bold) tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        return tv;
    }

    private Button chromeButton(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setTextSize(11.5f);
        b.setTextColor(TEXT);
        b.setAllCaps(false);
        b.setSingleLine(true);
        b.setEllipsize(TextUtils.TruncateAt.END);
        b.setIncludeFontPadding(false);
        b.setMinHeight(0);
        b.setMinimumHeight(0);
        b.setMinWidth(0);
        b.setMinimumWidth(0);
        b.setGravity(Gravity.CENTER);
        b.setPadding(dp(14), 0, dp(14), 0);
        styleChromeButton(b, false);
        installPressFeedback(b);
        return b;
    }

    private Button filterButton(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setTextSize(11.5f);
        b.setTextColor(TEXT);
        b.setAllCaps(false);
        b.setSingleLine(true);
        b.setEllipsize(TextUtils.TruncateAt.END);
        b.setIncludeFontPadding(false);
        b.setMinHeight(0);
        b.setMinimumHeight(0);
        b.setMinWidth(0);
        b.setMinimumWidth(0);
        b.setGravity(Gravity.CENTER);
        b.setPadding(dp(12), 0, dp(12), 0);
        styleFilterButton(b, false);
        installPressFeedback(b);
        return b;
    }

    private void styleChromeButton(Button b, boolean active) {
        b.setTextColor(active ? CHIP_ACTIVE_TEXT : TEXT);
        b.setBackground(roundedStrokeBackground(
                active ? CHIP_ACTIVE_FILL : CHIP_FILL,
                active ? GLASS_STROKE : CHIP_STROKE,
                18,
                1
        ));
        b.setAlpha(active ? 1f : 0.94f);
    }

    private void styleFilterButton(Button b, boolean active) {
        b.setTextColor(active ? BLUE : TEXT);
        b.setBackground(filterButtonBackground(active));
        b.setAlpha(1f);
    }

    private void styleFilterActionButton(Button b, int accent) {
        b.setTextColor(Color.WHITE);
        b.setBackground(filterActionBackground(accent));
        b.setAlpha(1f);
    }

    private void refreshUpdateSettingViews() {
        if (!updateBusy) {
            persistStableUpdateStatus();
        }
        if (updateVersionLineView != null) {
            updateVersionLineView.setText("当前版本：" + currentVersionSummary());
        }
        if (updateRepoLineView != null) {
            updateRepoLineView.setText("GitHub 仓库：" + updateRepoSummary());
        }
        if (updateStatusLineView != null) {
            updateStatusLineView.setText("更新状态：" + updateStatusSummary());
        }
        if (updateCheckButton != null) {
            updateCheckButton.setEnabled(!updateBusy);
            updateCheckButton.setAlpha(updateBusy ? 0.72f : 1f);
            updateCheckButton.setText(updateBusy ? "处理中..." : "检查更新");
        }
    }

    private String currentVersionSummary() {
        int code = currentVersionCode();
        if (code > 0) {
            return currentVersionName() + " (" + code + ")";
        }
        return currentVersionName();
    }

    private String updateRepoSummary() {
        return hasUpdateRepoConfig() ? updateRepoSlug : "未配置";
    }

    private String updateStatusSummary() {
        return updateStatusText == null || updateStatusText.trim().length() == 0 ? UPDATE_STATUS_NOT_CHECKED : updateStatusText;
    }

    private void restorePersistedUpdateStatus() {
        if (!hasUpdateRepoConfig()) {
            updateStatusText = UPDATE_STATUS_REPO_NOT_CONFIGURED;
            persistStableUpdateStatus();
            rememberCurrentLaunchVersion();
            return;
        }

        int currentCode = currentVersionCode();
        int lastLaunchedCode = prefs.getInt(PREF_LAST_LAUNCHED_VERSION_CODE, 0);
        int persistedCode = prefs.getInt(PREF_UPDATE_STATUS_VERSION_CODE, 0);
        String persistedText = prefs.getString(PREF_UPDATE_STATUS_TEXT, "");
        String trimmedText = persistedText == null ? "" : persistedText.trim();

        if (currentCode > 0 && lastLaunchedCode > 0 && currentCode > lastLaunchedCode) {
            updateStatusText = UPDATE_STATUS_UPDATED_TO_PREFIX + currentVersionSummary();
        } else if (currentCode > 0 && lastLaunchedCode > 0 && currentCode < lastLaunchedCode) {
            updateStatusText = UPDATE_STATUS_NOT_CHECKED;
        } else if (trimmedText.length() > 0 && (persistedCode == 0 || persistedCode == currentCode)) {
            updateStatusText = trimmedText;
        } else {
            updateStatusText = UPDATE_STATUS_NOT_CHECKED;
        }

        persistStableUpdateStatus();
        rememberCurrentLaunchVersion();
    }

    private void persistStableUpdateStatus() {
        if (prefs == null) return;
        prefs.edit()
                .putString(PREF_UPDATE_STATUS_TEXT, updateStatusText == null ? "" : updateStatusText)
                .putInt(PREF_UPDATE_STATUS_VERSION_CODE, currentVersionCode())
                .apply();
    }

    private void rememberCurrentLaunchVersion() {
        if (prefs == null) return;
        prefs.edit()
                .putInt(PREF_LAST_LAUNCHED_VERSION_CODE, currentVersionCode())
                .apply();
    }

    private String currentVersionName() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            String value = info == null ? "" : info.versionName;
            return value == null || value.trim().length() == 0 ? "0.0.0" : value.trim();
        } catch (Exception e) {
            return "0.0.0";
        }
    }

    private int currentVersionCode() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (info == null) return 0;
            if (Build.VERSION.SDK_INT >= 28) {
                return (int) info.getLongVersionCode();
            }
            return info.versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean hasUpdateRepoConfig() {
        return updateRepoSlug.matches("^[^/]+/[^/]+$");
    }

    private String normalizeRepoSlug(String raw) {
        String value = raw == null ? "" : raw.trim();
        if (value.length() == 0) return "";
        value = value.replace("\\", "/");
        value = value.replace("https://github.com/", "");
        value = value.replace("http://github.com/", "");
        int queryIndex = value.indexOf('?');
        if (queryIndex >= 0) value = value.substring(0, queryIndex);
        int hashIndex = value.indexOf('#');
        if (hashIndex >= 0) value = value.substring(0, hashIndex);
        if (value.endsWith(".git")) value = value.substring(0, value.length() - 4);
        while (value.startsWith("/")) value = value.substring(1);
        while (value.endsWith("/")) value = value.substring(0, value.length() - 1);
        if (value.contains("/releases")) value = value.substring(0, value.indexOf("/releases"));
        String[] parts = value.split("/");
        if (parts.length >= 2) {
            value = parts[0] + "/" + parts[1];
        }
        return value.replace(" ", "");
    }

    private String ensureDefaultUpdateRepoSlug(String raw) {
        String normalized = normalizeRepoSlug(raw);
        if (LEGACY_UPDATE_REPO_SLUG.equalsIgnoreCase(normalized)
                || LEGACY_REVIEW_UPDATE_REPO_SLUG.equalsIgnoreCase(normalized)) {
            return DEFAULT_UPDATE_REPO_SLUG;
        }
        if (normalized.matches("^[^/]+/[^/]+$")) {
            return normalized;
        }
        return DEFAULT_UPDATE_REPO_SLUG;
    }

    private void saveUpdateRepoSlug(String raw) {
        updateRepoSlug = ensureDefaultUpdateRepoSlug(raw);
        prefs.edit().putString(PREF_UPDATE_REPO_SLUG, updateRepoSlug).apply();
        lastUpdateInfo = null;
        updateStatusText = hasUpdateRepoConfig() ? UPDATE_STATUS_NOT_CHECKED : UPDATE_STATUS_REPO_NOT_CONFIGURED;
        refreshUpdateSettingViews();
    }

    private boolean isRepoSlugInputValid(String raw) {
        String trimmed = raw == null ? "" : raw.trim();
        if (trimmed.length() == 0) return true;
        return normalizeRepoSlug(trimmed).matches("^[^/]+/[^/]+$");
    }

    private void probeUpdateSourceAfterSave(final boolean showToastOnSuccess) {
        if (updateBusy) return;
        if (!hasUpdateRepoConfig()) return;
        updateBusy = true;
        updateStatusText = "正在测试 GitHub 仓库...";
        refreshUpdateSettingViews();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final UpdateInfo info = fetchLatestUpdateInfo();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateBusy = false;
                            lastUpdateInfo = info;
                            updateStatusText = updateProbeStatusText(info, compareUpdateWithCurrent(info));
                            refreshUpdateSettingViews();
                            if (showToastOnSuccess) {
                                Toast.makeText(MainActivity.this, updateStatusText, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateBusy = false;
                            updateStatusText = safeErrorMessage(e);
                            cleanupUpdateCache(null);
                            refreshUpdateSettingViews();
                            Toast.makeText(MainActivity.this, updateStatusText, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void showUpdateRepoConfigDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        FrameLayout shell = new FrameLayout(this);
        shell.setPadding(dp(18), statusBarInset() + dp(18), dp(18), bottomSafeInset() + dp(14));
        shell.setBackgroundColor(Color.argb(THEME_LIGHT.equals(themeMode) ? 54 : 84, 11, 17, 26));

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(18), dp(18), dp(18), dp(16));
        panel.setBackground(choiceSheetBackground());
        panel.setElevation(dp(12));
        shell.addView(panel, new FrameLayout.LayoutParams(-1, -2, Gravity.CENTER));

        TextView title = text("GitHub 更新仓库", 18, TEXT, true);
        panel.addView(title, new LinearLayout.LayoutParams(-1, -2));

        EditText input = new EditText(this);
        input.setText(updateRepoSlug);
        input.setHint("owner/repo 或 GitHub 链接");
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setTextColor(TEXT);
        input.setHintTextColor(MUTED);
        input.setPadding(dp(14), dp(12), dp(14), dp(12));
        input.setBackground(roundedStrokeBackground(PANEL, GLASS_STROKE, 18, 1));
        LinearLayout.LayoutParams inputLp = new LinearLayout.LayoutParams(-1, -2);
        inputLp.topMargin = dp(14);
        panel.addView(input, inputLp);

        Button saveButton = bigButton("保存并测试", true);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String raw = input.getText().toString();
                if (!isRepoSlugInputValid(raw)) {
                    Toast.makeText(MainActivity.this, "请填写 owner/repo 或 GitHub 仓库链接", Toast.LENGTH_LONG).show();
                    return;
                }
                saveUpdateRepoSlug(raw);
                dialog.dismiss();
                if (hasUpdateRepoConfig()) {
                    Toast.makeText(MainActivity.this, "GitHub 仓库已保存，开始测试连通性", Toast.LENGTH_SHORT).show();
                    probeUpdateSourceAfterSave(true);
                } else {
                    Toast.makeText(MainActivity.this, "已清空更新仓库", Toast.LENGTH_SHORT).show();
                }
            }
        });
        LinearLayout.LayoutParams saveLp = new LinearLayout.LayoutParams(-1, dp(46));
        saveLp.topMargin = dp(14);
        panel.addView(saveButton, saveLp);

        Button clearButton = bigButton("清空仓库", false);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setText("");
            }
        });
        LinearLayout.LayoutParams clearLp = new LinearLayout.LayoutParams(-1, dp(46));
        clearLp.topMargin = dp(8);
        panel.addView(clearButton, clearLp);

        Button closeButton = chromeButton("关闭");
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(-1, dp(44));
        closeLp.topMargin = dp(8);
        panel.addView(closeButton, closeLp);

        dialog.setContentView(shell);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    private void checkForUpdates(final boolean userInitiated) {
        logUpdateDebug("checkForUpdates start userInitiated=" + userInitiated + ", busy=" + updateBusy + ", repo=" + updateRepoSlug);
        if (updateBusy) {
            Toast.makeText(this, "正在处理更新，请稍等", Toast.LENGTH_SHORT).show();
            logUpdateDebug("skip checkForUpdates because updateBusy=true");
            return;
        }
        if (!hasUpdateRepoConfig()) {
            logUpdateDebug("skip checkForUpdates because repo is not configured");
            showUpdateRepoConfigDialog();
            return;
        }
        updateBusy = true;
        updateStatusText = "正在检查 GitHub Release...";
        refreshUpdateSettingViews();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final UpdateInfo info = fetchLatestUpdateInfo();
                    final int compare = compareUpdateWithCurrent(info);
                    logUpdateDebug("update info ready latest=" + info.displayVersion() + ", local=" + currentVersionSummary() + ", compare=" + compare);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateBusy = false;
                            lastUpdateInfo = info;
                            if (compare > 0) {
                                updateStatusText = "发现新版本 " + info.displayVersion();
                                logUpdateDebug("show update dialog for " + info.displayVersion());
                                showUpdateAvailableDialog(info);
                            } else {
                                updateStatusText = updateProbeStatusText(info, compare);
                                logUpdateDebug("no newer version, status=" + updateStatusText);
                                if (userInitiated) {
                                    Toast.makeText(MainActivity.this, updateStatusText, Toast.LENGTH_LONG).show();
                                }
                            }
                            refreshUpdateSettingViews();
                        }
                    });
                } catch (final Exception e) {
                    logUpdateWarn("checkForUpdates failed", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateBusy = false;
                            updateStatusText = safeErrorMessage(e);
                            refreshUpdateSettingViews();
                            if (userInitiated) {
                                Toast.makeText(MainActivity.this, updateStatusText, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void scheduleAutoUpdateCheck() {
        if (autoUpdateCheckScheduled) {
            logUpdateDebug("skip scheduleAutoUpdateCheck because already scheduled");
            return;
        }
        if (rootFrame == null) {
            logUpdateDebug("skip scheduleAutoUpdateCheck because rootFrame is null");
            return;
        }
        logUpdateDebug("scheduleAutoUpdateCheck queued");
        autoUpdateCheckScheduled = true;
        rootFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoUpdateCheckScheduled = false;
                logUpdateDebug("scheduleAutoUpdateCheck fired");
                maybeAutoCheckForUpdates();
            }
        }, 1800L);
    }

    private void maybeAutoCheckForUpdates() {
        if (updateBusy) {
            logUpdateDebug("skip auto check because updateBusy=true");
            return;
        }
        if (!hasUpdateRepoConfig()) {
            logUpdateDebug("skip auto check because repo is not configured");
            return;
        }
        if (pendingInstallApkPath != null && pendingInstallApkPath.trim().length() > 0) {
            logUpdateDebug("skip auto check because pending install exists");
            return;
        }
        if (!hasUsableNetworkConnection()) {
            logUpdateDebug("skip auto check because network is unavailable");
            return;
        }
        logUpdateDebug("auto check starts now");
        checkForUpdates(false);
    }

    private boolean hasUsableNetworkConnection() {
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager == null) {
                logUpdateDebug("network check: manager is null");
                return false;
            }
            Network network = manager.getActiveNetwork();
            if (network == null) {
                logUpdateDebug("network check: active network is null");
                return false;
            }
            NetworkCapabilities capabilities = manager.getNetworkCapabilities(network);
            if (capabilities == null) {
                logUpdateDebug("network check: capabilities are null");
                return false;
            }
            boolean ok = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                    || capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED));
            logUpdateDebug("network check result=" + ok
                    + ", internet=" + capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    + ", validated=" + capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    + ", wifi=" + capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    + ", cellular=" + capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    + ", ethernet=" + capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    + ", vpn=" + capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
            return ok;
        } catch (Exception e) {
            logUpdateWarn("network check threw", e);
            return false;
        }
    }

    private UpdateInfo fetchLatestUpdateInfo() throws Exception {
        String repoSlug = updateRepoSlug;
        UpdateInfo info = new UpdateInfo();
        info.repoSlug = repoSlug;

        try {
            JSONObject fastMeta = fetchFastUpdateMetadata(repoSlug);
            String packageName = fastMeta.optString("packageName", "").trim();
            if (packageName.length() > 0 && !getPackageName().equals(packageName)) {
                throw new UpdateCheckException("这个更新源不是当前 App 的安装包（包名不匹配）");
            }
            info.hasMetadataAsset = true;
            info.repoTitle = repoSlug;
            info.releaseTitle = nonEmpty(fastMeta.optString("versionName", ""), "latest");
            info.versionName = normalizeVersionName(fastMeta.optString("versionName", ""));
            info.versionCode = fastMeta.optInt("versionCode", 0);
            info.apkName = nonEmpty(fastMeta.optString("apkFileName", ""), info.apkName);
            info.downloadUrl = nonEmpty(fastMeta.optString("apkDownloadUrl", ""), info.downloadUrl);
            info.htmlUrl = nonEmpty(fastMeta.optString("releaseHtmlUrl", ""), buildGithubReleasePageUrl(repoSlug));
            info.notes = nonEmpty(fastMeta.optString("releaseNotes", ""), info.notes);
            info.assetSize = fastMeta.optLong("apkSize", 0L);
            JSONArray fastCandidates = fastMeta.optJSONArray("apkDownloadCandidates");
            if (fastCandidates != null) {
                for (int i = 0; i < fastCandidates.length(); i++) {
                    appendDownloadCandidate(info.downloadCandidates, fastCandidates.optString(i, ""));
                }
            }
            if (hasUsableFastMetadata(info)) {
                finalizeUpdateDownloadCandidates(repoSlug, info);
                logUpdateDebug("fast metadata satisfied update check for " + info.displayVersion());
                return info;
            }
        } catch (UpdateCheckException fastError) {
            info.validationNotes.add("加速更新元数据读取失败，已回退到 GitHub Release：" + fastError.userMessage);
        }

        JSONObject repo = fetchGithubRepoInfo();
        JSONObject release = fetchLatestReleaseJson();
        info.repoTitle = nonEmpty(repo.optString("full_name", ""), info.repoTitle);
        info.releaseTitle = nonEmpty(release.optString("name", ""), nonEmpty(release.optString("tag_name", ""), info.releaseTitle));
        info.versionName = nonEmpty(normalizeVersionName(release.optString("tag_name", "")), info.versionName);
        info.notes = nonEmpty(release.optString("body", ""), info.notes);
        info.htmlUrl = nonEmpty(release.optString("html_url", ""), nonEmpty(info.htmlUrl, buildGithubReleasePageUrl(repoSlug)));
        info.publishedAt = nonEmpty(release.optString("published_at", ""), info.publishedAt);

        JSONArray assets = release.optJSONArray("assets");
        JSONObject metadataAsset = null;
        List<JSONObject> apkAssets = new ArrayList<>();
        if (assets != null) {
            for (int i = 0; i < assets.length(); i++) {
                JSONObject asset = assets.optJSONObject(i);
                if (asset == null) continue;
                String name = asset.optString("name", "");
                if (UPDATE_METADATA_NAME.equalsIgnoreCase(name)) {
                    metadataAsset = asset;
                } else if (metadataAsset == null && LEGACY_UPDATE_METADATA_NAME.equalsIgnoreCase(name)) {
                    metadataAsset = asset;
                }
                if (name.toLowerCase(Locale.US).endsWith(".apk")) {
                    apkAssets.add(asset);
                }
            }
        }

        if (metadataAsset != null) {
            JSONObject meta = null;
            try {
                meta = readGithubJsonObject(
                        metadataAsset.optString("browser_download_url", ""),
                        "Release 缺少可读取的更新元数据文件",
                        "读取 Release 更新元数据失败"
                );
            } catch (UpdateCheckException metadataError) {
                info.validationNotes.add("更新元数据读取失败，已回退到直接识别 APK：" + metadataError.userMessage);
            }
            if (meta != null) {
                info.hasMetadataAsset = true;
                String packageName = meta.optString("packageName", "").trim();
                if (packageName.length() > 0 && !getPackageName().equals(packageName)) {
                    throw new UpdateCheckException("这个 Release 不是当前 App 的更新包（包名不匹配）");
                }
                info.versionName = nonEmpty(normalizeVersionName(meta.optString("versionName", "")), info.versionName);
                info.versionCode = meta.optInt("versionCode", info.versionCode);
                info.apkName = nonEmpty(meta.optString("apkFileName", ""), info.apkName);
                info.downloadUrl = nonEmpty(meta.optString("apkDownloadUrl", ""), info.downloadUrl);
                info.notes = nonEmpty(meta.optString("releaseNotes", ""), info.notes);
                info.htmlUrl = nonEmpty(meta.optString("releaseHtmlUrl", ""), info.htmlUrl);
                info.assetSize = meta.optLong("apkSize", info.assetSize);
                JSONArray metaCandidates = meta.optJSONArray("apkDownloadCandidates");
                if (metaCandidates != null) {
                    for (int i = 0; i < metaCandidates.length(); i++) {
                        appendDownloadCandidate(info.downloadCandidates, metaCandidates.optString(i, ""));
                    }
                }
            }
        } else {
            info.validationNotes.add("Release 未附带更新元数据，当前会退回到直接识别 APK。");
        }

        JSONObject apkAsset = pickPreferredApkAsset(apkAssets, info.apkName);
        if (apkAsset != null) {
            info.apkName = nonEmpty(info.apkName, apkAsset.optString("name", ""));
            info.downloadUrl = nonEmpty(info.downloadUrl, apkAsset.optString("browser_download_url", ""));
            info.assetSize = apkAsset.optLong("size", 0L);
        }
        appendDownloadCandidate(info.downloadCandidates, buildGhfastMirrorUrl(info.downloadUrl));
        appendDownloadCandidate(info.downloadCandidates, info.downloadUrl);
        appendDownloadCandidate(info.downloadCandidates, buildGhfastMirrorUrl(buildGithubLatestDownloadUrl(repoSlug, info.apkName)));
        appendDownloadCandidate(info.downloadCandidates, buildGithubLatestDownloadUrl(repoSlug, info.apkName));
        if (info.versionName.length() > 0) {
            appendDownloadCandidate(info.downloadCandidates, buildGhfastMirrorUrl(buildGithubTagDownloadUrl(repoSlug, info.versionName, info.apkName)));
            appendDownloadCandidate(info.downloadCandidates, buildGithubTagDownloadUrl(repoSlug, info.versionName, info.apkName));
        }
        info.apkAssetCount = apkAssets.size();
        if (info.apkAssetCount > 1) {
            info.validationNotes.add("这个 Release 包含多个 APK，当前会优先挑选最像正式包的那个。");
        }

        if (info.versionName.length() == 0 && info.versionCode <= 0) {
            throw new UpdateCheckException("这个 Release 缺少可识别的版本号，请检查 tag 或更新元数据");
        }
        if (info.downloadUrl.length() == 0) {
            throw new UpdateCheckException("这个 Release 里没有找到 APK 安装包");
        }
        if (!info.hasMetadataAsset) {
            info.validationNotes.add("未提供更新元数据文件，安装前无法提前核验 APK 包名。");
        }
        finalizeUpdateDownloadCandidates(repoSlug, info);
        return info;
    }

    private boolean hasUsableFastMetadata(UpdateInfo info) {
        if (info == null) return false;
        boolean hasVersion = info.versionCode > 0 || info.versionName.length() > 0;
        boolean hasDownload = info.downloadUrl.length() > 0 || !info.downloadCandidates.isEmpty();
        return hasVersion && hasDownload;
    }

    private void finalizeUpdateDownloadCandidates(String repoSlug, UpdateInfo info) {
        if (info == null) return;
        List<String> existingCandidates = new ArrayList<>(info.downloadCandidates);
        List<String> orderedCandidates = new ArrayList<>();
        appendDownloadCandidateWithMirrors(orderedCandidates, info.downloadUrl);
        appendDownloadCandidateWithMirrors(orderedCandidates, buildGithubLatestDownloadUrl(repoSlug, info.apkName));
        if (info.versionName.length() > 0) {
            appendDownloadCandidateWithMirrors(orderedCandidates, buildGithubTagDownloadUrl(repoSlug, info.versionName, info.apkName));
        }
        for (String candidate : existingCandidates) {
            appendDownloadCandidateWithMirrors(orderedCandidates, candidate);
        }
        info.downloadCandidates.clear();
        info.downloadCandidates.addAll(orderedCandidates);
        if (!info.downloadCandidates.isEmpty()) {
            info.downloadUrl = info.downloadCandidates.get(0);
        }
    }

    private JSONObject fetchGithubRepoInfo() throws UpdateCheckException {
        return readGithubJsonObject(
                "https://api.github.com/repos/" + updateRepoSlug,
                "找不到这个 GitHub 仓库，请检查 owner/repo 是否正确",
                "读取 GitHub 仓库信息失败"
        );
    }

    private JSONObject fetchLatestReleaseJson() throws UpdateCheckException {
        return readGithubJsonObject(
                "https://api.github.com/repos/" + updateRepoSlug + "/releases/latest",
                "这个仓库还没有可用的 Release",
                "读取最新 Release 失败"
        );
    }

    private JSONObject readGithubJsonObject(String url, String notFoundMessage, String genericMessage) throws UpdateCheckException {
        try {
            return readJsonObject(url);
        } catch (Exception e) {
            throw classifyGithubException(e, notFoundMessage, genericMessage);
        }
    }

    private JSONObject readFastGithubJsonObject(String url, String notFoundMessage, String genericMessage) throws UpdateCheckException {
        try {
            return readJsonObject(url, FAST_HTTP_TIMEOUT_MS, FAST_HTTP_TIMEOUT_MS);
        } catch (Exception e) {
            throw classifyGithubException(e, notFoundMessage, genericMessage);
        }
    }

    private JSONObject readJsonObject(String url) throws Exception {
        return new JSONObject(readTextUrl(url));
    }

    private JSONObject readJsonObject(String url, int connectTimeoutMs, int readTimeoutMs) throws Exception {
        return new JSONObject(readTextUrl(url, connectTimeoutMs, readTimeoutMs));
    }

    private UpdateCheckException classifyGithubException(Exception e, String notFoundMessage, String genericMessage) {
        if (e instanceof UpdateCheckException) {
            return (UpdateCheckException) e;
        }
        String raw = rawErrorMessage(e);
        String lower = raw.toLowerCase(Locale.US);
        if (lower.contains("unable to resolve host")
                || lower.contains("failed to connect")
                || lower.contains("connection timed out")
                || lower.contains("network is unreachable")) {
            return new UpdateCheckException("网络连接失败，请检查网络后重试", e);
        }
        if (lower.contains("api rate limit exceeded")) {
            return new UpdateCheckException("GitHub 接口访问过于频繁，请稍后再试", e);
        }
        if (lower.contains("bad credentials") || lower.contains("requires authentication")) {
            return new UpdateCheckException("这个 GitHub 仓库当前无法匿名访问", e);
        }
        String githubMessage = extractGithubMessage(raw);
        if ("Not Found".equalsIgnoreCase(githubMessage)) {
            return new UpdateCheckException(notFoundMessage, e);
        }
        if (githubMessage.length() > 0 && !"Not Found".equalsIgnoreCase(githubMessage)) {
            return new UpdateCheckException(genericMessage + "：" + githubMessage, e);
        }
        return new UpdateCheckException(genericMessage + "：" + raw, e);
    }

    private String extractGithubMessage(String raw) {
        if (raw == null) return "";
        String trimmed = raw.trim();
        if (!trimmed.startsWith("{")) return trimmed;
        try {
            JSONObject obj = new JSONObject(trimmed);
            return obj.optString("message", trimmed).trim();
        } catch (Exception ignored) {
            return trimmed;
        }
    }

    private JSONObject pickPreferredApkAsset(List<JSONObject> apkAssets, String preferredName) {
        if (apkAssets == null || apkAssets.isEmpty()) return null;
        String preferred = preferredName == null ? "" : preferredName.trim();
        if (preferred.length() > 0) {
            for (JSONObject asset : apkAssets) {
                if (preferred.equalsIgnoreCase(asset.optString("name", ""))) {
                    return asset;
                }
            }
        }
        for (JSONObject asset : apkAssets) {
            String name = asset.optString("name", "");
            if (RELEASE_APK_NAME.equalsIgnoreCase(name)) {
                return asset;
            }
            if (LEGACY_RELEASE_APK_NAME.equalsIgnoreCase(name)) {
                return asset;
            }
            if ("备考宝典.apk".equalsIgnoreCase(name)) {
                return asset;
            }
            if ("计算机网络复习宝典.apk".equalsIgnoreCase(name)) {
                return asset;
            }
        }
        return apkAssets.get(0);
    }

    private String buildJsDelivrUpdateMetadataUrl(String repoSlug) {
        return buildJsDelivrUpdateMetadataUrl(repoSlug, UPDATE_METADATA_NAME);
    }

    private String buildJsDelivrUpdateMetadataUrl(String repoSlug, String metadataName) {
        return "https://cdn.jsdelivr.net/gh/" + repoSlug + "@main/release/" + metadataName;
    }

    private String buildGithubRawUpdateMetadataUrl(String repoSlug) {
        return buildGithubRawUpdateMetadataUrl(repoSlug, UPDATE_METADATA_NAME);
    }

    private String buildGithubRawUpdateMetadataUrl(String repoSlug, String metadataName) {
        return "https://raw.githubusercontent.com/" + repoSlug + "/main/release/" + metadataName;
    }

    private String buildGithubLatestMetadataUrl(String repoSlug) {
        return buildGithubLatestMetadataUrl(repoSlug, UPDATE_METADATA_NAME);
    }

    private String buildGithubLatestMetadataUrl(String repoSlug, String metadataName) {
        return "https://github.com/" + repoSlug + "/releases/latest/download/" + metadataName;
    }

    private String buildGhfastLatestMetadataUrl(String repoSlug) {
        return buildGhfastLatestMetadataUrl(repoSlug, UPDATE_METADATA_NAME);
    }

    private String buildGhfastLatestMetadataUrl(String repoSlug, String metadataName) {
        return buildGhfastMirrorUrl(buildGithubLatestMetadataUrl(repoSlug, metadataName));
    }

    private String buildGhProxyLatestMetadataUrl(String repoSlug, String metadataName) {
        return buildGhProxyMirrorUrl(buildGithubLatestMetadataUrl(repoSlug, metadataName));
    }

    private String buildGithubReleasePageUrl(String repoSlug) {
        return "https://github.com/" + repoSlug + "/releases/latest";
    }

    private String buildGithubLatestDownloadUrl(String repoSlug, String apkName) {
        return "https://github.com/" + repoSlug + "/releases/latest/download/" + encodeUrlPathSegment(apkName);
    }

    private String buildGithubTagDownloadUrl(String repoSlug, String versionName, String apkName) {
        return "https://github.com/" + repoSlug + "/releases/download/v" + normalizeVersionName(versionName)
                + "/" + encodeUrlPathSegment(apkName);
    }

    private String buildGhfastMirrorUrl(String originalUrl) {
        String trimmed = unwrapUpdateMirrorUrl(originalUrl);
        if (trimmed.length() == 0) return "";
        if (trimmed.startsWith("https://ghfast.top/")) return trimmed;
        return "https://ghfast.top/" + trimmed;
    }

    private String buildGhProxyMirrorUrl(String originalUrl) {
        String trimmed = unwrapUpdateMirrorUrl(originalUrl);
        if (trimmed.length() == 0) return "";
        if (trimmed.startsWith("https://gh-proxy.com/")) return trimmed;
        return "https://gh-proxy.com/" + trimmed;
    }

    private String unwrapUpdateMirrorUrl(String originalUrl) {
        String trimmed = originalUrl == null ? "" : originalUrl.trim();
        if (trimmed.startsWith("https://ghfast.top/")) {
            return trimmed.substring("https://ghfast.top/".length());
        }
        if (trimmed.startsWith("https://gh-proxy.com/")) {
            return trimmed.substring("https://gh-proxy.com/".length());
        }
        return trimmed;
    }

    private String encodeUrlPathSegment(String value) {
        String safe = value == null ? "" : value.trim();
        if (safe.length() == 0) return "";
        try {
            return URLEncoder.encode(safe, "UTF-8").replace("+", "%20");
        } catch (Exception ignored) {
            return safe.replace(" ", "%20");
        }
    }

    private String encodeUrlQueryParam(String value) {
        String safe = value == null ? "" : value;
        try {
            return URLEncoder.encode(safe, "UTF-8");
        } catch (Exception ignored) {
            return safe.replace(" ", "+");
        }
    }

    private void appendDownloadCandidate(List<String> out, String candidate) {
        if (out == null) return;
        String normalized = candidate == null ? "" : candidate.trim();
        if (normalized.length() == 0) return;
        if (!out.contains(normalized)) {
            out.add(normalized);
        }
    }

    private void appendDownloadCandidateWithMirrors(List<String> out, String candidate) {
        String direct = unwrapUpdateMirrorUrl(candidate);
        appendDownloadCandidate(out, direct);
        appendDownloadCandidate(out, buildGhProxyMirrorUrl(direct));
        appendDownloadCandidate(out, buildGhfastMirrorUrl(direct));
    }

    private JSONObject fetchFastUpdateMetadata(String repoSlug) throws UpdateCheckException {
        logUpdateDebug("fetchFastUpdateMetadata repo=" + repoSlug);
        UpdateCheckException lastError = null;
        List<String> urls = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        for (String metadataName : updateMetadataNames()) {
            urls.add(buildGithubLatestMetadataUrl(repoSlug, metadataName));
            errorMessages.add("读取 Release 元数据失败");
            urls.add(buildGhProxyLatestMetadataUrl(repoSlug, metadataName));
            errorMessages.add("读取 gh-proxy Release 元数据失败");
            urls.add(buildGhfastLatestMetadataUrl(repoSlug, metadataName));
            errorMessages.add("读取 ghfast Release 元数据失败");
            urls.add(buildGithubRawUpdateMetadataUrl(repoSlug, metadataName));
            errorMessages.add("读取原始更新元数据失败");
            urls.add(buildJsDelivrUpdateMetadataUrl(repoSlug, metadataName));
            errorMessages.add("读取 jsDelivr 更新元数据失败");
        }
        for (int i = 0; i < urls.size(); i++) {
            try {
                postUpdateStatus("正在读取更新信息 " + (i + 1) + "/" + urls.size() + "：" + updateDownloadSourceLabel(urls.get(i)));
                return readFastGithubJsonObject(
                        urls.get(i),
                        "还没有找到更新元数据文件",
                        errorMessages.get(i)
                );
            } catch (UpdateCheckException error) {
                lastError = error;
                logUpdateWarn("fast metadata candidate failed: " + urls.get(i), error);
            }
        }
        if (lastError != null) {
            throw lastError;
        }
        throw new UpdateCheckException("还没有找到更新元数据文件");
    }

    private String[] updateMetadataNames() {
        return new String[] { UPDATE_METADATA_NAME, LEGACY_UPDATE_METADATA_NAME };
    }

    private String readTextUrl(String urlValue) throws Exception {
        return readTextUrl(urlValue, HTTP_TIMEOUT_MS, HTTP_TIMEOUT_MS);
    }

    private String readTextUrl(String urlValue, int connectTimeoutMs, int readTimeoutMs) throws Exception {
        HttpURLConnection connection = null;
        InputStream stream = null;
        BufferedReader reader = null;
        try {
            logUpdateDebug("HTTP GET " + urlValue + " connect=" + connectTimeoutMs + " read=" + readTimeoutMs);
            connection = (HttpURLConnection) new URL(urlValue).openConnection();
            connection.setConnectTimeout(connectTimeoutMs);
            connection.setReadTimeout(readTimeoutMs);
            connection.setRequestProperty("Accept", "application/vnd.github+json");
            connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
            connection.setRequestProperty("User-Agent", "ExamPrepHandbookUpdater/" + currentVersionName());
            int code = connection.getResponseCode();
            stream = code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream();
            if (stream == null) {
                throw new IOException("HTTP " + code);
            }
            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            String text = sb.toString().trim();
            if (code < 200 || code >= 300) {
                logUpdateDebug("HTTP FAIL " + code + " for " + urlValue);
                throw new IOException(text.length() == 0 ? ("HTTP " + code) : text);
            }
            logUpdateDebug("HTTP OK " + code + " for " + urlValue);
            return text;
        } finally {
            closeQuietly(reader);
            closeQuietly(stream);
            if (connection != null) connection.disconnect();
        }
    }

    private void logUpdateDebug(String message) {
        Log.d(UPDATE_LOG_TAG, message);
    }

    private void logUpdateWarn(String message, Throwable error) {
        Log.w(UPDATE_LOG_TAG, message, error);
    }

    private int compareUpdateWithCurrent(UpdateInfo info) {
        int localCode = currentVersionCode();
        if (info.versionCode > 0 && localCode > 0 && info.versionCode != localCode) {
            return info.versionCode > localCode ? 1 : -1;
        }
        return compareVersionNames(info.versionName, currentVersionName());
    }

    private String updateProbeStatusText(UpdateInfo info, int compare) {
        if (compare < 0) {
            return "本机版本较新（" + currentVersionSummary() + "），GitHub latest 还是 " + info.displayVersion();
        }
        return info.probeStatusText(compare);
    }

    private int compareVersionNames(String left, String right) {
        List<Integer> a = versionParts(left);
        List<Integer> b = versionParts(right);
        int size = Math.max(a.size(), b.size());
        for (int i = 0; i < size; i++) {
            int av = i < a.size() ? a.get(i) : 0;
            int bv = i < b.size() ? b.get(i) : 0;
            if (av != bv) return av > bv ? 1 : -1;
        }
        return normalizeVersionName(left).compareToIgnoreCase(normalizeVersionName(right));
    }

    private List<Integer> versionParts(String raw) {
        List<Integer> parts = new ArrayList<>();
        String[] tokens = normalizeVersionName(raw).split("[^0-9]+");
        for (String token : tokens) {
            if (token.length() == 0) continue;
            try {
                parts.add(Integer.parseInt(token));
            } catch (Exception ignored) {
            }
        }
        return parts;
    }

    private String normalizeVersionName(String raw) {
        String value = raw == null ? "" : raw.trim();
        if (value.startsWith("v") || value.startsWith("V")) {
            value = value.substring(1);
        }
        return value.trim();
    }

    private void showUpdateAvailableDialog(final UpdateInfo info) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        FrameLayout shell = new FrameLayout(this);
        shell.setPadding(dp(18), statusBarInset() + dp(18), dp(18), bottomSafeInset() + dp(14));
        shell.setBackgroundColor(Color.argb(THEME_LIGHT.equals(themeMode) ? 54 : 84, 11, 17, 26));

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(18), dp(18), dp(18), dp(16));
        panel.setBackground(choiceSheetBackground());
        panel.setElevation(dp(12));
        shell.addView(panel, new FrameLayout.LayoutParams(-1, -2, Gravity.CENTER));

        TextView title = text("发现新版本", 18, TEXT, true);
        panel.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView meta = text("当前 " + currentVersionSummary() + "  路  最新 " + info.displayVersion()
                + (info.assetSize > 0 ? "  路  " + humanFileSize(info.assetSize) : ""), 13, MUTED, false);
        meta.setLineSpacing(dp(3), 1.0f);
        LinearLayout.LayoutParams metaLp = new LinearLayout.LayoutParams(-1, -2);
        metaLp.topMargin = dp(10);
        panel.addView(meta, metaLp);

        String updateNotice = buildUpdateDialogNotice(info);
        if (updateNotice.length() > 0) {
            TextView notice = text(updateNotice, 12, updateDialogNoticeColor(info), false);
            notice.setLineSpacing(dp(3), 1.0f);
            LinearLayout.LayoutParams noticeLp = new LinearLayout.LayoutParams(-1, -2);
            noticeLp.topMargin = dp(8);
            panel.addView(notice, noticeLp);
        }

        ScrollView scroller = new ScrollView(this);
        scroller.setVerticalScrollBarEnabled(false);
        LinearLayout.LayoutParams scrollerLp = new LinearLayout.LayoutParams(-1, dp(280));
        scrollerLp.topMargin = dp(14);
        panel.addView(scroller, scrollerLp);

        LinearLayout notesContainer = new LinearLayout(this);
        notesContainer.setOrientation(LinearLayout.VERTICAL);
        scroller.addView(notesContainer, new ScrollView.LayoutParams(-1, -2));
        if (info.notes != null && info.notes.trim().length() > 0) {
            renderMarkdown(notesContainer, info.notes);
        } else {
            TextView empty = text("这个 Release 没有填写更新说明。", 14, MUTED, false);
            empty.setLineSpacing(dp(4), 1.0f);
            notesContainer.addView(empty);
        }

        Button installButton = bigButton("下载并安装", true);
        installButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                downloadAndInstallUpdate(info);
            }
        });
        LinearLayout.LayoutParams installLp = new LinearLayout.LayoutParams(-1, dp(46));
        installLp.topMargin = dp(14);
        panel.addView(installButton, installLp);

        Button releaseButton = bigButton("打开 Release 页面", false);
        releaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(info.htmlUrl.length() > 0 ? info.htmlUrl : ("https://github.com/" + updateRepoSlug + "/releases"));
            }
        });
        LinearLayout.LayoutParams releaseLp = new LinearLayout.LayoutParams(-1, dp(46));
        releaseLp.topMargin = dp(8);
        panel.addView(releaseButton, releaseLp);

        Button laterButton = chromeButton("稍后再说");
        laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        LinearLayout.LayoutParams laterLp = new LinearLayout.LayoutParams(-1, dp(44));
        laterLp.topMargin = dp(8);
        panel.addView(laterButton, laterLp);

        dialog.setContentView(shell);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    private String buildUpdateDialogNotice(UpdateInfo info) {
        if (info == null || info.validationNotes.isEmpty()) return "";
        String raw = info.validationNotes.get(0);
        if (raw == null) return "";
        raw = raw.trim();
        if (raw.length() == 0) return "";
        if (raw.startsWith("更新元数据读取失败，已回退到直接识别 APK：")
                || raw.startsWith("network_quiz_update.json 读取失败，已回退到直接识别 APK：")) {
            return "补充说明：版本和安装包已经识别完成，附加元数据读取较慢，不影响下载和安装。";
        }
        if (raw.startsWith("加速更新元数据读取失败，已回退到 GitHub Release：")) {
            return "补充说明：加速通道响应较慢，已自动切换到 GitHub Release 检查。";
        }
        if (raw.startsWith("Release 未附带更新元数据")
                || raw.startsWith("Release 未附带 network_quiz_update.json")) {
            return "补充说明：这个版本未附带额外元数据，已直接根据 APK 识别更新。";
        }
        if (raw.startsWith("未提供更新元数据文件")) {
            return "补充说明：安装前无法提前核验包名，但仍可继续下载更新。";
        }
        return "提示： " + raw;
    }

    private int updateDialogNoticeColor(UpdateInfo info) {
        if (info == null || info.validationNotes.isEmpty()) return MUTED;
        String raw = info.validationNotes.get(0);
        if (raw == null) return MUTED;
        raw = raw.trim();
        if (raw.startsWith("更新元数据读取失败，已回退到直接识别 APK：")
                || raw.startsWith("network_quiz_update.json 读取失败，已回退到直接识别 APK：")
                || raw.startsWith("加速更新元数据读取失败，已回退到 GitHub Release：")
                || raw.startsWith("Release 未附带更新元数据")
                || raw.startsWith("Release 未附带 network_quiz_update.json")
                || raw.startsWith("未提供更新元数据文件")) {
            return BLUE;
        }
        return AMBER;
    }

    private void downloadAndInstallUpdate(final UpdateInfo info) {
        if (updateBusy) return;
        updateBusy = true;
        updateStatusText = "正在下载更新...";
        refreshUpdateSettingViews();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final File apkFile = downloadUpdateApk(info);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateStatusText = "安装包已下载，准备安装";
                            refreshUpdateSettingViews();
                            prepareInstallDownloadedApk(apkFile, info.displayVersion());
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateBusy = false;
                            updateStatusText = "下载失败：" + safeErrorMessage(e);
                            refreshUpdateSettingViews();
                            Toast.makeText(MainActivity.this, updateStatusText, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private File downloadUpdateApk(UpdateInfo info) throws Exception {
        Exception lastError = null;
        List<String> candidates = info.downloadCandidates.isEmpty()
                ? Collections.singletonList(info.downloadUrl)
                : info.downloadCandidates;
        int totalCandidates = candidates.size();
        for (int i = 0; i < totalCandidates; i++) {
            String candidate = candidates.get(i);
            final String sourceLabel = updateDownloadSourceLabel(candidate);
            postUpdateStatus("正在尝试下载源 " + (i + 1) + "/" + totalCandidates + "：" + sourceLabel);
            try {
                return downloadUpdateApkFromUrl(info, candidate, sourceLabel);
            } catch (Exception e) {
                lastError = e;
                logUpdateWarn("download candidate failed: " + candidate, e);
                if (i + 1 < totalCandidates) {
                    postUpdateStatus("下载源 " + (i + 1) + " 失败，正在切换：" + sourceLabel);
                }
            }
        }
        if (lastError != null) {
            throw new IOException("所有下载源都失败，最后错误：" + safeErrorMessage(lastError), lastError);
        }
        throw new IOException("没有可用的下载地址");
    }

    private File downloadUpdateApkFromUrl(UpdateInfo info, String url, String sourceLabel) throws Exception {
        HttpURLConnection connection = null;
        InputStream stream = null;
        FileOutputStream out = null;
        File tempFile = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(DOWNLOAD_CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(DOWNLOAD_READ_TIMEOUT_MS);
            connection.setRequestProperty("User-Agent", "ExamPrepHandbookUpdater/" + currentVersionName());
            connection.setRequestProperty("Accept", "application/octet-stream");
            int code = connection.getResponseCode();
            if (code < 200 || code >= 300) {
                throw new IOException("下载失败，HTTP " + code);
            }
            long total = connection.getContentLengthLong();
            stream = connection.getInputStream();
            File dir = updateCacheDir();
            String fileName = safeFileName(info.apkName, "exam-prep-handbook-update.apk");
            tempFile = new File(dir, fileName + ".download");
            File finalFile = new File(dir, fileName);
            cleanupUpdateCache(tempFile.getAbsolutePath());
            deleteUpdateFile(tempFile);
            deleteUpdateFile(finalFile);
            out = new FileOutputStream(tempFile);
            byte[] buffer = new byte[32768];
            long copied = 0L;
            int read;
            int lastProgress = -1;
            long lastBytesStatus = 0L;
            long lastStatusAt = 0L;
            while ((read = stream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                copied += read;
                if (total > 0) {
                    final int progress = (int) ((copied * 100L) / total);
                    if (progress == 100 || progress - lastProgress >= 5) {
                        lastProgress = progress;
                        postUpdateStatus("正在下载更新 " + progress + "%（" + sourceLabel + "）");
                    }
                } else {
                    long now = SystemClock.elapsedRealtime();
                    if (copied - lastBytesStatus >= 1024L * 1024L || now - lastStatusAt >= 1000L) {
                        lastBytesStatus = copied;
                        lastStatusAt = now;
                        postUpdateStatus("正在下载更新 " + humanFileSize(copied) + "（" + sourceLabel + "）");
                    }
                }
            }
            out.flush();
            closeQuietly(out);
            out = null;
            if (!tempFile.renameTo(finalFile)) {
                throw new IOException("安装包保存失败");
            }
            return finalFile;
        } finally {
            closeQuietly(out);
            closeQuietly(stream);
            if (connection != null) connection.disconnect();
            if (tempFile != null && tempFile.exists() && tempFile.getName().endsWith(".download")) {
                deleteUpdateFile(tempFile);
            }
        }
    }

    private void postUpdateStatus(final String statusText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateStatusText = statusText;
                refreshUpdateSettingViews();
            }
        });
    }

    private String updateDownloadSourceLabel(String url) {
        String value = url == null ? "" : url.trim();
        if (value.contains("gh-proxy.com")) return "gh-proxy 镜像";
        if (value.contains("ghfast.top")) return "ghfast 镜像";
        if (value.contains("github.com")) return "GitHub 原链";
        try {
            String host = new URL(value).getHost();
            return host == null || host.length() == 0 ? "备用源" : host;
        } catch (Exception ignored) {
            return "备用源";
        }
    }

    private void prepareInstallDownloadedApk(File file, String versionName) {
        if (file == null || !file.exists()) {
            updateBusy = false;
            updateStatusText = "安装包不存在，请重新下载";
            refreshUpdateSettingViews();
            return;
        }
        rememberPendingCleanupPath(file.getAbsolutePath());
        if (Build.VERSION.SDK_INT >= 26 && !getPackageManager().canRequestPackageInstalls()) {
            pendingInstallApkPath = file.getAbsolutePath();
            pendingInstallVersionName = versionName;
            persistPendingInstallState();
            updateBusy = false;
            updateStatusText = "请允许本应用安装更新";
            refreshUpdateSettingViews();
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                Toast.makeText(this, "授权后回到 App，会继续安装更新", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "无法打开安装来源授权页：" + safeErrorMessage(e), Toast.LENGTH_LONG).show();
            }
            return;
        }
        installDownloadedApkSession(file, versionName);
    }

    private void maybeResumePendingInstall() {
        if (pendingInstallApkPath == null || pendingInstallApkPath.length() == 0) return;
        if (Build.VERSION.SDK_INT >= 26 && !getPackageManager().canRequestPackageInstalls()) return;
        File file = new File(pendingInstallApkPath);
        String versionName = pendingInstallVersionName == null ? "" : pendingInstallVersionName;
        pendingInstallApkPath = null;
        pendingInstallVersionName = null;
        clearPendingInstallState();
        if (!file.exists()) {
            forgetPendingCleanupPath(file.getAbsolutePath());
            updateStatusText = "安装包已丢失，请重新下载";
            refreshUpdateSettingViews();
            return;
        }
        installDownloadedApkSession(file, versionName);
    }

    private void installDownloadedApkSession(final File file, final String versionName) {
        updateBusy = true;
        updateStatusText = "正在写入系统安装器...";
        refreshUpdateSettingViews();
        new Thread(new Runnable() {
            @Override
            public void run() {
                PackageInstaller.Session session = null;
                FileInputStream in = null;
                OutputStream out = null;
                try {
                    PackageInstaller installer = getPackageManager().getPackageInstaller();
                    PackageInstaller.SessionParams params =
                            new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
                    params.setAppPackageName(getPackageName());
                    int sessionId = installer.createSession(params);
                    session = installer.openSession(sessionId);
                    in = new FileInputStream(file);
                    out = session.openWrite("update.apk", 0, file.length());
                    byte[] buffer = new byte[32768];
                    long copied = 0L;
                    long total = Math.max(1L, file.length());
                    int read;
                    int lastProgress = -1;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                        copied += read;
                        final int progress = (int) ((copied * 100L) / total);
                        if (progress == 100 || progress - lastProgress >= 20) {
                            lastProgress = progress;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateStatusText = "正在写入系统安装器 " + progress + "%";
                                    refreshUpdateSettingViews();
                                }
                            });
                        }
                    }
                    session.fsync(out);
                    closeQuietly(out);
                    closeQuietly(in);
                    out = null;
                    in = null;
                    clearPendingInstallState();
                    rememberPendingCleanupPath(file.getAbsolutePath());
                    cleanupUpdateCache(file.getAbsolutePath());
                    deleteUpdateFile(file);

                    Intent callback = new Intent(UPDATE_INSTALL_ACTION);
                    callback.setPackage(getPackageName());
                    callback.putExtra("update_version_name", versionName);
                    int pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        pendingIntentFlags |= PendingIntent.FLAG_MUTABLE;
                    } else {
                        pendingIntentFlags |= PendingIntent.FLAG_IMMUTABLE;
                    }
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            MainActivity.this,
                            sessionId,
                            callback,
                            pendingIntentFlags
                    );
                    session.commit(pendingIntent.getIntentSender());
                    session.close();
                    session = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateBusy = false;
                            updateStatusText = "已提交安装请求，请在系统界面确认";
                            refreshUpdateSettingViews();
                            Toast.makeText(MainActivity.this, "安装包已交给系统安装器，缓存已清理", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (final Exception e) {
                    if (session != null) {
                        try {
                            session.abandon();
                        } catch (Exception ignored) {
                        }
                    }
                    closeQuietly(out);
                    closeQuietly(in);
                    pendingInstallApkPath = file == null ? "" : file.getAbsolutePath();
                    pendingInstallVersionName = versionName == null ? "" : versionName;
                    persistPendingInstallState();
                    cleanupUpdateCache(null);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateBusy = false;
                            updateStatusText = "安装准备失败：" + safeErrorMessage(e);
                            refreshUpdateSettingViews();
                            Toast.makeText(MainActivity.this, updateStatusText, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void ensureUpdateInstallReceiver() {
        if (updateInstallReceiver != null) return;
        updateInstallReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleInstallStatus(intent);
            }
        };
        IntentFilter filter = new IntentFilter(UPDATE_INSTALL_ACTION);
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(updateInstallReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(updateInstallReceiver, filter);
        }
    }

    private void handleInstallStatus(Intent intent) {
        int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);
        String message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
        String versionName = intent.getStringExtra("update_version_name");
        if (status == PackageInstaller.STATUS_PENDING_USER_ACTION) {
            updateStatusText = "请在系统安装界面确认 " + (versionName == null ? "" : versionName);
            refreshUpdateSettingViews();
            Intent confirmIntent = (Intent) intent.getParcelableExtra(Intent.EXTRA_INTENT);
            if (confirmIntent != null) {
                confirmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(confirmIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "无法打开系统安装界面：" + safeErrorMessage(e), Toast.LENGTH_LONG).show();
                }
            }
            return;
        }
        if (status == PackageInstaller.STATUS_SUCCESS) {
            updateStatusText = versionName == null || versionName.length() == 0 ? "安装完成" : ("安装完成：" + versionName);
            cleanupUpdateCache(null);
            refreshUpdateSettingViews();
            Toast.makeText(this, updateStatusText, Toast.LENGTH_LONG).show();
            return;
        }
        updateStatusText = "安装失败：" + (message == null || message.trim().length() == 0 ? ("状态 " + status) : message.trim());
        cleanupUpdateCache(null);
        refreshUpdateSettingViews();
        Toast.makeText(this, updateStatusText, Toast.LENGTH_LONG).show();
    }

    private void loadPendingUpdateCleanupPaths() {
        pendingUpdateCleanupPaths.clear();
        if (prefs == null) return;
        Set<String> saved = prefs.getStringSet(PREF_UPDATE_PENDING_CLEANUP, null);
        if (saved != null) {
            pendingUpdateCleanupPaths.addAll(saved);
        }
    }

    private void restorePendingInstallState() {
        if (prefs == null) return;
        pendingInstallApkPath = prefs.getString(PREF_PENDING_INSTALL_APK_PATH, "");
        pendingInstallVersionName = prefs.getString(PREF_PENDING_INSTALL_VERSION_NAME, "");
        if (pendingInstallApkPath == null) pendingInstallApkPath = "";
        if (pendingInstallVersionName == null) pendingInstallVersionName = "";
        pendingInstallApkPath = pendingInstallApkPath.trim();
        pendingInstallVersionName = pendingInstallVersionName.trim();
    }

    private void persistPendingInstallState() {
        if (prefs == null) return;
        prefs.edit()
                .putString(PREF_PENDING_INSTALL_APK_PATH, pendingInstallApkPath == null ? "" : pendingInstallApkPath)
                .putString(PREF_PENDING_INSTALL_VERSION_NAME, pendingInstallVersionName == null ? "" : pendingInstallVersionName)
                .commit();
    }

    private void clearPendingInstallState() {
        pendingInstallApkPath = "";
        pendingInstallVersionName = "";
        if (prefs == null) return;
        prefs.edit()
                .remove(PREF_PENDING_INSTALL_APK_PATH)
                .remove(PREF_PENDING_INSTALL_VERSION_NAME)
                .commit();
    }

    private void persistPendingUpdateCleanupPaths() {
        if (prefs == null) return;
        if (pendingUpdateCleanupPaths.isEmpty()) {
            prefs.edit().remove(PREF_UPDATE_PENDING_CLEANUP).commit();
            return;
        }
        prefs.edit()
                .putStringSet(PREF_UPDATE_PENDING_CLEANUP, new LinkedHashSet<>(pendingUpdateCleanupPaths))
                .commit();
    }

    private void rememberPendingCleanupPath(String path) {
        if (path == null || path.trim().length() == 0) return;
        if (pendingUpdateCleanupPaths.add(path)) {
            persistPendingUpdateCleanupPaths();
        }
    }

    private void forgetPendingCleanupPath(String path) {
        if (path == null || path.trim().length() == 0) return;
        if (pendingUpdateCleanupPaths.remove(path)) {
            persistPendingUpdateCleanupPaths();
        }
    }

    private boolean deleteUpdateFile(File file) {
        if (file == null) return true;
        String path = file.getAbsolutePath();
        for (int attempt = 0; attempt < 4; attempt++) {
            if (!file.exists()) {
                forgetPendingCleanupPath(path);
                return true;
            }
            if (file.delete() || !file.exists()) {
                forgetPendingCleanupPath(path);
                return true;
            }
            SystemClock.sleep(120L * (attempt + 1));
        }
        rememberPendingCleanupPath(path);
        return false;
    }

    private void flushPendingUpdateCleanup() {
        if (pendingUpdateCleanupPaths.isEmpty()) return;
        List<String> paths = new ArrayList<>(pendingUpdateCleanupPaths);
        for (String path : paths) {
            if (path == null || path.trim().length() == 0) {
                forgetPendingCleanupPath(path);
                continue;
            }
            deleteUpdateFile(new File(path));
        }
    }

    private File updateCacheDir() {
        File dir = new File(getCacheDir(), UPDATE_CACHE_DIR);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private void cleanupUpdateCache(String keepPath) {
        flushPendingUpdateCleanup();
        File dir = updateCacheDir();
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file == null) continue;
            if (keepPath != null && keepPath.equals(file.getAbsolutePath())) continue;
            deleteUpdateFile(file);
        }
    }

    private String pendingInstallKeepPath() {
        if (pendingInstallApkPath == null) return null;
        String trimmed = pendingInstallApkPath.trim();
        return trimmed.length() == 0 ? null : trimmed;
    }

    private String safeFileName(String value, String fallback) {
        String safe = value == null ? "" : value.replaceAll("[\\\\/:*?\"<>|]+", "_").trim();
        if (safe.length() == 0) safe = fallback;
        return safe;
    }

    private void openUrl(String url) {
        if (url == null || url.trim().length() == 0) return;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "无法打开链接：" + safeErrorMessage(e), Toast.LENGTH_LONG).show();
        }
    }

    private String safeErrorMessage(Throwable t) {
        if (t == null) return "未知错误";
        if (t instanceof UpdateCheckException) {
            return ((UpdateCheckException) t).userMessage;
        }
        String message = t.getMessage();
        if (message == null || message.trim().length() == 0) {
            return t.getClass().getSimpleName();
        }
        message = message.replace('\n', ' ').replace('\r', ' ').trim();
        return message.length() > 120 ? message.substring(0, 120) + "..." : message;
    }

    private String rawErrorMessage(Throwable t) {
        if (t == null) return "未知错误";
        String message = t.getMessage();
        if (message == null || message.trim().length() == 0) {
            return t.getClass().getSimpleName();
        }
        return message.replace('\n', ' ').replace('\r', ' ').trim();
    }

    private String humanFileSize(long bytes) {
        if (bytes <= 0) return "";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format(Locale.US, "%.1f KB", bytes / 1024f);
        return String.format(Locale.US, "%.1f MB", bytes / 1024f / 1024f);
    }

    private String nonEmpty(String first, String second) {
        if (first != null && first.trim().length() > 0) return first.trim();
        return second == null ? "" : second.trim();
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (Exception ignored) {
        }
    }

    private static class UpdateCheckException extends Exception {
        final String userMessage;

        UpdateCheckException(String userMessage) {
            super(userMessage);
            this.userMessage = userMessage;
        }

        UpdateCheckException(String userMessage, Throwable cause) {
            super(userMessage, cause);
            this.userMessage = userMessage;
        }
    }

    private static class UpdateInfo {
        String repoSlug = "";
        String repoTitle = "";
        String releaseTitle = "";
        String versionName = "";
        int versionCode = 0;
        String apkName = "";
        String downloadUrl = "";
        String htmlUrl = "";
        String notes = "";
        String publishedAt = "";
        long assetSize = 0L;
        boolean hasMetadataAsset = false;
        int apkAssetCount = 0;
        final List<String> downloadCandidates = new ArrayList<>();
        final List<String> validationNotes = new ArrayList<>();

        String displayVersion() {
            if (versionCode > 0 && versionName.length() > 0) {
                return versionName + " (" + versionCode + ")";
            }
            if (versionName.length() > 0) return versionName;
            if (versionCode > 0) return "build " + versionCode;
            return releaseTitle.length() > 0 ? releaseTitle : "新版本";
        }

        String probeStatusText(int compare) {
            if (compare > 0) {
                return "仓库可用，发现新版本 " + displayVersion();
            }
            if (hasMetadataAsset) {
                return "仓库可用，Release 结构完整，当前已是最新版本";
            }
            return "仓库可用，可直接检查并安装更新";
        }
    }

    private interface ChoiceHandler {
        void onChosen(int which, String item);
    }

    private void showChoiceSheet(String title, List<String> items, int checkedIndex, final ChoiceHandler handler) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        FrameLayout shell = new FrameLayout(this);
        shell.setPadding(dp(18), statusBarInset() + dp(18), dp(18), bottomSafeInset() + dp(14));
        shell.setBackgroundColor(Color.argb(THEME_LIGHT.equals(themeMode) ? 54 : 84, 11, 17, 26));
        shell.setClickable(true);
        shell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(18), dp(18), dp(18), dp(16));
        panel.setBackground(choiceSheetBackground());
        panel.setElevation(dp(12));
        panel.setClickable(true);
        panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        shell.addView(panel, new FrameLayout.LayoutParams(-1, -2, Gravity.CENTER));

        TextView titleView = text(title, 18, TEXT, true);
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleView.setIncludeFontPadding(false);
        panel.addView(titleView, new LinearLayout.LayoutParams(-1, -2));

        TextView subtitleView = text("轻点一项立即切换", 11, MUTED, false);
        subtitleView.setGravity(Gravity.CENTER_HORIZONTAL);
        subtitleView.setIncludeFontPadding(false);
        LinearLayout.LayoutParams subtitleLp = new LinearLayout.LayoutParams(-1, -2);
        subtitleLp.topMargin = dp(6);
        panel.addView(subtitleView, subtitleLp);

        ScrollView scroller = new ScrollView(this);
        scroller.setVerticalScrollBarEnabled(false);
        scroller.setOverScrollMode(View.OVER_SCROLL_NEVER);
        LinearLayout.LayoutParams scrollerLp = new LinearLayout.LayoutParams(-1, -2);
        scrollerLp.topMargin = dp(14);
        panel.addView(scroller, scrollerLp);

        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        scroller.addView(list, new ScrollView.LayoutParams(-1, -2));

        for (int i = 0; i < items.size(); i++) {
            final int index = i;
            final String item = items.get(i);
            boolean selectedState = i == checkedIndex;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(14), dp(14), dp(14), dp(14));
            row.setBackground(choiceRowBackground(selectedState));
            row.setClickable(true);
            row.setFocusable(true);
            installPressFeedback(row);

            TextView labelView = text(item, 14, selectedState ? BLUE : TEXT, true);
            labelView.setSingleLine(true);
            labelView.setEllipsize(TextUtils.TruncateAt.END);
            row.addView(labelView, new LinearLayout.LayoutParams(0, -2, 1f));

            TextView badgeView = text(selectedState ? "当前" : "选择", 10, selectedState ? BLUE : MUTED, true);
            badgeView.setPadding(dp(10), dp(6), dp(10), dp(6));
            badgeView.setBackground(roundedBackground(selectedState
                    ? Color.argb(THEME_LIGHT.equals(themeMode) ? 44 : 78, Color.red(BLUE), Color.green(BLUE), Color.blue(BLUE))
                    : Color.argb(THEME_LIGHT.equals(themeMode) ? 34 : 52, 255, 255, 255), 999));
            row.addView(badgeView, new LinearLayout.LayoutParams(-2, -2));

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (handler != null) {
                        handler.onChosen(index, item);
                    }
                }
            });

            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(-1, -2);
            if (i > 0) rowLp.topMargin = dp(8);
            list.addView(row, rowLp);
        }

        dialog.setContentView(shell);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    private Button smallButton(String s) {
        Button b = chromeButton(s);
        b.setTextSize(12);
        return b;
    }

    private Button bigButton(String s, boolean primary) {
        Button b = new Button(this);
        b.setText(s);
        b.setTextSize(16);
        b.setTextColor(primary ? Color.WHITE : TEXT);
        b.setAllCaps(false);
        b.setSingleLine(true);
        b.setEllipsize(TextUtils.TruncateAt.END);
        b.setIncludeFontPadding(false);
        b.setMinHeight(0);
        b.setMinimumHeight(0);
        b.setMinWidth(0);
        b.setMinimumWidth(0);
        b.setGravity(Gravity.CENTER);
        b.setPadding(dp(18), 0, dp(18), 0);
        b.setBackground(roundedStrokeBackground(
                primary ? BLUE : PANEL,
                primary ? BLUE : GLASS_STROKE,
                24,
                1
        ));
        installPressFeedback(b);
        return b;
    }

    private void installPressFeedback(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.985f).scaleY(0.985f).alpha(0.94f).setDuration(90).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(140).start();
                        break;
                }
                return false;
            }
        });
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private class MenuIconButton extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        MenuIconButton(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            float stroke = Math.max(dp(2), w * 0.055f);
            paint.reset();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(stroke);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(THEME_LIGHT.equals(themeMode) ? BLUE : Color.WHITE);

            canvas.drawLine(w * 0.29f, h * 0.34f, w * 0.71f, h * 0.34f, paint);
            canvas.drawLine(w * 0.29f, h * 0.50f, w * 0.71f, h * 0.50f, paint);
            canvas.drawLine(w * 0.29f, h * 0.66f, w * 0.71f, h * 0.66f, paint);
        }
    }

    private static class Question {
        int id;
        String label;
        String type;
        String typeName;
        String stem;
        Object answer;
        String chapter;
        String knowledge;
        String quickExplanation;
        String knowledgeDetail;
        String explanation;
        String courseId;
        int blankCount;
        final List<Option> options = new ArrayList<>();
        final List<String> images = new ArrayList<>();
        final List<String> stemImages = new ArrayList<>();
        final List<String> answerImages = new ArrayList<>();
    }

    private static class Option {
        final String key;
        final String text;
        final String image;

        Option(String key, String text) {
            this(key, text, "");
        }

        Option(String key, String text, String image) {
            this.key = key;
            this.text = text;
            this.image = image == null ? "" : image;
        }
    }

    private static class MemoryCard {
        final String chapter;
        final String knowledge;
        final int questionCount;
        final String labels;
        final String layerHint;
        final String modelSummary;
        final String chapterMap;
        final String typeDistribution;
        final List<String> eyeLines;
        final List<String> selfChecks;
        final List<String> corePoints;
        final List<String> mustRemember;
        final List<String> traps;
        final List<String> questionTips;
        final String frontMarkdown;
        final String backMarkdown;
        final boolean overviewCard;
        final String mindMapTitle;
        final List<MindMapNode> mindMapNodes;

        MemoryCard(String chapter, String knowledge, int questionCount, String labels,
                   String layerHint, String chapterMap, String typeDistribution,
                   List<String> eyeLines, List<String> selfChecks,
                   List<String> corePoints, List<String> mustRemember,
                   List<String> traps, List<String> questionTips,
                   String frontMarkdown, String backMarkdown) {
            this(chapter, knowledge, questionCount, labels, layerHint, chapterMap, typeDistribution,
                    eyeLines, selfChecks, corePoints, mustRemember, traps, questionTips,
                    frontMarkdown, backMarkdown, false, "", new ArrayList<MindMapNode>());
        }

        MemoryCard(String chapter, String knowledge, int questionCount, String labels,
                   String layerHint, String chapterMap, String typeDistribution,
                   List<String> eyeLines, List<String> selfChecks,
                   List<String> corePoints, List<String> mustRemember,
                   List<String> traps, List<String> questionTips,
                   String frontMarkdown, String backMarkdown,
                   boolean overviewCard, String mindMapTitle, List<MindMapNode> mindMapNodes) {
            this.chapter = chapter;
            this.knowledge = knowledge;
            this.questionCount = questionCount;
            this.labels = labels;
            this.layerHint = layerHint;
            this.modelSummary = corePoints == null || corePoints.isEmpty() ? "" : corePoints.get(0);
            this.chapterMap = chapterMap;
            this.typeDistribution = typeDistribution;
            this.eyeLines = new ArrayList<>(eyeLines);
            this.selfChecks = new ArrayList<>(selfChecks);
            this.corePoints = new ArrayList<>(corePoints);
            this.mustRemember = new ArrayList<>(mustRemember);
            this.traps = new ArrayList<>(traps);
            this.questionTips = new ArrayList<>(questionTips);
            this.frontMarkdown = frontMarkdown;
            this.backMarkdown = backMarkdown;
            this.overviewCard = overviewCard;
            this.mindMapTitle = mindMapTitle == null ? "" : mindMapTitle;
            this.mindMapNodes = mindMapNodes == null ? new ArrayList<MindMapNode>() : new ArrayList<>(mindMapNodes);
        }

        boolean usesRichNotebookCard() {
            return hasFrontMarkdown() || hasBackMarkdown();
        }

        boolean hasFrontMarkdown() {
            return frontMarkdown != null && frontMarkdown.trim().length() > 0;
        }

        boolean hasBackMarkdown() {
            return backMarkdown != null && backMarkdown.trim().length() > 0;
        }

        boolean hasMindMap() {
            return mindMapNodes != null && !mindMapNodes.isEmpty();
        }
    }

    private static class MindMapNode {
        final String title;
        final String summary;
        final String badge;
        final List<String> points;
        final List<MindMapNode> children;

        MindMapNode(String title, String summary, String badge, List<String> points, List<MindMapNode> children) {
            this.title = title == null ? "" : title;
            this.summary = summary == null ? "" : summary;
            this.badge = badge == null ? "" : badge;
            this.points = points == null ? new ArrayList<String>() : new ArrayList<>(points);
            this.children = children == null ? new ArrayList<MindMapNode>() : new ArrayList<>(children);
        }
    }

    private static class MindMapDraftNode {
        final String title;
        String badge = "";
        final StringBuilder summary = new StringBuilder();
        final List<String> points = new ArrayList<>();
        final List<MindMapDraftNode> children = new ArrayList<>();

        MindMapDraftNode(String title) {
            this.title = title == null ? "" : title;
        }
    }

    private interface MindMapSelectionListener {
        void onNodeSelected(MindMapNode node, int accentColor);
    }

    private interface MindMapPageStateListener {
        void onPageStateChanged(int page, int totalPages, boolean canGoPrevious, boolean canGoNext);
    }

    private class MindMapCanvasView extends View {
        private final MindMapNode rootNode;
        private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint togglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final TextPaint titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        private final TextPaint badgePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        private final List<RenderNode> renderNodes = new ArrayList<>();
        private final Map<String, RenderNode> renderNodeMap = new LinkedHashMap<>();
        private final Set<String> expandedKeys = new LinkedHashSet<>();
        private final ScaleGestureDetector scaleGestureDetector;
        private MindMapSelectionListener selectionListener;
        private MindMapPageStateListener pageStateListener;
        private String selectedKey = "root";
        private boolean layoutDirty = true;
        private int currentPage = 0;
        private int totalPages = 1;
        private int contentWidth = 0;
        private int contentHeight = 0;
        private float pageOffset = 0f;
        private float viewportOffsetY = 0f;
        private float viewportScale = 1f;
        private float touchDownX = 0f;
        private float touchDownY = 0f;
        private float lastTouchX = 0f;
        private float lastTouchY = 0f;
        private int activePointerId = -1;
        private boolean draggingCanvas = false;
        private boolean scalingCanvas = false;
        private ValueAnimator pageAnimator;

        MindMapCanvasView(Context context, String rootTitle, List<MindMapNode> nodes) {
            super(context);
            String safeTitle = cleanMindMapText(rootTitle);
            List<String> rootPoints = new ArrayList<>();
            if (nodes != null) {
                for (MindMapNode node : nodes) {
                    if (node != null && node.title.trim().length() > 0) {
                        rootPoints.add(node.title);
                    }
                }
            }
            this.rootNode = new MindMapNode(
                    safeTitle.length() == 0 ? "知识导图" : safeTitle,
                    safeTitle,
                    "",
                    rootPoints,
                    nodes == null ? new ArrayList<MindMapNode>() : nodes);
            expandedKeys.add("root");
            for (int i = 0; i < this.rootNode.children.size(); i++) {
                expandedKeys.add("root/" + i);
            }
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setStrokeWidth(dp(2));
            linePaint.setStrokeCap(Paint.Cap.ROUND);
            linePaint.setStrokeJoin(Paint.Join.ROUND);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeWidth(toPx(1.4f));
            fillPaint.setStyle(Paint.Style.FILL);
            togglePaint.setStyle(Paint.Style.STROKE);
            togglePaint.setStrokeWidth(toPx(1.6f));
            togglePaint.setStrokeCap(Paint.Cap.ROUND);
            titlePaint.setColor(Color.WHITE);
            titlePaint.setTextSize(dp(14));
            titlePaint.setFakeBoldText(true);
            badgePaint.setTextSize(dp(10));
            badgePaint.setFakeBoldText(true);
            scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScaleBegin(ScaleGestureDetector detector) {
                    scalingCanvas = true;
                    requestMindMapNoIntercept(true);
                    return true;
                }

                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    scaleViewport(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
                    return true;
                }

                @Override
                public void onScaleEnd(ScaleGestureDetector detector) {
                    scalingCanvas = false;
                    clampViewport();
                    updatePageMetrics();
                    invalidate();
                }
            });
            setClickable(true);
        }

        void setSelectionListener(MindMapSelectionListener listener) {
            this.selectionListener = listener;
        }

        void setPageStateListener(MindMapPageStateListener listener) {
            this.pageStateListener = listener;
            notifyPageState();
        }

        void selectInitialNode() {
            post(new Runnable() {
                @Override
                public void run() {
                    ensureLayout();
                    selectNodeByKey("root", false);
                }
            });
        }

        void goToPreviousPage() {
            goToPage(currentPage - 1, true);
        }

        void goToNextPage() {
            goToPage(currentPage + 1, true);
        }

        void expandAllBranches() {
            if (pageAnimator != null) {
                pageAnimator.cancel();
            }
            expandedKeys.clear();
            collectExpandableKeys(rootNode, "root");
            layoutDirty = true;
            ensureLayout();
            if (!renderNodeMap.containsKey(selectedKey)) {
                selectedKey = "root";
            }
            fitWholeMap();
            notifySelection();
            invalidate();
        }

        void collapseAllBranches() {
            if (pageAnimator != null) {
                pageAnimator.cancel();
            }
            expandedKeys.clear();
            expandedKeys.add("root");
            selectedKey = "root";
            layoutDirty = true;
            ensureLayout();
            focusPageForKey("root", true);
            notifySelection();
            invalidate();
        }

        void fitWholeMap() {
            if (pageAnimator != null) {
                pageAnimator.cancel();
            }
            ensureLayout();
            if (getWidth() <= 0 || getHeight() <= 0 || contentWidth <= 0 || contentHeight <= 0) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        fitWholeMap();
                    }
                });
                return;
            }
            float fitPadding = dp(152);
            float targetScale = Math.min(
                    getWidth() / Math.max(1f, contentWidth + fitPadding),
                    getHeight() / Math.max(1f, contentHeight + fitPadding));
            viewportScale = clampFloat(targetScale, MIND_MAP_MIN_SCALE, 1.05f);
            pageOffset = 0f;
            viewportOffsetY = 0f;
            clampViewport();
            updatePageMetrics();
            invalidate();
        }

        private void collectExpandableKeys(MindMapNode node, String key) {
            if (node == null || node.children.isEmpty()) {
                return;
            }
            expandedKeys.add(key);
            for (int i = 0; i < node.children.size(); i++) {
                collectExpandableKeys(node.children.get(i), key + "/" + i);
            }
        }

        @Override
        public boolean performClick() {
            super.performClick();
            return true;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            scaleGestureDetector.onTouchEvent(event);
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    if (pageAnimator != null) {
                        pageAnimator.cancel();
                    }
                    activePointerId = event.getPointerId(0);
                    touchDownX = lastTouchX = event.getX();
                    touchDownY = lastTouchY = event.getY();
                    draggingCanvas = false;
                    scalingCanvas = false;
                    requestMindMapNoIntercept(true);
                    return true;
                case MotionEvent.ACTION_POINTER_UP:
                    handlePointerUp(event);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (event.getPointerCount() > 1 || scalingCanvas) {
                        requestMindMapNoIntercept(true);
                        return true;
                    }
                    int pointerIndex = event.findPointerIndex(activePointerId);
                    if (pointerIndex < 0) {
                        return true;
                    }
                    float x = event.getX(pointerIndex);
                    float y = event.getY(pointerIndex);
                    float dx = x - lastTouchX;
                    float dy = y - lastTouchY;
                    float totalDx = x - touchDownX;
                    float totalDy = y - touchDownY;
                    if (!draggingCanvas
                            && (Math.abs(totalDx) > Math.max(touchSlop, dp(8))
                            || Math.abs(totalDy) > Math.max(touchSlop, dp(8)))) {
                        draggingCanvas = true;
                    }
                    if (draggingCanvas) {
                        panViewport(-dx / viewportScale, -dy / viewportScale);
                    }
                    lastTouchX = x;
                    lastTouchY = y;
                    requestMindMapNoIntercept(true);
                    return true;
                case MotionEvent.ACTION_UP:
                    boolean wasDragging = draggingCanvas || scalingCanvas;
                    draggingCanvas = false;
                    scalingCanvas = false;
                    activePointerId = -1;
                    requestMindMapNoIntercept(false);
                    if (!wasDragging
                            && Math.abs(event.getX() - touchDownX) < dp(10)
                            && Math.abs(event.getY() - touchDownY) < dp(10)) {
                        RenderNode hit = findNodeAt(event.getX(), event.getY());
                        if (hit != null) {
                            performClick();
                            selectedKey = hit.key;
                            if (hit.expandable && !"root".equals(hit.key)) {
                                if (expandedKeys.contains(hit.key)) {
                                    expandedKeys.remove(hit.key);
                                } else {
                                    expandedKeys.add(hit.key);
                                }
                                layoutDirty = true;
                                ensureLayout();
                            }
                            focusPageForKey(hit.key, true);
                            notifySelection();
                            invalidate();
                            return true;
                        }
                    }
                    performClick();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    draggingCanvas = false;
                    scalingCanvas = false;
                    activePointerId = -1;
                    requestMindMapNoIntercept(false);
                    return true;
                default:
                    return true;
            }
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            layoutDirty = true;
            ensureLayout();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            ensureLayout();
            canvas.save();
            canvas.scale(viewportScale, viewportScale);
            canvas.translate(-pageOffset, -viewportOffsetY);

            for (RenderNode node : renderNodes) {
                if (node.parentKey == null) continue;
                RenderNode parent = renderNodeMap.get(node.parentKey);
                if (parent == null) continue;
                drawConnector(canvas, parent, node);
            }

            for (RenderNode node : renderNodes) {
                drawNode(canvas, node);
            }
            canvas.restore();
        }

        private void ensureLayout() {
            if (!layoutDirty || getWidth() <= 0 || getHeight() <= 0) {
                if (!layoutDirty) {
                    notifyPageState();
                }
                return;
            }
            renderNodes.clear();
            renderNodeMap.clear();
            float padding = dp(72);
            float totalHeight = measureSubtree(rootNode, "root", 0);
            float maxRight = layoutNode(rootNode, "root", 0, padding, padding, totalHeight, BLUE, null);
            contentWidth = (int) Math.ceil(maxRight + padding);
            contentHeight = (int) Math.ceil(totalHeight + padding * 2f);
            if (pageAnimator == null || !pageAnimator.isRunning()) {
                clampViewport();
            }
            updatePageMetrics();
            layoutDirty = false;
            invalidate();
        }

        private float measureSubtree(MindMapNode node, String key, int depth) {
            float nodeHeight = measureNodeHeight(node, depth == 0);
            if (!isExpanded(key) || node.children.isEmpty()) {
                return nodeHeight;
            }
            float total = 0f;
            for (int i = 0; i < node.children.size(); i++) {
                if (i > 0) total += dp(22);
                total += measureSubtree(node.children.get(i), key + "/" + i, depth + 1);
            }
            return Math.max(nodeHeight, total);
        }

        private float layoutNode(MindMapNode node, String key, int depth, float left, float top, float subtreeHeight, int accentColor, String parentKey) {
            float width = depth == 0 ? dp(196) : dp(176);
            float nodeHeight = measureNodeHeight(node, depth == 0);
            float nodeTop = top + (subtreeHeight - nodeHeight) / 2f;
            RectF rect = new RectF(left, nodeTop, left + width, nodeTop + nodeHeight);
            RenderNode renderNode = new RenderNode(node, key, rect, accentColor, parentKey, depth, !node.children.isEmpty());
            renderNodes.add(renderNode);
            renderNodeMap.put(key, renderNode);

            float maxRight = rect.right;
            if (!isExpanded(key) || node.children.isEmpty()) {
                return maxRight;
            }

            float childrenTotalHeight = 0f;
            for (int i = 0; i < node.children.size(); i++) {
                if (i > 0) childrenTotalHeight += dp(22);
                childrenTotalHeight += measureSubtree(node.children.get(i), key + "/" + i, depth + 1);
            }
            float childTop = top + (subtreeHeight - childrenTotalHeight) / 2f;
            for (int i = 0; i < node.children.size(); i++) {
                MindMapNode child = node.children.get(i);
                float childHeight = measureSubtree(child, key + "/" + i, depth + 1);
                int childAccent = depth == 0 ? mindMapAccentColor(i) : accentColor;
                maxRight = Math.max(maxRight, layoutNode(
                        child,
                        key + "/" + i,
                        depth + 1,
                        rect.right + dp(56),
                        childTop,
                        childHeight,
                        childAccent,
                        key));
                childTop += childHeight + dp(22);
            }
            return maxRight;
        }

        private float measureNodeHeight(MindMapNode node, boolean root) {
            int width = root ? dp(196) : dp(176);
            int textWidth = width - dp(26);
            StaticLayout layout = StaticLayout.Builder
                    .obtain(node.title, 0, node.title.length(), titlePaint, textWidth)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setIncludePad(false)
                    .build();
            int base = root ? dp(84) : dp(72);
            int extra = node.badge.length() > 0 ? dp(22) : 0;
            return Math.max(base, layout.getHeight() + extra + dp(26));
        }

        private void requestMindMapNoIntercept(boolean disallow) {
            ViewParent parent = getParent();
            while (parent != null) {
                parent.requestDisallowInterceptTouchEvent(disallow);
                if (parent instanceof View) {
                    parent = ((View) parent).getParent();
                } else {
                    break;
                }
            }
        }

        private void handlePointerUp(MotionEvent event) {
            int pointerIndex = event.getActionIndex();
            int pointerId = event.getPointerId(pointerIndex);
            if (pointerId != activePointerId) {
                return;
            }
            int nextIndex = pointerIndex == 0 ? 1 : 0;
            if (nextIndex >= event.getPointerCount()) {
                activePointerId = -1;
                return;
            }
            activePointerId = event.getPointerId(nextIndex);
            lastTouchX = event.getX(nextIndex);
            lastTouchY = event.getY(nextIndex);
            touchDownX = lastTouchX;
            touchDownY = lastTouchY;
        }

        private void panViewport(float dx, float dy) {
            pageOffset += dx;
            viewportOffsetY += dy;
            clampViewport();
            updatePageMetrics();
            invalidate();
        }

        private void scaleViewport(float factor, float focusX, float focusY) {
            float oldScale = viewportScale;
            float newScale = clampFloat(oldScale * factor, MIND_MAP_MIN_SCALE, MIND_MAP_MAX_SCALE);
            if (Math.abs(newScale - oldScale) < 0.001f) {
                return;
            }
            float worldFocusX = focusX / oldScale + pageOffset;
            float worldFocusY = focusY / oldScale + viewportOffsetY;
            viewportScale = newScale;
            pageOffset = worldFocusX - focusX / newScale;
            viewportOffsetY = worldFocusY - focusY / newScale;
            clampViewport();
            updatePageMetrics();
            invalidate();
        }

        private void clampViewport() {
            if (getWidth() <= 0 || getHeight() <= 0) {
                pageOffset = 0f;
                viewportOffsetY = 0f;
                return;
            }
            float extra = dp(128);
            float maxX = Math.max(0f, contentWidth + extra - viewportWorldWidth());
            float maxY = Math.max(0f, contentHeight + extra - viewportWorldHeight());
            pageOffset = clampFloat(pageOffset, 0f, maxX);
            viewportOffsetY = clampFloat(viewportOffsetY, 0f, maxY);
        }

        private float viewportWorldWidth() {
            return getWidth() / Math.max(0.01f, viewportScale);
        }

        private float viewportWorldHeight() {
            return getHeight() / Math.max(0.01f, viewportScale);
        }

        private float clampFloat(float value, float min, float max) {
            return Math.max(min, Math.min(max, value));
        }

        private void updatePageMetrics() {
            float worldPageWidth = Math.max(1f, viewportWorldWidth());
            totalPages = Math.max(1, (int) Math.ceil((contentWidth + dp(72)) / worldPageWidth));
            currentPage = Math.max(0, Math.min(totalPages - 1, (int) Math.floor((pageOffset + worldPageWidth * 0.45f) / worldPageWidth)));
            notifyPageState();
        }

        private void drawConnector(Canvas canvas, RenderNode parent, RenderNode child) {
            int lineColor = mixColors(parent.accentColor, child.accentColor, 0.5f);
            linePaint.setColor(Color.argb(THEME_LIGHT.equals(themeMode) ? 176 : 202,
                    Color.red(lineColor), Color.green(lineColor), Color.blue(lineColor)));
            float startX = parent.rect.right;
            float startY = parent.rect.centerY();
            float endX = child.rect.left;
            float endY = child.rect.centerY();
            float control = Math.max(dp(32), (endX - startX) * 0.42f);
            Path path = new Path();
            path.moveTo(startX, startY);
            path.cubicTo(startX + control, startY, endX - control, endY, endX, endY);
            canvas.drawPath(path, linePaint);
        }

        private void drawNode(Canvas canvas, RenderNode node) {
            boolean selected = node.key.equals(selectedKey);
            int fill = node.depth == 0
                    ? mixColors(THEME_LIGHT.equals(themeMode) ? Color.WHITE : Color.rgb(23, 30, 43), node.accentColor, THEME_LIGHT.equals(themeMode) ? 0.18f : 0.28f)
                    : mixColors(THEME_LIGHT.equals(themeMode) ? Color.WHITE : Color.rgb(28, 34, 48), node.accentColor, THEME_LIGHT.equals(themeMode) ? 0.10f : 0.18f);
            int stroke = selected
                    ? node.accentColor
                    : Color.argb(THEME_LIGHT.equals(themeMode) ? 102 : 78, 210, 220, 242);
            fillPaint.setColor(fill);
            strokePaint.setColor(stroke);
            strokePaint.setStrokeWidth(selected ? dp(2) : toPx(1.3f));
            canvas.drawRoundRect(node.rect, dp(22), dp(22), fillPaint);
            canvas.drawRoundRect(node.rect, dp(22), dp(22), strokePaint);

            RectF contentRect = new RectF(
                    node.rect.left + dp(13),
                    node.rect.top + dp(12),
                    node.rect.right - dp(13),
                    node.rect.bottom - dp(12));

            float badgeBottom = contentRect.top;
            if (node.node.badge.length() > 0) {
                String badge = node.node.badge;
                float badgeWidth = badgePaint.measureText(badge) + dp(16);
                float badgeHeight = dp(22);
                RectF badgeRect = new RectF(contentRect.left, contentRect.top, contentRect.left + badgeWidth, contentRect.top + badgeHeight);
                Paint badgeFill = new Paint(Paint.ANTI_ALIAS_FLAG);
                badgeFill.setColor(Color.argb(THEME_LIGHT.equals(themeMode) ? 226 : 196,
                        Color.red(node.accentColor), Color.green(node.accentColor), Color.blue(node.accentColor)));
                canvas.drawRoundRect(badgeRect, dp(999), dp(999), badgeFill);
                badgePaint.setColor(Color.WHITE);
                canvas.drawText(badge, badgeRect.left + dp(8), badgeRect.top + toPx(14.5f), badgePaint);
                badgeBottom = badgeRect.bottom + dp(8);
            }

            titlePaint.setColor(node.depth == 0
                    ? (THEME_LIGHT.equals(themeMode) ? Color.rgb(28, 36, 52) : Color.WHITE)
                    : (THEME_LIGHT.equals(themeMode) ? Color.rgb(38, 47, 66) : Color.argb(246, 255, 255, 255)));
            int availableWidth = (int) (contentRect.width());
            int titleTop = (int) Math.max(contentRect.top, badgeBottom);
            StaticLayout titleLayout = StaticLayout.Builder
                    .obtain(node.node.title, 0, node.node.title.length(), titlePaint, availableWidth)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setIncludePad(false)
                    .setMaxLines(node.depth == 0 ? 4 : 3)
                    .build();
            canvas.save();
            canvas.translate(contentRect.left, titleTop);
            titleLayout.draw(canvas);
            canvas.restore();

            if (node.expandable && !"root".equals(node.key)) {
                float cx = node.rect.right - dp(18);
                float cy = node.rect.bottom - dp(18);
                Paint bubble = new Paint(Paint.ANTI_ALIAS_FLAG);
                bubble.setColor(Color.argb(THEME_LIGHT.equals(themeMode) ? 230 : 178,
                        Color.red(node.accentColor), Color.green(node.accentColor), Color.blue(node.accentColor)));
                canvas.drawCircle(cx, cy, dp(11), bubble);
                togglePaint.setColor(Color.WHITE);
                canvas.drawLine(cx - dp(4), cy, cx + dp(4), cy, togglePaint);
                if (!expandedKeys.contains(node.key)) {
                    canvas.drawLine(cx, cy - dp(4), cx, cy + dp(4), togglePaint);
                }
            }
        }

        private RenderNode findNodeAt(float x, float y) {
            float worldX = x / viewportScale + pageOffset;
            float worldY = y / viewportScale + viewportOffsetY;
            for (int i = renderNodes.size() - 1; i >= 0; i--) {
                RenderNode node = renderNodes.get(i);
                if (node.rect.contains(worldX, worldY)) {
                    return node;
                }
            }
            return null;
        }

        private boolean isExpanded(String key) {
            return "root".equals(key) || expandedKeys.contains(key);
        }

        private void selectNodeByKey(String key, boolean animatePage) {
            selectedKey = key;
            ensureLayout();
            focusPageForKey(key, animatePage);
            notifySelection();
            invalidate();
        }

        private void focusPageForKey(String key, boolean animate) {
            RenderNode node = renderNodeMap.get(key);
            if (node == null || getWidth() <= 0) return;
            float targetX = node.rect.centerX() - viewportWorldWidth() * 0.5f;
            float targetY = node.rect.centerY() - viewportWorldHeight() * 0.5f;
            goToViewport(targetX, targetY, animate);
        }

        private void goToPage(int page, boolean animate) {
            int target = Math.max(0, Math.min(page, totalPages - 1));
            float targetOffset = target * viewportWorldWidth();
            goToViewport(targetOffset, viewportOffsetY, animate);
        }

        private void goToViewport(float targetX, float targetY, boolean animate) {
            float oldX = pageOffset;
            float oldY = viewportOffsetY;
            pageOffset = targetX;
            viewportOffsetY = targetY;
            clampViewport();
            final float safeTargetX = pageOffset;
            final float safeTargetY = viewportOffsetY;
            pageOffset = oldX;
            viewportOffsetY = oldY;
            if (pageAnimator != null) {
                pageAnimator.cancel();
            }
            if (animate) {
                pageAnimator = ValueAnimator.ofFloat(0f, 1f);
                pageAnimator.setDuration(240);
                pageAnimator.setInterpolator(new DecelerateInterpolator());
                pageAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float progress = (float) animation.getAnimatedValue();
                        pageOffset = oldX + (safeTargetX - oldX) * progress;
                        viewportOffsetY = oldY + (safeTargetY - oldY) * progress;
                        updatePageMetrics();
                        invalidate();
                    }
                });
                pageAnimator.start();
            } else {
                pageOffset = safeTargetX;
                viewportOffsetY = safeTargetY;
                updatePageMetrics();
                invalidate();
            }
        }

        private void notifySelection() {
            if (selectionListener == null) return;
            RenderNode node = renderNodeMap.get(selectedKey);
            if (node == null) {
                node = renderNodeMap.get("root");
            }
            if (node != null) {
                selectionListener.onNodeSelected(node.node, node.accentColor);
            }
        }

        private void notifyPageState() {
            if (pageStateListener == null) return;
            pageStateListener.onPageStateChanged(
                    currentPage + 1,
                    totalPages,
                    currentPage > 0,
                    currentPage < totalPages - 1);
        }

        private int mixColors(int from, int to, float ratio) {
            float clamped = Math.max(0f, Math.min(1f, ratio));
            int a = (int) (Color.alpha(from) * (1f - clamped) + Color.alpha(to) * clamped);
            int r = (int) (Color.red(from) * (1f - clamped) + Color.red(to) * clamped);
            int g = (int) (Color.green(from) * (1f - clamped) + Color.green(to) * clamped);
            int b = (int) (Color.blue(from) * (1f - clamped) + Color.blue(to) * clamped);
            return Color.argb(a, r, g, b);
        }

        private float toPx(float value) {
            return getResources().getDisplayMetrics().density * value;
        }

        private class RenderNode {
            final MindMapNode node;
            final String key;
            final RectF rect;
            final int accentColor;
            final String parentKey;
            final int depth;
            final boolean expandable;

            RenderNode(MindMapNode node, String key, RectF rect, int accentColor, String parentKey, int depth, boolean expandable) {
                this.node = node;
                this.key = key;
                this.rect = rect;
                this.accentColor = accentColor;
                this.parentKey = parentKey;
                this.depth = depth;
                this.expandable = expandable;
            }
        }
    }
}
