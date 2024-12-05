package com.worthybitbuilders.squadsense.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.NewUpdateTaskFileAdapter;
import com.worthybitbuilders.squadsense.adapters.UpdateTaskCommentAdapter;
import com.worthybitbuilders.squadsense.adapters.UpdateTaskFileAdapter;
import com.worthybitbuilders.squadsense.adapters.UpdateTaskImageAdapter;
import com.worthybitbuilders.squadsense.adapters.UpdateTaskVideoAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityUpdateTaskCommentBinding;
import com.worthybitbuilders.squadsense.factory.UpdateTaskCommentViewModelFactory;
import com.worthybitbuilders.squadsense.models.UpdateTask;
import com.worthybitbuilders.squadsense.models.UpdateTaskAndCommentModel;
import com.worthybitbuilders.squadsense.utils.CustomUtils;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.UpdateTaskCommentViewModel;

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

public class UpdateTaskCommentActivity extends AppCompatActivity {
    private final int CAMERA_PERMISSION_REQUEST_CODE = 10;
    private final List<Uri> fileUris = new ArrayList<>();
    private NewUpdateTaskFileAdapter newCommentFileAdapter;
    private UpdateTaskCommentAdapter commentAdapter;
    private UpdateTaskCommentViewModel viewModel;
    private ActivityUpdateTaskCommentBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateTaskCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        changeBottomButtonsOnKeyboardShowing();

        Intent intent = getIntent();
        String projectId = intent.getStringExtra("projectId");
        String boardId = intent.getStringExtra("boardId");
        String cellId = intent.getStringExtra("cellId");
        String updateTaskId = intent.getStringExtra("updateTaskId");
        boolean isReadOnly = intent.getBooleanExtra("isReadOnly", false);
        if (projectId == null || boardId == null || cellId == null || updateTaskId == null) {
            ToastUtils.showToastError(this, "Something wrong happens, please try again", Toast.LENGTH_SHORT);
            finish();
        }
        UpdateTaskCommentViewModelFactory factory = new UpdateTaskCommentViewModelFactory(projectId, boardId, cellId, updateTaskId);
        viewModel = new ViewModelProvider(this, factory).get(UpdateTaskCommentViewModel.class);

        newCommentFileAdapter = new NewUpdateTaskFileAdapter(fileUris, this, position -> {
            fileUris.remove(position);
            newCommentFileAdapter.notifyItemRemoved(position);
            newCommentFileAdapter.notifyItemRangeChanged(position, fileUris.size());
            checkAndChangeFilesRecyclerViewVisible();
        });
        binding.rvCommentFiles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvCommentFiles.setAdapter(newCommentFileAdapter);

        binding.rvComments.setLayoutManager(new LinearLayoutManager(this));

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
        binding.btnSendComment.setOnClickListener(view -> uploadUpdateTask());

        Dialog loadingDialog = DialogUtils.GetLoadingDialog(this);
        loadingDialog.show();
        viewModel.getUpdateTaskAndComment(new UpdateTaskCommentViewModel.ApiCallHandler() {
            @Override
            public void onSuccess() {
                commentAdapter = new UpdateTaskCommentAdapter(UpdateTaskCommentActivity.this, isReadOnly, viewModel);
                binding.rvComments.setAdapter(commentAdapter);
                binding.tvAuthorName.setText(viewModel.getUpdateTask().getAuthorName());
                binding.tvTimestamp.setText(CustomUtils.mongooseDateToFormattedString(viewModel.getUpdateTask().getCreatedAt()));
                binding.tvTaskContent.setText(viewModel.getUpdateTask().getContent());
                Glide.with(UpdateTaskCommentActivity.this).load(viewModel.getUpdateTask().getAuthorImagePath()).placeholder(R.drawable.ic_user).into(binding.ivAuthorAvatar);

                setUpButtonLike();
                setUpFileRecyclerView();
                setUpVideoRecyclerView();
                setUpImageRecyclerView();

                if(isReadOnly) {
                    binding.etCommentContent.setVisibility(View.GONE);
                    binding.btnLike.setOnClickListener(view -> {
                        ToastUtils.showToastError(UpdateTaskCommentActivity.this, "Feature turned off due to task completion", Toast.LENGTH_SHORT);
                    });
                }
                else {
                    setUpButtonLike();
                    binding.etCommentContent.setVisibility(View.VISIBLE);
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(UpdateTaskCommentActivity.this, message, Toast.LENGTH_SHORT);
                loadingDialog.dismiss();
                finish();
            }
        });

        binding.btnBack.setOnClickListener(view -> finish());
    }

