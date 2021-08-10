package agri.route.agriroute;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.callback.Callback;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity
{

    SwipeRefreshLayout swipe ;
    WebView mywebView;
    private String currentUrl="https://www.google.com/";
    private static final int ALL_PERMISSIONS = 101;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR=1;
    private boolean multiple_files = false;
    CheckConnection checkinternet = new CheckConnection();

    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        boolean checkinternetconnection = checkinternet.checkInternetConnection();
        if(checkinternetconnection) {
            swipe = (SwipeRefreshLayout) findViewById(R.id.swippy);
            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    loadweb();
                }
            });

            loadweb();
        }else{
            setContentView(R.layout.internetlost);
        }


    }

    //WebView Code
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void loadweb(){

        mywebView = (WebView) findViewById(R.id.agriRouteView);
        assert mywebView != null;
        WebSettings webSettings = mywebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setGeolocationEnabled(true);
        // webSettings.setGeolocationDatabasePath( getApplicationContext().getFilesDir().getPath() );
        mywebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mywebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
      //  mywebView.addJavascriptInterface(new MyJavascriptInterface(this), "MyJSClient");

        if (Build.VERSION.SDK_INT >= 21)
        {
            webSettings.setMixedContentMode(0);
            mywebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else if (Build.VERSION.SDK_INT >= 19)
        {
            mywebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else
        {
            mywebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (Build.VERSION.SDK_INT > 17)
        {
            mywebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        mywebView.setWebViewClient(new Callback());
        mywebView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);

        mywebView.setWebViewClient(new WebViewClient()
        {
            ProgressDialog progressDialog = null;
            // ProgressDialogue progressDialogue = new ProgressDialogue();

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
               /* if(progressDialog != null || progressDialog.isVisible())
                {
                    progressDialog.dismiss();
                }*/
                // progressDialog.show();

                if(progressDialog == null)
                {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Loding....");
                    progressDialog.show();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                mywebView.loadUrl("javascript:(function() { " + "document.getElementsByClassName('menu-icone-container')[0].style.display='none';  })()");
                swipe.setRefreshing(false);


                try{
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                }catch (Exception exception)
                {
                    exception.printStackTrace();
                }
                /*try {

                        if(progressDialogue.isVisible() || progressDialogue != null)
                        {
                            progressDialogue.dismiss();
                        }
                   // progressDialogue = null;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }*/
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if (progressDialog == null)
                {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
                // progressDialogue.show(getSupportFragmentManager(), "Loading...");

                if(url.startsWith("whatsapp:"))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                if (url.contains("google.com/maps"))
                {
                    Uri gmmIntentUri = Uri.parse(url);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null)
                    {
                        startActivity(mapIntent);
                    }
                    return true;
                }

                currentUrl=url;
                return super.shouldOverrideUrlLoading(view, url);
               /* view.loadUrl(url);
                return true;*/
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Server Error, try after sometime");
                alertDialog.setMessage(description);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        return;
                    }
                });
                alertDialog.show();
            }
        });

        mywebView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback)
            {
                callback.invoke(origin, true, false);
            }

            // Grant permissions for cam
            @Override
            public void onPermissionRequest(final PermissionRequest request)
            {
                Log.d(TAG, "onPermissionRequest");
                MainActivity.this.runOnUiThread(new Runnable()
                {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void run()
                    {
                        Log.d(TAG, request.getOrigin().toString());
                        Log.d(TAG, "GRANTED");
                        request.grant(request.getResources());
                    }
                });
            }

            /*
             * openFileChooser is not a public Android API and has never been part of the SDK.
             */
            //handling input[type="file"] requests for android API 16+
            @SuppressLint("ObsoleteSdkInt")
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                if (multiple_files && Build.VERSION.SDK_INT >= 18)
                {
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
            }

            //handling input[type="file"] requests for android API 21+
            @SuppressLint("InlinedApi")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                if (file_permission())
                {
                    String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

                    //checking for storage permission to write images for upload
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, perms, FCR);
                    }
                    else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, FCR);
                    }
                    else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, FCR);
                    }
                    if (mUMA != null)
                    {
                        mUMA.onReceiveValue(null);
                    }
                    mUMA = filePathCallback;
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null)
                    {
                        File photoFile = null;
                        try
                        {
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", mCM);
                        }
                        catch (IOException ex)
                        {
                            Log.e(TAG, "Image file creation failed", ex);
                        }
                        if (photoFile != null)
                        {
                            mCM = "file:" + photoFile.getAbsolutePath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        }
                        else
                        {
                            takePictureIntent = null;
                        }
                    }
                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType("*/*");
                    if (multiple_files)
                    {
                        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    }
                    Intent[] intentArray;
                    if (takePictureIntent != null)
                    {
                        intentArray = new Intent[]{takePictureIntent};
                    }
                    else
                    {
                        intentArray = new Intent[0];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "File Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, FCR);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        mywebView.loadUrl(currentUrl); //add your test web/page address here

        swipe.setRefreshing(true);
    }

    public void get_info(){
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(mywebView.getUrl(), "DEVICE=android");
        cookieManager.setCookie(mywebView.getUrl(), "DEV_API=" + Build.VERSION.SDK_INT);
    }

    //callback reporting if error occurs
    public class Callback extends WebViewClient{
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean file_permission(){
        if(Build.VERSION.SDK_INT >=23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
            return false;
        }else{
            return true;
        }
    }

    //creating new image file here
    private File createImageFile() throws IOException{
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".jpg",storageDir);
    }

    //back/down key handling
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode){
                case KeyEvent.KEYCODE_BACK:
                    if(mywebView.canGoBack()){
                        mywebView.goBack();
                    }else{
                        finish();
                       /* if (backPressedTime + 2000 > System.currentTimeMillis()) {
                            backToast.cancel();
                            super.onBackPressed();
                           // return;
                        } else {
                            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
                            backToast.show();
                        }
                        backPressedTime = System.currentTimeMillis();*/
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}