package com.worthybitbuilders.squadsense.Fragments;


import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.Pages.page_add_board;
import com.worthybitbuilders.squadsense.Pages.page_search;
import com.worthybitbuilders.squadsense.Util.SwitchActivity;

public class HomeFragment extends Fragment {

    RelativeLayout layout = null;
    Button btn_myfavorities = null;
    ImageButton btn_addperson = null;
    ImageButton btn_add = null;
    EditText inputSearchLabel = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        //Init variables here
        btn_myfavorities = v.findViewById(R.id.btn_myfavorites);
        btn_addperson = v.findViewById(R.id.btn_addperson);
        btn_add = v.findViewById(R.id.btn_add);
        inputSearchLabel = v.findViewById(R.id.input_search_label);
        layout = v.findViewById(R.id.home_fragment);

        //set onclick buttons here
        btn_myfavorities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_myfavorities_showPopup();
            }
        });
        btn_addperson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_addperson_showPopup();

            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAdd_showPopup();
            }
        });
        inputSearchLabel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction() & MotionEvent.ACTION_MASK;

                if (action == MotionEvent.ACTION_UP) {
                    inputSearch_showActivity();
                }
                return true;
            }
        });
        return v;
    }


    //define function here
    private void btnAdd_showPopup() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_btn_add, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        PopupWindow popupBtnAdd = new PopupWindow(popupView,width,height, true);
        popupBtnAdd.setAnimationStyle(R.style.PopupAnimationRight);
        layout.post(new Runnable() {
            @Override
            public void run() {
                popupBtnAdd.showAtLocation(layout, Gravity.RIGHT, 0, 550);
            }
        });

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupBtnAdd.dismiss();
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


        //


        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
        ImageButton btnClosePopup = (ImageButton) dialog.findViewById(R.id.btn_close_popup);
        btnClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void btn_add_item_showPopup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_add_new_item);

        //Set activity of button in dialog here


        //


        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
        ImageButton btnClosePopup = (ImageButton) dialog.findViewById(R.id.btn_close_popup);
        btnClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void btn_add_board_showPopup() {
        SwitchActivity.switchToActivity(getContext(), page_add_board.class);
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

        //
    }



    private void updateButtonState(ImageView icon, TextView title, ImageView tick, int color, boolean tickState)
    {
        icon.setColorFilter(color);
        title.setTextColor(color);
        if(tickState)  tick.setVisibility(View.VISIBLE);
        else tick.setVisibility(View.INVISIBLE);
    }
    private void inputSearch_showActivity() {
        SwitchActivity.switchToActivity(getContext(), page_search.class);
    }
}