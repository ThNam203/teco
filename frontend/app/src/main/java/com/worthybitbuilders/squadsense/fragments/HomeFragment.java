package com.worthybitbuilders.squadsense.fragments;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

import com.worthybitbuilders.squadsense.databinding.FragmentHomeBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;
import com.worthybitbuilders.squadsense.activities.AddBoardActivity;
import com.worthybitbuilders.squadsense.activities.SearchActivity;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.Activity;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    FriendViewModel friendViewModel;
    UserViewModel userViewModel;
    String hello;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        SharedPreferencesManager.init(getContext());

        //set onclick buttons here
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
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_add_item_showPopup();
            }
        });

        LinearLayout btnAddBoard = popupView.findViewById(R.id.btn_add_board);
        btnAddBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_add_board_showPopup();
            }
        });
    }

    private void btn_addperson_showPopup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_btn_invite_by_email);

        //Set activity of button in dialog here
        ImageButton btnClosePopup = (ImageButton) dialog.findViewById(R.id.btn_close_popup);
        AppCompatButton btnInvite = (AppCompatButton) dialog.findViewById(R.id.btn_invite);
        EditText inputEmail = (EditText) dialog.findViewById(R.id.input_email);


        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiverEmail = inputEmail.getText().toString();

                if(!friendViewModel.IsValidEmail(receiverEmail))
                {
                    Toast.makeText(getContext(), "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                userViewModel.getUserByEmail(receiverEmail, new UserViewModel.UserCallback() {
                    @Override
                    public void onSuccess(UserModel user) {
                        friendViewModel.createRequest(SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USERID), user.getId(), new FriendViewModel.FriendRequestCallback() {
                            @Override
                            public void onSuccess() {
                                Toast t = Toast.makeText(getContext(), "request was sent to " + receiverEmail + "!!", Toast.LENGTH_SHORT);
                                t.setGravity(Gravity.TOP, 0, 0);
                                t.show();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast t = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
                                t.setGravity(Gravity.TOP, 0, 0);
                                t.show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast t = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP, 0, 0);
                        t.show();
                    }
                });
            }
        });

        btnClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //

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

        //
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void btn_add_board_showPopup() {
        Activity.switchToActivity(getContext(), AddBoardActivity.class);
    }

    private void btn_myfavorities_showPopup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_btn_myfavorite);

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
        Activity.switchToActivity(getContext(), SearchActivity.class);
    }
}