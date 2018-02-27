package com.isens.sugarnote;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.isens.module.bloodglucosemonitor.BgmBootLoader;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitor;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitorCallBack;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeasureFragment extends Fragment implements View.OnClickListener {

    private BloodGlucoseMonitor _bloodGlucoseMonitor;

    private Activity ac;
    private View view;

    private FragmentInterActionListener listener;
    private SharedPreferences prefs_root, prefs_user;

    private ImageView iv_meaure_status;
    private LinearLayout LL_measure_result, LL_mealoption;
    private FrameLayout FL_measuring;

    private TextView tv_measure_info, tv_timer, result_txt, tv_measure_error, premeal_btn, postmeal_btn, nomeal_btn, tv_dialog, btn_dialog_ok, btn_dialog_cancel;
    private Button btn_navi_right, btn_navi_center, btn_navi_left;

    private ProgressBar _progress_bar, progressbar_result;
    private Intent pre_intent;

    private Dialog dialog_no_save, dialog_restart;
    private String meal_option = "식전";
    private String _progress_status = "";
    private volatile Thread _progress_thread;

    private int _progress_count;
    private int _glucose_value = 0;
    private boolean _isResultView = false, restartFlag = false;

    private Handler _handler;
    private DBHelper dbHelper;

    private String userAccount;
    private int _bgm_value = 0;
    private int _bgm_status = 0;
    private int cnt =0;
    private int ll_max_premeal, ll_min_premeal, ll_max_postmeal, ll_min_postmeal, ll_max_nomeal, ll_min_nomeal;
    private Handler handler = new Handler(); // Thread 에서 화면에 그리기 위해서 필요
    private final int premeal = 1, postmeal = 2, nomeal = 3;
    private boolean target_accept;

    public MeasureFragment() {
        // Required empty public constructor
    }

    private final BloodGlucoseMonitorCallBack _bgm_callBack = new BloodGlucoseMonitorCallBack() {
        @Override
        public void bgmcallBackMethod(String str, int status, int value) {

            _bgm_status = status;
            _bgm_value = value;

            ac.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setStatus(_bgm_status, _bgm_value);
                }
            });
        }

        @Override
        public void bgmBootLoadercallBackMethod(String str, int status, BgmBootLoader bootloader) {
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (FragmentInterActionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _bloodGlucoseMonitor = BloodGlucoseMonitor.getInstance();
        BloodGlucoseMonitor.setCallbackInterface(_bgm_callBack);
        _bloodGlucoseMonitor.enableBGM(_bloodGlucoseMonitor.BGM_INT_SWITCH);

        ac = getActivity();
        view = inflater.inflate(R.layout.fragment_measure, container, false);

        prefs_root = ac.getSharedPreferences("ROOT", 0);
        userAccount = prefs_root.getString("SIGNIN", "none");
        prefs_user = ac.getSharedPreferences(userAccount, 0);

        _progress_bar = (ProgressBar) view.findViewById(R.id.circle_progress);
        progressbar_result = (ProgressBar) view.findViewById(R.id.Progressbar_result);

        FL_measuring = (FrameLayout) view.findViewById(R.id.FL_measuring);
        LL_measure_result = (LinearLayout) view.findViewById(R.id.LL_measure_result);
        LL_mealoption = (LinearLayout) view.findViewById(R.id.LL_measure_mealoption);
        iv_meaure_status = (ImageView) view.findViewById(R.id.iv_measure_status);

        tv_measure_info = (TextView) view.findViewById(R.id.tv_measure_info);
        tv_measure_error = (TextView) view.findViewById(R.id.tv_measure_error);
        tv_timer = (TextView) view.findViewById(R.id.tv_timer);
        result_txt = (TextView) view.findViewById(R.id.result_text);

        premeal_btn = (TextView) view.findViewById(R.id.meas_premeal_btn);
        postmeal_btn = (TextView) view.findViewById(R.id.meas_postmeal_btn);
        nomeal_btn = (TextView) view.findViewById(R.id.meas_nomeal_btn);

        btn_navi_center = (Button) ac.findViewById(R.id.btn_navi_center);
        btn_navi_right = (Button) ac.findViewById(R.id.btn_navi_right);
        btn_navi_left = (Button) ac.findViewById(R.id.btn_navi_left);

        btn_navi_center.setBackgroundResource(R.drawable.state_btn_navi_home);
        btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_restart);
        btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_save);

        btn_navi_center.setOnClickListener(this);
        btn_navi_right.setOnClickListener(this);
        btn_navi_left.setOnClickListener(this);
        premeal_btn.setOnClickListener(this);
        postmeal_btn.setOnClickListener(this);
        nomeal_btn.setOnClickListener(this);

        setView("MEASURING");
        mealOptionReset();
        premeal_btn.setTextColor(Color.BLUE);

        if (_handler == null)
            if (dbHelper == null) dbHelper = new DBHelper(ac, "GLUCOSEDATA.db", null, 1);

        if (_handler == null)
            _handler = new Handler(Looper.getMainLooper());

        if (MyApplication.isIsStrip() == true) {
            MyApplication.setIsStrip(false);
            _bgm_status = BloodGlucoseMonitor.BGM_STATUS_INSERT_STRIP;
            setStatus(_bgm_status, _bgm_value);
        }

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.meas_premeal_btn:
                meal_option = "식전";
                mealOptionReset();
                premeal_btn.setTextColor(Color.BLUE);
                set_result_progress(premeal);
                break;

            case R.id.meas_postmeal_btn:
                meal_option = "식후";
                mealOptionReset();
                postmeal_btn.setTextColor(Color.BLUE);
                set_result_progress(postmeal);
                break;

            case R.id.meas_nomeal_btn:
                meal_option = "공복";
                mealOptionReset();
                nomeal_btn.setTextColor(Color.BLUE);
                set_result_progress(nomeal);
                break;

            case R.id.btn_navi_center:
                if (_isResultView == true) {
                    dialog_no_save = new Dialog(ac);
                    dialog_no_save.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog_no_save.setContentView(R.layout.dialog_default);

                    tv_dialog = (TextView) dialog_no_save.findViewById(R.id.tv_dialog);
                    btn_dialog_ok = (TextView) dialog_no_save.findViewById(R.id.btn_dialog_ok);
                    btn_dialog_cancel = (TextView) dialog_no_save.findViewById(R.id.btn_dialog_cancel);

                    btn_dialog_cancel.setText("아니요");
                    tv_dialog.setText("지금 나가시면 데이터가\n저장이 되지 않습니다.\n그래도 나가시겠습니까?");

                    btn_dialog_ok.setOnClickListener(this);
                    btn_dialog_cancel.setOnClickListener(this);

                    dialog_no_save.show();
                } else
                    listener.setFrag("HOME");
                break;

            case R.id.btn_navi_right:
                String mdate = tv_measure_info.getText().toString();
                int gluco = _glucose_value;

                dbHelper.insert(mdate, gluco, meal_option);
                _isResultView = false;

                Toast.makeText(ac, "저장완료", Toast.LENGTH_SHORT).show();
                listener.setFrag("HOME");

                break;

            case R.id.btn_navi_left:
                restartFlag = true;

                dialog_restart = new Dialog(ac);
                dialog_restart.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_restart.setContentView(R.layout.dialog_default);

                tv_dialog = (TextView) dialog_restart.findViewById(R.id.tv_dialog);
                btn_dialog_ok = (TextView) dialog_restart.findViewById(R.id.btn_dialog_ok);
                btn_dialog_cancel = (TextView) dialog_restart.findViewById(R.id.btn_dialog_cancel);

                btn_dialog_cancel.setText("아니요");
                tv_dialog.setText("측정된 데이터를 저장하지 않고\n재측정하시겠습니까?");

                btn_dialog_ok.setOnClickListener(this);
                btn_dialog_cancel.setOnClickListener(this);

                dialog_restart.show();
                break;

            case R.id.btn_dialog_ok:
                if (restartFlag == true) {
                    restartFlag = false;
                    dialog_restart.dismiss();
                    setView("MEASURING");
                    tv_measure_info.setText("검사지를 삽입해주세요.");
                    mealOptionReset();
                    premeal_btn.setTextColor(Color.BLUE);
                    _isResultView = false;
                } else {
                    _isResultView = false;
                    dialog_no_save.dismiss();
                    listener.setFrag("HOME");
                }
                break;

            case R.id.btn_dialog_cancel:
                if (restartFlag == true)
                    dialog_restart.dismiss();
                else
                    dialog_no_save.dismiss();
                break;

            default:
                break;
        }
    }

    public synchronized void startProgressBarThread() {
        stopProgressBarThread();

        if (_progress_thread == null) {
            _progress_thread = new Thread(null, _background_thread, "start");
            _progress_thread.start();
        }
    }

    public synchronized void stopProgressBarThread() {
        if (_progress_thread != null) {
            Thread tempThread = _progress_thread;
            _progress_thread = null;
            tempThread.interrupt();
        }
    }

    private Runnable _background_thread = new Runnable() {
        @Override
        public void run() {
            if (Thread.currentThread() == _progress_thread) {
                _progress_count = 5;
                final int total = 100;
                while (_progress_count < total) {
                    try {
                        _progress_handler.sendMessage(_progress_handler.obtainMessage());
                        Thread.sleep(1100);
                    } catch (final InterruptedException e) {
                        return;
                    } catch (final Exception e) {
                        return;
                    }
                }
            }
        }

        Handler _progress_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                _progress_bar.setProgress(_progress_count);
                if (_progress_status == "")
                    tv_timer.setText(Integer.toString(_progress_count % 6));
                else
                    tv_timer.setText(_progress_status);

                _progress_count = _progress_count + 23;

                if (_progress_count > 100)
                    stopProgressBarThread();
            }

        };
    };

    void setView(String str) {
        switch (str) {
            case "MEASURED":
                iv_meaure_status.setVisibility(View.INVISIBLE);
                LL_measure_result.setVisibility(View.VISIBLE);
                LL_mealoption.setVisibility(View.VISIBLE);
                FL_measuring.setVisibility(View.INVISIBLE);
                btn_navi_right.setEnabled(true);
                btn_navi_left.setEnabled(true);
                break;

            case "MEASURING":
                iv_meaure_status.setVisibility(View.VISIBLE);
                LL_measure_result.setVisibility(View.INVISIBLE);
                LL_mealoption.setVisibility(View.INVISIBLE);
                FL_measuring.setVisibility(View.VISIBLE);
                btn_navi_right.setEnabled(false);
                btn_navi_left.setEnabled(false);
                break;
        }
    }

    void mealOptionReset() {
        premeal_btn.setTextColor(Color.GRAY);
        postmeal_btn.setTextColor(Color.GRAY);
        nomeal_btn.setTextColor(Color.GRAY);
    }

    public void setStatus(int status, int value) {

        if (_isResultView)
            return;

        switch (status) {
            case BloodGlucoseMonitor.BGM_STATUS_INSERT_STRIP:
                tv_measure_error.setText("");
                tv_measure_info.setText("스트립이 삽입되었습니다.");
                _progress_bar.setVisibility(View.VISIBLE);
                Log.v("jj", "스트립삽입");
                _glucose_value = 0;
                _progress_status = "wait";
                break;

            case BloodGlucoseMonitor.BGM_STATUS_OUT_STRIP:
                setView("MEASURING");
                _progress_bar.setVisibility(View.INVISIBLE);
                tv_timer.setVisibility(View.INVISIBLE);
                iv_meaure_status.setImageResource(R.drawable.insert);
                tv_measure_info.setText("스트립이 빠졌습니다.");
                tv_measure_error.setText("error code : " + Integer.toString(value));
                Log.v("jj", "스트립아웃");
                break;

            case BloodGlucoseMonitor.BGM_STATUS_DROP_BLOOD:
                iv_meaure_status.setImageResource(R.drawable.measure);
                _progress_bar.setVisibility(View.INVISIBLE);
                tv_measure_error.setText("");
                tv_measure_info.setText("혈액을 주입해주세요.");
                Log.v("jj", "혈액 주입 요청");
                break;

            case BloodGlucoseMonitor.BGM_STATUS_PROCESS_START:
                _progress_status = "";
                _progress_bar.setVisibility(View.VISIBLE);
                tv_timer.setVisibility(View.VISIBLE);
                Log.v("jj", "스테이터스 프로세스 시작");
                startProgressBarThread();
                break;

            case BloodGlucoseMonitor.BGM_STATUS_RESULT_TEMPERATURE:
                break;

            case BloodGlucoseMonitor.BGM_STATUS_RESULT_GLUCOSE:
                _isResultView = true;
                _progress_bar.setVisibility(View.INVISIBLE);
                tv_timer.setVisibility(View.INVISIBLE);
                setView("MEASURED");
                premeal_btn.setSelected(true);
                postmeal_btn.setSelected(false);
                nomeal_btn.setSelected(false);

                // 현재 시간 구하기
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                // 출력될 포맷 설정
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/ MM/ dd, HH:mm:ss");
                Log.v("jj", simpleDateFormat.format(date));
                tv_measure_info.setText(simpleDateFormat.format(date));

                result_txt.setText(Integer.toString(value));
                Log.v("jj", "측정완료" + Integer.toString(value) + "mg/dL");

                _glucose_value = value;
                meal_option = "식전";

                ll_max_premeal = prefs_user.getInt("PREHIGH",100);
                ll_min_premeal = prefs_user.getInt("PRELOW",50);
                ll_max_postmeal = prefs_user.getInt("POSTHIGH",150);
                ll_min_postmeal = prefs_user.getInt("POSTLOW",100);
                ll_max_nomeal = prefs_user.getInt("NOHIGH",125);
                ll_min_nomeal = prefs_user.getInt("NOLOW",75);

                set_result_progress(premeal);


                break;

            case BloodGlucoseMonitor.BGM_STATUS_RESULT_CONTROLSOLUTION:
            case BloodGlucoseMonitor.BGM_STATUS_RESULT_CURRENT:
            case BloodGlucoseMonitor.BGM_STATUS_RESULT_KETONE:
            case BloodGlucoseMonitor.BGM_STATUS_RESULT_KETONE_CS:
            case BloodGlucoseMonitor.BGM_STATUS_ERROR:
                setView("MEASURING");
                _progress_bar.setVisibility(View.INVISIBLE);
                tv_timer.setVisibility(View.INVISIBLE);
                tv_measure_info.setText("검사지를 다시 삽입해주세요.");
                tv_measure_error.setText("error code : " + Integer.toString(value));
                Log.v("jj", "error" + Integer.toString(value) + "strip error");
                break;

            case BloodGlucoseMonitor.BGM_STATUS_PARSE_ERROR:
                break;

        }
    }

    private void set_result_progress(int meal){

        progressbar_result.setSecondaryProgress(0);
        progressbar_result.setProgress(0);
        progressbar_result.setVisibility(View.VISIBLE);

        switch (meal) {

            case premeal:
                if(_glucose_value >= ll_min_premeal && _glucose_value <= ll_max_premeal)
                    target_accept = true;
                else
                    target_accept = false;

                run_progress();
                break;

            case postmeal:
                if(_glucose_value >= ll_min_postmeal && _glucose_value <= ll_max_postmeal)
                    target_accept = true;
                else
                    target_accept = false;

                run_progress();
                break;

            case nomeal:
                if(_glucose_value >= ll_min_nomeal && _glucose_value <= ll_max_nomeal)
                    target_accept = true;
                else
                    target_accept = false;

                run_progress();
                break;
        }

    }

    private void run_progress(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() { // Thread 로 작업할 내용을 구현
                cnt = 0;
                while(cnt <= 100) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() { // 화면에 변경하는 작업을 구현
                            if (target_accept == true) {
                                progressbar_result.incrementProgressBy(2);
                            }
                            else{
                                progressbar_result.incrementSecondaryProgressBy(2);
                            }
                            cnt = cnt + 1;
                        }
                    });
                    try {
                        Thread.sleep(8); // 시간지연
                    } catch (InterruptedException e) {    }
                } // end of while
            }
        });

        t.start(); // 쓰레드 시작
    }
}