    private void uploadUpdateTask() {
        String commentContent = binding.etCommentContent.getText().toString();
        if (fileUris.size() == 0 && commentContent.isEmpty()) {
            ToastUtils.showToastError(UpdateTaskCommentActivity.this, "Comment must have something", Toast.LENGTH_SHORT);
            return;
        }

        List<MultipartBody.Part> parts = new ArrayList<>();
        for (int i = 0; i < fileUris.size(); i++) {
            try {
                Uri fileUri = fileUris.get(i);
                String fileName = getFileName(fileUri);

                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                File file = createTempFile(fileUri);
                copyInputStreamToFile(inputStream, file);
                RequestBody requestFile = RequestBody.create(MediaType.parse(getMimeType(fileUri)), file);
                parts.add(MultipartBody.Part.createFormData("files", fileName, requestFile));
            } catch (IOException ignored) {}
        }

        Dialog loadingDialog = DialogUtils.GetLoadingDialog(this);
        loadingDialog.show();
        viewModel.uploadNewComment(parts, new UpdateTaskAndCommentModel.UpdateTaskComment(commentContent), new UpdateTaskCommentViewModel.ApiCallHandler() {
            @Override
            public void onSuccess() {
                commentAdapter.notifyItemInserted(viewModel.getComments().size() - 1);
                binding.etCommentContent.setText("");
                fileUris.clear();
                newCommentFileAdapter.notifyItemRemoved(0);
                newCommentFileAdapter.notifyItemRangeChanged(0, 0);
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(UpdateTaskCommentActivity.this, message, Toast.LENGTH_LONG);
                loadingDialog.dismiss();
            }
        });
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
                ToastUtils.showToastError(this, "Permission not granted, operation failed", Toast.LENGTH_LONG);
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
                ToastUtils.showToastError(this, "Unable to take picture, please try again", Toast.LENGTH_LONG);
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
//            ToastUtils.showToastError(this, "No usable camera, operation failed", Toast.LENGTH_LONG);
            ToastUtils.showToastError(this, "Something wrong with camera", Toast.LENGTH_LONG);
        }
    }

    private void choosePhotoFromStorage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        choosePhotoFromStorageResultLauncher.launch(Intent.createChooser(intent, "Select photo"));
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

    ActivityResultLauncher<Intent> takePhotoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) return;
                    newCommentFileAdapter.notifyItemInserted(fileUris.size() - 1);
                } else {
                    fileUris.remove(fileUris.size() - 1);
                }
                checkAndChangeFilesRecyclerViewVisible();
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
                        newCommentFileAdapter.notifyItemInserted(fileUris.size() - 1);
                    } else {
                        ToastUtils.showToastError(this, "File is already selected", Toast.LENGTH_SHORT);
                    }
                }
                checkAndChangeFilesRecyclerViewVisible();
            }
    );

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
                        newCommentFileAdapter.notifyItemInserted(fileUris.size() - 1);
                    } else {
                        ToastUtils.showToastError(this, "File is already selected", Toast.LENGTH_SHORT);
                    }
                }
                checkAndChangeFilesRecyclerViewVisible();
            }
    );

    private void checkAndChangeFilesRecyclerViewVisible() {
        if (fileUris.size() == 0) {
            binding.rvCommentFiles.setVisibility(View.GONE);
        } else binding.rvCommentFiles.setVisibility(View.VISIBLE);
    }

    private void setUpButtonLike() {
        UpdateTask task = viewModel.getUpdateTask();
        binding.btnLike.setOnClickListener(view -> {
            viewModel.toggleLikeUpdateTask().enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        task.setLiked(!task.isLiked());
                        if (task.isLiked()) task.setLikeCount(task.getLikeCount() + 1);
                        else task.setLikeCount(task.getLikeCount() - 1);
                        setLikeButtonText(task);
                    } else {
                        ToastUtils.showToastError(UpdateTaskCommentActivity.this, response.message(), Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    ToastUtils.showToastError(UpdateTaskCommentActivity.this, "Something went wrong", Toast.LENGTH_SHORT);
                }
            });
        });

        setLikeButtonText(task);
    }

    private void setLikeButtonText(UpdateTask task) {
        if (task.isLiked()) {
            int color = Color.parseColor("#0073ea");
            binding.btnLike.setIconTint(ColorStateList.valueOf(color));

            if (task.getLikeCount() == 1) binding.btnLike.setText("Liked");
            else binding.btnLike.setText(String.format(Locale.US, "You, %d others liked", task.getLikeCount() - 1));
        } else {
            int color = ContextCompat.getColor(UpdateTaskCommentActivity.this, R.color.primary_icon_color);
            binding.btnLike.setText(String.format(Locale.US, "%d Like", task.getLikeCount()));
            binding.btnLike.setIconTint(ColorStateList.valueOf(color));
        }
    }

    private void setUpImageRecyclerView() {
        UpdateTask task = viewModel.getUpdateTask();
        List<UpdateTaskImageAdapter.TaskImageFile> imageFiles = new ArrayList<>();
        List<UpdateTask.UpdateTaskFile> allFiles = task.getFiles();
        for (int i = 0; i < allFiles.size(); i++) {
            UpdateTask.UpdateTaskFile file = allFiles.get(i);
            if (Objects.equals(file.fileType, "Image"))
                imageFiles.add(new UpdateTaskImageAdapter.TaskImageFile(file.location, file.name));
        }

        if (imageFiles.size() > 0) {
            UpdateTaskImageAdapter adapter = new UpdateTaskImageAdapter(UpdateTaskCommentActivity.this, imageFiles, position -> {
                Intent showImagesIntent = new Intent(UpdateTaskCommentActivity.this, ShowImagesActivity.class);

                ArrayList<String> imagePathsArrayList = new ArrayList<>();
                ArrayList<String> imageNamesArrayList = new ArrayList<>();
                for (int i = 0; i < imageFiles.size(); i++) {
                    imagePathsArrayList.add(imageFiles.get(i).location);
                    imageNamesArrayList.add(imageFiles.get(i).name);
                }

                showImagesIntent.putStringArrayListExtra("imagePaths", imagePathsArrayList);
                showImagesIntent.putStringArrayListExtra("imageNames", imageNamesArrayList);
                UpdateTaskCommentActivity.this.startActivity(showImagesIntent);
            });
            binding.rvImageFiles.setVisibility(View.VISIBLE);
            binding.rvImageFiles.setLayoutManager(new LinearLayoutManager(UpdateTaskCommentActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.rvImageFiles.setAdapter(adapter);
        }
    }

    private void setUpFileRecyclerView() {
        UpdateTask task = viewModel.getUpdateTask();
        List<UpdateTaskFileAdapter.TaskFile> files = new ArrayList<>();
        List<UpdateTask.UpdateTaskFile> allFiles = task.getFiles();
        for (int i = 0; i < allFiles.size(); i++) {
            UpdateTask.UpdateTaskFile file = allFiles.get(i);
            if (Objects.equals(file.fileType, "Document"))
                files.add(new UpdateTaskFileAdapter.TaskFile(file.location, file.name));
        }

        if (files.size() > 0) {
            UpdateTaskFileAdapter adapter = new UpdateTaskFileAdapter(files, position -> {
                UpdateTaskFileAdapter.TaskFile file = files.get(position);
                Uri fileUri = Uri.parse(file.location);
                DownloadManager downloadManager = (DownloadManager) UpdateTaskCommentActivity.this.getSystemService(UpdateTaskCommentActivity.this.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(fileUri);
                request.setTitle(file.name);
                request.setDestinationInExternalFilesDir(UpdateTaskCommentActivity.this, Environment.DIRECTORY_DOWNLOADS, file.name);
                ToastUtils.showToastSuccess(UpdateTaskCommentActivity.this, "Started download", Toast.LENGTH_LONG);
                downloadManager.enqueue(request);
            });

            binding.rvFiles.setVisibility(View.VISIBLE);
            binding.rvFiles.setLayoutManager(new LinearLayoutManager(UpdateTaskCommentActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.rvFiles.setAdapter(adapter);
        }
    }

    private void setUpVideoRecyclerView() {
        UpdateTask task = viewModel.getUpdateTask();
        List<UpdateTaskVideoAdapter.TaskVideoFile> videoFiles = new ArrayList<>();
        List<UpdateTask.UpdateTaskFile> allFiles = task.getFiles();
        for (int i = 0; i < allFiles.size(); i++) {
            UpdateTask.UpdateTaskFile file = allFiles.get(i);
            if (Objects.equals(file.fileType, "Video"))
                videoFiles.add(new UpdateTaskVideoAdapter.TaskVideoFile(file.location, file.name));
        }

        if (videoFiles.size() > 0) {
            UpdateTaskVideoAdapter adapter = new UpdateTaskVideoAdapter(UpdateTaskCommentActivity.this, videoFiles, position -> {
                UpdateTaskVideoAdapter.TaskVideoFile file = videoFiles.get(position);
                Intent showVideoIntent = new Intent(UpdateTaskCommentActivity.this, ShowVideoActivity.class);
                showVideoIntent.putExtra("videoPath", file.location);
                showVideoIntent.putExtra("videoName", file.name);
                UpdateTaskCommentActivity.this.startActivity(showVideoIntent);
            });

            binding.rvVideoFiles.setVisibility(View.VISIBLE);
            binding.rvVideoFiles.setLayoutManager(new LinearLayoutManager(UpdateTaskCommentActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.rvVideoFiles.setAdapter(adapter);
        }
    }

    private void changeBottomButtonsOnKeyboardShowing() {
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        binding.getRoot().getWindowVisibleDisplayFrame(r);
                        int screenHeight = binding.getRoot().getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            binding.bottomButtonsBar.setVisibility(View.VISIBLE);
                        }
                        else {
                            // keyboard is closed
                            binding.bottomButtonsBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}