package com.lendeasy.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;

public class PDFActivity extends AppCompatActivity implements DownloadFile.Listener{

    PDFPagerAdapter pdfPagerAdapter;
    RemotePDFViewPager remotePDFViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f);

        String url="https://daancorona.tech/media/tnc.pdf";

        remotePDFViewPager = new RemotePDFViewPager(PDFActivity.this, url, this);
    }

    @Override
    public void onSuccess(String url, String destinationPath) {

        Log.d("sdncks","Done");
        pdfPagerAdapter = new PDFPagerAdapter(this, "tnc.pdf");
        remotePDFViewPager.setAdapter(pdfPagerAdapter);
        setContentView(remotePDFViewPager);
    }

    @Override
    public void onFailure(Exception e) {
        Log.d("sdncks","Fail");
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        pdfPagerAdapter.close();
    }
}
