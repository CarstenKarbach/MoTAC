package de.karbach.tac.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

/**
 * Shows a list of moves in the game. A move is a played card aloing with a possible color.
 *
 */
public class MoveListFragment extends ListFragment{

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        String[] data= new String[]{"Trickser", "Tac", "Eins"};

        setListAdapter(new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, data) );
    }

}
