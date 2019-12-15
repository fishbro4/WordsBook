package com.example.wordsbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ViewUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.util.List;



public class MainActivity extends AppCompatActivity{

    private MyDataBaseOpenHelper helper;
    private WordDao dao;
    private ListView list;//竖屏的list
    private ListView list2;//横屏的list
    private LinearLayout linearLayout;
    private List<Word> words;
    private MyAdapter myAdapter;
    private MyAdapter_land myAdapter_land;
    private String url="https://openapi.youdao.com/api";
    private String appid="34f9536e316bca2b";
    private String key="5rkjdSWU449W3Zy0R84QvB0YG8SVDq0R";
    private FragmentActivity fragmentActivity;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int orientation=getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main2);
            helper = new MyDataBaseOpenHelper(this);
            helper.getWritableDatabase();
            list2 = findViewById(R.id.list);
//            Word w=new Word();
//            dao=new WordDao(this);
//            w=dao.findfirstword();
//            String firWord=w.getWord();
//            String firMeaning=w.getMeaning();
//            String firSample=w.getSample();
//            final Bundle bundle=new Bundle();
//            bundle.putString("word",firWord);
//            bundle.putString("meaning",firMeaning);
//            bundle.putString("sample",firSample);
//            fragmentActivity=new FragmentActivity();
//            fragmentActivity.setArguments(bundle);
//            FragmentManager fragmentManager=getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.frameLayout,fragmentActivity);
//            fragmentTransaction.commit();
            list2.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            TextView textView=view.findViewById(R.id.item_word);
                            String word=textView.getText().toString();
 //                           Toast.makeText(MainActivity.this,"获取到的word:"+word, Toast.LENGTH_SHORT).show();
                            Word fraWord=new Word();
                            fraWord=dao.findByWord(word);
                            String meaning=fraWord.getMeaning();
                            String sample=fraWord.getSample();
                            Bundle bundle= new Bundle();
                            bundle.putString("word",word);
                            bundle.putString("meaning",meaning);
                            bundle.putString("sample",sample);
                            fragmentActivity=new FragmentActivity();
                            fragmentActivity.setArguments(bundle);
                            FragmentManager fragmentManager=getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frameLayout,fragmentActivity);
                            fragmentTransaction.commit();
