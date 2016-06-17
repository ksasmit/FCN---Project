package pltanalyzer.com.pltanalyzer;

import android.app.ListActivity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;

public class SitesListView extends ListActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sites_list_view);
        //tv = (TextView)findViewById(R.id.mainText);

        String[] myStringArray = { "www.google.com", "www.youtube.com", "www.facebook.com",
                "www.baidu.com", "www.amazon.com", "www.yahoo.com", "www.wikipedia.org",
                "www.qq.com", "www.twitter.com", "www.live.com", "www.taobao.com",
                "www.sina.com.cn", "www.msn.com", "www.bing.com", "www.weibo.com",
                "www.linked.com", "www.vk.com", "www.instagram.com", "www.ebay.com",
                "www.reddit.com", "www.netflix.com", "www.paypal.com", "www.apple.com",
                "www.stackoverflow.com ", "www.wordpress.com"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_url_list, R.id.url, myStringArray);

        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);

        String selectedItem = (String) getListView().getItemAtPosition(position);
        Toast.makeText(getApplicationContext(),"You clicked " + selectedItem + " at position " + position, Toast.LENGTH_SHORT).show();
        displayStats(selectedItem);
    }

    private void displayStats(String url){

        if(url == null | url.length() == 0){
            Toast.makeText(getApplicationContext(), R.string.invalid_url, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, DisplayStatsActivity.class);
        intent.putExtra("URL_NAME", url);

        String cmd[] = {"curl -o /dev/null -s -w %{time_namelookup} "+url,
                "curl -o /dev/null -s -w %{time_connect} "+url,
                "curl -o /dev/null -s -w %{time_pretransfer} "+url,
                "curl -o /dev/null -s -w %{time_starttransfer} "+url,
                "curl -o /dev/null -s -w %{size_download} "+url,
                "curl -o /dev/null -s -w %{speed_download} "+url,
                "curl -o /dev/null -s -w %{time_total} "+url
        };

        int i=0;
        Process process=null;
        OutputStreamWriter osw = null;
        try {
            for(i=0;i<cmd.length;i++)
            {

                process = Runtime.getRuntime().exec("su");
                osw = new OutputStreamWriter(process.getOutputStream());
                osw.write(cmd[i]);

                osw.flush();
                osw.close();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                int read;
                char[] buffer = new char[4096];
                StringBuffer output = new StringBuffer();
                while ((read = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                }
                reader.close();
                process.waitFor();
                String res = output.toString();
                Log.d("Alpit", res);
                switch(i)
                {
                    case 0:
                        intent.putExtra(DisplayStatsActivity.LOOKUP_TIME, res);
                        break;
                    case 1:
                        intent.putExtra(DisplayStatsActivity.CONNECT_TIME, res);
                        break;
                    case 2:
                        intent.putExtra(DisplayStatsActivity.PRE_TRANSFER_TIME, res);
                        break;
                    case 3:
                        intent.putExtra(DisplayStatsActivity.START_TRANSFER_TIME, res);
                        break;
                    case 4:
                        intent.putExtra(DisplayStatsActivity.DOWNLOADED_SIZE, res);
                        break;
                    case 5:
                        intent.putExtra(DisplayStatsActivity.DOWNLOADED_SPEED, res);
                        break;
                    case 6:
                        intent.putExtra(DisplayStatsActivity.TOTAL_TIME, res);
                        break;
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //starting pie chart Activity
        startActivity(intent);
    }
}
