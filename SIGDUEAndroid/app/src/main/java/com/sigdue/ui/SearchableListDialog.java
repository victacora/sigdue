package com.sigdue.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.sigdue.R;

import java.io.Serializable;
import java.util.List;

public class SearchableListDialog extends DialogFragment implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String ITEMS = "items";

    private ArrayAdapter listAdapter;

    private ListView _listViewItems;

    private SearchableItem _searchableItem;

    private OnSearchTextChanged _onSearchTextChanged;

    private SearchView _searchView;

    private String _strTitle;

    private String _strPositiveButtonText;

    private DialogInterface.OnClickListener _onClickListener;

    public SearchableListDialog() {

    }

    public static SearchableListDialog newInstance(List items) {
        try {
            SearchableListDialog multiSelectExpandableFragment = new
                    SearchableListDialog();

            Bundle args = new Bundle();
            args.putSerializable(ITEMS, (Serializable) items);

            multiSelectExpandableFragment.setArguments(args);

            return multiSelectExpandableFragment;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams
                    .SOFT_INPUT_STATE_HIDDEN);
            return super.onCreateView(inflater, container, savedInstanceState);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            if (null != savedInstanceState) {
                _searchableItem = (SearchableItem) savedInstanceState.getSerializable("item");
            }

            View rootView = inflater.inflate(R.layout.searchablespinner_list_dialog, null);
            setData(rootView);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setView(rootView);

            String strPositiveButton = _strPositiveButtonText == null ? "Cerrar" : _strPositiveButtonText;
            alertDialog.setPositiveButton(strPositiveButton, _onClickListener);

            String strTitle = _strTitle == null ? "Seleccione un elemento" : _strTitle;
            alertDialog.setTitle(strTitle);

            final AlertDialog dialog = alertDialog.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                    .SOFT_INPUT_STATE_HIDDEN);
            return dialog;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("item", _searchableItem);
        super.onSaveInstanceState(outState);
    }

    public void setTitle(String strTitle) {
        _strTitle = strTitle;
    }

    public void setPositiveButton(String strPositiveButtonText) {
        _strPositiveButtonText = strPositiveButtonText;
    }

    public void setPositiveButton(String strPositiveButtonText, DialogInterface.OnClickListener onClickListener) {
        _strPositiveButtonText = strPositiveButtonText;
        _onClickListener = onClickListener;
    }

    public void setOnSearchableItemClickListener(SearchableItem searchableItem) {
        this._searchableItem = searchableItem;
    }

    public void setOnSearchTextChangedListener(OnSearchTextChanged onSearchTextChanged) {
        this._onSearchTextChanged = onSearchTextChanged;
    }

    private void setData(View rootView) {
        try {
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context
                    .SEARCH_SERVICE);

            _searchView = (SearchView) rootView.findViewById(R.id.search);
            _searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName
                    ()));
            _searchView.setIconifiedByDefault(false);
            _searchView.setOnQueryTextListener(this);
            _searchView.setOnCloseListener(this);
            _searchView.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(_searchView.getWindowToken(), 0);

            List items = (List) getArguments().getSerializable(ITEMS);

            _listViewItems = (ListView) rootView.findViewById(R.id.listItems);

            listAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                    items);

            _listViewItems.setAdapter(listAdapter);

            _listViewItems.setTextFilterEnabled(true);

            _listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    _searchableItem.onSearchableItemClicked(listAdapter.getItem(position));
                    getDialog().dismiss();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        _searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        try {
            if (TextUtils.isEmpty(s)) {
                ((ArrayAdapter) _listViewItems.getAdapter()).getFilter().filter(null);
            } else {
                ((ArrayAdapter) _listViewItems.getAdapter()).getFilter().filter(s);
            }
            if (null != _onSearchTextChanged) {
                _onSearchTextChanged.onSearchTextChanged(s);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public interface SearchableItem<T> extends Serializable {
        void onSearchableItemClicked(T item);
    }

    public interface OnSearchTextChanged {
        void onSearchTextChanged(String strText);
    }
}
