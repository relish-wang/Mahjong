package wang.relish.mahjong.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import wang.relish.mahjong.R;
import wang.relish.mahjong.activity.abs.AbsGameActivity;
import wang.relish.mahjong.callback.UserCallback;
import wang.relish.mahjong.entity.Game;
import wang.relish.mahjong.entity.Record;
import wang.relish.mahjong.entity.User;
import wang.relish.mahjong.util.ToastUtil;

/**
 * @author Relish Wang
 * @since 2018/02/20
 */
public class MahjongActivity extends AbsGameActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, MahjongActivity.class);
        intent.putExtra("game", Game.MAHJONG);
        context.startActivity(intent);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle("麻将");
    }

    /**
     * 回合结算
     */
    @Override
    protected void settlement() {
        new AlertDialog.Builder(MahjongActivity.this)
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

    private User winner, loser;

    /**
     * 结算
     *
     * @param isZimo 自摸 or 放铳
     */
    private void balance(final boolean isZimo) {
        View view = LayoutInflater.from(MahjongActivity.this).inflate(R.layout.dialog_fangchong, null);
        final TextView tvWinner = view.findViewById(R.id.tv_winner);
        final TextView tvLoser = view.findViewById(R.id.tv_loser);

        tvWinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectUser(new UserCallback() {
                    @Override
                    public void onUserGet(User...user) {
                        winner = user[0];
                        tvWinner.setText(winner.getName());
                    }
                });
            }
        });
        tvLoser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectUser(new UserCallback() {
                    @Override
                    public void onUserGet(User...user) {
                        loser = user[0];
                        tvLoser.setText(loser.getName());
                    }
                });
            }
        });
        tvLoser.setVisibility(isZimo ? View.GONE : View.VISIBLE);
        new AlertDialog.Builder(MahjongActivity.this)
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
}
