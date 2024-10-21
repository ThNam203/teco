package com.worthybitbuilders.squadsense.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.BoardAddNewRowPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardEditBoardsOptionMoreBinding;
import com.worthybitbuilders.squadsense.databinding.BoardEditBoardsViewBinding;
import com.worthybitbuilders.squadsense.databinding.ConfirmDeleteViewBinding;
import com.worthybitbuilders.squadsense.models.board_models.BoardContentModel;
import com.worthybitbuilders.squadsense.models.board_models.ProjectModel;

import java.util.ArrayList;
import java.util.Locale;

public class EditBoardsAdapter extends RecyclerView.Adapter<EditBoardsAdapter.EditBoardItemViewHolder> {
    ProjectModel projectModel;
    ClickHandlers handlers;
    private Context context;

    public EditBoardsAdapter(ProjectModel projectModel, Context context) {
        this.projectModel = projectModel;
        this.context = context;
    }

    public void setHandlers(ClickHandlers handlers) {
        this.handlers = handlers;
    }

    @NonNull
    @Override
    public EditBoardItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_edit_boards_item_view, parent, false);
        return new EditBoardItemViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull EditBoardItemViewHolder holder, int position) {
        holder.bind(projectModel.getBoards().get(position).getBoardTitle(), handlers, projectModel.getChosenPosition(), position);
    }

    @Override
    public int getItemCount() {
        return projectModel.getBoards().size();
    }

    public static class EditBoardItemViewHolder extends RecyclerView.ViewHolder {
        private final EditText etBoardName;
        private final TextView tvBoardName;
        private final ImageButton btnMoreOptions;
        private final LinearLayout container;
        private final ImageButton btnConfirmRename;
        private final Context context;
        private ClickHandlers handlers;
        public EditBoardItemViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            this.etBoardName = itemView.findViewById(R.id.etBoardName);
            this.tvBoardName = itemView.findViewById(R.id.tvBoardName);
            this.btnMoreOptions = itemView.findViewById(R.id.btnMoreOptions);
            this.container = itemView.findViewById(R.id.boardEditContainer);
            this.btnConfirmRename = itemView.findViewById(R.id.btnConfirmRename);
        }

        public void bind(String boardName, ClickHandlers handlers, int chosenPosition, int position) {
            this.etBoardName.setText(boardName);
            this.tvBoardName.setText(boardName);
            this.handlers = handlers;
            if (chosenPosition == position) this.container.setBackgroundColor(Color.parseColor("#464646"));

            this.btnMoreOptions.setOnClickListener(view -> showMoreOptionsPopup(position, boardName));

            this.container.setOnClickListener(view -> handlers.onItemClick(position));
            this.btnConfirmRename.setOnClickListener(view -> {
                String newTitle = this.etBoardName.getText().toString();
                if (newTitle.isEmpty()) return;
                handlers.onRenameClick(position, newTitle);
                this.tvBoardName.setVisibility(View.VISIBLE);
                this.etBoardName.setVisibility(View.GONE);
                this.btnConfirmRename.setVisibility(View.GONE);
            });
        }

        private void showMoreOptionsPopup(int position, String currentTitle) {
            BoardEditBoardsOptionMoreBinding binding = BoardEditBoardsOptionMoreBinding.inflate(LayoutInflater.from(context));
            PopupWindow popupWindow = new PopupWindow(binding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);

            binding.btnRenameBoard.setOnClickListener(view -> {
                this.etBoardName.requestFocus();
                this.tvBoardName.setVisibility(View.GONE);
                this.etBoardName.setVisibility(View.VISIBLE);
                this.btnConfirmRename.setVisibility(View.VISIBLE);
                popupWindow.dismiss();
            });
            binding.btnDeleteBoard.setOnClickListener(view -> {
//                showConfirmDelete(position, currentTitle);
                showConfirmDelete(position, currentTitle);
                popupWindow.dismiss();
            });

            // align the drop down
            binding.getRoot().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int xOffset = -(binding.getRoot().getMeasuredWidth() - this.btnMoreOptions.getWidth());

            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAsDropDown(this.btnMoreOptions, xOffset, 0);
        }

        private void showConfirmDelete(int position, String currentTitle) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            ConfirmDeleteViewBinding binding = ConfirmDeleteViewBinding.inflate(LayoutInflater.from(context));
            dialog.setContentView(binding.getRoot());
            binding.deleteTitle.setText(currentTitle);
            binding.etContent.setText(String.format(Locale.US, "Are you sure to delete \"%s\" board", currentTitle));
            binding.btnClosePopup.setOnClickListener(view -> dialog.dismiss());
            binding.btnCancel.setOnClickListener(view -> dialog.dismiss());
            binding.btnAccept.setOnClickListener(view -> {
                handlers.onRemoveClick(position);
                dialog.dismiss();
            });

            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();
        }
    }

    public interface ClickHandlers {
        void onRemoveClick(int position);
        void onRenameClick(int position, String newTitle);
        void onItemClick(int position);
    }
}
