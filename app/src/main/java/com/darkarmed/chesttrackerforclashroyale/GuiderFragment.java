package com.darkarmed.chesttrackerforclashroyale;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GuiderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GuiderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuiderFragment extends Fragment {
    private static final String TAG = "GuiderFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "user";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mUser;
    private String mParam2;

    private Context mContext;
    private Menu mMenu;
    private View mView;
    private Toast mToast;
    private GridView mGridView;
    private TextView mHelperTextView;
    private TextView mMatchedTextView;

    private ChestAdapter mAdapter;
    private List<Chest> mChests;
    private Set<Map.Entry<Integer, Integer>> mMatchPositions;

    private String mLoop;

    private final int SHORT_LOOP_LENGTH = 40;
    private final int FULL_LOOP_LENGTH = 240;

    private OnFragmentInteractionListener mListener;

    public enum ChestButtonEnum {
        SILVER(R.id.silver_chest_button, Chest.Type.SILVER),
        GOLDEN(R.id.golden_chest_button, Chest.Type.GOLDEN),
        GIANT(R.id.giant_chest_button, Chest.Type.GIANT),
        MAGICAL(R.id.magical_chest_button, Chest.Type.MAGICAL);

        private int mViewResId;
        private Chest.Type mType;

        ChestButtonEnum(int viewResId, Chest.Type type) {
            mViewResId = viewResId;
            mType = type;
        }

        public int getViewResId() {
            return mViewResId;
        }

        public Chest.Type getType() {
            return mType;
        }
    }

    public GuiderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GuiderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GuiderFragment newInstance(String user, String param2) {
        GuiderFragment fragment = new GuiderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, user);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mUser = getArguments().getString(ARG_USER);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mLoop = getString(R.string.chest_loop);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_guider, container, false);
        mGridView = (GridView) mView.findViewById(R.id.guiderview);
        return mView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Supercell-Magic_5.ttf");

        mHelperTextView = (TextView) mView.findViewById(R.id.helper_text);
        mHelperTextView.setText(R.string.guider_helper);
        if (Locale.getDefault().getLanguage().equals("en")) {
            mHelperTextView.setTypeface(tf);
        }

        mMatchedTextView = (TextView) mView.findViewById(R.id.matched_text);
        mMatchedTextView.setText("");
        mMatchedTextView.setTypeface(tf);

        loadChests();
        loadViews();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<Chest.Type, Integer> types = ((Chest) mAdapter.getItem(position)).getTypes();
                if (types.size() > 1) {
                    mListener.onShowHint(types);
                }
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < mChests.size()) {
                    mChests.remove(position);
                    loadViews();
                    loadProgress();
                }
                return true;
            }
        });

        for (ChestButtonEnum e : ChestButtonEnum.values()) {
            final ImageButton imageButton = (ImageButton) mView.findViewById(e.getViewResId());
            imageButton.setTag(e.getType());
            imageButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            imageButton.setScaleX(0.9f);
                            imageButton.setScaleY(0.9f);
                            break;
                        case MotionEvent.ACTION_UP:
                            imageButton.setScaleX(1f);
                            imageButton.setScaleY(1f);
                            break;
                        default:
                    }
                    return false;
                }
            });

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    addChest((Chest.Type) v.getTag());

                    if (mToast == null) {
                        mToast = Toast.makeText(mContext, getString(R.string.long_press_to_cancel),
                                Toast.LENGTH_SHORT);
                    } else {
                        mToast.setDuration(Toast.LENGTH_SHORT);
                    }
                    mToast.show();
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveChests();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onMatchPositionApply(int pos, int length);
        void onShowHint(Map<Chest.Type, Integer> types);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        inflater.inflate(R.menu.menu_guider, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.d(TAG, "TrackerFragment action settings clicked.");
                return true;
            case R.id.action_clear:
                Log.d(TAG, "TrackerFragment action clear clicked.");
                clearAll();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private boolean loadChests() {
        SharedPreferences chestPref = getActivity().getSharedPreferences(mUser, Context.MODE_PRIVATE);
        String json = chestPref.getString("CHESTS", "");

        if (json.equalsIgnoreCase("")) {
            mChests = new ArrayList<>();
            return false;
        } else {
            Log.d(TAG, json);
            mChests = new Gson().fromJson(json, new TypeToken<List<Chest>>() {}.getType());
            return true;
        }
    }

    private boolean saveChests() {
        SharedPreferences chestPref = getActivity().getSharedPreferences(mUser, Context.MODE_PRIVATE);
        String json = new Gson().toJson(mChests);
        chestPref.edit().putString("CHESTS", json).commit();

        Log.d(TAG, json);
        Log.d(TAG, chestPref.getString("CHESTS", ""));
        return true;
    }

    private void addChest(Chest.Type type) {
        Chest chest = new Chest(mChests.size(), type, Chest.Status.OPENED);
        mChests.add(chest);
        loadViews();
        loadProgress();
    }

    private void loadViews() {

        if (mChests.size() == 0) {
            mGridView.setVisibility(View.GONE);
            mMatchedTextView.setVisibility(View.GONE);
            mHelperTextView.setVisibility(View.VISIBLE);
            return;
        }

        mHelperTextView.setVisibility(View.GONE);
        mGridView.setVisibility(View.VISIBLE);
        mMatchedTextView.setVisibility(View.VISIBLE);

        List<Chest> chests = new ArrayList<>(mChests.size());
        try {
            for (Chest chest : mChests) {
                chests.add((Chest)chest.clone());
            }
        } catch (CloneNotSupportedException e) {
            Log.e(TAG, e.getMessage());
        }

        if (getMatched()) {
            int firstMatched =  mChests.size() - mMatchPositions.iterator().next().getValue();
            for (int i = 0; i < mChests.size(); ++i) {
                chests.get(i).setMatched(i >= firstMatched);
            }

            List<Integer> posList = new ArrayList<>(mMatchPositions.size());
            for (Map.Entry<Integer, Integer> e : mMatchPositions) {
                posList.add(e.getKey());
            }

            for (int i = 0; i < SHORT_LOOP_LENGTH; ++i) {
                Map<Character, Integer> types = new HashMap<>();
                for (Integer pos : posList) {
                    char c = mLoop.charAt((pos + i) % FULL_LOOP_LENGTH);
                    if (types.containsKey(c)) {
                        types.put(c, types.get(c) + 1);
                    } else {
                        types.put(c, 1);
                    }
                }

                if (types.size() == 1) {
                    chests.add(new Chest(chests.size() + 1, types.keySet().iterator().next()));
                } else {
                    Chest chest = new Chest(chests.size() + 1, 'x');
                    for (Map.Entry<Character, Integer> e : types.entrySet()) {
                        chest.addTypeCount(e.getKey(), e.getValue());
                    }
                    chests.add(chest);
                }
            }
        }

        if (mAdapter == null) {
            mAdapter = new ChestAdapter(mContext, chests, false);
            mGridView.setAdapter(mAdapter);
        } else {
            mAdapter.updateChests(chests);
        }

        mGridView.smoothScrollToPosition(mChests.size() + 5);
    }

    private void loadProgress() {
        if (mMatchPositions.size() == 1) {
            Map.Entry<Integer, Integer> matchedEntry = mMatchPositions.iterator().next();
            final Integer finalMatchedPosition = matchedEntry.getKey();
            final Integer finalMatchedLength = matchedEntry.getValue();
            mListener.onMatchPositionApply(finalMatchedPosition, finalMatchedLength);
        }
    }

    private boolean getMatched() {
        final String chests = getChestSequence();

        ChestMatcher matcher = new ChestMatcher(getString(R.string.chest_loop));

        mMatchPositions = matcher.getMatchedPositions(chests).entrySet();

        String matchResult = getString(R.string.matched);
        for (Map.Entry<Integer, Integer> e : mMatchPositions) {
            matchResult += "  " + e.getKey().toString();
        }

        Set<Integer> set = new HashSet<>();
        for (Map.Entry<Integer, Integer> e : mMatchPositions) {
            set.add(e.getKey() % SHORT_LOOP_LENGTH);
        }

        Log.i(TAG, matchResult);

        if (set.size() == 1) {
            mMatchedTextView.setText(matchResult);
            Log.i(TAG, "Short loop @" + set.iterator().next().toString());
            return true;
        } else {
            mMatchedTextView.setText("");
            return false;
        }
    }

    private String getChestSequence() {
        String chests = "";
        for (Chest chest : mChests) {
            switch (chest.getType()) {
                case SILVER:
                    chests += "s";
                    break;
                case GOLDEN:
                    chests += "g";
                    break;
                case GIANT:
                    chests += "G";
                    break;
                case MAGICAL:
                    chests += "m";
                    break;
                default:
                    chests += "s";
            }
        }
        return chests;
    }

    private void clearAll() {
        mChests.clear();
        loadViews();
    }
}
