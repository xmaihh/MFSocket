package tp.xmaihh.sample;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import tp.xmaihh.sample.utils.Uri2path;

public class AudioTrackActivity extends Activity implements View.OnClickListener {

    private TextView mTvChooseFile;
    private ValueAnimator animator;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_track);

        mTvChooseFile = findViewById(R.id.tv_choose_file);
        mTvChooseFile.setOnClickListener(this);

//        WindowManager manager = this.getWindowManager();
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        manager.getDefaultDisplay().getMetrics(outMetrics);
//        int width2 = outMetrics.widthPixels;
//        int height2 = outMetrics.heightPixels;
//
//        final int left = mTvChooseFile.getLeft();
//        final int top = mTvChooseFile.getTop();
//        animator = ValueAnimator.ofInt(0, 500);
//        animator.setDuration(3000);
//        animator.setInterpolator(new LinearInterpolator());
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                int current = (int) animator.getAnimatedValue();
//                Log.d("521", "onAnimationUpdate: " + current);
//                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mTvChooseFile.getLayoutParams();
//                layoutParams.leftMargin = left + current;
//                layoutParams.topMargin = top + current;
//                mTvChooseFile.setLayoutParams(layoutParams);
//            }
//        });
//        animator.start();
        // Request necessary permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(perms[1]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }
    }


    private static final String TAG1 = "FileChoose";

    // 调用系统文件管理器
    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*").addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Choose File"), CHOOSE_FILE_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int CHOOSE_FILE_CODE = 0;

    @Override
// 文件选择完之后，自动调用此函数
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_FILE_CODE) {
                Uri uri = data.getData();
                if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                    path = uri.getPath();
                    mTvChooseFile.setText(path);
                    Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//Android4.4以上
                    path = Uri2path.getPath(this, uri);
                    mTvChooseFile.setText(path);
                    Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                } else {//4.4以下下系统调用方法
                    path = Uri2path.getRealPathFromURI(this, uri);
                    mTvChooseFile.setText(path);
                    Toast.makeText(this, path + "222222", Toast.LENGTH_SHORT).show();
                }

            }
        } else {
            Log.e(TAG1, "onActivityResult() error, resultCode: " + resultCode);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_choose_file:
                chooseFile();
                break;
        }
    }
}
