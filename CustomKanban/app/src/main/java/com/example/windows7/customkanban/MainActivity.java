package com.example.windows7.customkanban;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MainActivity extends ActionBarActivity {


    private ArrayList<Sticker> listPlaned;
    private ArrayList<Sticker> listInProgress;
    private ArrayList<Sticker> listCompleted;
    private ArrayList<Sticker> listCanceled;

    private  ListView lvPlaned;
    private  ListView lvInProgress;
    private  ListView lvCompleted;
    private  ListView lvCanceled;

    private String FILE_PLAN = "fileplan";
    private String FILE_INPROGRESS = "fileinprogress";
    private String FILE_COMPLETED = "filecompleted";
    private String FILE_CANCELED = "filecanceled";


    protected TableLayout layout;

    InputStream inStream;
    InputStreamReader sr;
    OutputStreamWriter sw;

    //считывание стикеров при запуске
    public void readVariables(String filename,  List<Sticker> list) {

        try {
            inStream = openFileInput(filename);
            sr = new InputStreamReader(inStream);

            BufferedReader reader = new BufferedReader(sr);
            String str;
            StringBuffer buffer = new StringBuffer();

            while ((str = reader.readLine()) != null) {

                String title = str;
                String description = reader.readLine();
                String readedPriority = reader.readLine();
                int priority = Integer.parseInt(readedPriority);

                String date = reader.readLine();
                Sticker sticker = new Sticker(title, description, priority);
                sticker.setDate(date);

                if (list == listPlaned) {
                    sticker.setLocation(CURRENT_LOCATON.Planed);
                } else if (list == listInProgress) {
                    sticker.setLocation(CURRENT_LOCATON.InProgress);
                } else if (list == listCompleted) {
                    sticker.setLocation(CURRENT_LOCATON.Compleated);
                } else if (list == listCanceled) {
                    sticker.setLocation(CURRENT_LOCATON.Canceled);
                }

                list.add(sticker);
            }
                   /*
                *   Sticker sticker = list.get(i);
                sw.write(sticker.getTitle()+'\n');
                sw.write(sticker.getDescription()+'\n');
                sw.write(sticker.getPriority()+'\n');
                sw.write(sticker.getCreationDate()+'\n');
                * */
            inStream.close();
        } catch (Exception e) {
        }
    }
    //запись стикеров при паузе
    public void writeVariables(String filename, List<Sticker> list)
    {
        OutputStream os = null;
        try {
            os = openFileOutput(filename, 0);
            sw = new OutputStreamWriter(os);

            for (int i = 0; i< list.size(); i++)
            {
                Sticker sticker = list.get(i);
                sw.write(sticker.getTitle()+'\n');
                sw.write(sticker.getDescription()+'\n');
                String priority = sticker.getPriority()+'\n';
                sw.write(sticker.getPriority()+'\n');
                sw.write(sticker.getCreationDate()+'\n');
            }

        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        finally {
            if(sw!=null)
            {
                try {
                    sw.close();
                }
                catch(IOException ex) {ex.printStackTrace();}
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


            writeVariables(FILE_PLAN, listPlaned);
            writeVariables(FILE_INPROGRESS, listInProgress);
            writeVariables(FILE_COMPLETED, listCompleted);
            writeVariables(FILE_CANCELED, listCanceled);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //устанавливаем полноэкранный режим
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.board);

        //инициализация переменных
        listPlaned = new ArrayList<Sticker>();
        listInProgress = new ArrayList<Sticker>();
        listCompleted = new ArrayList<Sticker>();
        listCanceled = new ArrayList<Sticker>();

        lvPlaned = (ListView) findViewById(R.id.lv_planed);
        lvInProgress = (ListView) findViewById(R.id.lv_inrogress);
        lvCompleted = (ListView) findViewById(R.id.lv_completed);
        lvCanceled = (ListView) findViewById(R.id.lv_canceled);

        layout = (TableLayout)findViewById(R.id.board);


        registerForContextMenu(lvPlaned);

        //заполняем списки
        fillList();
        fillListView();

        //привязываем контектсное меню
        registerForContextMenu(layout);

    }

    private void fillList() {

        readVariables(FILE_PLAN, listPlaned);
        readVariables(FILE_INPROGRESS, listInProgress);
        readVariables(FILE_COMPLETED, listCompleted);
        readVariables(FILE_CANCELED, listCanceled);

    }

    private void fillListView() {
        ArrayAdapter<Sticker> stickersAdapter = new StickerAdapter(listPlaned);
        lvPlaned.setAdapter(stickersAdapter);

        stickersAdapter = new StickerAdapter(listInProgress);
        lvInProgress.setAdapter(stickersAdapter);

        stickersAdapter = new StickerAdapter(listCompleted);
        lvCompleted.setAdapter(stickersAdapter);

        stickersAdapter = new StickerAdapter(listCanceled);
        lvCanceled.setAdapter(stickersAdapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

       menu.add("Создать новый стикер");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle() == "Создать новый стикер")
        {
           addSticker();
        }
        return super.onContextItemSelected(item);
    }

    //диалоговое окно для создания стикера
    private void addSticker()
    {
        final  Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialoglayout);

        final EditText title = (EditText) dialog.findViewById(R.id.et_title);
        final EditText description = (EditText) dialog.findViewById(R.id.et_description);
        final RatingBar priority = (RatingBar) dialog.findViewById(R.id.rb_priority);
        Button btn_save = (Button) dialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stickerTitle = title.getText().toString();
                String stickerDescription = description.getText().toString();
                int stickerPriority = (int) priority.getRating();

                Sticker newSticker = new Sticker(stickerTitle, stickerDescription, stickerPriority);
                listPlaned.add(newSticker);
                fillListView();

                dialog.cancel();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private class StickerAdapter extends ArrayAdapter<Sticker> {

        List<Sticker> collection;

        public StickerAdapter(List<Sticker> collection) {
            super(MainActivity.this, R.layout.new_row, collection);
            this.collection = collection;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View item = convertView;
            if (item == null) {
                item = getLayoutInflater().inflate(R.layout.new_row, parent, false);
            }

            final Sticker sticker = collection.get(position);

            TextView tvTitle = (TextView) item.findViewById(R.id.title);
            tvTitle.setText(sticker.getTitle());

            final TextView tvDescription = (TextView) item.findViewById(R.id.description);
            tvDescription.setText(sticker.getDescription());

            TextView tvCreation = (TextView) item.findViewById(R.id.creationdate);
            tvCreation.setText(sticker.getCreationDate());

            TextView tvPriority = (TextView) item.findViewById(R.id.priority);
            tvPriority.setText("Приоритет "+sticker.getPriority()+"");

            final Button btn_remove = (Button)item.findViewById(R.id.remove);
            btn_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sticker.getLocation() == CURRENT_LOCATON.Compleated ||
                            sticker.getLocation() == CURRENT_LOCATON.Canceled
                            ) {
                        delete(sticker);
                    } else {
                        Toast.makeText(getContext(), "Удалять можно только в \" Выполненные\"" +
                                "или в \" Отклоненные\"", Toast.LENGTH_LONG).show();
                    }

                }
            });

            final Button btn_edit = (Button)item.findViewById(R.id.edit);
            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(sticker.getLocation() == CURRENT_LOCATON.InProgress ||
                       sticker.getLocation() == CURRENT_LOCATON.Planed
                            ) {
                        edit(sticker);
                    }
                    else
                    {
                        Toast.makeText( getContext(), "Редактировать можно только в \" Запланирвоанные\"" +
                                "или в \" В процессе\"", Toast.LENGTH_LONG).show();
                    }
                }
            });

            final Button btn_cancel = (Button)item.findViewById(R.id.cancel);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(sticker.getLocation() == CURRENT_LOCATON.InProgress ||
                            sticker.getLocation() == CURRENT_LOCATON.Planed
                            ) {
                        decline(sticker);
                    }
                    else
                    {
                        Toast.makeText( getContext(), "Отклонять можно только в \" Запланирвоанные\"" +
                                "или в \" В процессе\"", Toast.LENGTH_LONG).show();
                    }
                }
            });
            ImageButton moveRight = (ImageButton) item.findViewById(R.id.moveright);
            moveRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    move(sticker, false);

                }
            });

            ImageButton moveLeft = (ImageButton) item.findViewById(R.id.moveleft);
            moveLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    move(sticker, true);

                }
            });

            final ImageButton lvImageButton = (ImageButton) item.findViewById(R.id.showdescription);

            lvImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (tvDescription.getTextSize() == 0) {
                        tvDescription.setTextSize(15);
                        lvImageButton.setImageResource(android.R.drawable.arrow_up_float);

                        switch(sticker.getLocation())
                        {
                            case Planed:
                                btn_cancel.setVisibility(View.VISIBLE);
                                btn_edit.setVisibility(View. VISIBLE);
                                btn_remove.setVisibility(View.GONE);
                                break;
                            case InProgress:
                                btn_cancel.setVisibility(View.VISIBLE);
                                btn_edit.setVisibility(View. VISIBLE);
                                btn_remove.setVisibility(View.GONE);
                                break;
                            case Compleated:
                                btn_cancel.setVisibility(View.GONE);
                                btn_edit.setVisibility(View. GONE);
                                btn_remove.setVisibility(View.VISIBLE);
                                break;
                            case Canceled:
                                btn_cancel.setVisibility(View.GONE);
                                btn_edit.setVisibility(View. GONE);
                                btn_remove.setVisibility(View.VISIBLE);
                                break;
                        }

                    } else {
                        tvDescription.setTextSize(0);
                        lvImageButton.setImageResource(android.R.drawable.arrow_down_float);
                        btn_remove.setVisibility(View.GONE);
                        btn_edit.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);

                    }

                }
            });

            return item;
        }

        //перемещение стикера
        private void move(Sticker sticker, boolean moveLeft) {
            if (moveLeft && sticker.getLocation() == CURRENT_LOCATON.Planed) return;
            if (collection.size() == 0) return;

            //изменяем текущее местонахождение стикера
            // сортируем список
            switch (sticker.getLocation()) {
                case Planed:
                    if (!moveLeft) {
                        listPlaned.remove(sticker);
                        sticker.setLocation(CURRENT_LOCATON.InProgress);
                        listInProgress.add(sticker);
                        Collections.sort(listInProgress);
                    }
                    break;
                case InProgress:
                    if (moveLeft) {
                        listInProgress.remove(sticker);
                        sticker.setLocation(CURRENT_LOCATON.Planed);
                        listPlaned.add(sticker);
                        Collections.sort(listPlaned);

                    } else {
                        listInProgress.remove(sticker);
                        sticker.setLocation(CURRENT_LOCATON.Compleated);
                        listCompleted.add(sticker);
                        Collections.sort(listCompleted);
                    }
                    break;
                case Compleated:

                    if (moveLeft) {
                        listCompleted.remove(sticker);
                        sticker.setLocation(CURRENT_LOCATON.InProgress);
                        listInProgress.add(sticker);
                        Collections.sort(listInProgress);
                    }
            }

            fillListView();
            // notifyDataSetChanged();
        }
        //редактирование
        private void edit(final Sticker sticker) {
          final  Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Translucent);
            dialog.setContentView(R.layout.dialoglayout);

            final EditText title = (EditText) dialog.findViewById(R.id.et_title);
            final EditText description = (EditText) dialog.findViewById(R.id.et_description);
            final RatingBar priority = (RatingBar) dialog.findViewById(R.id.rb_priority);
            Button btn_save = (Button) dialog.findViewById(R.id.btn_save);
            Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);


                title.setText(sticker.getTitle());
                description.setText(sticker.getDescription());
                priority.setRating(Integer.parseInt(sticker.getPriority()));


            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String stickerTitle = title.getText().toString();
                    String stickerDescription = description.getText().toString();
                    int stickerPriority = (int) priority.getRating();

                        sticker.setTitle(stickerTitle);
                        sticker.setDescription(stickerDescription);
                        sticker.setPriority(stickerPriority);

                    notifyDataSetChanged();
                    dialog.cancel();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            if(sticker != null && sticker.getLocation() == CURRENT_LOCATON.InProgress)
            {
                TextView tv_title = (TextView)dialog.findViewById(R.id.dialog_title);
                tv_title.setVisibility(View.GONE);
                TextView tv_description = (TextView)dialog.findViewById(R.id.dialog_description);
                tv_description.setVisibility(View.GONE);

                title.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
            }
            dialog.show();
        }
        //помещение стикера в отклоненные
        private void decline(Sticker sticker)
        {
           this.collection.remove(sticker);
            sticker.setLocation(CURRENT_LOCATON.Canceled);
           listCanceled.add(sticker);
            notifyDataSetChanged();
        }
        //удаление стикера
        private void delete(Sticker sticker)
        {
            collection.remove(sticker);
            notifyDataSetChanged();
        }
    }


}
