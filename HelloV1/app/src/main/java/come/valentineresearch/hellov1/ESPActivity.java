package come.valentineresearch.hellov1;

import androidx.appcompat.app.AppCompatActivity;

import com.esplibrary.bluetooth.ConnectionEvent;
import com.esplibrary.data.AlertData;
import com.esplibrary.packets.InfDisplayData;

import java.util.List;

public class ESPActivity extends AppCompatActivity implements V1Manager.V1ManagerDelegate {

    @Override
    public void onConnectionEvent(ConnectionEvent event) {

    }

    @Override
    public void onDisplayData(InfDisplayData displayData) {

    }

    @Override
    public void onAlertTableReceived(List<AlertData> alerts) {

    }
}
