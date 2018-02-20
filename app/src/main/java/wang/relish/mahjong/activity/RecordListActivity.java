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
        mRecords = Record.getRecords();
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
        }

        private SpannableStringBuilder getRecordStr(Record record) {
            SpannableStringBuilder sb = new SpannableStringBuilder();
            long winnerId = record.getWinnerId();
            long loserId = record.getLoserId();
            if (game == Game.MAHJONG) {
                SpannableString winnerName = getName(winnerId, R.color.colorAccent);
                if (loserId == 0) {//自摸
                    sb.append(winnerName);
                    sb.append("自摸");
                } else {//放铳
                    SpannableString loserName = getName(loserId, R.color.colorPrimary);

                    sb.append(loserName);
                    sb.append("放铳给");
                    sb.append(winnerName);
                }
            } else if (game == Game.RED_TEN) {
                // TODO 红十

            } else {
                sb.append("未知赌局");
            }
            return sb;
        }

        private SpannableString getName(long userId, @ColorRes int colorRes) {
            String name = User.getNameById(userId);
            if (TextUtils.isEmpty(name)) {
                throw new RuntimeException("no such id: " + userId);
            }
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

            public VHolder(View itemView) {
                super(itemView);
                tv_day = itemView.findViewById(R.id.tv_day);
                tv_time = itemView.findViewById(R.id.tv_time);
                tv_record = itemView.findViewById(R.id.tv_record);
            }
        }
    }
}
