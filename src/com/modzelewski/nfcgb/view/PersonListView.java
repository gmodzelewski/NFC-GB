package com.modzelewski.nfcgb.view;

import android.content.ClipData;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.controller.PersonAdapter;
import com.modzelewski.nfcgb.model.Person;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

/**
 * Created with IntelliJ IDEA.
 * User: melitta
 * Date: 11.03.13
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
public class PersonListView {

    private final ListView personsLV;

    public PersonListView(ListView personsLV) {
        this.personsLV = personsLV;
    }

    /**
     * Create list referencing at persons in background model.
     *
     * @param databaseHelper
     * @param context
     * @param model
     * @return
     */
    public ListView create(final BackgroundModel model, Context context, DatabaseHelper databaseHelper, PersonAdapter pa) {
        personsLV.setAdapter(pa);

        // On long Click: Initiate Drag
        personsLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
                Person person = model.persons.get(position);
                ClipData dragData = ClipData.newPlainText(person.getClass().getSimpleName(), String.valueOf(person.getId()));
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                l.startDrag(dragData, // the data to be dragged
                        shadowBuilder, // the drag shadow builder
                        null, // no need to use local data
                        0 // flags (not currently used, set to 0)
                );
                return false;
            }
        });
//      // On normal Click: Context Menu
//      personsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//          @Override
//          public void onItemClick(AdapterView<?> l, View v, int position, long id) {
//              MainActivity.class.getActivity().openContextMenu(v);
//          }
//      });
        return personsLV;
    }
}
