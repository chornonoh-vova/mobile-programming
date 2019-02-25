package lab2.photostar.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import lab2.photostar.R;
import lab2.photostar.utils.FileUtils;

public class RenameFileDialog extends DialogFragment {
    public static final String FILE_PATH_KEY = "file_path";

    public static RenameFileDialog newInstance(String filePath) {
        RenameFileDialog dialog = new RenameFileDialog();

        Bundle args = new Bundle();
        args.putString(FILE_PATH_KEY, filePath);

        dialog.setArguments(args);
        return dialog;
    }

    private String oldFilePath;
    private EditText editFileName;
    private Button confirm;
    private Button cancel;

    private EditListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rename_file, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        oldFilePath = getArguments().getString(FILE_PATH_KEY);

        editFileName = view.findViewById(R.id.edit_file_name);
        confirm = view.findViewById(R.id.confirm);
        cancel = view.findViewById(R.id.cancel);

        String fileName = oldFilePath.substring(oldFilePath.lastIndexOf('/') + 1, oldFilePath.indexOf('.'));

        editFileName.setText(fileName);

        getDialog().setTitle("Rename file");
        editFileName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editFileName.getText().toString().trim().isEmpty()) {
                    String newFileName = editFileName.getText().toString().trim();
                    String newFilePath =
                            oldFilePath.substring(0, oldFilePath.lastIndexOf('/') + 1) + newFileName +
                                    oldFilePath.substring(oldFilePath.indexOf('.'));
                    if (FileUtils.rename(oldFilePath, newFilePath)) {
                        listener.onRenamed(oldFilePath, newFilePath);
                        getDialog().dismiss();
                    }
                } else {
                    listener.onCancelled();
                    getDialog().dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    public void setListener(EditListener listener) {
        this.listener = listener;
    }


    public interface EditListener {
        void onRenamed(String oldFilePath, String newFilePath);
        void onCancelled();
    }
}
