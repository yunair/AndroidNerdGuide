package com.air.criminalintent;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.air.criminalintent.model.Crime;
import com.air.criminalintent.model.CrimeLab;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CrimeListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CrimeListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CrimeListFragment extends ListFragment {
    private static final String TAG = "CrimeListFragment";

    private static final int REQUEST_CRIME = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private List<Crime> mCrimes;
    private boolean mSubtitleVisible;

    public CrimeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mSubtitleVisible = false;
        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.getInstance(getActivity()).getCrimes();

        CrimeAdapter mCrimeAdapter = new CrimeAdapter(mCrimes);
        setListAdapter(mCrimeAdapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Crime c = ((CrimeAdapter) getListAdapter()).getItem(position);

        Intent intent = new Intent(getActivity(), CrimePageActivity.class);
        intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
        startActivityForResult(intent, REQUEST_CRIME);
    }

    static class ViewHolder{
        TextView mTitle;
        TextView mDate;
        CheckBox mSolved;
    }
    private class CrimeAdapter extends ArrayAdapter<Crime>{

        public CrimeAdapter( List<Crime> objects) {
            super(getActivity(), 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            if(convertView == null) {
                convertView = getActivity().getLayoutInflater().
                        inflate(R.layout.list_item_crime, parent, false);
                viewHolder = new ViewHolder();

                viewHolder.mTitle = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
                viewHolder.mDate = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
                viewHolder.mSolved = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
                convertView.setTag(viewHolder);

            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Crime c = getItem(position);

            viewHolder.mTitle.setText(c.getTitle());
            viewHolder.mDate.setText(c.getDate().toString());
            viewHolder.mSolved.setChecked(c.isSolved());

            return convertView;
        }

    }

    @TargetApi(11)
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View v = super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (mSubtitleVisible) {
                getActivity().getActionBar().setSubtitle(R.string.subtitle);
            }
        }

        Button mAddCrimeButton = (Button) v.findViewById(R.id.btn_add_crime);
        mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewCrime();
            }
        });
        //getListView() returns null until after onCreateView(...) returns.
        ListView listView = (ListView) v.findViewById(android.R.id.list);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // Use floating context menus on Froyo and Gingerbread
            registerForContextMenu(listView);
        } else {
            // Use contextual action bar on Honeycomb and higher
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    final MenuInflater menuInflater = actionMode.getMenuInflater();
                    menuInflater.inflate(R.menu.crime_list_item_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    final int id = menuItem.getItemId();
                    switch (id) {
                        case R.id.menu_item_delete_crime :
                            CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
                            CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
                            for(int i = adapter.getCount() - 1; i >= 0; i--){
                                if(getListView().isItemChecked(i)){
                                    crimeLab.deleteCrime(adapter.getItem(i));
                                }
                            }
                            actionMode.finish();
                            adapter.notifyDataSetChanged();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {

                }
            });
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position = menuInfo.position;
        CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
        Crime c = adapter.getItem(position);
        final int id = item.getItemId();
        switch (id) {
            case R.id.menu_item_delete_crime :
                CrimeLab.getInstance(getActivity()).deleteCrime(c);
                return  true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if(mSubtitleVisible && subtitleItem != null) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }
    }

    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id){
            case R.id.menu_item_new_crime :
                createNewCrime();
                return true;
            case R.id.menu_item_show_subtitle :
                final ActionBar actionBar = getActivity().getActionBar();
                final CharSequence subtitle = actionBar.getSubtitle();
                if(subtitle == null){
                    actionBar.setSubtitle(R.string.subtitle);
                    item.setTitle(R.string.hide_subtitle);
                    mSubtitleVisible = true;
                }else {
                    actionBar.setSubtitle(null);
                    item.setTitle(R.string.show_subtitle);
                    mSubtitleVisible = false;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewCrime() {
        Crime crime = new Crime();
        CrimeLab.getInstance(getActivity()).addCrime(crime);
        Intent intent = new Intent(getActivity(), CrimePageActivity.class);
        intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
        startActivityForResult(intent, 0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CRIME){

        }
    }

}
