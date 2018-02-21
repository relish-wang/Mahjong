package wang.relish.mahjong.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wang.relish.mahjong.R;
import wang.relish.mahjong.entity.Game;
import wang.relish.mahjong.entity.Record;
import wang.relish.mahjong.entity.RedTen;
import wang.relish.mahjong.entity.User;
import wang.relish.mahjong.util.TimeUtl;

public class RecordListActivity extends AppCompatActivity {

    private RecyclerView mRvRecords;
    private List<Record> mRecords = new ArrayList<>();
    private RecordAdapter mAdapter;


    public static void start(Context context, int game) {
        Intent intent = new Intent(context, RecordListActivity.class);
        intent.putExtra("game", game);
        context.startActivity(intent);
    }

    public static final String[] RESULT = {"平局", "单关", "双关", "三关"};

    /**
     * 游戏
     */
    private int game = Game.UNDEFINED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("对局明细");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        game = intent.getIntExtra("game", Game.UNDEFINED);

        mRvRecords = findViewById(R.id.rv_records);
        mAdapter = new RecordAdapter();
        mAdapter.notifyDataSetChanged();
        mRvRecords.setAdapter(mAdapter);
        mRvRecords.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    public void loadData() {
        mRecords = Record.getRecords(game);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.VHolder> {

        @Override
        public VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
            return new VHolder(view);
        }

        @Override
        public void onBindViewHolder(VHolder holder, int position) {
            Record record = mRecords.get(position);
            boolean dayFirstTime = isDayFirstTime(position);
            holder.tv_day.setVisibility(dayFirstTime ? View.VISIBLE : View.GONE);
            holder.tv_day.setText(TimeUtl.getyyyyMMdd(record.getCreateTime()));
            holder.tv_time.setText(TimeUtl.getHHmm(record.getCreateTime()));
            holder.tv_record.setText(getRecordStr(record));

            if (game == Game.RED_TEN) {
                int isBasked = record.getIsBasked();
                holder.tv_basked.setVisibility(isBasked == 1 ? View.VISIBLE : View.GONE);
                long loserId = record.getLoserId();
                holder.tv_double_red_ten.setVisibility(loserId == 0 ? View.VISIBLE : View.GONE);
                int isReversed = record.getIsReversed();
                holder.tv_reversed.setVisibility(isReversed == 1 ? View.VISIBLE : View.GONE);

                int result = record.getResult();
                holder.tv_value.setVisibility(result == RedTen.DOGFALL ? View.GONE : View.VISIBLE);

                int value = 0;
                int rate = 1 << isBasked << (isBasked == 1 ? isReversed : 0);
                if (loserId == 0) {
                    int realResult = result == RedTen.TRIPLE_SHUT ? result : 0;
                    if (isReversed == 0) {
                        value = rate * realResult * 3;
                    } else {
                        value = rate * result;
                    }
                } else {
                    value = rate * result;
                }
                holder.tv_value.setText(value >= 0 ? "+" + value : value + "");
                holder.tv_value.setTextColor(ContextCompat.getColor(RecordListActivity.this,isReversed==1?R.color.colorPrimary:R.color.red_ten));
                holder.tv_value.setBackgroundResource(isReversed==1?R.drawable.red_ten_reverse:R.drawable.red_ten_double);
            }
        }

        private SpannableStringBuilder getRecordStr(Record record) {
            SpannableStringBuilder sb = new SpannableStringBuilder();
            long winnerId = record.getWinnerId();
            long loserId = record.getLoserId();
            if (game == Game.MAHJONG) {
                SpannableString winnerName = getColorfulName(winnerId, R.color.colorAccent);
                if (loserId == 0) {//自摸
                    sb.append(winnerName);
                    sb.append("自摸");
                } else {//放铳
                    SpannableString loserName = getColorfulName(loserId, R.color.colorPrimary);

                    sb.append(loserName);
                    sb.append("放铳给");
                    sb.append(winnerName);
                }
            } else if (game == Game.RED_TEN) {
                // TODO 红十
                @RedTen int result = record.getResult();
                String resultStr = RESULT[result];
                if (record.getLoserId() == 0 && record.getIsReversed() == 1) {
                    resultStr = "关";
                }
                String winners = User.getNameById(winnerId) + (loserId == 0 ? "" : "、" + User.getNameById(loserId));
                SpannableString winnerNames = getColorfulName(winners, R.color.colorAccent);
                StringBuilder losers = new StringBuilder();
                List<User> users = User.getUsers();
                for (User user : users) {
                    if (user.getId() == winnerId) {
                        continue;
                    }
                    if (loserId != 0 && user.getId() == loserId) {
                        continue;
                    }
                    losers.append(user.getName()).append("、");
                }
                losers.deleteCharAt(losers.length() - 1);
                SpannableString loserNames = getColorfulName(losers.toString(), R.color.colorPrimary);

                sb.append(winnerNames).append(" ");
                if (record.getIsReversed() == 1) {
                    sb.append("被 ").append(loserNames).append(" ").append(resultStr);
                } else {
                    sb.append(resultStr).append(" ").append(loserNames);
                }
            } else {
                sb.append("未知赌局");
            }
            return sb;
        }

        private SpannableString getColorfulName(long userId, @ColorRes int colorRes) {
            String name = User.getNameById(userId);
            if (TextUtils.isEmpty(name)) {
                throw new RuntimeException("no such id: " + userId);
            }
            return getColorfulName(name, colorRes);
        }

        private SpannableString getColorfulName(String name, @ColorRes int colorRes) {
            SpannableString ss = new SpannableString(name);
            int color = ContextCompat.getColor(RecordListActivity.this, colorRes);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
            ss.setSpan(colorSpan, 0, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            return ss;
        }

        @Override
        public int getItemCount() {
            return mRecords.size();
        }

        boolean isDayFirstTime(int position) {
            int sum = mRecords.size();
            if (position < 0 || position >= sum) {
                throw new RuntimeException("no such position: " + position + "/" + sum);
            }
            if (position == 0) return true;
            return !TimeUtl.isSameDay(mRecords.get(position - 1).getCreateTime(), mRecords.get(position).getCreateTime());
        }

        class VHolder extends RecyclerView.ViewHolder {

            TextView tv_day;
            TextView tv_time;
            TextView tv_record;
            TextView tv_basked;
            TextView tv_double_red_ten;
            TextView tv_reversed;
            TextView tv_value;

            public VHolder(View itemView) {
                super(itemView);
                tv_day = itemView.findViewById(R.id.tv_day);
                tv_time = itemView.findViewById(R.id.tv_time);
                tv_record = itemView.findViewById(R.id.tv_record);
                tv_basked = itemView.findViewById(R.id.tv_basked);
                tv_double_red_ten = itemView.findViewById(R.id.tv_double_red_ten);
                tv_reversed = itemView.findViewById(R.id.tv_reversed);
                tv_value = itemView.findViewById(R.id.tv_value);
            }
        }
    }
}
