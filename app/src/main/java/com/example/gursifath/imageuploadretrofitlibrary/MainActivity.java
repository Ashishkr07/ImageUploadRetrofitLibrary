package com.example.gursifath.imageuploadretrofitlibrary;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.ShutterCallback, Camera.PictureCallback , TextToSpeech.OnInitListener {

    Button btnUpload, btnPickImage;
    String mediaPath;
    ImageView imgView;
    String[] mediaColumns = {MediaStore.Video.Media._ID};
    ProgressDialog progressDialog;
    String encodedImage;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    String recognised;
    Camera mCamera;
    SurfaceView mPreview;
    int counter=100;
    boolean first=true;
    private TextToSpeech tts;
    float score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Bundle se recognised

        Intent i=getIntent();
        recognised=i.getStringExtra("object");
        tts = new TextToSpeech(this,this);
        speakOut("Searching for"+ recognised);

        mPreview = (SurfaceView)findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            //ask for authorisation
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);
        else
            mCamera = Camera.open();


    }
    private void uploadFile(Bitmap bm) {

        // Map is used to multipart the file using okhttp3.RequestBody

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Log.d("lala",encodedImage);

//        Request request = Request.Builder
//
//        Call<ResponseBody> call = service.getStringRequestBody(body);
//        Response<ResponseBody> response = call.execute();
//        String value = response.body().string();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        String value=encodedImage;
        Call<Answer> user = apiService.getUser(value,recognised);
        user.enqueue(new Callback<Answer>() {
            @Override
            public void onResponse(Call<Answer> call, Response<Answer> response) {

               // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                Log.d("lala",response.toString());
                Log.d("lala", String.valueOf(response.body().getScore()));
                speakOut(Level(response.body().getScore()));

            }

            @Override
            public void onFailure(Call<Answer> call, Throwable t) {
                Log.d("dada" , t.getLocalizedMessage());

            }
        });
//        user.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
//                Log.d("lala",response.toString());
//                Log.d("lala", response.body());
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//
//                Log.d("dada" , t.getLocalizedMessage());
//
//            }
//        });
        // Parsing any Media type file
//        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
//        MultipartBody.Part photoToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
//        RequestBody photoname = RequestBody.create(MediaType.parse("text/plain"), file.getName());
//
//        ApiService getResponse = ApiClient.getClient().create(ApiService.class);
//        Call<ServerResponse> call = getResponse.uploadPhoto(photoToUpload, photoname);
//        call.enqueue(new Callback<ServerResponse>() {
//            @Override
//            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
//                ServerResponse serverResponse = response.body();
//                if (serverResponse != null) {
//                    if (serverResponse.getSuccess()) {
//                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    assert serverResponse != null;
//                    //Log.v("Response", serverResponse.toString());
//                }
//                progressDialog.dismiss();
//            }
//
//
//            @Override
//            public void onFailure(Call<ServerResponse> call, Throwable t) {
//
//            }
//        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    public void onDestroy() {
        if(tts != null)
        {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
        mCamera.release();
        Log.d("CAMERA","Destroy");

    }

    private Camera.PreviewCallback  previewCallback= new Camera.PreviewCallback()
    {
        @Override
        public void onPreviewFrame(byte[] data,Camera cam)
        {
            if(first&&tts!=null)
            {
                first=false;
                speakOut("Searching for "+ recognised);

            }
            Camera.Size previewSize = cam.getParameters().getPreviewSize();
            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21,previewSize.width,previewSize.height, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0,0,previewSize.width,previewSize.height),80,baos);
            byte[] jdata = baos.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(jdata,0,jdata.length);
            counter++;
            if(counter>=130)
            {
                counter=0;
                Log.d("lala","hogaya");
                //Code to make network calls
             uploadFile(bitmap);

            }
        }
    };

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        //Here, we chose internal storage
        try {
            FileOutputStream out = openFileOutput("picture.jpg", Activity.MODE_PRIVATE);
            out.write(data);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void onShutter() {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
            mCamera.setPreviewCallback(previewCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        params.setPreviewSize(selected.width,selected.height);
        mCamera.setParameters(params);
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("PREVIEW","surfaceDestroyed");
    }


    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS)
        {
            int result = tts.setLanguage(Locale.US);
//          int result = tts.setLanguage(Locale.CHINESE);
            tts.setPitch((float) 0.6);
            tts.setSpeechRate((float) 0.5);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

            }


        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut(String toSpeak) {

        tts.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);

    }

    public String Level(float val)
    {
        Log.d("lalala12",String.valueOf(val));
        if(val<=0.1)
        {
            return "Very Cold";
        }
        else if(val<=0.3)
        {
            return "Cold";
        }
        else if(val<=0.4)
        {
            return "Cold";
        }
        else if(val<=0.5)
        {
            return "Warm";
        }
        else if(val<=0.6)
        {
            return "Found it";
        }
        else
        {
            return "Found it";
        }
    }
}


