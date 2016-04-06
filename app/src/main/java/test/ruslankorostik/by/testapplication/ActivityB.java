package test.ruslankorostik.by.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import test.ruslankorostik.by.testapplication.utils.ImageSaveLoadHelper;

public class ActivityB extends AppCompatActivity {

    private ImageView ivImage;

    private ImageSaveLoadHelper imageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ivImage = (ImageView)findViewById(R.id.ivImage);

        imageHelper = new ImageSaveLoadHelper();

        Intent intent = getIntent();
        String choise = intent.getStringExtra("choise");

        if (choise.equals("1")){
            ivImage.setImageBitmap(imageHelper.loadFileByPath(this, "1"));
        }

        if (choise.equals("2")){
            ivImage.setImageBitmap(imageHelper.loadFileByPath(this, "2"));
        }

    }

}
