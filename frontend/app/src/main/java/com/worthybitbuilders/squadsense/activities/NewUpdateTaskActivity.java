package com.worthybitbuilders.squadsense.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.worthybitbuilders.squadsense.adapters.NewUpdateTaskFileAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityNewUpdateTaskBinding;
import com.worthybitbuilders.squadsense.models.UpdateTask;
import com.worthybitbuilders.squadsense.services.ProjectService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewUpdateTaskActivity extends AppCompatActivity {
    private final int CAMERA_PERMISSION_REQUEST_CODE = 10;
    private ActivityNewUpdateTaskBinding binding;
    private final ProjectService projectService = RetrofitServices.getProjectService();
    private String projectId;
    private String boardId;
    private String cellId;
    private final List<Uri> fileUris = new ArrayList<>();
    private NewUpdateTaskFileAdapter newUpdateTaskFileAdapter;
    ActivityResultLauncher<Intent> getFileResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) return;
                    Uri fileUri = data.getData();
                    boolean isAdded = false;
                    for (int i = 0; i < fileUris.size(); i++) {
                        if (areUrisEqual(fileUris.get(i), fileUri)) {
                            isAdded = true;
                            break;
                        }
                    }

                    if (!isAdded) {
                        fileUris.add(fileUri);
                        newUpdateTaskFileAdapter.notifyItemInserted(fileUris.size() - 1);
                    } else {
                        Toast.makeText(this, "File is already selected", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> takePhotoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) return;
                    newUpdateTaskFileAdapter.notifyItemInserted(fileUris.size() - 1);
                } else {
                    fileUris.remove(fileUris.size() - 1);
                }
            }
    );

    ActivityResultLauncher<Intent> choosePhotoFromStorageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) return;
                    Uri fileUri = data.getData();
                    boolean isAdded = false;
                    for (int i = 0; i < fileUris.size(); i++) {
                        if (areUrisEqual(fileUris.get(i), fileUri)) {
                            isAdded = true;
                            break;
                        }
                    }

                    if (!isAdded) {
                        fileUris.add(fileUri);
                        newUpdateTaskFileAdapter.notifyItemInserted(fileUris.size() - 1);
                    } else {
                        Toast.makeText(this, "File is already selected", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewUpdateTaskBinding.inflate(getLayoutInflater());
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = getIntent();
        String columnTitle = intent.getStringExtra("columnTitle");
        String projectTitle = intent.getStringExtra("projectTitle");
        String boardTitle = intent.getStringExtra("boardTitle");
        this.projectId = intent.getStringExtra("projectId");
        this.boardId = intent.getStringExtra("boardId");
        this.cellId = intent.getStringExtra("cellId");
        binding.updateColumnTitle.setText(columnTitle);
        binding.additionalTitle.setText(String.format(Locale.US, "%s in %s", boardTitle, projectTitle));

        newUpdateTaskFileAdapter = new NewUpdateTaskFileAdapter(fileUris, this, position -> {
            fileUris.remove(position);
            newUpdateTaskFileAdapter.notifyItemRemoved(position);
            newUpdateTaskFileAdapter.notifyItemRangeChanged(position, fileUris.size());
        });

        binding.rvFiles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvFiles.setAdapter(newUpdateTaskFileAdapter);

        binding.btnAttachFile.setOnClickListener(view -> {
            Intent getFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getFileIntent.setType("*/*");
            getFileResultLauncher.launch(getFileIntent);
        });

        binding.btnTakePhoto.setOnClickListener(view -> {
            if (!checkPermissionAndAskForIt()) return;
            getPhotoFromCamera();
        });

        binding.btnGetPhoto.setOnClickListener(view -> choosePhotoFromStorage());

        binding.btnSendUpdate.setOnClickListener(view -> {
            String content = binding.etUpdateContent.getText().toString();
            uploadUpdateTask(fileUris, content);
        });

        binding.btnClose.setOnClickListener(view -> finish());
        setContentView(binding.getRoot());
    }

    private boolean checkPermissionAndAskForIt() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        } else return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhotoFromCamera();
            } else {
                Toast.makeText(this, "Permission not granted, operation failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getPhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // the "if" below not working on some phone
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        try {
            File photoFile = null;
            try {
                photoFile = createTempImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Unable to take picture, please try again", Toast.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.worthybitbuilders.squadsense.fileprovider",
                        photoFile);
                fileUris.add(photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePhotoResultLauncher.launch(takePictureIntent);
            }
        } catch (Exception e) {
//            Toast.makeText(this, "No usable camera, operation failed", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Something wrong with camera", Toast.LENGTH_LONG).show();
        }
    }

    private void choosePhotoFromStorage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        choosePhotoFromStorageResultLauncher.launch(Intent.createChooser(intent, "Select photo"));
    }

    private void uploadUpdateTask(List<Uri> selectedUris, String content) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (int i = 0; i < selectedUris.size(); i++) {
            try {
                Uri fileUri = selectedUris.get(i);
                String fileName = getFileName(fileUri);

                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                File file = createTempFile(fileUri);
                copyInputStreamToFile(inputStream, file);
                RequestBody requestFile = RequestBody.create(MediaType.parse(getMimeType(fileUri)), file);
                parts.add(MultipartBody.Part.createFormData("files", fileName, requestFile));
            } catch (IOException ignored) {}
        }

        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<Void> call = projectService.createNewUpdateTaskToRemote(
                userId,
                projectId,
                boardId,
                cellId,
                parts,
                new UpdateTask(userId, content)
        );

        Dialog loadingDialog = DialogUtils.GetLoadingDialog(this);
        loadingDialog.show();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    finish();
                } else Toast.makeText(NewUpdateTaskActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(NewUpdateTaskActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                loadingDialog.dismiss();
            }
        });
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private File createTempImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private File createTempFile(Uri fileUri) throws IOException {
        String fileName = getFileName(fileUri);
        String fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(fileUri));
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        return File.createTempFile(fileName, fileExtension, storageDir);
    }

    private void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024]; // 4KB buffer
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public boolean areUrisEqual(Uri firstUri, Uri secondUri) {
        ContentResolver contentResolver = getContentResolver();

        // Get the document IDs for the URIs
        String documentId1 = DocumentsContract.getDocumentId(firstUri);
        String documentId2 = DocumentsContract.getDocumentId(secondUri);

        // Retrieve the DocumentFile instances using the document IDs
        DocumentFile document1 = DocumentFile.fromSingleUri(this, firstUri);
        DocumentFile document2 = DocumentFile.fromSingleUri(this, secondUri);

        // Check if the documents are null or not
        if (document1 == null || document2 == null) {
            return false;
        }

        // Compare the document IDs
        boolean areDocumentIdsEqual = documentId1.equals(documentId2);

        // Compare the actual files using their content URIs
        boolean areFilesEqual = document1.getUri().equals(document2.getUri());

        // Return true if both the document IDs and files are equal
        return areDocumentIdsEqual && areFilesEqual;
    }
}