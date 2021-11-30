package com.ryd.androidapt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.ryd.apt_annotation.BindAnim;
import com.ryd.apt_annotation.BindArray;
import com.ryd.apt_annotation.BindBitmap;
import com.ryd.apt_annotation.BindColor;
import com.ryd.apt_annotation.BindView;
import com.ryd.apt_annotation.info.InfoDesc;
import com.ryd.apt_api.ButterKnife;
import com.ryd.apt_api.InfoKnife;
import com.ryd.apt_runtime.Unbinder;
import com.ryd.apt_annotation.OnClick;
import com.ryd.apt_annotation.BindString;

@InfoDesc(desc = "我是MainActivity上面的注解")
public class MainActivity extends AppCompatActivity {

    @BindView(value = R.id.hello_text)
    TextView textView;
    @BindView(value = R.id.rotate_text)
    TextView rotateTextView;
    @BindView(value = R.id.bind_bitmap_image)
    ImageView bindBitmapImage;
    @BindBitmap(bitmapResId = R.mipmap.ic_launcher)
    Bitmap bindBitmap;

    @BindString(stringId = R.string.app_name)
    String appName;
    @BindAnim(animResId = R.anim.translate_anim)
    Animation rotateAnim;
    @BindColor(colorResId = R.color.purple_500)
    int color;
    @BindColor(colorResId = R.color.color_selector)
    ColorStateList colorStateList;

    @BindArray(arrayResId = R.array.cs_language)
    String[] cs_lang;
    @BindArray(arrayResId = R.array.cs_language)
    TypedArray ta;
    @BindArray(arrayResId = R.array.cs_language)
    CharSequence[] charSequences;
    @BindArray(arrayResId = R.array.int_array)
    int[] ints;



    private Unbinder mUnBinder;

    @InfoDesc(desc = "我是onCreate上面的注解")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUnBinder = ButterKnife.bind(this);
        textView.setText(appName);
        rotateTextView.startAnimation(rotateAnim);
        textView.setTextColor(colorStateList);
        rotateTextView.setTextColor(color);
        bindBitmapImage.setImageBitmap(bindBitmap);

//        String[] cs_lang = getResources().getStringArray(R.array.cs_language);
//        TypedArray ta = getResources().obtainTypedArray(R.array.cs_language);
//        CharSequence[] charSequences = getResources().getTextArray(R.array.cs_language);
//        int[] ints = getResources().getIntArray(R.array.int_array);
        for (int i = 0; i < cs_lang.length; i++) {
            Log.d("ruanyandong", "onCreate: 资源内容getStringArray-->"+cs_lang[i]+" obtainTypedArray资源id-->"+ta.getResourceId(i,-1)+" getTextArray-->"+charSequences[i]+" getIntArray-->"+ints[i]);
        }
        ta.recycle();

        InfoKnife.print();


    }

    @OnClick(value = {R.id.hello_text})
    public void onViewClick(){
        Toast.makeText(this,"11111",Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mUnBinder != null){
                mUnBinder.unbind();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}