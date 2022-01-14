package com.amir.todone.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.amir.todone.R;

public class AppDialog extends DialogFragment {

    public interface onTextResult {
        void textResult(String text);
    }
    public interface onCheckResult {
        void checkResult(boolean is_check);
    }

    private Context context;
    private onTextResult onTextResult;
    private onCheckResult onCheckResult;
    private View.OnClickListener btnOkListener;
    private View.OnClickListener btnCancelListener;
    private View.OnClickListener btnNaturalListener;

    private TextView txtTitle, txtMassage, txtInputHint, txtDialogHint;
    private EditText edtDialogInput;
    private Button btnOk, btnCancel, btnNatural;
    private CheckBox checkBox;

    boolean has_title, has_massage, has_dialogInput, has_inputHint, has_dialogHint, has_btnOk,
            has_btnCancel, has_btnNatural, has_checkBox, isChecked_checkBox;

    String title, massage, inputText, inputPlaceHolder, inputHint, dialogHint, text_btnOk, text_btnCancel, text_btnNatural, text_checkbox;

    public AppDialog(Context context) {
        this.context = context;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_MaterialComponents_BottomSheetDialog);
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_layout, null);

        txtTitle = view.findViewById(R.id.txtDCategoryTitle);
        txtTitle.setVisibility(View.GONE);
        txtMassage = view.findViewById(R.id.txtDialogMassage);
        txtMassage.setVisibility(View.GONE);
        edtDialogInput = view.findViewById(R.id.edtDialogInput);
        edtDialogInput.setVisibility(View.GONE);
        txtInputHint = view.findViewById(R.id.txtInputHint);
        txtInputHint.setVisibility(View.GONE);
        txtDialogHint = view.findViewById(R.id.txtDialogHint);
        txtDialogHint.setVisibility(View.GONE);
        btnOk = view.findViewById(R.id.btnOk);
        btnOk.setVisibility(View.GONE);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setVisibility(View.GONE);
        btnNatural = view.findViewById(R.id.btnNatural);
        btnNatural.setVisibility(View.GONE);
        checkBox = view.findViewById(R.id.checkBox);
        checkBox.setVisibility(View.GONE);

        if (has_title) {
            txtTitle.setVisibility(View.VISIBLE);
            txtTitle.setText(title);
        }
        if (has_massage) {
            txtMassage.setVisibility(View.VISIBLE);
            txtMassage.setText(massage);
        }
        if (has_dialogInput){
            edtDialogInput.setVisibility(View.VISIBLE);
            if (has_inputHint) {
                txtInputHint.setVisibility(View.VISIBLE);
                txtInputHint.setText(inputHint);
            }
            edtDialogInput.setText(inputText);
            edtDialogInput.setHint(inputPlaceHolder);
        }
        if (has_dialogHint){
            txtDialogHint.setVisibility(View.VISIBLE);
            txtDialogHint.setText(dialogHint);
        }
        if (has_checkBox){
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setText(text_checkbox);
            checkBox.setChecked(isChecked_checkBox);
        }
        if (has_btnOk){
            btnOk.setVisibility(View.VISIBLE);
            btnOk.setText(text_btnOk);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnOkListener.onClick(v);
                    if (has_dialogInput)
                        onTextResult.textResult(edtDialogInput.getText().toString().trim());
                    if (has_checkBox)
                        onCheckResult.checkResult(checkBox.isChecked());
                    dismiss();
                }
            });
        }
        if (has_btnCancel){
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setText(text_btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnCancelListener.onClick(v);
                    dismiss();
                }
            });
        }
        if (has_btnNatural){
            btnNatural.setVisibility(View.VISIBLE);
            btnNatural.setText(text_btnNatural);
            btnNatural.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnNaturalListener.onClick(v);
                    dismiss();
                }
            });
        }
        builder.setView(view);
        return builder.create();
    }

    public void setTitle(@NonNull String title) {
        has_title = true;
        this.title = title;
    }

    public void setMassage(@NonNull String massage) {
        has_massage = true;
        this.massage = massage;
    }

    public void setInput(@NonNull String text, @NonNull String placeHolder, @Nullable String hint, @Nullable onTextResult onTextResult) {
        has_dialogInput = true;
        this.inputText = text;
        this.inputPlaceHolder = placeHolder;
        if (hint != null) {
            has_inputHint = true;
            inputHint = hint;
        }
        if (onTextResult != null) {
            this.onTextResult = onTextResult;
        }else {
            this.onTextResult = text1 -> Log.e("TEXT", text1);
        }
    }

    public void setHint(@NonNull String hint){
        has_dialogHint = true;
        this.dialogHint = hint;
    }

    public void setCheckBox(@NonNull String text_checkbox , @NonNull boolean isChecked_checkBox , @Nullable onCheckResult onCheckResult){
        has_checkBox = true;
        this.text_checkbox = text_checkbox;
        this.isChecked_checkBox = isChecked_checkBox;
        if (onCheckResult != null) {
            this.onCheckResult = onCheckResult;
        }else {
            this.onCheckResult = result -> Log.e("checked", result+"");
        }
    }

    public void setOkButton(@NonNull String okButton , @Nullable View.OnClickListener btnOkListener){
        has_btnOk = true;
        text_btnOk=okButton;
        if (btnOkListener != null){
            this.btnOkListener = btnOkListener;
        }else {
            this.btnOkListener = v -> {};
        }
    }

    public void setCancelButton(@NonNull String cancelButton , @Nullable View.OnClickListener btnCancelListener){
        has_btnCancel = true;
        text_btnCancel=cancelButton;
        if (btnCancelListener != null){
            this.btnCancelListener = btnCancelListener;
        }else {
            this.btnCancelListener = v -> {};
        }
    }

    public void setNaturalButton(@NonNull String naturalButton , @Nullable View.OnClickListener btnNaturalListener){
        has_btnNatural = true;
        text_btnNatural=naturalButton;
        if (btnNaturalListener != null){
            this.btnNaturalListener = btnNaturalListener;
        }else {
            this.btnNaturalListener = v -> {};
        }
    }

}
