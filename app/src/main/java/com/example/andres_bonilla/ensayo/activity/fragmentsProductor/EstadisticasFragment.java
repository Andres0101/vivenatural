package com.example.andres_bonilla.ensayo.activity.fragmentsProductor;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.HomeProductor;

import java.util.ArrayList;
import java.util.List;

public class EstadisticasFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String nombreDelProductor;

    public EstadisticasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_estadisticas, container, false);
        setHasOptionsMenu(true);

        // Obtiene el nombre de la persona que inicia sesión.
        nombreDelProductor = getArguments().getString("nombreDelProductor");

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        // Inflate the layout for this fragment
        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new MisProductos(), "Mis productos");
        adapter.addFragment(new TodosProductos(), "Todos los productos");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    // Display first fragment
                    Fragment fragment = new MisProductos();
                    // Pass extra values using bundle extras as arguments.
                    Bundle args = new Bundle();
                    args.putString("nombreDelProductor", nombreDelProductor);
                    fragment.setArguments(args);
                    return fragment;
                case 1:
                    return new TodosProductos();
                default:
                    return new MisProductos();
            }
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

