package com.worthybitbuilders.squadsense.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.BoardItemDetailActivity;
import com.worthybitbuilders.squadsense.activities.ProjectActivity;
import com.worthybitbuilders.squadsense.adapters.WorkAdapter;
import com.worthybitbuilders.squadsense.databinding.FragmentWorkBinding;
import com.worthybitbuilders.squadsense.models.WorkModel;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.MainActivityViewModel;

public class WorkFragment extends Fragment {
    private FragmentWorkBinding binding;
    private Boolean isHidingDoneItems = false;
    private Context mContext;
    private MainActivityViewModel activityViewModel;
    private WorkAdapter workAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkBinding.inflate(getLayoutInflater());
        activityViewModel = new ViewModelProvider((AppCompatActivity) mContext).get(MainActivityViewModel.class);
        workAdapter = new WorkAdapter(position -> {
            // deep link
            Intent projectIntent = new Intent(mContext, ProjectActivity.class);
            WorkModel model = activityViewModel.getUserWorks().get(position);
            projectIntent.putExtra("projectId", model.getProjectId());
            projectIntent.putExtra("boardPosition", model.getBoardPosition());
            projectIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(projectIntent);

            Intent detailIntent = new Intent(mContext, BoardItemDetailActivity.class);
            detailIntent.putExtra("projectId", model.getProjectId());
            detailIntent.putExtra("boardId", model.getBoardId());
            detailIntent.putExtra("projectTitle", model.getProjectTitle());
            detailIntent.putExtra("boardTitle", model.getBoardTitle());
            detailIntent.putExtra("rowPosition", model.getCellRowPosition());
            detailIntent.putExtra("rowTitle", model.getRowTitle());
            detailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(detailIntent);
        });
        binding.rvWork.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvWork.setAdapter(workAdapter);
        //set onclick buttons here
        binding.btnHideDoneItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isHidingDoneItems)
                {
                    Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_checkbox_checked);
                    drawable.setColorFilter(ContextCompat.getColor(getActivity(), R.color.chosen_color), PorterDuff.Mode.SRC_IN);
                    binding.btnHideDoneItem.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

                    isHidingDoneItems = true;
                }
                else
                {
                    Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_checkbox_unchecked);
                    drawable.setColorFilter(ContextCompat.getColor(getActivity(), R.color.primary_icon_color), PorterDuff.Mode.SRC_IN);
                    binding.btnHideDoneItem.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

                    isHidingDoneItems = false;
                }

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Dialog loadingDialog = DialogUtils.GetLoadingDialog(mContext);
        loadingDialog.show();
        activityViewModel.getAllUserWork(new MainActivityViewModel.ApiCallHandler() {
            @Override
            public void onSuccess() {
                workAdapter.setData(activityViewModel.getUserWorks());
                if (activityViewModel.getUserWorks().size() == 0) {
                    binding.rvWork.setVisibility(View.GONE);
                    binding.emptyWorkNotification.setVisibility(View.VISIBLE);
                } else {
                    binding.rvWork.setVisibility(View.VISIBLE);
                    binding.emptyWorkNotification.setVisibility(View.GONE);
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(mContext, message, Toast.LENGTH_LONG);
                loadingDialog.dismiss();
            }
        });
    }

    //define function here
}