package wang.relish.mahjong.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wang.relish.mahjong.R;
import wang.relish.mahjong.activity.abs.AbsGameActivity;
import wang.relish.mahjong.callback.RedTenCallback;
import wang.relish.mahjong.callback.UserCallback;
import wang.relish.mahjong.entity.Game;
import wang.relish.mahjong.entity.Record;
import wang.relish.mahjong.entity.RedTen;
import wang.relish.mahjong.entity.User;
import wang.relish.mahjong.util.ToastUtil;

/**
 * @author Relish Wang
 * @since 2018/02/20
 */
public class RedTenActivity extends AbsGameActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, RedTenActivity.class);
        intent.putExtra("game", Game.RED_TEN);
        context.startActivity(intent);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle("红十");
    }

    private static final String[] RED_TEN = {"平局", "单关", "双关", "三关"};

    private List<User> redTenOwners = new ArrayList<>();
    @RedTen
    private int result = RedTen.DOGFALL;

    @Override
    protected void settlement() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialoag_redten, null);
        final TextView tv_without_red_ten = view.findViewById(R.id.tv_without_red_ten);
        final TextView tv_red_ten_owner = view.findViewById(R.id.tv_red_ten_owner);
        tv_red_ten_owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectUsers(new UserCallback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onUserGet(User... user) {
                        int length = user.length;
                        if (length == 0) {
                            ToastUtil.show("未选择红十拥有者！");
                            return;
                        }
                        if (length > 2) {
                            ToastUtil.show("红十最多两个人拥有！");
                            return;
                        }
                        redTenOwners.addAll(Arrays.asList(user));
                        StringBuilder sb = new StringBuilder(redTenOwners.get(0).getName());
                        if (length > 1) {
                            sb.append("、").append(redTenOwners.get(1).getName());
                        }
                        tv_red_ten_owner.setText(sb.toString());

                        List<User> users = User.getUsers();
                        StringBuilder sbNoRedTen = new StringBuilder();
                        for (User u : users) {
                            if (u.getId() == redTenOwners.get(0).getId()) {
                                continue;
                            }
                            if (redTenOwners.size() > 1 && u.getId() == redTenOwners.get(1).getId()) {
                                continue;
                            }
                            sbNoRedTen.append(u.getName()).append("、");
                        }
                        sbNoRedTen.deleteCharAt(sbNoRedTen.length() - 1);
                        tv_without_red_ten.setText(sbNoRedTen.toString());
                    }
                });
            }
        });
        final TextView tv_red_ten = view.findViewById(R.id.tv_red_ten);
        tv_red_ten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRedTen(new RedTenCallback() {
                    @Override
                    public void onRedTenGet(int redTen) {
                        result = redTen;
                        tv_red_ten.setText(RED_TEN[result]);
                    }
                });
            }
        });
        final Switch st_reverse = view.findViewById(R.id.st_reverse);
        final Switch st_basked = view.findViewById(R.id.st_basked);
        new AlertDialog.Builder(this)
                .setTitle("结算")
                .setView(view)
                .setPositiveButton("录入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Record record = new Record();
                        record.setId(Record.maxId() + 1);
                        record.setCreateTime(System.currentTimeMillis());
                        record.setGame(Game.RED_TEN);
                        record.setWinnerId(redTenOwners.get(0).getId());
                        record.setLoserId(redTenOwners.size() > 1 ? redTenOwners.get(1).getId() : 0);
                        record.setIsReversed(st_reverse.isChecked() ? 1 : 0);
                        record.setIsBasked(st_basked.isChecked() ? 1 : 0);
                        record.setResult(result);
                        boolean save = record.save();

                        if (save) {
                            loadData();
                            redTenOwners.clear();
                            result = RedTen.DOGFALL;
                        }
                    }
                })
                .create()
                .show();
    }

    private void selectRedTen(final RedTenCallback callback) {
        final List<Integer> redTens = new ArrayList<>();
        if (redTenOwners.size() == 1) {
            redTens.add(RedTen.DOGFALL);
            redTens.add(RedTen.TRIPLE_SHUT);
        } else {
            redTens.add(RedTen.DOGFALL);
            redTens.add(RedTen.SINGLE_SHUT);
            redTens.add(RedTen.DOUBLE_SHUT);
        }
        int count = redTens.size();
        String[] names = new String[count];
        if (redTenOwners.size() == 1) {
            names[0] = "平局";
            names[1] = "三关";
        } else if (redTenOwners.size() == 2) {
            names[0] = "平局";
            names[1] = "单关";
            names[2] = "双关";
        } else {
            ToastUtil.show("红十最多两个人拥有！");
            throw new RuntimeException("红十最多两个人拥有！");
        }
        new AlertDialog.Builder(RedTenActivity.this)
                .setSingleChoiceItems(names, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        callback.onRedTenGet(redTens.get(which));
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }
}