//                            getFragmentManager().beginTransaction()
//                                    .replace(R.id.fragment,fragmentActivity)
//                                    .commit();
                        }
                    }
            );
            registerForContextMenu(list2);
            refreshData_land();

        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
            helper = new MyDataBaseOpenHelper(this);
            helper.getWritableDatabase();
            list = findViewById(R.id.list);
            registerForContextMenu(list);
            refreshData();
        }
    }

    private void refreshData_land() {
        dao = new WordDao(this);
        words = dao.findAll();
        if(myAdapter_land==null){
            myAdapter_land=new MyAdapter_land();
            list2.setAdapter(myAdapter_land);
        }else {
            //通知数据适配器更新数据
            myAdapter_land.notifyDataSetChanged();
        }

    }
    private class MyAdapter_land extends BaseAdapter{
        @Override
        public int getCount() {
            return words.size();
        }

        @Override
        public Object getItem(int i) {
            return words.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = null;
            if (view == null) {
                v = View.inflate(MainActivity.this, R.layout.item, null);
            } else {
                v = view;
            }
            TextView textView1 = v.findViewById(R.id.item_word);
            textView1.setText(words.get(i).getWord());
            return v;
        }
    }

    private void refreshData() {
        dao = new WordDao(this);
        words = dao.findAll();
        if(myAdapter==null){
            myAdapter=new MyAdapter();
            list.setAdapter(myAdapter);
        }else {
            //通知数据适配器更新数据
            myAdapter.notifyDataSetChanged();
        }

    }
    private class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return words.size();
        }

        @Override
        public Object getItem(int i) {
            return words.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = null;
            if (view == null) {
                v = View.inflate(MainActivity.this, R.layout.item, null);
            } else {
                v = view;
            }
            TextView textView1 = v.findViewById(R.id.item_word);
            TextView textView2 = v.findViewById(R.id.item_meaning);
            TextView textView3 = v.findViewById(R.id.item_sample);
            textView1.setText(words.get(i).getWord());
            textView2.setText(words.get(i).getMeaning());
            textView3.setText(words.get(i).getSample());
            return v;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
    }
    /**
     * 菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.operation,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.Item1:
                FindDialog();
                return true;
            case R.id.Item2:
                AddDialog();
                return true;
            case R.id.Item3:
                TranDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void FindDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        final View view=LayoutInflater.from(MainActivity.this).inflate(R.layout.find,null);
        final ListView find_list=view.findViewById(R.id.find_list);
        final EditText editText=(EditText)view.findViewById(R.id.editText);
        Button button=view.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword=editText.getText().toString();
                words=dao.findTest(keyword);
                find_list.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return words.size();
                    }

                    @Override
                    public Object getItem(int i) {
                        return words.get(i);
                    }

                    @Override
                    public long getItemId(int i) {
                        return i;
                    }

                    @Override
                    public View getView(int i, View view, ViewGroup viewGroup) {
                        View v = null;
                        if (view == null) {
                            v = View.inflate(MainActivity.this, R.layout.find_item, null);
                        } else {
                            v = view;
                        }
                        TextView textView1 = v.findViewById(R.id.item_word);
                        TextView textView2 = v.findViewById(R.id.item_meaning);
                        TextView textView3 = v.findViewById(R.id.item_sample);
                        textView1.setText(words.get(i).getWord());
                        textView2.setText(words.get(i).getMeaning());
                        textView3.setText(words.get(i).getSample());
                        return v;
                    }
                });
            }
        });
        builder.setView(view).show();
    }
    public void AddDialog(){
        //dao = new WordDao(this);
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        final View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.add,null);
        Button button=view.findViewById(R.id.button);
        final EditText editText1=(EditText)view.findViewById(R.id.editText1);
        final EditText editText2=(EditText)view.findViewById(R.id.editText2);
        final EditText editText3=(EditText)view.findViewById(R.id.editText3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word=editText1.getText().toString();
                String meaning=editText2.getText().toString();
                String sample=editText3.getText().toString();
                dao.add(word,meaning,sample);
                Toast.makeText(MainActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                refreshData();
            }
        });
        builder.setView(view).show();
    }
    public void DeleteDialog(final String strWord){
        //dao = new WordDao(this);
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        final View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.delete,null);
        Button button1=view.findViewById(R.id.delete_button1);
        Button button2=view.findViewById(R.id.delete_button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dao.delete(strWord);
                Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                refreshData();
            }
        });
        builder.setView(view).show();
    }
    public void UpdateDialog(final String strWord, final String strMeaning, final String strSample){
        //dao = new WordDao(this);
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        final View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.updata,null);
        Button button=view.findViewById(R.id.updata_button);
        final EditText editText1=(EditText)view.findViewById(R.id.updata_editText1);
        final EditText editText2=(EditText)view.findViewById(R.id.updata_editText2);
        final EditText editText3=(EditText)view.findViewById(R.id.updata_editText3);
        editText1.setText(strWord);
        editText2.setText(strMeaning);
        editText3.setText(strSample);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id=dao.findId(strWord);
                String word=editText1.getText().toString();
                String meaning=editText2.getText().toString();
                String sample=editText3.getText().toString();
                dao.updata(id,word,meaning,sample);
                Toast.makeText(MainActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                refreshData();
            }
        });
        builder.setView(view).show();
    }
    public void TranDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        final View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.translate,null);
        Button button=view.findViewById(R.id.tran_button);
        final EditText editText=view.findViewById(R.id.tran_editText);
        final TextView textView=view.findViewById(R.id.tran_TextView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Access access =new Access();
                access.YouDao_SetInstance("https://openapi.youdao.com/api","34f9536e316bca2b","5rkjdSWU449W3Zy0R84QvB0YG8SVDq0R");
                access.YouDao_SetTranslation(editText.getText().toString(),"en","zh-CHS");
                JSONObject jsonObject=access.getConnect();
                textView.setText(jsonObject.get("translation").toString());
            }
        });
        builder.setView(view).show();
    }
    /**
     * 上下文菜单
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.contextmenu,menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        TextView word=null;
        TextView meaning=null;
        TextView sample=null;
        AdapterView.AdapterContextMenuInfo info=null;
        View itemView=null;
        switch (item.getItemId()){
            case R.id.Item3:
                info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                itemView = info.targetView;
                word = itemView.findViewById(R.id.item_word);
                if(word !=null){
                    String strWord = word.getText().toString();
                    DeleteDialog(strWord);
                }
                break;
            case R.id.Item4:
                info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                itemView=info.targetView;
                word =(TextView)itemView.findViewById(R.id.item_word);
                meaning =(TextView)itemView.findViewById(R.id.item_meaning);
                sample =(TextView)itemView.findViewById(R.id.item_sample);
                if(word!=null && meaning!=null && sample!=null){
                    String strWord=word.getText().toString();
                    String strMeaning=meaning.getText().toString();
                    String strSample=sample.getText().toString();
                    UpdateDialog(strWord, strMeaning, strSample);
                }
                break;
        }
        return true;
    }

}
