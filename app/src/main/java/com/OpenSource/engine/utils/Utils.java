package com.OpenSource.engine.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.OpenSource.app.R;
import com.OpenSource.engine.application.AppInstance;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.androidannotations.annotations.App;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by parag_sarkar on 27-01-2017.
 */

public class Utils {

    AppInstance appInstance;

    public int checkPlayServices(Activity parentActivity, int requestCode) {
        int result = ConnectionResult.SUCCESS;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability
                .getInstance();
        int resultCode = apiAvailability
                .isGooglePlayServicesAvailable(parentActivity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(parentActivity, resultCode,
                        requestCode).show();
                result = ConnectionResult.RESOLUTION_REQUIRED;

            } else {
                result = ConnectionResult.INTERNAL_ERROR;
            }
        }
        return result;
    }

    public int getCountryPhoneNumberCode(String countryNameCode) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        return phoneUtil
                .getCountryCodeForRegion(countryNameCode.toUpperCase());
    }


    public boolean isValidPhoneNumber(String phoneNumber, String countryNameCode) {
        boolean valid = false;
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, countryNameCode.toUpperCase());
            boolean isPossibleNumber = phoneUtil.isPossibleNumber(numberProto);
            boolean isValidNumber = phoneUtil.isValidNumber(numberProto);
            boolean isFixedLine = phoneUtil.getNumberType(numberProto) == PhoneNumberUtil.PhoneNumberType.FIXED_LINE;
            boolean isValidForRegion = phoneUtil.isValidNumberForRegion(numberProto, countryNameCode.toUpperCase());
            if (isPossibleNumber && isValidNumber && isValidForRegion) {
                valid = true;
            }
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return valid;
    }


    public boolean isValidFixedLinePhoneNumber(String phoneNumber, String countryNameCode) {
        boolean valid = false;
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, countryNameCode.toUpperCase());
            boolean isPossibleNumber = phoneUtil.isPossibleNumber(numberProto);
            boolean isValidNumber = phoneUtil.isValidNumber(numberProto);
            boolean isFixedLine = phoneUtil.getNumberType(numberProto) == PhoneNumberUtil.PhoneNumberType.FIXED_LINE;
            boolean isValidForRegion = phoneUtil.isValidNumberForRegion(numberProto, countryNameCode.toUpperCase());
            if (isPossibleNumber && isValidNumber && isFixedLine && isValidForRegion) {
                valid = true;
            }
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return valid;
    }

    public boolean isValidMobilePhoneNumber(String phoneNumber, String countryNameCode) {
        boolean valid = false;
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, countryNameCode.toUpperCase());
            boolean isPossibleNumber = phoneUtil.isPossibleNumber(numberProto);
            boolean isValidNumber = phoneUtil.isValidNumber(numberProto);
            boolean isMobile = phoneUtil.getNumberType(numberProto) == PhoneNumberUtil.PhoneNumberType.MOBILE;
            boolean isValidForRegion = phoneUtil.isValidNumberForRegion(numberProto, countryNameCode.toUpperCase());
            if (isPossibleNumber && isValidNumber && isMobile && isValidForRegion) {
                valid = true;
            }
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return valid;
    }

    public String formatPhoneNumber(String phoneNumber, String countryNameCode) {
        String formattedPhoneNumber = "";
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        AsYouTypeFormatter formatter;
        formatter = phoneUtil.getAsYouTypeFormatter(countryNameCode.toUpperCase());
        char[] charArray = phoneNumber.toCharArray();
        formatter.clear();
        for (char c : charArray) {
            formattedPhoneNumber = formatter.inputDigit(c);
        }
        return formattedPhoneNumber;
    }

    public File getFileFromUri(Uri uri) {

        File file = new File(uri.getPath());
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public String getUniqueFileName(String extensionWithDotPrefix) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = timeStamp + "_" + extensionWithDotPrefix;
        return fileName;
    }

    public File getFileForCapturingImage() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File imageFile = null;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        try {
            imageFile = File.createTempFile(
                    imageFileName,  // prefix
                    ".jpg",         // suffix
                    storageDir      // directory
            );
        } catch (IOException e) {

        }

        return imageFile;
    }

    public Uri getCaptureImageOutputUri(Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpg";
        Uri outputFileUri = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), imageFileName));
        }
        return outputFileUri;
    }


    public Bitmap getBitmapFromUri(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(appInstance.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap base64StringToBitmap(String base64ImageString) {
        byte[] bytes = Base64.decode(base64ImageString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public String bitmapToBase64String(Bitmap bitmap) {
        return Base64.encodeToString(bitmap2ByteArray(bitmap), Base64.DEFAULT);
    }

    public byte[] bitmap2ByteArray(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }

    public Bitmap byteArray2Bitmap(byte[] bitmap) {
        return BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
    }

    public void errorToast(String errorMessage) {
        Toast toast = Toast.makeText(appInstance, errorMessage,
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }


    public void notProductionReadyCodeError() {

    }


    public void copyFile(File src, File dst) {

        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            try {
                inChannel = new FileInputStream(src).getChannel();
                outChannel = new FileOutputStream(dst).getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
            } finally {
                if (inChannel != null)
                    inChannel.close();
                if (outChannel != null)
                    outChannel.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Spanned getHtmlFormated(String message) {
        Spanned formattedMsg;
        if (Build.VERSION.SDK_INT >= 24) {
            formattedMsg = Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT);
        } else {
            formattedMsg = Html.fromHtml(message);
        }
        return formattedMsg;
    }


    public void clearCache() {
        try {
            File dir = appInstance.getCacheDir();
            FileUtils.cleanDirectory(dir);
        } catch (Exception e) {
        }
    }

    public final int getColor(int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(appInstance, id);
        } else {
            return appInstance.getResources().getColor(id);
        }
    }

    public final boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final boolean isValidWebSiteAddress(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return Patterns.WEB_URL.matcher(target).matches();
        }
    }

    public Bitmap viewToBitmap(View view) {
        //TODO review this code do we need to disable
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap drawingCache = view.getDrawingCache();
        Bitmap b = drawingCache.copy(Bitmap.Config.ARGB_8888, true);
        view.destroyDrawingCache();
        return b;
    }

    public Uri saveBitmapToInternalFileStorageForSharing(String fileName, Bitmap bitmap) {
        File userSharableDir = getUserSharableDirectory();
        File fileToBeShared = new File(userSharableDir, fileName);
        try {
            FileOutputStream fileOutputStream = FileUtils.openOutputStream(fileToBeShared);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bitmapData = bos.toByteArray();
            fileOutputStream.write(bitmapData);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return Uri.fromFile(fileToBeShared);
    }

    @NonNull
    public File getUserSharableDirectory() {
        File userDir = appInstance.getDir("user", Context.MODE_PRIVATE);
        File userSharableDir = new File(userDir, "sharable");
        userSharableDir.mkdir();
        return userSharableDir;
    }

    public void addBorderToView(View v) {
        GradientDrawable border = new GradientDrawable();
        int color = Color.TRANSPARENT;
        Drawable background = v.getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();
        border.setColor(color);
        border.setStroke(10, getColor(android.R.color.holo_blue_bright), 16, 16);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackgroundDrawable(border);
        } else {
            v.setBackground(border);
        }
    }

    public AnimationDrawable addAnimationBorderToView(View v, int backGroundColor) {
        //v.setBackgroundResource(R.drawable.border_animation_rectangle_view);
        AnimationDrawable frameAnimation = (AnimationDrawable) v.getBackground();
        int frames = frameAnimation.getNumberOfFrames();
        for (int i = 0; i < frames; i++) {
            Drawable d = frameAnimation.getFrame(i);
            if (d instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) d;
                gradientDrawable.mutate();
                gradientDrawable.setColor(backGroundColor);
            }
        }
        frameAnimation.mutate();
        return frameAnimation;
    }

    public void removeBorderFromView(View v) {
        GradientDrawable border = new GradientDrawable();
        int color = Color.TRANSPARENT;
        Drawable background = v.getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();
        border.setColor(color);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackgroundDrawable(border);
        } else {
            v.setBackground(border);
        }
    }

    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

  /*  @Bean
    AppPreference appPreferences;

    public double getDistanceInMetersFromMyLocation(LatLng endLocation) {
        LocationInfo locationInfo = appPreferences.getLocationInfoForUserCanBeNull();
        LatLng myLocation = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());
        return SphericalUtil.computeDistanceBetween(myLocation, endLocation);
    }
*/}
