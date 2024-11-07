package com.worthybitbuilders.squadsense.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
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

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.AddBoardActivity;
import com.worthybitbuilders.squadsense.activities.ProjectActivity;
import com.worthybitbuilders.squadsense.activities.SearchActivity;
import com.worthybitbuilders.squadsense.adapters.ProjectAdapter;
import com.worthybitbuilders.squadsense.databinding.FragmentHomeBinding;
import com.worthybitbuilders.squadsense.databinding.MemberMoreOptionsBinding;
import com.worthybitbuilders.squadsense.databinding.PopupBtnAddBinding;
import com.worthybitbuilders.squadsense.databinding.PopupOptionViewProjectBinding;
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
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ProjectAdapter projectAdapter;
    private MainActivityViewModel viewModel;

    private FriendViewModel friendViewModel;
    private UserViewModel userViewModel;
    private List<AppCompatButton> listOption = new ArrayList<>();
    private List<MinimizedProjectModel> listMinimizeProject = new ArrayList<>();
    private enum OptionViewProject {ALLPROJECT, RECENT, MYPROJECT}
    private OptionViewProject optionViewProject = OptionViewProject.ALLPROJECT;
    private EventChecker eventChecker = new EventChecker();

    @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        friendViewModel = new ViewModelProvider(getActivity()).get(FriendViewModel.class);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        binding.rvProjects.setLayoutManager(new LinearLayoutManager(getContext()));

        projectAdapter = new ProjectAdapter(listMinimizeProject, _id -> {
            Intent intent = new Intent(getContext(), ProjectActivity.class);
            intent.putExtra("whatToDo", "fetch");
            intent.putExtra("projectId", _id);
            startActivity(intent);
        });

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
                btnAdd_showPopup(view);
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
    private void btnAdd_showPopup(View anchor) {
        PopupBtnAddBinding popupBtnAddBinding = PopupBtnAddBinding.inflate(getLayoutInflater());
        PopupWindow popupWindow = new PopupWindow(popupBtnAddBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.PopupAnimationRight);
        popupWindow.setElevation(50);

        popupBtnAddBinding.btnAddItem.setOnClickListener(view -> btn_add_item_showPopup());
        popupBtnAddBinding.btnAddBoard.setOnClickListener(view -> btn_add_board_showPopup());

        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        int xOffset = - 3 * anchor.getWidth();
        int yOffset = - 3 * anchor.getHeight();
        popupWindow.showAsDropDown(anchor, xOffset, yOffset);
    }

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

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void btn_add_item_showPopup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_add_new_item);

        //Set activity of button in dialog here
        ImageButton btnClosePopup = (ImageButton) dialog.findViewById(R.id.btn_close_popup);
        btnClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void btn_add_board_showPopup() {
        ActivityUtils.switchToActivity(getContext(), AddBoardActivity.class);
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
                optionViewProject = OptionViewProject.RECENT;
                LoadData();
                dialog.dismiss();
            }
        });

        popupOptionViewProjectBinding.optionAllPorject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedOption(popupOptionViewProjectBinding.optionAllPorject);
                optionViewProject = OptionViewProject.ALLPROJECT;
                LoadData();
                dialog.dismiss();
            }
        });

        popupOptionViewProjectBinding.optionMyPorject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedOption(popupOptionViewProjectBinding.optionMyPorject);
                optionViewProject = OptionViewProject.MYPROJECT;
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

        if(optionViewProject == OptionViewProject.ALLPROJECT)
            setSelectedOption(popupOptionViewProjectBinding.optionAllPorject);
        else if (optionViewProject == OptionViewProject.RECENT)
            setSelectedOption(popupOptionViewProjectBinding.optionRecent);
        else if (optionViewProject == OptionViewProject.MYPROJECT)
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
        if(optionViewProject == OptionViewProject.ALLPROJECT) {
            LoadListMinimizeProjectView();
        }
        else if (optionViewProject == OptionViewProject.RECENT) {
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
        else if(optionViewProject == OptionViewProject.MYPROJECT)
        {
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
}