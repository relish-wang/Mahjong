package wang.relish.mahjong.activity.abs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wang.relish.mahjong.R;
import wang.relish.mahjong.activity.RecordListActivity;
import wang.relish.mahjong.callback.UserCallback;
import wang.relish.mahjong.entity.Game;
import wang.relish.mahjong.entity.User;
import wang.relish.mahjong.util.ToastUtil;

public abstract class AbsGameActivity extends AppCompatActivity {

    /**
     * 初始化toolbar
     */
    protected abstract void initToolbar(Toolbar toolbar);

    /**
     * 回合结算
     */
    protected abstract void settlement();

    List<User> mUsers = new ArrayList<>();
    private RecyclerView mRvUsers;
    private UsersAdapter mAdapter;
    private Button mBtnTurn;

    @Game
    private int game = Game.UNDEFINED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        initToolbar(toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        game = intent.getIntExtra("game", Game.UNDEFINED);

        mBtnTurn = findViewById(R.id.btn_turn);
        mBtnTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settlement();
            }
        });

        mRvUsers = findViewById(R.id.rv_users);
        mAdapter = new UsersAdapter();
        mRvUsers.setAdapter(mAdapter);
        mRvUsers.setLayoutManager(new LinearLayoutManager(this));
        loadData();
    }

    protected void loadData() {
        mUsers = User.getUsers();
        mAdapter.notifyDataSetChanged();
    }

    protected void selectUser(final UserCallback callback) {

        final List<User> users = User.getUsers();
        int count = users.size();
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            names[i] = users.get(i).getName();
        }
        new AlertDialog.Builder(AbsGameActivity.this)
                .setSingleChoiceItems(names, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        callback.onUserGet(users.get(which));
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    protected void selectUsers(final UserCallback callback) {

        final List<User> users = User.getUsers();
        int count = users.size();
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            names[i] = users.get(i).getName();
        }
        boolean[] booleans = new boolean[count];
        final List<User> redTenOwners = new ArrayList<>();
        new AlertDialog.Builder(AbsGameActivity.this)
                .setMultiChoiceItems(names, booleans, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        User o = users.get(i);
                        if (b) {
                            if (redTenOwners.size() >= 2) {
                                ToastUtil.show("红十最多两个人拥有！");
                                dialogInterface.dismiss();
                                return;
                            }
                            redTenOwners.add(o);
                        } else {
                            if (redTenOwners.contains(o)) {
                                redTenOwners.remove(o);
                            }
                        }
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int size = redTenOwners.size();
                        if (size == 0) {
                            ToastUtil.show("未选择红十拥有者！");
                            return;
                        }
                        if (size > 2) {
                            ToastUtil.show("红十最多两个人拥有！");
                            return;
                        }
                        callback.onUserGet(redTenOwners.toArray(new User[size]));
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.menu_add_user) {
            final View view = LayoutInflater.from(AbsGameActivity.this).inflate(R.layout.dialog_add_user, null);
            new AlertDialog.Builder(AbsGameActivity.this)
                    .setTitle("新建")
                    .setView(view)
                    .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText etUserName = view.findViewById(R.id.et_user_name);
                            String name = etUserName.getText().toString();
                            if (TextUtils.isEmpty(name)) {
                                ToastUtil.show("名字不得为空!");
                                return;
                            }
                            User.createUser(name);
                            loadData();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return true;
        } else if (id == R.id.menu_reset) {
            if (User.reset()) {
                loadData();
            } else {
                ToastUtil.show("重置失败!");
            }
        } else if (id == R.id.menu_turn) {
            RecordListActivity.start(this, game);
        }

        return super.onOptionsItemSelected(item);
    }


    class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.VHolder> {


        @Override
        public UsersAdapter.VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(AbsGameActivity.this).inflate(R.layout.item_user, parent, false);
            return new VHolder(view);
        }

        @Override
        public void onBindViewHolder(UsersAdapter.VHolder holder, int position) {
            User user = mUsers.get(position);
            if (user == null) return;
            final long id = user.getId();
            holder.tv_id.setText(id + "");
            final String name = user.getName();
            holder.tv_name.setText(name);
            holder.tv_score.setText(user.getScore() + "");
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(AbsGameActivity.this)
                            .setTitle("删除")
                            .setMessage("是否删除[" + name + "]?")
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    long score = User.getScoreById(id);
                                    if (score == -1) {
                                        ToastUtil.show("数据库异常");
                                    } else if (score == -2) {
                                        ToastUtil.show("此人不存在");
                                    } else if (score > 0) {
                                        ToastUtil.show("此人账目未清零不可删除!");
                                    } else {
                                        if (User.deleteUser(id)) {
                                            loadData();
                                            ToastUtil.show("删除成功！");
                                        } else {
                                            ToastUtil.show("删除失败！");
                                        }
                                    }
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }

        class VHolder extends RecyclerView.ViewHolder {

            TextView tv_id;
            TextView tv_name;
            TextView tv_score;

            public VHolder(View view) {
                super(view);
                tv_id = view.findViewById(R.id.tv_id);
                tv_name = view.findViewById(R.id.tv_name);
                tv_score = view.findViewById(R.id.tv_score);
            }
        }
    }
}
