package io.auraapp.auraandroid.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import io.auraapp.auraandroid.Communicator.CommunicatorState;
import io.auraapp.auraandroid.R;
import io.auraapp.auraandroid.common.Config;
import io.auraapp.auraandroid.common.EmojiHelper;
import io.auraapp.auraandroid.common.ExternalInvocation;
import io.auraapp.auraandroid.common.Peer;
import io.auraapp.auraandroid.common.PermissionHelper;
import io.auraapp.auraandroid.ui.common.CommunicatorProxy;
import io.auraapp.auraandroid.ui.debug.DebugFragment;
import io.auraapp.auraandroid.ui.permissions.PermissionsFragment;
import io.auraapp.auraandroid.ui.profile.ProfileFragment;
import io.auraapp.auraandroid.ui.profile.profileModel.MyProfileManager;
import io.auraapp.auraandroid.ui.tutorial.ColorStep;
import io.auraapp.auraandroid.ui.tutorial.EnabledStep;
import io.auraapp.auraandroid.ui.tutorial.NameStep;
import io.auraapp.auraandroid.ui.tutorial.SwipeStep;
import io.auraapp.auraandroid.ui.tutorial.TextStep;
import io.auraapp.auraandroid.ui.tutorial.TutorialStep;
import io.auraapp.auraandroid.ui.world.PeerMapTransformer;
import io.auraapp.auraandroid.ui.world.PeerSlogan;
import io.auraapp.auraandroid.ui.world.WorldFragment;

