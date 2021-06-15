package com.learn.kdnn.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.learn.kdnn.AppCacheManage;
import com.learn.kdnn.MainActivity;
import com.learn.kdnn.R;
import com.learn.kdnn.databinding.FragmentViewAddedItemBinding;
import com.learn.kdnn.model.Product;

import org.jetbrains.annotations.NotNull;

public class ViewAddedItemBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    public static String ADDED_ITEM_INDEX = "addItemIndex";
    public static String QUALITY = "quality";
    private FragmentViewAddedItemBinding binding;
    private int index;
    private int quality;

    public static ViewAddedItemBottomSheet newInstance(int index,int quality) {
        ViewAddedItemBottomSheet fragment = new ViewAddedItemBottomSheet();
        Bundle args = new Bundle();
        args.putInt(ADDED_ITEM_INDEX, index);
        args.putInt(QUALITY, quality);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
        if (getArguments() != null) {
            this.index = getArguments().getInt(ADDED_ITEM_INDEX);
            this.quality = getArguments().getInt(QUALITY);
        }
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        binding = FragmentViewAddedItemBinding.inflate(inflater, container, false);

        binding.btnClose.setOnClickListener(v -> this.dismiss());

        binding.btnViewBag.setOnClickListener(this);
                binding.btnContinuteBuy.setOnClickListener(this);

        Product addedProduct = AppCacheManage.products.get(this.index);
        binding.addedProductName.setText(addedProduct.getName());
        binding.addedQuality.setText(String.valueOf(quality));
        Glide.with(getContext())
                .load(addedProduct.getPrimaryImgUrl())
                .centerCrop()
                .into(binding.addedImg);
        return binding.getRoot();
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
        MainActivity mainActivity = (MainActivity) getContext();
        switch (v.getId()){
            case R.id.btn_view_bag:{
                mainActivity.getMainNavController().navigate(R.id.action_nav_product_details_to_bagFragment);
                break;
            }
            case R.id.btn_continute_buy:{
                mainActivity.getMainNavController().navigate(R.id.action_nav_product_details_to_nav_home);
                break;
            }
        }
    }
}
