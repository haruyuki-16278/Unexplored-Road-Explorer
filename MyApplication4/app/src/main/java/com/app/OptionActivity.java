package com.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.util.SharedPreferencesUtil;
import com.app.util.VariableUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class OptionActivity extends AppCompatActivity {

	private Context mContext;

	private EditText mURL;

	private Intent intent;
	private VariableUtil variableUtil;

	private int debugCount;
	private TableRow debugRow;

	private boolean isDemo;
	private boolean isDebug;

	private SharedPreferencesUtil util;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);

		debugCount = -1;
		debugRow = findViewById(R.id.secret_row);
		float fontSize = setTextSize();

		intent = getIntent();
		variableUtil = (VariableUtil) intent.getSerializableExtra(VariableUtil.SERIAL_NAME);

		final SharedPreferencesUtil preferences = new SharedPreferencesUtil(this);

		// トグルスイッチのインスタンス生成
		Switch toggleSwitch = findViewById(R.id.switch2);
		toggleSwitch.setChecked(variableUtil.getIsForeground());
		toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				variableUtil.setIsForeground(isChecked);
				if (debugCount > 0){
					debugCount++;
				}
				if(debugCount > 10){
					debugRow.setVisibility(View.VISIBLE);
					isDebug = true;
				}
			}
		});

		// デモンストレーション用
		Switch demoSwitch = findViewById(R.id.switch3);
		isDemo = preferences.getIsDemo();
		demoSwitch.setChecked(isDemo);
		demoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isDemo = isChecked;
			}
		});

		// buttonのスケールを決める
		float scale = getResources().getDisplayMetrics().density;
		int size;
		if(Build.VERSION.SDK_INT <= 24){
			size = (int)(50 * scale);
		}
		else{
			size = (int)(70 * scale);
		}
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);

		// 戻るボタンを押したら戻る
		ImageButton back = findViewById(R.id.button_back);
		back.setLayoutParams(params);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("isLocationServer", variableUtil.getIsLocationService());
				intent.putExtra("isDemo", isDemo);
				intent.putExtra("isDebug", isDebug);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		// LocationServiceを止めるボタン
		Button stopServiceButton = findViewById(R.id.stop_service);
		stopServiceButton.setTextSize(fontSize - ((fontSize - 20) / 2));
		stopServiceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// serviceを停止する
				variableUtil.setIsLocationService(false);
			}
		});

		// Applicationを終了させるボタン
		mContext = this;
		Button stopApplicationButton = findViewById(R.id.stop_application);
		stopApplicationButton.setTextSize(fontSize - ((fontSize - 20) / 2));
		stopApplicationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 確認ダイアログ
				new AlertDialog.Builder(mContext)
						.setTitle("アプリケーションを終了しますか?")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent();
								if(!variableUtil.getIsForeground()){
									intent.putExtra("isForeground", variableUtil.getIsForeground());
								}
								// 終了する
								intent.putExtra("isApplicationStop", true);
								setResult(RESULT_OK, intent);
								finish();
							}
						})
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								debugCount = 1;
							}
						})
						.create()
						.show();
			}
		});

		mURL = findViewById(R.id.edit_text);
		mURL.setText(preferences.getServerIP());
		mURL.setTextSize(fontSize);

		Button editReloadButton = findViewById(R.id.button_edit_reload);
		editReloadButton.setTextSize(fontSize - 5);
		editReloadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				preferences.setServerIP("192.168.11.16:5001");
				mURL.setText(preferences.getServerIP());
			}
		});

		Button editSetButton = findViewById(R.id.button_edit_set);
		editReloadButton.setTextSize(fontSize - 5);
		editSetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				preferences.setServerIP(mURL.getText().toString());
				mURL.setText(preferences.getServerIP());
			}
		});
	}

	private float setTextSize(){
		float titleFontSize;
		float bodyFontSize;
		if(Build.VERSION.SDK_INT <= 24){
			titleFontSize = 20;
			bodyFontSize = 15;
		}
		else {
			titleFontSize = 30;
			bodyFontSize = 25;
		}

		TextView title1 = findViewById(R.id.title1);
		TextView title2 = findViewById(R.id.title2);
		TextView title3 = findViewById(R.id.title3);

		title1.setTextSize(titleFontSize);
		title2.setTextSize(titleFontSize);
		title3.setTextSize(titleFontSize);

		TextView body1 = findViewById(R.id.body1);
		TextView body2 = findViewById(R.id.body2);

		body1.setTextSize(bodyFontSize);
		body2.setTextSize(bodyFontSize);

		return titleFontSize;
	}
}