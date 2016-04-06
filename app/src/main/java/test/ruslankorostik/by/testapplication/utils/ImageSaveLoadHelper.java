package test.ruslankorostik.by.testapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import test.ruslankorostik.by.testapplication.constants.Constants;


public class ImageSaveLoadHelper {

    public String saveImageToExternalStorage(Bitmap image, Context context, String name) {

        String APP_PATH_SD_CARD = "/data/" + name + "/";
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD;
        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream fOut = null;
            File file = new File(fullPath, Constants.FILENAME);
            file.createNewFile();
            fOut = new FileOutputStream(file);
            Bitmap bitmap = null;
            if ((image.getWidth() > 1000) || (image.getHeight() > 1000)) {
                Integer _height = image.getHeight();
                Integer _width = image.getWidth();
                Double dheight = _height * 0.5;
                Double dwidth = _width * 0.5;
                Integer height = dheight.intValue();
                Integer width = dwidth.intValue();
                bitmap = Bitmap.createScaledBitmap(
                        image, width, height, false);
            } else {
                bitmap = image;
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), file.getName());

         return fullPath;
        } catch (Exception e) {
            return fullPath;
        }
    }


    public boolean isSdReadable() {
        boolean mExternalStorageAvailable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
        } else {
            mExternalStorageAvailable = false;
        }
        return mExternalStorageAvailable;
    }


    public Bitmap loadFileByPath(Context context, String name) {

        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/" + name + "/";
        Bitmap thumbnail = null;

        try {
            if (isSdReadable() == true) {
                thumbnail = BitmapFactory.decodeFile(fullPath  + Constants.FILENAME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (thumbnail == null) {
            try {

                File filePath = context.getFileStreamPath(Constants.FILENAME);
                FileInputStream fi = new FileInputStream(filePath);
                thumbnail = BitmapFactory.decodeStream(fi);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return thumbnail;
    }

}
