package com.learn.kdnn.ui.bag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.learn.kdnn.MainViewModel;
import com.learn.kdnn.R;
import com.learn.kdnn.databinding.FragmentBagItemOptionsBinding;

import java.util.HashMap;


public class BagItemOptionsFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private FragmentBagItemOptionsBinding binding;
    private MainViewModel mainViewModel;
    public static final String PRODUCT_ID = "productID";
    private int productId;

    public BagItemOptionsFragment(int productId) {
        Bundle bundle = new Bundle();
        bundle.putInt(PRODUCT_ID, productId);
        setArguments(bundle);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
        if (getArguments() != null) {
            this.productId = getArguments().getInt(PRODUCT_ID);
        }
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBagItemOptionsBinding.inflate(inflater, container, false);

        binding.closeBagItemOptions.setOnClickListener(this);
        binding.removeFromBag.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.removeFromBag: {
                HashMap<Integer, Object> items = mainViewModel.getBag().getValue();
                items.remove(this.productId);
                mainViewModel.getBag().setValue(items);
                this.dismiss();
                break;
            }
            default: {
                this.dismiss();
            }
        }
    }
}