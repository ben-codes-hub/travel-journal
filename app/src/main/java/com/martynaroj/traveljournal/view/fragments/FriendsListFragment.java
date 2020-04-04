package com.martynaroj.traveljournal.view.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.martynaroj.traveljournal.R;
import com.martynaroj.traveljournal.databinding.FragmentFriendsListBinding;
import com.martynaroj.traveljournal.services.models.User;
import com.martynaroj.traveljournal.view.adapters.UserAdapter;
import com.martynaroj.traveljournal.view.base.BaseFragment;
import com.martynaroj.traveljournal.view.others.interfaces.Constants;
import com.martynaroj.traveljournal.viewmodels.UserViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsListFragment extends BaseFragment {

    private FragmentFriendsListBinding binding;
    private UserViewModel userViewModel;
    private User user;
    private User loggedUser;
    private UserAdapter adapter;


    static FriendsListFragment newInstance(User user) {
        return new FriendsListFragment(user);
    }


    public FriendsListFragment() {
        super();
    }


    private FriendsListFragment(User user) {
        super();
        this.user = user;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFriendsListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initViewModels();
        setListeners();

        initUser();
        initLoggedUser();

        initFriends();

        return view;
    }


    //INIT DATA-------------------------------------------------------------------------------------


    private void initViewModels() {
        if (getActivity() != null) {
            userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        }
    }


    private void initUser() {
        if (user == null) {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        }
    }


    private void initLoggedUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            startProgressBar();
            userViewModel.getUserData(firebaseAuth.getCurrentUser().getUid());
            userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    this.loggedUser = user;
                    initTitle();
                } else
                    showSnackBar(getResources().getString(R.string.messages_error_current_user_not_available), Snackbar.LENGTH_LONG);
                stopProgressBar();
            });
        }
    }


    @SuppressLint("SetTextI18n")
    private void initTitle() {
        if (user != null && loggedUser != null) {
            if (user.isUserProfile(loggedUser))
                binding.friendsListTitle.setText("My " + getResources().getString(R.string.friends_list_title));
            else
                binding.friendsListTitle.setText(user.getUsername() + "\'s\n" +
                        getResources().getString(R.string.friends_list_title));
        }
    }


    private void initFriends() {
        if (user != null && user.getFriends() != null && !user.getFriends().isEmpty()) {
            startProgressBar();
            binding.friendsListMessage.setVisibility(View.INVISIBLE);
            userViewModel.getUsersListData(user.getFriends());
            userViewModel.getUsersList().observe(getViewLifecycleOwner(), users -> {
                if (users != null) {
                    initFriendsList(users);
                }
                stopProgressBar();
            });
        } else {
            binding.friendsListRecyclerView.setVisibility(View.INVISIBLE);
            binding.friendsListMessage.setVisibility(View.VISIBLE);
        }
    }


    private void initFriendsList(List<User> friends) {
        adapter = new UserAdapter(getContext(), friends, user.isUserProfile(loggedUser));
        binding.friendsListRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((object, position, view) -> {
            User userItem = (User) object;
            if (userItem != null) {
                switch (view.getId()) {
                    case R.id.user_item:
                        if (loggedUser != null && loggedUser.isUserProfile(userItem))
                            showSnackBar(getResources().getString(R.string.messages_its_you), Snackbar.LENGTH_SHORT);
                        else
                            changeFragment(ProfileFragment.newInstance(userItem));
                        break;
                    case R.id.user_item_delete_button:
                        showDeleteDialog(userItem, position);
                        break;
                }
            }
        });
    }


    //LISTENERS-------------------------------------------------------------------------------------


    private void setListeners() {
        binding.friendsListArrowButton.setOnClickListener(view -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0)
                getParentFragmentManager().popBackStack();
        });
    }


    //DIALOG----------------------------------------------------------------------------------------


    @SuppressLint("SetTextI18n")
    private void showDeleteDialog(User user, int position) {
        if (getContext() != null && getActivity() != null) {
            Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_custom);

            TextView title = dialog.findViewById(R.id.dialog_custom_title);
            TextView message = dialog.findViewById(R.id.dialog_custom_desc);
            MaterialButton buttonPositive = dialog.findViewById(R.id.dialog_custom_button_positive);
            MaterialButton buttonNegative = dialog.findViewById(R.id.dialog_custom_button_negative);

            title.setText(getResources().getString(R.string.dialog_remove_friends_title));
            message.setText(getResources().getString(R.string.dialog_remove_friends_desc) + " " + user.getUsername() + "?");
            buttonPositive.setText(getResources().getString(R.string.dialog_button_remove));
            buttonPositive.setOnClickListener(v -> {
                dialog.dismiss();
                removeFriend(user, position);
            });
            buttonNegative.setText(getResources().getString(R.string.dialog_button_cancel));
            buttonNegative.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        }
    }


    //FRIENDS---------------------------------------------------------------------------------------


    private void removeFriend(User friend, int position) {
        startProgressBar();

        Map<String, Object> changesFriendUser = new HashMap<>();
        friend.getFriends().remove(loggedUser.getUid());
        changesFriendUser.put(Constants.DB_FRIENDS, friend.getFriends());
        userViewModel.updateUser(friend, changesFriendUser);

        Map<String, Object> changesLoggedUser = new HashMap<>();
        loggedUser.getFriends().remove(friend.getUid());
        changesLoggedUser.put(Constants.DB_FRIENDS, loggedUser.getFriends());

        userViewModel.updateUser(loggedUser, changesLoggedUser);
        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                this.user = user;
                this.loggedUser = user;
                userViewModel.setUser(user);
                adapter.remove(position);
                if (adapter.getItemCount() == 0) {
                    binding.friendsListRecyclerView.setVisibility(View.INVISIBLE);
                    binding.friendsListMessage.setVisibility(View.VISIBLE);
                }
                showSnackBar(getResources().getString(R.string.messages_remove_friend_success),
                        Snackbar.LENGTH_SHORT);
            } else
                showSnackBar(getResources().getString(R.string.messages_error_failed_remove_friend),
                        Snackbar.LENGTH_LONG);
            stopProgressBar();
        });
    }


    //OTHERS----------------------------------------------------------------------------------------


    private void changeFragment(BaseFragment next) {
        if (user.isUserProfile(loggedUser))
            getNavigationInteractions().changeFragment(this, next, true);
        else
            getNavigationInteractions().changeFragment(getParentFragment(), next, true);
    }


    private void startProgressBar() {
        getProgressBarInteractions().startProgressBar(binding.getRoot(),
                binding.friendsListProgressbarLayout, binding.friendsListProgressbar);
    }


    private void stopProgressBar() {
        getProgressBarInteractions().stopProgressBar(binding.getRoot(),
                binding.friendsListProgressbarLayout, binding.friendsListProgressbar);
    }


    private void showSnackBar(String message, int duration) {
        getSnackBarInteractions().showSnackBar(binding.getRoot(), getActivity(), message, duration);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
