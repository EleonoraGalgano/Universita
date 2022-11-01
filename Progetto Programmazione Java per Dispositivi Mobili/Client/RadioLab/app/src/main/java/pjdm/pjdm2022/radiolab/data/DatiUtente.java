package pjdm.pjdm2022.radiolab.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class DatiUtente {
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ROLE = "role";
    private static final String ACCESS_TOKEN = "accessToken";
    private Context context;

    public DatiUtente( Context context ) {
        this.context = context;
    }

    public void saveData(String role, String accessToken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ROLE, role);
            editor.putString(ACCESS_TOKEN, accessToken);
            editor.apply();
    }

    public void reset() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ROLE, "role");
        editor.putString(ACCESS_TOKEN, "accessToken");
        editor.apply();
    }

    public String getRole() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ROLE, "");
    }

    public boolean isLogged() {
        return !getAccessToken().equals("accessToken");
    }

    public String getAccessToken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ACCESS_TOKEN, "");
    }
}
