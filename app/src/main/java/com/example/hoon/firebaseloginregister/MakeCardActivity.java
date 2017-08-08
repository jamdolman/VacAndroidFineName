package com.example.hoon.firebaseloginregister;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MakeCardActivity extends AppCompatActivity {


    final static int SELECT_IMAGE =1;
    private static final int CAMERA_CAPTURE = 0;

    ListView list;
    String[] titles = {
            "이름",
            "전화번호",
            "E-mail",
            "회사명",
            "주소"
    };

    Integer[] images = {
            R.drawable.trash,
            R.drawable.donut,
            R.drawable.trash,
            R.drawable.trash,
            R.drawable.trash
    };

    ArrayList<MyDessert> arDessert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_card);

        setTitle("내 명함");

        list = (ListView) findViewById(R.id.list);
        arDessert = new ArrayList<MyDessert>();
        MyDessert mydessert;
        for (int i = 0; i < 5; i++) {
            mydessert = new MyDessert(images[i], titles[i], images[i]);
            arDessert.add(mydessert);
        }
        final MyDessertAdapter adapter = new MyDessertAdapter(this, R.layout.listitem, arDessert);
        list.setAdapter(adapter);
        Button btn = (Button) findViewById(R.id.plus);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDessert mydessert;
                mydessert = new MyDessert(images[0], titles[0], images[0]);
                arDessert.add(mydessert);
                adapter.notifyDataSetInvalidated();
            }
        });

    }

    public void onClick(View button){
        PopupMenu popup = new PopupMenu(this, button);
        popup.getMenuInflater().inflate(R.menu.popup_makecard,popup.getMenu());
        popup.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("캡쳐")) {
                            Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                            i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("/sdcard/image.jpg")));

                            startActivityForResult(i,CAMERA_CAPTURE);
                        }
                        else if(item.getTitle().equals("사진 가져오기")){
                            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            Intent intent = new Intent(Intent.ACTION_PICK, uri);
                            startActivityForResult(intent,SELECT_IMAGE );
                        }
                        return false;
                    }
                }
        );
        popup.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        Bitmap bitmap = null;
        if(resultCode == RESULT_OK && requestCode == SELECT_IMAGE){
            Uri image = intent.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),image);

            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            cropImage(image);
            imageView.setImageBitmap(bitmap);
        }else if(resultCode ==RESULT_OK && requestCode == CAMERA_CAPTURE){
            File file = new File("/sdcard/image.jpg");
            Uri imageUri = Uri.fromFile(file);
            try{
                String imagepath = imageUri.getPath();
                bitmap = BitmapFactory.decodeFile(imagepath);
                // bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.fromFile(file));
                ExifInterface exif = new ExifInterface(imagepath);
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = exifOrientationToDegrees(exifOrientation);
                bitmap =rotate(bitmap,exifDegree);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            ImageView imageView = (ImageView) findViewById(R.id.imageView);


            imageView.setImageBitmap(bitmap);

        }
        else
            return;

    }
    public void cropImage(Uri uri){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(uri,"image/*");
        cropIntent.putExtra("scale",true);
        startActivityForResult(cropIntent, SELECT_IMAGE);
    }
    public int exifOrientationToDegrees(int exifOrientation){
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90){
            return 90;
        }else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
    }
    public Bitmap rotate(Bitmap bitmap, int degress){
        if(degress != 0 && bitmap != null){
            Matrix m =new Matrix();
            m.setRotate(degress,(float)bitmap.getWidth()/2,(float)bitmap.getHeight()/2);
            try{
                Bitmap converted = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
                if(bitmap != converted){
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch (OutOfMemoryError ex){
                ex.printStackTrace();
            }
        }
        return bitmap;
    }



}


class MyDessert {
    int image_first;
    int image_second;
    String title;

    MyDessert(int Licon, String Text, int Ricon) {
        title = Text;
        image_first = Licon;
        image_second = Ricon;
    }

}

class MyDessertAdapter extends BaseAdapter {
    Context con;
    LayoutInflater inflater;
    ArrayList<MyDessert> arD;
    int layout;
    MakeCardActivity ad = new MakeCardActivity();
    private SparseBooleanArray mSelectedItemsIds;

    public MyDessertAdapter(Context context, int alayout, ArrayList<MyDessert> aarD) {
        con = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arD = aarD;
        layout = alayout;
    }

    @Override
    public int getCount() {
        return arD.size();
    }

    @Override
    public Object getItem(int position) {
        return arD.get(position).title;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }
        convertView = inflater.inflate(R.layout.listitem, parent, false);
        ImageView image_left = (ImageView) convertView.findViewById(R.id.image_first);
        ImageView image_right = (ImageView) convertView.findViewById(R.id.image_second);
        TextView text = (TextView) convertView.findViewById(R.id.title);
        EditText edit = (EditText) convertView.findViewById(R.id.Edit);
        String arg = edit.getText().toString();

        text.setText(arD.get(position).title);
        image_left.setImageResource(arD.get(position).image_first);
        image_right.setImageResource(arD.get(position).image_second);
        edit.setText(arg);
        image_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove(position);
            }
        });

        image_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeIntent();
            }
        });



        return convertView;

    }

    public void makeIntent(){
        Intent it = new Intent(con, IconListActivity.class);
        con.startActivity(it);
    }

    public void remove(int object) {
        arD.remove(object);
        notifyDataSetChanged();
    }

}