import static io.auraapp.auraandroid.common.FormattedLog.d;
import static io.auraapp.auraandroid.common.FormattedLog.i;
import static io.auraapp.auraandroid.common.FormattedLog.v;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "@aura/main";

    private static final int BROKEN_BT_STACK_ALERT_DEBOUNCE = 1000 * 60;

    private MyProfileManager mMyProfileManager;
    private CommunicatorProxy mCommunicatorProxy;
    private boolean inForeground = false;
    private SharedPreferences mPrefs;

    private long mBrokenBtStackLastVisibleTimestamp;
    private CommunicatorState mCommunicatorState;
    private Set<Peer> mPeers = new HashSet<>();
    private final Handler mHandler = new Handler();
    /**
     * slogan:PeerSlogan
     */
    TreeMap<String, PeerSlogan> mPeerSloganMap = new TreeMap<>();
    private DialogManager mDialogManager;

    private ScreenPager mPager;
    private ScreenPagerAdapter mPagerAdapter;
    private ToolbarAspect mToolbarAspect;
    private DebugFragment mDebugFragment;
    private WorldFragment mWorldFragment;
    private ProfileFragment mProfileFragment;

    @Override
    @ExternalInvocation
    protected void onCreate(Bundle savedInstanceState) {

        // Necessary for splash screen
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);

        super.onCreate(savedInstanceState);

        mHandler.post(() -> {
            v(TAG, "onCreate, intent: %s", getIntent().getAction());

            setContentView(R.layout.activity_main);

            mPager = findViewById(R.id.pager);

            mWorldFragment = WorldFragment.create(
                    this,
                    slogan -> {
                        if (mMyProfileManager.getProfile().getSlogans().contains(slogan)) {
                            toast(R.string.ui_world_toast_slogan_already_adopted);
                        } else if (mMyProfileManager.spaceAvailable()) {
                            mMyProfileManager.adopt(slogan);
                        } else {
                            mDialogManager.showReplace(
                                    mMyProfileManager.getProfile().getSlogans(),
                                    sloganToReplace -> mMyProfileManager.replace(sloganToReplace, slogan)
                            );
                        }
                    });

            mMyProfileManager = new MyProfileManager(this);
            mDialogManager = new DialogManager(this);

            mProfileFragment = ProfileFragment.create(this, mMyProfileManager, mDialogManager);

            mPagerAdapter = new ScreenPagerAdapter(
                    getSupportFragmentManager(),
                    mProfileFragment,
                    mWorldFragment,
                    mPager,
                    this);
            mPager.setAdapter(mPagerAdapter);

            // Load preferences
            mPrefs = getSharedPreferences(Config.PREFERENCES_BUCKET, MODE_PRIVATE);

            String prefKey = getString(R.string.prefs_current_screen_key);

            if (!PermissionHelper.granted(this)) {
                showPermissionMissingFragment();
            } else {
                String currentScreen = mPrefs.getString(prefKey, ScreenPagerAdapter.SCREEN_WELCOME);
                i(TAG, "Current screen is set to %s", currentScreen);
                mPager.goTo(mPagerAdapter.getClassForHandle(currentScreen), false);

                mPager.addChangeListener(fragment -> mPrefs.edit()
                        .putString(prefKey, mPagerAdapter.getHandleForClass(fragment.getClass()))
                        .apply());
            }

            mMyProfileManager.addChangedCallback(event -> {
                d(TAG, "My profile changed");
                mCommunicatorProxy.updateMyProfile(mMyProfileManager.getProfile());
                reflectStatus();
                switch (event) {
                    case MyProfileManager.EVENT_ADOPTED:
//                        mProfileFragment.notifySlogansChanged();
                        toast(R.string.ui_profile_toast_slogan_adopted);
                        break;
                    case MyProfileManager.EVENT_REPLACED:
//                        mProfileFragment.notifySlogansChanged();
                        toast(R.string.ui_profile_toast_slogan_replaced);
                        break;
                    case MyProfileManager.EVENT_DROPPED:
//                        mProfileFragment.notifySlogansChanged();
                        toast(R.string.ui_profile_toast_slogan_dropped);
                        break;
                    case MyProfileManager.EVENT_COLOR_CHANGED:
                        break;
                    case MyProfileManager.EVENT_NAME_CHANGED:
                        toast(R.string.ui_profile_toast_name_changed);
                        break;
                    case MyProfileManager.EVENT_TEXT_CHANGED:
                        toast(R.string.ui_profile_toast_text_changed);
                        break;
                    default:
                        throw new RuntimeException("Unknown slogan event " + event);
                }
            });
            mCommunicatorProxy = new CommunicatorProxy(
                    this,
                    peers -> {
                        mPeers = peers;
                        mPeerSloganMap = PeerMapTransformer.buildMapFromPeerList(peers);
                        mWorldFragment.notifyPeerSlogansChanged(mPeerSloganMap);
                        reflectStatus();
                        mWorldFragment.setData(mCommunicatorState, mPeerSloganMap, mPeers);
                        mWorldFragment.updateAllViews();
                    },
                    peer -> {
                        for (Peer candidate : mPeers) {
                            if (candidate.mId.equals(peer.mId)) {
                                mPeers.remove(candidate);
                                break;
                            }
                        }
                        mPeers.add(peer);
                        mPeerSloganMap = PeerMapTransformer.buildMapFromPeerAndPreviousMap(peer, mPeerSloganMap);
                        mWorldFragment.notifyPeerSlogansChanged(mPeerSloganMap);
                        reflectStatus();
                        mWorldFragment.setData(mCommunicatorState, mPeerSloganMap, mPeers);
                        mWorldFragment.updateAllViews();
                    },
                    state -> {
                        if (mCommunicatorState == null || !mCommunicatorState.mScanning && state.mScanning) {
                            // Scan just started, let's make sure we hide the "looking around" info if
                            // nothing is found for some time.
                            mHandler.postDelayed(() -> {
                                reflectStatus();
                                mWorldFragment.setData(mCommunicatorState, mPeerSloganMap, mPeers);
                                mWorldFragment.reflectCommunicatorState();
                                mProfileFragment.reflectCommunicatorState(mCommunicatorState);
                            }, Config.MAIN_LOOKING_AROUND_SHOW_DURATION);
                        }
                        mCommunicatorState = state;
                        reflectStatus();
                        mWorldFragment.setData(mCommunicatorState, mPeerSloganMap, mPeers);
                        mWorldFragment.reflectCommunicatorState();
                        mProfileFragment.reflectCommunicatorState(mCommunicatorState);
                    });

            mDebugFragment = DebugFragment.create(this, mMyProfileManager);

            mToolbarAspect = new ToolbarAspect(
                    this,
                    mPager,
                    mPrefs,
                    mCommunicatorProxy,
                    mMyProfileManager,
                    mHandler,
                    mDebugFragment);
            mToolbarAspect.initToolbar();

            if (mToolbarAspect.isAuraEnabled()) {
                mCommunicatorProxy.enable();
            }

//        EmojiCompat.init(new BundledEmojiCompatConfig(this));

            mCommunicatorProxy.updateMyProfile(mMyProfileManager.getProfile());

            reflectStatus();
            mWorldFragment.setData(mCommunicatorState, mPeerSloganMap, mPeers);
        });
    }

    // TODO Bug: peer adopts and drops slogan but stays visible here

    // TODO keep peers if aura disabled and communicator destroyed

    private void reflectStatus() {
        v(TAG, "Reflecting status, peers: %d, slogans: %d, state: %s", mPeers.size(), mPeerSloganMap.size(), mCommunicatorState);

        if (mToolbarAspect != null && mToolbarAspect.isDebugFragmentEnabled()) {
            mDebugFragment.update(mCommunicatorState, mPeers);
        }

        if (mCommunicatorState == null) {
            return;
        }
        if (!mCommunicatorState.mHasPermission) {
            showPermissionMissingFragment();
            return;
        }
        if (mCommunicatorState.mRecentBtTurnOnEvents >= Config.COMMUNICATOR_RECENT_BT_TURNING_ON_EVENTS_ALERT_THRESHOLD) {
            showBrokenBtStackAlert();
        }
    }

    private void showBrokenBtStackAlert() {
        String prefKey = getString(R.string.prefs_hide_broken_bt_warning_key);
        if (!inForeground
                || System.currentTimeMillis() - mBrokenBtStackLastVisibleTimestamp > BROKEN_BT_STACK_ALERT_DEBOUNCE
                || mPrefs.getBoolean(prefKey, false)) {
            return;
        }
        mDialogManager.showBtBroken(neverShowAgain -> {
            if (neverShowAgain) {
                mPrefs.edit().putBoolean(prefKey, true).apply();
            } else {
                mBrokenBtStackLastVisibleTimestamp = System.currentTimeMillis();
            }
        });
    }

    private void toast(@StringRes int text) {
        Toast.makeText(this, EmojiHelper.replaceShortCode(getString(text)), Toast.LENGTH_SHORT).show();
    }

    private void toast(String text) {
        Toast.makeText(this, EmojiHelper.replaceShortCode(text), Toast.LENGTH_SHORT).show();
    }

    @FunctionalInterface
    interface ToolbarButtonVisibilityUpdater {
        void update(Fragment fragment);
    }

    /**
     * This is called once, after onCreate
     * https://stackoverflow.com/questions/7705927/android-when-is-oncreateoptionsmenu-called-during-activity-lifecycle
     */
    @Override
    @ExternalInvocation
    public boolean onCreateOptionsMenu(Menu menu) {
        mHandler.post(() -> mToolbarAspect.createOptionsMenu(menu));
        return true;
    }

    private void showPermissionMissingFragment() {
        mPagerAdapter.addPermissionsFragment();
        mPager.goTo(PermissionsFragment.class, false);
    }

    @Override
    @ExternalInvocation
    protected void onResume() {
        super.onResume();

        mHandler.post(() -> {
            if (!PermissionHelper.granted(this)) {
                showPermissionMissingFragment();
                return;
            }

            mCommunicatorProxy.startListening();
            if (mToolbarAspect.isAuraEnabled()) {
                mCommunicatorProxy.askForPeersUpdate();
            }

            inForeground = true;
            mWorldFragment.onResume();
        });
    }

    @Override
    @ExternalInvocation
    protected void onPause() {
        super.onPause();

        mHandler.post(() -> {
            mCommunicatorProxy.stopListening();
            inForeground = false;

            mWorldFragment.onPause();
        });
    }


    TutorialManager mTutorialManager = null;

    public void showTutorial() {
        if (mTutorialManager != null) {
            return;
        }
        mTutorialManager = new TutorialManager(this, findViewById(R.id.activity_wrapper), mPager, () -> {
            mTutorialManager = null;
        });
        mTutorialManager.goTo(EnabledStep.class);
    }

    static class TutorialManager {

        private final Context mContext;
        private final RelativeLayout mRootView;
        private final ScreenPager mPager;
        private final Runnable mEndCallback;
        private LayoutInflater mInflater;
        private View mCurrentScreen;
        private TutorialStep mCurrentStep = null;

        public TutorialManager(Context context,
                               RelativeLayout rootView,
                               ScreenPager pager,
                               Runnable endCallback) {
            mContext = context;
            mRootView = rootView;
            mPager = pager;
            mEndCallback = endCallback;
            mInflater = LayoutInflater.from(mContext);
        }

        public void goTo(Class<? extends TutorialStep> step) {
            if (step == null) {
                close();
                return;
            }
            if (mCurrentStep != null) {
                mCurrentStep.leave();
                removeCurrentScreen();
            }
            if (step.equals(EnabledStep.class)) {
                mCurrentStep = new EnabledStep(mRootView, mContext, mPager);
            } else if (step.equals(SwipeStep.class)) {
                mCurrentStep = new SwipeStep(mRootView, mContext, mPager);
            } else if (step.equals(ColorStep.class)) {
                mCurrentStep = new ColorStep(mRootView, mContext, mPager);
            } else if (step.equals(NameStep.class)) {
                mCurrentStep = new NameStep(mRootView, mContext, mPager);
            } else if (step.equals(TextStep.class)) {
                mCurrentStep = new TextStep(mRootView, mContext, mPager);
            }
            mCurrentScreen = mCurrentStep.enter();

            Button backButton = mCurrentScreen.findViewById(R.id.tutorial_back);
            if (backButton != null) {
                backButton.setOnClickListener($ -> goTo(mCurrentStep.getPrevious()));
            }
            Button nextButton = mCurrentScreen.findViewById(R.id.tutorial_next);
            if (nextButton != null) {
                nextButton.setOnClickListener($ -> goTo(mCurrentStep.getNextStep()));
            }

            mCurrentScreen.findViewById(R.id.tutorial_overlay).setOnClickListener($ -> {
                // Just catch clicks so that they don't bubble to the background
            });
            mRootView.addView(mCurrentScreen);
        }

        private void removeCurrentScreen() {
            i(TAG, "Removing current tutorial screen");
            ((ViewGroup) mCurrentScreen.getParent()).removeView(mCurrentScreen);
            mCurrentScreen = null;
        }

        private void close() {
            removeCurrentScreen();
            mPager.setLocked(false);
            mEndCallback.run();
        }
    }
}
