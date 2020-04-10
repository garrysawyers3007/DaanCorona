package com.lendeasy.daancorona;

import android.content.Context;
import android.os.StrictMode;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.IOException;
import java.io.InputStream;

public class TranslateTo {

    public static String getTranslation(String originalText, Context context) {

        Translate translate;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = context.getResources().openRawResource(R.raw.daancorona)) {

            //Get credentials:
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

            Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage("hi"), Translate.TranslateOption.model("base"));
            return translation.getTranslatedText();

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
        return "";
    }

}
