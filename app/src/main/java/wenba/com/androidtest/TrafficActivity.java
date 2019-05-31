package wenba.com.androidtest;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TrafficActivity extends ActionBarActivity {

    private TextView show_liuliang;//显示流量
    private Button btn_scan;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_activity);

        show_liuliang = (TextView) findViewById(R.id.show_liuliang);
        listView = (ListView) findViewById(R.id.listView);
        btn_scan = (Button) findViewById(R.id.btn_scan);

        long mrb = TrafficStats.getMobileRxBytes();   //手机接收的字节数,非WiFi状态

        long mtb = TrafficStats.getMobileTxBytes();   //手机发送的字节数，非WiFi状态

        long tMrb = TrafficStats.getTotalRxBytes();   //全部接收的字节数

        long tMtb = TrafficStats.getTotalTxBytes();   //全部发送的字节数

        StringBuilder sb = new StringBuilder();
        sb.append("非WiFi总接受:").append(byteToMB(mrb));
        sb.append("非WiFi总发送:").append(byteToMB(mtb));
        sb.append("全部接收:").append(byteToMB(tMrb));
        sb.append("全部发送:").append(byteToMB(tMtb));

        show_liuliang.setText(sb.toString());//显示

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();             //拿到包管理者
                //拿到包的信息  PackageInfo是系统的一个类
                List<PackageInfo> info = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
                        | PackageManager.GET_PERMISSIONS);
                List<Map<String, String>> appInfo = new ArrayList<>();//用于存放App的名称，上传和下载的字节
                /**
                 * 通过看它有没有网络权限权限
                 */
                for (PackageInfo temp : info) {
                    String appName = temp.applicationInfo.loadLabel(pm).toString();       //App名称
                    if (appName.equals("学霸君1对1")) {
                        String permissions[] = temp.requestedPermissions;    //拿到该包的权限
                        if (permissions != null && permissions.length > 0) {   //如果有权限
                            for (String p : permissions) {
                                if (p.equals("android.permission.INTERNET")) {//是否有网络权限
                                    long appMrb = TrafficStats.getUidRxBytes(temp.applicationInfo.uid);//App接收的字节
                                    long appMtb = TrafficStats.getUidTxBytes(temp.applicationInfo.uid);//App发送的字节
                                    String str = appName + "下载：" + byteToMB(appMrb) + "上传:" + byteToMB(appMtb);
                                    Map<String, String> map = new HashMap<>();
                                    map.put("info", str);
                                    appInfo.add(map);
                                }
                            }
                        }
                    }
                }

                SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(),
                        appInfo,
                        android.R.layout.simple_list_item_1,
                        new String[]{"info"},
                        new int[]{android.R.id.text1});

                listView.setAdapter(simpleAdapter);//设置Adapter
            }
        });
    }

    //将字节数转化为MB
    private String byteToMB(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size > kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }

}
