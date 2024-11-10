package com.worthybitbuilders.squadsense.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.worthybitbuilders.squadsense.MainActivity;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.AddBoardActivity;
import com.worthybitbuilders.squadsense.activities.MemberActivity;
import com.worthybitbuilders.squadsense.activities.ProjectActivity;
import com.worthybitbuilders.squadsense.activities.SearchActivity;
import com.worthybitbuilders.squadsense.adapters.ProjectAdapter;
import com.worthybitbuilders.squadsense.databinding.FragmentHomeBinding;
import com.worthybitbuilders.squadsense.databinding.MemberMoreOptionsBinding;
import com.worthybitbuilders.squadsense.databinding.MinimizeProjectMoreOptionsBinding;
import com.worthybitbuilders.squadsense.databinding.PopupOptionViewProjectBinding;
import com.worthybitbuilders.squadsense.databinding.ProjectMoreOptionsBinding;
import com.worthybitbuilders.squadsense.models.MinimizedProjectModel;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.models.board_models.ProjectModel;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.EventChecker;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;
import com.worthybitbuilders.squadsense.viewmodels.MainActivityViewModel;
import com.worthybitbuilders.squadsense.viewmodels.ProjectActivityViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ProjectAdapter projectAdapter;
    private MainActivityViewModel viewModel;

    private ProjectActivityViewModel projectActivityViewModel;
    private FriendViewModel friendViewModel;
    private UserViewModel userViewModel;
    private List<AppCompatButton> listOption = new ArrayList<>();
    private List<MinimizedProjectModel> listMinimizeProject = new ArrayList<>();

    private MainActivity.OptionViewProject optionViewProject;
    private EventChecker eventChecker = new EventChecker();

    @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        friendViewModel = new ViewModelProvider(getActivity()).get(FriendViewModel.class);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        projectActivityViewModel = new ViewModelProvider(getActivity()).get(ProjectActivityViewModel.class);
        binding.rvProjects.setLayoutManager(new LinearLayoutManager(getContext()));

        projectAdapter = new ProjectAdapter(
                listMinimizeProject,
                _id -> {
            SharedPreferencesManager.saveData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID, _id);
            saveRecentAccessProject(_id);
            Intent intent = new Intent(getContext(), ProjectActivity.class);
            intent.putExtra("whatToDo", "fetch");
            intent.putExtra("projectId", _id);
            startActivity(intent);
        },
                (view, _id) -> showMinimizeProjectOptions(view, _id));

        binding.btnOptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnOptionView_showPopup();
            }
        });
        binding.btnAddperson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_addperson_showPopup();
            }
        });
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.switchToActivity(getContext(), AddBoardActivity.class);
            }
        });
        binding.labelSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction() & MotionEvent.ACTION_MASK;
                if (action == MotionEvent.ACTION_UP) {
                    labelSearch_showActivity();
                }
                return true;
            }
        });
        return binding.getRoot();
    }

    //define function here
    private void LoadData()
    {
        Dialog loadingDialog = DialogUtils.GetLoadingDialog(getContext());
        loadingDialog.show();
        eventChecker.setActionWhenComplete(new EventChecker.CompleteCallback() {
            @Override
            public void Action() {
                loadingDialog.dismiss();
            }
        });

        optionViewProject = MainActivity.getHomeFragmentOptionViewProject();
        int GET_ALL_PROJECT_CODE = eventChecker.addEventStatusAndGetCode();
        viewModel.getAllProjects(new MainActivityViewModel.GetProjectsFromRemoteHandlers() {
            @Override
            public void onSuccess(List<MinimizedProjectModel> dataMinimizeProjects) {
                listMinimizeProject.clear();
                listMinimizeProject.addAll(dataMinimizeProjects);
                setListMinimizeProjectOnOptionView();
                eventChecker.markEventAsCompleteAndDoActionIfNeeded(GET_ALL_PROJECT_CODE);
            }

            @Override
            public void onFailure(String message) {
                eventChecker.markEventAsCompleteAndDoActionIfNeeded(GET_ALL_PROJECT_CODE);
                ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
            }
        });
    }

    private void LoadListMinimizeProjectView()
    {
        binding.rvProjects.setAdapter(projectAdapter);

        if(listMinimizeProject.size() > 0)
        {
            binding.emptyProjectsContainer.setVisibility(View.GONE);
            binding.rvProjects.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.emptyProjectsContainer.setVisibility(View.VISIBLE);
            binding.rvProjects.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LoadData();
    }

    @SuppressLint("ClickableViewAccessibility")

    private void btn_addperson_showPopup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_invite_by_email);

        //Set activity of button in dialog here
        ImageButton btnClosePopup = (ImageButton) dialog.findViewById(R.id.btn_close_popup);
        AppCompatButton btnInvite = (AppCompatButton) dialog.findViewById(R.id.btn_invite);
        EditText inputEmail = (EditText) dialog.findViewById(R.id.input_email);


        btnInvite.setOnClickListener(view -> {
            String receiverEmail = inputEmail.getText().toString();

            if(!friendViewModel.IsValidEmail(receiverEmail))
            {
                ToastUtils.showToastError(getContext(), "Invalid email", Toast.LENGTH_SHORT);
                return;
            }

            userViewModel.getUserByEmail(receiverEmail, new UserViewModel.UserCallback() {
                @Override
                public void onSuccess(UserModel user) {
                    friendViewModel.createRequest(SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID), user.getId(), new FriendViewModel.FriendRequestCallback() {
                        @Override
                        public void onSuccess() {
                            ToastUtils.showToastSuccess(getContext(), "Request was sent to " + receiverEmail + "!!", Toast.LENGTH_SHORT);
                        }

                        @Override
                        public void onFailure(String message) {
                            ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
                        }
                    });
                }

                @Override
                public void onFailure(String message) {
                    ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
                }
            });
        });

        btnClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(getContext().WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int halfScreenHeight = screenHeight / 2;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, halfScreenHeight);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void btnOptionView_showPopup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        PopupOptionViewProjectBinding popupOptionViewProjectBinding = PopupOptionViewProjectBinding.inflate(getLayoutInflater());
        dialog.setContentView(popupOptionViewProjectBinding.getRoot());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        setupListOption(popupOptionViewProjectBinding);

        //      Set activity of button in dialog here
        popupOptionViewProjectBinding.btnClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        popupOptionViewProjectBinding.optionRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedOption(popupOptionViewProjectBinding.optionRecent);
                optionViewProject = MainActivity.OptionViewProject.RECENT;
                MainActivity.setHomeFragmentOptionViewProject(optionViewProject);
                LoadData();
                dialog.dismiss();
            }
        });

        popupOptionViewProjectBinding.optionAllPorject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedOption(popupOptionViewProjectBinding.optionAllPorject);
                optionViewProject = MainActivity.OptionViewProject.ALLPROJECT;
                MainActivity.setHomeFragmentOptionViewProject(optionViewProject);
                LoadData();
                dialog.dismiss();
            }
        });

        popupOptionViewProjectBinding.optionMyPorject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedOption(popupOptionViewProjectBinding.optionMyPorject);
                optionViewProject = MainActivity.OptionViewProject.MYPROJECT;
                MainActivity.setHomeFragmentOptionViewProject(optionViewProject);
                LoadData();
                dialog.dismiss();
            }
        });
    }

    private void setupListOption(PopupOptionViewProjectBinding popupOptionViewProjectBinding)
    {
        listOption.clear();
        listOption.add(popupOptionViewProjectBinding.optionRecent);
        listOption.add(popupOptionViewProjectBinding.optionAllPorject);
        listOption.add(popupOptionViewProjectBinding.optionMyPorject);

        if(optionViewProject == MainActivity.OptionViewProject.ALLPROJECT)
            setSelectedOption(popupOptionViewProjectBinding.optionAllPorject);
        else if (optionViewProject == MainActivity.OptionViewProject.RECENT)
            setSelectedOption(popupOptionViewProjectBinding.optionRecent);
        else if (optionViewProject == MainActivity.OptionViewProject.MYPROJECT)
            setSelectedOption(popupOptionViewProjectBinding.optionMyPorject);
    }


    private void setSelectedOption(AppCompatButton option)
    {
        int chosenOptionColor = ContextCompat.getColor(getContext(), R.color.chosen_color);
        int primaryOptionColor = ContextCompat.getColor(getContext(), R.color.primary_word_color);

        listOption.forEach(item -> {
            setDrawableRight(item, null);
            changeColorButton(item, primaryOptionColor);
        });

        Drawable drawableRight = getResources().getDrawable(R.drawable.ic_tick, null);
        setDrawableRight(option, drawableRight);
        changeColorButton(option, chosenOptionColor);

        binding.titleOptionView.setText(option.getText().toString());
    }

    private void changeColorButton(AppCompatButton btn, int color)
    {
        btn.setTextColor(color);
        btn.setCompoundDrawableTintList(ColorStateList.valueOf(color));
    }

    private void setDrawableRight(AppCompatButton button, Drawable drawableRight) {
        button.post(new Runnable() {
            @Override
            public void run() {
                Drawable[] drawables = button.getCompoundDrawables();
                button.setCompoundDrawablesRelativeWithIntrinsicBounds(drawables[0], drawables[1], drawableRight, drawables[3]);
            }
        });
    }

    private void setListMinimizeProjectOnOptionView()
    {
        PopupOptionViewProjectBinding popupOptionViewProjectBinding = PopupOptionViewProjectBinding.inflate(getLayoutInflater());
        if(optionViewProject == MainActivity.OptionViewProject.ALLPROJECT) {
            binding.titleOptionView.setText(popupOptionViewProjectBinding.optionAllPorject.getText().toString());
            LoadListMinimizeProjectView();
        }
        else if (optionViewProject == MainActivity.OptionViewProject.RECENT) {
            binding.titleOptionView.setText(popupOptionViewProjectBinding.optionRecent.getText().toString());
            int GET_RECENT_PROJECT_ID_CODE = eventChecker.addEventStatusAndGetCode();
            userViewModel.getRecentProjectId(new UserViewModel.RecentProjectIdsCallback() {
                @Override
                public void onSuccess(List<String> dataRecentProjectIds) {
                    List<MinimizedProjectModel> toRemove = new ArrayList<>();
                    listMinimizeProject.forEach(minimizedProjectModel -> {
                        if (!dataRecentProjectIds.contains(minimizedProjectModel.get_id()))
                            toRemove.add(minimizedProjectModel);
                    });

                    toRemove.forEach(item -> {
                        listMinimizeProject.remove(item);
                    });

                    listMinimizeProject.sort((project1, project2) -> {
                        int index1 = dataRecentProjectIds.indexOf(project1.get_id());
                        int index2 = dataRecentProjectIds.indexOf(project2.get_id());
                        return Integer.compare(index1, index2);
                    });
                    LoadListMinimizeProjectView();
                    eventChecker.markEventAsCompleteAndDoActionIfNeeded(GET_RECENT_PROJECT_ID_CODE);
                }

                @Override
                public void onFailure(String message) {
                    ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
                    eventChecker.markEventAsCompleteAndDoActionIfNeeded(GET_RECENT_PROJECT_ID_CODE);
                }
            });
        }
        else if(optionViewProject == MainActivity.OptionViewProject.MYPROJECT)
        {
            binding.titleOptionView.setText(popupOptionViewProjectBinding.optionMyPorject.getText().toString());
            int GET_MY_OWN_PROJECT_CODE = eventChecker.addEventStatusAndGetCode();
            userViewModel.getMyOwnProject(new UserViewModel.ApiCallMyOwnProjectsHandlers() {
                @Override
                public void onSuccess(List<String> dataMyOwnProjectIds) {
                    List<MinimizedProjectModel> toRemove = new ArrayList<>();
                    listMinimizeProject.forEach(minimizedProjectModel -> {
                        if (!dataMyOwnProjectIds.contains(minimizedProjectModel.get_id()))
                            toRemove.add(minimizedProjectModel);
                    });

                    toRemove.forEach(item -> {
                        listMinimizeProject.remove(item);
                    });
                    LoadListMinimizeProjectView();
                    eventChecker.markEventAsCompleteAndDoActionIfNeeded(GET_MY_OWN_PROJECT_CODE);
                }

                @Override
                public void onFailure(String message) {
                    ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
                    eventChecker.markEventAsCompleteAndDoActionIfNeeded(GET_MY_OWN_PROJECT_CODE);
                }
            });
        }
    }

    private void labelSearch_showActivity() {
        ActivityUtils.switchToActivity(getContext(), SearchActivity.class);
    }

    private void showMinimizeProjectOptions(View anchor, String projectId) {
        MinimizeProjectMoreOptionsBinding binding = MinimizeProjectMoreOptionsBinding.inflate(getLayoutInflater());
        PopupWindow popupWindow = new PopupWindow(binding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setElevation(50);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        int xOffset = anchor.getWidth(); // Offset from the right edge of the anchor view
        int yOffset = - anchor.getHeight() / 2;
        popupWindow.showAsDropDown(anchor, xOffset, yOffset);

        binding.btnDeleteProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleConfirmDialog = "Delete";
                String contentConfirmDialog = "Do you want to delete this project ?";
                DialogUtils.showConfirmDialogDelete(getContext(), titleConfirmDialog, contentConfirmDialog, new DialogUtils.ConfirmAction() {
                    @Override
                    public void onAcceptToDo(Dialog thisDialog) {
                        thisDialog.dismiss();
                        projectActivityViewModel.deleteProject(projectId, new ProjectActivityViewModel.ApiCallHandlers() {
                            @Override
                            public void onSuccess() {
                                popupWindow.dismiss();
                                ToastUtils.showToastSuccess(getContext(), "Project deleted", Toast.LENGTH_SHORT);
                                LoadData();
                            }

                            @Override
                            public void onFailure(String message) {
                                ToastUtils.showToastError(getContext(), "You are not allowed to delete this project", Toast.LENGTH_SHORT);
                            }
                        });

                    }

                    @Override
                    public void onCancel(Dialog thisDialog) {
                        thisDialog.dismiss();
                    }
                });
            }
        });
    }

    private void saveRecentAccessProject(String projectId)
    {
        if(projectId == null) return;
        userViewModel.saveRecentProjectId(projectId, new UserViewModel.DefaultCallback() {
            @Override
            public void onSuccess() {
                //just save recent access and do no thing when success
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
            }
        });
    }
}