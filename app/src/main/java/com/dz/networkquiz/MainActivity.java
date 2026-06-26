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
import android.text.style.StyleSpan;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
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
    private static final String UPDATE_LOG_TAG = "NetworkQuizUpdater";
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
    private static final String LEGACY_UPDATE_REPO_SLUG = "ZhiKong0/network-quiz-apk";
    private static final String THEME_DARK = "dark";
    private static final String THEME_LIGHT = "light";
    private static final String DEFAULT_UPDATE_REPO_SLUG = "ZhiKong0/review-baodian-apk";
    private static final String UPDATE_METADATA_NAME = "network_quiz_update.json";
    private static final String UPDATE_CACHE_DIR = "updates";
    private static final String UPDATE_INSTALL_ACTION = "com.dz.networkquiz.UPDATE_INSTALL_STATUS";
    private static final String UPDATE_STATUS_NOT_CHECKED = "\u672a\u68c0\u67e5";
    private static final String UPDATE_STATUS_REPO_NOT_CONFIGURED = "\u672a\u914d\u7f6e GitHub \u4ed3\u5e93";
    private static final String UPDATE_STATUS_UPDATED_TO_PREFIX = "\u5df2\u66f4\u65b0\u5230 ";
    private static final int HTTP_TIMEOUT_MS = 15000;
    private static final int FAST_HTTP_TIMEOUT_MS = 6000;
    private static final int DEFAULT_WRONG_REQUIRED = 2;
    private static final int MIN_WRONG_REQUIRED = 1;
    private static final int MAX_WRONG_REQUIRED = 10;

    private final List<Question> allQuestions = new ArrayList<>();
    private final List<MemoryCard> allMemoryCards = new ArrayList<>();
    private final List<Question> visibleQuestions = new ArrayList<>();
    private final List<MemoryCard> visibleCards = new ArrayList<>();
    private final Set<String> pendingUpdateCleanupPaths = new LinkedHashSet<>();
    private final Set<String> selected = new LinkedHashSet<>();
    private final List<EditText> blankInputs = new ArrayList<>();
    private LinearLayout optionList;
    private LinearLayout imageList;
    private FrameLayout pageFrame;
    private FrameLayout rootFrame;
    private ScrollView scrollView;
    private LinearLayout contentContainer;
    private LinearLayout swipePreview;
    private TextView swipePreviewTitle;
    private TextView swipePreviewBody;
    private LinearLayout headerContainer;
    private TextView titleView;
    private TextView metaView;
    private TextView progressPeekView;
    private TextView updateVersionLineView;
    private TextView updateRepoLineView;
    private TextView updateStatusLineView;
    private SeekBar questionSeekBar;
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
    private TextView settingsNavButton;
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
    private String currentCardChapter = null;
    private String typeFilter = ALL_TYPES;
    private String chapterFilter = ALL_CHAPTERS;
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
    private boolean updateBusy = false;
    private boolean autoUpdateCheckScheduled = false;
    private String updateRepoSlug = "";
    private String updateStatusText = UPDATE_STATUS_NOT_CHECKED;
    private String pendingInstallApkPath = null;
    private String pendingInstallVersionName = null;
    private UpdateInfo lastUpdateInfo = null;
    private BroadcastReceiver updateInstallReceiver;

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
        ensureUpdateInstallReceiver();
        cleanupUpdateCache(pendingInstallKeepPath());
        loadQuestions();
        loadMemoryCards();
        buildLayout();
        applySystemBars();
        showAllMode();
        scheduleAutoUpdateCheck();
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
        if (isQuestionPageSwipeEnabled() && handleQuestionPageSwipe(event)) {
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean isQuestionPageSwipeEnabled() {
        return !suppressQuestionPageSwipe && !cardMode && !settingsMode;
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

    private void loadQuestions() {
        try {
            String json = readAssetText("questions.json");
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
                JSONArray opts = obj.optJSONArray("options");
                if (opts != null) {
                    for (int j = 0; j < opts.length(); j++) {
                        JSONObject opt = opts.getJSONObject(j);
                        q.options.add(new Option(opt.getString("key"), opt.getString("text")));
                    }
                }
                JSONArray imgs = obj.optJSONArray("images");
                if (imgs != null) {
                    for (int j = 0; j < imgs.length(); j++) {
                        q.images.add(imgs.getString(j));
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
        try {
            String json = readAssetText("chapter_cards.json");
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
            String json = readAssetText("chapter_card_overrides.json");
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

        titleView = text("", 17, BLUE, true);
        titleView.setIncludeFontPadding(false);
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        titleView.setGravity(Gravity.CENTER);
        titleView.setPadding(dp(16), dp(6), dp(16), dp(6));
        titleRow.addView(titleView, new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER));

        progressPeekView = text("", 10, MUTED, true);
        progressPeekView.setIncludeFontPadding(false);
        progressPeekView.setGravity(Gravity.CENTER);
        progressPeekView.setPadding(dp(9), dp(4), dp(9), dp(4));
        titleRow.addView(progressPeekView, new FrameLayout.LayoutParams(-2, -2, Gravity.END | Gravity.CENTER_VERTICAL));

        metaView = text("", 11, MUTED, false);
        metaView.setIncludeFontPadding(false);
        metaView.setSingleLine(true);
        metaView.setEllipsize(TextUtils.TruncateAt.END);
        metaView.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams metaLp = new LinearLayout.LayoutParams(-1, -2);
        metaLp.topMargin = dp(6);
        infoCard.addView(metaView, metaLp);

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
        FrameLayout seekShell = new FrameLayout(this);
        seekShell.setPadding(dp(10), dp(2), dp(10), dp(2));
        seekShell.setBackground(headerSeekShellBackground());
        LinearLayout.LayoutParams seekShellLp = new LinearLayout.LayoutParams(-1, dp(28));
        seekShellLp.topMargin = dp(6);
        infoCard.addView(seekShell, seekShellLp);
        seekShell.addView(questionSeekBar, new FrameLayout.LayoutParams(-1, dp(22), Gravity.CENTER));

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

        contentContainer = new LinearLayout(this);
        contentContainer.setOrientation(LinearLayout.VERTICAL);
        contentContainer.setPadding(0, 0, 0, dp(24));
        scrollView.addView(contentContainer, new ScrollView.LayoutParams(-1, -2));

        stemView = text("", 24, TEXT, true);
        stemView.setLineSpacing(dp(6), 1.0f);
        installQuestionPageSwipeProxy(stemView);
        contentContainer.addView(stemView, new LinearLayout.LayoutParams(-1, -2));

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

        prevButton = bigButton("上一个", false);
        nextButton = bigButton("下一个", true);
        filterReady = true;
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
        rememberMode = false;
        cardMode = false;
        wrongMode = false;
        settingsMode = false;
        rebuildVisibleQuestions();
        currentIndex = clampIndex(currentIndex, visibleQuestions.size());
        renderQuestion();
    }

    private void showRememberMode() {
        rememberMode = true;
        cardMode = false;
        wrongMode = false;
        settingsMode = false;
        rebuildVisibleQuestions();
        currentIndex = clampIndex(currentIndex, visibleQuestions.size());
        renderQuestion();
    }

    private void showWrongMode() {
        rememberMode = false;
        cardMode = false;
        wrongMode = true;
        settingsMode = false;
        rebuildVisibleQuestions();
        currentIndex = clampIndex(currentIndex, visibleQuestions.size());
        renderQuestion();
    }

    private void showSettingsMode() {
        settingsMode = true;
        rememberMode = false;
        cardMode = false;
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
        if (cardMode || settingsMode) return;
        rebuildVisibleQuestions();
        currentIndex = 0;
        renderQuestion();
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
        if (settingsMode) {
            return false;
        }
        if (!cardMode) {
            return handleQuestionPageSwipe(event);
        }
        return handleCardNavigationTouch(target, event, false);
    }

    private boolean handleCardNavigationTouch(View target, MotionEvent event, boolean allowTapFlip) {
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
        settingsNavButton = navButton("设置", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsMode();
            }
        });

        bar.addView(navSlot(rememberNavButton), new LinearLayout.LayoutParams(0, dp(44), 1));
        LinearLayout.LayoutParams quizLp = new LinearLayout.LayoutParams(0, dp(44), 1);
        quizLp.leftMargin = dp(4);
        bar.addView(navSlot(quizNavButton), quizLp);
        LinearLayout.LayoutParams wrongLp = new LinearLayout.LayoutParams(0, dp(44), 1);
        wrongLp.leftMargin = dp(4);
        bar.addView(navSlot(wrongNavButton), wrongLp);
        LinearLayout.LayoutParams cardsLp = new LinearLayout.LayoutParams(0, dp(44), 1);
        cardsLp.leftMargin = dp(4);
        bar.addView(navSlot(cardsNavButton), cardsLp);
        LinearLayout.LayoutParams settingsLp = new LinearLayout.LayoutParams(0, dp(44), 1);
        settingsLp.leftMargin = dp(4);
        bar.addView(navSlot(settingsNavButton), settingsLp);
        return bar;
    }

    private TextView navButton(String label, View.OnClickListener listener) {
        TextView view = text(label, 12, MUTED, true);
        view.setGravity(Gravity.CENTER);
        view.setIncludeFontPadding(false);
        view.setSingleLine(true);
        view.setPadding(dp(16), dp(8), dp(16), dp(8));
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
        if (quizNavButton == null) return;
        styleNavButton(rememberNavButton, rememberMode);
        styleNavButton(quizNavButton, !rememberMode && !wrongMode && !cardMode && !settingsMode);
        styleNavButton(wrongNavButton, wrongMode);
        styleNavButton(cardsNavButton, cardMode);
        styleNavButton(settingsNavButton, settingsMode);
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
        if (settingsMode) {
            applyHeaderTitleStyle("设置", GREEN);
            metaView.setVisibility(View.GONE);
            questionSeekBar.setVisibility(View.GONE);
            progressPeekView.setVisibility(View.GONE);
            if (filterRowView != null) filterRowView.setVisibility(View.GONE);
            return;
        }

        if (cardMode) {
            applyHeaderTitleStyle("导图", AMBER);
            metaView.setVisibility(View.VISIBLE);
            questionSeekBar.setVisibility(View.GONE);
            progressPeekView.setVisibility(View.GONE);
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
        metaView.setVisibility(View.VISIBLE);
        questionSeekBar.setVisibility(View.VISIBLE);
        progressPeekView.setVisibility(View.VISIBLE);
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
        items.add("判断题");
        items.add("单选题");
        items.add("多选题");
        items.add("填空题");
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
        String info = (currentIndex + 1) + "/" + visibleQuestions.size()
                + "  ·  " + q.label
                + "  ·  " + q.typeName
                + "  ·  " + truncate(TextUtils.isEmpty(q.knowledge) ? q.chapter : q.knowledge, 24);
        metaView.setText(info);
    }

    private void refreshEmptyMeta() {
        int total = wrongMode ? filteredWrongCount() : filteredTotalCount();
        metaView.setText("0/" + total + "  ·  " + activeFilterText());
    }

    private void syncQuestionSeekBar() {
        if (questionSeekBar == null || cardMode || settingsMode) return;
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
        progressPeekView.setAlpha(dragging ? 1f : 0.9f);
    }

    private void renderQuestion() {
        refreshChrome();
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
            stemView.setText(emptyMessage());
            refreshEmptyMeta();
            submitButton.setText("提交");
            submitButton.setEnabled(false);
            submitButton.setVisibility(View.GONE);
            return;
        }

        Question q = currentQuestion();
        if ("tf".equals(q.type) || "single".equals(q.type)) {
            submitButton.setText("");
            submitButton.setEnabled(false);
            submitButton.setVisibility(View.GONE);
        } else if ("multi".equals(q.type)) {
            submitButton.setText("提交多选");
            submitButton.setEnabled(true);
            submitButton.setVisibility(rememberMode ? View.GONE : View.VISIBLE);
        } else {
            submitButton.setText("提交填空");
            submitButton.setEnabled(true);
            submitButton.setVisibility(rememberMode ? View.GONE : View.VISIBLE);
        }
        refreshMeta(q);
        stemView.setText(q.stem);

        for (String path : q.images) {
            addImage(path);
        }
        if ("blank".equals(q.type)) {
            renderBlankInputs(q);
        } else {
            for (Option opt : q.options) {
                addOptionView(q, opt);
            }
        }
        if (rememberMode) {
            revealRememberModeAnswer(q);
        }
    }

    private void renderCardView() {
        selected.clear();
        blankInputs.clear();
        submitted = false;
        lastAnswerOk = null;
        optionList.removeAllViews();
        imageList.removeAllViews();
        memoryReasonContainer.removeAllViews();
        memoryReasonContainer.setVisibility(View.GONE);
        feedbackContainer.removeAllViews();
        submitButton.setVisibility(View.GONE);

        if (visibleCards.isEmpty()) {
            if (filterRowView != null) filterRowView.setVisibility(View.VISIBLE);
            stemView.setVisibility(View.VISIBLE);
            stemView.setText("当前没有可显示的思维导图");
            metaView.setText("请重新选择章节");
            return;
        }
        if (currentCardIndex < 0) currentCardIndex = 0;
        if (currentCardIndex >= visibleCards.size()) currentCardIndex = visibleCards.size() - 1;
        MemoryCard card = visibleCards.get(currentCardIndex);
        cardBackVisible = true;
        final boolean allowTapFlip = false;
        if (filterRowView != null) filterRowView.setVisibility(View.VISIBLE);
        stemView.setVisibility(View.GONE);
        metaView.setText((currentCardIndex + 1) + "/" + visibleCards.size()
                + "  ·  " + card.chapter
                + "  ·  思维导图"
                + "  ·  轻点节点展开，左右滑动切换知识点");
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
        cardMode = true;
        rememberMode = false;
        wrongMode = false;
        settingsMode = false;
        currentCardChapter = chapter;
        visibleCards.clear();
        visibleCards.addAll(buildMemoryCards(chapter));
        currentCardIndex = 0;
        cardBackVisible = true;
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
        addCardHint(cardBox, "上一页 / 下一页切换导图页 · 轻点节点展开分支 · 左右滑动切换知识点");
        addCardBottomSpacer(cardBox, 56);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(2);
        lp.bottomMargin = dp(8);
        container.addView(cardBox, lp);
    }

    private void renderNotebookMemoryCard(LinearLayout cardBox, MemoryCard card, boolean backVisible, int sectionColor) {
        String markdown = backVisible ? card.backMarkdown : card.frontMarkdown;
        addNotebookMarkdownShell(cardBox, markdown, sectionColor, dp(4));
        addCardHint(cardBox, "左右滑动切换知识点");
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

        TextView guide = text("导图模式：先看主干，再用上一页 / 下一页推进到更深分支；轻点节点会展开，并同步下方详细说明。", 13, MUTED, false);
        guide.setLineSpacing(dp(3), 1.0f);
        mapStage.addView(guide, new LinearLayout.LayoutParams(-1, -2));

        FrameLayout boardShell = new FrameLayout(this);
        boardShell.setBackground(roundedStrokeBackground(
                THEME_LIGHT.equals(themeMode) ? Color.argb(238, 20, 25, 34) : Color.argb(244, 13, 18, 28),
                Color.argb(THEME_LIGHT.equals(themeMode) ? 112 : 76, 115, 152, 219),
                22, 1));
        LinearLayout.LayoutParams boardLp = new LinearLayout.LayoutParams(-1, dp(520));
        boardLp.topMargin = dp(12);
        mapStage.addView(boardShell, boardLp);

        final MindMapCanvasView canvasView = new MindMapCanvasView(this,
                card.mindMapTitle.length() == 0 ? card.knowledge : card.mindMapTitle,
                card.mindMapNodes);
        boardShell.addView(canvasView, new FrameLayout.LayoutParams(-1, -1));

        final TextView boardTag = text("NotebookLM 风格导图", 12, Color.WHITE, true);
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

    private List<String> defaultCardBullets() {
        List<String> values = new ArrayList<>();
        values.add("先抓题干关键词，再判断它问的是层次、设备、地址、协议还是计算。");
        values.add("把相邻概念分开：不要把应用层服务、网络层转发和链路层传输混在一起。");
        values.add("能说出为什么对，也能说出为什么其他说法错，才算掌握。");
        return values;
    }

    private Question currentQuestion() {
        return visibleQuestions.get(currentIndex);
    }

    private void addImage(String path) {
        try {
            InputStream in = getAssets().open(path);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            ImageView img = new ImageView(this);
            img.setImageBitmap(bitmap);
            img.setAdjustViewBounds(true);
            img.setBackgroundColor(Color.WHITE);
            img.setPadding(dp(4), dp(4), dp(4), dp(4));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.bottomMargin = dp(12);
            imageList.addView(img, lp);
        } catch (Exception e) {
            TextView missing = text("图片加载失败：" + path, 14, RED, false);
            imageList.addView(missing);
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

    private void addOptionView(final Question q, final Option opt) {
        TextView view = text(opt.text, 18, TEXT, false);
        view.setGravity(Gravity.CENTER_VERTICAL);
        view.setMinHeight(dp(62));
        view.setBackground(optionBackground(false));
        view.setPadding(dp(16), dp(14), dp(16), dp(14));
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
        renderMarkdown(feedbackContainer, buildFeedbackMarkdown(q, ok));
        Toast.makeText(this, shortProgressToast(q, ok), Toast.LENGTH_SHORT).show();
        refreshMeta(q);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, feedbackContainer.getTop());
            }
        });
    }

    private String buildFeedbackMarkdown(Question q, boolean ok) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(ok ? "回答正确" : "回答错误，已加入错题本").append("\n\n");
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
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.length() == 0) {
                sb.append("\n");
                continue;
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
        for (int i = 0; i < lines.length; i++) {
            String rawLine = lines[i];
            String line = rawLine.trim();
            if (line.length() == 0) {
                flushMarkdownParagraph(container, paragraph);
                addMarkdownSpacer(container, dp(4));
                inOptionJudgmentSection = false;
                continue;
            }
            if (isMarkdownTableStart(lines, i)) {
                flushMarkdownParagraph(container, paragraph);
                i = addMarkdownTableBlock(container, lines, i);
                inOptionJudgmentSection = false;
                continue;
            }
            if (line.startsWith("# ")) {
                flushMarkdownParagraph(container, paragraph);
                int titleColor = line.contains("错误") ? RED : GREEN;
                inOptionJudgmentSection = false;
                addMarkdownText(container, line.substring(2), 20, titleColor, true, 0, 6, 0);
            } else if (line.startsWith("## ")) {
                flushMarkdownParagraph(container, paragraph);
                inOptionJudgmentSection = false;
                addMarkdownText(container, line.substring(3), 18, BLUE, true, 12, 6, 0);
            } else if (line.startsWith("### ")) {
                flushMarkdownParagraph(container, paragraph);
                String title = line.substring(4);
                inOptionJudgmentSection = title.contains("选项判断");
                addMarkdownText(container, title, 16, TEXT, true, 10, 4, 0);
            } else if (line.startsWith("- ")) {
                flushMarkdownParagraph(container, paragraph);
                if (isReasonHighlightLine(line)) {
                    addMarkdownHighlight(container, line.substring(2));
                } else if (inOptionJudgmentSection && isOptionJudgmentLine(line)) {
                    addMarkdownOptionJudgment(container, line.substring(2));
                } else {
                    addMarkdownText(container, "• " + line.substring(2), 15, TEXT, false, 2, 4, dp(8));
                }
            } else {
                inOptionJudgmentSection = false;
                if (paragraph.length() > 0) paragraph.append('\n');
                paragraph.append(line);
            }
        }
        flushMarkdownParagraph(container, paragraph);
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
        if (!trimmed.startsWith("|") || !trimmed.endsWith("|")) return false;
        return trimmed.indexOf('|', 1) > 1;
    }

    private boolean isMarkdownTableSeparator(String line) {
        if (!looksLikeMarkdownTableRow(line)) return false;
        List<String> cells = splitMarkdownTableRow(line);
        if (cells.isEmpty()) return false;
        for (String cell : cells) {
            String normalized = cell.replace(" ", "");
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
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);

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
        if (paragraph.length() == 0) return;
        addMarkdownText(container, paragraph.toString(), 15, TEXT, false, 4, 8, 0);
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
                "理由：", "底层理由：", "做题理由：", "判题理由：", "判断理由：",
                "选择理由：", "关键理由：", "为什么选它：", "为什么这样判题：",
                "原因：", "依据："
        };
        for (String label : labels) {
            if (compact.startsWith("-**" + label + "**") || compact.startsWith("-" + label)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOptionJudgmentLine(String line) {
        String trimmed = line == null ? "" : line.trim();
        if (!trimmed.startsWith("- ")) return false;
        String body = trimmed.substring(2);
        if (body.length() < 2) return false;
        char first = body.charAt(0);
        if (!(first >= 'A' && first <= 'Z')) return false;
        return body.contains("：对，因为")
                || body.contains("：错，因为")
                || body.contains(":对，因为")
                || body.contains(":错，因为")
                || body.contains("：对，")
                || body.contains("：错，")
                || body.contains(":对，")
                || body.contains(":错，");
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
        boolean correct = value.contains("：对，因为") || value.contains(":对，因为") || value.contains("：对，") || value.contains(":对，");
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
        int index = 0;
        while (index < value.length()) {
            int start = value.indexOf("**", index);
            if (start < 0) {
                out.append(value.substring(index));
                break;
            }
            out.append(value.substring(index, start));
            int end = value.indexOf("**", start + 2);
            if (end < 0) {
                out.append(value.substring(start));
                break;
            }
            int spanStart = out.length();
            out.append(value.substring(start + 2, end));
            out.setSpan(new StyleSpan(Typeface.BOLD), spanStart, out.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            index = end + 2;
        }
        return out;
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
        if (!"blank".equals(q.type)) {
            paintSubmittedOptions(q);
        }
        showRememberReason(q);
    }

    private void showRememberReason(Question q) {
        if (memoryReasonContainer == null) return;
        String markdown = rememberReasonMarkdown(q);
        if (markdown.length() == 0) {
            memoryReasonContainer.removeAllViews();
            memoryReasonContainer.setVisibility(View.GONE);
            return;
        }
        renderMarkdown(memoryReasonContainer, "## 理由与辨析\n\n" + markdown);
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
        return new HashSet<>(prefs.getStringSet("wrong", new HashSet<>()));
    }

    private void addWrong(String label) {
        Set<String> wrong = getWrongSet();
        wrong.add(label);
        prefs.edit().putStringSet("wrong", wrong).putInt(wrongMasteryKey(label), 0).apply();
    }

    private void removeWrong(String label) {
        Set<String> wrong = getWrongSet();
        if (wrong.remove(label)) {
            prefs.edit().putStringSet("wrong", wrong).remove(wrongMasteryKey(label)).apply();
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
        return "wrong_mastery_" + label;
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
                renderMarkdown(feedbackContainer, buildFeedbackMarkdown(currentQuestion(), lastAnswerOk));
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
                    "计算机网络错题本",
                    "分享错题本",
                    md,
                    "计算机网络错题本 Markdown 文件见附件。",
                    "已生成错题本 Markdown，并打开分享"
            );
        } catch (Exception e) {
            Toast.makeText(this, "导出失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
                clipboard.setPrimaryClip(ClipData.newPlainText("计算机网络当前题目", text));
            }

            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_SUBJECT, "计算机网络题目 " + q.label);
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
        sb.append("请你像计算机网络老师一样，帮我把这道题讲到小白能懂。");
        sb.append("请基于 App 的短解析和知识点详解继续展开：题眼是什么、为什么这样选、其他常见想法错在哪里、我应该怎么记。\n\n");
        sb.append("# ").append(q.label).append(" ").append(q.typeName).append("\n\n");
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
        sb.append("# 计算机网络错题本\n\n");
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
            sb.append("# 计算机网络复习宝典：章节思维导图全集\n\n");
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
        sb.append("- 生成方式：直接从 App 内置 372 题题库按章节与知识点分组生成。\n\n");
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
        if (LEGACY_UPDATE_REPO_SLUG.equalsIgnoreCase(normalized)) {
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
                info.validationNotes.add("network_quiz_update.json 读取失败，已回退到直接识别 APK：" + metadataError.userMessage);
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
            info.validationNotes.add("Release 未附带 network_quiz_update.json，当前会退回到直接识别 APK。");
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
            throw new UpdateCheckException("这个 Release 缺少可识别的版本号，请检查 tag 或 network_quiz_update.json");
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
        appendDownloadCandidate(info.downloadCandidates, buildGhfastMirrorUrl(info.downloadUrl));
        appendDownloadCandidate(info.downloadCandidates, info.downloadUrl);
        appendDownloadCandidate(info.downloadCandidates, buildGhfastMirrorUrl(buildGithubLatestDownloadUrl(repoSlug, info.apkName)));
        appendDownloadCandidate(info.downloadCandidates, buildGithubLatestDownloadUrl(repoSlug, info.apkName));
        if (info.versionName.length() > 0) {
            appendDownloadCandidate(info.downloadCandidates, buildGhfastMirrorUrl(buildGithubTagDownloadUrl(repoSlug, info.versionName, info.apkName)));
            appendDownloadCandidate(info.downloadCandidates, buildGithubTagDownloadUrl(repoSlug, info.versionName, info.apkName));
        }
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
            if ("计算机网络复习宝典.apk".equalsIgnoreCase(name)) {
                return asset;
            }
        }
        return apkAssets.get(0);
    }

    private String buildJsDelivrUpdateMetadataUrl(String repoSlug) {
        return "https://cdn.jsdelivr.net/gh/" + repoSlug + "@main/release/" + UPDATE_METADATA_NAME;
    }

    private String buildGithubRawUpdateMetadataUrl(String repoSlug) {
        return "https://raw.githubusercontent.com/" + repoSlug + "/main/release/" + UPDATE_METADATA_NAME;
    }

    private String buildGithubLatestMetadataUrl(String repoSlug) {
        return "https://github.com/" + repoSlug + "/releases/latest/download/" + UPDATE_METADATA_NAME;
    }

    private String buildGhfastLatestMetadataUrl(String repoSlug) {
        return buildGhfastMirrorUrl(buildGithubLatestMetadataUrl(repoSlug));
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
        String trimmed = originalUrl == null ? "" : originalUrl.trim();
        if (trimmed.length() == 0) return "";
        if (trimmed.startsWith("https://ghfast.top/")) return trimmed;
        return "https://ghfast.top/" + trimmed;
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

    private void appendDownloadCandidate(List<String> out, String candidate) {
        if (out == null) return;
        String normalized = candidate == null ? "" : candidate.trim();
        if (normalized.length() == 0) return;
        if (!out.contains(normalized)) {
            out.add(normalized);
        }
    }

    private JSONObject fetchFastUpdateMetadata(String repoSlug) throws UpdateCheckException {
        logUpdateDebug("fetchFastUpdateMetadata repo=" + repoSlug);
        UpdateCheckException lastError = null;
        String[] urls = new String[] {
                buildGhfastLatestMetadataUrl(repoSlug),
                buildGithubLatestMetadataUrl(repoSlug),
                buildGithubRawUpdateMetadataUrl(repoSlug),
                buildJsDelivrUpdateMetadataUrl(repoSlug)
        };
        String[] errorMessages = new String[] {
                "读取加速 Release 元数据失败",
                "读取 Release 元数据失败",
                "读取原始更新元数据失败",
                "读取 jsDelivr 更新元数据失败"
        };
        for (int i = 0; i < urls.length; i++) {
            try {
                return readFastGithubJsonObject(
                        urls[i],
                        "还没有找到更新元数据文件",
                        errorMessages[i]
                );
            } catch (UpdateCheckException error) {
                lastError = error;
                logUpdateWarn("fast metadata candidate failed: " + urls[i], error);
            }
        }
        if (lastError != null) {
            throw lastError;
        }
        throw new UpdateCheckException("还没有找到更新元数据文件");
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
            connection.setRequestProperty("User-Agent", "NetworkQuizUpdater/" + currentVersionName());
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
        if (raw.startsWith("network_quiz_update.json 读取失败，已回退到直接识别 APK：")) {
            return "补充说明：版本和安装包已经识别完成，附加元数据读取较慢，不影响下载和安装。";
        }
        if (raw.startsWith("加速更新元数据读取失败，已回退到 GitHub Release：")) {
            return "补充说明：加速通道响应较慢，已自动切换到 GitHub Release 检查。";
        }
        if (raw.startsWith("Release 未附带 network_quiz_update.json")) {
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
        if (raw.startsWith("network_quiz_update.json 读取失败，已回退到直接识别 APK：")
                || raw.startsWith("加速更新元数据读取失败，已回退到 GitHub Release：")
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
        for (String candidate : candidates) {
            try {
                return downloadUpdateApkFromUrl(info, candidate);
            } catch (Exception e) {
                lastError = e;
            }
        }
        if (lastError != null) throw lastError;
        throw new IOException("没有可用的下载地址");
    }

    private File downloadUpdateApkFromUrl(UpdateInfo info, String url) throws Exception {
        HttpURLConnection connection = null;
        InputStream stream = null;
        FileOutputStream out = null;
        File tempFile = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(HTTP_TIMEOUT_MS);
            connection.setReadTimeout(HTTP_TIMEOUT_MS * 2);
            connection.setRequestProperty("User-Agent", "NetworkQuizUpdater/" + currentVersionName());
            connection.setRequestProperty("Accept", "application/octet-stream");
            int code = connection.getResponseCode();
            if (code < 200 || code >= 300) {
                throw new IOException("下载失败，HTTP " + code);
            }
            long total = connection.getContentLengthLong();
            stream = connection.getInputStream();
            File dir = updateCacheDir();
            String fileName = safeFileName(info.apkName, "network-quiz-update.apk");
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
            while ((read = stream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                copied += read;
                if (total > 0) {
                    final int progress = (int) ((copied * 100L) / total);
                    if (progress == 100 || progress - lastProgress >= 5) {
                        lastProgress = progress;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateStatusText = "正在下载更新 " + progress + "%";
                                refreshUpdateSettingViews();
                            }
                        });
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

        FrameLayout shell = new FrameLayout(this);
        shell.setPadding(dp(18), statusBarInset() + dp(18), dp(18), bottomSafeInset() + dp(14));
        shell.setBackgroundColor(Color.argb(THEME_LIGHT.equals(themeMode) ? 54 : 84, 11, 17, 26));

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(18), dp(18), dp(18), dp(16));
        panel.setBackground(choiceSheetBackground());
        panel.setElevation(dp(12));
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

        Button closeButton = chromeButton("关闭");
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(-1, dp(44));
        closeLp.topMargin = dp(14);
        panel.addView(closeButton, closeLp);

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
        int blankCount;
        final List<Option> options = new ArrayList<>();
        final List<String> images = new ArrayList<>();
    }

    private static class Option {
        final String key;
        final String text;

        Option(String key, String text) {
            this.key = key;
            this.text = text;
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
        private MindMapSelectionListener selectionListener;
        private MindMapPageStateListener pageStateListener;
        private String selectedKey = "root";
        private boolean layoutDirty = true;
        private int currentPage = 0;
        private int totalPages = 1;
        private int contentWidth = 0;
        private float pageOffset = 0f;
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

        @Override
        public boolean performClick() {
            super.performClick();
            return true;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                case MotionEvent.ACTION_UP:
                    getParent().requestDisallowInterceptTouchEvent(false);
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
                    performClick();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                default:
                    return super.onTouchEvent(event);
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
            canvas.translate(-pageOffset, 0f);

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
            float padding = dp(18);
            float totalHeight = measureSubtree(rootNode, "root", 0);
            contentWidth = (int) Math.ceil(layoutNode(rootNode, "root", 0, padding, padding, totalHeight, BLUE, null));
            totalPages = Math.max(1, (int) Math.ceil((contentWidth + padding) / Math.max(1f, getWidth())));
            currentPage = Math.max(0, Math.min(currentPage, totalPages - 1));
            if (pageAnimator == null || !pageAnimator.isRunning()) {
                pageOffset = currentPage * getWidth();
            }
            layoutDirty = false;
            notifyPageState();
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
            float worldX = x + pageOffset;
            for (int i = renderNodes.size() - 1; i >= 0; i--) {
                RenderNode node = renderNodes.get(i);
                if (node.rect.contains(worldX, y)) {
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
            int targetPage = Math.max(0, Math.min(totalPages - 1, (int) (node.rect.centerX() / getWidth())));
            goToPage(targetPage, animate);
        }

        private void goToPage(int page, boolean animate) {
            int target = Math.max(0, Math.min(page, totalPages - 1));
            currentPage = target;
            float targetOffset = target * getWidth();
            if (pageAnimator != null) {
                pageAnimator.cancel();
            }
            if (animate) {
                pageAnimator = ValueAnimator.ofFloat(pageOffset, targetOffset);
                pageAnimator.setDuration(240);
                pageAnimator.setInterpolator(new DecelerateInterpolator());
                pageAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        pageOffset = (float) animation.getAnimatedValue();
                        invalidate();
                    }
                });
                pageAnimator.start();
            } else {
                pageOffset = targetOffset;
                invalidate();
            }
            notifyPageState();
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

