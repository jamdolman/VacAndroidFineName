package com.example.hoon.firebaseloginregister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Hoon on 2017-08-04.
 */

public class Loading extends Activity {
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(2500);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this , LoginActivity.class));
        finish();
    }
}
