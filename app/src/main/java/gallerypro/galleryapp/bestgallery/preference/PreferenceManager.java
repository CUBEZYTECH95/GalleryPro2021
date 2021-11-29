package gallerypro.galleryapp.bestgallery.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    public PreferenceManager(Context context) {

        this.context = context;
        sharedPreferences = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE);

    }

    public void saveGalleryView(boolean isAlbumView){
        editor =sharedPreferences.edit();
        editor.putBoolean("galleryView",isAlbumView);
        editor.apply();
    }

    public boolean getGalleryView(){
        return sharedPreferences.getBoolean("galleryView",true);
    }

    public void  saveSecurityQuestion(String question){
        editor = sharedPreferences.edit();
        editor.putString("sec_ques",question);
        editor.apply();
    }

    public String getSecurityQuestion(){
        return sharedPreferences.getString("sec_ques",null);
    }

    public void saveSecurityAnswer(String answer){
        editor = sharedPreferences.edit();
        editor.putString("sec_ans",answer);
        editor.apply();
    }

    public String getSecurityAnswer(){
        return sharedPreferences.getString("sec_ans",null);
    }

    public void savePin(String pin){
        editor = sharedPreferences.edit();
        editor.putString("pin",pin);
        editor.apply();
    }

    public String getPin(){
        return sharedPreferences.getString("pin",null);
    }

    public void pinSet(boolean isSeteed){
        editor = sharedPreferences.edit();
        editor.putBoolean("pinSet",isSeteed);
        editor.apply();
    }

    public boolean isPinSeted(){
        return sharedPreferences.getBoolean("pinSet",false);
    }

    public void isNightMode(boolean nightMode){
        editor = sharedPreferences.edit();
        editor.putBoolean("night_mode",nightMode);
        editor.apply();
    }

    public boolean checkMode(){
        return sharedPreferences.getBoolean("night_mode",false);
    }


    public void saveAdsTime(long time){
        editor = sharedPreferences.edit();
        editor.putLong("adsTime",time);
        editor.apply();
    }

    public long getAdsTime(){
        return sharedPreferences.getLong("adsTime",0);
    }
}
