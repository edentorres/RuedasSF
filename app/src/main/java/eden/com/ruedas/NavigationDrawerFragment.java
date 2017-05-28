package eden.com.ruedas;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {

    private RecyclerView recyclerView;
    public static final String PREF_FILE_NAME = "testpref";
    public static final String KEY_USER_LEARNED_DRAWER="user_learned_drawer";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Adapter adapter;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View containerView;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer=Boolean.getBoolean(readFromPreferencea(getActivity(),KEY_USER_LEARNED_DRAWER,"false"));
        mUserLearnedDrawer=true;
        if (savedInstanceState!=null){
            mFromSavedInstanceState=true;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView =(RecyclerView) layout.findViewById(R.id.drawerlist);
        adapter= new Adapter(getActivity(),getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        layout.findViewById(R.id.te).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapaActivity.class);
                startActivity(intent);
            }
        });
        return layout ;
    }
    public List<Information> getData(){
        List<Information> data=new ArrayList<>();
        int[] icons={R.drawable.ic_gps_fixed_black_24dp, R.drawable.ic_gps_fixed_black_24dp};
        String title="Ver Mapa"; // si quiero mas array

        Information current=new Information();

        for(int i=0; i<icons.length;i++) {
            current.iconID = icons[i];
            current.title = title;
            data.add(current);
        }
        return data;

    }

    public void setUp(int fragmentId,DrawerLayout drawerLayout, Toolbar toolbar) {
        containerView=getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!mUserLearnedDrawer){
                    mUserLearnedDrawer=true;
                    setToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }
                getActivity().invalidateOptionsMenu();

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();

            }
        };
        if (!mUserLearnedDrawer&&!mFromSavedInstanceState){
            mDrawerLayout.openDrawer(containerView);
        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static void setToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferencea(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }


}