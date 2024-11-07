package com.worthybitbuilders.squadsense.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.AddBoardActivity;
import com.worthybitbuilders.squadsense.activities.ProjectActivity;
import com.worthybitbuilders.squadsense.activities.SearchActivity;
import com.worthybitbuilders.squadsense.adapters.ProjectAdapter;
import com.worthybitbuilders.squadsense.databinding.FragmentHomeBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;
import com.worthybitbuilders.squadsense.viewmodels.MainActivityViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ProjectAdapter projectAdapter;
    private MainActivityViewModel viewModel;

    FriendViewModel friendViewModel;
    UserViewModel userViewModel;

    @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        friendViewModel = new ViewModelProvider(getActivity()).get(FriendViewModel.class);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        LoadData();

        binding.btnMyfavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_myfavorities_showPopup();
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
                btnAdd_showPopup();
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
        // THERE IS ONLY "ONFAILURE" method
        Dialog loadingDialog = DialogUtils.GetLoadingDialog(getContext());
        loadingDialog.show();
        viewModel.getAllProjects(new MainActivityViewModel.GetProjectsFromRemoteHandlers() {
            @Override
            public void onSuccess() {
                loadingDialog.dismiss();
            }
            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(getContext(), message, Toast.LENGTH_LONG);
                loadingDialog.dismiss();
            }
        });

        viewModel.getProjectsLiveData().observe(getViewLifecycleOwner(), minimizedProjectModels -> {
            if (minimizedProjectModels == null || minimizedProjectModels.size() == 0)
                binding.emptyProjectsContainer.setVisibility(View.VISIBLE);
            else binding.emptyProjectsContainer.setVisibility(View.GONE);
            projectAdapter.setData(minimizedProjectModels);
        });

        projectAdapter = new ProjectAdapter(null, _id -> {
            Intent intent = new Intent(getContext(), ProjectActivity.class);
            intent.putExtra("whatToDo", "fetch");
            intent.putExtra("projectId", _id);
            startActivity(intent);
        });
        binding.rvProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvProjects.setHasFixedSize(true);
        binding.rvProjects.setAdapter(projectAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadData();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void btnAdd_showPopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_btn_add, null);
        View layout = binding.getRoot();
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        PopupWindow popupWindow = new PopupWindow(popupView,width,height, true);
        popupWindow.setAnimationStyle(R.style.PopupAnimationRight);
        layout.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(layout, Gravity.RIGHT, 0, 550);
            }
        });

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return true;
            }
        });

        LinearLayout btnAddItem = popupView.findViewById(R.id.btn_add_item);
        btnAddItem.setOnClickListener(view -> btn_add_item_showPopup());

        LinearLayout btnAddBoard = popupView.findViewById(R.id.btn_add_board);
        btnAddBoard.setOnClickListener(view -> btn_add_board_showPopup());
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

    private void btn_myfavorities_showPopup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_favorite_project);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        //      Set activity of button in dialog here
        ImageButton btnClosePopup = (ImageButton) dialog.findViewById(R.id.btn_close_popup);
        btnClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        LinearLayout optionRecent = (LinearLayout) dialog.findViewById(R.id.option_recent);
        LinearLayout optionMyfavorite = (LinearLayout) dialog.findViewById(R.id.option_myfavorite);

        ImageView iconRecent = (ImageView) dialog.findViewById(R.id.option_recent_icon);
        TextView titleRecent = (TextView) dialog.findViewById(R.id.option_recent_title);
        ImageView tickRecent = (ImageView) dialog.findViewById(R.id.option_recent_tick);
        ImageView iconMyfavorite = (ImageView) dialog.findViewById(R.id.option_myfavorite_icon);
        TextView titleMyfavorite = (TextView) dialog.findViewById(R.id.option_myfavorite_title);
        ImageView tickMyfavorite = (ImageView) dialog.findViewById(R.id.option_myfavorite_tick);

        int chosenColor = getResources().getColor(R.color.chosen_color);
        int defaultColor = getResources().getColor(R.color.primary_icon_color);

        updateButtonState(iconRecent, titleRecent, tickRecent, chosenColor, true);
        optionRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateButtonState(iconRecent, titleRecent, tickRecent, chosenColor, true);
                updateButtonState(iconMyfavorite, titleMyfavorite, tickMyfavorite, defaultColor, false);
            }
        });

        optionMyfavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateButtonState(iconRecent, titleRecent, tickRecent, defaultColor, false);
                updateButtonState(iconMyfavorite, titleMyfavorite, tickMyfavorite, chosenColor, true);
            }
        });
    }

    private void updateButtonState(ImageView icon, TextView title, ImageView tick, int color, boolean tickState)
    {
        icon.setColorFilter(color);
        title.setTextColor(color);
        if(tickState)  tick.setVisibility(View.VISIBLE);
        else tick.setVisibility(View.INVISIBLE);
    }
    private void labelSearch_showActivity() {
        ActivityUtils.switchToActivity(getContext(), SearchActivity.class);
    }
}