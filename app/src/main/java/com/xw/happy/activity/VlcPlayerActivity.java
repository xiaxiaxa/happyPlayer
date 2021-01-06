package com.xw.happy.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.xw.happy.R;
import com.xw.helper.utils.MLog;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

import static com.xw.happy.ui.CommonUtil.getAbility;

/**
 * @Time: 2020/8/27 10:11
 * @Description: 自定义VlcPlayer
 * @Author: Mr.xw
 */
public class VlcPlayerActivity extends Activity implements IVLCVout.OnNewVideoLayoutListener {
    private static final boolean ENABLE_SUBTITLES = true;
    private static final String TAG = "VlcActivity";
    public static String SAMPLE_URL = "rtp://239.76.245.115:1234";

//    public static String SAMPLE_URL =  "rtsp://10.255.25.214:10143/3ZWCA335226M8U3_0?key=3f682321fb88971f76cc68505261e9b9&tk=35cb05ca-6278-4187-a3b3-3b305efe0f08";
    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_SCREEN = 1;
    private static final int SURFACE_FILL = 2;
    private static final int SURFACE_16_9 = 3;
    private static final int SURFACE_4_3 = 4;
    private static final int SURFACE_ORIGINAL = 5;
    private static int CURRENT_SIZE = SURFACE_BEST_FIT;

    private FrameLayout mVideoSurfaceFrame = null;
    private SurfaceView mVideoSurface = null;
    private SurfaceView mSubtitlesSurface = null;

    private final Handler mHandler = new Handler();
    private View.OnLayoutChangeListener mOnLayoutChangeListener = null;

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;
    private int mVideoHeight = 0;
    private int mVideoWidth = 0;
    private int mVideoVisibleHeight = 0;
    private int mVideoVisibleWidth = 0;
    private int mVideoSarNum = 0;
    private int mVideoSarDen = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MLog.d("VlcPlayerActivity  onCreate====");
//        getAbility("search_page");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vlc_play);
//        SAMPLE_URL = getIntent().getStringExtra("extra_url");
        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        mLibVLC = new LibVLC(this, args);
        mMediaPlayer = new MediaPlayer(mLibVLC);

        mVideoSurfaceFrame = (FrameLayout) findViewById(R.id.video_surface_frame);
        mVideoSurface = (SurfaceView) findViewById(R.id.video_surface);
        if (ENABLE_SUBTITLES) {
            final ViewStub stub = (ViewStub) findViewById(R.id.subtitles_stub);
            mSubtitlesSurface = (SurfaceView) stub.inflate();
            mSubtitlesSurface.setZOrderMediaOverlay(true);
            mSubtitlesSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseData();
    }

