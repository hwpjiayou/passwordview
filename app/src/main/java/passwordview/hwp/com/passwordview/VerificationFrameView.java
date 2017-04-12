package passwordview.hwp.com.passwordview;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hwp on 2017/3/28.
 */

public class VerificationFrameView extends LinearLayout {

    private Context context;
    private EditText inputEditText;
    private TextView textView1,textView2,textView3,
                     textView4,textView5,textView6;
    private List<TextView> textViewList = new ArrayList<>();

    private StringBuffer stringBuffer = new StringBuffer();//存储字符
    private static final int MAX_INPUT_NUM = 6;
    private int count = MAX_INPUT_NUM;
    private boolean canAutoLogin = true;

    private String verifyCode="";
    private IVerifyListener listener;

    public VerificationFrameView(Context context) {
        super(context);
    }

    public VerificationFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public VerificationFrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context){
        this.context = context;
        View view =  ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_verifycation_frame,this);
        inputEditText = (EditText) view.findViewById(R.id.edit_account);
        textView1 = (TextView) view.findViewById(R.id.num_a);
        textView2 = (TextView) view.findViewById(R.id.num_b);
        textView3 = (TextView) view.findViewById(R.id.num_c);
        textView4 = (TextView) view.findViewById(R.id.num_d);
        textView5 = (TextView) view.findViewById(R.id.num_e);
        textView6 = (TextView) view.findViewById(R.id.num_f);
        textViewList.add(textView1);textViewList.add(textView2);textViewList.add(textView3);
        textViewList.add(textView4);textViewList.add(textView5);textViewList.add(textView6);
        setListener();
    }



    private void setListener(){
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("")) {
                    verifyCode = editable.toString();
                    if (stringBuffer.length()> (MAX_INPUT_NUM-1)){
                        inputEditText.setText("");
                        if (listener!=null && canAutoLogin){
                            listener.autoVerifyLogin();
                            canAutoLogin = false;
                        }
                        return;
                    }else {
                        canAutoLogin = true;
                        stringBuffer.append(verifyCode);
                        count = stringBuffer.length();
                        String[] smsCode = new String[stringBuffer.toString().length()];
                        for (int i=0;i<stringBuffer.toString().length();i++){
                            smsCode[i]= stringBuffer.toString().substring(i,i+1);
                        }
                        for (int i=0;i<smsCode.length;i++){
                            textViewList.get(i).setText(smsCode[i]);
                        }
                        inputEditText.setText("");
                    }
                }
            }
        });

        inputEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL
                        && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (onKeyDelete()) return true;
                    return true;
                }
                return false;
            }
        });
    }

    private boolean onKeyDelete() {
        if (count ==0){
            count = MAX_INPUT_NUM;
            return true;
        }
        if (stringBuffer.length()>0){
            //删除相应位置的字符
            stringBuffer.delete((count-1),count);
            count--;
            String[] smsCode = new String[stringBuffer.toString().length()];
            for (int i=0;i<stringBuffer.toString().length();i++){
                smsCode[i]= stringBuffer.toString().substring(i,i+1);
            }
            for (int i=0;i<textViewList.size();i++){
                textViewList.get(i).setText("");
            }
            for (int i=0;i<smsCode.length;i++){
                textViewList.get(i).setText(smsCode[i]);
            }
            canAutoLogin = true;
        }
        return false;
    }

    public void setAutoNum(String num){
        stringBuffer.delete(0,stringBuffer.length());
        stringBuffer.append(num);
        String[] smsCode = new String[stringBuffer.toString().length()];
        for (int i=0;i<stringBuffer.toString().length();i++){
            smsCode[i]= stringBuffer.toString().substring(i,i+1);
        }
        for (int i=0;i<textViewList.size();i++){
            textViewList.get(i).setText("");
        }
        for (int i=0;i<smsCode.length;i++){
            textViewList.get(i).setText(smsCode[i]);
        }
    }

    public void setIVerifyListener(IVerifyListener listener){
        this.listener = listener;
    }

    public abstract interface IVerifyListener{
        abstract void autoVerifyLogin();
    }

}
