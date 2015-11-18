package de.karbach.tac.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.karbach.tac.R;
import de.karbach.tac.core.BoardData;
import de.karbach.tac.core.Move;
import de.karbach.tac.ui.ColorToBallImage;
import de.karbach.tac.ui.ExportMovesTask;

/**
 * Shows a list of images, which were exported from the game.
 *
 */
public class ExportedImagesFragment extends ListFragment{

    /**
     * The data model, which has to be shown
     */
    private List<String> files;

    /**
     * Adapter for showing moves
     */
    private class ExportImageAdapter extends ArrayAdapter<String> {

        public ExportImageAdapter(List<String> files) {
            super(getActivity(), 0, files);


        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.image_item, null);
            }

            String filepath = getItem(position);

            ImageView thumbnailView = (ImageView) convertView.findViewById(R.id.image_preview);

            TextView dateText = (TextView) convertView.findViewById(R.id.text_date);
            TextView partText = (TextView) convertView.findViewById(R.id.text_part);
            TextView gameIDText = (TextView) convertView.findViewById(R.id.text_id);

            dateText.setText(ExportMovesTask.getDateFromFilename(filepath, true));
            partText.setText(ExportMovesTask.getPartIDFromFilename(filepath)+"/"+
                    ExportMovesTask.getPartcountForFilename(filepath, getActivity()));
            gameIDText.setText(ExportMovesTask.getGameIDFromFilename(filepath));

            return convertView;
        }
    }

    public ExportedImagesFragment(){
        setRetainInstance(true);
        files = new ArrayList<String>();
    }

    /**
     * Set the paths to the files to display in this fragment
     * @param files string list, where each entry is the absolute path to the file containing the image
     */
    public void setFiles(List<String> files){
        this.files = files;
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        setListAdapter(new ExportImageAdapter(files) );
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String file = files.get(position);
        File f = new File(file);
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(f), "image/png");
        getActivity().startActivity(intent);
    }
}
