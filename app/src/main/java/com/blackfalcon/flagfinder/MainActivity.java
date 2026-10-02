package com.blackfalcon.flagfinder;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity {

    // letter, English, Urdu, hex
    private static final String[][] COLORS = {
            {"R", "Red", "سرخ", "#D32F2F"},
            {"W", "White", "سفید", "#FFFFFF"},
            {"B", "Blue", "نیلا", "#1E4FB5"},
            {"G", "Green", "سبز", "#2E9E4F"},
            {"Y", "Yellow", "پیلا", "#FBC02D"},
            {"K", "Black", "کالا", "#111111"},
            {"O", "Orange", "نارنجی", "#F57C00"},
            {"L", "Light Blue", "آسمانی", "#4FC3F7"}
    };

    // ISO code : colors present in the flag (R W B G Y K O L)
    private static final String DATA =
            "AF:KRGW,AL:RK,DZ:GWR,AD:BYR,AO:RKY,AG:RKBWY,AR:LWY,AM:RBO,AU:BRW,AT:RW,"
            + "AZ:LRGW,BS:LYK,BH:RW,BD:GR,BB:BYK,BY:RGW,BE:KYR,BZ:BRWG,BJ:GYR,BT:YOW,"
            + "BO:RYG,BA:BYW,BW:LKW,BR:GYBW,BN:YWKR,BG:WGR,BF:RGY,BI:RGW,CV:BWRY,KH:BRW,"
            + "CM:GRY,CA:RW,CF:BWGYR,TD:BYR,CL:BWR,CN:RY,CO:YBR,KM:YWRBG,CG:GYR,CD:LYR,"
            + "CR:BWR,CI:OWG,HR:RWB,CU:BWR,CY:WOG,CZ:WRB,DK:RW,DJ:LGWR,DM:GYKWR,DO:BRW,"
            + "EC:YBR,EG:RWK,SV:BW,GQ:GWRB,ER:GRBY,EE:BKW,SZ:BYRWK,ET:GYRB,FJ:LRWB,FI:WB,"
            + "FR:BWR,GA:GYB,GM:RBGW,GE:WR,DE:KRY,GH:RYGK,GR:BW,GD:RYG,GT:LW,GN:RYG,"
            + "GW:RYGK,GY:GWYKR,HT:BR,HN:BW,HU:RWG,IS:BWR,IN:OWGB,ID:RW,IR:GWR,IQ:RWKG,"
            + "IE:GWO,IL:WB,IT:GWR,JM:GYK,JP:WR,JO:KWGR,KZ:LY,KE:KRGW,KI:RBYW,KP:BRW,"
            + "KR:WKRB,KW:GWRK,KG:RY,LA:RBW,LV:RW,LB:RWG,LS:BWGK,LR:RWB,LY:RKGW,LI:BRY,"
            + "LT:YGR,LU:RWL,MG:RGW,MW:KRG,MY:RWBY,MV:RGW,ML:GYR,MT:WR,MH:BOW,MR:GYR,"
            + "MU:RBYG,MX:GWR,FM:LW,MD:BYR,MC:RW,MN:RBY,ME:RY,MA:RG,MZ:GKYRW,MM:YGRW,"
            + "NA:BRGWY,NR:BYW,NP:RBW,NL:RWB,NZ:BRW,NI:BWY,NE:OWG,NG:GW,MK:RY,NO:RWB,"
            + "OM:RWG,PK:GW,PW:LY,PA:RWB,PG:RKYW,PY:RWB,PE:RW,PH:BRWY,PL:WR,PT:GRY,"
            + "QA:RW,RO:BYR,RU:WBR,RW:LYGB,KN:GYKRW,LC:LYWK,VC:BYG,WS:RBW,SM:WL,ST:GYRK,"
            + "SA:GW,SN:GYR,RS:RBW,SC:BYRWG,SL:GWB,SG:RW,SK:WBR,SI:WBR,SB:BGYW,SO:LW,"
            + "ZA:RGYKWB,SS:KRGWBY,ES:RY,LK:YOGR,SD:RWKG,SR:GWRY,SE:BY,CH:RW,SY:RWKG,TW:RBW,"
            + "TJ:RWGY,TZ:GYKB,TH:RWB,TL:RYKW,TG:GYRW,TO:RW,TT:RWK,TN:RW,TR:RW,TM:GRW,"
            + "TV:LYRWB,UG:KYRW,UA:BY,AE:GWKR,GB:BRW,US:RWB,UY:WLY,UZ:LWGR,VU:RGKY,VA:YW,"
            + "VE:YBR,VN:RY,YE:RWK,ZM:GRKO,ZW:GYRKW,PS:KWGR,XK:BYW";

    static class Country {
        String flag, en, ur, colors;
    }

    private final List<Country> all = new ArrayList<>();
    private final List<Country> shown = new ArrayList<>();
    private final Set<String> selected = new LinkedHashSet<>();
    private final Map<String, String> hexOf = new HashMap<>();
    private final List<TextView> chips = new ArrayList<>();
    private TextView countView;
    private CheckBox exactBox;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (String[] c : COLORS) hexOf.put(c[0], c[3]);
        loadCountries();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(dp(12), dp(10), dp(12), dp(6));
        root.setFitsSystemWindows(true);

        TextView title = makeText("دنیا کے جھنڈے • World Flags", 22, true);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView hint = makeText("جھنڈے میں نظر آنے والے رنگ منتخب کریں\nTap the colors you see in the flag", 13, false);
        hint.setTextColor(Color.parseColor("#666666"));
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, dp(4), 0, dp(8));
        root.addView(hint);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        for (final String[] c : COLORS) {
            final TextView chip = new TextView(this);
            chip.setGravity(Gravity.CENTER);
            chip.setTextSize(13);
            chip.setPadding(dp(4), dp(10), dp(4), dp(10));
            chip.setTag(c);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selected.contains(c[0])) selected.remove(c[0]);
                    else selected.add(c[0]);
                    styleChips();
                    refresh();
                }
            });
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f));
            lp.width = 0;
            lp.setMargins(dp(3), dp(3), dp(3), dp(3));
            grid.addView(chip, lp);
            chips.add(chip);
        }
        root.addView(grid, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout opts = new LinearLayout(this);
        opts.setOrientation(LinearLayout.HORIZONTAL);
        opts.setGravity(Gravity.CENTER_VERTICAL);
        exactBox = new CheckBox(this);
        exactBox.setText("صرف یہی رنگ • Only these colors");
        exactBox.setTextSize(13);
        exactBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        opts.addView(exactBox, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        Button clear = new Button(this);
        clear.setText("صاف • Clear");
        clear.setTextSize(12);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected.clear();
                styleChips();
                refresh();
            }
        });
        opts.addView(clear, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(opts);

        countView = makeText("", 14, true);
        countView.setPadding(dp(4), dp(4), dp(4), dp(4));
        root.addView(countView);

        ListView list = new ListView(this);
        adapter = new BaseAdapter() {
            @Override public int getCount() { return shown.size(); }
            @Override public Object getItem(int i) { return shown.get(i); }
            @Override public long getItemId(int i) { return i; }
            @Override public View getView(int i, View convertView, ViewGroup parent) {
                return buildRow(shown.get(i));
            }
        };
        list.setAdapter(adapter);
        root.addView(list, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        TextView credit = makeText("By: Black Falcon", 12, true);
        credit.setTextColor(Color.parseColor("#666666"));
        credit.setGravity(Gravity.END);
        credit.setPadding(0, dp(4), dp(4), 0);
        root.addView(credit);

        setContentView(root);
        styleChips();
        refresh();
    }

    private void loadCountries() {
        Locale ur = new Locale("ur");
        for (String item : DATA.split(",")) {
            String[] p = item.split(":");
            String code = p[0];
            Country c = new Country();
            c.colors = p[1];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < code.length(); i++) {
                sb.appendCodePoint(0x1F1E6 + (code.charAt(i) - 'A'));
            }
            c.flag = sb.toString();
            Locale loc = new Locale("", code);
            c.en = loc.getDisplayCountry(Locale.ENGLISH);
            if (c.en == null || c.en.isEmpty()) c.en = code;
            c.ur = loc.getDisplayCountry(ur);
            if (c.ur == null || c.ur.isEmpty() || c.ur.equals(code)) c.ur = c.en;
            all.add(c);
        }
        Collections.sort(all, (a, b) -> a.en.compareTo(b.en));
    }

    private void refresh() {
        shown.clear();
        boolean exact = exactBox.isChecked();
        for (Country c : all) {
            if (selected.isEmpty()) {
                shown.add(c);
                continue;
            }
            boolean ok = true;
            for (String s : selected) {
                if (c.colors.indexOf(s) < 0) { ok = false; break; }
            }
            if (ok && exact && c.colors.length() != selected.size()) ok = false;
            if (ok) shown.add(c);
        }
        if (!selected.isEmpty()) {
            // closest matches (fewest extra colors) first
            Collections.sort(shown, (a, b) -> {
                if (a.colors.length() != b.colors.length()) return a.colors.length() - b.colors.length();
                return a.en.compareTo(b.en);
            });
        }
        countView.setText("نتائج • Results: " + shown.size() + " / " + all.size());
        adapter.notifyDataSetChanged();
    }

    private void styleChips() {
        for (TextView chip : chips) {
            String[] c = (String[]) chip.getTag();
            boolean sel = selected.contains(c[0]);
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.parseColor(c[3]));
            gd.setCornerRadius(dp(10));
            gd.setStroke(dp(sel ? 4 : 1), sel ? Color.BLACK : Color.parseColor("#999999"));
            chip.setBackground(gd);
            chip.setAlpha(sel ? 1f : 0.6f);
            boolean darkText = c[0].equals("W") || c[0].equals("Y") || c[0].equals("O") || c[0].equals("L");
            chip.setTextColor(darkText ? Color.BLACK : Color.WHITE);
            chip.setTypeface(null, sel ? Typeface.BOLD : Typeface.NORMAL);
            chip.setText((sel ? "✓ " : "") + c[1] + "\n" + c[2]);
        }
    }

    private View buildRow(Country c) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(4), dp(8), dp(4), dp(8));

        TextView flag = new TextView(this);
        flag.setText(c.flag);
        flag.setTextSize(40);
        flag.setPadding(0, 0, dp(14), 0);
        row.addView(flag);

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);

        TextView en = makeText(c.en, 17, true);
        col.addView(en);
        if (!c.ur.equals(c.en)) {
            TextView ur = makeText(c.ur, 15, false);
            ur.setTextColor(Color.parseColor("#555555"));
            col.addView(ur);
        }

        LinearLayout dots = new LinearLayout(this);
        dots.setOrientation(LinearLayout.HORIZONTAL);
        dots.setPadding(0, dp(4), 0, 0);
        for (int i = 0; i < c.colors.length(); i++) {
            View d = new View(this);
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.parseColor(hexOf.get(String.valueOf(c.colors.charAt(i)))));
            gd.setCornerRadius(dp(3));
            gd.setStroke(dp(1), Color.parseColor("#888888"));
            d.setBackground(gd);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(16), dp(16));
            lp.rightMargin = dp(4);
            dots.addView(d, lp);
        }
        col.addView(dots);

        row.addView(col, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        return row;
    }

    private TextView makeText(String s, int sp, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(Color.parseColor("#111111"));
        if (bold) t.setTypeface(null, Typeface.BOLD);
        return t;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }
}