    private void releaseData() {

        if (isFinishing()) {
            if (mOnLayoutChangeListener != null) {
                if (mVideoSurfaceFrame != null) {
                    mVideoSurfaceFrame.removeOnLayoutChangeListener(mOnLayoutChangeListener);
                }
                mOnLayoutChangeListener = null;
            }
            if (mMediaPlayer != null) {
                mMediaPlayer.getVLCVout().detachViews();
                mMediaPlayer.pause();
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }

            if (mSubtitlesSurface != null) {
                mSubtitlesSurface = null;
            }

            if (mHandler != null) {
                mHandler.removeCallbacks(null);
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        vlcVout.setVideoView(mVideoSurface);
        if (mSubtitlesSurface != null)
            vlcVout.setSubtitlesView(mSubtitlesSurface);
        vlcVout.attachViews(this);

        Media media = new Media(mLibVLC, Uri.parse(SAMPLE_URL));
        mMediaPlayer.setMedia(media);
        media.release();
        mMediaPlayer.play();
        MLog.d("vlconStart是否播放");

        if (mOnLayoutChangeListener == null) {
            mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
                private final Runnable mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        updateVideoSurfaces();
                    }
                };

                @Override
                public void onLayoutChange(View v, int left, int top, int right,
                                           int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                        mHandler.removeCallbacks(mRunnable);
                        mHandler.post(mRunnable);
                    }
                }
            };
        }
        mVideoSurfaceFrame.addOnLayoutChangeListener(mOnLayoutChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseData();
    }

    private void changeMediaPlayerLayout(int displayW, int displayH) {
        /* Change the video placement using the MediaPlayer API */
        switch (CURRENT_SIZE) {
            case SURFACE_BEST_FIT:
                mMediaPlayer.setAspectRatio(null);
                mMediaPlayer.setScale(0);
                break;
            case SURFACE_FIT_SCREEN:
            case SURFACE_FILL: {
                Media.VideoTrack vtrack = mMediaPlayer.getCurrentVideoTrack();
                if (vtrack == null)
                    return;
                final boolean videoSwapped = vtrack.orientation == Media.VideoTrack.Orientation.LeftBottom
                        || vtrack.orientation == Media.VideoTrack.Orientation.RightTop;
                if (CURRENT_SIZE == SURFACE_FIT_SCREEN) {
                    int videoW = vtrack.width;
                    int videoH = vtrack.height;

                    if (videoSwapped) {
                        int swap = videoW;
                        videoW = videoH;
                        videoH = swap;
                    }
                    if (vtrack.sarNum != vtrack.sarDen)
                        videoW = videoW * vtrack.sarNum / vtrack.sarDen;

                    float ar = videoW / (float) videoH;
                    float dar = displayW / (float) displayH;

                    float scale;
                    if (dar >= ar)
                        scale = displayW / (float) videoW; /* horizontal */
                    else
                        scale = displayH / (float) videoH; /* vertical */
                    mMediaPlayer.setScale(scale);
                    mMediaPlayer.setAspectRatio(null);
                } else {
                    mMediaPlayer.setScale(0);
                    mMediaPlayer.setAspectRatio(!videoSwapped ? "" + displayW + ":" + displayH
                            : "" + displayH + ":" + displayW);
                }
                break;
            }
            case SURFACE_16_9:
                mMediaPlayer.setAspectRatio("16:9");
                mMediaPlayer.setScale(0);
                break;
            case SURFACE_4_3:
                mMediaPlayer.setAspectRatio("4:3");
                mMediaPlayer.setScale(0);
                break;
            case SURFACE_ORIGINAL:
                mMediaPlayer.setAspectRatio(null);
                mMediaPlayer.setScale(1);
                break;
        }
    }

    private void updateVideoSurfaces() {
        int sw = getWindow().getDecorView().getWidth();
        int sh = getWindow().getDecorView().getHeight();
        // sanity check
        if (sw * sh == 0) {
            Log.e(TAG, "Invalid surface size");
            return;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.getVLCVout().setWindowSize(sw, sh);
        }
        if (mVideoSurface != null) {
            ViewGroup.LayoutParams lp = mVideoSurface.getLayoutParams();
            if (mVideoWidth * mVideoHeight == 0) {
                /* Case of OpenGL vouts: handles the placement of the video using MediaPlayer API */
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                mVideoSurface.setLayoutParams(lp);
                lp = mVideoSurfaceFrame.getLayoutParams();
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                mVideoSurfaceFrame.setLayoutParams(lp);
                changeMediaPlayerLayout(sw, sh);
                return;
            }

            if (lp.width == lp.height && lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                /* We handle the placement of the video using Android View LayoutParams */
                if (mMediaPlayer != null) {
                    mMediaPlayer.setAspectRatio(null);
                    mMediaPlayer.setScale(0);
                }
            }

            double dw = sw, dh = sh;
            final boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

            if (sw > sh && isPortrait || sw < sh && !isPortrait) {
                dw = sh;
                dh = sw;
            }

            // compute the aspect ratio
            double ar, vw;
            if (mVideoSarDen == mVideoSarNum) {
                /* No indication about the density, assuming 1:1 */
                vw = mVideoVisibleWidth;
                ar = (double) mVideoVisibleWidth / (double) mVideoVisibleHeight;
            } else {
                /* Use the specified aspect ratio */
                vw = mVideoVisibleWidth * (double) mVideoSarNum / mVideoSarDen;
                ar = vw / mVideoVisibleHeight;
            }

            // compute the display aspect ratio
            double dar = dw / dh;

            switch (CURRENT_SIZE) {
                case SURFACE_BEST_FIT:
                    if (dar < ar)
                        dh = dw / ar;
                    else
                        dw = dh * ar;
                    break;
                case SURFACE_FIT_SCREEN:
                    if (dar >= ar)
                        dh = dw / ar; /* horizontal */
                    else
                        dw = dh * ar; /* vertical */
                    break;
                case SURFACE_FILL:
                    break;
                case SURFACE_16_9:
                    ar = 16.0 / 9.0;
                    if (dar < ar)
                        dh = dw / ar;
                    else
                        dw = dh * ar;
                    break;
                case SURFACE_4_3:
                    ar = 4.0 / 3.0;
                    if (dar < ar)
                        dh = dw / ar;
                    else
                        dw = dh * ar;
                    break;
                case SURFACE_ORIGINAL:
                    dh = mVideoVisibleHeight;
                    dw = vw;
                    break;
            }

            // set display size
            lp.width = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
            lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
            mVideoSurface.setLayoutParams(lp);
            if (mSubtitlesSurface != null)
                mSubtitlesSurface.setLayoutParams(lp);

            // set frame size (crop if necessary)
            lp = mVideoSurfaceFrame.getLayoutParams();
            lp.width = (int) Math.floor(dw);
            lp.height = (int) Math.floor(dh);
            mVideoSurfaceFrame.setLayoutParams(lp);

            mVideoSurface.invalidate();
            if (mSubtitlesSurface != null)
                mSubtitlesSurface.invalidate();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        mVideoWidth = width;
        mVideoHeight = height;
        mVideoVisibleWidth = visibleWidth;
        mVideoVisibleHeight = visibleHeight;
        mVideoSarNum = sarNum;
        mVideoSarDen = sarDen;
        if (isFinishing()){

        } else {
            updateVideoSurfaces();
        }
    }
}
