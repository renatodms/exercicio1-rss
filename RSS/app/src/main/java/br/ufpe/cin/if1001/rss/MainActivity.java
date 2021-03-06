package br.ufpe.cin.if1001.rss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    //ao fazer envio da resolucao, use este link no seu codigo!
    private final String RSS_FEED = "http://leopoldomt.com/if1001/g1brasil.xml";

    //OUTROS LINKS PARA TESTAR...
    //http://rss.cnn.com/rss/edition.rss
    //http://pox.globo.com/rss/g1/brasil/
    //http://pox.globo.com/rss/g1/ciencia-e-saude/
    //http://pox.globo.com/rss/g1/tecnologia/

    //use ListView ao invés de TextView - deixe o atributo com o mesmo nome
    //private TextView conteudoRSS;
    private ListView conteudoRSS;

    //Lista de ItemRSS que for recuperada
    private List<ItemRSS> itemRSSList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Salvar o feed em uma SharedPreferences
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("rssfeed", getString(R.string.rss_feed_default));
        editor.commit();

        //use ListView ao invés de TextView - deixe o ID no layout XML com o mesmo nome conteudoRSS
        //isso vai exigir o processamento do XML baixado da internet usando o ParserRSS
        //conteudoRSS = (TextView) findViewById(R.id.conteudoRSS);
        conteudoRSS = (ListView) findViewById(R.id.conteudoRSS);

        //Listener do LisTWiew para abrir o WebView ao clicar em um título
        conteudoRSS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Recupera o link do item selecionado
                Uri uri = Uri.parse(itemRSSList.get(i).getLink());

                //Abrir o browser
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

                //Toast.makeText(getApplicationContext(), itemRSSList.get(i).getLink(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Carregar o feed a partir da SharedPreferences
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        String rss_feed_preference = sharedPref.getString("rssfeed", "");

        new CarregaRSStask().execute(rss_feed_preference);
    }

    private class CarregaRSStask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "iniciando...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            String conteudo = "provavelmente deu erro...";
            try {
                conteudo = getRssFeed(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return conteudo;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), "terminando...", Toast.LENGTH_SHORT).show();

            //ajuste para usar uma ListView
            //o layout XML a ser utilizado esta em res/layout/itemlista.xml
            ParserRSS parserRSS = new ParserRSS();
            AdapterRSS adaperRSS = null;
            try{
                itemRSSList = parserRSS.parse(s);
                //Uso do adapter para popular o ListView
                adaperRSS = new AdapterRSS(MainActivity.this, itemRSSList);
            }catch (Exception e){
                //Caso ocorra um erro no parser
                Toast.makeText(getApplicationContext(), "erro no parser", Toast.LENGTH_SHORT).show();
            }
            //conteudoRSS.setText(s);
            conteudoRSS.setAdapter(adaperRSS);
        }
    }

    //Opcional - pesquise outros meios de obter arquivos da internet
    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }
}
