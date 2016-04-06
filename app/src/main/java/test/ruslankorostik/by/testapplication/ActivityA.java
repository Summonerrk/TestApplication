package test.ruslankorostik.by.testapplication;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import test.ruslankorostik.by.testapplication.constants.Constants;
import test.ruslankorostik.by.testapplication.network.Network;
import test.ruslankorostik.by.testapplication.utils.ImageSaveLoadHelper;

public class ActivityA extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Bitmap> {

    private FloatingActionButton fab;

    private ProgressDialog dialog;

    private static String choise = "0";

    private ImageSaveLoadHelper imageHelper;

    private Bitmap image1, image2;

    private ImageView ivFirst, ivSecond;

    private CheckBox cbFirst, cbSecond;

    private boolean loading1IsActive = false;
    private boolean loading2IsActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dialog = new ProgressDialog(ActivityA.this);

        cbFirst = (CheckBox)findViewById(R.id.cbFirst);
        cbFirst.setVisibility(View.GONE);
        cbFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFirst();
            }
        });

        cbSecond= (CheckBox)findViewById(R.id.cbSecond);
        cbSecond.setVisibility(View.GONE);
        cbSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSecond();
            }
        });


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityA.this, ActivityB.class);
                intent.putExtra("choise", choise);
                startActivity(intent);
            }
        });
        fab.setVisibility(View.GONE);


        ivFirst = (ImageView)findViewById(R.id.ivFirst);
        ivSecond = (ImageView)findViewById(R.id.ivSecond);

        //class, which helps with IO
        imageHelper = new ImageSaveLoadHelper();

        //after oncreate fab is hidden, because images not loaded, and user dont choise anyone
        //but if this conditions was comply - we can show fab
        tryToShowFab();


        checkImagesIsAvailable();
    }


    //if images available (can be loaded from storage)  - use can choose one
    //if not - we must load both
    public void checkImagesIsAvailable(){

        image1 = imageHelper.loadFileByPath(this, "1");
        image2 = imageHelper.loadFileByPath(this, "2");

        if ((image1 == null) || (image2 == null)){
            getImagesByUrl();
        } else {
            ivFirst.setImageBitmap(image1);
            ivSecond.setImageBitmap(image2);
        }
    }

    //if connection is available - we can start to download, if not - alert dialog shows
    public void getImagesByUrl() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()){

            if (image1 == null){
                downloadFirstImage();
            }

            if (image2 == null){
                downloadSecondImage();
            }
        } else {
            show_Connection_Dialog();
        }
    }

    //show until inet connection is off, when it be turn on - we can start to download
    public void show_Connection_Dialog(){
        new AlertDialog.Builder(this)
                .setTitle(Constants.CONNECTION_PROBLEM)
                .setPositiveButton(Constants.TRY_AGAIN, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getImagesByUrl();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }


    public void downloadFirstImage() {

        //this parameter need to show ProgressDialog if screen will be turned
        loading1IsActive = true;

        //create Loader
        Bundle bndl = new Bundle();
        bndl.putString("URL", Constants.URL_OF_1_IMAGE);
        getLoaderManager().initLoader(1, bndl, this);
    }

    public void downloadSecondImage() {
        loading2IsActive = true;
        Bundle bndl2 = new Bundle();
        bndl2.putString("URL", Constants.URL_OF_2_IMAGE);
        getLoaderManager().initLoader(2, bndl2, this);
    }

    public void tryShowWaiting(){
        if ((dialog.isShowing() != true)&&((loading1IsActive == true)||(loading2IsActive == true))) {
            dialog.setMessage(Constants.WAIT);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public void hideWaiting(){
        if ((dialog.isShowing() == true)&&((loading1IsActive == false)&&(loading2IsActive == false))) {
            dialog.dismiss();
        }
    }

    @Override
    public Loader<Bitmap> onCreateLoader(int id, Bundle args) {

        Loader<Bitmap> loader = null;
        loader = new Network(this, args);

        tryShowWaiting();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Bitmap> loader, Bitmap result) {
        if (result != null){

            //handle Bitmap result
            handleResult(loader.getId(), result);

        } else {
            Toast toast = Toast.makeText(getApplicationContext(), Constants.CONNECTION_PROBLEM
                    , Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Bitmap> loader) {
    }

    //images is too large
    public Bitmap resize(Bitmap image, double size){
        Bitmap bitmap = null;

        if ((image.getWidth() > 1000) || (image.getHeight() > 1000)) {

            Integer _height = image.getHeight();
            Integer _width = image.getWidth();
            Double dheight = _height * size;
            Double dwidth = _width * size;
            Integer height = dheight.intValue();
            Integer width = dwidth.intValue();
            bitmap = Bitmap.createScaledBitmap(
                    image, width, height, false);
        } else {
            bitmap = image;
        }

        return bitmap;
    }


    @Override
    protected void onResume() {
        super.onResume();

        // if user stop app, and delete images from extstorage
        checkImagesIsAvailable();
        showChoise();
        tryToShowFab();
    }

    //if images loaded  - user can choose
    private void showChoise(){
        if ((image1 != null) && (image2 != null)){
            cbFirst.setVisibility(View.VISIBLE);
            cbSecond.setVisibility(View.VISIBLE);
        }
    }

    private void tryToShowFab(){

        if (cbFirst.isChecked() == true){
            fab.setVisibility(View.VISIBLE);
        } else {
            if (cbSecond.isChecked() == true){
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }
        }
    }

    private void checkFirst(){

        // this  parameter will go into Intent in ActB
        choise = "1";

        cbSecond.setChecked(false);
        tryToShowFab();
    }

    private void checkSecond(){

        choise = "2";
        cbFirst.setChecked(false);
        tryToShowFab();
    }

    private void handleResult(int i, Bitmap result){
        if (i == 1){
            image1 = resize(result, 0.5);
            ivFirst.setImageBitmap(image1);

            // save on ExtStorage after loaded
            imageHelper.saveImageToExternalStorage(image1, ActivityA.this, "1");
            loading1IsActive = false;
        }

        if (i == 2){
            image2 = resize(result, 0.5);
            ivSecond.setImageBitmap(image2);
            imageHelper.saveImageToExternalStorage(image2, ActivityA.this, "2");
            loading2IsActive = false;
        }

        hideWaiting();
        showChoise();
    }



    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // this needed to show progress dialog after turning of screen
        outState.putBoolean("loading1IsActive", loading1IsActive);
        outState.putBoolean("loading2IsActive", loading2IsActive);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // part to show images if user turn screen when they will be download
        image1 = imageHelper.loadFileByPath(this, "1");
        image2 = imageHelper.loadFileByPath(this, "2");

        ivFirst = (ImageView)findViewById(R.id.ivFirst);
        ivSecond = (ImageView)findViewById(R.id.ivSecond);

        ivFirst.setImageBitmap(image1);
        ivSecond.setImageBitmap(image2);

        // this needed to show progress dialog after turning of screen
        loading1IsActive = savedInstanceState.getBoolean("loading1IsActive");
        loading2IsActive = savedInstanceState.getBoolean("loading2IsActive");

        if ((loading1IsActive == true)||(loading2IsActive == true)){
            tryShowWaiting();
        }
    }

}
