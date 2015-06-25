package com.hp.advantage.utils;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.hp.advantage.R;

import java.io.IOException;

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private boolean isPreviewRunning;
    private SoundPool soundPool;
    private int soundID;
    private boolean soundLoaded = false;
    private boolean mTakingPicture;
    private PictureCallback capturedIt = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap == null) {
                //   Toast.makeText(getApplicationContext(), "not taken", Toast.LENGTH_SHORT).show();
            } else {
                PlaySound(soundID);

                //   Toast.makeText(getApplicationContext(), "taken", Toast.LENGTH_SHORT).show();
            }
            //showCamera.releaseCameraAndPreview();
            //cameraObject.release();
            mTakingPicture = false;
        }
    };

    public ShowCamera(Context context) {
        super(context);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        // Load the sound
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                soundLoaded = true;
            }
        });
        soundID = soundPool.load(context, R.raw.shutter, 1);
        mTakingPicture = false;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera == null)
            return;
        if (isPreviewRunning) {
            mCamera.stopPreview();
        }

        Parameters parameters = mCamera.getParameters();
        Display display = ((WindowManager) (getContext().getSystemService(Service.WINDOW_SERVICE))).getDefaultDisplay();

        if (display.getRotation() == Surface.ROTATION_0) {
            Camera.Size size = getBestPreviewSize(height, width);
            parameters.setPreviewSize(size.width, size.height);
            mCamera.setDisplayOrientation(90);
        }

        if (display.getRotation() == Surface.ROTATION_90) {
            Camera.Size size = getBestPreviewSize(width, height);
            parameters.setPreviewSize(size.width, size.height);
            parameters.setPreviewSize(width, height);
        }

        if (display.getRotation() == Surface.ROTATION_180) {
            Camera.Size size = getBestPreviewSize(height, width);
            parameters.setPreviewSize(size.width, size.height);
        }

        if (display.getRotation() == Surface.ROTATION_270) {
            Camera.Size size = getBestPreviewSize(width, height);
            parameters.setPreviewSize(size.width, size.height);
            mCamera.setDisplayOrientation(180);
        }

        try {
            mCamera.setParameters(parameters);
        }
        catch (RuntimeException exp)
        {
            LogManager.Error(exp);
        }
        previewCamera();
    }

    private Camera.Size getBestPreviewSize(int width, int height) {
        Camera.Size result = null;
        Camera.Parameters p = mCamera.getParameters();
        for (Camera.Size size : p.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        return result;

    }

    public void previewCamera() {
        if (mCamera == null)
            return;
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
            isPreviewRunning = true;
        } catch (Exception e) {
            //Log.d(APP_CLASS, "Cannot start preview", e);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        releaseCameraAndPreview();
        mCamera = isCameraAvailiable();
        if (mCamera == null)
            return;

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        releaseCameraAndPreview();
    }

    public Camera isCameraAvailiable() {
        Camera object = null;
        try {
            object = Camera.open();
        } catch (Exception e) {
            LogManager.Debug(e);
        }
        return object;
    }

    public void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            isPreviewRunning = false;
        }
        mTakingPicture = false;
    }

    public void takePicture() {
        if (mCamera != null && !mTakingPicture) {
            mTakingPicture = true;
            mCamera.takePicture(null, null, capturedIt);
        }

    }

    protected void PlaySound(int soundID2) {
        // Getting the user sound settings

        if (soundLoaded) {
            AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            float actualVolume = (float) audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = actualVolume / maxVolume;
            soundPool.play(soundID, volume, volume, 1, 0, 1f);
        }
    }
}
