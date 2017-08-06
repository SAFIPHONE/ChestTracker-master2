package com.darkarmed.chesttrackerforclashroyale;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


public class HintFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TYPES = "types";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Map<Chest.Type, Integer> mTypes;
    private String mParam2;

    private Context mContext;

    public HintFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param types Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HintFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HintFragment newInstance(HashMap<Chest.Type, Integer> types, String param2) {
        HintFragment fragment = new HintFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPES, types);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTypes = (Map) getArguments().getSerializable(ARG_TYPES);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hint, container, false);
        view.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDark));
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.linear_layout);
        float total = 0;
        for (int value : mTypes.values()) {
            total += value;
        }

        int margin = mContext.getResources().getDimensionPixelOffset(R.dimen.hint_view_margin);

        for (Map.Entry<Chest.Type, Integer> e : mTypes.entrySet()) {
            View chestView = inflater.inflate(R.layout.view_chest, container, false);

            ImageView imageView = (ImageView) chestView.findViewById(R.id.chest_image);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            lp.setMargins(margin, margin, margin, 0);
            imageView.setLayoutParams(lp);

            switch (e.getKey()) {
                case GOLDEN:
                    imageView.setImageResource(R.drawable.golden_chest_locked);
                    break;
                case GIANT:
                    imageView.setImageResource(R.drawable.giant_chest_locked);
                    break;
                case MAGICAL:
                    imageView.setImageResource(R.drawable.magical_chest_locked);
                    break;
                default:
            }
            TextView textView = (TextView) chestView.findViewById(R.id.chest_text);
            float rate = e.getValue() / total;
            textView.setText(String.format("%.1f", rate * 100) + "%");
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Supercell-Magic_5.ttf");
            textView.setTypeface(tf);

            ll.addView(chestView);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
