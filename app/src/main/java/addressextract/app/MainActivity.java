package addressextract.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    private  EditText editText;
    private  EditText outputText;
    public static final String ADDRESSREGEX = "\\d{1,3}.?\\d{0,3}\\s[a-zA-Z]{2,30}\\s[a-zA-Z]{2,15}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.InputText);
        outputText = (EditText) findViewById(R.id.OutputText);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            }
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            editText.setText(sharedText);
            ExtractAddress();
            CopyAddress();
        }
    }


    public void ExtractAddress(View view) {
        ExtractAddress();
    }

    private void ExtractAddress() {
        String output = editText.getText().toString();
        String address ="";
        Pattern p = Pattern.compile(ADDRESSREGEX);
        Matcher m = p.matcher(output);

        if(m.find())
           address = m.group(0);

        if(address.isEmpty()) {
            String newLine = System.getProperty("line.separator");
            String lines[] = output.split(newLine);
            if(lines.length >= 6 )
                if (lines[6] != null  && !lines[6].isEmpty())
                    address = lines[6];
        }

        if(address.isEmpty())
        {
            address = "No address found";
        }

        outputText.setText(address);
    }

    public void CopyAddress(View view)
    {
        CopyAddress();
    }

    public void WazeGPS(View view)
    { Intent intent = new Intent();
        intent.setData(Uri.parse("waze://?q=" + outputText.getText().toString()));
        startActivity(intent);
    }

    public void MapsGPS(View view)
    { Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("google.navigation:q=" +  getGeoAddress()));
        startActivity(intent);
    }

    String getGeoAddress()
    {
        return  outputText.getText().toString().replace(' ','+');
    }

    private void CopyAddress() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText( "Address", outputText.getText());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, ("Copied: " + outputText.getText()), Toast.LENGTH_LONG).show();
    }
}
