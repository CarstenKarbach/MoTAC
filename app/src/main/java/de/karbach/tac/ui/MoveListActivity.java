package de.karbach.tac.ui;

import android.support.v4.app.Fragment;

import de.karbach.tac.ui.fragments.MoveListFragment;

/**
 * Created by carsten on 02.10.15.
 */
public class MoveListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new MoveListFragment();
    }
}
