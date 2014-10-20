package com.jurgenromeijn.pokedex;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.view.SurfaceView;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;

/**
 * This Activity controls the SurfaceView, the Camera and the Camera button.
 */
public class PokedexActivity extends Activity implements SurfaceHolder.Callback, OnClickListener,
        PictureCallback, AutoFocusCallback, IImageRecognitionResultPresenter {
	private SurfaceView _cameraView;
	private SurfaceHolder _cameraViewHolder;
	private Camera _camera;
	private ImageView _cameraIndicator;
	private Animation _cameraIndicatorAnimation;
    private ImageButton _scanButton;
	private boolean _scanning = false;

    public final static String SECRET_KEY = ""; // Enter secret key

    /**
     * Setup the pokedex and layout.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ImageView view = (ImageView)findViewById(android.R.id.home);
        view.setPadding(0, 0, 16, 0);
        
        setupCameraView();
        setClickListeners();
    }

    /**
     * release all resources on exit.
     */
    @Override
    public void onDestroy()
    {
        if (_camera != null) {
            _camera.stopPreview();
            _camera.setPreviewCallback(null);
            _camera.release();
            _camera = null;
        }
        super.onDestroy();
    }

    /**
     * Click listener, only listens to camera button.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.scanButton:
                if(!_scanning) {
                    _camera.autoFocus(this);
                    _scanning = true;
                    Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_rotation);
                    rotation.setRepeatCount(Animation.INFINITE);
                    _scanButton.startAnimation(rotation);
                }
                break;
            default:
                // Do nothing
                break;
        }
    }

    /**
     * Take a picture after autofocussing.
     *
     * @param success
     * @param camera
     */
    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        // TODO Auto-generated method stub
        _camera.takePicture(null, null, this);
        stopIndicatorBlinking();
    }

    /**
     * Send the picture to the autoRecognition
     *
     * @param data
     * @param camera
     */
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        new ImageRecognitionTask(this, SECRET_KEY).execute(data);
    }

    /**
     * Show the pokemon found in our image.
     *
     * @param pokemon
     */
    @Override
    public void showImageRecognitionResult(String pokemon)
    {
        try {
            startIndicatorBlinking();
            _scanButton.clearAnimation();
            _camera.setPreviewDisplay(_cameraViewHolder);
            _camera.startPreview();
            _scanning = false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Intent pokedexEntry = new Intent(this, EntryDialogActivity.class);
        pokedexEntry.putExtra("pokemon", pokemon);
        startActivity(pokedexEntry);
    }

    /**
     * Need it for the surfaceview, don't do anything with it.
     *
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing
    }

    /**
     * Setup the camera preview and the blinking light
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setupCamera();
        try {
            _camera.setPreviewDisplay(_cameraViewHolder);
            _camera.startPreview();
            startIndicatorBlinking();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Clean up all used resources, because our SurfaceView is no longer visible.
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if (_camera != null)
        {
            _camera.stopPreview();
            _camera.setPreviewCallback(null);
            _camera.release();
            _camera = null;
            stopIndicatorBlinking();
        }
    }

    /**
     * Blink the indicator light.
     */
    private void startIndicatorBlinking()
	{
		if(_cameraIndicator == null) {
	        _cameraIndicator = (ImageView)findViewById(R.id.cameraIndicator);
	        _cameraIndicatorAnimation = new AlphaAnimation(0, 1);
	        _cameraIndicatorAnimation.setDuration(500);
	        _cameraIndicatorAnimation.setInterpolator(new LinearInterpolator());
	        _cameraIndicatorAnimation.setRepeatCount(Animation.INFINITE);
	        _cameraIndicatorAnimation.setRepeatMode(Animation.REVERSE);
		}
		_cameraIndicator.setAlpha(new Float(1));
		_cameraIndicator.startAnimation(_cameraIndicatorAnimation);
	}

    /**
     * Stop the blinking light
     */
	private void stopIndicatorBlinking()
	{
		_cameraIndicator.clearAnimation();
		_cameraIndicator.setAlpha(new Float(0));
	}

    /**
     * Get the cameraview and set callbacks.
     */
    private void setupCameraView()
    {
        _cameraView = (SurfaceView) findViewById(R.id.cameraView);
        _cameraViewHolder = _cameraView.getHolder();
        _cameraViewHolder.addCallback(this);
    }

    /**
     * Set a click listener on the camera button.
     */
    public void setClickListeners()
    {
    	_scanButton = (ImageButton)findViewById(R.id.scanButton);
    	_scanButton.setOnClickListener(this);
    }

    /**
     * Setup the camera and parameters.
     */
    private void setupCamera()
    {
    	_camera = Camera.open();
        Camera.Parameters parameters = _camera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setRotation(90);
        _camera.setParameters(parameters);
        _camera.setDisplayOrientation(90);
    }
}