package com.sigdue.ui;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.support.v7.widget.AppCompatSpinner;
import android.widget.SpinnerAdapter;

import com.sigdue.R;

import java.util.ArrayList;
import java.util.List;

public class SearchableSpinner extends AppCompatSpinner implements View.OnTouchListener,
        SearchableListDialog.SearchableItem {

    private static final int NO_ITEM_SELECTED = -1;
    private Context _context;
    private List _items;
    private SearchableListDialog _searchableListDialog;

    private boolean _isDirty;
    private ArrayAdapter _arrayAdapter;
    private String _strHintText;
    private boolean _isFromInit;
    public SearchableSpinner(Context context) {
        super(context);
        try {
            this._context = context;
            init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SearchableSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            this._context = context;
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SearchableSpinner);
            final int N = a.getIndexCount();
            for (int i = 0; i < N; ++i) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.SearchableSpinner_hintText) {
                    _strHintText = a.getString(attr);
                }
            }
            a.recycle();
            init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SearchableSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this._context = context;
        init();
    }

    private void init() {
        try {
            _items = new ArrayList();
            _searchableListDialog = SearchableListDialog.newInstance(_items);
            _searchableListDialog.setOnSearchableItemClickListener(this);
            setOnTouchListener(this);

            _arrayAdapter = (ArrayAdapter) getAdapter();
            if (!TextUtils.isEmpty(_strHintText)) {
                ArrayAdapter arrayAdapter = new ArrayAdapter(_context, android.R.layout.simple_list_item_1, new String[]{_strHintText});
                _isFromInit = true;
                setAdapter(arrayAdapter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            if (event.getAction() == MotionEvent.ACTION_UP) {

                if (null != _arrayAdapter) {
                    _items.clear();
                    for (int i = 0; i < _arrayAdapter.getCount(); i++) {
                        _items.add(_arrayAdapter.getItem(i));
                    }
                    _searchableListDialog.show(scanForActivity(_context).getFragmentManager(), "TAG");
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        try {
            if (!_isFromInit) {
                _arrayAdapter = (ArrayAdapter) adapter;
                if (!TextUtils.isEmpty(_strHintText) && !_isDirty) {
                    ArrayAdapter arrayAdapter = new ArrayAdapter(_context, android.R.layout
                            .simple_list_item_1, new String[]{_strHintText});
                    super.setAdapter(arrayAdapter);
                } else {
                    super.setAdapter(adapter);
                }

            } else {
                _isFromInit = false;
                super.setAdapter(adapter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setAdapter(SpinnerAdapter adapter, Object item) {
        try
        {
            _isFromInit = false;
            _isDirty=true;
            _arrayAdapter = (ArrayAdapter) adapter;
            super.setAdapter(_arrayAdapter);
            int pos=_arrayAdapter.getPosition(item);
            setSelection(pos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onSearchableItemClicked(Object item) {
        try {
            setSelection(_items.indexOf(item));
            if (!_isDirty) {
                _isDirty = true;
                setAdapter(_arrayAdapter);
                setSelection(_items.indexOf(item));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setTitle(String strTitle) {
        _searchableListDialog.setTitle(strTitle);
    }

    public void setPositiveButton(String strPositiveButtonText) {
        _searchableListDialog.setPositiveButton(strPositiveButtonText);
    }

    private Activity scanForActivity(Context cont) {
        try {
            if (cont == null)
                return null;
            else if (cont instanceof Activity)
                return (Activity) cont;
            else if (cont instanceof ContextWrapper)
                return scanForActivity(((ContextWrapper) cont).getBaseContext());
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public int getSelectedItemPosition() {
        try {
            if (!TextUtils.isEmpty(_strHintText) && !_isDirty) {
                return NO_ITEM_SELECTED;
            } else {
                return super.getSelectedItemPosition();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    @Override
    public Object getSelectedItem() {
        try {
            if (!TextUtils.isEmpty(_strHintText) && !_isDirty) {
                return null;
            } else {
                return super.getSelectedItem();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}