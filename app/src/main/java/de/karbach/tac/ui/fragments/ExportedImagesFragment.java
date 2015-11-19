package de.karbach.tac.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

            Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(filepath), 100, 100);
            thumbnailView.setImageBitmap(resized);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        ListView listview = (ListView) result.findViewById(android.R.id.list);
        registerForContextMenu(listview);

        return result;
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getActivity().getMenuInflater().inflate(R.menu.menu_exportedimages, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;

        if(item.getItemId() == R.id.menu_exportedimages_delete){
            String path = files.get(position);
            if(path == null){
                return true;
            }
            File file = new File(path);
            file.delete();

            files.remove(position);

            ExportImageAdapter adapter = (ExportImageAdapter)getListAdapter();
            adapter.notifyDataSetChanged();
        }

        return super.onContextItemSelected(item);
    }
}
