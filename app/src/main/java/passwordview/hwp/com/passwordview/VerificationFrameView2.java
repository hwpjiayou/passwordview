package passwordview.hwp.com.passwordview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func6;

/**
 * Created by houwenpeng on 2017/3/28.
 * <p>
 * 通用验证码，密码输入框
 */

public class VerificationFrameView2 extends LinearLayout implements View.OnKeyListener {

    private Context context;
    private EditText editText1, editText2, editText3,
            editText4, editText5, editText6;

    private FrameLayout coverLinear;
    private List<EditText> editTextList = new ArrayList<>();

    private String verifyCode = "";
    private IVerifyListener listener;

    private InputMethodManager inputManager;

    public VerificationFrameView2(Context context) {
        super(context);
    }

    public VerificationFrameView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public VerificationFrameView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context) {
        this.context = context;
        inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_verifycation_frame2, this);
        coverLinear = (FrameLayout) view.findViewById(R.id.cover);
        editText1 = (EditText) view.findViewById(R.id.num_a);
        editText2 = (EditText) view.findViewById(R.id.num_b);
        editText3 = (EditText) view.findViewById(R.id.num_c);
        editText4 = (EditText) view.findViewById(R.id.num_d);
        editText5 = (EditText) view.findViewById(R.id.num_e);
        editText6 = (EditText) view.findViewById(R.id.num_f);
        editTextList.add(editText1);
        editTextList.add(editText2);
        editTextList.add(editText3);
        editTextList.add(editText4);
        editTextList.add(editText5);
        editTextList.add(editText6);
        setListener();
    }

    public void showKeybord(){
        coverLinear.performClick();
    }

    private void setListener() {
        try {
            coverLinear.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < editTextList.size(); i++) {
                        if (i == 0 && editTextList.get(i).getText().toString().length() == 0) {
                            editTextList.get(i).setFocusable(true);
                            editTextList.get(i).setFocusableInTouchMode(true);
                            editTextList.get(i).requestFocus();
                            showKeybrod(editTextList.get(i));
                            return;
                        }
                        //让其光标对准下一位
                        if (editTextList.get(i).getText().toString().length() > 0) {
                            if (i < (editTextList.size() - 1)) {
                                editTextList.get(i + 1).setFocusable(true);
                                editTextList.get(i + 1).requestFocus();
                                showKeybrod(editTextList.get(i + 1));
                            }
                        }
                    }
                }
            });

            List<CharSequence> charSequences = new ArrayList<>();
            Observable.combineLatest(
                    RxTextView.textChanges(editTextList.get(0)), RxTextView.textChanges(editTextList.get(1)),
                    RxTextView.textChanges(editTextList.get(2)), RxTextView.textChanges(editTextList.get(3)),
                    RxTextView.textChanges(editTextList.get(4)), RxTextView.textChanges(editTextList.get(5)),
                    new Func6<CharSequence, CharSequence, CharSequence, CharSequence, CharSequence, CharSequence, Boolean>() {
                        @Override
                        public Boolean call(CharSequence charSequence1, CharSequence charSequence2, CharSequence charSequence3,
                                            CharSequence charSequence4, CharSequence charSequence5, CharSequence charSequence6) {
                            charSequences.clear();
                            charSequences.add(charSequence1);
                            charSequences.add(charSequence2);
                            charSequences.add(charSequence3);
                            charSequences.add(charSequence4);
                            charSequences.add(charSequence5);
                            charSequences.add(charSequence6);

                            for (int i = 0; i < charSequences.size(); i++) {
                                //循环判断当前的光标位置
                                if (charSequences.get(i).length() > 0 && i < (editTextList.size() - 1)) {
                                    editTextList.get(i + 1).setFocusable(true);
                                    editTextList.get(i + 1).setFocusableInTouchMode(true);
                                    editTextList.get(i + 1).requestFocus();
                                }
                                //如果当前输入框内没有任何内容，光标显示在第一位上。
                                if (i == 0 && charSequences.get(i).length() == 0) {
                                    editTextList.get(i).setFocusable(true);
                                    editTextList.get(i).setFocusableInTouchMode(true);
                                    editTextList.get(i).requestFocus();
                                    showKeybrod(editTextList.get(i));
                                    return false;
                                }
                            }
                            verifyCode = String.valueOf(charSequence1) + String.valueOf(charSequence2) + String.valueOf(charSequence3)
                                    + String.valueOf(charSequence4) + String.valueOf(charSequence5) + String.valueOf(charSequence6);
                            return charSequence1.length() > 0 && charSequence2.length() > 0 && charSequence3.length() > 0
                                    && charSequence4.length() > 0 && charSequence5.length() > 0 && charSequence6.length() > 0;
                        }
                    }).subscribe(new Subscriber<Boolean>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onNext(Boolean aBoolean) {
                    if (aBoolean && listener != null) {
                        listener.autoVerifyLogin();
                    }
                }
            });

            for (int i = 0; i < editTextList.size(); i++) {
                editTextList.get(i).setTag(i);
                editTextList.get(i).setSelection(editTextList.get(i).getText().length());
                editTextList.get(i).setOnKeyListener(this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        try {
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL
                    && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                onDeleteDown((EditText) view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void onDeleteDown(EditText editText) {
        /**
         * 根据当前的控件的tag判断删除操作.
         */
        int tag = (int) editText.getTag();
        if (tag == editTextList.size() - 1 && editText.getText().toString().length() > 0) {
            return;
        }
        if (tag >= 1) {
            editTextList.get(tag - 1).setText("");
        }
    }

    /**
     * 自动填充
     *
     * @param num
     */
    public void autoNum(String num) {
        if (!TextUtils.isEmpty(num)) {
            String[] smsCode = new String[num.length()];
            for (int i = 0; i < num.length(); i++) {
                smsCode[i] = num.substring(i, i + 1);
            }
            for (int i = 0; i < editTextList.size(); i++) {
                editTextList.get(i).setText("");
            }
            for (int i = 0; i < smsCode.length; i++) {
                editTextList.get(i).setText(smsCode[i]);
                editTextList.get(i).setSelection(editTextList.get(i).getText().length());
            }
        }else{
            for (int i = 0; i < editTextList.size(); i++) {
                editTextList.get(i).setText("");
            }
        }
    }


    private void showKeybrod(EditText editText) {
        inputManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    private void hideKeybrod(EditText editText) {
        inputManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    public void setIVerifyListener(IVerifyListener listener) {
        this.listener = listener;
    }


    public abstract interface IVerifyListener {
        abstract void autoVerifyLogin();
    }

}
