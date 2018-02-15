package wang.relish.mahjong.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import wang.relish.mahjong.callback.UserCallback;
import wang.relish.mahjong.dao.Record;
import wang.relish.mahjong.dao.User;
import wang.relish.mahjong.util.ToastUtil;

public class UserListActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, UserListActivity.class);
        context.startActivity(intent);
    }

    List<User> mUsers = new ArrayList<>();
    private RecyclerView mRvUsers;
    private UsersAdapter mAdapter;
    private Button mBtnTurn;

    private User winner;
    private User loser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBtnTurn = findViewById(R.id.btn_turn);
        mBtnTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectWinMode();
            }
        });

        mRvUsers = findViewById(R.id.rv_users);
        mAdapter = new UsersAdapter();
        mRvUsers.setAdapter(mAdapter);
        mRvUsers.setLayoutManager(new LinearLayoutManager(this));
        loadData();
    }

    private void loadData() {
        mUsers = User.getUsers();
        mAdapter.notifyDataSetChanged();
    }

    private void selectWinMode() {
        new AlertDialog.Builder(UserListActivity.this)
                .setTitle("选择赢局模式")
                .setPositiveButton("放铳", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        balance(false);
                    }
                })
                .setNeutralButton("取消", null)
                .setNegativeButton("自摸", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        balance(true);

                    }
                })
                .show();
    }

    private void balance(final boolean isZimo) {
        View view = LayoutInflater.from(UserListActivity.this).inflate(R.layout.dialog_fangchong, null);
        final TextView tvWinner = view.findViewById(R.id.tv_winner);
        final TextView tvLoser = view.findViewById(R.id.tv_loser);

        tvWinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectUser(new UserCallback() {
                    @Override
                    public void onUserGet(User user) {
                        winner = user;
                        tvWinner.setText(user.getName());
                    }
                });
            }
        });
        tvLoser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectUser(new UserCallback() {
                    @Override
                    public void onUserGet(User user) {
                        loser = user;
                        tvLoser.setText(user.getName());
                    }
                });
            }
        });
        tvLoser.setVisibility(isZimo ? View.GONE : View.VISIBLE);
        new AlertDialog.Builder(UserListActivity.this)
                .setView(view)
                .setPositiveButton("录入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (winner == null) {
                            ToastUtil.show("未填入赢家");
                            return;
                        }
                        if (!isZimo && loser == null) {
                            ToastUtil.show("未填入放铳者");
                            return;
                        }
                        Record record = new Record();
                        record.setCreateTime(System.currentTimeMillis());
                        record.setWinnerId(winner.getId());
                        record.setLoserId(isZimo ? 0 : loser.getId());
                        record.setId(Record.maxId() + 1);
                        boolean save = record.save();
                        if (save) {
                            loadData();
                            winner = null;
                            loser = null;
                        }
                    }
                })
                .show();
    }

    private void selectUser(final UserCallback callback) {

        final List<User> users = User.getUsers();
        int count = users.size();
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            names[i] = users.get(i).getName();
        }
        new AlertDialog.Builder(UserListActivity.this)
                .setSingleChoiceItems(names, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        callback.onUserGet(users.get(which));
                        dialogInterface.dismiss();
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
        if (id == R.id.menu_add_user) {

            final View view = LayoutInflater.from(UserListActivity.this).inflate(R.layout.dialog_add_user, null);
            new AlertDialog.Builder(UserListActivity.this)
                    .setTitle("新建麻友")
                    .setView(view)
                    .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText etUserName = view.findViewById(R.id.et_user_name);
                            String name = etUserName.getText().toString();
                            if (TextUtils.isEmpty(name)) {
                                ToastUtil.show("麻友名不得为空!");
                                return;
                            }
                            User.createUser(name);
                            loadData();
                        }
                    }).setNegativeButton("取消", null)
                    .show();
            return true;
        } else if (id == R.id.menu_reset) {
            if (User.reset()) {
                loadData();
            } else {
                ToastUtil.show("重置失败!");
            }
        }else if(id == R.id.menu_turn){
            RecordListActivity.start(this);
        }

        return super.onOptionsItemSelected(item);
    }


    class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.VHolder> {


        @Override
        public UsersAdapter.VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(UserListActivity.this).inflate(R.layout.item_user, parent, false);
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
                    new AlertDialog.Builder(UserListActivity.this)
                            .setTitle("删除麻友")
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
